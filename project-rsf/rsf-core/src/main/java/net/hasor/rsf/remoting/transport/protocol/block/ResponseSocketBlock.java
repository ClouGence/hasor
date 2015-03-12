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
package net.hasor.rsf.remoting.transport.protocol.block;
import org.more.util.ArrayUtils;
/**
 * RSF 1.0 Response 协议
 * --------------------------------------------------------bytes =13
 * byte[1]  version                              RSF版本(0xC1 or 0x81)
 * byte[8]  requestID                            包含的请求ID
 * byte[1]  keepData                             保留区
 * byte[3]  contentLength                        内容大小(max ~ 16MB)
 * --------------------------------------------------------bytes =8
 * byte[2]  status                               响应状态
 * byte[2]  serializeType-(attr-index)           序列化策略
 * byte[2]  returnType-(attr-index)              返回类型
 * byte[2]  returnData-(attr-index)              返回数据
 * --------------------------------------------------------bytes =1 ~ 1021
 * byte[1]  optionCount                          选项参数总数
 *     byte[4]  attr-0-(attr-index,attr-index)   选项参数1
 *     byte[4]  attr-1-(attr-index,attr-index)   选项参数2
 *     ...
 * --------------------------------------------------------bytes =6 ~ 8192
 * byte[2]  attrPool-size (Max = 2047)           池大小
 *     byte[4] att-length                        属性1大小
 *     byte[4] att-length                        属性2大小
 *     ...
 * --------------------------------------------------------bytes =n
 * dataBody                                      数据内容
 *     bytes[...]
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class ResponseSocketBlock extends BaseSocketBlock {
    private byte  version       = 0; //byte[1]  RSF版本(0xC1 or 0x81)
    private long  requestID     = 0; //byte[8]  请求ID
    private short status        = 0; //byte[2]  响应状态
    private short serializeType = 0; //byte[2]  序列化类型
    private short returnType    = 0; //byte[2]  返回类型
    private short returnData    = 0; //byte[2]  返回数据
    private int[] optionMap     = {}; //(attr-index,attr-index)
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
    /**获取响应状态*/
    public short getStatus() {
        return this.status;
    }
    /**设置响应状态*/
    public void setStatus(short status) {
        this.status = status;
    }
    /**获取序列化类型*/
    public short getSerializeType() {
        return this.serializeType;
    }
    /**设置序列化类型*/
    public void setSerializeType(short serializeType) {
        this.serializeType = serializeType;
    }
    /**获取返回值类型*/
    public short getReturnType() {
        return this.returnType;
    }
    /**设置返回值类型*/
    public void setReturnType(short returnType) {
        this.returnType = returnType;
    }
    /**获取返回值数据*/
    public short getReturnData() {
        return returnData;
    }
    /**设置返回值数据*/
    public void setReturnData(short returnData) {
        this.returnData = returnData;
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
            optDatas[i] = (short) this.optionMap[i];
        }
        return optDatas;
    }
    /**获取Option。*/
    public int[] getOptions() {
        return this.optionMap;
    }
}