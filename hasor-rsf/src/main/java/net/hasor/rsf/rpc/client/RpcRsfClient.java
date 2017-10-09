/*
 * Copyright 2008-2009 the original author or authors.
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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.provider.AddressProvider;
import net.hasor.rsf.rpc.caller.RsfCaller;
import net.hasor.utils.future.FutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
/**
 *
 * @version : 2015年12月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcRsfClient implements RsfClient {
    private AddressProvider provider;
    private RsfCaller       rsfCaller;
    public RpcRsfClient(AddressProvider provider, RsfCaller rsfCaller) {
        this.provider = provider;
        this.rsfCaller = rsfCaller;
    }
    protected AddressProvider getTargetProvider() {
        return this.provider;
    }
    protected RsfCaller getRsfCaller() {
        return this.rsfCaller;
    }
    @Override
    public <T> T getRemoteByID(String serviceID) {
        return (T) this.getRsfCaller().getRemoteByID(getTargetProvider(), serviceID);
    }
    @Override
    public <T> T getRemote(String group, String name, String version) {
        return (T) this.getRsfCaller().getRemote(getTargetProvider(), group, name, version);
    }
    @Override
    public <T> T getRemote(RsfBindInfo<T> bindInfo) {
        return this.getRsfCaller().getRemote(getTargetProvider(), bindInfo);
    }
    @Override
    public <T> T wrapperByID(String serviceID, Class<T> interFace) {
        return this.getRsfCaller().wrapperByID(getTargetProvider(), serviceID, interFace);
    }
    @Override
    public <T> T wrapper(Class<T> interFace) {
        return this.getRsfCaller().wrapper(getTargetProvider(), interFace);
    }
    @Override
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) {
        return this.getRsfCaller().wrapper(getTargetProvider(), group, name, version, interFace);
    }
    @Override
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) {
        return this.getRsfCaller().wrapper(getTargetProvider(), bindInfo, interFace);
    }
    @Override
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws InterruptedException, ExecutionException, TimeoutException {
        return this.getRsfCaller().syncInvoke(getTargetProvider(), bindInfo, methodName, parameterTypes, parameterObjects);
    }
    @Override
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        return this.getRsfCaller().asyncInvoke(getTargetProvider(), bindInfo, methodName, parameterTypes, parameterObjects);
    }
    @Override
    public void callBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener) {
        this.getRsfCaller().callBackInvoke(getTargetProvider(), bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
    @Override
    public void callBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        this.getRsfCaller().callBackRequest(getTargetProvider(), bindInfo, methodName, parameterTypes, parameterObjects, listener);
    }
}