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
package net.hasor.rsf.center.domain.constant;
/**
 * 事件
 * 
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfEvent {
    /** ZK连接可用，参数为：ZooKeeperNode */
    public static final String SyncConnected = "SyncConnected";
    /** 确认Leader，参数为：DataDiplomat */
    public static final String ConfirmLeader = "ConfirmLeader";
}