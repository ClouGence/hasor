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
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * {@link IAttribute}属性接口的基本实现类，其子类可以通过操作受保护的字段{@link #entrySet}实现更高级的操作。
 * @version : 2012-2-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class Attribute<T> extends AbstractMap<String, T> implements IAttribute<T> {
    private Set<java.util.Map.Entry<String, T>> entrySet = null;
    /** 创建一个基本属性对象。 */
    public Attribute() {}
    /** 创建一个基本属性对象，使用参数对象初始化它。 */
    public Attribute(Map<String, T> prop) {
        this.putAll(prop);
    }
    public final Set<java.util.Map.Entry<String, T>> entrySet() {
        if (this.entrySet == null)
            this.entrySet = this.createEntrySet();
        return this.entrySet;
    }
    /**当{@link #entrySet()}方法被首次调用时会通过该方法创建一个存放{@link Entry}的Set集合。*/
    protected Set<Entry<String, T>> createEntrySet() {
        return new HashSet<Map.Entry<String, T>>();
    };
    /*----------------------------------------------------------------------*/
    public boolean contains(String name) {
        return this.containsKey(name);
    }
    public void setAttribute(String name, T value) {
        this.put(name, value);
    }
    public T getAttribute(String name) {
        return this.get(name);
    }
    public void removeAttribute(String name) {
        this.remove(name);
    }
    public String[] getAttributeNames() {
        String[] keys = new String[this.size()];
        this.keySet().toArray(keys);
        return keys;
    }
    public void clearAttribute() {
        this.clear();
    }
    public Map<String, T> toMap() {
        return this;
    }
    /**将一个map的内容完整的复制到{@link IAttribute}接口中。*/
    public void putMap(Map<String, T> params) {
        this.putAll(params);
    };
    public T put(String key, T value) {
        Entry<String, T> e = new SimpleEntry<T>(key, value);
        this.entrySet().add(e);
        return value;
    }
    public static class SimpleEntry<T> implements Entry<String, T> {
        private String entry_key = null;
        private T      entry_var = null;
        public SimpleEntry(String key, T value) {
            this.entry_key = key;
            this.entry_var = value;
        }
        public String getKey() {
            return this.entry_key;
        }
        public T getValue() {
            return this.entry_var;
        }
        public T setValue(T value) {
            this.entry_var = value;
            return this.entry_var;
        }
    }
}