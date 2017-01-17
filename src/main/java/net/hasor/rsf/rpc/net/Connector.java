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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import org.more.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 连接器
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
public class Connector {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private String        protocol;
    private InterAddress  bindAddress;
    private InterAddress  gatewayAddress;
    private RsfNetChannel bindListener;
    public Connector(String protocolKey, InterAddress local, InterAddress gateway) {
        this.protocol = protocolKey;
        this.bindAddress = local;
        this.gatewayAddress = gateway;
    }
    //
    //
    public InterAddress getBindAddress() {
        return bindAddress;
    }
    public InterAddress getGatewayAddress() {
        return gatewayAddress;
    }
    //
    public RsfNetChannel connectionTo(InterAddress hostAddress, EventLoopGroup workLoopGroup) {
        Bootstrap boot = new Bootstrap();
        boot.group(workLoopGroup);
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(//
                        new RSFCodec(rsfEnvironment),                // RSF协议
                        new RpcCodec(RsfNetManager.this)// RSF握手协议 TODO
                );
            }
        });
        configBoot(boot).connect(hostAddress.toSocketAddress()).addListener(new ConnSocketCallBack(result));
        return result;
        return null;
    }
    private static class ConnSocketCallBack implements ChannelFutureListener {
        private BasicFuture<RsfNetChannel> result = null;
        public ConnSocketCallBack(BasicFuture<RsfNetChannel> result) {
            this.result = result;
        }
        public void operationComplete(ChannelFuture future) {
            if (!future.isSuccess()) {
                future.channel().close();
                this.result.failed(future.cause());
            } else {
                //TODO
            }
        }
    }
    //
    public void shutdown() {
        this.bindListener.close();
    }
    public void startListener(NioEventLoopGroup listenLoopGroup, EventLoopGroup workLoopGroup) {
        //        InetAddress localAddress = local.toSocketAddress();
        //        int bindSocket = local.getPort();
        //        RsfSettings rsfSettings = this.rsfEnvironment.getSettings();
        //        if (localAddress == null) {
        //            localAddress = NetworkUtils.finalBindAddress(rsfSettings.getBindAddress());
        //        }
        //        if (bindSocket == 0) {
        //            bindSocket = rsfSettings.getBindPort();
        //        }
        //        try {
        //            this.bindAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, rsfSettings.getUnitName());
        //            logger.info("bindSocket at {}", this.bindAddress);
        //            if (StringUtils.isNotBlank(rsfSettings.getGatewayAddress()) && rsfSettings.getGatewayPort() > 0) {
        //                this.gatewayAddress = new InterAddress(rsfSettings.getGatewayAddress(), rsfSettings.getGatewayPort(), rsfSettings.getUnitName());
        //                logger.info("gatewayBindSocket at {}", this.gatewayAddress);
        //            }
        //        } catch (Throwable e) {
        //            throw new UnknownHostException(e.getMessage());
        //        }
        //
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(listenLoopGroup, workLoopGroup);
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(//
                        new RSFCodec(rsfEnvironment),//
                        new RpcCodec(RsfNetManager.this)// TODO
                );
            }
        });
        boot.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.childOption(ChannelOption.SO_KEEPALIVE, true);
        ChannelFuture future = configBoot(boot).bind(localAddress, bindSocket);
        //
        final BasicFuture<RsfNetChannel> result = new BasicFuture<RsfNetChannel>();
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    future.channel().close();
                    result.failed(future.cause());
                } else {
                    Channel channel = future.channel();
                    result.completed(new RsfNetChannel(bindAddress, channel, new AtomicBoolean(true)));
                }
            }
        });
        try {
            this.bindListener = result.get();
            logger.info("rsf Server started at {}:{}", localAddress, bindSocket);
        } catch (Exception e) {
            logger.error("rsf start listener error: " + e.getMessage(), e);
            throw new RsfException(ProtocolStatus.NetworkError, e);
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
}