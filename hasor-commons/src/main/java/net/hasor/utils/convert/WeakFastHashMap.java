/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.utils.convert;
import java.util.*;
/**
 * <p>A customized implementation of <code>java.util.HashMap</code> designed
 * to operate in a multithreaded environment where the large majority of
 * method calls are read-only, instead of structural changes.  When operating
 * in "fast" mode, read calls are non-synchronized and write calls perform the
 * following steps:</p>
 * <ul>
 * <li>Clone the existing collection
 * <li>Perform the modification on the clone
 * <li>Replace the existing collection with the (modified) clone
 * </ul>
 * <p>When first created, objects of this class default to "slow" mode, where
 * all accesses of any type are synchronized but no cloning takes place.  This
 * is appropriate for initially populating the collection, followed by a switch
 * to "fast" mode (by calling <code>setFast(true)</code>) after initialization
 * is complete.</p>
 *
 * <p><strong>NOTE</strong>: If you are creating and accessing a
 * <code>HashMap</code> only within a single thread, you should use
 * <code>java.util.HashMap</code> directly (with no synchronization), for
 * maximum performance.</p>
 *
 * <p><strong>NOTE</strong>: <i>This class is not cross-platform.  
 * Using it may cause unexpected failures on some architectures.</i>
 * It suffers from the same problems as the double-checked locking idiom.  
 * In particular, the instruction that clones the internal collection and the 
 * instruction that sets the internal reference to the clone can be executed 
 * or perceived out-of-order.  This means that any read operation might fail 
 * unexpectedly, as it may be reading the state of the internal collection
 * before the internal collection is fully formed.
 * For more information on the double-checked locking idiom, see the
 * <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">
 * Double-Checked Locking Idiom Is Broken Declaration</a>.</p>
 *
 * @since Commons Collections 1.0
 * @version $Revision: 687089 $ $Date: 2008-08-19 17:33:30 +0100 (Tue, 19 Aug 2008) $
 *
 * @author Craig R. McClanahan
 * @author Stephen Colebourne
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
class WeakFastHashMap extends HashMap {
    /** The underlying map we are managing. */
    private Map     map  = null;
    /** Are we currently operating in "fast" mode? */
    private boolean fast = false;
    // Constructors
    // ----------------------------------------------------------------------
    /** Construct an empty map. */
    public WeakFastHashMap() {
        super();
        this.map = this.createMap();
    }
    /**
     * Construct an empty map with the specified capacity.
     * @param capacity  the initial capacity of the empty map
     */
    public WeakFastHashMap(final int capacity) {
        super();
        this.map = this.createMap(capacity);
    }
    /**
     * Construct an empty map with the specified capacity and load factor.
     * @param capacity  the initial capacity of the empty map
     * @param factor  the load factor of the new map
     */
    public WeakFastHashMap(final int capacity, final float factor) {
        super();
        this.map = this.createMap(capacity, factor);
    }
    /**
     * Construct a new map with the same mappings as the specified map.
     * @param map  the map whose mappings are to be copied
     */
    public WeakFastHashMap(final Map map) {
        super();
        this.map = this.createMap(map);
    }
    // Property access
    // ----------------------------------------------------------------------
    /**
     *  Returns true if this map is operating in fast mode.
     *  @return true if this map is operating in fast mode
     */
    public boolean getFast() {
        return this.fast;
    }
    /**
     *  Sets whether this map is operating in fast mode.
     *  @param fast true if this map should operate in fast mode
     */
    public void setFast(final boolean fast) {
        this.fast = fast;
    }
    // Map access
    // ----------------------------------------------------------------------
    // These methods can forward straight to the wrapped Map in 'fast' mode.
    // (because they are query methods)
    /**
     * Return the value to which this map maps the specified key.  Returns
     * <code>null</code> if the map contains no mapping for this key, or if
     * there is a mapping with a value of <code>null</code>.  Use the
     * <code>containsKey()</code> method to disambiguate these cases.
     *
     * @param key  the key whose value is to be returned
     * @return the value mapped to that key, or null
     */
    @Override
    public Object get(final Object key) {
        if (this.fast) {
            return this.map.get(key);
        } else {
            synchronized (this.map) {
                return this.map.get(key);
            }
        }
    }
    /**
     * Return the number of key-value mappings in this map.
     *
     * @return the current size of the map
     */
    @Override
    public int size() {
        if (this.fast) {
            return this.map.size();
        } else {
            synchronized (this.map) {
                return this.map.size();
            }
        }
    }
    /**
     * Return <code>true</code> if this map contains no mappings.
     *
     * @return is the map currently empty
     */
    @Override
    public boolean isEmpty() {
        if (this.fast) {
            return this.map.isEmpty();
        } else {
            synchronized (this.map) {
                return this.map.isEmpty();
            }
        }
    }
    /**
     * Return <code>true</code> if this map contains a mapping for the
     * specified key.
     *
     * @param key  the key to be searched for
     * @return true if the map contains the key
     */
    @Override
    public boolean containsKey(final Object key) {
        if (this.fast) {
            return this.map.containsKey(key);
        } else {
            synchronized (this.map) {
                return this.map.containsKey(key);
            }
        }
    }
    /**
     * Return <code>true</code> if this map contains one or more keys mapping
     * to the specified value.
     *
     * @param value  the value to be searched for
     * @return true if the map contains the value
     */
    @Override
    public boolean containsValue(final Object value) {
        if (this.fast) {
            return this.map.containsValue(value);
        } else {
            synchronized (this.map) {
                return this.map.containsValue(value);
            }
        }
    }
    // Map modification
    // ----------------------------------------------------------------------
    // These methods perform special behaviour in 'fast' mode.
    // The map is cloned, updated and then assigned back.
    // See the comments at the top as to why this won't always work.
    /**
     * Associate the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced and returned.
     *
     * @param key  the key with which the value is to be associated
     * @param value  the value to be associated with this key
     * @return the value previously mapped to the key, or null
     */
    @Override
    public Object put(final Object key, final Object value) {
        if (this.fast) {
            synchronized (this) {
                Map temp = this.cloneMap(this.map);
                Object result = temp.put(key, value);
                this.map = temp;
                return result;
            }
        } else {
            synchronized (this.map) {
                return this.map.put(key, value);
            }
        }
    }
    /**
     * Copy all of the mappings from the specified map to this one, replacing
     * any mappings with the same keys.
     *
     * @param in  the map whose mappings are to be copied
     */
    @Override
    public void putAll(final Map in) {
        if (this.fast) {
            synchronized (this) {
                Map temp = this.cloneMap(this.map);
                temp.putAll(in);
                this.map = temp;
            }
        } else {
            synchronized (this.map) {
                this.map.putAll(in);
            }
        }
    }
    /**
     * Remove any mapping for this key, and return any previously
     * mapped value.
     *
     * @param key  the key whose mapping is to be removed
     * @return the value removed, or null
     */
    @Override
    public Object remove(final Object key) {
        if (this.fast) {
            synchronized (this) {
                Map temp = this.cloneMap(this.map);
                Object result = temp.remove(key);
                this.map = temp;
                return result;
            }
        } else {
            synchronized (this.map) {
                return this.map.remove(key);
            }
        }
    }
    /**
     * Remove all mappings from this map.
     */
    @Override
    public void clear() {
        if (this.fast) {
            synchronized (this) {
                this.map = this.createMap();
            }
        } else {
            synchronized (this.map) {
                this.map.clear();
            }
        }
    }
    // Basic object methods
    // ----------------------------------------------------------------------
    /**
     * Compare the specified object with this list for equality.  This
     * implementation uses exactly the code that is used to define the
     * list equals function in the documentation for the
     * <code>Map.equals</code> method.
     *
     * @param o  the object to be compared to this list
     * @return true if the two maps are equal
     */
    @Override
    public boolean equals(final Object o) {
        // Simple tests that require no synchronization
        if (o == this) {
            return true;
        } else if (!(o instanceof Map)) {
            return false;
        }
        Map mo = (Map) o;
        // Compare the two maps for equality
        if (this.fast) {
            if (mo.size() != this.map.size()) {
                return false;
            }
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry e = (Map.Entry) i.next();
                Object key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (!(mo.get(key) == null && mo.containsKey(key))) {
                        return false;
                    }
                } else {
                    if (!value.equals(mo.get(key))) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            synchronized (this.map) {
                if (mo.size() != this.map.size()) {
                    return false;
                }
                Iterator i = this.map.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    Object key = e.getKey();
                    Object value = e.getValue();
                    if (value == null) {
                        if (!(mo.get(key) == null && mo.containsKey(key))) {
                            return false;
                        }
                    } else {
                        if (!value.equals(mo.get(key))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }
    /**
     * Return the hash code value for this map.  This implementation uses
     * exactly the code that is used to define the list hash function in the
     * documentation for the <code>Map.hashCode</code> method.
     *
     * @return suitable integer hash code
     */
    @Override
    public int hashCode() {
        if (this.fast) {
            int h = 0;
            Iterator i = this.map.entrySet().iterator();
            while (i.hasNext()) {
                h += i.next().hashCode();
            }
            return h;
        } else {
            synchronized (this.map) {
                int h = 0;
                Iterator i = this.map.entrySet().iterator();
                while (i.hasNext()) {
                    h += i.next().hashCode();
                }
                return h;
            }
        }
    }
    /**
     * Return a shallow copy of this <code>FastHashMap</code> instance.
     * The keys and values themselves are not copied.
     *
     * @return a clone of this map
     */
    @Override
    public Object clone() {
        WeakFastHashMap results = null;
        if (this.fast) {
            results = new WeakFastHashMap(this.map);
        } else {
            synchronized (this.map) {
                results = new WeakFastHashMap(this.map);
            }
        }
        results.setFast(this.getFast());
        return results;
    }
    // Map views
    // ----------------------------------------------------------------------
    /**
     * Return a collection view of the mappings contained in this map.  Each
     * element in the returned collection is a <code>Map.Entry</code>.
     * @return the set of map Map entries
     */
    @Override
    public Set entrySet() {
        return new EntrySet();
    }
    /**
     * Return a set view of the keys contained in this map.
     * @return the set of the Map's keys
     */
    @Override
    public Set keySet() {
        return new KeySet();
    }
    /**
     * Return a collection view of the values contained in this map.
     * @return the set of the Map's values
     */
    @Override
    public Collection values() {
        return new Values();
    }
    // Abstractions on Map creations (for subclasses such as WeakFastHashMap)
    // ----------------------------------------------------------------------
    protected Map createMap() {
        return new WeakHashMap();
    }
    protected Map createMap(final int capacity) {
        return new WeakHashMap(capacity);
    }
    protected Map createMap(final int capacity, final float factor) {
        return new WeakHashMap(capacity, factor);
    }
    protected Map createMap(final Map map) {
        return new WeakHashMap(map);
    }
    protected Map cloneMap(final Map map) {
        return this.createMap(map);
    }
    // Map view inner classes
    // ----------------------------------------------------------------------
    /**
     * Abstract collection implementation shared by keySet(), values() and entrySet().
     */
    private abstract class CollectionView implements Collection {
        public CollectionView() {
        }
        protected abstract Collection get(Map map);

        protected abstract Object iteratorNext(Map.Entry entry);
        @Override
        public void clear() {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    WeakFastHashMap.this.map = WeakFastHashMap.this.createMap();
                }
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    this.get(WeakFastHashMap.this.map).clear();
                }
            }
        }
        @Override
        public boolean remove(final Object o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).remove(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).remove(o);
                }
            }
        }
        @Override
        public boolean removeAll(final Collection o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).removeAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).removeAll(o);
                }
            }
        }
        @Override
        public boolean retainAll(final Collection o) {
            if (WeakFastHashMap.this.fast) {
                synchronized (WeakFastHashMap.this) {
                    Map temp = WeakFastHashMap.this.cloneMap(WeakFastHashMap.this.map);
                    boolean r = this.get(temp).retainAll(o);
                    WeakFastHashMap.this.map = temp;
                    return r;
                }
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).retainAll(o);
                }
            }
        }
        @Override
        public int size() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).size();
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).size();
                }
            }
        }
        @Override
        public boolean isEmpty() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).isEmpty();
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).isEmpty();
                }
            }
        }
        @Override
        public boolean contains(final Object o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).contains(o);
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).contains(o);
                }
            }
        }
        @Override
        public boolean containsAll(final Collection o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).containsAll(o);
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).containsAll(o);
                }
            }
        }
        @Override
        public Object[] toArray(final Object[] o) {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray(o);
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).toArray(o);
                }
            }
        }
        @Override
        public Object[] toArray() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).toArray();
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).toArray();
                }
            }
        }
        @Override
        public boolean equals(final Object o) {
            if (o.equals(this)) {
                return true;
            }
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).equals(o);
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).equals(o);
                }
            }
        }
        @Override
        public int hashCode() {
            if (WeakFastHashMap.this.fast) {
                return this.get(WeakFastHashMap.this.map).hashCode();
            } else {
                synchronized (WeakFastHashMap.this.map) {
                    return this.get(WeakFastHashMap.this.map).hashCode();
                }
            }
        }
        @Override
        public boolean add(final Object o) {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean addAll(final Collection c) {
            throw new UnsupportedOperationException();
        }
        @Override
        public Iterator iterator() {
            return new CollectionViewIterator();
        }
        private class CollectionViewIterator implements Iterator {
            private Map expected;
            private Map.Entry lastReturned = null;
            private Iterator iterator;
            public CollectionViewIterator() {
                this.expected = WeakFastHashMap.this.map;
                this.iterator = this.expected.entrySet().iterator();
            }
            @Override
            public boolean hasNext() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                return this.iterator.hasNext();
            }
            @Override
            public Object next() {
                if (this.expected != WeakFastHashMap.this.map) {
                    throw new ConcurrentModificationException();
                }
                this.lastReturned = (Map.Entry) this.iterator.next();
                return CollectionView.this.iteratorNext(this.lastReturned);
            }
            @Override
            public void remove() {
                if (this.lastReturned == null) {
                    throw new IllegalStateException();
                }
                if (WeakFastHashMap.this.fast) {
                    synchronized (WeakFastHashMap.this) {
                        if (this.expected != WeakFastHashMap.this.map) {
                            throw new ConcurrentModificationException();
                        }
                        WeakFastHashMap.this.remove(this.lastReturned.getKey());
                        this.lastReturned = null;
                        this.expected = WeakFastHashMap.this.map;
                    }
                } else {
                    this.iterator.remove();
                    this.lastReturned = null;
                }
            }
        }
    }
    /**
     * Set implementation over the keys of the FastHashMap
     */
    private class KeySet extends CollectionView implements Set {
        @Override
        protected Collection get(final Map map) {
            return map.keySet();
        }
        @Override
        protected Object iteratorNext(final Map.Entry entry) {
            return entry.getKey();
        }
    }
    /**
     * Collection implementation over the values of the FastHashMap
     */
    private class Values extends CollectionView {
        @Override
        protected Collection get(final Map map) {
            return map.values();
        }
        @Override
        protected Object iteratorNext(final Map.Entry entry) {
            return entry.getValue();
        }
    }
    /**
     * Set implementation over the entries of the FastHashMap
     */
    private class EntrySet extends CollectionView implements Set {
        @Override
        protected Collection get(final Map map) {
            return map.entrySet();
        }
        @Override
        protected Object iteratorNext(final Map.Entry entry) {
            return entry;
        }
    }
}