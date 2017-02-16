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
package net.hasor.registry.domain.server;
import java.util.Date;
/**
 * 授权信息
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class AuthInfo {
    private String appKey       = null; // Key
    private String appKeySecret = null; // Key秘钥
    private Date   expireTime   = null; // 过期时间
    //
    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getAppKeySecret() {
        return appKeySecret;
    }
    public void setAppKeySecret(String appKeySecret) {
        this.appKeySecret = appKeySecret;
    }
    public Date getExpireTime() {
        return expireTime;
    }
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}