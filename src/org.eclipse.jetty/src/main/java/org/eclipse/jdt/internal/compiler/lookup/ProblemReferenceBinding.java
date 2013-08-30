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
package org.eclipse.jdt.internal.compiler.lookup;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.compiler.CharOperation;

public class ProblemReferenceBinding extends ReferenceBinding {
	ReferenceBinding closestMatch;
	private int problemReason;

// NOTE: must only answer the subset of the name related to the problem

public ProblemReferenceBinding(char[][] compoundName, ReferenceBinding closestMatch, int problemReason) {
	this.compoundName = compoundName;
	this.closestMatch = closestMatch;
	this.problemReason = problemReason;
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#closestMatch()
 */
public TypeBinding closestMatch() {
	return this.closestMatch;
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#closestMatch()
 */
public ReferenceBinding closestReferenceMatch() {
	return this.closestMatch;
}

/* API
* Answer the problem id associated with the receiver.
* NoError if the receiver is a valid binding.
*/
public int problemId() {
	return this.problemReason;
}

public static String problemReasonString(int problemReason) {
	try {
		Class reasons = ProblemReasons.class;
		String simpleName = reasons.getName();
		int lastDot = simpleName.lastIndexOf('.');
		if (lastDot >= 0) {
			simpleName = simpleName.substring(lastDot+1);
		}
		Field[] fields = reasons.getFields();
		for (int i = 0, length = fields.length; i < length; i++) {
			Field field = fields[i];
			if (!field.getType().equals(int.class)) continue;
			if (field.getInt(reasons) == problemReason) {
				return simpleName + '.' + field.getName();
			}
		}
	} catch (IllegalAccessException e) {
		// do nothing
	}
	return "unknown"; //$NON-NLS-1$
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#shortReadableName()
 */
public char[] shortReadableName() {
	return readableName();
}

public String toString() {
	StringBuffer buffer = new StringBuffer(10);
	buffer.append("ProblemType:[compoundName="); //$NON-NLS-1$
	buffer.append(this.compoundName == null ? "<null>" : new String(CharOperation.concatWith(this.compoundName,'.'))); //$NON-NLS-1$
	buffer.append("][problemID=").append(problemReasonString(this.problemReason)); //$NON-NLS-1$
	buffer.append("][closestMatch="); //$NON-NLS-1$
	buffer.append(this.closestMatch == null ? "<null>" : this.closestMatch.toString()); //$NON-NLS-1$
	buffer.append("]"); //$NON-NLS-1$
	return buffer.toString();
}
}
