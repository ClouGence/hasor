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
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.binder.RsfBindCenter;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.objects.local.RsfRequestFormLocal;
import net.hasor.rsf.utils.RsfRuntimeUtils;
import org.more.classcode.delegate.faces.MethodClassConfig;
import org.more.classcode.delegate.faces.MethodDelegate;
import org.more.future.FutureCallback;
/**
 * 
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfClientWrappe implements RsfClient {
    private final AbstractRsfContext              rsfContext;
    private final RsfBindCenter                   rsfBindCenter;
    private final Object                          LOCK_OBJECT;
    private final ConcurrentMap<String, Class<?>> wrapperMap;
    //
    public RsfClientWrappe(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.rsfBindCenter = rsfContext.getBindCenter();
        this.LOCK_OBJECT = new Object();
        this.wrapperMap = new ConcurrentHashMap<String, Class<?>>();
    }
    //
    @Override
    public <T> T getRemoteByID(String serviceID) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getService(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    @Override
    public <T> T getRemoteByName(String serviceName) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getServiceByName(serviceName);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    @Override
    public <T> T getRemote(String group, String name, String version) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getService(group, name, version);
        if (bindInfo == null)
            return null;
        return this.getRemote(bindInfo);
    }
    @Override
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws RsfException {
        return this.wrapper(bindInfo, bindInfo.getBindType());
    }
    @Override
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getService(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapperByName(String serviceName, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getServiceByName(serviceName);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapper(Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getService(interFace);
        if (bindInfo == null)
            return null;
        return this.wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.rsfBindCenter.getService(group, name, version);
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
    @Override
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        //1.准备Request
        int timeout = validateTimeout(bindInfo.getClientTimeout());
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(bindInfo, targetMethod, parameterObjects, this.rsfContext);
        //2.发起Request
        RsfClientRequestManager reqManager = this.rsfContext.getRequestManager();
        RsfFuture rsfFuture = reqManager.sendRequest(request, null);
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
        RsfClientRequestManager reqManager = this.rsfContext.getRequestManager();
        return reqManager.sendRequest(request, null);
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
        RsfClientRequestManager reqManager = this.rsfContext.getRequestManager();
        reqManager.sendRequest(request, listener);
    }
    private int validateTimeout(int timeout) {
        if (timeout <= 0)
            timeout = this.rsfContext.getSettings().getDefaultTimeout();
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