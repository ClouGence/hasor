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
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.core.zookeeper.node.ZooKeeperNode_Alone;
import net.hasor.rsf.center.core.zookeeper.node.ZooKeeperNode_Master;
import net.hasor.rsf.center.core.zookeeper.node.ZooKeeperNode_Slave;
import net.hasor.rsf.center.domain.constant.WorkMode;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperModule implements LifeModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private WorkMode workAt;
    public ZooKeeperModule(WorkMode workAt) {
        this.workAt = workAt;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        ZooKeeperCfg cfg = ZooKeeperCfg.buildFormConfig(apiBinder.getEnvironment());
        ZooKeeperNode zkNode = null;
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n             workMode = " + workAt);
        switch (workAt) {
        case Alone:
            // 单机模式
            cfg.setBindAddress("127.0.0.1");
            cfg.setClientCnxns(2);// 准许两个客户端
            writer.append("\n              dataDir = " + cfg.getDataDir());
            writer.append("\n              snapDir = " + cfg.getSnapDir());
            zkNode = new ZooKeeperNode_Alone(cfg);
            break;
        case Master:
            // 集群主机模式
            writer.append("\n              dataDir = " + cfg.getDataDir());
            writer.append("\n              snapDir = " + cfg.getSnapDir());
            writer.append("\n             bindPort = " + cfg.getBindPort());
            writer.append("\n             tickTime = " + cfg.getTickTime());
            writer.append("\n    minSessionTimeout = " + cfg.getMinSessionTimeout());
            writer.append("\n    maxSessionTimeout = " + cfg.getMaxSessionTimeout());
            writer.append("\n          clientCnxns = " + cfg.getClientCnxns());
            writer.append("\n          electionAlg = " + cfg.getElectionAlg());
            writer.append("\n         electionPort = " + cfg.getElectionPort());
            zkNode = new ZooKeeperNode_Master(cfg);
            break;
        case Slave:
            // 集群从属模式
            writer.append("\n        clientTimeout = " + cfg.getClientTimeout());
            zkNode = new ZooKeeperNode_Slave(cfg);
            break;
        default:
            throw new InterruptedException("undefined workMode : " + workAt.getCodeString());
        }
        writer.append("\n            zkServers = " + cfg.getZkServersStr());
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        //
        apiBinder.bindType(ZooKeeperNode.class).toInstance(zkNode);
    }
    public void onStart(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("startZooKeeper...");
        zkNode.startZooKeeper();
    }
    public void onStop(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("shutdownZooKeeper...");
        zkNode.shutdownZooKeeper();
    }
}