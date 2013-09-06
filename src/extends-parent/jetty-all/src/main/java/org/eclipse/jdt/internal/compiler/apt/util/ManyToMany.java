/*******************************************************************************
 * Copyright (c) 2006, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation 
 *                      (originally in org.eclipse.jdt.apt.core)
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manage a Map<T1, Set<T2>>, with reverse links so that it is possible to
 * efficiently find all T1s that have a particular T2 associated with them.
 * Access to the map is synchronized, so that it is possible to read and
 * write simultaneously from multiple threads.
 * <p>
 * The map permits the null value for keys nor for value elements. 
 * <p>
 * Design invariants preserved by all operations on this map are as follows:
 * <ul>
 * <li> If a key exists, it has at least one value associated with it; that is,
 * for all k such that null != containsKey(k), getValues(k) returns a non-empty
 * set.</li>
 * <li> If a value exists, it has at least one key associated with it; that is,
 * for all v such that null != containsValue(v), getKeys(v) returns a non-empty
 * set.</li>
 */
public class ManyToMany<T1, T2> {
	
	private final Map<T1, Set<T2>> _forward = new HashMap<T1, Set<T2>>();
	private final Map<T2, Set<T1>> _reverse = new HashMap<T2, Set<T1>>();
	private boolean _dirty = false;
	
	/**
	 * Empty all maps.  If the maps previously contained entries, 
	 * this will set the dirty bit.
	 * @return true if the maps contained any entries prior to being cleared
	 */
	public synchronized boolean clear() {
		boolean hadContent = !_forward.isEmpty() || !_reverse.isEmpty();
		_reverse.clear();
		_forward.clear();
		_dirty |= hadContent;
		return hadContent;
	}
	
	/**
	 * Sets the dirty bit to false.  Internal operations do not use the dirty 
	 * bit; clearing it will not affect behavior of the map.  It's just there
	 * for the convenience of callers who don't want to keep track of every
	 * put() and remove().
	 */
	public synchronized void clearDirtyBit() {
		_dirty = false;
	}
	
	/**
	 * Equivalent to keySet().contains(key).
	 * @return true if the map contains the specified key.
	 */
	public synchronized boolean containsKey(T1 key) {
		return _forward.containsKey(key);
	}
	
	/**
	 * Is there a key that is mapped to the specified value?
	 * Search within the forward map.
	 * @return true if such a key exists
	 */
	public synchronized boolean containsKeyValuePair(T1 key, T2 value) {
		Set<T2> values = _forward.get(key);
		if (null == values) {
			return false;
		}
		return values.contains(value);
	}
	
	/**
	 * Equivalent to values().contains(value).
	 * @return true if the map contains the specified value (regardless
	 * of what key it might be associated with).
	 */
	public synchronized boolean containsValue(T2 value) {
		return _reverse.containsKey(value);
	}
	
	/**
	 * Search the reverse map for all keys that have been associated with
	 * a particular value.
	 * @return the set of keys that are associated with the specified value,
	 * or an empty set if the value does not exist in the map.
	 */
	public synchronized Set<T1> getKeys(T2 value) {
		Set<T1> keys = _reverse.get(value);
		if (null == keys) {
			return Collections.emptySet();
		}
		return new HashSet<T1>(keys);
	}
	
	/**
	 * Search the forward map for all values associated with a particular key.
	 * Returns a copy of the set of values.
	 * @return a copy of the set of values that are associated with the 
	 * specified key, or an empty set if the key does not exist in the map.
	 */
	public synchronized Set<T2> getValues(T1 key) {
		Set<T2> values = _forward.get(key);
		if (null == values) {
			return Collections.emptySet();
		}
		return new HashSet<T2>(values);
	}

	/**
	 * @return a copy of the set of all keys (that is, all items of type T1).
	 * If the maps are empty, the returned set will be empty, not null.  The
	 * returned set can be modified by the caller without affecting the map.
	 * @see #getValueSet()
	 */
	public synchronized Set<T1> getKeySet() {
		Set<T1> keys = new HashSet<T1>(_forward.keySet());
		return keys;
	}
	
	/**
	 * @return a copy of the set of all values (that is, all items of type T2).
	 * If the maps are empty, the returned set will be empty, not null.  The
	 * returned set can be modified by the caller without affecting the map.
	 * @see #getKeySet()
	 */
	public synchronized Set<T2> getValueSet() {
		Set<T2> values = new HashSet<T2>(_reverse.keySet());
		return values;
	}
	
	/**
	 * Return the state of the dirty bit.  All operations that change the state
	 * of the maps, including @see #clear(), set the dirty bit if any content actually
	 * changed.  The only way to clear the dirty bit is to call @see #clearDirtyBit().
	 * @return true if the map content has changed since it was created or since
	 * the last call to clearDirtyBit().
	 * @see #clearDirtyBit()
	 */
	public synchronized boolean isDirty() {
		return _dirty;
	}
	
	/**
	 * Check whether <code>key</code> has an association to any values other
	 * than <code>value</code> - that is, whether the same key has been added
	 * with multiple values.  Equivalent to asking whether the intersection of
	 * <code>getValues(key)</code> and the set containing <code>value</code> is 
	 * non-empty. 
	 * @return true iff <code>key</code> is in the map and is associated 
	 * with values other than <code>value</code>. 
	 * @see #valueHasOtherKeys(Object, Object)
	 */
	public synchronized boolean keyHasOtherValues(T1 key, T2 value) {
		Set<T2> values = _forward.get(key);
		if (values == null)
			return false;
		int size = values.size();
		if (size == 0)
			return false;
		else if (size > 1)
			return true;
		else // size == 1
			return !values.contains(value);
	}

