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
import org.more.util.ArrayUtils;
/**
 * RSF 1.0 Request 协议
 * --------------------------------------------------------bytes =13
 * byte[1]  version                              RSF版本(0xC1 or 0x81)
 * byte[8]  requestID                            请求ID
 * byte[1]  keepData                             保留区
 * byte[3]  contentLength                        内容大小(max ~ 16MB)
 * --------------------------------------------------------bytes =14
 * byte[2]  servicesName-(attr-index)            远程服务名
 * byte[2]  servicesGroup-(attr-index)           远程服务分组
 * byte[2]  servicesVersion-(attr-index)         远程服务版本
 * byte[2]  servicesMethod-(attr-index)          远程服务方法名
 * byte[2]  serializeType-(attr-index)           序列化策略
 * byte[4]  clientTimeout                        远程客户端超时时间
 * --------------------------------------------------------bytes =1 ~ 1021
 * byte[1]  paramCount                           参数总数
 *     byte[4]  ptype-0-(attr-index,attr-index)  参数类型1
 *     byte[4]  ptype-1-(attr-index,attr-index)  参数类型2
 *     ...
 * --------------------------------------------------------bytes =1 ~ 1021
 * byte[1]  optionCount                          选项参数总数
 *     byte[4]  attr-0-(attr-index,attr-index)   选项参数1
 *     byte[4]  attr-1-(attr-index,attr-index)   选项参数2
 *     ...
 * --------------------------------------------------------bytes =6 ~ 8192
 * byte[2]  attrPool-size (0~4095)               池大小 0x0FFF
 *     byte[4] att-length                        属性1大小
 *     byte[4] att-length                        属性2大小
 *     ...
 * --------------------------------------------------------bytes =n
 * dataBody                                      数据内容
 *     bytes[...]
 * 
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RequestSocketBlock extends PoolSocketBlock implements RsfSocketBlock {
    private byte  version        = 0; //byte[1]  RSF版本(0xC1 or 0x81)
    private long  requestID      = 0; //byte[8]  请求ID
    private short serviceName    = 0; //byte[2]  远程服务名
    private short serviceGroup   = 0; //byte[2]  远程服务分组
    private short serviceVersion = 0; //byte[2]  远程服务版本
    private short targetMethod   = 0; //byte[2]  远程服务方法名
    private short serializeType  = 0; //byte[2]  序列化策略
    private int   clientTimeout  = 0; //byte[4]  远程客户端超时时间
    private int[] paramData      = {}; //(attr-index,attr-index)
    private int[] optionMap      = {}; //(attr-index,attr-index)
    //
    //
    /**获取协议版本。*/
    public byte getVersion() {
        return this.version;
    }
    /**设置协议版本。*/
    public void setVersion(byte version) {
        this.version = version;
    }
    /**获取请求ID。*/
    public long getRequestID() {
        return this.requestID;
    }
    /**设置请求ID。*/
    public void setRequestID(long requestID) {
        this.requestID = requestID;
    }
    /**获取服务名*/
    public short getServiceName() {
        return this.serviceName;
    }
    /**设置服务名*/
    public void setServiceName(short serviceName) {
        this.serviceName = serviceName;
    }
    /**获取服务分组*/
    public short getServiceGroup() {
        return this.serviceGroup;
    }
    /**设置服务分组*/
    public void setServiceGroup(short serviceGroup) {
        this.serviceGroup = serviceGroup;
    }
    /**获取服务版本*/
    public short getServiceVersion() {
        return this.serviceVersion;
    }
    /**设置服务版本*/
    public void setServiceVersion(short serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
    /**获取调用的方法名*/
    public short getTargetMethod() {
        return this.targetMethod;
    }
    /**设置调用的方法名*/
    public void setTargetMethod(short targetMethod) {
        this.targetMethod = targetMethod;
    }
    /**获取序列化类型*/
    public short getSerializeType() {
        return this.serializeType;
    }
    /**设置序列化类型*/
    public void setSerializeType(short serializeType) {
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
    public void addParameter(short paramType, short paramData) {
        int pType = paramType << 16;
        int pData = paramData;
        int mergeData = (pType | pData);
        this.addParameter(mergeData);
    }
    /**添加请求参数。*/
    public void addParameter(int mergeData) {
        this.paramData = ArrayUtils.add(this.paramData, mergeData);
    }
    /**获取请求参数类型列表。*/
    public short[] getParameterTypes() {
        short[] pTypes = new short[this.paramData.length];
        for (int i = 0; i < this.paramData.length; i++) {
            int mergeData = this.paramData[i];
            pTypes[i] = (short) (mergeData >>> 16);
        }
        return pTypes;
    }
    /**获取请求参数类型列表。*/
    public short[] getParameterValues() {
        short[] pDatas = new short[this.paramData.length];
        for (int i = 0; i < this.paramData.length; i++) {
            pDatas[i] = (short) (PoolSocketBlock.PoolMaxSize & this.paramData[i]);
        }
        return pDatas;
    }
    //
    /**添加选项。*/
    public void addOption(short paramType, short paramData) {
        int pType = paramType << 16;
        int pData = paramData;
        int mergeData = (pType | pData);
        this.addOption(mergeData);
    }
    /**添加选项。*/
    public void addOption(int mergeData) {
        this.optionMap = ArrayUtils.add(this.optionMap, mergeData);
    }
    /**获取选项Key集合。*/
    public short[] getOptionKeys() {
        short[] optKeys = new short[this.optionMap.length];
        for (int i = 0; i < this.optionMap.length; i++) {
            int mergeData = this.optionMap[i];
            optKeys[i] = (short) (mergeData >>> 16);
        }
        return optKeys;
    }
    /**获取选项数据*/
    public short[] getOptionValues() {
        short[] optDatas = new short[this.optionMap.length];
        for (int i = 0; i < this.optionMap.length; i++) {
            optDatas[i] = (short) (0x0000FFFF & this.optionMap[i]);
        }
        return optDatas;
    }
    /**获取Option。*/
    public int[] getOptions() {
        return this.optionMap;
    }
    /**获取请求参数类型列表。*/
    public int[] getParameters() {
        return this.paramData;
    }
}