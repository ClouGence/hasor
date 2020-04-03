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
import net.hasor.dataway.service.ApiCallService;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerConfig;
import net.hasor.web.InvokerFilter;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 负责处理 API 的执行
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
class InterfaceApiFilter implements InvokerFilter {
    @Inject
    private ApiCallService apiCallService;
    private String         apiBaseUri;

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
        CorsUtils.setup(invoker);
        //
        Map<String, Object> objectMap = apiCallService.doCall(invoker);
        if (!httpResponse.isCommitted()) {
            String body = JSON.toJSONString(objectMap, SerializerFeature.WriteMapNullValue);
            byte[] bodyByte = body.getBytes();
            //
            httpResponse.setContentType(invoker.getMimeType("json"));
            httpResponse.setContentLength(bodyByte.length);
            ServletOutputStream output = httpResponse.getOutputStream();
            output.write(bodyByte);
            output.flush();
            output.close();
        }
        return objectMap;
    }
}