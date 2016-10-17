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
package net.hasor.neuron.root;
import net.hasor.core.*;
import net.hasor.neuron.domain.NeuronEvent;
import net.hasor.neuron.domain.ServerStatus;
import net.hasor.neuron.domain.TermUtils;
import org.more.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class NeuronServer implements EventListener<Object> {
    @InjectSettings(value = "rsfNeuron.timeout", defaultValue = "500")
    private String                serverID            = null; //当前服务器ID
    @InjectSettings(value = "rsfNeuron.timeout", defaultValue = "500")
    private int                   timeout             = 1000; //基准心跳时间
    @Inject
    private AppContext            appContext          = null;
    //
    private Map<String, NodeData> allServiceNodes     = null; //所有服务器节点
    private String                currentTerm         = null; //服务器最后一次知道的LogID（初始化为 0，持续递增）
    private String                votedFor            = null; //当前获得选票的候选人的 ID
    private ServerStatus          status              = null; //当前服务器节点状态
    private long                  lastLeaderHeartbeat = 0;    //最后一次接收到来自Leader的心跳时间
    private String                commitTerm          = null; //已知的,最大的,已经被提交的日志条目的termID
    //
    //
    //
    @Init
    public void init() {
        // .基础属性
        this.serverID = "";
        this.currentTerm = "0";
        this.votedFor = null;
        this.allServiceNodes = new HashMap<String, NodeData>();
        this.status = ServerStatus.Follower;
        //
        appContext.getEnvironment().getEventContext().addListener(NeuronEvent.ServerStatus, this);
    }
    @Override
    public void onEvent(String event, Object eventData) throws Throwable {
        if (eventData == null) {
            return;
        }
        if (StringUtils.equalsIgnoreCase(event, NeuronEvent.ServerStatus)) {
            this.status = (ServerStatus) eventData;
        }
        if (StringUtils.equalsIgnoreCase(event, NeuronEvent.VotedFor_Event)) {
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
    public Map<String, NodeData> getAllServiceNodes() {
        return allServiceNodes;
    }
    public void setCommitTerm(String commitTerm) {
        this.commitTerm = commitTerm;
    }
    public void setAllServiceNodes(Map<String, NodeData> allServiceNodes) {
        this.allServiceNodes = allServiceNodes;
    }
}