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
package net.hasor.db.mapping;
import net.hasor.db.jdbc.RowMapper;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.db.types.UnknownTypeHandler;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用于 POJO 的 RowMapper，带有 ORM 能力
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRowMapper<T> implements RowMapper<T>, TableMapping {
    private final Class<T>                    mapperClass;
    private       String                      category;
    private       String                      tableName;
    private       boolean                     useQualifier;
    private       boolean                     caseInsensitive;
    //
    private final List<String>                propertyNames;
    private final Map<String, String>         propertyColumnMapping;
    private final Map<String, ColumnMapping>  propertyFieldInfoMap;
    private final Map<String, TypeHandler<?>> propertyTypeHandlerMap;
    //
    private final List<String>                columnNames;
    private final Map<String, List<String>>   columnPropertyMapping;
    private final Map<String, String>         columnPropertyMappingForWrite;
    //

    /** Create a new ResultMapper.*/
    public MappingRowMapper(Class<T> mapperClass) {
        this(mapperClass, TypeHandlerRegistry.DEFAULT);
    }

    /** Create a new ResultMapper.*/
    public MappingRowMapper(Class<T> mapperClass, TypeHandlerRegistry handlerRegistry) {
        this.mapperClass = mapperClass;
        this.caseInsensitive = true;
        this.propertyNames = new ArrayList<>();
        this.propertyColumnMapping = new HashMap<>();
        this.propertyFieldInfoMap = new HashMap<>();
        this.propertyTypeHandlerMap = new HashMap<>();
        this.columnPropertyMappingForWrite = new HashMap<>();
        this.columnNames = new ArrayList<>();
        this.columnPropertyMapping = new HashMap<>();
        this.initialize(mapperClass, Objects.requireNonNull(handlerRegistry, "handlerRegistry is null."));
    }

    private void initialize(Class<T> mapperClass, TypeHandlerRegistry registry) {
        Table defTable = null;
        if (mapperClass.isAnnotationPresent(Table.class)) {
            defTable = mapperClass.getAnnotation(Table.class);
        } else {
            defTable = new TableImpl("", mapperClass.getSimpleName());
        }
        this.category = defTable.category().trim();
        this.tableName = StringUtils.isNotBlank(defTable.name()) ? defTable.name() : defTable.value();
        this.useQualifier = defTable.useQualifier();
        boolean autoConfigField = defTable.autoFiled();
        List<java.lang.reflect.Field> allFields = BeanUtils.findALLFields(mapperClass);
        for (java.lang.reflect.Field field : allFields) {
            Field defField = defField(field, autoConfigField);
            if (defField == null) {
                continue;
            }
            //
            TypeHandler<?> typeHandler = null;
            if (defField.typeHandler() == UnknownTypeHandler.class) {
                typeHandler = registry.getTypeHandler(field.getType(), defField.jdbcType());
            } else {
                try {
                    typeHandler = defField.typeHandler().newInstance();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
            this.setupField(field, defField, typeHandler);
        }
    }

    protected Field defField(java.lang.reflect.Field dtoField, boolean autoConfigField) {
        if (dtoField.isAnnotationPresent(Field.class)) {
            return dtoField.getAnnotation(Field.class);
        } else if (autoConfigField) {
            Class<?> fieldType = dtoField.getType();
            JDBCType jdbcType = TypeHandlerRegistry.toSqlType(fieldType);
            return new FieldImpl(dtoField.getName(), jdbcType);
        } else {
            return null;
        }
    }

    private void setupField(java.lang.reflect.Field property, Field defField, TypeHandler<?> toTypeHandler) {
        String propertyName = property.getName();
        String columnName = null;
        JDBCType jdbcType = defField.jdbcType();
        if (StringUtils.isNotBlank(defField.name())) {
            columnName = defField.name();
        } else {
            columnName = defField.value();
        }
        if (StringUtils.isBlank(columnName)) {
            columnName = propertyName;
        }
        if (jdbcType == JDBCType.OTHER) {
            jdbcType = TypeHandlerRegistry.toSqlType(property.getType());
        }
        //
        String useColumnName = columnName;
        ColumnMapping columnMapping = new InnerColumnMapping(useColumnName, propertyName, jdbcType, property.getType(), defField.insert(), defField.update(), false);
        //
        this.propertyNames.add(propertyName);
        this.propertyColumnMapping.put(propertyName, useColumnName);
        this.propertyFieldInfoMap.put(propertyName, columnMapping);
        this.propertyTypeHandlerMap.put(propertyName, toTypeHandler);
        //
        if (!this.columnNames.contains(useColumnName)) {
            this.columnNames.add(useColumnName);
        }
        List<String> stringList = this.columnPropertyMapping.computeIfAbsent(useColumnName, k -> new ArrayList<>());
        stringList.add(propertyName);
        if (columnMapping.isInsert() || columnMapping.isUpdate()) {
            if (this.columnPropertyMappingForWrite.containsKey(useColumnName)) {
                String differentProperty = "'" + propertyName + "','" + this.columnPropertyMappingForWrite.get(useColumnName) + "'";
                throw new IllegalStateException("mapping different property " + differentProperty + " write the same column '" + useColumnName + "'.");
            } else {
                this.columnPropertyMappingForWrite.put(useColumnName, propertyName);
            }
        }
    }

    public Class<T> getMapperClass() {
        return this.mapperClass;
    }

    @Override
    public String getCategory() {
        return this.category;
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

    public TableMapping getTableInfo() {
        return this;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<String> getPropertyNames() {
        return propertyNames;
    }

    public ColumnMapping findFieldByProperty(String propertyName) {
        return this.propertyFieldInfoMap.get(propertyName);
    }

    public List<ColumnMapping> findFieldByColumn(String columnName) {
        List<String> propertyNames = this.columnPropertyMapping.get(columnName);
        if (propertyNames == null) {
            return null;
        }
        return propertyNames.stream()//
                .map(this.propertyFieldInfoMap::get)//
                .filter(Objects::nonNull)//
                .collect(Collectors.toList());
    }

    public ColumnMapping findWriteFieldByColumn(String columnName) {
        String propertyName = this.columnPropertyMappingForWrite.get(columnName);
        return this.propertyFieldInfoMap.get(propertyName);
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
        Map<String, Integer> resultColumnMap = this.caseInsensitive ? new LinkedCaseInsensitiveMap<>() : new LinkedHashMap<>();
        for (int i = 1; i <= nrOfColumns; i++) {
            String colName = rsmd.getColumnName(i);
            if (!resultColumnMap.containsKey(colName)) {
                resultColumnMap.put(colName, i);
            }
        }
        //
        for (String columnName : this.columnNames) {
            if (!resultColumnMap.containsKey(columnName)) {
                continue;
            }
            int realIndex = resultColumnMap.get(columnName);
            List<String> propertyNames = this.columnPropertyMapping.get(columnName);
            for (String propertyName : propertyNames) {
                TypeHandler<?> realHandler = this.propertyTypeHandlerMap.get(propertyName);
                Object result = realHandler.getResult(rs, realIndex);
                //
                Class<?> propertyType = BeanUtils.getPropertyOrFieldType(this.mapperClass, propertyName);
                Object convert = ConverterUtils.convert(propertyType, result);
                BeanUtils.writePropertyOrField(targetObject, propertyName, convert);
            }
        }
        return targetObject;
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> MappingRowMapper<T> newInstance(final Class<T> mappedClass) {
        return MappingHandler.DEFAULT.resolveMapper(mappedClass);
    }

    /**
     * Static factory method to create a new BeanPropertyRowMapper (with the mapped class specified only once).
     * @param mappedClass the class that each row should be mapped to
     */
    public static <T> MappingRowMapper<T> newInstance(final Class<T> mappedClass, final TypeHandlerRegistry registry) {
        return new MappingHandler(registry).resolveMapper(mappedClass);
    }
}
