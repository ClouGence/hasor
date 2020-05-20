package net.hasor.dataway.schema.types;
/**
 * 数组或集合类型
 */
public class ArrayType extends Type {
    /** 获取集合中的对象类型 */
    private Type genricType;

    public TypeEnum getType() {
        return TypeEnum.Array;
    }

    public Type getGenricType() {
        return genricType;
    }

    public void setGenricType(Type genricType) {
        this.genricType = genricType;
    }
}