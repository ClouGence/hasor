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
package net.hasor.db.metadata.domain.jdbc;
import net.hasor.db.metadata.SqlType;

import java.sql.JDBCType;

/**
 * Jdbc 的 数据类型
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcSqlTypes implements SqlType {
    /**
     * Identifies the generic SQL type {@code BIT}.
     */
    BIT(JDBCType.BIT),
    /**
     * Identifies the generic SQL type {@code TINYINT}.
     */
    TINYINT(JDBCType.TINYINT),
    /**
     * Identifies the generic SQL type {@code SMALLINT}.
     */
    SMALLINT(JDBCType.SMALLINT),
    /**
     * Identifies the generic SQL type {@code INTEGER}.
     */
    INTEGER(JDBCType.INTEGER),
    /**
     * Identifies the generic SQL type {@code BIGINT}.
     */
    BIGINT(JDBCType.BIGINT),
    /**
     * Identifies the generic SQL type {@code FLOAT}.
     */
    FLOAT(JDBCType.FLOAT),
    /**
     * Identifies the generic SQL type {@code REAL}.
     */
    REAL(JDBCType.REAL),
    /**
     * Identifies the generic SQL type {@code DOUBLE}.
     */
    DOUBLE(JDBCType.DOUBLE),
    /**
     * Identifies the generic SQL type {@code NUMERIC}.
     */
    NUMERIC(JDBCType.NUMERIC),
    /**
     * Identifies the generic SQL type {@code DECIMAL}.
     */
    DECIMAL(JDBCType.DECIMAL),
    /**
     * Identifies the generic SQL type {@code CHAR}.
     */
    CHAR(JDBCType.CHAR),
    /**
     * Identifies the generic SQL type {@code VARCHAR}.
     */
    VARCHAR(JDBCType.VARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGVARCHAR}.
     */
    LONGVARCHAR(JDBCType.LONGVARCHAR),
    /**
     * Identifies the generic SQL type {@code DATE}.
     */
    DATE(JDBCType.DATE),
    /**
     * Identifies the generic SQL type {@code TIME}.
     */
    TIME(JDBCType.TIME),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP}.
     */
    TIMESTAMP(JDBCType.TIMESTAMP),
    /**
     * Identifies the generic SQL type {@code BINARY}.
     */
    BINARY(JDBCType.BINARY),
    /**
     * Identifies the generic SQL type {@code VARBINARY}.
     */
    VARBINARY(JDBCType.VARBINARY),
    /**
     * Identifies the generic SQL type {@code LONGVARBINARY}.
     */
    LONGVARBINARY(JDBCType.LONGVARBINARY),
    /**
     * Identifies the generic SQL value {@code NULL}.
     */
    NULL(JDBCType.NULL),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    OTHER(JDBCType.OTHER),
    /**
     * Indicates that the SQL type
     * is database-specific and gets mapped to a Java object that can be
     * accessed via the methods getObject and setObject.
     */
    JAVA_OBJECT(JDBCType.JAVA_OBJECT),
    /**
     * Identifies the generic SQL type {@code DISTINCT}.
     */
    DISTINCT(JDBCType.DISTINCT),
    /**
     * Identifies the generic SQL type {@code STRUCT}.
     */
    STRUCT(JDBCType.STRUCT),
    /**
     * Identifies the generic SQL type {@code ARRAY}.
     */
    ARRAY(JDBCType.ARRAY),
    /**
     * Identifies the generic SQL type {@code BLOB}.
     */
    BLOB(JDBCType.BLOB),
    /**
     * Identifies the generic SQL type {@code CLOB}.
     */
    CLOB(JDBCType.CLOB),
    /**
     * Identifies the generic SQL type {@code REF}.
     */
    REF(JDBCType.REF),
    /**
     * Identifies the generic SQL type {@code DATALINK}.
     */
    DATALINK(JDBCType.DATALINK),
    /**
     * Identifies the generic SQL type {@code BOOLEAN}.
     */
    BOOLEAN(JDBCType.BOOLEAN),

    /* JDBC 4.0 Types */

    /**
     * Identifies the SQL type {@code ROWID}.
     */
    ROWID(JDBCType.ROWID),
    /**
     * Identifies the generic SQL type {@code NCHAR}.
     */
    NCHAR(JDBCType.NCHAR),
    /**
     * Identifies the generic SQL type {@code NVARCHAR}.
     */
    NVARCHAR(JDBCType.NVARCHAR),
    /**
     * Identifies the generic SQL type {@code LONGNVARCHAR}.
     */
    LONGNVARCHAR(JDBCType.LONGNVARCHAR),
    /**
     * Identifies the generic SQL type {@code NCLOB}.
     */
    NCLOB(JDBCType.NCLOB),
    /**
     * Identifies the generic SQL type {@code SQLXML}.
     */
    SQLXML(JDBCType.SQLXML),

    /* JDBC 4.2 Types */

    /**
     * Identifies the generic SQL type {@code REF_CURSOR}.
     */
    REF_CURSOR(JDBCType.REF_CURSOR),
    /**
     * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
     */
    TIME_WITH_TIMEZONE(JDBCType.TIME_WITH_TIMEZONE),
    /**
     * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
     */
    TIMESTAMP_WITH_TIMEZONE(JDBCType.TIMESTAMP_WITH_TIMEZONE);
    //
    private final String   codeKey;
    private final JDBCType jdbcType;

    JdbcSqlTypes(JDBCType jdbcType) {
        this.codeKey = jdbcType.name();
        this.jdbcType = jdbcType;
    }

    public static JdbcSqlTypes valueOfCode(String code) {
        for (JdbcSqlTypes tableType : JdbcSqlTypes.values()) {
            if (tableType.codeKey.equals(code)) {
                return tableType;
            }
        }
        return null;
    }

    public static JdbcSqlTypes valueOfCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JdbcSqlTypes tableType : JdbcSqlTypes.values()) {
            if (code.equals(tableType.jdbcType.getVendorTypeNumber())) {
                return tableType;
            }
        }
        return null;
    }

    @Override
    public String getCodeKey() {
        return this.codeKey;
    }

    @Override
    public Integer getJdbcType() {
        return this.jdbcType.getVendorTypeNumber();
    }

    @Override
    public JDBCType toJDBCType() {
        return this.jdbcType;
    }
}
