package net.hasor.db.dialect;
import net.hasor.utils.StringUtils;

import java.sql.JDBCType;

/**
 * 默认 SqlDialect 实现
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultSqlDialect implements SqlDialect {
    @Override
    public String buildSelect(String category, String tableName, String columnName, JDBCType jdbcType, Class<?> javaType) {
        return columnName;
    }

    @Override
    public String buildTableName(String category, String tableName) {
        if (StringUtils.isBlank(category)) {
            return tableName;
        } else {
            return category + "." + tableName;
        }
    }

    @Override
    public String buildColumnName(String category, String tableName, String columnName, JDBCType jdbcType, Class<?> javaType) {
        return columnName;
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
