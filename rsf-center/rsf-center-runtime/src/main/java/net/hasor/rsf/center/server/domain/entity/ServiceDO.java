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
/**
 * 服务
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceDO {
    private long          id;          // 服务编号（PK、自增）
    //
    private String        bindID;      // 服务ID
    private String        bindGroup;   // 服务分组（Group、Name、Version，联合唯一索引）
    private String        bindName;    // 服务名称（Group、Name、Version，联合唯一索引）
    private String        bindVersion; // 服务版本（Group、Name、Version，联合唯一索引）
    private String        bindType;    // 接口类型
    private StatusEnum    status;      //服务状态
    private Date          beatTime;    //最后心跳时间
    private String        forAppCode;  //关联的AppCode
    private RuleFeatureDO ruleFeature; //路由规则配置
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getBindID() {
        return bindID;
    }
    public void setBindID(String bindID) {
        this.bindID = bindID;
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
    public String getForAppCode() {
        return forAppCode;
    }
    public void setForAppCode(String forAppCode) {
        this.forAppCode = forAppCode;
    }
    public RuleFeatureDO getRuleFeature() {
        return ruleFeature;
    }
    public void setRuleFeature(RuleFeatureDO ruleFeature) {
        this.ruleFeature = ruleFeature;
    }
}