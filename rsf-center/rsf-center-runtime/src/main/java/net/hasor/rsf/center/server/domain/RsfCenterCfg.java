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
package net.hasor.rsf.center.server.domain;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterCfg {
    protected static final Logger logger = LoggerFactory.getLogger(RsfCenterCfg.class);
    // - 通用
    private WorkMode              workMode;
    private long                  serverID;
    private Map<Long, ServerInfo> zkServers;
    // - Client模式
    private int                   clientTimeout;
    // - Server模式
    private String                workDir;
    private String                dataDir;
    private String                snapDir;
    private InetSocketAddress     bindInetAddress;
    private int                   tickTime;
    private int                   minSessionTimeout;
    private int                   maxSessionTimeout;
    private int                   clientCnxns;
    private int                   initLimit;
    private int                   syncLimit;
    private boolean               syncEnabled;
    private int                   electionPort;
    private LearnerType           peerType;
    //
    private String                centerVersion;
    private int                   pushQueueMaxSize;
    private int                   pushSleepTime;
    private String                anonymousAppCode;
    //
    //
    private RsfCenterCfg() {}
    public static RsfCenterCfg buildFormConfig(Environment env) throws UnknownHostException {
        RsfCenterCfg cfg = new RsfCenterCfg();
        Settings settings = env.getSettings();
        cfg.workMode = settings.getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.Alone);
        cfg.serverID = settings.getLong("rsfCenter.serverID", 0L);
        //
        cfg.zkServers = new HashMap<Long, ServerInfo>();
        cfg.peerType = settings.getEnum("rsfCenter.zooKeeper.peerType", LearnerType.class, LearnerType.PARTICIPANT);
        if (cfg.getWorkMode() != WorkMode.Alone) {
            XmlNode[] zkServers = settings.getXmlNodeArray("rsfCenter.zooKeeper.zkServers.server");
            int defaultBindPort = settings.getInteger("rsfCenter.zooKeeper.zkServers.defaultBindPort", 2181);
            int defaultElectionPort = settings.getInteger("rsfCenter.zooKeeper.zkServers.defaultElectionPort", 2182);
            if (zkServers != null && zkServers.length > 0) {
                for (XmlNode server : zkServers) {
                    String binPortStr = server.getAttribute("bindPort");
                    String electionPortStr = server.getAttribute("electionPort");
                    //
                    int bindPort = StringUtils.isBlank(binPortStr) ? defaultBindPort : Integer.valueOf(binPortStr);
                    int electionPort = StringUtils.isBlank(electionPortStr) ? defaultElectionPort : Integer.valueOf(electionPortStr);
                    long sid = Long.valueOf(server.getAttribute("sid"));
                    String address = server.getText();
                    InetAddress bindAddress = NetworkUtils.finalBindAddress(address);
                    //
                    ServerInfo serverInfo = new ServerInfo();
                    serverInfo.setServerID(sid);
                    serverInfo.setAddress(bindAddress.getHostAddress());
                    serverInfo.setBindPort(bindPort);
                    serverInfo.setElectionPort(electionPort);
                    cfg.zkServers.put(sid, serverInfo);
                    //
                }
            }
        }
        //
        cfg.clientTimeout = settings.getInteger("rsfCenter.zooKeeper.clientTimeout", 15000);
        //
        cfg.workDir = env.getWorkSpaceDir();
        cfg.dataDir = new File(cfg.workDir, "zookeeper/data").getAbsolutePath();
        cfg.snapDir = new File(cfg.workDir, "zookeeper/snap").getAbsolutePath();
        //
        String bindAddress = settings.getString("hasor.rsfConfig.address", "local");//settings.getString("rsfCenter.bindAddress", "local");
        int bindPort = settings.getInteger("rsfCenter.zooKeeper.bindPort", 2181);// 绑定的端口
        InetAddress inetAddress = NetworkUtils.finalBindAddress(bindAddress);
        cfg.bindInetAddress = new InetSocketAddress(inetAddress, bindPort);
        //
        cfg.tickTime = settings.getInteger("rsfCenter.zooKeeper.tickTime", 3000);// 心跳时间
        cfg.minSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.minSessionTimeout", 15000);
        cfg.maxSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.maxSessionTimeout", 30000);
        cfg.clientCnxns = settings.getInteger("rsfCenter.zooKeeper.clientCnxns", 300);// 最大客户端连接数
        //
        cfg.initLimit = settings.getInteger("rsfCenter.zooKeeper.initLimit", 10);
        cfg.syncLimit = settings.getInteger("rsfCenter.zooKeeper.syncLimit", 5);
        cfg.syncEnabled = settings.getBoolean("rsfCenter.zooKeeper.syncEnabled", true);
        cfg.electionPort = settings.getInteger("rsfCenter.zooKeeper.electionPort", 2182);
        //
        // -check electionPort
        if (cfg.getWorkMode() == WorkMode.Master) {
            ServerInfo thisServer = cfg.zkServers.get(cfg.serverID);
            if (thisServer == null) {
                throw new IllegalStateException("In 'rsfCenter.zooKeeper.zkServers' configuration lose yourself. -> serverID = " + cfg.serverID);
            } else {
                if (thisServer.getElectionPort() != cfg.electionPort) {
                    throw new IllegalStateException("electionPort configuration is not consistent . -> serverID = " + cfg.serverID);
                }
            }
        }
        //
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            cfg.centerVersion = !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            cfg.centerVersion = "undefined";
        }
        //
        cfg.pushQueueMaxSize = settings.getInteger("rsfCenter.push.queueMaxSize", 100);// 推送队列最大长度，当待推送服务达到这个阀值之后注册中心会做一次推送动作。
        cfg.pushSleepTime = settings.getInteger("rsfCenter.push.sleepTime", 3000);// 数据推送线程每次工作等待的时间
        cfg.anonymousAppCode = settings.getString("rsfCenter.push.anonymousAppCode", "anonymous");// 默认推送使用的：应用程序代码
        return cfg;
    }
    public String getZkServersStr() {
        Map<Long, ServerInfo> servers = this.getZkServers();
        if (servers == null || servers.isEmpty()) {
            return "";
        } else {
            StringBuilder strBuilder = new StringBuilder("");
            for (ServerInfo ent : servers.values()) {
                String host = ent.getAddress() + ":" + ent.getBindPort();
                if (this.getWorkMode() != WorkMode.Slave) {
                    host += (":" + ent.getElectionPort());
                }
                if (strBuilder.length() > 2) {
                    strBuilder.append(",");
                }
                strBuilder.append(host);
            }
            return strBuilder.toString();
        }
    }
    public String getZkServersStrForLog() {
        Map<Long, ServerInfo> servers = this.getZkServers();
        if (servers == null || servers.isEmpty()) {
            return "[]";
        } else {
            StringBuilder strBuilder = new StringBuilder("[ ");
            for (Entry<Long, ServerInfo> ent : servers.entrySet()) {
                long sid = ent.getKey();
                ServerInfo qServer = ent.getValue();
                String host = sid + "=" + qServer.getAddress() + ":" + qServer.getBindPort() + ":" + qServer.getElectionPort();
                if (strBuilder.length() > 2) {
                    strBuilder.append(" , ");
                }
                strBuilder.append(host);
            }
            return strBuilder.append(" ]").toString();
        }
    }
    //
    //
    //
    /** 获取RSF-Center服务器版本 */
    public String getVersion() {
        return this.centerVersion;
    }
    public WorkMode getWorkMode() {
        return workMode;
    }
    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }
    public long getServerID() {
        return serverID;
    }
    public void setServerID(long serverID) {
        this.serverID = serverID;
    }
    public Map<Long, ServerInfo> getZkServers() {
        return zkServers;
    }
    public void setZkServers(Map<Long, ServerInfo> zkServers) {
        this.zkServers = zkServers;
    }
    public int getClientTimeout() {
        return clientTimeout;
    }
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    public String getWorkDir() {
        return workDir;
    }
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
    public String getDataDir() {
        return dataDir;
    }
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    public String getSnapDir() {
        return snapDir;
    }
    public void setSnapDir(String snapDir) {
        this.snapDir = snapDir;
    }
    public InetSocketAddress getBindInetAddress() {
        return bindInetAddress;
    }
    public void setBindInetAddress(InetSocketAddress bindInetAddress) {
        this.bindInetAddress = bindInetAddress;
    }
    public int getTickTime() {
        return tickTime;
    }
    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }
    public int getMinSessionTimeout() {
        return minSessionTimeout;
    }
    public void setMinSessionTimeout(int minSessionTimeout) {
        this.minSessionTimeout = minSessionTimeout;
    }
    public int getMaxSessionTimeout() {
        return maxSessionTimeout;
    }
    public void setMaxSessionTimeout(int maxSessionTimeout) {
        this.maxSessionTimeout = maxSessionTimeout;
    }
    public int getClientCnxns() {
        return clientCnxns;
    }
    public void setClientCnxns(int clientCnxns) {
        this.clientCnxns = clientCnxns;
    }
    public int getInitLimit() {
        return initLimit;
    }
    public void setInitLimit(int initLimit) {
        this.initLimit = initLimit;
    }
    public int getSyncLimit() {
        return syncLimit;
    }
    public void setSyncLimit(int syncLimit) {
        this.syncLimit = syncLimit;
    }
    public boolean isSyncEnabled() {
        return syncEnabled;
    }
    public void setSyncEnabled(boolean syncEnabled) {
        this.syncEnabled = syncEnabled;
    }
    public LearnerType getPeerType() {
        return peerType;
    }
    public void setPeerType(LearnerType peerType) {
        this.peerType = peerType;
    }
    public int getElectionPort() {
        return electionPort;
    }
    public void setElectionPort(int electionPort) {
        this.electionPort = electionPort;
    }
    public int getPushQueueMaxSize() {
        return this.pushQueueMaxSize;
    }
    public void setPushQueueMaxSize(int pushQueueMaxSize) {
        this.pushQueueMaxSize = pushQueueMaxSize;
    }
    public int getPushSleepTime() {
        return pushSleepTime;
    }
    public void setPushSleepTime(int pushSleepTime) {
        this.pushSleepTime = pushSleepTime;
    }
    public String getAnonymousAppCode() {
        return anonymousAppCode;
    }
    public void setAnonymousAppCode(String anonymousAppCode) {
        this.anonymousAppCode = anonymousAppCode;
    }
}