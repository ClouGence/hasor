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
import net.hasor.db.metadata.ColumnDef;

import java.sql.JDBCType;

/**
 * Jdbc Column
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcColumn implements ColumnDef {
    private String           tableCatalog;
    private String           tableSchema;
    private String           tableName;
    private String           columnName;
    private String           columnType;
    private JdbcSqlTypes     sqlType;
    private JDBCType         jdbcType;
    private Integer          jdbcNumber;
    private JdbcNullableType nullableType;
    private Boolean          nullable;
    private Boolean          autoincrement;
    private Boolean          generatedColumn;
    private Integer          columnSize;
    private boolean          primaryKey;
    private boolean          uniqueKey;//如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String           comment;
    //
    private String           scopeCatalog;
    private String           scopeSchema;
    private String           scopeTable;
    private Integer          decimalDigits;
    private Integer          numberPrecRadix;
    private String           defaultValue;
    private Integer          charOctetLength;
    private Integer          ordinalPosition;
    private Integer          sourceDataType;

    public String getTableCatalog() {
        return this.tableCatalog;
    }

    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    public String getTableSchema() {
        return this.tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public JdbcNullableType getNullableType() {
        return this.nullableType;
    }

    public void setNullableType(JdbcNullableType nullableType) {
        this.nullableType = nullableType;
    }

    public Boolean getNullable() {
        return this.nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public Boolean getAutoincrement() {
        return this.autoincrement;
    }

    public void setAutoincrement(Boolean autoincrement) {
        this.autoincrement = autoincrement;
    }

    public Boolean getGeneratedColumn() {
        return this.generatedColumn;
    }

    public void setGeneratedColumn(Boolean generatedColumn) {
        this.generatedColumn = generatedColumn;
    }

    public JdbcSqlTypes getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(JdbcSqlTypes sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    public void setJdbcType(JDBCType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Integer getJdbcNumber() {
        return this.jdbcNumber;
    }

    public void setJdbcNumber(Integer jdbcNumber) {
        this.jdbcNumber = jdbcNumber;
    }

    public Integer getColumnSize() {
        return this.columnSize;
    }

    public void setColumnSize(Integer columnSize) {
        this.columnSize = columnSize;
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

    public String getScopeCatalog() {
        return this.scopeCatalog;
    }

    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    public String getScopeSchema() {
        return this.scopeSchema;
    }

    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    public String getScopeTable() {
        return this.scopeTable;
    }

    public void setScopeTable(String scopeTable) {
        this.scopeTable = scopeTable;
    }

    public Integer getDecimalDigits() {
        return this.decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    public Integer getNumberPrecRadix() {
        return this.numberPrecRadix;
    }

    public void setNumberPrecRadix(Integer numberPrecRadix) {
        this.numberPrecRadix = numberPrecRadix;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getCharOctetLength() {
        return this.charOctetLength;
    }

    public void setCharOctetLength(Integer charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    public Integer getOrdinalPosition() {
        return this.ordinalPosition;
    }

    public void setOrdinalPosition(Integer ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public Integer getSourceDataType() {
        return this.sourceDataType;
    }

    public void setSourceDataType(Integer sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    @Override
    public String getName() {
        return this.getColumnName();
    }
}
