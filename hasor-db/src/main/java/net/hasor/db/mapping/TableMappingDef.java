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
import net.hasor.db.metadata.ColumnDef;
import net.hasor.utils.ref.LinkedCaseInsensitiveMap;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 一个实体的映射信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
class TableMappingDef implements TableMapping {
    private       String                       category;
    private       String                       tableName;
    private       boolean                      useDelimited;
    private       boolean                      autoProperty;
    private       CaseSensitivityType          caseSensitivity;
    private final Class<?>                     entityType;
    private final Map<String, ColumnDef>       columnMappingMap;
    private final Map<String, PropertyMapping> propertyMappingMap;

    public TableMappingDef(Class<?> entityType, boolean caseInsensitive) {
        this.entityType = entityType;
        if (caseInsensitive) {
            this.columnMappingMap = new LinkedCaseInsensitiveMap<>();
        } else {
            this.columnMappingMap = new LinkedHashMap<>();
        }
        this.propertyMappingMap = new LinkedHashMap<>();
    }

    @Override
    public Class<?> entityType() {
        return this.entityType;
    }

    @Override
    public GenerationType generationKey() {
        return null;
    }

    @Override
    public boolean isAutoProperty() {
        return this.autoProperty;
    }

    public void setAutoProperty(boolean autoProperty) {
        this.autoProperty = autoProperty;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
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

    public void addMapping(PropertyMapping mapping) {
        this.columnMappingMap.put(mapping.getName(), mapping);
        this.propertyMappingMap.put(mapping.getPropertyName(), mapping);
    }

    public boolean isEmpty() {
        return this.columnMappingMap.isEmpty() || this.propertyMappingMap.isEmpty();
    }
}