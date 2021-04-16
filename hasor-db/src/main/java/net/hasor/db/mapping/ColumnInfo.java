package net.hasor.db.mapping;
import net.hasor.db.types.TypeHandler;
import net.hasor.db.types.UnknownTypeHandler;

import java.lang.annotation.Annotation;

class ColumnInfo implements Column {
    private final String propertyName;

    public ColumnInfo(String propertyName) {
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
    public boolean update() {
        return true;
    }

    @Override
    public boolean insert() {
        return true;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Column.class;
    }
}
