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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import net.hasor.core.AppContext;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.future.BasicFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
/**
 * RSF网络服务，并提供数据传出、传入，以及端口监听服务。
 * tips:支持多协议
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment         rsfEnvironment;    // RSF环境
    private final LinkPool               linkPool;          // 网络连接池，负责管理所有网络连接
    private final EventLoopGroup         workLoopGroup;     // I/O线程
    private final NioEventLoopGroup      listenLoopGroup;   // 监听线程
    private final RsfReceivedListener    receivedListener;  // 负责汇总所有来自底层网络的 RequestInfo、ResponseInfo消息
    private final Map<String, Connector> bindListener;      // 不同协议都有自己独立的‘RPC协议连接器’
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, RsfReceivedListener receivedListener) {
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        this.bindListener = new HashMap<String, Connector>();
        this.linkPool = new LinkPool(rsfEnvironment);
        //
        int workerThread = rsfSettings.getNetworkWorker();
        int listenerThread = rsfEnvironment.getSettings().getNetworkListener();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s", rsfEnvironment.getClassLoader()));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s", rsfEnvironment.getClassLoader()));
        this.logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        //
        this.rsfEnvironment = rsfEnvironment;
        this.receivedListener = receivedListener;
    }
    //
    //
    /** 环境对象 */
    public RsfEnvironment getRsfEnvironment() {
        return this.rsfEnvironment;
    }
    /**获取运行着哪些协议*/
    public Set<String> runProtocols() {
        return Collections.unmodifiableSet(this.bindListener.keySet());
    }
    /** 查找RPC连接器。 */
    public Connector findConnector(String protocol) {
        return this.bindListener.get(protocol);
    }
    //
    //
    /** 启动RSF上配置的所有连接器。*/
    public void start(AppContext appContext) {
        //
        this.linkPool.initPool();
        RsfSettings settings = this.getRsfEnvironment().getSettings();
        String defaultProtocol = settings.getDefaultProtocol();
        Map<String, InterAddress> connectorSet = settings.getBindAddressSet();
        Map<String, InterAddress> gatewaySet = settings.getGatewaySet();
        //
        for (Map.Entry<String, InterAddress> entry : connectorSet.entrySet()) {
            String protocolKey = entry.getKey();
            InterAddress local = entry.getValue();
            InterAddress gateway = gatewaySet.get(protocolKey);
            if (local.getPort() <= 0) {
                throw new IllegalStateException("[" + protocolKey + "] the prot is zero.");
            }
            if (gateway != null && gateway.getPort() <= 0) {
                throw new IllegalStateException("[" + protocolKey + "] the gateway prot is zero.");
            }
            //
            try {
                Connector connector = new Connector(appContext, protocolKey, local, gateway, this.receivedListener, this.linkPool, this.workLoopGroup);
                connector.startListener(this.listenLoopGroup);//启动连接器
                this.bindListener.put(protocolKey, connector);
            } catch (Throwable e) {
                this.logger.error("connector[{}] failed -> {}", protocolKey, e.getMessage(), e);
                if (defaultProtocol.equals(protocolKey)) {
                    throw new IllegalStateException("default connector start failed.", e);//默认连接器启动失败
                }
            }
        }
        //
    }
    /** 销毁 */
    public void shutdown() {
        logger.info("rsfNetManager, shutdownGracefully.");
        if (this.bindListener != null && !this.bindListener.isEmpty()) {
            for (Connector listener : this.bindListener.values()) {
                listener.shutdown();
            }
            this.bindListener.clear();
        }
        this.linkPool.destroyPool();
        this.listenLoopGroup.shutdownGracefully();
        this.workLoopGroup.shutdownGracefully();
    }
    //
    /** 建立或获取和远程的连接(异步+回调) */
    public Future<RsfChannel> getChannel(InterAddress target) throws InterruptedException {
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
        // .异步新建连接
        synchronized (this) {
            channelFuture = this.linkPool.findChannel(hostPort);
            if (channelFuture != null) {
                return channelFuture;
            }
            channelFuture = this.linkPool.preConnection(hostPort);
            String protocol = target.getSechma();
            Connector connector = this.findConnector(protocol);// tips：例如：如果本地都不支持 rsf 协议，那么也没有必要连接远程的 rsf 协议。
            if (connector == null) {
                this.logger.error("connect to {} failed. ", hostPort);
                channelFuture.failed(new RsfException(ProtocolStatus.ProtocolUndefined, "Connector Undefined for protocol " + protocol));
            } else {
                logger.info("connect to {} ...", hostPort);
                connector.connectionTo(target, channelFuture);
            }
        }
        return channelFuture;
    }
}