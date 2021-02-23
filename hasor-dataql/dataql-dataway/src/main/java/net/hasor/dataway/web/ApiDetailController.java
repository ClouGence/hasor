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
import net.hasor.dataway.authorization.PermissionType;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.dal.*;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Api 详情数据
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/api-detail")
@RefAuthorization(PermissionType.ApiInfo)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiDetailController extends BasicController {
    @Get
    public Result<Map<String, Object>> apiDetail(@QueryParameter("id") String apiId) {
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (object == null) {
            return Result.of(404, "not found Api.");
        }
        //
        Map<String, Object> dataMap = new LinkedHashMap<>();
        dataMap.put("id", object.get(FieldDef.ID));
        dataMap.put("select", object.get(FieldDef.METHOD));
        dataMap.put("path", object.get(FieldDef.PATH));
        dataMap.put("status", ApiStatusEnum.typeOf(object.get(FieldDef.STATUS)).typeNum());
        dataMap.put("apiComment", object.get(FieldDef.COMMENT));
        dataMap.put("codeType", ApiTypeEnum.typeOf(object.get(FieldDef.TYPE)).typeString());
        dataMap.put("codeInfo", new HashMap<String, Object>() {{
            put("codeValue", object.get(FieldDef.SCRIPT_ORI));
            put("requestBody", object.get(FieldDef.REQ_BODY_SAMPLE));
            put("headerData", JSON.parseArray(object.get(FieldDef.REQ_HEADER_SAMPLE), HeaderData.class));
        }});
        dataMap.put("optionData", JSON.parseObject(object.get(FieldDef.OPTION)));
        return Result.of(dataMap);
    }
}
