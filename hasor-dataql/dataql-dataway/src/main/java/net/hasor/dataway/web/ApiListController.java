package net.hasor.dataway.web;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Get;
import net.hasor.web.render.RenderType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@MappingToUrl("/api/api-list")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiListController {
    @Get
    public Result apiList(Invoker invoker) {
        List<Map<String, Object>> mockData = new ArrayList<Map<String, Object>>() {{
            add(newData(0, "/demos/db/databases/"));
            add(newData(1, "/demos/db/tables/"));
            add(newData(2, "/demos/db/select/"));
            add(newData(3, "/demos/user/user-list/"));
            add(newData(0, "/demos/user/add-user/"));
            add(newData(1, "/demos/user/delete-user/"));
            add(newData(2, "/demos/role/role-list/"));
            add(newData(3, "/demos/role/add-role/"));
            add(newData(0, "/demos/role/delete-role/"));
            add(newData(1, "/demos/role/update-role/"));
            add(newData(2, "/demos/power/poser-list/"));
            add(newData(3, "/demos/power/power-id/"));
            add(newData(0, "/demos/power/check/"));
        }};
        return Result.of(mockData);
    }

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    private HashMap<String, Object> newData(int status, String pathInfo) {
        return new HashMap<String, Object>() {{
            put("id", atomicInteger.incrementAndGet());
            put("checked", false);
            put("path", pathInfo);
            put("status", status);
            put("comment", "现实所有表。");
        }};
    }
}
