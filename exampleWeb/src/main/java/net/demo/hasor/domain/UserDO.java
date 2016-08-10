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
import org.more.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * OAuth AccessToken
 * @version : 2016年1月1日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserDO {
    private long   userID   = 0;    //Hasor 平台上的UserID
    private String account  = null; //帐号
    private String password = null; //密码
    private String nick     = null; //Nick
    private String email    = null; //email
    private String avatar   = null; //头像
    //
    private Map<String, AccessInfo> accessInfo;
    //
    public void putAccessInfo(String providerName, AccessInfo result) {
        if (StringUtils.isBlank(providerName) || result == null) {
            return;
        }
        if (this.accessInfo == null) {
            this.accessInfo = new HashMap<String, AccessInfo>();
        }
        this.accessInfo.put(providerName.toUpperCase(), result);
    }
    public void removeAccessInfo(String providerName) {
        if (StringUtils.isBlank(providerName) || this.accessInfo == null) {
            return;
        }
        this.accessInfo.remove(providerName.toUpperCase());
    }
    //
    public long getUserID() {
        return userID;
    }
    public void setUserID(long userID) {
        this.userID = userID;
    }
    public String getAccount() {
        return account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getNick() {
        return nick;
    }
    public void setNick(String nick) {
        this.nick = nick;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public Map<String, AccessInfo> getAccessInfo() {
        return accessInfo;
    }
    public void setAccessInfo(Map<String, AccessInfo> accessInfo) {
        this.accessInfo = accessInfo;
    }
}
