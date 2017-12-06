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
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.OptionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
/**
 * RSF网络服务，并提供数据传出、传入，以及端口监听服务。
 * tips:支持多协议
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment            rsfEnvironment;     // RSF环境
    private final ReceivedAdapter           receivedAdapter;    // 负责汇总所有来自底层网络的 RequestInfo、ResponseInfo消息
    private final Map<String, Connector>    protocolConnector;  // 不同协议都有自己独立的‘RPC协议连接器’
    private final Map<String, List<String>> sechmaMapping;      // 不同协议都有自己独立的‘RPC协议连接器’
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedAdapter receivedAdapter) {
        this.protocolConnector = new HashMap<String, Connector>();
        this.sechmaMapping = new HashMap<String, List<String>>();
        this.rsfEnvironment = rsfEnvironment;
        this.receivedAdapter = receivedAdapter;
    }
    //
    /** 环境对象 */
    public RsfEnvironment getRsfEnvironment() {
        return this.rsfEnvironment;
    }
    /**获取运行着哪些协议*/
    public Set<String> runProtocols() {
        return Collections.unmodifiableSet(this.protocolConnector.keySet());
    }
    /** 查找RPC连接器。 */
    public Connector findConnector(String protocol) {
        return this.protocolConnector.get(protocol);
    }
    public Connector findConnectorBySechma(String sechma) {
        if (!this.sechmaMapping.containsKey(sechma)) {
            return null;
        }
        List<String> protocolNames = this.sechmaMapping.get(sechma);
        return this.findConnector(protocolNames.get(0));
    }
    //
    /** 启动RSF上配置的所有连接器(传入方向)。*/
    public void start(AppContext appContext) {
        //
        RsfSettings settings = this.getRsfEnvironment().getSettings();
        String defaultProtocol = settings.getDefaultProtocol();
        Set<String> protocolSet = settings.getProtocos();
        //
        for (String protocol : protocolSet) {
            // .Sechma 注册
            String configKey = settings.getProtocolConfigKey(protocol);
            String sechmaName = settings.getString(configKey + ".protocol");
            List<String> sechmaMapping = this.sechmaMapping.get(sechmaName);
            if (sechmaMapping == null) {
                sechmaMapping = new ArrayList<String>();
                this.sechmaMapping.put(sechmaName, sechmaMapping);
            }
            if (sechmaMapping.contains(protocol)) {
                this.logger.error("connector[{}] failed -> repeat.", protocol);
                if (defaultProtocol.equals(protocol)) {
                    throw new IllegalStateException("default connector start failed. " + protocol + "-> repeat protocol.");
                }
            }
            //
            try {
                String connectorFactory = settings.getString(configKey + ".factory");
                Class<?> factoryClass = appContext.getClassLoader().loadClass(connectorFactory);
                ConnectorFactory factory = (ConnectorFactory) appContext.getInstance(factoryClass);
                Connector connector = factory.create(protocol, appContext, new ReceivedListener() {
                    @Override
                    public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) {
                        receivedAdapter.receivedMessage(rsfChannel, info);
                    }
                }, new ConnectionAccepter() {
                    @Override
                    public boolean acceptIn(RsfChannel rsfChannel) throws IOException {
                        return acceptChannel(rsfChannel);
                    }
                });
                //
                if (connector == null) {
                    this.logger.info("connector[{}] disable, connector is null.", protocol);
                    continue;
                }
                // .启动连接器
                connector.startListener(appContext);
                sechmaMapping.add(protocol);
                this.protocolConnector.put(protocol, connector);
            } catch (Throwable e) {
                this.logger.error("connector[{}] failed -> {}", protocol, e.getMessage(), e);
                if (defaultProtocol.equals(protocol)) {
                    throw new IllegalStateException("default connector start failed.", e);//默认连接器启动失败
                }
            }
        }
        //
        if (this.findConnector(defaultProtocol) == null) {
            try {
                this.logger.error("start failed , default {} protocol failed", defaultProtocol);
                throw new IllegalStateException("start failed , default " + defaultProtocol + " protocol failed");
            } finally {
                shutdown();// 一定要做清理
            }
        }
    }
    //
    protected boolean acceptChannel(RsfChannel rsfChannel) throws IOException {
        return true;
    }
    /** 销毁 */
    public void shutdown() {
        logger.info("rsfNetManager, shutdownGracefully.");
        if (!this.protocolConnector.isEmpty()) {
            for (Connector listener : this.protocolConnector.values()) {
                listener.shutdown();
            }
            this.protocolConnector.clear();
            this.sechmaMapping.clear();
        }
    }
}