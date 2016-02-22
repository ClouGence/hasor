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
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.core.zookeeper.ZkNodeType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
import net.hasor.rsf.center.domain.constant.RsfEvent;
/**
 * 集群数据协调器，负责读写zk集群数据信息。
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
@Event(RsfEvent.SyncConnected)
public class DataDiplomat implements EventListener<ZooKeeperNode> {
    protected Logger      logger         = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext    appContext     = null;
    @Inject
    private RsfCenterCfg  rsfCenterCfg;
    private Configuration configuration  = null;
    private String        leaderHostName = null;
    //
    //
    //
    public String getLeaderHostName() {
        return leaderHostName;
    }
    /** 获取RSF-Center服务器信息 */
    public String serverInfo() throws Throwable {
        RsfCenterCfg rsfCenterCfg = this.appContext.getInstance(RsfCenterCfg.class);
        Map<String, Object> dataModel = new HashMap<String, Object>();
        dataModel.put("cfg", rsfCenterCfg);
        String fmt = "/META-INF/zookeeper/server-info.tmp";
        Template template = this.configuration.getTemplate(fmt, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
    /** 时间戳 */
    private String nowData() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
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
    //
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
    //
    //
    @Init
    public void init() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        configuration.setTemplateLoader(new ClassPathTemplateLoader());
        configuration.setDefaultEncoding("utf-8");// 默认页面编码UTF-8
        configuration.setOutputEncoding("utf-8");// 输出编码格式UTF-8
        configuration.setLocalizedLookup(false);// 是否开启国际化false
        configuration.setNumberFormat("0");
        configuration.setClassicCompatible(true);// null值测处理配置
        this.configuration = configuration;
        //        Heartbeat
    }
    /*加入RSF-Center集群*/
    private void initZooKeeperInfo(ZooKeeperNode zkNode) throws Throwable {
        logger.info("init rsf-center to zooKeeper.");
        // -必要的目录
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.ROOT_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.SERVICES_PATH);
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.CONFIG_PATH);
        // -Server信息
        String serverInfoPath = getZooKeeperServerPath();
        zkNode.createNode(ZkNodeType.Persistent, serverInfoPath);
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/info", this.serverInfo());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/version", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Persistent, serverInfoPath + "/auth", this.rsfCenterCfg.getVersion());
        zkNode.saveOrUpdate(ZkNodeType.Session, serverInfoPath + "/heartbeat", this.nowData());
        // -Leader选举
        zkNode.createNode(ZkNodeType.Persistent, ZooKeeperNode.LEADER_PATH);
        //
    }
}