/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 185682 - Increment/decrement operators mark local variables as read
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class JavadocFieldReference extends FieldReference {

	public int tagSourceStart, tagSourceEnd;
	public int tagValue;
	public MethodBinding methodBinding;

	public JavadocFieldReference(char[] source, long pos) {
		super(source, pos);
		this.bits |= InsideJavadoc;
	}

	/*
	public Binding getBinding() {
		if (this.methodBinding != null) {
			return this.methodBinding;
		}
		return this.binding;
	}
	*/

	/*
	 * Resolves type on a Block or Class scope.
	 */
	protected TypeBinding internalResolveType(Scope scope) {

		this.constant = Constant.NotAConstant;
		if (this.receiver == null) {
			this.actualReceiverType = scope.enclosingReceiverType();
		} else if (scope.kind == Scope.CLASS_SCOPE) {
			this.actualReceiverType = this.receiver.resolveType((ClassScope) scope);
		} else {
			this.actualReceiverType = this.receiver.resolveType((BlockScope)scope);
		}
		if (this.actualReceiverType == null) {
			return null;
		}

		Binding fieldBinding = (this.receiver != null && this.receiver.isThis())
			? scope.classScope().getBinding(this.token, this.bits & RestrictiveFlagMASK, this, true /*resolve*/)
			: scope.getField(this.actualReceiverType, this.token, this);
		if (!fieldBinding.isValidBinding()) {
			// implicit lookup may discover issues due to static/constructor contexts. javadoc must be resilient
			switch (fieldBinding.problemId()) {
				case ProblemReasons.NonStaticReferenceInConstructorInvocation:
				case ProblemReasons.NonStaticReferenceInStaticContext:
				case ProblemReasons.InheritedNameHidesEnclosingName :
					FieldBinding closestMatch = ((ProblemFieldBinding)fieldBinding).closestMatch;
					if (closestMatch != null) {
						fieldBinding = closestMatch; // ignore problem if can reach target field through it
					}
			}
		}
		// When there's no valid field binding, try to resolve possible method reference without parenthesis
		if (!fieldBinding.isValidBinding() || !(fieldBinding instanceof FieldBinding)) {
			if (this.receiver.resolvedType instanceof ProblemReferenceBinding) {
				// problem already got signaled on receiver, do not report secondary problem
				return null;
			}
			if (this.actualReceiverType instanceof ReferenceBinding) {
				ReferenceBinding refBinding = (ReferenceBinding) this.actualReceiverType;
				char[] selector = this.token;
				MethodBinding possibleMethod = null;
				if (CharOperation.equals(this.actualReceiverType.sourceName(), selector)) {
					possibleMethod = scope.getConstructor(refBinding, Binding.NO_TYPES, this);
				} else {
					possibleMethod = this.receiver.isThis()
						? scope.getImplicitMethod(selector, Binding.NO_TYPES, this)
						: scope.getMethod(refBinding, selector, Binding.NO_TYPES, this);
				}
				if (possibleMethod.isValidBinding()) {
					this.methodBinding = possibleMethod;
				} else {
					ProblemMethodBinding problemMethodBinding = (ProblemMethodBinding) possibleMethod;
					if (problemMethodBinding.closestMatch == null) {
						if (fieldBinding.isValidBinding()) {
							// When the binding is not on a field (e.g. local variable), we need to create a problem field binding to report the correct problem
							// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=254825
							fieldBinding = new ProblemFieldBinding(refBinding, fieldBinding.readableName(), ProblemReasons.NotFound);
						}
						scope.problemReporter().javadocInvalidField(this, fieldBinding, this.actualReceiverType, scope.getDeclarationModifiers());
					} else {
						this.methodBinding = problemMethodBinding.closestMatch;
					}
				}
			}
			return null;
		}
		this.binding = (FieldBinding) fieldBinding;

		if (isFieldUseDeprecated(this.binding, scope, this.bits)) {
			scope.problemReporter().javadocDeprecatedField(this.binding, this, scope.getDeclarationModifiers());
		}
		return this.resolvedType = this.binding.type;
	}

	public boolean isSuperAccess() {
		return (this.bits & ASTNode.SuperAccess) != 0;
	}

	public StringBuffer printExpression(int indent, StringBuffer output) {

		if (this.receiver != null) {
			this.receiver.printExpression(0, output);
		}
		output.append('#').append(this.token);
		return output;
	}

	public TypeBinding resolveType(BlockScope scope) {
		return internalResolveType(scope);
	}

	public TypeBinding resolveType(ClassScope scope) {
		return internalResolveType(scope);
	}

	/* (non-Javadoc)
	 * Redefine to capture javadoc specific signatures
	 * @see org.eclipse.jdt.internal.compiler.ast.ASTNode#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(ASTVisitor visitor, BlockScope scope) {

		if (visitor.visit(this, scope)) {
			if (this.receiver != null) {
				this.receiver.traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
	public void traverse(ASTVisitor visitor, ClassScope scope) {

		if (visitor.visit(this, scope)) {
			if (this.receiver != null) {
				this.receiver.traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
}
