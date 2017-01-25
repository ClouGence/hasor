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
package org.more.xml;
import org.more.util.map.DecStackMap;

import java.util.HashMap;
/**
 * 该类继承自{@link DecStackMap}装饰器，作用是提供了一个context对象的支持。
 * @version 2010-9-23
 * @author 赵永春 (zyc@hasor.net)
 */
public class XmlStackDecorator<T> extends DecStackMap<String, Object> {
    private T context = null;
    /**获取Context*/
    public T getContext() {
        return this.context;
    }
    /**设置Context*/
    public void setContext(final T context) {
        this.context = context;
    }
    //---------------
    private HashMap<String, NameSpace> nameSpaceMap = new HashMap<String, NameSpace>();
    public NameSpace getNameSpace(final String prefix) {
        return this.nameSpaceMap.get(prefix);
    }
    void addNameSpace(final String prefix, final NameSpace nameSpace) {
        this.nameSpaceMap.put(prefix, nameSpace);
    }
}