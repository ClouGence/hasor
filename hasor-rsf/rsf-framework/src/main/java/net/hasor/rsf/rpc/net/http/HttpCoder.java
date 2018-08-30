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
import io.netty.util.ReferenceCounted;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.Connector;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Http Netty 请求处理器
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HttpCoder extends ChannelDuplexHandler {
    protected Logger                      logger = LoggerFactory.getLogger(getClass());
    private   WorkStatus                  workStatus;
    private   RsfContext                  rsfContext;
    private   HttpHandler                 httpHandler;
    private   Connector                   connector;
    private   RsfHttpRequestObject        httpRequest;
    private   RsfHttpResponseObject       httpResponse;
    private   HttpHandler.ResponseEncoder encoder;
    //
    public HttpCoder(RsfContext rsfContext, Connector connector, HttpHandler httpHandler) {
        this.rsfContext = rsfContext;
        this.connector = connector;
        this.httpHandler = httpHandler;
        this.workStatus = WorkStatus.Idle;
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (this.httpRequest != null)
            this.httpRequest.release();
        if (this.httpResponse != null)
            this.httpResponse.release();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ReferenceCounted referenceCounted = null;
        try {
            if (msg instanceof ReferenceCounted) {
                referenceCounted = (ReferenceCounted) msg;
            }
            readData(ctx, msg);
        } catch (Throwable e) {
            this.exceptionCaught(ctx, e);
        } finally {
            IOUtils.releaseByteBuf(referenceCounted);
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
        FullHttpResponse httpResponse = null;
        if (this.httpResponse == null) {
            HttpVersion version = (this.httpRequest != null) ? this.httpRequest.getNettyRequest().protocolVersion() : HttpVersion.HTTP_1_1;
            HttpResponseStatus status = HttpResponseStatus.parseLine(errorCode + " " + errorMessage);
            new DefaultFullHttpResponse(version, status);
            this.httpResponse = new RsfHttpResponseObject(version, status);
        } else {
            this.httpResponse.sendError(errorCode, errorMessage);
        }
        //
        if (this.encoder != null) {
            try {
                this.encoder.exception(this.httpResponse, e);
            } catch (Exception ee) {
                logger.error(ee.getMessage(), ee);
            }
        }
        //
        httpResponse = this.httpResponse.getHttpResponse();
        ctx.writeAndFlush(httpResponse).channel().close().sync();
    }
    private void readData(final ChannelHandlerContext ctx, Object msg) throws Throwable {
        // .请求头
        if (msg instanceof HttpRequest) {
            HttpVersion httpVersion = ((HttpRequest) msg).protocolVersion();
            HttpMethod httpMethod = ((HttpRequest) msg).method();
            String requestURI = ((HttpRequest) msg).uri();
            InetSocketAddress remoteSocket = (InetSocketAddress) ctx.channel().remoteAddress();
            InterAddress remoteAddress = new InterAddress("socket", remoteSocket.getAddress().getHostAddress(), remoteSocket.getPort(), "unknown");
            InterAddress local = this.connector.getBindAddress();
            //
            this.httpRequest = new RsfHttpRequestObject(remoteAddress, local, new DefaultFullHttpRequest(httpVersion, httpMethod, requestURI));
            this.httpResponse = new RsfHttpResponseObject(this.httpRequest);
            this.workStatus = WorkStatus.ReceiveRequest;
            this.httpRequest.getNettyRequest().headers().set(((HttpRequest) msg).headers());
            //
            return;
        }
        // .请求数据(最后一个)
        if (msg instanceof LastHttpContent) {
            ByteBuf content = ((LastHttpContent) msg).content();
            this.httpRequest.getNettyRequest().content().writeBytes(content);
            if (HttpMethod.POST.equals(this.httpRequest.getNettyRequest().method())) {
                this.httpRequest.loadPostRequestBody();
            }
            //
            doInvoker(ctx);
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
    //
    private void doInvoker(final ChannelHandlerContext ctx) throws Throwable {
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        HttpHandler.HttpResult httpResult = new HttpHandler.HttpResult() {
            @Override
            public void callRPC(RequestInfo requestInfo, HttpHandler.ResponseEncoder encoder) {
                Objects.requireNonNull(requestInfo);
                Objects.requireNonNull(encoder);
                if (atomicBoolean.get()) {
                    throw new IllegalStateException("callRPC and finishRPC , have only one of to use");
                }
                httpRequest.setRsfRequest(requestInfo);
                HttpCoder.this.encoder = encoder;
                atomicBoolean.set(true);
            }
            @Override
            public void finishRPC() {
                if (atomicBoolean.get()) {
                    throw new IllegalStateException("callRPC and finishRPC , have only one of to use");
                }
                atomicBoolean.set(true);
            }
        };
        this.httpHandler.receivedRequest(this.httpRequest, this.httpResponse, httpResult);
        if (!atomicBoolean.get()) {
            if (this.httpResponse.getStatus() == 0) {
                this.httpResponse.sendError(ProtocolStatus.InvokeError, "the server didn't respond");
            }
            this.write(ctx, this.httpResponse.getHttpResponse(), null);
            return;
        }
        //
        // .引发fireChannelRead或者响应response
        // .已经做出 response 回应，不需要在处理RequestInfo。
        if (this.httpResponse.isCommitted()) {
            this.write(ctx, this.httpResponse.getHttpResponse(), null);
            return;
        }
        // .需要解析 Request，启动一个定时任务，防止任务执行时间过长导致资源无法释放。
        RequestInfo rsfRequest = this.httpRequest.getRsfRequest();
        if (rsfRequest != null) {
            this.rsfContext.getEnvironment().atTime(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    if (ctx.channel().isActive()) {
                        exceptionCaught(ctx, new RsfException(ProtocolStatus.Timeout, "request timeout."));
                    }
                }
            }, this.rsfContext.getEnvironment().getSettings().getRequestTimeout());
            ctx.fireChannelRead(rsfRequest);
            return;
        }
        //
        // .没有解析到 request，直接响应结束
        ResponseInfo info = ProtocolUtils.buildResponseStatus(//
                this.rsfContext.getEnvironment(), 0, ProtocolStatus.ProtocolError, "request has no invoker.");
        this.write(ctx, info, null);
    }
    //
    //
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ResponseInfo) {
            ResponseInfo response = (ResponseInfo) msg;
            if (response.getStatus() == ProtocolStatus.Accept) {
                return; //ACK 确认包忽略不计
            }
            //
            if (this.encoder != null) {
                this.encoder.complete(this.httpResponse, response);
            }
            msg = this.httpResponse.getHttpResponse();
        }
        //
        if (msg instanceof FullHttpResponse) {
            ctx.writeAndFlush(msg).sync().channel().close().sync();
            return;
        }
        super.write(ctx, msg, promise);
    }
}