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
/**
 * 应用
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class AppDO {
    private long   id;       // 应用ID（PK，自增）
    private String appName;  // 应用名称
    private String appCode;  // 应用Code（唯一）
    private String authCode; // 授权码
    //
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getAppCode() {
        return appCode;
    }
    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
    public String getAuthCode() {
        return authCode;
    }
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}