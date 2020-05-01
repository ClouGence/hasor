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
package net.hasor.rsf.protocol.http_hprose;
import hprose.io.HproseWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.rpc.net.http.*;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.ProtocolUtils;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Http Netty 请求处理器
 * @version : 2017年11月23日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HproseHttpHandler implements HttpHandler, HttpHandlerFactory {
    public HproseHttpHandler() {
    }

    @Override
    public HttpHandler newHandler(String contextPath, Connector connector, AppContext appContext) {
        return new HproseHttpHandler(contextPath, connector, appContext);
    }

    private String     contextPath;
    private Connector  connector;
    private RsfContext rsfContext;

    public HproseHttpHandler(String contextPath, Connector connector, AppContext appContext) {
        this.contextPath = contextPath;
        this.connector = connector;
        this.rsfContext = appContext.getInstance(RsfContext.class);
    }

    @Override
    public void receivedRequest(RsfHttpRequest httpRequest, RsfHttpResponse httpResponse, HttpResult outputTo) throws IOException {
        String requestURI = httpRequest.getRequestURI();
        if (!requestURI.startsWith(this.contextPath)) {
            httpResponse.sendError(404, "not found service.");
            return;
        }
        //
        // .设置跨域
        final String originString = httpRequest.getHeader(ORIGIN);
        this.httpOrigin(originString, httpResponse);
        // .基础数据
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
            httpResponse.flushBuffer();
            outputTo.finishRPC();
            return;
        } else if ((char) aByte == 'C') {
            // .请求
            RequestInfo[] info = HproseUtils.doCall(this.rsfContext, inputStream, requestURI, origin);
            if (info.length == 0) {
                throw new RsfException(ProtocolStatus.ProtocolError, "undefined calls.");
            }
            if (info.length != 1) {
                throw new RsfException(ProtocolStatus.ProtocolError, "not support multiple calls.");
            }
            //
            ResponseEncoder responseEncoder = new ResponseEncoder() {
                public void exception(RsfHttpResponse httpResponse, Throwable e) throws IOException {
                    onException(originString, httpResponse, e);
                }

                public void complete(RsfHttpResponse httpResponse, ResponseInfo info) throws IOException {
                    onComplete(originString, httpResponse, info);
                }
            };
            outputTo.callRPC(info[0], responseEncoder);
            return;
        }
        throw new RsfException(ProtocolStatus.ProtocolError, "command error. -> " + aByte);
    }

    private void onException(String originString, RsfHttpResponse httpResponse, Throwable e) throws IOException {
        httpOrigin(originString, httpResponse);
        //
        //        if ("debug".equalsIgnoreCase(this.rsfContext.getTarget().getWorkMode())) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        new OutputStreamWriter(httpResponse.getOutputStream(), "UTF-8").write(sw.toString());
        //        }
    }

    private void onComplete(String originString, RsfHttpResponse httpResponse, ResponseInfo info) throws IOException {
        httpOrigin(originString, httpResponse);
        if (info != null) {
            HproseUtils.parseResponse(-1, info, httpResponse.getOutputStream());
        }
    }

    protected void httpOrigin(String originString, RsfHttpResponse httpResponse) {
        httpResponse.setContentType("application/hprose");
        if (originString != null && !originString.equals("null")) {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, originString);
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        } else {
            httpResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
    }

    @Override
    public void sendRequest(InterAddress server, RequestInfo info, SenderBuilder builder) throws Throwable {
        String group = URLEncoder.encode(info.getServiceGroup(), "UTF-8");
        String name = URLEncoder.encode(info.getServiceName(), "UTF-8");
        String version = URLEncoder.encode(info.getServiceVersion(), "UTF-8");
        String pathInfo = ("/" + group + "/" + name + "/" + version).replaceAll("/{2,}", "/");
        URL requestURL = new URL("http", server.getHost(), server.getPort(), pathInfo);
        //
        ByteBuf byteBuf = HproseUtils.encodeRequest(this.rsfContext, info);
        InputStream inputStream = new ByteBufInputStream(byteBuf);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(inputStream, out);
        //
        RequestBuilder requestBuilder = RequestBuilder.newBuild("GET", requestURL);
        requestBuilder.setContentData(out.toByteArray());
        for (String optKey : info.getOptionKeys()) {
            requestBuilder.addHeader(optKey, info.getOption(optKey));
        }
        //
        RequestObject httpObject = requestBuilder.buildObject();
        builder.sendRequest(httpObject, this::decodeResponseInfo);
    }

    private ResponseInfo decodeResponseInfo(long requestID, RsfHttpResponseData httpResponse) throws IOException {
        short responseStatus = (short) httpResponse.getStatus();
        if (httpResponse.getStatus() != ProtocolStatus.OK) {
            String errorInfo = httpResponse.getStatusMessage();
            return ProtocolUtils.buildResponseStatus(null, requestID, responseStatus, errorInfo);
        }
        Object result = HproseUtils.decodeResponse(httpResponse.getInputStream());
        //
        ResponseInfo responseInfo = new ResponseInfo();
        Collection<String> headerNames = httpResponse.getHeaderNames();
        for (String optKey : headerNames) {
            responseInfo.addOption(optKey, httpResponse.getHeader(optKey));
        }
        responseInfo.setReceiveTime(System.currentTimeMillis());
        responseInfo.setRequestID(requestID);
        responseInfo.setStatus(responseStatus);
        responseInfo.setSerializeType("Hprose");
        responseInfo.setReturnType("");
        responseInfo.setReturnData(result);
        //
        return responseInfo;
    }
}