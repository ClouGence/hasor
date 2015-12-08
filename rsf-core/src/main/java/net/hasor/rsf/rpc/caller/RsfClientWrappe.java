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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.more.classcode.MoreClassLoader;
import org.more.future.FutureCallback;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
/**
 * 
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfClientWrappe implements RsfClient {
    private final AbstractRsfContext            rsfContext;
    private final RsfBeanContainer              rsfBeanContainer;
    private final Object                        LOCK_OBJECT;
    private final ConcurrentMap<String, Object> wrapperMap;
    //
    public RsfClientWrappe(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfBindCenter = rsfContext.getBindCenter();
        this.LOCK_OBJECT = new Object();
        this.wrapperMap = new ConcurrentHashMap<String, Object>();
    }
    //
    @Override
    public <T> T getRemoteByID(String serviceID) throws RsfException {
        RsfBindInfo<?> bindInfo = this.rsfBeanContainer.getRsfBindInfo(serviceID);
        if (bindInfo == null)
            return null;
        return (T) this.wrapper(bindInfo, bindInfo.getBindType());
    }
    @Override
    public <T> T getRemote(String group, String name, String version) throws RsfException {
        RsfBindInfo<?> bindInfo = this.rsfBeanContainer.getRsfBindInfo(group, name, version);
        if (bindInfo == null)
            return null;
        return (T) this.getRemote(bindInfo);
    }
    @Override
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws RsfException {
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    @Override
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.rsfBeanContainer.getRsfBindInfo(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapper(Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBeanContainer.getRsfBindInfo(interFace);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.rsfBeanContainer.getRsfBindInfo(group, name, version);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException {
        if (bindInfo == null)
            throw new NullPointerException();
        if (interFace.isInterface() == false)
            throw new UnsupportedOperationException("interFace parameter must be an interFace.");
        //
        String bindID = bindInfo.getBindID();
        Object wrapperObject = this.wrapperMap.get(bindID);
        if (wrapperObject == null) {
            synchronized (LOCK_OBJECT) {
                wrapperObject = this.wrapperMap.get(bindID);
                if (wrapperObject == null) {
                    ClassLoader loader = new MoreClassLoader();
                    wrapperObject = Proxy.newProxyInstance(loader, new Class<?>[] { interFace }, new RemoteWrapper(bindInfo, this));
                    this.wrapperMap.put(bindID, wrapperObject);
                }
            }
        }
        return (T) wrapperObject;
        //
    }
    @Override
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        //1.准备Request
        int timeout = validateTimeout(bindInfo.getClientTimeout());
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(bindInfo, targetMethod, parameterObjects, this.rsfContext);
        //2.发起Request
        RsfFuture rsfFuture = doSendRequest(request, null);
        //3.返回数据
        RsfResponse response = rsfFuture.get(timeout, TimeUnit.MILLISECONDS);
        return response.getResponseData();
    }
    @Override
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        //1.准备Request
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(bindInfo, targetMethod, parameterObjects, this.rsfContext);
        //2.发起Request
        return doSendRequest(request, null);
    }
    @Override
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
    @Override
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        //1.准备Request
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(bindInfo, targetMethod, parameterObjects, this.rsfContext);
        //2.发起Request
        doSendRequest(request, listener);
    }
    protected RsfFuture doSendRequest(RsfRequestFormLocal request, FutureCallback<RsfResponse> listener) {
        RsfClientRequestManager reqManager = this.rsfContext.getRequestManager();
        return reqManager.sendRequest(request, listener);
    }
    private int validateTimeout(int timeout) {
        if (timeout <= 0)
            timeout = this.rsfContext.getSettings().getDefaultTimeout();
        return timeout;
    }
    private static class RemoteWrapper implements InvocationHandler {
        private RsfBindInfo<?> bindInfo = null;
        private RsfClient      client   = null;
        //
        public RemoteWrapper(RsfBindInfo<?> bindInfo, RsfClient client) {
            this.bindInfo = bindInfo;
            this.client = client;
        }
        public Object invoke(Object proxy, Method callMethod, Object[] args) throws Throwable {
            return this.client.syncInvoke(this.bindInfo, callMethod.getName(), callMethod.getParameterTypes(), args);
        }
    }
}