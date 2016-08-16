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
package com.qq.connect.utils.http;
import com.qq.connect.QQConnectException;
import net.hasor.core.Singleton;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
/**
 * 扩展 QQ 的 HttpClient 提供一个能够正常返回 response error code 的 post 方法。
 * @version : 2016年08月13日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class HttpClientUtil extends com.qq.connect.utils.http.HttpClient {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    public Response httpGet(String tokenURL) throws QQConnectException {
        GetMethod getMethod = new GetMethod(tokenURL);
        HttpMethodParams var6 = getMethod.getParams();
        var6.setContentCharset("UTF-8");
        return doRequest(getMethod);
    }
    public Response httpPost(String tokenURL) throws QQConnectException {
        return this.httpPost(tokenURL, new PostParameter[0], new Header[0]);
    }
    public Response httpPost(String tokenURL, PostParameter[] params, Header[] headers) throws QQConnectException {
        PostMethod postMethod = new PostMethod(tokenURL);
        for (int param = 0; param < params.length; ++param) {
            postMethod.addParameter(params[param].getName(), params[param].getValue());
        }
        for (int param = 0; param < headers.length; ++param) {
            postMethod.addRequestHeader(headers[param]);
        }
        HttpMethodParams var6 = postMethod.getParams();
        var6.setContentCharset("UTF-8");
        return doRequest(postMethod);
    }
    private Response doRequest(HttpMethodBase httpMethod) throws QQConnectException {
        byte responseCode = -1;
        //
        Response response;
        try {
            httpMethod.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
            this.client.executeMethod(httpMethod);
            Header[] hearders = httpMethod.getResponseHeaders();
            int var18 = httpMethod.getStatusCode();
            this.logger.info("Response:");
            this.logger.info("https StatusCode:" + String.valueOf(var18));
            int e = hearders.length;
            for (int i$ = 0; i$ < e; ++i$) {
                Header header = hearders[i$];
                this.logger.info(header.getName() + ":" + header.getValue());
            }
            response = new Response();
            response.setResponseAsString(new String(httpMethod.getResponseBody(), "utf-8"));
            response.setStatusCode(var18);
            //
            this.logger.info(response.toString() + "\n");
            //
        } catch (IOException var16) {
            throw new QQConnectException(var16.getMessage(), var16, responseCode);
        } finally {
            httpMethod.releaseConnection();
        }
        return response;
    }
}
