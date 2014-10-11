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
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.field.DataField;
import net.hasor.rsf.protocol.field.ParamField;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 调用请求
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Request
 */
public class ProtocolRequest extends ProtocolHead {
    //1.Header
    private int              clientTimeout   = RSFConstants.ClientTimeout;
    private DataField        serviceName     = new DataField();
    private DataField        serviceGroup    = new DataField();
    private DataField        serviceVersion  = new DataField();
    private DataField        targetMethod    = new DataField();
    private DataField        serializeType   = new DataField();
    private List<DataField>  paramTypeList   = new ArrayList<DataField>();
    //2.Body
    private List<ParamField> paramObjectList = new ArrayList<ParamField>();
    //
    //
    /**获取服务名*/
    public String getServiceName() {
        return this.serviceName.getValue();
    }
    /**设置服务名*/
    public void setServiceName(String serviceName) {
        this.serviceName.setValue(serviceName);
    }
    /**获取服务分组*/
    public String getServiceGroup() {
        return this.serviceGroup.getValue();
    }
    /**设置服务分组*/
    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup.setValue(serviceGroup);
    }
    /**获取服务版本*/
    public String getServiceVersion() {
        return this.serviceVersion.getValue();
    }
    /**设置服务版本*/
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion.setValue(serviceVersion);
    }
    /**获取调用的方法名*/
    public String getTargetMethod() {
        return this.targetMethod.getValue();
    }
    /**设置调用的方法名*/
    public void setTargetMethod(String targetMethod) {
        this.targetMethod.setValue(targetMethod);
    }
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType.getValue();
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.serializeType.setValue(serializeType);
    }
    /**调用超时时间（毫秒）*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    /**调用超时时间（毫秒）*/
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    //
    /**设置序列化类型*/
    public int addParameter(Class<?> classType) {
        DataField typeString = new DataField();
        typeString.setValue(classType.getName());
        this.paramTypeList.add(typeString);
        return this.paramTypeList.indexOf(typeString);
    }
    /**设置参数对象*/
    public void addParameter(Object newValue, SerializeFactory factory) throws Throwable {
        ParamField field = new ParamField(this.getSerializeType());
        field.writeObject(newValue, factory);
        this.paramObjectList.add(field);
    }
    //
    //
    public void decode(ByteBuf buf) {
        //1.header
        super.decode(buf);
        //2.Timeout
        this.clientTimeout = buf.readInt();
        if (this.clientTimeout == 0) {
            this.clientTimeout = RSFConstants.ClientTimeout;
        }
        //3.service
        this.serviceName.decode(buf);
        this.serviceGroup.decode(buf);
        this.serviceVersion.decode(buf);
        this.serializeType.decode(buf);
        //4.method
        this.targetMethod.decode(buf);
        short typeCount = buf.readByte();
        for (int i = 0; i < typeCount; i++) {
            DataField field = new DataField();
            field.decode(buf);
            this.paramTypeList.add(field);
        }
        //5.params
        String serializeType = this.serializeType.getValue();
        short paramCount = buf.readByte();
        for (int i = 0; i < paramCount; i++) {
            ParamField field = new ParamField(serializeType);
            field.decode(buf);
            this.paramObjectList.add(field);
        }
    }
    public void encode(ByteBuf buf) {
        //1.header
        super.encode(buf);
        //2.Timeout
        buf.writeInt(this.clientTimeout);
        //3.service
        this.serviceName.encode(buf);
        this.serviceGroup.encode(buf);
        this.serviceVersion.encode(buf);
        this.serializeType.encode(buf);
        //4.method
        this.targetMethod.encode(buf);
        int typeCount = this.paramTypeList.size();
        buf.writeByte(typeCount);
        for (int i = 0; i < typeCount; i++) {
            this.paramTypeList.get(i).encode(buf);
        }
        //5.params
        int paramCount = this.paramObjectList.size();
        buf.writeByte(paramCount);
        for (int i = 0; i < paramCount; i++) {
            this.paramObjectList.get(i).encode(buf);
        }
    }
    public int size() {
        int finalSize = super.size();//Head
        finalSize += 4;//clientTimeout
        finalSize += this.serviceName.size();
        finalSize += this.serviceGroup.size();
        finalSize += this.serviceVersion.size();
        finalSize += this.serializeType.size();
        finalSize += this.targetMethod.size();
        finalSize += 1;
        for (DataField field : paramTypeList) {
            finalSize += field.size();
        }
        finalSize += 1;
        for (ParamField field : paramObjectList) {
            finalSize += field.size();
        }
        return finalSize;
    }
}