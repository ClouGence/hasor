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
package net.hasor.rsf.rpc.message;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.constants.ProtocolStatus;
import net.hasor.rsf.constants.RsfException;
import net.hasor.rsf.protocol.protocol.ProtocolUtils;
import net.hasor.rsf.serialize.SerializeCoder;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * RSF 1.0-Request 协议数据.
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestMsg extends BaseMsg {
    private String       serviceName    = "";
    private String       serviceGroup   = "";
    private String       serviceVersion = "";
    private String       targetMethod   = "";
    private int          clientTimeout  = 0;
    private List<String> paramTypes     = new ArrayList<String>(10); //参数列表
    private List<byte[]> paramDatas     = new ArrayList<byte[]>(10); //参数值映射
    private long         receiveTime    = 0;                        //收到消息的时间
    //
    //
    /**设置协议版本。*/
    public void setVersion(byte version) {
        super.setVersion(ProtocolUtils.finalVersionForRequest(version));
    }
    /**获取服务名*/
    public String getServiceName() {
        return this.serviceName;
    }
    /**设置服务名*/
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    /**获取服务分组*/
    public String getServiceGroup() {
        return this.serviceGroup;
    }
    /**设置服务分组*/
    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
    /**获取服务版本*/
    public String getServiceVersion() {
        return this.serviceVersion;
    }
    /**设置服务版本*/
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
    /**获取调用的方法名*/
    public String getTargetMethod() {
        return this.targetMethod;
    }
    /**设置调用的方法名*/
    public void setTargetMethod(String targetMethod) {
        this.targetMethod = targetMethod;
    }
    /**获取远程客户端调用超时时间。*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    /**设置远程客户端调用超时时间。*/
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    /**设置收到消息的本地时间*/
    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }
    /**获取收到消息本地时间*/
    public long getReceiveTime() {
        return this.receiveTime;
    }
    /**添加请求参数。*/
    public void addParameter(String paramType, byte[] rawData) {
        this.paramTypes.add(paramType);
        this.paramDatas.add(rawData);
    }
    /**获取请求参数类型列表。*/
    public List<String> getParameterTypes() {
        return this.paramTypes;
    }
    /**获取请求参数总数。*/
    public int getParameterCount() {
        return this.paramTypes.size();
    }
    public String getParameterType(int index) {
        return this.paramTypes.get(index);
    }
    public byte[] getParameterValue(int index) {
        return this.paramDatas.get(index);
    }
    //
    /**将请求参数转换为对象。*/
    public Object[] toParameters(SerializeFactory serializeFactory) throws Throwable {
        SerializeCoder coder = serializeFactory.getSerializeCoder(this.getSerializeType());
        //
        int paramTypesLength = this.paramTypes.size();
        Object[] paramObject = new Object[paramTypesLength];
        //
        if (coder == null && (this.paramTypes.size() > 0)) {
            throw new RsfException(ProtocolStatus.SerializeError,//
                    "Undefined ‘" + this.getSerializeType() + "’ serialize decoder ");
        }
        //
        for (int i = 0; i < paramTypesLength; i++) {
            byte[] paramData = this.paramDatas.get(i);
            paramObject[i] = coder.decode(paramData);
        }
        return paramObject;
    }
}