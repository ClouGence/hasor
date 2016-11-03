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
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.transform.netty.RSFCodec;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.TimerManager;
import org.more.future.BasicFuture;
import org.more.util.NameThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 维护RSF同其它RSF的连接，并提供数据投递和接收服务。
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment                                    rsfEnvironment;
    private final TimerManager                                      timerManager;
    private final ConcurrentMap<String, BasicFuture<RsfNetChannel>> channelMapping;
    private final EventLoopGroup                                    workLoopGroup;
    private final NioEventLoopGroup                                 listenLoopGroup;
    private final ReceivedListener                                  receivedListener;
    private final ChannelRegister                                   channelRegister;
    //
    private       InterAddress                                      bindAddress;
    private       RsfNetChannel                                     bindListener;
    //
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedListener receivedListener) {
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        int connectTimeout = rsfSettings.getConnectTimeout();
        this.timerManager = new TimerManager(connectTimeout, "RSF-Network", rsfEnvironment.getClassLoader());
        this.channelMapping = new ConcurrentHashMap<String, BasicFuture<RsfNetChannel>>();
        //
        int workerThread = rsfSettings.getNetworkWorker();
        int listenerThread = rsfEnvironment.getSettings().getNetworkListener();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s", rsfEnvironment.getClassLoader()));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s", rsfEnvironment.getClassLoader()));
        logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        //
        this.rsfEnvironment = rsfEnvironment;
        this.receivedListener = receivedListener;
        this.channelRegister = new ManagerChannelRegister();
    }
    /** 销毁。 */
    public void shutdown() {
        logger.info("rsfNetManager, shutdownGracefully.");
        if (bindListener != null) {
            bindListener.close();
        }
        listenLoopGroup.shutdownGracefully();
        workLoopGroup.shutdownGracefully();
    }
    //
    /** 获取RSF运行的地址。 */
    public InterAddress bindAddress() {
        return this.bindAddress;
    }
    /** 建立或获取和远程的连接。 */
    public Future<RsfNetChannel> getChannel(InterAddress target) throws InterruptedException, ExecutionException {
        Future<RsfNetChannel> channelFuture = this.channelMapping.get(target.getHostPort());
        if (channelFuture != null && channelFuture.isDone()) {
            RsfNetChannel channel = null;
            try {
                channel = channelFuture.get();
                if (channel != null && !channel.isActive()) {
                    this.channelMapping.remove(target.getHostPort());// conect is bad.
                    channelFuture = null;
                }
            } catch (Exception e) {
                this.channelMapping.remove(target.getHostPort());// conect is bad.
                channelFuture = null;
            }
        }
        if (channelFuture != null) {
            return channelFuture;
        } else {
            channelFuture = connSocket(target);// TODO 这里应考虑到并发
        }
        return channelFuture;
    }
    /*连接到远程机器*/
    private Future<RsfNetChannel> connSocket(InterAddress hostAddress) {
        String hostPort = hostAddress.getHostPort();
        BasicFuture<RsfNetChannel> result = this.channelMapping.get(hostPort);
        if (result != null) {
            return result;
        }
        synchronized (this) {
            result = this.channelMapping.get(hostPort);
            if (result != null) {
                return result;
            }
            result = new BasicFuture<RsfNetChannel>();
            this.channelMapping.put(hostPort, result);
            logger.info("connect to {} ...", hostAddress);
            Bootstrap boot = new Bootstrap();
            boot.group(this.workLoopGroup);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(//
                            new RSFCodec(rsfEnvironment),//
                            new RpcCodec(RsfNetManager.this)//
                    );
                }
            });
            configBoot(boot).connect(hostAddress.toSocketAddress()).addListener(new ConnSocketCallBack(result));
        }
        return result;
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
    /** 启动服务器，使用“local”地址作为服务提供地址。 */
    public void start() throws UnknownHostException {
        this.start(NetworkUtils.finalBindAddress("local"), 0);
    }
    /** 启动服务器。 */
    public void start(InetAddress localAddress) throws UnknownHostException {
        this.start(localAddress, 0);
    }
    /** 启动服务器。 */
    public void start(String bindAddress, int bindSocket) throws UnknownHostException {
        InetAddress localAddress = NetworkUtils.finalBindAddress(bindAddress);
        this.start(localAddress, bindSocket);
    }
    /** 启动服务器。 */
    public void start(InetAddress localAddress, int bindSocket) throws UnknownHostException {
        RsfSettings rsfSettings = this.rsfEnvironment.getSettings();
        if (localAddress == null) {
            localAddress = NetworkUtils.finalBindAddress(rsfSettings.getBindAddress());
        }
        if (bindSocket == 0) {
            bindSocket = rsfSettings.getBindPort();
        }
        try {
            this.bindAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, rsfSettings.getUnitName());
        } catch (Throwable e) {
            throw new UnknownHostException(e.getMessage());
        }
        logger.info("bindSocket at {}", this.bindAddress);
        //
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.listenLoopGroup, this.workLoopGroup);
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(//
                        new RSFCodec(rsfEnvironment),//
                        new RpcCodec(RsfNetManager.this)//
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
    //
    //
    //
    ReceivedListener getReceivedListener() {
        return this.receivedListener;
    }
    public TimerManager getTimerManager() {
        return this.timerManager;
    }
    ChannelRegister getChannelRegister() {
        return this.channelRegister;
    }
    private class ManagerChannelRegister implements ChannelRegister {
        public void completed(InterAddress targetAddress, RsfNetChannel netChannel) {
            String hostPort = targetAddress.getHostPort();
            BasicFuture<RsfNetChannel> future = channelMapping.get(hostPort);
            if (future != null) {
                future.completed(netChannel);
            } else {
                future = new BasicFuture<RsfNetChannel>();
                future.completed(netChannel);
                channelMapping.put(hostPort, future);
            }
        }
        public void failed(InterAddress targetAddress, Throwable cause) {
            if (targetAddress == null) {
                logger.error(cause.getMessage(), cause);
                return;
            }
            BasicFuture<RsfNetChannel> future = channelMapping.get(targetAddress.getHostPort());
            if (future != null) {
                future.failed(cause);
            }
        }
    }
}