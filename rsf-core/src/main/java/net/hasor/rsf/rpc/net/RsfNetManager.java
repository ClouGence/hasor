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
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
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
    protected Logger                                         logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment                             rsfEnvironment;
    private final int                                        connectTimeout;
    private final TimerManager                               timerManager;
    private final ConcurrentMap<InterAddress, RsfNetChannel> channelMapping;
    //
    private InterAddress                                     bindAddress;
    private Channel                                          socketListener;
    private EventLoopGroup                                   workLoopGroup;
    private NioEventLoopGroup                                listenLoopGroup;
    private ReceivedListener                                 receivedListener;
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedListener receivedListener) {
        this.rsfEnvironment = rsfEnvironment;
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        this.connectTimeout = rsfSettings.getConnectTimeout();
        this.timerManager = new TimerManager(this.connectTimeout, "RSF-Network");
        this.channelMapping = new ConcurrentHashMap<InterAddress, RsfNetChannel>();
        //
        Hasor.addShutdownListener(rsfEnvironment, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                logger.info("rsfNetManager, shutdownGracefully.");
                socketListener.close();
                listenLoopGroup.shutdownGracefully();
                workLoopGroup.shutdownGracefully();
            }
        });
        //
        int workerThread = rsfSettings.getNetworkWorker();
        int listenerThread = this.rsfEnvironment.getSettings().getNetworkListener();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s"));
        logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        //
        this.receivedListener = receivedListener;
    }
    //
    //
    //
    /**获取RSF运行的地址。*/
    public InterAddress bindAddress() {
        return this.bindAddress;
    }
    /**建立或获取和远程的连接。*/
    public RsfNetChannel getChannel(InterAddress target) {
        RsfNetChannel client = this.channelMapping.get(target);
        if (client != null && client.isActive() == false) {
            this.channelMapping.remove(target);//conect is bad.
            client = null;
        }
        if (client != null) {
            return client;
        }
        /*同步调用不存在并发*/
        return connSocket(target);
    }
    /*连接到远程机器*/
    private synchronized RsfNetChannel connSocket(InterAddress hostAddress) {
        SocketAddress remote = new InetSocketAddress(hostAddress.getHost(), hostAddress.getPort());
        logger.info("connect to {} ...", hostAddress);
        //
        ChannelFuture future = null;
        Bootstrap boot = new Bootstrap();
        boot.group(this.workLoopGroup);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new RsfChannelInitializer());
        future = boot.connect(remote).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        //
        try {
            future.await(this.connectTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            future.channel().close();
            logger.error("connect to {} failure , {}", hostAddress, e.getMessage());
        }
        if (future.isSuccess() == true) {
            logger.info("remote {} connected.", hostAddress);
            return new RsfNetChannel(hostAddress, future.channel());
        }
        return null;
    }
    //
    //
    //
    /**启动服务器。*/
    public void start() throws UnknownHostException, URISyntaxException {
        this.start("127.0.0.1", 0);
    }
    /**启动服务器。*/
    public void start(InetAddress localAddress) throws UnknownHostException, URISyntaxException {
        this.start(localAddress, 0);
    }
    /**启动服务器。*/
    public void start(String bindAddress, int bindSocket) throws UnknownHostException, URISyntaxException {
        InetAddress localAddress = NetworkUtils.finalBindAddress(bindAddress);
        this.start(localAddress, bindSocket);
    }
    /**启动服务器。*/
    public void start(InetAddress localAddress, int bindSocket) throws UnknownHostException, URISyntaxException {
        RsfSettings rsfSettings = this.rsfEnvironment.getSettings();
        if (localAddress == null)
            localAddress = NetworkUtils.finalBindAddress(rsfSettings.getBindAddress());
        if (bindSocket == 0)
            bindSocket = rsfSettings.getBindPort();
        this.bindAddress = new InterAddress(localAddress.getHostAddress(), bindSocket, rsfSettings.getUnitName());
        logger.info("bindSocket at {}", this.bindAddress);
        //
        ChannelFuture future = null;
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(this.listenLoopGroup, this.workLoopGroup);
        boot.channel(NioServerSocketChannel.class);
        boot.childHandler(new RsfChannelInitializer());
        boot.option(ChannelOption.SO_BACKLOG, 128);
        boot.childOption(ChannelOption.SO_KEEPALIVE, true);
        future = boot.bind(localAddress, bindSocket).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        //
        try {
            future.await(this.connectTimeout, TimeUnit.MILLISECONDS);
            if (future.isSuccess() == true) {
                logger.info("rsf Server started at {}:{}", localAddress, bindSocket);
                this.socketListener = future.channel();
            }
        } catch (InterruptedException e) {
            future.channel().close();
            logger.error(e.getMessage(), e);
        }
    }
    //
    //
    //
    void addChannel(InterAddress targetKey, RsfNetChannel channel) {
        if (channel.isActive()) {
            this.channelMapping.put(targetKey, channel);
        }
    }
    void removeChannel(InterAddress target) {
        if (target == null) {
            return;
        }
        RsfNetChannel netChannel = this.channelMapping.remove(target);
        if (netChannel != null) {
            netChannel.close();
        }
    }
    private class RsfChannelInitializer extends ChannelInitializer<SocketChannel> {
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new RSFCodec(), new RpcCodec(timerManager, RsfNetManager.this, receivedListener));
        }
    }
}