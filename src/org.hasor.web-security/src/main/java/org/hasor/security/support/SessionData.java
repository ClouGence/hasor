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
package org.hasor.security.support;
/**
 * 权限会话数据
 * @version : 2013-4-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class SessionData implements Cloneable {
    private String   userCode      = null;                      //用户标识码
    private String   authSystem    = null;                      //授权系统ID
    private String[] permissionSet = null;                      //会话的权限
    private long     loginTime     = 0;                         //登陆时间
    private long     lastTime      = System.currentTimeMillis(); //最后更新时间
    /*--------------------------------------------------*/
    @Override
    public SessionData clone() throws CloneNotSupportedException {
        SessionData newData = new SessionData();
        newData.userCode = newData.userCode;
        newData.authSystem = newData.authSystem;
        newData.permissionSet = newData.permissionSet.clone();
        newData.loginTime = newData.loginTime;
        newData.lastTime = newData.lastTime;
        return newData;
    }
    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
    public String getAuthSystem() {
        return authSystem;
    }
    public void setAuthSystem(String authSystem) {
        this.authSystem = authSystem;
    }
    public String[] getPermissionSet() {
        return permissionSet;
    }
    public void setPermissionSet(String[] permissionSet) {
        this.permissionSet = permissionSet;
    }
    public long getLoginTime() {
        return loginTime;
    }
    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }
    public long getLastTime() {
        return lastTime;
    }
    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}