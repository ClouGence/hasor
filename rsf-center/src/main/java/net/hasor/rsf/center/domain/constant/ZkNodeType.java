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
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 工作模式
 * 
 * @version : 2015年7月3日
 * @author 赵永春(zyc@hasor.net)
 */
public enum ZkNodeType {
    /** 持久化节点，节点数据永久保存到zk集群中 */
    Persistent(CreateMode.PERSISTENT, "Persistent", "持久化节点"),
    /** 临时节点，当zk会话失效自动销毁节点 */
    Session(CreateMode.EPHEMERAL, "Session", "会话节点"),
    /** 共享节点，多个zk会话共用同一个节点，当会话结束自动清除自己的共享 */
    Share(CreateMode.EPHEMERAL_SEQUENTIAL, "Share", "共享节点"),;
    //
    //
    //
    // ---------------------------------------------
    private static Logger logger = LoggerFactory.getLogger(ZkNodeType.class);
    private CreateMode    nodeType;
    private String        codeString;
    private String        message;
    ZkNodeType(CreateMode nodeType, String codeString, String message) {
        this.nodeType = nodeType;
        this.codeString = codeString;
        this.message = message;
    }
    public CreateMode getNodeType() {
        return this.nodeType;
    }
    public String getCodeString() {
        return this.codeString;
    }
    public String getMessage() {
        return this.message;
    }
    public static ZkNodeType getModeByCodeType(CreateMode nodeType) {
        for (ZkNodeType a : ZkNodeType.values()) {
            if (a.getNodeType() == nodeType) {
                return a;
            }
        }
        logger.error("ZkNodeType = " + nodeType);
        throw new RuntimeException("not found ZkNodeType: " + nodeType);
    }
}
