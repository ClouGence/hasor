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
package net.hasor.db.metadata.domain.postgres;
import net.hasor.db.metadata.ColumnDef;

import java.sql.JDBCType;

/**
 * Postgres 的列
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgresColumn implements ColumnDef {
    private String        name;
    private boolean       nullable;
    private String        columnType;
    private PostgresTypes sqlType;
    private JDBCType      jdbcType;
    private boolean       primaryKey;
    private boolean       uniqueKey;//如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String        comment;
    //
    private Long          typeOid;
    private String        dataType;
    private String        elementType;
    private Integer       characterMaximumLength;
    private Integer       characterOctetLength;
    private String        defaultValue;
    //
    private Integer       numericPrecision;
    private Integer       numericPrecisionRadix;
    private Integer       numericScale;
    private Integer       datetimePrecision;

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    @Override
    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public PostgresTypes getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(PostgresTypes sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public boolean isPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isUniqueKey() {
        return this.uniqueKey;
    }

    public void setUniqueKey(boolean uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTypeOid() {
        return this.typeOid;
    }

    public void setTypeOid(Long typeOid) {
        this.typeOid = typeOid;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getElementType() {
        return this.elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public Integer getCharacterMaximumLength() {
        return this.characterMaximumLength;
    }

    public void setCharacterMaximumLength(Integer characterMaximumLength) {
        this.characterMaximumLength = characterMaximumLength;
    }

    public Integer getCharacterOctetLength() {
        return this.characterOctetLength;
    }

    public void setCharacterOctetLength(Integer characterOctetLength) {
        this.characterOctetLength = characterOctetLength;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getNumericPrecision() {
        return this.numericPrecision;
    }

    public void setNumericPrecision(Integer numericPrecision) {
        this.numericPrecision = numericPrecision;
    }

    public Integer getNumericPrecisionRadix() {
        return this.numericPrecisionRadix;
    }

    public void setNumericPrecisionRadix(Integer numericPrecisionRadix) {
        this.numericPrecisionRadix = numericPrecisionRadix;
    }

    public Integer getNumericScale() {
        return this.numericScale;
    }

    public void setNumericScale(Integer numericScale) {
        this.numericScale = numericScale;
    }

    public Integer getDatetimePrecision() {
        return this.datetimePrecision;
    }

    public void setDatetimePrecision(Integer datetimePrecision) {
        this.datetimePrecision = datetimePrecision;
    }
}
