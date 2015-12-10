/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.more.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.domain.RsfRuntimeUtils;
import net.hasor.rsf.domain.RsfTimeoutException;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.protocol.RequestInfo;
import net.hasor.rsf.transform.protocol.ResponseInfo;
import net.hasor.rsf.utils.TimerManager;
/**
 * 负责管理所有 RSF 发起的请求。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfRequestManager {
    protected Logger                                 logger = LoggerFactory.getLogger(getClass());
    private final ConcurrentHashMap<Long, RsfFuture> rsfResponse;
    private final TimerManager                       timerManager;
    private final AtomicInteger                      requestCount;
    private final RsfContext                         rsfContext;
    private final SerializeFactory                   serializeFactory;
    //
    public RsfRequestManager(RsfContext rsfContext) {
        this.rsfResponse = new ConcurrentHashMap<Long, RsfFuture>();
        this.timerManager = new TimerManager(rsfContext.getSettings().getDefaultTimeout());
        this.requestCount = new AtomicInteger(0);
        this.rsfContext = rsfContext;
        this.serializeFactory = SerializeFactory.createFactory(rsfContext.getSettings());
    }
    /**获取RSF容器对象。*/
    public final RsfContext getContext() {
        return this.rsfContext;
    }
    /**获取{@link RsfBeanContainer}。*/
    protected abstract RsfBeanContainer getContainer();
    /**发送数据包*/
    protected abstract void sendData(Provider<InterAddress> target, RequestInfo info);
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
     * @param response 响应结果
     */
    public boolean putResponse(ResponseInfo info) {
        long requestID = info.getRequestID();
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture == null) {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", requestID);
            return false;
        }
        //
        RsfRequest rsfRequest = rsfFuture.getRequest();
        RsfResponseFormLocal local = new RsfResponseFormLocal(rsfRequest);
        local.addOptionMap(info);
        local.sendStatus(info.getStatus());
        //        logger.info("received response({}) status = {}", requestID, info.getStatus());
        try {
            SerializeCoder coder = serializeFactory.getSerializeCoder(info.getSerializeType());
            byte[] returnDataData = info.getReturnData();
            Object returnObject = coder.decode(returnDataData);
            local.sendData(returnObject);
            return rsfFuture.completed(local);
        } catch (Throwable e) {
            logger.error("recovery form Socket > " + e.getMessage(), e);
            return rsfFuture.failed(e);
        }
    }
    /**
     * 响应挂起的Request请求。
     * @param response 响应结果
     */
    public boolean putResponse(RsfResponse response) {
        long requestID = response.getRequestID();
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            logger.debug("received response({}) status = {}", requestID, response.getStatus());
            return rsfFuture.completed(response);
        } else {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", requestID);
            return false;
        }
    }
    /**
     * 响应挂起的Request请求。
     * @param requestID 请求ID
     * @param rsfException 异常响应
     */
    public void putResponse(long requestID, Throwable e) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            logger.error("received error ,requestID({}) status = {}", requestID, e.getMessage());
            rsfFuture.failed(e);
        } else {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", requestID);
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
                String errorInfo = "send request timeout, the maximum client Settings is " + request.getTimeout();
                logger.warn(errorInfo);
                /*回应Response*/
                putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
            }
        };
        this.timerManager.atTime(timeTask, request.getTimeout());
    };
    /**
     * 发送RSF调用请求。
     * @param rsfRequest rsf请求
     * @param listener FutureCallback回调监听器。
     * @return 返回RsfFuture。
     */
    protected RsfFuture doSendRequest(RsfRequestFormLocal rsfRequest, FutureCallback<RsfResponse> listener) {
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        //
        try {
            RsfBindInfo<?> bindInfo = rsfRequest.getBindInfo();
            Provider<RsfFilter>[] rsfFilterList = this.getContainer().getFilterProviders(bindInfo.getBindID());
            RsfResponseFormLocal res = new RsfResponseFormLocal(rsfRequest);
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
    private void sendRequest(RsfFuture rsfFuture) throws Throwable {
        /*1.远程目标机*/
        final RsfRequestFormLocal rsfRequest = (RsfRequestFormLocal) rsfFuture.getRequest();
        Provider<InterAddress> target = rsfRequest.getTarget();
        /*2.发送之前的检查（允许的最大并发请求数）*/
        RsfSettings rsfSettings = this.getContainer().getEnvironment().getSettings();
        if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
            SendLimitPolicy sendPolicy = rsfSettings.getSendLimitPolicy();
            String errorMessage = "maximum number of requests, apply SendPolicy = " + sendPolicy.name();
            logger.warn(errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                throw new RsfException(ProtocolStatus.ClientError, errorMessage);
            } else {
                try {
                    Thread.sleep(1000);/*SendLimitPolicy.WaitSecond*/
                } catch (InterruptedException e) {/**/}
            }
        }
        /*3.回调函数（错误处理）*/
        //        FutureCallback<RsfResponse> sendErrorCallBack = new FutureCallback<RsfResponse>() {
        //            @Override
        //            public void cancelled() {
        //                /*回应Response -> 用户取消*/
        //                String errorMsg = "send callback , request[" + rsfRequest.getRequestID() + "] to cancelled by user.";
        //                logger.error(errorMsg);
        //                putResponse(rsfRequest.getRequestID(), new RsfException(ProtocolStatus.ClientError, errorMsg));
        //            }
        //            @Override
        //            public void completed(RsfResponse response) {
        //                /*OK*/
        //                putResponse(response);
        //            }
        //            @Override
        //            public void failed(Throwable e) {
        //                String errorMsg = "send callback , request[" + rsfRequest.getRequestID() + "] error:" + e.getMessage();
        //                logger.error(errorMsg, e);
        //                putResponse(rsfRequest.getRequestID(), e);
        //            }
        //        };
        /*4.发送请求*/
        try {
            this.startRequest(rsfFuture);//             <- 1.计时request。
            RequestInfo info = buildInfo(rsfRequest);// <- 2.生成RequestInfo
            sendData(target, info);//                   <- 3.发送数据
        } catch (Throwable e) {
            logger.error("request[" + rsfRequest.getRequestID() + "] send error, " + e.getMessage(), e);
            putResponse(rsfRequest.getRequestID(), e);
        }
    }
    private RequestInfo buildInfo(RsfRequest rsfRequest) throws Throwable {
        RequestInfo info = new RequestInfo();
        RsfBindInfo<?> rsfBindInfo = rsfRequest.getBindInfo();
        String serializeType = rsfRequest.getSerializeType();
        SerializeCoder coder = serializeFactory.getSerializeCoder(serializeType);
        //
        //1.基本信息
        info.setRequestID(rsfRequest.getRequestID());//请求ID
        info.setServiceGroup(rsfBindInfo.getBindGroup());//序列化策略
        info.setServiceName(rsfBindInfo.getBindName());//序列化策略
        info.setServiceVersion(rsfBindInfo.getBindVersion());//序列化策略
        info.setTargetMethod(rsfRequest.getMethod().getName());//序列化策略
        info.setSerializeType(serializeType);//序列化策略
        info.setClientTimeout(rsfRequest.getTimeout());
        //
        //2.params
        Class<?>[] pTypes = rsfRequest.getParameterTypes();
        Object[] pObjects = rsfRequest.getParameterObject();
        for (int i = 0; i < pTypes.length; i++) {
            String typeByte = RsfRuntimeUtils.toAsmType(pTypes[i]);
            byte[] paramByte = coder.encode(pObjects[i]);
            info.addParameter(typeByte, paramByte);
        }
        //
        //3.Opt参数
        info.addOptionMap(rsfRequest);
        //
        return info;
    }
}