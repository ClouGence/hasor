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
import net.hasor.db.metadata.*;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.db.types.UnknownTypeHandler;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射注册器。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRegistry {
    public final static MappingRegistry               DEFAULT = new MappingRegistry(TypeHandlerRegistry.DEFAULT, null);
    protected final     TypeHandlerRegistry           typeRegistry;
    protected final     MetaDataService               metaService;
    protected final     Map<Class<?>, TableMapping>   entityMappingMap;
    protected final     Map<Class<?>, TableReader<?>> entityReaderMap;

    public MappingRegistry() {
        this(TypeHandlerRegistry.DEFAULT);
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry) {
        this(typeRegistry, null);
    }

    public MappingRegistry(MetaDataService metaDataService) {
        this.typeRegistry = TypeHandlerRegistry.DEFAULT;
        this.metaService = metaDataService;
        this.entityMappingMap = new ConcurrentHashMap<>();
        this.entityReaderMap = new ConcurrentHashMap<>();
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry, MetaDataService metaDataService) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.metaService = metaDataService;
        this.entityMappingMap = new ConcurrentHashMap<>();
        this.entityReaderMap = new ConcurrentHashMap<>();
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public <T> TableReader<T> resolveTableReader(Class<T> entityType) {
        return resolveTableReader(entityType, this.metaService);
    }

    public TableMapping resolveMapping(Class<?> entityType) {
        return resolveMapping(entityType, this.metaService);
    }

    public <T> TableReader<T> resolveTableReader(Class<T> entityType, MetaDataService metaDataService) {
        TableReader<T> resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
        if (resultMapper == null) {
            synchronized (this) {
                resultMapper = (TableReader<T>) this.entityReaderMap.get(entityType);
                if (resultMapper != null) {
                    return resultMapper;
                }
                TableMapping tableMapping = resolveMapping(entityType, metaDataService);
                if (tableMapping == null) {
                    return null;
                }
                resultMapper = new TableReaderImpl<>(entityType, tableMapping);
                //
                this.entityReaderMap.put(entityType, resultMapper);
            }
        }
        return resultMapper;
    }

    public TableMapping resolveMapping(Class<?> entityType, MetaDataService metaDataService) {
        TableMapping tableMapping = this.entityMappingMap.get(entityType);
        if (tableMapping == null) {
            synchronized (this) {
                tableMapping = this.entityMappingMap.get(entityType);
                if (tableMapping != null) {
                    return tableMapping;
                }
                try {
                    tableMapping = parserEntity(entityType, metaDataService);
                    this.entityMappingMap.put(entityType, tableMapping);
                } catch (SQLException e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        }
        return tableMapping;
    }

    private TableMapping parserEntity(Class<?> entityType, MetaDataService metaDataService) throws SQLException {
        boolean useDelimited;
        CaseSensitivityType caseSensitivity;
        TableMappingDef def = new TableMappingDef(entityType);
        //
        // build MappingDef
        if (entityType.isAnnotationPresent(Table.class)) {
            Table defTable = entityType.getAnnotation(Table.class);
            String schema = defTable.schema().trim();
            String tableName = StringUtils.isNotBlank(defTable.name()) ? defTable.name() : defTable.value();
            def.setSchema(schema);
            def.setTable(tableName);
            def.setAutoProperty(defTable.autoMapping());
            useDelimited = defTable.useDelimited();
        } else {
            def.setSchema("");
            def.setTable(entityType.getSimpleName());
            def.setAutoProperty(true);
            useDelimited = false;
        }
        //
        // make sure CaseSensitivity
        if (metaDataService != null) {
            if (useDelimited) {
                // in delimited CaseSensitivity can not be Fuzzy
                caseSensitivity = caseSensitivity(metaDataService.getDelimited(), CaseSensitivityType.Exact);
            } else {
                caseSensitivity = caseSensitivity(metaDataService.getPlain(), CaseSensitivityType.Fuzzy);
            }
        } else {
            caseSensitivity = CaseSensitivityType.Fuzzy;
        }
        def.setUseDelimited(useDelimited);
        def.setCaseSensitivity(caseSensitivity);
        //
        // modify the names by referring to the metadata.
        if (metaDataService != null) {
            String schema = def.getSchema();
            if (StringUtils.isBlank(schema)) {
                schema = metaDataService.getCurrentSchema();
            }
            schema = formatCaseSensitivity(schema, def.getCaseSensitivity());
            String tableName = formatCaseSensitivity(def.getTable(), def.getCaseSensitivity());
            TableDef tableDef = metaDataService.searchTable(schema, tableName);
            if (tableDef != null) {
                def.setSchema(tableDef.getSchema());
                def.setTable(tableDef.getTable());
            }
        } else {
            def.setSchema(formatCaseSensitivity(def.getSchema(), def.getCaseSensitivity()));
            def.setTable(formatCaseSensitivity(def.getTable(), def.getCaseSensitivity()));
        }
        //
        return parserProperty(def, metaDataService);
    }

    private TableMapping parserProperty(TableMappingDef def, MetaDataService metaDataService) throws SQLException {
        // collect @Property and ColumnDef
        Map<String, WrapProperty> propertyInfoMap = matchProperty(def, def.isAutoProperty(), this.typeRegistry);
        Map<String, ColumnDef> columnDefMap = null;
        if (metaDataService != null) {
            columnDefMap = metaDataService.getColumnMap(def.getSchema(), def.getTable());
        }
        //
        for (String propertyName : propertyInfoMap.keySet()) {
            WrapProperty wrapProperty = propertyInfoMap.get(propertyName);
            String columnName = getColumnName(propertyName, propertyInfoMap);
            columnName = formatCaseSensitivity(columnName, def.getCaseSensitivity());
            //
            ColumnDef columnDef = null;
            if (columnDefMap != null && columnDefMap.containsKey(columnName)) {
                columnDef = columnDefMap.get(columnName);
            } else {
                columnDef = convertColumnDef(columnName, wrapProperty, def.getCaseSensitivity());
            }
            if (columnDef == null) {
                continue;
            }
            //
            // build PropertyMapping
            Class<?> propertyType = wrapProperty.propertyField.getType();
            ColumnMappingDef mappingDef = new ColumnMappingDef(propertyName, propertyType, columnDef.getName());
            mappingDef.setJdbcType(columnDef.getJdbcType());
            mappingDef.setPrimary(columnDef.isPrimaryKey());
            mappingDef.setInsert(wrapProperty.column.insert());
            mappingDef.setUpdate(wrapProperty.column.update());
            mappingDef.setTypeHandler(wrapProperty.typeHandler);
            //
            // add to def
            def.addMapping(mappingDef);
            //
            if (def.isEmpty()) {
                throw new IllegalStateException(def.entityType().getName() + " Missing property mapping.");
            }
        }
        return def;
    }

    private static class WrapProperty {
        public final Column         column;
        public final ColumnMeta     columnMeta;
        public final Field          propertyField;
        public final TypeHandler<?> typeHandler;

        public WrapProperty(Column column, ColumnMeta columnMeta, Field propertyField, TypeHandler<?> typeHandler) {
            this.column = Objects.requireNonNull(column, "property is not null.");
            this.columnMeta = columnMeta;
            this.propertyField = Objects.requireNonNull(propertyField, "propertyField is not null.");
            this.typeHandler = Objects.requireNonNull(typeHandler, "typeHandler is not null.");
        }
    }

    private static ColumnDef convertColumnDef(String columnName, WrapProperty wrapProperty, CaseSensitivityType caseSensitivity) {
        Field propertyField = wrapProperty.propertyField;
        ColumnMeta columnMeta = wrapProperty.columnMeta;
        Class<?> propertyType = propertyField.getType();
        //
        SimpleColumnDef columnDef = new SimpleColumnDef();
        columnDef.setName(formatCaseSensitivity(columnName, caseSensitivity));
        columnDef.setJavaType(propertyType);
        columnDef.setJdbcType(TypeHandlerRegistry.toSqlType(propertyType));
        if (columnMeta != null) {
            columnDef.setPrimary(columnMeta.primary());
        } else {
            columnDef.setPrimary(false);
        }
        return columnDef;
    }

    private static Map<String, WrapProperty> matchProperty(TableMappingDef def, boolean includeAll, TypeHandlerRegistry typeRegistry) {
        Map<String, WrapProperty> propertyMap = new LinkedHashMap<>();
        //
        // keep sort
        List<String> targetProperties = new ArrayList<>();
        List<String> sourceProperties = BeanUtils.getProperties(def.entityType());
        List<Field> sourceFields = BeanUtils.findALLFields(def.entityType());
        for (Field source : sourceFields) {
            if (!targetProperties.contains(source.getName())) {
                targetProperties.add(source.getName());
            }
        }
        for (String source : sourceProperties) {
            if (!targetProperties.contains(source)) {
                targetProperties.add(source);
            }
        }
        //
        for (String name : targetProperties) {
            Field propertyField = BeanUtils.getField(name, def.entityType());
            if (propertyField == null) {
                continue;
            }
            Column info = null;
            if (propertyField.isAnnotationPresent(Column.class)) {
                info = propertyField.getAnnotation(Column.class);
            } else if (includeAll) {
                info = new ColumnInfo(name);
            } else {
                continue;
            }
            ColumnMeta columnMeta = null;
            if (propertyField.isAnnotationPresent(ColumnMeta.class)) {
                columnMeta = propertyField.getAnnotation(ColumnMeta.class);
            }
            //
            Class<?> propertyType = propertyField.getType();
            Class<? extends TypeHandler<?>> typeHandlerClass = info.typeHandler();
            TypeHandler<?> typeHandler = null;
            if (typeHandlerClass == UnknownTypeHandler.class) {
                JDBCType jdbcType = (columnMeta != null) ? columnMeta.jdbcType() : JDBCType.OTHER;
                if (jdbcType == JDBCType.OTHER) {
                    jdbcType = TypeHandlerRegistry.toSqlType(propertyType);
                }
                typeHandler = typeRegistry.getTypeHandler(propertyType, jdbcType);
            } else if (TypeHandlerRegistry.hasTypeHandlerType(typeHandlerClass)) {
                typeHandler = TypeHandlerRegistry.getTypeHandlerByType(typeHandlerClass);
            } else {
                try {
                    typeHandler = typeHandlerClass.newInstance();
                } catch (Exception e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
            //typeRegistry
            propertyMap.put(name, new WrapProperty(info, columnMeta, propertyField, typeHandler));
        }
        return propertyMap;
    }

    private static String getColumnName(String propertyName, Map<String, WrapProperty> propertyInfoMap) {
        WrapProperty wrapProperty = propertyInfoMap.get(propertyName);
        if (wrapProperty != null) {
            Column column = wrapProperty.column;
            String columnName = propertyName;
            if (StringUtils.isNotBlank(column.name())) {
                columnName = column.name();
            } else {
                columnName = column.value();
            }
            if (StringUtils.isBlank(columnName)) {
                columnName = propertyName;
            }
            return columnName;
        } else {
            return propertyName;
        }
    }

    private static CaseSensitivityType caseSensitivity(CaseSensitivityType check, CaseSensitivityType defaultType) {
        return (check == null) ? defaultType : check;
    }

    private static String formatCaseSensitivity(String dataString, CaseSensitivityType sensitivityType) {
        if (sensitivityType == null) {
            return dataString;
        }
        switch (sensitivityType) {
            case Lower: {
                return dataString.toLowerCase();
            }
            case Upper: {
                return dataString.toUpperCase();
            }
            default: {
                return dataString;
            }
        }
    }
}
