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
package net.hasor.rsf.rpc.client;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.more.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.domain.RsfTimeoutException;
import net.hasor.rsf.domain.ServiceDefine;
import net.hasor.rsf.rpc.RsfFilterHandler;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.manager.TimerManager;
import net.hasor.rsf.rpc.objects.local.RsfRequestFormLocal;
import net.hasor.rsf.rpc.objects.local.RsfResponseFormLocal;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.protocol.RequestBlock;
/**
 * 负责管理所有 RSF 发起的请求。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClientRequestManager {
    protected Logger                                 logger = LoggerFactory.getLogger(getClass());
    private final AbstractRsfContext                 rsfContext;
    private final RsfClientChannelManager            clientManager;
    private final ConcurrentHashMap<Long, RsfFuture> rsfResponse;
    private final TimerManager                       timerManager;
    private final AtomicInteger                      requestCount;
    private final RsfClientWrappe                    rsfClientWrappe;
    //
    public RsfClientRequestManager(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.clientManager = new RsfClientChannelManager(rsfContext);
        this.rsfResponse = new ConcurrentHashMap<Long, RsfFuture>();
        this.timerManager = new TimerManager(getRsfContext().getSettings().getDefaultTimeout());
        this.requestCount = new AtomicInteger(0);
        this.rsfClientWrappe = new RsfClientWrappe(rsfContext);
    }
    /** @return 获取{@link RsfContext}*/
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    /** @return 获取客户端管理器*/
    public RsfClientChannelManager getClientManager() {
        return this.clientManager;
    }
    /**获取远程客户端包装接口，返回形式为{@link RsfClient}类型。*/
    public RsfClient getClientWrappe() {
        return this.rsfClientWrappe;
    }
    /**
     * 获取正在进行中的调用请求。
     * @param requestID 请求ID
     * @return 返回RsfFuture。
     */
    public RsfFuture getRequest(long requestID) {
        return this.rsfResponse.get(requestID);
    }
    //
    private RsfFuture removeRsfFuture(long requestID) {
        RsfFuture rsfFuture = this.rsfResponse.remove(requestID);
        if (rsfFuture != null) {
            this.requestCount.decrementAndGet();// i--;
        }
        return rsfFuture;
    }
    /**
     * 响应挂起的Request请求。
     * @param requestID 请求ID
     * @param response 响应结果
     */
    public void putResponse(long requestID, RsfResponse response) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            logger.debug("received response({}) status = {}", requestID, response.getResponseStatus());
            rsfFuture.completed(response);
        } else {
            logger.warn("give up the response,requestID({}) ,maybe because timeout! ", requestID);
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
    /**
     * 尝试再次发送Request请求（如果request已经超时则无效）。
     * @param requestID 请求ID
     */
    public void tryAgain(long requestID) {
        this.putResponse(requestID, new RsfException(ProtocolStatus.ChooseOther, "Server response  ChooseOther!"));
        System.out.println("RequestID:" + requestID + " -> ChooseOther"); //TODO ChooseOther机制的实现
    }
    //
    /**负责客户端引发的超时逻辑。*/
    private void startRequest(RsfFuture rsfFuture) {
        this.requestCount.incrementAndGet();// i++;
        this.rsfResponse.put(rsfFuture.getRequest().getRequestID(), rsfFuture);
        final RsfRequestFormLocal request = (RsfRequestFormLocal) rsfFuture.getRequest();
        TimerTask timeTask = new TimerTask() {
            public void run(Timeout timeoutObject) throws Exception {
                //超时检测
                RsfFuture rsfCallBack = RsfClientRequestManager.this.getRequest(request.getRequestID());
                if (rsfCallBack == null)
                    return;
                //引发超时Response
                String errorInfo = "timeout is reached on client side:" + request.getTimeout();
                logger.warn(errorInfo);
                //回应Response
                RsfClientRequestManager.this.putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
            }
        };
        //
        this.timerManager.atTime(timeTask, request.getTimeout());
    };
    /**
     * 发送连接请求。
     * @param rsfRequest rsf请求
     * @param listener FutureCallback回调监听器。
     * @return 返回RsfFuture。
     */
    public RsfFuture sendRequest(RsfRequestFormLocal rsfRequest, FutureCallback<RsfResponse> listener) {
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        RsfResponseFormLocal res = rsfRequest.buildResponse();
        //
        try {
            RsfBindInfo<?> bindInfo = rsfRequest.getBindInfo();
            ServiceDefine<?> rsfDefine = this.getRsfContext().getBindCenter().getService(bindInfo.getBindID());
            List<RsfFilter> rsfFilterList = rsfDefine.getFilters();
            //
            /*下面这段代码要负责 -> 执行rsfFilter过滤器链，并最终调用sendRequest发送请求。*/
            new RsfFilterHandler(rsfFilterList, new RsfFilterChain() {
                public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
                    sendRequest(rsfFuture);//发送请求到远方
                }
            }).doFilter(rsfRequest, res);
        } catch (Throwable e) {
            try {
                rsfFuture.failed(e);
            } catch (Throwable e2) {
                logger.error("do callback for failed error->" + e.getMessage(), e);
            }
        }
        //
        if (res.isResponse()) {
            try {
                rsfFuture.completed(res);
            } catch (Throwable e) {
                logger.error("do callback for completed error->" + e.getMessage(), e);
            }
        }
        return rsfFuture;
    }
    /**将请求发送到远端服务器。*/
    private void sendRequest(RsfFuture rsfFuture) throws Throwable {
        //发送之前的检查
        RsfSettings rsfSettings = this.getRsfContext().getSettings();
        if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
            SendLimitPolicy sendPolicy = rsfSettings.getSendLimitPolicy();
            String errorMessage = "maximum number of requests, apply SendPolicy = " + sendPolicy.name();
            logger.warn(errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                throw new RsfException(ProtocolStatus.ClientError, errorMessage);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
        //RsfFilter
        final RsfRequestFormLocal rsfRequest = (RsfRequestFormLocal) rsfFuture.getRequest();
        //查找远程服务地址
        final Channel rsfClient = this.getClientManager().getChannel(rsfRequest);
        final long beginTime = System.currentTimeMillis();
        final long timeout = rsfRequest.getTimeout();
        //
        if (rsfClient == null) {
            rsfFuture.failed(new IllegalStateException("The lack of effective service provider."));
            return;
        }
        //
        SerializeFactory factory = this.rsfContext.getSerializeFactory();
        RequestBlock block = rsfRequest.buildSocketBlock(factory);
        //
        this.startRequest(rsfFuture);/*应用 timeout 属性，避免在服务端无任何返回情况下一直无法除去request。*/
        ChannelFuture future = rsfClient.writeAndFlush(block);
        //
        /*为sendData添加侦听器，负责处理意外情况。*/
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    return;
                }
                String errorMsg = null;
                //超时
                if (System.currentTimeMillis() - beginTime >= timeout) {
                    errorMsg = "send request too long time(" + (System.currentTimeMillis() - beginTime) + "),requestID:" + rsfRequest.getRequestID();
                }
                //用户取消
                if (future.isCancelled()) {
                    errorMsg = "send request to cancelled by user,requestID:" + rsfRequest.getRequestID();
                }
                //异常状况
                if (!future.isSuccess()) {
                    errorMsg = "send request error " + future.cause();
                }
                logger.error(RsfClientRequestManager.this + ":" + errorMsg);
                //回应Response
                putResponse(rsfRequest.getRequestID(), new RsfException(ProtocolStatus.ClientError, errorMsg));
            }
        });
    }
}