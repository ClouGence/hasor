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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.StringUtil;
import org.platform.Assert;
import org.platform.Platform;
import org.platform.clock.Clock;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class AuthSession {
    private String                  sessionID;
    private boolean                 cookieRecover;
    private UserInfo                userInfo;
    private Map<String, Permission> permissionMap;
    private SessionData             authSessionData;
    private SecurityContext         securityContext;
    private boolean                 isClose;
    //
    protected AuthSession(String sessionID, SecurityContext securityContext) {
        Assert.isLegal(!StringUtil.isBlank(sessionID), "sessionID is Undefined!");
        Assert.isNotNull(securityContext, "SecurityContext is Undefined!");
        this.sessionID = sessionID;
        this.securityContext = securityContext;
        this.isClose = false;
        this.permissionMap = new HashMap<String, Permission>();
    }
    /**获取SessionData*/
    protected SessionData getSessionData() {
        try {
            return this.authSessionData.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    /**获取会话ID。*/
    public String getSessionID() {
        return this.sessionID;
    };
    /**获取登入的用户对象，如果未登录系统而且启用了来宾帐号则会返回来宾帐号。*/
    public UserInfo getUserObject() {
        try {
            this.checkClose();/*Check*/
            if (this.userInfo == null) {
                String userCode = this.authSessionData.getUserCode();
                if (StringUtil.isBlank(userCode) == false) {
                    String userFromAuth = this.authSessionData.getAuthSystem();
                    ISecurityAuth auth = this.getSecurityContext().getSecurityAuth(userFromAuth);
                    this.userInfo = auth.getUserInfo(userCode);
                }
            }
            return this.userInfo;
        } catch (SecurityException e) {
            Platform.debug(Platform.logString(e));
            return null;
        }
    };
    /**获取登陆会话时使用的具体权限系统。*/
    public String getAuthSystem() {
        if (getUserObject() != null)
            return this.authSessionData.getAuthSystem();
        return null;
    }
    /**向会话添加一条临时权限。*/
    public void addPermission(Permission permission) throws SecurityException {
        this.checkClose();/*Check*/
        this.permissionMap.put(permission.getPermissionCode(), permission);
        this.authSessionData.setLastTime(Clock.getSyncTime());
    };
    /**向会话添加一条临时权限。*/
    public void addPermission(String permissionCode) throws SecurityException {
        this.checkClose();/*Check*/
        if (StringUtil.isBlank(permissionCode))
            return;
        this.permissionMap.put(permissionCode, new Permission(permissionCode));
        this.authSessionData.setLastTime(Clock.getSyncTime());
    };
    /**临时撤销用户会话中一条权限。*/
    public void removeTempPermission(Permission permission) throws SecurityException {
        this.checkClose();/*Check*/
        if (permission == null)
            return;
        this.permissionMap.remove(permission.getPermissionCode());
        this.authSessionData.setLastTime(Clock.getSyncTime());
    };
    /**临时撤销用户会话中一条权限。*/
    public void removeTempPermission(String permissionCode) throws SecurityException {
        this.checkClose();/*Check*/
        if (StringUtil.isBlank(permissionCode))
            return;
        this.permissionMap.remove(permissionCode);
        this.authSessionData.setLastTime(Clock.getSyncTime());
    };
    /**获取会话中包含的所有权限信息。*/
    public Permission[] getPermissionObjects() {
        return this.permissionMap.values().toArray(new Permission[this.permissionMap.size()]);
    };
    /**获取会话中包含的所有权限信息。*/
    public String[] getPermissions() {
        String[] pers = new String[this.permissionMap.size()];
        int i = 0;
        for (Permission per : this.permissionMap.values()) {
            pers[i] = per.getPermissionCode();
            i++;
        }
        return pers;
    };
    /**判断会话中是否包含指定权限。*/
    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.getPermissionCode());
    };
    /**判断会话中是否包含指定权限。*/
    public boolean hasPermission(String permissionCode) {
        return this.permissionMap.containsKey(permissionCode);
    };
    /**是否已经登入。*/
    public boolean isLogin() {
        if (this.isClose())
            return false;
        //
        UserInfo userInfo = this.getUserObject();
        return userInfo != null;
    };
    /**判断是否为来宾帐号。来宾帐号是一种用户身份，通常用来表示不需要登入系统时使用的用户。
     * 用户使用来宾帐号登入系统虽然已经登入但是身份不会随着登入动作变为常规用户。*/
    public boolean isGuest() {
        if (this.isClose())
            return false;
        //
        UserInfo userInfo = this.getUserObject();
        if (userInfo == null)
            return false;
        return userInfo.isGuest();
    };
    /**获取session创建时间*/
    public long getLoginTime() throws SecurityException {
        if (this.isLogin() == true)
            return this.authSessionData.getLoginTime();
        else
            return -1;
    };
    /**获取一个值，该值决定了session是否支持从Cookie中恢复会话。*/
    public boolean supportCookieRecover() {
        return this.cookieRecover;
    };
    /**设置true表示支持会话从Cookie中会恢复登陆，每当重新登陆之后该值都会被重置为false。*/
    public void setSupportCookieRecover(boolean cookieRecover) {
        this.cookieRecover = cookieRecover;
    };
    /**获取SecurityContext对象*/
    protected SecurityContext getSecurityContext() {
        return this.securityContext;
    };
    /**放弃缓存中的权限数据，重新载入授权数据。*/
    public synchronized void reloadPermission() throws SecurityException {
        this.checkClose();/*Check*/
        //
        ISecurityAccess access = this.getSecurityContext().getSecurityAccess(this.authSessionData.getAuthSystem());
        List<Permission> perList = access.loadPermission(this.getUserObject());
        if (perList != null)
            for (Permission per : perList)
                this.permissionMap.put(per.getPermissionCode(), per);
        this.authSessionData.setLastTime(Clock.getSyncTime());
    }
    /**用指定的用户对象登入到权限系统，如果登陆失败会抛出SecurityException类型异常。*/
    public synchronized void doLogin(String authSystem, UserInfo user) throws SecurityException {
        this.checkClose();/*Check*/
        Assert.isNotNull(user);
        this.doLoginCode(authSystem, user.getUserCode());
    };
    /**用指定的用户userCode登入到权限系统，如果登陆失败会抛出SecurityException类型异常。*/
    public synchronized void doLoginCode(String authSystem, String userCode) throws SecurityException {
        this.checkClose();/*Check*/
        ISecurityAuth authApi = this.getSecurityContext().getSecurityAuth(authSystem);
        if (authApi == null)
            throw new SecurityException("Not register " + authSystem + " ISecurityAuth.");
        UserInfo userInfo = authApi.getUserInfo(userCode);
        if (userInfo != null) {
            this.userInfo = userInfo;
            this.authSessionData.setUserCode(this.userInfo.getUserCode());//用户标识码
            this.authSessionData.setAuthSystem(authSystem);
            this.authSessionData.setLoginTime(Clock.getSyncTime());//登陆时间
            this.authSessionData.setLastTime(Clock.getSyncTime());
            this.reloadPermission();/*重载权限*/
            this.refreshCacheTime();
            return;
        }
        throw new SecurityException("unknown user!");
    };
    /**用指定的用户帐号密码系统。*/
    public synchronized void doLogin(String authSystem, String account, String password) throws SecurityException {
        this.checkClose();/*Check*/
        ISecurityAuth authApi = this.getSecurityContext().getSecurityAuth(authSystem);
        if (authApi == null)
            throw new SecurityException("Not register " + authSystem + " ISecurityAuth.");
        UserInfo userInfo = authApi.getUserInfo(account, password);
        if (userInfo != null) {
            this.userInfo = userInfo;
            this.authSessionData.setUserCode(this.userInfo.getUserCode());//用户标识码
            this.authSessionData.setAuthSystem(authSystem);
            this.authSessionData.setLoginTime(Clock.getSyncTime());//登陆时间
            this.authSessionData.setLastTime(Clock.getSyncTime());
            this.reloadPermission();/*重载权限*/
            this.refreshCacheTime();
            return;
        }
        throw new SecurityException("unknown user!");
    };
    /**执行退出。*/
    public synchronized void doLogout() throws SecurityException {
        this.checkClose();/*Check*/
        this.cookieRecover = false;
        this.authSessionData = new SessionData();
        this.userInfo = null;
        this.permissionMap.clear();
        this.getSecurityContext().removeSessionData(this.sessionID);
    };
    /**关闭会话（退出会话，并且从当前线程中注销）。*/
    public synchronized void close() throws SecurityException {
        this.checkClose();/*Check*/
        this.doLogout();
        this.getSecurityContext().inactivationAuthSession(this);
        this.isClose = true;
    }
    /**判断会话是否关闭*/
    public boolean isClose() {
        return this.isClose;
    }
    private void checkClose() throws SecurityException {
        if (isClose())
            throw new SecurityException("AuthSession is closed!");
    }
    /**刷新权限数据在缓存中的时间,(未登录\来宾帐号\已经关闭)满足前面三个情况中任意一种时都放弃向缓存服务更新。*/
    protected synchronized void refreshCacheTime() {
        if (this.isClose() == true || this.isLogin() == false)
            return;
        SessionData cacheData = this.getSecurityContext().getSessionData(this.sessionID);
        if (cacheData == null || cacheData.getLastTime() < this.authSessionData.getLastTime()) {
            String[] proArray = this.permissionMap.keySet().toArray(new String[this.permissionMap.size()]);
            this.authSessionData.setPermissionSet(proArray);
            this.getSecurityContext().updateSessionData(this.sessionID, this.authSessionData);
        } else
            this.getSecurityContext().updateSessionData(this.sessionID);
    }
    /**装载权限数据*/
    protected void loadSessionData(SessionData sessionData) {
        this.permissionMap.clear();
        String[] perArray = sessionData.getPermissionSet();
        if (perArray != null)
            for (String per : perArray)
                this.permissionMap.put(per, new Permission(per));
        if (this.authSessionData == null)
            this.authSessionData = sessionData;
        this.authSessionData.setLastTime(Clock.getSyncTime());
    }
}