/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for Bug 342671 - ClassCastException: org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding cannot be cast to org.eclipse.jdt.internal.compiler.lookup.ArrayBinding
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;

/**
 * Syntactic representation of a reference to a generic type.
 * Note that it might also have a dimension.
 */
public class ParameterizedSingleTypeReference extends ArrayTypeReference {

	public TypeReference[] typeArguments;

	public ParameterizedSingleTypeReference(char[] name, TypeReference[] typeArguments, int dim, long pos){
		super(name, dim, pos);
		this.originalSourceEnd = this.sourceEnd;
		this.typeArguments = typeArguments;
	}
	public void checkBounds(Scope scope) {
		if (this.resolvedType == null) return;

		if (this.resolvedType.leafComponentType() instanceof ParameterizedTypeBinding) {
			ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding) this.resolvedType.leafComponentType();
			ReferenceBinding currentType = parameterizedType.genericType();
			TypeVariableBinding[] typeVariables = currentType.typeVariables();
			TypeBinding[] argTypes = parameterizedType.arguments;
			if (argTypes != null && typeVariables != null) { // may be null in error cases
				parameterizedType.boundCheck(scope, this.typeArguments);
			}
		}
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#copyDims(int)
	 */
	public TypeReference copyDims(int dim) {
		return new ParameterizedSingleTypeReference(this.token, this.typeArguments, dim, (((long)this.sourceStart)<<32)+this.sourceEnd);
	}

	/**
	 * @return char[][]
	 */
	public char [][] getParameterizedTypeName(){
		StringBuffer buffer = new StringBuffer(5);
		buffer.append(this.token).append('<');
		for (int i = 0, length = this.typeArguments.length; i < length; i++) {
			if (i > 0) buffer.append(',');
			buffer.append(CharOperation.concatWith(this.typeArguments[i].getParameterizedTypeName(), '.'));
		}
		buffer.append('>');
		int nameLength = buffer.length();
		char[] name = new char[nameLength];
		buffer.getChars(0, nameLength, name, 0);
		int dim = this.dimensions;
		if (dim > 0) {
			char[] dimChars = new char[dim*2];
			for (int i = 0; i < dim; i++) {
				int index = i*2;
				dimChars[index] = '[';
				dimChars[index+1] = ']';
			}
			name = CharOperation.concat(name, dimChars);
		}
		return new char[][]{ name };
	}
	/**
     * @see org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference#getTypeBinding(org.eclipse.jdt.internal.compiler.lookup.Scope)
     */
    protected TypeBinding getTypeBinding(Scope scope) {
        return null; // not supported here - combined with resolveType(...)
    }

