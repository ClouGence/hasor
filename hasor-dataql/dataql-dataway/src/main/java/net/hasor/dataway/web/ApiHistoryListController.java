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
import net.hasor.dataway.authorization.PermissionType;
import net.hasor.dataway.authorization.RefAuthorization;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.config.MappingToUrl;
import net.hasor.dataway.config.Result;
import net.hasor.dataway.dal.ApiStatusEnum;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.web.annotation.Get;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Api 历史列表
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/api-history")
@RefAuthorization(PermissionType.ApiHistory)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class ApiHistoryListController extends BasicController {
    @Get
    public Result<List<Map<String, Object>>> apiHistory(@QueryParameter("id") String apiId) {
        List<Map<FieldDef, String>> releaseList = this.dataAccessLayer.listObjectBy(//
                EntityDef.RELEASE,      //
                DatawayUtils.conditionByApiId(apiId) //
        );
        releaseList = (releaseList == null) ? Collections.emptyList() : releaseList;
        //
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> dataList = releaseList.parallelStream()//
                .filter(releaseItem -> {
                    if (releaseItem == null) {
                        return false;
                    }
                    ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(releaseItem.get(FieldDef.STATUS));
                    return statusEnum != null && statusEnum != ApiStatusEnum.Delete;
                }).map((Function<Map<FieldDef, String>, Map<String, Object>>) releaseItem -> {
                    return new HashMap<String, Object>() {{
                        put("historyId", releaseItem.get(FieldDef.ID));
                        put("status", ApiStatusEnum.typeOf(releaseItem.get(FieldDef.STATUS)).typeNum());
                        put("time", dateFormat.format(new Date(Long.parseLong(releaseItem.get(FieldDef.RELEASE_TIME)))));
                    }};
                }).collect(Collectors.toList());
        return Result.of(dataList);
    }
}
