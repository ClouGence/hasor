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
package net.hasor.dataway.service;
import com.alibaba.fastjson.JSON;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.Hints;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataway.DatawayApi;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.dal.*;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CallSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务调用。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class DatawayServiceImpl implements DatawayService {
    @Inject
    private ApiCallService     callService;
    @Inject
    private ApiDataAccessLayer dataAccessLayer;
    @Inject
    private SpiTrigger         spiTrigger;

    @Override
    public Object invokeApi(String apiPath, Map<String, Object> jsonParam) throws Throwable {
        Map<FieldDef, String> object = this.dataAccessLayer.getObjectBy(EntityDef.RELEASE, FieldDef.PATH, apiPath);
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setCallSource(CallSource.Internal);
        apiInfo.setReleaseID(object.get(FieldDef.ID));
        apiInfo.setApiID(object.get(FieldDef.API_ID));
        apiInfo.setMethod(object.get(FieldDef.METHOD));
        apiInfo.setApiPath(object.get(FieldDef.PATH));
        apiInfo.setParameterMap(jsonParam);
        //
        String script = object.get(FieldDef.SCRIPT);
        return this.callService.doCall(apiInfo, param -> script);
    }

    public DatawayApi getApiById(String apiId) throws Throwable {
        Map<FieldDef, String> objectBy = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (objectBy == null) {
            return null;
        }
        return new BasicDatawayApi(objectBy);
    }

    @Override
    public boolean disableApi(String apiId) {
        Map<FieldDef, String> objectBy = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (objectBy == null) {
            return true;
        }
        ApiStatusEnum apiStatusEnum = ApiStatusEnum.typeOf(objectBy.get(FieldDef.STATUS));
        if (apiStatusEnum == ApiStatusEnum.Disable || apiStatusEnum == ApiStatusEnum.Delete) {
            return true;
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
            apiRelease.putAll(DatawayUtils.STATUS_UPDATE_TO_DISABLE.get());
            this.dataAccessLayer.updateObject(//
                    EntityDef.RELEASE,  //
                    releaseId,          //
                    apiRelease          //
            );
        });
        // 更新主Api
        objectBy.putAll(DatawayUtils.STATUS_UPDATE_TO_DISABLE.get());
        return this.dataAccessLayer.updateObject(//
                EntityDef.INFO, //
                apiId,          //
                objectBy        //
        );
    }

    @Override
    public boolean deleteApi(String apiId) {
        Map<FieldDef, String> objectBy = this.dataAccessLayer.getObjectBy(EntityDef.INFO, FieldDef.ID, apiId);
        if (objectBy == null) {
            return true;
        }
        ApiStatusEnum apiStatusEnum = ApiStatusEnum.typeOf(objectBy.get(FieldDef.STATUS));
        if (apiStatusEnum == ApiStatusEnum.Delete) {
            return true;
        }
        //
        // 查询所有相关的 Release
        List<Map<FieldDef, String>> releaseList = this.dataAccessLayer.listObjectBy(//
                EntityDef.RELEASE,                  //
                DatawayUtils.conditionByApiId(apiId)//
        );
        //
        // 更新每一个 Release
        releaseList = (releaseList == null) ? Collections.emptyList() : releaseList;
        releaseList.stream().filter(apiRelease -> {
            // 已经是 Delete 的不在处理
            ApiStatusEnum statusEnum = ApiStatusEnum.typeOf(apiRelease.get(FieldDef.STATUS));
            return statusEnum != ApiStatusEnum.Delete;
        }).forEach(apiRelease -> {
            // 更新状态为 Delete
            String releaseId = apiRelease.get(FieldDef.ID);
            apiRelease.putAll(DatawayUtils.STATUS_UPDATE_TO_DELETE.get());
            this.dataAccessLayer.updateObject(//
                    EntityDef.RELEASE,  //
                    releaseId,          //
                    apiRelease          //
            );
        });
        //
        // 删除主Api
        return this.dataAccessLayer.deleteObject(EntityDef.INFO, apiId);
    }

    private static final class BasicDatawayApi extends HintsSet implements DatawayApi {
        private final Map<FieldDef, String> objectBy;

        public BasicDatawayApi(Map<FieldDef, String> objectBy) {
            this.objectBy = objectBy;
            Map<String, Object> prepareHint = JSON.parseObject(this.objectBy.get(FieldDef.PREPARE_HINT));
            if (prepareHint != null) {
                prepareHint.forEach((key, value) -> super.setHint(key, value.toString()));
            }
        }

        @Override
        public String getApiID() {
            return this.objectBy.get(FieldDef.ID);
        }

        @Override
        public String getMethod() {
            return this.objectBy.get(FieldDef.METHOD);
        }

        @Override
        public String getApiPath() {
            return this.objectBy.get(FieldDef.PATH);
        }

        @Override
        public Map<String, Object> getOptionMap() {
            return JSON.parseObject(this.objectBy.get(FieldDef.OPTION));
        }

        @Override
        public Hints getPrepareHint() {
            return this;
        }
    }
}