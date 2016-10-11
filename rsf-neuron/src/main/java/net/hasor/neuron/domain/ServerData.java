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
package net.hasor.neuron.domain;
import net.hasor.core.EventListener;

import java.util.List;
import java.util.Map;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerData implements EventListener<ServerStatus> {
    private String                serverID            = null; //当前服务器ID
    private String                currentTerm         = null; //服务器最后一次知道的LogID（初始化为 0，持续递增）
    private String                votedFor            = null; //当前获得选票的候选人的 ID
    private Map<String, NodeData> allServiceNodes     = null; //所有服务器节点
    private ServerStatus          status              = null; //当前服务器节点状态
    private int                   timeout             = 1000; //基准心跳时间
    //
    private List<LogData>         logs                = null; //日志条目集
    private String                commitIndex         = null; //已知的,最大的,已经被提交的日志条目的termID
    private String                lastApplied         = null; //最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）
    private long                  lastLeaderHeartbeat = 0;    //最后一次接收到来自Leader的心跳时间
    //
    //
    //
    @Override
    public void onEvent(String event, ServerStatus eventData) throws Throwable {
        this.status = eventData;
    }
    public synchronized void incrementAndGetTerm() {
        this.currentTerm = TermUtils.incrementAndGet(this.currentTerm);
    }
    //
    //
    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    public String getCurrentTerm() {
        return currentTerm;
    }
    public String getVotedFor() {
        return votedFor;
    }
    public void setVotedFor(String votedFor) {
        this.votedFor = votedFor;
    }
    public List<LogData> getLogs() {
        return logs;
    }
    public void setLogs(List<LogData> logs) {
        this.logs = logs;
    }
    public ServerStatus getStatus() {
        return status;
    }
    public String getCommitIndex() {
        return commitIndex;
    }
    public void setCommitIndex(String commitIndex) {
        this.commitIndex = commitIndex;
    }
    public String getLastApplied() {
        return lastApplied;
    }
    public void setLastApplied(String lastApplied) {
        this.lastApplied = lastApplied;
    }
    public Map<String, NodeData> getAllServiceNodes() {
        return allServiceNodes;
    }
    public void setAllServiceNodes(Map<String, NodeData> allServiceNodes) {
        this.allServiceNodes = allServiceNodes;
    }
    public void setCurrentTerm(String currentTerm) {
        this.currentTerm = currentTerm;
    }
    public long getLastLeaderHeartbeat() {
        return lastLeaderHeartbeat;
    }
    public void setLastLeaderHeartbeat(long lastLeaderHeartbeat) {
        this.lastLeaderHeartbeat = lastLeaderHeartbeat;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}