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
package net.hasor.rsf.rpc.caller;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.RsfResponse;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.utils.StringUtils;
/**
 * 调用请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfResponseObject extends OptionInfo implements RsfResponse {
    private final RsfRequest rsfRequest;
    private short   status       = ProtocolStatus.Unknown;
    private Object  returnObject = null;
    private boolean committed    = false;
    //
    public RsfResponseObject(RsfRequest rsfRequest) {
        this.rsfRequest = rsfRequest;
    }
    //
    @Override
    public String toString() {
        return "responseID:" + this.getRequestID() + " from Setvice " + this.getBindInfo();
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
    public Object getData() {
        return this.returnObject;
    }
    @Override
    public Class<?> getReturnType() {
        return this.rsfRequest.getMethod().getReturnType();
    }
    @Override
    public short getStatus() {
        return this.status;
    }
    @Override
    public void sendData(Object returnObject) {
        this.updateReturn(ProtocolStatus.OK, returnObject, null);
    }
    @Override
    public void sendStatus(short status) {
        this.updateReturn(status, null, null);
    }
    @Override
    public void sendStatus(short status, String returnMessage) {
        this.updateReturn(status, null, returnMessage);
    }
    private void updateReturn(short status, Object returnData, String returnMessage) {
        this.status = status;
        this.returnObject = returnData;
        this.committed = true;
        if (StringUtils.isNotBlank(returnMessage)) {
            this.addOption("message", returnMessage);
        }
    }
    @Override
    public boolean isResponse() {
        return this.committed;
    }
}