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
package net.hasor.rsf.protocol.block;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.AbstractBlock;
/**
 * 协议头
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class HeadBlock extends AbstractBlock {
    private byte version       = RSFConstants.RSF_Version;
    private byte protocolType  = 0;
    private int  requestID     = 0;
    private int  contentLength = 0;
    //
    /**获取协议版本*/
    public ProtocolVersion getVersion() {
        return ProtocolVersion.valueOf(this.version);
    }
    /**设置协议版本*/
    public void setVersion(ProtocolVersion version) {
        if (version == null) {
            version = ProtocolVersion.Unknown;
        }
        this.version = version.value();
    }
    /**获取请求ID*/
    public int getRequestID() {
        return this.requestID;
    }
    /**设置请求ID*/
    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }
    /**获取协议类型*/
    public ProtocolType getProtocolType() {
        return ProtocolType.valueOf(this.protocolType);
    }
    /**设置协议类型*/
    public void setProtocolType(ProtocolType protocolType) {
        if (protocolType == null) {
            protocolType = ProtocolType.Unknown;
        }
        this.protocolType = protocolType.value();
    }
    /**请求数据长度*/
    public int getContentLength() {
        return this.contentLength;
    }
    /**请求数据长度*/
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
    //
    public void decode(ByteBuf buf) {
        this.version = buf.readByte();
        this.protocolType = buf.readByte();
        this.requestID = buf.readInt();
        this.contentLength = buf.readInt();
    }
    public void encode(ByteBuf buf) {
        buf.writeByte(this.version);
        buf.writeByte(this.protocolType);
        buf.writeInt(this.requestID);
        buf.writeInt(this.contentLength);
    }
    public int size() {
        return 10;
    }
}