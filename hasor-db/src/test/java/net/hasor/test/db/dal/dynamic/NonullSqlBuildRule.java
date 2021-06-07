package net.hasor.test.db.dal.dynamic;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.QuerySqlBuilder;
import net.hasor.db.dal.dynamic.ognl.OgnlUtils;
import net.hasor.db.dal.dynamic.rule.SqlBuildRule;

import java.sql.SQLException;
import java.util.Map;

public class NonullSqlBuildRule implements SqlBuildRule {
    @Override
    public void executeRule(BuilderContext builderContext, QuerySqlBuilder querySqlBuilder, String ruleValue, Map<String, String> config) throws SQLException {
        Object evalOgnl = OgnlUtils.evalOgnl(ruleValue, builderContext.getContext());
        querySqlBuilder.appendSql(evalOgnl.toString());
    }
}
