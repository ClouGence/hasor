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
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
/**
 * 集群节点模式，自己本身作为ZK的一个数据节点加入到ZK集群中，并且该模式参与ZK的选举。如果使用该模式，则至少需要三台以上机器以保证leader的正常选举。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Master extends ZooKeeperNode_Slave implements ZooKeeperNode {
    protected Logger        logger   = LoggerFactory.getLogger(getClass());
    private ZooKeeperMaster zkServer = new ZooKeeperMaster();
    public ZooKeeperNode_Master(RsfCenterCfg rsfCenterCfg) {
        super(rsfCenterCfg);
    }
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper(AppContext appContext) throws IOException, InterruptedException {
        super.shutdownZooKeeper(appContext);
        this.zkServer.shutdown();
    }
    //
    /** 启动ZooKeeper */
    public void startZooKeeper(final RsfContext rsfContext, final QuorumPeerConfig config) throws Throwable {
        //
        //1. start zookeeper
        logger.info("zkNode starting...");
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    zkServer.runFromConfig(config);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        t.setDaemon(true);
        t.setName("ZooKeeper-Main");
        t.start();
        //
        //4.init zooKeeper Client
        RsfCenterCfg rsfCenterCfg = rsfContext.getAppContext().getInstance(RsfCenterCfg.class);
        InetSocketAddress inetAddress = rsfCenterCfg.getBindInetAddress();
        String serverConnection = inetAddress.getHostName() + ":" + inetAddress.getPort();
        super.startZooKeeper(rsfContext, serverConnection);
    }
    private static class ZooKeeperMaster extends QuorumPeerMain {
        public void shutdown() {
            super.quorumPeer.shutdown();
        }
    }
}