	/**
	 * Associate the specified value with the key.  Adds the entry
	 * to both the forward and reverse maps.  Adding the same value
	 * twice to a particular key has no effect.  Because this is a
	 * many-to-many map, adding a new value for an existing key does
	 * not change the existing association, it adds a new one.
	 * @param key can be null
	 * @param value can be null
	 * @return true if the key/value pair did not exist prior to being added
	 */
	public synchronized boolean put(T1 key, T2 value) {
		// Add to forward map
		Set<T2> values = _forward.get(key);
		if (null == values) {
			values = new HashSet<T2>();
			_forward.put(key, values);
		}
		boolean added = values.add(value);
		_dirty |= added;
		
		// Add to reverse map
		Set<T1> keys = _reverse.get(value);
		if (null == keys) {
			keys = new HashSet<T1>();
			_reverse.put(value, keys);
		}
		keys.add(key);
		
		assert checkIntegrity();
		return added;
	}
	
	/**
	 * Remove a particular key-value association.  This is the inverse
	 * of put(key, value).  If the key does not exist, or the value
	 * does not exist, or the association does not exist, this call
	 * has no effect.
	 * @return true if the key/value pair existed in the map prior to removal
	 */
	public synchronized boolean remove(T1 key, T2 value) {
		Set<T2> values = _forward.get(key);
		if (values == null) {
			assert checkIntegrity();
			return false;
		}
		boolean removed = values.remove(value);
		if (values.isEmpty()) {
			_forward.remove(key);
		}
		if (removed) {
			_dirty = true;
			// it existed, so we need to remove from reverse map as well
			Set<T1> keys = _reverse.get(value);
			keys.remove(key);
			if (keys.isEmpty()) {
				_reverse.remove(value);
			}
		}
		assert checkIntegrity();
		return removed;
	}

	/**
	 * Remove the key and its associated key/value entries.
	 * Calling removeKey(k) is equivalent to calling remove(k,v) 
	 * for every v in getValues(k).
	 * @return true if the key existed in the map prior to removal
	 */
	public synchronized boolean removeKey(T1 key) {
		// Remove all back-references to key.
		Set<T2> values = _forward.get(key);
		if (null == values) {
			// key does not exist in map.
			assert checkIntegrity();
			return false;
		}
		for (T2 value : values) {
			Set<T1> keys = _reverse.get(value);
			if (null != keys) {
				keys.remove(key);
				if (keys.isEmpty()) {
					_reverse.remove(value);
				}
			}
		}
		// Now remove the forward references from key.
		_forward.remove(key);
		_dirty = true;
		assert checkIntegrity();
		return true;
	}
	
	/**
	 * Remove the value and its associated key/value entries.
	 * Calling removeValue(v) is equivalent to calling remove(k,v)
	 * for every k in getKeys(v).
	 * @return true if the value existed in the map prior to removal.
	 */
	public synchronized boolean removeValue(T2 value) {
		// Remove any forward references to value
		Set<T1> keys = _reverse.get(value);
		if (null == keys) {
			// value does not exist in map.
			assert checkIntegrity();
			return false;
		}
		for (T1 key : keys) {
			Set<T2> values = _forward.get(key);
			if (null != values) {
				values.remove(value);
				if (values.isEmpty()) {
					_forward.remove(key);
				}
			}
		}
		// Now remove the reverse references from value.
		_reverse.remove(value);
		_dirty = true;
		assert checkIntegrity();
		return true;
	}
	
	/**
	 * Check whether <code>value</code> has an association from any keys other
	 * than <code>key</code> - that is, whether the same value has been added
	 * with multiple keys.  Equivalent to asking whether the intersection of
	 * <code>getKeys(value)</code> and the set containing <code>key</code> is 
	 * non-empty. 
	 * @return true iff <code>value</code> is in the map and is associated 
	 * with keys other than <code>key</code>. 
	 * @see #keyHasOtherValues(Object, Object)
	 */
	public synchronized boolean valueHasOtherKeys(T2 value, T1 key) {
		Set<T1> keys = _reverse.get(key);
		if (keys == null)
			return false;
		int size = keys.size();
		if (size == 0)
			return false;
		else if (size > 1)
			return true;
		else // size == 1
			return !keys.contains(key);
	}

	/**
	 * Check the integrity of the internal data structures.  This is intended to
	 * be called within an assert, so that if asserts are disabled the integrity
	 * checks will not cause a performance impact.
	 * @return true if everything is okay.
	 * @throws IllegalStateException if there is a problem.
	 */
	private boolean checkIntegrity() {
		// For every T1->T2 mapping in the forward map, there should be a corresponding
		// T2->T1 mapping in the reverse map.
		for (Map.Entry<T1, Set<T2>> entry : _forward.entrySet()) {
			Set<T2> values = entry.getValue();
			if (values.isEmpty()) {
				throw new IllegalStateException("Integrity compromised: forward map contains an empty set"); //$NON-NLS-1$
			}
			for (T2 value : values) {
				Set<T1> keys = _reverse.get(value);
				if (null == keys || !keys.contains(entry.getKey())) {
					throw new IllegalStateException("Integrity compromised: forward map contains an entry missing from reverse map: " + value); //$NON-NLS-1$
				}
			}
		}
		// And likewise in the other direction.
		for (Map.Entry<T2, Set<T1>> entry : _reverse.entrySet()) {
			Set<T1> keys = entry.getValue();
			if (keys.isEmpty()) {
				throw new IllegalStateException("Integrity compromised: reverse map contains an empty set"); //$NON-NLS-1$
			}
			for (T1 key : keys) {
				Set<T2> values = _forward.get(key);
				if (null == values || !values.contains(entry.getKey())) {
					throw new IllegalStateException("Integrity compromised: reverse map contains an entry missing from forward map: " + key); //$NON-NLS-1$
				}
			}
		}
		return true;
	}

}
