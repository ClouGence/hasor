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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL 外键
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public class MySqlForeignKey extends MySqlConstraint {
    private List<String>        columns          = new ArrayList<>();
    private Map<String, String> storageType      = new HashMap<>();
    private String              referenceSchema;
    private String              referenceTable;
    private Map<String, String> referenceMapping = new HashMap<>();
    private MySqlForeignKeyRule updateRule;
    private MySqlForeignKeyRule deleteRule;

    public List<String> getColumns() {
        return this.columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Map<String, String> getStorageType() {
        return this.storageType;
    }

    public void setStorageType(Map<String, String> storageType) {
        this.storageType = storageType;
    }

    public String getReferenceSchema() {
        return this.referenceSchema;
    }

    public void setReferenceSchema(String referenceSchema) {
        this.referenceSchema = referenceSchema;
    }

    public String getReferenceTable() {
        return this.referenceTable;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    public Map<String, String> getReferenceMapping() {
        return this.referenceMapping;
    }

    public void setReferenceMapping(Map<String, String> referenceMapping) {
        this.referenceMapping = referenceMapping;
    }

    public MySqlForeignKeyRule getUpdateRule() {
        return this.updateRule;
    }

    public void setUpdateRule(MySqlForeignKeyRule updateRule) {
        this.updateRule = updateRule;
    }

    public MySqlForeignKeyRule getDeleteRule() {
        return this.deleteRule;
    }

    public void setDeleteRule(MySqlForeignKeyRule deleteRule) {
        this.deleteRule = deleteRule;
    }
}
