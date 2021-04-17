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
import net.hasor.db.jdbc.SqlParameter.InSqlParameter;
import net.hasor.db.jdbc.SqlParameter.OutSqlParameter;
import net.hasor.db.jdbc.SqlParameter.ReturnSqlParameter;
import net.hasor.db.jdbc.extractor.ColumnMapResultSetExtractor;
import net.hasor.db.jdbc.extractor.RowCallbackHandlerResultSetExtractor;
import net.hasor.db.jdbc.extractor.RowMapperResultSetExtractor;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.jdbc.mapper.MappingRowMapper;
import net.hasor.db.jdbc.mapper.SingleColumnRowMapper;
import net.hasor.db.jdbc.paramer.MapSqlParameterSource;
import net.hasor.db.mapping.MappingRegistry;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.ResourcesUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.io.IOUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Spring-jdbc based and reimplements
 * @version : 2013-10-12
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Thomas Risberg
 * @author 赵永春 (zyc@byshell.org)
 * @see PreparedStatementCreator
 * @see PreparedStatementSetter
 * @see CallableStatementCreator
 * @see PreparedStatementCallback
 * @see CallableStatementCallback
 * @see ResultSetExtractor
 * @see RowCallbackHandler
 * @see RowMapper
 */
public class JdbcTemplate extends JdbcConnection implements JdbcOperations {
    private static final Logger          logger                 = LoggerFactory.getLogger(JdbcTemplate.class);
    /*当JDBC 结果集中如出现相同的列名仅仅大小写不同时。是否保留大小写列名敏感。
     * 如果为 true 表示不敏感，并且结果集Map中保留两个记录。如果为 false 则表示敏感，如出现冲突列名后者将会覆盖前者。*/
    private              boolean         resultsCaseInsensitive = true;
    private final        MappingRegistry mappingRegistry;

