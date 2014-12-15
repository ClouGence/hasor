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
import net.hasor.rsf.address.AddressInfo;
import net.hasor.rsf.address.AddressManager;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
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
    private final AbstractRsfContext rsfContext;
    //
    public RsfClientFactory(RsfContext rsfContext) {
        this.rsfContext = (AbstractRsfContext) rsfContext;
    }
    //
    /**连接远程服务（具体的地址）*/
    public RsfClient getClient() throws RsfException, InterruptedException {
        InnerRsfClient client = new InnerRsfClient(this, this.rsfContext);
        RsfOptionSet optManager = this.rsfContext.getSettings().getClientOption();
        for (String optKey : optManager.getOptionKeys()) {
            client.addOption(optKey, optManager.getOption(optKey));
        }
        return client;
    }
    //
    /**关闭这个连接并解除注册。*/
    void closeChannel(NetworkConnection conn) {
        this.addressMapping.remove(conn);
        conn.close();
    }
    //
    private final Map<String, NetworkConnection> addressMapping = new ConcurrentHashMap<String, NetworkConnection>();
    NetworkConnection getConnection(ServiceMetaData<?> metaData, final InnerRsfClient rsfClient) {
        AddressManager addressManager = this.rsfContext.getRegisterCenter().getAddressManager();
        while (true) {
            //查找可用的连接
            AddressInfo address = addressManager.findAddress(metaData);
            if (address == null) {
                return null;
            }
            String addressKey = address.getID();
            NetworkConnection conn = this.addressMapping.get(addressKey);
            //尝试连接到远端
            if (conn == null) {
                conn = this.connSocket(address, rsfClient);
                if (conn != null) {
                    this.addressMapping.put(addressKey, conn);
                }
            }
            //
            if (conn == null) {
                addressManager.invalidAddress(address);
            } else {
                return conn;
            }
        }
    }
    private NetworkConnection connSocket(AddressInfo address, final InnerRsfClient rsfClient) {
        Bootstrap boot = new Bootstrap();
        boot.group(this.rsfContext.getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(channel);
                //
                ch.pipeline().addLast(new RSFCodec(), new InnerClientHandler(rsfClient));
            }
        });
        ChannelFuture future = null;
        SocketAddress remote = new InetSocketAddress(address.getHostIP(), address.getHostPort());
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