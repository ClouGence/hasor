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
package net.hasor.rsf.center.server.core.zookeeper;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Alone;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Master;
import net.hasor.rsf.center.server.core.zookeeper.node.ZooKeeperNode_Slave;
import net.hasor.rsf.center.server.domain.RsfCenterCfg;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ZooKeeperModule implements LifeModule {
    protected Logger     logger       = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg = null;
    public ZooKeeperModule(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (this.rsfCenterCfg == null) {
            this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        }
        ZooKeeperNode zkNode = null;
        StringWriter writer = new StringWriter();
        writer.append("\n----------- ZooKeeper -----------");
        writer.append("\n             workMode = " + rsfCenterCfg.getWorkMode());
        switch (rsfCenterCfg.getWorkMode()) {
        case Alone:
            // 单机模式
            writer.append("\n              dataDir = " + rsfCenterCfg.getDataDir());
            writer.append("\n              snapDir = " + rsfCenterCfg.getSnapDir());
            writer.append("\n zooKeeperBindAddress = " + rsfCenterCfg.getBindInetAddress());
            zkNode = new ZooKeeperNode_Alone(rsfCenterCfg);
            break;
        case Master:
            // 集群主机模式
            writer.append("\n              dataDir = " + rsfCenterCfg.getDataDir());
            writer.append("\n              snapDir = " + rsfCenterCfg.getSnapDir());
            writer.append("\n zooKeeperBindAddress = " + rsfCenterCfg.getBindInetAddress());
            writer.append("\n             tickTime = " + rsfCenterCfg.getTickTime());
            writer.append("\n    minSessionTimeout = " + rsfCenterCfg.getMinSessionTimeout());
            writer.append("\n    maxSessionTimeout = " + rsfCenterCfg.getMaxSessionTimeout());
            writer.append("\n          clientCnxns = " + rsfCenterCfg.getClientCnxns());
            writer.append("\n         electionPort = " + rsfCenterCfg.getElectionPort());
            zkNode = new ZooKeeperNode_Master(rsfCenterCfg);
            break;
        case Slave:
            // 集群从属模式
            writer.append("\n        clientTimeout = " + rsfCenterCfg.getClientTimeout());
            zkNode = new ZooKeeperNode_Slave(rsfCenterCfg);
            break;
        default:
            throw new InterruptedException("undefined workMode : " + rsfCenterCfg.getWorkMode().getCodeString());
        }
        writer.append("\n            zkServers = " + rsfCenterCfg.getZkServersStrForLog());
        writer.append("\n---------------------------------");
        logger.info("ZooKeeper config following:" + writer.toString());
        //
        apiBinder.bindType(ZooKeeperNode.class).toInstance(zkNode);
    }
    public void onStart(AppContext appContext) throws Throwable {
        //
        // 启动ZK
        logger.info("startZooKeeper...");
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        zkNode.startZooKeeper(appContext);
        //
    }
    public void onStop(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("shutdownZooKeeper...");
        zkNode.shutdownZooKeeper(appContext);
    }
}