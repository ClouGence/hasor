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
 * All DML SqlConfig
 * @version : 2021-06-19
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class DmlSqlConfig extends SegmentSqlConfig {
    private StatementType statementType = StatementType.Prepared;
    private int           timeout       = -1;
    private String        parameterType = null;

    public DmlSqlConfig(DynamicSql target) {
        super(target);
    }

    public DmlSqlConfig(DynamicSql target, Node operationNode) {
        super(target);
        NamedNodeMap nodeAttributes = operationNode.getAttributes();
        Node statementTypeNode = nodeAttributes.getNamedItem("statementType");
        Node timeoutNode = nodeAttributes.getNamedItem("timeout");
        Node parameterTypeNode = nodeAttributes.getNamedItem("parameterType");
        String statementType = (statementTypeNode != null) ? statementTypeNode.getNodeValue() : null;
        String timeout = (timeoutNode != null) ? timeoutNode.getNodeValue() : null;
        String parameterType = (parameterTypeNode != null) ? parameterTypeNode.getNodeValue() : null;
        //
        this.statementType = StatementType.valueOfCode(statementType, StatementType.Prepared);
        this.timeout = StringUtils.isBlank(timeout) ? -1 : Integer.parseInt(timeout);
        this.parameterType = parameterType;
    }

    public abstract QueryType getDynamicType();

    public StatementType getStatementType() {
        return this.statementType;
    }

    public void setStatementType(StatementType statementType) {
        this.statementType = statementType;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getParameterType() {
        return this.parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }
}
