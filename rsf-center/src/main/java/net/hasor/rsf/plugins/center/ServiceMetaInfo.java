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
package net.hasor.rsf.plugins.center;
/**
 * 
 * @version : 2015年4月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceMetaInfo {
    private String bindID        = null;     //服务名
    private String bindName      = null;     //服务名
    private String bindGroup     = "default"; //服务分组
    private String bindVersion   = "1.0.0";  //服务版本
    private String bindType      = null;     //服务类型
    private int    clientTimeout = 6000;     //调用超时（毫秒）
    private String serializeType = null;     //传输序列化类型
    //
    private String remoteHost    = null;     //远程机器
    private int    remotePort    = 8000;     //发布端口
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
    public String getRemoteHost() {
        return remoteHost;
    }
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
    public int getRemotePort() {
        return remotePort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
}