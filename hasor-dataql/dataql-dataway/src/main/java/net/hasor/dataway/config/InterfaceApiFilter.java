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
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataway.daos.ApiQuery;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 负责处理 API 的执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
class InterfaceApiFilter implements InvokerFilter {
    @Inject
    private DataQL   dataQL;
    @Inject
    private ApiQuery apiQuery;
    private String   apiBaseUri;

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
        //        //        Map<String, List<String>> headerMap = RequestUtils.headerMap(invoker);
        //        Map<String, List<String>> cookieMap = RequestUtils.headerMap(invoker);
        //
        //            put("executionTime", System.currentTimeMillis());
        //            put("data", new HashMap<String, Object>() {{
        //                put("body", "<div>请编辑html内容</div>" + apiId);
        //                put("headers", "{'abc':" + apiId + "}");
        //                put("headerData", new ArrayList<Map<String, Object>>() {{
        //                    add(newData(true, "key1", "value-1"));
        //                    add(newData(true, "key2", "value-2"));
        //                    add(newData(true, "key3", "value-3"));
        //                    add(newData(false, "key4", "value-4"));
        //                }});
        //            }});
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        String requestUrl = invoker.getRequestPath();
        String queryApi = "return true;";//apiQuery.queryApi(requestUrl);
        QueryResult execute = dataQL.createQuery(queryApi).execute();
        //
        httpResponse.getWriter().write(JSON.toJSONString(execute.getData().unwrap()));
        //
        return null;
    }
}