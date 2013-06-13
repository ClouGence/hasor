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
package org.platform.security.support;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.StringUtils;
import org.platform.Assert;
import org.platform.Platform;
import org.platform.security.AuthSession;
import org.platform.security.SecurityAccess;
import org.platform.security.SecurityAuth;
import org.platform.security.Permission;
import org.platform.security.SecurityException;
import org.platform.security.UserInfo;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
class AbstractAuthSession implements AuthSession {
    private SessionData             authSessionData;
    private boolean                 isClose;
    private Map<String, Permission> permissionMap;
    private AbstractSecurityContext securityContext;
    private String                  sessionID;
    private UserInfo                userInfo;
    //
    protected AbstractAuthSession(String sessionID, AbstractSecurityContext securityContext) {
        Assert.isLegal(!StringUtils.isBlank(sessionID), "sessionID is Undefined!");
        Assert.isNotNull(securityContext, "SecurityContext is Undefined!");
        this.sessionID = sessionID;
        this.securityContext = securityContext;
        this.isClose = false;
        this.permissionMap = new HashMap<String, Permission>();
    }
    @Override
    public void addPermission(Permission permission) throws SecurityException {
        this.checkClose();/*Check*/
        this.permissionMap.put(permission.getPermissionCode(), permission);
        this.authSessionData.setLastTime(System.currentTimeMillis());
    }
    @Override
    public void addPermission(String permissionCode) throws SecurityException {
        this.checkClose();/*Check*/
        if (StringUtils.isBlank(permissionCode))
            return;
        this.permissionMap.put(permissionCode, new Permission(permissionCode));
        this.authSessionData.setLastTime(System.currentTimeMillis());
    };
    private void checkClose() throws SecurityException {
        if (isClose())
            throw new SecurityException("AuthSession is closed!");
    };
    @Override
    public synchronized void close() throws SecurityException {
        this.checkClose();/*Check*/
        this.getSecurityContext().throwEvent(SecurityEventDefine.AuthSession_Close, this);/*抛出事件*/
        this.doLogout();
        this.getSecurityContext().inactivationAuthSession(this);
        this.isClose = true;
    }
    @Override
    public synchronized void doLogin(String authSystem, String account, String password) throws SecurityException {
        this.checkClose();/*Check*/
        SecurityAuth authApi = this.getSecurityContext().getSecurityAuth(authSystem);
        if (authApi == null)
            throw new SecurityException("Not register " + authSystem + " ISecurityAuth.");
        UserInfo userInfo = authApi.getUserInfo(account, password);
        if (userInfo != null) {
            this.userInfo = userInfo;
            this.authSessionData.setUserCode(this.userInfo.getUserCode());//用户标识码
            this.authSessionData.setAuthSystem(authSystem);
            this.authSessionData.setLoginTime(System.currentTimeMillis());//登陆时间
            this.authSessionData.setLastTime(System.currentTimeMillis());
            this.reloadPermission();/*重载权限*/
            this.refreshCacheTime();
            Platform.debug("%s :doLogin authSystem=%s ,account=%s ,password=%s", this.sessionID, authSystem, account, password);
            {
                HashMap<String, String> attr = new HashMap<String, String>();
                attr.put("Type", "doLogin");
                attr.put("AuthSystem", authSystem);
                attr.put("Account", account);
                attr.put("Password", password);
                this.getSecurityContext().throwEvent(SecurityEventDefine.Login, attr, this);/*抛出事件*/
            }
            return;
        }
        throw new SecurityException("unknown user!");
    };
    @Override
    public synchronized void doLogin(String authSystem, UserInfo user) throws SecurityException {
        this.checkClose();/*Check*/
        Assert.isNotNull(user);
        this.doLoginCode(authSystem, user.getUserCode());
    };
    @Override
    public synchronized void doLoginCode(String authSystem, String userCode) throws SecurityException {
        this.checkClose();/*Check*/
        SecurityAuth authApi = this.getSecurityContext().getSecurityAuth(authSystem);
        if (authApi == null)
            throw new SecurityException("Not register " + authSystem + " ISecurityAuth.");
        UserInfo userInfo = authApi.getUserInfo(userCode);
        if (userInfo != null) {
            this.userInfo = userInfo;
            this.authSessionData.setUserCode(this.userInfo.getUserCode());//用户标识码
            this.authSessionData.setAuthSystem(authSystem);
            this.authSessionData.setLoginTime(System.currentTimeMillis());//登陆时间
            this.authSessionData.setLastTime(System.currentTimeMillis());
            this.reloadPermission();/*重载权限*/
            this.refreshCacheTime();
            Platform.debug("%s :doLogin authSystem=%s ,userCode=%s", this.sessionID, authSystem, userCode);
            {
                HashMap<String, String> attr = new HashMap<String, String>();
                attr.put("Type", "doLoginCode");
                attr.put("AuthSystem", authSystem);
                attr.put("UserCode", userCode);
                this.getSecurityContext().throwEvent(SecurityEventDefine.Login, attr, this);/*抛出事件*/
            }
            return;
        }
        throw new SecurityException("unknown user!");
    };
    @Override
    public synchronized void doLogout() throws SecurityException {
        this.checkClose();/*Check*/
        this.getSecurityContext().throwEvent(SecurityEventDefine.Logout, this);/*抛出事件*/
        this.authSessionData = new SessionData();
        this.userInfo = null;
        this.permissionMap.clear();
        this.getSecurityContext().removeSessionData(this.sessionID);
        Platform.debug("%s :doLogout!", this.sessionID);
    };
    @Override
    public String getAuthSystem() {
        if (getUserObject() != null)
            return this.authSessionData.getAuthSystem();
        return null;
    };
    @Override
    public long getLoginTime() throws SecurityException {
        if (this.isLogin() == true)
            return this.authSessionData.getLoginTime();
        else
            return -1;
    };
    @Override
    public Permission[] getPermissionObjects() {
        return this.permissionMap.values().toArray(new Permission[this.permissionMap.size()]);
    };
    @Override
    public String[] getPermissions() {
        String[] pers = new String[this.permissionMap.size()];
        int i = 0;
        for (Permission per : this.permissionMap.values()) {
            pers[i] = per.getPermissionCode();
            i++;
        }
        return pers;
    };
    /**获取SecurityContext对象*/
    protected AbstractSecurityContext getSecurityContext() {
        return this.securityContext;
    }
    /**获取SessionData*/
    protected SessionData getSessionData() {
        try {
            return this.authSessionData.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    };
    @Override
    public String getSessionID() {
        return this.sessionID;
    };
    @Override
    public UserInfo getUserObject() {
        try {
            this.checkClose();/*Check*/
            if (this.userInfo == null) {
                String userCode = this.authSessionData.getUserCode();
                if (StringUtils.isBlank(userCode) == false) {
                    String userFromAuth = this.authSessionData.getAuthSystem();
                    SecurityAuth auth = this.getSecurityContext().getSecurityAuth(userFromAuth);
                    this.userInfo = auth.getUserInfo(userCode);
                }
            }
            return this.userInfo;
        } catch (SecurityException e) {
            Platform.debug("%s", e);
            return null;
        }
    }
    @Override
    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.getPermissionCode());
    };
    @Override
    public boolean hasPermission(String permissionCode) {
        return this.permissionMap.containsKey(permissionCode);
    };
    @Override
    public boolean isBlank() {
        if (this.isClose() == false && (this.isLogin() == false || this.isGuest() == true))
            return true;
        return false;
    }
    @Override
    public boolean isClose() {
        return this.isClose;
    };
    @Override
    public boolean isGuest() {
        if (this.isClose())
            return false;
        //
        UserInfo userInfo = this.getUserObject();
        if (userInfo == null)
            return false;
        return userInfo.isGuest();
    };
    @Override
    public boolean isLogin() {
        if (this.isClose() || this.isGuest() == true)
            return false;
        //
        UserInfo userInfo = this.getUserObject();
        return userInfo != null;
    };
    /**装载权限数据*/
    protected void loadSessionData(SessionData sessionData) {
        this.permissionMap.clear();
        String[] perArray = sessionData.getPermissionSet();
        if (perArray != null)
            for (String per : perArray)
                this.permissionMap.put(per, new Permission(per));
        if (this.authSessionData == null)
            this.authSessionData = sessionData;
        this.authSessionData.setLastTime(System.currentTimeMillis());
    };
    @Override
    public synchronized void refreshCacheTime() {
        if (this.isBlank() == true || this.isGuest() == true)
            return;
        SessionData cacheData = this.getSecurityContext().getSessionData(this.sessionID);
        if (cacheData == null || cacheData.getLastTime() < this.authSessionData.getLastTime()) {
            String[] proArray = this.permissionMap.keySet().toArray(new String[this.permissionMap.size()]);
            this.authSessionData.setPermissionSet(proArray);
            this.getSecurityContext().updateSessionData(this.sessionID, this.authSessionData);
        } else
            this.getSecurityContext().updateSessionData(this.sessionID);
    }
    @Override
    public synchronized void reloadPermission() throws SecurityException {
        this.checkClose();/*Check*/
        SecurityAccess access = this.getSecurityContext().getSecurityAccess(this.authSessionData.getAuthSystem());
        List<Permission> perList = access.loadPermission(this.getUserObject());
        if (perList != null)
            for (Permission per : perList)
                this.permissionMap.put(per.getPermissionCode(), per);
        this.authSessionData.setLastTime(System.currentTimeMillis());
    }
    @Override
    public void removeTempPermission(Permission permission) throws SecurityException {
        this.checkClose();/*Check*/
        if (permission == null)
            return;
        this.permissionMap.remove(permission.getPermissionCode());
        this.authSessionData.setLastTime(System.currentTimeMillis());
    }
    @Override
    public void removeTempPermission(String permissionCode) throws SecurityException {
        this.checkClose();/*Check*/
        if (StringUtils.isBlank(permissionCode))
            return;
        this.permissionMap.remove(permissionCode);
        this.authSessionData.setLastTime(System.currentTimeMillis());
    }
}