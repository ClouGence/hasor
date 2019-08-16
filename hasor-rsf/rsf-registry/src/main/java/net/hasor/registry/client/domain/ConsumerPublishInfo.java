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
import java.util.List;

/**
 * 服务消费者信息
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ConsumerPublishInfo implements Serializable {
    private static final long         serialVersionUID = -335204051257003763L;
    private              int          clientTimeout;         // 获取客户端调用服务超时时间
    private              String       serializeType;         // 获取序列化方式
    private              int          clientMaximumRequest;  //最大并发请求数
    private              boolean      message;               // 是否工作在消息模式
    private              List<String> protocol;              // 注册的协议
    private              String       communicationAddress;  // 通信地址
    private              BeanInfo     clientBeanInfo;        // 客户端Bean信息

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

    public int getClientMaximumRequest() {
        return clientMaximumRequest;
    }

    public void setClientMaximumRequest(int clientMaximumRequest) {
        this.clientMaximumRequest = clientMaximumRequest;
    }

    public boolean isMessage() {
        return message;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public List<String> getProtocol() {
        return protocol;
    }

    public void setProtocol(List<String> protocol) {
        this.protocol = protocol;
    }

    public String getCommunicationAddress() {
        return communicationAddress;
    }

    public void setCommunicationAddress(String communicationAddress) {
        this.communicationAddress = communicationAddress;
    }

    public BeanInfo getClientBeanInfo() {
        return clientBeanInfo;
    }

    public void setClientBeanInfo(BeanInfo clientBeanInfo) {
        this.clientBeanInfo = clientBeanInfo;
    }
}