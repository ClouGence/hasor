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
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.http.*;
import net.hasor.rsf.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
/**
 * Http 解码器组
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
class RsfHttpResponseObject implements RsfHttpResponse {
    private FullHttpResponse httpResponse;
    private AtomicBoolean    committedStatus;
    //
    RsfHttpResponseObject(RsfHttpRequestObject httpRequest) {
        FullHttpRequest nettyRequest = httpRequest.getNettyRequest();
        HttpVersion httpVersion = nettyRequest.protocolVersion();
        HttpResponseStatus status = HttpResponseStatus.OK;
        this.httpResponse = new DefaultFullHttpResponse(httpVersion, status);
        this.committedStatus = new AtomicBoolean(false);
    }
    RsfHttpResponseObject(HttpVersion httpVersion, HttpResponseStatus status) {
        this.httpResponse = new DefaultFullHttpResponse(httpVersion, status);
        this.committedStatus = new AtomicBoolean(false);
    }
    //
    FullHttpResponse getHttpResponse() {
        return httpResponse;
    }
    void release() {
        IOUtils.releaseByteBuf(this.httpResponse);
    }
    // ----------------------------------------------------------------------------------
    @Override
    public String getContentType() {
        return this.httpResponse.headers().get(CONTENT_TYPE);
    }
    @Override
    public void setContentType(String type) {
        this.httpResponse.headers().set(CONTENT_TYPE, type);
    }
    @Override
    public OutputStream getOutputStream() throws IOException {
        return new ByteBufOutputStream(this.httpResponse.content());
    }
    @Override
    public boolean isCommitted() {
        return this.committedStatus.get();
    }
    public void flushBuffer() throws IOException {
        this.committedStatus.set(true);
        int readableBytes = this.httpResponse.content().readableBytes();
        this.setContentLength(readableBytes);
    }
    @Override
    public void setContentLength(long len) {
        this.httpResponse.headers().set(CONTENT_LENGTH, len);
    }
    //
    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.httpResponse.setStatus(HttpResponseStatus.parseLine(String.valueOf(sc) + " " + msg));
    }
    @Override
    public void sendError(int sc) throws IOException {
        this.sendError(sc, null);
    }
    @Override
    public int getStatus() {
        if (this.httpResponse.status() != null) {
            return this.httpResponse.status().code();
        }
        return 0;
    }
    //
    @Override
    public boolean containsHeader(String name) {
        return this.httpResponse.headers().contains(name);
    }
    @Override
    public void setHeader(String name, String value) {
        this.httpResponse.headers().remove(name);
        this.httpResponse.headers().set(name, value);
    }
    @Override
    public void addHeader(String name, String value) {
        this.httpResponse.headers().set(name, value);
    }
    @Override
    public String getHeader(String name) {
        return this.httpResponse.headers().get(name);
    }
    @Override
    public Collection<String> getHeaders(String name) {
        return Collections.unmodifiableCollection(this.httpResponse.headers().getAll(name));
    }
    @Override
    public Collection<String> getHeaderNames() {
        return Collections.unmodifiableCollection(this.httpResponse.headers().names());
    }
}