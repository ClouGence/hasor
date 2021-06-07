package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.utils.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class QueryDynamicSql extends DmlDynamicSql {
    private String        resultMapper;
    private String        resultType;
    private int           fetchSize;
    private ResultSetType resultSetType;
    private String        resultDataQL;

    public QueryDynamicSql(DynamicSql target) {
        super(target);
    }

    public QueryDynamicSql(DynamicSql target, Node operationNode) {
        super(target, operationNode);
        NamedNodeMap nodeAttributes = operationNode.getAttributes();
        Node resultMapperNode = nodeAttributes.getNamedItem("resultMapper");
        Node resultTypeNode = nodeAttributes.getNamedItem("resultType");
        Node fetchSizeNode = nodeAttributes.getNamedItem("fetchSize");
        Node resultSetTypeNode = nodeAttributes.getNamedItem("resultSetType");
        Node resultDataQLNode = nodeAttributes.getNamedItem("resultDataQL");
        String resultMapper = (resultMapperNode != null) ? resultMapperNode.getNodeValue() : null;
        String resultType = (resultTypeNode != null) ? resultTypeNode.getNodeValue() : null;
        String fetchSize = (fetchSizeNode != null) ? fetchSizeNode.getNodeValue() : null;
        String resultSetType = (resultSetTypeNode != null) ? resultSetTypeNode.getNodeValue() : null;
        String resultDataQL = (resultDataQLNode != null) ? resultDataQLNode.getNodeValue() : null;
        //
        this.resultMapper = resultMapper;
        this.resultType = resultType;
        this.fetchSize = StringUtils.isBlank(fetchSize) ? 256 : Integer.parseInt(fetchSize);
        this.resultSetType = ResultSetType.valueOfCode(resultSetType, ResultSetType.DEFAULT);
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

    public String getResultDataQL() {
        return this.resultDataQL;
    }

    public void setResultDataQL(String resultDataQL) {
        this.resultDataQL = resultDataQL;
    }
}
