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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.more.util.MergeUtil;
/**
 * 合并多个Map的工具（对于被合并的Map只支持读操作）
 * @version : 2012-2-23
 * @author 赵永春 (zyc@byshell.org)
 */
public class DecSequenceMap<T> extends AbstractMap<String, T> {
    private Map<String, T> entryMap = new HashMap<String, T>();
    private SimpleSet<T>   entrySet = null;
    /** 创建一个基本属性对象。 */
    public DecSequenceMap() {}
    /** 创建一个基本属性对象。 */
    public DecSequenceMap(Map<String, T> entryMap) {
        this.entryMap = entryMap;
    }
    public final SimpleSet<T> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new SimpleSet<T>();
            this.entrySet.addMap(entryMap);
        }
        return this.entrySet;
    }
    public void addMap(Map<String, T> newMap) {
        entrySet().addMap(newMap);
    }
    @Override
    public T put(String key, T value) {
        return this.entryMap.put(key, value);
    }
    @Override
    public T remove(Object key) {
        return this.entryMap.remove(key);
    }
    /*----------------------------------------------------------------------*/
    public static class SimpleSet<T> extends AbstractSet<Entry<String, T>> {
        private List<Map<String, T>> mapList = new ArrayList<Map<String, T>>();
        public void addMap(Map<String, T> newMap) {
            mapList.add(newMap);
        }
        @Override
        public Iterator<java.util.Map.Entry<String, T>> iterator() {
            Iterator<java.util.Map.Entry<String, T>> seqIter = null;
            for (Map<String, T> mapItem : this.mapList)
                seqIter = MergeUtil.mergeIterator(seqIter, mapItem.entrySet().iterator());
            return seqIter;
        }
        @Override
        public int size() {
            int count = 0;
            for (Map<String, T> map : mapList)
                count += map.size();
            return count;
        }
    }
}