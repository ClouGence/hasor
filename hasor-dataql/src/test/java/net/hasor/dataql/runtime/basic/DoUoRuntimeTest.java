package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DoUoRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void do_plus_1_Test() throws Exception {
        Query compilerQL = compilerQL("return true + false;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("truefalse");
    }

    @Test
    public void do_plus_2_Test() throws Exception {
        Query compilerQL = compilerQL("return 12 + 12;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 24;
    }

    @Test
    public void do_plus_3_Test() throws Exception {
        Query compilerQL = compilerQL("return 2 + 3 * 4;");
        compilerQL.setOption(MIN_INTEGER_WIDTH, MIN_INTEGER_WIDTH_BYTE);
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isByte();
        assert ((ValueModel) dataModel).asByte() == 14;
    }

    @Test
    public void do_plus_4_Test() throws Exception {
        Query compilerQL = compilerQL("return (2 + 3) * 4;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 20;
    }

    @Test
    public void do_minus_1_Test() throws Exception {
        Query compilerQL = compilerQL("return (2 + 3) - 0xF;"); // 0xF = 10
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == -10;
    }

    @Test
    public void do_minus_2_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", DomainHelper.convertTo(12));
            put("b", DomainHelper.convertTo(6));
        }};
        //
        Query compilerQL = compilerQL("return ${a}-${b};");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 6;
    }

    @Test
    public void uo_minus_1_Test() throws Exception {
        Query compilerQL = compilerQL("return - 1;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == -1;
    }

    @Test
    public void uo_minus_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a=123 return -a;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == -123;
    }

    @Test
    public void uo_not_1_Test() throws Exception {
        Query compilerQL = compilerQL("return !true;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void uo_not_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a=true return !a;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void uo_not_3_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", DomainHelper.convertTo(true));
        }};
        //
        Query compilerQL = compilerQL("return !${a};");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert !((ValueModel) dataModel).asBoolean();
    }
}