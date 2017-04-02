package net.hasor.graphql;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public interface ResultFormat {
    public Object formatValue(QueryResult result);
}