/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataway.web;
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.schema.types.Type;
import net.hasor.dataway.schema.types.TypesUtils;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对请求类型参数的结构分析
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/analyze-schema")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class AnalyzeSchemaController extends BasicController {
    protected static Logger         logger = LoggerFactory.getLogger(AnalyzeSchemaController.class);
    @Inject
    private          ApiCallService apiCallService;

    @Post
    public Result<Object> doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) throws Throwable {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        Object requestParameters = requestBody.get("requestParameters");
        Object requestSchema = requestBody.get("requestSchema");
        Type reqType = TypesUtils.extractType(("ReqApiType_" + apiId + "_"), new AtomicInteger(), DomainHelper.convertTo(requestParameters));
        //
        return Result.of(reqType);
    }
}