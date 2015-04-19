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
/**
 * RSF 1.0 Request 协议下请求响应数据格式中通用数据的获取接口。   
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfSocketBlock {
    /**获取协议版本。*/
    public byte getVersion();
    /**获取请求ID。*/
    public long getRequestID();
    /**获取序列化类型*/
    public short getSerializeType();
    //
    /**添加选项。*/
    public void addOption(short paramType, short paramData);
    /**添加选项。*/
    public void addOption(int mergeData);
    /**获取选项Key集合。*/
    public short[] getOptionKeys();
    /**获取选项数据*/
    public short[] getOptionValues();
    /**获取Option。*/
    public int[] getOptions();
    //
    /**内容所处起始位置*/
    public byte[] readPool(short attrIndex);
    /**添加请求参数。*/
    public short pushData(byte[] dataArray);
}