    /*
     * No need to check for reference to raw type per construction
     */
	private TypeBinding internalResolveType(Scope scope, ReferenceBinding enclosingType, boolean checkBounds) {
		// handle the error here
		this.constant = Constant.NotAConstant;
		if ((this.bits & ASTNode.DidResolve) != 0) { // is a shared type reference which was already resolved
			if (this.resolvedType != null) { // is a shared type reference which was already resolved
				if (this.resolvedType.isValidBinding()) {
					return this.resolvedType;
				} else {
					switch (this.resolvedType.problemId()) {
						case ProblemReasons.NotFound :
						case ProblemReasons.NotVisible :
						case ProblemReasons.InheritedNameHidesEnclosingName :
							TypeBinding type = this.resolvedType.closestMatch();
							return type;
						default :
							return null;
					}
				}
			}
		}
		this.bits |= ASTNode.DidResolve;
		TypeBinding type = internalResolveLeafType(scope, enclosingType, checkBounds);
		// handle three different outcomes:
		if (type == null) {
			this.resolvedType = createArrayType(scope, this.resolvedType);
			return null;							// no useful type, but still captured dimensions into this.resolvedType
		} else {
			type = createArrayType(scope, type);
			if (!this.resolvedType.isValidBinding())
				return type;						// found some error, but could recover useful type (like closestMatch)
			else 
				return this.resolvedType = type; 	// no complaint, keep fully resolved type (incl. dimensions)
		}
	}
	private TypeBinding internalResolveLeafType(Scope scope, ReferenceBinding enclosingType, boolean checkBounds) {
		ReferenceBinding currentType;
		if (enclosingType == null) {
			this.resolvedType = scope.getType(this.token);
			if (this.resolvedType.isValidBinding()) {
				currentType = (ReferenceBinding) this.resolvedType;
			} else {
				reportInvalidType(scope);
				switch (this.resolvedType.problemId()) {
					case ProblemReasons.NotFound :
					case ProblemReasons.NotVisible :
					case ProblemReasons.InheritedNameHidesEnclosingName :
						TypeBinding type = this.resolvedType.closestMatch();
						if (type instanceof ReferenceBinding) {
							currentType = (ReferenceBinding) type;
							break;
						}
						//$FALL-THROUGH$ - unable to complete type binding, but still resolve type arguments
					default :
						boolean isClassScope = scope.kind == Scope.CLASS_SCOPE;
					int argLength = this.typeArguments.length;
					for (int i = 0; i < argLength; i++) {
						TypeReference typeArgument = this.typeArguments[i];
						if (isClassScope) {
							typeArgument.resolveType((ClassScope) scope);
						} else {
							typeArgument.resolveType((BlockScope) scope, checkBounds);
						}
					}
					return null;
				}
				// be resilient, still attempt resolving arguments
			}
			enclosingType = currentType.enclosingType(); // if member type
			if (enclosingType != null) {
				enclosingType = currentType.isStatic()
					? (ReferenceBinding) scope.environment().convertToRawType(enclosingType, false /*do not force conversion of enclosing types*/)
					: scope.environment().convertToParameterizedType(enclosingType);
				currentType = scope.environment().createParameterizedType((ReferenceBinding) currentType.erasure(), null /* no arg */, enclosingType);
			}
		} else { // resolving member type (relatively to enclosingType)
			this.resolvedType = currentType = scope.getMemberType(this.token, enclosingType);
			if (!this.resolvedType.isValidBinding()) {
				scope.problemReporter().invalidEnclosingType(this, currentType, enclosingType);
				return null;
			}
			if (isTypeUseDeprecated(currentType, scope))
				scope.problemReporter().deprecatedType(currentType, this);
			ReferenceBinding currentEnclosing = currentType.enclosingType();
			if (currentEnclosing != null && currentEnclosing.erasure() != enclosingType.erasure()) {
				enclosingType = currentEnclosing; // inherited member type, leave it associated with its enclosing rather than subtype
			}
		}

		// check generic and arity
	    boolean isClassScope = scope.kind == Scope.CLASS_SCOPE;
	    TypeReference keep = null;
	    if (isClassScope) {
	    	keep = ((ClassScope) scope).superTypeReference;
	    	((ClassScope) scope).superTypeReference = null;
	    }
		int argLength = this.typeArguments.length;
		TypeBinding[] argTypes = new TypeBinding[argLength];
		boolean argHasError = false;
		ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
		for (int i = 0; i < argLength; i++) {
		    TypeReference typeArgument = this.typeArguments[i];
		    TypeBinding argType = isClassScope
				? typeArgument.resolveTypeArgument((ClassScope) scope, currentOriginal, i)
				: typeArgument.resolveTypeArgument((BlockScope) scope, currentOriginal, i);
		     if (argType == null) {
		         argHasError = true;
		     } else {
			    argTypes[i] = argType;
		     }
		}
		if (argHasError) {
			return null;
		}
		if (isClassScope) {
	    	((ClassScope) scope).superTypeReference = keep;
			if (((ClassScope) scope).detectHierarchyCycle(currentOriginal, this))
				return null;
		}

		final boolean isDiamond = (this.bits & ASTNode.IsDiamond) != 0;
		TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
		if (typeVariables == Binding.NO_TYPE_VARIABLES) { // non generic invoked with arguments
			boolean isCompliant15 = scope.compilerOptions().originalSourceLevel >= ClassFileConstants.JDK1_5;
			if ((currentOriginal.tagBits & TagBits.HasMissingType) == 0) {
				if (isCompliant15) { // below 1.5, already reported as syntax error
					this.resolvedType = currentType;
					scope.problemReporter().nonGenericTypeCannotBeParameterized(0, this, currentType, argTypes);
					return null;
				}
			}
			// resilience do not rebuild a parameterized type unless compliance is allowing it
			if (!isCompliant15) {
				if (!this.resolvedType.isValidBinding())
					return currentType;
				return this.resolvedType = currentType;
			}
			// if missing generic type, and compliance >= 1.5, then will rebuild a parameterized binding
		} else if (argLength != typeVariables.length) {
			if (!isDiamond) { // check arity, IsDiamond never set for 1.6-
				scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes);
				return null;
			} 
		} else if (!currentType.isStatic()) {
			ReferenceBinding actualEnclosing = currentType.enclosingType();
			if (actualEnclosing != null && actualEnclosing.isRawType()){
				scope.problemReporter().rawMemberTypeCannotBeParameterized(
						this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
				return null;
			}
		}

    	ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, enclosingType);
		// check argument type compatibility for non <> cases - <> case needs no bounds check, we will scream foul if needed during inference.
    	if (!isDiamond) {
    		if (checkBounds) // otherwise will do it in Scope.connectTypeVariables() or generic method resolution
    			parameterizedType.boundCheck(scope, this.typeArguments);
    		else
    			scope.deferBoundCheck(this);
    	}
		if (isTypeUseDeprecated(parameterizedType, scope))
			reportDeprecatedType(parameterizedType, scope);

