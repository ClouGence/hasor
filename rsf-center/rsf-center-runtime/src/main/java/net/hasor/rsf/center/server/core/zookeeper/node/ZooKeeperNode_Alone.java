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
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * 单机模式，不加入任何ZK集群，自己本身就是一个ZK节点。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Alone extends ZooKeeperNode_Slave implements ZooKeeperNode {
    private ZooKeeperAlone zkServer = new ZooKeeperAlone();
    public ZooKeeperNode_Alone(RsfCenterCfg centerConfig) {
        super(centerConfig);
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
                    ServerConfig serverConfig = new ServerConfig();
                    serverConfig.readFrom(config);
                    zkServer.runFromConfig(serverConfig);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };
        t.setDaemon(true);
        t.setName("ZooKeeper-Main");
        t.start();
        //
        //2.wait server start.
        long curTime = System.currentTimeMillis();
        int bindProt = this.centerConfig.getBindInetAddress().getPort();
        while (true) {
            if (NetworkUtils.isPortAvailable(bindProt) == false) {
                Thread.sleep(1000);
                long passTime = System.currentTimeMillis() - curTime;
                long second = passTime / 1000;
                if (second > 150) {
                    throw new InterruptedException("15s, zkNode start fail.");/*15秒没起来*/
                } else if (second > 0) {
                    logger.info("zkNode starting {} second pass.", second);
                }
                continue;
            }
            break;
        }
        //
        //3.init zooKeeper Client
        RsfCenterCfg rsfCenterCfg = rsfContext.getAppContext().getInstance(RsfCenterCfg.class);
        InetSocketAddress inetAddress = rsfCenterCfg.getBindInetAddress();
        String serverConnection = inetAddress.getHostName() + ":" + inetAddress.getPort();
        super.startZooKeeper(rsfContext, serverConnection);
    }
    private static class ZooKeeperAlone extends ZooKeeperServerMain {
        public void shutdown() {
            super.shutdown();
        }
    }
}