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
package org.moreframework.security;
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登入之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AuthSession {
    public static final String HttpSessionAuthSessionSetName = AuthSession.class.getName();
    /**获取会话ID。*/
    public abstract String getSessionID();
    /**获取登入的用户对象，如果未登录系统而且启用了来宾帐号则会返回来宾帐号。*/
    public abstract UserInfo getUserObject();
    /**获取登陆会话时使用的具体权限系统。*/
    public abstract String getAuthSystem();
    /**向会话添加一条临时权限。*/
    public abstract void addPermission(Permission permission) throws SecurityException;
    /**向会话添加一条临时权限。*/
    public abstract void addPermission(String permissionCode) throws SecurityException;
    /**临时撤销用户会话中一条权限。*/
    public abstract void removeTempPermission(Permission permission) throws SecurityException;
    /**临时撤销用户会话中一条权限。*/
    public abstract void removeTempPermission(String permissionCode) throws SecurityException;
    /**获取会话中包含的所有权限信息。*/
    public abstract Permission[] getPermissionObjects();
    /**获取会话中包含的所有权限信息。*/
    public abstract String[] getPermissions();
    /**判断会话中是否包含指定权限。*/
    public abstract boolean hasPermission(Permission permission);
    /**判断会话中是否包含指定权限。*/
    public abstract boolean hasPermission(String permissionCode);
    /**判断会话是否关闭*/
    public abstract boolean isClose();
    /**是否已经登入。*/
    public abstract boolean isLogin();
    /**判断是否为来宾帐号。来宾帐号是一种用户身份，通常用来表示不需要登入系统时使用的用户。
     * 用户使用来宾帐号登入系统虽然已经登入但是身份不会随着登入动作变为常规用户。*/
    public abstract boolean isGuest();
    /**是否为空白状态，新的Session、退出之后的.但是尚未关闭的。*/
    public abstract boolean isBlank();
    /**获取session创建时间*/
    public abstract long getLoginTime() throws SecurityException;
    /**放弃缓存中的权限数据，重新载入授权数据。*/
    public abstract void reloadPermission() throws SecurityException;
    /**用指定的用户对象登入到权限系统，如果登陆失败会抛出SecurityException类型异常。*/
    public abstract void doLogin(String authSystem, UserInfo user) throws SecurityException;
    /**用指定的用户userCode登入到权限系统，如果登陆失败会抛出SecurityException类型异常。*/
    public abstract void doLoginCode(String authSystem, String userCode) throws SecurityException;
    /**用指定的用户帐号密码系统。*/
    public abstract void doLogin(String authSystem, String account, String password) throws SecurityException;
    /**执行退出。*/
    public abstract void doLogout() throws SecurityException;
    /**关闭会话（退出会话，并且从当前线程中注销）。*/
    public abstract void close() throws SecurityException;
    /**刷新权限数据在缓存中的时间,(未登录\来宾帐号\已经关闭)满足前面三个情况中任意一种时都放弃向缓存服务更新。*/
    public abstract void refreshCacheTime();
}