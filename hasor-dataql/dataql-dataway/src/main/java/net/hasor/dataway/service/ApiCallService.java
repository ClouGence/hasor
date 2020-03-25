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
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.config.RequestUtils;
import net.hasor.dataway.daos.ReleaseDetailQuery;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务调用。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class ApiCallService {
    @Inject
    private DataQL dataQL;

    public Map<String, Object> doCall(Invoker invoker) {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        String httpMethod = httpRequest.getMethod().toUpperCase().trim();
        String requestURI = httpRequest.getRequestURI();
        String script = null;
        try {
            QueryResult queryResult = new ReleaseDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
                put("apiMethod", httpMethod);
                put("apiPath", requestURI);
            }});
            ObjectModel dataModel = (ObjectModel) queryResult.getData();
            script = dataModel.getValue("script").asString();
            //    "releaseID" : pub_id,
            //    "apiID"     : pub_api_id,
            //    "apiMethod" : pub_method,
            //    "apiPath"   : pub_path,
            //    "script"    : pub_script
        } catch (Exception e) {
            return RequestUtils.exceptionToResult(e).getResult();
        }
        //
        try {
            Map<String, ?> jsonParam;
            if ("GET".equalsIgnoreCase(httpMethod)) {
                jsonParam = httpRequest.getParameterMap();
            } else {
                String jsonBody = invoker.getJsonBodyString();
                if (StringUtils.isNotBlank(jsonBody)) {
                    jsonParam = JSON.parseObject(jsonBody);
                } else {
                    jsonParam = new HashMap<>();
                }
            }
            QueryResult execute = dataQL.createQuery(script).execute(jsonParam);
            HashMap<String, Object> hashMap = new HashMap<String, Object>() {{
                put("success", true);
                put("code", execute.getCode());
                put("executionTime", execute.executionTime());
                put("value", execute.getData().unwrap());
            }};
            return hashMap;
        } catch (Exception e) {
            return RequestUtils.exceptionToResult(e).getResult();
        }
    }
}