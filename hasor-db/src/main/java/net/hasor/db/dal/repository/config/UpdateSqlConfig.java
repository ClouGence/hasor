package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class UpdateSqlConfig extends DmlSqlConfig {
    public UpdateSqlConfig(DynamicSql target) {
        super(target);
    }

    public UpdateSqlConfig(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Update;
    }
}
