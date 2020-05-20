package net.hasor.dataway.schema.types;
import java.util.List;
import java.util.Map;

/**
 * 结构类型
 */
public class StrutsType extends Type {
    /** 字段名集合，有序 */
    private List<String>      fieldNames;
    /** 每个字段Map */
    private Map<String, Type> fieldTypeMap;

    public TypeEnum getType() {
        return TypeEnum.Struts;
    }

    public List<String> getFieldNames() {
        return this.fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Map<String, Type> getFieldTypeMap() {
        return this.fieldTypeMap;
    }

    public void setFieldTypeMap(Map<String, Type> fieldTypeMap) {
        this.fieldTypeMap = fieldTypeMap;
    }
}