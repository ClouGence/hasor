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
package net.hasor.rsf.center.server.core.diplomat;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.server.core.zktmp.ZkTmpService;
import net.hasor.rsf.center.server.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.server.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
import net.hasor.rsf.center.server.domain.RsfCenterEvent;
import net.hasor.rsf.center.server.utils.DateCenterUtils;
import net.hasor.rsf.utils.TimerManager;
/**
 * 负责在ZK集群上的注册中心资料。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
@Event(RsfCenterEvent.SyncConnected_Event)
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
    private String getServerNode() {
        RsfCenterCfg rsfCenterCfg = this.appContext.getInstance(RsfCenterCfg.class);
        InetSocketAddress bindInetAddress = rsfCenterCfg.getBindInetAddress();
        return bindInetAddress.getAddress().getHostAddress() + ":" + bindInetAddress.getPort();
    }
    //
    @Override
    public void onEvent(final String event, final ZooKeeperNode zkNode) throws Throwable {
        try {
            //
            //init节点数据
            this.initZooKeeperInfo(zkNode);
            //
            //Leader同步通知
            final Watcher watcher = new Watcher() {
                public void process(WatchedEvent event) {
                    try {
                        zkNode.watcherChildren(ZooKeeperNode.LEADER_PATH, this);
                        evalLeaderHostName(zkNode);
                        String bindAddress = rsfCenterCfg.getBindInetAddress().getAddress().getHostAddress();
                        if (bindAddress.equals(leaderHostName)) {
                            logger.info("confirm leader to {} , leader is myself.", leaderHostName);
                        } else {
                            logger.info("confirm leader to {}.", leaderHostName);
                        }
                        appContext.getEnvironment().getEventContext().fireAsyncEvent(RsfCenterEvent.ConfirmLeader_Event, DataDiplomat.this);
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
            zkNode.watcherChildren(ZooKeeperNode.LEADER_PATH, watcher);
            //
            //排队申请成为Leader
            String hostName = this.getServerNode();
            zkNode.saveOrUpdate(ZkNodeType.Share, ZooKeeperNode.LEADER_PATH + "/n_", hostName);
            this.evalLeaderHostName(zkNode);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
    private void evalLeaderHostName(ZooKeeperNode zkNode) throws KeeperException, InterruptedException {
        String rootNodeName = ZooKeeperNode.LEADER_PATH;
        List<String> preLeaders = zkNode.getChildrenNode(ZooKeeperNode.LEADER_PATH);
        List<LeaderOffer> leaderOffers = new ArrayList<LeaderOffer>(preLeaders.size());
        for (String offer : preLeaders) {
            String hostName = zkNode.readData(rootNodeName + "/" + offer);
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
        this.timerManager = new TimerManager(15000, "RsfCenter-Beat");
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
        final String serverInfoPath = ZooKeeperNode.SERVER_PATH + "/" + this.getServerNode();
        zkNode.createNode(ZkNodeType.Persistent, serverInfoPath);
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/info", this.zkTmpService.serverInfo());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/version", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/auth", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/beat", DateCenterUtils.timestamp());
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
            String date = DateCenterUtils.timestamp();
            logger.info("rsfCenter beat -> {}", date);
            zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/beat", date);
            timerManager.atTime(this);
        }
    };
    //
}