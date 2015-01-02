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
import java.io.IOException;
import java.net.URL;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.adapter.AbstractClientManager;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.utils.RuntimeUtils;
import org.more.future.FutureCallback;
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
    protected URL findURL(String group, String name, String version) {
        String serviceID = RuntimeUtils.bindID(group, name, version);
        return this.findURL(serviceID);
    }
    protected URL findURL(String serviceID) {
        RsfBindInfo<?> bindInfo = this.rsfContext.getBindCenter().getService(serviceID);
        if (bindInfo == null)
            return null;
        return this.rsfContext.getAddressCenter().findHostAddress(bindInfo);
    }
    protected RsfClient findRsfClient(URL hostAddress) {
        AbstractClientManager clientManager = this.rsfContext.getRequestManager().getClientManager();
        return clientManager.getClient(hostAddress);
    }
    //
    public <T> T getRemote(String serviceID) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        return this.findRsfClient(this.findURL(serviceID))//
                .getRemote(serviceID);
    }
    public <T> T wrapper(String serviceID, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        return this.findRsfClient(this.findURL(serviceID))//
                .wrapper(serviceID, interFace);
    }
    public <T> T getRemote(String group, String name, String version) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        return this.findRsfClient(this.findURL(group, name, version))//
                .getRemote(group, name, version);
    }
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        return this.findRsfClient(this.findURL(group, name, version))//
                .wrapper(group, name, version, interFace);
    }
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        return this.findRsfClient(this.findURL(bindInfo.getBindID()))//
                .syncInvoke(bindInfo, methodName, parameterTypes, parameterObjects);
    }
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        return this.findRsfClient(this.findURL(bindInfo.getBindID()))//
                .asyncInvoke(bindInfo, methodName, parameterTypes, parameterObjects);
    }
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener) {
        this.findRsfClient(this.findURL(bindInfo.getBindID()))//
                .doCallBackInvoke(bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        this.findRsfClient(this.findURL(bindInfo.getBindID()))//
                .doCallBackRequest(bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
}
