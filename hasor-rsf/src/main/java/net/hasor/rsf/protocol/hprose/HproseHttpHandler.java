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
package net.hasor.rsf.protocol.hprose;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.libs.com.hprose.io.HproseWriter;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.rpc.net.http.HttpHandler;
import net.hasor.rsf.rpc.net.http.HttpHandlerFactory;
import net.hasor.rsf.rpc.net.http.RsfHttpRequest;
import net.hasor.rsf.rpc.net.http.RsfHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
/**
 * Http Netty 请求处理器
 * @version : 2017年11月23日
 * @author 赵永春(zyc@hasor.net)
 */
public class HproseHttpHandler implements HttpHandler, HttpHandlerFactory {
    @Override
    public HttpHandler newHandler(String contextPath, Connector connector, AppContext appContext) {
        return new HproseHttpHandler(connector, appContext);
    }
    public HproseHttpHandler() {
    }
    private Connector  connector;
    private RsfContext rsfContext;
    public HproseHttpHandler(Connector connector, AppContext appContext) {
        this.connector = connector;
        this.rsfContext = appContext.getInstance(RsfContext.class);
    }
    //
    @Override
    public void doRequest(RsfHttpRequest httpRequest, RsfHttpResponse httpResponse) throws IOException {
        // .设置跨域
        this.httpOrigin(httpRequest, httpResponse);
        // .基础数据
        String requestURI = httpRequest.getRequestURI();
        String origin = httpRequest.getHeader(ORIGIN);
        InputStream inputStream = httpRequest.getInputStream();
        //
        int aByte = inputStream.read();
        if ((char) aByte == 'z') {
            // .函数列表
            String[] nameArrays = HproseUtils.doFunction(this.rsfContext);
            OutputStream outputStream = httpResponse.getOutputStream();
            outputStream.write(new byte[] { 'F' });
            new HproseWriter(outputStream).writeArray(nameArrays);
            outputStream.write(new byte[] { 'z' });
            //
            httpResponse.flushBuffer();
            return;
            //
        } else if ((char) aByte == 'C') {
            // .请求
            RequestInfo[] info = HproseUtils.doCall(this.rsfContext, inputStream, requestURI, origin);
            if (info.length == 0) {
                throw new RsfException(ProtocolStatus.ProtocolError, "undefined calls.");
            }
            if (info.length == 1) {
                httpRequest.setInvokerInfo(info[0]);
                return;
            } else {
                throw new RsfException(ProtocolStatus.ProtocolError, "not support multiple calls.");
            }
        }
        throw new RsfException(ProtocolStatus.ProtocolError, "command error. -> " + aByte);
    }
    @Override
    public void encodResponse(ResponseInfo info, RsfHttpRequest httpRequest, RsfHttpResponse httpResponse) throws IOException {
        httpOrigin(httpRequest, httpResponse);
        if (info != null) {
            HproseUtils.parseResponse(-1, info, httpResponse.getOutputStream());
        }
    }
    protected void httpOrigin(RsfHttpRequest httpRequest, RsfHttpResponse httpResponse) {
        httpResponse.setContentType("application/hprose");
        String origin = httpRequest.getHeader(ORIGIN);
        if (origin != null && !origin.equals("null")) {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        } else {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
    }
}