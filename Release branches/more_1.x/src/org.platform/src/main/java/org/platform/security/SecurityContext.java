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
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import org.platform.context.AppContext;
import org.platform.security.DefaultSecurityQuery.CheckPermission;
/**
 * 安全认证系统服务，子类只需要解决SessionData存储就可以了。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SecurityContext {
    private AppContext                            appContext               = null;
    private InternalDispatcherManager             dispatcherManager        = null;
    private InternalUriPatternMatcherManager      uriPatternMatcherManager = null;
    private InternalCodeDigestManager             codeDigestManager        = null;
    private UriPatternMatcher                     defaultRules             = null;
    private ThreadLocal<Map<String, AuthSession>> currentAuthSessionMap    = null;
    //
    //
    //
    public ISecurityAuth getSecurityAuth(String userAuthCode) {
        return null;
    }
    protected abstract AuthSession newAuthSession(SessionData sessionData);
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract SessionData createSessionData();
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract void removeSessionData(SessionData authSessionID);
    /**更新SessionData*/
    protected abstract void updateSessionData(SessionData sessionData);
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract SessionData getSessionDataByID(String authSessionID);
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract List<SessionData> getSessionDataList();
    //
    //
    //
    /**初始化服务*/
    public synchronized void initSecurity(AppContext appContext) {
        this.appContext = appContext;
        //
        this.dispatcherManager = new InternalDispatcherManager();
        this.uriPatternMatcherManager = new InternalUriPatternMatcherManager();
        this.codeDigestManager = new InternalCodeDigestManager();
        //
        SecuritySettings setting = appContext.getBean(SecuritySettings.class);
        this.defaultRules = setting.getRulesDefault();
        //
        this.dispatcherManager.initManager(appContext);
        this.uriPatternMatcherManager.initManager(appContext);
        this.codeDigestManager.initManager(appContext);
        //
        this.currentAuthSessionMap = new ThreadLocal<Map<String, AuthSession>>();
    }
    /**销毁服务*/
    public synchronized void destroySecurity(AppContext appContext) {
        this.dispatcherManager.destroyManager(appContext);
        this.uriPatternMatcherManager.destroyManager(appContext);
        this.codeDigestManager.destroyManager(appContext);
        this.currentAuthSessionMap = new ThreadLocal<Map<String, AuthSession>>();
    }
    /**创建一个权限会话。*/
    public synchronized AuthSession createAuthSession() {
        /*1.新建SessionData，并且扔进缓存里*/
        SessionData sessionData = this.createSessionData();
        AuthSession newAuthSession = this.newAuthSession(sessionData);
        /*3.放入ThreadLocal*/
        Map<String, AuthSession> curSessionMap = this.currentAuthSessionMap.get();
        if (curSessionMap == null) {
            curSessionMap = new HashMap<String, AuthSession>();
            this.currentAuthSessionMap.set(curSessionMap);
        }
        curSessionMap.put(newAuthSession.getSessionID(), newAuthSession);
        return newAuthSession;
    };
    /**通过AuthSessionID获取权限会话，不存在返回空。*/
    public AuthSession getAuthSession(String authSessionID) {
        SessionData sessionData = this.getSessionDataByID(authSessionID);
        if (sessionData != null)
            return this.newAuthSession(sessionData);
        return null;
    };
    /**判断权限系统中是否具有指定ID的权限会话，如果有返回true.*/
    public boolean hasAuthSession(String authSessionID) {
        return this.getSessionDataByID(authSessionID) != null;
    };
    /**将参数表示的会话激活到当前线程。*/
    public synchronized boolean activateAuthSession(String authSessionID) {
        Map<String, AuthSession> curSessionMap = this.currentAuthSessionMap.get();
        if (curSessionMap == null) {
            curSessionMap = new HashMap<String, AuthSession>();
            this.currentAuthSessionMap.set(curSessionMap);
        }
        if (this.hasAuthSession(authSessionID) == true && curSessionMap.containsKey(authSessionID) == false) {
            AuthSession authSession = this.getAuthSession(authSessionID);/*该方法会引发锁住authSessionID的动作*/
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
    public Digest getCodeDigest(String name) throws SecurityException {
        Digest digest = this.codeDigestManager.getCodeDigest(name);
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
        return new CheckPermission(permission);
    }
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode) {
        return new CheckPermission(new Permission(permissionCode));
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery() {
        return this.appContext.getGuice().getInstance(SecurityQuery.class);
    };
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission) {
        return this.newSecurityQuery().and(permission);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode) {
        return this.newSecurityQuery().and(permissionCode);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode) {
        return this.newSecurityQuery().andCustomer(testNode);
    }
}s