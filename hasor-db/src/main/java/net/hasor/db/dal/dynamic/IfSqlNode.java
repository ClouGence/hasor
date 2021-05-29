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
package net.hasor.db.dal.dynamic;
import net.hasor.db.dal.fxquery.DefaultFxQuery;

import java.util.Map;

/**
 * <if> 标签
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class IfSqlNode extends SqlNode {
    private final String  testExpr;   // 判断表达式
    private final SqlNode block;      // 代码块

    public IfSqlNode(String testExpr, SqlNode block) {
        this.testExpr = testExpr;
        this.block = block;
    }

    @Override
    public void buildSql(StringBuilder queryString, Map<String, Object> contextMap) {
        Object testExprResult = DefaultFxQuery.evalOgnl(this.testExpr, contextMap);
        if (Boolean.TRUE.equals(testExprResult)) {
            this.block.buildSql(queryString, contextMap);
        }
    }
}