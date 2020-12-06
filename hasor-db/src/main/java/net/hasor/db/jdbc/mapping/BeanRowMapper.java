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
package net.hasor.db.jdbc.mapping;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.jdbc.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 用于 POJO 的 RowMapper，带有 ORM 能力
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class BeanRowMapper<T> implements RowMapper<T>, TableInfo {
    private final Class<T>                    mapperClass;
    private       String                      tableName;
    private       boolean                     caseInsensitive;
    //
    private final Map<String, String>         propertyColumnMapping;
    private final Map<String, String>         columnPropertyMapping;
    //
    private final List<String>                columnNames;
    private final Map<String, FieldInfo>      columnFieldInfoMap;
    private final Map<String, TypeHandler<?>> columnTypeHandlerMap;

    /** Create a new ResultMapper.*/
    public BeanRowMapper(Class<T> mapperClass) {
        this.mapperClass = mapperClass;
        this.columnNames = new ArrayList<>();
        this.columnFieldInfoMap = new HashMap<>();
        this.columnTypeHandlerMap = new HashMap<>();
        this.propertyColumnMapping = new HashMap<>();
        this.columnPropertyMapping = new HashMap<>();
    }

    public Class<T> getMapperClass() {
        return this.mapperClass;
    }

    public String getTableName() {
        return this.tableName;
    }

    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    public FieldInfo findFieldInfoByProperty(String propertyName) {
        String columnName = this.propertyColumnMapping.get(propertyName);
        return this.columnFieldInfoMap.get(columnName);
    }

    public Collection<FieldInfo> allFieldInfoByProperty() {
        return this.columnFieldInfoMap.values();
    }

    public TableInfo getTableInfo() {
        return this;
    }

    void setupTable(Table defTable) {
        if (StringUtils.isNotBlank(defTable.name())) {
            this.tableName = defTable.name();
        } else {
            this.tableName = defTable.value();
        }
        this.caseInsensitive = defTable.caseInsensitive();
    }

    void setupField(java.lang.reflect.Field property, net.hasor.db.jdbc.mapping.Field defField, TypeHandler<?> toTypeHandler) {
        String columnName = null;
        JDBCType jdbcType = defField.jdbcType();
        if (StringUtils.isNotBlank(defField.name())) {
            columnName = defField.name();
        } else {
            columnName = defField.value();
        }
        if (StringUtils.isBlank(columnName)) {
            columnName = property.getName();
        }
        if (jdbcType == JDBCType.OTHER) {
            jdbcType = TypeHandlerRegistry.DEFAULT.toSqlType(property.getType());
        }
        //
        String useColumnName = columnName;
        if (this.caseInsensitive) {
            useColumnName = useColumnName.toUpperCase();
        }
        this.columnNames.add(useColumnName);
        this.columnFieldInfoMap.put(useColumnName, new FieldInfoImpl(columnName, jdbcType, property.getType()));
        this.columnTypeHandlerMap.put(useColumnName, toTypeHandler);
        this.columnPropertyMapping.put(useColumnName, property.getName());
        this.propertyColumnMapping.put(property.getName(), useColumnName);
    }

    @Override
    public T mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        T targetObject;
        try {
            targetObject = this.mapperClass.newInstance();
            return this.tranResultSet(rs, targetObject);
        } catch (ReflectiveOperationException e) {
            throw new SQLException(e);
        }
    }

    private T tranResultSet(final ResultSet rs, final T targetObject) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        Map<String, Integer> resultColumnMap = new HashMap<>();
        for (int i = 1; i <= nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            if (this.caseInsensitive) {
                resultColumnMap.put(colName.toUpperCase(), i);
            } else {
                resultColumnMap.put(colName, i);
            }
        }
        //
        for (String columnName : this.columnNames) {
            if (!resultColumnMap.containsKey(columnName)) {
                continue;
            }
            int realIndex = resultColumnMap.get(columnName);
            TypeHandler<?> realHandler = this.columnTypeHandlerMap.get(columnName);
            Object result = realHandler.getResult(rs, realIndex);
            //
            String propertyName = this.columnPropertyMapping.get(columnName);
            Class<?> propertyType = BeanUtils.getPropertyOrFieldType(this.mapperClass, propertyName);
            Object convert = ConverterUtils.convert(propertyType, result);
            BeanUtils.writePropertyOrField(targetObject, propertyName, convert);
        }
        return targetObject;
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanRowMapper<T> newInstance(final Class<T> mappedClass) {
        return newInstance(mappedClass, TypeHandlerRegistry.DEFAULT);
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> BeanRowMapper<T> newInstance(final Class<T> mappedClass, final TypeHandlerRegistry registry) {
        MappingHandler handler = new MappingHandler(registry);
        return handler.resolveMapper(mappedClass);
    }
}
