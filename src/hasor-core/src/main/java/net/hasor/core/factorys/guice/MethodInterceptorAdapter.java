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
package net.hasor.core.factorys.guice;
import java.lang.reflect.Method;
import net.hasor.core.factorys.AopMatcherMethodInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * Guice Aop Adapter
 * Hasor Aop 到 Aop 联盟的桥
 * @version : 2014年9月3日
 * @author 赵永春(zyc@hasor.net)
 */
class MethodInterceptorAdapter implements MethodInterceptor {
    private AopMatcherMethodInterceptor aopInterceptor = null;
    public MethodInterceptorAdapter(final AopMatcherMethodInterceptor aopInterceptor) {
        this.aopInterceptor = aopInterceptor;
    }
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(new net.hasor.core.MethodInvocation() {
            public Object proceed() throws Throwable {
                return invocation.proceed();
            }
            public Object getThis() {
                return invocation.getThis();
            }
            public Method getMethod() {
                return invocation.getMethod();
            }
            public Object[] getArguments() {
                return invocation.getArguments();
            }
        });
    }
}