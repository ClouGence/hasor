package net.hasor.test.db.dal.dynamic;
import net.hasor.db.dal.dynamic.rule.RuleRegistry;
import net.hasor.db.dal.dynamic.rule.SqlBuildRule;

public class TextRuleRegistry extends RuleRegistry {
    public SqlBuildRule findByName(String ruleName) {
        if ("nonull".equalsIgnoreCase(ruleName)) {
            return new NonullSqlBuildRule();
        }
        return super.findByName(ruleName);
    }
}
