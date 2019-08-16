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
package net.hasor.rsf.rpc.net.http;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URL;

/**
 * Http 请求对象
 * @version : 2017年12月04日
 * @author 赵永春 (zyc@hasor.net)
 */
public class RequestObject {
    private HttpMethod  httpMethod;
    private HttpHeaders headers;
    private URL         httpURL;
    private byte[]      bodyData;

    RequestObject(HttpMethod httpMethod, HttpHeaders headers, URL httpURL, byte[] bodyData) {
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.httpURL = httpURL;
        this.bodyData = bodyData;
    }

    HttpHeaders headers() {
        return this.headers;
    }

    HttpMethod method() {
        return this.httpMethod;
    }

    URL requestFullPath() {
        return this.httpURL;
    }

    byte[] getBodyData() {
        return this.bodyData;
    }
}
