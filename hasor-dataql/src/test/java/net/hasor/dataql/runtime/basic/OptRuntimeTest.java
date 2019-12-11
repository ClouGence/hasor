package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.Udf;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

public class OptRuntimeTest extends AbstractTestResource implements HintValue {
    @Test
    public void opt_bool_1_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = true; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void opt_bool_2_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = false; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void opt_num_1_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = 123; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void opt_num_2_Test() throws Exception {
        Query compilerQL = compilerQL("hint MIN_INTEGER_WIDTH = 'byte'; hint abc = 123; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isByte();
        assert ((ValueModel) dataModel).asByte() == 123;
    }

    @Test
    public void opt_num_3_Test() throws Exception {
        Query compilerQL = compilerQL("hint MIN_INTEGER_WIDTH = 'byte'; hint abc = 1234; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isShort();
        assert ((ValueModel) dataModel).asShort() == 1234;
    }

    @Test
    public void opt_num_4_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = 0xabcdef; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 11259375;
    }

    @Test
    public void opt_str_1_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = 'abc'; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("abc");
    }

    @Test
    public void opt_null_1_Test() throws Exception {
        Query compilerQL = compilerQL("hint abc = null; return ${_0}()");
        DataModel dataModel = compilerQL.execute((Udf) (readOnly, params) -> {
            return readOnly.getHint("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNull();
    }
}