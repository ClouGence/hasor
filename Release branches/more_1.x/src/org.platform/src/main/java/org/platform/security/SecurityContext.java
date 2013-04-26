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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import org.platform.Assert;
import org.platform.context.AppContext;
/**
 * 安全认证系统服务。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SecurityContext {
    private InternalDispatcherManager             dispatcherManager        = null;
    private InternalUriPatternMatcherManager      uriPatternMatcherManager = null;
    private InternalCodeDigestManager             codeDigestManager        = null;
    private UriPatternMatcher                     defaultRules             = null;
    private InternalSecurityQueryBuilder          securityQueryBuilder     = null; //负责处理SecurityQuer接口相关方法。
    private volatile Map<String, AuthSession>     authSessionMap           = null;
    private ThreadLocal<Map<String, AuthSession>> currentAuthSessionMap    = null;
    //
    public void initSecurity(AppContext appContext) {
        this.dispatcherManager = new InternalDispatcherManager();
        this.uriPatternMatcherManager = new InternalUriPatternMatcherManager();
        this.codeDigestManager = new InternalCodeDigestManager();
        //
        SecuritySettings setting = appContext.getBean(SecuritySettings.class);
        this.defaultRules = setting.getRulesDefault();
        //
        this.securityQueryBuilder = new InternalSecurityQueryBuilder(appContext);
        //
        this.dispatcherManager.initManager(appContext);
        this.uriPatternMatcherManager.initManager(appContext);
        this.codeDigestManager.initManager(appContext);
        //
        this.authSessionMap = new HashMap<String, AuthSession>();
        this.currentAuthSessionMap = new ThreadLocal<Map<String, AuthSession>>();
    }
    public void destroySecurity(AppContext appContext) {
        this.dispatcherManager.destroyManager(appContext);
        this.uriPatternMatcherManager.destroyManager(appContext);
        this.codeDigestManager.destroyManager(appContext);
        this.authSessionMap.clear();
        this.currentAuthSessionMap = new ThreadLocal<Map<String, AuthSession>>();
    }
    /**创建一个权限会话。*/
    public AuthSession createAuthSession() {
        AuthSession newAuthSession = this.newAuthSession();
        Assert.isNotNull(newAuthSession);
        String sessionID = newAuthSession.getSessionID();
        this.authSessionMap.put(sessionID, newAuthSession);
        this.activateAuthSession(sessionID);
        return newAuthSession;
    };
    /**处理新会话的创建由子类决定新会话的类型。*/
    protected abstract AuthSession newAuthSession();
    /**处理会话的关闭由子类决定新会话的类型。*/
    protected abstract void closeAuthSession(AuthSession authSession);
    /**通过AuthSessionID获取权限会话。*/
    public AuthSession getAuthSession(String authSessionID) {
        return this.authSessionMap.get(authSessionID);
    };
    /**判断权限系统中是否具有指定ID的权限会话，如果有返回true.*/
    public boolean hasAuthSession(String authSessionID) {
        return this.authSessionMap.containsKey(authSessionID);
    };
    /**将参数表示的会话激活到当前线程。*/
    public synchronized boolean activateAuthSession(String authSessionID) {
        Map<String, AuthSession> curSessionMap = this.currentAuthSessionMap.get();
        if (curSessionMap == null) {
            curSessionMap = new HashMap<String, AuthSession>();
            this.currentAuthSessionMap.set(curSessionMap);
        }
        if (this.hasAuthSession(authSessionID) == true && curSessionMap.containsKey(authSessionID) == false) {
            AuthSession authSession = this.getAuthSession(authSessionID);
            curSessionMap.put(authSessionID, authSession);
            return true;
        }
        return false;
    };
    /**从当前线程中活动的会话里去掉某个会话。*/
    public synchronized boolean inactivationAuthSession(String sessionID) {
        Map<String, AuthSession> curSessionMap = this.currentAuthSessionMap.get();
        if (curSessionMap == null)
            return false;
        //
        boolean returnData = false;
        if (curSessionMap.containsKey(sessionID) == true) {
            curSessionMap.remove(sessionID);
            returnData = true;
        }
        //
        if (curSessionMap.size() == 0)
            this.currentAuthSessionMap.remove();
        //
        return returnData;
    };
    /**获取当前线程绑定的权限会话集合。返回值不可以为空。*/
    public AuthSession[] getCurrentAuthSession() {
        Map<String, AuthSession> curSessionMap = this.currentAuthSessionMap.get();
        if (curSessionMap == null || curSessionMap.size() == 0)
            return new AuthSession[0];
        else {
            Collection<AuthSession> curAuthSessionSet = curSessionMap.values();
            return curAuthSessionSet.toArray(new AuthSession[curAuthSessionSet.size()]);
        }
    };
    /**获取编码工具*/
    public CodeDigest getCodeDigest(String name) throws SecurityException {
        CodeDigest digest = this.codeDigestManager.getCodeDigest(name);
        if (digest == null)
            throw new SecurityException("CodeDigest :" + name + " is Undefined.");
        return digest;
    };
    /**根据uri获取用于判断权限的功能接口。*/
    public UriPatternMatcher getUriMatcher(String requestPath) {
        UriPatternMatcher matcher = this.uriPatternMatcherManager.getUriMatcher(requestPath);
        return (matcher == null) ? this.defaultRules : matcher;
    }
    /**根据uri获取可用于跳转工具类。*/
    public SecurityDispatcher getDispatcher(String requestPath) throws ServletException {
        SecurityDispatcher dispatcher = this.dispatcherManager.getDispatcher(requestPath);
        if (dispatcher == null)
            throw new ServletException("no match SecurityDispatcher to " + requestPath + "");
        return dispatcher;
    };
    /**将Permission注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(Permission permission) {
        return this.securityQueryBuilder.getSecurityCondition(permission);
    }
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode) {
        return this.securityQueryBuilder.getSecurityCondition(permissionCode);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery() {
        return this.securityQueryBuilder.newSecurityQuery();
    };
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission) {
        return this.securityQueryBuilder.newSecurityQuery(permission);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode) {
        return this.securityQueryBuilder.newSecurityQuery(permissionCode);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode) {
        return this.securityQueryBuilder.newSecurityQuery(testNode);
    }
}