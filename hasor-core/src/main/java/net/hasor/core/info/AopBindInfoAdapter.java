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
package net.hasor.core.info;
import net.hasor.core.AppContext;
import net.hasor.core.spi.AppContextAware;
import net.hasor.core.MethodInterceptor;
import net.hasor.core.MethodInvocation;

import java.lang.reflect.Method;
import java.util.function.Predicate;
/**
 *
 * @version : 2014年5月22日
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopBindInfoAdapter implements MethodInterceptor, AppContextAware {
    private Predicate<Class<?>> matcherClass  = null;
    private Predicate<Method>   matcherMethod = null;
    private MethodInterceptor   interceptor   = null;
    //
    public AopBindInfoAdapter(final Predicate<Class<?>> matcherClass, final Predicate<Method> matcherMethod, final MethodInterceptor interceptor) {
        this.matcherClass = matcherClass;
        this.matcherMethod = matcherMethod;
        this.interceptor = interceptor;
    }
    //
    public Predicate<Class<?>> getMatcherClass() {
        return matcherClass;
    }
    public Predicate<Method> getMatcherMethod() {
        return matcherMethod;
    }
    //
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        return this.interceptor.invoke(invocation);
    }
    public void setAppContext(AppContext appContext) {
        if (this.interceptor instanceof AppContextAware) {
            ((AppContextAware) this.interceptor).setAppContext(appContext);
        }
    }
}