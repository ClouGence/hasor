package net.hasor.dataway.schema.types;
/**
 * 字符串值类型
 */
public class StringType extends DefaultValue<String> {
    public TypeEnum getType() {
        return TypeEnum.String;
    }
}