    /**
     * Construct a new JdbcTemplate for bean usage.
     * <p>Note: The DataSource has to be set before using the instance.
     * @see #setDataSource
     */
    public JdbcTemplate() {
        super();
        this.mappingRegistry = MappingRegistry.DEFAULT;
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     */
    public JdbcTemplate(final DataSource dataSource) {
        this(dataSource, MappingRegistry.DEFAULT);
    }

    /**
     * Construct a new JdbcTemplate, given a DataSource to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param dataSource the JDBC DataSource to obtain connections from
     * @param mappingRegistry the MappingRegistry
     */
    public JdbcTemplate(final DataSource dataSource, MappingRegistry mappingRegistry) {
        super(dataSource);
        this.mappingRegistry = Objects.requireNonNull(mappingRegistry, "mappingRegistry is null.");
    }

    /**
     * Construct a new JdbcTemplate, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     */
    public JdbcTemplate(final Connection conn) {
        this(conn, MappingRegistry.DEFAULT);
    }

    /**
     * Construct a new JdbcTemplate, given a Connection to obtain connections from.
     * <p>Note: This will not trigger initialization of the exception translator.
     * @param conn the JDBC Connection
     * @param mappingRegistry the MappingRegistry
     */
    public JdbcTemplate(final Connection conn, MappingRegistry mappingRegistry) {
        super(conn);
        this.mappingRegistry = Objects.requireNonNull(mappingRegistry, "mappingRegistry is null.");
    }

    public boolean isResultsCaseInsensitive() {
        return this.resultsCaseInsensitive;
    }

    public void setResultsCaseInsensitive(final boolean resultsCaseInsensitive) {
        this.resultsCaseInsensitive = resultsCaseInsensitive;
    }

    public MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    public void loadSQL(final String sqlResource) throws IOException, SQLException {
        this.loadSplitSQL(null, StandardCharsets.UTF_8, sqlResource);
    }

    public void loadSQL(final Charset charset, final String sqlResource) throws IOException, SQLException {
        this.loadSplitSQL(null, charset, sqlResource);
    }

    public void loadSQL(final Reader sqlReader) throws IOException, SQLException {
        this.loadSplitSQL(null, sqlReader);
    }

    public void loadSplitSQL(final String splitString, final String sqlResource) throws IOException, SQLException {
        this.loadSplitSQL(splitString, StandardCharsets.UTF_8, sqlResource);
    }

    public void loadSplitSQL(final String splitString, final Charset charset, final String sqlResource) throws IOException, SQLException {
        InputStream inStream = ResourcesUtils.getResourceAsStream(sqlResource);
        if (inStream == null) {
            throw new IOException("can't find resource '" + sqlResource + "'");
        }
        InputStreamReader reader = new InputStreamReader(inStream, charset);
        this.loadSplitSQL(splitString, reader);
    }

    public void loadSplitSQL(final String splitString, final Reader sqlReader) throws IOException, SQLException {
        StringWriter outWriter = new StringWriter();
        IOUtils.copy(sqlReader, outWriter);
        //
        List<String> taskList = null;
        if (StringUtils.isBlank(splitString)) {
            taskList = Collections.singletonList(outWriter.toString());
        } else {
            taskList = Arrays.asList(outWriter.toString().split(splitString));
        }
        taskList = taskList.parallelStream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        //
        for (String str : taskList) {
            this.execute(str);
        }
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
            try (PreparedStatement ps = psc.createPreparedStatement(con)) {
                JdbcTemplate.this.applyStatementSettings(ps);
                T result = action.doInPreparedStatement(ps);
                JdbcTemplate.this.handleWarnings(ps);
                return result;
            } catch (SQLException ex) {
                if (logger.isDebugEnabled()) {
                    String sql = JdbcTemplate.getSql(psc);
                    logger.error("execute SQL :" + sql, ex);
                }
                throw ex;
            } finally {
                if (psc instanceof ParameterDisposer) {
                    ((ParameterDisposer) psc).cleanupParameters();
                }
            }
        });
    }

    @Override
    public <T> T execute(final String sql, final PreparedStatementCallback<T> action) throws SQLException {
        return this.execute(new SimplePreparedStatementCreator(sql), action);
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

    @Override
    public List<Object> multipleExecute(final String sql) throws SQLException {
        return this.execute(sql, new MultipleResultExtractor());
    }

    @Override
    public List<Object> multipleExecute(final String sql, final Object... args) throws SQLException {
        PreparedStatementSetter pss = newArgPreparedStatementSetter(args);
        return this.execute(new SimplePreparedStatementCreator(sql), ps -> {
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
                return new MultipleResultExtractor().doInPreparedStatement(ps);
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
        });
    }

    @Override
    public List<Object> multipleExecute(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.multipleExecute(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public List<Object> multipleExecute(final String sql, final SqlParameterSource parameterSource) throws SQLException {
        return this.execute(this.getPreparedStatementCreator(sql, parameterSource), new MultipleResultExtractor());
    }

    @Override
    public <T> T execute(final PreparedStatementCreator psc, final PreparedStatementSetter pss, final ResultSetExtractor<T> rse) throws SQLException {
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
        return this.execute(psc, null, rse);
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
        return this.execute(new SimplePreparedStatementCreator(sql), pss, rse);
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
        return this.query(psc, new RowMapperResultSetExtractor<>(rowMapper));
    }

    @Override
    public <T> List<T> query(final String sql, final PreparedStatementSetter pss, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, pss, new RowMapperResultSetExtractor<>(rowMapper));
    }

    @Override
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper, final Object... args) throws SQLException {
        return this.query(sql, args, new RowMapperResultSetExtractor<>(rowMapper));
    }

    @Override
    public <T> List<T> query(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, args, new RowMapperResultSetExtractor<>(rowMapper));
    }

    @Override
    public <T> List<T> query(final String sql, final RowMapper<T> rowMapper) throws SQLException {
        return this.query(sql, new RowMapperResultSetExtractor<>(rowMapper));
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
        return requiredSingleResult(this.query(sql, rowMapper));
    }

    @Override
    public <T> T queryForObject(final String sql, final RowMapper<T> rowMapper, final Object... args) throws SQLException {
        return requiredSingleResult(this.query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1)));
    }

    @Override
    public <T> T queryForObject(final String sql, final Object[] args, final RowMapper<T> rowMapper) throws SQLException {
        return requiredSingleResult(this.query(sql, args, new RowMapperResultSetExtractor<>(rowMapper, 1)));
    }

    @Override
    public <T> T queryForObject(final String sql, final SqlParameterSource paramSource, final RowMapper<T> rowMapper) throws SQLException {
        return requiredSingleResult(this.query(this.getPreparedStatementCreator(sql, paramSource), rowMapper));
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
        Number number = this.queryForObject(sql, this.getSingleColumnRowMapper(long.class));
        return number != null ? number.longValue() : 0;
    }

    @Override
    public long queryForLong(final String sql, final Object... args) throws SQLException {
        Number number = this.queryForObject(sql, args, this.getSingleColumnRowMapper(long.class));
        return number != null ? number.longValue() : 0;
    }

    @Override
    public long queryForLong(final String sql, final SqlParameterSource paramSource) throws SQLException {
        Number number = this.queryForObject(sql, paramSource, this.getSingleColumnRowMapper(long.class));
        return number != null ? number.longValue() : 0;
    }

    @Override
    public long queryForLong(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForLong(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public int queryForInt(final String sql) throws SQLException {
        Number number = this.queryForObject(sql, this.getSingleColumnRowMapper(int.class));
        return number != null ? number.intValue() : 0;
    }

    @Override
    public int queryForInt(final String sql, final Object... args) throws SQLException {
        Number number = this.queryForObject(sql, args, this.getSingleColumnRowMapper(int.class));
        return number != null ? number.intValue() : 0;
    }

    @Override
    public int queryForInt(final String sql, final SqlParameterSource paramSource) throws SQLException {
        Number number = this.queryForObject(sql, paramSource, this.getSingleColumnRowMapper(int.class));
        return number != null ? number.intValue() : 0;
    }

    @Override
    public int queryForInt(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForInt(sql, new MapSqlParameterSource(paramMap));
    }

    @Override
    public String queryForString(final String sql) throws SQLException {
        return this.queryForObject(sql, String.class);
    }

    @Override
    public String queryForString(final String sql, final Object... args) throws SQLException {
        return this.queryForObject(sql, String.class, args);
    }

    @Override
    public String queryForString(final String sql, final SqlParameterSource paramSource) throws SQLException {
        return this.queryForObject(sql, paramSource, String.class);
    }

    @Override
    public String queryForString(final String sql, final Map<String, ?> paramMap) throws SQLException {
        return this.queryForObject(sql, paramMap, String.class);
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

    @Override
    public List<Map<String, Object>> queryForList(final String sql, final PreparedStatementSetter args) throws SQLException {
        return this.query(sql, args, this.getColumnMapRowMapper());
    }

    /***/
    @Override
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
    public int[] executeBatch(String sql, Object[][] batchValues) throws SQLException {
        final TypeHandlerRegistry typeRegistry = getMappingRegistry().getTypeRegistry();
        return this.executeBatch(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int idx = 1;
                for (Object value : batchValues[i]) {
                    if (value == null) {
                        ps.setObject(idx, null);
                    } else {
                        JDBCType jdbcType = TypeHandlerRegistry.toSqlType(value.getClass());
                        TypeHandler typeHandler = typeRegistry.getTypeHandler(value.getClass());
                        typeHandler.setParameter(ps, idx, value, jdbcType);
                    }
                    idx++;
                }
            }

            @Override
            public int getBatchSize() {
                return batchValues.length;
            }
        });
    }

    @Override
    public int[] executeBatch(final String sql, final SqlParameterSource[] batchArgs) throws SQLException {
        if (batchArgs == null || batchArgs.length == 0) {
            return new int[0];
        }
        return this.executeBatch(sql, new SqlParameterSourceBatchPreparedStatementSetter(sql, batchArgs));
    }

    @Override
    public int[] executeBatch(final String sql, final BatchPreparedStatementSetter pss) throws SQLException {
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL batch update [{}].", sql);
        }
        String buildSql = getParsedSql(sql).buildSql();
        //
        return this.execute(buildSql, ps -> {
            try {
                int batchSize = pss.getBatchSize();
                DatabaseMetaData dbMetaData = ps.getConnection().getMetaData();
                if (dbMetaData.supportsBatchUpdates()) {
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (pss.isBatchExhausted(i)) {
                            break;
                        }
                        ps.addBatch();
                    }
                    return ps.executeBatch();
                } else {
                    List<Integer> rowsAffected = new ArrayList<>();
                    for (int i = 0; i < batchSize; i++) {
                        pss.setValues(ps, i);
                        if (pss.isBatchExhausted(i)) {
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

    @Override
    public <T> T call(final CallableStatementCreator csc, final CallableStatementCallback<T> action) throws SQLException {
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
                String sqlString = JdbcTemplate.getSql(csc);
                throw new SQLException("CallableStatementCallback SQL :" + sqlString, ex);
            } finally {
                if (csc instanceof ParameterDisposer) {
                    ((ParameterDisposer) csc).cleanupParameters();
                }
            }
        });
    }

    @Override
    public <T> T call(String callString, CallableStatementCallback<T> action) throws SQLException {
        return this.call(new SimpleCallableStatementCreator(callString), action);
    }

    @Override
    public <T> T call(String callString, CallableStatementSetter setter, CallableStatementCallback<T> action) throws SQLException {
        return this.call(new SimpleCallableStatementCreator(callString), cs -> {
            try {
                if (setter != null) {
                    setter.setValues(cs);
                }
                return action.doInCallableStatement(cs);
            } finally {
                if (setter instanceof ParameterDisposer) {
                    ((ParameterDisposer) setter).cleanupParameters();
                }
            }
        });
    }

    @Override
    public Map<String, Object> call(String callString, List<SqlParameter> declaredParameters) throws SQLException {
        return this.call(callString, cs -> {
            //
            // process params
            int sqlColIndex = 1;
            final List<ReturnSqlParameter> resultParameters = new ArrayList<>();
            for (SqlParameter declaredParam : declaredParameters) {
                // input parameters
                if (declaredParam instanceof InSqlParameter) {
                    JDBCType paramJdbcType = Objects.requireNonNull(((InSqlParameter) declaredParam).getJdbcType(), "jdbcType must not be null");
                    Object paramValue = ((InSqlParameter) declaredParam).getValue();
                    TypeHandler paramTypeHandler = ((InSqlParameter) declaredParam).getTypeHandler();
                    //
                    paramTypeHandler = (paramTypeHandler != null) ? paramTypeHandler : TypeHandlerRegistry.DEFAULT.getTypeHandler(paramJdbcType);
                    paramTypeHandler.setParameter(cs, sqlColIndex, paramValue, paramJdbcType);
                }
                // output parameters
                if (declaredParam instanceof OutSqlParameter) {
                    JDBCType paramJdbcType = Objects.requireNonNull(((OutSqlParameter) declaredParam).getJdbcType(), "jdbcType must not be null");
                    String paramTypeName = ((OutSqlParameter) declaredParam).getTypeName();
                    Integer paramScale = ((OutSqlParameter) declaredParam).getScale();
                    //
                    if (paramTypeName != null) {
                        cs.registerOutParameter(sqlColIndex, paramJdbcType, paramTypeName);
                    } else if (paramScale != null) {
                        cs.registerOutParameter(sqlColIndex, paramJdbcType, paramScale);
                    } else {
                        cs.registerOutParameter(sqlColIndex, paramJdbcType);
                    }
                }
                // return parameters
                if (declaredParam instanceof ReturnSqlParameter) {
                    resultParameters.add((ReturnSqlParameter) declaredParam);
                }
                sqlColIndex++;
            }
            //
            // execute call
            Map<String, Object> resultsMap = createResultsMap();
            boolean retVal = cs.execute();
            if (logger.isTraceEnabled()) {
                logger.trace("CallableStatement.execute() returned '" + retVal + "'");
            }
            //
            // fetch output
            for (int i = 1; i <= declaredParameters.size(); i++) {
                SqlParameter declaredParam = declaredParameters.get(i - 1);
                OutSqlParameter outParameter = null;
                if (!(declaredParam instanceof OutSqlParameter)) {
                    continue;
                }
                outParameter = (OutSqlParameter) declaredParam;
                String paramName = declaredParam.getName();
                JDBCType paramJdbcType = Objects.requireNonNull(outParameter.getJdbcType(), "jdbcType must not be null");
                TypeHandler paramTypeHandler = outParameter.getTypeHandler();
                //
                paramName = StringUtils.isNotBlank(paramName) ? paramName : "#out-" + i;
                paramTypeHandler = (paramTypeHandler != null) ? paramTypeHandler : TypeHandlerRegistry.DEFAULT.getTypeHandler(paramJdbcType);
                Object resultValue = paramTypeHandler.getResult(cs, i);
                resultsMap.put(paramName, resultValue);
            }
            //
            // fetch results
            int resultIndex = 1;
            ReturnSqlParameter sqlParameter = resultParameters.size() > 0 ? resultParameters.get(0) : null;
            if (retVal) {
                try (ResultSet resultSet = cs.getResultSet()) {
                    String name = resultParameterName(sqlParameter, "#result-set-" + resultIndex);
                    resultsMap.put(name, processResultSet(isResultsCaseInsensitive(), resultSet, sqlParameter));
                }
            } else {
                String name = resultParameterName(sqlParameter, "#update-count-" + resultIndex);
                resultsMap.put(name, cs.getUpdateCount());
            }
            while ((cs.getMoreResults()) || (cs.getUpdateCount() != -1)) {
                resultIndex++;
                sqlParameter = resultParameters.size() > resultIndex ? resultParameters.get(resultIndex - 1) : null;
                int updateCount = cs.getUpdateCount();
                //
                try (ResultSet resultSet = cs.getResultSet()) {
                    if (resultSet != null) {
                        String name = resultParameterName(sqlParameter, "#result-set-" + resultIndex);
                        resultsMap.put(name, processResultSet(isResultsCaseInsensitive(), resultSet, sqlParameter));
                    } else {
                        String name = resultParameterName(sqlParameter, "#update-count-" + resultIndex);
                        resultsMap.put(name, updateCount);
                    }
                }
            }
            return resultsMap;
        });
    }

    private static String resultParameterName(ReturnSqlParameter sqlParameter, String defaultName) {
        return (sqlParameter == null || StringUtils.isBlank(sqlParameter.getName())) ? defaultName : sqlParameter.getName();
    }

    /**
     * Process the given ResultSet from a stored procedure.
     * @param rs the ResultSet to process
     * @param param the corresponding stored procedure parameter
     * @return a Map that contains returned results
     */
    protected static Object processResultSet(boolean caseInsensitive, ResultSet rs, ReturnSqlParameter param) throws SQLException {
        if (rs != null) {
            if (param != null) {
                if (param.getRowMapper() != null) {
                    RowMapper<?> rowMapper = param.getRowMapper();
                    return (new RowMapperResultSetExtractor<>(rowMapper)).extractData(rs);
                } else if (param.getRowCallbackHandler() != null) {
                    RowCallbackHandler rch = param.getRowCallbackHandler();
                    new RowCallbackHandlerResultSetExtractor(rch).extractData(rs);
                    return "ResultSet returned from stored procedure was processed";
                } else if (param.getResultSetExtractor() != null) {
                    return param.getResultSetExtractor().extractData(rs);
                }
            } else {
                return new ColumnMapResultSetExtractor(caseInsensitive).extractData(rs);
            }
        }
        return null;
    }

    /** Create a new RowMapper for reading columns as key-value pairs. */
    protected RowMapper<Map<String, Object>> getColumnMapRowMapper() {
        return new ColumnMapRowMapper(this.mappingRegistry.getTypeRegistry()) {
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
        if (TypeHandlerRegistry.DEFAULT.hasTypeHandler(requiredType) || requiredType.isEnum()) {
            return this.getSingleColumnRowMapper(requiredType);
        }
        //
        return MappingRowMapper.newInstance(requiredType, this.mappingRegistry);
    }

    /** Create a new RowMapper for reading result objects from a single column.*/
    protected <T> RowMapper<T> getSingleColumnRowMapper(Class<T> requiredType) {
        TypeHandlerRegistry typeHandler = this.getMappingRegistry().getTypeRegistry();
        return new SingleColumnRowMapper<>(requiredType, typeHandler);
    }

    /**创建用于保存结果集的数据Map。*/
    protected Map<String, Object> createResultsMap() {
        if (this.isResultsCaseInsensitive()) {
            return new LinkedCaseInsensitiveMap<>();
        } else {
            return new LinkedHashMap<>();
        }
    }

    /** Create a new PreparedStatementSetter.*/
    protected PreparedStatementSetter newArgPreparedStatementSetter(final Object[] args) {
        return new ArgPreparedStatementSetter(this.mappingRegistry.getTypeRegistry(), args);
    }

    /** Build a PreparedStatementCreator based on the given SQL and named parameters. */
    protected PreparedStatementCreator getPreparedStatementCreator(final String sql, final SqlParameterSource paramSource) {
        return new MapPreparedStatementCreator(sql, paramSource);
    }

    /* Map of original SQL String to ParsedSql representation */
    private final Map<String, ParsedSql> parsedSqlCache = new HashMap<>();

    /* Obtain a parsed representation of the given SQL statement.*/
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

    /**获取SQL文本*/
    private static String getSql(final Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

    /** 至返回结果集中的一条数据。*/
    private static <T> T requiredSingleResult(final Collection<T> results) throws SQLException {
        if (results == null || results.isEmpty()) {
            return null;
        }
        int size = results.size();
        if (size > 1) {
            throw new SQLException("Incorrect record count: expected 1, actual " + size);
        }
        return results.iterator().next();
    }

    /** 获取SQL*/
    protected static interface SqlProvider {
        public String getSql();
    }

    /** 接口 {@link PreparedStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link PreparedStatement}对象。*/
    private static class SimplePreparedStatementCreator implements PreparedStatementCreator, JdbcTemplate.SqlProvider {
        private final String sql;

        public SimplePreparedStatementCreator(final String sql) {
            this.sql = Objects.requireNonNull(sql, "SQL must not be null");
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

    /** Simple adapter for CallableStatementCreator, allowing to use a plain SQL statement. */
    private static class SimpleCallableStatementCreator implements CallableStatementCreator, JdbcTemplate.SqlProvider {
        private final String callString;

        public SimpleCallableStatementCreator(String callString) {
            this.callString = Objects.requireNonNull(callString, "Call string must not be null");
        }

        @Override
        public CallableStatement createCallableStatement(Connection con) throws SQLException {
            if (!con.getMetaData().supportsStoredProcedures()) {
                throw new UnsupportedOperationException("target DataSource Unsupported.");
            }
            return con.prepareCall(this.callString);
        }

        @Override
        public String getSql() {
            return this.callString;
        }
    }

    private class MultipleResultExtractor implements PreparedStatementCallback<List<Object>> {
        @Override
        public List<Object> doInPreparedStatement(PreparedStatement ps) throws SQLException {
            ColumnMapRowMapper columnMapRowMapper = new ColumnMapRowMapper(getMappingRegistry().getTypeRegistry());
            ReturnSqlParameter result = SqlParameterUtils.withReturnResult("TMP", new RowMapperResultSetExtractor<>(columnMapRowMapper));
            boolean retVal = ps.execute();
            if (logger.isTraceEnabled()) {
                logger.trace("statement.execute() returned '" + retVal + "'");
            }
            //
            List<Object> resultList = new ArrayList<>();
            if (retVal) {
                try (ResultSet resultSet = ps.getResultSet()) {
                    resultList.add(processResultSet(isResultsCaseInsensitive(), resultSet, result));
                }
            } else {
                resultList.add(ps.getUpdateCount());
            }
            while ((ps.getMoreResults()) || (ps.getUpdateCount() != -1)) {
                int updateCount = ps.getUpdateCount();
                try (ResultSet resultSet = ps.getResultSet()) {
                    if (resultSet != null) {
                        resultList.add(processResultSet(isResultsCaseInsensitive(), resultSet, null));
                    } else {
                        resultList.add(updateCount);
                    }
                }
            }
            return resultList;
        }
    }

    /** 接口 {@link PreparedStatementCreator} 的简单实现，目的是根据 SQL 语句创建 {@link PreparedStatement}对象。*/
    private class MapPreparedStatementCreator implements PreparedStatementCreator, ParameterDisposer, JdbcTemplate.SqlProvider {
        private final ParsedSql          parsedSql;
        private final SqlParameterSource paramSource;

        public MapPreparedStatementCreator(final String originalSql, final SqlParameterSource paramSource) {
            Objects.requireNonNull(originalSql, "SQL must not be null");
            this.parsedSql = getParsedSql(originalSql);
            this.paramSource = paramSource;
        }

        @Override
        public PreparedStatement createPreparedStatement(final Connection con) throws SQLException {
            //1.根据参数信息生成最终会执行的SQL语句.
            String sqlToUse = this.parsedSql.buildSql();
            //2.确定参数对象
            Object[] paramArray = this.parsedSql.buildValues(this.paramSource);
            //3.创建PreparedStatement对象，并设置参数
            PreparedStatement statement = con.prepareStatement(sqlToUse);
            for (int i = 0; i < paramArray.length; i++) {
                getMappingRegistry().getTypeRegistry().setParameterValue(statement, i + 1, paramArray[i]);
            }
            StatementSetterUtils.cleanupParameters(paramArray);
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

    /** 接口 {@link BatchPreparedStatementSetter} 的简单实现，目的是设置批量操作 */
    private class SqlParameterSourceBatchPreparedStatementSetter implements BatchPreparedStatementSetter, ParameterDisposer {
        private final ParsedSql            parsedSql;
        private final SqlParameterSource[] batchArgs;

        public SqlParameterSourceBatchPreparedStatementSetter(final String sql, final SqlParameterSource[] batchArgs) {
            this.parsedSql = getParsedSql(sql);
            this.batchArgs = batchArgs;
        }

        @Override
        public void setValues(final PreparedStatement ps, final int index) throws SQLException {
            SqlParameterSource paramSource = this.batchArgs[index];
            //1.确定参数对象
            Object[] sqlValue = this.parsedSql.buildValues(paramSource);
            //2.设置参数
            int sqlColIndex = 1;
            for (Object element : sqlValue) {
                getMappingRegistry().getTypeRegistry().setParameterValue(ps, sqlColIndex++, element);
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
