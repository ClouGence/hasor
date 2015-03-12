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
import net.hasor.rsf.constants.RsfException;
import org.more.future.FutureCallback;
/**
 * RSF调用者。
 * @version : 2014年11月18日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfClient {
    /**
     * 根据服务ID，获取远程服务对象
     * @param serviceID 服务ID
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemoteByID(String serviceID) throws RsfException;
    /**
     * 根据服务名，获取远程服务对象。服务版本、服务分组将使用默认值。
     * @param serviceName 服务名
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemoteByName(String serviceName) throws RsfException;
    /**
     * 获取远程服务对象。
     * @param group 分组
     * @param name 服务名
     * @param version 版本
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemote(String group, String name, String version) throws RsfException;
    /**
     * 获取远程服务对象
     * @param bindInfo rsf服务注册信息。
     * @return 返回远程服务对象。
     * @throws RsfException rsf异常
     */
    public <T> T getRemote(RsfBindInfo<T> bindInfo) throws RsfException;
    /**
     * 将服务包装为另外一个接口然后返回。
     * @param serviceID 服务ID
     * @param interFace 要装成为的接口
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapperByID(String serviceID, Class<T> interFace) throws RsfException;
    /**
     * 将服务包装为另外一个接口。
     * @param serviceName 服务名
     * @param interFace 包装成为的服务接口
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapperByName(String serviceName, Class<T> interFace) throws RsfException;
    /**
     * 将服务包装为另外一个接口。
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(Class<T> interFace) throws RsfException;
    /**
     * 将服务包装为另外一个接口。
     * @param group 分组
     * @param name 服务名
     * @param version 版本
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(String group, String name, String version, Class<T> interFace) throws RsfException;
    /**
     * 将服务包装为另外一个接口。
     * @param bindInfo rsf服务注册信息。
     * @param interFace 服务接口类型
     * @return 返回包装之后的服务接口。
     * @throws RsfException rsf异常
     */
    public <T> T wrapper(RsfBindInfo<?> bindInfo, Class<T> interFace) throws RsfException;
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
    public Object syncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable;
    /**
     * 异步方式调用远程服务。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @return 返回异步执行结果
     */
    public RsfFuture asyncInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects);
    /**
     * 以回调方式调用远程服务。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @param listener 回调监听器。
     */
    public void doCallBackInvoke(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener);
    /**
     * 以回调方式发送RSF调用请求。
     * @param bindInfo 远程服务信息
     * @param methodName 远程方法名
     * @param parameterTypes 参数类型
     * @param parameterObjects 参数值
     * @param listener 回调监听器。
     */
    public void doCallBackRequest(RsfBindInfo<?> bindInfo, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener);
}