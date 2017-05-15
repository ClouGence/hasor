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
package net.hasor.rsf.domain;
import net.hasor.core.Hasor;
import net.hasor.core.info.MetaDataAdapter;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * 服务的描述信息，包括了服务的发布和订阅信息。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceDomain<T> extends MetaDataAdapter implements RsfBindInfo<T> {
    private String              bindID             = null;      //服务ID
    private String              bindName           = null;      //服务名
    private String              bindGroup          = "default"; //服务分组
    private String              bindVersion        = "1.0.0";   //服务版本
    private Map<String, String> aliasNameMap       = null;      //别名
    private Class<T>            bindType           = null;      //服务类型
    private boolean             asMessage          = false;     //是否为消息接口
    private boolean             asShadow           = false;     //是否为消息接口
    private boolean             isSharedThreadPool = true;     //是否共享调用线程池(提供者)
    private int                 clientTimeout      = 6000;      //调用超时（毫秒）
    private String              serializeType      = null;      //传输序列化类型
    private RsfServiceType      serviceType        = null;      //服务类型（提供者 or 消费者）
    //
    public ServiceDomain(Class<T> bindType) {
        this.bindType = bindType;
        this.asMessage = bindType.isAnnotationPresent(RsfMessage.class);
        this.aliasNameMap = new HashMap<String, String>();
    }
    public String getBindID() {
        if (bindID == null) {
            this.bindID = String.format("[%s]%s-%s", this.bindGroup, this.bindName, this.bindVersion);
        }
        return this.bindID;
    }
    /**获取发布服务的名称。*/
    public String getBindName() {
        return this.bindName;
    }
    @Override
    public Set<String> getAliasTypes() {
        return Collections.unmodifiableSet(this.aliasNameMap.keySet());
    }
    /**设置发布服务的名称。*/
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }
    /**获取发布服务的分组名称（默认是：default）。*/
    public String getBindGroup() {
        return this.bindGroup;
    }
    /**设置发布服务的分组名称（默认是：default）。*/
    public void setBindGroup(String bindGroup) {
        this.bindGroup = bindGroup;
    }
    /**获取发布服务的版本号。*/
    public String getBindVersion() {
        return this.bindVersion;
    }
    /**设置发布服务的版本号。*/
    public void setBindVersion(String bindVersion) {
        this.bindVersion = bindVersion;
    }
    /** @return 别名 */
    public String getAliasName(String aliasType) {
        return this.aliasNameMap.get(aliasType);
    }
    /**设置服务别名*/
    public void putAliasName(String aliasType, String aliasName) {
        aliasType = Hasor.assertIsNotNull(aliasType, "aliasType is null.");
        aliasName = Hasor.assertIsNotNull(aliasName, "aliasName is null.");
        this.aliasNameMap.put(aliasType, aliasName);
    }
    /**服务类型*/
    public Class<T> getBindType() {
        return this.bindType;
    }
    /**是否为消息接口。*/
    public boolean isMessage() {
        return this.asMessage || this.bindType.isAnnotationPresent(RsfMessage.class);
    }
    /** 设置接口的工作状态,如果接口标记了@RsfMessage,那么无论设置什么值 isMessage 都会返回true。 */
    public void setMessage(boolean asMessage) {
        this.asMessage = asMessage;
    }
    /** 接口是否要求工作在隐藏模式下。*/
    public boolean isShadow() {
        return asShadow;
    }
    public void setShadow(boolean asShadow) {
        this.asShadow = asShadow;
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
    @Override
    public boolean isSharedThreadPool() {
        return this.isSharedThreadPool;
    }
    public void setSharedThreadPool(boolean sharedThreadPool) {
        this.isSharedThreadPool = sharedThreadPool;
    }
    /**设置客户端使用的对象序列化格式。*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    /**获取服务类型，消费者还是提供者*/
    public RsfServiceType getServiceType() {
        return serviceType;
    }
    /**设置服务类型，消费者还是提供者*/
    public void setServiceType(RsfServiceType serviceType) {
        this.serviceType = serviceType;
    }
    //
    @Override
    public String toString() {
        return "ServiceDomain{" + "bindID='" + bindID + '\'' +//
                ", bindName='" + bindName + '\'' + //
                ", bindGroup='" + bindGroup + '\'' +//
                ", bindVersion='" + bindVersion + '\'' + //
                ", bindType=" + bindType + //
                '}';
    }
}