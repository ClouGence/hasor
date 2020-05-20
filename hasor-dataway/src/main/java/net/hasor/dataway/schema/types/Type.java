package net.hasor.dataway.schema.types;
/**
 * 参数类型
 */
public abstract class Type {
    /** 类型名 */
    private String name;

    /** 类型 */
    public abstract TypeEnum getType();

    /** 获取名字 */
    public String getName() {
        return this.name;
    }

    /** 设置名字 */
    public void setName(String name) {
        this.name = name;
    }
}