package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class UpdateDynamicSql extends DmlDynamicSql {
    public UpdateDynamicSql(DynamicSql target) {
        super(target);
    }

    public UpdateDynamicSql(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Update;
    }
}
