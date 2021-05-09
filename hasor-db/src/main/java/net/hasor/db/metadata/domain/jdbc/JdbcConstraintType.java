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
/**
 * Jdbc 约束类型
 * @version : 2020-04-25
 * @author 赵永春 (zyc@hasor.net)
 */
public enum JdbcConstraintType {
    /** 主键 */
    PrimaryKey("PRIMARY KEY"),
    /** 外建 */
    ForeignKey("FOREIGN KEY"),
    ;
    private final String typeName;

    JdbcConstraintType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static JdbcConstraintType valueOfCode(String code) {
        for (JdbcConstraintType constraintType : JdbcConstraintType.values()) {
            if (constraintType.typeName.equals(code)) {
                return constraintType;
            }
        }
        return null;
    }
}
