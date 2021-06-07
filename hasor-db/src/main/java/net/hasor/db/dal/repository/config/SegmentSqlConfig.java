package net.hasor.db.dal.repository.config;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;

import java.sql.SQLException;

public class SegmentSqlConfig implements DynamicSql {
    private final DynamicSql target;

    public SegmentSqlConfig(DynamicSql target) {
        this.target = target;
    }

    public QueryType getDynamicType() {
        return QueryType.Segment;
    }

    @Override
    public boolean isHavePlaceholder() {
        return this.target.isHavePlaceholder();
    }

    @Override
    public void buildQuery(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder) throws SQLException {
        this.target.buildQuery(builderContext, querySqlBuilder);
    }
}