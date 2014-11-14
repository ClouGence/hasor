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
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseImpl implements RsfResponse {
    private RsfRequestImpl rsfRequest   = null;
    private ResponseMsg    responseMsg  = null;
    private RsfContext     rsfContext   = null;
    //
    private Object         returnObject = null;
    private Class<?>       returnType   = null;
    private boolean        committed    = false;
    //
    public RsfResponseImpl(RsfRequestImpl rsfRequest, RsfContext rsfContext) {
        this.rsfRequest = rsfRequest;
        this.rsfContext = rsfContext;
        this.responseMsg = new ResponseMsg();
        this.responseMsg.setVersion(rsfRequest.getProtocol());
        this.responseMsg.setRequestID(rsfRequest.getRequestID());
        this.responseMsg.setStatus(ProtocolStatus.Unknown);
        this.responseMsg.setSerializeType(rsfRequest.getSerializeType());
    }
    //
    public void init() {
        this.returnType = this.rsfRequest.getTargetMethod().getReturnType();
        this.responseMsg.setReturnType(this.returnType.getName());
    }
    //
    private void check() {
        if (this.committed == true)
            throw new IllegalStateException("is committed.");
        if (this.rsfRequest.getConnection().isActive() == false)
            throw new IllegalStateException("connection is closed.");
    }
    public boolean isCommitted() {
        return this.committed;
    }
    public void refresh() {
        if (this.committed == true)
            return;
        //
        try {
            if (this.returnObject != null) {
                SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
                this.responseMsg.setReturnData(this.returnObject, serializeFactory);
            }
        } catch (Throwable e) {
            String msg = e.getClass().getName() + ":" + e.getMessage();
            this.responseMsg.setStatus(ProtocolStatus.SerializeError);;
            this.responseMsg.setReturnData(msg.getBytes());;
            this.responseMsg.setReturnType(String.class.getName());
        }
        this.rsfRequest.getConnection().sendData(this.responseMsg);
        this.committed = true;
    }
    //
    public Object getData() {
        return this.returnObject;
    }
    public Class<?> getReturnType() {
        return this.returnType;
    }
    public short getReturnStatus() {
        return this.responseMsg.getStatus();
    }
    public ServiceMetaData getMetaData() {
        return this.rsfRequest.getMetaData();
    }
    public RsfRequest fromRequest() {
        return this.rsfRequest;
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
    //
    public void sendData(Object returnObject) {
        updateReturn(ProtocolStatus.OK.shortValue(), returnObject);
    }
    public void sendStatus(short status) {
        updateReturn(status, null);
    }
    public void sendStatus(short status, Object messageBody) {
        updateReturn(status, messageBody);
    }
    public void sendStatus(ProtocolStatus status) {
        updateReturn(status.shortValue(), null);
    }
    public void sendStatus(ProtocolStatus status, Object messageBody) {
        updateReturn(status.shortValue(), messageBody);
    }
    private void updateReturn(short status, Object messageBody) {
        check();
        this.returnObject = messageBody;
        this.responseMsg.setStatus(status);
    }
}