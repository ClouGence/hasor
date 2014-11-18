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
package net.hasor.rsf.runtime;
import net.hasor.rsf.metadata.ServiceMetaData;
/**
 * 请求响应通用。
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RsfHeader {
    /**获取元信息*/
    public ServiceMetaData getMetaData();
    /**获取协议版本。*/
    public byte getProtocol();
    /**请求ID。*/
    public long getRequestID();
    /**客户端希望的序列化方式*/
    public String getSerializeType();
    // 
    /**获取选项Key集合。*/
    public String[] getOptionKeys();
    /**获取选项数据*/
    public String getOption(String key);
    /**设置选项数据*/
    public void addOption(String key, String value);
}