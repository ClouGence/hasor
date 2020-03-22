package net.hasor.dataway.web;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@MappingToUrl("/api/perform")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class PerformController {
    @Post
    public Result doPerform(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) {
        boolean hasId = requestBody.containsKey("id");
        boolean hasSelect = requestBody.containsKey("select");
        boolean hasApiPath = requestBody.containsKey("apiPath");
        boolean hasCodeType = requestBody.containsKey("codeType");
        boolean hasCodeValue = requestBody.containsKey("codeValue");
        boolean hasRequestBody = requestBody.containsKey("requestBody");
        hasId = hasId && apiId.equalsIgnoreCase(requestBody.get("id").toString());
        //
        boolean ok = hasId && hasSelect && hasApiPath && hasCodeType && hasCodeValue && hasRequestBody;
        return Result.of(new HashMap<String, Object>() {{
            put("success", ok);
            put("code", 500);
            put("executionTime", 1000);
            put("value", new HashMap<String, Object>() {{
                put("body", requestBody);
                put("headers", new ArrayList<Map<String, Object>>() {{
                    add(newData(true, "key1", "value-1"));
                    add(newData(true, "key2", "value-2"));
                    add(newData(true, "key3", "value-3"));
                    add(newData(false, "key4", "value-4"));
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