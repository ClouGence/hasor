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
import org.platform.security.Power.Level;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 支持Service等注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@byshell.org)
 */
@InitListener(displayName = "SecurityModuleServiceListener", description = "org.platform.security软件包功能支持。", startIndex = 0)
public class SecurityModuleServiceListener extends AbstractModuleListener {
    private SecurityService         secService  = null;
    private SecuritySessionListener secListener = null;
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
        /**/
        event.getGuiceBinder().bind(SecurityService.class);
    }
    @Override
    public void initialized(AppContext appContext) {
        this.secService = appContext.getBean(SecurityService.class);
    }
    /*-------------------------------------------------------------------------------------*/
    /*负责检测类是否匹配。规则：只要类型或方法上标记了@Power。*/
    private class ClassPowerMatcher extends AbstractMatcher<Class<?>> {
        @Override
        public boolean matches(Class<?> matcherType) {
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
            return secService.newTest().and(powerAnno).test(authSession);
        }
    }
    /*HttpSession动态监听*/
    private class SecuritySessionListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            secService.getAuthSession(se.getSession(), true);
        }
        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            AuthSession authSession = secService.getAuthSession(se.getSession(), true);
            authSession.close();
        }
    }
}