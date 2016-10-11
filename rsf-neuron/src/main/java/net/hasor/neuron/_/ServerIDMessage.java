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
package net.hasor.neuron._;
import net.hasor.neuron._.PaxosResult;
/**
 * 确定 ServerID 步骤
 *
 *  1. rsf-paxos 启动时随机生成一个 ServerID -> A
 *  2. 群发消息 ( step1 ) 给所有(节点 N ), 询问所有节点它们的 ServerID -> S。 (S是集合)
 *  3. 如果 A 在 S 中不存在那么结束。
 *  4. 如果 A 在 S 中存在那么出现冲突, 接下来从新生成一个 A 值
 *  5. 新生成的 A 值 发送消息( step2 )给出现冲突的那些 节点 N` 如果所有冲突的节点都接受 新值 A 那么结束。否则重复 4~5.
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public interface ServerIDMessage {
    /** step1 : 发给接受者,要求接受者把它的 ServerID 发送过来 */
    public String askServerID();

    /** step2 : 发给接受者,检查 ServerIDMessage ,是否与接受者的 ServerID 出现冲突 */
    public PaxosResult<Boolean> checkConflict(String serverID);
}