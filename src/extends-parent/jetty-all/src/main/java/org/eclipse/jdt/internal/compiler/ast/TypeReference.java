/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public abstract class TypeReference extends Expression {

	public static final TypeReference[] NO_TYPE_ARGUMENTS = new TypeReference[0];
/*
 * Answer a base type reference (can be an array of base type).
 */
public static final TypeReference baseTypeReference(int baseType, int dim) {

	if (dim == 0) {
		switch (baseType) {
			case (TypeIds.T_void) :
				return new SingleTypeReference(TypeBinding.VOID.simpleName, 0);
			case (TypeIds.T_boolean) :
				return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, 0);
			case (TypeIds.T_char) :
				return new SingleTypeReference(TypeBinding.CHAR.simpleName, 0);
			case (TypeIds.T_float) :
				return new SingleTypeReference(TypeBinding.FLOAT.simpleName, 0);
			case (TypeIds.T_double) :
				return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, 0);
			case (TypeIds.T_byte) :
				return new SingleTypeReference(TypeBinding.BYTE.simpleName, 0);
			case (TypeIds.T_short) :
				return new SingleTypeReference(TypeBinding.SHORT.simpleName, 0);
			case (TypeIds.T_int) :
				return new SingleTypeReference(TypeBinding.INT.simpleName, 0);
			default : //T_long
				return new SingleTypeReference(TypeBinding.LONG.simpleName, 0);
		}
	}
	switch (baseType) {
		case (TypeIds.T_void) :
			return new ArrayTypeReference(TypeBinding.VOID.simpleName, dim, 0);
		case (TypeIds.T_boolean) :
			return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, 0);
		case (TypeIds.T_char) :
			return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, 0);
		case (TypeIds.T_float) :
			return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, 0);
		case (TypeIds.T_double) :
			return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, 0);
		case (TypeIds.T_byte) :
			return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, 0);
		case (TypeIds.T_short) :
			return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, 0);
		case (TypeIds.T_int) :
			return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, 0);
		default : //T_long
			return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, 0);
	}
}

// allows us to trap completion & selection nodes
public void aboutToResolve(Scope scope) {
	// default implementation: do nothing
}
public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	return flowInfo;
}
public void checkBounds(Scope scope) {
	// only parameterized type references have bounds
}
public abstract TypeReference copyDims(int dim);
public int dimensions() {
	return 0;
}

public abstract char[] getLastToken();

/**
 * @return char[][]
 * TODO (jerome) should merge back into #getTypeName()
 */
public char [][] getParameterizedTypeName(){
	return getTypeName();
}
protected abstract TypeBinding getTypeBinding(Scope scope);
/**
 * @return char[][]
 */
public abstract char [][] getTypeName() ;

protected TypeBinding internalResolveType(Scope scope) {
	// handle the error here
	this.constant = Constant.NotAConstant;
	if (this.resolvedType != null) { // is a shared type reference which was already resolved
		if (this.resolvedType.isValidBinding()) {
			return this.resolvedType;
		} else {
			switch (this.resolvedType.problemId()) {
				case ProblemReasons.NotFound :
				case ProblemReasons.NotVisible :
				case ProblemReasons.InheritedNameHidesEnclosingName :
					TypeBinding type = this.resolvedType.closestMatch();
					if (type == null) return null;
					return scope.environment().convertToRawType(type, false /*do not force conversion of enclosing types*/);
				default :
					return null;
			}
		}
	}
	boolean hasError;
	TypeBinding type = this.resolvedType = getTypeBinding(scope);
	if (type == null) {
		return null; // detected cycle while resolving hierarchy
	} else if ((hasError = !type.isValidBinding()) == true) {
		reportInvalidType(scope);
		switch (type.problemId()) {
			case ProblemReasons.NotFound :
			case ProblemReasons.NotVisible :
			case ProblemReasons.InheritedNameHidesEnclosingName :
				type = type.closestMatch();
				if (type == null) return null;
				break;
			default :
				return null;
		}
	}
	if (type.isArrayType() && ((ArrayBinding) type).leafComponentType == TypeBinding.VOID) {
		scope.problemReporter().cannotAllocateVoidArray(this);
		return null;
	}
	if (!(this instanceof QualifiedTypeReference)   // QualifiedTypeReference#getTypeBinding called above will have already checked deprecation
			&& isTypeUseDeprecated(type, scope)) {
		reportDeprecatedType(type, scope);
	}
	type = scope.environment().convertToRawType(type, false /*do not force conversion of enclosing types*/);
	if (type.leafComponentType().isRawType()
			&& (this.bits & ASTNode.IgnoreRawTypeCheck) == 0
			&& scope.compilerOptions().getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore) {
		scope.problemReporter().rawTypeReference(this, type);
	}
	if (hasError) {
		// do not store the computed type, keep the problem type instead
		return type;
	}
	return this.resolvedType = type;
}
public boolean isTypeReference() {
	return true;
}

protected void reportDeprecatedType(TypeBinding type, Scope scope, int index) {
	scope.problemReporter().deprecatedType(type, this, index);
}

protected void reportDeprecatedType(TypeBinding type, Scope scope) {
	scope.problemReporter().deprecatedType(type, this, Integer.MAX_VALUE);
}

protected void reportInvalidType(Scope scope) {
	scope.problemReporter().invalidType(this, this.resolvedType);
}

public TypeBinding resolveSuperType(ClassScope scope) {
	// assumes the implementation of resolveType(ClassScope) will call back to detect cycles
	TypeBinding superType = resolveType(scope);
	if (superType == null) return null;

	if (superType.isTypeVariable()) {
		if (this.resolvedType.isValidBinding()) {
			this.resolvedType = new ProblemReferenceBinding(getTypeName(), (ReferenceBinding)this.resolvedType, ProblemReasons.IllegalSuperTypeVariable);
			reportInvalidType(scope);
		}
		return null;
	}
	return superType;
}

public final TypeBinding resolveType(BlockScope blockScope) {
	return resolveType(blockScope, true /* checkbounds if any */);
}

public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
	return internalResolveType(scope);
}

public TypeBinding resolveType(ClassScope scope) {
	return internalResolveType(scope);
}

public TypeBinding resolveTypeArgument(BlockScope blockScope, ReferenceBinding genericType, int rank) {
    return resolveType(blockScope, true /* check bounds*/);
}

public TypeBinding resolveTypeArgument(ClassScope classScope, ReferenceBinding genericType, int rank) {
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=294057, circularity is allowed when we are
	// resolving type arguments i.e interface A<T extends C> {}	interface B extends A<D> {}
	// interface D extends C {}	interface C extends B {}
	ReferenceBinding ref = classScope.referenceContext.binding;
	boolean pauseHierarchyCheck = false;
	try {
		if (ref.isHierarchyBeingConnected()) {
			ref.tagBits |= TagBits.PauseHierarchyCheck;
			pauseHierarchyCheck = true;
		}
	    return resolveType(classScope);
	} finally {
		if (pauseHierarchyCheck) {
			ref.tagBits &= ~TagBits.PauseHierarchyCheck;
		}
	}
}

public abstract void traverse(ASTVisitor visitor, BlockScope scope);

public abstract void traverse(ASTVisitor visitor, ClassScope scope);
}
