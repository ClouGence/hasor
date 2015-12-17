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
package net.hasor.rsf.rpc.net;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.more.future.BasicFuture;
import org.more.future.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.rsf.transform.netty.RSFCodec;
import net.hasor.rsf.utils.NameThreadFactory;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.TimerManager;
/**
 * 维护RSF同其它RSF的连接，并提供数据投递和接收服务。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger                                                 logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment                                     rsfEnvironment;
    private final TimerManager                                       timerManager;
    private final ConcurrentMap<InterAddress, Future<RsfNetChannel>> channelMapping;
    private EventLoopGroup                                           workLoopGroup;
    private NioEventLoopGroup                                        listenLoopGroup;
    private ReceivedListener                                         receivedListener;
    //
    private InterAddress                                             bindAddress;
    private RsfNetChannel                                            bindListener;
    //
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedListener receivedListener) {
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        int connectTimeout = rsfSettings.getConnectTimeout();
        this.timerManager = new TimerManager(connectTimeout, "RSF-Network");
        this.channelMapping = new ConcurrentHashMap<InterAddress, Future<RsfNetChannel>>();
        //
        Hasor.addShutdownListener(rsfEnvironment, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                logger.info("rsfNetManager, shutdownGracefully.");
                if (bindListener != null) {
                    bindListener.close();
                }
                listenLoopGroup.shutdownGracefully();
                workLoopGroup.shutdownGracefully();
            }
        });
        //
        int workerThread = rsfSettings.getNetworkWorker();
        int listenerThread = rsfEnvironment.getSettings().getNetworkListener();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s"));
        logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        //
        this.rsfEnvironment = rsfEnvironment;
        this.receivedListener = receivedListener;
    }
    //
    /**获取RSF运行的地址。*/
    public InterAddress bindAddress() {
        return this.bindAddress;
    }
    /**建立或获取和远程的连接。*/
    public Future<RsfNetChannel> getChannel(InterAddress target) throws InterruptedException, ExecutionException {
        Future<RsfNetChannel> channelFuture = this.channelMapping.get(target);
        if (channelFuture != null && channelFuture.isDone()) {
            RsfNetChannel channel = channelFuture.get();
            if (channel != null && channel.isActive() == false) {
                this.channelMapping.remove(target);//conect is bad.
                channelFuture = null;
            }
        }
        if (channelFuture != null) {
            return channelFuture;
        } else {
            channelFuture = connSocket(target);// TODO 这里应考虑到并发 
            this.channelMapping.put(target, channelFuture);
        }
        return channelFuture;
    }
    /*连接到远程机器*/
    private Future<RsfNetChannel> connSocket(InterAddress hostAddress) {s
        SocketAddress remote = new InetSocketAddress(hostAddress.getHost(), hostAddress.getPort());
        logger.info("connect to {} ...", hostAddress);
        //
        BasicFuture<RsfNetChannel> result = new BasicFuture<RsfNetChannel>(new ConnectionFutureCallback(this, hostAddress));
        Bootstrap boot = new Bootstrap();
        boot.group(this.workLoopGroup);
        boot.channel(NioSocketChannel.class);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(), new RpcCodec(RsfNetManager.this));
            }
        });
        configBoot(boot).connect(remote).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        //
        return result;
    }
    //
    /**启动服务器。*/
    public void start() throws UnknownHostException {
        this.start("127.0.0.1", 0);
    }
    /**启动服务器。*/
    public void start(InetAddress localAddress) throws UnknownHostException {
        this.start(localAddress, 0);
    }
    /**启动服务器。*/
    public void start(String bindAddress, int bindSocket) throws UnknownHostException {
        InetAddress localAddress = NetworkUtils.finalBindAddress(bindAddress);
        this.start(localAddress, bindSocket);
    }
    /**启动服务器。*/
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
                ch.pipeline().addLast(new RSFCodec(), new RpcCodec(RsfNetManager.this));
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
            logger.info("rsf Server started at {}:{}", localAddress, bindSocket);
            this.bindListener = result.get();
        } catch (Exception e) {
            logger.error("rsf start listener error: " + e.getMessage(), e);
            throw new RsfException(ProtocolStatus.NetworkError, e);
        }
    }
    private <T extends AbstractBootstrap<?, ?>> T configBoot(T boot) {
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.option(ChannelOption.SO_BACKLOG, 1024);
        boot.option(ChannelOption.SO_RCVBUF, 1024 * 256);
        boot.option(ChannelOption.SO_SNDBUF, 1024 * 256);
        boot.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return boot;
    }
    //
    //
    //
    ReceivedListener getReceivedListener() {
        return this.receivedListener;
    }
    TimerManager getTimerManager() {
        return this.timerManager;
    }
    //    void addChannel(InterAddress targetKey, RsfNetChannel channel) {
    //        if (channel.isActive()) {
    //            this.channelMapping.put(targetKey, channel);
    //        }
    //    }
    //    void removeChannel(InterAddress target) {
    //        if (target == null) {
    //            return;
    //        }
    //        RsfNetChannel netChannel = this.channelMapping.remove(target);
    //        if (netChannel != null) {
    //            netChannel.close();
    //        }
    //    }
    void closeChannel(InterAddress targetKey) {
        // TODO Auto-generated method stub
    }
}
class ConnectionFutureCallback implements FutureCallback<RsfNetChannel> {
    private RsfNetManager rsfNetManager;
    private InterAddress  hostAddress;
    public ConnectionFutureCallback(RsfNetManager rsfNetManager, InterAddress hostAddress) {
        this.rsfNetManager = rsfNetManager;
        this.hostAddress = hostAddress;
    }
    public void cancelled() {
        // TODO Auto-generated method stub
        System.out.println();
    }
    public void completed(RsfNetChannel arg0) {
        // TODO Auto-generated method stub
        System.out.println();
    }
    public void failed(Throwable ex) {
        // TODO Auto-generated method stub
        System.out.println();
    }
}