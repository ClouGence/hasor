package net.test.hasor.graphql.udfs;
import net.hasor.graphql.GraphUDF;
import net.hasor.graphql.UDF;

import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/18.
 */
@GraphUDF("foo")
public class Foo implements UDF {
    @Override
    public Object call(Map<String, Object> values) {
        return 12345;
    }
}