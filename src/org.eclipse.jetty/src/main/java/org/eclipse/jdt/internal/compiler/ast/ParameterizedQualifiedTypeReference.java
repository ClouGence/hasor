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
public class ParameterizedQualifiedTypeReference extends ArrayQualifiedTypeReference {

	public TypeReference[][] typeArguments;

	/**
	 * @param tokens
	 * @param dim
	 * @param positions
	 */
	public ParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, int dim, long[] positions) {

		super(tokens, dim, positions);
		this.typeArguments = typeArguments;
	}
	public void checkBounds(Scope scope) {
		if (this.resolvedType == null) return;

		checkBounds(
			(ReferenceBinding) this.resolvedType.leafComponentType(),
			scope,
			this.typeArguments.length - 1);
	}
	public void checkBounds(ReferenceBinding type, Scope scope, int index) {
		// recurse on enclosing type if any, and assuming explictly  part of the reference (index>0)
		if (index > 0 &&  type.enclosingType() != null) {
			checkBounds(type.enclosingType(), scope, index - 1);
		}
		if (type.isParameterizedTypeWithActualArguments()) {
			ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding) type;
			ReferenceBinding currentType = parameterizedType.genericType();
			TypeVariableBinding[] typeVariables = currentType.typeVariables();
			if (typeVariables != null) { // argTypes may be null in error cases
				parameterizedType.boundCheck(scope, this.typeArguments[index]);
			}
		}
	}
	public TypeReference copyDims(int dim){
		return new ParameterizedQualifiedTypeReference(this.tokens, this.typeArguments, dim, this.sourcePositions);
	}

	/**
	 * @return char[][]
	 */
	public char [][] getParameterizedTypeName(){
		int length = this.tokens.length;
		char[][] qParamName = new char[length][];
		for (int i = 0; i < length; i++) {
			TypeReference[] arguments = this.typeArguments[i];
			if (arguments == null) {
				qParamName[i] = this.tokens[i];
			} else {
				StringBuffer buffer = new StringBuffer(5);
				buffer.append(this.tokens[i]);
				buffer.append('<');
				for (int j = 0, argLength =arguments.length; j < argLength; j++) {
					if (j > 0) buffer.append(',');
					buffer.append(CharOperation.concatWith(arguments[j].getParameterizedTypeName(), '.'));
				}
				buffer.append('>');
				int nameLength = buffer.length();
				qParamName[i] = new char[nameLength];
				buffer.getChars(0, nameLength, qParamName[i], 0);
			}
		}
		int dim = this.dimensions;
		if (dim > 0) {
			char[] dimChars = new char[dim*2];
			for (int i = 0; i < dim; i++) {
				int index = i*2;
				dimChars[index] = '[';
				dimChars[index+1] = ']';
			}
			qParamName[length-1] = CharOperation.concat(qParamName[length-1], dimChars);
		}
		return qParamName;
	}

	/* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference#getTypeBinding(org.eclipse.jdt.internal.compiler.lookup.Scope)
     */
    protected TypeBinding getTypeBinding(Scope scope) {
        return null; // not supported here - combined with resolveType(...)
    }

    /*
     * No need to check for reference to raw type per construction
     */
	private TypeBinding internalResolveType(Scope scope, boolean checkBounds) {
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
		TypeBinding type = internalResolveLeafType(scope, checkBounds);
		createArrayType(scope);
		return type == null ? type : this.resolvedType;
	}
	private TypeBinding internalResolveLeafType(Scope scope, boolean checkBounds) {
		boolean isClassScope = scope.kind == Scope.CLASS_SCOPE;
		Binding binding = scope.getPackage(this.tokens);
		if (binding != null && !binding.isValidBinding()) {
			this.resolvedType = (ReferenceBinding) binding;
			reportInvalidType(scope);
			// be resilient, still attempt resolving arguments
			for (int i = 0, max = this.tokens.length; i < max; i++) {
				TypeReference[] args = this.typeArguments[i];
				if (args != null) {
					int argLength = args.length;
					for (int j = 0; j < argLength; j++) {
						TypeReference typeArgument = args[j];
						if (isClassScope) {
							typeArgument.resolveType((ClassScope) scope);
						} else {
							typeArgument.resolveType((BlockScope) scope, checkBounds);
						}
					}
				}
			}
			return null;
		}

		PackageBinding packageBinding = binding == null ? null : (PackageBinding) binding;
		boolean typeIsConsistent = true;
		ReferenceBinding qualifyingType = null;
		for (int i = packageBinding == null ? 0 : packageBinding.compoundName.length, max = this.tokens.length; i < max; i++) {
			findNextTypeBinding(i, scope, packageBinding);
			if (!(this.resolvedType.isValidBinding())) {
				reportInvalidType(scope);
				// be resilient, still attempt resolving arguments
				for (int j = i; j < max; j++) {
				    TypeReference[] args = this.typeArguments[j];
				    if (args != null) {
						int argLength = args.length;
						for (int k = 0; k < argLength; k++) {
						    TypeReference typeArgument = args[k];
						    if (isClassScope) {
						    	typeArgument.resolveType((ClassScope) scope);
						    } else {
						    	typeArgument.resolveType((BlockScope) scope);
						    }
						}
				    }
				}
				return null;
			}
			ReferenceBinding currentType = (ReferenceBinding) this.resolvedType;
			if (qualifyingType == null) {
				qualifyingType = currentType.enclosingType(); // if member type
				if (qualifyingType != null) {
					qualifyingType = currentType.isStatic()
						? (ReferenceBinding) scope.environment().convertToRawType(qualifyingType, false /*do not force conversion of enclosing types*/)
						: scope.environment().convertToParameterizedType(qualifyingType);
				}
			} else {
				if (typeIsConsistent && currentType.isStatic()
						&& (qualifyingType.isParameterizedTypeWithActualArguments() || qualifyingType.isGenericType())) {
					scope.problemReporter().staticMemberOfParameterizedType(this, scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifyingType), i);
					typeIsConsistent = false;
				}
				ReferenceBinding enclosingType = currentType.enclosingType();
				if (enclosingType != null && enclosingType.erasure() != qualifyingType.erasure()) { // qualifier != declaring/enclosing
					qualifyingType = enclosingType; // inherited member type, leave it associated with its enclosing rather than subtype
				}
			}

			// check generic and arity
		    TypeReference[] args = this.typeArguments[i];
		    if (args != null) {
			    TypeReference keep = null;
			    if (isClassScope) {
			    	keep = ((ClassScope) scope).superTypeReference;
			    	((ClassScope) scope).superTypeReference = null;
			    }
				int argLength = args.length;
				boolean isDiamond = argLength == 0 && (i == (max -1)) && ((this.bits & ASTNode.IsDiamond) != 0);
				TypeBinding[] argTypes = new TypeBinding[argLength];
				boolean argHasError = false;
				ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
				for (int j = 0; j < argLength; j++) {
				    TypeReference arg = args[j];
				    TypeBinding argType = isClassScope
						? arg.resolveTypeArgument((ClassScope) scope, currentOriginal, j)
						: arg.resolveTypeArgument((BlockScope) scope, currentOriginal, j);
					if (argType == null) {
						argHasError = true;
					} else {
						argTypes[j] = argType;
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

			    TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
				if (typeVariables == Binding.NO_TYPE_VARIABLES) { // check generic
					if (scope.compilerOptions().originalSourceLevel >= ClassFileConstants.JDK1_5) { // below 1.5, already reported as syntax error
						scope.problemReporter().nonGenericTypeCannotBeParameterized(i, this, currentType, argTypes);
						return null;
					}
					this.resolvedType =  (qualifyingType != null && qualifyingType.isParameterizedType())
						? scope.environment().createParameterizedType(currentOriginal, null, qualifyingType)
						: currentType;
					return this.resolvedType;
				} else if (argLength != typeVariables.length) {
					if (!isDiamond) { // check arity
						scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes, i);
						return null;
					}
				}
				// check parameterizing non-static member type of raw type
				if (typeIsConsistent && !currentType.isStatic()) {
					ReferenceBinding actualEnclosing = currentType.enclosingType();
					if (actualEnclosing != null && actualEnclosing.isRawType()) {
						scope.problemReporter().rawMemberTypeCannotBeParameterized(
								this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
						typeIsConsistent = false;
					}
				}
				ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, qualifyingType);
				// check argument type compatibility for non <> cases - <> case needs no bounds check, we will scream foul if needed during inference.
				if (!isDiamond) {
					if (checkBounds) // otherwise will do it in Scope.connectTypeVariables() or generic method resolution
						parameterizedType.boundCheck(scope, args);
					else
						scope.deferBoundCheck(this);
				}
				qualifyingType = parameterizedType;
		    } else {
				ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
				if (isClassScope)
					if (((ClassScope) scope).detectHierarchyCycle(currentOriginal, this))
						return null;
				if (currentOriginal.isGenericType()) {
	   			    if (typeIsConsistent && qualifyingType != null && qualifyingType.isParameterizedType()) {
						scope.problemReporter().parameterizedMemberTypeMissingArguments(this, scope.environment().createParameterizedType(currentOriginal, null, qualifyingType), i);
						typeIsConsistent = false;
					}
	   			    qualifyingType = scope.environment().createRawType(currentOriginal, qualifyingType); // raw type
				} else {
					qualifyingType = (qualifyingType != null && qualifyingType.isParameterizedType())
													? scope.environment().createParameterizedType(currentOriginal, null, qualifyingType)
													: currentType;
				}
			}
			if (isTypeUseDeprecated(qualifyingType, scope))
				reportDeprecatedType(qualifyingType, scope, i);
			this.resolvedType = qualifyingType;
		}
		return this.resolvedType;
	}
	private void createArrayType(Scope scope) {
		if (this.dimensions > 0) {
			if (this.dimensions > 255)
				scope.problemReporter().tooManyDimensions(this);
			this.resolvedType = scope.createArrayType(this.resolvedType, this.dimensions);
		}
	}

	public StringBuffer printExpression(int indent, StringBuffer output) {
		int length = this.tokens.length;
		for (int i = 0; i < length - 1; i++) {
			output.append(this.tokens[i]);
			TypeReference[] typeArgument = this.typeArguments[i];
			if (typeArgument != null) {
				output.append('<');
				int typeArgumentLength = typeArgument.length;
				if (typeArgumentLength > 0) {
					int max = typeArgumentLength - 1;
					for (int j = 0; j < max; j++) {
						typeArgument[j].print(0, output);
						output.append(", ");//$NON-NLS-1$
					}
					typeArgument[max].print(0, output);
				}
				output.append('>');
			}
			output.append('.');
		}
		output.append(this.tokens[length - 1]);
		TypeReference[] typeArgument = this.typeArguments[length - 1];
		if (typeArgument != null) {
			output.append('<');
			int typeArgumentLength = typeArgument.length;
			if (typeArgumentLength > 0) {
				int max = typeArgumentLength - 1;
				for (int j = 0; j < max; j++) {
					typeArgument[j].print(0, output);
					output.append(", ");//$NON-NLS-1$
				}
				typeArgument[max].print(0, output);
			}
			output.append('>');
		}
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
	    return internalResolveType(scope, checkBounds);
	}
	public TypeBinding resolveType(ClassScope scope) {
	    return internalResolveType(scope, false);
	}
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			for (int i = 0, max = this.typeArguments.length; i < max; i++) {
				if (this.typeArguments[i] != null) {
					for (int j = 0, max2 = this.typeArguments[i].length; j < max2; j++) {
						this.typeArguments[i][j].traverse(visitor, scope);
					}
				}
			}
		}
		visitor.endVisit(this, scope);
	}

	public void traverse(ASTVisitor visitor, ClassScope scope) {
		if (visitor.visit(this, scope)) {
			for (int i = 0, max = this.typeArguments.length; i < max; i++) {
				if (this.typeArguments[i] != null) {
					for (int j = 0, max2 = this.typeArguments[i].length; j < max2; j++) {
						this.typeArguments[i][j].traverse(visitor, scope);
					}
				}
			}
		}
		visitor.endVisit(this, scope);
	}

}
