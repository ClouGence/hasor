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
package org.platform.jdbc;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.platform.Assert;
import org.platform.Platform;
import org.platform.jdbc.datasource.ConnectionProxy;
import org.platform.jdbc.datasource.DataSourceUtils;
/**
* 
* @version : 2013-5-7
* @author ’‘”¿¥∫ (zyc@byshell.org)
*/
public class JdbcTemplate extends JdbcAccessor implements JdbcOperations {
    private boolean ignoreWarnings = true;
    private int     fetchSize      = 0;
    private int     maxRows        = 0;
    private int     queryTimeout   = 0;
    //
    public JdbcTemplate() {}
    public JdbcTemplate(DataSource dataSource) {
        setDataSource(dataSource);
    }
    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }
    public boolean isIgnoreWarnings() {
        return this.ignoreWarnings;
    }
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
    public int getFetchSize() {
        return this.fetchSize;
    }
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    public int getMaxRows() {
        return this.maxRows;
    }
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
    public int getQueryTimeout() {
        return this.queryTimeout;
    }
    //-------------------------------------------------------------------------
    // Methods dealing with a plain Transaction
    //------------------------------------------------------------------------- 
    @Override
    public boolean hasTransaction() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void commitTransaction() {
        // TODO Auto-generated method stub
    }
    @Override
    public void rollbackTransaction() {
        // TODO Auto-generated method stub
    }
    @Override
    public void beginTransaction() {
        // TODO Auto-generated method stub
    }
    //-------------------------------------------------------------------------
    // Methods dealing with a plain SQL
    //------------------------------------------------------------------------- 
    @Override
    public <T> T execute(ConnectionCallback<T> action) throws SQLException {
        Assert.isNotNull(action, "Callback object must not be null.");
        Connection con = DataSourceUtils.getConnection(getDataSource());
        try {
            con = createConnectionProxy(con);
            return action.doConnection(con);
        } catch (SQLException ex) {
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            Platform.error("ConnectionCallback SQLQuery:%s%s", getSql(action), ex);
            throw ex;
        } finally {
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
    @Override
    public <T> T execute(StatementCallback<T> action) throws SQLException {
        Assert.isNotNull(action, "Callback object must not be null.");
        Connection con = DataSourceUtils.getConnection(getDataSource());
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            applyStatementSettings(stmt);
            T result = action.doStatement(stmt);
            handleWarnings(stmt);
            return result;
        } catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock in the case when the exception translator hasn't been initialized yet.
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            Platform.error("StatementCallback SQLQuery:%s%s", getSql(action), ex);
            throw ex;
        } finally {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
    @Override
    public <T> T execute(String callString, PreparedStatementCallback<T> action) throws SQLException {
        Assert.isNotNull(action, "Callback object must not be null.");
        Connection con = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(callString);
            applyStatementSettings(stmt);
            T result = action.doPreparedStatement(stmt);
            handleWarnings(stmt);
            return result;
        } catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock in the case when the exception translator hasn't been initialized yet.
            JdbcUtils.closeStatement(stmt);
            stmt = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            Platform.error("PreparedStatementCallback SQLQuery:%s%s", getSql(action), ex);
            throw ex;
        } finally {
            JdbcUtils.closeStatement(stmt);
            DataSourceUtils.releaseConnection(con, getDataSource());
        }
    }
    @Override
    public <T> T execute(final String callString, CallableStatementCallback<T> action) throws SQLException {
        Platform.debug("Executing SQL statement [" + callString + "].");
        class ExecuteStatementCallback implements StatementCallback<T>, SqlProvider {
            public T doStatement(Statement stmt) throws SQLException {
                stmt.execute(callString);
                return null;
            }
            public String getSql() {
                return callString;
            }
        }
        return execute(new ExecuteStatementCallback());
    }
    @Override
    public void execute(String callString) throws SQLException {
        this.execute(callString, new Object[0]);
    }
    @Override
    public void execute(final String callString, Object... args) throws SQLException {
        Platform.debug("Executing SQL statement [" + callString + "].");
        class ExecutePreparedStatementCallback implements PreparedStatementCallback<Object>, SqlProvider {
            public Object doPreparedStatement(PreparedStatement stmt) throws SQLException {
                stmt.setObject(parameterIndex, x, targetSqlType);
                return null;
            }
            public String getSql() {
                return callString;
            }
        }
        execute(callString, new ExecutePreparedStatementCallback());
    }
    @Override
    public List<Map<String, Object>> queryForList(String sqlQuery) throws SQLException {
        return this.queryForList(sqlQuery, new Object[0]);
    }
    @Override
    public List<Map<String, Object>> queryForList(String sqlQuery, Object... args) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Map<String, Object> queryForMap(String sqlQuery) throws SQLException {
        return this.queryForMap(sqlQuery, new Object[0]);
    }
    @Override
    public Map<String, Object> queryForMap(String sqlQuery, Object... args) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int update(String sqlQuery) throws SQLException {
        return this.update(sqlQuery, new Object[0]);
    }
    @Override
    public int update(String sqlQuery, Object... args) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public int[] batchUpdate(String[] sqlQuerys) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int[] batchUpdate(String sqlQuerys, List<Object[]> args) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int[] batchUpdate(String sqlQuerys, List<Object[]> args, List<int[]> argTypes) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String queryForString(String sqlQuery) throws SQLException {
        return this.queryForString(sqlQuery, new Object[0]);
    }
    @Override
    public String queryForString(String sqlQuery, Object... args) throws SQLException {
        return queryForObject(sqlQuery, String.class, args);
    }
    @Override
    public int queryForInt(String sqlQuery) throws SQLException {
        return this.queryForInt(sqlQuery, new Object[0]);
    }
    @Override
    public int queryForInt(String sqlQuery, Object... args) throws SQLException {
        Number number = queryForObject(sqlQuery, Integer.class, args);
        return (number != null ? number.intValue() : 0);
    }
    @Override
    public long queryForLong(String sqlQuery) throws SQLException {
        return this.queryForLong(sqlQuery, new Object[0]);
    }
    @Override
    public long queryForLong(String sqlQuery, Object... args) throws SQLException {
        Number number = queryForObject(sqlQuery, Long.class, args);
        return (number != null ? number.longValue() : 0);
    }
    @Override
    public <T> T queryForObject(String sqlQuery, Class<T> requiredType) throws SQLException {
        return this.queryForObject(sqlQuery, requiredType, new Object[0]);
    }
    @Override
    public <T> T queryForObject(String sqlQuery, Class<T> requiredType, Object... args) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }
    //-------------------------------------------------------------------------
    // Methods dealing with a plain extends
    //------------------------------------------------------------------------- 
    protected void applyStatementSettings(Statement statement) throws SQLException {
        int fetchSize = getFetchSize();
        if (fetchSize > 0)
            statement.setFetchSize(fetchSize);
        int maxRows = getMaxRows();
        if (maxRows > 0)
            statement.setMaxRows(maxRows);
        DataSourceUtils.applyTimeout(statement, getDataSource(), getQueryTimeout());
    }
    //-------------------------------------------------------------------------
    // Methods dealing with a plain internal
    //------------------------------------------------------------------------- 
    private Connection createConnectionProxy(Connection con) {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, new CloseSuppressingInvocationHandler(con));
    }
    /** */
    private void handleWarnings(Statement stmt) throws SQLException {
        if (isIgnoreWarnings() == true) {
            SQLWarning warningToLog = stmt.getWarnings();
            while (warningToLog != null) {
                Platform.debug("SQLWarning ignored: SQL state '%s', error code '%s', message [%s]", warningToLog.getSQLState(), warningToLog.getErrorCode(), warningToLog.getMessage());
                warningToLog = warningToLog.getNextWarning();
            }
        } else {
            handleWarnings(stmt.getWarnings());
        }
    }
    /** */
    private void handleWarnings(SQLWarning warning) throws SQLWarningException {
        if (warning != null)
            throw new SQLWarningException("Warning not ignored", warning);
    }
    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }
    /** */
    private class CloseSuppressingInvocationHandler implements InvocationHandler {
        private final Connection target;
        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...
            if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            } else if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
            } else if (method.getName().equals("hashCode")) {
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            } else if (method.getName().equals("close")) {
                // Handle close method: suppress, not valid.
                return null;
            }
            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);
                // If return value is a JDBC Statement, apply statement settings (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement) {
                    applyStatementSettings(((Statement) retVal));
                }
                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}