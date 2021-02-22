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
package net.hasor.db.jdbc.lambda.query;
import net.hasor.db.JdbcUtils;
import net.hasor.db.dal.orm.MappingRowMapper;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.dialect.SqlDialectRegister;
import net.hasor.db.jdbc.*;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.lambda.QueryExecute;
import net.hasor.db.jdbc.page.Page;
import net.hasor.db.jdbc.page.PageObject;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Lambda SQL 执行器
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractQueryExecute<T> implements QueryExecute<T> {
    protected final String              dbType;
    private final   SqlDialect          dialect;
    private final   Class<T>            exampleType;
    private final   MappingRowMapper<T> exampleRowMapper;
    private final   JdbcOperations      jdbcOperations;
    private         boolean             useDialect;
    private final   Page                pageInfo = new PageObject(0, this::queryForCount);

    public AbstractQueryExecute(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        this.exampleType = exampleType;
        this.jdbcOperations = jdbcTemplate;
        this.exampleRowMapper = (jdbcTemplate != null) ?//
                jdbcTemplate.getMappingHandler().resolveMapper(exampleType) ://
                MappingRowMapper.newInstance(exampleType);
        String tmpDbType;
        try {
            tmpDbType = this.getJdbcOperations().execute((ConnectionCallback<String>) con -> {
                DatabaseMetaData metaData = con.getMetaData();
                return JdbcUtils.getDbType(metaData.getURL(), metaData.getDriverName());
            });
        } catch (Exception e) {
            tmpDbType = "";
        }
        //
        SqlDialect tempDialect = SqlDialectRegister.findOrCreate(tmpDbType);
        this.dbType = tmpDbType;
        this.dialect = (tempDialect == null) ? SqlDialect.DEFAULT : tempDialect;
        this.useDialect = false;
    }

    AbstractQueryExecute(Class<T> exampleType, JdbcOperations jdbcOperations, String dbType, SqlDialect dialect) {
        this.exampleType = exampleType;
        this.jdbcOperations = jdbcOperations;
        this.exampleRowMapper = (jdbcOperations instanceof JdbcTemplate) ?//
                ((JdbcTemplate) jdbcOperations).getMappingHandler().resolveMapper(exampleType) ://
                MappingRowMapper.newInstance(exampleType);
        this.dbType = dbType;
        this.dialect = (dialect == null) ? SqlDialect.DEFAULT : dialect;
    }

    public Class<T> exampleType() {
        return this.exampleType;
    }

    public JdbcOperations getJdbcOperations() {
        return this.jdbcOperations;
    }

    protected MappingRowMapper<T> getRowMapper() {
        return this.exampleRowMapper;
    }

    protected SqlDialect dialect() {
        return (this.useDialect && this.dialect != null) ? this.dialect : SqlDialect.DEFAULT;
    }

    public final BoundSql getBoundSql(SqlDialect dialect) {
        int pageSize = this.pageInfo.getPageSize();
        if (pageSize > 0) {
            int recordPosition = this.pageInfo.getFirstRecordPosition();
            return dialect.getPageSql(getOriginalBoundSql(), recordPosition, pageSize);
        } else {
            return this.getOriginalBoundSql();
        }
    }

    public final BoundSql getBoundSql() {
        return this.getBoundSql(this.dialect);
    }

    protected abstract BoundSql getOriginalBoundSql();

    public Page pageInfo() {
        return this.pageInfo;
    }

    @Override
    public <V> QueryExecute<V> wrapperType(Class<V> wrapperType) {
        AbstractQueryExecute<T> self = this;
        return new AbstractQueryExecute<V>(wrapperType, this.jdbcOperations, this.dbType, this.dialect) {
            @Override
            protected BoundSql getOriginalBoundSql() {
                return self.getOriginalBoundSql();
            }
        };
    }

    @Override
    public <V> V query(ResultSetExtractor<V> rse) throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.query(boundSql.getSqlString(), boundSql.getArgs(), rse);
    }

    @Override
    public void query(RowCallbackHandler rch) throws SQLException {
        BoundSql boundSql = getBoundSql();
        this.jdbcOperations.query(boundSql.getSqlString(), boundSql.getArgs(), rch);
    }

    @Override
    public <V> List<V> query(RowMapper<V> rowMapper) throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.query(boundSql.getSqlString(), boundSql.getArgs(), rowMapper);
    }

    @Override
    public List<T> queryForList() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.query(boundSql.getSqlString(), boundSql.getArgs(), getRowMapper());
    }

    @Override
    public T queryForObject() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.queryForObject(boundSql.getSqlString(), boundSql.getArgs(), getRowMapper());
    }

    @Override
    public Map<String, Object> queryForMap() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.queryForMap(boundSql.getSqlString(), boundSql.getArgs());
    }

    @Override
    public List<Map<String, Object>> queryForMapList() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.jdbcOperations.queryForList(boundSql.getSqlString(), boundSql.getArgs());
    }

    @Override
    public int queryForCount() throws SQLException {
        BoundSql countSql = this.dialect.getCountSql(this.getOriginalBoundSql());
        return this.jdbcOperations.queryForInt(countSql.getSqlString(), countSql.getArgs());
    }

    @Override
    public long queryForLargeCount() throws SQLException {
        BoundSql countSql = this.dialect.getCountSql(this.getOriginalBoundSql());
        return this.jdbcOperations.queryForLong(countSql.getSqlString(), countSql.getArgs());
    }
}
