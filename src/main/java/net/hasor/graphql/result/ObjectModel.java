package net.hasor.graphql.result;
import net.hasor.graphql.ListResult;
import net.hasor.graphql.ObjectResult;
import net.hasor.graphql.ValueResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public class ObjectModel extends HashMap<String, Object> implements ObjectResult {
    private List<String>        sortList;
    private Map<String, Object> objectData;
    public ObjectModel(List<String> sortList) {
        this.sortList = sortList;
    }
    @Override
    public int getFieldSize() {
        return 0;
    }
    @Override
    public List<String> getFieldNames() {
        return null;
    }
    @Override
    public boolean hasField(String fieldName) {
        return false;
    }
    @Override
    public Object getOriResult(String fieldName) {
        return this.objectData.get(fieldName);
    }
    @Override
    public ValueResult getValueResult(String fieldName) {
        return null;
    }
    @Override
    public ListResult getListResult(String fieldName) {
        return null;
    }
    @Override
    public ObjectResult getObjectResult(String fieldName) {
        return null;
    }
}