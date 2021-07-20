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
import net.hasor.db.dal.repository.config.ResultSetType;
import net.hasor.db.jdbc.SqlParameter;
import net.hasor.db.jdbc.SqlParameterUtils;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.jdbc.extractor.MultipleProcessType;
import net.hasor.db.jdbc.extractor.SimpleCallableStatementCallback;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 负责存储过程调用的执行器
 * @version : 2021-07-20
 * @author 赵永春 (zyc@hasor.net)
 */
public class CallableStatementExecute extends AbstractStatementExecute<Object> {
    public CallableStatementExecute(BuilderContext builderContext, ExecuteInfo executeInfo, JdbcTemplate jdbcTemplate) {
        super(builderContext, executeInfo, jdbcTemplate);
    }

    protected CallableStatement createCallableStatement(Connection conn, String queryString, ResultSetType resultSetType) throws SQLException {
        if (resultSetType == null || resultSetType.getResultSetType() == null) {
            return conn.prepareCall(queryString);
        } else {
            int resultSetTypeInt = resultSetType.getResultSetType();
            return conn.prepareCall(queryString, resultSetTypeInt, ResultSet.CONCUR_READ_ONLY);
        }
    }

    protected Object executeQuery(Connection con, QuerySqlBuilder queryBuilder) throws SQLException {
        if (!con.getMetaData().supportsStoredProcedures()) {
            throw new UnsupportedOperationException("target DataSource Unsupported.");
        }
        //
        ExecuteInfo executeInfo = getExecuteInfo();
        try (CallableStatement ps = createCallableStatement(con, executeInfo.sqlString, executeInfo.resultSetType)) {
            return executeQuery(ps, queryBuilder);
        }
    }

    protected Object executeQuery(CallableStatement cs, QuerySqlBuilder queryBuilder) throws SQLException {
        ExecuteInfo executeInfo = getExecuteInfo();
        configStatement(executeInfo, cs);
        //
        List<SqlArg> sqlArg = queryBuilder.getSqlArg();
        List<SqlParameter> paramList = sqlArg.stream().map(arg -> {
            switch (arg.getSqlMode()) {
                case In:
                    return SqlParameterUtils.withInput(arg.getValue(), arg.getJdbcType(), arg.getTypeHandler());
                case Out:
                    return SqlParameterUtils.withOutput(arg.getJdbcType(), arg.getTypeHandler());
                case InOut:
                    return SqlParameterUtils.withInOut(arg.getValue(), arg.getJdbcType(), arg.getTypeHandler());
                default:
                    throw new UnsupportedOperationException("SqlMode " + arg.getSqlMode() + " Unsupported.");
            }
        }).collect(Collectors.toList());
        //
        MultipleProcessType multipleType = MultipleProcessType.valueOf(executeInfo.multipleResultType.getTypeName());
        SimpleCallableStatementCallback callback = new SimpleCallableStatementCallback(multipleType, paramList);
        Map<String, Object> result = callback.doInCallableStatement(cs);
        if (result.isEmpty()) {
            return null;
        }
        //
        if (multipleType == MultipleProcessType.FIRST || multipleType == MultipleProcessType.LAST) {
            return result.entrySet().iterator().next().getValue();
        } else {
            return result;
        }
    }
}
