package net.hasor.db.dialect;
import net.hasor.db.types.mapping.FieldInfo;
import net.hasor.db.types.mapping.TableInfo;

/**
 * 默认 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSqlDialect implements SqlDialect {
    @Override
    public String buildSelect(TableInfo tableInfo, FieldInfo fieldInfo) {
        return fieldInfo.getColumnName();
    }

    @Override
    public String buildTableName(TableInfo tableInfo) {
        return tableInfo.getTableName();
    }

    @Override
    public String buildConditionName(TableInfo tableInfo, FieldInfo fieldInfo) {
        return fieldInfo.getColumnName();
    }

    @Override
    public BoundSql getCountSql(BoundSql boundSql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BoundSql getPageSql(BoundSql boundSql, int start, int limit) {
        throw new UnsupportedOperationException();
    }
}