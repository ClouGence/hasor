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
package net.hasor.registry.domain.server;
import java.util.List;
/**
 * 服务提供者信息
 *
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderInfo {
    /** 获取客户端调用服务超时时间。*/
    private int          clientTimeout;
    /** 获取序列化方式*/
    private String       serializeType;
    /** 最大服务处理队列长度 */
    private int          queueMaxSize;
    /** 是否是共享处理线程池 */
    private boolean      sharedThreadPool;
    /** 服务地址*/
    private List<String> rsfAddress;
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
    public List<String> getRsfAddress() {
        return rsfAddress;
    }
    public void setRsfAddress(List<String> rsfAddress) {
        this.rsfAddress = rsfAddress;
    }
}