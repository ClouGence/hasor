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
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.toos.ProtocolUtils;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfRequestImpl implements RsfRequest {
    private ServiceMetaData metaData         = null;
    private RequestMsg      requestMsg       = null;
    private RsfContext      rsfContext       = null;
    private NetworkChanne   connection       = null;
    //
    private Method          targetMethod     = null;
    private Class<?>[]      parameterTypes   = null;
    private Object[]        parameterObjects = null;
    //
    public RsfRequestImpl(RequestMsg requestMsg, NetworkChanne connection, RsfContext rsfContext) {
        this.requestMsg = requestMsg;
        this.rsfContext = rsfContext;
        this.connection = connection;
    }
    //
    public void init() throws RsfException {
        //1.获取MetaData
        this.metaData = this.rsfContext.getService(requestMsg.getServiceName());
        if (this.metaData == null) {
            throw new RsfException(ProtocolStatus.NotFound, "service was not found.");
        }
        //2.反序列化
        try {
            SerializeFactory serializeFactory = this.rsfContext.getSerializeFactory();
            this.parameterObjects = this.requestMsg.toParameters(serializeFactory);
            //
            String[] pTypes = this.requestMsg.getParameterTypes();
            this.parameterTypes = new Class<?>[pTypes.length];
            for (int i = 0; i < pTypes.length; i++) {
                this.parameterTypes[i] = ProtocolUtils.toJavaType(pTypes[i], Thread.currentThread().getContextClassLoader());
            }
        } catch (RsfException e) {
            throw e;
        } catch (Throwable e) {
            throw new RsfException(ProtocolStatus.SerializeError, e);
        }
        //3.check target Method
        Class<?> targeType = this.rsfContext.getBeanType(metaData);
        Object forbidden = null;
        try {
            if (targeType == null) {
                forbidden = "undefined service.";
            } else {
                this.targetMethod = targeType.getMethod(//
                        this.requestMsg.getTargetMethod(), this.parameterTypes);
            }
        } catch (Exception e) {
            forbidden = e;
        }
        if (forbidden != null) {
            if (forbidden instanceof Exception) {
                throw new RsfException(ProtocolStatus.Forbidden, (Exception) forbidden);
            } else {
                throw new RsfException(ProtocolStatus.Forbidden, (String) forbidden);
            }
        }
    }
    //
    public Method getTargetMethod() {
        return this.targetMethod;
    }
    public NetworkChanne getConnection() {
        return this.connection;
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
    public ServiceMetaData getMetaData() {
        return this.metaData;
    }
    public Class<?>[] getParameterTypes() {
        return this.parameterTypes;
    }
    public Object[] getParameterObject() {
        return this.parameterObjects;
    }
}