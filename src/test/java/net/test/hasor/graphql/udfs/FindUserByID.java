package net.test.hasor.graphql.udfs;
import net.hasor.graphql.GraphUDF;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/18.
 */
public class FindUserByID implements GraphUDF {
    @Override
    public Object call(Map<String, Object> values) {
        HashMap<String, Object> udfData = new HashMap<String, Object>();
        udfData.put("name", "this is name.");
        udfData.put("name2", "this is name2.");
        udfData.put("age", 31);
        udfData.put("nick", "my name is nick. dd");
        udfData.put("userID", 12345);
        udfData.put("status", true);
        return udfData;
    }
}