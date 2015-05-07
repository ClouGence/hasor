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
package net.hasor.rsf.center.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
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
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import net.hasor.rsf.manager.TimerManager;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.utils.NetworkUtils;
import org.more.future.BasicFuture;
import org.more.logger.LoggerHelper;
/***
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class HttpClient {
    private final String         centerHost;
    private final int            centerPort;
    private final TimerManager   timerManager;
    private final EventLoopGroup workerGroup;
    //
    public HttpClient(AbstractRsfContext rsfContext) throws UnknownHostException {
        InetAddress address = NetworkUtils.finalBindAddress(rsfContext.getSettings().getCenterAddress());
        this.centerHost = address.getHostAddress();
        this.centerPort = rsfContext.getSettings().getCenterPort();
        this.workerGroup = rsfContext.getWorkLoopGroup();
        this.timerManager = new TimerManager(12000, "CenterClient");
    }
    //
    public BasicFuture<HttpResponse> request(final String requestPath, Map<String, String> reqParams) throws Exception {
        //
        // 初始化Netty
        final Bootstrap b = new Bootstrap();
        final BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>();
        b.group(this.workerGroup);
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
        final ChannelFuture f = b.connect(this.centerHost, this.centerPort).sync();
        //
        // 构建http请求
        URL reqPath = new URL("http", this.centerHost, this.centerPort, requestPath);
        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, reqPath.toString());
        request.headers().set(HttpHeaders.Names.HOST, this.centerHost);
        request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        request.headers().set("RSF", "RSF");
        //
        // 发送http请求
        LoggerHelper.logInfo("center request ->" + requestPath);
        HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk if MINSIZE exceed
        HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, false); // false => not multipart
        if (reqParams != null && !reqParams.isEmpty()) {
            for (String key : reqParams.keySet()) {
                String reqKey = key;
                String reqVal = reqParams.get(key);
                bodyRequestEncoder.addBodyAttribute(reqKey, reqVal);
            }
        }
        request = bodyRequestEncoder.finalizeRequest();
        f.channel().write(request);
        f.channel().flush();
        //
        timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                if (!future.isDone()) {
                    LoggerHelper.logSevere("center '" + requestPath + "' response timeout.");
                    future.cancel();
                }
                f.channel().close().sync();
            }
        });
        //
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
            LoggerHelper.logInfo("center response status ->" + response.getStatus().code());
            ctx.close().sync();
        }
    }
}