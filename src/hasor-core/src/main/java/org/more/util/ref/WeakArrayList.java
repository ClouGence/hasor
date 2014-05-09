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
package org.more.util.ref;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
/**
 * 弱引用列表，WeakArrayList是修改自org.arakhne.util.ref下的WeakArrayList
 * @version : 2013-11-8
 * @author (网络收集)
 */
public class WeakArrayList<T> extends AbstractList<T> {
    private static final Object               NULL_VALUE = new Object();
    private final transient ReferenceQueue<T> queue;
    private Object[]                          data;
    private int                               size;
    private boolean                           enquedElement;
    @SuppressWarnings("unchecked")
    private static <T> T maskNull(T value) {
        return (T) (value == null ? WeakArrayList.NULL_VALUE : value);
    }
    private static <T> T unmaskNull(T value) {
        return (value == WeakArrayList.NULL_VALUE ? null : value);
    }
    public WeakArrayList(int initialCapacity) {
        this.queue = new ReferenceQueue<T>();
        this.enquedElement = false;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
    }
    public WeakArrayList() {
        this(10);
    }
    public WeakArrayList(Collection<? extends T> c) {
        this.queue = new ReferenceQueue<T>();
        this.enquedElement = false;
        this.data = new Object[c.size()];
        this.size = this.data.length;
        int i = 0;
        for (T t : c) {
            this.data[i] = this.createRef(t);
            ++i;
        }
    }
    @SuppressWarnings("unchecked")
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < this.size; ++i) {
            Object obj;
            Reference<T> ref = (Reference<T>) this.data[i];
            if (this.data[i] == null) {
                obj = null;
            } else {
                obj = ref.get();
            }
            buffer.append('{');
            buffer.append(obj == null ? null : obj.toString());
            buffer.append('}');
        }
        return buffer.toString();
    }
    @SuppressWarnings("unchecked")
    private Reference<T> createRef(T obj) {
        return new WeakReference(WeakArrayList.maskNull(obj), this.queue);
    }
    public void ensureCapacity(int minCapacity) {
        this.modCount += 1;
        int oldCapacity = this.data.length;
        if (minCapacity > oldCapacity) {
            Object[] oldData = this.data;
            int newCapacity = oldCapacity * 3 / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            this.data = Arrays.copyOf(oldData, newCapacity);
        }
    }
    public void trimToSize() {
        this.modCount += 1;
        int oldCapacity = this.data.length;
        if (this.size < oldCapacity) {
            this.data = Arrays.copyOf(this.data, this.size);
        }
    }
    @SuppressWarnings("unchecked")
    public int expurge() {
        int j;
        while (this.queue.poll() != null) {
            this.enquedElement = true;
        }
        if (this.enquedElement) {
            j = 0;
            for (int i = 0; i < this.size; ++i) {
                Reference<T> ref = (Reference<T>) this.data[i];
                if (ref == null || ref.isEnqueued() || ref.get() == null) {
                    if (ref != null) {
                        ref.clear();
                    }
                    this.data[i] = null;
                } else {
                    if (i != j) {
                        this.data[j] = this.data[i];
                        this.data[i] = null;
                    }
                    ++j;
                }
            }
            this.enquedElement = false;
        } else {
            j = this.size;
        }
        while (this.queue.poll() != null) {
            this.enquedElement = true;
        }
        this.size = j;
        return this.size;
    }
    protected void assertRange(int index, boolean allowLast) {
        int csize = this.expurge();
        if (index < 0) {
            throw new IndexOutOfBoundsException("invalid negative value: " + Integer.toString(index));
        }
        if (allowLast && index > csize) {
            throw new IndexOutOfBoundsException("index>" + csize + ": " + Integer.toString(index));
        }
        if (!allowLast && index >= csize) {
            throw new IndexOutOfBoundsException("index>=" + csize + ": " + Integer.toString(index));
        }
    }
    public int size() {
        return this.expurge();
    }
    @SuppressWarnings("unchecked")
    public T get(int index) {
        Object value;
        do {
            this.assertRange(index, false);
            value = ((Reference<T>) this.data[index]).get();
        } while (value == null);
        return (T) WeakArrayList.unmaskNull(value);
    }
    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        Object oldValue;
        Reference<T> ref;
        do {
            this.assertRange(index, false);
            ref = (Reference<T>) this.data[index];
            oldValue = ref.get();
        } while (oldValue == null);
        ref.clear();
        this.data[index] = this.createRef(element);
        this.modCount += 1;
        return (T) WeakArrayList.unmaskNull(oldValue);
    }
    public void add(int index, T element) {
        this.assertRange(index, true);
        this.ensureCapacity(this.size + 1);
        System.arraycopy(this.data, index, this.data, index + 1, this.size - index);
        this.data[index] = this.createRef(element);
        this.size += 1;
        this.modCount += 1;
    }
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        Object oldValue;
        Reference<T> ref;
        do {
            this.assertRange(index, false);
            ref = (Reference<T>) this.data[index];
            oldValue = ref.get();
        } while (oldValue == null);
        ref.clear();
        System.arraycopy(this.data, index + 1, this.data, index, this.size - index - 1);
        this.data[(this.size - 1)] = null;
        this.size -= 1;
        this.modCount += 1;
        return (T) WeakArrayList.unmaskNull(oldValue);
    }
    public static void main(String[] args) {
        Object a = new Object();
        WeakArrayList<Object> list = new WeakArrayList<Object>();
        for (int i = 0; i < 100000; i++) {
            list.add(a);
        }
        int size = list.size();
        System.out.println(size);
        a = null;
        while (list.size() == size) {
            System.gc();
        }
        System.out.println(list.size());
    }
}