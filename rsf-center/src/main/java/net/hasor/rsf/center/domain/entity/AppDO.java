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
/**
 * 一个应用
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class AppDO {
    private long   appID;       //应用ID
    private String appName;     //应用名称
    private String accessKey;   //应用描述
    private String accessSecret; //授权KEY
    private String onwer;       //授权密钥
    private String description; //责任人
    //
    public long getAppID() {
        return appID;
    }
    public void setAppID(long appID) {
        this.appID = appID;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}