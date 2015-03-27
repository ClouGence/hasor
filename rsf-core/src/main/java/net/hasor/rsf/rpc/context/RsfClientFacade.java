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
package net.hasor.rsf.rpc.context;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.binder.RsfServiceInfo;
import net.hasor.rsf.rpc.client.InnerClientManager;
import net.hasor.rsf.utils.RuntimeUtils;
import org.more.future.FutureCallback;
import org.more.logger.LoggerHelper;
/**
 * 
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfClientFacade implements RsfClient {
    private AbstractRsfContext rsfContext;
    //
    public RsfClientFacade(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    //
    protected <T> RsfBindInfo<T> findBindInfo(String group, String name, String version) {
        String serviceID = RuntimeUtils.bindID(group, name, version);
        return this.findBindInfoByID(serviceID);
    }
    protected <T> RsfBindInfo<T> findBindInfoByID(String serviceID) {
        return this.rsfContext.getBindCenter().getServiceByID(serviceID);
    }
    protected <T> RsfBindInfo<T> findBindInfoByName(String serviceName) {
        return this.rsfContext.getBindCenter().getServiceByName(serviceName);
    }
    protected RsfClient findRsfClient(RsfBindInfo<?> bindInfo) {
        if (bindInfo == null) {
            NullPointerException npe = new NullPointerException("bindInfo is null. ");
            LoggerHelper.logSevere(npe.getMessage(), npe);
            throw npe;
        }
        InnerClientManager clientManager = this.rsfContext.getRequestManager().getClientManager();
        return clientManager.getClient(bindInfo);
    }
    //
    @Override
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.findBindInfoByID(serviceID);
        return this.findRsfClient(bindInfo).wrapper(bindInfo, interFace);
    }
    @Override
    public <T> T wrapperByName(String serviceName, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.findBindInfoByName(serviceName);
        return this.wrapper(bindInfo, interFace);
    }
    public <T> T wrapper(Class<T> interFace) throws RsfException {
        RsfServiceInfo info = new RsfServiceInfo(this.rsfContext, interFace);
        RsfBindInfo<?> bindInfo = this.findBindInfo(info.group(), info.name(), info.version());
        return this.wrapper(bindInfo, interFace);
    }
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws RsfException {
        RsfBindInfo<?> bindInfo = this.findBindInfo(group, name, version);
        return this.wrapper(bindInfo, interFace);
    }
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException {
        return this.findRsfClient(bindInfo).wrapper(bindInfo, interFace);
    }
    //
    public <T> T getRemoteByName(String serviceName) throws RsfException {
        return (T) this.getRemote(this.findBindInfoByName(serviceName));
    }
    public <T> T getRemoteByID(String serviceID) throws RsfException {
        return (T) this.getRemote(this.findBindInfoByID(serviceID));
    }
    public <T> T getRemote(String group, String name, String version) throws RsfException {
        return (T) getRemote(this.findBindInfo(group, name, version));
    }
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws RsfException {
        return this.findRsfClient(bindInfo).getRemote(bindInfo);
    }
    //
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        return this.findRsfClient(this.findBindInfoByID(bindInfo.getBindID()))//
                .syncInvoke(bindInfo, methodName, parameterTypes, parameterObjects);
    }
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        return this.findRsfClient(this.findBindInfoByID(bindInfo.getBindID()))//
                .asyncInvoke(bindInfo, methodName, parameterTypes, parameterObjects);
    }
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener) {
        this.findRsfClient(this.findBindInfoByID(bindInfo.getBindID()))//
                .doCallBackInvoke(bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        this.findRsfClient(this.findBindInfoByID(bindInfo.getBindID()))//
                .doCallBackRequest(bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
}