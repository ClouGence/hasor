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
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.utils.ProtocolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
/**
 * Hprose
 * @version : 2017年1月26日
 * @author 赵永春(zyc@hasor.net)
 */
//@ChannelHandler.Sharable
public class HproseHttpCoder extends ChannelDuplexHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RsfContext rsfContext;
    private String     requestURI;
    private String     origin;
    //
    public HproseHttpCoder(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ResponseInfo) {
            ResponseInfo response = (ResponseInfo) msg;
            if (response.getStatus() == ProtocolStatus.Accept) {
                return;
            }
            //
            FullHttpResponse httpResponse = newResponse(response);
            super.write(ctx, httpResponse, promise);
            return;
        }
        super.write(ctx, msg, promise);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof HttpContent) {
                HttpContent http = (HttpContent) msg;
                ByteBuf content = http.content();
                byte aByte = content.readByte();
                if ((char) aByte == 'z') {
                    // .函数列表
                    FullHttpResponse response = this.newResponse(HproseUtils.doFunction(this.rsfContext), this.origin);
                    ctx.writeAndFlush(response);
                } else if ((char) aByte == 'C') {
                    // .请求
                    RequestInfo[] info = HproseUtils.doCall(this.rsfContext, content, this.requestURI, this.origin);
                    if (info != null && info.length > 0)
                        super.channelRead(ctx, info[0]);
                }
                //
                this.requestURI = null;
                this.origin = null;
                return;
            }
            if (msg instanceof HttpRequest) {
                HttpHeaders headers = ((HttpRequest) msg).headers();
                this.requestURI = ((HttpRequest) msg).uri();
                this.origin = headers.get(ORIGIN);
            }
            //
            super.channelRead(ctx, msg);
        } catch (Exception e) {
            short errorCode = ProtocolStatus.Unknown;
            String errorMessage = e.getMessage();
            if (e instanceof RsfException) {
                errorCode = ((RsfException) e).getStatus();
                errorMessage = e.getMessage();
            }
            ResponseInfo info = ProtocolUtils.buildResponseStatus(rsfContext.getEnvironment(), 0, errorCode, errorMessage);
            FullHttpResponse fullHttpResponse = this.newResponse(info);
            ctx.writeAndFlush(fullHttpResponse);
        }
    }
    //
    //
    private FullHttpResponse newResponse(ResponseInfo response) {
        long requestID = response.getRequestID();
        ByteBuf result = HproseUtils.doResult(requestID, response);
        return newResponse(result, response.getOption(ORIGIN));
    }
    private FullHttpResponse newResponse(ByteBuf result, String origin) {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, result);
        httpResponse.headers().set(CONTENT_TYPE, "application/hprose");
        httpResponse.headers().set(CONTENT_LENGTH, result.readableBytes());
        //
        if (origin != null && !origin.equals("null")) {
            httpResponse.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, origin);
            httpResponse.headers().set(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        } else {
            httpResponse.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }
        return httpResponse;
    }
}