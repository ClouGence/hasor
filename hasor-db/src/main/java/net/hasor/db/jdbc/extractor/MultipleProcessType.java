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
package net.hasor.db.jdbc.extractor;
/**
 * 在处理多结果集的时结果集保留策略。
 * @version : 2021-07-20
 * @author 赵永春 (zyc@byshell.org)
 */
public enum MultipleProcessType {
    /** 多结果，保留第一个结果 */
    FIRST("FIRST"),
    /** 多结果，保留最后结果 */
    LAST("LAST"),
    /** 多结果，全部保留 */
    ALL("ALL");
    private final String typeName;

    MultipleProcessType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static MultipleProcessType valueOfCode(String code, MultipleProcessType defaultType) {
        for (MultipleProcessType tableType : MultipleProcessType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}