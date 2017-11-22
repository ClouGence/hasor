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
package net.hasor.rsf.protocol.hprose;
/**
 * Hprose 协议工作状态
 * @version : 2017年1月28日
 * @author 赵永春(zyc@hasor.net)
 */
public enum WorkStatus {
    // Idle -> ReceiveRequest -> WaitResult -> Idle
    Idle,           // 空闲
    ReceiveRequest, // 收到请求调用，并等待数据
    WaitResult      // 发起调用并等待执行结果
}