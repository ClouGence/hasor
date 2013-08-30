/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;

public class CharArrayCache {
	// to avoid using Enumerations, walk the individual tables skipping nulls
	public char[] keyTable[];
	public int valueTable[];
	int elementSize; // number of elements in the table
	int threshold;
/**
 * Constructs a new, empty hashtable. A default capacity is used.
 * Note that the hashtable will automatically grow when it gets full.
 */
public CharArrayCache() {
	this(9);
}
/**
 * Constructs a new, empty hashtable with the specified initial
 * capacity.
 * @param initialCapacity int
 *	the initial number of buckets; must be less than Integer.MAX_VALUE / 2
 */
public CharArrayCache(int initialCapacity) {
	this.elementSize = 0;
	this.threshold = (initialCapacity * 2) / 3; // faster than float operation
	this.keyTable = new char[initialCapacity][];
	this.valueTable = new int[initialCapacity];
}
/**
 * Clears the hash table so that it has no more elements in it.
 */
public void clear() {
	for (int i = this.keyTable.length; --i >= 0;) {
		this.keyTable[i] = null;
		this.valueTable[i] = 0;
	}
	this.elementSize = 0;
}
/** Returns true if the collection contains an element for the key.
 *
 * @param key char[] the key that we are looking for
 * @return boolean
 */
public boolean containsKey(char[] key) {
	int length = this.keyTable.length, index = CharOperation.hashCode(key) % length;
	while (this.keyTable[index] != null) {
		if (CharOperation.equals(this.keyTable[index], key))
			return true;
		if (++index == length) { // faster than modulo
			index = 0;
		}
	}
	return false;
}
/** Gets the object associated with the specified key in the
 * hashtable.
 * @param key <CODE>char[]</CODE> the specified key
 * @return int the element for the key or -1 if the key is not
 *	defined in the hash table.
 */
public int get(char[] key) {
	int length = this.keyTable.length, index = CharOperation.hashCode(key) % length;
	while (this.keyTable[index] != null) {
		if (CharOperation.equals(this.keyTable[index], key))
			return this.valueTable[index];
		if (++index == length) { // faster than modulo
			index = 0;
		}
	}
	return -1;
}
/**
 * Puts the specified element into the hashtable if it wasn't there already,
 * using the specified key.  The element may be retrieved by doing a get() with the same key.
 * The key and the element cannot be null.
 *
 * @param key the given key in the hashtable
 * @param value the given value
 * @return int the old value of the key, or -value if it did not have one.
 */
public int putIfAbsent(char[] key, int value) {
	int length = this.keyTable.length, index = CharOperation.hashCode(key) % length;
	while (this.keyTable[index] != null) {
		if (CharOperation.equals(this.keyTable[index], key))
			return this.valueTable[index];
		if (++index == length) { // faster than modulo
			index = 0;
		}
	}
	this.keyTable[index] = key;
	this.valueTable[index] = value;

	// assumes the threshold is never equal to the size of the table
	if (++this.elementSize > this.threshold)
		rehash();
	return -value; // negative when added (value is assumed to be > 0)
}

/**
 * Puts the specified element into the hashtable, using the specified
 * key.  The element may be retrieved by doing a get() with the same key.
 * The key and the element cannot be null.
 *
 * @param key <CODE>Object</CODE> the specified key in the hashtable
 * @param value <CODE>int</CODE> the specified element
 * @return int the old value of the key, or -1 if it did not have one.
 */
private int put(char[] key, int value) {
	int length = this.keyTable.length, index = CharOperation.hashCode(key) % length;
	while (this.keyTable[index] != null) {
		if (CharOperation.equals(this.keyTable[index], key))
			return this.valueTable[index] = value;
		if (++index == length) { // faster than modulo
			index = 0;
		}
	}
	this.keyTable[index] = key;
	this.valueTable[index] = value;

	// assumes the threshold is never equal to the size of the table
	if (++this.elementSize > this.threshold)
		rehash();
	return value;
}
/**
 * Rehashes the content of the table into a bigger table.
 * This method is called automatically when the hashtable's
 * size exceeds the threshold.
 */
private void rehash() {
	CharArrayCache newHashtable = new CharArrayCache(this.keyTable.length * 2);
	for (int i = this.keyTable.length; --i >= 0;)
		if (this.keyTable[i] != null)
			newHashtable.put(this.keyTable[i], this.valueTable[i]);

	this.keyTable = newHashtable.keyTable;
	this.valueTable = newHashtable.valueTable;
	this.threshold = newHashtable.threshold;
}
/** Remove the object associated with the specified key in the
 * hashtable.
 * @param key <CODE>char[]</CODE> the specified key
 */
public void remove(char[] key) {
	int length = this.keyTable.length, index = CharOperation.hashCode(key) % length;
	while (this.keyTable[index] != null) {
		if (CharOperation.equals(this.keyTable[index], key)) {
			this.valueTable[index] = 0;
			this.keyTable[index] = null;
			return;
		}
		if (++index == length) { // faster than modulo
			index = 0;
		}
	}
}
/**
 * Returns the key corresponding to the value. Returns null if the
 * receiver doesn't contain the value.
 * @param value int the value that we are looking for
 * @return Object
 */
public char[] returnKeyFor(int value) {
	for (int i = this.keyTable.length; i-- > 0;) {
		if (this.valueTable[i] == value) {
			return this.keyTable[i];
		}
	}
	return null;
}
/**
 * Returns the number of elements contained in the hashtable.
 *
 * @return <CODE>int</CODE> The size of the table
 */
public int size() {
	return this.elementSize;
}
/**
 * Converts to a rather lengthy String.
 *
 * return String the ascii representation of the receiver
 */
public String toString() {
	int max = size();
	StringBuffer buf = new StringBuffer();
	buf.append("{"); //$NON-NLS-1$
	for (int i = 0; i < max; ++i) {
		if (this.keyTable[i] != null) {
			buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]); //$NON-NLS-1$
		}
		if (i < max) {
			buf.append(", "); //$NON-NLS-1$
		}
	}
	buf.append("}"); //$NON-NLS-1$
	return buf.toString();
}
}
