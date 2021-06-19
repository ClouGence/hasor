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
 * 在处理多结果集的时结果集保留策略。
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public enum MultipleResultsType {
    /** 多结果，保留第一个结果 */
    FIRST("FIRST"),
    /** 多结果，保留最后结果 */
    LAST("LAST"),
    /** 多结果，全部保留 */
    ALL("ALL");
    private final String typeName;

    MultipleResultsType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public static MultipleResultsType valueOfCode(String code, MultipleResultsType defaultType) {
        for (MultipleResultsType tableType : MultipleResultsType.values()) {
            if (tableType.typeName.equalsIgnoreCase(code)) {
                return tableType;
            }
        }
        return defaultType;
    }
}