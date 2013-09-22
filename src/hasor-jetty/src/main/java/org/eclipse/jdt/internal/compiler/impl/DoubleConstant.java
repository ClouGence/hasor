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

public class DoubleConstant extends Constant {

	private double value;

	public static Constant fromValue(double value) {
		return new DoubleConstant(value);
	}

	private DoubleConstant(double value) {
		this.value = value;
	}

	public byte byteValue() {
		return (byte) this.value;
	}

	public char charValue() {
		return (char) this.value;
	}

	public double doubleValue() {
		return this.value;
	}

	public float floatValue() {
		return (float) this.value;
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
		if (this == NotAConstant)
			return "(Constant) NotAConstant"; //$NON-NLS-1$
		return "(double)" + this.value;  //$NON-NLS-1$
	}

	public int typeID() {
		return T_double;
	}

	public int hashCode() {
		long temp = Double.doubleToLongBits(this.value);
		return (int) (temp ^ (temp >>> 32));
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
		DoubleConstant other = (DoubleConstant) obj;
		return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
	}
}
