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
package net.hasor.rsf.center.core.zookeeper;
import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.more.util.StringUtils;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperCfg {
    // - 通用
    private long                    serverID;
    private Map<Long, QuorumServer> zkServers;
    // - Client模式
    private int                     clientTimeout;
    // - Server模式
    private String                  workDir;
    private String                  dataDir;
    private String                  snapDir;
    private String                  bindAddress;
    private int                     bindPort;
    private int                     tickTime;
    private int                     minSessionTimeout;
    private int                     maxSessionTimeout;
    private int                     clientCnxns;
    private int                     initLimit;
    private int                     syncLimit;
    private boolean                 syncEnabled;
    private int                     electionAlg;
    private int                     electionPort;
    private LearnerType             peerType;
    private boolean                 quorumListenOnAllIPs;
    //
    //
    private ZooKeeperCfg() {}
    public static ZooKeeperCfg buildFormConfig(Environment env) throws UnknownHostException {
        ZooKeeperCfg cfg = new ZooKeeperCfg();
        Settings settings = env.getSettings();
        cfg.serverID = settings.getLong("rsfCenter.serverID", 0L);
        cfg.zkServers = new HashMap<Long, QuorumServer>();
        XmlNode[] zkServers = settings.getXmlNodeArray("rsfCenter.zooKeeper.zkServers.server");
        int defaultBindPort = settings.getInteger("rsfCenter.zooKeeper.zkServers.defaultBindPort", 2180);
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
                InetSocketAddress bindAddr = new InetSocketAddress(bindAddress, bindPort);
                InetSocketAddress electionAddr = new InetSocketAddress(bindAddress, electionPort);
                cfg.zkServers.put(sid, new QuorumServer(sid, bindAddr, electionAddr));
                //
            }
        }
        //
        cfg.clientTimeout = settings.getInteger("rsfCenter.zooKeeper.clientTimeout", 15000);
        //
        cfg.workDir = env.getWorkSpaceDir();
        cfg.dataDir = new File(cfg.workDir, "data").getAbsolutePath();
        cfg.snapDir = new File(cfg.workDir, "snap").getAbsolutePath();
        cfg.bindAddress = settings.getString("rsfCenter.zooKeeper.bindAddress", "local");
        cfg.bindPort = settings.getInteger("rsfCenter.zooKeeper.bindPort", 2180);// 绑定的端口
        cfg.tickTime = settings.getInteger("rsfCenter.zooKeeper.tickTime", 3000);// 心跳时间
        cfg.minSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.minSessionTimeout", 15000);
        cfg.maxSessionTimeout = settings.getInteger("rsfCenter.zooKeeper.maxSessionTimeout", 30000);
        cfg.clientCnxns = settings.getInteger("rsfCenter.zooKeeper.clientCnxns", 300);// 最大客户端连接数
        //
        cfg.initLimit = settings.getInteger("rsfCenter.zooKeeper.initLimit", 10);
        cfg.syncLimit = settings.getInteger("rsfCenter.zooKeeper.syncLimit", 5);
        cfg.syncEnabled = settings.getBoolean("rsfCenter.zooKeeper.syncEnabled", true);
        cfg.electionAlg = settings.getInteger("rsfCenter.zooKeeper.electionAlg", 3);
        cfg.electionPort = settings.getInteger("rsfCenter.zooKeeper.electionPort", 2182);
        cfg.peerType = settings.getEnum("rsfCenter.zooKeeper.peerType", LearnerType.class, LearnerType.PARTICIPANT);
        cfg.quorumListenOnAllIPs = settings.getBoolean("rsfCenter.zooKeeper.quorumListenOnAllIPs", false);
        //
        // -check electionPort
        QuorumServer thisServer = cfg.zkServers.get(cfg.serverID);
        if (thisServer == null) {
            throw new IllegalStateException("In 'rsfCenter.zooKeeper.zkServers' configuration lose yourself. -> serverID = " + cfg.serverID);
        } else {
            if (thisServer.electionAddr.getPort() != cfg.electionPort) {
                throw new IllegalStateException("electionPort configuration is not consistent . -> serverID = " + cfg.serverID);
            }
        }
        return cfg;
    }
    //
    public long getServerID() {
        return serverID;
    }
    public void setServerID(long serverID) {
        this.serverID = serverID;
    }
    public Map<Long, QuorumServer> getZkServers() {
        return zkServers;
    }
    public void setZkServers(Map<Long, QuorumServer> zkServers) {
        this.zkServers = zkServers;
    }
    public String getZkServersStr() {
        Map<Long, QuorumServer> servers = this.getZkServers();
        if (servers == null || servers.isEmpty()) {
            return "[]";
        } else {
            StringBuilder strBuilder = new StringBuilder("[ ");
            for (Entry<Long, QuorumServer> ent : servers.entrySet()) {
                long sid = ent.getKey();
                QuorumServer qServer = ent.getValue();
                String host = sid + "=" + qServer.addr.getHostName() + ":" + qServer.addr.getPort() + ":" + qServer.electionAddr.getPort();
                if (strBuilder.length() > 2) {
                    strBuilder.append(" , ");
                }
                strBuilder.append(host);
            }
            return strBuilder.append(" ]").toString();
        }
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
    public String getBindAddress() {
        return bindAddress;
    }
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }
    public int getBindPort() {
        return bindPort;
    }
    public void setBindPort(int bindPort) {
        this.bindPort = bindPort;
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
    public int getElectionAlg() {
        return electionAlg;
    }
    public void setElectionAlg(int electionAlg) {
        this.electionAlg = electionAlg;
    }
    public LearnerType getPeerType() {
        return peerType;
    }
    public void setPeerType(LearnerType peerType) {
        this.peerType = peerType;
    }
    public boolean isQuorumListenOnAllIPs() {
        return quorumListenOnAllIPs;
    }
    public void setQuorumListenOnAllIPs(boolean quorumListenOnAllIPs) {
        this.quorumListenOnAllIPs = quorumListenOnAllIPs;
    }
    public int getElectionPort() {
        return electionPort;
    }
    public void setElectionPort(int electionPort) {
        this.electionPort = electionPort;
    }
}