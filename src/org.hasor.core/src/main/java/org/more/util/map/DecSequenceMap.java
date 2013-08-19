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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.util.MergeUtils;
/**
 * 按照顺序合并多个Map的工具，可以用来进行读写操作。
 * @version : 2012-2-23
 * @author 赵永春 (zyc@hasor.net)
 */
public class DecSequenceMap<K, T> extends AbstractMap<K, T> {
    private SimpleSet<K, T> entrySet = null;
    /** 创建一个基本属性对象。 */
    public DecSequenceMap() {}
    /** 创建一个基本属性对象，如果构造该参数为空则 */
    public DecSequenceMap(Map<K, T> entryMap) {
        if (entryMap != null)
            this.entrySet().addMap(entryMap);
    }
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
    /**删除一个map*/
    public void removeAllMap() {
        if (entrySet().isEmpty() == false)
            entrySet().clear();
    }
    public List<Map<K, T>> getMapList() {
        return Collections.unmodifiableList(this.entrySet().mapList);
    };
    public T put(K key, T value) {
        return this.entrySet().mapList.get(0).put(key, value);
    }
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
        public Iterator<java.util.Map.Entry<K, T>> iterator() {
            Iterator<java.util.Map.Entry<K, T>> seqIter = null;
            for (Map<K, T> mapItem : this.mapList)
                seqIter = MergeUtils.mergeIterator(seqIter, mapItem.entrySet().iterator());
            return seqIter;
        }
        public int size() {
            int count = 0;
            for (Map<K, T> map : mapList)
                count += map.size();
            return count;
        }
    }
}