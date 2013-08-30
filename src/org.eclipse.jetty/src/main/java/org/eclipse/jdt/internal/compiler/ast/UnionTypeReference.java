/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
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
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class UnionTypeReference extends TypeReference {
	public TypeReference[] typeReferences;

	public UnionTypeReference(TypeReference[] typeReferences) {
		this.bits |= ASTNode.IsUnionType;
		this.typeReferences = typeReferences;
		this.sourceStart = typeReferences[0].sourceStart;
		int length = typeReferences.length;
		this.sourceEnd = typeReferences[length - 1].sourceEnd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#copyDims(int)
	 */
	public TypeReference copyDims(int dim) {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#getLastToken()
	 */
	public char[] getLastToken() {
		return null;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference#getTypeBinding(org.eclipse.jdt.internal.compiler.lookup.Scope)
	 */
	protected TypeBinding getTypeBinding(Scope scope) {
		return null; // not supported here - combined with resolveType(...)
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#getTypeBinding(org.eclipse.jdt.internal.compiler.lookup.Scope)
	 */
	public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
		// return the lub (least upper bound of all type binding) 
		int length = this.typeReferences.length;
		TypeBinding[] allExceptionTypes = new TypeBinding[length];
		boolean hasError = false;
		for (int i = 0; i < length; i++) {
			TypeBinding exceptionType = this.typeReferences[i].resolveType(scope, checkBounds);
			if (exceptionType == null) {
				return null;
			}
			switch(exceptionType.kind()) {
				case Binding.PARAMETERIZED_TYPE :
					if (exceptionType.isBoundParameterizedType()) {
						hasError = true;
						scope.problemReporter().invalidParameterizedExceptionType(exceptionType, this.typeReferences[i]);
						// fall thru to create the variable - avoids additional errors because the variable is missing
					}
					break;
				case Binding.TYPE_PARAMETER :
					scope.problemReporter().invalidTypeVariableAsException(exceptionType, this.typeReferences[i]);
					hasError = true;
					// fall thru to create the variable - avoids additional errors because the variable is missing
					break;
			}
			if (exceptionType.findSuperTypeOriginatingFrom(TypeIds.T_JavaLangThrowable, true) == null
					&& exceptionType.isValidBinding()) {
				scope.problemReporter().cannotThrowType(this.typeReferences[i], exceptionType);
				hasError = true;
			}
			allExceptionTypes[i] = exceptionType;
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=340486, ensure types are of union type.
			for (int j = 0; j < i; j++) {
				if (allExceptionTypes[j].isCompatibleWith(exceptionType)) {
					scope.problemReporter().wrongSequenceOfExceptionTypes(
							this.typeReferences[j],
							allExceptionTypes[j],
							exceptionType);
					hasError = true;
				} else if (exceptionType.isCompatibleWith(allExceptionTypes[j])) {
					scope.problemReporter().wrongSequenceOfExceptionTypes(
							this.typeReferences[i],
							exceptionType,
							allExceptionTypes[j]);
					hasError = true;
				}
			}
		}
		if (hasError) {
			return null;
		}
		// compute lub
		return (this.resolvedType = scope.lowerUpperBound(allExceptionTypes));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#getTypeName()
	 */
	public char[][] getTypeName() {
		// we need to keep a return value that is a char[][]
		return this.typeReferences[0].getTypeName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			int length = this.typeReferences == null ? 0 : this.typeReferences.length;
			for (int i = 0; i < length; i++) {
				this.typeReferences[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeReference#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.ClassScope)
	 */
	public void traverse(ASTVisitor visitor, ClassScope scope) {
		if (visitor.visit(this, scope)) {
			int length = this.typeReferences == null ? 0 : this.typeReferences.length;
			for (int i = 0; i < length; i++) {
				this.typeReferences[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.compiler.ast.Expression#printExpression(int, java.lang.StringBuffer)
	 */
	public StringBuffer printExpression(int indent, StringBuffer output) {
		int length = this.typeReferences == null ? 0 : this.typeReferences.length;
		printIndent(indent, output);
		for (int i = 0; i < length; i++) {
			this.typeReferences[i].printExpression(0, output);
			if (i != length - 1) {
				output.append(" | "); //$NON-NLS-1$
			}
		}
		return output;
	}

}
