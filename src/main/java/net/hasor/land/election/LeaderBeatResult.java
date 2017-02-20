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
 * 心跳回应包
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class LeaderBeatResult {
    private String  serverID = null;  //服务器ID
    private boolean accept   = false; //候选人赢得了此张选票时为真
    //
    public String getServerID() {
        return serverID;
    }
    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
    public boolean isAccept() {
        return accept;
    }
    public void setAccept(boolean accept) {
        this.accept = accept;
    }
}