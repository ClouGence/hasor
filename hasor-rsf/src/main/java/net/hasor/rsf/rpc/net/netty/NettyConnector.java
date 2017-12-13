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
package net.hasor.rsf.rpc.net.netty;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.rpc.net.*;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * RPC协议连接器，负责创建某个特定RPC协议的网络事件。
 * tips：传入的网络连接，交给{@link LinkPool}进行处理，{@link NettyConnector}本身不维护任何连接。
 * @version : 2017年01月16日
 * @author 赵永春 (zyc@hasor.net)
 */
public class NettyConnector extends Connector {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private RsfChannel             localListener;   // Socket监听器
    private NettyThreadGroup       threadGroup;     // Netty 线程组
    private ProtocolHandlerFactory handlerFactory;  // Netty ChannelHandler 组
    private AppContext             appContext;      // App
    //
    public NettyConnector(String protocol, final AppContext appContext, final ReceivedListener receivedListener, ConnectionAccepter accepter) throws ClassNotFoundException {
        super(protocol, appContext.getInstance(RsfEnvironment.class), receivedListener, accepter);
        this.appContext = appContext;
    }
    /**获取work线程组*/
    public EventLoopGroup getWorkerGroup() {
        if (this.threadGroup == null)
            return null;
        return this.threadGroup.getWorkLoopGroup();
    }
    /**创建 ProtocolHandlerFactory 对象。*/
    protected ProtocolHandlerFactory createHandler(String protocol, AppContext appContext) throws ClassNotFoundException {
        String configKey = getRsfEnvironment().getSettings().getProtocolConfigKey(protocol);
        String nettyHandlerType = getRsfEnvironment().getSettings().getString(configKey + ".nettyHandlerFactory");
        Class<ProtocolHandlerFactory> handlerClass = (Class<ProtocolHandlerFactory>) appContext.getClassLoader().loadClass(nettyHandlerType);
        return appContext.getInstance(handlerClass);
    }
    //
    /** 启动本地监听器 */
    public void startListener(AppContext appContext) throws Throwable {
        this.threadGroup = new NettyThreadGroup(this.getProtocol(), this.getRsfEnvironment());
        this.handlerFactory = createHandler(this.getProtocol(), appContext);
        //
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.threadGroup.getListenLoopGroup(), this.threadGroup.getWorkLoopGroup());
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(channelHandlerList());
            }
        });
        boot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = configBoot(boot).bind(this.getBindAddress().toSocketAddress());
        //
        final BasicFuture<RsfChannel> result = new BasicFuture<RsfChannel>();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    result.failed(future.cause());
                } else {
                    Channel channel = future.channel();
                    result.completed(new RsfChannelOnNetty(getBindAddress(), channel, LinkType.Listener));
                }
            }
        });
        try {
            this.localListener = result.get();
            logger.info("rsf[{}] Server started at {}", this.getProtocol(), this.getBindAddress());
        } catch (Exception e) {
            logger.error("rsf[{}] start listener error: " + e.getMessage(), this.getProtocol(), e);
            throw new RsfException(ProtocolStatus.NetworkError, this.getBindAddress().toString() + " -> " + e.getMessage());
        }
        //
    }
    private ChannelHandler[] channelHandlerList() {
        //
        ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
        // 1st,IP黑名单实现（检测是否可以连入或者连出[IP黑名单实现]）
        handlers.add(new NettySocketAccept(this));
        // 2st,编码解码器
        handlers.addAll(Arrays.asList(this.handlerFactory.channelHandler(this, this.appContext)));
        // 3st,转发RequestInfo、ResponseInfo到RSF
        handlers.add(new NettySocketReader(this));
        return handlers.toArray(new ChannelHandler[handlers.size()]);
    }
    /**停止监听器*/
    public void shutdownListener() {
        this.localListener.close();
        this.threadGroup.shutdownGracefully();
    }
    //
    /**接收到数据(方法public化)*/
    public void receivedData(RsfChannel target, OptionInfo object) {
        super.receivedData(target, object);
    }
    /** 连接到远程机器 */
    public void connectionTo(final InterAddress hostAddress, final BasicFuture<RsfChannel> result) {
        //
        logger.info("connect to {} ...", hostAddress.toHostSchema());
        Bootstrap boot = new Bootstrap();
        boot.group(this.threadGroup.getWorkLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(channelHandlerList());
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
                    RsfChannelOnNetty onNetty = new RsfChannelOnNetty(getBindAddress(), channel, LinkType.Out);
                    result.completed(configListener(onNetty));
                }
            }
        });
    }
    //
    /** IP黑名单实现(包内可见) */
    protected boolean acceptIn(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if (socketAddress == null) {
            return false;
        }
        String hostAddress = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        String hostPort = hostAddress + ":" + port;
        //
        InterAddress target = new InterAddress(this.getSechma(), hostAddress, port, "unknown");
        RsfChannel rsfChannel = new RsfChannelOnNetty(target, ctx.channel(), LinkType.In);
        //
        // .检查当前连接是否被允许接入，如果不允许接入关闭这个连接
        if (!super.acceptChannel(rsfChannel)) {
            rsfChannel.close();
            this.logger.warn("connection refused form {} ,", hostPort);
            return false;
        }
        return true;
    }
    //
    //
    private <T extends AbstractBootstrap<?, ?>> T configBoot(T boot) {
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        // boot.option(ChannelOption.SO_BACKLOG, 128);
        // boot.option(ChannelOption.SO_BACKLOG, 1024);
        // boot.option(ChannelOption.SO_RCVBUF, 1024 * 256);
        // boot.option(ChannelOption.SO_SNDBUF, 1024 * 256);
        boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return boot;
    }
}