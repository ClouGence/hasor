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
package net.hasor.db.mapping.resolve;
import net.hasor.db.mapping.*;
import net.hasor.db.metadata.CaseSensitivityType;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.MetaDataService;
import net.hasor.db.metadata.TableDef;
import net.hasor.db.metadata.domain.SimpleColumnDef;
import net.hasor.db.metadata.domain.jdbc.JdbcTableType;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.TypeHandlerRegistry;
import net.hasor.db.types.UnknownTypeHandler;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过 Class 来解析 TableMapping
 * @version : 2021-06-21
 * @author 赵永春 (zyc@hasor.net)
 */
public class ClassResolveTableMapping extends AbstractResolveTableMapping implements ResolveTableMapping<Class<?>> {
    @Override
    public TableMappingDef resolveTableMapping(Class<?> entityType, ClassLoader classLoader, TypeHandlerRegistry typeRegistry, MetaDataService metaDataService, MappingOptions options) throws SQLException {
        options = new MappingOptions(options);
        TableMappingDef def = this.parserTable(entityType, metaDataService, options);
        return parserProperty(def, typeRegistry, metaDataService, options);
    }

    public TableMappingDef parserTable(Class<?> entityType, MetaDataService metaDataService, MappingOptions options) throws SQLException {
        boolean useDelimited;
        CaseSensitivityType caseSensitivity;
        TableMappingDef def = new TableMappingDef(entityType);
        //
        // build MappingDef
        if (entityType.isAnnotationPresent(Table.class)) {
            Table defTable = entityType.getAnnotation(Table.class);
            if (options.getMapUnderscoreToCamelCase() == null || !options.getMapUnderscoreToCamelCase()) {
                options.setMapUnderscoreToCamelCase(defTable.mapUnderscoreToCamelCase());
            }
            String catalog = defTable.catalog();
            String schema = defTable.schema();
            String table = StringUtils.isNotBlank(defTable.name()) ? defTable.name() : defTable.value();
            //
            def.setCatalog(StringUtils.isNotBlank(catalog) ? catalog : null);
            def.setSchema(StringUtils.isNotBlank(schema) ? schema : null);
            def.setTable(table);
            def.setTableType(JdbcTableType.Table);
            def.setAutoProperty(defTable.autoMapping());
            useDelimited = defTable.useDelimited();
        } else {
            def.setCatalog(null);
            def.setSchema(null);
            def.setTable(humpToLine(entityType.getSimpleName(), options.getMapUnderscoreToCamelCase()));
            def.setTableType(null);
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
            String catalog = def.getCatalog();
            String schema = def.getSchema();
            String table = def.getTable();
            //
            if (StringUtils.isBlank(catalog)) {
                catalog = metaDataService.getCurrentCatalog();
            }
            if (StringUtils.isBlank(schema)) {
                schema = metaDataService.getCurrentSchema();
            }
            //
            catalog = formatCaseSensitivity(catalog, def.getCaseSensitivity());
            schema = formatCaseSensitivity(schema, def.getCaseSensitivity());
            table = formatCaseSensitivity(table, def.getCaseSensitivity());
            TableDef tableDef = metaDataService.searchTable(catalog, schema, table);
            if (tableDef != null) {
                def.setCatalog(tableDef.getCatalog());
                def.setSchema(tableDef.getSchema());
                def.setTable(tableDef.getTable());
                def.setTableType(tableDef.getTableType());
            }
        } else {
            def.setCatalog(formatCaseSensitivity(def.getCatalog(), def.getCaseSensitivity()));
            def.setSchema(formatCaseSensitivity(def.getSchema(), def.getCaseSensitivity()));
            def.setTable(formatCaseSensitivity(def.getTable(), def.getCaseSensitivity()));
        }
        //
        return def;
    }

    private TableMappingDef parserProperty(TableMappingDef def, TypeHandlerRegistry typeRegistry, MetaDataService metaDataService, MappingOptions options) throws SQLException {
        // collect @Property and ColumnDef
        Map<String, WrapProperty> propertyInfoMap = matchProperty(def, def.isAutoProperty(), typeRegistry);
        Map<String, ColumnDef> columnDefMap = null;
        if (metaDataService != null) {
            columnDefMap = metaDataService.getColumnMap(def.getCatalog(), def.getSchema(), def.getTable());
        }
        //
        for (String propertyName : propertyInfoMap.keySet()) {
            WrapProperty wrapProperty = propertyInfoMap.get(propertyName);
            String columnName = getColumnName(propertyName, propertyInfoMap, options);
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
            ColumnMappingDef mappingDef = new ColumnMappingDef(propertyName, propertyType, columnDef);
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

    private ColumnDef convertColumnDef(String columnName, WrapProperty wrapProperty, CaseSensitivityType caseSensitivity) {
        Field propertyField = wrapProperty.propertyField;
        ColumnMeta columnMeta = wrapProperty.columnMeta;
        Class<?> propertyType = propertyField.getType();
        //
        SimpleColumnDef columnDef = new SimpleColumnDef();
        columnDef.setName(formatCaseSensitivity(columnName, caseSensitivity));
        columnDef.setJavaType(propertyType);
        columnDef.setJdbcType(TypeHandlerRegistry.toSqlType(propertyType));
        if (columnMeta != null) {
            columnDef.setPrimaryKey(columnMeta.primary());
        } else {
            columnDef.setPrimaryKey(false);
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
                    throw ExceptionUtils.toRuntime(e);
                }
            }
            //typeRegistry
            propertyMap.put(name, new WrapProperty(info, columnMeta, propertyField, typeHandler));
        }
        return propertyMap;
    }

    private static String getColumnName(String propertyName, Map<String, WrapProperty> propertyInfoMap, MappingOptions options) {
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
            return humpToLine(columnName, options.getMapUnderscoreToCamelCase());
        } else {
            return humpToLine(propertyName, options.getMapUnderscoreToCamelCase());
        }
    }

    private static final Pattern humpPattern = Pattern.compile("[A-Z]");

    private static String humpToLine(String str, Boolean mapUnderscoreToCamelCase) {
        if (StringUtils.isBlank(str) || mapUnderscoreToCamelCase == null || !mapUnderscoreToCamelCase) {
            return str;
        }
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        //
        String strString = sb.toString();
        strString = strString.replaceAll("_{2,}", "_");
        if (strString.charAt(0) == '_') {
            strString = strString.substring(1);
        }
        return strString;
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

    private static class ColumnInfo implements Column {
        private final String propertyName;

        public ColumnInfo(String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public String value() {
            return this.propertyName;
        }

        @Override
        public String name() {
            return this.propertyName;
        }

        @Override
        public Class<? extends TypeHandler<?>> typeHandler() {
            return UnknownTypeHandler.class;
        }

        @Override
        public boolean update() {
            return true;
        }

        @Override
        public boolean insert() {
            return true;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Column.class;
        }
    }
}
