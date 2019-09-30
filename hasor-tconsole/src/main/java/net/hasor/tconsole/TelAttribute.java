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
package net.hasor.tconsole;
import java.util.Set;

/**
 * 属性
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface TelAttribute {
    /** 获取属性。*/
    public Object getAttribute(String key);

    /** 设置属性。*/
    public void setAttribute(String key, Object value);

    /** 获取属性名称集合 */
    public Set<String> getAttributeNames();
}