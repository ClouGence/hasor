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
package net.hasor.rsf.rpc.net;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
/**
 * RPC协议连接器，负责创建某个特定RPC协议的网络事件。
 * tips：传入的网络连接，交给{@link LinkPool}进行处理，{@link Connector}本身不维护任何连接。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
@ChannelHandler.Sharable
public class Connector extends ChannelInboundHandlerAdapter implements ReceivedListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final String           protocol;
    private final AppContext       appContext;
    private final InterAddress     bindAddress;
    private final InterAddress     gatewayAddress;
    private       RsfChannel       localListener;
    private final LinkPool         linkPool;
    private final ReceivedListener receivedListener;
    private final EventLoopGroup   workLoopGroup;
    private final ProtocolHandler  handler;
    public Connector(AppContext appContext, String protocol, InterAddress local, InterAddress gateway,//
            final ReceivedListener receivedListener, LinkPool linkPool, EventLoopGroup workLoopGroup) throws ClassNotFoundException {
        //
        this.protocol = protocol;
        this.appContext = appContext;
        this.bindAddress = local;
        this.gatewayAddress = gateway;
        this.receivedListener = receivedListener;
        this.linkPool = linkPool;
        this.workLoopGroup = workLoopGroup;
        //
        RsfEnvironment env = this.appContext.getInstance(RsfEnvironment.class);
        Map<String, String> protocolHandlerMapping = env.getSettings().getProtocolHandlerMapping();
        String handlerName = protocolHandlerMapping.get(this.protocol);
        Class<?> handlerType = appContext.getClassLoader().loadClass(handlerName);
        this.handler = (ProtocolHandler) appContext.getInstance(handlerType);
    }
    @Override
    public String toString() {
        InterAddress local = this.gatewayAddress;
        local = (local == null) ? this.bindAddress : local;
        return "Connector{ protocol='" + protocol + "', bindAddress=" + local + '}';
    }
    //
    //
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.exceptionCaught(ctx, null);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String hostPort = converToHostProt(ctx);
        if (cause == null) {
            this.logger.warn("close socket=" + hostPort + " channel Inactive.");
        } else {
            this.logger.error("close socket=" + hostPort + " with error -> " + cause.getMessage(), cause);
        }
        this.linkPool.closeConnection(hostPort);
        ctx.close();
    }
    /** 接收解析好的 RequestInfo、ResponseInfo 对象，并将它们转发到 {@link RsfChannel}接收事件中。 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof OptionInfo) {
            String hostPort = converToHostProt(ctx);
            BasicFuture<RsfChannel> channel = this.linkPool.findChannel(hostPort);
            if (channel == null || !channel.isDone()) {
                this.exceptionCaught(ctx, new RsfException(ProtocolStatus.NetworkError, "the " + hostPort + " connection is not in the pool."));
                return;
            }
            RsfChannel rsfChannel = channel.get();
            if (rsfChannel.getTarget() == null) {
                this.exceptionCaught(ctx, new RsfException(ProtocolStatus.NetworkError, "the " + hostPort + " connection is not management."));
                return;
            }
            //
            rsfChannel.receivedData((OptionInfo) msg);
        }
        super.channelRead(ctx, msg);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if (socketAddress == null) {
            super.channelActive(ctx);
            return;
        }
        String hostAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        String hostPort = hostAddress + ":" + port;
        this.logger.info("connected form {}", hostPort);
        //
        InterAddress target = new InterAddress(this.protocol, hostAddress, port, "unknown");
        BasicFuture<RsfChannel> future = this.linkPool.preConnection(hostPort);
        RsfChannel rsfChannel = null;
        if (future.isDone()) {
            rsfChannel = future.get();
        } else {
            rsfChannel = new RsfChannel(this.protocol, target, ctx.channel(), LinkType.In);
            future.completed(rsfChannel);
        }
        //
        boolean accept = this.handler.acceptIn(this, rsfChannel);
        if (accept) {
            rsfChannel.addListener(this);
        } else {
            this.logger.warn("connection refused form {} ,", hostPort);
            this.linkPool.closeConnection(hostPort);
            ctx.close();
        }
        //
    }
    @Override
    public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) throws IOException {
        if (!rsfChannel.isActive()) {
            return;
        }
        this.receivedListener.receivedMessage(rsfChannel, info);
    }
    //
    //
    /** 监听的本地端口号 */
    public InterAddress getBindAddress() {
        return this.bindAddress;
    }
    /** 如果工作在内网，这里返回配置的外网映射地址 */
    public InterAddress getGatewayAddress() {
        return this.gatewayAddress;
    }
    //
    //
    /** 连接到远程机器 */
    public void connectionTo(final InterAddress hostAddress, final BasicFuture<RsfChannel> result) {
        //
        //
        Bootstrap boot = new Bootstrap();
        boot.group(this.workLoopGroup);
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelHandler[] handlerArrays = channelHandler();
                ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
                handlers.addAll(Arrays.asList(handlerArrays));            // 编码解码器
                handlers.add(Connector.this);                             // 转发RequestInfo、ResponseInfo到RSF
                //
                ch.pipeline().addLast(handlers.toArray(new ChannelHandler[handlers.size()]));
            }
        });
        ChannelFuture future = configBoot(boot).connect(hostAddress.toSocketAddress());
        //
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    logger.error("connect to {} error.", hostAddress, future.cause());
                    result.failed(future.cause());
                } else {
                    Channel channel = future.channel();
                    logger.info("connect to {} Success.", hostAddress);
                    result.completed(new RsfChannel(protocol, bindAddress, channel, LinkType.Out));
                }
            }
        });
    }
    /**
     * 启动本地监听器
     * @param listenLoopGroup 监听器线程组
     */
    public void startListener(NioEventLoopGroup listenLoopGroup) {
        //
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(listenLoopGroup, this.workLoopGroup);
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelHandler[] handlerArrays = channelHandler();
                ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
                handlers.addAll(Arrays.asList(handlerArrays));            // 编码解码器
                handlers.add(Connector.this);                             // 转发RequestInfo、ResponseInfo到RSF
                //
                ch.pipeline().addLast(handlers.toArray(new ChannelHandler[handlers.size()]));
            }
        });
        boot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = configBoot(boot).bind(this.bindAddress.toSocketAddress());
        //
        final BasicFuture<RsfChannel> result = new BasicFuture<RsfChannel>();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    result.failed(future.cause());
                } else {
                    Channel channel = future.channel();
                    result.completed(new RsfChannel(protocol, bindAddress, channel, LinkType.Listener));
                }
            }
        });
        try {
            this.localListener = result.get();
            logger.info("rsf Server started at {}", this.bindAddress);
        } catch (Exception e) {
            logger.error("rsf start listener error: " + e.getMessage(), e);
            throw new RsfException(ProtocolStatus.NetworkError, this.bindAddress.toString() + " -> " + e.getMessage());
        }
        //
    }
    private <T extends AbstractBootstrap<?, ?>> T configBoot(T boot) {
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        // boot.option(ChannelOption.SO_BACKLOG, 128);
        // boot.option(ChannelOption.SO_BACKLOG, 1024);
        // boot.option(ChannelOption.SO_RCVBUF, 1024 * 256);
        // boot.option(ChannelOption.SO_SNDBUF, 1024 * 256);
        boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return boot;
    }
    /**停止监听器*/
    public void shutdown() {
        this.localListener.close();
    }
    public void mappingTo(RsfChannel rsfChannel, InterAddress interAddress) {
        rsfChannel.inverseMappingTo(interAddress);
        this.linkPool.mappingTo(rsfChannel, interAddress.getHostPort());
    }
    //
    private static String converToHostProt(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress() + ":" + socketAddress.getPort();
    }
    private ChannelHandler[] channelHandler() {
        return this.handler.channelHandler(this, this.appContext);
    }
}