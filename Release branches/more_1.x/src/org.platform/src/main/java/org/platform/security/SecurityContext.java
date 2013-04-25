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
import java.util.Map;
import javax.servlet.ServletException;
import org.platform.context.AppContext;
/**
 * 该类提供了获取与当前线程进行绑定的{@link AuthSession}。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SecurityContext {
    private InternalDispatcherManager        dispatcherManager        = null;
    private InternalUriPatternMatcherManager uriPatternMatcherManager = null;
    private UriPatternMatcher                defaultRules             = null;
    private Map<String, CodeDigest>          codeDigestMap            = null;
    private InternalSecurityQueryBuilder     securityQueryBuilder     = null; //负责处理SecurityQuer接口相关方法。
    //
    public void initSecurity(AppContext appContext) {
        this.securityQueryBuilder = new InternalSecurityQueryBuilder(appContext);
        this.dispatcherManager = new InternalDispatcherManager();
        this.uriPatternMatcherManager = new InternalUriPatternMatcherManager();
        //
        this.dispatcherManager.initManager(appContext);
        this.uriPatternMatcherManager.initManager(appContext);
        //
        SecuritySettings setting = appContext.getBean(SecuritySettings.class);
        this.defaultRules = setting.getRulesDefault();
    }
    public void destroySecurity(AppContext appContext) {
        this.codeDigestMap.clear();
    }
    /**创建一个权限会话。*/
    public abstract AuthSession createAuthSession();
    /**通过AuthSessionID获取权限会话。*/
    public AuthSession getAuthSession(String authSessionID);
    /**判断权限系统中是否具有指定ID的权限会话，如果有返回true.*/
    public boolean hasAuthSession(String authSessionID);
    /**激活authSessionID参数表示的权限会话，被激活的权限会话将处于当前线程会话中。*/
    public void activateAuthSession(String authSessionID);
    /***/
    public void inactivationAuthSession(String sessionID);
    /**获取当前线程绑定的权限会话集合。*/
    public AuthSession[] getCurrentAuthSession();
    /**获取编码工具*/
    public CodeDigest getCodeDigest(String name) throws SecurityException {
        if (this.codeDigestMap.containsKey(name) == true)
            return this.codeDigestMap.get(name);
        throw new SecurityException("CodeDigest :" + name + " is Undefined.");
    };
    //
    //
    //
    //
    //
    //
    //
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