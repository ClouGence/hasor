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
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.Hints;
import net.hasor.dataql.runtime.HintsSet;
import net.hasor.dataway.DatawayApi;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.config.DatawayUtils;
import net.hasor.dataway.daos.impl.ApiDataAccessLayer;
import net.hasor.dataway.daos.impl.EntityDef;
import net.hasor.dataway.daos.impl.FieldDef;
import net.hasor.dataway.domain.ApiInfoData;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.dataway.spi.CallSource;

import java.util.Map;
import java.util.Objects;

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
        ApiInfoData infoData = DatawayUtils.fillApiInfo(objectBy, new ApiInfoData());
        return new BasicDatawayApi(infoData);
    }

    private static final class BasicDatawayApi extends HintsSet implements DatawayApi {
        private ApiInfoData infoData;

        public BasicDatawayApi(ApiInfoData infoData) {
            this.infoData = Objects.requireNonNull(infoData, "is null");
            Map<String, Object> prepareHint = this.infoData.getPrepareHint();
            if (prepareHint != null) {
                prepareHint.forEach((key, value) -> super.setHint(key, value.toString()));
            }
        }

        @Override
        public String getApiID() {
            return this.infoData.getApiId();
        }

        @Override
        public String getMethod() {
            return this.infoData.getMethod();
        }

        @Override
        public String getApiPath() {
            return this.infoData.getApiPath();
        }

        @Override
        public Map<String, Object> getOptionMap() {
            return this.infoData.getOptionMap();
        }

        @Override
        public Hints getPrepareHint() {
            return this;
        }
    }
}