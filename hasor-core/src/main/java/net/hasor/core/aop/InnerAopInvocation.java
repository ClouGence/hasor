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
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerAopInvocation implements MethodInvocation {
    private MethodInterceptor[] interceptorDefinitions;
    private MethodInvocation    proceedingChain;
    private int                 index = -1;
    //
    public InnerAopInvocation(String targetMethodDesc, final Method targetMethod, final Method proxyMethod, final Object targetObject, Object[] methodParams) {
        Class<?> targetClass = targetObject.getClass();
        ClassLoader loader = targetClass.getClassLoader();
        if (loader instanceof AopClassLoader) {
            AopClassConfig cc = ((AopClassLoader) loader).findClassConfig(targetClass.getName());
            this.interceptorDefinitions = cc.findInterceptor(targetMethodDesc);
        }
        if (this.interceptorDefinitions == null) {
            this.interceptorDefinitions = new MethodInterceptor[0];
        }
        //
        this.proceedingChain = new InnerChainMethodInvocation(proxyMethod, targetMethod, targetObject, methodParams);
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