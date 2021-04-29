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
 * Jdbc 外建约束的及联更新策略
 * @version : 2020-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcForeignKeyRule {
    /** do not allow update of primary key if it has been imported. */
    ImportedKeyNoAction(DatabaseMetaData.importedKeyNoAction),
    /** change imported key to agree with primary key update */
    ImportedKeyCascade(DatabaseMetaData.importedKeyCascade),
    /** change imported key to NULL if its primary key has been updated. */
    ImportedKeySetNull(DatabaseMetaData.importedKeySetNull),
    /** change imported key to default values if its primary key has been updated. */
    ImportedKeySetDefault(DatabaseMetaData.importedKeySetDefault),
    /** same as importedKeyNoAction (for ODBC 2.x compatibility). */
    ImportedKeyRestrict(DatabaseMetaData.importedKeyRestrict);
    private final int typeNumber;

    JdbcForeignKeyRule(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getTypeNumber() {
        return this.typeNumber;
    }

    public static JdbcForeignKeyRule valueOfCode(int typeNumber) {
        for (JdbcForeignKeyRule tableType : JdbcForeignKeyRule.values()) {
            if (tableType.typeNumber == typeNumber) {
                return tableType;
            }
        }
        return null;
    }
}
