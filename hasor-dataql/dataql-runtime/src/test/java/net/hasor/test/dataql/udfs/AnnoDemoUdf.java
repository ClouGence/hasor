package net.hasor.test.dataql.udfs;
import net.hasor.dataql.DimUdf;
import net.hasor.dataql.Hints;
import net.hasor.dataql.Udf;

@DimUdf("test")
public class AnnoDemoUdf implements Udf {
    @Override
    public Object call(Hints readOnly, Object[] params) {
        return "test";
    }
}