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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.constants.RsfTimeoutException;
import net.hasor.rsf.manager.TimerManager;
import net.hasor.rsf.remoting.transport.component.RsfFilterHandler;
import net.hasor.rsf.remoting.transport.component.RsfRequestImpl;
import net.hasor.rsf.remoting.transport.component.RsfResponseImpl;
import net.hasor.rsf.remoting.transport.protocol.message.RequestMsg;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.more.future.FutureCallback;
import org.more.logger.LoggerHelper;
/**
 * 负责管理所有 RSF 发起的请求。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestManager {
    private final AbstractRsfContext                 rsfContext;
    private final InnerClientManager                 clientManager;
    private final ConcurrentHashMap<Long, RsfFuture> rsfResponse;
    private final TimerManager                       timerManager;
    private final AtomicInteger                      requestCount;
    //
    public RsfRequestManager(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.clientManager = new InnerClientManager(this);
        this.rsfResponse = new ConcurrentHashMap<Long, RsfFuture>();
        this.timerManager = new TimerManager(getRsfContext().getSettings().getDefaultTimeout());
        this.requestCount = new AtomicInteger(0);
    }
    /** @return 获取{@link RsfContext}*/
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    /** @return 获取客户端管理器*/
    public InnerClientManager getClientManager() {
        return this.clientManager;
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
            LoggerHelper.logFinest("received response(%s) status = %s", requestID, response.getResponseStatus());
            rsfFuture.completed(response);
        } else {
            LoggerHelper.logWarn("give up the response,requestID(%s) ,maybe because timeout! ", requestID);
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
            LoggerHelper.logSevere("received error ,requestID(%s) status = %s", requestID, e.getMessage());
            rsfFuture.failed(e);
        } else {
            LoggerHelper.logWarn("give up the response,requestID(%s) ,maybe because timeout! ", requestID);
        }
    }
    /**
     * 尝试再次发送Request请求（如果request已经超时则无效）。
     * @param requestID 请求ID
     */
    public void tryAgain(long requestID) {
        this.putResponse(requestID, new RsfException(ProtocolStatus.ChooseOther, "Server response  ChooseOther!"));
        System.out.println("RequestID:" + requestID + " -> ChooseOther"); //TODO
    }
    //
    /**负责客户端引发的超时逻辑。*/
    private void startRequest(RsfFuture rsfFuture) {
        this.requestCount.incrementAndGet();// i++;
        this.rsfResponse.put(rsfFuture.getRequest().getRequestID(), rsfFuture);
        final RsfRequestImpl request = (RsfRequestImpl) rsfFuture.getRequest();
        TimerTask timeTask = new TimerTask() {
            public void run(Timeout timeoutObject) throws Exception {
                //超时检测
                RsfFuture rsfCallBack = RsfRequestManager.this.getRequest(request.getRequestID());
                if (rsfCallBack == null)
                    return;
                //引发超时Response
                String errorInfo = "timeout is reached on client side:" + request.getTimeout();
                LoggerHelper.logWarn(errorInfo);
                //回应Response
                RsfRequestManager.this.putResponse(request.getRequestID(), new RsfTimeoutException(errorInfo));
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
    public RsfFuture sendRequest(RsfRequest rsfRequest, FutureCallback<RsfResponse> listener) {
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        RsfRequestImpl req = (RsfRequestImpl) rsfFuture.getRequest();
        RsfResponseImpl res = req.buildResponse();
        //
        try {
            RsfBindInfo<?> bindInfo = req.getBindInfo();
            Provider<RsfFilter>[] rsfFilter = this.getRsfContext().getFilters(bindInfo);
            /*下面这段代码要负责 -> 执行rsfFilter过滤器链，并最终调用sendRequest发送请求。*/
            new InnterRsfFilterHandler(rsfFilter, new RsfFilterChain() {
                public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
                    sendRequest(rsfFuture);//发送请求到远方
                }
            }).doFilter(req, res);
        } catch (Throwable e) {
            rsfFuture.failed(e);
        }
        //
        if (res.isResponse()) {
            rsfFuture.completed(res);
        }
        return rsfFuture;
    }
    /**将请求发送到远端服务器。*/
    private void sendRequest(RsfFuture rsfFuture) {
        //发送之前的检查
        RsfSettings rsfSettings = this.getRsfContext().getSettings();
        if (this.requestCount.get() >= rsfSettings.getMaximumRequest()) {
            SendLimitPolicy sendPolicy = rsfSettings.getSendLimitPolicy();
            String errorMessage = "maximum number of requests, apply SendPolicy = " + sendPolicy.name();
            LoggerHelper.logWarn(errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                throw new RsfException(ProtocolStatus.ClientError, errorMessage);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
        //RsfFilter
        final RsfRequestImpl request = (RsfRequestImpl) rsfFuture.getRequest();
        final RequestMsg rsfMessage = request.getMsg();
        //查找远程服务地址
        final AbstractRsfClient rsfClient = this.getClientManager().getClient(request.getBindInfo());
        final long beginTime = System.currentTimeMillis();
        final long timeout = rsfMessage.getClientTimeout();
        //
        if (rsfClient == null) {
            rsfFuture.failed(new IllegalStateException("The lack of effective service provider."));
            return;
        }
        if (rsfClient.isActive() == false) {
            rsfFuture.failed(new IllegalStateException("client is closed."));
            return;
        }
        this.startRequest(rsfFuture);/*应用 timeout 属性，避免在服务端无任何返回情况下一直无法除去request。*/
        //
        ChannelFuture future = rsfClient.getChannel().writeAndFlush(rsfMessage);
        /*为sendData添加侦听器，负责处理意外情况。*/
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    return;
                }
                String errorMsg = null;
                //超时
                if (System.currentTimeMillis() - beginTime >= timeout) {
                    errorMsg = "send request too long time(" + (System.currentTimeMillis() - beginTime) + "),requestID:" + rsfMessage.getRequestID();
                }
                //用户取消
                if (future.isCancelled()) {
                    errorMsg = "send request to cancelled by user,requestID:" + rsfMessage.getRequestID();
                }
                //异常状况
                if (!future.isSuccess()) {
                    if (rsfClient.isActive()) {
                        // maybe some exception, so close the channel
                        getClientManager().unRegistered(rsfClient.getHostAddress());
                    }
                    errorMsg = "send request error " + future.cause();
                }
                LoggerHelper.logSevere(RsfRequestManager.this + ":" + errorMsg);
                //回应Response
                putResponse(request.getRequestID(), new RsfException(ProtocolStatus.ClientError, errorMsg));
            }
        });
    }
    //
    private class InnterRsfFilterHandler extends RsfFilterHandler {
        public InnterRsfFilterHandler(Provider<RsfFilter>[] rsfFilters, RsfFilterChain rsfChain) {
            super(rsfFilters, rsfChain);
        }
        public void doFilter(RsfRequest request, RsfResponse response) throws Throwable {
            try {
                super.doFilter(request, response);
            } finally {}
        }
    }
}