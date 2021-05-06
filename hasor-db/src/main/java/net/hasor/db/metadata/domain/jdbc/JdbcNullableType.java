/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.db.metadata.domain.jdbc;
import java.sql.DatabaseMetaData;

/**
 * Jdbc 规定的空值类型
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcNullableType {
    /** might not allow NULL values */
    ColumnNoNull(DatabaseMetaData.columnNoNulls),
    /** definitely allows NULL values */
    ColumnNullable(DatabaseMetaData.columnNullable),
    /** nullability unknown */
    ColumnNullableUnknown(DatabaseMetaData.columnNullableUnknown);
    private final int typeNumber;

    JdbcNullableType(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getTypeNumber() {
        return this.typeNumber;
    }

    public static JdbcNullableType valueOfCode(int typeNumber) {
        for (JdbcNullableType tableType : JdbcNullableType.values()) {
            if (tableType.typeNumber == typeNumber) {
                return tableType;
            }
        }
        return ColumnNullableUnknown;
    }
}