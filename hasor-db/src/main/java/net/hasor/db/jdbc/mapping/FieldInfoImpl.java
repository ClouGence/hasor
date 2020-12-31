package net.hasor.db.jdbc.mapping;
import java.sql.JDBCType;

class FieldInfoImpl implements FieldInfo {
    private final String   columnName;
    private final JDBCType jdbcType;
    private final Class<?> javaType;

    public FieldInfoImpl(String columnName, JDBCType jdbcType, Class<?> javaType) {
        this.columnName = columnName;
        this.jdbcType = jdbcType;
        this.javaType = javaType;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public JDBCType getJdbcType() {
        return this.jdbcType;
    }

    @Override
    public Class<?> getJavaType() {
        return this.javaType;
    }
}