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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 该类提供了获取与当前线程进行绑定的{@link AuthSession}。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SecurityContext {
    /**获取或创建一个{@link AuthSession}接口。*/
    public AuthSession getAuthSession(HttpServletRequest request, HttpServletResponse response, boolean created);
    /**获取或创建一个{@link AuthSession}接口。*/
    public AuthSession getAuthSession(String authSessionID, boolean created);
    /**获取或创建一个{@link AuthSession}接口。*/
    public AuthSession getAuthSession(HttpSession session, boolean created);
    /**获取当前的权限会话。*/
    public AuthSession getCurrentAuthSession();
    /**根据uri获取用于判断权限的功能接口。*/
    public UriPatternMatcher getUriMatcher(String requestPath);
    /**根据uri获取可用于跳转工具类。*/
    public SecurityDispatcher getDispatcher(String requestPath);
    //
    /**将Permission注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(Permission permission);
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode);
    //
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery();
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission);
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode);
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode);
}