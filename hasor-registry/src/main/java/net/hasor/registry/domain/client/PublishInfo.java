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
package net.hasor.registry.domain.client;
import java.io.Serializable;
/**
 * 发布的服务信息
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
public class PublishInfo implements Serializable {
    private static final long serialVersionUID = -7962837923093982098L;
    /** 唯一标识（客户端唯一标识）。*/
    private String bindID;
    /** 服务名称。*/
    private String bindName;
    /** 服务分组。*/
    private String bindGroup;
    /** 服务版本。*/
    private String bindVersion;
    /** 注册的服务类型。*/
    private String bindType;
    /** 获取客户端调用服务超时时间。*/
    private int    clientTimeout;
    /** 获取序列化方式*/
    private String serializeType;
    /** 服务地址'逗号分割'*/
    private String targetList;
    //
    public String getBindID() {
        return bindID;
    }
    public void setBindID(String bindID) {
        this.bindID = bindID;
    }
    public String getBindName() {
        return bindName;
    }
    public void setBindName(String bindName) {
        this.bindName = bindName;
    }
    public String getBindGroup() {
        return bindGroup;
    }
    public void setBindGroup(String bindGroup) {
        this.bindGroup = bindGroup;
    }
    public String getBindVersion() {
        return bindVersion;
    }
    public void setBindVersion(String bindVersion) {
        this.bindVersion = bindVersion;
    }
    public String getBindType() {
        return bindType;
    }
    public void setBindType(String bindType) {
        this.bindType = bindType;
    }
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
    public String getTargetList() {
        return targetList;
    }
    public void setTargetList(String targetList) {
        this.targetList = targetList;
    }
}