package net.hasor.graphql;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/2.
 */
public interface UDF {
    public Object call(Map<String, Object> values);
}