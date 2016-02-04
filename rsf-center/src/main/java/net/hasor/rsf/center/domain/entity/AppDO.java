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
package net.hasor.rsf.center.domain.entity;
import java.util.Date;
/**
 * 一个应用
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class AppDO {
    private long   appID;        // 应用ID（PK，自增）
    private String appCode;      // 应用Code（唯一）
    private String appName;      // 应用名称
    private String icon;         // 应用图标
    private String accessKey;    // 授权KEY
    private String accessSecret; // 授权密钥
    private String onwer;        // 接口人（创建应用的人）
    private String contactUsers; // 接口人（多个人）
    private String description;  // 应用描述
    private Date   createTime;   // 创建时间
    private Date   modifyTime;   // 修改时间
    //
    public long getAppID() {
        return appID;
    }
    public void setAppID(long appID) {
        this.appID = appID;
    }
    public String getAppCode() {
        return appCode;
    }
    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getAccessKey() {
        return accessKey;
    }
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    public String getAccessSecret() {
        return accessSecret;
    }
    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
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