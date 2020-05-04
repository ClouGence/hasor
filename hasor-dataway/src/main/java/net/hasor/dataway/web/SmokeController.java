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
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiDetailQuery;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * 冒烟测试（于执行不同的是，冒烟测试的执行脚本和脚本类型信息来源于已保存的）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/smoke")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class SmokeController extends BasicController {
    @Inject
    private ApiCallService apiCallService;

    @Post
    public Result<Object> doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) throws Throwable {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        // .查询接口数据
        QueryResult queryDetail = new ApiDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        ObjectModel objectModel = ((ObjectModel) queryDetail.getData());
        //
        // .获取API信息
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setPerform(true);
        apiInfo.setApiID(apiId);
        apiInfo.setReleaseID("");
        apiInfo.setMethod(objectModel.getValue("select").asString());
        apiInfo.setApiPath(objectModel.getValue("path").asString());
        String strCodeType = objectModel.getValue("codeType").asString();
        String strCodeValue = objectModel.getObject("codeInfo").getValue("codeValue").asString();
        //
        // .准备参数
        String jsonParamValue = objectModel.getObject("codeInfo").getValue("requestBody").asString();
        jsonParamValue = (StringUtils.isBlank(jsonParamValue)) ? "{}" : jsonParamValue;
        apiInfo.setParameterMap(JSON.parseObject(jsonParamValue));
        apiInfo.setOptionMap(objectModel.getObject("optionData").unwrap());
        //
        // .执行调用
        Object objectMap = this.apiCallService.doCallWithoutError(apiInfo, jsonParam -> {
            if ("sql".equalsIgnoreCase(strCodeType)) {
                // .如果是 SQL 还需要进行代码替换
                return DatawayUtils.evalCodeValueForSQL(strCodeValue, jsonParam);
            } else {
                // .如果是 DataQL 那么就返回
                return strCodeValue;
            }
        });
        this.updateSchema(apiId, apiInfo.getParameterMap(), objectMap);
        return Result.of(objectMap);
    }

    private void updateSchema(String apiID, Map<String, Object> requestData, Object responseData) {
        //
    }
}