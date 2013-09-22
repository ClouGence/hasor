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
 * Represents a class reference in the class file.
 * One of the possible results for the default value of an annotation method or an element value pair.
 */
public class ClassSignature {

	char[] className;

public ClassSignature(final char[] className) {
	this.className = className;
}

/**
 * @return name of the type in the class file format
 */
public char[] getTypeName() {
	return this.className;
}

public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append(this.className);
	buffer.append(".class"); //$NON-NLS-1$
	return buffer.toString();
}

public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + CharOperation.hashCode(this.className);
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
	ClassSignature other = (ClassSignature) obj;
	return Arrays.equals(this.className, other.className);
}
}