package net.hasor.dataql.runtime;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.CustomizeScope;
import net.hasor.dataql.OptionValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataql.domain.ValueModel;
import net.hasor.test.dataql.udfs.DataBean;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class RouRuntimeTest extends AbstractTestResource implements OptionValue {
    @Test
    public void rou_1_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", true);
        }};
        //
        Query compilerQL = compilerQL("return ${a};");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_2_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", new HashMap<String, Object>() {{
                put("a", true);
            }});
        }};
        //
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_3_Test() throws Exception {
        ObjectModel objectModel = new ObjectModel();
        objectModel.put("a", true);
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", objectModel);
        }};
        //
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_4_Test() throws Exception {
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute((CustomizeScope) null).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).isNull();
    }

    @Test
    public void rou_5_Test() throws Exception {
        Map<String, Object> objectMap = new HashMap<String, Object>() {{
            put("a", new DataBean());
        }};
        Query compilerQL = compilerQL("return ${a}.name;");
        DataModel dataModel = compilerQL.execute(objectMap).getData();
        //
        assert dataModel.isValueModel();
        assert ((ValueModel) dataModel).asString().equals("马三");
    }
}