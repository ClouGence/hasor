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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * 该类的职责是将{@link Map}接口对象转换为{@link IAttribute}接口对象。
 * 但是请注意，{@link Map}的Key必须为String类型否则可能无法通过字符串形式的Key获取到值。
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransformToMap<T> implements Map<String, T> {
    private IAttribute<T> values = null;
    /**创建一个{@link TransformToMap}对象，该对象的作用是将{@link IAttribute}转换为{@link Map}接口。*/
    public TransformToMap(IAttribute<T> values) {
        this.values = values;
    };
    public int size() {
        return this.values.getAttributeNames().length;
    };
    public boolean isEmpty() {
        return (this.size() == 0) ? true : false;
    };
    /**Key必须是字符类型的的。*/
    public boolean containsKey(Object key) {
        return this.values.contains(key.toString());
    };
    public boolean containsValue(Object value) {
        for (String k : this.values.getAttributeNames()) {
            Object obj = this.values.getAttribute(k);
            if (obj != null)
                if (obj.equals(value) == true)
                    return true;
        }
        return false;
    };
    /**Key必须是字符类型的的。*/
    public T get(Object key) {
        return this.values.getAttribute((String) key);
    };
    public T put(String key, T value) {
        this.values.setAttribute(key, value);
        return value;
    };
    public T remove(Object key) {
        String k = (String) key;
        T value = this.values.getAttribute(k);
        this.values.removeAttribute(k);
        return value;
    };
    public void putAll(Map<? extends String, ? extends T> m) {
        for (String key : m.keySet())
            this.put(key, m.get(key));
    };
    public void clear() {
        this.values.clearAttribute();
    };
    public Set<String> keySet() {
        HashSet<String> al = new HashSet<String>();
        for (String k : this.values.getAttributeNames())
            al.add(k);
        return al;
    };
    public Collection<T> values() {
        ArrayList<T> al = new ArrayList<T>(this.size());
        for (String k : this.values.getAttributeNames())
            al.add(this.values.getAttribute(k));
        return al;
    };
    public Set<Map.Entry<String, T>> entrySet() {
        HashSet<Map.Entry<String, T>> al = new HashSet<Map.Entry<String, T>>();
        for (String k : this.values.getAttributeNames())
            al.add(new Entry(k, this.values));
        return al;
    };
    private class Entry implements Map.Entry<String, T> {
        private IAttribute<T> values = null;
        private String        key    = null;
        public Entry(String key, IAttribute<T> values) {
            this.key = key;
            this.values = values;
        };
        public T setValue(T value) {
            this.values.setAttribute(this.key, value);
            return value;
        };
        public T getValue() {
            return this.values.getAttribute(this.key);
        };
        public String getKey() {
            return this.key;
        };
    };
};