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
 * 查询类型
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public enum QueryType {
    /** Insert 类型 */
    Insert("insert"),
    /** Delete 类型 */
    Delete("delete"),
    /** Update 类型 */
    Update("update"),
    /** 查询类型 类型 */
    Query("select"),
    /** 调用存储过程 */
    Callable("callable"),
    /** Sql 片段，可以被 include */
    Segment("sql"),
    ;
    private final String xmlTag;

    public String getXmlTag() {
        return this.xmlTag;
    }

    QueryType(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public static QueryType valueOfTag(String xmlTag) {
        for (QueryType tableType : QueryType.values()) {
            if (tableType.xmlTag.equalsIgnoreCase(xmlTag)) {
                return tableType;
            }
        }
        return null;
    }
}
