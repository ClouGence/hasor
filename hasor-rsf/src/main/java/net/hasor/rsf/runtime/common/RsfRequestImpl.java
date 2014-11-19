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
import java.lang.reflect.Method;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RsfException;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestImpl implements RsfRequest {
    private ServiceMetaData   metaData         = null;
    private RequestMsg        requestMsg       = null;
    private NetworkConnection connection       = null;
    private RsfContext        rsfContext       = null;
    //
    private Class<?>[]        parameterTypes   = null;
    private Object[]          parameterObjects = null;
    //
    public RsfRequestImpl(Class<?>[] parameterTypes, Object[] parameterObjects,//
            ServiceMetaData metaData, RequestMsg requestMsg,//
            NetworkConnection connection, RsfContext rsfContext) throws RsfException {
        this.parameterTypes = parameterTypes;
        this.parameterObjects = parameterObjects;
        this.metaData = metaData;
        this.requestMsg = requestMsg;
        this.connection = connection;
        this.rsfContext = rsfContext;
        //check Forbidden
        if (getServiceMethod() == null) {
            throw new RsfException(ProtocolStatus.Forbidden, "undefined service.");
        }
    }
    //
    public NetworkConnection getConnection() {
        return this.connection;
    }
    public RequestMsg getMsg() {
        return this.requestMsg;
    }
    //
    public String getRemotHost() {
        return this.connection.getRemotHost();
    }
    public int getRemotePort() {
        return this.connection.getRemotePort();
    }
    public String getLocalHost() {
        return this.connection.getLocalHost();
    }
    public int getLocalPort() {
        return this.connection.getLocalPort();
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
    //
    public RsfContext getContext() {
        return this.rsfContext;
    }
    public ServiceMetaData getMetaData() {
        return this.metaData;
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
            this.serviceMethod = this.getMetaData().getServiceMethod(this.requestMsg.getTargetMethod(), this.parameterTypes);
        return this.serviceMethod;
    }
    //
    /**根据{@link RsfRequest}创建对应的Response。*/
    public RsfResponseImpl buildResponse() {
        return new RsfResponseImpl(this);
    }
}