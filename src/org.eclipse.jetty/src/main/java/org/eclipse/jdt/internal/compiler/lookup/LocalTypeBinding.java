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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public final class LocalTypeBinding extends NestedTypeBinding {
	final static char[] LocalTypePrefix = { '$', 'L', 'o', 'c', 'a', 'l', '$' };

	private InnerEmulationDependency[] dependents;
	public ArrayBinding[] localArrayBindings; // used to cache array bindings of various dimensions for this local type
	public CaseStatement enclosingCase; // from 1.4 on, local types should not be accessed across switch case blocks (52221)
	public int sourceStart; // used by computeUniqueKey to uniquely identify this binding
	public MethodBinding enclosingMethod;

public LocalTypeBinding(ClassScope scope, SourceTypeBinding enclosingType, CaseStatement switchCase) {
	super(
		new char[][] {CharOperation.concat(LocalTypeBinding.LocalTypePrefix, scope.referenceContext.name)},
		scope,
		enclosingType);
	TypeDeclaration typeDeclaration = scope.referenceContext;
	if ((typeDeclaration.bits & ASTNode.IsAnonymousType) != 0) {
		this.tagBits |= TagBits.AnonymousTypeMask;
	} else {
		this.tagBits |= TagBits.LocalTypeMask;
	}
	this.enclosingCase = switchCase;
	this.sourceStart = typeDeclaration.sourceStart;
	MethodScope methodScope = scope.enclosingMethodScope();
	AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
	if (methodDeclaration != null) {
		this.enclosingMethod = methodDeclaration.binding;
	}
}

/* Record a dependency onto a source target type which may be altered
* by the end of the innerclass emulation. Later on, we will revisit
* all its dependents so as to update them (see updateInnerEmulationDependents()).
*/
public void addInnerEmulationDependent(BlockScope dependentScope, boolean wasEnclosingInstanceSupplied) {
	int index;
	if (this.dependents == null) {
		index = 0;
		this.dependents = new InnerEmulationDependency[1];
	} else {
		index = this.dependents.length;
		for (int i = 0; i < index; i++)
			if (this.dependents[i].scope == dependentScope)
				return; // already stored
		System.arraycopy(this.dependents, 0, (this.dependents = new InnerEmulationDependency[index + 1]), 0, index);
	}
	this.dependents[index] = new InnerEmulationDependency(dependentScope, wasEnclosingInstanceSupplied);
	//  System.out.println("Adding dependency: "+ new String(scope.enclosingType().readableName()) + " --> " + new String(this.readableName()));
}

/*
 * Returns the anonymous original super type (in some error cases, superclass may get substituted with Object)
 */
public ReferenceBinding anonymousOriginalSuperType() {
	if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
		return this.superInterfaces[0];
	}
	if ((this.tagBits & TagBits.HierarchyHasProblems) == 0) {
		return this.superclass;
	}
	if (this.scope != null) {
		TypeReference typeReference = this.scope.referenceContext.allocation.type;
		if (typeReference != null) {
			return (ReferenceBinding) typeReference.resolvedType;
		}
	}
	return this.superclass; // default answer
}

public char[] computeUniqueKey(boolean isLeaf) {
	char[] outerKey = outermostEnclosingType().computeUniqueKey(isLeaf);
	int semicolon = CharOperation.lastIndexOf(';', outerKey);

	StringBuffer sig = new StringBuffer();
	sig.append(outerKey, 0, semicolon);

	// insert $sourceStart
	sig.append('$');
	sig.append(String.valueOf(this.sourceStart));

	// insert $LocalName if local
	if (!isAnonymousType()) {
		sig.append('$');
		sig.append(this.sourceName);
	}

	// insert remaining from outer key
	sig.append(outerKey, semicolon, outerKey.length-semicolon);

	int sigLength = sig.length();
	char[] uniqueKey = new char[sigLength];
	sig.getChars(0, sigLength, uniqueKey, 0);
	return uniqueKey;
}

public char[] constantPoolName() /* java/lang/Object */ {
	if (this.constantPoolName == null && this.scope != null) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=322154, we do have some
		// cases where the left hand does not know what the right is doing.
		this.constantPoolName = this.scope.compilationUnitScope().computeConstantPoolName(this);
	}
	return this.constantPoolName;	
}

ArrayBinding createArrayType(int dimensionCount, LookupEnvironment lookupEnvironment) {
	if (this.localArrayBindings == null) {
		this.localArrayBindings = new ArrayBinding[] {new ArrayBinding(this, dimensionCount, lookupEnvironment)};
		return this.localArrayBindings[0];
	}
	// find the cached array binding for this dimensionCount (if any)
	int length = this.localArrayBindings.length;
	for (int i = 0; i < length; i++)
		if (this.localArrayBindings[i].dimensions == dimensionCount)
			return this.localArrayBindings[i];

	// no matching array
	System.arraycopy(this.localArrayBindings, 0, this.localArrayBindings = new ArrayBinding[length + 1], 0, length);
	return this.localArrayBindings[length] = new ArrayBinding(this, dimensionCount, lookupEnvironment);
}

