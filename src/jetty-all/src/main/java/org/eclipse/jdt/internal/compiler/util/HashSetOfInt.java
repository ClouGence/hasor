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
package org.eclipse.jdt.internal.compiler.util;

/**
 * HashSet of Object[]
 */
public final class HashSetOfInt implements Cloneable {

	// to avoid using Enumerations, walk the individual tables skipping nulls
	public int[] set;

	public int elementSize; // number of elements in the table
	int threshold;

	public HashSetOfInt() {
		this(13);
	}

	public HashSetOfInt(int size) {

		this.elementSize = 0;
		this.threshold = size; // size represents the expected number of elements
		int extraRoom = (int) (size * 1.75f);
		if (this.threshold == extraRoom)
			extraRoom++;
		this.set = new int[extraRoom];
	}

	public Object clone() throws CloneNotSupportedException {
		HashSetOfInt result = (HashSetOfInt) super.clone();
		result.elementSize = this.elementSize;
		result.threshold = this.threshold;

		int length = this.set.length;
		result.set = new int[length];
		System.arraycopy(this.set, 0, result.set, 0, length);

		return result;
	}

	public boolean contains(int element) {
		int length = this.set.length;
		int index = element % length;
		int currentElement;
		while ((currentElement = this.set[index]) != 0) {
			if (currentElement == element)
				return true;
			if (++index == length) {
				index = 0;
			}
		}
		return false;
	}

	public int add(int element) {
		int length = this.set.length;
		int index = element % length;
		int currentElement;
		while ((currentElement = this.set[index]) != 0) {
			if (currentElement == element)
				return this.set[index] = element;
			if (++index == length) {
				index = 0;
			}
		}
		this.set[index] = element;

		// assumes the threshold is never equal to the size of the table
		if (++this.elementSize > this.threshold)
			rehash();
		return element;
	}

	public int remove(int element) {
		int length = this.set.length;
		int index = element % length;
		int currentElement;
		while ((currentElement = this.set[index]) != 0) {
			if (currentElement == element) {
				int existing = this.set[index];
				this.elementSize--;
				this.set[index] = 0;
				rehash();
				return existing;
			}
			if (++index == length) {
				index = 0;
			}
		}
		return 0;
	}

	private void rehash() {

		HashSetOfInt newHashSet = new HashSetOfInt(this.elementSize * 2);		// double the number of expected elements
		int currentElement;
		for (int i = this.set.length; --i >= 0;)
			if ((currentElement = this.set[i]) != 0)
				newHashSet.add(currentElement);

		this.set = newHashSet.set;
		this.threshold = newHashSet.threshold;
	}

	public int size() {
		return this.elementSize;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int element;
		for (int i = 0, length = this.set.length; i < length; i++)
			if ((element = this.set[i]) != 0) {
				buffer.append(element);
				if (i != length-1)
					buffer.append('\n');
			}
		return buffer.toString();
	}
}
