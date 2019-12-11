package net.hasor.test.dataql.udfs;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;

public class DemoUdf implements Udf {
    @Override
    public Object call(Hints readOnly, Object[] params) {
        return new DataBean();
    }
}