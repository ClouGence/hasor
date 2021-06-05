package net.hasor.db.dal;
import net.hasor.db.dialect.BoundSql;
import net.hasor.db.types.TypeHandler;

import java.sql.JDBCType;

public interface DalBoundSql extends BoundSql {
    public SqlMode[] getSqlModes();

    public JDBCType[] getJdbcType();

    public Class<?>[] getJavaType();

    public TypeHandler<?>[] getTypeHandlers();
}
