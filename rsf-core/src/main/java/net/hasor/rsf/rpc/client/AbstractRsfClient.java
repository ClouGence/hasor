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
import io.netty.channel.Channel;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.classcode.delegate.faces.MethodClassConfig;
import org.more.classcode.delegate.faces.MethodDelegate;
import org.more.future.FutureCallback;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfClient implements RsfClient {
    /** @return 客户端连接是否激活*/
    public abstract boolean isActive();
    /**关闭连接*/
    public abstract void close();
    /** @return Netty的管道*/
    public abstract Channel getChannel();
    /** @return 远程服务地址*/
    public abstract URL getHostAddress();
    /** @return 获取请求管理器*/
    public abstract RsfRequestManager getRequestManager();
    //
    //
    //
    /** @return 获取上下文*/
    public AbstractRsfContext getRsfContext() {
        return this.getRequestManager().getRsfContext();
    }
    /**
     * 根据服务ID，获取远程服务对象
     * @param serviceID 服务ID
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemoteByID(String serviceID) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getServiceByID(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    /**
     * 根据服务名，获取远程服务对象。服务版本、服务分组将使用默认值。
     * @param serviceName 服务名
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemoteByName(String serviceName) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getServiceByName(serviceName);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    /**
     * 获取远程服务对象。
     * @param group 分组
     * @param name 服务名
     * @param version 版本
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemote(String group, String name, String version) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getService(group, name, version);
        if (bindInfo == null)
            return null;
        return this.getRemote(bindInfo);
    }
    /**
     * 获取远程服务对象
     * @param bindInfo rsf服务注册信息。
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws RsfException {
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    /**
     * 将服务包装为另外一个接口然后返回。
     * @param serviceID 服务ID
     * @param interFace 要装成为的接口
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getServiceByID(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    /**
     * 将服务包装为另外一个接口。
     * @param serviceName 服务名
     * @param interFace 包装成为的服务接口
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapperByName(String serviceName, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getServiceByName(serviceName);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    /**
     * 将服务包装为另外一个接口。
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.getRsfContext().getBindCenter().getService(interFace);
        return this.wrapper(bindInfo, interFace);
    }
    /**
     * 将服务包装为另外一个接口。
     * @param group 分组
     * @param name 服务名
     * @param version 版本
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getRsfContext().getBindCenter().getService(group, name, version);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    //
    private ConcurrentMap<String, Class<?>> wrapperMap  = new ConcurrentHashMap<String, Class<?>>();
    private final static Object             LOCK_OBJECT = new Object();
    /**
     * 将服务包装为另外一个接口。
     * @param bindInfo rsf服务注册信息。
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException {
        if (bindInfo == null)
            throw new NullPointerException();
        if (interFace.isInterface() == false)
            throw new UnsupportedOperationException("interFace parameter must be an interFace.");
        //
        try {
            //
            String bindID = bindInfo.getBindID();
            Class<?> wrapperType = this.wrapperMap.get(bindID);
            if (wrapperType == null)
                synchronized (LOCK_OBJECT) {
                    wrapperType = this.wrapperMap.get(bindID);
                    if (wrapperType == null) {
                        MethodClassConfig mcc = new MethodClassConfig();
                        mcc.addDelegate(interFace, new RemoteWrapper(bindInfo, this));
                        wrapperType = mcc.toClass();
                        this.wrapperMap.put(bindID, wrapperType);
                    }
                }
            return (T) wrapperType.newInstance();
            //
        } catch (Exception e) {
            throw new RsfException(e.getMessage(), e);
        }
    }
    //
    /**
     * 同步方式调用远程服务。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @return 返回执行结果
     * @throws Throwable 同步执行期间遇到的错误。
     */
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        RsfRequestManager reqManager = this.getRequestManager();
        //1.准备Request
        int timeout = validateTimeout(bindInfo.getClientTimeout());
        RsfRequest request = RsfRuntimeUtils.buildRequest(bindInfo, reqManager, methodName, parameterTypes, parameterObjects);
        //2.发起Request
        RsfFuture rsfFuture = reqManager.sendRequest(request, null);
        //3.返回数据
        RsfResponse response = rsfFuture.get(timeout, TimeUnit.MILLISECONDS);
        return response.getResponseData();
    }
    /**
     * 异步方式调用远程服务。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @return 返回异步执行结果
     */
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        RsfRequestManager reqManager = this.getRequestManager();
        //1.准备Request
        RsfRequest request = RsfRuntimeUtils.buildRequest(bindInfo, reqManager, methodName, parameterTypes, parameterObjects);
        //2.发起Request
        return reqManager.sendRequest(request, null);
    }
    /**
     * 以回调方式调用远程服务。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @param listener 回调监听器。
     */
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, final FutureCallback<Object> listener) {
        this.doCallBackRequest(bindInfo, methodName, parameterTypes, parameterObjects, new FutureCallback<RsfResponse>() {
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
    /**
     * 以回调方式发送RSF调用请求。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @param listener 回调监听器。
     */
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, final FutureCallback<RsfResponse> listener) {
        RsfRequestManager reqManager = this.getRequestManager();
        //1.准备Request
        RsfRequest request = RsfRuntimeUtils.buildRequest(bindInfo, reqManager, methodName, parameterTypes, parameterObjects);
        //2.发起Request
        reqManager.sendRequest(request, listener);
    }
    //
    private int validateTimeout(int timeout) {
        if (timeout <= 0)
            timeout = this.getRsfContext().getSettings().getDefaultTimeout();
        return timeout;
    }
    private static class RemoteWrapper implements MethodDelegate {
        private RsfBindInfo<?> bindInfo = null;
        private RsfClient      client   = null;
        //
        public RemoteWrapper(RsfBindInfo<?> bindInfo, RsfClient client) {
            this.bindInfo = bindInfo;
            this.client = client;
        }
        public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
            return this.client.syncInvoke(this.bindInfo, callMethod.getName(), callMethod.getParameterTypes(), params);
        }
    }
}