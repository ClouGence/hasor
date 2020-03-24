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
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiInfoQuery;
import net.hasor.dataway.daos.InsertApiQuery;
import net.hasor.dataway.daos.UpdateApiQuery;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 把编辑的结果保存起来。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/save-api")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class SaveApiController {
    @Inject
    private DataQL dataQL;

    @Post
    public Result doSave(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) throws IOException {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        Query query = null;
        if ("-1".equalsIgnoreCase(apiId)) {
            query = new InsertApiQuery(this.dataQL);
        } else {
            QueryResult queryResult = new ApiInfoQuery(this.dataQL).execute(new HashMap<String, String>() {{
                put("apiId", apiId);
            }});
            int status = ((ObjectModel) queryResult.getData()).getValue("status").asInt();
            requestBody.put("newStatus", (status == 1 || status == 2) ? 2 : 0);
            query = new UpdateApiQuery(this.dataQL);
        }
        //
        QueryResult queryResult = query.execute(new HashMap<String, Object>() {{
            put("postData", requestBody);
        }});
        return Result.of(queryResult.getData().unwrap());
    }
}