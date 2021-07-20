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
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.repository.config.*;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.utils.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 执行器入口
 * @version : 2021-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class DalExecute {
    public static Object execute(Connection connection, DynamicSql dynamicSql, BuilderContext builderContext) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        return execute(jdbcTemplate, dynamicSql, builderContext);
    }

    public static Object execute(DataSource dataSource, DynamicSql dynamicSql, BuilderContext builderContext) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return execute(jdbcTemplate, dynamicSql, builderContext);
    }

    public static Object execute(JdbcTemplate jdbcTemplate, DynamicSql dynamicSql, BuilderContext builderContext) throws SQLException {
        QuerySqlBuilder queryBuilder = dynamicSql.buildQuery(builderContext);
        ExecuteInfo executeInfo = new ExecuteInfo();
        StatementType statementType = StatementType.Prepared;
        //
        executeInfo.sqlString = queryBuilder.getSqlString();
        executeInfo.timeout = -1;
        executeInfo.parameterType = null;
        executeInfo.resultMap = "";
        executeInfo.fetchSize = 256;
        executeInfo.resultSetType = ResultSetType.DEFAULT;
        executeInfo.multipleResultType = MultipleResultsType.LAST;
        if (dynamicSql instanceof DmlSqlConfig) {
            statementType = ((DmlSqlConfig) dynamicSql).getStatementType();
            executeInfo.timeout = ((DmlSqlConfig) dynamicSql).getTimeout();
            executeInfo.parameterType = ((DmlSqlConfig) dynamicSql).getParameterType();
        }
        if (dynamicSql instanceof QuerySqlConfig) {
            String resultMapStr = ((QuerySqlConfig) dynamicSql).getResultMap();
            String resultTypeStr = ((QuerySqlConfig) dynamicSql).getResultType();
            executeInfo.resultMap = StringUtils.isNotBlank(resultTypeStr) ? resultTypeStr : resultMapStr;
            executeInfo.fetchSize = ((QuerySqlConfig) dynamicSql).getFetchSize();
            executeInfo.resultSetType = ((QuerySqlConfig) dynamicSql).getResultSetType();
            executeInfo.multipleResultType = ((QuerySqlConfig) dynamicSql).getMultipleResultType();
        }
        //
        switch (statementType) {
            case Statement: {
                return new StatementExecute(builderContext, executeInfo, jdbcTemplate).execute(queryBuilder);
            }
            case Prepared: {
                return new PreparedStatementExecute(builderContext, executeInfo, jdbcTemplate).execute(queryBuilder);
            }
            case Callable: {
                return new CallableStatementExecute(builderContext, executeInfo, jdbcTemplate).execute(queryBuilder);
            }
            default: {
                throw new UnsupportedOperationException("statementType '" + statementType.getTypeName() + "' Unsupported.");
            }
        }
    }
}
