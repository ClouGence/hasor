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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 映射注册器。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MappingRegistry {
    public final static MappingRegistry             DEFAULT = new MappingRegistry(TypeHandlerRegistry.DEFAULT);
    protected final     TypeHandlerRegistry         typeRegistry;
    protected final     Map<Class<?>, TableMapping> entityMappingMap;

    public MappingRegistry() {
        this(TypeHandlerRegistry.DEFAULT);
    }

    public MappingRegistry(TypeHandlerRegistry typeRegistry) {
        this.typeRegistry = Objects.requireNonNull(typeRegistry, "typeRegistry not null.");
        this.entityMappingMap = new ConcurrentHashMap<>();
    }

    public TypeHandlerRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public TableMapping resolveMapping(Class<?> entityType) {
        return resolveMapping(entityType, null);
    }

    public TableMapping resolveMapping(Class<?> entityType, MetaDataService metaDataService) {
        return this.entityMappingMap.computeIfAbsent(entityType, aClass -> {
            return parserEntity(entityType, metaDataService);
        });
    }

    private TableMapping parserEntity(Class<?> entityType, MetaDataService metaDataService) {
        boolean useDelimited;
        CaseSensitivityType caseSensitivity;
        TableMappingDef def = new TableMappingDef(entityType, false);
        //
        // build MappingDef
        if (entityType.isAnnotationPresent(Table.class)) {
            Table defTable = entityType.getAnnotation(Table.class);
            String category = defTable.schema().trim();
            String tableName = StringUtils.isNotBlank(defTable.name()) ? defTable.name() : defTable.value();
            def.setCategory(category);
            def.setTableName(tableName);
            def.setAutoProperty(defTable.autoProperty());
            useDelimited = defTable.useDelimited();
        } else {
            def.setCategory("");
            def.setTableName(entityType.getSimpleName());
            def.setAutoProperty(true);
            useDelimited = false;
        }
        //
        // make sure CaseSensitivity
        if (metaDataService != null) {
            if (useDelimited) {
                caseSensitivity = caseSensitivity(metaDataService.getDelimited());
            } else {
                caseSensitivity = caseSensitivity(metaDataService.getPlain());
            }
        } else {
            caseSensitivity = CaseSensitivityType.Exact;
        }
        def.setUseDelimited(useDelimited);
        def.setCaseSensitivity(caseSensitivity);
        //
        // modify the names by referring to the metadata.
        if (metaDataService != null) {
            TableDef tableDef = metaDataService.searchTable(def.getCategory(), def.getTableName());
            if (tableDef != null) {
                def.setCategory(tableDef.getCategory());
                def.setTableName(tableDef.getTableName());
            }
        } else {
            def.setCategory(formatCaseSensitivity(def.getCategory(), def.getCaseSensitivity()));
            def.setTableName(formatCaseSensitivity(def.getTableName(), def.getCaseSensitivity()));
        }
        //
        return parserProperty(def, metaDataService);
    }

    private TableMapping parserProperty(TableMappingDef def, MetaDataService metaDataService) {
        // collect @Property and ColumnDef
        Map<String, WrapProperty> propertyInfoMap = matchProperty(def, def.isAutoProperty(), this.typeRegistry);
        Map<String, ColumnDef> columnDefMap = null;
        if (metaDataService != null) {
            columnDefMap = metaDataService.getColumnMap(def.getCategory(), def.getTableName());
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
                columnDef = convertColumnDef(wrapProperty, def.getCaseSensitivity());
            }
            if (columnDef == null) {
                continue;
            }
            //
            // build PropertyMapping
            Class<?> propertyType = wrapProperty.propertyField.getType();
            PropertyMappingDef mappingDef = new PropertyMappingDef(propertyName, propertyType, columnDef.getName());
            mappingDef.setJdbcType(columnDef.getJdbcType());
            mappingDef.setPrimary(columnDef.isPrimaryKey());
            mappingDef.setInsert(wrapProperty.property.insert());
            mappingDef.setUpdate(wrapProperty.property.update());
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
        public final Property       property;
        public final Field          propertyField;
        public final TypeHandler<?> typeHandler;

        public WrapProperty(Property property, Field propertyField, TypeHandler<?> typeHandler) {
            this.property = Objects.requireNonNull(property, "property is not null.");
            this.propertyField = Objects.requireNonNull(propertyField, "propertyField is not null.");
            this.typeHandler = Objects.requireNonNull(typeHandler, "typeHandler is not null.");
        }
    }

    private static ColumnDef convertColumnDef(WrapProperty wrapProperty, CaseSensitivityType caseSensitivity) {
        Field propertyField = wrapProperty.propertyField;
        Class<?> propertyType = propertyField.getType();
        //
        SimpleColumnDef columnDef = new SimpleColumnDef();
        columnDef.setName(formatCaseSensitivity(propertyField.getName(), caseSensitivity));
        columnDef.setJavaType(propertyType);
        columnDef.setJdbcType(TypeHandlerRegistry.toSqlType(propertyType));
        columnDef.setPrimary(false);
        return columnDef;
    }

    private static Map<String, WrapProperty> matchProperty(TableMappingDef def, boolean includeAll, TypeHandlerRegistry typeRegistry) {
        Map<String, WrapProperty> propertyMap = new LinkedHashMap<>();
        List<String> pojoProperty = BeanUtils.getPropertys(def.entityType());
        for (String name : pojoProperty) {
            Field propertyField = BeanUtils.getField(name, def.entityType());
            if (propertyField == null) {
                continue;
            }
            Property info = null;
            if (propertyField.isAnnotationPresent(Property.class)) {
                info = propertyField.getAnnotation(Property.class);
            } else if (includeAll) {
                info = new PropertyInfo(name);
            } else {
                continue;
            }
            //
            Class<?> propertyType = propertyField.getType();
            Class<? extends TypeHandler<?>> typeHandlerClass = info.typeHandler();
            TypeHandler<?> typeHandler = null;
            if (typeHandlerClass == UnknownTypeHandler.class) {
                JDBCType jdbcType = info.jdbcType();
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
            propertyMap.put(name, new WrapProperty(info, propertyField, typeHandler));
        }
        return propertyMap;
    }

    private static String getColumnName(String propertyName, Map<String, WrapProperty> propertyInfoMap) {
        WrapProperty wrapProperty = propertyInfoMap.get(propertyName);
        if (wrapProperty != null) {
            Property property = wrapProperty.property;
            String columnName = propertyName;
            if (StringUtils.isNotBlank(property.name())) {
                columnName = property.name();
            } else {
                columnName = property.value();
            }
            if (StringUtils.isBlank(columnName)) {
                columnName = propertyName;
            }
            return columnName;
        } else {
            return propertyName;
        }
    }

    private static CaseSensitivityType caseSensitivity(CaseSensitivityType type) {
        return (type == null) ? CaseSensitivityType.Exact : type;
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
