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
package org.more.core.iatt;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * 该类的职责是将{@link Map}接口对象转换为{@link IAttribute}接口对象。
 * 但是请注意，{@link Map}的Key必须为String类型否则可能无法通过字符串形式的Key获取到值。
 * Date : 2011-4-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class TransformToMap<T> extends AbstractMap<String, T> {
    private IAttribute<T> values   = null;
    private TransformSet  entrySet = null;
    //
    /**创建一个{@link TransformToMap}对象，该对象的作用是将{@link IAttribute}转换为{@link Map}接口。*/
    public TransformToMap(IAttribute<T> values) {
        this.values = values;
        this.entrySet = new TransformSet(values);
    };
    public T put(String key, T value) {
        this.values.setAttribute(key, value);
        return value;
    };
    public Set<java.util.Map.Entry<String, T>> entrySet() {
        return this.entrySet;
    };
    //
    //
    //
    private class TransformSet extends AbstractSet<java.util.Map.Entry<String, T>> {
        private IAttribute<T> values = null;
        public TransformSet(IAttribute<T> values) {
            this.values = values;
        };
        public int size() {
            return values.size();
        };
        public Iterator<java.util.Map.Entry<String, T>> iterator() {
            return new TransformIterator(this.values);
        };
    };
    private class TransformIterator implements Iterator<Map.Entry<String, T>> {
        private IAttribute<T>    values      = null;
        private Iterator<String> names       = null;
        private String           currentName = null;
        public TransformIterator(IAttribute<T> values) {
            this.values = values;
            List<String> names = Arrays.asList(this.values.getAttributeNames());
            this.names = names.iterator();
        }
        public boolean hasNext() {
            return this.names.hasNext();
        }
        public java.util.Map.Entry<String, T> next() {
            this.currentName = this.names.next();
            return new Entry(this.currentName, this.values);
        }
        public void remove() {
            this.values.removeAttribute(this.currentName);
        }
    }
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
    }
};