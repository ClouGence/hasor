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
package net.hasor.rsf.rpc.message;
import net.hasor.rsf.constants.RSFConstants;
import net.hasor.rsf.manager.OptionManager;
import net.hasor.rsf.protocol.protocol.ProtocolUtils;
/**
 * 
 * @version : 2014年11月3日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class BaseMsg extends OptionManager {
    private byte   version       = RSFConstants.RSF;
    private long   requestID     = 0;
    private String serializeType = "";
    //
    /**设置协议版本。*/
    protected void setVersion(byte version) {
        this.version = version;
    }
    /**是否为Request消息。*/
    public boolean isRequest() {
        return ProtocolUtils.isRequest(this.version);
    }
    /**是否为Response消息。*/
    public boolean isResponse() {
        return ProtocolUtils.isResponse(this.version);
    }
    /**获取协议版本。*/
    public byte getVersion() {
        return ProtocolUtils.getVersion(this.version);
    }
    /**获取请求ID。*/
    public long getRequestID() {
        return this.requestID;
    }
    /**设置请求ID。*/
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
}