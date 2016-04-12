/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.center.server.launcher.http;
import java.net.URL;
import java.util.Map;
import org.more.future.BasicFuture;
import org.more.util.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
/***
 * 基于Netty的简易HttpClient
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpClient {
    private final String         remoteHost;
    private final int            remotePort;
    private final EventLoopGroup worker;
    //
    public HttpClient(URL remoteHost, EventLoopGroup worker) {
        this.remoteHost = remoteHost.getHost();
        this.remotePort = remoteHost.getPort();
        this.worker = worker;
    }
    //
    public BasicFuture<HttpResponse> request(String requestPath, Map<String, String> reqParams, String body) throws Exception {
        if (StringUtils.isBlank(body)) {
            return this.request(requestPath, reqParams, new byte[0]);
        } else {
            return this.request(requestPath, reqParams, body.getBytes("UTF-8"));
        }
    }
    public BasicFuture<HttpResponse> request(String requestPath, Map<String, String> reqParams, byte[] body) throws Exception {
        // 初始化Netty
        final Bootstrap b = new Bootstrap();
        final BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>();
        b.group(this.worker);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                ch.pipeline().addLast(new HttpResponseDecoder());
                // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new ResponseRead(future));
            }
        });
        //
        // 连接Server
        ChannelFuture f = b.connect(this.remoteHost, this.remotePort).sync();
        //
        // 构建http请求
        URL reqPath = new URL("http", this.remoteHost, this.remotePort, requestPath);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, reqPath.toString());
        request.headers().set(HttpHeaders.Names.HOST, this.remoteHost);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
        //
        // 发送http请求
        if (body != null) {
            request.content().writeBytes(body);
        }
        f.channel().write(request);
        f.channel().flush();
        //
        // 返回异步对象
        f.channel().closeFuture().addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture nettyFuture) throws Exception {
                future.cancel();//因连接关闭而取消
            }
        });
        return future;
    }
}
class ResponseRead extends ChannelInboundHandlerAdapter {
    private BasicFuture<HttpResponse> future;
    public ResponseRead(BasicFuture<HttpResponse> future) {
        this.future = future;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            future.completed(response);
        }
    }
}