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
package net.hasor.rsf.runtime.client;
import java.io.IOException;
import java.util.concurrent.Future;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfResponse;
import org.more.future.FutureCallback;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfClient {
    /**server address.*/
    public String getServerHost();
    /**server port.*/
    public int getServerPort();
    /**本地IP。*/
    public String getLocalHost();
    /**本地端口。*/
    public int getLocalPort();
    /**获取{@link RsfContext}*/
    public RsfContext getRsfContext();
    //
    /**获取选项Key集合。*/
    public String[] getOptionKeys();
    /**获取选项数据*/
    public String getOption(String key);
    /**设置选项数据*/
    public void addOption(String key, String value);
    //
    /**关闭与远端的连接（异步）*/
    public Future<Void> close() throws InterruptedException;
    /**连接是否为活动的。*/
    public boolean isActive();
    //
    /**获取正在进行中的调用请求。*/
    public RsfFuture getRequest(long requestID);
    //
    /**获取远程服务对象*/
    public <T> T getRemote(String serviceName) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String serviceName, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**获取远程服务对象*/
    public <T> T getRemote(String serviceName, String group, String version) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    /**将服务包装为另外一个接口。*/
    public <T> T wrapper(String serviceName, String group, String version, Class<T> interFace) throws //
            ClassNotFoundException, IOException, InstantiationException, IllegalAccessException;
    //
    /**同步方式调用远程服务。*/
    public Object syncInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects) throws Throwable;
    /**异步方式调用远程服务。*/
    public RsfFuture asyncInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects);
    /**以回调方式调用远程服务。*/
    public void doCallBackInvoke(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<Object> listener);
    /**以回调方式发送RSF调用请求。*/
    public void doCallBackRequest(ServiceMetaData<?> metaData, String methodName, Class<?>[] parameterTypes, Object[] parameterObjects, FutureCallback<RsfResponse> listener);
}