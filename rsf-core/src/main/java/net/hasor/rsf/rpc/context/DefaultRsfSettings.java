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
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.SettingsWarp;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.SendLimitPolicy;
import net.hasor.rsf.constants.ProtocolVersion;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class DefaultRsfSettings extends SettingsWarp implements RsfSettings {
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
    //
    private String          bindAddress          = "local";
    private int             bindPort             = 8000;
    //
    //
    //
    public DefaultRsfSettings(Settings settings) throws IOException {
        super(settings);
        this.loadRsfConfig();
    }
    //
    public int getDefaultTimeout() {
        return this.defaultTimeout;
    }
    public RsfOptionSet getServerOption() {
        return this.serverOptionManager;
    }
    public RsfOptionSet getClientOption() {
        return this.clientOptionManager;
    }
    public byte getVersion() {
        return ProtocolVersion.V_1_0.value();
    }
    public String getDefaultGroup() {
        return this.defaultGroup;
    }
    public String getDefaultVersion() {
        return this.defaultVersion;
    }
    public String getDefaultSerializeType() {
        return this.defaultSerializeType;
    }
    public int getNetworkWorker() {
        return this.networkWorker;
    }
    public int getNetworkListener() {
        return this.networkListener;
    }
    public int getQueueMaxSize() {
        return this.queueMaxSize;
    }
    public int getQueueMinPoolSize() {
        return this.queueMinPoolSize;
    }
    public int getQueueMaxPoolSize() {
        return this.queueMaxPoolSize;
    }
    public long getQueueKeepAliveTime() {
        return this.queueKeepAliveTime;
    }
    public int getRequestTimeout() {
        return this.requestTimeout;
    }
    public int getMaximumRequest() {
        return this.maximumRequest;
    }
    public SendLimitPolicy getSendLimitPolicy() {
        return this.sendLimitPolicy;
    }
    public String getBindAddress() {
        return this.bindAddress;
    }
    public int getBindPort() {
        return this.bindPort;
    }
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
        //
        this.bindAddress = getString("hasor.rsfConfig.address", "local");
        this.bindPort = getInteger("hasor.rsfConfig.port", 8000);
        LoggerHelper.logInfo("loadRsfConfig complete!");
    }
}