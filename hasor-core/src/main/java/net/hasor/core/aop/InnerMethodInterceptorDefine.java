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
package net.hasor.core.aop;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 *
 * @version : 2014年9月8日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerMethodInterceptorDefine implements MethodInterceptor, Predicate<Method> {
    private Predicate<Method> aopMatcher     = null;
    private MethodInterceptor aopInterceptor = null;

    public InnerMethodInterceptorDefine(Predicate<Method> aopMatcher, MethodInterceptor aopInterceptor) {
        this.aopMatcher = aopMatcher;
        this.aopInterceptor = aopInterceptor;
    }

    public boolean test(Method target) {
        return this.aopMatcher.test(target);
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        return this.aopInterceptor.invoke(invocation);
    }
}