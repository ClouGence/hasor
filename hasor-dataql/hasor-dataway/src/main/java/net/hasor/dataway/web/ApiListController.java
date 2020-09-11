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
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.daos.ApiStatusEnum;
import net.hasor.dataway.daos.EntityDef;
import net.hasor.dataway.daos.FieldDef;
import net.hasor.web.annotation.Get;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Api 列表
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/api-list")
@RefAuthorization(AuthorizationType.ApiList)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiListController extends BasicController {
    @Get
    public Result<List<Map<String, Object>>> apiList() {
        List<Map<FieldDef, String>> infoList = this.dataAccessLayer.listObjectBy(//
                EntityDef.INFO,         //
                conditionByOrderByTime()//
        );
        infoList = (infoList == null) ? Collections.emptyList() : infoList;
        //
        List<Map<String, Object>> dataList = infoList.parallelStream()//
                .map((Function<Map<FieldDef, String>, Map<String, Object>>) infoMap -> {
                    return new HashMap<String, Object>() {{
                        put("id", infoMap.get(FieldDef.ID));
                        put("checked", false);
                        put("select", infoMap.get(FieldDef.METHOD));
                        put("path", infoMap.get(FieldDef.PATH));
                        put("status", ApiStatusEnum.typeOf(infoMap.get(FieldDef.STATUS)).typeNum());
                        put("comment", infoMap.get(FieldDef.COMMENT));
                    }};
                }).collect(Collectors.toList());
        return Result.of(dataList);
    }
}