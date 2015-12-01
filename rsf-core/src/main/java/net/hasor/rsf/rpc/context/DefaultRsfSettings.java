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
package net.hasor.rsf.rpc.context;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.SettingsWarp;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.rpc.manager.OptionManager;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfSettings extends SettingsWarp implements RsfSettings {
    protected Logger        logger               = LoggerFactory.getLogger(getClass());
    private int             defaultTimeout       = 6000;
    private String          defaultGroup         = "RSF";
    private String          defaultVersion       = "1.0.0";
    private String          defaultSerializeType = "Hessian";
    //
    private OptionManager   serverOptionManager  = new OptionManager();
    private OptionManager   clientOptionManager  = new OptionManager();
    //
    private int             networkWorker        = 2;
    private int             networkListener      = 1;
    //
    private int             queueMaxSize         = 4096;
    private int             queueMinPoolSize     = 1;
    private int             queueMaxPoolSize     = 7;
    private long            queueKeepAliveTime   = 300L;
    //
    private int             requestTimeout       = 6000;
    private int             maximumRequest       = 200;
    private SendLimitPolicy sendLimitPolicy      = SendLimitPolicy.Reject;
    private int             connectTimeout       = 100;
    //
    private String          bindAddress          = "local";
    private int             bindPort             = 8000;
    //
    private String          centerAddress        = "local";
    private int             centerPort           = 8000;
    private int             centerInterval       = 60000;
    //
    private String          unitName             = "local";
    private int             invalidWaitTime      = 30000;
    private long            refreshCacheTime     = 360000;
    private boolean         localDiskCache       = true;
    //
    //
    //
    public DefaultRsfSettings(Settings settings) throws IOException {
        super(settings);
        this.loadRsfConfig();
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
    public byte getProtocolVersion() {
        return RSFConstants.Version_1;
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
    public String getBindAddress() {
        return this.bindAddress;
    }
    @Override
    public int getBindPort() {
        return this.bindPort;
    }
    @Override
    public String getCenterAddress() {
        return this.centerAddress;
    }
    @Override
    public int getCenterPort() {
        return this.centerPort;
    }
    @Override
    public int getCenterInterval() {
        return this.centerInterval;
    }
    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    @Override
    public String getUnitName() {
        return this.unitName;
    }
    @Override
    public int getInvalidWaitTime() {
        return this.invalidWaitTime;
    }
    @Override
    public long getRefreshCacheTime() {
        return this.refreshCacheTime;
    }
    @Override
    public boolean islocalDiskCache() {
        return this.localDiskCache;
    }
    //
    public OptionManager getServerOptionManager() {
        return serverOptionManager;
    }
    //
    public void refresh() throws IOException {
        super.refresh();
        this.loadRsfConfig();
    }
    protected void loadRsfConfig() throws IOException {
        this.defaultGroup = getString("hasor.rsfConfig.defaultServiceValue.group", "RSF");
        this.defaultVersion = getString("hasor.rsfConfig.defaultServiceValue.version", "1.0.0");
        this.defaultTimeout = getInteger("hasor.rsfConfig.defaultServiceValue.timeout", 6000);
        this.defaultSerializeType = getString("hasor.rsfConfig.serializeType.default", "Hessian");
        //
        XmlNode[] clientOptSetArray = getXmlNodeArray("hasor.rsfConfig.clientOptionSet");
        if (clientOptSetArray != null) {
            for (XmlNode optSet : clientOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (StringUtils.isBlank(key) == false) {
                        this.clientOptionManager.addOption(key, var);
                    }
                }
            }
        }
        XmlNode[] serverOptSetArray = getXmlNodeArray("hasor.rsfConfig.serverOptionSet");
        if (serverOptSetArray != null) {
            for (XmlNode optSet : serverOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (StringUtils.isBlank(key) == false) {
                        this.serverOptionManager.addOption(key, var);
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
        this.requestTimeout = getInteger("hasor.rsfConfig.client.defaultTimeout", 6000);
        this.maximumRequest = getInteger("hasor.rsfConfig.client.maximumRequest", 200);
        this.sendLimitPolicy = getEnum("hasor.rsfConfig.client.sendLimitPolicy", SendLimitPolicy.class, SendLimitPolicy.Reject);
        this.connectTimeout = getInteger("hasor.rsfConfig.client.connectTimeout", 100);
        //
        this.bindAddress = getString("hasor.rsfConfig.address", "local");
        this.bindPort = getInteger("hasor.rsfConfig.port", 8000);
        //
        this.centerAddress = getString("hasor.rsfConfig.centerServer.address", "local");
        this.centerPort = getInteger("hasor.rsfConfig.centerServer.port", 8000);
        this.centerInterval = getInteger("hasor.rsfConfig.centerServer.interval", 60000);;
        //
        this.unitName = getString("hasor.rsfConfig.unitName", "local");
        this.invalidWaitTime = getInteger("hasor.rsfConfig.addressPool.invalidWaitTime", 60000);
        this.refreshCacheTime = getLong("hasor.rsfConfig.addressPool.refreshCacheTime", 360000L);
        this.localDiskCache = getBoolean("hasor.rsfConfig.addressPool.localDiskCache", true);
        logger.info("loadRsfConfig complete!");
    }
}