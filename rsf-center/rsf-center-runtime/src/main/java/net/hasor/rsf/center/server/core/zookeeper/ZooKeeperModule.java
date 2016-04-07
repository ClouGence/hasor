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
package net.hasor.rsf.center.server.core.zookeeper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Alone;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Master;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Slave;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.ServerInfo;
import net.hasor.rsf.center.server.domain.WorkMode;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperModule implements LifeModule {
    protected Logger     logger       = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg = null;
    public ZooKeeperModule(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (this.rsfCenterCfg == null) {
            this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        }
        ZooKeeperNode zkNode = null;
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n             workMode = " + this.rsfCenterCfg.getWorkMode());
        switch (this.rsfCenterCfg.getWorkMode()) {
        case Alone:
            // 单机模式
            writer.append("\n              dataDir = " + this.rsfCenterCfg.getDataDir());
            writer.append("\n              snapDir = " + this.rsfCenterCfg.getSnapDir());
            writer.append("\n zooKeeperBindAddress = " + this.rsfCenterCfg.getBindInetAddress());
            zkNode = new ZooKeeperNode_Alone(this.rsfCenterCfg);
            break;
        case Master:
            // 集群主机模式
            writer.append("\n              dataDir = " + this.rsfCenterCfg.getDataDir());
            writer.append("\n              snapDir = " + this.rsfCenterCfg.getSnapDir());
            writer.append("\n zooKeeperBindAddress = " + this.rsfCenterCfg.getBindInetAddress());
            writer.append("\n             tickTime = " + this.rsfCenterCfg.getTickTime());
            writer.append("\n    minSessionTimeout = " + this.rsfCenterCfg.getMinSessionTimeout());
            writer.append("\n    maxSessionTimeout = " + this.rsfCenterCfg.getMaxSessionTimeout());
            writer.append("\n          clientCnxns = " + this.rsfCenterCfg.getClientCnxns());
            writer.append("\n         electionPort = " + this.rsfCenterCfg.getElectionPort());
            zkNode = new ZooKeeperNode_Master(this.rsfCenterCfg);
            break;
        case Slave:
            // 集群从属模式
            writer.append("\n        clientTimeout = " + this.rsfCenterCfg.getClientTimeout());
            zkNode = new ZooKeeperNode_Slave(this.rsfCenterCfg);
            break;
        default:
            throw new InterruptedException("undefined workMode : " + this.rsfCenterCfg.getWorkMode().getCodeString());
        }
        writer.append("\n            zkServers = " + this.rsfCenterCfg.getZkServersStrForLog());
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        //
        apiBinder.bindType(ZooKeeperNode.class).toInstance(zkNode);
    }
    public void onStart(AppContext appContext) throws Throwable {
        //
        //1. init config
        Properties zkProp = new Properties();
        InetSocketAddress inetAddress = this.rsfCenterCfg.getBindInetAddress();
        zkProp.setProperty("dataDir", this.rsfCenterCfg.getDataDir());
        zkProp.setProperty("dataLogDir", this.rsfCenterCfg.getSnapDir());
        zkProp.setProperty("clientPort", String.valueOf(inetAddress.getPort()));
        zkProp.setProperty("clientPortAddress", inetAddress.getHostName());
        zkProp.setProperty("tickTime", String.valueOf(this.rsfCenterCfg.getTickTime()));
        zkProp.setProperty("maxClientCnxns", String.valueOf(this.rsfCenterCfg.getClientCnxns()));
        zkProp.setProperty("minSessionTimeout", String.valueOf(this.rsfCenterCfg.getMinSessionTimeout()));
        zkProp.setProperty("maxSessionTimeout", String.valueOf(this.rsfCenterCfg.getMaxSessionTimeout()));
        zkProp.setProperty("initLimit", String.valueOf(this.rsfCenterCfg.getInitLimit()));
        zkProp.setProperty("syncLimit", String.valueOf(this.rsfCenterCfg.getSyncLimit()));
        zkProp.setProperty("syncEnabled", String.valueOf(this.rsfCenterCfg.isSyncEnabled()));
        zkProp.setProperty("electionAlg", "3");
        zkProp.setProperty("quorumListenOnAllIPs", "false");
        zkProp.setProperty("electionPort", String.valueOf(this.rsfCenterCfg.getElectionPort()));
        zkProp.setProperty("peerType", this.rsfCenterCfg.getPeerType().name());
        if (this.rsfCenterCfg.getWorkMode() != WorkMode.Alone) {
            Map<Long, ServerInfo> serverMap = this.rsfCenterCfg.getZkServers();
            if (serverMap == null || serverMap.isEmpty()) {
                throw new IllegalStateException("rsfCenter.zooKeeper.zkServers.server is null.");
            } else {
                for (Entry<Long, ServerInfo> ent : serverMap.entrySet()) {
                    //server.1=localhost:2887:3887
                    String key = "server." + ent.getKey();
                    String value = ent.getValue().toString();
                    zkProp.setProperty(key, value);
                }
            }
        }
        //        // - Client模式
        //        private int                     clientTimeout;
        //
        //2.zk相关目录
        new File(this.rsfCenterCfg.getDataDir()).mkdirs();
        new File(this.rsfCenterCfg.getSnapDir()).mkdirs();
        File myIdFile = new File(this.rsfCenterCfg.getDataDir(), "myid");
        if (myIdFile.exists() == false) {
            myIdFile.getParentFile().mkdirs();
            myIdFile.createNewFile();
        }
        FileWriter myIdWriter = new FileWriter(new File(this.rsfCenterCfg.getDataDir(), "myid"), false);
        IOUtils.write(String.valueOf(this.rsfCenterCfg.getServerID()), myIdWriter);
        myIdWriter.flush();
        myIdWriter.close();
        RsfQuorumPeerConfig config = new RsfQuorumPeerConfig();
        config.parseProperties(zkProp);
        //
        //3.启动ZK
        logger.info("startZooKeeper...");
        RsfContext rsfContext = appContext.getInstance(RsfContext.class);
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        zkNode.startZooKeeper(rsfContext, config);
        //
    }
    public void onStop(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("shutdownZooKeeper...");
        zkNode.shutdownZooKeeper(appContext);
    }
    private static class RsfQuorumPeerConfig extends QuorumPeerConfig {
        @Override
        public void parseProperties(Properties zkProp) throws IOException, ConfigException {
            super.parseProperties(zkProp);
            this.electionPort = Integer.parseInt((String) zkProp.get("electionPort"));
        }
    }
}