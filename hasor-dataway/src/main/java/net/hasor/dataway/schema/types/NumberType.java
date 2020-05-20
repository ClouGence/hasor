package net.hasor.dataway.schema.types;
/**
 * 数
 */
public class NumberType extends DefaultValue<Number> {
    public TypeEnum getType() {
        return TypeEnum.Number;
    }
}