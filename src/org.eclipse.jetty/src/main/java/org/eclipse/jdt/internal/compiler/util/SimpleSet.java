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
public final class SimpleSet implements Cloneable {

// to avoid using Enumerations, walk the individual values skipping nulls
public Object[] values;
public int elementSize; // number of elements in the table
public int threshold;

public SimpleSet() {
	this(13);
}

public SimpleSet(int size) {
	if (size < 3) size = 3;
	this.elementSize = 0;
	this.threshold = size + 1; // size is the expected number of elements
	this.values = new Object[2 * size + 1];
}

public Object add(Object object) {
	int length = this.values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = this.values[index]) != null) {
		if (current.equals(object)) return this.values[index] = object;
		if (++index == length) index = 0;
	}
	this.values[index] = object;

	// assumes the threshold is never equal to the size of the table
	if (++this.elementSize > this.threshold) rehash();
	return object;
}

public Object addIfNotIncluded(Object object) {
	int length = this.values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = this.values[index]) != null) {
		if (current.equals(object)) return null; // already existed
		if (++index == length) index = 0;
	}
	this.values[index] = object;

	// assumes the threshold is never equal to the size of the table
	if (++this.elementSize > this.threshold) rehash();
	return object;
}

public void asArray(Object[] copy) {
	if (this.elementSize != copy.length)
		throw new IllegalArgumentException();
	int index = this.elementSize;
	for (int i = 0, l = this.values.length; i < l && index > 0; i++)
		if (this.values[i] != null)
			copy[--index] = this.values[i];
}

public void clear() {
	for (int i = this.values.length; --i >= 0;)
		this.values[i] = null;
	this.elementSize = 0;
}

public Object clone() throws CloneNotSupportedException {
	SimpleSet result = (SimpleSet) super.clone();
	result.elementSize = this.elementSize;
	result.threshold = this.threshold;

	int length = this.values.length;
	result.values = new Object[length];
	System.arraycopy(this.values, 0, result.values, 0, length);
	return result;
}

public boolean includes(Object object) {
	int length = this.values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = this.values[index]) != null) {
		if (current.equals(object)) return true;
		if (++index == length) index = 0;
	}
	return false;
}

public Object remove(Object object) {
	int length = this.values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = this.values[index]) != null) {
		if (current.equals(object)) {
			this.elementSize--;
			Object oldValue = this.values[index];
			this.values[index] = null;
			if (this.values[index + 1 == length ? 0 : index + 1] != null)
				rehash(); // only needed if a possible collision existed
			return oldValue;
		}
		if (++index == length) index = 0;
	}
	return null;
}

private void rehash() {
	SimpleSet newSet = new SimpleSet(this.elementSize * 2); // double the number of expected elements
	Object current;
	for (int i = this.values.length; --i >= 0;)
		if ((current = this.values[i]) != null)
			newSet.add(current);

	this.values = newSet.values;
	this.elementSize = newSet.elementSize;
	this.threshold = newSet.threshold;
}

public String toString() {
	String s = ""; //$NON-NLS-1$
	Object object;
	for (int i = 0, l = this.values.length; i < l; i++)
		if ((object = this.values[i]) != null)
			s += object.toString() + "\n"; //$NON-NLS-1$
	return s;
}
}
