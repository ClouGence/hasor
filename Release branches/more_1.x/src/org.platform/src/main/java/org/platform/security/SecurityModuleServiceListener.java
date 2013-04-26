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
import org.platform.security.internal.DefaultSecurityService;
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
        /*绑定核心功能实现类。*/
        event.getGuiceBinder().bind(SecuritySettings.class).toInstance(this.settings);//通过Guice
        event.getGuiceBinder().bind(SecurityContext.class).to(DefaultSecurityService.class);
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
                AuthSession authSession = secService.createAuthSession();
                if (settings.isGuestEnable() == true) {
                    authSession.doLoginGuest();/*登陆来宾帐号*/
                    authSession.setSupportCookieRecover(false);/*不恢复*/
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