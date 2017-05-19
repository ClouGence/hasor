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
/**
 * 服务器操作
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Operation extends Server {
    /** Term 自增 */
    public void incrementAndGetTerm();

    /** Term 更新成比自己大的那个 */
    public boolean updateTermTo(String remoteTerm);

    /** 更新最后一次收到 Leader 的心跳时间 */
    public void newLastLeaderHeartbeat();

    /** 应用选票 */
    public void applyVoted(String remoteServerID, boolean voteGranted);

    /** 清空所有选票 */
    public void clearVoted();
}