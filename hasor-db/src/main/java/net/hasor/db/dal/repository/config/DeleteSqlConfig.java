package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class DeleteSqlConfig extends DmlSqlConfig {
    public DeleteSqlConfig(DynamicSql target) {
        super(target);
    }

    public DeleteSqlConfig(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Delete;
    }
}
