package net.hasor.dataql.runtime.basic;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.HintValue;
import net.hasor.dataql.Query;
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ListModel;
import net.hasor.test.dataql.udfs.DataBean;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FmtListRuntimeTest extends AbstractTestResource implements HintValue {
    @Test
    public void fmt_1_Test() throws Exception {
        Query compilerQL = compilerQL("return ${data} => [a]");
        DataModel dataModel = compilerQL.execute().getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 0;
    }

    @Test
    public void fmt_2_Test() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("dataList", Arrays.asList(1, 3, 5, 7, 9));
        }};
        Query compilerQL = compilerQL("return ${dataList} => [#]");
        DataModel dataModel = compilerQL.execute(data).getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 5;
        assert ((ListModel) dataModel).getValue(0).asInt() == 1;
        assert ((ListModel) dataModel).getValue(2).asInt() == 5;
    }

    @Test
    public void fmt_3_Test() throws Exception {
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("dataList", Collections.singletonList(new DataBean(12)));
        }};
        Query compilerQL = compilerQL("return ${dataList} => [#]");
        DataModel dataModel = compilerQL.execute(data).getData();
        assert dataModel.isList();
        assert ((ListModel) dataModel).size() == 1;
        assert ((ListModel) dataModel).getObject(0).getValue("name").asString().equals("马三_12");
    }
}