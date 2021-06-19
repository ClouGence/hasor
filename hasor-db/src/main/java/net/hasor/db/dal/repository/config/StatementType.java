/*
 * Copyright 2002-2005 the original author or authors.
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
package net.hasor.db.dal.repository.config;
/**
 * 使用 Statement 方式。
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public enum StatementType {
    /** 使用 java.sql.Statement */
    Statement("STATEMENT"),
    /** 使用 java.sql.PreparedStatement */
    Prepared("PREPARED"),
    /** 使用 java.sql.CallableStatement */
    Callable("CALLABLE"),
    ;
    private final String typeName;

    StatementType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static StatementType valueOfCode(String code, StatementType defaultType) {
        for (StatementType tableType : StatementType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}
