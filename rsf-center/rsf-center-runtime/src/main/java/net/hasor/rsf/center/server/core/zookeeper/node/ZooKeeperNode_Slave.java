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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventContext;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.center.server.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
/**
 * 集群客户端模式，加入已有ZK集群。作为ZK客户端还提供了对ZK的读写功能。
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperNode_Slave implements ZooKeeperNode, Watcher {
    protected Logger       logger           = LoggerFactory.getLogger(getClass());
    private RsfContext     rsfContext       = null;
    protected RsfCenterCfg centerConfig     = null;
    private String         serverConnection = null;
    private ZooKeeper      zooKeeper        = null;
    private boolean        start            = false;
    private Thread         zkCheckManager   = null;
    //
    public ZooKeeperNode_Slave(RsfCenterCfg centerConfig) {
        this.centerConfig = centerConfig;
        /* 该线程对象负责当 start 了之后，不断的检测zk是否挂掉需要重新链（例如：Session超时） */
        this.zkCheckManager = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (start == true && zooKeeper.getState() == States.CLOSED) {
                            zooKeeper.close();
                            zooKeeper = createZK();
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e2) { /**/ }
                    }
                }
            }
        };
        this.zkCheckManager.setDaemon(true);
        this.zkCheckManager.setName("RsfCenter-CheckZK");
        this.zkCheckManager.start();
    }
    //
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {
            logger.info("zookeeper client -> SyncConnected.");// 链接成功。
            EventContext ec = this.rsfContext.getAppContext().getEnvironment().getEventContext();
            ec.fireSyncEvent(RsfCenterEvent.SyncConnected_Event, this);
        }
    }
    //
    /** 终止ZooKeeper */
    public void shutdownZooKeeper(AppContext appContext) throws IOException, InterruptedException {
        this.start = false;
        if (zooKeeper != null) {
            this.zooKeeper.close();
            this.zooKeeper = null;
        }
    }
    /** 启动ZooKeeper */
    public void startZooKeeper(RsfContext rsfContext, QuorumPeerConfig config) throws Throwable {
        this.startZooKeeper(rsfContext, centerConfig.getZkServersStr());
    }
    /** 启动ZooKeeper */
    protected void startZooKeeper(RsfContext rsfContext, String serverConnection) throws IOException, InterruptedException {
        logger.info("zkClient connected to {}.", serverConnection);
        if (this.start == true) {
            return;
        }
        this.rsfContext = rsfContext;
        this.serverConnection = serverConnection;
        this.zooKeeper = createZK();
        this.start = true;
        //
        //3.watch server start.
        long curTime = System.currentTimeMillis();
        while (true) {
            if (this.zooKeeper.getState() == States.CONNECTING) {
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
        logger.info("zkClient connected -> ok.");
    }
    private ZooKeeper createZK() throws IOException {
        return new ZooKeeper(this.serverConnection, centerConfig.getClientTimeout(), this);
    }
    //
    //
    @Override
    public void watcherChildren(String nodePath, Watcher watcher) throws KeeperException, InterruptedException {
        if (watcher != null) {
            this.zooKeeper.getChildren(nodePath, watcher);
        }
    }
    @Override
    public boolean existsNode(String nodePath) throws KeeperException, InterruptedException {
        return this.zooKeeper.exists(nodePath, false) != null;
    }
    @Override
    public List<String> getChildrenNode(String nodePath) throws KeeperException, InterruptedException {
        if (this.zooKeeper.exists(nodePath, false) != null) {
            return this.zooKeeper.getChildren(nodePath, false);
        }
        return Collections.EMPTY_LIST;
    }
    @Override
    public String createNode(ZkNodeType nodtType, String nodePath) throws KeeperException, InterruptedException {
        if (this.zooKeeper.exists(nodePath, false) == null) {
            try {
                String parent = new File(nodePath).getParent().replace("\\\\", "/");
                if (this.zooKeeper.exists(parent, false) == null) {
                    this.createNode(nodtType, parent);
                }
                //
                if (nodePath.startsWith(ROOT_PATH) == false) {
                    throw new IllegalArgumentException("zkPath " + nodePath + " is not rsfCenter path.");
                }
                //
                String result = this.zooKeeper.create(nodePath, null, Ids.OPEN_ACL_UNSAFE, nodtType.getNodeType());
                logger.debug("zkClient createNode {} -> {}", nodePath, result);
                return result;
            } catch (NodeExistsException e) {
                logger.warn("zkClient createNode {} -> NodeExistsException ,maybe someone created first.-> {}", nodePath, e.getMessage());
            } catch (NoNodeException e) {
                logger.warn("zkClient createNode {} -> NoNodeException -> {}", nodePath, e.getMessage());
            }
        } else {
            logger.info("zkClient createNode {} -> exists.", nodePath);
        }
        return null;
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
                logger.debug("zkClient deleteNode {}", nodePath);
            } catch (NoNodeException e) {
                logger.warn("zkClient deleteNode {} -> NoNodeException ,maybe someone deleted first.-> {}", nodePath, e.getMessage());
            }
        } else {
            logger.info("zkClient deleteNode {} -> is not exists.", nodePath);
        }
    }
    @Override
    public Stat saveOrUpdate(ZkNodeType nodtType, String nodePath, String data) throws KeeperException, InterruptedException {
        Stat stat = this.zooKeeper.exists(nodePath, false);
        if (stat == null) {
            String createNodePath = this.createNode(nodtType, nodePath);
            stat = this.zooKeeper.exists(createNodePath, false);
            if (stat == null) {
                return null;
            }
            nodePath = createNodePath;
        }
        //
        byte[] byteDatas = (data == null) ? null : data.getBytes();
        stat = this.zooKeeper.setData(nodePath, byteDatas, stat.getVersion());
        logger.debug("zkClient saveOrUpdate Node {}", nodePath);
        return stat;
    }
    @Override
    public String readData(String nodePath) throws KeeperException, InterruptedException {
        Stat stat = this.zooKeeper.exists(nodePath, false);
        if (stat == null) {
            return null;
        }
        //
        byte[] byteDatas = this.zooKeeper.getData(nodePath, false, stat);
        logger.debug("zkClient readData Node {}", nodePath);
        if (byteDatas == null) {
            return null;
        } else {
            return new String(byteDatas);
        }
    }
}