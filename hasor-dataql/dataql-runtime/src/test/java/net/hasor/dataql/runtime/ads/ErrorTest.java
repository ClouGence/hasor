package net.hasor.dataql.runtime.ads;
import net.hasor.core.Hasor;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.InstructRuntimeException;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.test.dataql.udfs.ErrorUdf;
import org.junit.Test;

public class ErrorTest extends AbstractTestResource {
    @Test
    public void udf_error() throws InstructRuntimeException {
        String qlString = "";
        qlString = qlString + "import 'net.hasor.test.dataql.udfs.ErrorUdf' as err;\n";
        qlString = qlString + "return err(a)";
        //
        try {
            DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
            DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
            assert false;
        } catch (Exception e) {
            e.printStackTrace();
            assert e instanceof RuntimeException;
            assert e.getCause() == ErrorUdf.ERR;
        }
    }

    @Test
    public void lambda_error() throws Throwable {
        String qlString = "";
        qlString = qlString + "var err = () -> throw 123, 'abc';\n";
        qlString = qlString + "var abc = err(); return 12345";
        //
        try {
            DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
            dataQL.configOption(DataQL.ConfigOption.CODE_LOCATION, CompilerArguments.CodeLocationEnum.TERM);
            DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
            assert false;
        } catch (ThrowRuntimeException e) {
            assert e.getLocation().toString().equalsIgnoreCase("1:16~1:32");
            assert e.getThrowCode() == 123;
            assert e.getResult().isValue();
            assert ((ValueModel) e.getResult()).asString().equals("abc");
        }
    }

    @Test
    public void eval_error() throws Throwable {
        String qlString = "";
        qlString = qlString + "var dat1 = 1;\n";
        qlString = qlString + "return null / dat1";
        //
        try {
            DataQL dataQL = Hasor.create().build().getInstance(DataQL.class);
            dataQL.configOption(DataQL.ConfigOption.CODE_LOCATION, CompilerArguments.CodeLocationEnum.TERM);
            DataModel dataModel = dataQL.createQuery(qlString).execute().getData();
            assert false;
        } catch (InstructRuntimeException e) {
            assert e.getLocation().toString().equalsIgnoreCase("2:12~2:13");
            assert e.getMessage().endsWith(" DO -> first data is null.");
        }
    }
}