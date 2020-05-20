package net.hasor.dataway.schema.types;
/**
 * 引用其它类型
 */
public class RefType extends Type {
    /** 采用的原始类型 */
    private String refType;

    public TypeEnum getType() {
        return TypeEnum.Ref;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }
}