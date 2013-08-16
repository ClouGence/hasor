/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.hasor.context.anno.support;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hasor.Hasor;
import org.hasor.context.ApiBinder;
import org.hasor.context.AppContext;
import org.hasor.context.anno.Before;
import com.google.inject.matcher.AbstractMatcher;
/**
 * 支持Bean注解功能。
 * @version : 2013-4-8
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeforeSupport {
    private GetContext getContext = null;
    //
    public BeforeSupport(ApiBinder apiBinder, GetContext getContext) {
        if (apiBinder.getInitContext().getSettings().getBoolean("hasor.annotation") == false) {
            Hasor.warning("init Annotation false!");
            return;
        }
        this.getContext = getContext;
        apiBinder.getGuiceBinder().bindInterceptor(new BeforeMatcher(), new BeforeMatcher(), new BeforeInterceptor());
    }
    /*-------------------------------------------------------------------------------------*/
    /*拦截器*/
    private class BeforeInterceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            List<Class<? extends MethodInterceptor>> list = new ArrayList<Class<? extends MethodInterceptor>>();
            Method targetMethod = invocation.getMethod();
            //
            Before beforeAnno = targetMethod.getAnnotation(Before.class);
            if (beforeAnno != null) {
                for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                    if (interType != null)
                        list.add(interType);
            }
            beforeAnno = targetMethod.getDeclaringClass().getAnnotation(Before.class);
            if (beforeAnno != null) {
                for (Class<? extends MethodInterceptor> interType : beforeAnno.value())
                    if (interType != null)
                        list.add(interType);
            }
            //2.获取拦截器
            AppContext appContext = getContext.getAppContext();
            return new BeforeChainInvocation(appContext, list, invocation).invoke(invocation);
        }
    }
    /*负责检测匹配。规则：只要类型或方法上标记了@Before。*/
    private class BeforeMatcher extends AbstractMatcher<Object> {
        @Override
        public boolean matches(Object type) {
            if (type instanceof Class<?>)
                return this.matches((Class<?>) type);
            if (type instanceof Method)
                return this.matches((Method) type);
            return false;
        }
        public boolean matches(Class<?> matcherType) {
            if (matcherType.isAnnotationPresent(Before.class) == true)
                return true;
            Method[] m1s = matcherType.getMethods();
            Method[] m2s = matcherType.getDeclaredMethods();
            for (Method m1 : m1s) {
                if (m1.isAnnotationPresent(Before.class) == true)
                    return true;
            }
            for (Method m2 : m2s) {
                if (m2.isAnnotationPresent(Before.class) == true)
                    return true;
            }
            return false;
        }
        public boolean matches(Method matcherType) {
            if (matcherType.isAnnotationPresent(Before.class) == true)
                return true;
            if (matcherType.getDeclaringClass().isAnnotationPresent(Before.class) == true)
                return true;
            return false;
        }
    }
}