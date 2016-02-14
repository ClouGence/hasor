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
package net.hasor.rsf.center.core.zookeeper.node;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
/**
 * 单机模式，不加入任何ZK集群，自己本身就是一个ZK节点。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Alone extends ZooKeeperNode_Slave implements ZooKeeperNode {
    protected Logger          logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg      zooKeeperCfg;
    private FileTxnSnapLog    txnLog;
    private ZooKeeperServer   zkServer;
    private ServerCnxnFactory cnxnFactory;
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
            this.zkServer.shutdown();
            this.txnLog.close();
            this.cnxnFactory = null;
            this.zkServer = null;
            this.txnLog = null;
        }
    }
    /** 启动ZooKeeper */
    public void startZooKeeper(AppContext appContext) throws IOException, InterruptedException {
        //
        File dataDir = new File(this.zooKeeperCfg.getDataDir());
        File snapDir = new File(this.zooKeeperCfg.getSnapDir());
        this.txnLog = new FileTxnSnapLog(dataDir, snapDir);
        //
        int tickTime = this.zooKeeperCfg.getTickTime();
        int minSessionTimeout = this.zooKeeperCfg.getMinSessionTimeout();
        int maxSessionTimeout = this.zooKeeperCfg.getMaxSessionTimeout();
        this.zkServer = new ZooKeeperServer();
        this.zkServer.setTxnLogFactory(txnLog);
        this.zkServer.setTickTime(tickTime);
        this.zkServer.setMinSessionTimeout(minSessionTimeout);
        this.zkServer.setMaxSessionTimeout(maxSessionTimeout);
        // ZooKeeperServer zkServer = new ZooKeeperServer(txnLog, tickTime,
        // minSessionTimeout, maxSessionTimeout, this, new ZKDatabase(txnLog));
        //
        InetSocketAddress inetAddress = this.zooKeeperCfg.getBindInetAddress();
        this.cnxnFactory = ServerCnxnFactory.createFactory();
        this.cnxnFactory.configure(inetAddress, this.zooKeeperCfg.getClientCnxns());
        //
        logger.info("zkNode starting...");
        cnxnFactory.startup(zkServer);
        //
        // watch server start.
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
        String serverConnection = inetAddress.getHostName() + ":" + inetAddress.getPort();
        super.startZooKeeper(appContext, serverConnection);
    }
}