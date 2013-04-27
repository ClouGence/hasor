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
package org.platform.security;
import org.platform.clock.Clock;
/**
 * 权限会话数据
 * @version : 2013-4-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class SessionData {
    private String   sessionID     = null;
    private String   userCode      = null;
    private String   fromAccess    = null;
    private String   fromAuth      = null;
    private boolean  loginMark     = false;
    private boolean  cookieRecover = false;
    private String[] permissionSet = null;
    private long     createdTime   = Clock.getSyncTime();
    //
    //
    //
    public String getSessionID() {
        return sessionID;
    }
    protected void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public String getFromAccess() {
        return fromAccess;
    }
    public void setFromAccess(String fromAccess) {
        this.fromAccess = fromAccess;
    }
    public String getFromAuth() {
        return fromAuth;
    }
    public void setFromAuth(String fromAuth) {
        this.fromAuth = fromAuth;
    }
    public boolean isLoginMark() {
        return loginMark;
    }
    public void setLoginMark(boolean loginMark) {
        this.loginMark = loginMark;
    }
    public boolean isCookieRecover() {
        return cookieRecover;
    }
    public void setCookieRecover(boolean cookieRecover) {
        this.cookieRecover = cookieRecover;
    }
    public String[] getPermissionSet() {
        return permissionSet;
    }
    public void setPermissionSet(String[] permissionSet) {
        this.permissionSet = permissionSet;
    }
    public long getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}