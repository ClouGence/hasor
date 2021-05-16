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
 * Jdbc Deferrability
 * @version : 2020-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcDeferrability {
    /** see SQL92 for definition. */
    ImportedKeyInitiallyDeferred(DatabaseMetaData.importedKeyInitiallyDeferred),
    /** see SQL92 for definition */
    ImportedKeyInitiallyImmediate(DatabaseMetaData.importedKeyInitiallyImmediate),
    /** see SQL92 for definition */
    ImportedKeyNotDeferrable(DatabaseMetaData.importedKeyNotDeferrable);
    private final int typeNumber;

    JdbcDeferrability(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getTypeNumber() {
        return this.typeNumber;
    }

    public static JdbcDeferrability valueOfCode(Integer typeNumber) {
        if (typeNumber == null) {
            return null;
        }
        for (JdbcDeferrability tableType : JdbcDeferrability.values()) {
            if (tableType.typeNumber == typeNumber) {
                return tableType;
            }
        }
        return null;
    }
}