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
package net.hasor.db.dal.execute;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.jdbc.ConnectionCallback;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.MultipleProcessType;
import net.hasor.db.jdbc.extractor.MultipleResultSetExtractor;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.jdbc.mapper.MappingRowMapper;
import net.hasor.db.mapping.reader.TableReader;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 执行器基类
 * @version : 2021-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractStatementExecute<T> {
    private final BuilderContext builderContext;
    private final ExecuteInfo    executeInfo;
    private final JdbcTemplate   jdbcTemplate;

    public AbstractStatementExecute(BuilderContext builderContext, ExecuteInfo executeInfo, JdbcTemplate jdbcTemplate) {
        this.builderContext = builderContext;
        this.executeInfo = executeInfo;
        this.jdbcTemplate = jdbcTemplate;
    }

    protected BuilderContext getBuilderContext() {
        return this.builderContext;
    }

    protected ExecuteInfo getExecuteInfo() {
        return this.executeInfo;
    }

    protected JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public T execute(QuerySqlBuilder queryBuilder) throws SQLException {
        return this.getJdbcTemplate().execute((ConnectionCallback<T>) con -> executeQuery(con, queryBuilder));
    }

    protected abstract T executeQuery(Connection con, QuerySqlBuilder queryBuilder) throws SQLException;

    protected void configStatement(ExecuteInfo executeInfo, Statement statement) throws SQLException {
        if (executeInfo.timeout > 0) {
            statement.setQueryTimeout(executeInfo.timeout);
        }
        if (executeInfo.fetchSize > 0) {
            statement.setFetchSize(executeInfo.fetchSize);
        }
    }

    protected MultipleResultSetExtractor buildMultipleResultExtractor(ExecuteInfo executeInfo) {
        String[] resultMapSplit = executeInfo.resultMap.split(",");
        RowMapper<?>[] rowMappers = new RowMapper[resultMapSplit.length];
        for (int i = 0; i < resultMapSplit.length; i++) {
            TableReader<?> tableReader = getBuilderContext().findTableReaderById(resultMapSplit[i]);
            if (tableReader != null) {
                rowMappers[i] = new MappingRowMapper<>(tableReader);
            } else {
                rowMappers[i] = new ColumnMapRowMapper(getBuilderContext().getHandlerRegistry());
            }
        }
        //
        MultipleProcessType multipleType = MultipleProcessType.valueOf(executeInfo.multipleResultType.getTypeName());
        return new MultipleResultSetExtractor(multipleType, rowMappers);
    }
}
