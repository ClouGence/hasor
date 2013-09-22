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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;

public class PackageBinding extends Binding implements TypeConstants {
	public long tagBits = 0; // See values in the interface TagBits below

	public char[][] compoundName;
	PackageBinding parent;
	public LookupEnvironment environment;
	HashtableOfType knownTypes;
	HashtableOfPackage knownPackages;
protected PackageBinding() {
	// for creating problem package
}
public PackageBinding(char[] topLevelPackageName, LookupEnvironment environment) {
	this(new char[][] {topLevelPackageName}, null, environment);
}
/* Create the default package.
*/
public PackageBinding(char[][] compoundName, PackageBinding parent, LookupEnvironment environment) {
	this.compoundName = compoundName;
	this.parent = parent;
	this.environment = environment;
	this.knownTypes = null; // initialized if used... class counts can be very large 300-600
	this.knownPackages = new HashtableOfPackage(3); // sub-package counts are typically 0-3
}

public PackageBinding(LookupEnvironment environment) {
	this(CharOperation.NO_CHAR_CHAR, null, environment);
}
private void addNotFoundPackage(char[] simpleName) {
	this.knownPackages.put(simpleName, LookupEnvironment.TheNotFoundPackage);
}
private void addNotFoundType(char[] simpleName) {
	if (this.knownTypes == null)
		this.knownTypes = new HashtableOfType(25);
	this.knownTypes.put(simpleName, LookupEnvironment.TheNotFoundType);
}
void addPackage(PackageBinding element) {
	if ((element.tagBits & TagBits.HasMissingType) == 0) clearMissingTagBit();
	this.knownPackages.put(element.compoundName[element.compoundName.length - 1], element);
}
void addType(ReferenceBinding element) {
	if ((element.tagBits & TagBits.HasMissingType) == 0) clearMissingTagBit();
	if (this.knownTypes == null)
		this.knownTypes = new HashtableOfType(25);
	this.knownTypes.put(element.compoundName[element.compoundName.length - 1], element);
}

void clearMissingTagBit() {
	PackageBinding current = this;
	do {
		current.tagBits &= ~TagBits.HasMissingType;
	} while ((current = current.parent) != null);
}
/*
 * slash separated name
 * org.eclipse.jdt.core --> org/eclipse/jdt/core
 */
public char[] computeUniqueKey(boolean isLeaf) {
	return CharOperation.concatWith(this.compoundName, '/');
}
private PackageBinding findPackage(char[] name) {
	if (!this.environment.isPackage(this.compoundName, name))
		return null;

	char[][] subPkgCompoundName = CharOperation.arrayConcat(this.compoundName, name);
	PackageBinding subPackageBinding = new PackageBinding(subPkgCompoundName, this, this.environment);
	addPackage(subPackageBinding);
	return subPackageBinding;
}
/* Answer the subpackage named name; ask the oracle for the package if its not in the cache.
* Answer null if it could not be resolved.
*
* NOTE: This should only be used when we know there is NOT a type with the same name.
*/
PackageBinding getPackage(char[] name) {
	PackageBinding binding = getPackage0(name);
	if (binding != null) {
		if (binding == LookupEnvironment.TheNotFoundPackage)
			return null;
		else
			return binding;
	}
	if ((binding = findPackage(name)) != null)
		return binding;

	// not found so remember a problem package binding in the cache for future lookups
	addNotFoundPackage(name);
	return null;
}
/* Answer the subpackage named name if it exists in the cache.
* Answer theNotFoundPackage if it could not be resolved the first time
* it was looked up, otherwise answer null.
*
* NOTE: Senders must convert theNotFoundPackage into a real problem
* package if its to returned.
*/

PackageBinding getPackage0(char[] name) {
	return this.knownPackages.get(name);
}
/* Answer the type named name; ask the oracle for the type if its not in the cache.
* Answer a NotVisible problem type if the type is not visible from the invocationPackage.
* Answer null if it could not be resolved.
*
* NOTE: This should only be used by source types/scopes which know there is NOT a
* package with the same name.
*/

ReferenceBinding getType(char[] name) {
	ReferenceBinding referenceBinding = getType0(name);
	if (referenceBinding == null) {
		if ((referenceBinding = this.environment.askForType(this, name)) == null) {
			// not found so remember a problem type binding in the cache for future lookups
			addNotFoundType(name);
			return null;
		}
	}

	if (referenceBinding == LookupEnvironment.TheNotFoundType)
		return null;

	referenceBinding = (ReferenceBinding) BinaryTypeBinding.resolveType(referenceBinding, this.environment, false /* no raw conversion for now */);
	if (referenceBinding.isNestedType())
		return new ProblemReferenceBinding(new char[][]{ name }, referenceBinding, ProblemReasons.InternalNameProvided);
	return referenceBinding;
}
/* Answer the type named name if it exists in the cache.
* Answer theNotFoundType if it could not be resolved the first time
* it was looked up, otherwise answer null.
*
* NOTE: Senders must convert theNotFoundType into a real problem
* reference type if its to returned.
*/

ReferenceBinding getType0(char[] name) {
	if (this.knownTypes == null)
		return null;
	return this.knownTypes.get(name);
}
/* Answer the package or type named name; ask the oracle if it is not in the cache.
* Answer null if it could not be resolved.
*
* When collisions exist between a type name & a package name, answer the type.
* Treat the package as if it does not exist... a problem was already reported when the type was defined.
*
* NOTE: no visibility checks are performed.
* THIS SHOULD ONLY BE USED BY SOURCE TYPES/SCOPES.
*/

public Binding getTypeOrPackage(char[] name) {
	ReferenceBinding referenceBinding = getType0(name);
	if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
		referenceBinding = (ReferenceBinding) BinaryTypeBinding.resolveType(referenceBinding, this.environment, false /* no raw conversion for now */);
		if (referenceBinding.isNestedType()) {
			return new ProblemReferenceBinding(new char[][]{name}, referenceBinding, ProblemReasons.InternalNameProvided);
		}
		if ((referenceBinding.tagBits & TagBits.HasMissingType) == 0) {
			return referenceBinding;
		}
		// referenceBinding is a MissingType, will return it if no package is found
	}

