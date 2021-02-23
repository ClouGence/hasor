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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Api 信息
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/api-info")
@RefAuthorization(PermissionType.ApiInfo)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiInfoController extends BasicController {
    @Get
    public Result<Object> apiInfo(@QueryParameter("id") String apiId) {
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (object == null) {
            return Result.of(404, "not found Api.");
        }
        //
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", object.get(FieldDef.ID));
        hashMap.put("select", object.get(FieldDef.METHOD));
        hashMap.put("path", object.get(FieldDef.PATH));
        hashMap.put("status", ApiStatusEnum.typeOf(object.get(FieldDef.STATUS)).typeNum());
        //hashMap.put("apiComment", apiInfo.getComment());
        hashMap.put("codeType", ApiTypeEnum.typeOf(object.get(FieldDef.TYPE)).typeString());
        hashMap.put("requestBody", object.get(FieldDef.REQ_BODY_SAMPLE));
        //
        List<HeaderData> headerData = JSON.parseArray(object.get(FieldDef.REQ_HEADER_SAMPLE), HeaderData.class);
        headerData = (headerData == null) ?//
                Collections.emptyList() ://
                headerData.parallelStream().filter(HeaderData::isChecked).collect(Collectors.toList());
        hashMap.put("headerData", headerData);
        return Result.of(hashMap);
    }
}
