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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Postgres 外键
 * @version : 2021-05-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class PostgresForeignKey extends PostgresConstraint {
    private List<String>               columns          = new ArrayList<>();
    private String                     referenceSchema;
    private String                     referenceTable;
    private Map<String, String>        referenceMapping = new HashMap<>();
    private PostgresForeignKeyRule     updateRule;
    private PostgresForeignKeyRule     deleteRule;
    private PostgresForeignMatchOption matchOption;

    public List<String> getColumns() {
        return this.columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
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

    public PostgresForeignKeyRule getUpdateRule() {
        return this.updateRule;
    }

    public void setUpdateRule(PostgresForeignKeyRule updateRule) {
        this.updateRule = updateRule;
    }

    public PostgresForeignKeyRule getDeleteRule() {
        return this.deleteRule;
    }

    public void setDeleteRule(PostgresForeignKeyRule deleteRule) {
        this.deleteRule = deleteRule;
    }

    public PostgresForeignMatchOption getMatchOption() {
        return this.matchOption;
    }

    public void setMatchOption(PostgresForeignMatchOption matchOption) {
        this.matchOption = matchOption;
    }
}