package net.hasor.dataway.web;
import net.hasor.dataql.fx.DateTimeUdfSource;
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
@MappingToUrl("/api/api-history")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiHistoryController {
    @Get
    public Result apiHistory(@QueryParameter("id") String apiId, Invoker invoker) {
        String format = DateTimeUdfSource.format(System.currentTimeMillis(), "yyyy-MM-dd hh:mm:ss");
        return Result.of(new ArrayList<Map<String, Object>>() {{
            add(newData(0, format));
            add(newData(1, format));
            add(newData(2, format));
            add(newData(3, format));
        }});
    }

    private HashMap<String, Object> newData(int historyId, String timeStr) {
        return new HashMap<String, Object>() {{
            put("historyId", historyId);
            put("time", historyId + "-" + timeStr);
        }};
    }
}
