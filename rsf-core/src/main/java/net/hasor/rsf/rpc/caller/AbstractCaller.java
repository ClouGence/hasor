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
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.more.future.FutureCallback;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.container.RsfBeanContainer;
import net.hasor.rsf.domain.RsfException;
/**
 * 
 * @version : 2015年12月8日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractCaller {
    private final RsfContext rsfContext;
    public AbstractCaller(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    public RsfContext getContext() {
        return this.rsfContext;
    }
    //
    public <T> T getRemoteByID(InterAddress target, String serviceID) throws RsfException {
        RsfBindInfo<?> bindInfo = this.getContainer().getRsfBindInfo(serviceID);
        if (bindInfo == null)
            return null;
        return (T) this.wrapper(target, bindInfo, bindInfo.getBindType());
    }
    public <T> T getRemote(InterAddress target, String group, String name, String version) throws RsfException {
        RsfBindInfo<?> bindInfo = this.getContainer().getRsfBindInfo(group, name, version);
        if (bindInfo == null)
            return null;
        return (T) this.getRemote(target, bindInfo);
    }
    public <T> T getRemote(InterAddress target, RsfBindInfo<T> bindInfo) throws RsfException {
        return this.wrapper(target, bindInfo, bindInfo.getBindType());
    }
    public <T> T wrapperByID(InterAddress target, String serviceID, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.getContainer().getRsfBindInfo(serviceID);
        if (bindInfo == null)
            return null;
        return this.wrapper(target, bindInfo, interFace);
    }
    public <T> T wrapper(InterAddress target, Class<T> interFace) throws RsfException {
        RsfBindInfo<T> bindInfo = this.getContainer().getRsfBindInfo(interFace);
        if (bindInfo == null)
            return null;
        return this.wrapper(target, bindInfo, interFace);
    }
    public <T> T wrapper(InterAddress target, String group, String name, String version, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.getContainer().getRsfBindInfo(group, name, version);
        if (bindInfo == null)
            return null;
        return this.wrapper(target, bindInfo, interFace);
    }
    public abstract <T> T wrapper(InterAddress target, RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException;
    //
    //
    //
    //
    //
    //
    public Object syncInvoke(InterAddress target, RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        //1.准备Request
        int timeout = validateTimeout(bindInfo.getClientTimeout());
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(target, bindInfo, targetMethod, parameterObjects, this);
        //2.发起Request
        RsfFuture rsfFuture = doSendRequest(request, null);
        //3.返回数据
        RsfResponse response = rsfFuture.get(timeout, TimeUnit.MILLISECONDS);
        return response.getResponseData();
    }
    public RsfFuture asyncInvoke(InterAddress target, RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        //1.准备Request
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(target, bindInfo, targetMethod, parameterObjects, this);
        //2.发起Request
        return doSendRequest(request, null);
    }
    public void doCallBackInvoke(InterAddress target, RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, final FutureCallback<Object> listener) {
        this.doCallBackRequest(target, bindInfo, methodName, parameterTypes, parameterObjects, new FutureCallback<RsfResponse>() {
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
    public void doCallBackRequest(InterAddress target, RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        //1.准备Request
        Method targetMethod = RsfRuntimeUtils.getServiceMethod(bindInfo.getBindType(), methodName, parameterTypes);
        RsfRequestFormLocal request = new RsfRequestFormLocal(target, bindInfo, targetMethod, parameterObjects, this);
        //2.发起Request
        doSendRequest(request, listener);
    }
    private int validateTimeout(int timeout) {
        if (timeout <= 0)
            timeout = this.getContext().getSettings().getDefaultTimeout();
        return timeout;
    }
    //
    protected abstract RsfBeanContainer getContainer();
    protected abstract RsfFuture doSendRequest(RsfRequestFormLocal request, FutureCallback<RsfResponse> listener);
}