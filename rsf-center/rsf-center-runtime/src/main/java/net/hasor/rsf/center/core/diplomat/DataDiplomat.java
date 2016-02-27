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
package net.hasor.rsf.center.core.diplomat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.core.zktmp.ZkTmpService;
import net.hasor.rsf.center.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.RsfEvent;
import net.hasor.rsf.utils.TimerManager;
/**
 * 注册中心在集群上的注册资料维护类。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Event(RsfEvent.SyncConnected)
public class DataDiplomat implements EventListener<ZooKeeperNode> {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext   appContext;
    @Inject
    private RsfCenterCfg rsfCenterCfg;
    @Inject
    private ZkTmpService zkTmpService;
    private String       leaderHostName;
    private TimerManager timerManager;
    //
    //
    public String getLeaderHostName() {
        return leaderHostName;
    }
    /** 获取服务信息，在ZK上的路径 */
    private String getZooKeeperServerPath() {
        RsfCenterCfg rsfCenterCfg = this.appContext.getInstance(RsfCenterCfg.class);
        return ZooKeeperNode.SERVER_PATH + "/" + rsfCenterCfg.getHostAndPort();
    }
    /** 获取服务信息，在ZK上的路径 */
    private String getServerInfo() {
        RsfCenterCfg rsfCenterCfg = this.appContext.getInstance(RsfCenterCfg.class);
        return rsfCenterCfg.getHostAndPort();
    }
    //
    @Override
    public void onEvent(String event, ZooKeeperNode zkNode) throws Throwable {
        try {
            //init节点数据
            this.initZooKeeperInfo(zkNode);
            //Leader同步通知
            final ZooKeeper zooKeeper = zkNode.getZooKeeper();
            final Watcher watcher = new Watcher() {
                public void process(WatchedEvent event) {
                    try {
                        zooKeeper.getChildren(ZooKeeperNode.LEADER_PATH, this);
                        evalLeaderHostName(zooKeeper);
                        if (rsfCenterCfg.getHostAndPort().equals(leaderHostName)) {
                            logger.info("confirm leader to {} , leader is myself.", leaderHostName);
                        } else {
                            logger.info("confirm leader to {}.", leaderHostName);
                        }
                        appContext.getEnvironment().getEventContext().fireAsyncEvent(RsfEvent.ConfirmLeader, DataDiplomat.this);
                    } catch (NoNodeException e) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) { /**/ }
                        //
                        logger.info("zkNode {} , NoNodeException -> retry event. {}", event.getPath(), event.getState());
                        this.process(event);
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            };
            zooKeeper.getChildren(ZooKeeperNode.LEADER_PATH, watcher);
            //计算Leader数据
            String hostName = this.getServerInfo();
            zooKeeper.create(ZooKeeperNode.LEADER_PATH + "/n_", hostName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            this.evalLeaderHostName(zooKeeper);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
    private void evalLeaderHostName(ZooKeeper zooKeeper) throws KeeperException, InterruptedException {
        String rootNodeName = ZooKeeperNode.LEADER_PATH;
        List<String> preLeaders = zooKeeper.getChildren(ZooKeeperNode.LEADER_PATH, false);
        List<LeaderOffer> leaderOffers = new ArrayList<LeaderOffer>(preLeaders.size());
        for (String offer : preLeaders) {
            String hostName = new String(zooKeeper.getData(rootNodeName + "/" + offer, false, null));
            leaderOffers.add(new LeaderOffer(Integer.valueOf(offer.substring("n_".length())), rootNodeName + "/" + offer, hostName));
        }
        Collections.sort(leaderOffers, new LeaderOffer.IdComparator());
        if (leaderOffers.size() > 0) {
            this.leaderHostName = leaderOffers.get(0).getHostName();
        } else {
            this.leaderHostName = null;
        }
    }
    //
    @Init
    public void init() {
        this.timerManager = new TimerManager(15000);
    }
    /*加入RSF-Center集群*/
    private void initZooKeeperInfo(final ZooKeeperNode zkNode) throws Throwable {
        logger.info("init rsf-center to zooKeeper.");
        //
        // -必要的目录
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.ROOT_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVICES_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.CONFIG_PATH);
        //
        // -Server信息
        final String serverInfoPath = getZooKeeperServerPath();
        zkNode.createNode(ZkNodeType.Persistent, serverInfoPath);
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/info", this.zkTmpService.serverInfo());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/version", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/auth", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/heartbeat", this.zkTmpService.heartbeat());
        //
        // -Leader选举
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        //
        // -Beat(心跳)
        this.timerManager.atTime(new BeatTask(++this.beatID, serverInfoPath, zkNode));
    }
    private long beatID = 0;
    private class BeatTask implements TimerTask {
        private long          curentBeatID   = 0;
        private String        serverInfoPath = null;
        private ZooKeeperNode zkNode         = null;
        public BeatTask(long curentBeatID, String serverInfoPath, ZooKeeperNode zkNode) {
            this.curentBeatID = curentBeatID;
            this.serverInfoPath = serverInfoPath;
            this.zkNode = zkNode;
        }
        public void run(Timeout timeout) throws Exception {
            if (curentBeatID != beatID) {
                return;
            }
            String date = zkTmpService.heartbeat();
            logger.info("rsfCenter beat -> {}", date);
            zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/heartbeat", date);
            timerManager.atTime(this);
        }
    };
}