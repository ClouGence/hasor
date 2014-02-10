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
package net.hasor.jdbc.template.core.mapper;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Hasor;
import net.hasor.jdbc.template.RowMapper;
import net.hasor.jdbc.template.core.util.JdbcUtils;
import org.more.UnhandledException;
import org.more.util.BeanUtils;
/**
 * 用于 POJO 的 RowMapper
 * @version : 2013-12-18
 * @author 赵永春(zyc@hasor.net)
 */
public class BeanPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T>            requiredType;
    private boolean             caseInsensitive = false;
    private Map<String, String> columnMapping   = new HashMap<String, String>();
    /**
     * Create a new BeanPropertyRowMapper.
     * @see #setRequiredType
     */
    public BeanPropertyRowMapper() {}
    /**
     * Create a new BeanPropertyRowMapper.
     * @param requiredType the type that each result object is expected to match
     */
    public BeanPropertyRowMapper(Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType, "requiredType is null.");
        this.requiredType = requiredType;
        this.loadMapping();
    }
    /** Set the type that each result object is expected to match. <p>If not specified, the column value will be exposed as returned by the JDBC driver.*/
    public void setRequiredType(Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType, "requiredType is null.");
        this.requiredType = requiredType;
        this.loadMapping();
    }
    private void loadMapping() {
        /*借助用属性名统一大写，来实现属性感性。*/
        this.columnMapping.clear();
        List<String> prop = BeanUtils.getPropertysAndFields(this.requiredType);
        for (String pName : prop)
            this.columnMapping.put(pName.toUpperCase(), pName);
    }
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T targetObject = this.requiredType.newInstance();
            return this.tranResultSet(rs, targetObject);
        } catch (Exception e) {
            if (e instanceof SQLException)
                throw (SQLException) e;
            else if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            throw new UnhandledException(e);
        }
    }
    private T tranResultSet(ResultSet rs, T targetObject) throws SQLException, IOException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        for (int i = 1; i < nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            /*处理属性*/
            if (!caseInsensitive)
                colName = this.columnMapping.get(colName.toUpperCase());
            //
            Class<?> paramType = BeanUtils.getPropertyOrFieldType(this.requiredType, colName);
            if (paramType == null)
                continue;
            Object colValue = getColumnValue(rs, i, paramType);
            BeanUtils.writePropertyOrField(targetObject, colName, colValue);
        }
        return targetObject;
    }
    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation calls
     * {@link JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)}.
     * If no required type has been specified, this method delegates to
     * <code>getColumnValue(rs, index)</code>, which basically calls
     * <code>ResultSet.getObject(index)</code> but applies some additional
     * default conversion to appropriate value types.
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @param requiredType the type that each result object is expected to match
     * (or <code>null</code> if none specified)
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see net.hasor.jdbc.jdbc.core.util.support.noe.platform.modules.db.jdbcorm.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, Class)
     * @see #getColumnValue(java.sql.ResultSet, int)
     */
    protected Object getColumnValue(ResultSet rs, int index, Class requiredType) throws SQLException {
        if (requiredType != null) {
            return JdbcUtils.getResultSetValue(rs, index, requiredType);
        } else {
            // No required type specified -> perform default extraction.
            return getColumnValue(rs, index);
        }
    }
    /**
     * Retrieve a JDBC object value for the specified column, using the most
     * appropriate value type. Called if no required type has been specified.
     * <p>The default implementation delegates to <code>JdbcUtils.getResultSetValue()</code>,
     * which uses the <code>ResultSet.getObject(index)</code> method. Additionally,
     * it includes a "hack" to get around Oracle returning a non-standard object for
     * their TIMESTAMP datatype. See the <code>JdbcUtils#getResultSetValue()</code>
     * javadoc for details.
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @return the Object value
     * @throws SQLException in case of extraction failure
     * @see net.hasor.jdbc.jdbc.core.util.support.noe.platform.modules.db.jdbcorm.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int)
     */
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index);
    }
    /**
     * Static factory method to create a new BeanPropertyRowMapper
     * (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        BeanPropertyRowMapper<T> newInstance = new BeanPropertyRowMapper<T>();
        newInstance.setRequiredType(mappedClass);
        return newInstance;
    }
}