/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.metadata;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Connection 接口代理，目的是拦截 close 方法，使其失效。
 * @version : 2021-03-26
 * @author 赵永春 (zyc@hasor.net)
 */
class CloseIsNothingInvocationHandler implements InvocationHandler {
    private final Connection connection;

    CloseIsNothingInvocationHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (method.getName().equals("getTargetConnection")) {
            return connection;
        } else if (method.getName().equals("toString")) {
            return this.connection.toString();
        } else if (method.getName().equals("equals")) {
            return proxy == args[0];
        } else if (method.getName().equals("hashCode")) {
            return System.identityHashCode(proxy);
        } else if (method.getName().equals("close")) {
            return null;
        }
        //
        try {
            return method.invoke(this.connection, args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
