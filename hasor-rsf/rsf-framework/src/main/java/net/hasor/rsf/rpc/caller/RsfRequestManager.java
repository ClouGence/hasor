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
import io.netty.util.TimerTask;
import net.hasor.core.Hasor;
import net.hasor.rsf.*;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.rpc.net.SendCallBack;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
/**
 * 负责管理所有 RSF 发起的请求，Manager还提供了最大并发上限的配置.
 * @version : 2014年9月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class RsfRequestManager {
    protected static Logger                         logger    = LoggerFactory.getLogger(RsfRequestManager.class);
    protected static Logger                         invLogger = LoggerFactory.getLogger(RsfConstants.LoggerName_Invoker);
    private final    ConcurrentMap<Long, RsfFuture> rsfResponse;
    private final    RsfContext                     rsfContext;
    private final    AtomicInteger                  requestCount;
    private final    SenderListener                 senderListener;
    //
    public RsfRequestManager(RsfContext rsfContext, SenderListener senderListener) {
        senderListener = Hasor.assertIsNotNull(senderListener, "not found SendData.");
        this.rsfContext = rsfContext;
        this.rsfResponse = new ConcurrentHashMap<>();
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
    private void sendData(InterAddress toAddress, RequestInfo info, SendCallBack callBack) {
        this.senderListener.sendRequest(toAddress, info, callBack);
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
        invLogger.info("response({}) -> receiveTime ={}, serializeType ={}, status ={}, isMessage ={}, bindID ={}, callMethod ={}.",//
                requestID, info.getReceiveTime(), serializeType, info.getStatus(), rsfRequest.isMessage(), bindID, callMethod);
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
            local.sendData(info.getReturnData());
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
     * 发送RSF调用请求，处理RsfFilter
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
        // .setup RsfRequest
        rsfRequest.addOptionMap(this.getContext().getSettings().getClientOption());//写入客户端选项，并将选项发送到Server。
        //
        // .call Chain
        try {
            RsfResponseObject res = new RsfResponseObject(rsfRequest);
            /*下面这段代码要负责 -> 执行rsfFilter过滤器链，并最终调用sendRequest发送请求。*/
            Supplier<RsfFilter>[] rsfFilterList = this.getContainer().getFilterProviders(serviceID);
            new RsfFilterHandler(rsfFilterList, (request, response) -> {
                if (response.isResponse()) {
                    invLogger.info("request({}) -> sendRequest, response form local.", request.getRequestID());
                    rsfFuture.completed(response);//如果本地调用链已经做出了响应，那么不在需要发送到远端。
                } else {
                    invLogger.info("request({}) -> sendRequest, response wait for remote.", request.getRequestID());
                    sendRequest(rsfFuture);//发送请求到远方
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
                rsfFuture.failed(new RsfException(ProtocolStatus.SendLimitPolicy, errorMessage));
                return;
            } else {
                // - B.等待1秒之后重新尝试，如果依然资源不足，那么抛异常
                try {
                    Thread.sleep(1000);/*SendLimitPolicy.WaitSecond*/
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
                    invLogger.error(errorMessage);
                    rsfFuture.failed(new RsfException(ProtocolStatus.SendLimitPolicy, errorMessage));
                    return;
                }
            }
        }
        /*3.准备发送数据*/
        InterAddress toAddress = rsfRequest.getTargetAddress();
        if (toAddress == null) {
            invLogger.warn("request({}) -> targetAddress Unavailable, bindID ={}.", rsfRequest.getRequestID(), serviceID);
            rsfFuture.failed(new RsfException(ProtocolStatus.Forbidden, "Service [" + serviceID + "] Address Unavailable."));
            return;
        }
        /*4.发送请求*/
        try {
            invLogger.warn("request({}) -> pre sendData, bindID ={}, targetAddress ={}.", rsfRequest.getRequestID(), serviceID, toAddress);
            RsfEnvironment environment = this.getContext().getEnvironment();
            RequestInfo info = ProtocolUtils.buildRequestInfo(environment, rsfRequest); // <- 1.生成RequestInfo
            info.setFlags(rsfRequest.getFlags());
            startRequest(rsfFuture);                                                    // <- 2.开始 timeout 计时
            sendData(toAddress, info, new SendCallBack() {                              // <- 3.发送数据
                public void failed(long requestID, Throwable e) {
                    putResponse(requestID, e);                                          // <- 4.发送失败直接 failed
                }
                public void complete(long requestID) {
                    /* wait response data */
                }
            });
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
        TimerTask timeTask = timeoutObject -> {
            RsfFuture rsfCallBack = getRequest(request.getRequestID());
            /*检测不到说明请求已经被正确响应。*/
            if (rsfCallBack == null) {
                return;
            }
            /*异常信息*/
            String errorInfo = "request(" + request.getRequestID() + ") -> timeout for client.";
            invLogger.error(errorInfo);
            /*回应Response*/
            putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
        };
        invLogger.info("request({}) -> startRequest, timeout at {} ,bindID ={}, callMethod ={}.", //
                request.getRequestID(), request.getTimeout(), request.getBindInfo().getBindID(), request.getMethod());
        this.getContext().getEnvironment().atTime(timeTask, request.getTimeout());
    }
}
