package net.test.hasor.graphql.udfs;
import net.hasor.graphql.GraphUDF;
import net.hasor.graphql.UDF;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/18.
 */
@GraphUDF("userManager.findUserByID")
public class UserManager implements UDF {
    @Override
    public Object call(Map<String, Object> values) {
        HashMap<String, Object> udfData = new HashMap<String, Object>();
        udfData.put("userID", 12345);
        udfData.put("age", 31);
        udfData.put("nick", "my name is nick.");
        udfData.put("name", "this is name2.");
        udfData.put("status", true);
        return udfData;
    }
}