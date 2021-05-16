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
import net.hasor.db.metadata.TableDef;

/**
 * Postgres 的表
 * @version : 2021-05-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgresTable implements TableDef {
    private String            schema;
    private String            table;
    private PostgresTableType tableType;
    private boolean           typed;
    private String            comment;

    @Override
    public String getCatalog() {
        return null;
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

    public PostgresTableType getTableType() {
        return this.tableType;
    }

    public void setTableType(PostgresTableType tableType) {
        this.tableType = tableType;
    }

    public boolean isTyped() {
        return this.typed;
    }

    public void setTyped(boolean typed) {
        this.typed = typed;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
