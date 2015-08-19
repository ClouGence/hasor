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
import java.io.StringWriter;
import java.net.InetSocketAddress;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.StartModule;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.DataTree;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooKeeperServer.DataTreeBuilder;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperModule extends WebModule implements StartModule {
    public void loadModule(WebApiBinder apiBinder) throws Throwable {
        Environment env = apiBinder.getEnvironment();
        String workDir = env.getWorkSpaceDir();
        FileTxnSnapLog txnLog = new FileTxnSnapLog(new File(workDir, "data"), new File(workDir, "snap"));
        int serverTickTime = 500;
        int serverMinSessionTimeout = 500;
        int serverMaxSessionTimeout = 1000;
        int serverMaxClientCnxns = 100;//最大客户端连接数
        int serverPort = 1230;
        int clientSessionTimeout = 500;
        String zkServerIPs = "127.0.0.1:1230";
        //
        //logs
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n                    dataDir = " + txnLog.getDataDir());
        writer.append("\n                    snapDir = " + txnLog.getSnapDir());
        writer.append("\n                 serverPort = " + serverPort);
        writer.append("\n             serverTickTime = " + serverTickTime);
        writer.append("\n    serverMinSessionTimeout = " + serverMinSessionTimeout);
        writer.append("\n    serverMaxSessionTimeout = " + serverMaxSessionTimeout);
        writer.append("\n       serverMaxClientCnxns = " + serverMaxClientCnxns);
        writer.append("\n       clientSessionTimeout = " + clientSessionTimeout);
        writer.append("\n                zkServerIPs = " + zkServerIPs);
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        //
        //create Object
        DataTreeBuilder treeBuilder = new DataTreeBuilder() {
            public DataTree build() {
                return new DataTree();
            }
        };
        final ZooKeeperServer zkServer = new ZooKeeperServer(txnLog, serverTickTime, serverMinSessionTimeout, serverMaxSessionTimeout, treeBuilder, new ZKDatabase(txnLog));
        ServerCnxnFactory cnxnFactory = ServerCnxnFactory.createFactory();
        cnxnFactory.configure(new InetSocketAddress("0.0.0.0", serverPort), serverMaxClientCnxns);
        logger.info("ZooKeeperServer starting...");
        cnxnFactory.startup(zkServer);
        //
        //watch server start.
        long curTime = System.currentTimeMillis();
        while (true) {
            if (!zkServer.isRunning()) {
                Thread.sleep(100);
                long passTime = System.currentTimeMillis() - curTime;
                if (passTime / 1000 > 15) {
                    throw new RuntimeException("15s, zkServer start fail.");/*15秒没起来*/
                }
                continue;
            }
            break;
        }
        //
        //保证系统关闭的时候zk被停止
        logger.info("ZooKeeperServer addShutdownListener.");
        Hasor.addShutdownListener(env, new EventListener() {
            public void onEvent(String event, Object[] params) throws Throwable {
                if (zkServer.isRunning()) {
                    zkServer.shutdown();
                }
            }
        });
        //
        //zk客户端
        logger.info("ZooKeeper connection to shelf.");
        ZooKeeper zooKeeper = new ZooKeeper(zkServerIPs, clientSessionTimeout, new Watcher() {
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