package net.hasor.dataql.runtime.ads;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.test.dataql.udfs.ErrorUdf;
import org.junit.Test;

public class ErrorTest extends AbstractTestResource {
    @Test
    public void udf_error() throws InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.test.dataql.udfs.ErrorUdf' as err;";
        qlString = qlString + "return err(a)";
        //
        try {
            DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
            DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
            assert false;
        } catch (Exception e) {
            assert e instanceof RuntimeException;
            assert e == ErrorUdf.ERR;
        }
    }

    @Test
    public void lambda_error() throws Throwable {
        String qlString = "";
        qlString = qlString + "var err = () -> throw 123, 'abc'; ";
        qlString = qlString + "var abc = err(); return 12345";
        //
        try {
            DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
            DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
            assert false;
        } catch (ThrowRuntimeException e) {
            assert e.getThrowCode() == 123;
            assert e.getResult().isValue();
            assert ((ValueModel) e.getResult()).asString().equals("abc");
        }
    }
}