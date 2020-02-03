package net.hasor.dataql.runtime.basic;
import com.alibaba.fastjson.JSON;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.test.dataql.udfs.DataBean;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FmtObjRuntimeTest extends AbstractTestResource implements HintValue {
    private Map<String, Object> object_list_map0 = new HashMap<String, Object>() {{
        put("dataList", new ArrayList<>());
    }};
    private Map<String, Object> object_list_map1 = new HashMap<String, Object>() {{
        put("dataList", new ArrayList<Object>() {{
            add(new DataBean(1));
            add(new DataBean(2));
            add(new DataBean(3));
        }});
    }};
    //
    private Map<String, Object> object_list_map2 = new HashMap<String, Object>() {{
        put("dataList", new DataBean[0]);
    }};
    private Map<String, Object> object_list_map3 = new HashMap<String, Object>() {{
        put("dataList", new DataBean[] {//
                new DataBean(1),//
                new DataBean(2),//
                new DataBean(3) //
        });
    }};

    @Test
    public void fmt_1_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => {}");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).size() == 0;
    }

    @Test
    public void fmt_2_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => []");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 0;
    }

    @Test
    public void fmt_3_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => { 'a': name }");
        DataModel dataModel = compilerQL.execute(object_list_map1).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).getValue("a").asString().equals("马三_1");
    }

    @Test
    public void fmt_4_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => { 'a': name }");
        DataModel dataModel = compilerQL.execute(object_list_map3).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).getValue("a").asString().equals("马三_1");
    }

    @Test
    public void npt_1_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => { 'a': name }");
        DataModel dataModel = compilerQL.execute(object_list_map0).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).getValue("a").asString() == null;
    }

    @Test
    public void npt_2_Test() throws Exception {
        Query compilerQL = compilerQL("return ${dataList} => { 'a': name }");
        DataModel dataModel = compilerQL.execute(object_list_map2).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).getValue("a").asString() == null;
    }

    @Test
    public void self_1_Test() throws Exception {
        Query compiler1 = compilerQL("return ${_0} => { 'a': # }");
        Query compiler2 = compilerQL("return ${_0} => { 'a': ${_0}[0] }");
        Query compiler3 = compilerQL("return ${_0} => { 'a': $ }");
        DataModel data1 = compiler1.execute(new Object[] { Arrays.asList(1, 2, 3, 4, 5, 6, 7) }).getData();
        DataModel data2 = compiler2.execute(new Object[] { Arrays.asList(1, 2, 3, 4, 5, 6, 7) }).getData();
        DataModel data3 = compiler3.execute(new Object[] { Arrays.asList(1, 2, 3, 4, 5, 6, 7) }).getData();
        //
        String str1 = JSON.toJSONString(data1.unwrap());
        String str2 = JSON.toJSONString(data2.unwrap());
        String str3 = JSON.toJSONString(data3.unwrap());
        //
        assert str1.equalsIgnoreCase(str2);
        assert str2.equalsIgnoreCase(str3);
    }

    @Test
    public void self_2_Test() throws Exception {
        Object dataBeans = new DataBean[] {//
                new DataBean(1),//
                new DataBean(2),//
                new DataBean(3) //
        };
        Query compilerQL = compilerQL("return ${_0} => { 'a': $ }");
        DataModel dataModel = compilerQL.execute(new Object[] { dataBeans }).getData();
        assert dataModel.isObject();
        assert ((ObjectModel) dataModel).getObject("a").getValue("name").asString().equals("马三_1");
    }

    @Test
    public void self_3_1_Test() throws Exception {
        Object dataBeans = new DataBean[] {//
                new DataBean(1),//
                new DataBean(2),//
                new DataBean(3) //
        };
        Query compilerQL = compilerQL("return ${_0} => [ #.name ]");
        DataModel dataModel = compilerQL.execute(new Object[] { dataBeans }).getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).getValue(0).asString().equals("马三_1");
        assert ((ListModel) dataModel).getValue(1).asString().equals("马三_2");
        assert ((ListModel) dataModel).getValue(2).asString().equals("马三_3");
    }

    @Test
    public void self_3_2_Test() throws Exception {
        Object dataBeans = new DataBean[] {//
                new DataBean(1),//
                new DataBean(2),//
                new DataBean(3) //
        };
        Query compilerQL = compilerQL("return ${_0} => [ name ]");
        DataModel dataModel = compilerQL.execute(new Object[] { dataBeans }).getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).getValue(0).asString().equals("马三_1");
        assert ((ListModel) dataModel).getValue(1).asString().equals("马三_2");
        assert ((ListModel) dataModel).getValue(2).asString().equals("马三_3");
    }
}