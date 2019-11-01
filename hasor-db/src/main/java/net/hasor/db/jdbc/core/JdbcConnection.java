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
package net.hasor.db.jdbc.core;
import net.hasor.db.datasource.ConnectionProxy;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.transaction.TranManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 *
 * @version : 2013-10-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcConnection extends JdbcAccessor {
    private static Logger logger       = LoggerFactory.getLogger(JdbcConnection.class);
    /*JDBC查询和从结果集里面每次取设置行数，循环去取，直到取完。合理设置该参数可以避免内存异常。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 fetchSize 属性。*/
    private        int    fetchSize    = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 maxRows 属性。*/
    private        int    maxRows      = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 queryTimeout 属性。*/
    private        int    queryTimeout = 0;

    /**
     * Construct a new JdbcConnection for bean usage.
     * <p>Note: The DataSource has to be set before using the instance.
     * @see #setDataSource
     */
    public JdbcConnection() {
    }

    /**
     * Construct a new JdbcConnection, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public JdbcConnection(final DataSource dataSource) {
        this.setDataSource(dataSource);
    }

    /**
     * Construct a new JdbcConnection, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     */
    public JdbcConnection(final Connection conn) {
        this.setConnection(conn);
    }

    //
    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(final int maxRows) {
        this.maxRows = maxRows;
    }

    public int getQueryTimeout() {
        return this.queryTimeout;
    }

    public void setQueryTimeout(final int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public <T> T execute(final ConnectionCallback<T> action) throws SQLException {
        Objects.requireNonNull(action, "Callback object must not be null");
        //
        Connection localConn = this.getConnection();
        DataSource localDS = this.getDataSource();//获取数据源
        boolean usingDS = (localConn == null);
        if (logger.isDebugEnabled()) {
            logger.debug("database connection using DataSource = {}", usingDS);
        }
        //
        ConnectionProxy useConn = null;
        try {
            if (usingDS) {
                localConn = TranManager.currentConnection(localDS);
                useConn = this.newProxyConnection(localConn, localDS);//代理连接
            } else {
                useConn = this.newProxyConnection(localConn, null);//代理连接
            }
            return action.doInConnection(useConn);
        } finally {
            if (usingDS) {
                if (localConn != null) {
                    localConn.close();
                }
            }
        }
    }

    /**对Statement的属性进行设置。设置 JDBC Statement 对象的 fetchSize、maxRows、Timeout等参数。*/
    protected void applyStatementSettings(final Statement stmt) throws SQLException {
        int fetchSize = this.getFetchSize();
        if (fetchSize > 0) {
            stmt.setFetchSize(fetchSize);
        }
        int maxRows = this.getMaxRows();
        if (maxRows > 0) {
            stmt.setMaxRows(maxRows);
        }
        int timeout = this.getQueryTimeout();
        if (timeout > 0) {
            stmt.setQueryTimeout(timeout);
        }
    }

    /**获取与本地线程绑定的数据库连接，JDBC 框架会维护这个连接的事务。开发者不必关心该连接的事务管理，以及资源释放操作。*/
    private ConnectionProxy newProxyConnection(final Connection target, final DataSource targetSource) {
        Objects.requireNonNull(target, "Connection is null.");
        CloseSuppressingInvocationHandler handler = new CloseSuppressingInvocationHandler(target, targetSource);
        return (ConnectionProxy) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, handler);
    }

    /**Connection 接口代理，目的是为了控制一些方法的调用。同时进行一些特殊类型的处理。*/
    private class CloseSuppressingInvocationHandler implements InvocationHandler {
        private final Connection target;
        private final DataSource targetSource;

        public CloseSuppressingInvocationHandler(final Connection target, final DataSource targetSource) {
            this.target = target;
            this.targetSource = targetSource;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...
            if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            } else if (method.getName().equals("getTargetSource")) {
                // Handle getTargetConnection method: return underlying DataSource.
                return this.targetSource;
            } else if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return proxy == args[0];
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("close")) {
                return null;
            }
            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);
                // If return value is a JDBC Statement, apply statement settings (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement) {
                    JdbcConnection.this.applyStatementSettings((Statement) retVal);
                }
                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}