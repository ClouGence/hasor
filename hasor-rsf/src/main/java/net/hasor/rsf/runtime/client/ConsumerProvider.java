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
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.Provider;
import net.hasor.rsf.metadata.ServiceMetaData;
import org.more.classcode.delegate.faces.MethodDelegate;
/**
 * 远程服务消费者
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ConsumerProvider<T> implements Provider<T> {
    private Class<T>        serviceInterface = null;
    private ServiceMetaData metaData         = new ServiceMetaData();
    private AtomicBoolean   inited           = new AtomicBoolean(false);
    private T               serviceWarp      = null;
    //
    /**获取服务接口类型*/
    public Class<T> getServiceInterface() {
        return this.serviceInterface;
    }
    /**设置服务接口类型*/
    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }
    /**获取客户端调用服务超时时间。*/
    public int getClientTimeout() {
        return this.metaData.getClientTimeout();
    }
    /**设置客户端调用服务超时时间。*/
    public void setClientTimeout(int clientTimeout) {
        this.metaData.setClientTimeout(clientTimeout);
    }
    /**获取客户端使用的对象序列化格式。*/
    public String getSerializeType() {
        return this.metaData.getSerializeType();
    }
    /**设置客户端使用的对象序列化格式。*/
    public void setSerializeType(String serializeType) {
        this.metaData.setSerializeType(serializeType);
    }
    //
    //
    /**初始化服务*/
    public void initService() {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        /*构建RemoteTicket对象*/
        RemoteTicket remoteTicket = RemoteTicket.getWrapper(this.serviceInterface);
        remoteTicket.initTicket(this.metaData);
        
        
        this.metaData
    }
    //
    public T get() {
        if (this.serviceWarp == null) {
            throw new java.lang.IllegalStateException("Service Uninitialized.");
        }
        return this.serviceWarp;
    }
}
//
class ObjectWrapper implements MethodDelegate {
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable {
        // TODO Auto-generated method stub
        return null;
    }
}