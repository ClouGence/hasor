package net.hasor.dataql;
import net.hasor.dataql.Option;
import net.hasor.dataql.UDF;
/** 返回一个 double 类型的 number */
public class DoubleFoo implements UDF {
    public Object call(Object[] values, Option readOnly) {
        return 1234567.89012;
    }
}
