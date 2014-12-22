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
package net.hasor.rsf.remoting.transport.customer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.RsfFilterChain;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.adapter.AbstractfRsfClient;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.transport.component.RsfFilterHandler;
import net.hasor.rsf.remoting.transport.component.RsfRequestImpl;
import net.hasor.rsf.remoting.transport.component.RsfResponseImpl;
import net.hasor.rsf.remoting.transport.connection.ConnectionFactory;
import net.hasor.rsf.remoting.transport.connection.NetworkConnection;
import net.hasor.rsf.remoting.transport.protocol.message.RequestMsg;
import org.more.future.FutureCallback;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRsfP2PClient extends AbstractfRsfClient {
    private ConnectionFactory                        connectionFactory = null;
    private final ConcurrentHashMap<Long, RsfFuture> rsfResponse       = new ConcurrentHashMap<Long, RsfFuture>();
    private final Timer                              timer             = new HashedWheelTimer();
    private final AtomicInteger                      requestCount      = new AtomicInteger(0);
    //
    public InnerRsfP2PClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }
    /**获取 {@link AbstractRsfContext}*/
    public AbstractRsfContext getRsfContext() {
        return this.connectionFactory.getRsfContext();
    }
    /**获取正在进行中的调用请求。*/
    public RsfFuture getRequest(long requestID) {
        return this.rsfResponse.get(requestID);
    }
    //
    private int validateTimeout(int timeout) {
        if (timeout <= 0)
            timeout = this.getRsfContext().getSettings().getDefaultTimeout();
        return timeout;
    }
    private RsfFuture removeRsfFuture(long requestID) {
        RsfFuture rsfFuture = this.rsfResponse.remove(requestID);
        if (rsfFuture != null) {
            this.requestCount.decrementAndGet();// i--;
        }
        return rsfFuture;
    }
    /**收到Response响应。*/
    public void putResponse(long requestID, RsfResponse response) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            rsfFuture.completed(response);
        } else {
            Hasor.logWarn("give up the response,requestID:" + requestID + " ,maybe because timeout! ");
        }
    }
    /**收到Response响应。*/
    public void putResponse(long requestID, Throwable e) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            rsfFuture.failed(e);
        } else {
            Hasor.logWarn("give up the response,requestID:" + requestID + " ,maybe because timeout! ");
        }
    }
    /**要求重新发起请求*/
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
                RsfFuture rsfCallBack = InnerRsfP2PClient.this.getRequest(request.getRequestID());
                if (rsfCallBack == null)
                    return;
                //引发超时Response
                String errorInfo = "timeout is reached on client side:" + request.getTimeout();
                Hasor.logWarn(errorInfo);
                //回应Response
                InnerRsfP2PClient.this.putResponse(request.getRequestID(), //
                        new RsfException(ProtocolStatus.RequestTimeout, errorInfo));
            }
        };
        //
        int reqTimeout = validateTimeout(request.getTimeout());
        this.timer.newTimeout(timeTask, reqTimeout, TimeUnit.MILLISECONDS);
    };
    /**发送连接请求。*/
    public RsfFuture sendRequest(RsfRequest rsfRequest, FutureCallback<RsfResponse> listener) {
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        RsfRequestImpl req = (RsfRequestImpl) rsfFuture.getRequest();
        RsfResponseImpl res = req.buildResponse();
        //
        try {
            RsfBindInfo<?> bindInfo = req.getBindInfo();
            Provider<RsfFilter>[] rsfFilter = this.getRsfContext().getFilters(bindInfo);
            new RsfFilterHandler(rsfFilter, new RsfFilterChain() {
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
            Hasor.logWarn(errorMessage);
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
        final NetworkConnection netConnection = this.connectionFactory.getConnection(request.getBindInfo(), this);
        final long beginTime = System.currentTimeMillis();
        final long timeout = rsfMessage.getClientTimeout();
        //
        if (netConnection == null) {
            rsfFuture.failed(new IllegalStateException("The lack of effective service provider."));
            return;
        }
        if (netConnection.isActive() == false) {
            rsfFuture.failed(new IllegalStateException("client is closed."));
            return;
        }
        this.startRequest(rsfFuture);/*应用 timeout 属性，避免在服务端无任何返回情况下一直无法除去request。*/
        //
        ChannelFuture future = netConnection.getChannel().writeAndFlush(rsfMessage);
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
                    if (netConnection.isActive()) {
                        // maybe some exception, so close the channel
                        InnerRsfP2PClient.this.connectionFactory.closeChannel(netConnection);
                    }
                    errorMsg = "send request error " + future.cause();
                }
                Hasor.logError(InnerRsfP2PClient.this + ":" + errorMsg);
                //回应Response
                InnerRsfP2PClient.this.putResponse(request.getRequestID(), //
                        new RsfException(ProtocolStatus.ClientError, errorMsg));
            }
        });
    }
}