package net.hasor.dataql.runtime;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class IfRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void if_1_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", true);
        }};
        //
        Query compilerQL = compilerQL(" if (${a}) return 123 else return 321");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 123;
    }

    @Test
    public void if_2_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", false);
        }};
        //
        Query compilerQL = compilerQL(" if (${a}) return 123 else return 321");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 321;
    }

    @Test
    public void if_3_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", false);
        }};
        //
        Query compilerQL = compilerQL("return ${a} ? 123 : 321");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 321;
    }

    @Test
    public void if_4_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", true);
        }};
        //
        Query compilerQL = compilerQL("return ${a} ? 123 : 321");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 123;
    }
}