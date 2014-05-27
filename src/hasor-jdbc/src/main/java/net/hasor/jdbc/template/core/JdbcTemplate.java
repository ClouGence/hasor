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
package net.hasor.jdbc.template.core;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.jdbc.datasource.ConnectionProxy;
import net.hasor.jdbc.datasource.DataSourceUtils;
import net.hasor.jdbc.template.BatchPreparedStatementSetter;
import net.hasor.jdbc.template.CallableStatementCallback;
import net.hasor.jdbc.template.CallableStatementCreator;
import net.hasor.jdbc.template.ConnectionCallback;
import net.hasor.jdbc.template.JdbcOperations;
import net.hasor.jdbc.template.PreparedStatementCallback;
import net.hasor.jdbc.template.PreparedStatementCreator;
import net.hasor.jdbc.template.PreparedStatementSetter;
import net.hasor.jdbc.template.ResultSetExtractor;
import net.hasor.jdbc.template.RowCallbackHandler;
import net.hasor.jdbc.template.RowMapper;
import net.hasor.jdbc.template.SqlParameterSource;
import net.hasor.jdbc.template.StatementCallback;
import net.hasor.jdbc.template.core.mapper.BeanPropertyRowMapper;
import net.hasor.jdbc.template.core.mapper.ColumnMapRowMapper;
import net.hasor.jdbc.template.core.mapper.SingleColumnRowMapper;
import org.more.util.ArrayUtils;
import org.more.util.IOUtils;
import org.more.util.ResourcesUtils;
/**
 * 数据库操作模板方法。
 * @version : 2013-10-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class JdbcTemplate extends JdbcAccessor implements JdbcOperations {
    /*是否忽略出现的 SQL 警告*/
    private boolean ignoreWarnings         = true;
    /*JDBC查询和从结果集里面每次取设置行数，循环去取，直到取完。合理设置该参数可以避免内存异常。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 fetchSize 属性。*/
    private int     fetchSize              = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 maxRows 属性。*/
    private int     maxRows                = 0;
    /*从 JDBC 中可以查询的最大行数。
     * 如果这个变量被设置为非零值,它将被用于设置 statements 的 queryTimeout 属性。*/
    private int     queryTimeout           = 0;
    /*当JDBC 结果集中如出现相同的列名仅仅大小写不同时。是否保留大小写列名敏感。
     * 如果为 true 表示敏感，并且结果集Map中保留两个记录。如果为 false 则表示不敏感，如出现冲突列名后者将会覆盖前者。*/
    private boolean resultsCaseInsensitive = false;
    //
    //
    //
    /**
     * Construct a new JdbcTemplate for bean usage.
     * <p>Note: The DataSource has to be set before using the instance.
     * @see #setDataSource
     */
    public JdbcTemplate() {}
    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public JdbcTemplate(DataSource dataSource) {
        this();
        setDataSource(dataSource);
    }
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
    public boolean isResultsCaseInsensitive() {
        return resultsCaseInsensitive;
    }
    public void setResultsCaseInsensitive(boolean resultsCaseInsensitive) {
        this.resultsCaseInsensitive = resultsCaseInsensitive;
    }
    //
    //
    public void loadSQL(String sqlResource) throws IOException, SQLException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(sqlResource);
        if (inStream == null)
            throw new IOException("can't find :" + sqlResource);
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(inStream, outWriter);
        this.execute(outWriter.toString());
    }
    public void loadSQL(Reader sqlReader) throws IOException, SQLException {
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(sqlReader, outWriter);
        this.execute(outWriter.toString());
    }
    /** 判断表是否已经存在*/
    public boolean tableExist(final String name) throws SQLException {
        return this.execute(new ConnectionCallback<Boolean>() {
            public Boolean doInConnection(Connection con) throws SQLException {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet rs = metaData.getTables(null, null, name.toUpperCase(), new String[] { "TABLE" });
                return rs.next();
            }
        });
    }
    //
    //
    public <T> T execute(ConnectionCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //
        DataSource ds = this.getDataSource();//获取数据源
        Connection con = DataSourceUtils.getConnection(ds);//申请本地连接（和当前线程绑定的连接）
        con = this.newProxyConnection(con, ds);//代理连接
        //
        try {
            return action.doInConnection(con);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            DataSourceUtils.releaseConnection(con, this.getDataSource());//关闭或释放连接
        }
    }
    public <T> T execute(StatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //
        DataSource ds = this.getDataSource();//获取数据源
        Connection con = DataSourceUtils.getConnection(ds);//申请本地连接（和当前线程绑定的连接）
        con = this.newProxyConnection(con, ds);//代理连接
        //
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            applyStatementSettings(stmt);
            T result = action.doInStatement(stmt);
            handleWarnings(stmt);
            return result;
        } catch (SQLException ex) {
            throw ex;
        } finally {
            try {
                stmt.close();
            } finally {}
            DataSourceUtils.releaseConnection(con, this.getDataSource());//关闭或释放连接
        }
    }
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(psc, "PreparedStatementCreator must not be null");
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        if (Hasor.isDebugLogger()) {
            String sql = getSql(psc);
            Hasor.logDebug("Executing prepared SQL statement " + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        DataSource ds = this.getDataSource();//获取数据源
        Connection con = DataSourceUtils.getConnection(ds);//申请本地连接（和当前线程绑定的连接）
        con = this.newProxyConnection(con, ds);//代理连接
        //
        PreparedStatement ps = null;
        try {
            ps = psc.createPreparedStatement(con);
            applyStatementSettings(ps);
            T result = action.doInPreparedStatement(ps);
            handleWarnings(ps);
            return result;
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (psc instanceof ParameterDisposer)
                ((ParameterDisposer) psc).cleanupParameters();
            try {
                ps.close();
            } finally {}
            DataSourceUtils.releaseConnection(con, this.getDataSource());//关闭或释放连接
        }
    }
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(csc, "CallableStatementCreator must not be null");
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        if (Hasor.isDebugLogger()) {
            String sql = getSql(csc);
            Hasor.logDebug("Calling stored procedure" + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        DataSource ds = this.getDataSource();//获取数据源
        Connection con = DataSourceUtils.getConnection(ds);//申请本地连接（和当前线程绑定的连接）
        con = this.newProxyConnection(con, ds);//代理连接
        //
        CallableStatement cs = null;
        try {
            cs = csc.createCallableStatement(con);
            applyStatementSettings(cs);
            T result = action.doInCallableStatement(cs);
            handleWarnings(cs);
            return result;
        } catch (SQLException ex) {
            throw new SQLException("CallableStatementCallback SQL :" + getSql(action), ex);
        } finally {
            if (csc instanceof ParameterDisposer)
                ((ParameterDisposer) csc).cleanupParameters();
            try {
                cs.close();
            } finally {}
            DataSourceUtils.releaseConnection(con, this.getDataSource());//关闭或释放连接
        }
    }
    public <T> T execute(String sql, PreparedStatementCallback<T> action) throws SQLException {
        return execute(new SimplePreparedStatementCreator(sql), action);
    }
    public <T> T execute(String callString, CallableStatementCallback<T> action) throws SQLException {
        return execute(new SimpleCallableStatementCreator(callString), action);
    }
    public <T> T execute(String sql, SqlParameterSource paramSource, PreparedStatementCallback<T> action) throws SQLException {
        return execute(getPreparedStatementCreator(sql, paramSource), action);
    }
    public <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action) throws SQLException {
        return execute(sql, new InnerMapSqlParameterSource(paramMap), action);
    }
    //
    //
    //
    public void execute(final String sql) throws SQLException {
        Hasor.logDebug("Executing SQL statement [%s].", sql);
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
    //
    //
    //
    /***/
    public <T> T query(PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws SQLException {
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.logDebug("Executing prepared SQL query");
        return execute(psc, new PreparedStatementCallback<T>() {
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = null;
                try {
                    if (pss != null)
                        pss.setValues(ps);
                    rs = ps.executeQuery();
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                    if (pss instanceof ParameterDisposer)
                        ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });
    }
    public <T> T query(PreparedStatementCreator psc, ResultSetExtractor<T> rse) throws SQLException {
        return query(psc, null, rse);
    }
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws SQLException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.logDebug("Executing SQL query [%s].", sql);
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            public T doInStatement(Statement stmt) throws SQLException {
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                    rs = null;
                }
            }
            public String getSql() {
                return sql;
            }
        }
        return execute(new QueryStatementCallback());
    }
    public <T> T query(String sql, PreparedStatementSetter pss, ResultSetExtractor<T> rse) throws SQLException {
        return query(new SimplePreparedStatementCreator(sql), pss, rse);
    }
    public <T> T query(String sql, ResultSetExtractor<T> rse, Object... args) throws SQLException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public <T> T query(String sql, Object[] args, ResultSetExtractor<T> rse) throws SQLException {
        return query(sql, newArgPreparedStatementSetter(args), rse);
    }
    public <T> T query(String sql, SqlParameterSource paramSource, ResultSetExtractor<T> rse) throws SQLException {
        return query(getPreparedStatementCreator(sql, paramSource), rse);
    }
    public <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws SQLException {
        return query(sql, new InnerMapSqlParameterSource(paramMap), rse);
    }
    //
    //
    //
    public void query(PreparedStatementCreator psc, RowCallbackHandler rch) throws SQLException {
        query(psc, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, RowCallbackHandler rch) throws SQLException {
        query(sql, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, PreparedStatementSetter pss, RowCallbackHandler rch) throws SQLException {
        query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }
    public void query(String sql, RowCallbackHandler rch, Object... args) throws SQLException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public void query(String sql, Object[] args, RowCallbackHandler rch) throws SQLException {
        query(sql, newArgPreparedStatementSetter(args), rch);
    }
    public void query(String sql, SqlParameterSource paramSource, RowCallbackHandler rch) throws SQLException {
        query(getPreparedStatementCreator(sql, paramSource), rch);
    }
    public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws SQLException {
        query(sql, new InnerMapSqlParameterSource(paramMap), rch);
    }
    //
    //
    //
    public <T> List<T> query(PreparedStatementCreator psc, RowMapper<T> rowMapper) throws SQLException {
        return query(psc, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rowMapper) throws SQLException {
        return query(sql, pss, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws SQLException {
        return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
        return query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
        return query(sql, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    public <T> List<T> query(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws SQLException {
        return query(getPreparedStatementCreator(sql, paramSource), rowMapper);
    }
    public <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws SQLException {
        return query(sql, new InnerMapSqlParameterSource(paramMap), rowMapper);
    }
    //
    //
    //
    public <T> List<T> queryForList(String sql, Class<T> elementType) throws SQLException {
        return query(sql, getBeanPropertyRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws SQLException {
        return query(sql, args, getBeanPropertyRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Object[] args, Class<T> elementType) throws SQLException {
        return query(sql, args, getBeanPropertyRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, SqlParameterSource paramSource, Class<T> elementType) throws SQLException {
        return query(sql, paramSource, getBeanPropertyRowMapper(elementType));
    }
    public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType) throws SQLException {
        return queryForList(sql, new InnerMapSqlParameterSource(paramMap), elementType);
    }
    //
    //
    //
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws SQLException {
        List<T> results = query(sql, rowMapper);
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws SQLException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) throws SQLException {
        List<T> results = query(getPreparedStatementCreator(sql, paramSource), rowMapper);
        return requiredSingleResult(results);
    }
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws SQLException {
        return queryForObject(sql, new InnerMapSqlParameterSource(paramMap), rowMapper);
    }
    public <T> T queryForObject(String sql, Class<T> requiredType) throws SQLException {
        return queryForObject(sql, getBeanPropertyRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws SQLException {
        return queryForObject(sql, args, getBeanPropertyRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Object[] args, Class<T> requiredType) throws SQLException {
        return queryForObject(sql, args, getBeanPropertyRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, SqlParameterSource paramSource, Class<T> requiredType) throws SQLException {
        return queryForObject(sql, paramSource, getBeanPropertyRowMapper(requiredType));
    }
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws SQLException {
        return queryForObject(sql, paramMap, getBeanPropertyRowMapper(requiredType));
    }
    //
    //
    //
    public long queryForLong(String sql) throws SQLException {
        Number number = queryForObject(sql, getSingleColumnRowMapper(Long.class));
        return (number != null ? number.longValue() : 0);
    }
    public long queryForLong(String sql, Object... args) throws SQLException {
        Number number = queryForObject(sql, args, getSingleColumnRowMapper(Long.class));
        return (number != null ? number.longValue() : 0);
    }
    public long queryForLong(String sql, SqlParameterSource paramSource) throws SQLException {
        Number number = queryForObject(sql, paramSource, getSingleColumnRowMapper(Number.class));
        return (number != null ? number.longValue() : 0);
    }
    public long queryForLong(String sql, Map<String, ?> paramMap) throws SQLException {
        return queryForLong(sql, new InnerMapSqlParameterSource(paramMap));
    }
    public int queryForInt(String sql) throws SQLException {
        Number number = queryForObject(sql, getSingleColumnRowMapper(Integer.class));
        return (number != null ? number.intValue() : 0);
    }
    public int queryForInt(String sql, Object... args) throws SQLException {
        Number number = queryForObject(sql, args, getSingleColumnRowMapper(Integer.class));
        return (number != null ? number.intValue() : 0);
    }
    public int queryForInt(String sql, SqlParameterSource paramSource) throws SQLException {
        Number number = queryForObject(sql, paramSource, getSingleColumnRowMapper(Number.class));
        return (number != null ? number.intValue() : 0);
    }
    public int queryForInt(String sql, Map<String, ?> paramMap) throws SQLException {
        return queryForInt(sql, new InnerMapSqlParameterSource(paramMap));
    }
    //
    //
    //
    public Map<String, Object> queryForMap(String sql) throws SQLException {
        return queryForObject(sql, getColumnMapRowMapper());
    }
    public Map<String, Object> queryForMap(String sql, Object... args) throws SQLException {
        return queryForObject(sql, args, getColumnMapRowMapper());
    }
    public Map<String, Object> queryForMap(String sql, SqlParameterSource paramSource) throws SQLException {
        return queryForObject(sql, paramSource, getColumnMapRowMapper());
    }
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws SQLException {
        return queryForObject(sql, paramMap, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql) throws SQLException {
        return query(sql, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql, Object... args) throws SQLException {
        return query(sql, args, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql, SqlParameterSource paramSource) throws SQLException {
        return query(sql, paramSource, getColumnMapRowMapper());
    }
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws SQLException {
        return queryForList(sql, new InnerMapSqlParameterSource(paramMap));
    }
    //
    //
    //
    //    public SqlRowSet queryForRowSet(String sql) throws DataAccessException {
    //        return query(sql, new SqlRowSetResultSetExtractor());
    //    }
    //    public SqlRowSet queryForRowSet(String sql, Object... args) throws DataAccessException {
    //        return query(sql, args, new SqlRowSetResultSetExtractor());
    //    }
    //    public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) throws DataAccessException {
    //        return query(sql, args, argTypes, new SqlRowSetResultSetExtractor());
    //    }
    //    public SqlRowSet queryForRowSet(String sql, SqlParameterSource paramSource) throws DataAccessException {
    //        return query(getPreparedStatementCreator(sql, paramSource), new SqlRowSetResultSetExtractor());
    //    }
    //    public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException {
    //        return queryForRowSet(sql, new MapSqlParameterSource(paramMap));
    //    }
    //
    //
    //
    /***/
    public int update(final PreparedStatementCreator psc, final PreparedStatementSetter pss) throws SQLException {
        Hasor.logDebug("Executing prepared SQL update");
        return execute(psc, new PreparedStatementCallback<Integer>() {
            public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
                try {
                    if (pss != null)
                        pss.setValues(ps);
                    int rows = ps.executeUpdate();
                    Hasor.logDebug("SQL update affected " + rows + " rows");
                    return rows;
                } finally {
                    if (pss instanceof ParameterDisposer)
                        ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });
    }
    public int update(PreparedStatementCreator psc) throws SQLException {
        return update(psc, (PreparedStatementSetter) null);
    }
    public int update(final String sql) throws SQLException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.logDebug("Executing SQL update [%s]", sql);
        //
        class UpdateStatementCallback implements StatementCallback<Integer>, SqlProvider {
            public Integer doInStatement(Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                Hasor.logDebug("SQL update affected %s rows.", rows);
                return rows;
            }
            public String getSql() {
                return sql;
            }
        }
        return execute(new UpdateStatementCallback());
    }
    public int update(String sql, PreparedStatementSetter pss) throws SQLException {
        return update(new SimplePreparedStatementCreator(sql), pss);
    }
    public int update(String sql, Object... args) throws SQLException {
        return update(sql, newArgPreparedStatementSetter(args));
    }
    public int update(String sql, SqlParameterSource paramSource) throws SQLException {
        return update(getPreparedStatementCreator(sql, paramSource));
    }
    public int update(String sql, Map<String, ?> paramMap) throws SQLException {
        return update(sql, new InnerMapSqlParameterSource(paramMap));
    }
    //
    //
    //
    public int[] batchUpdate(final String[] sql) throws SQLException {
        if (ArrayUtils.isEmpty(sql))
            throw new NullPointerException(sql + "SQL array must not be empty");
        Hasor.logDebug("Executing SQL batch update of %s statements", sql.length);
        //
        class BatchUpdateStatementCallback implements StatementCallback<int[]>, SqlProvider {
            private String currSql;
            public int[] doInStatement(Statement stmt) throws SQLException {
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
                            throw new SQLException("Invalid batch SQL statement: " + sql[i]);
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
    public int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss) throws SQLException {
        Hasor.logDebug("Executing SQL batch update [%s].", sql);
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
                        for (int i = 0; i < rowsAffectedArray.length; i++)
                            rowsAffectedArray[i] = rowsAffected.get(i);
                        return rowsAffectedArray;
                    }
                } finally {
                    if (pss instanceof ParameterDisposer)
                        ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });
    }
    public int[] batchUpdate(String sql, Map<String, ?>[] batchValues) {
        SqlParameterSource[] batchArgs = new SqlParameterSource[batchValues.length];
        int i = 0;
        for (Map<String, ?> values : batchValues) {
            batchArgs[i] = new InnerMapSqlParameterSource(values);
            i++;
        }
        return batchUpdate(sql, batchArgs);
    }
    public int[] batchUpdate(String sql, SqlParameterSource[] batchArgs) {
        //        ParsedSql parsedSql = this.getParsedSql(sql);
        //        return NamedBatchUpdateUtils.executeBatchUpdateWithNamedParameters(parsedSql, batchArgs, this);
        return null;
    }
    //
    //
    //
    /** Create a new RowMapper for reading columns as key-value pairs. */
    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        return new ColumnMapRowMapper() {
            protected Map<String, Object> createColumnMap(int columnCount) {
                return createResultsMap();
            }
        };
    }
    /** Create a new RowMapper for reading columns as Bean pairs. */
    protected <T> RowMapper<T> getBeanPropertyRowMapper(Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType != null, "requiredType is null.");
        if (Map.class.isAssignableFrom(requiredType))
            return (RowMapper<T>) getColumnMapRowMapper();
        //
        if (requiredType.isPrimitive() || Number.class.isAssignableFrom(requiredType) || String.class.isAssignableFrom(requiredType))
            return getSingleColumnRowMapper(requiredType);
        //
        return new BeanPropertyRowMapper<T>(requiredType) {
            public boolean isCaseInsensitive() {
                return isResultsCaseInsensitive();
            }
        };
    }
    /** Create a new RowMapper for reading result objects from a single column.*/
    protected <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType) {
        return new SingleColumnRowMapper<T>(requiredType);
    }
    //
    //
    //
    /**创建用于保存结果集的数据Map。*/
    protected Map<String, Object> createResultsMap() {
        if (!isResultsCaseInsensitive())
            return new LinkedCaseInsensitiveMap<Object>();
        else
            return new LinkedHashMap<String, Object>();
    }
    /** Create a new PreparedStatementSetter.*/
    protected PreparedStatementSetter newArgPreparedStatementSetter(Object[] args) throws SQLException {
        return new InnerArgPreparedStatementSetter(args);
    }
    /**对Statement的属性进行设置。设置 JDBC Statement 对象的 fetchSize、maxRows、Timeout等参数。*/
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
    /**
     * Build a PreparedStatementCreator based on the given SQL and named parameters.
     * <p>Note: Not used for the <code>update</code> variant with generated key handling.
     */
    protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
        return new MapPreparedStatementCreator(sql, paramSource);
    }
    //
    /**处理潜在的 SQL 警告。当要求不忽略 SQL 警告时，检测到 SQL 警告抛出 SQL 异常。*/
    private void handleWarnings(Statement stmt) throws SQLException {
        if (isIgnoreWarnings()) {
            if (Hasor.isDebugLogger()) {
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    Hasor.logDebug("SQLWarning ignored: SQL state '%s', error code '%s', message [%s].", warningToLog.getSQLState(), warningToLog.getErrorCode(), warningToLog.getMessage());
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        } else {
            SQLWarning warning = stmt.getWarnings();
            if (warning != null)
                throw new SQLException("Warning not ignored", warning);
        }
    }
    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider)
            return ((SqlProvider) sqlProvider).getSql();
        else
            return null;
    }
    //
    /**至返回结果集中的一条数据。*/
    private static <T> T requiredSingleResult(Collection<T> results) throws SQLException {
        int size = (results != null ? results.size() : 0);
        if (size == 0)
            throw new SQLException("Empty Result");
        if (results.size() > 1)
            throw new SQLException("Incorrect column count: expected " + 1 + ", actual " + size);
        return results.iterator().next();
    }
    /**获取与本地线程绑定的数据库连接，JDBC 框架会维护这个连接的事务。开发者不必关心该连接的事务管理，以及资源释放操作。*/
    private Connection newProxyConnection(Connection target, DataSource targetSource) {
        Hasor.assertIsNotNull(target, "Connection is null.");
        CloseSuppressingInvocationHandler handler = new CloseSuppressingInvocationHandler(target, targetSource);
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, handler);
    }
    //
    //
    //
    protected static interface SqlProvider {
        public String getSql();
    }
    /**Connection 接口代理，目的是为了控制一些方法的调用。同时进行一些特殊类型的处理。*/
    private class CloseSuppressingInvocationHandler implements InvocationHandler {
        private final Connection target;
        private final DataSource targetSource;
        public CloseSuppressingInvocationHandler(Connection target, DataSource targetSource) {
            this.target = target;
            this.targetSource = targetSource;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Invocation on ConnectionProxy interface coming in...
            if (method.getName().equals("getTargetConnection")) {
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            } else if (method.getName().equals("getTargetSource")) {
                // Handle getTargetConnection method: return underlying DataSource.
                return this.targetSource;
            } else if (method.getName().equals("equals")) {
                // Only consider equal when proxies are identical.
                return (proxy == args[0]);
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
            while (rs.next())
                this.rch.processRow(rs);
            return null;
        }
    }
}
//
//
//
/**接口 {@link CallableStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link CallableStatement}对象。*/
class MapPreparedStatementCreator implements PreparedStatementCreator, JdbcTemplate.SqlProvider {
    private String             originalSql = null;
    private SqlParameterSource paramSource = null;
    //
    public MapPreparedStatementCreator(String originalSql, SqlParameterSource paramSource) {
        Hasor.assertIsNotNull(originalSql, "SQL must not be null");
        this.originalSql = originalSql;
        this.paramSource = paramSource;
    }
    //
    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
        //
        //1.关键参数定义
        List<String> parameterNames = new ArrayList<String>();
        List<int[]> parameterIndexes = new ArrayList<int[]>();
        int namedParameterCount = 0;//带有名字参数的总数
        int unnamedParameterCount = 0;//无名字参数总数
        int totalParameterCount = 0;//参数总数
        //
        //2.分析SQL，提取出SQL中参数信息
        {
            Hasor.assertIsNotNull(this.originalSql, "SQL must not be null");
            Set<String> namedParameters = new HashSet<String>();
            char[] statement = this.originalSql.toCharArray();
            int i = 0;
            while (i < statement.length) {
                int skipToPosition = skipCommentsAndQuotes(statement, i);//从当前为止掠过的长度
                if (i != skipToPosition) {
                    if (skipToPosition >= statement.length)
                        break;
                    i = skipToPosition;
                }
                char c = statement[i];
                if (c == ':' || c == '&') {
                    int j = i + 1;
                    if (j < statement.length && statement[j] == ':' && c == ':') {
                        i = i + 2;// Postgres-style "::" casting operator - to be skipped.
                        continue;
                    }
                    while (j < statement.length && !isParameterSeparator(statement[j])) {
                        j++;
                    }
                    if (j - i > 1) {
                        String parameter = this.originalSql.substring(i + 1, j);
                        if (!namedParameters.contains(parameter)) {
                            namedParameters.add(parameter);
                            namedParameterCount++;
                        }
                        parameterNames.add(parameter);
                        parameterIndexes.add(new int[] { i, j });//startIndex, endIndex
                        totalParameterCount++;
                    }
                    i = j - 1;
                } else {
                    if (c == '?') {
                        unnamedParameterCount++;
                        totalParameterCount++;
                    }
                }
                i++;
            }
            //this.namedParameterCount = namedParameterCount;/*带有名字参数的总数*/
            //this.unnamedParameterCount = unnamedParameterCount;/*匿名参数的总数*/
            //this.totalParameterCount = totalParameterCount;/*总共参数个数*/
        }
        //
        //3.根据参数信息生成最终会执行的SQL语句.
        StringBuilder sqlToUse = new StringBuilder();
        {
            int lastIndex = 0;
            for (int i = 0; i < parameterNames.size(); i++) {
                String paramName = (String) parameterNames.get(i);
                int[] indexes = parameterIndexes.get(i);
                int startIndex = indexes[0];
                int endIndex = indexes[1];
                sqlToUse.append(this.originalSql.substring(lastIndex, startIndex));
                if (this.paramSource != null && this.paramSource.hasValue(paramName)) {
                    Object value = this.paramSource.getValue(paramName);
                    if (value instanceof Collection) {
                        Iterator<?> entryIter = ((Collection<?>) value).iterator();
                        int k = 0;
                        while (entryIter.hasNext()) {
                            if (k > 0)
                                sqlToUse.append(", ");
                            k++;
                            Object entryItem = entryIter.next();
                            if (entryItem instanceof Object[]) {
                                Object[] expressionList = (Object[]) entryItem;
                                sqlToUse.append("(");
                                for (int m = 0; m < expressionList.length; m++) {
                                    if (m > 0)
                                        sqlToUse.append(", ");
                                    sqlToUse.append("?");
                                }
                                sqlToUse.append(")");
                            } else {
                                sqlToUse.append("?");
                            }
                        }
                    } else {
                        sqlToUse.append("?");
                    }
                } else {
                    sqlToUse.append("?");
                }
                lastIndex = endIndex;
            }
            sqlToUse.append(this.originalSql.substring(lastIndex, this.originalSql.length()));
        }
        //
        //4.确定参数对象        
        Object[] paramArray = new Object[totalParameterCount];
        if (namedParameterCount > 0 && unnamedParameterCount > 0)
            throw new SQLException("You can't mix named and traditional ? placeholders. You have " + namedParameterCount + " named parameter(s) and " + unnamedParameterCount + " traditonal placeholder(s) in [" + this.originalSql + "]");
        for (int i = 0; i < parameterNames.size(); i++) {
            String paramName = parameterNames.get(i);
            paramArray[i] = this.paramSource.getValue(paramName);
        }
        //
        //5.创建PreparedStatement对象，并设置参数
        PreparedStatement statement = con.prepareStatement(sqlToUse.toString());
        for (int i = 0; i < paramArray.length; i++)
            InnerStatementSetterUtils.setParameterValue(statement, i + 1, paramArray[i]);
        InnerStatementSetterUtils.cleanupParameters(paramArray);
        return statement;
    }
    public String getSql() {
        return this.originalSql;
    }
    //
    //
    //
    /**Set of characters that qualify as parameter separators, indicating that a parameter name in a SQL String has ended. */
    private static final char[]   PARAMETER_SEPARATORS = new char[] { '"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^' };
    /** Set of characters that qualify as comment or quotes starting characters.*/
    private static final String[] START_SKIP           = new String[] { "'", "\"", "--", "/*" };
    /**Set of characters that at are the corresponding comment or quotes ending characters. */
    private static final String[] STOP_SKIP            = new String[] { "'", "\"", "\n", "*/" };
    //-------------------------------------------------------------------------
    // Core methods used by NamedParameterJdbcTemplate and SqlQuery/SqlUpdate
    //-------------------------------------------------------------------------
    /** Skip over comments and quoted names present in an SQL statement */
    private int skipCommentsAndQuotes(char[] statement, int position) {
        for (int i = 0; i < START_SKIP.length; i++) {
            if (statement[position] == START_SKIP[i].charAt(0)) {
                boolean match = true;
                for (int j = 1; j < START_SKIP[i].length(); j++) {
                    if (!(statement[position + j] == START_SKIP[i].charAt(j))) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int offset = START_SKIP[i].length();
                    for (int m = position + offset; m < statement.length; m++) {
                        if (statement[m] == STOP_SKIP[i].charAt(0)) {
                            boolean endMatch = true;
                            int endPos = m;
                            for (int n = 1; n < STOP_SKIP[i].length(); n++) {
                                if (m + n >= statement.length)
                                    return statement.length;// last comment not closed properly
                                if (!(statement[m + n] == STOP_SKIP[i].charAt(n))) {
                                    endMatch = false;
                                    break;
                                }
                                endPos = m + n;
                            }
                            if (endMatch)
                                return endPos + 1;// found character sequence ending comment or quote
                        }
                    }
                    // character sequence ending comment or quote not found
                    return statement.length;
                }
            }
        }
        return position;
    }
    /** Determine whether a parameter name ends at the current position, that is, whether the given character qualifies as a separator. */
    private boolean isParameterSeparator(char c) {
        if (Character.isWhitespace(c))
            return true;
        for (char separator : PARAMETER_SEPARATORS)
            if (c == separator)
                return true;
        return false;
    }
}