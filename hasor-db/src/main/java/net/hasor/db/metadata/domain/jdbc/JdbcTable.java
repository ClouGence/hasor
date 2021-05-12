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
import net.hasor.db.metadata.TableDef;

/**
 * Jdbc Table
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcTable implements TableDef {
    private String        catalog;
    private String        schema;
    private String        table;
    private JdbcTableType tableType;
    private String        tableTypeString;
    private String        comment;
    //
    private String        typeCatalog;
    private String        typeSchema;
    private String        typeName;
    private String        selfReferencingColName;
    private String        refGeneration;

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override
    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    @Override
    public JdbcTableType getTableType() {
        return this.tableType;
    }

    public void setTableType(JdbcTableType tableType) {
        this.tableType = tableType;
    }

    public String getTableTypeString() {
        return this.tableTypeString;
    }

    public void setTableTypeString(String tableTypeString) {
        this.tableTypeString = tableTypeString;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTypeCatalog() {
        return this.typeCatalog;
    }

    public void setTypeCatalog(String typeCatalog) {
        this.typeCatalog = typeCatalog;
    }

    public String getTypeSchema() {
        return this.typeSchema;
    }

    public void setTypeSchema(String typeSchema) {
        this.typeSchema = typeSchema;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSelfReferencingColName() {
        return this.selfReferencingColName;
    }

    public void setSelfReferencingColName(String selfReferencingColName) {
        this.selfReferencingColName = selfReferencingColName;
    }

    public String getRefGeneration() {
        return this.refGeneration;
    }

    public void setRefGeneration(String refGeneration) {
        this.refGeneration = refGeneration;
    }
}