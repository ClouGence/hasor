package net.hasor.test.db.dal.dynamic;
import net.hasor.db.dal.dynamic.BuilderContext;
import net.hasor.db.dal.dynamic.DynamicSql;
import net.hasor.db.dal.dynamic.nodes.TextDynamicSql;

import java.util.Map;

public class TextBuilderContext extends BuilderContext {
    public TextBuilderContext(Map<String, Object> context) {
        super(context);
    }

    @Override
    public DynamicSql findDynamicSqlById(String dynamicId) {
        if (dynamicId.endsWith("_allColumns")) {
            return new TextDynamicSql("*");
        } else {
            return super.findDynamicSqlById(dynamicId);
        }
    }
}
