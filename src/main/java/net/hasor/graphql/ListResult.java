package net.hasor.graphql;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public interface ListResult extends QueryResult {
    public int getSize();

    public QueryResult getOriResult(int index);

    public ValueResult getValueResult(int index);

    public ListResult getListResult(int index);

    public ObjectResult getObjectResult(int index);
}