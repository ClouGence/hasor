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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfFuture;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.adapter.AbstracAddressCenter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import org.more.future.FutureCallback;
/**
 * 
 * @version : 2014年12月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfClientFacade implements RsfClient {
    private AbstracAddressCenter addressCenter;
    //
    public RsfClientFacade(AbstractRsfContext rsfContext) {
        this.addressCenter = rsfContext.getAddressCenter();
    }s
    //
    public <T> T getRemote(String serviceID) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T wrapper(String serviceID, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T getRemote(String group, String name, String version) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        // TODO Auto-generated method stub
        return null;
    }
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) {
        // TODO Auto-generated method stub
        return null;
    }
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener) {
        // TODO Auto-generated method stub
    }
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener) {
        // TODO Auto-generated method stub
    }
}
