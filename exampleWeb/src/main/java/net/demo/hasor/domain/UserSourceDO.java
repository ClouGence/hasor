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
package net.demo.hasor.domain;
import net.demo.hasor.domain.enums.UserStatus;

import java.util.Date;
/**
 * 用户外部登陆信息
 * @version : 2016年08月11日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserSourceDO {
    private long       sourceID       = 0;    // UserSourceID（PK，自增）
    private String     provider       = null; // 来源
    private String     uniqueID       = null; // 外部唯一码
    //
    private long       userID         = 0;    // 关联的UserID
    private AccessInfo accessInfo     = null; // 外部登陆详细信息
    private boolean    status         = true; // 是否有效
    private long       loginCount     = 0;    // 登录次数
    private Date       firstLoginTime = null; // 首次登陆时间
    private Date       lastLoginTime  = null; // 最后一次登陆时间
    private Date       createTime     = null; // 创建时间
    private Date       modifyTime     = null; // 修噶改时间
    //
    public long getSourceID() {
        return sourceID;
    }
    public void setSourceID(long sourceID) {
        this.sourceID = sourceID;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    public String getUniqueID() {
        return uniqueID;
    }
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }
    public long getUserID() {
        return userID;
    }
    public void setUserID(long userID) {
        this.userID = userID;
    }
    public AccessInfo getAccessInfo() {
        return accessInfo;
    }
    public void setAccessInfo(AccessInfo accessInfo) {
        this.accessInfo = accessInfo;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
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
    public long getLoginCount() {
        return loginCount;
    }
    public void setLoginCount(long loginCount) {
        this.loginCount = loginCount;
    }
    public Date getFirstLoginTime() {
        return firstLoginTime;
    }
    public void setFirstLoginTime(Date firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }
    public Date getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}