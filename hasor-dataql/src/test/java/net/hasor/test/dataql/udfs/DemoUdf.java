package net.hasor.test.dataql.udfs;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;

public class DemoUdf implements UDF {
    @Override
    public Object call(Object[] params, Option readOnly) {
        return new DataBean();
    }
}