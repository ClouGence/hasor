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
package net.hasor.rsf.protocol;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.protocol.field.HeadField;
/**
 * 协议头
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolHead implements ProtocolCoder, BlockSize {
    private HeadField head = new HeadField();
    //
    /**获取协议版本*/
    public byte getVersion() {
        return this.head.getVersion();
    }
    /**设置协议版本*/
    public void setVersion(byte version) {
        this.head.setVersion(version);
    }
    /**获取请求ID*/
    public int getRequestID() {
        return this.head.getRequestID();
    }
    /**设置请求ID*/
    public void setRequestID(int requestID) {
        this.head.setRequestID(requestID);
    }
    /**获取协议类型*/
    public ProtocolType getProtocolType() {
        return ProtocolType.valueOf(this.head.getProtocolType());
    }
    /**设置协议类型*/
    public void setProtocolType(ProtocolType protocolType) {
        if (protocolType == null) {
            protocolType = ProtocolType.Unknown;
        }
        this.head.setProtocolType(protocolType.value());
    }
    /**请求数据长度*/
    public int getContentLength() {
        return this.head.getContentLength();
    }
    //
    public void decode(ByteBuf buf) {
        this.head.decode(buf);
    }
    public void encode(ByteBuf buf) {
        this.head.setContentLength(this.size());
        this.head.encode(buf);
    }
    public int size() {
        return this.head.size();
    }
}