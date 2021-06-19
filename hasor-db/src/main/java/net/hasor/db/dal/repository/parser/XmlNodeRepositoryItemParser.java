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
package net.hasor.db.dal.repository.parser;
import net.hasor.db.dal.dynamic.DynamicParser;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.repository.RepositoryItemParser;
import net.hasor.db.dal.repository.config.*;
import net.hasor.utils.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 解析动态 SQL 配置（XML形式）
 * @version : 2021-06-05
 * @author 赵永春 (zyc@byshell.org)
 */
public class XmlNodeRepositoryItemParser extends DynamicParser implements RepositoryItemParser<Node> {
    protected QueryType getQueryType(String elementName, StatementType statementTypeEnum) {
        if (StringUtils.isBlank(elementName)) {
            throw new UnsupportedOperationException("tag name is Empty.");
        }
        if (statementTypeEnum == StatementType.Callable) {
            return QueryType.Callable;
        }
        return QueryType.valueOfTag(elementName);
    }

    public DynamicSql parseSqlConfig(Node configNode) {
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
                return new InsertSqlConfig(dynamicSql, configNode);
            case Delete:
                return new DeleteSqlConfig(dynamicSql, configNode);
            case Update:
                return new UpdateSqlConfig(dynamicSql, configNode);
            case Query:
                return new QuerySqlConfig(dynamicSql, configNode);
            case Callable:
                return new CallableSqlConfig(dynamicSql, configNode);
            case Segment:
                return new SegmentSqlConfig(dynamicSql);
            default:
                throw new UnsupportedOperationException("queryType '" + queryType.name() + "' Unsupported.");
        }
    }
}
