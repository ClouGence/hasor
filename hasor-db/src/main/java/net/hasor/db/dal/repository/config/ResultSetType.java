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
import java.sql.ResultSet;

/**
 * FORWARD_ONLY，SCROLL_SENSITIVE, SCROLL_INSENSITIVE 或 DEFAULT（等价于 unset） 中的一个，默认值为 unset （依赖数据库驱动）。
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public enum ResultSetType {
    FORWARD_ONLY("FORWARD_ONLY", ResultSet.TYPE_FORWARD_ONLY),
    SCROLL_SENSITIVE("SCROLL_SENSITIVE", ResultSet.TYPE_SCROLL_SENSITIVE),
    SCROLL_INSENSITIVE("SCROLL_INSENSITIVE", ResultSet.TYPE_SCROLL_INSENSITIVE),
    DEFAULT("DEFAULT", null),
    ;
    private final String  typeName;
    private final Integer resultSetType;

    ResultSetType(String typeName, Integer resultSetType) {
        this.typeName = typeName;
        this.resultSetType = resultSetType;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public Integer getResultSetType() {
        return this.resultSetType;
    }

    public static ResultSetType valueOfCode(String code, ResultSetType defaultType) {
        for (ResultSetType tableType : ResultSetType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}
