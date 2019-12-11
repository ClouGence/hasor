package net.hasor.test.dataql.udfs;
import net.hasor.dataql.Hints;
import net.hasor.dataql.UDF;

public class DemoUdf implements UDF {
    @Override
    public Object call(Object[] params, Hints readOnly) {
        return new DataBean();
    }
}