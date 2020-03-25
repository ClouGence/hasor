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
import net.hasor.dataql.domain.DataModel;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.config.JsonRenderEngine;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.RequestUtils;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiDetailQuery;
import net.hasor.dataway.service.CheckService;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.render.RenderType;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 冒烟测试（于执行不同的是，冒烟测试的执行脚本和脚本类型信息来源于已保存的）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/smoke")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class SmokeController {
    @Inject
    private DataQL       dataQL;
    @Inject
    private CheckService checkService;

    @Post
    public Result<Map<String, Object>> doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) throws IOException {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        QueryResult queryDetail = new ApiDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        this.checkService.checkApi(((ObjectModel) queryDetail.getData()).getValue("path").asString());
        String strCodeType = ((ObjectModel) queryDetail.getData()).getValue("codeType").asString();
        String strCodeValue = ((ObjectModel) queryDetail.getData()).getObject("codeInfo").getValue("codeValue").asString();
        Map<String, Object> strRequestBody = (Map<String, Object>) requestBody.get("requestBody");
        if ("sql".equalsIgnoreCase(strCodeType)) {
            strCodeValue = RequestUtils.evalCodeValueForSQL(strCodeValue, strRequestBody);
        }
        //
        try {
            Query dataQLQuery = this.dataQL.createQuery(strCodeValue);
            QueryResult queryResult = dataQLQuery.execute(strRequestBody);
            DataModel resultData = queryResult.getData();
            Result<Map<String, Object>> result = Result.of(new HashMap<String, Object>() {{
                put("success", true);
                put("code", queryResult.getCode());
                put("executionTime", queryResult.executionTime());
                put("value", resultData.unwrap());
            }});
            //
            this.updateSchema(apiId, strRequestBody, resultData);
            return result;
        } catch (Exception e) {
            return RequestUtils.exceptionToResult(e);
        }
    }

    private void updateSchema(String apiID, Map<String, Object> requestData, DataModel responseData) {
        //
    }
}