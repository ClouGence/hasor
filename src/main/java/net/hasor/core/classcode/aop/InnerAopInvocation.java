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
package net.hasor.core.classcode.aop;
import net.hasor.core.classcode.AbstractClassConfig;
import net.hasor.core.classcode.MoreClassLoader;

import java.lang.reflect.Method;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerAopInvocation implements AopInvocation {
    private AopInterceptor[] interceptorDefinitions;
    private AopInvocation    proceedingChain;
    private int index = -1;
    //
    public InnerAopInvocation(String targetMethodDesc, final Method targetMethod, final AopInvocation proceedingChain) {
        Class<?> targetClass = targetMethod.getDeclaringClass();
        ClassLoader loader = targetClass.getClassLoader();
        if (loader instanceof MoreClassLoader) {
            String className = targetMethod.getDeclaringClass().getName();
            AbstractClassConfig cc = ((MoreClassLoader) loader).findClassConfig(className);
            if (cc != null && cc instanceof AopClassConfig) {
                AopClassConfig aopCC = (AopClassConfig) cc;
                this.interceptorDefinitions = aopCC.findInterceptor(targetMethodDesc);
            }
        }
        if (this.interceptorDefinitions == null) {
            this.interceptorDefinitions = new AopInterceptor[0];
        }
        this.proceedingChain = proceedingChain;
    }
    public Method getMethod() {
        return this.proceedingChain.getMethod();
    }
    public Object[] getArguments() {
        return this.proceedingChain.getArguments();
    }
    public Object proceed() throws Throwable {
        this.index++;
        if (this.index < this.interceptorDefinitions.length) {
            return this.interceptorDefinitions[this.index].invoke(this);
        } else {
            return this.proceedingChain.proceed();
        }
    }
    public Object getThis() {
        return this.proceedingChain.getThis();
    }
}