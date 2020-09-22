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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class ApiBinderInvocationHandler implements InvocationHandler {
    private static Logger                logger = LoggerFactory.getLogger(ApiBinderInvocationHandler.class);
    private final  Map<Class<?>, Object> supportMap;

    protected Map<Class<?>, Object> supportMap() {
        return Collections.unmodifiableMap(supportMap);
    }

    public ApiBinderInvocationHandler(Map<Class<?>, Object> supportMap) {
        this.supportMap = supportMap;
        for (Map.Entry<Class<?>, Object> entry : supportMap.entrySet()) {
            if (entry.getValue() == null) {
                throw new UnsupportedOperationException("this method is not support -> " + entry.getKey());
            }
        }
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return proxyToString();
        }
        //
        Class<?> declaringClass = method.getDeclaringClass();
        Object target = this.supportMap.get(declaringClass);
        //
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    private Object proxyToString() {
        StringBuilder builder = new StringBuilder();
        builder = builder.append("count = ").append(this.supportMap.size()).append(" - [");
        for (Class<?> face : this.supportMap.keySet()) {
            builder = builder.append(face.getName()).append(",");
        }
        if (builder.charAt(builder.length() - 1) == ',') {
            builder = builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }
}
