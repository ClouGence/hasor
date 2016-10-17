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
import net.hasor.neuron.domain.NodeStatus;
import net.hasor.neuron.election.ElectionService;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
/**
 * 集群中服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class NodeData {
    private String          serverID        = null; //服务器ID
    private InterAddress    interAddress    = null; //服务器地址
    private boolean         voteGranted     = false;//是否赢得了这台服务器的选票
    private NodeStatus      nodeStatus      = NodeStatus.Offline; //节点状态
    //
    private ElectionService electionService = null;
    public ElectionService getElectionService(RsfContext rsfContext) {
        if (this.electionService == null) {
            this.electionService = rsfContext.getRsfClient(this.interAddress).wrapper(ElectionService.class);
        }
        return this.electionService;
    }
    //
    public String getServerID() {
        return serverID;
    }
    public InterAddress getInterAddress() {
        return interAddress;
    }
    public NodeStatus getNodeStatus() {
        return nodeStatus;
    }
    public boolean isVoteGranted() {
        return voteGranted;
    }
    //
    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
    public void setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
    }
}