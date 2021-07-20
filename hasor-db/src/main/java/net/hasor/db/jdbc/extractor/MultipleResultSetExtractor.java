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
import net.hasor.db.jdbc.CallableStatementCallback;
import net.hasor.db.jdbc.PreparedStatementCallback;
import net.hasor.db.jdbc.ResultSetExtractor;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.mapper.ColumnMapRowMapper;
import net.hasor.db.types.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@link ResultSetExtractor} 接口实现类，该类会将结果集中的每一行进行处理，并返回一个 List 用以封装处理结果集。
 * @author 赵永春 (zyc@byshell.org)
 */
public class MultipleResultSetExtractor implements PreparedStatementCallback<List<Object>>, CallableStatementCallback<List<Object>> {
    private static final Logger              logger      = LoggerFactory.getLogger(MultipleResultSetExtractor.class);
    private final List<RowMapper<?>>  rowMappers;
    private       MultipleProcessType processType = MultipleProcessType.ALL;

    public MultipleResultSetExtractor(RowMapper<?>... rowMapper) {
        this.rowMappers = Arrays.asList(rowMapper);
    }

    public MultipleResultSetExtractor(MultipleProcessType processType, RowMapper<?>... rowMapper) {
        this.processType = processType;
        this.rowMappers = Arrays.asList(rowMapper);
    }

    @Override
    public List<Object> doInCallableStatement(CallableStatement cs) throws SQLException {
        boolean retVal = cs.execute();
        return doResult(retVal, cs);
    }

    @Override
    public List<Object> doInPreparedStatement(PreparedStatement ps) throws SQLException {
        boolean retVal = ps.execute();
        return doResult(retVal, ps);
    }

    public List<Object> doResult(boolean retVal, Statement stmt) throws SQLException {
        if (logger.isTraceEnabled()) {
            logger.trace("statement.execute() returned '" + retVal + "'");
        }
        //
        List<Object> resultList = new ArrayList<>();
        if (retVal) {
            try (ResultSet resultSet = stmt.getResultSet()) {
                RowMapper<?> rowMapper = this.rowMappers.isEmpty() ? getDefaultRowMapper() : this.rowMappers.get(0);
                resultList.add(processResultSet(resultSet, rowMapper));
            }
        } else {
            resultList.add(stmt.getUpdateCount());
        }
        //
        if (this.processType == MultipleProcessType.FIRST) {
            return resultList;
        }
        //
        int resultIndex = 1;
        while ((stmt.getMoreResults()) || (stmt.getUpdateCount() != -1)) {
            int updateCount = stmt.getUpdateCount();
            Object last = null;
            try (ResultSet resultSet = stmt.getResultSet()) {
                if (resultSet != null) {
                    if (this.rowMappers.size() > resultIndex) {
                        last = processResultSet(resultSet, this.rowMappers.get(resultIndex++));
                    } else {
                        last = processResultSet(resultSet, getDefaultRowMapper());
                    }
                } else {
                    last = updateCount;
                }
            }
            //
            if (this.processType == MultipleProcessType.LAST) {
                resultList.set(0, last);
            } else {
                resultList.add(last);
            }
        }
        return resultList;
    }

    protected RowMapper<?> getDefaultRowMapper() {
        return new ColumnMapRowMapper(TypeHandlerRegistry.DEFAULT);
    }

    /**
     * Process the given ResultSet from a stored procedure.
     * @param rs the ResultSet to process
     * @param rowMapper the corresponding stored procedure parameter
     * @return a Map that contains returned results
     */
    protected static Object processResultSet(ResultSet rs, RowMapper<?> rowMapper) throws SQLException {
        if (rs == null) {
            return null;
        }
        return new RowMapperResultSetExtractor<>(rowMapper).extractData(rs);
    }
}
