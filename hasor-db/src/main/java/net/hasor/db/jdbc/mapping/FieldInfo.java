package net.hasor.db.jdbc.mapping;
import net.hasor.db.jdbc.TypeHandler;
import net.hasor.db.types.UnknownTypeHandler;

import java.lang.annotation.Annotation;
import java.sql.JDBCType;

class FieldInfo implements Field {
    private final String   fieldName;
    private final JDBCType jdbcType;

    public FieldInfo(String fieldName, JDBCType jdbcType) {
        this.fieldName = fieldName;
        this.jdbcType = jdbcType;
    }

    @Override
    public String value() {
        return this.fieldName;
    }

    @Override
    public String name() {
        return this.fieldName;
    }

    @Override
    public JDBCType jdbcType() {
        return this.jdbcType;
    }

    @Override
    public Class<? extends TypeHandler<?>> typeHandler() {
        return UnknownTypeHandler.class;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Field.class;
    }
}