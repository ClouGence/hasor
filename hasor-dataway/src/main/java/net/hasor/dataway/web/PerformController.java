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
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.authorization.AuthorizationType;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CallSource;
import net.hasor.web.Invoker;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;

import javax.inject.Inject;
import java.util.Map;

/**
 * 编辑页面中预执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/perform")
@RefAuthorization(AuthorizationType.ApiEdit)
public class PerformController extends BasicController {
    @Inject
    private ApiCallService apiCallService;

    @Post
    public void doPerform(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody, Invoker invoker) throws Throwable {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        //
        // .准备参数
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setCallSource(CallSource.InterfaceUI);
        apiInfo.setApiID(apiId);
        apiInfo.setReleaseID("");
        apiInfo.setMethod(requestBody.get("select").toString());
        apiInfo.setApiPath(requestBody.get("apiPath").toString());
        apiInfo.setParameterMap((Map<String, Object>) requestBody.get("requestBody"));
        apiInfo.setOptionMap((Map<String, Object>) requestBody.get("optionInfo"));
        //
        // .执行调用
        Object objectMap = this.apiCallService.doCallWithoutError(apiInfo, jsonParam -> {
            String strCodeType = requestBody.get("codeType").toString();
            String strCodeValue = requestBody.get("codeValue").toString();
            if ("sql".equalsIgnoreCase(strCodeType)) {
                // .如果是 SQL 还需要进行代码替换
                return DatawayUtils.evalCodeValueForSQL(strCodeValue, jsonParam);
            } else {
                // .如果是 DataQL 那么就返回
                return strCodeValue;
            }
        });
        //
        DatawayUtils.responseData(//
                this.spiTrigger, apiInfo, invoker.getMimeType("json"), invoker, objectMap//
        );
    }
}