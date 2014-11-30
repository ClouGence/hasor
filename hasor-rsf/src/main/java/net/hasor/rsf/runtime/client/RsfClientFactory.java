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
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.general.SendLimitPolicy;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfOptionSet;
import net.hasor.rsf.runtime.common.NetworkConnection;
import net.hasor.rsf.runtime.context.AbstractRsfContext;
/**
 * 负责维持与远程RSF服务器连接的客户端类，并同时负责维护request/response。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClientFactory {
    private final int                                            maximumRequest;
    private final SendLimitPolicy                                sendLimitPolicy;
    private final AbstractRsfContext                             rsfContext;
    private final Map<NetworkConnection, InnerAbstractRsfClient> connClientMapping;
    //
    public RsfClientFactory(AbstractRsfContext rsfContext) {
        Settings settings = rsfContext.getSettings();
        this.maximumRequest = settings.getInteger("hasor.rsfConfig.client.maximumRequest", 200);
        this.sendLimitPolicy = settings.getEnum("hasor.rsfConfig.client.sendLimitPolicy", SendLimitPolicy.class, SendLimitPolicy.Reject);
        //
        this.rsfContext = rsfContext;
        this.connClientMapping = new ConcurrentHashMap<NetworkConnection, InnerAbstractRsfClient>();
    }
    //
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(String hostName, int port) throws RsfException, InterruptedException {
        return connect(new InetSocketAddress(hostName, port));
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress) throws RsfException, InterruptedException {
        return connect(remoteAddress, null);
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress, SocketAddress localAddress) throws RsfException, InterruptedException {
        Hasor.assertIsNotNull(remoteAddress, "remoteAddress is null.");
        //
        Bootstrap boot = new Bootstrap();
        boot.group(this.rsfContext.getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(channel);
                //
                ch.pipeline().addLast(new RSFCodec(), new InnerClientHandler(RsfClientFactory.this));
            }
        });
        ChannelFuture future = null;
        if (localAddress != null) {
            future = boot.connect(remoteAddress, localAddress);
        } else {
            future = boot.connect(remoteAddress);
        }
        future.await();
        if (future.isSuccess()) {
            NetworkConnection conn = NetworkConnection.getConnection(future.channel());
            InnerAbstractRsfClient client = this.createRsfClient(conn);
            RsfOptionSet optManager = this.rsfContext.getClientOption();
            for (String optKey : optManager.getOptionKeys()) {
                client.addOption(optKey, optManager.getOption(optKey));
            }
            this.connClientMapping.put(conn, client);
            return client;
        }
        throw new RsfException(ProtocolStatus.ClientError, future.cause());
    }
    //
    /**获取{@link AbstractRsfContext}对象。*/
    public RsfContext getRsfContext() {
        return this.rsfContext;
    }
    /**获取每个客户端连接可以发起的最大连接请求数量。*/
    public int getMaximumRequest() {
        return this.maximumRequest;
    }
    /**获取当客户端达到了最大连接请求数的时所做出的策略。*/
    public SendLimitPolicy getSendLimitPolicy() {
        return this.sendLimitPolicy;
    }
    //
    /**获取NetworkConnection 所属的 RsfClient*/
    InnerAbstractRsfClient getRsfClient(NetworkConnection conn) {
        return connClientMapping.get(conn);
    }
    /**删除NetworkConnection 与 RsfClient 的映射关系。*/
    void removeChannelMapping(NetworkConnection conn) {
        connClientMapping.remove(conn);
    }
    //
    /**创建 RsfClient 对象*/
    protected InnerAbstractRsfClient createRsfClient(NetworkConnection connection) {
        return new SingleRsfClient(connection, this, this.rsfContext);
    }
}