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
package net.hasor.db.metadata.domain.oracle;
import net.hasor.db.metadata.ColumnDef;
import net.hasor.db.metadata.SqlType;

import java.sql.JDBCType;

/**
 * Oracle 的列
 * @version : 2021-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class OracleColumn implements ColumnDef {
    private String   name;
    private boolean  nullable;
    private String   columnType;
    private String   columnTypeOwner;
    private SqlType  sqlType;
    private JDBCType jdbcType;
    private boolean  primaryKey;
    private boolean  uniqueKey;//如若存在联合唯一索引需要借助getUniqueKey 来查询具体信息，这里只会表示该列存在至少一个唯一索引的引用。
    private String   comment;
    //
    private Long     dataBytesLength;
    private Long     dataCharLength;
    private Integer  dataPrecision;
    private Integer  dataScale;
    private String   dataDefault;
    private String   characterSetName;
    private boolean  hidden;
    private boolean  virtual;
    private boolean  identity;
    private boolean  sensitive;

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

    public String getColumnTypeOwner() {
        return this.columnTypeOwner;
    }

    public void setColumnTypeOwner(String columnTypeOwner) {
        this.columnTypeOwner = columnTypeOwner;
    }

    public SqlType getSqlType() {
        return this.sqlType;
    }

    public void setSqlType(SqlType sqlType) {
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

    public Long getDataBytesLength() {
        return this.dataBytesLength;
    }

    public void setDataBytesLength(Long dataBytesLength) {
        this.dataBytesLength = dataBytesLength;
    }

    public Long getDataCharLength() {
        return this.dataCharLength;
    }

    public void setDataCharLength(Long dataCharLength) {
        this.dataCharLength = dataCharLength;
    }

    public Integer getDataPrecision() {
        return this.dataPrecision;
    }

    public void setDataPrecision(Integer dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    public Integer getDataScale() {
        return this.dataScale;
    }

    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }

    public String getDataDefault() {
        return this.dataDefault;
    }

    public void setDataDefault(String dataDefault) {
        this.dataDefault = dataDefault;
    }

    public String getCharacterSetName() {
        return this.characterSetName;
    }

    public void setCharacterSetName(String characterSetName) {
        this.characterSetName = characterSetName;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public boolean isIdentity() {
        return this.identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public boolean isSensitive() {
        return this.sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }
}
