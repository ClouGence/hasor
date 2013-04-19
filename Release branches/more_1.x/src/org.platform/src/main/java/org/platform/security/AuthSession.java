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
/**
 * 负责权限系统中的用户会话。用户会话中保存了用户登陆之后的权限数据。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AuthSession {
    /**获取会话ID*/
    public String getSessionID();
    /**获取登陆的用户对象，如果未登录系统而且启用了来宾帐号则会返回来宾帐号。*/
    public UserInfo getUserObject();
    /**用指定的用户对象登陆到权限系统。*/
    public boolean doLogin(UserInfo user) throws SecurityException;
    /**用指定的用户帐号密码系统。*/
    public boolean doLogin(String account, String password) throws SecurityException;
    /**执行退出。*/
    public boolean doLogout() throws SecurityException;
    /**向会话添加一条临时权限。*/
    public void addPermission(Permission permission);
    /**临时撤销用户会话中一条权限。*/
    public void removeTempPermission(Permission permission);
    /**获取会话中包含的所有权限信息。*/
    public Permission[] getPermissions();
    /**是否已经登陆*/
    public boolean isLogin();
    /**判断是否为来宾帐号，当启用了来宾帐号，并且尚未登陆系统的情况下该值返回为true。*/
    public boolean isGuest();
    /**关闭会话*/
    public void close();
}