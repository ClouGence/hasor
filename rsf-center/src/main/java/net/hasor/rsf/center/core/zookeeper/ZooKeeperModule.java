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
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServer.DataTreeBuilder;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.rsf.center.domain.constant.WorkMode;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperModule extends WebModule implements StartModule {
    private WorkMode workAt;
    public ZooKeeperModule(WorkMode workAt) {
        this.workAt = workAt;
    }
    //
    private ZooKeeperCfg startZooKeeperServer(ZooKeeperCfg cfg, Environment env) throws IOException, InterruptedException {
        FileTxnSnapLog txnLog = new FileTxnSnapLog(new File(cfg.getDataDir()), new File(cfg.getSnapDir()));
        DataTreeBuilder treeBuilder = new DataTreeBuilder() {
            public DataTree build() {
                return new DataTree();
            }
        };
        final ZooKeeperServer zkServer = new ZooKeeperServer(txnLog, cfg.getTickTime(), cfg.getMinSessionTimeout(), cfg.getMaxSessionTimeout(), treeBuilder, new ZKDatabase(txnLog));
        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
        cnxnFactory.configure(new InetSocketAddress(cfg.getBindAddress(), cfg.getBindPort()), cfg.getClientCnxns());
        logger.info("zkServer starting...");
        cnxnFactory.startup(zkServer);
        //
        //watch server start.
        long curTime = System.currentTimeMillis();
        while (true) {
            if (!zkServer.isRunning()) {
                Thread.sleep(100);
                long passTime = System.currentTimeMillis() - curTime;
                long second = passTime / 1000;
                if (second > 15) {
                    throw new InterruptedException("15s, zkServer start fail.");/*15秒没起来*/
                } else if (second > 0) {
                    logger.info("zkServer starting {} second pass.", second);
                }
                continue;
            }
            break;
        }
        //
        //保证系统关闭的时候zk被停止
        logger.info("zkServer addShutdownListener.");
        Hasor.addShutdownListener(env, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                if (zkServer.isRunning()) {
                    zkServer.shutdown();
                }
            }
        });
        //
        return cfg;
    }
    //
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        ZooKeeperCfg cfg = new ZooKeeperCfg(apiBinder.getEnvironment());
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n              dataDir = " + cfg.getDataDir());
        writer.append("\n              snapDir = " + cfg.getSnapDir());
        switch (workAt) {
        case Alone://单机模式
            cfg.setBindAddress("127.0.0.1");
            for (int port = 50000; port < 65535; port++) {
                if (NetworkUtils.isPortAvailable(port)) {
                    cfg.setBindPort(port);
                    break;
                }
            }
            cfg.setClientCnxns(2);
            cfg.setZkServers(cfg.getBindAddress() + ":" + cfg.getBindPort());
            break;
        case Master://集群下主机模式
            writer.append("\n             bindPort = " + cfg.getBindPort());
            writer.append("\n             tickTime = " + cfg.getTickTime());
            writer.append("\n    minSessionTimeout = " + cfg.getMinSessionTimeout());
            writer.append("\n    maxSessionTimeout = " + cfg.getMaxSessionTimeout());
            writer.append("\n          clientCnxns = " + cfg.getClientCnxns());
            break;
        case Slave://集群下丛机模式
            writer.append("\n        clientTimeout = " + cfg.getClientTimeout());
            writer.append("\n            zkServers = " + cfg.getZkServers());
            break;
        default:
            throw new InterruptedException("undefined workMode : " + workAt.getCodeString());
        }
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        //
        //zk客户端
        logger.info("ZooKeeper connection to shelf.");
        ZooKeeper zooKeeper = new ZooKeeper(cfg.getZkServers(), cfg.getClientTimeout(), new Watcher() {
            public void process(WatchedEvent event) {
                logger.info(event.getPath());
            }
        });
        //
        apiBinder.bindType(ZooKeeper.class).toInstance(zooKeeper);
    }
    public void onStart(AppContext appContext) throws Throwable {
        ZooKeeper zooKeeper = appContext.getInstance(ZooKeeper.class);
        logger.info("ZooKeeper starting");
        //
        System.out.println("ssssss");
        zooKeeper.create("/root", "data".getBytes(), null, null);
        zooKeeper.setData("/", "data".getBytes(), 0);
        zooKeeper.getState().isAlive();
    }
}