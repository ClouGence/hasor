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
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.address.InterAddress;
import net.hasor.rsf.transform.netty.RSFCodec;
import net.hasor.rsf.utils.NameThreadFactory;
/**
 * 维护RSF同其它RSF的连接，并提供数据投递和接收服务。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger                                         logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment                             rsfEnvironment;
    private final int                                        connectTimeout;
    private final ConcurrentMap<InterAddress, RsfNetChannel> channelMapping;
    private EventLoopGroup                                   workLoopGroup;
    private ReceivedListener                                 listener;
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedListener listener) {
        this.rsfEnvironment = rsfEnvironment;
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        this.connectTimeout = rsfSettings.getConnectTimeout();
        this.channelMapping = new ConcurrentHashMap<InterAddress, RsfNetChannel>();
        //
        Hasor.addShutdownListener(rsfEnvironment, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                logger.info("workLoopGroup, shutdownGracefully.");
                workLoopGroup.shutdownGracefully();
            }
        });
        //
        int workerThread = rsfSettings.getNetworkWorker();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        logger.info("nioEventLoopGroup, workerThread = " + workerThread);
    }
    protected RsfEnvironment getRsfEnvironment() {
        return rsfEnvironment;
    }
    protected EventLoopGroup getWorkLoopGroup() {
        return workLoopGroup;
    }
    protected ReceivedListener getListener() {
        return listener;
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
        if ((client = connSocket(target)) != null) {
            RsfNetChannel oldChannel = this.channelMapping.put(target, client);
            if (oldChannel != null && oldChannel != client) {
                oldChannel.close();
            }
            return client;
        }
        return null;
    }
    /**关闭连接释放资源。*/
    public void closeChannel(InterAddress key) {
        RsfNetChannel netChannel = this.channelMapping.remove(key);
        if (netChannel != null) {
            netChannel.close();
        }
    }
    //
    /*连接到远程机器*/
    private synchronized RsfNetChannel connSocket(final InterAddress hostAddress) {
        Bootstrap boot = new Bootstrap();
        boot.group(this.workLoopGroup);
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                logger.info("initConnection connect {}.", hostAddress);
                ch.pipeline().addLast(new RSFCodec(), new RpcCodec(listener));
            }
        });
        ChannelFuture future = null;
        SocketAddress remote = new InetSocketAddress(hostAddress.getHost(), hostAddress.getPort());
        logger.info("connect to {} ...", hostAddress);
        future = boot.connect(remote).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);;
        try {
            future.await(this.connectTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            future.channel().close();
            logger.error("connect to {} failure , {}", hostAddress, e.getMessage());
            return null;
        }
        if (future.isSuccess() == true) {
            logger.info("remote {} connected.", hostAddress);
            return new RsfNetChannel(hostAddress, future.channel());
        }
        //
        logger.error("connect to {} failure , {}", hostAddress, future.cause());
        future.channel().close();
        return null;
    }
}