		if (!this.resolvedType.isValidBinding()) {
			return parameterizedType;
		}
		return this.resolvedType = parameterizedType;
	}
	private TypeBinding createArrayType(Scope scope, TypeBinding type) {
		if (this.dimensions > 0) {
			if (this.dimensions > 255)
				scope.problemReporter().tooManyDimensions(this);
			return scope.createArrayType(type, this.dimensions);
		}
		return type;
	}

	public StringBuffer printExpression(int indent, StringBuffer output){
		output.append(this.token);
		output.append("<"); //$NON-NLS-1$
		int length = this.typeArguments.length;
		if (length > 0) {
			int max = length - 1;
			for (int i= 0; i < max; i++) {
				this.typeArguments[i].print(0, output);
				output.append(", ");//$NON-NLS-1$
			}
			this.typeArguments[max].print(0, output);
		}
		output.append(">"); //$NON-NLS-1$
		if ((this.bits & IsVarArgs) != 0) {
			for (int i= 0 ; i < this.dimensions - 1; i++) {
				output.append("[]"); //$NON-NLS-1$
			}
			output.append("..."); //$NON-NLS-1$
		} else {
			for (int i= 0 ; i < this.dimensions; i++) {
				output.append("[]"); //$NON-NLS-1$
			}
		}
		return output;
	}

	public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
	    return internalResolveType(scope, null, checkBounds);
	}

	public TypeBinding resolveType(ClassScope scope) {
	    return internalResolveType(scope, null, false /*no bounds check in classScope*/);
	}

	public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
	    return internalResolveType(scope, enclosingType, true/*check bounds*/);
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			for (int i = 0, max = this.typeArguments.length; i < max; i++) {
				this.typeArguments[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}

	public void traverse(ASTVisitor visitor, ClassScope scope) {
		if (visitor.visit(this, scope)) {
			for (int i = 0, max = this.typeArguments.length; i < max; i++) {
				this.typeArguments[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
}
