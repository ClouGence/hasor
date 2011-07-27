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
package org.more.util.attribute;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * 基本属性接口实现类。注意：IAttribute接口特性及其子接口的功能将不会作用到Map接口上。
 * 开发人员仍然可以通过Map接口操作AttBase中的数据。但是Map接口将不会支持IAttribute接口的所有装饰器接口特性。
 * @version 2009-4-29
 * @author 赵永春 (zyc@byshell.org)
 */
public class AttBase implements IAttribute, Map<String, Object>, Serializable {
    //========================================================================================Field
    private static final long   serialVersionUID = 5330675593787806813L;
    /** 存放属性的集合 */
    private Map<String, Object> prop;
    //==================================================================================Constructor
    //
    /**
     * 创建一个基本属性对象，属性的存放使用HashMap作为属性盛装器。基本属性接口实现类。
     * 注意：IAttribute接口特性及其子接口的功能将不会作用到Map接口上。开发人员仍然可以通过Map接口操作AttBase中的数据。
     * 但是Map接口将不会支持IAttribute接口的所有装饰器接口特性。
     */
    public AttBase() {
        this.prop = new HashMap<String, Object>();
    }
    /**
     * 创建一个基本属性对象，存放属性的盛装器可以是任何Map接口子接口及其实现类。如果prop参数提供的是
     * Hashtable则Attribute接口将不可接受空值的属性。如果prop属性为空BaseAtt将默认使用HashMap作为
     * 属性盛装器。
     * @param prop 属性盛装器，如果prop属性为空BaseAtt将默认使用HashMap作为属性盛装器。
     */
    protected AttBase(Map<String, Object> prop) {
        if (prop == null)
            this.prop = new HashMap<String, Object>();
        else
            this.prop = prop;
    }
    //==========================================================================================Job
    /**
     * 获取BaseAtt的属性盛装器。
     * @return 返回获取BaseAtt的属性盛装器。
     */
    protected Map<String, Object> getProp() {
        return this.prop;
    }
    /**
     * 设置BaseAtt的属性保存对象，该方法将会替换原有BaseAtt对象内部的属性保存对象。
     * @param prop 设置属性对象保存Map
     */
    protected void setProp(HashMap<String, Object> prop) {
        this.prop = prop;
    }
    public void clearAttribute() {
        this.prop.clear();
    }
    public boolean contains(String name) {
        return this.prop.containsKey(name);
    }
    public Object getAttribute(String name) {
        return this.prop.get(name);
    }
    public String[] getAttributeNames() {
        String[] keys = new String[this.prop.size()];
        this.prop.keySet().toArray(keys);
        return keys;
    }
    public void removeAttribute(String name) {
        this.prop.remove(name);
    }
    public void setAttribute(String name, Object value) {
        this.prop.put(name, value);
    }
    //==================================================================================Map接口实现
    public void clear() {
        this.prop.clear();
    }
    public boolean containsKey(Object key) {
        return this.prop.containsKey(key);
    }
    public boolean containsValue(Object value) {
        return this.prop.containsValue(value);
    }
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return this.prop.entrySet();
    }
    public Object get(Object key) {
        return this.prop.get(key);
    }
    public boolean isEmpty() {
        return this.prop.isEmpty();
    }
    public Set<String> keySet() {
        return this.prop.keySet();
    }
    public Object put(String key, Object value) {
        return this.prop.put(key, value);
    }
    public void putAll(Map<? extends String, ? extends Object> m) {
        this.prop.putAll(m);
    }
    public Object remove(Object key) {
        return this.prop.remove(key);
    }
    public int size() {
        return this.prop.size();
    }
    public Collection<Object> values() {
        return this.prop.values();
    }
    public Map<String, Object> toMap() {
        return this.prop;
    }
}