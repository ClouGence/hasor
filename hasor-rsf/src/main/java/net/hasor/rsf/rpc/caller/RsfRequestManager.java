/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.rsf.rpc.caller;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.rsf.*;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.domain.provider.AddressProvider;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 负责管理所有 RSF 发起的请求，Manager还提供了最大并发上限的配置.
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfRequestManager {
    protected static Logger logger    = LoggerFactory.getLogger(RsfRequestManager.class);
    protected static Logger invLogger = LoggerFactory.getLogger(RsfConstants.LoggerName_Invoker);
    private final ConcurrentMap<Long, RsfFuture> rsfResponse;
    private final RsfContext                     rsfContext;
    private final AtomicInteger                  requestCount;
    private final SenderListener                 senderListener;
    //
    public RsfRequestManager(RsfContext rsfContext, SenderListener senderListener) {
        senderListener = Hasor.assertIsNotNull(senderListener, "not found SendData.");
        this.rsfContext = rsfContext;
        this.rsfResponse = new ConcurrentHashMap<Long, RsfFuture>();
        this.requestCount = new AtomicInteger(0);
        this.senderListener = senderListener;
    }
    /**获取RSF容器对象。*/
    public RsfContext getContext() {
        return this.rsfContext;
    }
    /**获取{@link RsfBeanContainer}。*/
    public abstract RsfBeanContainer getContainer();
    /**发送数据包*/
    private void sendData(Provider<InterAddress> target, RequestInfo info) {
        this.senderListener.sendRequest(target, info);
    }
    //
    /**
     * 获取正在进行中的调用请求。
     * @param requestID 请求ID
     * @return 返回RsfFuture。
     */
    public RsfFuture getRequest(long requestID) {
        return this.rsfResponse.get(requestID);
    }
    /**
     * 响应挂起的Request请求。
     * @param info 响应结果
     */
    public boolean putResponse(ResponseInfo info) {
        long requestID = info.getRequestID();
        RsfFuture rsfFuture = this.rsfResponse.get(requestID);
        if (rsfFuture == null) {
            invLogger.warn("response({}) -> timeoutFailed, RsfFuture is not exist. -> maybe is timeout!", requestID);
            return false;
        }
        //
        // 1.处理ACK应答 -> (Invoke类型调用,不处理ACK应答)
        if (info.getStatus() == ProtocolStatus.Accept) {
            if (!rsfFuture.getRequest().isMessage()) {
                invLogger.info("response({}) -> ignore, rpcType = Invoke, status = Accept", requestID);
                return true;/* Invoker类型request不处理 ack 应答,只有Message类型请求才会把ACK应答作为response进行处理。 */
            }
        }
        //
        // 2.处理response
        rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture == null) {
            invLogger.warn("response({}) -> timeoutFailed, RsfFuture is not exist. -> maybe is timeout!", requestID);
            return false;
        }
        //
        // 3.反序列化
        RsfRequest rsfRequest = rsfFuture.getRequest();
        RsfResponseObject local = new RsfResponseObject(rsfRequest);
        local.addOptionMap(info);
        local.sendStatus(info.getStatus());
        String serializeType = info.getSerializeType();
        String bindID = local.getBindInfo().getBindID();
        Method callMethod = rsfRequest.getMethod();
        int length = info.getReturnData() == null ? 0 : info.getReturnData().length;
        invLogger.info("response({}) -> receiveTime ={}, serializeType ={}, status ={}, dataLength ={}, isMessage ={}, bindID ={}, callMethod ={}.",//
                requestID, info.getReceiveTime(), serializeType, info.getStatus(), length, rsfRequest.isMessage(), bindID, callMethod);
        //
        // - Message 调用
        if (rsfRequest.isMessage()) {
            Class<?> returnType = rsfRequest.getMethod().getReturnType();
            RsfResultDO returnObject = null;
            if (info.getStatus() == ProtocolStatus.Accept) {
                returnObject = new RsfResultDO(requestID, true);
            } else {
                returnObject = new RsfResultDO(requestID, false);
                returnObject.setErrorCode(info.getStatus());
                returnObject.setErrorMessage(info.getOption("message"));
            }
            //
            if (returnObject.isSuccess()) {
                invLogger.info("response({}) -> successful.", requestID);
                if (returnType.isAssignableFrom(RsfResult.class)) {
                    local.sendData(returnObject);
                    return rsfFuture.completed(local);
                }
                if (returnObject.isSuccess()) {
                    local.sendData(null);
                    return rsfFuture.completed(local);
                }
            }
            //
            String errorInfo = "errorCode = " + returnObject.getErrorCode() + ", errorMessage=" + returnObject.getErrorMessage();
            invLogger.error("response({}) -> invokeFailed, {}", requestID, errorInfo);
            return rsfFuture.failed(new RsfException(local.getStatus(), errorInfo));
        }
        // - Invoker 调用
        if (info.getStatus() == ProtocolStatus.OK) {
            SerializeCoder coder = this.getContext().getEnvironment().getSerializeCoder(serializeType);
            Class<?> returnType = rsfRequest.getMethod().getReturnType();
            try {
                byte[] returnDataData = info.getReturnData();
                Object returnObject = coder.decode(returnDataData, returnType);
                local.sendData(returnObject);
            } catch (Throwable e) {
                invLogger.error("response({}) -> serializeFailed, bindID ={}, serializeType ={}, callMethod ={}, dataType ={}, message ={}.",//
                        requestID, bindID, serializeType, callMethod, returnType, e.getMessage(), e);
                return rsfFuture.failed(e);
            }
            return rsfFuture.completed(local);
        } else {
            invLogger.error("response({}) -> statusFailed, bindID ={}, status ={}.",//
                    requestID, bindID, local.getStatus());
            return rsfFuture.failed(new RsfException(local.getStatus(), "status."));
        }
    }
    /**
     * 响应挂起的Request请求。
     * @param requestID 请求ID
     * @param e 异常响应
     */
    public void putResponse(long requestID, Throwable e) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            invLogger.error("response({}) -> errorFailed, {}", requestID, e.getMessage(), e);
            rsfFuture.failed(e);
        } else {
            invLogger.error("response({}) -> errorFailed, RsfFuture is not exist. -> maybe is timeout! ,error= {}.", requestID, e.getMessage(), e);
        }
    }
    private RsfFuture removeRsfFuture(long requestID) {
        RsfFuture rsfFuture = this.rsfResponse.remove(requestID);
        if (rsfFuture != null) {
            this.requestCount.decrementAndGet();// i--;
        }
        return rsfFuture;
    }
    /**
     * 发送RSF调用请求。
     * @param rsfRequest rsf请求
     * @param listener FutureCallback回调监听器。
     * @return 返回RsfFuture。
     */
    protected RsfFuture doSendRequest(RsfRequestFormLocal rsfRequest, FutureCallback<RsfResponse> listener) {
        RsfBindInfo<?> bindInfo = rsfRequest.getBindInfo();
        String serviceID = bindInfo.getBindID();
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        invLogger.info("request({}) -> doSendRequest, bindID ={}, callMethod ={}, isMessage ={}.", //
                rsfRequest.getRequestID(), serviceID, rsfRequest.getMethod(), bindInfo.isMessage());
        //
        if (bindInfo.isMessage()) {
            rsfRequest.addOption("RPC_TYPE", "MESSAGE");
        } else {
            rsfRequest.addOption("RPC_TYPE", "INVOKER");
        }
        rsfRequest.addOptionMap(this.getContext().getSettings().getClientOption());//写入客户端选项，并将选项发送到Server。
        Provider<RsfFilter>[] rsfFilterList = this.getContainer().getFilterProviders(serviceID);
        RsfResponseObject res = new RsfResponseObject(rsfRequest);
        //
        try {
            /*下面这段代码要负责 -> 执行rsfFilter过滤器链，并最终调用sendRequest发送请求。*/
            new RsfFilterHandler(rsfFilterList, new RsfFilterChain() {
                public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
                    if (response.isResponse()) {
                        invLogger.info("request({}) -> sendRequest, response form local.", request.getRequestID());
                        rsfFuture.completed(response);//如果本地调用链已经做出了响应，那么不在需要发送到远端。
                    } else {
                        invLogger.info("request({}) -> sendRequest, response wait for remote.", request.getRequestID());
                        sendRequest(rsfFuture);//发送请求到远方
                    }
                }
            }).doFilter(rsfRequest, res);
        } catch (Throwable e) {
            invLogger.error("request({}) -> errorFailed, sendRequest, doRsfFilterChain. error ={}.", rsfRequest.getRequestID(), e.getMessage(), e);
            try {
                rsfFuture.failed(e);
            } catch (Throwable e2) {
                logger.error("request({}) -> {}.", rsfRequest.getRequestID(), e2.getMessage(), e2);
            }
        }
        return rsfFuture;
    }
    /**将请求发送到远端服务器。*/
    private void sendRequest(final RsfFuture rsfFuture) throws Throwable {
        /*1.远程目标机*/
        final RsfRequestFormLocal rsfRequest = (RsfRequestFormLocal) rsfFuture.getRequest();
        final AddressProvider target = rsfRequest.getTarget();
        String serviceID = rsfRequest.getBindInfo().getBindID();
        invLogger.info("request({}) -> bindID ={}, callMethod ={}, serializeType ={}, isMessage ={}, isP2PCalls ={}.",//
                rsfRequest.getRequestID(), serviceID, rsfRequest.getMethod(), rsfRequest.getSerializeType(), rsfRequest.isMessage(), rsfRequest.isP2PCalls());
        //
        /*2.发送之前的检查（允许的最大并发请求数）*/
        RsfSettings rsfSettings = this.getContainer().getEnvironment().getSettings();
        if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
            SendLimitPolicy sendPolicy = rsfSettings.getSendLimitPolicy();
            String errorMessage = "request(" + rsfRequest.getRequestID() + ") -> sendDataFailed, maximum number of requests, apply SendPolicy = " + sendPolicy.name();
            invLogger.error(errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                // - A.直接抛异常
                throw new RsfException(ProtocolStatus.SendLimitPolicy, errorMessage);
            } else {
                // - B.等待1秒之后重新尝试，如果依然资源不足，那么抛异常
                try {
                    Thread.sleep(1000);/*SendLimitPolicy.WaitSecond*/
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
                    invLogger.error(errorMessage);
                    throw new RsfException(ProtocolStatus.SendLimitPolicy, errorMessage);
                }
            }
        }
        /*3.准备发送数据*/
        String methodName = rsfRequest.getMethod().getName();
        Object[] args = rsfRequest.getParameterObject();
        InterAddress address = target.get(serviceID, methodName, args);
        if (address == null) {
            invLogger.warn("request({}) -> targetAddress Unavailable, bindID ={}.", rsfRequest.getRequestID(), serviceID);
            rsfFuture.failed(new RsfException(ProtocolStatus.Forbidden, "Service [" + serviceID + "] Address Unavailable."));
            return;
        }
        if (rsfRequest.isP2PCalls()) {
            rsfRequest.addOption(OptionKeys.TargetAddress, address.toHostSchema());
        }
        /*4.发送请求*/
        try {
            Provider<InterAddress> targetProvider = new InstanceProvider<InterAddress>(address);
            invLogger.warn("request({}) -> pre sendData, bindID ={}, targetAddress ={}.", rsfRequest.getRequestID(), serviceID, address);
            RsfEnvironment environment = this.getContext().getEnvironment();
            startRequest(rsfFuture);                                                    // <- 1.计时request。
            RequestInfo info = ProtocolUtils.buildRequestInfo(environment, rsfRequest); // <- 2.生成RequestInfo
            sendData(targetProvider, info);                                             // <- 3.发送数据
        } catch (Throwable e) {
            invLogger.error("request(" + rsfRequest.getRequestID() + ") send error, " + e.getMessage(), e);
            putResponse(rsfRequest.getRequestID(), e);
        }
    }
    /**
     * 负责客户端引发的超时逻辑。
     * @param rsfFuture 开始计时的请求。
     */
    private void startRequest(RsfFuture rsfFuture) {
        this.requestCount.incrementAndGet();// i++;
        this.rsfResponse.put(rsfFuture.getRequest().getRequestID(), rsfFuture);
        final RsfRequestFormLocal request = (RsfRequestFormLocal) rsfFuture.getRequest();
        TimerTask timeTask = new TimerTask() {
            public void run(Timeout timeoutObject) throws Exception {
                RsfFuture rsfCallBack = getRequest(request.getRequestID());
                /*检测不到说明请求已经被正确响应。*/
                if (rsfCallBack == null)
                    return;
                /*异常信息*/
                String errorInfo = "request(" + request.getRequestID() + ") -> timeout for client.";
                invLogger.error(errorInfo);
                /*回应Response*/
                putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
            }
        };
        invLogger.info("request({}) -> startRequest, timeout at {} ,bindID ={}, callMethod ={}.", //
                request.getRequestID(), request.getTimeout(), request.getBindInfo().getBindID(), request.getMethod());
        this.getContext().getEnvironment().atTime(timeTask, request.getTimeout());
    }
}
