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
import net.hasor.neuron.election.ElectionService;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;

import java.util.Date;
/**
 * 集群中服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class NodeData {
    private String       serverID          = null; //服务器ID
    private boolean      voteGranted       = false;//是否赢得了这台服务器的选票
    private Date         lastHeartbeatTime = null; //最后心跳时间
    private InterAddress interAddress      = null; //服务器地址
    private boolean      isOnline          = false;//服务器是否在线
    //
    private String       nextTermIndex     = null; //发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）
    private String       maxTermIndex      = null; //已经复制给他的日志的最高索引值
    //
    //
    public ElectionService getElectionService(RsfContext rsfContext) {
        return rsfContext.getRsfClient(this.interAddress).wrapper(ElectionService.class);
    }
    //
    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    public boolean isVoteGranted() {
        return voteGranted;
    }
    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }
    public void setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }
    public InterAddress getInterAddress() {
        return interAddress;
    }
    public void setInterAddress(InterAddress interAddress) {
        this.interAddress = interAddress;
    }
    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
    public String getNextTermIndex() {
        return nextTermIndex;
    }
    public void setNextTermIndex(String nextTermIndex) {
        this.nextTermIndex = nextTermIndex;
    }
    public String getMaxTermIndex() {
        return maxTermIndex;
    }
    public void setMaxTermIndex(String maxTermIndex) {
        this.maxTermIndex = maxTermIndex;
    }
}