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
package net.hasor.rsf.metadata;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.Hasor;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceMetaData {
    private Class<?>            serviceType    = null;     //服务类型
    private Mode                mode           = null;
    private Map<String, Method> methodMap      = null;
    //Provider
    private String              serviceName    = null;     //服务名
    private String              serviceGroup   = "default"; //服务分组
    private String              serviceVersion = "1.0.0";  //服务版本
    private String              serviceDesc    = "";       //服务描述
    //Consumer
    private int                 clientTimeout  = 6000;     //调用超时（毫秒）
    private String              serializeType  = null;     //传输序列化类型
    //
    public static enum Mode {
        Provider, Consumer
    }
    //
    //
    public ServiceMetaData(Mode mode, Class<?> serviceType) {
        this.mode = Hasor.assertIsNotNull(mode, "mode is null.");
        this.serviceType = serviceType;
        this.methodMap = new ConcurrentHashMap<String, Method>();
    }
    //
    /**获取发布服务的名称。*/
    public String getServiceName() {
        return this.serviceName;
    }
    /**设置发布服务的名称。*/
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    /**获取发布服务的分组名称（默认是：default）。*/
    public String getServiceGroup() {
        return this.serviceGroup;
    }
    /**设置发布服务的分组名称（默认是：default）。*/
    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
    /**获取发布服务的版本号。*/
    public String getServiceVersion() {
        return this.serviceVersion;
    }
    /**设置发布服务的版本号。*/
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
    /**获取服务描述性信息。*/
    public String getServiceDesc() {
        return this.serviceDesc;
    }
    /**设置服务描述性信息。*/
    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }
    /**获取客户端调用服务超时时间。*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    /**设置客户端调用服务超时时间。*/
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    /**获取客户端使用的对象序列化格式。*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**设置客户端使用的对象序列化格式。*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    /**服务提供者*/
    public boolean isProvider() {
        return this.mode == Mode.Provider;
    }
    /**服务消费者*/
    public boolean isConsumer() {
        return this.mode == Mode.Consumer;
    }
    public Method getServiceMethod(String methodName, Class<?>[] parameterTypes) {
        StringBuffer key = new StringBuffer(methodName);
        for (Class<?> pt : parameterTypes) {
            key.append(pt.getName() + ";");
        }
        String mKey = key.toString();
        if (this.methodMap.containsKey(mKey) == false) {
            try {
                Method m = this.serviceType.getMethod(methodName, parameterTypes);
                this.methodMap.put(mKey, m);
            } catch (Exception e) {
                this.methodMap.put(mKey, null);
            }
        }
        return this.methodMap.get(mKey);
    }
    /**服务类型*/
    public Class<?> getServiceType() {
        return this.serviceType;
    }
    public String toString() {
        return String.format("Service - N=%s ,G=%s ,V=%s", this.serviceName, this.serviceGroup, this.serviceVersion);
    }
}