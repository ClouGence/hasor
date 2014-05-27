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
package net.hasor.core.binder;
import java.lang.reflect.Method;
import net.hasor.core.ApiBinder.Matcher;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
/**
 * 
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
class AopMatcherRegisterData implements AopMatcherRegister {
    private Matcher<Class<?>> matcherClass  = null;
    private Matcher<Method>   matcherMethod = null;
    private MethodInterceptor interceptor   = null;
    //
    public AopMatcherRegisterData(Matcher<Class<?>> matcherClass, Matcher<Method> matcherMethod, MethodInterceptor interceptor) {
        this.matcherClass = matcherClass;
        this.matcherMethod = matcherMethod;
        this.interceptor = interceptor;
    }
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return this.interceptor.invoke(invocation);
    }
    public boolean matcher(Class<?> targetClass) {
        return this.matcherClass.matches(targetClass);
    }
    public boolean matcher(Method targetMethod) {
        return this.matcherMethod.matches(targetMethod);
    }
}