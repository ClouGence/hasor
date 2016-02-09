/* Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License. */
package net.hasor.rsf.center.core.leader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
import net.hasor.rsf.center.core.leader.LeaderElectionSupport.EventType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
/**
 * Leader选举。
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
public class LeaderModule implements LifeModule, LeaderElectionAware {
    protected Logger     logger = LoggerFactory.getLogger(getClass());
    private RsfCenterCfg rsfCenterCfg;
    //
    public LeaderModule() {}
    public LeaderModule(RsfCenterCfg rsfCenterCfg) {
        this.rsfCenterCfg = rsfCenterCfg;
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (this.rsfCenterCfg == null) {
            this.rsfCenterCfg = RsfCenterCfg.buildFormConfig(apiBinder.getEnvironment());
        }
        //
        apiBinder.bindType(LeaderElectionSupport.class).toInstance(new LeaderElectionSupport());
    }
    //
    public void onStart(AppContext appContext) throws Throwable {
        LeaderElectionSupport support = appContext.getInstance(LeaderElectionSupport.class);
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        String serverInfo = this.rsfCenterCfg.getHostAndPort();
        //
        support.setZooKeeper(zkNode.getZooKeeper());
        support.setRootNodeName(ZooKeeperNode.LEADER_PATH);
        support.removeListener(this);
        support.addListener(this);
        support.setHostName(serverInfo);
        support.start();
    }
    public void onStop(AppContext appContext) throws Throwable {
        LeaderElectionSupport support = appContext.getInstance(LeaderElectionSupport.class);
        support.stop();
    }
    @Override
    public void onElectionEvent(EventType eventType) {
        // TODO Auto-generated method stub
    }
}