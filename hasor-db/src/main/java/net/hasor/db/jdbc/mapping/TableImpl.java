package net.hasor.db.jdbc.mapping;
import java.lang.annotation.Annotation;

class TableImpl implements Table {
    private final String tableName;

    public TableImpl(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String value() {
        return this.tableName;
    }

    @Override
    public String name() {
        return this.tableName;
    }

    @Override
    public boolean caseInsensitive() {
        return true;
    }

    @Override
    public boolean autoFiled() {
        return true;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Table.class;
    }
}