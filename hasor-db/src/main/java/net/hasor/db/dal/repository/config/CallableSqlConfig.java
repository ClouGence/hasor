package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class CallableSqlConfig extends QuerySqlConfig {
    public CallableSqlConfig(DynamicSql target) {
        super(target);
    }

    public CallableSqlConfig(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Callable;
    }
}
