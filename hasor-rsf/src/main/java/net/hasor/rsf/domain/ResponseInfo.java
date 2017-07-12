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
package net.hasor.rsf.domain;
/**
 * RSF Response 的化身,是封装 Response 的数据对象。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResponseInfo extends OptionInfo {
    private long   requestID     = 0;    //请求ID
    private long   receiveTime   = 0;    //数据包到达时间
    private short  status        = 0;    //响应状态
    private String serializeType = null; //序列化类型
    private byte[] returnData    = null; //返回数据
    //
    //
    /**获取请求ID。*/
    public long getRequestID() {
        return this.requestID;
    }
    /**设置请求ID。*/
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }
    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }
    /**数据包到达时间*/
    public long getReceiveTime() {
        return this.receiveTime;
    }
    /**
     * 获取响应状态
     * @see net.hasor.rsf.domain.ProtocolStatus
     */
    public short getStatus() {
        return this.status;
    }
    /**
     * 设置响应状态
     * @see net.hasor.rsf.domain.ProtocolStatus
     */
    public void setStatus(short status) {
        this.status = status;
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    /**获取返回值数据*/
    public byte[] getReturnData() {
        return this.returnData;
    }
    /**设置返回值数据*/
    public void setReturnData(byte[] returnData) {
        this.returnData = returnData;
    }
}