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
package net.hasor.db.lambda.query;
import net.hasor.db.lambda.dialect.BoundSql;
import net.hasor.db.lambda.dialect.SqlDialect;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowCallbackHandler;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.lambda.page.Page;
import net.hasor.db.lambda.page.PageObject;
import net.hasor.db.lambda.QueryExecute;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 带有查询能力的 SQL 执行器基类，实现了 QueryExecute 接口
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractQueryExecute<T> extends AbstractExecute<T> implements QueryExecute<T> {
    private final Page pageInfo = new PageObject(0, this::queryForCount);

    public AbstractQueryExecute(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        super(exampleType, jdbcTemplate);
    }

    AbstractQueryExecute(Class<T> exampleType, JdbcTemplate jdbcTemplate, String dbType, SqlDialect dialect) {
        super(exampleType, jdbcTemplate, dbType, dialect);
    }

    public final BoundSql getBoundSql(SqlDialect dialect) {
        SqlDialect currentSqlDialect = this.dialect();
        try {
            setDialect(dialect);
            int pageSize = this.pageInfo.getPageSize();
            if (pageSize > 0) {
                int recordPosition = this.pageInfo.getFirstRecordPosition();
                return dialect.pageSql(getOriginalBoundSql(), recordPosition, pageSize);
            } else {
                return this.getOriginalBoundSql();
            }
        } finally {
            if (currentSqlDialect != dialect) {
                setDialect(currentSqlDialect);
            }
        }
    }

    public final BoundSql getBoundSql() {
        return this.getBoundSql(this.dialect());
    }

    protected abstract BoundSql getOriginalBoundSql();

    public Page pageInfo() {
        return this.pageInfo;
    }

    @Override
    public <V> QueryExecute<V> wrapperType(Class<V> wrapperType) {
        AbstractQueryExecute<T> self = this;
        return new AbstractQueryExecute<V>(wrapperType, this.getJdbcTemplate(), this.dbType, this.dialect()) {
            @Override
            protected BoundSql getOriginalBoundSql() {
                return self.getOriginalBoundSql();
            }
        };
    }

    @Override
    public <V> V query(ResultSetExtractor<V> rse) throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().query(boundSql.getSqlString(), boundSql.getArgs(), rse);
    }

    @Override
    public void query(RowCallbackHandler rch) throws SQLException {
        BoundSql boundSql = getBoundSql();
        this.getJdbcTemplate().query(boundSql.getSqlString(), boundSql.getArgs(), rch);
    }

    @Override
    public <V> List<V> query(RowMapper<V> rowMapper) throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().query(boundSql.getSqlString(), boundSql.getArgs(), rowMapper);
    }

    @Override
    public List<T> queryForList() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().query(boundSql.getSqlString(), boundSql.getArgs(), getRowMapper());
    }

    @Override
    public T queryForObject() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().queryForObject(boundSql.getSqlString(), boundSql.getArgs(), getRowMapper());
    }

    @Override
    public Map<String, Object> queryForMap() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().queryForMap(boundSql.getSqlString(), boundSql.getArgs());
    }

    @Override
    public List<Map<String, Object>> queryForMapList() throws SQLException {
        BoundSql boundSql = getBoundSql();
        return this.getJdbcTemplate().queryForList(boundSql.getSqlString(), boundSql.getArgs());
    }

    @Override
    public int queryForCount() throws SQLException {
        BoundSql countSql = this.dialect().countSql(this.getOriginalBoundSql());
        return this.getJdbcTemplate().queryForInt(countSql.getSqlString(), countSql.getArgs());
    }

    @Override
    public long queryForLargeCount() throws SQLException {
        BoundSql countSql = this.dialect().countSql(this.getOriginalBoundSql());
        return this.getJdbcTemplate().queryForLong(countSql.getSqlString(), countSql.getArgs());
    }
}
