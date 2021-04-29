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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jdbc 外键
 * @version : 2020-04-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdbcForeignKey extends JdbcConstraint {
    private List<String>        fkColumn         = new ArrayList<>();
    private String              referenceCatalog;
    private String              referenceSchema;
    private String              referenceTable;
    private Map<String, String> referenceMapping = new HashMap<>();
    private JdbcForeignKeyRule  updateRule;
    private JdbcForeignKeyRule  deleteRule;
    private JdbcDeferrability   deferrability;

    public List<String> getFkColumn() {
        return fkColumn;
    }

    public void setFkColumn(List<String> fkColumn) {
        this.fkColumn = fkColumn;
    }

    public String getReferenceCatalog() {
        return this.referenceCatalog;
    }

    public void setReferenceCatalog(String referenceCatalog) {
        this.referenceCatalog = referenceCatalog;
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

    public JdbcForeignKeyRule getUpdateRule() {
        return this.updateRule;
    }

    public void setUpdateRule(JdbcForeignKeyRule updateRule) {
        this.updateRule = updateRule;
    }

    public JdbcForeignKeyRule getDeleteRule() {
        return this.deleteRule;
    }

    public void setDeleteRule(JdbcForeignKeyRule deleteRule) {
        this.deleteRule = deleteRule;
    }

    public JdbcDeferrability getDeferrability() {
        return this.deferrability;
    }

    public void setDeferrability(JdbcDeferrability deferrability) {
        this.deferrability = deferrability;
    }
}