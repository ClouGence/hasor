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
import net.hasor.dataql.domain.DomainHelper;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.authorization.AuthorizationType;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.daos.ApiDetailQuery;
import net.hasor.dataway.daos.UpdateSchemaQuery;
import net.hasor.dataway.schema.types.Type;
import net.hasor.dataway.schema.types.TypesUtils;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CallSource;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 冒烟测试（于执行不同的是，冒烟测试的执行脚本和脚本类型信息来源于已保存的）
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/smoke")
@RefAuthorization(AuthorizationType.ApiEdit)
public class SmokeController extends BasicController {
    protected static Logger         logger = LoggerFactory.getLogger(SmokeController.class);
    @Inject
    private          ApiCallService apiCallService;

    @Post
    public void doSmoke(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) throws Throwable {
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
        apiInfo.setCallSource(CallSource.InterfaceUI);
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
        //
        DatawayUtils.responseData(//
                this.spiTrigger, apiInfo, invoker.getMimeType("json"), invoker, objectMap//
        );
    }

    private void updateSchema(String apiID, Object requestData, Object responseData) throws IOException {
        AtomicInteger atomicInteger = new AtomicInteger();
        String prefixType = "ApiType_" + apiID + "_";
        final Type reqType = TypesUtils.extractType(prefixType, atomicInteger, DomainHelper.convertTo(requestData));
        final Type resType = TypesUtils.extractType(prefixType, atomicInteger, DomainHelper.convertTo(responseData));
        //
        // .查询接口数据
        QueryResult result = new UpdateSchemaQuery(this.dataQL).execute(new HashMap<String, Object>() {{
            put("apiID", apiID);
            put("requestSchema", TypesUtils.toJsonSchema(reqType, false));
            put("responseSchema", TypesUtils.toJsonSchema(resType, false));
        }});
        logger.info("update schema apiID = " + apiID, ", result = " + JSON.toJSONString(result));
    }
}