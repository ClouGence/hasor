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
import com.alibaba.fastjson.JSON;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.RequestUtils;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiDetailQuery;
import net.hasor.dataway.daos.PublishApiQuery;
import net.hasor.utils.StringUtils;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 发布API
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/publish")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class PublishController extends BasicController {
    @Post
    public Result<Object> doPublish(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) throws IOException {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        QueryResult queryDetail = new ApiDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        String strCodeType = ((ObjectModel) queryDetail.getData()).getValue("codeType").asString();
        String strCodeValue = ((ObjectModel) queryDetail.getData()).getObject("codeInfo").getValue("codeValue").asString();
        String requestBodyJson = ((ObjectModel) queryDetail.getData()).getObject("codeInfo").getValue("requestBody").asString();
        if (StringUtils.isBlank(requestBodyJson)) {
            requestBodyJson = "{}";
        }
        Map<String, Object> strRequestBody = JSON.parseObject(requestBodyJson);
        if ("sql".equalsIgnoreCase(strCodeType)) {
            strCodeValue = RequestUtils.evalCodeValueForSQL(strCodeValue, strRequestBody);
        }
        //
        String finalStrCodeValue = strCodeValue;
        new PublishApiQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
            put("newScript", finalStrCodeValue);
        }});
        //
        return Result.of(true);
    }
}