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
package net.hasor.rsf.center.core.zookeeper.node;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import org.apache.zookeeper.server.DatadirCleanupManager;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.flexible.QuorumMaj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperCfg;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * 集群节点模式，自己本身作为ZK的一个数据节点加入到ZK集群中，并且该模式参与ZK的选举。如果使用该模式，则至少需要三台以上机器以保证leader的正常选举。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Master extends ZooKeeperNode_Slave implements ZooKeeperNode {
    protected Logger              logger = LoggerFactory.getLogger(getClass());
    private ZooKeeperCfg          zooKeeperCfg;
    private QuorumPeer            quorumPeer;
    private DatadirCleanupManager purgeMgr;
    //
    public ZooKeeperNode_Master(ZooKeeperCfg zooKeeperCfg) {
        super(zooKeeperCfg);
        this.zooKeeperCfg = zooKeeperCfg;
    }
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper() throws IOException, InterruptedException {
        if (this.quorumPeer != null) {
            this.quorumPeer.shutdown();
            this.purgeMgr.shutdown();
            this.quorumPeer = null;
            this.purgeMgr = null;
        }
    }
    /** 启动ZooKeeper */
    public void startZooKeeper() throws IOException, InterruptedException {
        //
        File dataDir = new File(this.zooKeeperCfg.getDataDir());
        File snapDir = new File(this.zooKeeperCfg.getSnapDir());
        FileTxnSnapLog txnLog = new FileTxnSnapLog(dataDir, snapDir);
        //
        InetAddress bindAddress = NetworkUtils.finalBindAddress(this.zooKeeperCfg.getBindAddress());
        int bindPort = this.zooKeeperCfg.getBindPort();
        InetSocketAddress inetAddress = new InetSocketAddress(bindAddress, bindPort);
        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
        cnxnFactory.configure(inetAddress, this.zooKeeperCfg.getClientCnxns());
        Map<Long, QuorumServer> servers = this.zooKeeperCfg.getZkServers();
        //
        this.purgeMgr = new DatadirCleanupManager(dataDir.getAbsolutePath(), snapDir.getAbsolutePath(), 3, 0);
        this.purgeMgr.start();
        //
        // @see org.apache.zookeeper.server.quorum.QuorumPeerMain
        this.quorumPeer = new QuorumPeer();
        this.quorumPeer.setClientPortAddress(inetAddress);
        this.quorumPeer.setTxnFactory(txnLog);
        this.quorumPeer.setQuorumPeers(servers);
        this.quorumPeer.setElectionType(this.zooKeeperCfg.getElectionAlg());
        this.quorumPeer.setMyid(this.zooKeeperCfg.getServerID());
        this.quorumPeer.setTickTime(this.zooKeeperCfg.getTickTime());
        this.quorumPeer.setMinSessionTimeout(this.zooKeeperCfg.getMinSessionTimeout());
        this.quorumPeer.setMaxSessionTimeout(this.zooKeeperCfg.getMaxSessionTimeout());
        this.quorumPeer.setInitLimit(this.zooKeeperCfg.getInitLimit());
        this.quorumPeer.setSyncLimit(this.zooKeeperCfg.getSyncLimit());
        this.quorumPeer.setQuorumVerifier(new QuorumMaj(servers.size()));
        this.quorumPeer.setCnxnFactory(cnxnFactory);
        this.quorumPeer.setZKDatabase(new ZKDatabase(this.quorumPeer.getTxnFactory()));
        this.quorumPeer.setLearnerType(this.zooKeeperCfg.getPeerType());
        this.quorumPeer.setSyncEnabled(this.zooKeeperCfg.isSyncEnabled());
        this.quorumPeer.setQuorumListenOnAllIPs(this.zooKeeperCfg.isQuorumListenOnAllIPs());
        //
        logger.info("zkNode starting...");
        this.quorumPeer.start();
        //
        //
        // watch server start.
        long curTime = System.currentTimeMillis();
        while (true) {
            if (!this.quorumPeer.isRunning()) {
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
        //
        String serverConnection = inetAddress.getHostName() + ":" + inetAddress.getPort();
        // super.startZooKeeper(serverConnection);
    }
}