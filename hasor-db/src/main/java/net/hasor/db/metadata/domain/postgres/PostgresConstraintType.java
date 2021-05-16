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
/**
 * PostgresSQL 约束类型
 * @version : 2021-03-30
 * @author 赵永春 (zyc@hasor.net)
 */
public enum PostgresConstraintType {
    /** 主键 */
    PrimaryKey("PRIMARY KEY"),
    /** 唯一 */
    Unique("UNIQUE"),
    /** 外建 */
    ForeignKey("FOREIGN KEY"),
    /** 检查 */
    Check("CHECK"),
    ;
    private final String typeName;

    PostgresConstraintType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static PostgresConstraintType valueOfCode(String code) {
        for (PostgresConstraintType constraintType : PostgresConstraintType.values()) {
            if (constraintType.typeName.equals(code)) {
                return constraintType;
            }
        }
        return null;
    }
}
