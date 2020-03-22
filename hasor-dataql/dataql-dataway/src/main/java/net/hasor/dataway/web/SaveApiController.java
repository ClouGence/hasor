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
@MappingToUrl("/api/save-api")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class SaveApiController {
    @Post
    public Result doSave(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) {
        boolean hasId = requestBody.containsKey("id");
        boolean hasSelect = requestBody.containsKey("select");
        boolean hasApiPath = requestBody.containsKey("apiPath");
        boolean hasComment = requestBody.containsKey("comment");
        boolean hasCodeType = requestBody.containsKey("codeType");
        boolean hasCodeValue = requestBody.containsKey("codeValue");
        boolean hasRequestBody = requestBody.containsKey("requestBody");
        boolean hasHeaderData = requestBody.containsKey("headerData");
        hasId = hasId && apiId.equalsIgnoreCase(requestBody.get("id").toString());
        //
        if (hasId && hasSelect && hasApiPath && hasComment && hasCodeType && hasCodeValue && hasRequestBody && hasHeaderData) {
            return Result.of(Integer.parseInt(apiId));
        } else {
            return Result.of(500, "lost field.");
        }
    }
}