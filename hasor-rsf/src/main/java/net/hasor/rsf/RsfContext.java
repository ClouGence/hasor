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
 * RSF 环境。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfContext {
    /**获取注册中心。*/
    public BindCenter getBindCenter();
    /**获取元信息所描述的服务对象。*/
    public <T> T getBean(RsfBindInfo<T> metaData);
    /**获取配置*/
    public RsfSettings getSettings();
    //
    /**获取远程服务对象*/
    public <T> T getRemote(String serviceID) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String serviceID, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**获取远程服务对象*/
    public <T> T getRemote(String name, String group, String version) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String name, String group, String version, Class<T> interFace) throws //
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