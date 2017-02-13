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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 *
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerChainAopInvocation implements AopInvocation {
    private Method     targetMethod = null;
    private Object     targetObject = null;
    private Object[]   paramObjects = null;
    private Class<?>[] paramTypes   = null;
    //
    public InnerChainAopInvocation(Method targetMethod, Object targetObject, Object[] paramObjects) {
        this.targetMethod = targetMethod;
        this.targetObject = targetObject;
        this.paramObjects = paramObjects;
        this.paramTypes = targetMethod.getParameterTypes();
    }
    // 
    public Method getMethod() {
        Class<?> superType = this.targetMethod.getDeclaringClass().getSuperclass();
        try {
            try {
                return superType.getDeclaredMethod(this.targetMethod.getName(), this.paramTypes);
            } catch (Throwable e) {
                return superType.getMethod(this.targetMethod.getName(), this.paramTypes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Object[] getArguments() {
        return this.paramObjects;
    }
    public Object proceed() throws Throwable {
        Class<?> targetClass = this.targetMethod.getDeclaringClass();
        Method invokeMethod = targetClass.getDeclaredMethod(AopClassAdapter.AopPrefix + this.targetMethod.getName(), this.paramTypes);
        invokeMethod.setAccessible(true);
        try {
            return invokeMethod.invoke(this.targetObject, this.paramObjects);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    public Object getThis() {
        return this.targetObject;
    }
}