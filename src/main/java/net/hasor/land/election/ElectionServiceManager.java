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
import net.hasor.land.replicator.LogDataContext;
import net.hasor.land.utils.StringUtils;
import net.hasor.land.utils.TermUtils;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * 选举服务,负责选出 Leader
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ElectionServiceManager implements ElectionService, EventListener<ServerStatus> {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private ServerNode     server;
    @Inject
    private LogDataContext dataContext;
    @Inject
    private LandContext    landContext;
    @Inject
    private RsfContext     rsfContext;
    private AtomicBoolean  landStatus;          // 在线状态
    //
    private AtomicBoolean  followerTimer;       // follower 定时器
    private AtomicBoolean  candidateTimer;      // candidate定时器
    private AtomicBoolean  leaderTimer;         // leader   定时器
    //
    //
    @Init
    public void start() {
        this.landStatus = new AtomicBoolean(true);
        this.followerTimer = new AtomicBoolean(false);
        this.candidateTimer = new AtomicBoolean(false);
        this.leaderTimer = new AtomicBoolean(false);
        //
        this.landContext.addStatusListener(this);
        //
        this.startFollowerTimer();
        this.startCandidateTimer();
        this.startLeaderTimer();
        //
        this.landContext.fireStatus(ServerStatus.Follower);
    }
    //
    // --------------------------------------------------------------------------------------------
    // .状态切换事件
    //      当发生角色转换，负责更新各个定时器状态
    public void onEvent(String event, ServerStatus eventData) {
        this.followerTimer.set(false);
        this.candidateTimer.set(false);
        this.leaderTimer.set(false);
        //
        logger.info("Land[Status] - switchTo -> {}.", eventData);
        if (eventData == ServerStatus.Follower) {
            this.followerTimer.set(true);
            return;
        }
        // .一旦成为候选人那么马上进行选举
        if (eventData == ServerStatus.Candidate) {
            this.candidateTimer.set(true);
            return;
        }
        // .一旦成为 Leader 马上发送一个 term 以建立权威,然后通过心跳维持权威
        if (eventData == ServerStatus.Leader) {
            this.leaderTimer.set(true);
            return;
        }
    }
    // --------------------------------------------------------------------------------------------
    // .follower
    //      startFollowerTimer      启动定时器
    //      processFollowerTimer    定时器的循环调用
    //      processFollower         follower 逻辑代码
    private void startFollowerTimer() {
        if (!this.followerTimer.compareAndSet(false, true)) {
            this.logger.error("Land[Follower] - followerTimer -> already started");
            return;
        }
        this.logger.info("Land[Follower] - start followerTimer.");
        final long lastLeaderHeartbeat = this.server.getLastLeaderHeartbeat();
        this.landContext.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                processFollowerTimer(lastLeaderHeartbeat);
            }
        });
    }
    private void processFollowerTimer(long lastLeaderHeartbeat) {
        // .如果系统退出，那么结束定时器循环
        if (!this.landStatus.get()) {
            return;
        }
        // .执行 Follower 任务
        try {
            this.processFollower(lastLeaderHeartbeat);
        } catch (Exception e) {
            logger.error("Land[Follower] - " + e.getMessage(), e);
        }
        // .重启定时器
        final long curLeaderHeartbeat = this.server.getLastLeaderHeartbeat();
        this.landContext.atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                processFollowerTimer(curLeaderHeartbeat);
            }
        });
    }
    private void processFollower(long lastLeaderHeartbeat) {
        if (!this.followerTimer.get()) {
            return;
        }
        //
        // .如果已经不在处于追随者,那么放弃后续处理
        if (this.server.getStatus() != ServerStatus.Follower) {
            this.logger.info("Land[Follower] -> server mast be Follower, but ->" + this.server.getStatus());
            return;
        }
        // .清空计票(Follower不需要计票)
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            nodeData.setVoteGranted(false);
        }
        //
        // .判断启动定时器之后是否收到最新的 Leader 心跳 ,如果收到了心跳,那么放弃后续处理维持 Follower 状态
        //      (新的Leader心跳时间比启动定时器之前心跳时间要新,即为收到了心跳)
        boolean leaderHeartbeat = this.server.getLastLeaderHeartbeat() > lastLeaderHeartbeat;
        if (leaderHeartbeat) {
            printLeader();
            return;
        }
        //
        // .确保状态从 Follower 切换到 Candidate
        this.logger.info("Land[Follower] -> initiate the election.");
        if (this.server.getStatus() == ServerStatus.Follower) {
            this.landContext.fireStatus(ServerStatus.Candidate);
        }
    }
    // --------------------------------------------------------------------------------------------
    // .candidate
    //      startCandidateTimer     启动定时器
    //      processCandidateTimer   定时器的循环调用
    //      processCandidate        candidate 逻辑代码
    private void startCandidateTimer() {
        if (!this.candidateTimer.compareAndSet(false, true)) {
            this.logger.error("Land[Candidate] - candidateTimer -> already started");
            return;
        }
        this.logger.info("Land[Candidate] - start candidateTimer.");
        this.landContext.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                processCandidateTimer();
            }
        });
    }
    private void processCandidateTimer() {
        // .如果系统退出，那么结束定时器循环
        if (!this.landStatus.get()) {
            return;
        }
        // .执行 Candidate 任务
        try {
            this.processCandidate();
        } catch (Exception e) {
            logger.error("Land[Candidate] - " + e.getMessage(), e);
        }
        // .重启定时器
        this.landContext.atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                processCandidateTimer();
            }
        }, genTimeout());
    }
    private void processCandidate() {
        // .候选人会继续保持着当前状态直到以下三件事情之一发生：
        //  (a) 他自己赢得了这次的选举，    -> 成为 Leader
        //  (b) 其他的服务器成为领导者，    -> 成为 Follower
        //  (c) 一段时间之后没有任何获胜的， -> 重新开始选举
        if (!this.candidateTimer.get()) {
            return;
        }
        //
        // .尝试成为 Leader( 返回true表示赢得了这次的选举 )
        boolean tryToLeader = this.testToLeader();
        if (tryToLeader) {
            logger.info("Land[Candidate] -> {} server was elected leader.", this.landContext.getServerID());
            this.landContext.fireStatus(ServerStatus.Leader);
            return;
        }
        //
        // .term自增
        this.server.incrementAndGetTerm();
        this.logger.info("Land[Candidate] -> solicit votes , current Trem is {}", this.server.getCurrentTerm());
        //
        // .清空当前投票、发起选举,征集选票
        this.landContext.fireVotedFor(null);
        CollectVoteData voteData = new CollectVoteData();
        voteData.setServerID(this.landContext.getServerID());
        voteData.setTerm(this.server.getCurrentTerm());
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            // .如果目标是自己，那么直接投给自己
            if (this.landContext.getServerID().equalsIgnoreCase(nodeData.getServerID())) {
                this.landContext.fireVotedFor(voteData.getServerID());
                nodeData.setVoteGranted(true);
                continue;
            }
            //
            // .征集选票(Message模式RPC)
            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            doRequestVote(voteData, electionService);
            //
        }
        //
    }
    // --------------------------------------------------------------------------------------------
    // .leader
    //      startLeaderTimer        启动定时器
    //      processLeaderTimer      定时器的循环调用
    //      processLeader           leader 逻辑代码
    private void startLeaderTimer() {
        if (!this.leaderTimer.compareAndSet(false, true)) {
            this.logger.error("Land[Leader] - leaderTimer -> already started");
            return;
        }
        this.logger.info("Land[Leader] - start leaderTimer.");
        this.landContext.atTime(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                processLeaderTimer();
            }
        });
    }
    private void processLeaderTimer() {
        // .如果系统退出，那么结束定时器循环
        if (!this.landStatus.get()) {
            return;
        }
        // .执行 Leader 任务
        try {
            this.processLeader();
        } catch (Exception e) {
            logger.error("Land[Leader] - " + e.getMessage(), e);
        }
        // .重启定时器
        this.landContext.atTime(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                processLeaderTimer();
            }
        });
    }
    private void processLeader() {
        if (!this.leaderTimer.get()) {
            return;
        }
        //
        LeaderBeatData leaderData = new LeaderBeatData();
        leaderData.setServerID(this.landContext.getServerID());
        leaderData.setTerm(this.server.getCurrentTerm());
        leaderData.setCommitIndex(this.dataContext.getCommitTerm());
        //
        printLeader();
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
            // .发送Leader心跳包
            ElectionService electionService = nodeData.getElectionService(this.rsfContext);
            doHeartBeet(leaderData, electionService);
        }
        //
    }
    // --------------------------------------------------------------------------------------------
    // .拉选票
    //      requestVote             收到拉票请求，做出回应
    //      responseVote            收到拉票结果，尝试成为Leader
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
            logger.info("Land[Vote] -> reject to {} votes. cause: currentTerm({}) > remoteTerm({})", //
                    targetServerID, selfTerm, remoteTerm);
            voteResult.setVoteGranted(false);
            pushVoteResult(targetServerID, voteResult);// .发送结果消息
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
            logger.info("Land[Vote] -> accept votes from {} votes.", //
                    targetServerID, selfTerm, remoteTerm);
            this.landContext.fireVotedFor(voteData.getServerID());
            voteResult.setVoteGranted(true);
        } else {
            logger.info("Land[Vote] -> reject votes from {} votes. cause : remote is {} ,local is {}", //
                    targetServerID, remoteTerm, selfTerm);
            voteResult.setVoteGranted(false);
        }
        //
        pushVoteResult(targetServerID, voteResult);// .发送结果消息
        return null;
    }
    public RsfResult responseVote(CollectVoteResult voteData) {
        NodeData data = this.findTargetServer(voteData.getServerID());
        if (data == null) {
            return null;
        }
        //
        if (ServerStatus.Candidate != this.server.getStatus()) {
            logger.info("Land[Vote] -> received {} of the vote({}), but status is invalid.", data.getServerID(), voteData.isVoteGranted());
            return null;
        }
        //
        // .回填选票结果，重新计票，然后 确定是否得到了多数派的确认,如果得到多数派投票,立刻成为 leader。
        boolean voteGranted = voteData.isVoteGranted();
        logger.info("Land[Vote] -> received {} of the vote, voteGranted is {}.", data.getServerID(), voteGranted);
        data.setVoteGranted(voteGranted);
        if (voteGranted) {
            boolean tryToLeader = this.testToLeader();
            if (tryToLeader) {
                logger.info("Land[Vote] -> {} server was elected leader.", this.landContext.getServerID());
                this.landContext.fireStatus(ServerStatus.Leader);
            }
        }
        return null;
    }
    // --------------------------------------------------------------------------------------------
    // .来自 Leader 的心跳
    //      heartbeatForLeader      Leader心跳
    //      heartbeatResponse       接受心跳回应的结果
    public RsfResult heartbeatForLeader(LeaderBeatData leaderBeatData) {
        String selfTerm = this.server.getCurrentTerm();
        String targetServerID = leaderBeatData.getServerID();
        NodeData data = this.findTargetServer(targetServerID);
        if (data == null) {
            return null;
        }
        //
        // .如果收到 Leader 的心跳中 term 比自己高,或者和自己相等。那么就追随这个 Leader
        //      - 追随 Leader 会更新 自己的 term 和 Leader 一样
        boolean toFollower = false;
        String remoteCommitIndex = leaderBeatData.getCommitIndex();
        String remoteTerm = leaderBeatData.getTerm();
        if (this.server.updateTermTo(remoteTerm) || selfTerm.equalsIgnoreCase(remoteTerm)) {
            this.server.newLastLeaderHeartbeat();
            toFollower = true;
        }
        //
        // .处理拒绝 Leader 的心跳包
        ElectionService electionService = data.getElectionService(this.rsfContext);
        LeaderBeatResult leaderBeatResult = new LeaderBeatResult();
        leaderBeatResult.setServerID(this.landContext.getServerID());
        if (!toFollower) {
            leaderBeatResult.setAccept(false);
            doHeardBeetResponse(electionService, leaderBeatResult);
            return null;
        }
        //
        // .转换为 Follower 身份,并响应心跳包
        if (ServerStatus.Follower != this.server.getStatus()) {
            this.landContext.fireStatus(ServerStatus.Follower);
        }
        //
        this.landContext.fireVotedFor(targetServerID);
        leaderBeatResult.setAccept(true);
        doHeardBeetResponse(electionService, leaderBeatResult);
        return null;
    }
    public RsfResult heartbeatResponse(LeaderBeatResult leaderBeatResult) {
        String targetServerID = leaderBeatResult.getServerID();
        NodeData data = this.findTargetServer(targetServerID);
        if (data == null) {
            return null;
        }
        data.setNodeStatus(NodeStatus.Online);
        data.setVoteGranted(leaderBeatResult.isAccept());//可能失去了这个选票
        return null;
    }
    //
    //
    //
    //
    // --------------------------------------------------------------------------------------------
    /** 测试选票是否够得上成为Leader */
    private boolean testToLeader() {
        // .计票
        int voteGrantedCount = 0;
        List<NodeData> nodeList = this.server.getAllServiceNodes();
        for (NodeData nodeData : nodeList) {
            if (nodeData.isVoteGranted()) {
                voteGrantedCount++;
            }
        }
        return voteGrantedCount * 2 > nodeList.size();
    }
    /** 打印 Leader 信息 */
    private long lastPrintLeaderLog;
    private void printLeader() {
        //
        // .10秒打印一次 Leader 的心跳
        boolean printLeaderLog = this.lastPrintLeaderLog + 10000L < System.currentTimeMillis();
        if (printLeaderLog) {
            this.lastPrintLeaderLog = System.currentTimeMillis();
            this.logger.info("Land[Leader] -> leader is {}", this.server.getVotedFor());
        }
    }
    /** 生成最长 (150 ~ 300) 的一个随机数,用作超时时间 */
    public int genTimeout() {
        return new Random(System.currentTimeMillis()).nextInt(150) + 150;
    }
    //
    //
    //
    //
    //
    // --------------------------------------------------------------------------------------------
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
        //
        // .数据响应如果失败不进行重试,由请求拉票的候选人 的超时机制保证重新拉票
        try {
            nodeData.getElectionService(this.rsfContext).responseVote(voteResult);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                e = e.getCause();
            }
            logger.error(e.getMessage());
        }
    }
    private void doHeartBeet(LeaderBeatData leaderData, ElectionService electionService) {
        try {
            electionService.heartbeatForLeader(leaderData);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                e = e.getCause();
            }
            logger.error(e.getMessage());
        }
    }
    private void doRequestVote(CollectVoteData voteData, ElectionService electionService) {
        try {
            electionService.requestVote(voteData);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                e = e.getCause();
            }
            logger.error(e.getMessage());
        }
    }
    private void doHeardBeetResponse(ElectionService electionService, LeaderBeatResult leaderBeatResult) {
        try {
            electionService.heartbeatResponse(leaderBeatResult);
        } catch (Exception e) {
            /* */
        }
    }
}