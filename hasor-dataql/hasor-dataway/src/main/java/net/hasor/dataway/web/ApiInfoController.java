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
import net.hasor.dataway.authorization.AuthorizationType;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.impl.EntityDef;
import net.hasor.dataway.daos.impl.FieldDef;
import net.hasor.dataway.domain.ApiInfoData;
import net.hasor.dataway.domain.HeaderData;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.util.HashMap;
import java.util.Map;

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
    public Result<Object> apiInfo(@QueryParameter("id") String apiId) {
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (object == null) {
            return Result.of(404, "not found Api.");
        }
        ApiInfoData apiInfo = DatawayUtils.fillApiInfo(object, new ApiInfoData());
        //
        return Result.of(new HashMap<String, Object>() {{
            put("id", apiInfo.getApiId());
            put("select", apiInfo.getMethod());
            put("path", apiInfo.getApiPath());
            put("status", apiInfo.getStatus().typeNum());
            //put("apiComment", apiInfo.getComment());
            put("codeType", apiInfo.getType().typeString());
            put("requestBody", apiInfo.getRequestInfo().getExampleData());
            put("headerData", headerToList(apiInfo.getRequestInfo(), HeaderData::isChecked));
        }});
    }
}