/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.plugins.datachain;
import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @version : 2016年5月6日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerDataDomain<I> implements Domain<I> {
    private I                     domain;
    private Map<Class<?>, Object> attachData = new HashMap<Class<?>, Object>();
    InnerDataDomain(I domain) {
        this.domain = domain;
    }
    public I getDomain() {
        return this.domain;
    }
    /* 写入附加数据 */
    public <T> void attachData(Class<T> type, T attachData) {
        this.attachData.put(type, attachData);
    }
    /* 读取附加数据 */
    public <T> T attachData(Class<T> type) {
        return (T) this.attachData.get(type);
    }
}