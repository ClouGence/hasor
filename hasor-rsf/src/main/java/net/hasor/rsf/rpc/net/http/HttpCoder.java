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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.utils.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Http Netty 请求处理器
 * @version : 2017年1月26日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpCoder extends ChannelDuplexHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RsfContext             rsfContext;
    private WorkStatus             workStatus;
    private DefaultFullHttpRequest httpRequest;
    //
    public HttpCoder(RsfContext rsfContext, InterAddress publishAddress) {
        this.rsfContext = rsfContext;
        this.workStatus = WorkStatus.Idle;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            readData(ctx, msg);
        } catch (Throwable e) {
            short errorCode = ProtocolStatus.Unknown;
            String errorMessage = e.getMessage();
            if (e instanceof RsfException) {
                errorCode = ((RsfException) e).getStatus();
                errorMessage = e.getMessage();
            }
            ResponseInfo info = ProtocolUtils.buildResponseStatus(rsfContext.getEnvironment(), 0, errorCode, errorMessage);
            FullHttpResponse fullHttpResponse = this.newResponse(info);
            ctx.writeAndFlush(fullHttpResponse).channel().close();
        }
    }
    private void readData(ChannelHandlerContext ctx, Object msg) throws Throwable {
        // .请求头
        if (msg instanceof HttpRequest) {
            HttpVersion httpVersion = ((HttpRequest) msg).protocolVersion();
            HttpMethod httpMethod = ((HttpRequest) msg).method();
            String requestURI = ((HttpRequest) msg).uri();
            this.httpRequest = new DefaultFullHttpRequest(httpVersion, httpMethod, requestURI);
            this.workStatus = WorkStatus.ReceiveRequest;
            this.httpRequest.headers().set(((HttpRequest) msg).headers());
            return;
        }
        // .请求数据(最后一个)
        if (msg instanceof LastHttpContent) {
            ByteBuf content = ((LastHttpContent) msg).content();
            this.httpRequest.content().writeBytes(content);
            content = this.httpRequest.content();
            //
            byte aByte = content.readByte();
            return;
        }
        // 请求数据
        if (msg instanceof HttpContent) {
            HttpContent http = (HttpContent) msg;
            ByteBuf content = http.content();
            this.httpRequest.content().writeBytes(content);
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
            FullHttpResponse httpResponse = newResponse(response);
            super.write(ctx, httpResponse, promise);
            this.workStatus = WorkStatus.Idle;
            return;
        }
        super.write(ctx, msg, promise);
    }
}