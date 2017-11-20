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
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
/**
 * RPC协议连接器，负责创建某个特定RPC协议的网络事件。
 * @version : 2017年01月16日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class Connector {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final String             protocol;        // 协议名，例如：RSF/1.0、Hprose/HTTP
    private final String             sechma;          // 协议头，例如：rsf、hprose
    private final RsfEnvironment     rsfEnvironment;  // Rsf环境
    private final InterAddress       bindAddress;     // 网络通信地址
    private final InterAddress       gatewayAddress;  // 网络通信地址(网关)
    private final LinkPool           linkPool;        // 连接池
    private final ReceivedListener   receivedListener;// 数据接收器
    private final ConnectionAccepter accepter;        // 连接接受器，用于IP黑名单实现
    //
    public Connector(String protocol, RsfEnvironment rsfEnvironment, ReceivedListener receivedListener, ConnectionAccepter accepter) {
        this.protocol = protocol;
        this.rsfEnvironment = rsfEnvironment;
        this.linkPool = new LinkPool(rsfEnvironment);
        this.linkPool.initPool();
        this.receivedListener = receivedListener;
        this.accepter = accepter;
        //
        RsfSettings settings = rsfEnvironment.getSettings();
        String configKey = settings.getProtocolConfigKey(protocol);
        this.sechma = settings.getString(configKey + ".protocol");
        this.bindAddress = settings.getBindAddressSet(protocol);
        this.gatewayAddress = settings.getGatewaySet(protocol);
        if (this.bindAddress.getPort() <= 0) {
            throw new IllegalStateException("[" + protocol + "] the prot is zero.");
        }
        if (this.gatewayAddress != null && this.gatewayAddress.getPort() <= 0) {
            throw new IllegalStateException("[" + protocol + "] the gateway prot is zero.");
        }
    }
    //
    @Override
    public String toString() {
        InterAddress local = this.gatewayAddress;
        local = (local == null) ? this.bindAddress : local;
        return "Connector{ protocol='" + protocol + "', bindAddress=" + local + '}';
    }
    //
    /** 获取协议名 */
    public String getProtocol() {
        return this.protocol;
    }
    /** 获取协议头 */
    public String getSechma() {
        return this.sechma;
    }
    public RsfEnvironment getRsfEnvironment() {
        return rsfEnvironment;
    }
    /** 监听的本地端口号 */
    public InterAddress getBindAddress() {
        return this.bindAddress;
    }
    /** 如果工作在内网，这里返回配置的外网映射地址 */
    public InterAddress getGatewayAddress() {
        return this.gatewayAddress;
    }
    /** 获取RSF运行的网关地址（如果有），或者本地绑定地址。 */
    public InterAddress getPublishAddress() {
        InterAddress address = getGatewayAddress();
        if (address == null) {
            address = getBindAddress();
        }
        return address;
    }
    //
    /**根据主机ip和端口号查找 RsfChannel*/
    public Future<RsfChannel> findRsfChannelByHostPort(String hostPort) {
        return this.linkPool.findChannel(hostPort);
    }
    /** 建立或获取和远程的连接(异步+回调) */
    public Future<RsfChannel> getOrConnectionTo(InterAddress target) throws InterruptedException {
        String protocol = target.getSechma();
        if (!this.sechma.equalsIgnoreCase(protocol)) {
            throw new RsfException(ProtocolStatus.ProtocolError, "sechma not match.");
        }
        //
        // .查找连接，并确定已有连接是否有效
        String hostPort = target.getHostPort();
        BasicFuture<RsfChannel> channelFuture = this.linkPool.findChannel(hostPort);
        if (channelFuture != null && channelFuture.isDone()) {
            RsfChannel channel = null;
            try {
                channel = channelFuture.get();
                if (channel != null && !channel.isActive()) {
                    this.linkPool.closeConnection(hostPort);// 连接已经失效，需要重新连接
                    channelFuture = null;
                }
            } catch (Exception e) {
                this.linkPool.closeConnection(hostPort);// 连接失败
                channelFuture = null;
            }
        }
        if (channelFuture != null) {
            return channelFuture;
        }
        //
        // .新建连接
        synchronized (this) {
            channelFuture = this.linkPool.findChannel(hostPort);    // 查找连接，有可能当进入到同步块中时已经创建了相同的连接。
            if (channelFuture != null) {
                return channelFuture;
            }
            channelFuture = this.linkPool.preConnection(hostPort);  // 根据host+prot 创建一个 Future，并启动一个超时的定时器。当超时到达时仍然没有建立连接那么引发error。
            //
            logger.info("connect to {} ...", hostPort);
            this.connectionTo(target, channelFuture);               // 连接到目标地址
        }
        return channelFuture;
    }
    //
    //
    /** 是否允许接入，IP黑名单实现 */
    protected boolean acceptChannel(RsfChannel rsfChannel) throws Exception {
        // .检查当前连接是否被允许接入，如果不允许接入关闭这个连接
        if (this.accepter.acceptIn(rsfChannel)) {
            String hostPort = rsfChannel.getTarget().getHostPort();
            BasicFuture<RsfChannel> future = this.linkPool.preConnection(hostPort);
            if (!future.isDone()) {
                future.completed(rsfChannel);
            }
            if (rsfChannel.equalsSameAs(future.get())) {
                future.get().addListener(this.receivedListener);
                future.get().onClose(new CloseListener(this.linkPool));
                return true;
            }
            // 理论上不应该出现同一个 hostPort 对应两个 RsfChannel 的情况。
            RsfException rsfException = new RsfException(ProtocolStatus.NetworkError, "the same port, multiple connected to the request. -> " + hostPort);
            future.failed(rsfException);
            throw rsfException;
        } else {
            this.logger.warn("connection[{}] refused form {} ,", this.getProtocol(), rsfChannel.getTarget().getHostPort());
            return false;
        }
    }
    //
    /**停止监听器*/
    public final void shutdown() {
        this.shutdownListener();
        this.linkPool.destroyPool();
    }
    /**
     * 启动本地监听器
     */
    public abstract void startListener(AppContext appContext);

    /**
     * 停止本地监听器
     */
    public abstract void shutdownListener();

    /**
     * 连接到远程机器
     */
    protected abstract void connectionTo(InterAddress target, BasicFuture<RsfChannel> channelFuture);
}