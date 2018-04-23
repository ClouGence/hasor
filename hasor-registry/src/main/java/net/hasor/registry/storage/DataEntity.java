package net.hasor.registry.storage;
import java.util.Map;
public class DataEntity {
    private String              dataKey;
    private String              dataValue;
    private Map<String, Object> attributeMap;
    //
    public String getDataKey() {
        return dataKey;
    }
    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }
    public String getDataValue() {
        return dataValue;
    }
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }
    public void setAttributeMap(Map<String, Object> attributeMap) {
        this.attributeMap = attributeMap;
    }
    public <T> T getAttr(String name, Class<T> type) {
    }
}
