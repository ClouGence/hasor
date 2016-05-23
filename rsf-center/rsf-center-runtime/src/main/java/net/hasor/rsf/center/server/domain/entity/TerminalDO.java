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
package net.hasor.rsf.center.server.domain.entity;
import java.util.Date;
import net.hasor.rsf.address.InterAddress;
/**
 * 连接到Service上的端点（提供者和消费者为两条记录）。
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class TerminalDO {
    private long              id;             // 应用ID（PK，自增）
    private InterAddress      rsfUrl;         //
    private String            hostPort;       //center的服务地址
    private String            unit;           //所属单元
    private String            version;        //RSF版本
    //
    private String            bindID;         // 服务ID
    private TerminalTypeEnum  persona;        // 身份(C:订阅者、P:提供者)
    private StatusEnum        status;         //服务状态
    private Date              beatTime;       //最后心跳时间
    private String            saltValue;      //盐值
    //
    private int               timeout;        // 超时时间
    private String            serializeType;  // 序列化策略
    private TerminalFeatureDO terminalFeature;//注册节点额外信息
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public InterAddress getRsfUrl() {
        return rsfUrl;
    }
    public void setRsfUrl(InterAddress rsfUrl) {
        this.rsfUrl = rsfUrl;
    }
    public String getHostPort() {
        return hostPort;
    }
    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public String getBindID() {
        return bindID;
    }
    public void setBindID(String bindID) {
        this.bindID = bindID;
    }
    public TerminalTypeEnum getPersona() {
        return persona;
    }
    public void setPersona(TerminalTypeEnum persona) {
        this.persona = persona;
    }
    public StatusEnum getStatus() {
        return status;
    }
    public void setStatus(StatusEnum status) {
        this.status = status;
    }
    public Date getBeatTime() {
        return beatTime;
    }
    public void setBeatTime(Date beatTime) {
        this.beatTime = beatTime;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public String getSaltValue() {
        return saltValue;
    }
    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }
    public String getSerializeType() {
        return serializeType;
    }
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    public TerminalFeatureDO getTerminalFeature() {
        return terminalFeature;
    }
    public void setTerminalFeature(TerminalFeatureDO terminalFeature) {
        this.terminalFeature = terminalFeature;
    }
}