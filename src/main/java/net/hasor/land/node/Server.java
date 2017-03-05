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
import net.hasor.land.domain.ServerStatus;

import java.util.List;
/**
 * 当前服务器节点信息
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Server {
    /** 当前条目ID(已递交，已生效) */
    public String getCurrentTerm();

    /** 当前服务器的选票投给了谁 */
    public String getVotedFor();

    /** 当前服务器节点状态 */
    public ServerStatus getStatus();

    /** 最后一次 Leader 发来的心跳时间 */
    public long getLastHeartbeat();

    /** 获取所有在线状态的节点 */
    public List<NodeData> getOnlineNodes();

    /** 同步执行server的修改 */
    public void lockRun(RunLock runnable);

    /** 测试是否得到某个服务器的投票 */
    public boolean testVote(String serverID);
}