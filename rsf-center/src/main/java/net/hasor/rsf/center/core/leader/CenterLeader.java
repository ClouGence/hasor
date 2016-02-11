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
package net.hasor.rsf.center.core.leader;
import static net.hasor.rsf.center.domain.constant.CenterEventType.Center_Stop_Event;
import static net.hasor.rsf.center.domain.constant.CenterEventType.ZooKeeper_SyncConnected;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.InjectMembers;
import net.hasor.plugins.event.Event;
import net.hasor.rsf.center.core.leader.LeaderElectionSupport.EventType;
import net.hasor.rsf.center.core.zookeeper.ZooKeeperNode;
import net.hasor.rsf.center.domain.constant.RsfCenterCfg;
/**
 * Leader选举。
 * 
 * @version : 2015年5月5日
 * @author 赵永春(zyc@hasor.net)
 */
@Event({ ZooKeeper_SyncConnected, Center_Stop_Event })
public class CenterLeader implements EventListener, LeaderElectionAware, InjectMembers {
    protected Logger              logger  = LoggerFactory.getLogger(getClass());
    private LeaderElectionSupport support = new LeaderElectionSupport();
    //
    @Override
    public void doInject(AppContext appContext) throws Throwable {
        RsfCenterCfg rsfCenterCfg = appContext.getInstance(RsfCenterCfg.class);
        ZooKeeperNode zkNode = appContext.getInstance(ZooKeeperNode.class);
        String serverInfo = rsfCenterCfg.getHostAndPort();
        //
        this.support = appContext.getInstance(LeaderElectionSupport.class);
        this.support.setZooKeeper(zkNode.getZooKeeper());
        this.support.setRootNodeName(ZooKeeperNode.LEADER_PATH);
        this.support.addListener(this);
        this.support.setHostName(serverInfo);
    }
    //
    @Override
    public void onEvent(String event, Object[] params) throws Throwable {
        /*  */if (StringUtils.equals(event, ZooKeeper_SyncConnected)) {
            this.support.start();
        } else if (StringUtils.equals(event, Center_Stop_Event)) {
            this.support.stop();
        }
    }
    @Override
    public void onElectionEvent(EventType eventType) {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + eventType);
        // TODO Auto-generated method stub
        try {
            System.out.println("@@@@@@@@@@@@@@@@" + this.support.getLeaderHostName());
        } catch (Exception e) {
            System.out.println("@@@@@@@@@@@@@@@@- Error:" + e.getMessage());
            // TODO: handle exception
        }
    }
}