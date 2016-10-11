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
/**
 * 征集选票数据
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class CollectVoteData {
    private String term        = null; //候选人的任期号
    private String serverID    = null; //请求选票的候选人的 Id
    private String lastApplied = null; //候选人的最后日志条目的索引值
    private String commitIndex = null; //候选人最后日志条目的任期号
    //
    //
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    public String getLastApplied() {
        return lastApplied;
    }
    public void setLastApplied(String lastApplied) {
        this.lastApplied = lastApplied;
    }
    public String getCommitIndex() {
        return commitIndex;
    }
    public void setCommitIndex(String commitIndex) {
        this.commitIndex = commitIndex;
    }
}