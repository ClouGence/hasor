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
/**
 * MySQL 外建约束的及联更新策略
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public enum MySqlForeignKeyRule {
    NoAction("NO ACTION"),
    Restrict("RESTRICT"),
    Cascade("CASCADE"),
    SetNull("SET NULL"),
    SetDefault("SET DEFAULT");
    private final String typeName;

    MySqlForeignKeyRule(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static MySqlForeignKeyRule valueOfCode(String code) {
        for (MySqlForeignKeyRule foreignKeyRule : MySqlForeignKeyRule.values()) {
            if (foreignKeyRule.typeName.equals(code)) {
                return foreignKeyRule;
            }
        }
        return null;
    }
}
