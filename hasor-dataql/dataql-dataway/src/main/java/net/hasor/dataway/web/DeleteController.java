package net.hasor.dataway.web;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import java.util.Map;

/**
 *
 */
@MappingToUrl("/api/delete")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class DeleteController {
    @Post
    public Result doDelete(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) {
        boolean hasId = requestBody.containsKey("id");
        hasId = hasId && apiId.equalsIgnoreCase(requestBody.get("id").toString());
        //
        if (hasId) {
            return Result.of(hasId);
        } else {
            return Result.of(500, "delete failed.");
        }
    }
}