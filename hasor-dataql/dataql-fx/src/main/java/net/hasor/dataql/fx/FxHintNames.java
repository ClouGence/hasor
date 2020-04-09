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
package net.hasor.dataql.fx;
import static net.hasor.dataql.fx.FxHintValue.FRAGMENT_SQL_OPEN_PACKAGE_COLUMN;
import static net.hasor.dataql.fx.FxHintValue.FRAGMENT_SQL_QUERY_BY_PAGE_DISABLE;

/**
 * Hint 的 keys 定义。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public enum FxHintNames {
    /** SqlFragment 返回值，拆开方式 */
    FRAGMENT_SQL_OPEN_PACKAGE(FRAGMENT_SQL_OPEN_PACKAGE_COLUMN),
    /** SqlFragment 查询执行是否使用分页模式（默认：不使用） */
    FRAGMENT_SQL_QUERY_BY_PAGE(FRAGMENT_SQL_QUERY_BY_PAGE_DISABLE),
    /** SqlFragment 分页查询在改写分页查询语句时使用的方言（默认：空，需要明确指定） */
    FRAGMENT_SQL_PAGE_DIALECT("");
    //
    private String defaultVal;

    public String getDefaultVal() {
        return this.defaultVal;
    }

    FxHintNames(String defaultVal) {
        this.defaultVal = defaultVal;
    }
}