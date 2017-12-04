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
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/**
 * Http Netty 请求处理器
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestBuilder {
    private URL         httpRequest;
    private String      httpMethod;
    private HttpHeaders headers;
    private byte[]      contentData;
    private RequestBuilder(URL httpRequest, String httpMethod) {
        this.httpRequest = httpRequest;
        this.httpMethod = httpMethod;
        this.headers = new DefaultHttpHeaders();
    }
    public static RequestBuilder newBuild(String httpMethod, URL httpRequest) {
        return new RequestBuilder(httpRequest, httpMethod);
    }
    //
    /** Returns the name of the HTTP method with which this request was made, for example, GET, POST, or PUT. */
    public String getMethod() {
        return this.httpMethod;
    }
    /**设置请求数据*/
    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
    }
    /** Returns the value of the specified request header as a <code>String</code>. */
    public String getHeader(String name) {
        return this.headers.get(name);
    }
    /** set http request head. */
    public void setHeader(String name, String value) {
        this.headers.remove(name);
        this.headers.set(name, value);
    }
    /** set http request head. */
    public void addHeader(String name, String value) {
        this.headers.add(name, value);
    }
    /** remove http request head. */
    public void removeHeader(String name) {
        this.headers.remove(name);
    }
    /** set http request heads. */
    public void setHeaderValues(String name, List<String> valueList) {
        if (valueList == null) {
        }
        this.headers.remove(name);
        this.headers.set(name, valueList.iterator());
    }
    /** Returns all the values of the specified request header as an <code>Enumeration</code> of <code>String</code> objects. */
    public List<String> getHeaders(String name) {
        return Collections.unmodifiableList(this.headers.getAll(name));
    }
    /**
     * Returns an enumeration of all the header names this request contains.
     * If the request has no headers, this method returns an empty enumeration.
     */
    public Set<String> getHeaderNames() {
        return Collections.unmodifiableSet(this.headers.names());
    }
    public RequestObject buildObject() {
        return new RequestObject(HttpMethod.valueOf(this.httpMethod), this.headers, this.httpRequest, this.contentData);
    }
}