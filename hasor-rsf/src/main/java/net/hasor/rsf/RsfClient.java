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
package net.hasor.rsf;
import java.io.IOException;
import org.more.future.FutureCallback;
/**
 * RSF调用者。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfClient {
    /**获取远程服务对象*/
    public <T> T getRemoteByID(String serviceID) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**获取远程服务对象*/
    public <T> T getRemoteByName(String serviceName) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**获取远程服务对象*/
    public <T> T getRemote(String group, String name, String version) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**获取远程服务对象*/
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    //
    /**将服务包装为另外一个接口。*/
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapperByName(String serviceName, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    //
    /**同步方式调用远程服务。*/
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable;
    /**异步方式调用远程服务。*/
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects);
    /**以回调方式调用远程服务。*/
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener);
    /**以回调方式发送RSF调用请求。*/
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener);
}