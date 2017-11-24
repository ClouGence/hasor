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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.utils.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
/**
 * Http Netty 请求处理器
 * @version : 2017年11月22日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpCoder extends ChannelDuplexHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private WorkStatus            workStatus;
    private RsfContext            rsfContext;
    private HttpHandler           httpHandler;
    private Connector             connector;
    //
    private RsfHttpRequestObject  httpRequest;
    private RsfHttpResponseObject httpResponse;
    //
    public HttpCoder(RsfContext rsfContext, Connector connector, HttpHandler httpHandler) {
        this.rsfContext = rsfContext;
        this.connector = connector;
        this.httpHandler = httpHandler;
        this.workStatus = WorkStatus.Idle;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            readData(ctx, msg);
        } catch (Throwable e) {
            this.exceptionCaught(ctx, e);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        int errorCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        String errorMessage = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase();
        if (e instanceof RsfException) {
            errorCode = ((RsfException) e).getStatus();
            errorMessage = e.getMessage();
        }
        //
        HttpVersion version = (this.httpRequest != null) ? this.httpRequest.getNettyRequest().protocolVersion() : HttpVersion.HTTP_1_1;
        HttpResponseStatus status = HttpResponseStatus.parseLine(errorCode + " " + errorMessage);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(version, status);
        //
        if ("debug".equalsIgnoreCase(this.rsfContext.getEnvironment().getWorkMode())) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            httpResponse.content().writeCharSequence(sw.toString(), Charset.forName("UTF-8"));
        }
        //
        ctx.writeAndFlush(httpResponse).channel().close();
    }
    private void readData(final ChannelHandlerContext ctx, Object msg) throws Throwable {
        // .请求头
        if (msg instanceof HttpRequest) {
            HttpVersion httpVersion = ((HttpRequest) msg).protocolVersion();
            HttpMethod httpMethod = ((HttpRequest) msg).method();
            String requestURI = ((HttpRequest) msg).uri();
            InetSocketAddress remoteSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            InterAddress remoteAddress = new InterAddress("socket", remoteSocket.getAddress().getHostAddress(), remoteSocket.getPort(), "unknown");
            InterAddress local = this.connector.getPublishAddress();
            //
            this.httpRequest = new RsfHttpRequestObject(remoteAddress, local, new DefaultFullHttpRequest(httpVersion, httpMethod, requestURI));
            this.httpResponse = new RsfHttpResponseObject(this.httpRequest);
            this.workStatus = WorkStatus.ReceiveRequest;
            this.httpRequest.getNettyRequest().headers().set(((HttpRequest) msg).headers());
            return;
        }
        // .请求数据(最后一个)
        if (msg instanceof LastHttpContent) {
            ByteBuf content = ((LastHttpContent) msg).content();
            this.httpRequest.getNettyRequest().content().writeBytes(content);
            //
            RequestInfo requestInfo = this.httpHandler.parseRequest(this.httpRequest, this.httpResponse);
            if (requestInfo != null) {
                // 启动一个定时任务，防止任务执行时间过长导致资源无法释放。
                this.rsfContext.getEnvironment().atTime(new TimerTask() {
                    @Override
                    public void run(Timeout timeout) throws Exception {
                        if (ctx.channel().isActive()) {
                            exceptionCaught(ctx, new RsfException(ProtocolStatus.Timeout, "request timeout."));
                        }
                    }
                }, this.rsfContext.getEnvironment().getSettings().getRequestTimeout());
                this.httpRequest.setRsfRequest(requestInfo);
                ctx.fireChannelRead(requestInfo);
            } else {
                ResponseInfo info = ProtocolUtils.buildResponseStatus(//
                        this.rsfContext.getEnvironment(), 0, ProtocolStatus.ProtocolError, "request has no invoker.");
                this.write(ctx, info, null);
            }
            return;
        }
        // 请求数据
        if (msg instanceof HttpContent) {
            HttpContent http = (HttpContent) msg;
            ByteBuf content = http.content();
            this.httpRequest.getNettyRequest().content().writeBytes(content);
            return;
        }
        //
        super.channelRead(ctx, msg);
    }
    //
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ResponseInfo) {
            ResponseInfo response = (ResponseInfo) msg;
            if (response.getStatus() == ProtocolStatus.Accept) {
                return; //ACK 确认包忽略不计
            }
            //
            this.httpResponse.setRsfResponse(response);
            this.httpHandler.buildResponse(this.httpRequest, this.httpResponse);
            ctx.writeAndFlush(this.httpResponse.getHttpResponse()).channel().close();
            return;
        }
        //
        super.write(ctx, msg, promise);
    }
    //
}