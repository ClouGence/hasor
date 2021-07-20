/*
 * Copyright 2002-2008 the original author or authors.
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
package net.hasor.db.jdbc.extractor;
import net.hasor.db.jdbc.*;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.StringUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * {@link ResultSetExtractor} 接口实现类，该类会将结果集中的每一行进行处理，并返回一个 List 用以封装处理结果集。
 * @version : 2021-07-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class SimpleCallableStatementCallback implements CallableStatementCallback<Map<String, Object>> {
    private static final Logger              logger                 = LoggerFactory.getLogger(SimpleCallableStatementCallback.class);
    private final        List<SqlParameter>  declaredParameters;
    private final        MultipleProcessType processType;
    private              boolean             resultsCaseInsensitive = false;

    public SimpleCallableStatementCallback(List<SqlParameter> declaredParameters) {
        this(MultipleProcessType.ALL, declaredParameters);
    }

    public SimpleCallableStatementCallback(MultipleProcessType processType, List<SqlParameter> declaredParameters) {
        this.processType = processType;
        this.declaredParameters = declaredParameters;
    }

    public boolean isResultsCaseInsensitive() {
        return this.resultsCaseInsensitive;
    }

    public void setResultsCaseInsensitive(final boolean resultsCaseInsensitive) {
        this.resultsCaseInsensitive = resultsCaseInsensitive;
    }

    protected Map<String, Object> createResultsMap() {
        if (this.isResultsCaseInsensitive()) {
            return new LinkedCaseInsensitiveMap<>();
        } else {
            return new LinkedHashMap<>();
        }
    }

    @Override
    public Map<String, Object> doInCallableStatement(CallableStatement cs) throws SQLException {
        // process params
        int sqlColIndex = 1;
        final List<SqlParameter.ReturnSqlParameter> resultParameters = new ArrayList<>();
        for (SqlParameter declaredParam : declaredParameters) {
            // input parameters
            if (declaredParam instanceof SqlParameter.InSqlParameter) {
                JDBCType paramJdbcType = Objects.requireNonNull(((SqlParameter.InSqlParameter) declaredParam).getJdbcType(), "jdbcType must not be null");
                Object paramValue = ((SqlParameter.InSqlParameter) declaredParam).getValue();
                TypeHandler paramTypeHandler = ((SqlParameter.InSqlParameter) declaredParam).getTypeHandler();
                //
                paramTypeHandler = (paramTypeHandler != null) ? paramTypeHandler : TypeHandlerRegistry.DEFAULT.getTypeHandler(paramJdbcType);
                paramTypeHandler.setParameter(cs, sqlColIndex, paramValue, paramJdbcType);
            }
            // output parameters
            if (declaredParam instanceof SqlParameter.OutSqlParameter) {
                JDBCType paramJdbcType = Objects.requireNonNull(((SqlParameter.OutSqlParameter) declaredParam).getJdbcType(), "jdbcType must not be null");
                String paramTypeName = ((SqlParameter.OutSqlParameter) declaredParam).getTypeName();
                Integer paramScale = ((SqlParameter.OutSqlParameter) declaredParam).getScale();
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
            if (declaredParam instanceof SqlParameter.ReturnSqlParameter) {
                resultParameters.add((SqlParameter.ReturnSqlParameter) declaredParam);
            }
            sqlColIndex++;
        }
        //
        // execute call
        Map<String, Object> resultMap = createResultsMap();
        boolean retVal = cs.execute();
        if (logger.isTraceEnabled()) {
            logger.trace("CallableStatement.execute() returned '" + retVal + "'");
        }
        //
        // fetch output
        for (int i = 1; i <= declaredParameters.size(); i++) {
            SqlParameter declaredParam = declaredParameters.get(i - 1);
            SqlParameter.OutSqlParameter outParameter = null;
            if (!(declaredParam instanceof SqlParameter.OutSqlParameter)) {
                continue;
            }
            outParameter = (SqlParameter.OutSqlParameter) declaredParam;
            String paramName = declaredParam.getName();
            JDBCType paramJdbcType = Objects.requireNonNull(outParameter.getJdbcType(), "jdbcType must not be null");
            TypeHandler paramTypeHandler = outParameter.getTypeHandler();
            //
            paramName = StringUtils.isNotBlank(paramName) ? paramName : "#out-" + i;
            paramTypeHandler = (paramTypeHandler != null) ? paramTypeHandler : TypeHandlerRegistry.DEFAULT.getTypeHandler(paramJdbcType);
            Object resultValue = paramTypeHandler.getResult(cs, i);
            resultMap.put(paramName, resultValue);
        }
        //
        // fetch results
        int resultIndex = 1;
        SqlParameter.ReturnSqlParameter sqlParameter = resultParameters.size() > 0 ? resultParameters.get(0) : null;
        String resultName = "";
        Object resultValue = null;
        if (retVal) {
            try (ResultSet resultSet = cs.getResultSet()) {
                resultName = resultParameterName(sqlParameter, "#result-set-" + resultIndex);
                resultValue = processResultSet(isResultsCaseInsensitive(), resultSet, sqlParameter);
            }
        } else {
            resultName = resultParameterName(sqlParameter, "#update-count-" + resultIndex);
            resultValue = cs.getUpdateCount();
        }
        resultMap.put(resultName, resultValue);
        if (processType == MultipleProcessType.FIRST) {
            return resultMap;
        }
        //
        while ((cs.getMoreResults()) || (cs.getUpdateCount() != -1)) {
            if (processType == MultipleProcessType.ALL && StringUtils.isNotBlank(resultName)) {
                resultMap.put(resultName, resultValue);
            }
            //
            resultIndex++;
            sqlParameter = resultParameters.size() > resultIndex ? resultParameters.get(resultIndex - 1) : null;
            int updateCount = cs.getUpdateCount();
            //
            try (ResultSet resultSet = cs.getResultSet()) {
                if (resultSet != null) {
                    resultName = resultParameterName(sqlParameter, "#result-set-" + resultIndex);
                    resultValue = processResultSet(isResultsCaseInsensitive(), resultSet, sqlParameter);
                } else {
                    resultName = resultParameterName(sqlParameter, "#update-count-" + resultIndex);
                    resultValue = updateCount;
                }
            }
        }
        //
        resultMap.put(resultName, resultValue);
        return resultMap;
    }

    private static String resultParameterName(SqlParameter.ReturnSqlParameter sqlParameter, String defaultName) {
        return (sqlParameter == null || StringUtils.isBlank(sqlParameter.getName())) ? defaultName : sqlParameter.getName();
    }

    /**
     * Process the given ResultSet from a stored procedure.
     * @param rs the ResultSet to process
     * @param param the corresponding stored procedure parameter
     * @return a Map that contains returned results
     */
    protected static Object processResultSet(boolean caseInsensitive, ResultSet rs, SqlParameter.ReturnSqlParameter param) throws SQLException {
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
}
