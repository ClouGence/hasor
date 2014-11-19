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
package net.hasor.rsf.runtime.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.rsf.executes.NameThreadFactory;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.general.SendLimitPolicy;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.runtime.common.NetworkConnection;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClientFactory {
    private int                                        defaultTimeout  = RSFConstants.ClientTimeout;
    private int                                        maximumRequest  = 200;
    private SendLimitPolicy                            sendLimitPolicy = SendLimitPolicy.Reject;

    
    private AbstractRsfContext                         rsfContext;
    private final Map<Channel, InnerAbstractRsfClient> channelClientMapping;
    //
    public RsfClientFactory(AbstractRsfContext rsfContext) {
        Settings settings = rsfContext.getSettings();
        this.defaultTimeout = settings.getInteger("hasor.rsfConfig.client.defaultTimeout", RSFConstants.ClientTimeout);
        this.maximumRequest = settings.getInteger("hasor.rsfConfig.client.maximumRequest", 200);
        this.sendLimitPolicy = settings.getEnum("hasor.rsfConfig.client.sendLimitPolicy", SendLimitPolicy.class, SendLimitPolicy.Reject);
        //
        BlockingQueue<Runnable> inWorkQueue = new LinkedBlockingQueue<Runnable>();
        this.clientExecutor = new ThreadPoolExecutor(1, 1, 300L, TimeUnit.SECONDS, inWorkQueue,//
                new NameThreadFactory("RSF-Client-%s"), new ThreadPoolExecutor.AbortPolicy());
        //
        this.rsfContext = rsfContext;
        this.channelClientMapping = new ConcurrentHashMap<Channel, InnerAbstractRsfClient>();
    }
    Executor getExecutor() {
        // TODO Auto-generated method stub
        return this.clientExecutor;
    }
    //
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(String hostName, int port) {
        return connect(new InetSocketAddress(hostName, port));
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress) {
        return connect(remoteAddress, null);
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        Hasor.assertIsNotNull(remoteAddress, "remoteAddress is null.");
        //
        Bootstrap boot = new Bootstrap();
        boot.group(this.getRsfContext().getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(), new InnerClientHandler(RsfClientFactory.this));
            }
        });
        ChannelFuture future = null;
        if (localAddress != null) {
            future = boot.connect(remoteAddress, localAddress);
        } else {
            future = boot.connect(remoteAddress);
        }
        //
        NetworkConnection connection = new NetworkConnection(future.channel());
        InnerAbstractRsfClient client = this.createRsfClient(connection);
        channelClientMapping.put(future.channel(), client);
        return client;
    }
    //
    /**获取{@link AbstractRsfContext}对象。*/
    protected AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    /**获取默认超时时间。*/
    public int getDefaultTimeout() {
        return this.defaultTimeout;
    }
    //
    /**获取Channel 所属的 RsfClient*/
    InnerAbstractRsfClient getRsfClient(Channel socketChanne) {
        return channelClientMapping.get(socketChanne);
    }
    /**删除Channel 与 RsfClient 的映射关系。*/
    void removeChannelMapping(Channel socketChanne) {
        channelClientMapping.remove(socketChanne);
    }
    //  
    protected InnerAbstractRsfClient createRsfClient(NetworkConnection connection) {
        return new SingleRsfClient(connection, this);
    }
}