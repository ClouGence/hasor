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
import java.net.InetSocketAddress;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class InitDataModue implements LifeModule {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg;
    private String       serverInfoPath;
    public InitDataModue(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    //
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //
    }
    public void onStart(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("ZooKeeper data -> init data.");
        //
        InetSocketAddress inetAddress = this.rsfCenterCfg.getBindInetAddress();
        String localAddress = inetAddress.getAddress().getHostAddress();
        int localPort = this.rsfCenterCfg.getRsfPort();
        this.serverInfoPath = String.format(ZooKeeperNode.SERVER_PATH + "/%s:%s", localAddress, localPort);
        //
        // 初始化ZK信息
        this.initCenterInfo(zkNode);
    }
    public void onStop(AppContext appContext) throws Throwable {
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        logger.info("ZooKeeper data -> remove server info.");
        //
        zkNode.deleteNode(this.serverInfoPath);
    }
    //
    //
    //
    private void initCenterInfo(ZooKeeperNode zkNode) throws KeeperException, InterruptedException {
        logger.info("init rsf-center to zooKeeper.");
        //
        // -必要的目录
        zkNode.createNode(ZooKeeperNode.ROOT_PATH);
        zkNode.createNode(ZooKeeperNode.SERVER_PATH);
        zkNode.createNode(ZooKeeperNode.LEADER_PATH);
        zkNode.createNode(ZooKeeperNode.SERVICES_PATH);
        zkNode.createNode(ZooKeeperNode.CONFIG_PATH);
        //
        // Server信息
        zkNode.createNode(this.serverInfoPath);
        zkNode.saveOrUpdate(this.serverInfoPath + "/info", "ddddd");
        //
    }
}