/*
 * Copyright 2002-2006 the original author or authors.
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
import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
/**
 *
 * @version : 2013-10-30
 * @author 赵永春 (zyc@hasor.net)
 */
public class DataSourceManager {
    /**申请连接*/
    public static Connection genConnection(DataSource dataSource) {
        ConnectionHolder holder = genConnectionHolder(dataSource);
        if (holder == null) {
            throw new NullPointerException("genConnectionHolder. return null.");
        }
        return newProxyConnection(holder);
    }
    /**申请{@link ConnectionHolder}*/
    public static ConnectionHolder genConnectionHolder(DataSource dataSource) {
        return new ConnectionHolder(dataSource);
    }
    //
    /**获取与本地线程绑定的数据库连接，JDBC 框架会维护这个连接的事务。开发者不必关心该连接的事务管理，以及资源释放操作。*/
    protected static ConnectionProxy newProxyConnection(ConnectionHolder holder) {
        CloseSuppressingInvocationHandler handler = new CloseSuppressingInvocationHandler(holder);
        return (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, handler);
    }
}