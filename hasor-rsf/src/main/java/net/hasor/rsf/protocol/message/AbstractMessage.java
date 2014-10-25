/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.protocol.message;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.protocol.block.HeadBlock;
/**
 * 
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class AbstractMessage {
    private HeadBlock headBlock = new HeadBlock();
    //
    //
    /**获取协议版本*/
    public ProtocolVersion getVersion() {
        return this.headBlock.getVersion();
    }
    /**设置协议版本*/
    public void setVersion(ProtocolVersion version) {
        this.headBlock.setVersion(version);
    }
    /**获取请求ID*/
    public int getRequestID() {
        return this.headBlock.getRequestID();
    }
    /**设置请求ID*/
    public void setRequestID(int requestID) {
        this.headBlock.setRequestID(requestID);
    }
    /**获取协议类型*/
    public ProtocolType getProtocolType() {
        return this.headBlock.getProtocolType();
    }
    /**设置协议类型*/
    public void setProtocolType(ProtocolType protocolType) {
        this.headBlock.setProtocolType(protocolType);
    }
    //
    //
    public void useProtocol(HeadBlock head) {
        if (head != null) {
            this.headBlock = head;
        }
    }
}