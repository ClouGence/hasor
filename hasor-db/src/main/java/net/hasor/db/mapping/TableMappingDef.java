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
import net.hasor.db.lambda.generation.GenerationType;
import net.hasor.db.metadata.CaseSensitivityType;
import net.hasor.db.metadata.TableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一个实体的映射信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class TableMappingDef implements TableMapping {
    private       String                           catalogName;
    private       String                           schemaName;
    private       String                           tableName;
    private       TableType                        tableType;
    private       boolean                          useDelimited;
    private       boolean                          autoProperty;
    private       CaseSensitivityType              caseSensitivity;
    private final Class<?>                         entityType;
    //
    private final List<String>                     propertyNames;
    private final Map<String, ColumnMapping>       propertyMapping;
    private final List<ColumnMapping>              mappingList;
    private final List<String>                     columnNames;
    private final Map<String, List<ColumnMapping>> columnNameMapping;

    public TableMappingDef(Class<?> entityType) {
        this.entityType = entityType;
        this.propertyNames = new ArrayList<>();
        this.propertyMapping = new HashMap<>();
        this.mappingList = new ArrayList<>();
        this.columnNames = new ArrayList<>();
        this.columnNameMapping = new HashMap<>();
    }

    @Override
    public boolean isAutoProperty() {
        return this.autoProperty;
    }

    public void setAutoProperty(boolean autoProperty) {
        this.autoProperty = autoProperty;
    }

    @Override
    public String getCatalog() {
        return this.catalogName;
    }

    public void setCatalog(String catalog) {
        this.catalogName = catalog;
    }

    @Override
    public String getSchema() {
        return this.schemaName;
    }

    public void setSchema(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public String getTable() {
        return this.tableName;
    }

    @Override
    public TableType getTableType() {
        return this.tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    public void setTable(String tableName) {
        this.tableName = tableName;
    }

    public boolean isUseDelimited() {
        return this.useDelimited;
    }

    public void setUseDelimited(boolean useDelimited) {
        this.useDelimited = useDelimited;
    }

    public CaseSensitivityType getCaseSensitivity() {
        return this.caseSensitivity;
    }

    public void setCaseSensitivity(CaseSensitivityType caseSensitivity) {
        this.caseSensitivity = caseSensitivity;
    }

    @Override
    public Class<?> entityType() {
        return this.entityType;
    }

    @Override
    public List<ColumnMapping> getProperties() {
        return this.mappingList;
    }

    @Override
    public List<String> getPropertyNames() {
        return this.propertyNames;
    }

    @Override
    public List<String> getColumnNames() {
        return this.columnNames;
    }

    @Override
    public ColumnMapping getMapping(String propertyName) {
        return this.propertyMapping.get(propertyName);
    }

    @Override
    public List<ColumnMapping> getMappingByColumnName(String columnName) {
        return this.columnNameMapping.get(columnName);
    }

    public boolean isEmpty() {
        return this.propertyNames.isEmpty();
    }

    @Override
    public GenerationType generationKey() {
        return null;
    }

    public void addMapping(ColumnMapping mapping) {
        String columnName = mapping.getName();
        String propertyName = mapping.getPropertyName();
        this.propertyNames.add(propertyName);
        this.propertyMapping.put(propertyName, mapping);
        this.mappingList.add(mapping);
        this.columnNames.add(columnName);
        List<ColumnMapping> propertyNames = this.columnNameMapping.computeIfAbsent(columnName, k -> new ArrayList<>());
        propertyNames.add(mapping);
    }
}