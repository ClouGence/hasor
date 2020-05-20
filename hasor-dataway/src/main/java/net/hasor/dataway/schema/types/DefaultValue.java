package net.hasor.dataway.schema.types;
/**
 * 参数类型
 */
public abstract class DefaultValue<T> extends Type {
    private T defaultValue;

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }
}