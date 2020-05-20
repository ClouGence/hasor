package net.hasor.dataway.schema.types;
/**
 * Map类型，和StrutsType类似但对 key 无限制
 */
public class MapType extends Type {
    public TypeEnum getType() {
        return TypeEnum.Map;
    }
}