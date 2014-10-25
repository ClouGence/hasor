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
package net.hasor.rsf.protocol.block;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.protocol.AbstractBlock;
import net.hasor.rsf.protocol.field.DataField;
/**
 * 调用请求头
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 * @see net.hasor.rsf.general.ProtocolType#Request
 */
public class ReqHeadBlock extends AbstractBlock {
    //1.Header
    private DataField       serviceName    = new DataField();
    private DataField       serviceGroup   = new DataField();
    private DataField       serviceVersion = new DataField();
    private DataField       targetMethod   = new DataField();
    private DataField       serializeType  = new DataField();
    private List<DataField> paramTypeList  = new ArrayList<DataField>();
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
    /**获取参数名*/
    public String[] getParameterTypes() {
        String[] str = new String[this.paramTypeList.size()];
        int typeCount = this.paramTypeList.size();
        for (int i = 0; i < typeCount; i++) {
            str[i] = this.paramTypeList.get(i).getValue();
        }
        return str;
    }
    //
    /**添加参数*/
    public int addParameter(String classType) {
        DataField paramType = new DataField();
        paramType.setValue(classType);
        this.paramTypeList.add(paramType);
        return this.paramTypeList.indexOf(paramType);
    }
    //
    public void decode(ByteBuf buf) {
        //1.request HeadLength
        buf.skipBytes(4);
        //2.service
        this.serviceName.decode(buf);
        this.serviceGroup.decode(buf);
        this.serviceVersion.decode(buf);
        this.serializeType.decode(buf);
        //3.method
        this.targetMethod.decode(buf);
        //4.param Types
        short typeCount = buf.readByte();
        for (int i = 0; i < typeCount; i++) {
            DataField field = new DataField();
            field.decode(buf);
            this.paramTypeList.add(field);
        }
    }
    public void encode(ByteBuf buf) {
        //1.request HeadLength
        buf.writeInt(this.size());
        //2.service
        this.serviceName.encode(buf);
        this.serviceGroup.encode(buf);
        this.serviceVersion.encode(buf);
        this.serializeType.encode(buf);
        //3.method
        this.targetMethod.encode(buf);
        //4.param Types
        int typeCount = this.paramTypeList.size();
        buf.writeByte(typeCount);
        for (int i = 0; i < typeCount; i++) {
            this.paramTypeList.get(i).encode(buf);
        }
    }
    public int size() {
        int finalSize = 0;//Head
        finalSize += 4;//requestHeadLength
        finalSize += this.serviceName.size();
        finalSize += this.serviceGroup.size();
        finalSize += this.serviceVersion.size();
        finalSize += this.serializeType.size();
        finalSize += this.targetMethod.size();
        finalSize += 1;
        for (DataField field : paramTypeList) {
            finalSize += field.size();
        }
        return finalSize;
    }
}