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
/**
 * 征集选票的结果
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class CollectVoteResult {
    private String  serverID    = null;  //服务器ID
    private String  remoteTerm  = null;  //当前任期号，以便于候选人去更新自己的任期号
    private boolean voteGranted = false; //候选人赢得了此张选票时为真
    //
    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    public String getRemoteTerm() {
        return remoteTerm;
    }
    public void setRemoteTerm(String remoteTerm) {
        this.remoteTerm = remoteTerm;
    }
    public boolean isVoteGranted() {
        return voteGranted;
    }
    public void setVoteGranted(boolean voteGranted) {
        this.voteGranted = voteGranted;
    }
}