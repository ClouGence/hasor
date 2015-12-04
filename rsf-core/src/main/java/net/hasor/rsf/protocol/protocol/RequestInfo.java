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
package net.hasor.rsf.protocol.protocol;
import java.util.ArrayList;
import java.util.List;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.protocol.ProtocolUtils;
import net.hasor.rsf.utils.ByteStringCachelUtils;
/**
 * RSF Request 数据对象。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestInfo extends OptionInfo {
    private long         requestID      = 0;    //请求ID
    private long         receiveTime    = 0;    //接收请求（本地时间戳）
    private String       serviceName    = null; //远程服务名
    private String       serviceGroup   = null; //远程服务分组
    private String       serviceVersion = null; //远程服务版本
    private String       targetMethod   = null; //远程服务方法名
    private String       serializeType  = null; //序列化策略
    private int          clientTimeout  = 0;    //远程调用时最大忍受等待时间
    private List<String> paramTypes     = null; //参数类型
    private List<byte[]> paramValues    = null; //参数值
    //
    //
    public RequestInfo() {
        this.paramTypes = new ArrayList<String>();
        this.paramValues = new ArrayList<byte[]>();
    }
    public RequestInfo(RequestBlock rsfBlock) {
        this();
        this.recovery(rsfBlock);
    }
    /**获取请求ID。*/
    public long getRequestID() {
        return this.requestID;
    }
    /**设置请求ID。*/
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }
    /**数据包到达时间*/
    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }
    /**数据包到达时间*/
    public long getReceiveTime() {
        return this.receiveTime;
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
    /**获取序列化类型*/
    public String getSerializeType() {
        return this.serializeType;
    }
    /**设置序列化类型*/
    public void setSerializeType(String serializeType) {
        this.serializeType = serializeType;
    }
    /**获取远程客户端调用超时时间。*/
    public int getClientTimeout() {
        return this.clientTimeout;
    }
    /**设置远程客户端调用超时时间。*/
    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }
    //
    /**添加请求参数。*/
    public void addParameter(String paramType, byte[] paramData) {
        this.paramTypes.add(paramType);
        this.paramValues.add(paramData);
    }
    /**添加请求参数。*/
    public void updateParameter(int index, String paramType, byte[] paramData) {
        if (index < 0 || index > this.paramTypes.size()) {
            throw new IndexOutOfBoundsException("index out of range 0~" + this.paramTypes.size());
        }
        this.paramTypes.set(index, paramType);
        this.paramValues.set(index, paramData);
    }
    /**获取请求参数类型列表。*/
    public List<String> getParameterTypes() {
        return new ArrayList<String>(this.paramTypes);
    }
    /**获取请求参数值列表。*/
    public List<byte[]> getParameterValues() {
        return new ArrayList<byte[]>(this.paramValues);
    }
    //
    //
    protected void recovery(RequestBlock rsfBlock) {
        //
        //1.基本数据
        this.requestID = rsfBlock.getRequestID();
        short serializeType = rsfBlock.getSerializeType();
        this.serializeType = ByteStringCachelUtils.fromCache(rsfBlock.readPool(serializeType));
        //
        //2.Opt参数
        int[] optionArray = rsfBlock.getOptions();
        for (int optItem : optionArray) {
            short optKey = (short) (optItem >>> 16);
            short optVal = (short) (optItem & PoolBlock.PoolMaxSize);
            String optKeyStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optKey));
            String optValStr = ByteStringCachelUtils.fromCache(rsfBlock.readPool(optVal));
            this.addOption(optKeyStr, optValStr);
        }
        //
        //3.Request
        this.targetMethod = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getTargetMethod()));
        this.serviceGroup = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceGroup()));
        this.serviceName = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceName()));
        this.serviceVersion = ByteStringCachelUtils.fromCache(rsfBlock.readPool(rsfBlock.getServiceVersion()));
        this.clientTimeout = rsfBlock.getClientTimeout();
        this.paramTypes = new ArrayList<String>();
        this.paramValues = new ArrayList<byte[]>();
        int[] paramDatas = rsfBlock.getParameters();
        for (int i = 0; i < paramDatas.length; i++) {
            int paramItem = paramDatas[i];
            short paramKey = (short) (paramItem >>> 16);
            short paramVal = (short) (paramItem & PoolBlock.PoolMaxSize);
            byte[] keyData = rsfBlock.readPool(paramKey);
            byte[] valData = rsfBlock.readPool(paramVal);
            //
            String paramType = ByteStringCachelUtils.fromCache(keyData);
            this.paramTypes.add(paramType);
            this.paramValues.add(valData);
        }
    }
    /**构建一个二进制协议对象。*/
    public RequestBlock buildBlock() {
        RequestBlock block = new RequestBlock();
        //
        //1.基本信息
        block.setHead(RSFConstants.RSF_Request);
        block.setRequestID(this.getRequestID());//请求ID
        block.setServiceGroup(ProtocolUtils.pushString(block, this.getServiceGroup()));
        block.setServiceName(ProtocolUtils.pushString(block, this.getServiceName()));
        block.setServiceVersion(ProtocolUtils.pushString(block, this.getServiceVersion()));
        block.setTargetMethod(ProtocolUtils.pushString(block, this.getTargetMethod()));
        block.setSerializeType(ProtocolUtils.pushString(block, this.getSerializeType()));
        block.setClientTimeout(this.getClientTimeout());
        //
        //2.params
        List<String> pTypes = this.getParameterTypes();
        List<byte[]> pValues = getParameterValues();
        for (int i = 0; i < pTypes.size(); i++) {
            String typeKey = pTypes.get(i);
            byte[] valKey = pValues.get(i);
            //
            short paramType = ProtocolUtils.pushString(block, typeKey);
            short paramData = ProtocolUtils.pushBytes(block, valKey);
            block.addParameter(paramType, paramData);
        }
        //
        //3.Opt参数
        String[] optKeys = getOptionKeys();
        for (int i = 0; i < optKeys.length; i++) {
            short optKey = ProtocolUtils.pushString(block, optKeys[i]);
            short optVal = ProtocolUtils.pushString(block, getOption(optKeys[i]));
            block.addOption(optKey, optVal);
        }
        //
        return block;
    }
}