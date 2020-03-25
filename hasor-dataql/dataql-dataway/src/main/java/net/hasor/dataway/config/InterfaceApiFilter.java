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
package net.hasor.dataway.config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.daos.ReleaseDetailQuery;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 负责处理 API 的执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
class InterfaceApiFilter implements InvokerFilter {
    @Inject
    private DataQL dataQL;
    private String apiBaseUri;

    public InterfaceApiFilter(String apiBaseUri) {
        this.apiBaseUri = apiBaseUri;
    }

    @Override
    public void init(InvokerConfig config) {
        config.getAppContext().justInject(this);
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith(this.apiBaseUri)) {
            return chain.doNext(invoker);
        }
        //
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        String httpMethod = httpRequest.getMethod().toUpperCase().trim();
        Map<String, List<String>> headerMap = RequestUtils.headerMap(invoker);
        Map<String, List<String>> cookieMap = RequestUtils.headerMap(invoker);
        //
        //
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
            Map<String, Object> objectMap = RequestUtils.exceptionToResult(e).getResult();
            httpResponse.getWriter().write(JSON.toJSONString(objectMap));
            return objectMap;
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
            httpResponse.getWriter().write(JSON.toJSONString(hashMap, SerializerFeature.WriteMapNullValue));
            return hashMap;
        } catch (Exception e) {
            Map<String, Object> objectMap = RequestUtils.exceptionToResult(e).getResult();
            httpResponse.getWriter().write(JSON.toJSONString(objectMap, SerializerFeature.WriteMapNullValue));
            return objectMap;
        }
    }
}