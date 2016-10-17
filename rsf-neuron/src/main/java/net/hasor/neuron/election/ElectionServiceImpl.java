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
package net.hasor.neuron.election;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.*;
import net.hasor.neuron.bootstrap.NeuronTimerManager;
import net.hasor.neuron.domain.NeuronEvent;
import net.hasor.neuron.domain.NodeStatus;
import net.hasor.neuron.domain.ServerStatus;
import net.hasor.neuron.domain.TermUtils;
import net.hasor.neuron.root.NeuronServer;
import net.hasor.neuron.root.NodeData;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfResult;
import org.more.util.StringUtils;

import java.util.Map;
import java.util.Random;
/**
 * 选举服务,负责选出 Leader
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class ElectionServiceImpl implements ElectionService, EventListener<ServerStatus> {
    @Inject
    private NeuronServer       server;
    @Inject
    private NeuronTimerManager timerManager;
    @Inject
    private EventContext       eventContext;
    @Inject
    private RsfContext         rsfContext;
    //
    //
    @Init
    public void init() {
        this.eventContext.addListener(NeuronEvent.ServerStatus, this);
        //
        // .异步方式触发事件,让节点进入 Follower 状态
        rsfContext.getEnvironment().getEventContext().fireAsyncEvent(NeuronEvent.ServerStatus, ServerStatus.Follower);
    }
    /** 状态切换事件 */
    public void onEvent(String event, ServerStatus eventData) throws Throwable {
        // .一旦进入追随者状态,那么马上启动定时器,等待定时器超时之后进行选举
        if (eventData == ServerStatus.Follower) {
            this.processForFollower();
            return;
        }
        // .一旦成为候选人那么马上进行选举
        if (eventData == ServerStatus.Candidate) {
            this.processForCandidate();
            return;
        }
        // .一旦成为 Leader 马上发送一个 term 以建立权威,然后通过心跳维持权威
        if (eventData == ServerStatus.Leader) {
            this.processForLeader();
            return;
        }
    }
    /** 维持 Follower 状态 */
    private void processForFollower() {
        // .要想维持追随者状态,必须要在定时超时之前收到来自 Leader 的心跳
        final long lastLeaderHeartbeat = this.server.getLastLeaderHeartbeat();
        this.timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                //
                // .定时器超时,如果已经不在处于追随者,那么放弃后续处理
                if (server.getStatus() != ServerStatus.Follower) {
                    return;
                }
                // .判断启动定时器之后是否收到最新的 Leader 心跳 ,如果收到了心跳,那么放弃后续处理维持 Follower 状态
                //      (新的Leader心跳时间比启动定时器之前心跳时间要新,即为收到了心跳)
                boolean leaderHeartbeat = server.getLastLeaderHeartbeat() > lastLeaderHeartbeat;
                if (leaderHeartbeat) {
                    return;
                }
                //
                // .确保状态从 Follower 切换到 Candidate
                if (server.getStatus() == ServerStatus.Follower) {
                    eventContext.fireSyncEvent(NeuronEvent.ServerStatus, ServerStatus.Candidate);
                }
            }
        }, this.genTimeout());
    }
    /** 维持 Candidate 状态 */
    private void processForCandidate() {
        //
        // .term自增
        this.server.incrementAndGetTerm();
        //
        // .发起选举
        this.eventContext.fireSyncEvent(NeuronEvent.VotedFor_Event, null);//清空投票
        CollectVoteData voteData = new CollectVoteData();
        voteData.setServerID(this.server.getServerID());
        voteData.setTerm(this.server.getCurrentTerm());
        Map<String, NodeData> nodeMap = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeMap.values()) {
            //
            // -依次征集选票(Message模式RPC)
            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            RsfResult result = electionService.requestVote(voteData);   // 第一次拉票
            if (result == null || !result.isSuccess()) {
                result = electionService.requestVote(voteData);         // 第二次拉票
                if (result == null || !result.isSuccess()) {
                    result = electionService.requestVote(voteData);     // 第三次拉票
                }
            }
            nodeData.setVoteGranted(false);// -清空计票,重新赢的选票
        }
        //
        // .启动定时器等待定时器超时,以便判断是否继续维持在 Candidate 状态
        this.timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                // .候选人会继续保持着当前状态直到以下三件事情之一发生：
                //  (a) 他自己赢得了这次的选举，    -> 成为 Leader
                //  (b) 其他的服务器成为领导者，    -> 成为 Follower
                //  (c) 一段时间之后没有任何获胜的， -> 重新开始选举
                //
                // .尝试成为 Leader
                tryToLeader();
                //
                // . 如果当前仍然是 Candidate 那么重新激活选举
                if (server.getStatus() == ServerStatus.Candidate) {
                    processForCandidate();
                }
            }
        }, this.genTimeout());
        //
    }
    /** 生成最长 timeout + (150 ~ 300) 的一个随机数,用作超时时间 */
    public int genTimeout() {
        return this.server.getTimeout() + new Random(System.currentTimeMillis()).nextInt(150) + 150;
    }
    /** 尝试成为 Leader */
    private void tryToLeader() {
        // .只有候选人状态才会统计选票尝试成为leader
        if (server.getStatus() != ServerStatus.Candidate) {
            return;
        }
        // .计票
        Map<String, NodeData> nodeMap = this.server.getAllServiceNodes();
        int voteGrantedCount = 0;
        for (NodeData nodeData : nodeMap.values()) {
            if (nodeData.isVoteGranted()) {
                voteGrantedCount++;
            }
        }
        // .判断是否赢得了多数派投票,如果赢得了多数派投票那么转换为 Leader
        if (voteGrantedCount * 2 > nodeMap.size()) {
            eventContext.fireSyncEvent(NeuronEvent.ServerStatus, ServerStatus.Leader);
        }
    }
    /** 维持 Leader 状态 */
    private void processForLeader() {
        //
        LeaderBeatData leaderData = new LeaderBeatData();
        leaderData.setServerID(this.server.getServerID());
        leaderData.setTerm(this.server.getCurrentTerm());
        leaderData.setCommitIndex(this.server.getCommitTerm());
        //
        // .发送心跳log以维持 Leader 权威
        Map<String, NodeData> nodeMap = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeMap.values()) {
            //
            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            RsfResult result = electionService.heartbeatForLeader(leaderData);   // 第一次心跳
            if (result == null || !result.isSuccess()) {
                result = electionService.heartbeatForLeader(leaderData);         // 第二次重试
                if (result == null || !result.isSuccess()) {
                    result = electionService.heartbeatForLeader(leaderData);     // 第三次重试
                }
            }
            // .目标节点状态
            if (nodeData.getNodeStatus() == NodeStatus.Online) {
                nodeData.setNodeStatus(NodeStatus.Uncertainty);
            }
            if (nodeData.getNodeStatus() == NodeStatus.Uncertainty) {
                nodeData.setNodeStatus(NodeStatus.Offline);
            }
        }
        //
        // .启动心跳定时器
        this.timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                //
                // .定时器超时,如果已经不在处于 Leader 那么,那么放弃后续处理
                if (server.getStatus() != ServerStatus.Leader) {
                    return;
                }
                //
                // .重新激活定时器
                processForLeader();
            }
        }, this.server.getTimeout());
    }
    //
    //
    //
    /** 其它候选人发来的,拉票请求 */
    public RsfResult requestVote(CollectVoteData voteData) {
        String selfTerm = this.server.getCurrentTerm();
        String remoteTerm = voteData.getTerm();
        String targetServerID = voteData.getServerID();
        CollectVoteResult voteResult = new CollectVoteResult();
        voteResult.setServerID(this.server.getServerID());
        voteResult.setRemoteTerm(selfTerm);
        //
        // .如果当前服务器比请求者的大那么,拒绝
        if (TermUtils.gtFirst(remoteTerm, selfTerm)) {
            voteResult.setVoteGranted(false);
            // .向目标发送结果数据(RpC 接口在 Message 模式下不需要返回值)
            pushVoteResult(targetServerID, voteResult);
            return null;
        }
        // .如果 votedFor 为空或者就是 candidateId，并且候选人的日志也自己一样新，那么就投票给他
        //      - votedFor 为空               表示尚未投票给任何人。
        //      - votedFor 等于 candidateId   表示投票的目标未改变。
        //      - 候选人的日志(大于等于自己)     表示要么日志比自己新,要么一样
        //
        boolean votedForStatus = StringUtils.isBlank(this.server.getVotedFor());
        boolean votedUnchanged = StringUtils.equalsIgnoreCase(this.server.getVotedFor(), voteData.getServerID());
        boolean freshThenMe = TermUtils.gtFirst(selfTerm, remoteTerm) || StringUtils.equalsIgnoreCase(remoteTerm, selfTerm);
        if (votedForStatus || (votedUnchanged && freshThenMe)) {
            this.eventContext.fireSyncEvent(NeuronEvent.VotedFor_Event, voteData.getServerID());
            voteResult.setVoteGranted(true);
        } else {
            voteResult.setVoteGranted(false);
        }
        //
        // .向目标发送结果数据(RpC 接口在 Message 模式下不需要返回值)
        pushVoteResult(targetServerID, voteResult);
        return null;
    }
    private NodeData findTargetServer(String targetServerID) {
        Map<String, NodeData> nodeMap = this.server.getAllServiceNodes();
        return nodeMap.get(targetServerID);
    }
    private void pushVoteResult(String targetServerID, CollectVoteResult voteResult) {
        NodeData nodeData = this.findTargetServer(targetServerID);
        if (nodeData == null) {
            return;
        }
        //
        // .数据响应如果失败不进行重试,由请求拉票的候选人 的超时机制保证重新拉票
        try {
            nodeData.getElectionService(this.rsfContext).responseVote(voteResult);
        } catch (Exception e) {
            /* */
        }
    }
    /** 收到集群对于拉票的响应结果 */
    public RsfResult responseVote(CollectVoteResult voteData) {
        NodeData data = this.findTargetServer(voteData.getServerID());
        if (data == null) {
            return null;
        }
        // .回填选票结果
        data.setVoteGranted(voteData.isVoteGranted());
        // .计票 & 确定是否得到了多数派的确认,如果得到多数派投票,立刻成为 leader。
        tryToLeader();
        return null;
    }
    /** 接受来自 Leader 的心跳 */
    public RsfResult heartbeatForLeader(LeaderBeatData leaderBeatData) {
        String selfTerm = this.server.getCurrentTerm();
        String remoteTerm = leaderBeatData.getTerm();
        String targetServerID = leaderBeatData.getServerID();
        NodeData data = this.findTargetServer(targetServerID);
        if (data == null) {
            return null;
        }
        //
        // .如果收到 Leader 的心跳中 term 比自己高,或者和自己相等。那么就追随这个 Leader
        //      - 追随 Leader 会更新 自己的 term 和 Leader 一样
        boolean toFollower = false;
        if (this.server.updateTermTo(remoteTerm) || StringUtils.equalsIgnoreCase(remoteTerm, selfTerm)) {
            this.eventContext.fireSyncEvent(NeuronEvent.VotedFor_Event, targetServerID);
            this.server.newLastLeaderHeartbeat();
            toFollower = true;
        }
        //
        // .处理拒绝 Leader 的心跳包
        ElectionService electionService = data.getElectionService(this.rsfContext);
        LeaderBeatResult leaderBeatResult = new LeaderBeatResult();
        leaderBeatResult.setServerID(this.server.getServerID());
        if (!toFollower) {
            try {
                leaderBeatResult.setAccept(false);
                electionService.heartbeatResponse(leaderBeatResult);
            } catch (Exception e) {
            /* */
            }
            return null;
        }
        //
        // .转换为 Follower 身份,并响应心跳包
        if (this.server.getStatus() != ServerStatus.Follower) {
            this.eventContext.fireSyncEvent(NeuronEvent.ServerStatus, ServerStatus.Follower);
        }
        try {
            leaderBeatResult.setAccept(true);
            electionService.heartbeatResponse(leaderBeatResult);
        } catch (Exception e) {
            /* */
        }
        return null;
    }
    /** 接受心跳回应的结果 */
    public RsfResult heartbeatResponse(LeaderBeatResult leaderBeatResult) {
        String targetServerID = leaderBeatResult.getServerID();
        NodeData data = this.findTargetServer(targetServerID);
        if (data == null) {
            return null;
        }
        data.setNodeStatus(NodeStatus.Online);
        data.setVoteGranted(leaderBeatResult.isAccept()); //可能失去了这个选票
        return null;
    }
}