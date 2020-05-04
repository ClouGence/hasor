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
import net.hasor.core.spi.SpiTrigger;
import net.hasor.dataql.DataQL;
import net.hasor.dataql.QueryResult;
import net.hasor.dataql.domain.ObjectModel;
import net.hasor.dataway.daos.ReleaseDetailQuery;
import net.hasor.dataway.service.ApiCallService;
import net.hasor.dataway.spi.ApiInfo;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.hasor.dataway.config.DatawayModule.ISOLATION_CONTEXT;

/**
 * 负责处理 API 的执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
class InterfaceApiFilter implements InvokerFilter {
    protected static Logger         logger = LoggerFactory.getLogger(InterfaceApiFilter.class);
    @Inject
    @Named(ISOLATION_CONTEXT)
    private          DataQL         dataQL;
    @Inject
    private          ApiCallService callService;
    @Inject
    private          SpiTrigger     spiTrigger;
    private          String         apiBaseUri;

    public InterfaceApiFilter(String apiBaseUri) {
        this.apiBaseUri = apiBaseUri;
    }

    @Override
    public void init(InvokerConfig config) {
        config.getAppContext().justInject(this);
        Objects.requireNonNull(this.dataQL, "dataQL init failed");
    }

    @Override
    public Object doInvoke(Invoker invoker, InvokerChain chain) throws Throwable {
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        HttpServletResponse httpResponse = invoker.getHttpResponse();
        String requestURI = invoker.getRequestPath();
        String httpMethod = httpRequest.getMethod().toUpperCase().trim();
        if (!requestURI.startsWith(this.apiBaseUri)) {
            return chain.doNext(invoker);
        }
        //
        DatawayUtils.resetLocalTime();
        String mimeType = invoker.getMimeType("json");
        httpRequest.setCharacterEncoding("UTF-8");
        httpResponse.setCharacterEncoding("UTF-8");
        CorsUtils.setup(invoker);
        //
        // .查询接口数据
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setPerform(false);
        String script = null;
        try {
            QueryResult queryResult = new ReleaseDetailQuery(this.dataQL).execute(new HashMap<String, String>() {{
                put("apiMethod", httpMethod);
                put("apiPath", URLDecoder.decode(requestURI, "UTF-8"));
            }});
            ObjectModel dataModel = (ObjectModel) queryResult.getData();
            apiInfo.setApiID(dataModel.getValue("apiID").asString());
            apiInfo.setReleaseID(dataModel.getValue("releaseID").asString());
            apiInfo.setMethod(httpMethod);
            apiInfo.setApiPath(requestURI);
            apiInfo.setOptionMap(dataModel.getObject("optionData").unwrap());
            script = dataModel.getValue("script").asString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            Object result = DatawayUtils.exceptionToResult(e).getResult();
            return responseData(mimeType, httpResponse, result);
        }
        //
        // .准备参数
        Map<String, Object> jsonParam;
        if ("GET".equalsIgnoreCase(httpMethod)) {
            jsonParam = new HashMap<>();
            Enumeration<String> parameterNames = httpRequest.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                jsonParam.put(paramName + "Arrays", httpRequest.getParameterValues(paramName));
                jsonParam.put(paramName, httpRequest.getParameter(paramName));
            }
        } else {
            String jsonBody = invoker.getJsonBodyString();
            if (StringUtils.isNotBlank(jsonBody)) {
                jsonParam = JSON.parseObject(jsonBody);
            } else {
                jsonParam = new HashMap<>();
            }
        }
        apiInfo.setParameterMap(jsonParam);
        //
        // .执行调用
        String finalScript = script;
        Object objectMap = this.callService.doCallWithoutError(apiInfo, param -> finalScript);
        return responseData(mimeType, httpResponse, objectMap);
    }

    private Object responseData(String mimeType, HttpServletResponse httpResponse, Object objectMap) throws IOException {
        if (!httpResponse.isCommitted()) {
            String body = JSON.toJSONString(objectMap, SerializerFeature.WriteMapNullValue);
            byte[] bodyByte = body.getBytes();
            //
            httpResponse.setContentType(mimeType);
            httpResponse.setContentLength(bodyByte.length);
            ServletOutputStream output = httpResponse.getOutputStream();
            output.write(bodyByte);
            output.flush();
            output.close();
        }
        return objectMap;
    }
}