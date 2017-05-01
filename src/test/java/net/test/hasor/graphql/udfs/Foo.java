package net.test.hasor.graphql.udfs;
import net.hasor.graphql.GraphUDF;

import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/18.
 */
public class Foo implements GraphUDF {
    @Override
    public Object call(Map<String, Object> values) {
        return 12345;
    }
}