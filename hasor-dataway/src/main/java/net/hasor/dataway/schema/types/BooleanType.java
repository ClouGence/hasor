package net.hasor.dataway.schema.types;
/**
 * 布尔类型
 */
public class BooleanType extends DefaultValue<Boolean> {
    public TypeEnum getType() {
        return TypeEnum.Boolean;
    }
}