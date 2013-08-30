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

public class ByteConstant extends Constant {

	private byte value;

	public static Constant fromValue(byte value) {
		return new ByteConstant(value);
	}

	private ByteConstant(byte value) {
		this.value = value;
	}

	public byte byteValue() {
		return this.value;
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
		return this.value; // implicit cast to return type
	}

	public long longValue() {
		return this.value; // implicit cast to return type
	}

	public short shortValue() {
		return this.value; // implicit cast to return type
	}

	public String stringValue() {
		// spec 15.17.11
		return String.valueOf(this.value);
	}

	public String toString() {
		return "(byte)" + this.value; //$NON-NLS-1$
	}

	public int typeID() {
		return T_byte;
	}

	public int hashCode() {
		return this.value;
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
		ByteConstant other = (ByteConstant) obj;
		return this.value == other.value;
	}
}
