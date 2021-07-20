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
import net.hasor.db.dal.dynamic.DalBoundSql.SqlArg;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.repository.config.MultipleResultsType;
import net.hasor.db.dal.repository.config.ResultSetType;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.MultipleResultSetExtractor;
import net.hasor.db.types.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 负责参数化SQL调用的执行器
 * @version : 2021-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class PreparedStatementExecute extends AbstractStatementExecute<Object> {
    public PreparedStatementExecute(BuilderContext builderContext, ExecuteInfo executeInfo, JdbcTemplate jdbcTemplate) {
        super(builderContext, executeInfo, jdbcTemplate);
    }

    protected PreparedStatement createPreparedStatement(Connection conn, String queryString, ResultSetType resultSetType) throws SQLException {
        if (resultSetType == null || resultSetType.getResultSetType() == null) {
            return conn.prepareStatement(queryString);
        } else {
            int resultSetTypeInt = resultSetType.getResultSetType();
            return conn.prepareStatement(queryString, resultSetTypeInt, ResultSet.CONCUR_READ_ONLY);
        }
    }

    protected Object executeQuery(Connection con, QuerySqlBuilder queryBuilder) throws SQLException {
        ExecuteInfo executeInfo = getExecuteInfo();
        try (PreparedStatement ps = createPreparedStatement(con, executeInfo.sqlString, executeInfo.resultSetType)) {
            return executeQuery(ps, queryBuilder);
        }
    }

    protected Object executeQuery(PreparedStatement ps, QuerySqlBuilder queryBuilder) throws SQLException {
        ExecuteInfo executeInfo = getExecuteInfo();
        configStatement(executeInfo, ps);
        MultipleResultSetExtractor extractor = super.buildMultipleResultExtractor(executeInfo);
        //
        List<SqlArg> sqlArg = queryBuilder.getSqlArg();
        for (int i = 0; i < sqlArg.size(); i++) {
            SqlArg arg = sqlArg.get(i);
            TypeHandler typeHandler = arg.getTypeHandler();
            typeHandler.setParameter(ps, i + 1, arg.getValue(), arg.getJdbcType());
        }
        //
        boolean retVal = ps.execute();
        List<Object> result = extractor.doResult(retVal, ps);
        if (result.isEmpty()) {
            return null;
        }
        //
        if (executeInfo.multipleResultType == MultipleResultsType.FIRST || executeInfo.multipleResultType == MultipleResultsType.LAST) {
            return result.get(0);
        } else {
            return result;
        }
    }
}
