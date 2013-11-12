/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.util.map;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.util.MergeUtils;
/**
 * 可以将多个Map合并成一个Map对象给予操作。
 * @version : 2012-2-23
 * @author 赵永春 (zyc@hasor.net)
 */
public class DecSequenceMap<K, T> extends AbstractMap<K, T> {
    private volatile SimpleSet<K, T> entrySet = null;
    //
    //
    /** 创建DecSequenceMap对象，根据{@link #DecSequenceMap(boolean) DecSequenceMap(true)}规则进行初始化。*/
    public DecSequenceMap() {
        this(true);
    }
    /**
     * 创建DecSequenceMap对象。initMap参数表示是否为序列添加一个默认的初始Map。
     * @param initMap true表示自动加入一个初始Map作为第一个元素，否则DecSequenceMap中没有任何成员。
     *      初始Map的创建是通过受保护的方法{@link #initMap()}方法创建。
     */
    public DecSequenceMap(boolean initMap) {
        if (initMap) {
            Map<K, T> initializationMap = this.initMap();
            if (initializationMap == null)
                throw new NullPointerException("initMap has null.");
            this.entrySet().addMap(initializationMap);
        }
    }
    /**
     * 创建DecSequenceMap对象。
     * @param entryMap 参数表示在初始化时候，将参数表示的Map对象作为默认初始Map。
     *      如果参数为空则根据{@link #DecSequenceMap(boolean) DecSequenceMap(true)}规则进行初始化。
     */
    public DecSequenceMap(Map<K, T> entryMap) {
        this(entryMap, true);
    }
    /**
     * 创建DecSequenceMap对象。使用entryMap、initMap参数同时作用初始化。
     * @param entryMap 参数表示在初始化时候，将参数表示的Map对象作为默认初始第一个元素。
     *      如果参数为空则根据initMap参数值来决定初始化规则。
     * @param initMap 该值为true表示使用{@link #initMap()}方法创建一个Map作为第一个元素，否则DecSequenceMap中没有任何成员。
     */
    public DecSequenceMap(Map<K, T> entryMap, boolean initMap) {
        this(initMap);
        if (entryMap != null)
            this.entrySet().addMap(entryMap);
    }
    /***/
    protected Map<K, T> initMap() {
        return new HashMap<K, T>();
    }
    //
    public final SimpleSet<K, T> entrySet() {
        if (this.entrySet == null)
            this.entrySet = this.createSet();
        return this.entrySet;
    }
    /**创建{@link SimpleSet}对象。*/
    protected SimpleSet<K, T> createSet() {
        return new SimpleSet<K, T>();
    }
    /**按照顺序加入一个Map到序列中。*/
    public void addMap(Map<K, T> newMap) {
        entrySet().addMap(newMap);
    }
    /**按照指定顺序插入一个Map到序列中。*/
    public void addMap(int index, Map<K, T> newMap) {
        entrySet().addMap(index, newMap);
    }
    /**删除一个map*/
    public void removeMap(int index) {
        entrySet().removeMap(index);
    }
    /**删除一个map*/
    public void removeMap(Map<K, T> newMap) {
        entrySet().removeMap(newMap);
    }
    /**删除所有已经添加的map*/
    public void removeAllMap() {
        if (entrySet().isEmpty() == false)
            entrySet().clear();
    }
    public List<Map<K, T>> elementMapList() {
        return Collections.unmodifiableList(this.entrySet().mapList);
    };
    /**确认K所在的Map*/
    public Map<K, T> keyAt(K key) {
        for (Map<K, T> e : this.elementMapList())
            if (e.containsKey(key))
                return e;
        return null;
    }
    /**确认T所在的Map*/
    public Map<K, T> valueAt(T value) {
        for (Map<K, T> e : this.elementMapList())
            if (e.containsValue(value))
                return e;
        return null;
    }
    @Override
    public T put(K key, T value) {
        return this.entrySet().mapList.get(0).put(key, value);
    }
    @Override
    public T remove(Object key) {
        return this.entrySet().mapList.get(0).remove(key);
    }
    /*----------------------------------------------------------------------*/
    public static class SimpleSet<K, T> extends AbstractSet<Entry<K, T>> {
        protected List<Map<K, T>> mapList = new ArrayList<Map<K, T>>();
        public void addMap(Map<K, T> newMap) {
            this.mapList.add(newMap);
        }
        public void addMap(int index, Map<K, T> newMap) {
            this.mapList.add(index, newMap);
        }
        public void removeMap(int index) {
            this.mapList.remove(index);
        }
        public void removeMap(Map<K, T> newMap) {
            this.mapList.remove(newMap);
        }
        public void clear() {
            this.mapList.clear();
        }
        @Override
        public Iterator<java.util.Map.Entry<K, T>> iterator() {
            Iterator<java.util.Map.Entry<K, T>> seqIter = null;
            for (Map<K, T> mapItem : this.mapList)
                seqIter = MergeUtils.mergeIterator(seqIter, mapItem.entrySet().iterator());
            return seqIter;
        }
        @Override
        public int size() {
            int count = 0;
            for (Map<K, T> map : mapList)
                count += map.size();
            return count;
        }
    }
}