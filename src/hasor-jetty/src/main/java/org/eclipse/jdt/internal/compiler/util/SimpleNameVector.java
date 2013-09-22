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

import org.eclipse.jdt.core.compiler.CharOperation;

public final class SimpleNameVector {

	static int INITIAL_SIZE = 10;

	public int size;
	int maxSize;
	char[][] elements;

	public SimpleNameVector() {

		this.maxSize = INITIAL_SIZE;
		this.size = 0;
		this.elements = new char[this.maxSize][];
	}

	public void add(char[] newElement) {

		if (this.size == this.maxSize) // knows that size starts <= maxSize
			System.arraycopy(this.elements, 0, (this.elements = new char[this.maxSize *= 2][]), 0, this.size);
		this.elements[this.size++] = newElement;
	}

	public void addAll(char[][] newElements) {

		if (this.size + newElements.length >= this.maxSize) {
			this.maxSize = this.size + newElements.length; // assume no more elements will be added
			System.arraycopy(this.elements, 0, (this.elements = new char[this.maxSize][]), 0, this.size);
		}
		System.arraycopy(newElements, 0, this.elements, this.size, newElements.length);
		this.size += newElements.length;
	}

	public void copyInto(Object[] targetArray){

		System.arraycopy(this.elements, 0, targetArray, 0, this.size);
	}

	public boolean contains(char[] element) {

		for (int i = this.size; --i >= 0;)
			if (CharOperation.equals(element, this.elements[i]))
				return true;
		return false;
	}

	public char[] elementAt(int index) {
		return this.elements[index];
	}

	public char[] remove(char[] element) {

		// assumes only one occurrence of the element exists
		for (int i = this.size; --i >= 0;)
			if (element == this.elements[i]) {
				// shift the remaining elements down one spot
				System.arraycopy(this.elements, i + 1, this.elements, i, --this.size - i);
				this.elements[this.size] = null;
				return element;
			}
		return null;
	}

	public void removeAll() {

		for (int i = this.size; --i >= 0;)
			this.elements[i] = null;
		this.size = 0;
	}

	public int size(){

		return this.size;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.size; i++) {
			buffer.append(this.elements[i]).append("\n"); //$NON-NLS-1$
		}
		return buffer.toString();
	}
}
