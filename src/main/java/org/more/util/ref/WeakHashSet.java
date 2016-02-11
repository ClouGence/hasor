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
package org.more.util.ref;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
/**
 * 参照{@link java.util.HashSet}实现的WeakHashSet.详细介绍参看{@link java.util.Set}和{@link java.util.WeakHashMap}功能
 * @version : 2013-11-8
 * @author (网络收集)
 */
public class WeakHashSet<E> extends AbstractSet<E>implements Set<E> {
    private transient WeakHashMap<E, Object> map;
    private static final Object              PRESENT = new Object();
    public WeakHashSet() {
        this.map = new WeakHashMap<E, Object>();
    }
    public WeakHashSet(final Collection<? extends E> c) {
        this.map = new WeakHashMap<E, Object>(Math.max((int) (c.size() / .75f) + 1, 16));
        this.addAll(c);
    }
    public WeakHashSet(final int initialCapacity, final float loadFactor) {
        this.map = new WeakHashMap<E, Object>(initialCapacity, loadFactor);
    }
    public WeakHashSet(final int initialCapacity) {
        this.map = new WeakHashMap<E, Object>(initialCapacity);
    }
    @Override
    public Iterator<E> iterator() {
        return this.map.keySet().iterator();
    }
    @Override
    public int size() {
        return this.map.size();
    }
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    @Override
    public boolean contains(final Object o) {
        return this.map.containsKey(o);
    }
    @Override
    public boolean add(final E o) {
        return this.map.put(o, WeakHashSet.PRESENT) == null;
    }
    @Override
    public boolean remove(final Object o) {
        return this.map.remove(o) == WeakHashSet.PRESENT;
    }
    @Override
    public void clear() {
        this.map.clear();
    }
}
