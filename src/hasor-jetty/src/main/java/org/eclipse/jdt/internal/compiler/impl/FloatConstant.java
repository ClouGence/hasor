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

public class FloatConstant extends Constant {

	float value;

	public static Constant fromValue(float value) {
		return new FloatConstant(value);
	}

	private FloatConstant(float value) {
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
		return this.value;
	}

	public int intValue() {
		return (int) this.value;
	}

	public long longValue() {
		return (long) this.value;
	}

	public short shortValue() {
		return (short) this.value;
	}

	public String stringValue() {
		return String.valueOf(this.value);
	}

	public String toString() {
		return "(float)" + this.value; //$NON-NLS-1$
	}

	public int typeID() {
		return T_float;
	}

	public int hashCode() {
		return Float.floatToIntBits(this.value);
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
		FloatConstant other = (FloatConstant) obj;
		return Float.floatToIntBits(this.value) == Float.floatToIntBits(other.value);
	}
}
