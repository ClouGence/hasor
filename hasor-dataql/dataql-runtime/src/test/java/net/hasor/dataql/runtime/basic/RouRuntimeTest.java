package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.*;
import net.hasor.dataql.parser.QueryModel;
import net.hasor.dataql.compiler.qil.QIL;
import net.hasor.dataql.domain.*;
import net.hasor.dataql.runtime.CompilerArguments;
import net.hasor.dataql.runtime.QueryHelper;
import net.hasor.test.dataql.udfs.DataBean;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RouRuntimeTest extends AbstractTestResource implements HintValue {
    @Test
    public void rou_1_Test() throws Exception {
        Map<String, Object> objectMap1 = new HashMap<String, Object>() {{
            put("a", true);
        }};
        //
        Query compilerQL = compilerQL("return ${a};");
        DataModel dataModel = compilerQL.execute(objectMap1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_2_Test() throws Exception {
        Map<String, Object> objectMap2_1 = new HashMap<String, Object>() {{
            put("a", new HashMap<String, Object>() {{
                put("a", true);
            }});
        }};
        //
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute(objectMap2_1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_3_Test() throws Exception {
        Map<String, Object> objectMap2_2 = new HashMap<String, Object>() {{
            put("a", new ObjectModel() {{
                put("a", true);
            }});
        }};
        //
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute(objectMap2_2).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isBoolean();
        assert ((ValueModel) dataModel).asBoolean();
    }

    @Test
    public void rou_4_Test() throws Exception {
        Query compilerQL = compilerQL("return ${a}.a;");
        DataModel dataModel = compilerQL.execute((CustomizeScope) null).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isNull();
    }

    @Test
    public void rou_5_Test() throws Exception {
        Map<String, Object> objectMap3 = new HashMap<String, Object>() {{
            put("a", new DataBean());
        }};
        //
        Query compilerQL = compilerQL("return ${a}.name;");
        DataModel dataModel = compilerQL.execute(objectMap3).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("马三");
    }

    @Test
    public void rou_6_Test() throws Exception {
        Map<String, Map<String, Object>> objectMap = new HashMap<String, Map<String, Object>>() {{
            put("#", new HashMap<String, Object>() {{
                put("a", 1);
            }});
            put("$", new HashMap<String, Object>() {{
                put("a", 2);
            }});
            put("@", new HashMap<String, Object>() {{
                put("a", 3);
            }});
        }};
        Query compilerQL = compilerQL("return [#{a},${a},@{a}];");
        DataModel dataModel = compilerQL.execute(objectMap::get).getData();
        //
        assert dataModel.isList();
        assert ((ListModel) dataModel).getValue(0).asInt() == 1;
        assert ((ListModel) dataModel).getValue(1).asInt() == 2;
        assert ((ListModel) dataModel).getValue(2).asInt() == 3;
    }

    private Map<String, Object> object_list_map1 = new HashMap<String, Object>() {{
        put("list", new ArrayList<Object>() {{
            add("1");
            add("2");
            add(DomainHelper.convertTo("3"));
            add("4");
        }});
    }};
    private Map<String, Object> object_list_map2 = new HashMap<String, Object>() {{
        put("list", DomainHelper.convertTo(new ArrayList<Object>() {{
            add("1");
            add("2");
            add(DomainHelper.convertTo("3"));
            add("4");
        }}));
    }};

    @Test
    public void object_list_map_1_Test() throws Exception {
        Query compilerQL = compilerQL("return #{list}[-1];");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("4");
    }

    @Test
    public void object_list_map_2_Test() throws Exception {
        Query compilerQL = compilerQL("return #{list}[100];");// 从前向后检索溢出
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("4");
    }

    @Test
    public void object_list_map_3_Test() throws Exception {
        Query compilerQL = compilerQL("return #{list}[-2];");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("3");
    }

    @Test
    public void object_list_map_4_Test() throws Exception {
        Query compilerQL = compilerQL("return #{list}[1];");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("2");
    }

    @Test
    public void object_list_map_5_Test() throws Exception {
        Query compilerQL = compilerQL("return #{list}[-100];"); // 从后向前检索溢出
        DataModel dataModel = compilerQL.execute(object_list_map2).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("1");
    }

    @Test
    public void self_1_Test() throws Exception {
        Map<String, Object> objectMap1 = new HashMap<String, Object>() {{
            put("a", true);
        }};
        //
        Query compilerQL = compilerQL("return #");
        DataModel dataModel = compilerQL.execute(objectMap1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isNull();
    }

    @Test
    public void self_2_Test() throws Exception {
        Query compilerQL = compilerQL("var a=1; var data = [1,2,3]; return data[a]");
        DataModel dataModel = compilerQL.execute().getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asInt() == 2;
    }

    @Test
    public void list_1_Test() throws Exception {
        Map<String, Object> objectMap1 = new HashMap<String, Object>() {{
            put("a", Arrays.asList(null, null, null, null));
        }};
        //
        Query compilerQL = compilerQL("return ${_0}[1]");
        DataModel dataModel = compilerQL.execute(objectMap1).getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).isNull();
    }

    @Test
    public void compilerVar_1_Test() throws Exception {
        CompilerArguments arguments = CompilerArguments.DEFAULT;
        arguments.getCompilerVar().add("list");
        //
        QueryModel queryModel = QueryHelper.queryParser("return list[2]");
        QIL qil = QueryHelper.queryCompiler(queryModel, arguments, Finder.DEFAULT);
        Query query = QueryHelper.createQuery(qil, Finder.DEFAULT);
        //
        query.addShareVar("list", Arrays.asList("a", "b", "c", "d"));
        DataModel dataModel = query.execute().getData();
        //
        assert dataModel.isValue();
        assert ((ValueModel) dataModel).asString().equals("c");
    }
}
