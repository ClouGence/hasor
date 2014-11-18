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
package net.hasor.rsf.runtime.common;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.runtime.RsfResponse;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseImpl implements RsfResponse {
    private ServiceMetaData metaData     = null;
    private ResponseMsg     responseMsg  = null;
    //
    private Object          returnObject = null;
    private Class<?>        returnType   = null;
    private boolean         committed    = false;
    //
    public RsfResponseImpl(ServiceMetaData metaData, ResponseMsg responseMsg,//
            Object returnObject, Class<?> returnType) {
        this.metaData = metaData;
        this.responseMsg = responseMsg;
        this.returnObject = returnObject;
        this.returnType = returnType;
        this.committed = true;
    }
    RsfResponseImpl(RsfRequestImpl rsfRequest) {
        this.metaData = rsfRequest.getMetaData();
        //
        this.responseMsg = new ResponseMsg();
        this.responseMsg.setVersion(rsfRequest.getProtocol());
        this.responseMsg.setRequestID(rsfRequest.getRequestID());
        this.responseMsg.setStatus(ProtocolStatus.Unknown);
        this.responseMsg.setSerializeType(rsfRequest.getSerializeType());
        this.returnType = rsfRequest.getServiceMethod().getReturnType();
        this.responseMsg.setReturnType(this.returnType.getName());
    }
    //
    public ResponseMsg getMsg() {
        return this.responseMsg;
    }
    //
    public Object getResponseData() {
        return this.returnObject;
    }
    public Class<?> getResponseType() {
        return this.returnType;
    }
    public short getResponseStatus() {
        return this.responseMsg.getStatus();
    }
    public ServiceMetaData getMetaData() {
        return this.metaData;
    }
    //
    public String[] getOptionKeys() {
        return this.responseMsg.getOptionKeys();
    }
    public String getOption(String key) {
        return this.responseMsg.getOption(key);
    }
    public void addOption(String key, String value) {
        this.responseMsg.addOption(key, value);
    }
    public byte getProtocol() {
        return this.responseMsg.getVersion();
    }
    public long getRequestID() {
        return this.responseMsg.getRequestID();
    }
    public String getSerializeType() {
        return this.responseMsg.getSerializeType();
    }
    public boolean isResponse() {
        return this.committed;
    }
    //
    public void sendData(Object returnObject) {
        updateReturn(ProtocolStatus.OK, returnObject);
    }
    public void sendStatus(short status) {
        updateReturn(status, null);
    }
    public void sendStatus(short status, Object messageBody) {
        updateReturn(status, messageBody);
    }
    private void updateReturn(short status, Object messageBody) {
        this.returnObject = messageBody;
        this.responseMsg.setStatus(status);
        this.committed = true;
    }
}