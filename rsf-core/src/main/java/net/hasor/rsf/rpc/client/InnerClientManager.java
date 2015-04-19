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
package net.hasor.rsf.rpc.client;
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
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.AddressInfo;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.manager.DefaultAddressCenter;
import net.hasor.rsf.protocol.netty.RSFCodec;
import net.hasor.rsf.rpc.NetworkConnection;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import org.more.logger.LoggerHelper;
/**
 * 为{@link InnerRsfCustomerHandler}提供{@link AbstractRsfClient}列表维护。
 * 同时负责创建和销毁{@link AbstractRsfClient}的功能。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class InnerClientManager {
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
    /** @return 获取{@link RsfContext}*/
    public AbstractRsfContext getRsfContext() {
        return this.rsfContext;
    }
    private RsfRequestManager getRequestManager() {
        return rsfRequestManager;
    }
    /**
     * 获取或创建一个连接
     * @param rsfBindInfo 服务注册信息。
     * @return 返回远程服务所处的客户端连接。
     */
    public AbstractRsfClient getClient(RsfBindInfo<?> bindInfo) {
        if (bindInfo == null)
            return null;
        DefaultAddressCenter addressCenter = this.rsfContext.getAddressCenter();
        AddressInfo refereeAddress = null;
        while (true) {
            /*如果一个地址更新操作正在进行中，则该方法会被暂时阻塞直至操作结束。*/
            refereeAddress = addressCenter.findHostAddress(bindInfo);
            if (refereeAddress == null) {
                break;
            }
            synchronized (this.clientMapping) {
                AbstractRsfClient client = this.clientMapping.get(refereeAddress.getAddress());
                if (client != null && client.isActive() == false) {
                    this.unRegistered(refereeAddress.getAddress());
                }
                if (client == null) {
                    if ((client = connSocket(refereeAddress)) != null) {
                        this.clientMapping.put(refereeAddress.getAddress(), client);
                        return client;
                    }
                } else {
                    return client;
                }
            }
            /*如果一个地址更新操作正在进行中，则该方法会被暂时阻塞直至操作结束。
             * 而该方法会引发一个局部锁定。*/
            addressCenter.invalidAddress(refereeAddress);
        }
        //
        throw new RsfException(ProtocolStatus.ClientError, "there is not invalid address.");
    }
    /**
     * 关闭这个连接并解除注册。
     * @param hostAddress 主机地址
     */
    public void unRegistered(URL hostAddress) {
        if (hostAddress == null)
            return;
        //
        synchronized (this.clientMapping) {
            AbstractRsfClient localClient = this.clientMapping.get(hostAddress);
            if (localClient != null) {
                if (hostAddress.equals(localClient.getHostAddress()) == false) {
                    throw new RsfException(ProtocolStatus.ClientError, "target is not form me.");
                }
                this.clientMapping.remove(hostAddress).close();
            }
        }
    }
    //
    private synchronized AbstractRsfClient connSocket(final AddressInfo hostAddress) {
        final URL hostURL = hostAddress.getAddress();
        Bootstrap boot = new Bootstrap();
        boot.group(this.rsfContext.getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                Channel channel = ch.pipeline().channel();
                NetworkConnection.initConnection(hostURL, channel);
                LoggerHelper.logInfo("initConnection connect %s.", hostURL);
                //
                ch.pipeline().addLast(new RSFCodec(), new InnerRsfCustomerHandler(getRequestManager()));
            }
        });
        ChannelFuture future = null;
        SocketAddress remote = new InetSocketAddress(hostURL.getHost(), hostURL.getPort());
        LoggerHelper.logInfo("connect to %s ...", hostURL);
        future = boot.connect(remote);
        try {
            future.await();
        } catch (InterruptedException e) {
            LoggerHelper.logSevere("connect to %s failure , %s", hostURL, e.getMessage());
            return null;
        }
        if (future.isSuccess() == true) {
            LoggerHelper.logInfo("remote %s connected.", hostURL);
            NetworkConnection conn = NetworkConnection.getConnection(future.channel());
            return new InnerRsfClient(this.getRequestManager(), conn);
        }
        //
        try {
            LoggerHelper.logSevere("connect to %s failure , %s", hostURL, future.cause().getMessage());
            future.channel().close().await();
        } catch (InterruptedException e) {
            LoggerHelper.logSevere("close connect(%s) failure , %s", hostURL, e.getMessage());
        }
        return null;
    }
}