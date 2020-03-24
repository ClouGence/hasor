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
import net.hasor.dataql.DataQL;
import net.hasor.dataql.Query;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.runtime.ThrowRuntimeException;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.RequestUtils;
import net.hasor.dataway.config.Result;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 编辑页面中预执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/perform")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class PerformController {
    @Inject
    private DataQL dataQL;

    @Post
    public Result doPerform(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        String strCodeType = requestBody.get("codeType").toString();
        String strCodeValue = requestBody.get("codeValue").toString();
        Map<String, Object> strRequestBody = (Map<String, Object>) requestBody.get("requestBody");
        if ("sql".equalsIgnoreCase(strCodeType)) {
            strCodeValue = RequestUtils.evalCodeValueForSQL(strCodeValue, strRequestBody);
        }
        //
        try {
            Query dataQLQuery = this.dataQL.createQuery(strCodeValue);
            QueryResult queryResult = dataQLQuery.execute(strRequestBody);
            return Result.of(new HashMap<String, Object>() {{
                put("success", true);
                put("code", queryResult.getCode());
                put("executionTime", queryResult.executionTime());
                put("value", queryResult.getData().unwrap());
            }});
        } catch (Exception e) {
            if (e instanceof ThrowRuntimeException) {
                return Result.of(new HashMap<String, Object>() {{
                    put("success", false);
                    put("code", ((ThrowRuntimeException) e).getThrowCode());
                    put("executionTime", ((ThrowRuntimeException) e).getExecutionTime());
                    put("value", ((ThrowRuntimeException) e).getResult().unwrap());
                }});
            } else {
                return Result.of(new HashMap<String, Object>() {{
                    put("success", false);
                    put("code", 500);
                    put("executionTime", -1);
                    put("value", e.getMessage());
                }});
            }
        }
    }
}