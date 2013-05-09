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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import org.platform.context.AppContext;
import org.platform.security.DefaultSecurityQuery.CheckPermission;
/**
 * 安全认证系统服务，子类只需要解决SessionData存储就可以了。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SecurityContext {
    private AppContext                       appContext               = null;
    private InternalCodeDigestManager        codeDigestManager        = null;
    private ThreadLocal<List<AuthSession>>   currentAuthSessionList   = null;
    private UriPatternMatcher                defaultRules             = null;
    private InternalDispatcherManager        dispatcherManager        = null;
    private ManagedSecurityAccessManager     securityAccessManager    = null;
    private ManagedSecurityAuthManager       securityAuthManager      = null;
    private SecuritySettings                 settings                 = null;
    private InternalUriPatternMatcherManager uriPatternMatcherManager = null;
    /**将参数表示的会话激活到当前线程。*/
    public synchronized boolean activateAuthSession(AuthSession activateAuthSession) throws SecurityException {
        if (activateAuthSession == null)
            return false;
        //
        List<AuthSession> curSessionList = this.currentAuthSessionList.get();
        if (curSessionList == null) {
            curSessionList = new ArrayList<AuthSession>();
            this.currentAuthSessionList.set(curSessionList);
        }
        //
        String activateAuthSessionID = activateAuthSession.getSessionID();
        for (AuthSession authSession : curSessionList) {
            if (authSession.getSessionID().equals(activateAuthSessionID) == true)
                return false;
        }
        //
        curSessionList.add(activateAuthSession);
        this.throwEvent(SecurityEventDefine.AuthSession_Activate, activateAuthSession);/*抛出事件*/
        //
        return true;
    }
    /**将参数表示的会话激活到当前线程。*/
    public synchronized boolean activateAuthSession(String authSessionID) throws SecurityException {
        AuthSession authSession = this.getAuthSession(authSessionID);/*该方法会引发锁住authSessionID的动作*/
        if (authSession != null)
            return this.activateAuthSession(authSession);
        return false;
    }
    /**创建一个权限会话，参数为使用的授权系统*/
    public synchronized AuthSession createAuthSession() throws SecurityException {
        AuthSession newAuthSession = this.newAuthSession(AppContext.genIDBy36());
        newAuthSession.loadSessionData(new SessionData());
        this.activateAuthSession(newAuthSession);
        return newAuthSession;
    }
    /**销毁服务*/
    public synchronized void destroySecurity(AppContext appContext) {
        this.dispatcherManager.destroyManager(appContext);
        this.uriPatternMatcherManager.destroyManager(appContext);
        this.codeDigestManager.destroyManager(appContext);
        this.securityAuthManager.destroyManager(appContext);
        this.securityAccessManager.destroyManager(appContext);
        //
        this.currentAuthSessionList = new ThreadLocal<List<AuthSession>>();
    };
    /**根据用户身份类型从当前线程会话列表中查找会话集合。（参数为空会返回当前线程上所有的会话。）*/
    public AuthSession[] findCurrentAuthSession(RoleIdentity userIdentity) {
        AuthSession[] authSessionArray = getCurrentAuthSession();
        if (userIdentity == null)
            return authSessionArray;
        ArrayList<AuthSession> finds = new ArrayList<AuthSession>();
        for (AuthSession authSession : authSessionArray) {
            UserInfo userInfo = authSession.getUserObject();
            if (userInfo == null)
                continue;
            if (userInfo.getIdentity().equals(userIdentity) == true)
                finds.add(authSession);
        }
        return finds.toArray(new AuthSession[finds.size()]);
    };
    //
    public AppContext getAppContext() {
        return appContext;
    };
    /**通过AuthSessionID获取权限会话，不存在返回空。*/
    public AuthSession getAuthSession(String authSessionID) throws SecurityException {
        SessionData sessionData = this.getSessionData(authSessionID);
        if (sessionData != null) {
            AuthSession newAuthSession = this.newAuthSession(authSessionID);
            newAuthSession.loadSessionData(sessionData);
            return newAuthSession;
        }
        return null;
    }
    /**获取编码工具*/
    public Digest getCodeDigest(String name) throws SecurityException {
        Digest digest = this.codeDigestManager.getCodeDigest(name);
        if (digest == null)
            throw new SecurityException("CodeDigest :" + name + " is Undefined.");
        return digest;
    };
    /**获取当前线程绑定的权限会话集合。返回值不可以为空。*/
    public AuthSession[] getCurrentAuthSession() {
        List<AuthSession> curSessionMap = this.currentAuthSessionList.get();
        if (curSessionMap == null || curSessionMap.size() == 0)
            return new AuthSession[0];
        else {
            return curSessionMap.toArray(new AuthSession[curSessionMap.size()]);
        }
    }
    /**获取被标记为Blank的会话（来宾用户或者未登录的会话）*/
    public AuthSession getCurrentBlankAuthSession() {
        AuthSession[] authList = getCurrentAuthSession();
        if (authList != null)
            for (AuthSession auth : authList)
                if (auth.isBlank() == true)
                    return auth;
        return null;
    };
    /**获取当前来宾用户，如果存在的话。*/
    public AuthSession getCurrentGuestAuthSession() {
        AuthSession[] authList = getCurrentAuthSession();
        if (authList != null)
            for (AuthSession auth : authList)
                if (auth.isGuest() == true)
                    return auth;
        return null;
    };
    /**根据uri获取可用于跳转工具类。*/
    public SecurityDispatcher getDispatcher(String requestPath) throws ServletException {
        return this.dispatcherManager.getDispatcher(requestPath);
    }
    /**获取{@link ISecurityAccess}接口对象，如果不存在返回null。*/
    protected ISecurityAccess getSecurityAccess(String authName) {
        return securityAccessManager.getSecurityAccess(authName, this.appContext);
    }
    /**获取{@link ISecurityAuth}接口对象，如果不存在返回null。*/
    protected ISecurityAuth getSecurityAuth(String authName) throws SecurityException {
        return securityAuthManager.getSecurityAuth(authName, this.appContext);
    };
    /**将Permission注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(Permission permission) {
        return new CheckPermission(permission);
    }
    /**将String注解转换为SecurityNode。*/
    public SecurityNode getSecurityCondition(String permissionCode) {
        return new CheckPermission(new Permission(permissionCode));
    };
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract SessionData getSessionData(String sessionDataID);
    /**根据uri获取用于判断权限的功能接口。*/
    public UriPatternMatcher getUriMatcher(String requestPath) {
        UriPatternMatcher matcher = this.uriPatternMatcherManager.getUriMatcher(requestPath);
        return (matcher == null) ? this.defaultRules : matcher;
    }
    /**从当前线程中活动的会话里去掉某个会话。*/
    public synchronized boolean inactivationAuthSession(AuthSession authSession) {
        return this.inactivationAuthSession(authSession.getSessionID());
    }
    /**从当前线程中活动的会话里去掉某个会话。*/
    public synchronized boolean inactivationAuthSession(String sessionID) {
        List<AuthSession> curSessionList = this.currentAuthSessionList.get();
        if (curSessionList == null)
            return false;
        //
        AuthSession removeAuthSession = null;
        for (AuthSession authSession : curSessionList) {
            if (authSession.getSessionID().equals(sessionID) == true)
                removeAuthSession = authSession;
        }
        //
        if (removeAuthSession != null) {
            curSessionList.remove(removeAuthSession);
            if (curSessionList.size() == 0)
                this.currentAuthSessionList.remove();
            this.throwEvent(SecurityEventDefine.AuthSession_Inactivation, removeAuthSession);/*抛出事件*/
            return true;
        }
        //
        return false;
    }
    //
    /**初始化服务*/
    public synchronized void initSecurity(AppContext appContext) {
        this.appContext = appContext;
        //
        this.dispatcherManager = new InternalDispatcherManager();
        this.uriPatternMatcherManager = new InternalUriPatternMatcherManager();
        this.codeDigestManager = new InternalCodeDigestManager();
        this.securityAuthManager = new ManagedSecurityAuthManager();
        this.securityAccessManager = new ManagedSecurityAccessManager();
        //
        this.settings = appContext.getInstance(SecuritySettings.class);
        this.defaultRules = settings.getRulesDefault();
        //
        this.dispatcherManager.initManager(appContext);
        this.uriPatternMatcherManager.initManager(appContext);
        this.codeDigestManager.initManager(appContext);
        this.securityAuthManager.initManager(appContext);
        this.securityAccessManager.initManager(appContext);
        //
        this.currentAuthSessionList = new ThreadLocal<List<AuthSession>>();
    };
    /**新建AuthSession*/
    protected AuthSession newAuthSession(String authSessionID) throws SecurityException {
        AuthSession newAuthSession = new AuthSession(authSessionID, this) {};
        this.throwEvent(SecurityEventDefine.AuthSession_New, newAuthSession);/*抛出事件*/
        return newAuthSession;
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery() {
        return this.appContext.getGuice().getInstance(SecurityQuery.class);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(Permission permission) {
        return this.newSecurityQuery().and(permission);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(SecurityNode testNode) {
        return this.newSecurityQuery().andCustomer(testNode);
    }
    /**创建{@link SecurityQuery} 类，该类可以用来测试用户的权限。*/
    public SecurityQuery newSecurityQuery(String permissionCode) {
        return this.newSecurityQuery().and(permissionCode);
    }
    //
    /**同步方式抛出事件。*/
    protected abstract void throwEvent(String eventType, Object... objects);
    //
    /**使用SessionData的数据创建AuthSession。*/
    protected abstract void removeSessionData(String sessionDataID);
    /**仅仅刷新缓存数据的过期时间*/
    protected abstract void updateSessionData(String sessionID);
    /**更新SessionData*/
    protected abstract void updateSessionData(String sessionDataID, SessionData newSessionData);
}