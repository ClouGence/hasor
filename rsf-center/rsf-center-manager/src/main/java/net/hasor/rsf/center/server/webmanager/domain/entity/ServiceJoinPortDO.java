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
package net.hasor.rsf.center.server.webmanager.domain.entity;
/**
 * 连接到Service上的端点（提供者和消费者为两条记录）。
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceJoinPortDO {
    private long   appID;         // 服务ID
    private String serviceID;     // 服务ID
    private String terminalID;    // 终端ID
    private int    timeout;       // 超时时间
    private String serializeType; // 序列化策略
    private char   persona;       // 身份
    //
    public long getAppID() {
        return appID;
    }
    public void setAppID(long appID) {
        this.appID = appID;
    }
    public String getServiceID() {
        return serviceID;
    }
    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }
    public String getTerminalID() {
        return terminalID;
    }
    public void setTerminalID(String terminalID) {
        this.terminalID = terminalID;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public String getSerializeType() {
        return serializeType;
    }
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    public char getPersona() {
        return persona;
    }
    public void setPersona(char persona) {
        this.persona = persona;
    }
}