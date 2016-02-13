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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.EventContext;
import net.hasor.rsf.center.core.zookeeper.WatcherListener;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.ZkNodeType;
/**
 * 集群客户端模式，加入已有ZK集群。作为ZK客户端还提供了对ZK的读写功能。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Slave implements ZooKeeperNode, Watcher {
    protected Logger              logger = LoggerFactory.getLogger(getClass());
    private List<WatcherListener> listenerList;
    private String                serverConnection;
    private RsfCenterCfg          zooKeeperCfg;
    private ZooKeeper             zooKeeper;
    //
    public ZooKeeperNode_Slave(RsfCenterCfg zooKeeperCfg, EventContext eventContext) {
        this.listenerList = new ArrayList<WatcherListener>();
        this.zooKeeperCfg = zooKeeperCfg;
    }
    //
    @Override
    public void addListener(WatcherListener listener) {
        this.listenerList.add(listener);
    }
    @Override
    public void clearListener() {
        this.listenerList.clear();
    }
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            logger.info("zookeeper client -> SyncConnected.");// 链接成功。
            for (WatcherListener listener : this.listenerList) {
                listener.syncConnected(this);
            }
        }
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
        this.startZooKeeper(zooKeeperCfg.getZkServersStr());
    }
    /** 启动ZooKeeper */
    protected void startZooKeeper(String serverConnection) throws IOException, InterruptedException {
        logger.info("zkClient connected to {}.", serverConnection);
        this.serverConnection = serverConnection;
        this.zooKeeper = new ZooKeeper(this.serverConnection, zooKeeperCfg.getClientTimeout(), this);
        logger.info("zkClient connected -> ok.");
    }
    //
    //
    @Override
    public ZooKeeper getZooKeeper() {
        return this.zooKeeper;
    }
    @Override
    public void createNode(ZkNodeType nodtType, String nodePath) throws KeeperException, InterruptedException {
        if (this.zooKeeper.exists(nodePath, false) == null) {
            try {
                this.zooKeeper.delete(nodePath, -1);
            } catch (NoNodeException e) {
                // e: NoNodeException => // do nothing
            }
            try {
                String result = this.zooKeeper.create(nodePath, null, Ids.OPEN_ACL_UNSAFE, nodtType.getNodeType());
                logger.info("zkClient createNode {} -> {}", nodePath, result);
            } catch (NodeExistsException e) {
                logger.warn("zkClient createNode {} -> NodeExistsException -> {}", nodePath, e.getMessage());
            }
        } else {
            logger.info("zkClient createNode {} -> exists.", nodePath);
        }
    }
    @Override
    public void deleteNode(String nodePath) throws KeeperException, InterruptedException {
        if (this.zooKeeper.exists(nodePath, false) != null) {
            try {
                List<String> childrenList = this.zooKeeper.getChildren(nodePath, false);
                if (childrenList != null) {
                    for (String itemNodePath : childrenList) {
                        this.deleteNode(nodePath + "/" + itemNodePath);
                    }
                }
                this.zooKeeper.delete(nodePath, -1);
                logger.info("zkClient deleteNode {}", nodePath);
            } catch (NoNodeException e) {
                logger.warn("zkClient deleteNode {} -> NoNodeException -> {}", nodePath, e.getMessage());
            }
        } else {
            logger.info("zkClient deleteNode {} -> is not exists.", nodePath);
        }
    }
    @Override
    public Stat saveOrUpdate(ZkNodeType nodtType, String nodePath, String data) throws KeeperException, InterruptedException {
        Stat stat = this.zooKeeper.exists(nodePath, false);
        if (stat == null) {
            this.createNode(nodtType, nodePath);
            stat = this.zooKeeper.exists(nodePath, false);
            if (stat == null) {
                return null;
            }
        }
        //
        byte[] byteDatas = (data == null) ? null : data.getBytes();
        stat = this.zooKeeper.setData(nodePath, byteDatas, stat.getVersion());
        logger.info("zkClient saveOrUpdate Node {}", nodePath);
        return stat;
    }
}