/*
 * Copyright 2002-2010 the original author or authors.
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
package net.hasor.jdbc.jdbc.core;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.Hasor;
import net.hasor.jdbc.dao.DataAccessException;
import net.hasor.jdbc.dao.InvalidDataAccessException;
import net.hasor.jdbc.dao.SQLWarningException;
import net.hasor.jdbc.datasource.ConnectionProxy;
import net.hasor.jdbc.jdbc.BatchPreparedStatementSetter;
import net.hasor.jdbc.jdbc.CallableStatementCallback;
import net.hasor.jdbc.jdbc.CallableStatementCreator;
import net.hasor.jdbc.jdbc.ConnectionCallback;
import net.hasor.jdbc.jdbc.JdbcOperations;
import net.hasor.jdbc.jdbc.PreparedStatementCallback;
import net.hasor.jdbc.jdbc.PreparedStatementCreator;
import net.hasor.jdbc.jdbc.PreparedStatementSetter;
import net.hasor.jdbc.jdbc.ResultSetExtractor;
import net.hasor.jdbc.jdbc.RowCallbackHandler;
import net.hasor.jdbc.jdbc.RowMapper;
import net.hasor.jdbc.jdbc.SqlRowSet;
import net.hasor.jdbc.jdbc.StatementCallback;
import net.hasor.jdbc.jdbc.core._.ColumnMapRowMapper;
import net.hasor.jdbc.jdbc.core.util.LinkedCaseInsensitiveMap;
import net.hasor.jdbc.jdbc.parameter.ResultSetSupportingSqlParameter;
import net.hasor.jdbc.jdbc.parameter.SqlOutParameter;
import net.hasor.jdbc.jdbc.parameter.SqlOutUpdateCountParameter;
import net.hasor.jdbc.jdbc.parameter.SqlInputParameter;
import net.hasor.jdbc.jdbc.parameter.SqlReturnResultSet;
import org.more.util.ArrayUtils;
/**
 * 
 * @version : 2013-10-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class JdbcTemplate2 implements JdbcOperations {
    /*是否忽略出现的 SQL 警告*/
    private boolean ignoreWarnings            = true;
    /*JDBC查询和从结果集里面每次取设置行数，循环去取，直到取完。合理设置该参数可以避免内存异常。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 fetchSize 属性。*/
    private int     fetchSize                 = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 maxRows 属性。*/
    private int     maxRows                   = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 queryTimeout 属性。*/
    private int     queryTimeout              = 0;
    /*当JDBC 结果集中如出现相同的列名仅仅大小写不同时。是否保留大小写列名敏感。
     * 如果为 true 表示敏感，并且结果集Map中保留两个记录。如果为 false 则表示不敏感，如出现冲突列名后者将会覆盖前者。*/
    private boolean resultsMapCaseInsensitive = false;
    //
    /**
     * If this variable is set to true then all results checking will be bypassed for any
     * callable statement processing.  This can be used to avoid a bug in some older Oracle
     * JDBC drivers like 10.1.0.2.
     */
    private boolean skipResultsProcessing     = false;
    /**
     * If this variable is set to true then all results from a stored procedure call
     * that don't have a corresponding SqlOutParameter declaration will be bypassed.
     * All other results processng will be take place unless the variable 
     * <code>skipResultsProcessing</code> is set to <code>true</code> 
     */
    private boolean skipUndeclaredResults     = false;
    //
    //
    //
    //
    public boolean isIgnoreWarnings() {
        return ignoreWarnings;
    }
    public void setIgnoreWarnings(boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }
    public int getFetchSize() {
        return fetchSize;
    }
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
    public int getMaxRows() {
        return maxRows;
    }
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    public int getQueryTimeout() {
        return queryTimeout;
    }
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }
    public boolean isResultsMapCaseInsensitive() {
        return resultsMapCaseInsensitive;
    }
    public void setResultsMapCaseInsensitive(boolean resultsMapCaseInsensitive) {
        this.resultsMapCaseInsensitive = resultsMapCaseInsensitive;
    }
    //    /**
    //     * Construct a new JdbcTemplate for bean usage.
    //     * <p>Note: The DataSource has to be set before using the instance.
    //     * @see #setDataSource
    //     */
    //    public JdbcTemplate2() {}
    //    /**
    //     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
    //     * <p>Note: This will not trigger initialization of the exception translator.
    //     * @param dataSource the JDBC DataSource to obtain connections from
    //     */
    //    public JdbcTemplate2(DataSource dataSource) {
    //        setDataSource(dataSource);
    //        afterPropertiesSet();
    //    }
    //
    //
    //
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        //        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //        Connection con = DataSourceUtils.getConnection(getDataSource());
        //        try {
        //            Connection conToUse = con;
        //            if (this.nativeJdbcExtractor != null) {
        //                // Extract native JDBC Connection, castable to OracleConnection or the like.
        //                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
        //            } else {
        //                // Create close-suppressing Connection proxy, also preparing returned Statements.
        //                conToUse = createConnectionProxy(con);
        //            }
        //            return action.doInConnection(conToUse);
        //        } catch (SQLException ex) {
        //            // Release Connection early, to avoid potential connection pool deadlock
        //            // in the case when the exception translator hasn't been initialized yet.
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //            con = null;
        //            throw getExceptionTranslator().translate("ConnectionCallback", getSql(action), ex);
        //        } finally {
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //        }
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        //        Assert.notNull(action, "Callback object must not be null");
        //        Connection con = DataSourceUtils.getConnection(getDataSource());
        //        Statement stmt = null;
        //        try {
        //            Connection conToUse = con;
        //            if (this.nativeJdbcExtractor != null && this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativeStatements()) {
        //                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
        //            }
        //            stmt = conToUse.createStatement();
        //            applyStatementSettings(stmt);
        //            Statement stmtToUse = stmt;
        //            if (this.nativeJdbcExtractor != null) {
        //                stmtToUse = this.nativeJdbcExtractor.getNativeStatement(stmt);
        //            }
        //            T result = action.doInStatement(stmtToUse);
        //            handleWarnings(stmt);
        //            return result;
        //        } catch (SQLException ex) {
        //            // Release Connection early, to avoid potential connection pool deadlock
        //            // in the case when the exception translator hasn't been initialized yet.
        //            JdbcUtils.closeStatement(stmt);
        //            stmt = null;
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //            con = null;
        //            throw getExceptionTranslator().translate("StatementCallback", getSql(action), ex);
        //        } finally {
        //            JdbcUtils.closeStatement(stmt);
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //        }
        // TODO Auto-generated method stub
        return null;
    }
    public void execute(final String sql) throws DataAccessException {
        Hasor.debug("Executing SQL statement [%s].", sql);
        class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
            public Object doInStatement(Statement stmt) throws SQLException {
                stmt.execute(sql);
                return null;
            }
            public String getSql() {
                return sql;
            }
        }
        this.execute(new ExecuteStatementCallback());
    }
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws DataAccessException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.debug("Executing SQL query [%s].", sql);
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            public T doInStatement(Statement stmt) throws SQLException {
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                }
            }
            public String getSql() {
                return sql;
            }
        }
        return execute(new QueryStatementCallback());
    }
    public void query(String sql, RowCallbackHandler rch) throws DataAccessException {
        query(sql, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        return query(sql, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public Map<String, Object> queryForMap(String sql) throws DataAccessException {
        return queryForObject(sql, getColumnMapRowMapper());
    }
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, rowMapper);
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, getSingleColumnRowMapper(requiredType));
    }
    public long queryForLong(String sql) throws DataAccessException {
        Number number = queryForObject(sql, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public int queryForInt(String sql) throws DataAccessException {
        Number number = queryForObject(sql, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws DataAccessException {
        return query(sql, getSingleColumnRowMapper(elementType));
    }
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        return query(sql, getColumnMapRowMapper());
    }
    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
        return query(sql, new SqlRowSetResultSetExtractor());
    }
    public int update(final String sql) throws DataAccessException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.debug("Executing SQL update [%s]", sql);
        //
        class UpdateStatementCallback implements StatementCallback<Integer>, SqlProvider {
            public Integer doInStatement(Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                Hasor.debug("SQL update affected %s rows.", rows);
                return rows;
            }
            public String getSql() {
                return sql;
            }
        }
        return execute(new UpdateStatementCallback());
    }
    public int[] batchUpdate(final String[] sql) throws DataAccessException {
        if (ArrayUtils.isEmpty(sql))
            throw new NullPointerException(sql + "SQL array must not be empty");
        Hasor.debug("Executing SQL batch update of %s statements", sql.length);
        //
        class BatchUpdateStatementCallback implements StatementCallback<int[]>, SqlProvider {
            private String currSql;
            public int[] doInStatement(Statement stmt) throws SQLException, DataAccessException {
                DatabaseMetaData dbmd = stmt.getConnection().getMetaData();
                int[] rowsAffected = new int[sql.length];
                if (dbmd.supportsBatchUpdates()) {
                    /*连接支持批处理*/
                    for (String sqlStmt : sql) {
                        this.currSql = sqlStmt;
                        stmt.addBatch(sqlStmt);
                    }
                    rowsAffected = stmt.executeBatch();
                } else {
                    /*连接不支持批处理*/
                    for (int i = 0; i < sql.length; i++) {
                        this.currSql = sql[i];
                        if (!stmt.execute(sql[i]))
                            rowsAffected[i] = stmt.getUpdateCount();
                        else
                            throw new InvalidDataAccessException("Invalid batch SQL statement: " + sql[i]);
                    }
                }
                return rowsAffected;
            }
            public String getSql() {
                return this.currSql;
            }
        }
        return execute(new BatchUpdateStatementCallback());
    }
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws DataAccessException {
        //        Hasor.assertIsNotNull(psc, "PreparedStatementCreator must not be null");
        //        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //        if (logger.isDebugEnabled()) {
        //            String sql = getSql(psc);
        //            Hasor.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        //        }
        //        Connection con = DataSourceUtils.getConnection(getDataSource());
        //        PreparedStatement ps = null;
        //        try {
        //            Connection conToUse = con;
        //            if (this.nativeJdbcExtractor != null && this.nativeJdbcExtractor.isNativeConnectionNecessaryForNativePreparedStatements()) {
        //                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
        //            }
        //            ps = psc.createPreparedStatement(conToUse);
        //            applyStatementSettings(ps);
        //            PreparedStatement psToUse = ps;
        //            if (this.nativeJdbcExtractor != null) {
        //                psToUse = this.nativeJdbcExtractor.getNativePreparedStatement(ps);
        //            }
        //            T result = action.doInPreparedStatement(psToUse);
        //            handleWarnings(ps);
        //            return result;
        //        } catch (SQLException ex) {
        //            // Release Connection early, to avoid potential connection pool deadlock
        //            // in the case when the exception translator hasn't been initialized yet.
        //            if (psc instanceof ParameterDisposer) {
        //                ((ParameterDisposer) psc).cleanupParameters();
        //            }
        //            String sql = getSql(psc);
        //            psc = null;
        //            JdbcUtils.closeStatement(ps);
        //            ps = null;
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //            con = null;
        //            throw getExceptionTranslator().translate("PreparedStatementCallback", sql, ex);
        //        } finally {
        //            if (psc instanceof ParameterDisposer) {
        //                ((ParameterDisposer) psc).cleanupParameters();
        //            }
        //            JdbcUtils.closeStatement(ps);
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //        }
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws DataAccessException {
        return execute(new SimplePreparedStatementCreator(sql), action);
    }
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(psc, null, rse);
    }
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(new SimplePreparedStatementCreator(sql), pss, rse);
    }
    public <T> T query(String sql, Object[] args, int[] argTypes, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(sql, newArgTypePreparedStatementSetter(args, argTypes), rse);
    }
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws DataAccessException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws DataAccessException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws DataAccessException {
        query(psc, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws DataAccessException {
        query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, Object[] args, int[] argTypes, RowCallbackHandler rch) throws DataAccessException {
        query(sql, newArgTypePreparedStatementSetter(args, argTypes), rch);
    }
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws DataAccessException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public void query(String sql, RowCallbackHandler rch, Object... args) throws DataAccessException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws DataAccessException {
        return query(psc, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws DataAccessException {
        return query(sql, pss, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        return query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, args, argTypes, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, args, argTypes, getSingleColumnRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
        return queryForObject(sql, args, getSingleColumnRowMapper(requiredType));
    }
    public Map<String, Object> queryForMap(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return queryForObject(sql, args, argTypes, getColumnMapRowMapper());
    }
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        return queryForObject(sql, args, getColumnMapRowMapper());
    }
    public long queryForLong(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Number number = queryForObject(sql, args, argTypes, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public long queryForLong(String sql, Object... args) throws DataAccessException {
        Number number = queryForObject(sql, args, Long.class);
        return (number != null ? number.longValue() : 0);
    }
    public int queryForInt(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        Number number = queryForObject(sql, args, argTypes, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public int queryForInt(String sql, Object... args) throws DataAccessException {
        Number number = queryForObject(sql, args, Integer.class);
        return (number != null ? number.intValue() : 0);
    }
    public <T> List<T> queryForList(String sql, Object[] args, int[] argTypes, Class<T> elementType) throws DataAccessException {
        return query(sql, args, argTypes, getSingleColumnRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws DataAccessException {
        return query(sql, args, getSingleColumnRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return query(sql, args, getSingleColumnRowMapper(elementType));
    }
    public List<Map<String, Object>> queryForList(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return query(sql, args, argTypes, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
        return query(sql, args, getColumnMapRowMapper());
    }
    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return query(sql, args, argTypes, new SqlRowSetResultSetExtractor());
    }
    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
        return query(sql, args, new SqlRowSetResultSetExtractor());
    }
    public int update(PreparedStatementCreator psc) throws DataAccessException {
        return update(psc, (PreparedStatementSetter) null);
    }
    public int update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        return update(new SimplePreparedStatementCreator(sql), pss);
    }
    public int update(String sql, Object[] args, int[] argTypes) throws DataAccessException {
        return update(sql, newArgTypePreparedStatementSetter(args, argTypes));
    }
    public int update(String sql, Object... args) throws DataAccessException {
        return update(sql, newArgPreparedStatementSetter(args));
    }
    public int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss) throws DataAccessException {
        Hasor.debug("Executing SQL batch update [%s].", sql);
        return execute(sql, new PreparedStatementCallback<int[]>() {
            public int[] doInPreparedStatement(PreparedStatement ps) throws SQLException {
                try {
                    int batchSize = pss.getBatchSize();
                    InterruptibleBatchPreparedStatementSetter ipss = (pss instanceof InterruptibleBatchPreparedStatementSetter ? (InterruptibleBatchPreparedStatementSetter) pss : null);
                    DatabaseMetaData dbMetaData = ps.getConnection().getMetaData();
                    if (dbMetaData.supportsBatchUpdates()) {
                        for (int i = 0; i < batchSize; i++) {
                            pss.setValues(ps, i);
                            if (ipss != null && ipss.isBatchExhausted(i))
                                break;
                            ps.addBatch();
                        }
                        return ps.executeBatch();
                    } else {
                        List<Integer> rowsAffected = new ArrayList<Integer>();
                        for (int i = 0; i < batchSize; i++) {
                            pss.setValues(ps, i);
                            if (ipss != null && ipss.isBatchExhausted(i))
                                break;
                            rowsAffected.add(ps.executeUpdate());
                        }
                        int[] rowsAffectedArray = new int[rowsAffected.size()];
                        for (int i = 0; i < rowsAffectedArray.length; i++) {
                            rowsAffectedArray[i] = rowsAffected.get(i);
                        }
                        return rowsAffectedArray;
                    }
                } finally {
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });
    }
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws DataAccessException {
        //        Hasor.assertIsNotNull(csc, "CallableStatementCreator must not be null");
        //        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //        if (logger.isDebugEnabled()) {
        //            String sql = getSql(csc);
        //            Hasor.debug("Calling stored procedure" + (sql != null ? " [" + sql + "]" : ""));
        //        }
        //        Connection con = DataSourceUtils.getConnection(getDataSource());
        //        CallableStatement cs = null;
        //        try {
        //            Connection conToUse = con;
        //            if (this.nativeJdbcExtractor != null) {
        //                conToUse = this.nativeJdbcExtractor.getNativeConnection(con);
        //            }
        //            cs = csc.createCallableStatement(conToUse);
        //            applyStatementSettings(cs);
        //            CallableStatement csToUse = cs;
        //            if (this.nativeJdbcExtractor != null) {
        //                csToUse = this.nativeJdbcExtractor.getNativeCallableStatement(cs);
        //            }
        //            T result = action.doInCallableStatement(csToUse);
        //            handleWarnings(cs);
        //            return result;
        //        } catch (SQLException ex) {
        //            // Release Connection early, to avoid potential connection pool deadlock
        //            // in the case when the exception translator hasn't been initialized yet.
        //            if (csc instanceof ParameterDisposer) {
        //                ((ParameterDisposer) csc).cleanupParameters();
        //            }
        //            String sql = getSql(csc);
        //            csc = null;
        //            JdbcUtils.closeStatement(cs);
        //            cs = null;
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //            con = null;
        //            throw getExceptionTranslator().translate("CallableStatementCallback", sql, ex);
        //        } finally {
        //            if (csc instanceof ParameterDisposer) {
        //                ((ParameterDisposer) csc).cleanupParameters();
        //            }
        //            JdbcUtils.closeStatement(cs);
        //            DataSourceUtils.releaseConnection(con, getDataSource());
        //        }
        // TODO Auto-generated method stub
        return null;
    }
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws DataAccessException {
        return execute(new SimpleCallableStatementCreator(callString), action);
    }
    public Map<String, Object> call(CallableStatementCreator csc, List<SqlInputParameter> declaredParameters) throws DataAccessException {
        final List<SqlInputParameter> updateCountParameters = new ArrayList<SqlInputParameter>();
        final List<SqlInputParameter> resultSetParameters = new ArrayList<SqlInputParameter>();
        final List<SqlInputParameter> callParameters = new ArrayList<SqlInputParameter>();
        //1.对传入参数分类
        for (SqlInputParameter parameter : declaredParameters) {
            if (parameter.isOutputParameter()) {
                /*传出参数*/
                if (parameter instanceof SqlReturnResultSet) {
                    resultSetParameters.add(parameter);
                } else {
                    updateCountParameters.add(parameter);
                }
            } else {
                /*传入参数*/
                callParameters.add(parameter);
            }
        }
        //2.执行存储过程
        return execute(csc, new CallableStatementCallback<Map<String, Object>>() {
            public Map<String, Object> doInCallableStatement(CallableStatement cs) throws SQLException {
                boolean retVal = cs.execute();
                int updateCount = cs.getUpdateCount();
                Hasor.debug("CallableStatement.execute() returned '%s'.", retVal);
                Hasor.debug("CallableStatement.getUpdateCount() returned %s.", updateCount);
                //
                Map<String, Object> returnedResults = createResultsMap();
                if (retVal || updateCount != -1)
                    returnedResults.putAll(extractReturnedResults(cs, updateCountParameters, resultSetParameters, updateCount));
                returnedResults.putAll(extractOutputParameters(cs, callParameters));
                return returnedResults;
            }
        });
    }
    private static final String RETURN_RESULT_SET_PREFIX   = "#result-set-";
    private static final String RETURN_UPDATE_COUNT_PREFIX = "#update-count-";
    /**从存储过程的返回结果集中提取数据。*/
    private Map<String, Object> extractReturnedResults(CallableStatement cs, List updateCountParameters, List resultSetParameters, int updateCount) throws SQLException {
        Map<String, Object> returnedResults = new HashMap<String, Object>();
        int rsIndex = 0;
        int updateIndex = 0;
        boolean moreResults;
        if (!skipResultsProcessing) {
            do {
                if (updateCount == -1) {
                    if (resultSetParameters != null && resultSetParameters.size() > rsIndex) {
                        SqlReturnResultSet declaredRsParam = (SqlReturnResultSet) resultSetParameters.get(rsIndex);
                        returnedResults.putAll(processResultSet(cs.getResultSet(), declaredRsParam));
                        rsIndex++;
                    } else {
                        if (!skipUndeclaredResults) {
                            String rsName = RETURN_RESULT_SET_PREFIX + (rsIndex + 1);
                            SqlReturnResultSet undeclaredRsParam = new SqlReturnResultSet(rsName, new ColumnMapRowMapper());
                            Hasor.info("Added default SqlReturnResultSet parameter named " + rsName);
                            returnedResults.putAll(processResultSet(cs.getResultSet(), undeclaredRsParam));
                            rsIndex++;
                        }
                    }
                } else {
                    if (updateCountParameters != null && updateCountParameters.size() > updateIndex) {
                        SqlOutUpdateCountParameter ucParam = (SqlOutUpdateCountParameter) updateCountParameters.get(updateIndex);
                        String declaredUcName = ucParam.getName();
                        returnedResults.put(declaredUcName, updateCount);
                        updateIndex++;
                    } else {
                        if (!skipUndeclaredResults) {
                            String undeclaredUcName = RETURN_UPDATE_COUNT_PREFIX + (updateIndex + 1);
                            Hasor.info("Added default SqlReturnUpdateCount parameter named " + undeclaredUcName);
                            returnedResults.put(undeclaredUcName, updateCount);
                            updateIndex++;
                        }
                    }
                }
                moreResults = cs.getMoreResults();
                updateCount = cs.getUpdateCount();
                Hasor.debug("CallableStatement.getUpdateCount() returned %s.", updateCount);
            } while (moreResults || updateCount != -1);
        }
        return returnedResults;
    }
    /**提取存储过程的输出参数。*/
    private Map<String, Object> extractOutputParameters(CallableStatement cs, List<SqlInputParameter> parameters) throws SQLException {
        Map<String, Object> returnedResults = new HashMap<String, Object>();
        int sqlColIndex = 1;
        for (SqlInputParameter param : parameters) {
            if (param instanceof SqlOutParameter) {
                SqlOutParameter outParam = (SqlOutParameter) param;
                if (outParam.isReturnTypeSupported()) {
                    Object out = outParam.getSqlReturnType().getTypeValue(cs, sqlColIndex, outParam.getSqlType(), outParam.getTypeName());
                    returnedResults.put(outParam.getName(), out);
                } else {
                    Object out = cs.getObject(sqlColIndex);
                    if (out instanceof ResultSet) {
                        if (outParam.isResultSetSupported()) {
                            returnedResults.putAll(processResultSet((ResultSet) out, outParam));
                        } else {
                            String rsName = outParam.getName();
                            SqlReturnResultSet rsParam = new SqlReturnResultSet(rsName, new ColumnMapRowMapper());
                            returnedResults.putAll(processResultSet(cs.getResultSet(), rsParam));
                            Hasor.info("Added default SqlReturnResultSet parameter named " + rsName);
                        }
                    } else {
                        returnedResults.put(outParam.getName(), out);
                    }
                }
            }
            if (!(param.isOutputParameter())) {
                sqlColIndex++;
            }
        }
        return returnedResults;
    }
    /**从存储过程中取得记录.*/
    private Map<String, Object> processResultSet(ResultSet rs, ResultSetSupportingSqlParameter param) throws SQLException {
        if (rs == null)
            return Collections.emptyMap();
        Map<String, Object> returnedResults = new HashMap<String, Object>();
        try {
            if (param.getRowMapper() != null) {
                RowMapper rowMapper = param.getRowMapper();
                Object result = (new RowMapperResultSetExtractor(rowMapper)).extractData(rs);
                returnedResults.put(param.getName(), result);
            } else if (param.getRowCallbackHandler() != null) {
                RowCallbackHandler rch = param.getRowCallbackHandler();
                (new RowCallbackHandlerResultSetExtractor(rch)).extractData(rs);
                returnedResults.put(param.getName(), "ResultSet returned from stored procedure was processed");
            } else if (param.getResultSetExtractor() != null) {
                Object result = param.getResultSetExtractor().extractData(rs);
                returnedResults.put(param.getName(), result);
            }
        } finally {
            rs.close();
        }
        return returnedResults;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    public int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss) throws DataAccessException {
        Hasor.debug("Executing prepared SQL update");
        return execute(psc, new PreparedStatementCallback<Integer>() {
            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    int rows = ps.executeUpdate();
                    Hasor.debug("SQL update affected " + rows + " rows");
                    return rows;
                } finally {
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });
    }
    public <T> T query(PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws DataAccessException {
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.debug("Executing prepared SQL query");
        return execute(psc, new PreparedStatementCallback<T>() {
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = null;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    rs = ps.executeQuery();
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        });
    }
    /** Create a new RowMapper for reading columns as key-value pairs. */
    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        // TODO Auto-generated method stub
        return null;//new ColumnMapRowMapper();
    }
    /** Create a new RowMapper for reading result objects from a single column.*/
    protected <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType) {
        // TODO Auto-generated method stub
        return null;//new SingleColumnRowMapper<T>(requiredType);
    }
    protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) {
        // TODO Auto-generated method stub
        return null;// new ArgPreparedStatementSetter(args);
    }
    /**Create a new ArgTypePreparedStatementSetter using the args and argTypes passed in.
     * This method allows the creation to be overridden by sub-classes.
     */
    protected PreparedStatementSetter newArgTypePreparedStatementSetter(Object[] args, int[] argTypes) {
        // TODO Auto-generated method stub
        return null;// new ArgTypePreparedStatementSetter(args, argTypes);
    }
    /**获取一个数据库连接，JDBC 框架会从 DataSource 接口尝试获取一个新的连接资源给开发者。开发者需要自己维护连接的事务，并且要保证该资源可以被正常释放。*/
    protected Connection getConnection(DataSource source) {
        // TODO Auto-generated method stub
        return null;
    }
    /**获取与本地线程绑定的数据库连接，JDBC 框架会维护这个连接的事务。开发者不必关心该连接的事务管理，以及资源释放操作。*/
    protected Connection getLocalConnection() {
        // TODO Auto-generated method stub
        return null;
    }
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    //
    /**创建用于保存结果集的数据Map。*/
    protected Map<String, Object> createResultsMap() {
        if (!isResultsMapCaseInsensitive())
            return new LinkedCaseInsensitiveMap<Object>();
        else
            return new LinkedHashMap<String, Object>();
    }
    /**对Statement的属性进行设置。*/
    /**设置 JDBC Statement 对象的 fetchSize、maxRows、Timeout等参数。*/
    protected void applyStatementSettings(Statement stmt) throws SQLException {
        int fetchSize = getFetchSize();
        if (fetchSize > 0)
            stmt.setFetchSize(fetchSize);
        int maxRows = getMaxRows();
        if (maxRows > 0)
            stmt.setMaxRows(maxRows);
        int timeout = this.getQueryTimeout();
        if (timeout > 0)
            stmt.setQueryTimeout(timeout);
    }
    /**处理潜在的 SQL 警告。*/
    /**当要求不忽略 SQL 警告时，检测到 SQL 警告抛出 SQL 异常。*/
    private void handleWarnings(Statement stmt) throws SQLException {
        if (isIgnoreWarnings()) {
            if (Hasor.isDebugLogger()) {
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    Hasor.debug("SQLWarning ignored: SQL state '%s', error code '%s', message [%s].", warningToLog.getSQLState(), warningToLog.getErrorCode(), warningToLog.getMessage());
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        } else {
            SQLWarning warning = stmt.getWarnings();
            if (warning != null)
                throw new SQLWarningException("Warning not ignored", warning);
        }
    }
    /***/
    private Connection createConnectionProxy(Connection con) {
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, new CloseSuppressingInvocationHandler(con));
    }
    /**至返回结果集中的一条数据。*/
    private static <T> T requiredSingleResult(Collection<T> results) throws InvalidDataAccessException {
        int size = (results != null ? results.size() : 0);
        if (size == 0)
            throw new InvalidDataAccessException("Empty Result");
        if (results.size() > 1)
            throw new InvalidDataAccessException("Incorrect column count: expected " + 1 + ", actual " + size);
        return results.iterator().next();
    }
    //
    //
    /**Connection 接口代理，目的是为了控制一些方法的调用。同时进行一些特殊类型的处理。*/
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
                if (retVal instanceof Statement)
                    applyStatementSettings(((Statement) retVal));
                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
    /**接口 {@link PreparedStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link PreparedStatement}对象。*/
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, SqlProvider {
        private final String sql;
        public SimplePreparedStatementCreator(String sql) {
            Hasor.assertIsNotNull(sql, "SQL must not be null");
            this.sql = sql;
        }
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }
        public String getSql() {
            return this.sql;
        }
    }
    /**接口 {@link CallableStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link CallableStatement}对象。*/
    private static class SimpleCallableStatementCreator implements CallableStatementCreator, SqlProvider {
        private final String callString;
        public SimpleCallableStatementCreator(String callString) {
            Hasor.assertIsNotNull(callString, "Call string must not be null");
            this.callString = callString;
        }
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            return con.prepareCall(this.callString);
        }
        public String getSql() {
            return this.callString;
        }
    }
    /**使用 {@link RowCallbackHandler} 类型循环处理每一行记录的适配器*/
    private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor<Object> {
        private final RowCallbackHandler rch;
        public RowCallbackHandlerResultSetExtractor(RowCallbackHandler rch) {
            this.rch = rch;
        }
        public Object extractData(ResultSet rs) throws SQLException {
            while (rs.next()) {
                this.rch.processRow(rs);
            }
            return null;
        }
    }
}