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
import net.hasor.utils.StringUtils;
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
 * 发布API
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-24
 */
@MappingToUrl("/api/publish")
@RefAuthorization(AuthorizationType.ApiPublish)
@RenderType(value = "json", engineType = JsonRenderEngine.class)
public class PublishController extends BasicController {
    @Post
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Result<Object> doPublish(@QueryParameter("id") String apiId, @RequestBody() Map<String, Object> requestBody) {
        if (!apiId.equalsIgnoreCase(requestBody.get("id").toString())) {
            throw new IllegalArgumentException("id Parameters of the ambiguity.");
        }
        // 接口状态更新
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        object.putAll(STATUS_UPDATE_TO_PUBLISHED.get());// 把状态更新为发布
        boolean updateResult = this.dataAccessLayer.updateObject(//
                EntityDef.INFO, // 更新接口数据
                apiId,          // 接口ID
                object          // 把状态更新掉
        );
        if (!updateResult) {
            throw new RuntimeException("interface Published failed.");
        }
        //
        // 保存到发布列表
        String releaseID = this.dataAccessLayer.generateId(EntityDef.RELEASE, object.get(FieldDef.PATH));
        object.put(FieldDef.API_ID, object.get(FieldDef.ID));
        object.put(FieldDef.ID, releaseID);
        object.put(FieldDef.RELEASE_TIME, String.valueOf(System.currentTimeMillis()));
        object.put(FieldDef.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
        object.put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        boolean publishResult = this.dataAccessLayer.createObject(EntityDef.RELEASE, object);
        if (!publishResult) {
            throw new RuntimeException("release Published failed.");
        }
        /*      排除最后一个 Release 其余的全部更新为禁用      */
        //
        Map<QueryCondition, Object> releaseQueryCondition = new HashMap<>();
        releaseQueryCondition.put(QueryCondition.ApiId, apiId);
        List<Map<FieldDef, String>> releaseList = this.dataAccessLayer.listObjectBy(EntityDef.RELEASE, releaseQueryCondition);
        releaseList = (releaseList == null) ? Collections.emptyList() : releaseList;
        releaseList.stream().filter(apiRelease -> {
            String releaseItem = apiRelease.get(FieldDef.ID);
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(apiRelease.get(FieldDef.STATUS));
            // 已经是 Disable 的不在处理
            return statusEnum != ApiStatusEnum.Disable && !StringUtils.equalsIgnoreCase(releaseID, releaseItem);
        }).forEach(apiRelease -> {
            // 更新状态为 Disable
            String releaseId = apiRelease.get(FieldDef.ID);
            apiRelease = this.dataAccessLayer.getObjectBy(EntityDef.RELEASE, FieldDef.ID, releaseId);
            apiRelease.putAll(STATUS_UPDATE_TO_DISABLE.get());
            this.dataAccessLayer.updateObject(    //
                    EntityDef.RELEASE,  //
                    releaseId,          //
                    apiRelease          //
            );
        });
        return Result.of(true);
    }
}