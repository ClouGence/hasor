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
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.utils.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Query SqlConfig
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public class QuerySqlConfig extends DmlSqlConfig {
    private String              resultMapper;
    private String              resultType;
    private int                 fetchSize;
    private ResultSetType       resultSetType;
    private MultipleResultsType multipleResultType;
    private String              resultDataQL;

    public QuerySqlConfig(DynamicSql target) {
        super(target);
    }

    public QuerySqlConfig(DynamicSql target, Node operationNode) {
        super(target, operationNode);
        NamedNodeMap nodeAttributes = operationNode.getAttributes();
        Node resultMapperNode = nodeAttributes.getNamedItem("resultMapper");
        Node resultTypeNode = nodeAttributes.getNamedItem("resultType");
        Node fetchSizeNode = nodeAttributes.getNamedItem("fetchSize");
        Node resultSetTypeNode = nodeAttributes.getNamedItem("resultSetType");
        Node resultDataQLNode = nodeAttributes.getNamedItem("resultDataQL");
        Node multipleResultNode = nodeAttributes.getNamedItem("multipleResult");
        String resultMapper = (resultMapperNode != null) ? resultMapperNode.getNodeValue() : null;
        String resultType = (resultTypeNode != null) ? resultTypeNode.getNodeValue() : null;
        String fetchSize = (fetchSizeNode != null) ? fetchSizeNode.getNodeValue() : null;
        String resultSetType = (resultSetTypeNode != null) ? resultSetTypeNode.getNodeValue() : null;
        String multipleResult = (multipleResultNode != null) ? multipleResultNode.getNodeValue() : null;
        String resultDataQL = (resultDataQLNode != null) ? resultDataQLNode.getNodeValue() : null;
        //
        this.resultMapper = resultMapper;
        this.resultType = resultType;
        this.fetchSize = StringUtils.isBlank(fetchSize) ? 256 : Integer.parseInt(fetchSize);
        this.resultSetType = ResultSetType.valueOfCode(resultSetType, ResultSetType.DEFAULT);
        this.multipleResultType = MultipleResultsType.valueOfCode(multipleResult, MultipleResultsType.LAST);
        this.resultDataQL = resultDataQL;
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Query;
    }

    public String getResultMapper() {
        return this.resultMapper;
    }

    public void setResultMapper(String resultMapper) {
        this.resultMapper = resultMapper;
    }

    public String getResultType() {
        return this.resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getFetchSize() {
        return this.fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public ResultSetType getResultSetType() {
        return this.resultSetType;
    }

    public void setResultSetType(ResultSetType resultSetType) {
        this.resultSetType = resultSetType;
    }

    public MultipleResultsType getMultipleResultType() {
        return this.multipleResultType;
    }

    public void setMultipleResultType(MultipleResultsType multipleResultType) {
        this.multipleResultType = multipleResultType;
    }

    public String getResultDataQL() {
        return this.resultDataQL;
    }

    public void setResultDataQL(String resultDataQL) {
        this.resultDataQL = resultDataQL;
    }
}
