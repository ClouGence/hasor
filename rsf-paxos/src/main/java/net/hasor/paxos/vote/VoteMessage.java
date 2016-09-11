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
package net.hasor.paxos.vote;
import net.hasor.paxos.PaxosResult;
import net.hasor.paxos.domain.AcceptMessage;
import net.hasor.paxos.domain.ProposalID;
import net.hasor.paxos.domain.ProposalMessage;
/**
 * 选举
 *
 *  1. 启动提议( step1 ),并获取超过半数的赞同, 否则重新发起提议。
 *      接受提议之后 启动定时器并在 T 之后超时,
 *
 *      如果接受提议方 此时已经接受了其它 提议,那么会回应拒绝。
 *
 *  2. 递交提案, 需要得到半数赞同, 否则重新发起提议。
 *  3. 
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public interface VoteMessage {
    /** step1 : 启动提议 */
    public PaxosResult<Boolean> requestPropose(ProposalID proposalID, int timeout);

    /** step2 : 递交提案 */
    public PaxosResult<Long> requestProposal(ProposalMessage message, int timeout);

    /** step3 : 递交决议 */
    public boolean requestResolution(AcceptMessage accept);
}