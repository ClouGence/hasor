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
import net.hasor.dataway.dal.ApiStatusEnum;
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.dataway.dal.QueryCondition;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 禁用 API
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-25
 */
@MappingToUrl("/api/disable")
@RefAuthorization(AuthorizationType.ApiDisable)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class DisableController extends BasicController {
    @Post
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<Object> doDisable(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        Map<FieldDef, String> apiObject = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (apiObject == null) {
            return Result.of(404, "not found Api.");
        }
        //
        // 查询所有相关的 Release
        Map<QueryCondition, Object> queryCondition = new HashMap<QueryCondition, Object>() {{
            put(QueryCondition.ApiId, apiId);
        }};
        List<Map<FieldDef, String>> releaseList = this.dataAccessLayer.listObjectBy(EntityDef.RELEASE, queryCondition);
        //
        // 更新每一个 Release
        releaseList = (releaseList == null) ? Collections.emptyList() : releaseList;
        releaseList.stream().filter(apiRelease -> {
            // 已经是 Disable 的不在处理
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(apiRelease.get(FieldDef.STATUS));
            return statusEnum != ApiStatusEnum.Disable;
        }).forEach(apiRelease -> {
            // 更新状态为 Disable
            String releaseId = apiRelease.get(FieldDef.ID);
            apiRelease = this.dataAccessLayer.getObjectBy(EntityDef.RELEASE, FieldDef.ID, releaseId);
            apiRelease.putAll(STATUS_UPDATE_TO_DISABLE.get());
            this.dataAccessLayer.updateObject(//
                    EntityDef.RELEASE,  //
                    releaseId,          //
                    apiRelease          //
            );
        });
        // 更新主Api
        apiObject.putAll(STATUS_UPDATE_TO_DISABLE.get());
        return Result.of(this.dataAccessLayer.updateObject(//
                EntityDef.INFO, //
                apiId,          //
                apiObject       //
        ));
    }
}