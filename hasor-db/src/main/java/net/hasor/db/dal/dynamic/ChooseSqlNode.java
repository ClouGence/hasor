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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <choose>、<when>、<otherwise> 标签
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class ChooseSqlNode extends SqlNode {
    /** 子节点 */
    private final List<IfBlock> subNodes = new ArrayList<>();
    private       SqlNode       defaultSqlNode;

    /** 追加子节点 */
    public void addChildNode(String testExpr, SqlNode block) {
        IfBlock ifBlock = new IfBlock();
        ifBlock.testExpr = testExpr;
        ifBlock.block = block;
        this.subNodes.add(ifBlock);
    }

    /** 追加子节点 */
    public void setDefaultNode(SqlNode block) {
        this.defaultSqlNode = block;
    }

    @Override
    public void buildSql(StringBuilder queryString, Map<String, Object> contextMap) {
        boolean useDefault = true;
        for (IfBlock ifBlock : this.subNodes) {
            Object testExprResult = DefaultFxQuery.evalOgnl(ifBlock.testExpr, contextMap);
            if (!Boolean.TRUE.equals(testExprResult)) {
                continue;
            }
            ifBlock.block.buildSql(queryString, contextMap);
            useDefault = false;
        }
        //
        if (useDefault) {
            this.defaultSqlNode.buildSql(queryString, contextMap);
        }
    }

    private static class IfBlock {
        private String  testExpr;   // 判断表达式
        private SqlNode block;      // 代码块
    }
}