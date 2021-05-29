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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 多个 SQL 节点组合成一个 SqlNode
 * @author 赵永春 (zyc@byshell.org)
 * @version : 2021-05-24
 */
public class ArraySqlNode extends SqlNode {
    /** 子节点 */
    protected List<SqlNode> subNodes = new ArrayList<>();

    /** 追加子节点 */
    public void addChildNode(SqlNode node) {
        this.subNodes.add(node);
    }

    @Override
    public void buildSql(StringBuilder queryString, Map<String, Object> contextMap) {
        for (int i = 0; i < this.subNodes.size(); i++) {
            SqlNode sqlNode = this.subNodes.get(i);
            if (visitItem(i, sqlNode, contextMap)) {
                sqlNode.buildSql(queryString, contextMap);
            }
        }
    }

    protected boolean visitItem(int i, SqlNode sqlNode, Map<String, Object> contextMap) {
        return true;
    }
}