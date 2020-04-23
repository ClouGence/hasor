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
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.ParseParameterSpiListener;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * 编辑页面中预执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/perform")
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class PerformController extends BasicController {
    protected static Logger         logger = LoggerFactory.getLogger(PerformController.class);
    @Inject
    private          ApiCallService apiCallService;
    @Inject
    private          SpiTrigger     spiTrigger;

    @Post
    public Result<Map<String, Object>> doPerform(Invoker invoker, @QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        // .准备参数
        Map<String, Object> jsonParam = (Map<String, Object>) requestBody.get("requestBody");
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiID(apiId);
        apiInfo.setReleaseID("");
        apiInfo.setMethod(requestBody.get("select").toString());
        apiInfo.setApiPath(requestBody.get("apiPath").toString());
        apiInfo.setParameterMap(jsonParam);
        jsonParam = this.spiTrigger.chainSpi(ParseParameterSpiListener.class, (listener, lastResult) -> {
            return listener.parseParameter(true, apiInfo, invoker, lastResult);
        }, jsonParam);
        //
        String strCodeType = requestBody.get("codeType").toString();
        String strCodeValue = requestBody.get("codeValue").toString();
        if ("sql".equalsIgnoreCase(strCodeType)) {
            strCodeValue = DatawayUtils.evalCodeValueForSQL(strCodeValue, jsonParam);
        }
        // .执行调用
        try {
            Map<String, Object> objectMap = this.apiCallService.doCall(apiInfo, strCodeValue, jsonParam);
            return Result.of(objectMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return DatawayUtils.exceptionToResult(e);
        }
    }
}