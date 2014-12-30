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
package net.hasor.rsf.remoting.transport.customer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.hasor.core.Hasor;
import net.hasor.rsf.adapter.AbstracAddressCenter;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.adapter.AbstractfRsfClient;
import net.hasor.rsf.adapter.ConnectionManager;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.transport.connection.NetworkConnection;
import net.hasor.rsf.remoting.transport.netty.RSFCodec;
/**
 * 负责维持 RsfCustomerClient 列表，同时负责创建和销毁它们。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerConnectionManager implements ConnectionManager {
    private final AbstractRsfContext          rsfContext;
    private final Map<URL, RsfCustomerClient> clientMapping;
    //
    public InnerConnectionManager(AbstractRsfContext rsfContext) {
        this.rsfContext = rsfContext;
        this.clientMapping = new ConcurrentHashMap<URL, RsfCustomerClient>();
    }
    // 
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    /**连接远程服务（具体的地址）*/
    public RsfCustomerClient getClient(URL remoteAddress) throws RsfException, InterruptedException {
        AbstracAddressCenter addressCenter = this.rsfContext.getAddressCenter();
        while (true) {
            //查找可用的连接
            RsfCustomerClient client = this.clientMapping.get(remoteAddress);
            //异常状态关闭连接重新申请新的
            if (client.isOpen() == false) {
                this.unRegistered(client);//解除注册。
            }
            //尝试连接到远端
            if (conn == null) {
                conn = connSocket(addressURL, rsfClient);
                if (conn != null) {
                    this.addressMapping.put(addressURL, conn);
                }
            }
            //
            if (conn == null) {
                addressCenter.invalidAddress(addressURL);
            } else {
                return conn;
            }
        }
        return null;//TODO
    }
    public void unRegistered(AbstractfRsfClient client) {
        // TODO Auto-generated method stub
        
    }
    //
    /**关闭这个连接并解除注册。*/
    public void closeChannel(URL remoteAddress) {
        RsfCustomerClient client = this.clientMapping.get(remoteAddress);
        this.clientMapping.remove(remoteAddress);
        client.close();
    }
    //
    //
    //
    private static NetworkConnection connSocket(final URL addressURL, final RsfCustomerClient rsfClient) {
        Bootstrap boot = new Bootstrap();
        boot.group(rsfClient.getRsfContext().getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(addressURL, channel);
                //
                ch.pipeline().addLast(new RSFCodec(), RsfCustomerClient.buildCustomerHandler(rsfClient));
            }
        });
        ChannelFuture future = null;
        SocketAddress remote = new InetSocketAddress(addressURL.getHost(), addressURL.getPort());
        future = boot.connect(remote);
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new RsfException(ProtocolStatus.ClientError, e);
        }
        if (future.isSuccess()) {
            NetworkConnection conn = NetworkConnection.getConnection(future.channel());
            return conn;
        }
        //
        RsfException e = new RsfException(ProtocolStatus.ClientError, future.cause());
        Hasor.logWarn(e);
        return null;
    }
}