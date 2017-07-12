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
package net.hasor.core.setting;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 可以将多个Map合并成一个Map对象给予操作,每个子map可以通过一个空间字符串进行标识。
 * @version : 2016-07-17
 * @author 赵永春 (zyc@hasor.net)
 */
public class DecSpaceMap<K, T> {
    protected Map<String, Map<K, T>> spaceMap = new HashMap<String, Map<K, T>>();
    //
    /**将一个值保存到一个命名空间下。*/
    public T put(final String space, final K key, final T value) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap == null) {
            spaceMap = new ConcurrentHashMap<K, T>();
            this.spaceMap.put(space, spaceMap);
        }
        return spaceMap.put(key, value);
    }
    /**将一个map加入或追加到一个命名空间下。*/
    public void putAll(final String space, final Map<K, T> newMap) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap == null) {
            spaceMap = new ConcurrentHashMap<K, T>();
            this.spaceMap.put(space, spaceMap);
        }
        spaceMap.putAll(newMap);
    }
    //
    //
    /**确认K所在的命名空间。*/
    public List<T> get(final K key) {
        List<T> findVal = new ArrayList<T>();
        for (Map<K, T> map : this.spaceMap.values()) {
            T val = map.get(key);
            if (val != null) {
                findVal.add(val);
            }
        }
        return findVal;
    }
    /**确认K所在的命名空间。*/
    public T get(final String space, final K key) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map == null) {
            return null;
        } else {
            return map.get(key);
        }
    }
    //
    //
    /**删除命名空间下的key。*/
    public T remove(String space, K key) {
        Map<K, T> spaceMap = this.spaceMap.get(space);
        if (spaceMap != null) {
            return spaceMap.remove(key);
        }
        return null;
    }
    /**清空所有空间中为指定key的数据。*/
    public void removeAll(final K key) {
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            mapItem.remove(key);
        }
    }
    //
    //
    /**命名空间集合。*/
    public Set<String> spaceSet() {
        return this.spaceMap.keySet();
    }
    /**所有Key集合。*/
    public Set<K> keySet() {
        Set<K> keys = new HashSet<K>();
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            keys.addAll(mapItem.keySet());
        }
        return keys;
    }
    /**命名空间下的key集合。*/
    public Set<K> keySet(String space) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map != null) {
            return map.keySet();
        }
        return new HashSet<K>();
    }
    //
    //
    /**删除某个命名空间的所有数据。*/
    public void deleteSpace(String space) {
        this.spaceMap.remove(space);
    }
    /**删除某个命名空间的所有数据。*/
    public void deleteAllSpace() {
        this.spaceMap.clear();
    }
    //
    //
    public int size() {
        int count = 0;
        for (Map<K, T> map : this.spaceMap.values()) {
            count += map.size();
        }
        return count;
    }
    public int size(String space) {
        Map<K, T> map = this.spaceMap.get(space);
        if (map == null) {
            return 0;
        } else {
            return map.size();
        }
    }
    //
    //
    public DecSpaceMap<K, T> space(final String space) {
        DecSpaceMap<K, T> spaceMap = new DecSpaceMap<K, T>();
        Map<K, T> dataMap = this.spaceMap.get(space);
        if (dataMap != null) {
            spaceMap.putAll(space, dataMap);
        }
        return spaceMap;
    }
    //
    //
    /**所有Key集合。*/
    public Set<T> valueSet() {
        Set<T> values = new HashSet<T>();
        for (Map<K, T> mapItem : this.spaceMap.values()) {
            values.addAll(mapItem.values());
        }
        return values;
    }
    /**命名空间下的key集合。*/
    public Set<T> valueSet(String space) {
        Map<K, T> dataMap = this.spaceMap.get(space);
        if (dataMap != null) {
            return new HashSet<T>(dataMap.values());
        }
        return new HashSet<T>();
    }
    //    @Override
    //    public Iterator<Map.Entry<K, T>> iterator() {
    //        Iterator<Map.Entry<K, T>> seqIter = null;
    //        for (Map<K, T> mapItem : this.spaceMap.values()) {
    //            seqIter = MergeUtils.mergeIterator(seqIter, mapItem.entrySet().iterator());
    //        }
    //        return seqIter;
    //    }
}