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
import net.hasor.rsf.transform.protocol.OptionInfo;
import net.hasor.rsf.utils.NetworkUtils;
import org.more.convert.ConverterUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
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
    private   String                    defaultSerializeType  = "Hessian";
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
    private   Map<String, InterAddress> connectorSet          = null;
    private   Map<String, InterAddress> gatewayAddressMap     = null;
    //
    private   InterAddress[]            centerServerSet       = new InterAddress[0];
    private   int                       centerRsfTimeout      = 6000;
    private   int                       centerHeartbeatTime   = 15000;
    private   boolean                   enableCenter          = false;
    //
    private   int                       consolePort           = 2181;
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
    private   String                    wrapperType           = null;
    //
    private   String                    appKeyID              = null;
    private   String                    appKeySecret          = null;
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
    public String getVersion() {
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-core.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            return !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            return null;
        }
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
    public InterAddress[] getCenterServerSet() {
        return this.centerServerSet.clone();
    }
    public int getCenterRsfTimeout() {
        return this.centerRsfTimeout;
    }
    public int getCenterHeartbeatTime() {
        return this.centerHeartbeatTime;
    }
    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    @Override
    public String getWrapperType() {
        return this.wrapperType;
    }
    @Override
    public String getBindAddress() {
        return this.bindAddress;
    }
    @Override
    public Map<String, InterAddress> getConnectorSet() {
        return Collections.unmodifiableMap(this.connectorSet);
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
    public boolean isEnableCenter() {
        return this.enableCenter;
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
    @Override
    public String getAppKeyID() {
        return this.appKeyID;
    }
    @Override
    public String getAppKeySecret() {
        return this.appKeySecret;
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
        //
        this.connectorSet = new HashMap<String, InterAddress>();
        this.gatewayAddressMap = new HashMap<String, InterAddress>();
        //
        Properties protocolSchemas = new Properties();
        List<InputStream> streamList = ResourcesUtils.getResourcesAsStream("/META-INF/rsf-protocol.schemas");
        if (streamList != null) {
            for (InputStream inStream : streamList)
                protocolSchemas.load(inStream);
        }
        XmlNode[] connectorArrays = getXmlNodeArray("hasor.rsfConfig.connectorSet.connector");
        if (connectorArrays != null) {
            for (XmlNode connectorNode : connectorArrays) {
                String protocol = connectorNode.getAttribute("protocol");
                String localPort = connectorNode.getAttribute("localPort");
                String gatewayHost = connectorNode.getAttribute("gatewayAddress");
                String gatewayPort = connectorNode.getAttribute("gatewayPort");
                //
                String sechma = (String) protocolSchemas.get(protocol);
                if (StringUtils.isBlank(sechma))
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
                InterAddress localAddress = new InterAddress(sechma, this.bindAddress, localPortInt, this.unitName);
                this.connectorSet.put(protocol, localAddress);
                //
                if (gatewayPortInt <= 0 || StringUtils.isBlank(gatewayHost))
                    continue;
                InetAddress gatewayInetAddress = NetworkUtils.finalBindAddress(gatewayHost);
                InterAddress gatewayAddress = new InterAddress(sechma, gatewayInetAddress.getHostAddress(), gatewayPortInt, this.unitName);
                this.gatewayAddressMap.put(protocol, gatewayAddress);
            }
        }
        //
        XmlNode[] centerServerArrays = getXmlNodeArray("hasor.rsfConfig.centerServers.server");
        if (centerServerArrays != null) {
            ArrayList<InterAddress> addressArrays = new ArrayList<InterAddress>();
            for (XmlNode centerServer : centerServerArrays) {
                String serverURL = centerServer.getText();
                if (StringUtils.isNotBlank(serverURL)) {
                    serverURL = serverURL.trim();
                    try {
                        if (!InterAddress.checkFormat(new URI(serverURL))) {
                            serverURL = serverURL + "/default";
                            if (!InterAddress.checkFormat(new URI(serverURL))) {
                                logger.error("centerServer {} format error.", centerServer.getText());
                                continue;
                            }
                        }
                        InterAddress interAddress = new InterAddress(serverURL);
                        if (!addressArrays.contains(interAddress)) {
                            addressArrays.add(interAddress);
                        }
                    } catch (Exception e) {
                        logger.error("centerServer {} format error -> {}.", centerServer.getText(), e.getMessage());
                    }
                }
            }
            this.centerServerSet = addressArrays.toArray(new InterAddress[addressArrays.size()]);
        }
        this.centerRsfTimeout = getInteger("hasor.rsfConfig.centerServers.timeout", 6000);
        this.centerHeartbeatTime = getInteger("hasor.rsfConfig.centerServers.heartbeatTime", 15000);
        //
        this.consolePort = getInteger("hasor.rsfConfig.console.port", 2181);
        String consoleInBoundStr = getString("hasor.rsfConfig.console.inBound", "local");
        ArrayList<String> addressList = new ArrayList<String>();
        if (StringUtils.isNotBlank(consoleInBoundStr)) {
            for (String item : consoleInBoundStr.split(",")) {
                String itemTrim = item.trim();
                if (StringUtils.isNotBlank(itemTrim)) {
                    try {
                        if (StringUtils.equalsIgnoreCase("local", itemTrim)) {
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
        this.enableCenter = this.centerServerSet.length != 0;
        this.automaticOnline = getBoolean("hasor.rsfConfig.centerServers.automaticOnline", true);
        this.wrapperType = getString("hasor.rsfConfig.client.wrapperType", "fast");//默认使用快速的
        //
        this.appKeyID = getString("security.appKeyID");
        this.appKeySecret = getString("security.appKeySecret");
        //
        logger.info("loadRsfConfig complete!");
    }
}