	PackageBinding packageBinding = getPackage0(name);
	if (packageBinding != null && packageBinding != LookupEnvironment.TheNotFoundPackage) {
		return packageBinding;
	}
	if (referenceBinding == null) { // have not looked for it before
		if ((referenceBinding = this.environment.askForType(this, name)) != null) {
			if (referenceBinding.isNestedType()) {
				return new ProblemReferenceBinding(new char[][]{name}, referenceBinding, ProblemReasons.InternalNameProvided);
			}
			return referenceBinding;
		}

		// Since name could not be found, add a problem binding
		// to the collections so it will be reported as an error next time.
		addNotFoundType(name);
	}

	if (packageBinding == null) { // have not looked for it before
		if ((packageBinding = findPackage(name)) != null) {
			return packageBinding;
		}
		if (referenceBinding != null && referenceBinding != LookupEnvironment.TheNotFoundType) {
			return referenceBinding; // found cached missing type - check if package conflict
		}
		addNotFoundPackage(name);
	}

	return null;
}
public final boolean isViewedAsDeprecated() {
	if ((this.tagBits & TagBits.DeprecatedAnnotationResolved) == 0) {
		this.tagBits |= TagBits.DeprecatedAnnotationResolved;
		if (this.compoundName != CharOperation.NO_CHAR_CHAR) {
			ReferenceBinding packageInfo = this.getType(TypeConstants.PACKAGE_INFO_NAME);
			if (packageInfo != null) {
				packageInfo.initializeDeprecatedAnnotationTagBits();
				this.tagBits |= packageInfo.tagBits & TagBits.AllStandardAnnotationsMask;
			}
		}
	}
	return (this.tagBits & TagBits.AnnotationDeprecated) != 0;
}
/* API
* Answer the receiver's binding type from Binding.BindingID.
*/
public final int kind() {
	return Binding.PACKAGE;
}

public int problemId() {
	if ((this.tagBits & TagBits.HasMissingType) != 0)
		return ProblemReasons.NotFound;
	return ProblemReasons.NoError;
}

public char[] readableName() /*java.lang*/ {
	return CharOperation.concatWith(this.compoundName, '.');
}
public String toString() {
	String str;
	if (this.compoundName == CharOperation.NO_CHAR_CHAR) {
		str = "The Default Package"; //$NON-NLS-1$
	} else {
		str = "package " + ((this.compoundName != null) ? CharOperation.toString(this.compoundName) : "UNNAMED"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	if ((this.tagBits & TagBits.HasMissingType) != 0) {
		str += "[MISSING]"; //$NON-NLS-1$
	}
	return str;
}
}
