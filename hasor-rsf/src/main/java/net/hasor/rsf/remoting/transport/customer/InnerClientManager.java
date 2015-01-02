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
import net.hasor.rsf.adapter.AbstractClientManager;
import net.hasor.rsf.adapter.AbstractRequestManager;
import net.hasor.rsf.adapter.AbstractRsfClient;
import net.hasor.rsf.adapter.AbstractRsfContext;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.remoting.transport.connection.NetworkConnection;
import net.hasor.rsf.remoting.transport.netty.RSFCodec;
/**
 * 为{@link InnerRsfCustomerHandler}提供{@link RsfRequestManager}列表维护。
 * 同时负责创建和销毁{@link RsfRequestManager}的功能。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerClientManager extends AbstractClientManager {
    private final RsfRequestManager           rsfRequestManager;
    private final AbstractRsfContext          rsfContext;
    private final Map<URL, AbstractRsfClient> clientMapping;
    // 
    public InnerClientManager(RsfRequestManager rsfRequestManager) {
        this.rsfRequestManager = rsfRequestManager;
        this.rsfContext = rsfRequestManager.getRsfContext();
        this.clientMapping = new ConcurrentHashMap<URL, AbstractRsfClient>();
    }
    //
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    private AbstractRequestManager getRequestManager() {
        return rsfRequestManager;
    }
    /**连接远程服务（具体的地址）*/
    public AbstractRsfClient getClient(URL hostAddress) {
        AbstracAddressCenter addressCenter = this.rsfContext.getAddressCenter();
        {
            //查找可用的连接
            AbstractRsfClient client = this.clientMapping.get(hostAddress);
            //异常状态关闭连接重新申请新的
            if (client != null && client.isActive() == false) {
                this.unRegistered(client);//解除注册
            }
            //尝试连接到远端
            if (client == null) {
                client = connSocket(hostAddress);
            }
            //
            if (client == null) {
                addressCenter.invalidAddress(hostAddress);
            } else {
                return client;
            }
        }
        return null;
    }
    /**关闭这个连接并解除注册。*/
    public synchronized void unRegistered(AbstractRsfClient client) {
        if (client == null)
            return;
        URL hostAddress = client.getHostAddress();
        AbstractRsfClient localClient = this.clientMapping.get(hostAddress);
        if (client != localClient)
            throw new RsfException("target is not form me.");
        //
        this.clientMapping.remove(hostAddress).close();
    }
    //
    private synchronized AbstractRsfClient connSocket(final URL hostAddress) {
        AbstractRsfClient rsfClient = this.clientMapping.get(hostAddress);
        if (rsfClient != null) {
            return rsfClient;
        }
        //
        Bootstrap boot = new Bootstrap();
        boot.group(this.rsfContext.getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(hostAddress, channel);
                //
                ch.pipeline().addLast(new RSFCodec(), new InnerRsfCustomerHandler(getRequestManager()));
            }
        });
        ChannelFuture future = null;
        SocketAddress remote = new InetSocketAddress(hostAddress.getHost(), hostAddress.getPort());
        future = boot.connect(remote);
        try {
            future.await();
        } catch (InterruptedException e) {
            throw new RsfException(ProtocolStatus.ClientError, e);
        }
        if (future.isSuccess() == false) {
            RsfException e = new RsfException(ProtocolStatus.ClientError, future.cause());
            Hasor.logWarn(e);
        }
        //
        NetworkConnection conn = NetworkConnection.getConnection(future.channel());
        rsfClient = new InnerRsfClient(this.getRequestManager(), conn);
        if (rsfClient != null) {
            this.clientMapping.put(hostAddress, rsfClient);
        }
        return rsfClient;
    }
}