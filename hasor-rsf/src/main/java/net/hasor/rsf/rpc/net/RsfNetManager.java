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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * RSF网络服务，并提供数据传出、传入，以及端口监听服务。
 * tips:支持多协议
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfNetManager {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private final RsfEnvironment         rsfEnvironment;    // RSF环境
    private final ReceivedAdapter        receivedAdapter;   // 负责汇总所有来自底层网络的 RequestInfo、ResponseInfo消息
    private final Map<String, Connector> bindConnector;     // 不同协议都有自己独立的‘RPC协议连接器’
    //
    public RsfNetManager(RsfEnvironment rsfEnvironment, ReceivedAdapter receivedAdapter) {
        this.bindConnector = new HashMap<String, Connector>();
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
        return Collections.unmodifiableSet(this.bindConnector.keySet());
    }
    /** 查找RPC连接器。 */
    public Connector findConnector(String protocol) {
        return this.bindConnector.get(protocol);
    }
    //
    /** 启动RSF上配置的所有连接器(传入方向)。*/
    public void start(AppContext appContext) {
        //
        RsfSettings settings = this.getRsfEnvironment().getSettings();
        String defaultProtocol = settings.getDefaultProtocol();
        String[] protocolArrays = settings.getProtocos();
        //
        for (String protocol : protocolArrays) {
            try {
                String configKey = settings.getProtocolConfigKey(protocol);
                String connectorFactory = settings.getString(configKey + ".factory");
                Class<?> factoryClass = appContext.getClassLoader().loadClass(connectorFactory);
                ConnectorFactory factory = (ConnectorFactory) appContext.getInstance(factoryClass);
                Connector connector = factory.create(protocol, appContext, new ReceivedListener() {
                    @Override
                    public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) throws IOException {
                        receivedAdapter.receivedMessage(rsfChannel, info);
                    }
                }, new ConnectionAccepter() {
                    @Override
                    public boolean acceptIn(RsfChannel rsfChannel) throws IOException {
                        return acceptChannel(rsfChannel);
                    }
                });
                connector.startListener(appContext);//启动连接器
                this.bindConnector.put(protocol, connector);
            } catch (Throwable e) {
                this.logger.error("connector[{}] failed -> {}", protocol, e.getMessage(), e);
                if (defaultProtocol.equals(protocol)) {
                    throw new IllegalStateException("default connector start failed.", e);//默认连接器启动失败
                }
            }
        }
        //
    }
    //
    protected boolean acceptChannel(RsfChannel rsfChannel) throws IOException {
        return true;
    }
    /** 销毁 */
    public void shutdown() {
        logger.info("rsfNetManager, shutdownGracefully.");
        if (!this.bindConnector.isEmpty()) {
            for (Connector listener : this.bindConnector.values()) {
                listener.shutdown();
            }
            this.bindConnector.clear();
        }
    }
}