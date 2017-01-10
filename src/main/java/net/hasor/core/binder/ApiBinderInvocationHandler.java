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
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ApiBinderInvocationHandler implements InvocationHandler {
    private Map<Class<?>, Object> supportMap;
    //
    public ApiBinderInvocationHandler(Map<Class<?>, Object> supportMap) {
        this.supportMap = supportMap;
    }
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        //
        Class<?> declaringClass = method.getDeclaringClass();
        Object target = this.supportMap.get(declaringClass);
        if (target == null) {
            throw new UnsupportedOperationException("this method is not support -> " + method);
        }
        //
        if (method.getName().equals("installModule")) {
            if (args[0] != null)
                ((Module) args[0]).loadModule((ApiBinder) proxy);
            return null;
        }
        if (method.getName().equals("tryCast")) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length == 1 && types[0] == Class.class) {
                Class<?> castApiBinder = (Class<?>) args[0];
                if (castApiBinder == null) {
                    return null;
                }
                if (!castApiBinder.isInstance(proxy)) {
                    return null;
                }
                return proxy;
            }
        }
        //
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}