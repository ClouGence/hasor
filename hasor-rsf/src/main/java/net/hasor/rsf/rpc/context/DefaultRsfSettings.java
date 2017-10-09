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
import net.hasor.rsf.utils.StringUtils;
import net.hasor.utils.convert.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
/**
 *
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
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
    private   int                       networkWorker         = 2;
    private   int                       networkListener       = 1;
    //
    private   int                       queueMaxSize          = 4096;
    private   int                       queueMinPoolSize      = 1;
    private   int                       queueMaxPoolSize      = 7;
    private   long                      queueKeepAliveTime    = 300L;
    //
    private   String                    bindAddress           = "local";
    private   String                    defaultProtocol       = null;
    private   Map<String, InterAddress> bindAddressSet        = null;
    private   Map<String, InterAddress> gatewayAddressMap     = null;
    private   Map<String, String>       protocolHandlerMap    = null;
    //
    private   int                       consolePort           = 2180;
    private   String[]                  consoleInBound        = null;
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
    public int getNetworkWorker() {
        return this.networkWorker;
    }
    @Override
    public int getNetworkListener() {
        return this.networkListener;
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
    public Map<String, InterAddress> getBindAddressSet() {
        return Collections.unmodifiableMap(this.bindAddressSet);
    }
    @Override
    public Map<String, String> getProtocolHandlerMapping() {
        return Collections.unmodifiableMap(this.protocolHandlerMap);
    }
    @Override
    public Map<String, InterAddress> getGatewaySet() {
        return Collections.unmodifiableMap(this.gatewayAddressMap);
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
    @Override
    public int getConsolePort() {
        return this.consolePort;
    }
    @Override
    public String[] getConsoleInBoundAddress() {
        return this.consoleInBound;
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
        this.networkListener = getInteger("hasor.rsfConfig.network.listenThread", 1);
        this.networkWorker = getInteger("hasor.rsfConfig.network.workerThread", 2);
        //
        this.queueMaxSize = getInteger("hasor.rsfConfig.queue.maxSize", 4096);
        this.queueMinPoolSize = getInteger("hasor.rsfConfig.queue.minPoolSize", 1);
        this.queueMaxPoolSize = getInteger("hasor.rsfConfig.queue.maxPoolSize", 7);
        this.queueKeepAliveTime = getLong("hasor.rsfConfig.queue.keepAliveTime", 300L);
        //
        String bindAddress = getString("hasor.rsfConfig.address", "local");
        InetAddress inetAddress = NetworkUtils.finalBindAddress(bindAddress);
        this.bindAddress = inetAddress.getHostAddress();
        this.defaultProtocol = getString("hasor.rsfConfig.connectorSet.default", "RSF/1.0");
        this.protocolHandlerMap = new HashMap<String, String>();
        XmlNode[] protocolSetNode = getXmlNodeArray("hasor.rsfConfig.protocolSet");
        if (protocolSetNode != null) {
            for (XmlNode protocolNode : protocolSetNode) {
                this.parseProtocol(this.protocolHandlerMap, protocolNode);
            }
        }
        this.parseProtocol(this.protocolHandlerMap, getXmlNode("hasor.rsfConfig.protocolSet"));
        //
        //
        this.bindAddressSet = new HashMap<String, InterAddress>();
        this.gatewayAddressMap = new HashMap<String, InterAddress>();
        XmlNode[] connectorArrays = getXmlNodeArray("hasor.rsfConfig.connectorSet.connector");
        if (connectorArrays != null) {
            for (XmlNode connectorNode : connectorArrays) {
                String protocol = connectorNode.getAttribute("protocol");
                String localPort = connectorNode.getAttribute("localPort");
                String gatewayHost = connectorNode.getAttribute("gatewayAddress");
                String gatewayPort = connectorNode.getAttribute("gatewayPort");
                //
                if (StringUtils.isBlank(protocol))
                    continue;
                int localPortInt = 0;
                int gatewayPortInt = 0;
                if (StringUtils.isNotBlank(localPort))
                    localPortInt = (Integer) ConverterUtils.convert(Integer.TYPE, localPort);
                if (StringUtils.isNotBlank(gatewayPort))
                    gatewayPortInt = (Integer) ConverterUtils.convert(Integer.TYPE, gatewayPort);
                //
                if (localPortInt <= 0)
                    continue;
                InterAddress localAddress = new InterAddress(protocol, this.bindAddress, localPortInt, this.unitName);
                this.bindAddressSet.put(protocol, localAddress);
                //
                if (gatewayPortInt <= 0 || StringUtils.isBlank(gatewayHost))
                    continue;
                InetAddress gatewayInetAddress = NetworkUtils.finalBindAddress(gatewayHost);
                InterAddress gatewayAddress = new InterAddress(protocol, gatewayInetAddress.getHostAddress(), gatewayPortInt, this.unitName);
                this.gatewayAddressMap.put(protocol, gatewayAddress);
            }
        }
        //
        this.consolePort = getInteger("hasor.rsfConfig.console.port", 2180);
        String consoleInBoundStr = getString("hasor.rsfConfig.console.inBound", "local");
        ArrayList<String> addressList = new ArrayList<String>();
        if (StringUtils.isNotBlank(consoleInBoundStr)) {
            for (String item : consoleInBoundStr.split(",")) {
                String itemTrim = item.trim();
                if (StringUtils.isNotBlank(itemTrim)) {
                    try {
                        if ("local".equalsIgnoreCase(itemTrim)) {
                            addressList.add(NetworkUtils.finalBindAddress("local").getHostAddress());
                        } else {
                            addressList.add(itemTrim);
                        }
                    } catch (Exception e) {
                        logger.error("console - inBound address " + itemTrim + " error " + e.getMessage(), e);
                    }
                }
            }
        }
        if (addressList.isEmpty()) {
            try {
                addressList.add(NetworkUtils.finalBindAddress("local").getHostAddress());
            } catch (Exception e) {
                addressList.add("127.0.0.1");
            }
        }
        this.consoleInBound = addressList.toArray(new String[addressList.size()]);
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
    private void parseProtocol(Map<String, String> implementorMap, XmlNode protocolSetNode) {
        List<XmlNode> protocolSet = protocolSetNode.getChildren("protocol");
        if (protocolSet == null) {
            return;
        }
        for (XmlNode protocolNode : protocolSet) {
            String name = protocolNode.getAttribute("name");
            String implementor = protocolNode.getAttribute("implementor");
            if (StringUtils.isBlank(name) || StringUtils.isBlank(implementor))
                continue;
            //
            implementorMap.put(name, implementor);
        }
    }
}