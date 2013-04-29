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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AbstractModuleListener;
import org.platform.context.AppContext;
import org.platform.context.InitListener;
import org.platform.context.setting.Config;
import org.platform.security.Power.Level;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.internal.UniqueAnnotations;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 支持Service等注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "SecurityModuleServiceListener", description = "org.platform.security软件包功能支持。", startIndex = 1)
public class SecurityModuleServiceListener extends AbstractModuleListener {
    private SecurityContext         secService  = null;
    private SecuritySessionListener secListener = null;
    private SecuritySettings        settings    = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        /*配置*/
        this.settings = new SecuritySettings();
        this.settings.loadConfig(event.getSettings());
        /*HttpSession创建和销毁通知机制*/
        this.secListener = new SecuritySessionListener();
        event.sessionListener().bind(this.secListener);
        /*request，请求拦截器*/
        event.filter("*").through(SecurityFilter.class);
        /*aop，方法执行权限支持*/
        event.getGuiceBinder().bindInterceptor(new ClassPowerMatcher(), new MethodPowerMatcher(), new SecurityInterceptor());/*注册Aop*/
        /*装载SecurityAccess*/
        this.loadSecurityAuth(event);
        this.loadSecurityAccess(event);
        /*绑定核心功能实现类。*/
        event.getGuiceBinder().bind(SecuritySettings.class).toInstance(this.settings);//通过Guice
        event.getGuiceBinder().bind(SecurityContext.class).to(InternalSecurityService.class).asEagerSingleton();
        event.getGuiceBinder().bind(SecurityQuery.class).to(DefaultSecurityQuery.class);
    }
    @Override
    public void initialized(AppContext appContext) {
        Config systemConfig = appContext.getInitContext().getConfig();
        systemConfig.addSettingsListener(this.settings);
        //
        this.secService = appContext.getBean(SecurityContext.class);
        this.secService.initSecurity(appContext);
        Platform.info("online ->> security is " + (this.settings.isEnable() ? "enable." : "disable."));
    }
    @Override
    public void destroy(AppContext appContext) {
        Config systemConfig = appContext.getInitContext().getConfig();
        systemConfig.removeSettingsListener(this.settings);
        //
        this.secService.destroySecurity(appContext);
    }
    //
    /*装载SecurityAccess*/
    protected void loadSecurityAuth(ApiBinder event) {
        Platform.info("begin loadSecurityAuth...");
        //1.获取
        Set<Class<?>> authSet = event.getClassSet(SecurityAuth.class);
        List<Class<? extends ISecurityAuth>> authList = new ArrayList<Class<? extends ISecurityAuth>>();
        for (Class<?> cls : authSet) {
            if (ISecurityAuth.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadSecurityAuth : not implemented ISecurityAuth of type " + Platform.logString(cls));
            } else {
                authList.add((Class<? extends ISecurityAuth>) cls);
            }
        }
        //3.注册服务
        Binder binder = event.getGuiceBinder();
        Map<String, Integer> authIndex = new HashMap<String, Integer>();
        for (Class<? extends ISecurityAuth> authType : authList) {
            SecurityAuth authAnno = authType.getAnnotation(SecurityAuth.class);
            Key<? extends ISecurityAuth> authKey = Key.get(authType);
            String authSystem = authAnno.authSystem();
            //
            SecurityAuthDefinition authDefine = new SecurityAuthDefinition(authSystem, authKey);
            int maxIndex = (authIndex.containsKey(authSystem) == false) ? Integer.MAX_VALUE : authIndex.get(authSystem);
            if (authAnno.sort() <= maxIndex/*值越小越优先*/) {
                authIndex.put(authSystem, authAnno.sort());
                binder.bind(SecurityAuthDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(authDefine);
                binder.bind(ISecurityAuth.class).annotatedWith(UniqueAnnotations.create()).toProvider(authDefine);
                Platform.info(authSystem + "[" + authAnno.sort() + "] at SecurityAuth of type " + Platform.logString(authType));
            }
        }
    }
    //
    /*装载SecurityAccess*/
    protected void loadSecurityAccess(ApiBinder event) {
        Platform.info("begin loadSecurityAccess...");
        //1.获取
        Set<Class<?>> accessSet = event.getClassSet(SecurityAccess.class);
        List<Class<? extends ISecurityAccess>> accessList = new ArrayList<Class<? extends ISecurityAccess>>();
        for (Class<?> cls : accessSet) {
            if (ISecurityAccess.class.isAssignableFrom(cls) == false) {
                Platform.warning("loadSecurityAccess : not implemented ISecurityAccess of type " + Platform.logString(cls));
            } else {
                accessList.add((Class<? extends ISecurityAccess>) cls);
            }
        }
        //3.注册服务
        Binder binder = event.getGuiceBinder();
        Map<String, Integer> accessIndex = new HashMap<String, Integer>();
        for (Class<? extends ISecurityAccess> accessType : accessList) {
            SecurityAccess accessAnno = accessType.getAnnotation(SecurityAccess.class);
            Key<? extends ISecurityAccess> accessKey = Key.get(accessType);
            String authSystem = accessAnno.authSystem();
            //
            SecurityAccessDefinition accessDefine = new SecurityAccessDefinition(authSystem, accessKey);
            int maxIndex = (accessIndex.containsKey(authSystem) == false) ? Integer.MAX_VALUE : accessIndex.get(authSystem);
            if (accessAnno.sort() <= maxIndex/*值越小越优先*/) {
                accessIndex.put(authSystem, accessAnno.sort());
                binder.bind(SecurityAccessDefinition.class).annotatedWith(UniqueAnnotations.create()).toInstance(accessDefine);
                binder.bind(ISecurityAccess.class).annotatedWith(UniqueAnnotations.create()).toProvider(accessDefine);
                Platform.info(authSystem + "[" + accessAnno.sort() + "] at SecurityAccess of type " + Platform.logString(accessType));
            }
        }
    }
    /*-------------------------------------------------------------------------------------*/
    /*负责检测类是否匹配。规则：只要类型或方法上标记了@Power。*/
    private class ClassPowerMatcher extends AbstractMatcher<Class<?>> {
        @Override
        public boolean matches(Class<?> matcherType) {
            /*如果处于禁用状态则忽略权限检测*/
            if (settings.isEnableMethod() == false)
                return false;
            /*----------------------------*/
            if (matcherType.isAnnotationPresent(Power.class) == true)
                return true;
            Method[] m1s = matcherType.getMethods();
            Method[] m2s = matcherType.getDeclaredMethods();
            for (Method m1 : m1s) {
                if (m1.isAnnotationPresent(Power.class) == true)
                    return true;
            }
            for (Method m2 : m2s) {
                if (m2.isAnnotationPresent(Power.class) == true)
                    return true;
            }
            return false;
        }
    }
    /*负责检测方法是否匹配。规则：方法或方法所处类上标记了@Power。*/
    private class MethodPowerMatcher extends AbstractMatcher<Method> {
        @Override
        public boolean matches(Method matcherType) {
            /*如果处于禁用状态则忽略权限检测*/
            if (settings.isEnableMethod() == false)
                return false;
            /*----------------------------*/
            if (matcherType.isAnnotationPresent(Power.class) == true)
                return true;
            if (matcherType.getDeclaringClass().isAnnotationPresent(Power.class) == true)
                return true;
            return false;
        }
    }
    /*拦截器*/
    private class SecurityInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            /*如果处于禁用状态则忽略权限检测*/
            if (settings.isEnableMethod() == false)
                return invocation.proceed();
            /*----------------------------*/
            //1.获取权限数据
            Power powerAnno = invocation.getMethod().getAnnotation(Power.class);
            if (powerAnno == null)
                powerAnno = invocation.getMethod().getDeclaringClass().getAnnotation(Power.class);
            //2.测试权限
            boolean passPower = true;
            if (Level.PassLogin == powerAnno.level()) {
                passPower = this.doPassLogin(powerAnno, invocation.getMethod());
            } else if (Level.PassPolicy == powerAnno.level()) {
                passPower = this.doPassPolicy(powerAnno, invocation.getMethod());
            } else if (Level.Free == powerAnno.level()) {
                passPower = true;
            }
            //3.执行代码
            if (passPower)
                return invocation.proceed();
            String msg = "has no permission Level=" + powerAnno.level().name() + " Code : " + Platform.logString(powerAnno.value());
            throw new PermissionException(msg);
        }
        private boolean doPassLogin(Power powerAnno, Method method) {
            AuthSession[] authSessions = secService.getCurrentAuthSession();
            for (AuthSession authSession : authSessions)
                if (authSession.isLogin())
                    return true;
            return false;
        }
        private boolean doPassPolicy(Power powerAnno, Method method) {
            AuthSession[] authSessions = secService.getCurrentAuthSession();
            String[] powers = powerAnno.value();
            SecurityQuery query = secService.newSecurityQuery();
            for (String anno : powers)
                query.and(anno);
            return query.testPermission(authSessions);
        }
    }
    /*HttpSession动态监听*/
    private class SecuritySessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            if (settings.isEnable() == false)
                return;
            AuthSession[] authSessions = secService.getCurrentAuthSession();
            boolean needCreateSession = authSessions.length == 0;
            if (needCreateSession) {
                try {
                    AuthSession newAuthSession = secService.createAuthSession();
                    StringBuilder authSessionIDs = new StringBuilder("");
                    for (AuthSession authSession : authSessions)
                        authSessionIDs.append(authSession.getSessionID() + ",");
                    authSessionIDs.append(newAuthSession.getSessionID());
                    se.getSession().setAttribute(InternalSecurityProcess.HttpSessionAuthSessionSetName, authSessionIDs.toString());
                } catch (SecurityException e) {
                    Platform.error(Platform.logString(e));
                }
            }
        }
        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            if (settings.isEnable() == false)
                return;
            AuthSession[] authSessions = secService.getCurrentAuthSession();
            for (AuthSession authSession : authSessions)
                secService.inactivationAuthSession(authSession.getSessionID()); /*钝化AuthSession*/
        }
    }
}