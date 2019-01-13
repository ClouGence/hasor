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
import net.hasor.core.MethodInvocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerChainMethodInvocation implements MethodInvocation {
    private Method   proxyMethod  = null;
    private Method   targetMethod = null;
    private Object   targetObject = null;
    private Object[] paramObjects = null;
    //
    InnerChainMethodInvocation(Method proxyMethod, Method targetMethod, Object targetObject, Object[] paramObjects) {
        this.proxyMethod = proxyMethod;
        this.targetMethod = targetMethod;
        this.targetObject = targetObject;
        this.paramObjects = paramObjects;
    }
    //
    public Method getMethod() {
        return this.targetMethod;
    }
    public Object[] getArguments() {
        return this.paramObjects;
    }
    public Object proceed() throws Throwable {
        try {
            return proxyMethod.invoke(this.targetObject, this.paramObjects);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    public Object getThis() {
        return this.targetObject;
    }
}