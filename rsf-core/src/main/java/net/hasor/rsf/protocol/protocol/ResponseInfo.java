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
package net.hasor.rsf.protocol.protocol;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.protocol.ProtocolUtils;
import net.hasor.rsf.utils.ByteStringCachelUtils;
/**
 * RSF Response 数据对象
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResponseInfo extends OptionInfo {
    private long           requestID     = 0;    //请求ID
    private long           receiveTime   = 0;    //数据包到达时间
    private ProtocolStatus status        = null; //响应状态
    private String         serializeType = null; //序列化类型
    private String         returnType    = null; //返回类型
    private byte[]         returnData    = null; //返回数据
    //
    //
    public ResponseInfo() {}
    public ResponseInfo(ResponseBlock rsfBlock) {
        this.recovery(rsfBlock);
    }
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
    /**获取响应状态*/
    public ProtocolStatus getStatus() {
        return this.status;
    }
    /**设置响应状态*/
    public void setStatus(ProtocolStatus status) {
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
    /**获取返回值类型*/
    public String getReturnType() {
        return this.returnType;
    }
    /**设置返回值类型*/
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    /**获取返回值数据*/
    public byte[] getReturnData() {
        return this.returnData;
    }
    /**设置返回值数据*/
    public void setReturnData(byte[] returnData) {
        this.returnData = returnData;
    }
    //
    //
    protected void recovery(ResponseBlock rsfBlock) {
        //
        //1.基本数据
        this.requestID = rsfBlock.getRequestID();
        short serializeType = rsfBlock.getSerializeType();
        this.serializeType = ByteStringCachelUtils.fromCache(rsfBlock.readPool(serializeType));
        //
        //2.Opt参数
        int[] optionArray = rsfBlock.getOptions();
        for (int optItem : optionArray) {
            short optKey = (short) (optItem >>> 16);
            short optVal = (short) (optItem & PoolBlock.PoolMaxSize);
            String optKeyStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optKey));
            String optValStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optVal));
            this.addOption(optKeyStr, optValStr);
        }
        //
        //3.Response
        this.status = ProtocolStatus.valueOf(rsfBlock.getStatus());
        byte[] returnTypeData = rsfBlock.readPool(rsfBlock.getReturnType());
        byte[] returnDataData = rsfBlock.readPool(rsfBlock.getReturnData());
        this.returnType = ByteStringCachelUtils.fromCache(returnTypeData);
        this.returnData = returnDataData;
    }
    /**构建一个二进制协议对象。*/
    public ResponseBlock buildBlock() {
        ResponseBlock block = new ResponseBlock();
        //
        //1.基本信息
        block.setHead(RSFConstants.RSF_Response);
        block.setRequestID(this.getRequestID());//请求ID
        block.setSerializeType(ProtocolUtils.pushString(block, this.getSerializeType()));//序列化策略
        //
        //2.returnData
        block.setReturnType(ProtocolUtils.pushString(block, this.getReturnType()));//返回类型
        block.setReturnData(block.pushData(this.getReturnData()));
        ProtocolStatus status = this.getStatus();
        if (status == null) {
            status = ProtocolStatus.Unknown;
        }
        block.setStatus(status.getType());//响应状态
        //
        //3.Opt参数
        String[] optKeys = getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            short optKey = ProtocolUtils.pushString(block, optKeys[i]);
            short optVal = ProtocolUtils.pushString(block, getOption(optKeys[i]));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
}