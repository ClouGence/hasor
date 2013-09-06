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

import org.eclipse.jdt.core.compiler.CharOperation;

public class UnresolvedReferenceBinding extends ReferenceBinding {

ReferenceBinding resolvedType;
TypeBinding[] wrappers;

UnresolvedReferenceBinding(char[][] compoundName, PackageBinding packageBinding) {
	this.compoundName = compoundName;
	this.sourceName = compoundName[compoundName.length - 1]; // reasonable guess
	this.fPackage = packageBinding;
	this.wrappers = null;
}
void addWrapper(TypeBinding wrapper, LookupEnvironment environment) {
	if (this.resolvedType != null) {
		// the type reference B<B<T>.M> means a signature of <T:Ljava/lang/Object;>LB<LB<TT;>.M;>;
		// when the ParameterizedType for Unresolved B is created with args B<T>.M, the Unresolved B is resolved before the wrapper is added
		wrapper.swapUnresolved(this, this.resolvedType, environment);
		return;
	}
	if (this.wrappers == null) {
		this.wrappers = new TypeBinding[] {wrapper};
	} else {
		int length = this.wrappers.length;
		System.arraycopy(this.wrappers, 0, this.wrappers = new TypeBinding[length + 1], 0, length);
		this.wrappers[length] = wrapper;
	}
}
public String debugName() {
	return toString();
}
ReferenceBinding resolve(LookupEnvironment environment, boolean convertGenericToRawType) {
    ReferenceBinding targetType = this.resolvedType;
	if (targetType == null) {
		targetType = this.fPackage.getType0(this.compoundName[this.compoundName.length - 1]);
		if (targetType == this) {
			targetType = environment.askForType(this.compoundName);
		}
		if (targetType == null || targetType == this) { // could not resolve any better, error was already reported against it
			// report the missing class file first - only if not resolving a previously missing type
			if ((this.tagBits & TagBits.HasMissingType) == 0) {
				environment.problemReporter.isClassPathCorrect(
					this.compoundName,
					environment.unitBeingCompleted,
					environment.missingClassFileLocation);
			}
			// create a proxy for the missing BinaryType
			targetType = environment.createMissingType(null, this.compoundName);
		}
		setResolvedType(targetType, environment);
	}
	if (convertGenericToRawType) {
		targetType = (ReferenceBinding) environment.convertUnresolvedBinaryToRawType(targetType);
	}
	return targetType;
}
void setResolvedType(ReferenceBinding targetType, LookupEnvironment environment) {
	if (this.resolvedType == targetType) return; // already resolved

	// targetType may be a source or binary type
	this.resolvedType = targetType;
	// must ensure to update any other type bindings that can contain the resolved type
	// otherwise we could create 2 : 1 for this unresolved type & 1 for the resolved type
	if (this.wrappers != null)
		for (int i = 0, l = this.wrappers.length; i < l; i++)
			this.wrappers[i].swapUnresolved(this, targetType, environment);
	environment.updateCaches(this, targetType);
}
public String toString() {
	return "Unresolved type " + ((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED"); //$NON-NLS-1$ //$NON-NLS-2$
}
}
