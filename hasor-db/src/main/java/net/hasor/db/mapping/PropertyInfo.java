package net.hasor.db.mapping;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.UnknownTypeHandler;

import java.lang.annotation.Annotation;
import java.sql.JDBCType;

class PropertyInfo implements Property {
    private final String propertyName;

    public PropertyInfo(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String value() {
        return this.propertyName;
    }

    @Override
    public String name() {
        return this.propertyName;
    }

    @Override
    public Class<? extends TypeHandler<?>> typeHandler() {
        return UnknownTypeHandler.class;
    }

    @Override
    public JDBCType jdbcType() {
        return JDBCType.OTHER;
    }

    @Override
    public boolean update() {
        return true;
    }

    @Override
    public boolean insert() {
        return true;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Property.class;
    }
}
