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
package net.hasor.db.jdbc.core;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.hasor.core.Hasor;
import net.hasor.db.datasource.ConnectionProxy;
import net.hasor.db.datasource.DataSourceUtils;
import net.hasor.db.jdbc.BatchPreparedStatementSetter;
import net.hasor.db.jdbc.CallableStatementCallback;
import net.hasor.db.jdbc.CallableStatementCreator;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.JdbcOperations;
import net.hasor.db.jdbc.PreparedStatementCallback;
import net.hasor.db.jdbc.PreparedStatementCreator;
import net.hasor.db.jdbc.PreparedStatementSetter;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowCallbackHandler;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.SqlParameterSource;
import net.hasor.db.jdbc.StatementCallback;
import net.hasor.db.jdbc.core.mapper.BeanPropertyRowMapper;
import net.hasor.db.jdbc.core.mapper.ColumnMapRowMapper;
import net.hasor.db.jdbc.core.mapper.SingleColumnRowMapper;
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
    public JdbcTemplate(final DataSource dataSource) {
        this.setDataSource(dataSource);
    }
    public JdbcTemplate(final Connection conn) {
        this.setConnection(conn);
    }
    //
    //
    //
    public boolean isIgnoreWarnings() {
        return this.ignoreWarnings;
    }
    public void setIgnoreWarnings(final boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }
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
    public boolean isResultsCaseInsensitive() {
        return this.resultsCaseInsensitive;
    }
    public void setResultsCaseInsensitive(final boolean resultsCaseInsensitive) {
        this.resultsCaseInsensitive = resultsCaseInsensitive;
    }
    //
    //
    //
    public void loadSQL(final String sqlResource) throws IOException, SQLException {
        this.loadSQL("UTF-8", sqlResource);
    }
    public void loadSQL(final String charsetName, final String sqlResource) throws IOException, SQLException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(sqlResource);
        if (inStream == null)
            throw new IOException("can't find :" + sqlResource);
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(charsetName));
        this.loadSQL(reader);
    }
    public void loadSQL(final Reader sqlReader) throws IOException, SQLException {
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(sqlReader, outWriter);
        this.execute(outWriter.toString());
    }
    /** 判断表是否已经存在*/
    public boolean tableExist(final String name) throws SQLException {
        return this.execute(new ConnectionCallback<Boolean>() {
            @Override
            public Boolean doInConnection(final Connection con) throws SQLException {
                DatabaseMetaData metaData = con.getMetaData();
                ResultSet rs = metaData.getTables(null, null, name.toUpperCase(), new String[] { "TABLE" });
                return rs.next();
            }
        });
    }
    //
    //
    //
    @Override
    public <T> T execute(final ConnectionCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        //
        Connection con = this.getConnection();
        boolean useLocal = false;
        if (con == null) {
            DataSource ds = this.getDataSource();//获取数据源
            con = DataSourceUtils.getConnection(ds);//申请本地连接（和当前线程绑定的连接）
            useLocal = true;
            con = this.newProxyConnection(con, ds);//代理连接
        } else
            con = this.newProxyConnection(con, null);//代理连接
        //
        try {
            return action.doInConnection(con);
        } catch (SQLException ex) {
            throw ex;
        } finally {
            if (useLocal)
                DataSourceUtils.releaseConnection(con, this.getDataSource());//关闭或释放连接
        }
    }
    @Override
    public <T> T execute(final StatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        return this.execute(new ConnectionCallback<T>() {
            @Override
            public T doInConnection(final Connection con) throws SQLException {
                Statement stmt = null;
                try {
                    stmt = con.createStatement();
                    JdbcTemplate.this.applyStatementSettings(stmt);
                    T result = action.doInStatement(stmt);
                    JdbcTemplate.this.handleWarnings(stmt);
                    return result;
                } catch (SQLException ex) {
                    throw ex;
                } finally {
                    stmt.close();
                }
            }
        });
    }
    @Override
    public <T> T execute(final PreparedStatementCreator psc, final PreparedStatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(psc, "PreparedStatementCreator must not be null");
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        if (Hasor.isDebugLogger()) {
            String sql = JdbcTemplate.getSql(psc);
            Hasor.logDebug("Executing prepared SQL statement " + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        return this.execute(new ConnectionCallback<T>() {
            @Override
            public T doInConnection(final Connection con) throws SQLException {
                PreparedStatement ps = null;
                try {
                    ps = psc.createPreparedStatement(con);
                    JdbcTemplate.this.applyStatementSettings(ps);
                    T result = action.doInPreparedStatement(ps);
                    JdbcTemplate.this.handleWarnings(ps);
                    return result;
                } catch (SQLException ex) {
                    throw ex;
                } finally {
                    if (psc instanceof ParameterDisposer)
                        ((ParameterDisposer) psc).cleanupParameters();
                    ps.close();
                }
            }
        });
    }
    @Override
    public <T> T execute(final CallableStatementCreator csc, final CallableStatementCallback<T> action) throws SQLException {
        Hasor.assertIsNotNull(csc, "CallableStatementCreator must not be null");
        Hasor.assertIsNotNull(action, "Callback object must not be null");
        if (Hasor.isDebugLogger()) {
            String sql = JdbcTemplate.getSql(csc);
            Hasor.logDebug("Calling stored procedure" + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        return this.execute(new ConnectionCallback<T>() {
            @Override
            public T doInConnection(final Connection con) throws SQLException {
                CallableStatement cs = null;
                try {
                    cs = csc.createCallableStatement(con);
                    JdbcTemplate.this.applyStatementSettings(cs);
                    T result = action.doInCallableStatement(cs);
                    JdbcTemplate.this.handleWarnings(cs);
                    return result;
                } catch (SQLException ex) {
                    throw new SQLException("CallableStatementCallback SQL :" + JdbcTemplate.getSql(action), ex);
                } finally {
                    if (csc instanceof ParameterDisposer)
                        ((ParameterDisposer) csc).cleanupParameters();
                    cs.close();
                }
            }
        });
    }
    @Override
    public <T> T execute(final String sql, final PreparedStatementCallback<T> action) throws SQLException {
        return this.execute(new SimplePreparedStatementCreator(sql), action);
    }
    @Override
    public <T> T execute(final String callString, final CallableStatementCallback<T> action) throws SQLException {
        return this.execute(new SimpleCallableStatementCreator(callString), action);
    }
    @Override
    public <T> T execute(final String sql, final SqlParameterSource paramSource, final PreparedStatementCallback<T> action) throws SQLException {
        return this.execute(this.getPreparedStatementCreator(sql, paramSource), action);
    }
    @Override
    public <T> T execute(final String sql, final Map<String, ?> paramMap, final PreparedStatementCallback<T> action) throws SQLException {
        return this.execute(this.getPreparedStatementCreator(sql, new InnerMapSqlParameterSource(paramMap)), action);
    }
    //
    //
    //
    @Override
    public void execute(final String sql) throws SQLException {
        Hasor.logDebug("Executing SQL statement [%s].", sql);
        class ExecuteStatementCallback implements StatementCallback<Object>, SqlProvider {
            @Override
            public Object doInStatement(final Statement stmt) throws SQLException {
                stmt.execute(sql);
                return null;
            }
            @Override
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
    public <T> T query(final PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws SQLException {
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.logDebug("Executing prepared SQL query");
        return this.execute(psc, new PreparedStatementCallback<T>() {
            @Override
            public T doInPreparedStatement(final PreparedStatement ps) throws SQLException {
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
    @Override
    public <T> T query(final PreparedStatementCreator psc, final ResultSetExtractor<T> rse) throws SQLException {
        return this.query(psc, null, rse);
    }
    @Override
    public <T> T query(final String sql, final ResultSetExtractor<T> rse) throws SQLException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.assertIsNotNull(rse, "ResultSetExtractor must not be null");
        Hasor.logDebug("Executing SQL query [%s].", sql);
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            @Override
            public T doInStatement(final Statement stmt) throws SQLException {
                ResultSet rs = null;
                try {
                    rs = stmt.executeQuery(sql);
                    return rse.extractData(rs);
                } finally {
                    rs.close();
                    rs = null;
                }
            }
            @Override
            public String getSql() {
                return sql;
            }
        }
        return this.execute(new QueryStatementCallback());
    }
    @Override
    public <T> T query(final String sql, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws SQLException {
        return this.query(new SimplePreparedStatementCreator(sql), pss, rse);
    }
    @Override
    public <T> T query(final String sql, final ResultSetExtractor<T> rse, final Object... args) throws SQLException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }
    @Override
    public <T> T query(final String sql, final Object[] args, final ResultSetExtractor<T> rse) throws SQLException {
        return this.query(sql, this.newArgPreparedStatementSetter(args), rse);
    }
    @Override
    public <T> T query(final String sql, final SqlParameterSource paramSource, final ResultSetExtractor<T> rse) throws SQLException {
        return this.query(this.getPreparedStatementCreator(sql, paramSource), rse);
    }
    @Override
    public <T> T query(final String sql, final Map<String, ?> paramMap, final ResultSetExtractor<T> rse) throws SQLException {
        return this.query(this.getPreparedStatementCreator(sql, new InnerMapSqlParameterSource(paramMap)), rse);
    }
    //
    //
    //
    @Override
    public void query(final PreparedStatementCreator psc, final RowCallbackHandler rch) throws SQLException {
        this.query(psc, new RowCallbackHandlerResultSetExtractor(rch));
    }
    @Override
    public void query(final String sql, final RowCallbackHandler rch) throws SQLException {
        this.query(sql, new RowCallbackHandlerResultSetExtractor(rch));
    }
    @Override
    public void query(final String sql, final PreparedStatementSetter pss, final RowCallbackHandler rch) throws SQLException {
        this.query(sql, pss, new RowCallbackHandlerResultSetExtractor(rch));
    }
    @Override
    public void query(final String sql, final RowCallbackHandler rch, final Object... args) throws SQLException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }
    @Override
    public void query(final String sql, final Object[] args, final RowCallbackHandler rch) throws SQLException {
        this.query(sql, this.newArgPreparedStatementSetter(args), rch);
    }
    @Override
    public void query(final String sql, final SqlParameterSource paramSource, final RowCallbackHandler rch) throws SQLException {
        this.query(this.getPreparedStatementCreator(sql, paramSource), rch);
    }
    @Override
    public void query(final String sql, final Map<String, ?> paramMap, final RowCallbackHandler rch) throws SQLException {
        this.query(this.getPreparedStatementCreator(sql, new InnerMapSqlParameterSource(paramMap)), rch);
    }
    //
    //
    //
    @Override
    public <T> List<T> query(final PreparedStatementCreator psc, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(psc, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    @Override
    public <T> List<T> query(final String sql, final PreparedStatementSetter pss, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, pss, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    @Override
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... args) throws SQLException {
        return this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    @Override
    public <T> List<T> query(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    @Override
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, new RowMapperResultSetExtractor<T>(rowMapper));
    }
    @Override
    public <T> List<T> query(final String sql, final SqlParameterSource paramSource, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
    }
    @Override
    public <T> List<T> query(final String sql, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(this.getPreparedStatementCreator(sql, new InnerMapSqlParameterSource(paramMap)), rowMapper);
    }
    //
    //
    //
    @Override
    public <T> List<T> queryForList(final String sql, final Class<T> elementType) throws SQLException {
        return this.query(sql, this.getBeanPropertyRowMapper(elementType));
    }
    @Override
    public <T> List<T> queryForList(final String sql, final Class<T> elementType, final Object... args) throws SQLException {
        return this.query(sql, args, this.getBeanPropertyRowMapper(elementType));
    }
    @Override
    public <T> List<T> queryForList(final String sql, final Object[] args, final Class<T> elementType) throws SQLException {
        return this.query(sql, args, this.getBeanPropertyRowMapper(elementType));
    }
    @Override
    public <T> List<T> queryForList(final String sql, final SqlParameterSource paramSource, final Class<T> elementType) throws SQLException {
        return this.query(sql, paramSource, this.getBeanPropertyRowMapper(elementType));
    }
    @Override
    public <T> List<T> queryForList(final String sql, final Map<String, ?> paramMap, final Class<T> elementType) throws SQLException {
        return this.query(sql, paramMap, this.getBeanPropertyRowMapper(elementType));
    }
    //
    //
    //
    @Override
    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) throws SQLException {
        List<T> results = this.query(sql, rowMapper);
        return JdbcTemplate.requiredSingleResult(results);
    }
    @Override
    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) throws SQLException {
        List<T> results = this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return JdbcTemplate.requiredSingleResult(results);
    }
    @Override
    public <T> T queryForObject(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws SQLException {
        List<T> results = this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return JdbcTemplate.requiredSingleResult(results);
    }
    @Override
    public <T> T queryForObject(final String sql, final SqlParameterSource paramSource, final RowMapper<T> rowMapper) throws SQLException {
        List<T> results = this.query(this.getPreparedStatementCreator(sql, paramSource), rowMapper);
        return JdbcTemplate.requiredSingleResult(results);
    }
    @Override
    public <T> T queryForObject(final String sql, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) throws SQLException {
        return this.queryForObject(sql, new InnerMapSqlParameterSource(paramMap), rowMapper);
    }
    @Override
    public <T> T queryForObject(final String sql, final Class<T> requiredType) throws SQLException {
        return this.queryForObject(sql, this.getBeanPropertyRowMapper(requiredType));
    }
    @Override
    public <T> T queryForObject(final String sql, final Class<T> requiredType, final Object... args) throws SQLException {
        return this.queryForObject(sql, args, this.getBeanPropertyRowMapper(requiredType));
    }
    @Override
    public <T> T queryForObject(final String sql, final Object[] args, final Class<T> requiredType) throws SQLException {
        return this.queryForObject(sql, args, this.getBeanPropertyRowMapper(requiredType));
    }
    @Override
    public <T> T queryForObject(final String sql, final SqlParameterSource paramSource, final Class<T> requiredType) throws SQLException {
        return this.queryForObject(sql, paramSource, this.getBeanPropertyRowMapper(requiredType));
    }
    @Override
    public <T> T queryForObject(final String sql, final Map<String, ?> paramMap, final Class<T> requiredType) throws SQLException {
        return this.queryForObject(sql, paramMap, this.getBeanPropertyRowMapper(requiredType));
    }
    //
    //
    //
    @Override
    public long queryForLong(final String sql) throws SQLException {
        Number number = this.queryForObject(sql, this.getSingleColumnRowMapper(Long.class));
        return number != null ? number.longValue() : 0;
    }
    @Override
    public long queryForLong(final String sql, final Object... args) throws SQLException {
        Number number = this.queryForObject(sql, args, this.getSingleColumnRowMapper(Long.class));
        return number != null ? number.longValue() : 0;
    }
    @Override
    public long queryForLong(final String sql, final SqlParameterSource paramSource) throws SQLException {
        Number number = this.queryForObject(sql, paramSource, this.getSingleColumnRowMapper(Number.class));
        return number != null ? number.longValue() : 0;
    }
    @Override
    public long queryForLong(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForLong(sql, new InnerMapSqlParameterSource(paramMap));
    }
    @Override
    public int queryForInt(final String sql) throws SQLException {
        Number number = this.queryForObject(sql, this.getSingleColumnRowMapper(Integer.class));
        return number != null ? number.intValue() : 0;
    }
    @Override
    public int queryForInt(final String sql, final Object... args) throws SQLException {
        Number number = this.queryForObject(sql, args, this.getSingleColumnRowMapper(Integer.class));
        return number != null ? number.intValue() : 0;
    }
    @Override
    public int queryForInt(final String sql, final SqlParameterSource paramSource) throws SQLException {
        Number number = this.queryForObject(sql, paramSource, this.getSingleColumnRowMapper(Number.class));
        return number != null ? number.intValue() : 0;
    }
    @Override
    public int queryForInt(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForInt(sql, new InnerMapSqlParameterSource(paramMap));
    }
    //
    //
    //
    @Override
    public Map<String, Object> queryForMap(final String sql) throws SQLException {
        return this.queryForObject(sql, this.getColumnMapRowMapper());
    }
    @Override
    public Map<String, Object> queryForMap(final String sql, final Object... args) throws SQLException {
        return this.queryForObject(sql, args, this.getColumnMapRowMapper());
    }
    @Override
    public Map<String, Object> queryForMap(final String sql, final SqlParameterSource paramSource) throws SQLException {
        return this.queryForObject(sql, paramSource, this.getColumnMapRowMapper());
    }
    @Override
    public Map<String, Object> queryForMap(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForObject(sql, paramMap, this.getColumnMapRowMapper());
    }
    @Override
    public List<Map<String, Object>> queryForList(final String sql) throws SQLException {
        return this.query(sql, this.getColumnMapRowMapper());
    }
    @Override
    public List<Map<String, Object>> queryForList(final String sql, final Object... args) throws SQLException {
        return this.query(sql, args, this.getColumnMapRowMapper());
    }
    @Override
    public List<Map<String, Object>> queryForList(final String sql, final SqlParameterSource paramSource) throws SQLException {
        return this.query(sql, paramSource, this.getColumnMapRowMapper());
    }
    @Override
    public List<Map<String, Object>> queryForList(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForList(sql, new InnerMapSqlParameterSource(paramMap));
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
        return this.execute(psc, new PreparedStatementCallback<Integer>() {
            @Override
            public Integer doInPreparedStatement(final PreparedStatement ps) throws SQLException {
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
    @Override
    public int update(final PreparedStatementCreator psc) throws SQLException {
        return this.update(psc, (PreparedStatementSetter) null);
    }
    @Override
    public int update(final String sql) throws SQLException {
        Hasor.assertIsNotNull(sql, "SQL must not be null");
        Hasor.logDebug("Executing SQL update [%s]", sql);
        //
        class UpdateStatementCallback implements StatementCallback<Integer>, SqlProvider {
            @Override
            public Integer doInStatement(final Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                Hasor.logDebug("SQL update affected %s rows.", rows);
                return rows;
            }
            @Override
            public String getSql() {
                return sql;
            }
        }
        return this.execute(new UpdateStatementCallback());
    }
    @Override
    public int update(final String sql, final PreparedStatementSetter pss) throws SQLException {
        return this.update(new SimplePreparedStatementCreator(sql), pss);
    }
    @Override
    public int update(final String sql, final Object... args) throws SQLException {
        return this.update(sql, this.newArgPreparedStatementSetter(args));
    }
    @Override
    public int update(final String sql, final SqlParameterSource paramSource) throws SQLException {
        return this.update(this.getPreparedStatementCreator(sql, paramSource));
    }
    @Override
    public int update(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.update(this.getPreparedStatementCreator(sql, new InnerMapSqlParameterSource(paramMap)));
    }
    //
    //
    //
    @Override
    public int[] batchUpdate(final String[] sql) throws SQLException {
        if (ArrayUtils.isEmpty(sql))
            throw new NullPointerException(sql + "SQL array must not be empty");
        Hasor.logDebug("Executing SQL batch update of %s statements", sql.length);
        //
        class BatchUpdateStatementCallback implements StatementCallback<int[]>, SqlProvider {
            private String currSql;
            @Override
            public int[] doInStatement(final Statement stmt) throws SQLException {
                DatabaseMetaData dbmd = stmt.getConnection().getMetaData();
                int[] rowsAffected = new int[sql.length];
                if (dbmd.supportsBatchUpdates()) {
                    /*连接支持批处理*/
                    for (String sqlStmt : sql) {
                        this.currSql = sqlStmt;
                        stmt.addBatch(sqlStmt);
                    }
                    rowsAffected = stmt.executeBatch();
                } else
                    /*连接不支持批处理*/
                    for (int i = 0; i < sql.length; i++) {
                        this.currSql = sql[i];
                        if (!stmt.execute(sql[i]))
                            rowsAffected[i] = stmt.getUpdateCount();
                        else
                            throw new SQLException("Invalid batch SQL statement: " + sql[i]);
                    }
                return rowsAffected;
            }
            @Override
            public String getSql() {
                return this.currSql;
            }
        }
        return this.execute(new BatchUpdateStatementCallback());
    }
    @Override
    public int[] batchUpdate(final String sql, final Map<String, ?>[] batchValues) throws SQLException {
        SqlParameterSource[] batchArgs = new SqlParameterSource[batchValues.length];
        int i = 0;
        for (Map<String, ?> values : batchValues) {
            batchArgs[i] = new InnerMapSqlParameterSource(values);
            i++;
        }
        return this.batchUpdate(sql, batchArgs);
    }
    @Override
    public int[] batchUpdate(final String sql, final SqlParameterSource[] batchArgs) throws SQLException {
        if (batchArgs.length <= 0)
            return new int[] { 0 };
        return this.batchUpdate(sql, new SqlParameterSourceBatchPreparedStatementSetter(sql, batchArgs));
    }
    @Override
    public int[] batchUpdate(String sql, final BatchPreparedStatementSetter pss) throws SQLException {
        Hasor.logDebug("Executing SQL batch update [%s].", sql);
        final ParsedSql parsedSql = ParsedSql.getParsedSql(sql);
        sql = ParsedSql.buildSql(parsedSql, null);
        //
        return this.execute(sql, new PreparedStatementCallback<int[]>() {
            @Override
            public int[] doInPreparedStatement(final PreparedStatement ps) throws SQLException {
                try {
                    int batchSize = pss.getBatchSize();
                    InterruptibleBatchPreparedStatementSetter ipss = pss instanceof InterruptibleBatchPreparedStatementSetter ? (InterruptibleBatchPreparedStatementSetter) pss : null;
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
    //
    //
    //
    /** Create a new RowMapper for reading columns as key-value pairs. */
    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        return new ColumnMapRowMapper() {
            @Override
            protected Map<String, Object> createColumnMap(final int columnCount) {
                return JdbcTemplate.this.createResultsMap();
            }
        };
    }
    /** Create a new RowMapper for reading columns as Bean pairs. */
    protected <T> RowMapper<T> getBeanPropertyRowMapper(final Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType != null, "requiredType is null.");
        if (Map.class.isAssignableFrom(requiredType))
            return (RowMapper<T>) this.getColumnMapRowMapper();
        //
        if (requiredType.isPrimitive() || Number.class.isAssignableFrom(requiredType) || String.class.isAssignableFrom(requiredType))
            return this.getSingleColumnRowMapper(requiredType);
        //
        return new BeanPropertyRowMapper<T>(requiredType) {
            @Override
            public boolean isCaseInsensitive() {
                return JdbcTemplate.this.isResultsCaseInsensitive();
            }
        };
    }
    /** Create a new RowMapper for reading result objects from a single column.*/
    protected <T> RowMapper<T> getSingleColumnRowMapper(final Class<T> requiredType) {
        return new SingleColumnRowMapper<T>(requiredType);
    }
    //
    //
    /**创建用于保存结果集的数据Map。*/
    protected Map<String, Object> createResultsMap() {
        if (!this.isResultsCaseInsensitive())
            return new LinkedCaseInsensitiveMap<Object>();
        else
            return new LinkedHashMap<String, Object>();
    }
    /** Create a new PreparedStatementSetter.*/
    protected PreparedStatementSetter newArgPreparedStatementSetter(final Object[] args) throws SQLException {
        return new InnerArgPreparedStatementSetter(args);
    }
    /**对Statement的属性进行设置。设置 JDBC Statement 对象的 fetchSize、maxRows、Timeout等参数。*/
    protected void applyStatementSettings(final Statement stmt) throws SQLException {
        int fetchSize = this.getFetchSize();
        if (fetchSize > 0)
            stmt.setFetchSize(fetchSize);
        int maxRows = this.getMaxRows();
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
    protected PreparedStatementCreator getPreparedStatementCreator(final String sql, final SqlParameterSource paramSource) {
        return new MapPreparedStatementCreator(sql, paramSource);
    }
    //
    /**处理潜在的 SQL 警告。当要求不忽略 SQL 警告时，检测到 SQL 警告抛出 SQL 异常。*/
    private void handleWarnings(final Statement stmt) throws SQLException {
        if (this.isIgnoreWarnings()) {
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
    /**获取SQL文本*/
    private static String getSql(final Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider)
            return ((SqlProvider) sqlProvider).getSql();
        else
            return null;
    }
    //
    /**至返回结果集中的一条数据。*/
    private static <T> T requiredSingleResult(final Collection<T> results) throws SQLException {
        int size = results != null ? results.size() : 0;
        if (size == 0)
            throw new SQLException("Empty Result");
        if (results.size() > 1)
            throw new SQLException("Incorrect column count: expected " + 1 + ", actual " + size);
        return results.iterator().next();
    }
    /**获取与本地线程绑定的数据库连接，JDBC 框架会维护这个连接的事务。开发者不必关心该连接的事务管理，以及资源释放操作。*/
    private Connection newProxyConnection(final Connection target, final DataSource targetSource) {
        Hasor.assertIsNotNull(target, "Connection is null.");
        CloseSuppressingInvocationHandler handler = new CloseSuppressingInvocationHandler(target, targetSource);
        return (Connection) Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[] { ConnectionProxy.class }, handler);
    }
    //
    //
    /**获取SQL*/
    protected static interface SqlProvider {
        public String getSql();
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
            if (method.getName().equals("getTargetConnection"))
                // Handle getTargetConnection method: return underlying Connection.
                return this.target;
            else if (method.getName().equals("getTargetSource"))
                // Handle getTargetConnection method: return underlying DataSource.
                return this.targetSource;
            else if (method.getName().equals("equals"))
                // Only consider equal when proxies are identical.
                return proxy == args[0];
            else if (method.getName().equals("hashCode"))
                // Use hashCode of PersistenceManager proxy.
                return System.identityHashCode(proxy);
            else if (method.getName().equals("close"))
                return null;
            // Invoke method on target Connection.
            try {
                Object retVal = method.invoke(this.target, args);
                // If return value is a JDBC Statement, apply statement settings (fetch size, max rows, transaction timeout).
                if (retVal instanceof Statement)
                    JdbcTemplate.this.applyStatementSettings((Statement) retVal);
                return retVal;
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
    /**接口 {@link PreparedStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link PreparedStatement}对象。*/
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, JdbcTemplate.SqlProvider {
        private final String sql;
        public SimplePreparedStatementCreator(final String sql) {
            Hasor.assertIsNotNull(sql, "SQL must not be null");
            this.sql = sql;
        }
        @Override
        public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
            return con.prepareStatement(this.sql);
        }
        @Override
        public String getSql() {
            return this.sql;
        }
    }
    /**接口 {@link CallableStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link CallableStatement}对象。*/
    private static class SimpleCallableStatementCreator implements CallableStatementCreator, JdbcTemplate.SqlProvider {
        private final String callString;
        public SimpleCallableStatementCreator(final String callString) {
            Hasor.assertIsNotNull(callString, "Call string must not be null");
            this.callString = callString;
        }
        @Override
        public CallableStatement createCallableStatement(final Connection con) throws SQLException {
            return con.prepareCall(this.callString);
        }
        @Override
        public String getSql() {
            return this.callString;
        }
    }
    /**使用 {@link RowCallbackHandler} 类型循环处理每一行记录的适配器*/
    private static class RowCallbackHandlerResultSetExtractor implements ResultSetExtractor<Object> {
        private final RowCallbackHandler rch;
        public RowCallbackHandlerResultSetExtractor(final RowCallbackHandler rch) {
            this.rch = rch;
        }
        @Override
        public Object extractData(final ResultSet rs) throws SQLException {
            while (rs.next())
                this.rch.processRow(rs);
            return null;
        }
    }
    /**接口 {@link CallableStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link CallableStatement}对象。*/
    private static class MapPreparedStatementCreator implements PreparedStatementCreator, ParameterDisposer, JdbcTemplate.SqlProvider {
        private ParsedSql          parsedSql   = null;
        private SqlParameterSource paramSource = null;
        //
        public MapPreparedStatementCreator(final String originalSql, final SqlParameterSource paramSource) {
            Hasor.assertIsNotNull(originalSql, "SQL must not be null");
            this.parsedSql = ParsedSql.getParsedSql(originalSql);
            this.paramSource = paramSource;
        }
        //
        @Override
        public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
            //1.根据参数信息生成最终会执行的SQL语句.
            String sqlToUse = ParsedSql.buildSql(this.parsedSql, this.paramSource);
            //2.确定参数对象
            Object[] paramArray = ParsedSql.buildSqlValues(this.parsedSql, this.paramSource);
            //3.创建PreparedStatement对象，并设置参数
            PreparedStatement statement = con.prepareStatement(sqlToUse);
            for (int i = 0; i < paramArray.length; i++)
                InnerStatementSetterUtils.setParameterValue(statement, i + 1, paramArray[i]);
            InnerStatementSetterUtils.cleanupParameters(paramArray);
            return statement;
        }
        @Override
        public String getSql() {
            return this.parsedSql.getOriginalSql();
        }
        @Override
        public void cleanupParameters() {
            if (this.paramSource instanceof ParameterDisposer)
                ((ParameterDisposer) this.paramSource).cleanupParameters();
        }
    }
    /**接口 {@link BatchPreparedStatementSetter} 的简单实现，目的是设置批量操作*/
    private static class SqlParameterSourceBatchPreparedStatementSetter implements BatchPreparedStatementSetter, ParameterDisposer {
        private ParsedSql            parsedSql = null;
        private SqlParameterSource[] batchArgs = null;
        public SqlParameterSourceBatchPreparedStatementSetter(final String sql, final SqlParameterSource[] batchArgs) {
            this.parsedSql = ParsedSql.getParsedSql(sql);
            this.batchArgs = batchArgs;
        }
        //        public String preparedSQL(int i) throws SQLException {
        //            SqlParameterSource paramSource = this.batchArgs[i];
        //            //1.根据参数信息生成最终会执行的SQL语句.
        //            String sqlText = ParsedSql.buildSql(this.parsedSql, paramSource);
        //            return sqlText;
        //        }
        @Override
        public void setValues(final PreparedStatement ps, final int index) throws SQLException {
            SqlParameterSource paramSource = this.batchArgs[index];
            //1.确定参数对象
            Object[] sqlValue = ParsedSql.buildSqlValues(this.parsedSql, paramSource);
            //2.设置参数
            int sqlColIndx = 1;
            for (Object element : sqlValue)
                InnerStatementSetterUtils.setParameterValue(ps, sqlColIndx++, element);
        }
        @Override
        public int getBatchSize() {
            return this.batchArgs.length;
        }
        @Override
        public void cleanupParameters() {
            for (SqlParameterSource batchItem : this.batchArgs)
                if (batchItem instanceof ParameterDisposer)
                    ((ParameterDisposer) batchItem).cleanupParameters();
        }
    }
}