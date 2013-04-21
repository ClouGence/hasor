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
import static org.platform.PlatformConfigEnum.Security_Enable;
import static org.platform.PlatformConfigEnum.Security_EnableMethod;
import java.lang.reflect.Method;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.more.global.Global;
import org.platform.Platform;
import org.platform.binder.ApiBinder;
import org.platform.context.AbstractModuleListener;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.security.Power.Level;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 支持Service等注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
//@InitListener(displayName = "SecurityModuleServiceListener", description = "org.platform.security软件包功能支持。", startIndex = 1)
public class SecurityModuleServiceListener extends AbstractModuleListener {
    private boolean                 enable       = false; //启用禁用
    private boolean                 enableMethod = true; //方法权限检查
    private SecurityContext         secService   = null;
    private SecuritySessionListener secListener  = null;
    /**初始化.*/
    @Override
    public void initialize(ApiBinder event) {
        /*HttpSession创建和销毁通知机制*/
        this.secListener = new SecuritySessionListener();
        event.sessionListener().bind(this.secListener);
        /*request，请求拦截器*/
        event.filter("*").through(SecurityFilter.class);
        /*aop，方法执行权限支持*/
        event.getGuiceBinder().bindInterceptor(new ClassPowerMatcher(), new MethodPowerMatcher(), new SecurityInterceptor());/*注册Aop*/
        /*配置文件监听器*/
        SettingListener listener = new SettingListener() {
            @Override
            public void reLoadConfig(Global oldConfig, Global newConfig) {
                enable = newConfig.getBoolean(Security_Enable, false);
                enableMethod = newConfig.getBoolean(Security_EnableMethod, false);
                if (enable == false)
                    enableMethod = false;
            }
        };
        event.getInitContext().getConfig().addSettingsListener(listener);
        listener.reLoadConfig(null, event.getInitContext().getConfig().getSettings());
        /*绑定核心功能实现类。*/
        event.getGuiceBinder().bind(SecurityContext.class).to(DefaultSecurityService.class);
        event.getGuiceBinder().bind(SecurityQuery.class).to(DefaultSecurityQuery.class);
    }
    @Override
    public void initialized(AppContext appContext) {
        this.secService = appContext.getBean(SecurityContext.class);
    }
    /*-------------------------------------------------------------------------------------*/
    /*负责检测类是否匹配。规则：只要类型或方法上标记了@Power。*/
    private class ClassPowerMatcher extends AbstractMatcher<Class<?>> {
        @Override
        public boolean matches(Class<?> matcherType) {
            /*如果处于禁用状态则忽略权限检测*/
            if (enableMethod == false)
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
            if (enableMethod == false)
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
            if (enableMethod == false)
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
            AuthSession authSession = secService.getCurrentAuthSession();
            return authSession.isLogin();
        }
        private boolean doPassPolicy(Power powerAnno, Method method) {
            AuthSession authSession = secService.getCurrentAuthSession();
            String[] powers = powerAnno.value();
            SecurityQuery query = secService.newSecurityQuery();
            for (String anno : powers)
                query.and(anno);
            return query.testPermission(authSession);
        }
    }
    /*HttpSession动态监听*/
    private class SecuritySessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            if (enable == false)
                return;
            secService.getAuthSession(se.getSession(), true);
        }
        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            if (enable == false)
                return;
            AuthSession authSession = secService.getAuthSession(se.getSession(), true);
            authSession.close();
        }
    }
}