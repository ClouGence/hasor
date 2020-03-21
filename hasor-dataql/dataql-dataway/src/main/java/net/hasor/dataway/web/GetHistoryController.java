package net.hasor.dataway.web;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.render.RenderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@MappingToUrl("/api/get-history")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class GetHistoryController {
    @Get
    public Result getHistory(@QueryParameter("historyId") String historyId, Invoker invoker) {
        return Result.of(new HashMap<String, Object>() {{
            put("select", "PUT");
            put("codeType", "SQL");
            put("codeInfo", new HashMap<String, Object>() {{
                put("codeValue", "<div>111111111</div>" + historyId);
                put("requestBody", "{'aaac':" + historyId + "}");
                put("headerData", new ArrayList<Map<String, Object>>() {{
                    add(newData(true, "key1", "value-1"));
                    add(newData(true, "key2", "value-2"));
                    add(newData(true, "key3", "value-3"));
                    add(newData(true, "key4", "value-4"));
                }});
            }});
        }});
    }

    private HashMap<String, Object> newData(boolean checked, String key, String value) {
        return new HashMap<String, Object>() {{
            put("checked", checked);
            put("name", key);
            put("value", value);
        }};
    }
}