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
package net.hasor.land.election;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.land.bootstrap.LandContext;
import net.hasor.land.domain.NodeStatus;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.node.NodeData;
import net.hasor.land.node.ServerNode;
import net.hasor.land.utils.StringUtils;
import net.hasor.land.utils.TermUtils;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfResult;
import net.hasor.rsf.domain.RsfResultDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
/**
 * 选举服务,负责选出 Leader
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ElectionServiceManager implements ElectionService, EventListener<ServerStatus> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private ServerNode  server;
    @Inject
    private LandContext landContext;
    @Inject
    private RsfContext  rsfContext;
    private long        lastPrintLeaderLog;
    //
    //
    @Init
    public void init() {
        this.landContext.addStatusListener(this);
        this.landContext.fireStatus(ServerStatus.Follower);
    }
    /** 状态切换事件 */
    public void onEvent(String event, ServerStatus eventData) throws Throwable {
        logger.info("Land[Status] - switchTo -> {}.", eventData);
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
        this.landContext.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                //
                // .定时器超时,如果已经不在处于追随者,那么放弃后续处理
                if (server.getStatus() != ServerStatus.Follower) {
                    logger.info("Land[Follower] -> server mast be Follower, but ->" + server.getStatus());
                    return;
                }
                // .判断启动定时器之后是否收到最新的 Leader 心跳 ,如果收到了心跳,那么放弃后续处理维持 Follower 状态
                //      (新的Leader心跳时间比启动定时器之前心跳时间要新,即为收到了心跳)
                boolean leaderHeartbeat = server.getLastLeaderHeartbeat() > lastLeaderHeartbeat;
                if (leaderHeartbeat) {
                    logger.info("Land[Follower] -> received the leader a heartbeat. at -> " + lastLeaderHeartbeat);
                    return;
                }
                //
                // .确保状态从 Follower 切换到 Candidate
                if (server.getStatus() == ServerStatus.Follower) {
                    logger.info("Land[Follower] -> switch to Candidate.");
                    landContext.fireStatus(ServerStatus.Candidate);
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
        this.landContext.fireVotedFor(null);//清空投票
        CollectVoteData voteData = new CollectVoteData();
        voteData.setServerID(this.landContext.getServerID());
        voteData.setTerm(this.server.getCurrentTerm());
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            nodeData.setVoteGranted(false);// -清空计票,重新赢的选票
            //
            // .如果拉票的目标是自己，那么直接投给自己选票
            if (landContext.getServerID().equalsIgnoreCase(nodeData.getServerID())) {
                this.landContext.fireVotedFor(voteData.getServerID());
                nodeData.setVoteGranted(true);
                continue;
            }
            //
            // .依次征集选票(Message模式RPC)
            logger.info("Land[Candidate] -> request the votes from {} , current Trem is {}", nodeData.getServerID(), this.server.getCurrentTerm());
            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            RsfResult result = doRequestVote(voteData, electionService);   // 第一次拉票
            if (result == null || !result.isSuccess()) {
                result = doRequestVote(voteData, electionService);         // 第二次拉票
                if (result == null || !result.isSuccess()) {
                    result = doRequestVote(voteData, electionService);     // 第三次拉票
                }
            }
            //
            if (!result.isSuccess()) {
                logger.error("Land[Candidate] -> request the votes error({}) form : " + nodeData.getServerID(), result.getErrorCode());
            }
        }
        //
        // .启动定时器等待定时器超时,以便判断是否继续维持在 Candidate 状态
        this.landContext.atTime(new TimerTask() {
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
    private RsfResult doRequestVote(CollectVoteData voteData, ElectionService electionService) {
        try {
            return electionService.requestVote(voteData);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return new RsfResultDO(0, false);
        }
    }
    /** 生成最长 (150 ~ 300) 的一个随机数,用作超时时间 */
    public int genTimeout() {
        return new Random(System.currentTimeMillis()).nextInt(150) + 150;
    }
    /** 尝试成为 Leader */
    private void tryToLeader() {
        // .只有候选人状态才会统计选票尝试成为leader
        if (this.server.getStatus() != ServerStatus.Candidate) {
            return;
        }
        // .计票
        int voteGrantedCount = 0;
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            if (nodeData.isVoteGranted()) {
                voteGrantedCount++;
            }
        }
        // .判断是否赢得了多数派投票,如果赢得了多数派投票那么转换为 Leader
        if (voteGrantedCount * 2 > nodeList.size()) {
            logger.info("Land[Vote] -> most chose this server {} of {}, elected leader.", voteGrantedCount, nodeList.size());
            this.landContext.fireStatus(ServerStatus.Leader);
        } else {
            logger.info("Land[Vote] -> less than half of the votes {} of {}", voteGrantedCount, nodeList.size());
        }
    }
    /** 维持 Leader 状态 */
    private void processForLeader() {
        //
        LeaderBeatData leaderData = new LeaderBeatData();
        leaderData.setServerID(this.landContext.getServerID());
        leaderData.setTerm(this.server.getCurrentTerm());
        leaderData.setCommitIndex(this.server.getCommitTerm());
        //
        // .10秒打印一次 Leader 的心跳
        boolean printLeaderLog = lastPrintLeaderLog + 10000L < System.currentTimeMillis();
        //
        // .发送心跳log以维持 Leader 权威
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            //
            // .如果心跳目标是自己，那么直接更新心跳时间
            if (this.landContext.getServerID().equalsIgnoreCase(nodeData.getServerID())) {
                this.server.newLastLeaderHeartbeat();
                continue;
            }
            //
            if (printLeaderLog) {
                lastPrintLeaderLog = System.currentTimeMillis();
                logger.info("Land[Leader] -> leader heartbeat to {}", nodeData.getServerID());
            }
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
        this.landContext.atTime(new TimerTask() {
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
        });
    }
    //
    /** 其它候选人发来的,拉票请求 */
    public RsfResult requestVote(CollectVoteData voteData) {
        String selfTerm = this.server.getCurrentTerm();
        String remoteTerm = voteData.getTerm();
        String targetServerID = voteData.getServerID();
        CollectVoteResult voteResult = new CollectVoteResult();
        voteResult.setServerID(this.landContext.getServerID());
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
        boolean votedUnchanged = targetServerID.equalsIgnoreCase(this.server.getVotedFor());
        boolean freshThenMe = TermUtils.gtFirst(selfTerm, remoteTerm) || selfTerm.equalsIgnoreCase(remoteTerm);
        if (votedForStatus || (votedUnchanged && freshThenMe)) {
            this.landContext.fireVotedFor(voteData.getServerID());
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
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            if (nodeData.getServerID().equalsIgnoreCase(targetServerID)) {
                return nodeData;
            }
        }
        return null;
    }
    private void pushVoteResult(String targetServerID, CollectVoteResult voteResult) {
        NodeData nodeData = this.findTargetServer(targetServerID);
        if (nodeData == null) {
            return;
        }
        logger.info("Land[Vote] -> {} request vote ,but vote to the {}.", targetServerID, this.server.getVotedFor());
        //
        // .数据响应如果失败不进行重试,由请求拉票的候选人 的超时机制保证重新拉票
        try {
            nodeData.getElectionService(this.rsfContext).responseVote(voteResult);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    /** 收到集群对于拉票的响应结果 */
    public RsfResult responseVote(CollectVoteResult voteData) {
        NodeData data = this.findTargetServer(voteData.getServerID());
        if (data == null) {
            return null;
        }
        //
        logger.info("Land[Vote] -> request {} vote ,result is {}.", data.getServerID(), voteData.isVoteGranted());
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
        if (this.server.updateTermTo(remoteTerm) || selfTerm.equalsIgnoreCase(remoteTerm)) {
            this.landContext.fireVotedFor(targetServerID);
            this.server.newLastLeaderHeartbeat();
            toFollower = true;
        }
        //
        // .处理拒绝 Leader 的心跳包
        ElectionService electionService = data.getElectionService(this.rsfContext);
        LeaderBeatResult leaderBeatResult = new LeaderBeatResult();
        leaderBeatResult.setServerID(this.landContext.getServerID());
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
            this.landContext.fireStatus(ServerStatus.Follower);
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