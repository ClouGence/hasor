package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.utils.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

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
