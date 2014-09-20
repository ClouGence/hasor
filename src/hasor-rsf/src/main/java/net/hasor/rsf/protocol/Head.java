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
import net.hasor.rsf.general.RSFConstants;
import org.more.util.ByteUtils;
/**
 * RSF协议头
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class Head implements ProtocolCode {
    private static int atRequestID = 0;
    private static synchronized int newID() {
        if (atRequestID >= 0xFFFFFF) {
            atRequestID = 0;
        }
        return ++atRequestID;
    }
    //
    private byte version    = 0; // 1Byte
    private int  requestID  = 0; // 3Byte
    private byte info       = 0; // 1Byte
    private int  dataLength = 0; // 3Byte
    //
    public Head() {
        this.version = RSFConstants.RSF_Version; // 1Byte
        this.requestID = newID(); // 3Byte
    }
    public Head(int requestID) {
        if (requestID >= 0xFFFFFF) {
            throw new IndexOutOfBoundsException("requestID maximum is 0xFFFFFF.");
        }
        this.version = RSFConstants.RSF_Version; // 1Byte
        this.requestID = requestID; // 3Byte
    }
    //
    /**获取协议版本*/
    public byte getVersion() {
        return this.version;
    }
    /**获取请求ID*/
    public int getRequestID() {
        return this.requestID;
    }
    /**设置请求ID*/
    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }
    /**请求数据长度*/
    public int getDataLength() {
        return this.dataLength;
    }
    /**请求包含的信息*/
    public byte getInfo() {
        return this.info;
    }
    //
    //
    public void decode(ByteBuf buf) throws Throwable {
        //1.version
        this.version = buf.readByte();
        //2.requestID
        this.requestID = buf.readBytes(3).readInt();
        //3.info
        this.info = buf.readByte();
        //4.dataLength
        this.dataLength = buf.readBytes(3).readInt();
    }
    public void encode(ByteBuf buf) throws Throwable {
        //1.version
        buf.writeByte(this.version);
        //2.requestID
        buf.writeBytes(ByteUtils.toByteArray(this.requestID, 3));
        //3.info
        buf.writeByte(this.info);
        //4.dataLength 
        buf.writeBytes(ByteUtils.toByteArray(this.dataLength, 3));
    }
}