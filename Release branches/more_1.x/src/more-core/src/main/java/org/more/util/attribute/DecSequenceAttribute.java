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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * 将一组{@link IAttribute}对象以序列的方式进行操作
 * 这种结构{@link #setAttribute(String, Object)}、{@link #removeAttribute(String)}、{@link #clearAttribute()}
 * 三个方法只会影响到{@link #getSource()}返回的属性对象。通过构造方法可以替换它。
 * @version : 2011-7-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class DecSequenceAttribute<V> extends AttributeDecorator<V> {
    private Map<String, IAttribute<V>> attMap  = new HashMap<String, IAttribute<V>>();
    private List<IAttribute<V>>        attList = new ArrayList<IAttribute<V>>();
    /**创建DecSequenceAttribute对象，使用新建的Attribute对象作为数据源。*/
    public DecSequenceAttribute() {
        this(new Attribute<V>(), null);
    };
    /**创建DecSequenceAttribute对象，使用新建的Attribute对象作为数据源。*/
    public DecSequenceAttribute(Collection<IAttribute<V>> collection) {
        this(new Attribute<V>(), collection);
    };
    /**
     * 创建DecSequenceAttribute对象，使用一个已有的Attribute对象作为数据源。
     * @param source 数据源。
     */
    public DecSequenceAttribute(IAttribute<V> source, Collection<IAttribute<V>> collection) {
        super(source);
        if (collection != null)
            for (IAttribute<V> att : collection)
                if (att != null)
                    this.attList.add(att);
    };
    public void putAtt(String name, IAttribute<V> att) {
        if (this.attMap.containsKey(name) == false) {
            this.attMap.put(name, att);
            this.attList.add(att);
        }
    };
    public List<IAttribute<V>> getAttList() {
        return this.attList;
    };
    public int getAttCount() {
        return this.attList.size();
    };
    public boolean containsAtt(String name) {
        return this.attMap.containsKey(name);
    };
    public IAttribute<V> removeAtt(String name) {
        if (this.attMap.containsKey(name) == true) {
            IAttribute<V> att = this.attMap.remove(name);
            if (att != null)
                this.attList.remove(att);
            return att;
        }
        return null;
    };
    public void putAtt(IAttribute<V> att) {
        this.putAtt(UUID.randomUUID().toString(), att);
    };
    /**将{@link IAttribute}接口对象弹出队列。*/
    public void popStack(String name) {};
    /**获取序列中指定位置的属性对象。*/
    public IAttribute<V> getIndex(int index) {
        return this.attList.get(index);
    };
    public boolean contains(String name) {
        if (super.contains(name) == true)
            return true;
        for (IAttribute<?> iatt : this.attList)
            if (iatt.contains(name) == true)
                return true;
        return false;
    };
    public V getAttribute(String name) {
        V res = super.getAttribute(name);
        if (res == null)
            for (IAttribute<V> iatt : this.attList) {
                res = iatt.getAttribute(name);
                if (res != null)
                    return res;
            }
        return res;
    };
    /**返回当前属性集以及序列属性集中的所有属性名（不重复）。<br/><b>这是一个高成本操作。</b>*/
    public String[] getAttributeNames() {
        HashSet<String> names = new HashSet<String>();
        Collections.addAll(names, super.getAttributeNames());
        for (IAttribute<?> attItem : this.attList)
            Collections.addAll(names, attItem.getAttributeNames());
        String[] array = new String[names.size()];
        names.toArray(array);
        return array;
    };
    /**返回所有队列元素中的属性总数。<br/><b>这是一个高成本操作。*/
    public int size() {
        return this.getAttributeNames().length;
    };
};