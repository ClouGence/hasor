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
package net.hasor.rsf.rpc.objects.local;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.manager.OptionManager;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseFormLocal extends OptionManager implements RsfResponse {
    private final RsfRequest rsfRequest;
    private short            responseStatus;
    private Class<?>         returnType;
    private Object           returnObject;
    private boolean          committed;
    //
    public RsfResponseFormLocal(RsfRequest rsfRequest) {
        this.rsfRequest = rsfRequest;
    }
    //
    //
    @Override
    public String toString() {
        return "responseID:" + this.getRequestID() + " from Local," + this.bindInfo.toString();
    }
    @Override
    public RsfBindInfo<?> getBindInfo() {
        return this.rsfRequest.getBindInfo();
    }
    @Override
    public byte getProtocol() {
        return this.rsfRequest.getProtocol();
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
        this.responseStatus = status;
        this.committed = true;
    }
    @Override
    public boolean isResponse() {
        return this.committed;
    }
}