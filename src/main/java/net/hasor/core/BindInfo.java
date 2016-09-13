/*
 * Copyright 2008-2009 the original author or authors.
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
package net.hasor.core;
/**
 * 表示一个 bean 的配置信息。
 * @version : 2014-3-17
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindInfo<T> {
    /** @return 绑定的ID */
    public String getBindID();

    /** @return 为类型绑定的名称。*/
    public String getBindName();

    /** @return 获取注册的类型*/
    public Class<T> getBindType();

    /**
     * 获取元信息。
     * @param key 元信息 key
     * @return 返回元信息值
     */
    public Object getMetaData(String key);

    /**
     * 设置元数据
     * @param key 元信息 key
     * @param value 元信息值
     */
    public void setMetaData(String key, Object value);

    /**
     * 删除元数据
     * @param key 元信息 key
     */
    public void removeMetaData(String key);
}