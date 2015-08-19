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
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.StartModule;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.DataTree;
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
        FileTxnSnapLog snapLog = new FileTxnSnapLog(new File(workDir, "data"), new File(workDir, "snap"));
        ZKDatabase zkDB = new ZKDatabase(snapLog);
        int tickTime = 10;
        int minSessionTimeout = 10;
        int maxSessionTimeout = 10;
        DataTreeBuilder treeBuilder = new DataTreeBuilder() {
            public DataTree build() {
                return new DataTree();
            }
        };
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n    dataDir =" + snapLog.getDataDir());
        writer.append("\n    snapDir =" + snapLog.getSnapDir());
        writer.append("\n    tickTime =" + tickTime);
        writer.append("\n    minSessionTimeout =" + minSessionTimeout);
        writer.append("\n    maxSessionTimeout =" + maxSessionTimeout);
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        ZooKeeperServer server = new ZooKeeperServer(snapLog, tickTime, minSessionTimeout, maxSessionTimeout, treeBuilder, zkDB);
        apiBinder.bindType(ZooKeeperServer.class).toInstance(server);
        //
        //
    }
    public void onStart(AppContext appContext) throws Throwable {
        ZooKeeperServer zkServer = appContext.getInstance(ZooKeeperServer.class);
        logger.info("ZooKeeper starting");
        zkServer.startup();
        //
        ZooKeeper zk = new ZooKeeper("127.0.0.1", 10, new Watcher() {
            public void process(WatchedEvent event) {
                logger.info(event.getPath());
            }
        });
        zk.setData("/root", "data".getBytes(), 1);
        zk.getState().isAlive();
    }
}