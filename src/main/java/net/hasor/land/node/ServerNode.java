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
import io.netty.util.internal.ConcurrentSet;
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.land.bootstrap.LandContext;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.utils.TermUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerNode implements EventListener<Object>, Operation {
    protected     Logger         logger        = LoggerFactory.getLogger(getClass());
    @Inject
    private       LandContext    landContext   = null;
    private       List<NodeData> allNodes      = null; //所有服务器节点
    private       Set<String>    supporterVote = null; //支持者的选票
    //
    private final Object         lock          = new Object();
    private       ServerStatus   status        = null; //当前状态
    private       String         currentTerm   = null; //当前任期
    private       String         votedFor      = null; //得票候选人ID
    private       long           lastHeartbeat = 0;    //最后一次来自Leader的心跳时间
    //
    //
    //
    /** 当前条目ID(已递交，已生效) */
    public String getCurrentTerm() {
        return currentTerm;
    }
    /** 当前服务器的选票投给了谁 */
    public String getVotedFor() {
        return votedFor;
    }
    /** 当前服务器节点状态 */
    public ServerStatus getStatus() {
        return status;
    }
    /** 最后一次 Leader 发来的心跳时间 */
    public long getLastHeartbeat() {
        return lastHeartbeat;
    }
    /** 获取所有在线状态的节点 */
    public List<NodeData> getOnlineNodes() {
        return allNodes;
    }
    //
    //
    //
    @Init
    public void init() throws URISyntaxException {
        //
        this.currentTerm = "0";
        this.votedFor = null;
        this.status = ServerStatus.Follower;
        this.landContext.addVotedListener(this);
        this.landContext.addStatusListener(this);
        //
        // .添加节点
        this.allNodes = new ArrayList<NodeData>();
        Collection<String> serverIDs = this.landContext.getServerIDs();
        for (String serverID : serverIDs) {
            this.allNodes.add(new NodeData(serverID, this.landContext));
        }
        //
        this.supporterVote = new ConcurrentSet<String>();
    }
    //
    //
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
    public void lockRun(RunLock runnable) {
        synchronized (this.lock) {
            runnable.run(this);
        }
    }
    @Override
    public boolean testVote(String serverID) {
        return this.supporterVote.contains(serverID);
    }
    //
    //
    //
    /** Term 自增 */
    public void incrementAndGetTerm() {
        this.currentTerm = TermUtils.incrementAndGet(this.currentTerm);
    }
    /** Term 更新成比自己大的那个 */
    public boolean updateTermTo(String remoteTerm) {
        if (TermUtils.gtFirst(this.currentTerm, remoteTerm)) {
            this.currentTerm = remoteTerm;
            return true;
        }
        return false;
    }
    /** 更新最后一次收到 Leader 的心跳时间 */
    public void newLastLeaderHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
    @Override
    public void applyVoted(String serverID, boolean voteGranted) {
        if (voteGranted) {
            this.supporterVote.add(serverID);
        } else {
            this.supporterVote.remove(serverID);
        }
    }
    @Override
    public void clearVoted() {
        this.supporterVote.clear();
    }
}