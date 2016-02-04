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
import java.io.IOException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperCfg;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
/**
 * 集群客户端模式，加入已有ZK集群。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Slave implements ZooKeeperNode, Watcher {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private ZooKeeperCfg zooKeeperCfg;
    private ZooKeeper    zooKeeper;
    public ZooKeeperNode_Slave(ZooKeeperCfg zooKeeperCfg) {
        this.zooKeeperCfg = zooKeeperCfg;
    }
    //
    public void process(WatchedEvent event) {
        logger.info(event.getPath());
    }
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper() throws IOException, InterruptedException {
        if (zooKeeper != null) {
            zooKeeper.close();
            zooKeeper = null;
        }
    }
    /** 启动ZooKeeper */
    public void startZooKeeper() throws IOException, InterruptedException {
        // this.startZooKeeper(zooKeeperCfg.getZkServers());
    }
    /** 启动ZooKeeper */
    protected void startZooKeeper(String serverConnection) throws IOException, InterruptedException {
        logger.info("ZooKeeper connection to {}.", serverConnection);
        this.zooKeeper = new ZooKeeper(serverConnection, zooKeeperCfg.getClientTimeout(), this);
    }
    /** 返回ZK */
    public ZooKeeper getZooKeeper() {
        return this.zooKeeper;
    }
}