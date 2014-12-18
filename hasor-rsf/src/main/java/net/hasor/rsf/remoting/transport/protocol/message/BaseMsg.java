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
package net.hasor.rsf.remoting.transport.protocol.message;
import java.util.HashMap;
import java.util.Map;
import net.hasor.rsf.common.constants.RSFConstants;
import net.hasor.rsf.remoting.transport.protocol.toos.ProtocolUtils;
/**
 * 
 * @version : 2014年11月3日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class BaseMsg {
    private byte                version   = RSFConstants.RSF;             //
    private long                requestID = 0;                            //
    private Map<String, String> optionMap = new HashMap<String, String>(); //选项
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
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionMap.put(key, value);
    }
}