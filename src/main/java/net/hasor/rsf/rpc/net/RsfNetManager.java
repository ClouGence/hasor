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
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import org.more.util.NameThreadFactory;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 维护RSF同其它RSF的连接，并提供数据投递和接收服务。
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment                       rsfEnvironment;
    private final ConcurrentMap<String, RsfNetChannel> channelMapping;
    private final EventLoopGroup                       workLoopGroup;
    private final NioEventLoopGroup                    listenLoopGroup;
    private final ReceivedListener                     receivedListener;
    private final Map<String, Connector>               bindListener;
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedListener receivedListener) {
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        this.channelMapping = new ConcurrentHashMap<String, RsfNetChannel>();
        this.bindListener = new HashMap<String, Connector>();
        //
        int workerThread = rsfSettings.getNetworkWorker();
        int listenerThread = rsfEnvironment.getSettings().getNetworkListener();
        this.workLoopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s", rsfEnvironment.getClassLoader()));
        this.listenLoopGroup = new NioEventLoopGroup(listenerThread, new NameThreadFactory("RSF-Listen-%s", rsfEnvironment.getClassLoader()));
        logger.info("nioEventLoopGroup, workerThread = {} , listenerThread = {}", workerThread, listenerThread);
        //
        this.rsfEnvironment = rsfEnvironment;
        this.receivedListener = receivedListener;
    }
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
    public InterAddress bindAddress(String protocol) {
        Connector connector = this.findConnector(protocol);
        if (connector != null)
            return connector.getBindAddress();
        return null;
    }
    public InterAddress getGatewayAddress(String protocol) {
        Connector connector = this.findConnector(protocol);
        if (connector != null)
            return connector.getGatewayAddress();
        return null;
    }
    //
    //
    /** 销毁 */
    public void shutdown() {
        logger.info("rsfNetManager, shutdownGracefully.");
        if (bindListener != null && !this.bindListener.isEmpty()) {
            for (Connector listener : bindListener.values()) {
                listener.shutdown();
            }
        }
        listenLoopGroup.shutdownGracefully();
        workLoopGroup.shutdownGracefully();
    }
    //
    /** 建立或获取和远程的连接。 */
    public RsfNetChannel getChannel(InterAddress target) {
        String hostPortKey = target.getHostPort();
        RsfNetChannel channel = this.channelMapping.get(hostPortKey);
        if (channel != null && !channel.isActive()) {
            this.channelMapping.remove(hostPortKey);// conect is bad.
            channel = null;
        }
        //
        synchronized (this) {
            channel = this.channelMapping.get(hostPortKey);
            if (channel != null && channel.isActive()) {
                return channel;
            }
            this.channelMapping.remove(hostPortKey);// conect is bad.
            channel = connSocket(target);//
            if (channel != null) {
                this.channelMapping.put(hostPortKey, channel);
            }
            return channel;
        }
    }
    /*连接到远程机器*/
    private RsfNetChannel connSocket(InterAddress hostAddress) {
        //
        this.logger.info("connect to {} ...", hostAddress);
        String sechma = hostAddress.getSechma();
        Connector connector = this.findConnector(sechma);// tips：例如：如果本地都不支持 rsf 协议，那么也没有必要连接远程的 rsf 协议。
        if (connector == null) {
            return null;
        }
        //
        RsfNetChannel channel = connector.connectionTo(hostAddress, this.workLoopGroup);
        if (channel == null) {
            this.logger.error("connect to {} failed.", hostAddress);
        }
        return channel;
    }
    //
    /** 启动RSF上配置的所有连接器。 */
    public void start() {
        RsfSettings settings = this.getRsfEnvironment().getSettings();
        String defaultProtocol = settings.getDefaultProtocol();
        Map<String, InterAddress> connectorSet = settings.getConnectorSet();
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
            Connector connector = new Connector(protocolKey, local, gateway);
            try {
                connector.startListener(this.listenLoopGroup, this.workLoopGroup);//启动连接器
            } catch (Throwable e) {
                this.logger.error("connector[{}] failed -> {}", protocolKey, e.getMessage(), e);
                if (StringUtils.equals(defaultProtocol, protocolKey)) {
                    throw new IllegalStateException("default connector start failed.", e);//默认连接器启动失败
                }
            }
        }
    }
    //
    ReceivedListener getReceivedListener() {
        return this.receivedListener;
    }
}