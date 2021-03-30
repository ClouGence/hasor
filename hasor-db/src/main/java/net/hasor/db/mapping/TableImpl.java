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
import java.lang.annotation.Annotation;

/**
 * 查询的表
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
class TableImpl implements Table {
    private final String categoryName;
    private final String tableName;

    public TableImpl(String categoryName, String tableName) {
        this.categoryName = categoryName;
        this.tableName = tableName;
    }

    @Override
    public String category() {
        return this.categoryName;
    }

    @Override
    public String value() {
        return this.tableName;
    }

    @Override
    public String name() {
        return this.tableName;
    }

    @Override
    public boolean useQualifier() {
        return false;
    }

    @Override
    public boolean autoFiled() {
        return true;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Table.class;
    }
}
