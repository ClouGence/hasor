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
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.*;
import net.hasor.rsf.rpc.net.LinkType;
import net.hasor.rsf.rpc.net.RsfChannel;
import net.hasor.rsf.rpc.net.SendCallBack;
import net.hasor.rsf.rpc.net.http.HttpHandler.ResponseDecoder;
import net.hasor.rsf.rpc.net.http.HttpHandler.SenderBuilder;
import net.hasor.rsf.utils.ProtocolUtils;
import net.hasor.utils.Objects;
import net.hasor.utils.future.BasicFuture;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
/**
 * 封装Http网络连接，还负责向外发起远程调用。
 * @version : 2017年11月22日
 * @author 赵永春 (zyc@hasor.net)
 */
class RsfChannelOnHttp extends RsfChannel {
    private HttpConnector httpConnector;
    public RsfChannelOnHttp(InterAddress target, LinkType linkType, HttpConnector httpConnector) {
        super(target, linkType);
        this.httpConnector = httpConnector;
    }
    @Override
    public boolean isActive() {
        return true;
    }
    @Override
    protected void closeChannel() {
        //http本身就是请求一次自动关闭，因此不需要 close 任何东西。
    }
    //
    protected void sendData(OptionInfo sendData, final SendCallBack sendCallBack) {
        final long requestID = (sendData instanceof RequestInfo) ? ((RequestInfo) sendData).getRequestID() ://
                (sendData instanceof ResponseInfo) ? ((ResponseInfo) sendData).getRequestID() : 0;
        //
        sendData(requestID, sendData, new SendCallBack() {
            @Override
            public void failed(long requestID, Throwable e) {
                short status = ProtocolStatus.InvokeError;
                if (e instanceof RsfException) {
                    status = ((RsfException) e).getStatus();
                }
                ResponseInfo responseInfo = ProtocolUtils.buildResponseStatus(//
                        null, requestID, status, e.getMessage());
                httpConnector.receivedData(RsfChannelOnHttp.this, responseInfo);
                sendCallBack.failed(requestID, e);
            }
            @Override
            public void complete(long requestID) {
                sendCallBack.complete(requestID);
            }
        });
    }
    private void sendData(long requestID, OptionInfo sendData, final SendCallBack sendCallBack) {
        if (!(sendData instanceof RequestInfo)) {
            sendCallBack.failed(requestID, new RsfException(ProtocolStatus.InvokeError, "only support RequestInfo."));
            return;
        }
        // .通用数据
        RequestInfo requestInfo = (RequestInfo) sendData;
        URI uri = null;
        try {
            String remoteHost = this.getTarget().getHost();
            int remotePort = this.getTarget().getPort();
            String group = URLEncoder.encode(requestInfo.getServiceGroup(), "UTF-8");
            String name = URLEncoder.encode(requestInfo.getServiceName(), "UTF-8");
            String version = URLEncoder.encode(requestInfo.getServiceVersion(), "UTF-8");
            String pathInfo = String.format("group=%s&name=%s&version=%s", group, name, version);
            uri = new URL("http", remoteHost, remotePort, pathInfo).toURI();
        } catch (Exception e) {
            sendCallBack.failed(requestID, e);
            return;
        }
        //
        // ---------------------------------------------------------------------------------------- 本地部分
        // 构建http请求
        final FullHttpRequest nettyRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
        final ArrayList<Object> objectToUse = new ArrayList<Object>();
        try {
            SenderBuilder senderBuilder = new SenderBuilder() {
                @Override
                public void sendRequest(RequestObject httpRequest, ResponseDecoder decoder) {
                    Objects.requireNonNull(httpRequest);
                    Objects.requireNonNull(decoder);
                    if (!objectToUse.isEmpty()) {
                        throw new IllegalStateException("sendRequest and finishRequest, have only one of to use");
                    }
                    objectToUse.add(httpRequest);
                    objectToUse.add(decoder);
                }
                @Override
                public void finishRequest(ResponseInfo responseInfo) {
                    Objects.requireNonNull(responseInfo);
                    if (!objectToUse.isEmpty()) {
                        throw new IllegalStateException("sendRequest and finishRequest, have only one of to use");
                    }
                    objectToUse.add(responseInfo);
                }
            };
            this.httpConnector.getHttpHandler().sendRequest(this.getTarget(), requestInfo, senderBuilder);
        } catch (Throwable e) {
            sendCallBack.failed(requestID, e);
            return;
        }
        //
        // ---------------------------------------------------------------------------------------- 不需要发送远程请求的情况
        if (objectToUse.isEmpty()) {
            sendCallBack.failed(requestID, new RsfException(ProtocolStatus.InvokeError, "the server didn't respond"));
            return;
        }
        Object dat = objectToUse.get(0);
        try {
            if (dat instanceof ResponseInfo && objectToUse.size() == 1) {
                sendCallBack.complete(requestID);
                this.httpConnector.receivedData(this, (ResponseInfo) dat);
                return;
            }
        } catch (Exception e) {
            sendCallBack.failed(requestID, e);
            return;
        }
        //
        // ---------------------------------------------------------------------------------------- 网络部分
        Channel channel = null;
        final BasicFuture<RsfHttpResponseObject> responseFuture = new BasicFuture<>();
        ResponseDecoder requestEncoder = (ResponseDecoder) objectToUse.get(1);
        try {
            // .准备请求数据
            RequestObject requestObject = (RequestObject) dat;
            nettyRequest.headers().add(requestObject.headers());
            nettyRequest.setMethod(requestObject.method());
            nettyRequest.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            byte[] byteData = requestObject.getBodyData();
            if (byteData != null && byteData.length != 0) {
                nettyRequest.content().writeBytes(byteData);
            }
            nettyRequest.headers().set(HttpHeaders.Names.CONTENT_LENGTH, nettyRequest.content().readableBytes());
            //
            // .请求地址
            URL requestURL = requestObject.requestFullPath();
            if (!"http".equalsIgnoreCase(requestURL.getProtocol())) {
                sendCallBack.failed(requestID, new RsfException(ProtocolStatus.ProtocolError, "only support HTTP."));
                return;
            }
            String remoteHost = requestURL.getHost();
            int remotePort = (requestURL.getPort() <= 0) ? 80 : requestURL.getPort();
            nettyRequest.setUri("http://" + remoteHost + ":" + remotePort + "/" + requestURL.getFile());
            nettyRequest.headers().set(HttpHeaders.Names.HOST, remoteHost);
            //
            // .启动客户端发送数据包
            Bootstrap b = new Bootstrap();
            b.group(this.httpConnector.getWorkerGroup());
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new RsfChannelOnHttpClientHandler(responseFuture));
                }
            });
            channel = b.connect(remoteHost, remotePort).sync().channel();
            //
            // .向外发起远程调用
            channel.writeAndFlush(nettyRequest).sync();
        } catch (Exception e) {
            sendCallBack.failed(requestID, e);
            closeSocket(channel);
            return;
        }
        //
        // .发起一个Timeout 任务，避免远程服务器一直阻塞响应
        this.httpConnector.getRsfEnvironment().atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (!responseFuture.isDone()) {
                    responseFuture.failed(new RsfException(ProtocolStatus.Timeout, "client send request failed, request is timeout."));
                }
            }
        }, requestInfo.getClientTimeout());
        //
        //
        try {
            RsfHttpResponseObject responseObject = responseFuture.get();
            if (!requestInfo.isMessage()) {
                ResponseInfo responseInfo = requestEncoder.complete(requestID, responseObject);
                responseObject.release();
                sendCallBack.complete(requestID);
                this.httpConnector.receivedData(this, responseInfo);
            } else {
                ResponseInfo responseInfo = ProtocolUtils.buildResponseStatus(//
                        null, requestID, ProtocolStatus.Accept, "ok.");
                this.httpConnector.receivedData(this, responseInfo);
                sendCallBack.complete(requestID);
            }
        } catch (Exception e) {
            sendCallBack.failed(requestID, e);
        } finally {
            closeSocket(channel);
        }
        return;
    }
    //
    void closeSocket(Channel channel) {
        if (channel == null) {
            return;
        }
        try {
            channel.close().sync();
        } catch (Exception e1) {
            /*吃掉这个异常*/
        }
    }
}