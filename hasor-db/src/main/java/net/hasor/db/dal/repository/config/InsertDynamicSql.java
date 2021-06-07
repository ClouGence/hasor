package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class InsertDynamicSql extends DmlDynamicSql {
    public InsertDynamicSql(DynamicSql target) {
        super(target);
    }

    public InsertDynamicSql(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Insert;
    }
}
