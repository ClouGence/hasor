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
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.*;

/**
 * Node to represent Wildcard
 */
public class Wildcard extends SingleTypeReference {

    public static final int UNBOUND = 0;
    public static final int EXTENDS = 1;
    public static final int SUPER = 2;

	public TypeReference bound;
	public int kind;

	public Wildcard(int kind) {
		super(WILDCARD_NAME, 0);
		this.kind = kind;
	}

	public char [][] getParameterizedTypeName() {
		switch (this.kind) {
			case Wildcard.UNBOUND :
				return new char[][] { WILDCARD_NAME };
			case Wildcard.EXTENDS :
				return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
			default: // SUPER
				return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_SUPER, CharOperation.concatWith(this.bound.getParameterizedTypeName(), '.')) };
		}
	}

	public char [][] getTypeName() {
		switch (this.kind) {
			case Wildcard.UNBOUND :
				return new char[][] { WILDCARD_NAME };
			case Wildcard.EXTENDS :
				return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_EXTENDS, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
			default: // SUPER
				return new char[][] { CharOperation.concat(WILDCARD_NAME, WILDCARD_SUPER, CharOperation.concatWith(this.bound.getTypeName(), '.')) };
		}
	}

	private TypeBinding internalResolveType(Scope scope, ReferenceBinding genericType, int rank) {
		TypeBinding boundType = null;
		if (this.bound != null) {
			boundType = scope.kind == Scope.CLASS_SCOPE
					? this.bound.resolveType((ClassScope)scope)
					: this.bound.resolveType((BlockScope)scope, true /* check bounds*/);

			if (boundType == null) {
				return null;
			}
		}
		WildcardBinding wildcard = scope.environment().createWildcard(genericType, rank, boundType, null /*no extra bound*/, this.kind);
		return this.resolvedType = wildcard;
	}

	public StringBuffer printExpression(int indent, StringBuffer output){
		switch (this.kind) {
			case Wildcard.UNBOUND :
				output.append(WILDCARD_NAME);
				break;
			case Wildcard.EXTENDS :
				output.append(WILDCARD_NAME).append(WILDCARD_EXTENDS);
				this.bound.printExpression(0, output);
				break;
			default: // SUPER
			output.append(WILDCARD_NAME).append(WILDCARD_SUPER);
			this.bound.printExpression(0, output);
			break;
		}
		return output;
	}

	// only invoked for improving resilience when unable to bind generic type from parameterized reference
	public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
		if (this.bound != null) {
			this.bound.resolveType(scope, checkBounds);
		}
		return null;
	}
	// only invoked for improving resilience when unable to bind generic type from parameterized reference
	public TypeBinding resolveType(ClassScope scope) {
		if (this.bound != null) {
			this.bound.resolveType(scope);
		}
		return null;
	}
	public TypeBinding resolveTypeArgument(BlockScope blockScope, ReferenceBinding genericType, int rank) {
	    return internalResolveType(blockScope, genericType, rank);
	}

	public TypeBinding resolveTypeArgument(ClassScope classScope, ReferenceBinding genericType, int rank) {
	    return internalResolveType(classScope, genericType, rank);
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.bound != null) {
				this.bound.traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}

	public void traverse(ASTVisitor visitor, ClassScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.bound != null) {
				this.bound.traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
}
