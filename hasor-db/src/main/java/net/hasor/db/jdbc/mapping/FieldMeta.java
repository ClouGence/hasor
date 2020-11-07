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
package net.hasor.db.jdbc.mapping;
import net.hasor.db.jdbc.core.StatementSetterUtils;

import java.util.function.Predicate;

/**
 * 映射到数据库的列信息。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class FieldMeta implements Predicate<String> {
    private final String   columnName;
    private final Class<?> javaType;
    private final int      jdbcType;
    private       String   aliasName;
    private       boolean  caseStrategy;

    public FieldMeta(String columnName, Class<?> javaType) {
        this.columnName = columnName;
        this.javaType = javaType;
        this.jdbcType = StatementSetterUtils.javaTypeToSqlParameterType(javaType);
        this.caseStrategy = true;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getAliasName() {
        return this.aliasName;
    }

    public Class<?> getJavaType() {
        return this.javaType;
    }

    public int getJdbcType() {
        return this.jdbcType;
    }

    @Override
    public boolean test(String s) {
        if (this.caseStrategy) {
            return this.columnName.equalsIgnoreCase(s);
        } else {
            return this.columnName.equals(s);
        }
    }
}
