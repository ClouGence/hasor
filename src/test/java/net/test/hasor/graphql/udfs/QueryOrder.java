package net.test.hasor.graphql.udfs;
import net.hasor.graphql.GraphUDF;
import net.hasor.graphql.UDF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/4/18.
 */
@GraphUDF("queryOrder")
public class QueryOrder implements UDF {
    @Override
    public Object call(Map<String, Object> values) {
        ArrayList<Object> orderList = new ArrayList<Object>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> udfData = new HashMap<String, Object>();
            udfData.put("accountID", 123);
            udfData.put("orderID", 123456789);
            udfData.put("itemID", 987654321);
            udfData.put("itemName", "商品名称");
            orderList.add(udfData);
        }
        return orderList;
    }
}