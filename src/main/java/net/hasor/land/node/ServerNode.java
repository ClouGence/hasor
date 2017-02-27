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
import net.hasor.core.EventListener;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.land.bootstrap.LandContext;
import net.hasor.land.domain.LandEvent;
import net.hasor.land.domain.ServerStatus;
import net.hasor.land.utils.TermUtils;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerNode implements EventListener<Object> {
    protected Logger         logger              = LoggerFactory.getLogger(getClass());
    @Inject
    private   LandContext    landContext         = null;
    private   List<NodeData> allServiceNodes     = null; //所有服务器节点
    private   String         currentTerm         = null; //服务器最后一次知道的LogID（初始化为 0，持续递增）
    private   String         votedFor            = null; //当前获得选票的候选人的 ID
    private   ServerStatus   status              = null; //当前服务器节点状态
    private   long           lastLeaderHeartbeat = 0;    //最后一次接收到来自Leader的心跳时间
    private   String         commitTerm          = null; //已知的,最大的,已经被提交的日志条目的termID
    //
    /** 当前条目ID(已递交，已生效) */
    public String getCurrentTerm() {
        return currentTerm;
    }
    /** 当前服务器的选票投给了谁 */
    public String getVotedFor() {
        return votedFor;
    }
    /** 已知的最大提交日志条目的termID */
    public String getCommitTerm() {
        return commitTerm;
    }
    /** 当前服务器节点状态 */
    public ServerStatus getStatus() {
        return status;
    }
    /** 最后一次 Leader 发来的心跳时间 */
    public long getLastLeaderHeartbeat() {
        return lastLeaderHeartbeat;
    }
    /** 获取所有配置的集群节点 */
    public List<NodeData> getAllServiceNodes() {
        return allServiceNodes;
    }
    //
    @Init
    public void init() throws URISyntaxException {
        //
        this.currentTerm = "0";
        this.votedFor = null;
        this.status = ServerStatus.Follower;
        this.landContext.addStatusListener(this);
        //
        // .集群信息
        String services = this.landContext.getSettings("hasor.land.servers");
        Map<String, InterAddress> servers = new HashMap<String, InterAddress>();
        if (StringUtils.isNotBlank(services)) {
            String[] serverArrays = services.split(",");
            for (String serverInfo : serverArrays) {
                serverInfo = serverInfo.trim();
                String[] infos = serverInfo.split(":");
                if (infos.length != 3) {
                    continue;
                }
                String serverID = serverInfo.substring(0, infos[0].length());
                String serverTarget = serverInfo.substring(serverID.length() + 1);
                servers.put(serverID, new InterAddress("rsf://" + serverTarget + "/default"));
            }
        }
        // .添加节点
        this.allServiceNodes = new ArrayList<NodeData>();
        for (Map.Entry<String, InterAddress> entry : servers.entrySet()) {
            String serverID = entry.getKey();
            InterAddress serverAddress = entry.getValue();
            NodeData serverNode = new NodeData(serverID, serverAddress);
            this.allServiceNodes.add(serverNode);
        }
    }
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
}