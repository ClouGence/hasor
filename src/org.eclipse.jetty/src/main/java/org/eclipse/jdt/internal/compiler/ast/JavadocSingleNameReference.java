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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class JavadocSingleNameReference extends SingleNameReference {

	public int tagSourceStart, tagSourceEnd;

	public JavadocSingleNameReference(char[] source, long pos, int tagStart, int tagEnd) {
		super(source, pos);
		this.tagSourceStart = tagStart;
		this.tagSourceEnd = tagEnd;
		this.bits |= InsideJavadoc;
	}

	public void resolve(BlockScope scope) {
		resolve(scope, true, scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference);
	}

	/**
	 * Resolve without warnings
	 */
	public void resolve(BlockScope scope, boolean warn, boolean considerParamRefAsUsage) {

		LocalVariableBinding variableBinding = scope.findVariable(this.token);
		if (variableBinding != null && variableBinding.isValidBinding() && ((variableBinding.tagBits & TagBits.IsArgument) != 0)) {
			this.binding = variableBinding;
			if (considerParamRefAsUsage) {
				variableBinding.useFlag = LocalVariableBinding.USED;
			}
			return;
		}
		if (warn) {
			try {
				MethodScope methScope = (MethodScope) scope;
				scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, methScope.referenceMethod().modifiers);
			}
			catch (Exception e) {
				scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, -1);
			}
		}
	}

	/* (non-Javadoc)
	 * Redefine to capture javadoc specific signatures
	 * @see org.eclipse.jdt.internal.compiler.ast.ASTNode#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
	/* (non-Javadoc)
	 * Redefine to capture javadoc specific signatures
	 * @see org.eclipse.jdt.internal.compiler.ast.ASTNode#traverse(org.eclipse.jdt.internal.compiler.ASTVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(ASTVisitor visitor, ClassScope scope) {
		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
}
