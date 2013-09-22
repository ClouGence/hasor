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
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class VerificationTypeInfo {
	/**
	 * The tag value representing top variable info
	 * @since 3.2
	 */
	public static final int ITEM_TOP = 0;
	/**
	 * The tag value representing integer variable info
	 * @since 3.2
	 */
	public static final int ITEM_INTEGER = 1;
	/**
	 * The tag value representing float variable info
	 * @since 3.2
	 */
	public static final int ITEM_FLOAT = 2;
	/**
	 * The tag value representing double variable info
	 * @since 3.2
	 */
	public static final int ITEM_DOUBLE = 3;
	/**
	 * The tag value representing long variable info
	 * @since 3.2
	 */
	public static final int ITEM_LONG = 4;
	/**
	 * The tag value representing null variable info
	 * @since 3.2
	 */
	public static final int ITEM_NULL = 5;
	/**
	 * The tag value representing uninitialized this variable info
	 * @since 3.2
	 */
	public static final int ITEM_UNINITIALIZED_THIS = 6;
	/**
	 * The tag value representing object variable info
	 * @since 3.2
	 */
	public static final int ITEM_OBJECT = 7;
	/**
	 * The tag value representing uninitialized variable info
	 * @since 3.2
	 */
	public static final int ITEM_UNINITIALIZED = 8;

	public int tag;
	private int id;
	private char[] constantPoolName;
	public int offset;

private VerificationTypeInfo() {
	// for duplication
}
public VerificationTypeInfo(int id, char[] constantPoolName) {
	this(id, VerificationTypeInfo.ITEM_OBJECT, constantPoolName);
}
public VerificationTypeInfo(int id, int tag, char[] constantPoolName) {
	this.id = id;
	this.tag = tag;
	this.constantPoolName = constantPoolName;
}
public VerificationTypeInfo(int tag, TypeBinding binding) {
	this(binding);
	this.tag = tag;
}
public VerificationTypeInfo(TypeBinding binding) {
	this.id = binding.id;
	switch(binding.id) {
		case TypeIds.T_boolean :
		case TypeIds.T_byte :
		case TypeIds.T_char :
		case TypeIds.T_int :
		case TypeIds.T_short :
			this.tag = VerificationTypeInfo.ITEM_INTEGER;
			break;
		case TypeIds.T_float :
			this.tag = VerificationTypeInfo.ITEM_FLOAT;
			break;
		case TypeIds.T_long :
			this.tag = VerificationTypeInfo.ITEM_LONG;
			break;
		case TypeIds.T_double :
			this.tag = VerificationTypeInfo.ITEM_DOUBLE;
			break;
		case TypeIds.T_null :
			this.tag = VerificationTypeInfo.ITEM_NULL;
			break;
		default:
			this.tag =  VerificationTypeInfo.ITEM_OBJECT;
			this.constantPoolName = binding.constantPoolName();
	}
}
public void setBinding(TypeBinding binding) {
	this.constantPoolName = binding.constantPoolName();
	final int typeBindingId = binding.id;
	this.id = typeBindingId;
	switch(typeBindingId) {
		case TypeIds.T_boolean :
		case TypeIds.T_byte :
		case TypeIds.T_char :
		case TypeIds.T_int :
		case TypeIds.T_short :
			this.tag = VerificationTypeInfo.ITEM_INTEGER;
			break;
		case TypeIds.T_float :
			this.tag = VerificationTypeInfo.ITEM_FLOAT;
			break;
		case TypeIds.T_long :
			this.tag = VerificationTypeInfo.ITEM_LONG;
			break;
		case TypeIds.T_double :
			this.tag = VerificationTypeInfo.ITEM_DOUBLE;
			break;
		case TypeIds.T_null :
			this.tag = VerificationTypeInfo.ITEM_NULL;
			break;
		default:
			this.tag =  VerificationTypeInfo.ITEM_OBJECT;
	}
}
public int id() {
	return this.id;
}
public String toString() {
	StringBuffer buffer = new StringBuffer();
	switch(this.tag) {
		case VerificationTypeInfo.ITEM_UNINITIALIZED_THIS :
			buffer.append("uninitialized_this(").append(readableName()).append(")"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case VerificationTypeInfo.ITEM_UNINITIALIZED :
			buffer.append("uninitialized(").append(readableName()).append(")"); //$NON-NLS-1$//$NON-NLS-2$
			break;
		case VerificationTypeInfo.ITEM_OBJECT :
			buffer.append(readableName());
			break;
		case VerificationTypeInfo.ITEM_DOUBLE :
			buffer.append('D');
			break;
		case VerificationTypeInfo.ITEM_FLOAT :
			buffer.append('F');
			break;
		case VerificationTypeInfo.ITEM_INTEGER :
			buffer.append('I');
			break;
		case VerificationTypeInfo.ITEM_LONG :
			buffer.append('J');
			break;
		case VerificationTypeInfo.ITEM_NULL :
			buffer.append("null"); //$NON-NLS-1$
			break;
		case VerificationTypeInfo.ITEM_TOP :
			buffer.append("top"); //$NON-NLS-1$
			break;
	}
	return String.valueOf(buffer);
}
public VerificationTypeInfo duplicate() {
	final VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo();
	verificationTypeInfo.id = this.id;
	verificationTypeInfo.tag = this.tag;
	verificationTypeInfo.constantPoolName = this.constantPoolName;
	verificationTypeInfo.offset = this.offset;
	return verificationTypeInfo;
}
public boolean equals(Object obj) {
	if (obj instanceof VerificationTypeInfo) {
		VerificationTypeInfo info1 = (VerificationTypeInfo) obj;
		return info1.tag == this.tag && CharOperation.equals(info1.constantPoolName(), constantPoolName());
	}
	return false;
}
public int hashCode() {
	return this.tag + this.id + this.constantPoolName.length + this.offset;
}
public char[] constantPoolName() {
	return this.constantPoolName;
}
public char[] readableName() {
	return this.constantPoolName;
}
public void replaceWithElementType() {
	if (this.constantPoolName[1] == 'L') {
		this.constantPoolName = CharOperation.subarray(this.constantPoolName, 2,  this.constantPoolName.length - 1);
	} else {
		this.constantPoolName = CharOperation.subarray(this.constantPoolName, 1, this.constantPoolName.length);
		if (this.constantPoolName.length == 1) {
			switch(this.constantPoolName[0]) {
				case 'I' :
					this.id = TypeIds.T_int;
					break;
				case 'B' :
					this.id = TypeIds.T_byte;
					break;
				case 'S' :
					this.id = TypeIds.T_short;
					break;
				case 'C' :
					this.id = TypeIds.T_char;
					break;
				case 'J' :
					this.id = TypeIds.T_long;
					break;
				case 'F' :
					this.id = TypeIds.T_float;
					break;
				case 'D' :
					this.id = TypeIds.T_double;
					break;
				case 'Z' :
					this.id = TypeIds.T_boolean;
					break;
				case 'N' :
					this.id = TypeIds.T_null;
					break;
				case 'V' :
					this.id = TypeIds.T_void;
					break;
			}
		}
	}
}
}
