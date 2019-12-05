package net.hasor.dataql.runtime;
import net.hasor.dataql.AbstractTestResource;
import net.hasor.dataql.OptionValue;
import net.hasor.test.dataql.udfs.DataBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FmtListRuntimeTest extends AbstractTestResource implements OptionValue {
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
}