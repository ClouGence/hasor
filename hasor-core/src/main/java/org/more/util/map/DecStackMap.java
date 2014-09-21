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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.more.util.MergeUtils;
/**
 * 提供一种栈结构的操作Map序列属性对象，利用该属性装饰器可以在属性集上增加另一个属性栈。
 * @version 2010-9-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class DecStackMap<K, T> extends DecSequenceMap<K, T> {
    /** 创建一个基本属性对象。 */
    public DecStackMap() {
        /*创建一个默认的成员*/
        super(true);
    };
    /** 创建一个基本属性对象，参数是第一个栈对象。 */
    public DecStackMap(final Map<K, T> entryMap) {
        super(entryMap, true);
    };
    @Override
    public T put(final K key, final T value) {
        return this.elementMapList().get(0).put(key, value);
    }
    @Override
    public T remove(final Object key) {
        return this.elementMapList().get(0).remove(key);
    }
    @Override
    protected StackSimpleSet<K, T> createSet() {
        return new StackSimpleSet<K, T>();
    };
    /**获取当前堆的深度，该值会随着调用createStack方法而增加，随着dropStack方法而减少。*/
    public final int getDepth() {
        return this.entrySet().mapList.size() - 1;
    };
    /**在现有属性栈上创建一个新的栈，操作也会切换到这个新栈上。*/
    public synchronized void createStack() {
        StackSimpleSet<K, T> stackList = (StackSimpleSet<K, T>) this.entrySet();
        stackList.mapList.addFirst(new HashMap<K, T>());
    };
    /**销毁当前层次的属性栈，如果在栈顶执行该操作将会引发{@link IndexOutOfBoundsException}类型异常。*/
    public synchronized void dropStack() {
        StackSimpleSet<K, T> stackList = (StackSimpleSet<K, T>) this.entrySet();
        if (stackList.mapList.size() == 0) {
            throw new IndexOutOfBoundsException();
        }
        stackList.removeFirst();
    };
    /*----------------------------------------------------------------------*/
    public static class StackSimpleSet<K, T> extends SimpleSet<K, T> {
        private LinkedList<Map<K, T>> mapList = null;
        public StackSimpleSet() {
            this.mapList = new LinkedList<Map<K, T>>();
            super.mapList = this.mapList;
        }
        public void removeFirst() {
            this.mapList.removeFirst();
        }
        @Override
        public Iterator<java.util.Map.Entry<K, T>> iterator() {
            Iterator<java.util.Map.Entry<K, T>> seqIter = null;
            for (Map<K, T> mapItem : this.mapList) {
                seqIter = MergeUtils.mergeIterator(seqIter, mapItem.entrySet().iterator());
            }
            return seqIter;
        }
        @Override
        public int size() {
            int count = 0;
            for (Map<K, T> map : this.mapList) {
                count += map.size();
            }
            return count;
        }
    };
    /** 获取指定深度的父堆（如果可能）。0代表当前层，数字越大获取的深度越深。 */
    public Map<K, T> getParentStack(final int depth) {
        if (depth < 0 || depth > this.getDepth()) {
            throw new IndexOutOfBoundsException();
        }
        return this.entrySet().mapList.get(depth);
    };
    /** 获取当前堆的父堆（如果可能）。 */
    public Map<K, T> getParentStack() {
        return this.getParentStack(this.getDepth() - 1);
    };
}