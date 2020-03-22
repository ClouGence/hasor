package net.hasor.dataway.web;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import java.util.Map;

/**
 *
 */
@MappingToUrl("/api/check-path")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class CheckPathController {
    @Post
    public Result doCheckPath(@RequestBody() Map<String, Object> requestBody, Invoker invoker) {
        boolean hasPath = requestBody.containsKey("newPath");
        boolean hasSelect = requestBody.containsKey("newSelect");
        //
        if (hasPath && hasSelect) {
            return Result.of(true);
        } else {
            return Result.of(500, "lost field.");
        }
    }
}