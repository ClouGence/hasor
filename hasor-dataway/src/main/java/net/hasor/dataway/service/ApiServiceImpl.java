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
import net.hasor.core.Type;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.DatawayApi;
import net.hasor.dataway.daos.ApiInfoSampleQuery;
import net.hasor.dataway.spi.ApiInfo;

import java.util.HashMap;

import static net.hasor.dataway.config.DatawayModule.ISOLATION_CONTEXT;

/**
 * 服务调用。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class ApiServiceImpl {
    @Inject(value = ISOLATION_CONTEXT, byType = Type.ByName)
    private DataQL dataQL;

    public DatawayApi getApiById(String apiId) throws Throwable {
        if (apiId == null) {
            return null;
        }
        //
        QueryResult queryResult = new ApiInfoSampleQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiId", apiId);
        }});
        ObjectModel unwrap = (ObjectModel) queryResult.getData();
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setApiID(unwrap.getValue("id").asString());
        apiInfo.setMethod(unwrap.getValue("select").asString());
        apiInfo.setApiPath(unwrap.getValue("path").asString());
        apiInfo.setOptionMap(unwrap.getObject("optionData").unwrap());
        return apiInfo;
    }
}