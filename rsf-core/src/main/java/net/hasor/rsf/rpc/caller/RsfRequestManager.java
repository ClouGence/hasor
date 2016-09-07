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
import net.hasor.core.Provider;
import net.hasor.core.binder.InstanceProvider;
import net.hasor.rsf.*;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.domain.provider.AddressProvider;
import net.hasor.rsf.transform.codec.CodecAdapterFactory;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import net.hasor.rsf.utils.TimerManager;
import org.more.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 负责管理所有 RSF 发起的请求，Manager还提供了最大并发上限的配置.
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfRequestManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final ConcurrentMap<Long, RsfFuture> rsfResponse;
    private final RsfContext                     rsfContext;
    private final TimerManager                   timerManager;
    private final AtomicInteger                  requestCount;
    private final SenderListener                 senderListener;
    //
    public RsfRequestManager(RsfContext rsfContext, SenderListener senderListener) {
        if (senderListener == null) {
            throw new NullPointerException("not found SendData.");
        }
        this.rsfContext = rsfContext;
        RsfSettings rsfSetting = rsfContext.getSettings();
        this.rsfResponse = new ConcurrentHashMap<Long, RsfFuture>();
        this.timerManager = new TimerManager(rsfSetting.getDefaultTimeout(), "RsfRequestManager");
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
        RsfFuture rsfFuture = rsfResponse.get(requestID);
        if (rsfFuture == null) {
            logger.warn("received message for requestID:{} -> maybe is timeout! ", requestID);
            return false;
        }
        //
        // 1.处理ACK应答 -> (Invoke类型调用,不处理ACK应答)
        if (info.getStatus() == ProtocolStatus.Processing) {
            if (!rsfFuture.getRequest().isMessage()) {
                return true;/* Invoker类型request不处理 ack 应答,只有Message类型请求才会把ACK应答作为response进行处理。 */
            }
        }
        //
        // 2.处理response
        rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture == null) {
            logger.warn("received message for requestID:{} -> maybe is timeout! ", requestID);
            return false;
        }
        //
        // 3.反序列化
        RsfRequest rsfRequest = rsfFuture.getRequest();
        RsfResponseObject local = new RsfResponseObject(rsfRequest);
        local.addOptionMap(info);
        local.sendStatus(info.getStatus());
        logger.debug("received message for requestID:{} -> status is {}", requestID, info.getStatus());
        String serializeType = info.getSerializeType();
        try {
            //
            // - Message 调用
            if (rsfFuture.getRequest().isMessage()) {
                Class<?> returnType = rsfRequest.getMethod().getReturnType();
                RsfResultDO returnObject = null;
                if (info.getStatus() == ProtocolStatus.Processing) {
                    returnObject = new RsfResultDO(requestID, true);
                } else {
                    returnObject = new RsfResultDO(requestID, false);
                    returnObject.setErrorCode(info.getStatus());
                    returnObject.setErrorMessage(info.getOption("message"));
                }
                //
                if (returnType.isAssignableFrom(RsfResult.class)) {
                    local.sendData(returnObject);
                    return rsfFuture.completed(local);
                }
                if (returnObject.isSuccess()) {
                    local.sendData(null);
                    return rsfFuture.completed(local);
                }
                //
                String bindID = local.getBindInfo().getBindID();
                String callMethod = rsfRequest.getMethod().getName();
                String errorInfo = "responseID:" + requestID + " ,status= " + local.getStatus() + " ,bindID= " + bindID + " -> callMethod = " + callMethod;
                logger.error(errorInfo);
                return rsfFuture.failed(new RsfException(local.getStatus(), errorInfo));
            }
            // - Invoker 调用
            if (info.getStatus() == ProtocolStatus.OK) {
                SerializeCoder coder = this.getContext().getEnvironment().getSerializeCoder(serializeType);
                byte[] returnDataData = info.getReturnData();
                Object returnObject = coder.decode(returnDataData, rsfRequest.getMethod().getReturnType());
                local.sendData(returnObject);
                return rsfFuture.completed(local);
            } else {
                String bindID = local.getBindInfo().getBindID();
                String callMethod = rsfRequest.getMethod().getName();
                String errorInfo = "responseID:" + requestID + " ,status= " + local.getStatus() + " ,bindID= " + bindID + " -> callMethod = " + callMethod;
                logger.error(errorInfo);
                return rsfFuture.failed(new RsfException(local.getStatus(), errorInfo));
            }
        } catch (Throwable e) {
            String errorInfo = "decode response for requestID: " + requestID + " failed -> serializeType(" + serializeType + ") ,serialize error: " + e.getMessage();
            logger.error(errorInfo, e);
            return rsfFuture.failed(e);
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
            logger.error("received message for requestID: " + requestID + " -> error {}", e.getMessage());
            rsfFuture.failed(e);
        } else {
            logger.warn("received message for requestID: {} -> maybe is timeout! ", requestID);
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
                String errorInfo = "requestID: " + request.getRequestID() + " timeout for client.";
                logger.error(errorInfo);
                /*回应Response*/
                putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
            }
        };
        this.timerManager.atTime(timeTask, request.getTimeout());
    }
    ;
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
        //
        try {
            if (bindInfo.isMessage()) {
                rsfRequest.addOption("RPC_TYPE", "MESSAGE");
            } else {
                rsfRequest.addOption("RPC_TYPE", "INVOKER");
            }
            rsfRequest.addOptionMap(this.getContext().getSettings().getClientOption());//写入客户端选项，并将选项发送到Server。
            Provider<RsfFilter>[] rsfFilterList = this.getContainer().getFilterProviders(serviceID);
            RsfResponseObject res = new RsfResponseObject(rsfRequest);
            /*下面这段代码要负责 -> 执行rsfFilter过滤器链，并最终调用sendRequest发送请求。*/
            new RsfFilterHandler(rsfFilterList, new RsfFilterChain() {
                public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
                    if (response.isResponse()) {
                        rsfFuture.completed(response);//如果本地调用链已经做出了响应，那么不在需要发送到远端。
                    } else {
                        sendRequest(rsfFuture);//发送请求到远方
                    }
                }
            }).doFilter(rsfRequest, res);
        } catch (Throwable e) {
            try {
                rsfFuture.failed(e);
            } catch (Throwable e2) {
                logger.error("do callback for failed error->" + e.getMessage(), e);
            }
        }
        return rsfFuture;
    }
    /**将请求发送到远端服务器。*/
    private void sendRequest(final RsfFuture rsfFuture) throws Throwable {
        /*1.远程目标机*/
        final RsfRequestFormLocal rsfRequest = (RsfRequestFormLocal) rsfFuture.getRequest();
        final AddressProvider target = rsfRequest.getTarget();
        /*2.发送之前的检查（允许的最大并发请求数）*/
        RsfSettings rsfSettings = this.getContainer().getEnvironment().getSettings();
        if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
            SendLimitPolicy sendPolicy = rsfSettings.getSendLimitPolicy();
            String errorMessage = "maximum number of requests, apply SendPolicy = " + sendPolicy.name();
            logger.warn(errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                throw new RsfException(ProtocolStatus.SendLimitPolicy, errorMessage);
            } else {
                try {
                    Thread.sleep(1000);/*SendLimitPolicy.WaitSecond*/
                } catch (InterruptedException e) {/**/}
            }
        }
        /*3.发送请求*/
        try {
            String serviceID = rsfRequest.getBindInfo().getBindID();
            String methodName = rsfRequest.getMethod().getName();
            Object[] args = rsfRequest.getParameterObject();
            InterAddress address = target.get(serviceID, methodName, args);
            if (address == null) {
                rsfFuture.failed(new RsfException(ProtocolStatus.Forbidden, "Service [" + serviceID + "] Address Unavailable."));
                return;
            }
            Provider<InterAddress> targetProvider = new InstanceProvider<InterAddress>(address);
            startRequest(rsfFuture);                 // <- 1.计时request。
            RequestInfo info = CodecAdapterFactory.getCodecAdapterByVersion(this.getContext().getEnvironment(), RsfConstants.Version_1)//
                    .buildRequestInfo(rsfRequest);   // <- 2.生成RequestInfo
            sendData(targetProvider, info);          // <- 3.发送数据
        } catch (Throwable e) {
            logger.error("request(" + rsfRequest.getRequestID() + ") send error, " + e.getMessage(), e);
            putResponse(rsfRequest.getRequestID(), e);
        }
    }
}