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
import net.hasor.core.Hasor;
import net.hasor.utils.BeanUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 用于 POJO 的 RowMapper
 * @version : 2013-12-18
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanPropertyRowMapper<T> extends AbstractRowMapper<T> {
    private Class<T> requiredType;
    private boolean             caseInsensitive = false;
    private Map<String, String> columnMapping   = new HashMap<String, String>();
    //
    /** Create a new BeanPropertyRowMapper.*/
    public BeanPropertyRowMapper() {
    }
    /** Create a new BeanPropertyRowMapper.*/
    public BeanPropertyRowMapper(final Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType, "requiredType is null.");
        this.requiredType = requiredType;
        this.loadMapping();
    }
    /** Set the type that each result object is expected to match. <p>If not specified, the column value will be exposed as returned by the JDBC driver.*/
    public void setRequiredType(final Class<T> requiredType) {
        Hasor.assertIsNotNull(requiredType, "requiredType is null.");
        this.requiredType = requiredType;
        this.loadMapping();
    }
    private void loadMapping() {
        /*借助用属性名统一大写，来实现属性感性。*/
        this.columnMapping.clear();
        List<String> prop = BeanUtils.getPropertysAndFields(this.requiredType);
        for (String pName : prop) {
            this.columnMapping.put(pName.toUpperCase(), pName);
        }
    }
    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }
    public void setCaseInsensitive(final boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        T targetObject;
        try {
            targetObject = this.requiredType.newInstance();
            return this.tranResultSet(rs, targetObject);
        } catch (InstantiationException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
    private T tranResultSet(final ResultSet rs, final T targetObject) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        for (int i = 1; i <= nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            /*处理属性*/
            if (!this.caseInsensitive) {
                colName = this.columnMapping.get(colName.toUpperCase());
            }
            Class<?> paramType = BeanUtils.getPropertyOrFieldType(this.requiredType, colName);
            if (paramType == null) {
                continue;
            }
            Object colValue = this.getColumnValue(rs, i, paramType);
            BeanUtils.writePropertyOrField(targetObject, colName, colValue);
        }
        return targetObject;
    }
    /**取得指定列的值*/
    protected Object getColumnValue(final ResultSet rs, final int index, final Class<?> requiredType) throws SQLException {
        Object resultData = getResultSetValue(rs, index);
        if (requiredType != null) {
            return convertValueToRequiredType(resultData, requiredType);
        } else {
            return resultData;
        }
    }
    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanPropertyRowMapper<T> newInstance(final Class<T> mappedClass) {
        BeanPropertyRowMapper<T> newInstance = new BeanPropertyRowMapper<T>();
        newInstance.setRequiredType(mappedClass);
        return newInstance;
    }
}