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
package net.hasor.rsf.protocol;
import io.netty.buffer.ByteBuf;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.metadata.InvokeMetaData;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.serialize.SerializeFactory;
import org.more.util.ByteUtils;
/**
 * 请求信息
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolRequest implements ProtocolCode {
    private Head         head           = new Head();
    private DataField    serviceName    = new DataField();
    private DataField    serviceGroup   = new DataField();
    private DataField    serviceVersion = new DataField();
    private DataField    targetMethod   = new DataField();
    private DataField    serializeType  = new DataField();
    private int          clientTimeout  = RSFConstants.ClientTimeout;
    private ParamField[] paramArrays    = new ParamField[0];
    //
    /**获取协议版本*/
    public byte getVersion() {
        return this.head.getVersion();
    }
    /**获取请求ID*/
    public int getRequestID() {
        return this.head.getRequestID();
    }
    /**获取Info字段*/
    public byte getInfo() {
        return this.head.getInfo();
    }
    /**获取服务名*/
    public String getServiceName() {
        return this.serviceName.getValue();
    }
    /**获取服务分组*/
    public String getServiceGroup() {
        return this.serviceGroup.getValue();
    }
    /**获取服务版本*/
    public String getServiceVersion() {
        return this.serviceVersion.getValue();
    }
    /**获取调用的方法名*/
    public String getTargetMethod() {
        return this.targetMethod.getValue();
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType.getValue();
    }
    /**调用超时时间（毫秒）*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    //
    public void decode(ByteBuf buf) throws Throwable {
        this.head.decode(buf);
        this.serviceName.decode(buf);
        this.serviceGroup.decode(buf);
        this.serviceVersion.decode(buf);
        //
        this.targetMethod.decode(buf);
        this.serializeType.decode(buf);
        //
        int clientTimeout = buf.readBytes(4).readInt();
        if (clientTimeout == 0) {
            clientTimeout = RSFConstants.ClientTimeout;
        }
        this.clientTimeout = clientTimeout;
        //
        int paramCount = buf.readBytes(2).readInt();
        this.paramArrays = new ParamField[paramCount];
        String serializeType = this.serializeType.getValue();
        for (int i = 0; i < paramCount; i++) {
            this.paramArrays[i] = new ParamField(i, serializeType);
            this.paramArrays[i].decode(buf);
        }
    }
    public void encode(ByteBuf buf) throws Throwable {
        this.head.encode(buf);
        this.serviceName.encode(buf);
        this.serviceGroup.encode(buf);
        this.serviceVersion.encode(buf);
        //
        this.targetMethod.encode(buf);
        this.serializeType.encode(buf);
        //
        byte[] clientTimeoutBytes = ByteUtils.toByteArray(this.clientTimeout, 4);
        buf.writeBytes(clientTimeoutBytes);
        //
        int paramCount = this.paramArrays.length;
        byte[] paramCountBytes = ByteUtils.toByteArray(paramCount, 2);
        buf.writeBytes(paramCountBytes);
        for (int i = 0; i < paramCount; i++) {
            this.paramArrays[i].encode(buf);
        }
    }
    //
    /**根据 {@link ServiceMetaData} 描述信息创建一个remote请求对象。*/
    public static ProtocolRequest generationRequest(InvokeMetaData invokeMetaData) throws Throwable {
        ServiceMetaData metaData = invokeMetaData.getServiceMetaData();
        ProtocolRequest request = new ProtocolRequest();
        //
        request.serviceName.setValue(metaData.getServiceName());
        request.serviceGroup.setValue(metaData.getServiceGroup());
        request.serviceVersion.setValue(metaData.getServiceVersion());
        //
        request.targetMethod.setValue(invokeMetaData.getMethod());
        request.serializeType.setValue(metaData.getSerializeType());
        request.clientTimeout = metaData.getClientTimeout();
        //
        Class<?>[] paramTypes = invokeMetaData.getParameterTypes();
        Object[] paramObjects = invokeMetaData.getParameterObjects();
        SerializeFactory factory = invokeMetaData.getSerializeFactory();
        if (paramTypes != null) {
            request.paramArrays = new ParamField[paramTypes.length];
            String serializeType = metaData.getSerializeType();
            for (int i = 0; i < paramTypes.length; i++) {
                request.paramArrays[i] = new ParamField(i, serializeType);
                request.paramArrays[i].writeObject(paramObjects[i], factory);
            }
        }
        return request;
    }
}