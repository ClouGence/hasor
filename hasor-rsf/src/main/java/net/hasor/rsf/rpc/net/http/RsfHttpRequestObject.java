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
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.utils.Iterators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
/**
 * Http 解码器组
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
class RsfHttpRequestObject extends HashMap<String, Object> implements RsfHttpRequest {
    private InterAddress                  remoteAddress;
    private InterAddress                  localAddress;
    private FullHttpRequest               httpRequest;
    private RequestInfo                   rsfRequest;
    private HashMap<String, List<String>> parameterMap;
    //
    RsfHttpRequestObject(InterAddress remoteAddress, InterAddress localAddress, FullHttpRequest httpRequest) {
        this.remoteAddress = remoteAddress;
        this.localAddress = localAddress;
        this.httpRequest = httpRequest;
        this.parameterMap = new HashMap<String, List<String>>();
    }
    //
    FullHttpRequest getNettyRequest() {
        return httpRequest;
    }
    RequestInfo getRsfRequest() {
        return this.rsfRequest;
    }
    void setRsfRequest(RequestInfo rsfRequest) {
        this.rsfRequest = rsfRequest;
    }
    void loadPostRequestBody() {
        //throw new java.lang.UnsupportedOperationException("temporary does not support the :" + this.getMethod());
    }
    void release() {
        this.clear();
        this.parameterMap.clear();
        IOUtils.releaseByteBuf(this.httpRequest);
    }
    // ----------------------------------------------------------------------------------
    @Override
    public String getRequestURI() {
        return this.httpRequest.uri();
    }
    @Override
    public String getQueryString() {
        String requestURI = this.getRequestURI();
        int indexOf = requestURI.indexOf("?");
        return indexOf > 0 ? requestURI.substring(indexOf + 1) : "";
    }
    @Override
    public String getProtocol() {
        return this.httpRequest.protocolVersion().protocolName();
    }
    @Override
    public String getMethod() {
        return this.httpRequest.method().name();
    }
    @Override
    public Object getAttribute(String name) {
        return super.get(name);
    }
    @Override
    public Enumeration<String> getAttributeNames() {
        return Iterators.asEnumeration(this.keySet().iterator());
    }
    @Override
    public void setAttribute(String name, Object o) {
        super.put(name, o);
    }
    @Override
    public void removeAttribute(String name) {
        super.remove(name);
    }
    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteBufInputStream(this.httpRequest.content());
    }
    @Override
    public long getContentLength() {
        return this.httpRequest.content().readableBytes();
    }
    @Override
    public String getHeader(String name) {
        return this.httpRequest.headers().get(name);
    }
    @Override
    public Enumeration<String> getHeaders(String name) {
        return Iterators.asEnumeration(this.httpRequest.headers().getAll(name).iterator());
    }
    @Override
    public Enumeration<String> getHeaderNames() {
        return Iterators.asEnumeration(this.httpRequest.headers().names().iterator());
    }
    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return values == null ? null : values[0];
    }
    @Override
    public Enumeration<String> getParameterNames() {
        return Iterators.asEnumeration(this.parameterMap.keySet().iterator());
    }
    @Override
    public String[] getParameterValues(String name) {
        List<String> strings = this.parameterMap.get(name);
        return (strings == null || strings.isEmpty()) ? null : (String[]) strings.toArray();
    }
    @Override
    public String getRemoteAddr() {
        return this.remoteAddress.getHost();
    }
    @Override
    public int getRemotePort() {
        return this.remoteAddress.getPort();
    }
    @Override
    public String getLocalAddr() {
        return this.localAddress.getHost();
    }
    @Override
    public int getLocalPort() {
        return this.localAddress.getPort();
    }
}