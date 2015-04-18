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
package net.hasor.rsf.rpc.component;
import java.lang.reflect.Method;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfOptionSet;
import net.hasor.rsf.RsfRequest;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.rpc.message.RequestMsg;
import net.hasor.rsf.utils.MethodUtils;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestImpl implements RsfRequest {
    private RsfBindInfo<?> bindInfo         = null;
    private RequestMsg     requestMsg       = null;
    private RsfContext     rsfContext       = null;
    private boolean        local            = false;
    //
    private Class<?>[]     parameterTypes   = null;
    private Object[]       parameterObjects = null;
    //
    public RsfRequestImpl(boolean local, Class<?>[] parameterTypes, Object[] parameterObjects,//
            RsfBindInfo<?> bindInfo, RequestMsg requestMsg, RsfContext rsfContext) throws RsfException {
        this.parameterTypes = parameterTypes;
        this.parameterObjects = parameterObjects;
        this.bindInfo = bindInfo;
        this.requestMsg = requestMsg;
        this.rsfContext = rsfContext;
        this.local = local;
        //check Forbidden
        if (getServiceMethod() == null) {
            throw new RsfException(ProtocolStatus.Forbidden, "undefined service.");
        }
    }
    //
    public byte getProtocol() {
        return this.requestMsg.getVersion();
    }
    public String getSerializeType() {
        return this.requestMsg.getSerializeType();
    }
    public long getRequestID() {
        return this.requestMsg.getRequestID();
    }
    public int getTimeout() {
        return this.requestMsg.getClientTimeout();
    }
    public long getReceiveTime() {
        return this.requestMsg.getReceiveTime();
    }
    public String getMethod() {
        return this.requestMsg.getTargetMethod();
    }
    public String[] getOptionKeys() {
        return this.requestMsg.getOptionKeys();
    }
    public String getOption(String key) {
        return this.requestMsg.getOption(key);
    }
    public void addOption(String key, String value) {
        this.requestMsg.addOption(key, value);
    }
    public void removeOption(String key) {
        this.requestMsg.removeOption(key);
    }
    //
    public boolean isLocal() {
        return this.local;
    }
    public RsfContext getContext() {
        return this.rsfContext;
    }
    public RsfBindInfo<?> getBindInfo() {
        return this.bindInfo;
    }
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
    private Method serviceMethod = null;
    public Method getServiceMethod() {
        if (this.serviceMethod == null)
            this.serviceMethod = MethodUtils.getServiceMethod(//
                    this.bindInfo.getBindType(), this.requestMsg.getTargetMethod(), this.parameterTypes);
        return this.serviceMethod;
    }
    //
    private RsfResponseImpl response = null;
    /**根据{@link RsfRequest}创建对应的Response。*/
    public RsfResponseImpl buildResponse() {
        if (this.response == null) {
            this.response = new RsfResponseImpl(this);
            RsfOptionSet optMap = this.rsfContext.getSettings().getServerOption();
            for (String optKey : optMap.getOptionKeys())
                response.addOption(optKey, optMap.getOption(optKey));
        }
        return this.response;
    }
    public RequestMsg getMsg() {
        return this.requestMsg;
    }
    @Override
    public String toString() {
        return this.bindInfo.toString() + " - " + this.requestMsg.toString();
    }
}