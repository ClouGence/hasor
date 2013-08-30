/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.util;

/**
 * A simple lookup table is a non-synchronized Hashtable, whose keys
 * and values are Objects. It also uses linear probing to resolve collisions
 * rather than a linked list of hash table entries.
 */
public final class SimpleLookupTable implements Cloneable {

// to avoid using Enumerations, walk the individual tables skipping nulls
public Object[] keyTable;
public Object[] valueTable;
public int elementSize; // number of elements in the table
public int threshold;

public SimpleLookupTable() {
	this(13);
}

public SimpleLookupTable(int size) {
	this.elementSize = 0;
	this.threshold = size; // size represents the expected number of elements
	int extraRoom = (int) (size * 1.5f);
	if (this.threshold == extraRoom)
		extraRoom++;
	this.keyTable = new Object[extraRoom];
	this.valueTable = new Object[extraRoom];
}

public Object clone() throws CloneNotSupportedException {
	SimpleLookupTable result = (SimpleLookupTable) super.clone();
	result.elementSize = this.elementSize;
	result.threshold = this.threshold;

	int length = this.keyTable.length;
	result.keyTable = new Object[length];
	System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);

	length = this.valueTable.length;
	result.valueTable = new Object[length];
	System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
	return result;
}

public boolean containsKey(Object key) {
	int length = this.keyTable.length;
	int index = (key.hashCode() & 0x7FFFFFFF) % length;
	Object currentKey;
	while ((currentKey = this.keyTable[index]) != null) {
		if (currentKey.equals(key)) return true;
		if (++index == length) index = 0;
	}
	return false;
}

public Object get(Object key) {
	int length = this.keyTable.length;
	int index = (key.hashCode() & 0x7FFFFFFF) % length;
	Object currentKey;
	while ((currentKey = this.keyTable[index]) != null) {
		if (currentKey.equals(key)) return this.valueTable[index];
		if (++index == length) index = 0;
	}
	return null;
}

public Object getKey(Object key) {
	int length = this.keyTable.length;
	int index = (key.hashCode() & 0x7FFFFFFF) % length;
	Object currentKey;
	while ((currentKey = this.keyTable[index]) != null) {
		if (currentKey.equals(key)) return currentKey;
		if (++index == length) index = 0;
	}
	return key;
}

public Object keyForValue(Object valueToMatch) {
	if (valueToMatch != null)
		for (int i = 0, l = this.keyTable.length; i < l; i++)
			if (this.keyTable[i] != null && valueToMatch.equals(this.valueTable[i]))
				return this.keyTable[i];
	return null;
}

public Object put(Object key, Object value) {
	int length = this.keyTable.length;
	int index = (key.hashCode() & 0x7FFFFFFF) % length;
	Object currentKey;
	while ((currentKey = this.keyTable[index]) != null) {
		if (currentKey.equals(key)) return this.valueTable[index] = value;
		if (++index == length) index = 0;
	}
	this.keyTable[index] = key;
	this.valueTable[index] = value;

	// assumes the threshold is never equal to the size of the table
	if (++this.elementSize > this.threshold) rehash();
	return value;
}

public Object removeKey(Object key) {
	int length = this.keyTable.length;
	int index = (key.hashCode() & 0x7FFFFFFF) % length;
	Object currentKey;
	while ((currentKey = this.keyTable[index]) != null) {
		if (currentKey.equals(key)) {
			this.elementSize--;
			Object oldValue = this.valueTable[index];
			this.keyTable[index] = null;
			this.valueTable[index] = null;
			if (this.keyTable[index + 1 == length ? 0 : index + 1] != null)
				rehash(); // only needed if a possible collision existed
			return oldValue;
		}
		if (++index == length) index = 0;
	}
	return null;
}

public void removeValue(Object valueToRemove) {
	boolean rehash = false;
	for (int i = 0, l = this.valueTable.length; i < l; i++) {
		Object value = this.valueTable[i];
		if (value != null && value.equals(valueToRemove)) {
			this.elementSize--;
			this.keyTable[i] = null;
			this.valueTable[i] = null;
			if (!rehash && this.keyTable[i + 1 == l ? 0 : i + 1] != null)
				rehash = true; // only needed if a possible collision existed
		}
	}
	if (rehash) rehash();
}

private void rehash() {
	SimpleLookupTable newLookupTable = new SimpleLookupTable(this.elementSize * 2); // double the number of expected elements
	Object currentKey;
	for (int i = this.keyTable.length; --i >= 0;)
		if ((currentKey = this.keyTable[i]) != null)
			newLookupTable.put(currentKey, this.valueTable[i]);

	this.keyTable = newLookupTable.keyTable;
	this.valueTable = newLookupTable.valueTable;
	this.elementSize = newLookupTable.elementSize;
	this.threshold = newLookupTable.threshold;
}

public String toString() {
	String s = ""; //$NON-NLS-1$
	Object object;
	for (int i = 0, l = this.valueTable.length; i < l; i++)
		if ((object = this.valueTable[i]) != null)
			s += this.keyTable[i].toString() + " -> " + object.toString() + "\n"; 	//$NON-NLS-2$ //$NON-NLS-1$
	return s;
}
}
