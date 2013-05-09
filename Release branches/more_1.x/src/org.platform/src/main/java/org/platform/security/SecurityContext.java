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
import javax.servlet.ServletException;
import org.platform.context.AppContext;
public interface SecurityContext {
    /**将参数表示的会话激活到当前线程。*/
    public boolean activateAuthSession(AuthSession activateAuthSession) throws SecurityException;
    /**将参数表示的会话激活到当前线程。*/
    public boolean activateAuthSession(String authSessionID) throws SecurityException;
    /**创建一个权限会话，参数为使用的授权系统*/
    public AuthSession createAuthSession() throws SecurityException;
    /**销毁服务*/
    public void destroySecurity(AppContext appContext);
    /**根据用户身份类型从当前线程会话列表中查找会话集合。（参数为空会返回当前线程上所有的会话。）*/
    public AuthSession[] findCurrentAuthSession(RoleIdentity userIdentity);
    //
    public AppContext getAppContext();
    /**通过AuthSessionID获取权限会话，不存在返回空。*/
    public AuthSession getAuthSession(String authSessionID) throws SecurityException;
    /**获取编码工具*/
    public Digest getCodeDigest(String name) throws SecurityException;
    /**获取当前线程绑定的权限会话集合。返回值不可以为空。*/
    public AuthSession[] getCurrentAuthSession();
    /**获取被标记为Blank的会话（来宾用户或者未登录的会话）*/
    public AuthSession getCurrentBlankAuthSession();
    /**获取当前来宾用户，如果存在的话。*/
    public AuthSession getCurrentGuestAuthSession();
    /**根据uri获取可用于跳转工具类。*/
    public SecurityDispatcher getDispatcher(String requestPath) throws ServletException;
    /**将Permission注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(Permission permission);
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode);
    /**根据uri获取用于判断权限的功能接口。*/
    public UriPatternMatcher getUriMatcher(String requestPath);
    /**从当前线程中活动的会话里去掉某个会话。*/
    public boolean inactivationAuthSession(AuthSession authSession);
    /**从当前线程中活动的会话里去掉某个会话。*/
    public boolean inactivationAuthSession(String sessionID);
    //
    /**初始化服务*/
    public void initSecurity(AppContext appContext);
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery();
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission);
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode);
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode);
}