package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.Udf;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ValueModel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FunRuntimeTest extends AbstractTestResource implements HintValue {
    private Map<String, Object> object_list_map = new HashMap<String, Object>() {{
        put("list", new ArrayList<Object>() {{
            add("1");
            add("2");
            add(DomainHelper.convertTo("3"));
            add("4");
        }});
    }};

    @Test
    public void foo_1_Test() throws Exception {
        Map<String, Object> objectMap1 = new HashMap<String, Object>() {{
            put("udf", (Udf) (readOnly, values) -> object_list_map);
        }};
        //
        Query compilerQL = compilerQL("return ${udf}().list[0];");
        DataModel dataModel = compilerQL.execute(objectMap1).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isString();
        assert ((ValueModel) dataModel).asString().equals("1");
    }

    @Test
    public void foo_2_Test() throws Exception {
        //
        Udf udf = (readOnly, params) -> params;
        Query compilerQL = compilerQL("return ${_0}(1,2,3,4)[2];");
        DataModel dataModel = compilerQL.execute(udf).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNumber();
        assert ((ValueModel) dataModel).asInt() == 3;
    }
}