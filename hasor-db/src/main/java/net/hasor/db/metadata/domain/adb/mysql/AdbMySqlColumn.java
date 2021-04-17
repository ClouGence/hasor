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
package net.hasor.db.metadata.domain.adb.mysql;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.SqlType;

import java.sql.JDBCType;

/**
 * AdbMySql 列
 * @version : 2020-01-22
 * @author 赵永春 (zyc@hasor.net)
 */
public class AdbMySqlColumn implements ColumnDef {
    private String   name;
    private boolean  nullable;
    private String   dataType;
    private String   columnType;
    private SqlType  sqlType;
    private JDBCType jdbcType;
    private boolean  primaryKey;
    private String   comment;
    //
    private Integer  datetimePrecision;
    private Integer  numericPrecision;
    private Integer  numericScale;
    //
    private String   defaultCollationName;
    private String   defaultCharacterSetName;
    private Long     charactersMaxLength;
    private Integer  bytesMaxLength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public void setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
    }

    public JDBCType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getDatetimePrecision() {
        return datetimePrecision;
    }

    public void setDatetimePrecision(Integer datetimePrecision) {
        this.datetimePrecision = datetimePrecision;
    }

    public Integer getNumericPrecision() {
        return numericPrecision;
    }

    public void setNumericPrecision(Integer numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public Integer getNumericScale() {
        return numericScale;
    }

    public void setNumericScale(Integer numericScale) {
        this.numericScale = numericScale;
    }

    public String getDefaultCollationName() {
        return defaultCollationName;
    }

    public void setDefaultCollationName(String defaultCollationName) {
        this.defaultCollationName = defaultCollationName;
    }

    public String getDefaultCharacterSetName() {
        return defaultCharacterSetName;
    }

    public void setDefaultCharacterSetName(String defaultCharacterSetName) {
        this.defaultCharacterSetName = defaultCharacterSetName;
    }

    public Long getCharactersMaxLength() {
        return charactersMaxLength;
    }

    public void setCharactersMaxLength(Long charactersMaxLength) {
        this.charactersMaxLength = charactersMaxLength;
    }

    public Integer getBytesMaxLength() {
        return bytesMaxLength;
    }

    public void setBytesMaxLength(Integer bytesMaxLength) {
        this.bytesMaxLength = bytesMaxLength;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
