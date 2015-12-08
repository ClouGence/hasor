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
package net.hasor.rsf.rpc.caller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.rpc.context.AbstractRsfContext;
import net.hasor.rsf.rpc.net.RsfRuntimeUtils;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
import net.hasor.rsf.transform.codec.ProtocolUtils;
import net.hasor.rsf.transform.protocol.OptionInfo;
import net.hasor.rsf.transform.protocol.ResponseBlock;
import net.hasor.rsf.transform.protocol.ResponseInfo;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseFormLocal extends OptionInfo implements RsfResponse {
    protected Logger           logger = LoggerFactory.getLogger(getClass());
    private final RsfRequest   rsfRequest;
    private final ResponseInfo responseInfo;
    private short              responseStatus;
    private Class<?>           returnType;
    private Object             returnObject;
    private boolean            committed;
    // 
    public RsfResponseFormLocal(RsfRequest rsfRequest) {
        this.rsfRequest = rsfRequest;
        this.responseInfo = new ResponseInfo();
        
        rsfRequest.getServiceMethod()
        this.returnType = rsfRequest.getServiceMethod().getReturnType();

    }
    public RsfResponseFormLocal(AbstractRsfContext rsfContext, RsfBindInfo<?> bindInfo, ResponseInfo responseInfo2) {
        // TODO Auto-generated constructor stub
    }
    //
    //
    @Override
    public String toString() {
        return "responseID:" + this.getRequestID() + " from Local," + this.getBindInfo();
    }
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.rsfRequest.getBindInfo();
    }
    @Override
    public long getRequestID() {
        return this.rsfRequest.getRequestID();
    }
    @Override
    public String getSerializeType() {
        return this.rsfRequest.getSerializeType();
    }
    @Override
    public Object getResponseData() {
        return this.returnObject;
    }
    @Override
    public Class<?> getResponseType() {
        return this.returnType;
    }
    @Override
    public short getResponseStatus() {
        return this.responseStatus;
    }
    //
    @Override
    public void sendData(Object returnObject) {
        this.updateReturn(ProtocolStatus.OK.getType(), returnObject, null);
    }
    @Override
    public void sendStatus(ProtocolStatus status) {
        this.updateReturn(status.getType(), null, null);
    }
    @Override
    public void sendStatus(ProtocolStatus status, String messageBody) {
        this.updateReturn(status.getType(), null, messageBody);
    }
    @Override
    public void sendStatus(short status) {
        this.updateReturn(status, null, null);
    }
    @Override
    public void sendStatus(short status, String messageBody) {
        this.updateReturn(status, null, messageBody);
    }
    private void updateReturn(short status, Object returnData, String message) {
        this.returnObject = messageBody;
        this.responseStatus = status;
        this.committed = true;
    }
    @Override
    public boolean isResponse() {
        return this.committed;
    }
    public ResponseInfo buildSocketBlock(SerializeFactory serializeFactory) {
        SerializeCoder coder = serializeFactory.getSerializeCoder(getSerializeType());
        ResponseBlock block = new ResponseBlock();
        //
        //2.returnData
        try {
            Object result = getResponseData();
            byte[] contentByte = coder.encode(result);
            block.setReturnType(ProtocolUtils.pushString(block, RsfRuntimeUtils.toAsmType(getResponseType())));//返回类型
            block.setReturnData(block.pushData(contentByte));
            block.setStatus(getResponseStatus());//响应状态
            //
        } catch (Throwable e) {
            String msg = "buildSocketBlock " + e.getClass().getName() + ":" + e.getMessage();
            logger.error(e.getMessage(), e);
            block.setReturnType(ProtocolUtils.pushString(block, RsfRuntimeUtils.toAsmType(java.lang.String.class)));//返回类型
            block.setReturnData(block.pushData(msg.getBytes()));
            block.setStatus(ProtocolStatus.SerializeError);//响应状态
        }
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