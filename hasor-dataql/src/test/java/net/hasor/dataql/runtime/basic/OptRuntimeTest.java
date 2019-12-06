package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.UDF;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

public class OptRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void opt_bool_1_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = true; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void opt_bool_2_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = false; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void opt_num_1_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = 123; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void opt_num_2_Test() throws Exception {
        Query compilerQL = compilerQL("option MIN_INTEGER_WIDTH = 'byte'; option abc = 123; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isByte();
        assert ((ValueModel) dataModel).asByte() == 123;
    }

    @Test
    public void opt_num_3_Test() throws Exception {
        Query compilerQL = compilerQL("option MIN_INTEGER_WIDTH = 'byte'; option abc = 1234; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isShort();
        assert ((ValueModel) dataModel).asShort() == 1234;
    }

    @Test
    public void opt_num_4_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = 0xabcdef; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 11259375;
    }

    @Test
    public void opt_str_1_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = 'abc'; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("abc");
    }

    @Test
    public void opt_null_1_Test() throws Exception {
        Query compilerQL = compilerQL("option abc = null; return ${_0}()");
        DataModel dataModel = compilerQL.execute((UDF) (params, readOnly) -> {
            return readOnly.getOption("abc");
        }).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNull();
    }
}