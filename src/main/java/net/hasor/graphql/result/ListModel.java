package net.hasor.graphql.result;
import net.hasor.graphql.ListResult;
import net.hasor.graphql.ObjectResult;
import net.hasor.graphql.QueryResult;
import net.hasor.graphql.ValueResult;

import java.util.Collection;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public class ListModel implements ListResult {
    private Collection<Object> dataList;
    public ListModel(Collection<Object> dataList) {
        this.dataList = dataList;
    }
    @Override
    public int getSize() {
        return 0;
    }
    @Override
    public QueryResult getOriResult(int index) {
        return null;
    }
    @Override
    public ValueResult getValueResult(int index) {
        return null;
    }
    @Override
    public ListResult getListResult(int index) {
        return null;
    }
    @Override
    public ObjectResult getObjectResult(int index) {
        return null;
    }
}