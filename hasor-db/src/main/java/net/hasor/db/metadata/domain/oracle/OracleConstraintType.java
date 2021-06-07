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
package net.hasor.db.metadata.domain.oracle;
/**
 * Oracle 约束类型
 * @version : 2021-05-07
 * @author 赵永春 (zyc@hasor.net)
 */
public enum OracleConstraintType {
    /** 主键 */
    PrimaryKey("P"),
    /** 唯一 */
    Unique("U"),
    /** 外建 */
    ForeignKey("R"),
    /** 检查 */
    Check("C"),
    /** 视图检查 */
    CheckView("V"),
    //
    /** With read only, on a view */
    ReadOnly("O"),
    /** Hash expression */
    Hash("H"),
    /** Supplemental logging */
    Supplemental("S"),
    /** Constraint that involves a REF column */
    RefColumn("F"),
    ;
    private final String typeName;

    OracleConstraintType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static OracleConstraintType valueOfCode(String code) {
        for (OracleConstraintType constraintType : OracleConstraintType.values()) {
            if (constraintType.typeName.equalsIgnoreCase(code)) {
                return constraintType;
            }
        }
        return null;
    }
}
