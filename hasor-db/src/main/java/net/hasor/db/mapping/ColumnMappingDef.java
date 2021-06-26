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
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.types.TypeHandler;

import java.sql.JDBCType;

/**
 * 字段 or 列信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class ColumnMappingDef implements ColumnMapping {
    private final String         columnName;
    private final String         columnType;
    private final String         propertyName;
    private       JDBCType       jdbcType;
    private       Class<?>       javaType;
    private       TypeHandler<?> typeHandler;
    private       boolean        insert;
    private       boolean        update;
    private       boolean        primary;

    public ColumnMappingDef(String propertyName, Class<?> javaType, ColumnDef columnDef) {
        this.columnName = columnDef.getName();
        this.columnType = columnDef.getColumnType();
        this.propertyName = propertyName;
        this.jdbcType = columnDef.getJdbcType();
        this.javaType = javaType;
        this.insert = true;
        this.update = true;
        this.primary = columnDef.isPrimaryKey();
    }

    @Override
    public String getName() {
        return this.columnName;
    }

    @Override
    public String getColumnType() {
        return this.columnType;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    @Override
    public boolean isUpdate() {
        return this.update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    @Override
    public boolean isInsert() {
        return this.insert;
    }

    public void setInsert(boolean insert) {
        this.insert = insert;
    }

    @Override
    public boolean isPrimaryKey() {
        return this.primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
