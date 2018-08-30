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
package net.hasor.rsf.rpc.context;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.SettingsWrap;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 *
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefaultRsfSettings extends SettingsWrap implements RsfSettings {
    protected Logger                    logger                = LoggerFactory.getLogger(getClass());
    private   int                       defaultTimeout        = 6000;
    private   String                    defaultGroup          = "RSF";
    private   String                    defaultVersion        = "1.0.0";
    private   String                    defaultSerializeType  = "Hprose";
    //
    private   OptionInfo                serverOptionManager   = new OptionInfo();
    private   OptionInfo                clientOptionManager   = new OptionInfo();
    //
    private   int                       queueMaxSize          = 4096;
    private   int                       queueMinPoolSize      = 1;
    private   int                       queueMaxPoolSize      = 7;
    private   long                      queueKeepAliveTime    = 300L;
    //
    private   String                    bindAddress           = "local";
    private   String                    defaultProtocol       = null;
    private   Map<String, String>       connectorSet          = null;
    private   Map<String, InterAddress> bindAddressSet        = null;
    //
    private   int                       requestTimeout        = 6000;
    private   int                       maximumRequest        = 200;
    private   SendLimitPolicy           sendLimitPolicy       = SendLimitPolicy.Reject;
    private   int                       connectTimeout        = 100;
    private   String                    unitName              = "default";
    private   long                      invalidWaitTime       = 30000;
    private   long                      refreshCacheTime      = 360000;
    private   boolean                   localDiskCache        = true;
    private   long                      diskCacheTimeInterval = 3600000;
    private   boolean                   automaticOnline       = true;
    //
    //
    public DefaultRsfSettings(Settings settings) throws IOException {
        super(settings);
        this.refreshRsfConfig();
    }
    //
    @Override
    public int getDefaultTimeout() {
        return this.defaultTimeout;
    }
    @Override
    public RsfOptionSet getServerOption() {
        return this.serverOptionManager;
    }
    @Override
    public RsfOptionSet getClientOption() {
        return this.clientOptionManager;
    }
    @Override
    public String getDefaultGroup() {
        return this.defaultGroup;
    }
    @Override
    public String getDefaultVersion() {
        return this.defaultVersion;
    }
    @Override
    public String getDefaultSerializeType() {
        return this.defaultSerializeType;
    }
    @Override
    public int getQueueMaxSize() {
        return this.queueMaxSize;
    }
    @Override
    public int getQueueMinPoolSize() {
        return this.queueMinPoolSize;
    }
    @Override
    public int getQueueMaxPoolSize() {
        return this.queueMaxPoolSize;
    }
    @Override
    public long getQueueKeepAliveTime() {
        return this.queueKeepAliveTime;
    }
    @Override
    public int getRequestTimeout() {
        return this.requestTimeout;
    }
    @Override
    public int getMaximumRequest() {
        return this.maximumRequest;
    }
    @Override
    public SendLimitPolicy getSendLimitPolicy() {
        return this.sendLimitPolicy;
    }
    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    @Override
    public String getBindAddress() {
        return this.bindAddress;
    }
    @Override
    public String getDefaultProtocol() {
        return this.defaultProtocol;
    }
    @Override
    public Set<String> getProtocos() {
        return Collections.unmodifiableSet(this.connectorSet.keySet());
    }
    @Override
    public InterAddress getBindAddressSet(String protocolName) {
        return this.bindAddressSet.get(protocolName);
    }
    @Override
    public String getProtocolConfigKey(String protocolName) {
        return this.connectorSet.get(protocolName);
    }
    @Override
    public String getUnitName() {
        return this.unitName;
    }
    @Override
    public long getInvalidWaitTime() {
        return this.invalidWaitTime;
    }
    @Override
    public long getRefreshCacheTime() {
        return this.refreshCacheTime;
    }
    @Override
    public long getDiskCacheTimeInterval() {
        return this.diskCacheTimeInterval;
    }
    @Override
    public boolean islocalDiskCache() {
        return this.localDiskCache;
    }
    @Override
    public boolean isAutomaticOnline() {
        return this.automaticOnline;
    }
    //
    public void refresh() throws IOException {
        super.refresh();
        this.refreshRsfConfig();
    }
    public void refreshRsfConfig() throws IOException {
        this.defaultGroup = getString("hasor.rsfConfig.defaultServiceValue.group", "RSF");
        this.defaultVersion = getString("hasor.rsfConfig.defaultServiceValue.version", "1.0.0");
        this.defaultTimeout = getInteger("hasor.rsfConfig.defaultServiceValue.timeout", 6000);
        this.defaultSerializeType = getString("hasor.rsfConfig.serializeType.default", "Hessian");
        //
        XmlNode[] serverOptSetArray = getXmlNodeArray("hasor.rsfConfig.serverOptionSet");
        if (serverOptSetArray != null) {
            for (XmlNode optSet : serverOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (!StringUtils.isBlank(key)) {
                        this.serverOptionManager.addOption(key, var);
                    }
                }
            }
        }
        XmlNode[] clientOptSetArray = getXmlNodeArray("hasor.rsfConfig.clientOptionSet");
        if (clientOptSetArray != null) {
            for (XmlNode optSet : clientOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (!StringUtils.isBlank(key)) {
                        this.clientOptionManager.addOption(key, var);
                    }
                }
            }
        }
        //
        this.queueMaxSize = getInteger("hasor.rsfConfig.queue.maxSize", 4096);
        this.queueMinPoolSize = getInteger("hasor.rsfConfig.queue.minPoolSize", 1);
        this.queueMaxPoolSize = getInteger("hasor.rsfConfig.queue.maxPoolSize", 7);
        this.queueKeepAliveTime = getLong("hasor.rsfConfig.queue.keepAliveTime", 300L);
        //
        String bindAddress = getString("hasor.rsfConfig.address", "local");
        InetAddress inetAddress = NetworkUtils.finalBindAddress(bindAddress);
        this.bindAddress = inetAddress.getHostAddress();
        this.bindAddressSet = new HashMap<String, InterAddress>();
        Map<String, String> connectorTmpSet = new HashMap<String, String>();
        XmlNode[] connectorRoot = getXmlNodeArray("hasor.rsfConfig.connectorSet");
        if (connectorRoot != null) {
            for (XmlNode connectorSet : connectorRoot) {
                if (connectorSet == null || connectorSet.getChildren().isEmpty()) {
                    continue;
                }
                for (XmlNode connector : connectorSet.getChildren()) {
                    connectorTmpSet.put(connector.getName(), "hasor.rsfConfig.connectorSet." + connector.getName());
                }
            }
        }
        //
        this.connectorSet = new HashMap<String, String>();
        for (String connectorName : connectorTmpSet.keySet()) {
            String basePath = connectorTmpSet.get(connectorName);
            String name = this.getString(basePath + ".name");
            String protocol = this.getString(basePath + ".protocol");
            if (StringUtils.isBlank(protocol) || StringUtils.isBlank(name))
                continue;
            if (this.connectorSet.containsKey(name))
                throw new IOException("repeat connector config error , name is " + name);
            //
            // .先解析端口和地址
            int localPort = this.getInteger(basePath + ".localPort", 0);
            if (localPort <= 0)
                continue;
            InterAddress localAddress = new InterAddress(protocol, this.bindAddress, localPort, this.unitName);
            // .解析没问哦在放到connectorSet中
            this.connectorSet.put(name, basePath);
            this.bindAddressSet.put(name, localAddress);
        }
        //
        // .确保有默认的协议可用
        this.defaultProtocol = getString("hasor.rsfConfig.connectorSet.default");
        if (!this.bindAddressSet.containsKey(this.defaultProtocol)) {
            throw new IOException("default protocol missing config.");
        }
        //
        //
        this.requestTimeout = getInteger("hasor.rsfConfig.client.defaultTimeout", 6000);
        this.maximumRequest = getInteger("hasor.rsfConfig.client.maximumRequest", 200);
        this.sendLimitPolicy = getEnum("hasor.rsfConfig.client.sendLimitPolicy", SendLimitPolicy.class, SendLimitPolicy.Reject);
        this.connectTimeout = getInteger("hasor.rsfConfig.client.connectTimeout", 100);
        //
        this.unitName = getString("hasor.rsfConfig.unitName", "local");
        this.refreshCacheTime = getLong("hasor.rsfConfig.addressPool.refreshCacheTime", 60000L);
        this.invalidWaitTime = getLong("hasor.rsfConfig.addressPool.invalidWaitTime", 120000L);
        this.localDiskCache = getBoolean("hasor.rsfConfig.addressPool.localDiskCache", true);
        this.diskCacheTimeInterval = getLong("hasor.rsfConfig.addressPool.diskCacheTimeInterval", 3600000L);
        //
        this.automaticOnline = getBoolean("hasor.rsfConfig.automaticOnline", true);
        this.logger.info("loadRsfConfig complete!");
    }
}