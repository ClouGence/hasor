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
 * Jdbc 索引分类
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcIndexType {
    /** Indicates that this column contains table statistics that are returned in conjunction with a table's index descriptions. */
    TableIndexStatistic(DatabaseMetaData.tableIndexStatistic),
    /** this is a clustered index */
    TableIndexClustered(DatabaseMetaData.tableIndexClustered),
    /** this is a hashed index */
    TableIndexHashed(DatabaseMetaData.tableIndexHashed),
    /** this is some other style of index */
    TableIndexOther(DatabaseMetaData.tableIndexOther);
    private final int typeNumber;

    JdbcIndexType(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public int getTypeNumber() {
        return this.typeNumber;
    }

    public static JdbcIndexType valueOfCode(Integer typeNumber) {
        if (typeNumber == null) {
            return null;
        }
        for (JdbcIndexType tableType : JdbcIndexType.values()) {
            if (tableType.typeNumber == typeNumber) {
                return tableType;
            }
        }
        return TableIndexOther;
    }
}
