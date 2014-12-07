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
package net.hasor.rsf.runtime.client;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.general.SendLimitPolicy;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfFilterChain;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.RsfSettings;
import net.hasor.rsf.runtime.common.NetworkConnection;
import net.hasor.rsf.runtime.common.RsfFilterHandler;
import net.hasor.rsf.runtime.common.RsfRequestImpl;
import net.hasor.rsf.runtime.common.RsfResponseImpl;
import net.hasor.rsf.runtime.common.RuntimeUtils;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
import org.more.classcode.delegate.faces.MethodClassConfig;
import org.more.future.FutureCallback;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
abstract class InnerAbstractRsfClient implements RsfClient {
    private RsfClientFactory                         clientFactory = null;
    private AbstractRsfContext                       rsfContext    = null;
    private final Map<String, String>                optionMap     = new HashMap<String, String>();
    private final ConcurrentHashMap<Long, RsfFuture> rsfResponse   = new ConcurrentHashMap<Long, RsfFuture>();
    private final Timer                              timer         = new HashedWheelTimer();
    private final AtomicInteger                      requestCount  = new AtomicInteger(0);
    //
    public InnerAbstractRsfClient(RsfClientFactory clientFactory, AbstractRsfContext rsfContext) {
        this.clientFactory = clientFactory;
        this.rsfContext = rsfContext;
    }
    //
    /**server address.*/
    public String getServerHost() {
        return this.getConnection().getRemotHost();
    }
    /**server port.*/
    public int getServerPort() {
        return this.getConnection().getRemotePort();
    }
    /**本地IP。*/
    public String getLocalHost() {
        return this.getConnection().getLocalHost();
    }
    /**本地端口。*/
    public int getLocalPort() {
        return this.getConnection().getLocalPort();
    }
    /**获取 {@link AbstractRsfContext}*/
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    /**获取{@link RsfClientFactory}*/
    protected RsfClientFactory getRsfClientFactory() {
        return this.clientFactory;
    }
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionMap.put(key, value);
    }
    /**关闭与远端的连接（异步）*/
    public Future<Void> close() {
        NetworkConnection conn = this.getConnection();
        this.getRsfClientFactory().removeChannelMapping(conn);
        return conn.close();
    }
    /**连接是否为活动的。*/
    public boolean isActive() {
        return this.getConnection().isActive();
    }
    /**获取正在进行中的调用请求。*/
    public RsfFuture getRequest(long requestID) {
        return this.rsfResponse.get(requestID);
    }
    //
    private Map<String, Class<?>> wrapperMap = new ConcurrentHashMap<String, Class<?>>();
    /**获取远程服务对象*/
    public <T> T getRemote(String serviceName) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        RsfSettings rsfSettings = this.getRsfContext().getSettings();
        return this.getRemote(serviceName, rsfSettings.getDefaultGroup(), rsfSettings.getDefaultVersion());
    }
    /**获取远程服务对象*/
    public <T> T getRemote(String serviceName, String group, String version) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        ServiceMetaData<T> service = this.getRsfContext().getService(serviceName, group, version);
        return (T) wrapper(serviceName, service.getServiceType());
    }
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String serviceName, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        RsfSettings rsfSettings = this.getRsfContext().getSettings();
        return this.wrapper(serviceName, rsfSettings.getDefaultGroup(), rsfSettings.getDefaultVersion(), interFace);
    }
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String serviceName, String group, String version, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        Hasor.assertIsNotNull(serviceName, "serviceName is null.");
        Hasor.assertIsNotNull(interFace, "interFace is null.");
        if (interFace.isInterface() == false)
            throw new UnsupportedOperationException("interFace parameter must be an interFace.");
        //
        ServiceMetaData<?> service = this.getRsfContext().getService(serviceName, group, version);
        String cacheKey = service.toString() + interFace.getName();
        Class<?> wrapperType = this.wrapperMap.get(cacheKey);
        //
        if (wrapperType == null) {
            MethodClassConfig mcc = new MethodClassConfig();
            mcc.addDelegate(interFace, new RemoteWrapper(service, this));
            wrapperType = mcc.toClass();
            this.wrapperMap.put(cacheKey, wrapperType);
        }
        return (T) wrapperType.newInstance();
    }
    //
    /**同步方式调用远程服务。*/
    public Object syncInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        //1.准备Request
        int timeout = validateTimeout(metaData.getClientTimeout());
        RsfRequestImpl request = RuntimeUtils.buildRequest(metaData, this.getConnection(), this,//
                methodName, parameterTypes, parameterObjects);
        //2.发起Request
        RsfFuture rsfFuture = this.sendRequest(request, null);
        //3.返回数据
        RsfResponse response = rsfFuture.get(timeout, TimeUnit.MILLISECONDS);
        return response.getResponseData();
    }
    /**异步方式调用远程服务。*/
    public RsfFuture asyncInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        //1.准备Request
        RsfRequestImpl request = RuntimeUtils.buildRequest(metaData, this.getConnection(), this,//
                methodName, parameterTypes, parameterObjects);
        //2.发起Request
        return this.sendRequest(request, null);
    }
    /**以回调方式调用远程服务。*/
    public void doCallBackInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, final FutureCallback<Object> listener) {
        this.doCallBackRequest(metaData, methodName, parameterTypes, parameterObjects, new FutureCallback<RsfResponse>() {
            public void completed(RsfResponse result) {
                listener.completed(result.getResponseData());
            }
            public void failed(Throwable ex) {
                listener.failed(ex);
            }
            public void cancelled() {
                listener.cancelled();
            }
        });
    }
    /**以回调方式调用远程服务。*/
    public void doCallBackRequest(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, final FutureCallback<RsfResponse> listener) {
        //1.准备Request
        RsfRequestImpl request = RuntimeUtils.buildRequest(metaData, this.getConnection(), this,//
                methodName, parameterTypes, parameterObjects);
        //2.发起Request
        this.sendRequest(request, listener);
    }
    public String toString() {
        StringBuilder sb = new StringBuilder("Rsf Client -");
        sb.append("Local=" + getLocalHost() + ":" + this.getLocalPort());
        sb.append(", Remote=" + getServerHost() + ":" + this.getServerPort());
        sb.append(", Status=" + (this.isActive() ? "Connected" : "DisConnected"));
        return sb.toString();
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
    protected void putResponse(long requestID, RsfResponse response) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            rsfFuture.completed(response);
        } else {
            Hasor.logWarn(this + "give up the response,requestID:" + requestID + " ,maybe because timeout! ");
        }
    }
    /**收到Response响应。*/
    protected void putError(long requestID, Throwable e) {
        RsfFuture rsfFuture = this.removeRsfFuture(requestID);
        if (rsfFuture != null) {
            rsfFuture.failed(e);
        } else {
            Hasor.logWarn(this + "give up the response,requestID:" + requestID + " ,maybe because timeout! ");
        }
    }
    /**要求重新发起请求*/
    protected void tryAgain(long requestID) {
        this.putError(requestID, new RsfException(ProtocolStatus.ChooseOther, "Server response  ChooseOther!"));
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
                RsfFuture rsfCallBack = InnerAbstractRsfClient.this.getRequest(request.getRequestID());
                if (rsfCallBack == null)
                    return;
                //引发超时Response
                String errorInfo = "timeout is reached on client side:" + request.getTimeout();
                Hasor.logWarn(errorInfo);
                //回应Response
                InnerAbstractRsfClient.this.putError(request.getRequestID(), //
                        new RsfException(ProtocolStatus.RequestTimeout, errorInfo));
            }
        };
        this.timer.newTimeout(timeTask, request.getTimeout(), TimeUnit.MILLISECONDS);
    };
    /**发送连接请求。*/
    private RsfFuture sendRequest(RsfRequestImpl rsfRequest, FutureCallback<RsfResponse> listener) {
        final RsfFuture rsfFuture = new RsfFuture(rsfRequest, listener);
        RsfRequestImpl req = (RsfRequestImpl) rsfFuture.getRequest();
        RsfResponseImpl res = req.buildResponse();
        //
        try {
            ServiceMetaData<?> metaData = req.getMetaData();
            Provider<RsfFilter>[] rsfFilter = this.rsfContext.getRegisterCenter().getFilters(metaData);
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
            Hasor.logWarn(this + ": " + errorMessage);
            if (sendPolicy == SendLimitPolicy.Reject) {
                throw new RsfException(ProtocolStatus.ClientError, errorMessage);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }
        if (this.isActive() == false) {
            throw new IllegalStateException("client is closed.");
        }
        //RsfFilter
        final RsfRequestImpl request = (RsfRequestImpl) rsfFuture.getRequest();
        final RequestMsg rsfMessage = request.getMsg();
        final long beginTime = System.currentTimeMillis();
        final long timeout = rsfMessage.getClientTimeout();
        //
        this.startRequest(rsfFuture);/*应用 timeout 属性，避免在服务端无任何返回情况下一直无法除去request。*/
        ChannelFuture future = this.getConnection().getChannel().writeAndFlush(rsfMessage);
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
                    if (getConnection().isActive()) {
                        InnerAbstractRsfClient.this.close();// maybe some exception, so close the channel
                    }
                    errorMsg = "send request error " + future.cause();
                }
                Hasor.logError(InnerAbstractRsfClient.this + ":" + errorMsg);
                //回应Response
                InnerAbstractRsfClient.this.putError(request.getRequestID(), //
                        new RsfException(ProtocolStatus.ClientError, errorMsg));
            }
        });
    }
    /**获取网络连接。*/
    protected abstract NetworkConnection getConnection();
}