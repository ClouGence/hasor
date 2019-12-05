package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

public class LdcRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void LDC_B_1_Test() throws Exception {
        Query compilerQL = compilerQL("return true;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void LDC_B_2_Test() throws Exception {
        Query compilerQL = compilerQL("return false;");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void LDC_B_3_Test() throws Exception {
        Query compilerQL = compilerQL("var a = true ;return a");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void LDC_B_4_Test() throws Exception {
        Query compilerQL = compilerQL("var a = false ;return a");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert !((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void LDC_D_1_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 123 ;return a"); // 指定数据宽度
        compilerQL.setOption(MIN_INTEGER_WIDTH, MIN_INTEGER_WIDTH_BYTE);
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isByte();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void LDC_D_2_Test() throws Exception {
        Query compilerQL = compilerQL("return 123"); // 定义十进制数
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void LDC_D_3_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 0o123 ;return a"); // 定义八进制数
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 83;
    }

    @Test
    public void LDC_D_4_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 0b01111011 ;return a"); // 定义二进制数
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void LDC_D_5_Test() throws Exception {
        Query compilerQL = compilerQL("var a = 0x123 ;return a"); // 定义十六进制数
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isInt();
        assert ((ValueModel) dataModel).asInt() == 0x123;
    }

    @Test
    public void LDC_S_1_Test() throws Exception {
        Query compilerQL = compilerQL("var a = '' ;return a");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("");
    }

    @Test
    public void LDC_S_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a = \"\" ;return a");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("");
    }

    @Test
    public void LDC_S_3_Test() throws Exception {
        Query compilerQL = compilerQL("return 'abc'");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("abc");
    }

    @Test
    public void LDC_N_1_Test() throws Exception {
        Query compilerQL = compilerQL("return null");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isNumber();
        assert !((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).isNull();
    }

    @Test
    public void LDC_N_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a = null ;return a");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValueModel();
        assert !((ValueModel) dataModel).isNumber();
        assert !((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).isNull();
    }
}