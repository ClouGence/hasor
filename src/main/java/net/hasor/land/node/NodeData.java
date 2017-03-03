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
import net.hasor.core.future.FutureCallback;
import net.hasor.land.bootstrap.LandContext;
import net.hasor.land.domain.NodeStatus;
import net.hasor.land.election.CollectVoteData;
import net.hasor.land.election.CollectVoteResult;
import net.hasor.land.election.LeaderBeatData;
import net.hasor.land.election.LeaderBeatResult;
import net.hasor.land.replicator.DataContext;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfClient;
/**
 * 集群中服务器节点信息
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class NodeData {
    private String         serverID    = null; //服务器ID
    private NodeStatus     nodeStatus  = null; //节点状态
    private LandContext    landContext = null;
    private RsfBindInfo<?> bindInfo    = null;
    private RsfClient      rsfClient   = null;
    //
    protected NodeData(String serverID, LandContext landContext) {
        this.serverID = serverID;
        this.nodeStatus = NodeStatus.Offline;
        this.landContext = landContext;
        this.bindInfo = landContext.getElectionService();
        this.rsfClient = landContext.wrapperApi(serverID);
    }
    //
    /** 获取集群节点名称 */
    public String getServerID() {
        return serverID;
    }
    /** 集群节点是否为当前服务器节点 */
    public boolean isSelf() {
        return this.serverID.equalsIgnoreCase(this.landContext.getServerID());
    }
    //
    //
    /** 请求选票，并获得选票结果(异步) */
    public void collectVote(Server server, DataContext data, final FutureCallback<CollectVoteResult> callBack) {
        CollectVoteData voteData = new CollectVoteData();
        voteData.setServerID(this.landContext.getServerID());
        voteData.setTerm(server.getCurrentTerm());
        //
        this.rsfClient.callBackInvoke(this.bindInfo, "collectVote",//
                new Class[] { CollectVoteData.class },//
                new Object[] { voteData }, //
                new FutureCallback<Object>() {
                    @Override
                    public void completed(Object result) {
                        callBack.completed((CollectVoteResult) result);
                    }
                    @Override
                    public void failed(Throwable ex) {
                        callBack.failed(ex);
                    }
                    @Override
                    public void cancelled() {
                        callBack.cancelled();
                    }
                });
        //
    }
    /** leader心跳(异步) */
    public void leaderHeartbeat(Server server, DataContext data, final FutureCallback<LeaderBeatResult> callBack) {
        LeaderBeatData leaderData = new LeaderBeatData();
        leaderData.setServerID(this.landContext.getServerID());
        leaderData.setTerm(server.getCurrentTerm());
        //
        this.rsfClient.callBackInvoke(this.bindInfo, "leaderHeartbeat",//
                new Class[] { LeaderBeatData.class },//
                new Object[] { leaderData }, //
                new FutureCallback<Object>() {
                    @Override
                    public void completed(Object result) {
                        callBack.completed((LeaderBeatResult) result);
                    }
                    @Override
                    public void failed(Throwable ex) {
                        callBack.failed(ex);
                    }
                    @Override
                    public void cancelled() {
                        callBack.cancelled();
                    }
                });
        //
    }
}