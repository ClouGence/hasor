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
package org.more.util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
/**
 * 合并两个同类型对象的工具类。
 * @version : 2012-2-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class MergeUtils {
    /**合并两个迭代器*/
    public static <T> Iterator<T> mergeIterator(final Iterator<T> iterator1, final Iterator<T> iterator2) {
        final Iterator<T> i1 = (iterator1 != null) ? iterator1 : new ArrayList<T>(0).iterator();
        final Iterator<T> i2 = (iterator2 != null) ? iterator2 : new ArrayList<T>(0).iterator();
        return new Iterator<T>() {
            private Iterator<T> it = i1;
            public boolean hasNext() {
                return (i1.hasNext() || i2.hasNext()) ? true : false;
            }
            public T next() {
                if (this.it.hasNext() == false)
                    this.it = i2;
                return this.it.next();
            }
            public void remove() {
                this.it.remove();
            }
        };
    };
    /**合并两个{@link List}，判断依据来源于equals方法。*/
    public static <T> List<T> mergeList(List<T> data1, List<T> data2) {
        return mergeList(data1, data2, new Comparator<T>() {
            public int compare(T o1, T o2) {
                return (o1.equals(o2) == true) ? 0 : 1;
            }
        });
    }
    /**合并两个{@link List}，使用{@link Comparator}接口判断是否重复（返回0表示重复）。*/
    public static <T> List<T> mergeList(List<T> data1, List<T> data2, Comparator<T> comparator) {
        //1.准备数据
        List<T> d1 = (data1 != null) ? data1 : new ArrayList<T>(0);
        List<T> d2 = (data2 != null) ? data2 : new ArrayList<T>(0);
        //2.执行Array合并&去重
        ArrayList<T> array = new ArrayList<T>(d1);
        for (T itemTarget : d2) {
            boolean has = false;
            for (T itemHas : array)
                if (comparator.compare(itemTarget, itemHas) == 0) {
                    has = true;
                    break;
                }
            if (has == false)
                array.add(itemTarget);
        }
        return array;
    };
    /**合并两个{@link Map}，合并不同key的map相同key的只会保留一个。*/
    public static <K, V> Map<K, V> mergeMap(Map<K, V> dataMap1, Map<K, V> dataMap2) {
        return mergeMap(dataMap1, dataMap2, null);
    };
    /**合并两个{@link Map}，使用{@link Comparator}接口判断相同的key保留那个（接口返回值大于0使用o2、小于0使用o1、0抛弃冲突属性）。*/
    public static <K, V> Map<K, V> mergeMap(Map<K, V> dataMap1, Map<K, V> dataMap2, Comparator<Map.Entry<K, V>> comparator) {
        //1.准备数据
        Map<K, V> m1 = (dataMap1 != null) ? dataMap1 : new HashMap<K, V>();
        Map<K, V> m2 = (dataMap2 != null) ? dataMap2 : new HashMap<K, V>();
        //2.执行Map合并&去重
        HashMap<K, V> hashMap = new HashMap<K, V>(m1);
        for (Map.Entry<K, V> e_m2 : m2.entrySet()) {
            V target = e_m2.getValue();
            boolean remove = false;
            for (Map.Entry<K, V> e_m1 : hashMap.entrySet())
                if (e_m1.getKey().equals(e_m2.getKey()) == true) {
                    int res = comparator.compare(e_m1, e_m2);
                    if (res == 0)
                        remove = true;//等于0
                    else if (res < 0)
                        target = e_m1.getValue();//小于0
                    else
                        target = e_m2.getValue(); //大于0
                }
            if (remove == true)
                hashMap.remove(e_m2.getKey());
            else
                hashMap.put(e_m2.getKey(), target);
        }
        return hashMap;
    }
}