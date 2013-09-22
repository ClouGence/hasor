/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.impl;

public class LongConstant extends Constant {

		private static final LongConstant ZERO = new LongConstant(0L);
		private static final LongConstant MIN_VALUE = new LongConstant(Long.MIN_VALUE);
		
		private long value;

public static Constant fromValue(long value) {
	if (value == 0L) {
		return ZERO;
	} else if (value == Long.MIN_VALUE) {
		return MIN_VALUE;
	}
	return new LongConstant(value);
}

private LongConstant(long value) {
	this.value = value;
}

public byte byteValue() {
	return (byte) this.value;
}

public char charValue() {
	return (char) this.value;
}

public double doubleValue() {
	return this.value; // implicit cast to return type
}

public float floatValue() {
	return this.value; // implicit cast to return type
}

public int intValue() {
	return (int) this.value;
}

public long longValue() {
	return this.value;
}

public short shortValue() {
	return (short) this.value;
}

public String stringValue() {
	//spec 15.17.11
	return String.valueOf(this.value);
}

public String toString(){

	return "(long)" + this.value ; //$NON-NLS-1$
}

public int typeID() {
	return T_long;
}

public int hashCode() {
	return (int) (this.value ^ (this.value >>> 32));
}

public boolean equals(Object obj) {
	if (this == obj) {
		return true;
	}
	if (obj == null) {
		return false;
	}
	if (getClass() != obj.getClass()) {
		return false;
	}
	LongConstant other = (LongConstant) obj;
	return this.value == other.value;
}
}
