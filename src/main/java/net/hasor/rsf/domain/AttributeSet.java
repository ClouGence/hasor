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
package net.hasor.rsf.domain;
import net.hasor.rsf.utils.Iterators;

import java.util.*;
/**
 *
 * @version : 2015年1月24日
 * @author 赵永春(zyc@hasor.net)
 */
public class AttributeSet extends OptionInfo {
    private final Map<String, Object> attributeMap = new HashMap<String, Object>();
    //
    /**获取属性*/
    public Object getAttribute(String attrKey) {
        return this.attributeMap.get(attrKey);
    }
    /**保存属性,属性会在请求完毕之后丢失。特性和 web 下的 request 属性类似。*/
    public void setAttribute(String attrKey, Object attrValue) {
        this.attributeMap.put(attrKey, attrValue);
    }
    /**删除属性*/
    public void removeAttribute(String attrKey) {
        this.attributeMap.remove(attrKey);
    }
    /**获取所有属性名。*/
    public Enumeration<String> getAttributeNames() {
        Iterator<String> keys = new ArrayList<String>(this.attributeMap.keySet()).iterator();
        return Iterators.asEnumeration(keys);
    }
}