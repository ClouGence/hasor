/*
 * Copyright 2002-2009 the original author or authors.
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
package net.hasor.db.jdbc.mapper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
/**
 *
 * @version : 2014年5月23日
 * @author 赵永春 (zyc@byshell.org)
 */
public class SingleColumnRowMapper<T> extends AbstractRowMapper<T> {
    private Class<T> requiredType;
    /** Create a new SingleColumnRowMapper. */
    public SingleColumnRowMapper() {
    }
    /**
     * Create a new SingleColumnRowMapper.
     * @param requiredType the type that each result object is expected to match
     */
    public SingleColumnRowMapper(final Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    /** Set the type that each result object is expected to match. <p>If not specified, the column value will be exposed as returned by the JDBC driver. */
    public void setRequiredType(final Class<T> requiredType) {
        this.requiredType = requiredType;
    }
    //
    /**将当前行的第一列的值转换为指定的类型。*/
    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        //1.Validate column count.
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1) {
            throw new SQLException("Incorrect column count: expected 1, actual " + nrOfColumns);
        }
        //2.Extract column value from JDBC ResultSet.
        Object result = getResultSetValue(rs, 1);
        if (this.requiredType != null) {
            if (result != null && this.requiredType != null && !this.requiredType.isInstance(result)) {
                result = convertValueToRequiredType(result, this.requiredType);
            }
        }
        //3.Return
        return (T) result;
    }
}