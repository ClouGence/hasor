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
package org.more.hypha.el.assembler;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.more.hypha.ApplicationContext;
import org.more.hypha.el.ELObject;
/**
 * EL中对应为{@link ApplicationContext context}对象的Map形式，不支持赋值操作。
 * Date : 2011-4-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class EO_Attribute implements ELObject {
    private ApplicationContext context = null;
    public void init(ApplicationContext context) {
        this.context = context;
    };
    public boolean isReadOnly() {
        return true;
    };
    public void setValue(Object value) {
        throw new UnsupportedOperationException();
    };
    public Object getValue() {
        return new _ContextMap(this.context);//将Context转换为map对象
    };
};
class _ContextMap extends AbstractMap<String, Object> {
    private _ContextSet contextSet = null;
    public _ContextMap(ApplicationContext context) {
        this.contextSet = new _ContextSet(context);
    }
    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return this.contextSet;
    }
};
class _ContextSet extends AbstractSet<java.util.Map.Entry<String, Object>> {
    private ApplicationContext context = null;
    public _ContextSet(ApplicationContext context) {
        this.context = context;
    }
    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return new _ContextIterator(this.context, this.context.getBeanDefinitionIDs().iterator());
    }
    @Override
    public int size() {
        return this.context.getBeanDefinitionIDs().size();
    }
}
class _ContextIterator implements Iterator<Entry<String, Object>> {
    private ApplicationContext context  = null;
    private Iterator<String>   iterator = null;
    public _ContextIterator(ApplicationContext context, Iterator<String> iterator) {
        this.context = context;
        this.iterator = iterator;
    }
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    @Override
    public Entry<String, Object> next() {
        return new _ContextEntry(this.context, this.iterator.next());
    }
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
class _ContextEntry implements java.util.Map.Entry<String, Object> {
    private ApplicationContext context  = null;
    private String             beanName = null;
    public _ContextEntry(ApplicationContext context, String beanName) {
        this.context = context;
        this.beanName = beanName;
    }
    @Override
    public String getKey() {
        return this.beanName;
    }
    @Override
    public Object getValue() {
        return this.context.getBean(this.beanName);
    }
    @Override
    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }
}