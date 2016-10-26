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
import java.util.Date;
/**
 * 表示为一个Service
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceInfoDO {
    private long   serviceID;    // 服务编号（PK、自增）
    private long   appID;        // 所属应用
    private String bindGroup;    // 服务分组（Group、Name、Version，联合唯一索引）
    private String bindName;     // 服务名称（Group、Name、Version，联合唯一索引）
    private String bindVersion;  // 服务版本（Group、Name、Version，联合唯一索引）
    private String bindType;     // 接口名称
    private String onwer;        // 接口人（创建应用的人）
    private String contactUsers; // 接口人（多个人）
    private String consistency;  // 一致性校验Code（Group、Name、Version做一致性校验）
    private String description;  // 接口描述
    private Date   createTime;   // 创建时间
    private Date   modifyTime;   // 修改时间
    //
    public long getServiceID() {
        return serviceID;
    }
    public void setServiceID(long serviceID) {
        this.serviceID = serviceID;
    }
    public long getAppID() {
        return appID;
    }
    public void setAppID(long appID) {
        this.appID = appID;
    }
    public String getBindGroup() {
        return bindGroup;
    }
    public void setBindGroup(String bindGroup) {
        this.bindGroup = bindGroup;
    }
    public String getBindName() {
        return bindName;
    }
    public void setBindName(String bindName) {
        this.bindName = bindName;
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
    public String getOnwer() {
        return onwer;
    }
    public void setOnwer(String onwer) {
        this.onwer = onwer;
    }
    public String getContactUsers() {
        return contactUsers;
    }
    public void setContactUsers(String contactUsers) {
        this.contactUsers = contactUsers;
    }
    public String getConsistency() {
        return consistency;
    }
    public void setConsistency(String consistency) {
        this.consistency = consistency;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}