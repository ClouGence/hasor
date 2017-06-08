package net.hasor.data.ql.udfs;
import net.hasor.data.ql.UDF;
import net.hasor.data.ql.Var;

import java.util.Map;
/**
 * Created by yongchun.zyc on 2017/6/8.
 */
public class Foreach implements UDF {
    @Override
    public Object call(Map<String, Var> values) {
        Var var = values.get("list");
        return var.getValue();
    }
}
