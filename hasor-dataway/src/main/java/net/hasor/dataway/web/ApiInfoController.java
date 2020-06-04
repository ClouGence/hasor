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
import net.hasor.dataql.QueryResult;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.authorization.AuthorizationType;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiInfoQuery;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.io.IOException;
import java.util.HashMap;

/**
 * Api 信息
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/api-info")
@RefAuthorization(AuthorizationType.ApiInfo)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiInfoController extends BasicController {
    @Get
    public Result<Object> apiInfo(@QueryParameter("id") String apiId) throws IOException {
        QueryResult queryResult = new ApiInfoQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        return Result.of(queryResult.getData().unwrap());
    }
}