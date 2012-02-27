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
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * {@link IAttribute}属性接口的基本实现类，其子类可以通过操作受保护的字段{@link #entrySet}实现更高级的操作。
 * @version : 2012-2-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class Attribute<V> extends AbstractMap<String, V> implements IAttribute<V>, Serializable {
    private static final long                     serialVersionUID = -2405343948202779870L;
    protected Set<java.util.Map.Entry<String, V>> entrySet         = new HashSet<Map.Entry<String, V>>();
    /** 创建一个基本属性对象。 */
    public Attribute() {}
    /** 创建一个基本属性对象，使用参数对象初始化它。 */
    public Attribute(Map<String, V> prop) {
        if (prop != null)
            this.entrySet = prop.entrySet();
    }
    @Override
    public boolean contains(String name) {
        return this.containsKey(name);
    }
    @Override
    public void setAttribute(String name, V value) {
        this.put(name, value);
    }
    @Override
    public V getAttribute(String name) {
        return this.get(name);
    }
    @Override
    public void removeAttribute(String name) {
        this.remove(name);
    }
    @Override
    public String[] getAttributeNames() {
        String[] keys = new String[this.size()];
        this.keySet().toArray(keys);
        return keys;
    }
    @Override
    public void clearAttribute() {
        this.clear();
    }
    @Override
    public Map<String, V> toMap() {
        return this;
    }
    @Override
    public Set<java.util.Map.Entry<String, V>> entrySet() {
        return this.entrySet;
    }
    @Override
    public V put(String key, V value) {
        Entry<String, V> e = new SimpleEntry<String, V>(key, value);
        this.entrySet.add(e);
        return value;
    }
}