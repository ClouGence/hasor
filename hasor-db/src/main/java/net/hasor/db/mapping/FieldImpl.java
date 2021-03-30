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
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.UnknownTypeHandler;

import java.lang.annotation.Annotation;
import java.sql.JDBCType;

/**
 * 字段 or 列 信息
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
class FieldImpl implements Field {
    private final String   fieldName;
    private final JDBCType jdbcType;

    public FieldImpl(String fieldName, JDBCType jdbcType) {
        this.fieldName = fieldName;
        this.jdbcType = jdbcType;
    }

    @Override
    public String value() {
        return this.fieldName;
    }

    @Override
    public String name() {
        return this.fieldName;
    }

    @Override
    public JDBCType jdbcType() {
        return this.jdbcType;
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
        return Field.class;
    }
}
