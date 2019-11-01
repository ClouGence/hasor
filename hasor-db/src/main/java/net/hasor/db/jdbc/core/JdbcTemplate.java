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
import net.hasor.db.jdbc.*;
import net.hasor.db.jdbc.mapper.BeanPropertyRowMapper;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.jdbc.mapper.SingleColumnRowMapper;
import net.hasor.db.jdbc.paramer.MapSqlParameterSource;
import net.hasor.db.jdbc.result.LinkedCaseInsensitiveMap;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.io.IOUtils;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.*;

/**
 * 数据库操作模板方法。
 * @version : 2013-10-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class JdbcTemplate extends JdbcConnection implements JdbcOperations {
    /*是否忽略出现的 SQL 警告*/
    private boolean ignoreWarnings         = true;
    /*当JDBC 结果集中如出现相同的列名仅仅大小写不同时。是否保留大小写列名敏感。
     * 如果为 true 表示敏感，并且结果集Map中保留两个记录。如果为 false 则表示不敏感，如出现冲突列名后者将会覆盖前者。*/
    private boolean resultsCaseInsensitive = false;

    /**
     * Construct a new JdbcTemplate for bean usage.
     * <p>Note: The DataSource has to be set before using the instance.
     * @see #setDataSource
     */
    public JdbcTemplate() {
        super();
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public JdbcTemplate(final DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Construct a new JdbcTemplate, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     */
    public JdbcTemplate(final Connection conn) {
        super(conn);
    }

    public boolean isIgnoreWarnings() {
        return this.ignoreWarnings;
    }

    public void setIgnoreWarnings(final boolean ignoreWarnings) {
        this.ignoreWarnings = ignoreWarnings;
    }

    public boolean isResultsCaseInsensitive() {
        return this.resultsCaseInsensitive;
    }

    public void setResultsCaseInsensitive(final boolean resultsCaseInsensitive) {
        this.resultsCaseInsensitive = resultsCaseInsensitive;
    }

    public void loadSQL(final String sqlResource) throws IOException, SQLException {
        this.loadSQL("UTF-8", sqlResource);
    }

    public void loadSQL(final String charsetName, final String sqlResource) throws IOException, SQLException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(sqlResource);
        if (inStream == null) {
            throw new IOException("can't find :" + sqlResource);
        }
        InputStreamReader reader = new InputStreamReader(inStream, Charset.forName(charsetName));
        this.loadSQL(reader);
    }

    public void loadSQL(final Reader sqlReader) throws IOException, SQLException {
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(sqlReader, outWriter);
        this.execute(outWriter.toString());
    }

    @Override
    public <T> T execute(final StatementCallback<T> action) throws SQLException {
        Objects.requireNonNull(action, "Callback object must not be null");
        return this.execute((ConnectionCallback<T>) con -> {
            String stmtSQL = "";
            try (Statement stmt = con.createStatement()) {
                JdbcTemplate.this.applyStatementSettings(stmt);
                stmtSQL = stmt.toString();
                T result = action.doInStatement(stmt);
                JdbcTemplate.this.handleWarnings(stmt);
                return result;
            } catch (SQLException ex) {
                logger.error(stmtSQL, ex);
                throw ex;
            }
        });
    }

    @Override
    public <T> T execute(final PreparedStatementCreator psc, final PreparedStatementCallback<T> action) throws SQLException {
        Objects.requireNonNull(psc, "PreparedStatementCreator must not be null");
        Objects.requireNonNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = JdbcTemplate.getSql(psc);
            logger.debug("Executing prepared SQL statement " + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        return this.execute((ConnectionCallback<T>) con -> {
            String stmtSQL = "";
            try (PreparedStatement ps = psc.createPreparedStatement(con)) {
                JdbcTemplate.this.applyStatementSettings(ps);
                stmtSQL = ps.toString();
                T result = action.doInPreparedStatement(ps);
                JdbcTemplate.this.handleWarnings(ps);
                return result;
            } catch (SQLException ex) {
                logger.error(stmtSQL, ex);
                throw ex;
            } finally {
                if (psc instanceof ParameterDisposer) {
                    ((ParameterDisposer) psc).cleanupParameters();
                }
            }
        });
    }

    @Override
    public <T> T execute(final CallableStatementCreator csc, final CallableStatementCallback<T> action) throws SQLException {
        Objects.requireNonNull(csc, "CallableStatementCreator must not be null");
        Objects.requireNonNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = JdbcTemplate.getSql(csc);
            logger.debug("Calling stored procedure" + (sql != null ? " [" + sql + "]" : ""));
        }
        //
        return this.execute((ConnectionCallback<T>) con -> {
            try (CallableStatement cs = csc.createCallableStatement(con)) {
                JdbcTemplate.this.applyStatementSettings(cs);
                T result = action.doInCallableStatement(cs);
                JdbcTemplate.this.handleWarnings(cs);
                return result;
            } catch (SQLException ex) {
                String sqlString = JdbcTemplate.getSql(action);
                throw new SQLException("CallableStatementCallback SQL :" + sqlString, ex);
            } finally {
                if (csc instanceof ParameterDisposer) {
                    ((ParameterDisposer) csc).cleanupParameters();
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
        return this.execute(this.getPreparedStatementCreator(sql, new MapSqlParameterSource(paramMap)), action);
    }

    @Override
    public boolean execute(final String sql) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL statement [{}].", sql);
        }
        class ExecuteStatementCallback implements StatementCallback<Boolean>, SqlProvider {
            @Override
            public Boolean doInStatement(final Statement stmt) throws SQLException {
                return stmt.execute(sql);
            }

            @Override
            public String getSql() {
                return sql;
            }
        }
        return this.execute(new ExecuteStatementCallback());
    }

    /***/
    public <T> T query(final PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws SQLException {
        Objects.requireNonNull(rse, "ResultSetExtractor must not be null.");
        if (logger.isDebugEnabled()) {
            logger.debug("executing prepared SQL query");
        }
        return this.execute(psc, ps -> {
            if (pss != null) {
                pss.setValues(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rse.extractData(rs);
            } finally {
                if (pss instanceof ParameterDisposer) {
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
        Objects.requireNonNull(sql, "SQL must not be null.");
        Objects.requireNonNull(rse, "ResultSetExtractor must not be null.");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL query [{}].", sql);
        }
        class QueryStatementCallback implements StatementCallback<T>, SqlProvider {
            @Override
            public T doInStatement(final Statement stmt) throws SQLException {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    return rse.extractData(rs);
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
        return this.query(this.getPreparedStatementCreator(sql, new MapSqlParameterSource(paramMap)), rse);
    }

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
        this.query(this.getPreparedStatementCreator(sql, new MapSqlParameterSource(paramMap)), rch);
    }

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
        return this.query(this.getPreparedStatementCreator(sql, new MapSqlParameterSource(paramMap)), rowMapper);
    }

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

    @Override
    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper) throws SQLException {
        return JdbcTemplate.requiredSingleResult(this.query(sql, rowMapper));
    }

    @Override
    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) throws SQLException {
        return JdbcTemplate.requiredSingleResult(this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1)));
    }

    @Override
    public <T> T queryForObject(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws SQLException {
        return JdbcTemplate.requiredSingleResult(this.query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1)));
    }

    @Override
    public <T> T queryForObject(final String sql, final SqlParameterSource paramSource, final RowMapper<T> rowMapper) throws SQLException {
        return JdbcTemplate.requiredSingleResult(this.query(this.getPreparedStatementCreator(sql, paramSource), rowMapper));
    }

    @Override
    public <T> T queryForObject(final String sql, final Map<String, ?> paramMap, final RowMapper<T> rowMapper) throws SQLException {
        return this.queryForObject(sql, new MapSqlParameterSource(paramMap), rowMapper);
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
        return this.queryForLong(sql, new MapSqlParameterSource(paramMap));
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
        return this.queryForInt(sql, new MapSqlParameterSource(paramMap));
    }

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
        return this.queryForList(sql, new MapSqlParameterSource(paramMap));
    }
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

    /***/
    public int executeUpdate(final PreparedStatementCreator psc, final PreparedStatementSetter pss) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("executing prepared SQL update");
        }
        return this.execute(psc, ps -> {
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
                int rows = ps.executeUpdate();
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL update affected {} rows", rows);
                }
                return rows;
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });
    }

    @Override
    public int executeUpdate(final PreparedStatementCreator psc) throws SQLException {
        return this.executeUpdate(psc, null);
    }

    @Override
    public int executeUpdate(final String sql) throws SQLException {
        Objects.requireNonNull(sql, "SQL must not be null");
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL update [{}]", sql);
        }
        //
        class UpdateStatementCallback implements StatementCallback<Integer>, SqlProvider {
            @Override
            public Integer doInStatement(final Statement stmt) throws SQLException {
                int rows = stmt.executeUpdate(sql);
                if (logger.isDebugEnabled()) {
                    logger.debug("SQL update affected {} rows.", rows);
                }
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
    public int executeUpdate(final String sql, final PreparedStatementSetter pss) throws SQLException {
        return this.executeUpdate(new SimplePreparedStatementCreator(sql), pss);
    }

    @Override
    public int executeUpdate(final String sql, final Object... args) throws SQLException {
        return this.executeUpdate(sql, this.newArgPreparedStatementSetter(args));
    }

    @Override
    public int executeUpdate(final String sql, final SqlParameterSource paramSource) throws SQLException {
        return this.executeUpdate(this.getPreparedStatementCreator(sql, paramSource));
    }

    @Override
    public int executeUpdate(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.executeUpdate(this.getPreparedStatementCreator(sql, new MapSqlParameterSource(paramMap)));
    }

    @Override
    public int[] executeBatch(final String[] sql) throws SQLException {
        if (sql == null || sql.length == 0) {
            throw new NullPointerException("SQL array must not be empty");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update of {} statements", sql.length);
        }
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
                } else {
                    /*连接不支持批处理*/
                    for (int i = 0; i < sql.length; i++) {
                        this.currSql = sql[i];
                        if (!stmt.execute(sql[i])) {
                            rowsAffected[i] = stmt.getUpdateCount();
                        } else {
                            throw new SQLException("Invalid batch SQL statement: " + sql[i]);
                        }
                    }
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
    public int[] executeBatch(final String sql, final Map<String, ?>[] batchValues) throws SQLException {
        SqlParameterSource[] batchArgs = new SqlParameterSource[batchValues.length];
        int i = 0;
        for (Map<String, ?> values : batchValues) {
            batchArgs[i] = new MapSqlParameterSource(values);
            i++;
        }
        return this.executeBatch(sql, batchArgs);
    }

    @Override
    public int[] executeBatch(final String sql, final SqlParameterSource[] batchArgs) throws SQLException {
        if (batchArgs.length <= 0) {
            return new int[] { 0 };
        }
        return this.executeBatch(sql, new SqlParameterSourceBatchPreparedStatementSetter(sql, batchArgs));
    }

    @Override
    public int[] executeBatch(String sql, final BatchPreparedStatementSetter pss) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update [{}].", sql);
        }
        final ParsedSql parsedSql = getParsedSql(sql);
        sql = ParsedSql.buildSql(parsedSql, null);
        //
        return this.execute(sql, (PreparedStatementCallback<int[]>) ps -> {
            try {
                int batchSize = pss.getBatchSize();
                InterruptibleBatchPreparedStatementSetter ipss = pss instanceof InterruptibleBatchPreparedStatementSetter ? (InterruptibleBatchPreparedStatementSetter) pss : null;
                DatabaseMetaData dbMetaData = ps.getConnection().getMetaData();
                if (dbMetaData.supportsBatchUpdates()) {
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
                        ps.addBatch();
                    }
                    return ps.executeBatch();
                } else {
                    List<Integer> rowsAffected = new ArrayList<>();
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (ipss != null && ipss.isBatchExhausted(i)) {
                            break;
                        }
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
        });
    }

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
        Objects.requireNonNull(requiredType, "requiredType is null.");
        if (Map.class.isAssignableFrom(requiredType)) {
            return (RowMapper<T>) this.getColumnMapRowMapper();
        }
        //
        if (requiredType.isPrimitive() || Number.class.isAssignableFrom(requiredType) || String.class.isAssignableFrom(requiredType)) {
            return this.getSingleColumnRowMapper(requiredType);
        }
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

    /**创建用于保存结果集的数据Map。*/
    protected Map<String, Object> createResultsMap() {
        if (!this.isResultsCaseInsensitive()) {
            return new LinkedCaseInsensitiveMap<>();
        } else {
            return new LinkedHashMap<>();
        }
    }

    /** Create a new PreparedStatementSetter.*/
    protected PreparedStatementSetter newArgPreparedStatementSetter(final Object[] args) throws SQLException {
        return new InnerArgPreparedStatementSetter(args);
    }

    /**
     * Build a PreparedStatementCreator based on the given SQL and named parameters.
     * <p>Note: Not used for the <code>update</code> variant with generated key handling.
     */
    protected PreparedStatementCreator getPreparedStatementCreator(final String sql, final SqlParameterSource paramSource) {
        return new MapPreparedStatementCreator(sql, paramSource);
    }

    /* Map of original SQL String to ParsedSql representation */
    private final Map<String, ParsedSql> parsedSqlCache = new HashMap<>();

    /*Obtain a parsed representation of the given SQL statement.*/
    protected ParsedSql getParsedSql(String originalSql) {
        synchronized (this.parsedSqlCache) {
            ParsedSql parsedSql = this.parsedSqlCache.get(originalSql);
            if (parsedSql == null) {
                parsedSql = ParsedSql.getParsedSql(originalSql);
                this.parsedSqlCache.put(originalSql, parsedSql);
            }
            return parsedSql;
        }
    }
    //

    /**处理潜在的 SQL 警告。当要求不忽略 SQL 警告时，检测到 SQL 警告抛出 SQL 异常。*/
    private void handleWarnings(final Statement stmt) throws SQLException {
        if (this.isIgnoreWarnings()) {
            if (logger.isDebugEnabled()) {
                SQLWarning warningToLog = stmt.getWarnings();
                while (warningToLog != null) {
                    logger.debug("SQLWarning ignored: SQL state '{}', error code '{}', message [{}].",//
                            warningToLog.getSQLState(), warningToLog.getErrorCode(), warningToLog.getMessage());
                    warningToLog = warningToLog.getNextWarning();
                }
            }
        } else {
            SQLWarning warning = stmt.getWarnings();
            if (warning != null) {
                throw new SQLException("Warning not ignored", warning);
            }
        }
    }

    /**获取SQL文本*/
    private static String getSql(final Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    /**至返回结果集中的一条数据。*/
    private static <T> T requiredSingleResult(final Collection<T> results) throws SQLException {
        if (results == null || results.isEmpty()) {
            return null;
        }
        int size = results.size();
        if (size > 1) {
            throw new SQLException("Incorrect column count: expected 1, actual " + size);
        }
        return results.iterator().next();
    }

    /**获取SQL*/
    protected static interface SqlProvider {
        public String getSql();
    }

    /**接口 {@link PreparedStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link PreparedStatement}对象。*/
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, JdbcTemplate.SqlProvider {
        private final String sql;

        public SimplePreparedStatementCreator(final String sql) {
            Objects.requireNonNull(sql, "SQL must not be null");
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
            Objects.requireNonNull(callString, "Call string must not be null");
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
            while (rs.next()) {
                this.rch.processRow(rs);
            }
            return null;
        }
    }

    /**接口 {@link CallableStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link CallableStatement}对象。*/
    private class MapPreparedStatementCreator implements PreparedStatementCreator, ParameterDisposer, JdbcTemplate.SqlProvider {
        private ParsedSql          parsedSql   = null;
        private SqlParameterSource paramSource = null;

        public MapPreparedStatementCreator(final String originalSql, final SqlParameterSource paramSource) {
            Objects.requireNonNull(originalSql, "SQL must not be null");
            this.parsedSql = getParsedSql(originalSql);
            this.paramSource = paramSource;
        }

        @Override
        public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
            //1.根据参数信息生成最终会执行的SQL语句.
            String sqlToUse = ParsedSql.buildSql(this.parsedSql, this.paramSource);
            //2.确定参数对象
            Object[] paramArray = ParsedSql.buildSqlValues(this.parsedSql, this.paramSource);
            //3.创建PreparedStatement对象，并设置参数
            PreparedStatement statement = con.prepareStatement(sqlToUse);
            for (int i = 0; i < paramArray.length; i++) {
                InnerStatementSetterUtils.setParameterValue(statement, i + 1, paramArray[i]);
            }
            InnerStatementSetterUtils.cleanupParameters(paramArray);
            return statement;
        }

        @Override
        public String getSql() {
            return this.parsedSql.getOriginalSql();
        }

        @Override
        public void cleanupParameters() {
            if (this.paramSource instanceof ParameterDisposer) {
                ((ParameterDisposer) this.paramSource).cleanupParameters();
            }
        }
    }

    /**接口 {@link BatchPreparedStatementSetter} 的简单实现，目的是设置批量操作*/
    private class SqlParameterSourceBatchPreparedStatementSetter implements BatchPreparedStatementSetter, ParameterDisposer {
        private ParsedSql            parsedSql = null;
        private SqlParameterSource[] batchArgs = null;

        public SqlParameterSourceBatchPreparedStatementSetter(final String sql, final SqlParameterSource[] batchArgs) {
            this.parsedSql = getParsedSql(sql);
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
            for (Object element : sqlValue) {
                InnerStatementSetterUtils.setParameterValue(ps, sqlColIndx++, element);
            }
        }

        @Override
        public int getBatchSize() {
            return this.batchArgs.length;
        }

        @Override
        public void cleanupParameters() {
            for (SqlParameterSource batchItem : this.batchArgs) {
                if (batchItem instanceof ParameterDisposer) {
                    ((ParameterDisposer) batchItem).cleanupParameters();
                }
            }
        }
    }
}