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

import java.sql.JDBCType;

/**
 * 字段 or 列信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
class PropertyMappingDef implements PropertyMapping {
    private final String   columnName;
    private final String   propertyName;
    private final JDBCType jdbcType;
    private final Class<?> javaType;
    private final boolean  insert;
    private final boolean  update;
    private final boolean  primary;

    public PropertyMappingDef(String columnName, String propertyName, JDBCType jdbcType, Class<?> javaType, boolean insert, boolean update, boolean primary) {
        this.columnName = columnName;
        this.propertyName = propertyName;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
        this.insert = insert;
        this.update = update;
        this.primary = primary;
    }

    @Override
    public String getName() {
        return this.columnName;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public GenerationType generationStrategy() {
        return null;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }

    @Override
    public boolean isUpdate() {
        return this.update;
    }

    @Override
    public boolean isInsert() {
        return this.insert;
    }

    @Override
    public boolean isPrimaryKey() {
        return this.primary;
    }
}
