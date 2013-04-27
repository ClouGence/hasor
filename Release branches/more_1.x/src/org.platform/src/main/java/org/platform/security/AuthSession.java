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
import java.util.HashSet;
import org.more.util.StringUtil;
import org.platform.Assert;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AuthSession {
    private SessionData     sessionData     = null;
    private UserInfo        userInfo        = null;
    private SecurityContext securityContext = null;
    //
    public AuthSession(SessionData authSessionData, SecurityContext securityContext) {
        this.sessionData = authSessionData;
        this.securityContext = securityContext;
        Assert.isNotNull(authSessionData);
        Assert.isNotNull(securityContext);
    }
    /**获取会话ID。*/
    public String getSessionID() {
        return this.sessionData.getSessionID();
    };
    /**获取登入的用户对象，如果未登录系统而且启用了来宾帐号则会返回来宾帐号。*/
    public UserInfo getUserObject() {
        if (this.userInfo == null) {
            String userCode = this.sessionData.getUserCode();
            if (StringUtil.isBlank(userCode) == false) {
                String userAuthCode = this.sessionData.getFromAuth();
                ISecurityAuth auth = this.getSecurityContext().getSecurityAuth(userAuthCode);
                this.userInfo = auth.getUserInfo(userCode);
            }
        }
        return this.userInfo;
    };
    /**向会话添加一条临时权限。*/
    public void addPermission(Permission permission) {
        this.addPermission(permission.getPermissionCode());
    };
    /**向会话添加一条临时权限。*/
    public void addPermission(String permissionCode) {
        String[] pers = this.sessionData.getPermissionSet();
        HashSet<String> persArray = new HashSet<String>();
        for (String per : pers)
            persArray.add(per);
        persArray.add(permissionCode);
        pers = persArray.toArray(new String[persArray.size()]);
        this.sessionData.setPermissionSet(pers);
        this.getSecurityContext().updateSessionData(this.sessionData);
    };
    /**临时撤销用户会话中一条权限。*/
    public void removeTempPermission(Permission permission) {
        this.removeTempPermission(permission.getPermissionCode());
    };
    /**临时撤销用户会话中一条权限。*/
    public void removeTempPermission(String permissionCode) {
        String[] pers = this.sessionData.getPermissionSet();
        HashSet<String> persArray = new HashSet<String>();
        for (String per : pers)
            if (per.equals(permissionCode) == false)
                persArray.add(per);
        pers = persArray.toArray(new String[persArray.size()]);
        this.sessionData.setPermissionSet(pers);
        this.getSecurityContext().updateSessionData(this.sessionData);
    };
    /**获取会话中包含的所有权限信息。*/
    public String[] getPermissions() {
        String[] pers = this.sessionData.getPermissionSet();
        if (pers == null)
            return new String[0];
        else
            return pers;
    };
    /**判断会话中是否包含指定权限。*/
    public boolean hasPermission(Permission permission) {
        return this.hasPermission(permission.getPermissionCode());
    };
    /**判断会话中是否包含指定权限。*/
    public boolean hasPermission(String permissionCode) {
        String[] pers = this.sessionData.getPermissionSet();
        for (String per : pers)
            if (per.equals(permissionCode) == true)
                return true;
        return false;
    };
    /**是否已经登入*/
    public boolean isLogin() {
        return this.sessionData.isLoginMark();
    };
    /**判断是否为来宾帐号。来宾帐号是一种用户身份，通常用来表示不需要登入系统时使用的用户。
     * 用户使用来宾帐号登入系统虽然已经登入但是身份不会随着登入动作变为常规用户。*/
    public boolean isGuest() {
        if (this.userInfo == null)
            return false;
        return this.userInfo.isGuest();
    };
    /**获取session创建时间*/
    public long getCreatedTime() {
        return this.sessionData.getCreatedTime();
    };
    /**获取一个值，该值决定了session是否支持从Cookie中恢复会话。*/
    public boolean supportCookieRecover() {
        return this.sessionData.isCookieRecover();
    };
    /**设置true表示支持会话从Cookie中会恢复登陆，每当重新登陆之后该值都会被重置为false。*/
    public void setSupportCookieRecover(boolean cookieRecover) {
        this.sessionData.setCookieRecover(cookieRecover);
        this.getSecurityContext().updateSessionData(this.sessionData);
    };
    protected SecurityContext getSecurityContext() {
        return this.securityContext;
    };
    //
    //
    //
    //
    //
    /**登陆来宾帐号，该方法会将recover属性重置为false。*/
    public synchronized void doLoginGuest(){};
    /**用指定的用户对象登入到权限系统，该方法会将recover属性重置为false。*/
    public synchronized void doLogin(UserInfo user) throws SecurityException{};
    /**用指定的用户Code登陆系统（如果支持），该方法会将recover属性重置为false。*/
    public synchronized void doLoginCode(String userCode) throws SecurityException{};
    /**用指定的用户帐号密码系统，该方法会将recover属性重置为false。*/
    public synchronized void doLogin(String account, String password) throws SecurityException{};
    /**执行退出，该方法会将recover属性重置为false。*/
    public synchronized void doLogout() throws SecurityException{};
    /**关闭会话。*/
    public synchronized void close(){};
}