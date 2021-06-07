package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class CallableDynamicSql extends QueryDynamicSql {
    private MultipleResultsType multipleResultType;

    public CallableDynamicSql(DynamicSql target) {
        super(target);
    }

    public CallableDynamicSql(DynamicSql target, Node operationNode) {
        super(target, operationNode);
        NamedNodeMap nodeAttributes = operationNode.getAttributes();
        Node multipleResultNode = nodeAttributes.getNamedItem("multipleResult");
        String multipleResult = (multipleResultNode != null) ? multipleResultNode.getNodeValue() : null;
        //
        this.multipleResultType = MultipleResultsType.valueOfCode(multipleResult, MultipleResultsType.LAST);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Callable;
    }

    public MultipleResultsType getMultipleResultType() {
        return this.multipleResultType;
    }

    public void setMultipleResultType(MultipleResultsType multipleResultType) {
        this.multipleResultType = multipleResultType;
    }
}
