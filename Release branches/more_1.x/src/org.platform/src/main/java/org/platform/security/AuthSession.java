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
public abstract class AuthSession {
    /**获取登陆的用户对象，如果未登录系统而且启用了来宾帐号则会返回来宾帐号。*/
    public abstract IUser getUserObject();
    /**用指定的用户对象登陆到权限系统。*/
    public abstract void doLogin(IUser user);
    /**用指定的用户帐号密码系统。*/
    public abstract void doLogin(String account, String password);
    /**执行退出。*/
    public abstract void doLogout();
    /**添加会话级别临时权限，当会话消失权限消失。*/
    public abstract void addTempPermission(Permission permission);
    /**是否已经登陆*/
    public abstract boolean isLogin();
    /**判断是否为来宾帐号，当启用了来宾帐号，并且尚未登陆系统的情况下该值返回为true。*/
    public abstract boolean isGuest();
    /**关闭会话*/
    public abstract void close();
}