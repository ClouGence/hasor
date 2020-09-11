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
import net.hasor.dataway.dal.EntityDef;
import net.hasor.dataway.dal.FieldDef;
import net.hasor.db.transaction.Propagation;
import net.hasor.db.transaction.interceptor.Transactional;
import net.hasor.web.annotation.Post;
import net.hasor.web.annotation.QueryParameter;
import net.hasor.web.annotation.RequestBody;
import net.hasor.web.objects.JsonRenderEngine;
import net.hasor.web.render.RenderType;

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
        boolean updateResult = this.dataAccessLayer.updateObjectBy(//
                EntityDef.INFO, // 更新接口数据
                FieldDef.ID,    // 接口ID字段
                apiId,          // 接口ID
                object          // 把状态更新掉
        );
        if (!updateResult) {
            throw new RuntimeException("interface Published failed.");
        }
        //
        // 保存到发布列表
        object.put(FieldDef.API_ID, object.get(FieldDef.ID));
        object.put(FieldDef.ID, this.dataAccessLayer.generateId(EntityDef.RELEASE));
        object.put(FieldDef.RELEASE_TIME, String.valueOf(System.currentTimeMillis()));
        object.put(FieldDef.CREATE_TIME, String.valueOf(System.currentTimeMillis()));
        object.put(FieldDef.GMT_TIME, String.valueOf(System.currentTimeMillis()));
        return Result.of(this.dataAccessLayer.createObjectBy(EntityDef.RELEASE, object));
    }
}