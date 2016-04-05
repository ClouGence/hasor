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
package net.hasor.rsf.center.server.core.zookeeper.node;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;
import org.apache.zookeeper.server.NIOServerCnxn;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;
import org.more.util.io.IOUtils;
import net.hasor.core.AppContext;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
/**
 * 单机模式，不加入任何ZK集群，自己本身就是一个ZK节点。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Alone extends ZooKeeperNode_Slave implements ZooKeeperNode {
    private RsfCenterCfg          zooKeeperCfg;
    private NIOServerCnxn.Factory cnxnFactory;
    public ZooKeeperNode_Alone(RsfCenterCfg zooKeeperCfg) {
        super(zooKeeperCfg);
        this.zooKeeperCfg = zooKeeperCfg;
    }
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper(AppContext appContext) throws IOException, InterruptedException {
        super.shutdownZooKeeper(appContext);
        if (this.cnxnFactory != null) {
            this.cnxnFactory.shutdown();
            //            this.cnxnFactory.shutdown();
            //            this.zkServer.shutdown();
            //            this.txnLog.close();
            //            this.cnxnFactory = null;
            //            this.zkServer = null;
            //            this.txnLog = null;
        }
    }
    /** 启动ZooKeeper 
     * @throws ConfigException */
    public void startZooKeeper(AppContext appContext) throws Throwable {
        //        private Map<Long, QuorumServer> zkServers;
        //        // - Client模式
        //        private int                     clientTimeout;
        //        // - Server模式
        //        private InetSocketAddress       bindInetAddress;
        //
        //1. init config
        InetSocketAddress inetAddress = this.zooKeeperCfg.getBindInetAddress();
        Properties zkProp = new Properties();
        zkProp.setProperty("dataDir", this.zooKeeperCfg.getDataDir());
        zkProp.setProperty("dataLogDir", this.zooKeeperCfg.getSnapDir());
        zkProp.setProperty("clientPortAddress", inetAddress.getHostName());
        zkProp.setProperty("clientPort", String.valueOf(inetAddress.getPort()));
        zkProp.setProperty("tickTime", String.valueOf(this.zooKeeperCfg.getTickTime()));
        zkProp.setProperty("maxClientCnxns", String.valueOf(this.zooKeeperCfg.getClientCnxns()));
        zkProp.setProperty("minSessionTimeout", String.valueOf(this.zooKeeperCfg.getMinSessionTimeout()));
        zkProp.setProperty("maxSessionTimeout", String.valueOf(this.zooKeeperCfg.getMaxSessionTimeout()));
        zkProp.setProperty("initLimit", String.valueOf(this.zooKeeperCfg.getInitLimit()));
        zkProp.setProperty("syncLimit", String.valueOf(this.zooKeeperCfg.getSyncLimit()));
        zkProp.setProperty("syncEnabled", String.valueOf(this.zooKeeperCfg.isSyncEnabled()));
        zkProp.setProperty("electionAlg", "3");
        zkProp.setProperty("electionPort", String.valueOf(this.zooKeeperCfg.getElectionPort()));
        zkProp.setProperty("peerType", this.zooKeeperCfg.getPeerType().name());
        //
        new File(this.zooKeeperCfg.getDataDir()).mkdirs();
        new File(this.zooKeeperCfg.getSnapDir()).mkdirs();
        File myIdFile = new File(this.zooKeeperCfg.getDataDir(), "myid");
        if (myIdFile.exists() == false) {
            myIdFile.getParentFile().mkdirs();
            myIdFile.createNewFile();
        }
        FileWriter myIdWriter = new FileWriter(new File(this.zooKeeperCfg.getDataDir(), "myid"), false);
        IOUtils.write(String.valueOf(this.zooKeeperCfg.getServerID()), myIdWriter);
        myIdWriter.flush();
        myIdWriter.close();
        QuorumPeerConfig config = new QuorumPeerConfig();
        config.parseProperties(zkProp);
        //
        //2. start zookeeper
        logger.info("zkNode starting...");
        ZooKeeperServer zkServer = new ZooKeeperServer();
        try {
            ServerConfig serverConfig = new ServerConfig();
            serverConfig.readFrom(config);
            //
            File dataDir = new File(serverConfig.getDataDir());
            File dataLogDir = new File(serverConfig.getDataLogDir());
            FileTxnSnapLog ftxn = new FileTxnSnapLog(dataDir, dataLogDir);
            zkServer.setTxnLogFactory(ftxn);
            zkServer.setTickTime(serverConfig.getTickTime());
            zkServer.setMinSessionTimeout(serverConfig.getMinSessionTimeout());
            zkServer.setMaxSessionTimeout(serverConfig.getMaxSessionTimeout());
            this.cnxnFactory = new NIOServerCnxn.Factory(config.getClientPortAddress(), config.getMaxClientCnxns());
            this.cnxnFactory.startup(zkServer);
        } catch (InterruptedException e) {
            logger.info("zkNode interrupted", e);
        }
        //
        //3.watch server start.
        long curTime = System.currentTimeMillis();
        while (true) {
            if (!zkServer.isRunning()) {
                Thread.sleep(100);
                long passTime = System.currentTimeMillis() - curTime;
                long second = passTime / 1000;
                if (second > 15) {
                    throw new InterruptedException("15s, zkNode start fail.");/*15秒没起来*/
                } else if (second > 0) {
                    logger.info("zkNode starting {} second pass.", second);
                }
                continue;
            }
            break;
        }
        //
        //4.init zooKeeper Client
        String serverConnection = inetAddress.getHostName() + ":" + inetAddress.getPort();
        super.startZooKeeper(appContext, serverConnection);
    }
}