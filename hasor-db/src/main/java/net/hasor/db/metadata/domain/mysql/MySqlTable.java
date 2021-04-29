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
package net.hasor.db.metadata.domain.mysql;
import net.hasor.db.metadata.TableDef;

import java.util.Date;

/**
 * MySQL 表
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlTable implements TableDef {
    private String         catalog;
    private String         schema;
    private String         table;
    private MySqlTableType tableType;
    private String         collation;
    private Date           createTime;
    private Date           updateTime;
    private String         comment;

    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public MySqlTableType getTableType() {
        return this.tableType;
    }

    public void setTableType(MySqlTableType tableType) {
        this.tableType = tableType;
    }

    public String getCollation() {
        return this.collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
