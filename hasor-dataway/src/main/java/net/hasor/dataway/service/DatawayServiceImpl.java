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
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.DatawayService;
import net.hasor.dataway.daos.ReleaseDetailQuery;
import net.hasor.dataway.spi.ApiInfo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static net.hasor.dataway.config.DatawayModule.ISOLATION_CONTEXT;

/**
 * 服务调用。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class DatawayServiceImpl implements DatawayService {
    @Inject
    @Named(ISOLATION_CONTEXT)
    private DataQL         dataQL;
    @Inject
    private ApiCallService callService;

    @Override
    public Object invokeApi(String method, String apiPath, Map<String, Object> jsonParam) throws Throwable {
        String httpMethod = method.trim().toUpperCase();
        ApiInfo apiInfo = new ApiInfo();
        QueryResult queryResult = new ReleaseDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
            put("apiMethod", httpMethod);
            put("apiPath", apiPath);
        }});
        ObjectModel dataModel = (ObjectModel) queryResult.getData();
        apiInfo.setApiID(dataModel.getValue("apiID").asString());
        apiInfo.setReleaseID(dataModel.getValue("releaseID").asString());
        apiInfo.setMethod(httpMethod);
        apiInfo.setApiPath(apiPath);
        apiInfo.setParameterMap(jsonParam);
        //
        String script = dataModel.getValue("script").asString();
        return this.callService.doCall(apiInfo, param -> script);
    }
}