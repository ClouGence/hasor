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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import net.hasor.utils.future.BasicFuture;
/**
 * Http Netty 响应处理器
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
class RsfChannelOnHttpClientHandler extends ChannelInboundHandlerAdapter {
    private RsfHttpResponseObject              httpResponse;
    private BasicFuture<RsfHttpResponseObject> responseFuture;
    public RsfChannelOnHttpClientHandler(BasicFuture<RsfHttpResponseObject> responseFuture) {
        this.responseFuture = responseFuture;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // .请求头
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            this.httpResponse = new RsfHttpResponseObject(response.protocolVersion(), response.status());
            return;
        }
        // .请求数据(最后一个)
        if (msg instanceof LastHttpContent) {
            LastHttpContent http = (LastHttpContent) msg;
            ByteBuf content = http.content();
            this.httpResponse.getHttpResponse().content().writeBytes(content);
            this.responseFuture.completed(this.httpResponse);
            return;
        }
        // 请求数据
        if (msg instanceof HttpContent) {
            HttpContent http = (HttpContent) msg;
            ByteBuf content = http.content();
            this.httpResponse.getHttpResponse().content().writeBytes(content);
            return;
        }
        //
        super.channelRead(ctx, msg);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.responseFuture.failed(cause);
    }
}