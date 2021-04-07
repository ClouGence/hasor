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
import net.hasor.db.JdbcUtils;
import net.hasor.db.dialect.SqlDialect;
import net.hasor.db.dialect.SqlDialectRegister;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.mapping.MappingRowMapper;

import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.Objects;

/**
 * 所有 SQL 执行器必要的公共属性
 * @version : 2020-10-27
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractExecute<T> {
    protected final String              dbType;
    private         SqlDialect          dialect;
    private final   Class<T>            exampleType;
    private final   MappingRowMapper<T> exampleRowMapper;
    private final   JdbcTemplate        jdbcTemplate;
    private         boolean             qualifier;

    public AbstractExecute(Class<T> exampleType, JdbcTemplate jdbcTemplate) {
        if (Objects.requireNonNull(exampleType, "exampleType is null.") == Map.class) {
            throw new UnsupportedOperationException("Map cannot be used as lambda exampleType.");
        }
        this.exampleType = exampleType;
        this.jdbcTemplate = jdbcTemplate;
        this.exampleRowMapper = jdbcTemplate.getMappingHandler().resolveMapper(exampleType);
        this.exampleRowMapper.setCaseInsensitive(jdbcTemplate.isResultsCaseInsensitive());
        //
        String tmpDbType = "";
        try {
            tmpDbType = jdbcTemplate.execute((ConnectionCallback<String>) con -> {
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
    }

    AbstractExecute(Class<T> exampleType, JdbcTemplate jdbcTemplate, String dbType, SqlDialect dialect) {
        this.exampleType = exampleType;
        this.jdbcTemplate = jdbcTemplate;
        this.exampleRowMapper = jdbcTemplate.getMappingHandler().resolveMapper(exampleType);
        this.exampleRowMapper.setCaseInsensitive(jdbcTemplate.isResultsCaseInsensitive());
        //
        this.dbType = dbType;
        this.dialect = (dialect == null) ? SqlDialect.DEFAULT : dialect;
    }

    public final Class<T> exampleType() {
        return this.exampleType;
    }

    public final JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    protected final MappingRowMapper<T> getRowMapper() {
        return this.exampleRowMapper;
    }

    protected final SqlDialect dialect() {
        return this.dialect;
    }

    protected final void setDialect(SqlDialect sqlDialect) {
        this.dialect = sqlDialect;
    }

    protected void enableQualifier() {
        this.qualifier = true;
    }

    protected boolean isQualifier() {
        return this.qualifier;
    }
}
