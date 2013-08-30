/*******************************************************************************
 * Copyright (c) 2005, 2010 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *    olivier_thomann@ca.ibm.com - add hashCode() and equals(..) methods
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.env;

import java.util.Arrays;

import org.eclipse.jdt.core.compiler.CharOperation;
/**
 * Represents a reference to a enum constant in the class file.
 * One of the possible results for the default value of an annotation method.
 */
public class EnumConstantSignature {

	char[] typeName;
	char[] constName;

public EnumConstantSignature(char[] typeName, char[] constName) {
	this.typeName = typeName;
	this.constName = constName;
}

/**
 * @return name of the type in the class file format
 */
public char[] getTypeName() {
	return this.typeName;
}

/**
 * @return the name of the enum constant reference.
 */
public char[] getEnumConstantName() {
	return this.constName;
}

public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append(this.typeName);
	buffer.append('.');
	buffer.append(this.constName);
	return buffer.toString();
}

public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + CharOperation.hashCode(this.constName);
	result = prime * result + CharOperation.hashCode(this.typeName);
	return result;
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
	EnumConstantSignature other = (EnumConstantSignature) obj;
	if (!Arrays.equals(this.constName, other.constName)) {
		return false;
	}
	return Arrays.equals(this.typeName, other.typeName);
}
}
