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
import net.hasor.core.EventContext;
import net.hasor.core.EventListener;
import net.hasor.core.Inject;
import net.hasor.neuron.domain.*;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfResult;
import net.hasor.rsf.utils.TimerManager;
import org.more.util.StringUtils;

import java.util.Map;
/**
 * 选举服务,负责选出 Leader
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ElectionServiceImpl implements ElectionService, EventListener<ServerStatus> {
    @Inject
    private ServerData   serverData;
    @Inject
    private TimerManager timerManager;
    @Inject
    private EventContext eventContext;
    @Inject
    private RsfContext   rsfContext;
    //
    //
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
        final long lastLeaderHeartbeat = this.serverData.getLastLeaderHeartbeat();
        this.timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                //
                // .定时器超时,如果已经不在处于追随者,那么放弃后续处理
                if (serverData.getStatus() != ServerStatus.Follower) {
                    return;
                }
                // .判断启动定时器之后是否收到最新的 Leader 心跳 ,如果收到了心跳,那么放弃后续处理维持 Follower 状态
                //      (新的Leader心跳时间比启动定时器之前心跳时间要新,即为收到了心跳)
                boolean leaderHeartbeat = serverData.getLastLeaderHeartbeat() > lastLeaderHeartbeat;
                if (leaderHeartbeat) {
                    return;
                }
                //
                // .确保状态从 Follower 切换到 Candidate
                if (serverData.getStatus() == ServerStatus.Follower) {
                    eventContext.fireSyncEvent(NeuronEvent.ServerStatus, ServerStatus.Candidate);
                }
            }
        }, this.serverData.getTimeout());
    }
    //
    /** 维持 Candidate 状态 */
    private void processForCandidate() {
        //
        // .term自增
        this.serverData.incrementAndGetTerm();
        //
        // .发起选举
        this.serverData.setVotedFor(null);//清空投票
        CollectVoteData voteData = new CollectVoteData();
        voteData.setTerm(this.serverData.getCurrentTerm());
        voteData.setServerID(this.serverData.getServerID());
        voteData.setCommitIndex(this.serverData.getCommitIndex());
        voteData.setLastApplied(this.serverData.getLastApplied());
        Map<String, NodeData> nodeMap = this.serverData.getAllServiceNodes();
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
                if (serverData.getStatus() == ServerStatus.Candidate) {
                    processForCandidate();
                }
            }
        }, this.serverData.getTimeout());
        //
    }
    /** 尝试成为 Leader */
    private void tryToLeader() {
        // .只有候选人状态才会统计选票尝试成为leader
        if (serverData.getStatus() != ServerStatus.Candidate) {
            return;
        }
        // .计票
        Map<String, NodeData> nodeMap = this.serverData.getAllServiceNodes();
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
        // .发送心跳log以维持 Leader 权威
        Map<String, NodeData> nodeMap = this.serverData.getAllServiceNodes();
        for (NodeData nodeData : nodeMap.values()) {
            //            //
            //            // -依次征集选票(Message模式RPC)
            //            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            //            RsfResult result = electionService.requestVote(voteData);   // 第一次拉票
            //            if (result == null || !result.isSuccess()) {
            //                result = electionService.requestVote(voteData);         // 第二次拉票
            //                if (result == null || !result.isSuccess()) {
            //                    result = electionService.requestVote(voteData);     // 第三次拉票
            //                }
            //            }
            //            nodeData.setVoteGranted(false);// -无论如何都需要重新赢的服务器的选票
        }
        //
        // .启动心跳定时器
        this.timerManager.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                //
                // .定时器超时,如果已经不在处于 Leader 那么,那么放弃后续处理
                if (serverData.getStatus() != ServerStatus.Leader) {
                    return;
                }
                //
                // .重新激活定时器
                processForLeader();
            }
        }, this.serverData.getTimeout());
    }
    //
    //
    //
    /** 其它候选人发来的,拉票请求 */
    public RsfResult requestVote(CollectVoteData voteData) {
        String selfTerm = this.serverData.getCurrentTerm();
        String remoteTerm = voteData.getTerm();
        String targetServerID = voteData.getServerID();
        CollectVoteResult voteResult = new CollectVoteResult();
        voteResult.setServerID(this.serverData.getServerID());
        voteResult.setRemoteTerm(selfTerm);
        //
        // .如果当前服务器比请求者的大那么,拒绝
        if (TermUtils.gtFirst(remoteTerm, selfTerm)) {
            voteResult.setVoteGranted(false);
            return pushVoteResult(targetServerID, voteResult);
        }
        // .如果 votedFor 为空或者就是 candidateId，并且候选人的日志也自己一样新，那么就投票给他
        //      - votedFor 为空               表示尚未投票给任何人。
        //      - votedFor 等于 candidateId   表示投票的目标未改变。
        //      - 候选人的日志(大于等于自己)     表示要么日志比自己新,要么一样
        //
        boolean votedForStatus = StringUtils.isBlank(this.serverData.getVotedFor());
        boolean votedUnchanged = StringUtils.equalsIgnoreCase(this.serverData.getVotedFor(), voteData.getServerID());
        boolean freshThenMe = TermUtils.gtFirst(selfTerm, remoteTerm) || StringUtils.equalsIgnoreCase(remoteTerm, selfTerm);
        if (votedForStatus || (votedUnchanged && freshThenMe)) {
            this.serverData.setVotedFor(voteData.getServerID());
            voteResult.setVoteGranted(true);
        } else {
            voteResult.setVoteGranted(false);
        }
        //
        return pushVoteResult(targetServerID, voteResult);
    }
    private RsfResult pushVoteResult(String targetServerID, CollectVoteResult voteResult) {
        Map<String, NodeData> nodeMap = this.serverData.getAllServiceNodes();
        NodeData nodeData = nodeMap.get(targetServerID);
        //
        // .数据响应如果失败不进行重试,由请求拉票的候选人 的超时机制保证重新拉票
        try {
            nodeData.getElectionService(this.rsfContext).responseVote(voteResult);
        } catch (Exception e) {
            /* */
        }
        return null;
    }
    //
    /** 收到集群对于拉票的响应结果 */
    public RsfResult responseVote(CollectVoteResult voteData) {
        Map<String, NodeData> nodeMap = this.serverData.getAllServiceNodes();
        NodeData data = nodeMap.get(voteData.getServerID());
        //
        // .回填选票结果
        if (data != null) {
            data.setVoteGranted(voteData.isVoteGranted());
        }
        // .计票 & 确定是否得到了多数派的确认,如果得到多数派投票,立刻成为 leader。
        tryToLeader();
        return null;
    }
}