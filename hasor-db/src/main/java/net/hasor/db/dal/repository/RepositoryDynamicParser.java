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
package net.hasor.db.dal.repository;
import net.hasor.db.dal.dynamic.DynamicParser;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.repository.config.*;
import net.hasor.utils.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 解析动态 SQL 配置
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class RepositoryDynamicParser extends DynamicParser {
    protected QueryType getQueryType(String elementName, StatementType statementTypeEnum) {
        if (StringUtils.isBlank(elementName)) {
            throw new UnsupportedOperationException("tag name is Empty.");
        }
        if (statementTypeEnum == StatementType.Callable) {
            return QueryType.Callable;
        }
        switch (elementName) {
            case "insert":
                return QueryType.Insert;
            case "delete":
                return QueryType.Delete;
            case "update":
                return QueryType.Update;
            case "select":
                return QueryType.Query;
            case "callable":
                return QueryType.Callable;
            case "sql":
                return QueryType.Segment;
            default:
                return null;
        }
    }

    protected DynamicSql parseDynamicSql(Node configNode) {
        NamedNodeMap nodeAttributes = configNode.getAttributes();
        Node statementTypeNode = nodeAttributes.getNamedItem("statementType");
        String statementType = (statementTypeNode != null) ? statementTypeNode.getNodeValue() : null;
        StatementType statementTypeEnum = StatementType.valueOfCode(statementType, StatementType.Prepared);
        //
        QueryType queryType = getQueryType(configNode.getNodeName().toLowerCase().trim(), statementTypeEnum);
        if (queryType == null) {
            return null;
        }
        DynamicSql dynamicSql = super.parseDynamicSql(configNode);
        if (dynamicSql == null) {
            return null;
        }
        switch (queryType) {
            case Insert:
                return new InsertDynamicSql(dynamicSql, configNode);
            case Delete:
                return new DeleteDynamicSql(dynamicSql, configNode);
            case Update:
                return new UpdateDynamicSql(dynamicSql, configNode);
            case Query:
                return new QueryDynamicSql(dynamicSql, configNode);
            case Callable:
                return new CallableDynamicSql(dynamicSql, configNode);
            case Segment:
                return new SegmentDynamicSql(dynamicSql);
            default:
                throw new UnsupportedOperationException("" + queryType.name() + "> Unsupported.");
        }
    }
}
