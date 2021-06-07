package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.DynamicSql;
import org.w3c.dom.Node;

public class InsertSqlConfig extends DmlSqlConfig {
    public InsertSqlConfig(DynamicSql target) {
        super(target);
    }

    public InsertSqlConfig(DynamicSql target, Node operationNode) {
        super(target, operationNode);
    }

    @Override
    public QueryType getDynamicType() {
        return QueryType.Insert;
    }
}
