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
package net.hasor.rsf.remoting.transport.component;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.remoting.transport.protocol.message.RequestMsg;
import net.hasor.rsf.remoting.transport.protocol.message.ResponseMsg;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseImpl implements RsfResponse {
    private RsfBindInfo<?> bindInfo     = null;
    private ResponseMsg    responseMsg  = null;
    //
    private Object         returnObject = null;
    private Class<?>       returnType   = null;
    private boolean        committed    = false;
    //
    public RsfResponseImpl(RsfBindInfo<?> bindInfo, ResponseMsg responseMsg,//
            Object returnObject, Class<?> returnType) {
        this.bindInfo = bindInfo;
        this.responseMsg = responseMsg;
        this.returnObject = returnObject;
        this.returnType = returnType;
        this.committed = true;
    }
    RsfResponseImpl(RsfRequestImpl rsfRequest) {
        this.bindInfo = rsfRequest.getBindInfo();
        RequestMsg msg = rsfRequest.getMsg();
        //
        this.responseMsg = new ResponseMsg();
        this.responseMsg.setVersion(msg.getVersion());
        this.responseMsg.setRequestID(msg.getRequestID());
        this.responseMsg.setSerializeType(msg.getSerializeType());
        this.responseMsg.setStatus(ProtocolStatus.Unknown);
        this.returnType = rsfRequest.getServiceMethod().getReturnType();
        this.responseMsg.setReturnType(this.returnType.getName());
    }
    //
    public ResponseMsg getMsg() {
        return this.responseMsg;
    }
    //
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
        return this.responseMsg.getStatus();
    }
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    //
    @Override
    public String[] getOptionKeys() {
        return this.responseMsg.getOptionKeys();
    }
    @Override
    public String getOption(String key) {
        return this.responseMsg.getOption(key);
    }
    @Override
    public void addOption(String key, String value) {
        this.responseMsg.addOption(key, value);
    }
    @Override
    public void removeOption(String key) {
        this.responseMsg.removeOption(key);
    }
    @Override
    public byte getProtocol() {
        return this.responseMsg.getVersion();
    }
    @Override
    public long getRequestID() {
        return this.responseMsg.getRequestID();
    }
    @Override
    public String getSerializeType() {
        return this.responseMsg.getSerializeType();
    }
    @Override
    public boolean isResponse() {
        return this.committed;
    }
    //
    @Override
    public void sendData(Object returnObject) {
        updateReturn(ProtocolStatus.OK, returnObject);
    }
    @Override
    public void sendStatus(short status) {
        updateReturn(status, null);
    }
    @Override
    public void sendStatus(short status, Object messageBody) {
        updateReturn(status, messageBody);
    }
    private void updateReturn(short status, Object messageBody) {
        this.returnObject = messageBody;
        this.responseMsg.setStatus(status);
        this.committed = true;
    }
    @Override
    public String toString() {
        return this.bindInfo.toString() + " - " + this.responseMsg.toString();
    }
}