/*
 * Overriden for code assist. In this case, the constantPoolName() has not been computed yet.
 * Slam the source name so that the signature is syntactically correct.
 * (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=99686)
 */
public char[] genericTypeSignature() {
	if (this.genericReferenceTypeSignature == null && this.constantPoolName == null) {
		if (isAnonymousType())
			setConstantPoolName(superclass().sourceName());
		else
			setConstantPoolName(sourceName());
	}
	return super.genericTypeSignature();
}

public char[] readableName() /*java.lang.Object,  p.X<T> */ {
    char[] readableName;
	if (isAnonymousType()) {
		readableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().readableName(), TypeConstants.ANONYM_SUFFIX);
	} else if (isMemberType()) {
		readableName = CharOperation.concat(enclosingType().readableName(), this.sourceName, '.');
	} else {
		readableName = this.sourceName;
	}
	TypeVariableBinding[] typeVars;
	if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
	    StringBuffer nameBuffer = new StringBuffer(10);
	    nameBuffer.append(readableName).append('<');
	    for (int i = 0, length = typeVars.length; i < length; i++) {
	        if (i > 0) nameBuffer.append(',');
	        nameBuffer.append(typeVars[i].readableName());
	    }
	    nameBuffer.append('>');
	    int nameLength = nameBuffer.length();
		readableName = new char[nameLength];
		nameBuffer.getChars(0, nameLength, readableName, 0);
	}
	return readableName;
}

public char[] shortReadableName() /*Object*/ {
    char[] shortReadableName;
	if (isAnonymousType()) {
		shortReadableName = CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().shortReadableName(), TypeConstants.ANONYM_SUFFIX);
	} else if (isMemberType()) {
		shortReadableName = CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.');
	} else {
		shortReadableName = this.sourceName;
	}
	TypeVariableBinding[] typeVars;
	if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
	    StringBuffer nameBuffer = new StringBuffer(10);
	    nameBuffer.append(shortReadableName).append('<');
	    for (int i = 0, length = typeVars.length; i < length; i++) {
	        if (i > 0) nameBuffer.append(',');
	        nameBuffer.append(typeVars[i].shortReadableName());
	    }
	    nameBuffer.append('>');
		int nameLength = nameBuffer.length();
		shortReadableName = new char[nameLength];
		nameBuffer.getChars(0, nameLength, shortReadableName, 0);
	}
	return shortReadableName;
}

// Record that the type is a local member type
public void setAsMemberType() {
	this.tagBits |= TagBits.MemberTypeMask;
}

public void setConstantPoolName(char[] computedConstantPoolName) /* java/lang/Object */ {
	this.constantPoolName = computedConstantPoolName;
}

/*
 * Overriden for code assist. In this case, the constantPoolName() has not been computed yet.
 * Slam the source name so that the signature is syntactically correct.
 * (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=102284)
 */
public char[] signature() {
	if (this.signature == null && this.constantPoolName == null) {
		if (isAnonymousType())
			setConstantPoolName(superclass().sourceName());
		else
			setConstantPoolName(sourceName());
	}
	return super.signature();
}

public char[] sourceName() {
	if (isAnonymousType()) {
		return CharOperation.concat(TypeConstants.ANONYM_PREFIX, anonymousOriginalSuperType().sourceName(), TypeConstants.ANONYM_SUFFIX);
	} else
		return this.sourceName;
}

public String toString() {
	if (isAnonymousType())
		return "Anonymous type : " + super.toString(); //$NON-NLS-1$
	if (isMemberType())
		return "Local member type : " + new String(sourceName()) + " " + super.toString(); //$NON-NLS-2$ //$NON-NLS-1$
	return "Local type : " + new String(sourceName()) + " " + super.toString(); //$NON-NLS-2$ //$NON-NLS-1$
}

/* Trigger the dependency mechanism forcing the innerclass emulation
* to be propagated to all dependent source types.
*/
public void updateInnerEmulationDependents() {
	if (this.dependents != null) {
		for (int i = 0; i < this.dependents.length; i++) {
			InnerEmulationDependency dependency = this.dependents[i];
			// System.out.println("Updating " + new String(this.readableName()) + " --> " + new String(dependency.scope.enclosingType().readableName()));
			dependency.scope.propagateInnerEmulation(this, dependency.wasEnclosingInstanceSupplied);
		}
	}
}
}
