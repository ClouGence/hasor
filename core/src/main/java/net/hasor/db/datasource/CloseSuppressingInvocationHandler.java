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
package net.hasor.db.datasource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
/**
 * Connection 接口代理，目的是为了控制一些方法的调用。同时进行一些特殊类型的处理。
 * @version : 2013-10-30
 * @author 赵永春(zyc@hasor.net)
 */
class CloseSuppressingInvocationHandler implements InvocationHandler {
    private final ConnectionHolder holder;
    private       Connection       connection;
    public CloseSuppressingInvocationHandler(ConnectionHolder holder) {
        this.holder = holder;
        this.holder.requested();//ref++
    }
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        this.connection = holder.getConnection();
        //
        if (method.getName().equals("getTargetConnection"))
            return this.connection;
        else if (method.getName().equals("getTargetSource"))
            return this.holder.getDataSource();
        else if (method.getName().equals("equals"))
            return proxy == args[0];
        else if (method.getName().equals("hashCode"))
            return System.identityHashCode(proxy);
        else if (method.getName().equals("close")) {
            if (holder.isOpen()) {
                holder.released();//ref--
            }
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