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
package net.hasor.land.node;
import net.hasor.core.*;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.utils.TermUtils;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.StringUtils;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ServerNode implements EventListener<Object>, AskNameService {
    @InjectSettings(value = "hasor.rsfNeuron.serviceID", defaultValue = "local")
    private String         serverID            = null; //当前服务器ID
    @InjectSettings(value = "rsfNeuron.timeout", defaultValue = "500")
    private int            timeout             = 1000; //基准心跳时间
    @Inject
    private RsfContext     rsfContext          = null;
    @Inject
    private ClusterManager clusterManager      = null; //所有服务器节点
    private String         currentTerm         = null; //服务器最后一次知道的LogID（初始化为 0，持续递增）
    private String         votedFor            = null; //当前获得选票的候选人的 ID
    private ServerStatus   status              = null; //当前服务器节点状态
    private long           lastLeaderHeartbeat = 0;    //最后一次接收到来自Leader的心跳时间
    private String         commitTerm          = null; //已知的,最大的,已经被提交的日志条目的termID
    //
    @Init
    public void init() throws UnknownHostException, URISyntaxException {
        // .基础属性初始化
        if ("local".equalsIgnoreCase(this.serverID)) {
            this.serverID = NetworkUtils.finalBindAddress(this.serverID).getHostAddress();
        }
        this.currentTerm = "0";
        this.votedFor = null;
        this.status = ServerStatus.Follower;
        this.rsfContext.getEnvironment().getEventContext().addListener(LandEvent.ServerStatus, this);
        // .集群信息
        String serverSetting = this.rsfContext.getEnvironment().getSettings().getString("hasor.land.servers");
        String[] serverArrays = null;
        if (StringUtils.isBlank(serverSetting)) {
            serverArrays = new String[0];
        } else {
            serverArrays = serverSetting.split(",");
        }
        // .添加节点
        String defaultProtocol = this.rsfContext.getDefaultProtocol();
        this.clusterManager.addNode(this.rsfContext.bindAddress(defaultProtocol));
        for (String server : serverArrays) {
            this.clusterManager.addNode(new InterAddress((server)));
        }
    }
    //
    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (eventData == null) {
            return;
        }
        if (LandEvent.ServerStatus.equalsIgnoreCase(event)) {
            this.status = (ServerStatus) eventData;
        }
        if (LandEvent.VotedFor_Event.equalsIgnoreCase(event)) {
            this.votedFor = (String) eventData;
        }
    }
    /** Term 自增 */
    public synchronized void incrementAndGetTerm() {
        this.currentTerm = TermUtils.incrementAndGet(this.currentTerm);
    }
    /** Term 更新成比自己大的那个 */
    public synchronized boolean updateTermTo(String remoteTerm) {
        if (TermUtils.gtFirst(this.currentTerm, remoteTerm)) {
            this.currentTerm = remoteTerm;
            return true;
        }
        return false;
    }
    public void newLastLeaderHeartbeat() {
        this.lastLeaderHeartbeat = System.currentTimeMillis();
    }
    //
    //
    @Override
    public String askServerID() {
        return this.getServerID();
    }
    public String getServerID() {
        return serverID;
    }
    public String getCurrentTerm() {
        return currentTerm;
    }
    public String getVotedFor() {
        return votedFor;
    }
    public String getCommitTerm() {
        return commitTerm;
    }
    public ServerStatus getStatus() {
        return status;
    }
    public int getTimeout() {
        return timeout;
    }
    public long getLastLeaderHeartbeat() {
        return lastLeaderHeartbeat;
    }
    //
    //
    public void setCommitTerm(String commitTerm) {
        this.commitTerm = commitTerm;
    }
}