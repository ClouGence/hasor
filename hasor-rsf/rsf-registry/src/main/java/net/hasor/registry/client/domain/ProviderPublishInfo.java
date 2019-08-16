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
package net.hasor.registry.client.domain;
import java.io.Serializable;
import java.util.Map;

/**
 * 服务提供功着信息
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ProviderPublishInfo implements Serializable {
    private static final long                serialVersionUID = -6681610352758467621L;
    private              int                 clientTimeout;  // 获取客户端调用服务超时时间
    private              String              serializeType;  // 获取序列化方式
    private              int                 queueMaxSize;   // 最大服务处理队列长度
    private              boolean             sharedThreadPool;
    private              Map<String, String> addressMap;
    private              BeanInfo            clientBeanInfo; // 客户端Bean信息

    //
    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public String getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }

    public int getQueueMaxSize() {
        return queueMaxSize;
    }

    public void setQueueMaxSize(int queueMaxSize) {
        this.queueMaxSize = queueMaxSize;
    }

    public boolean isSharedThreadPool() {
        return sharedThreadPool;
    }

    public void setSharedThreadPool(boolean sharedThreadPool) {
        this.sharedThreadPool = sharedThreadPool;
    }

    public Map<String, String> getAddressMap() {
        return addressMap;
    }

    public void setAddressMap(Map<String, String> addressMap) {
        this.addressMap = addressMap;
    }

    public BeanInfo getClientBeanInfo() {
        return clientBeanInfo;
    }

    public void setClientBeanInfo(BeanInfo clientBeanInfo) {
        this.clientBeanInfo = clientBeanInfo;
    }
}