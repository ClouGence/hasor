package net.hasor.graphql;
import java.util.List;
/**
 * Created by yongchun.zyc on 2017/3/29.
 */
public interface ObjectResult extends QueryResult {
    public int getFieldSize();

    public List<String> getFieldNames();

    public boolean hasField(String fieldName);

    public Object getOriResult(String fieldName);

    public ValueResult getValueResult(String fieldName);

    public ListResult getListResult(String fieldName);

    public ObjectResult getObjectResult(String fieldName);
}