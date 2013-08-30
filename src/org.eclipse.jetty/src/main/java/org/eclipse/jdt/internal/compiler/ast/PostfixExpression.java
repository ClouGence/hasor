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
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class PostfixExpression extends CompoundAssignment {

public PostfixExpression(Expression lhs, Expression expression, int operator, int pos) {
	super(lhs, expression, operator, pos);
	this.sourceStart = lhs.sourceStart;
	this.sourceEnd = pos;
}
public boolean checkCastCompatibility() {
	return false;
}
/**
 * Code generation for PostfixExpression
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 * @param valueRequired boolean
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	// various scenarii are possible, setting an array reference,
	// a field reference, a blank final field reference, a field of an enclosing instance or
	// just a local variable.

	int pc = codeStream.position;
	 ((Reference) this.lhs).generatePostIncrement(currentScope, codeStream, this, valueRequired);
	if (valueRequired) {
		codeStream.generateImplicitConversion(this.implicitConversion);
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

public String operatorToString() {
	switch (this.operator) {
		case PLUS :
			return "++"; //$NON-NLS-1$
		case MINUS :
			return "--"; //$NON-NLS-1$
	}
	return "unknown operator"; //$NON-NLS-1$
}

public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
	return this.lhs.printExpression(indent, output).append(' ').append(operatorToString());
}

public boolean restrainUsageToNumericTypes() {
	return true;
}

public void traverse(ASTVisitor visitor, BlockScope scope) {

	if (visitor.visit(this, scope)) {
		this.lhs.traverse(visitor, scope);
	}
	visitor.endVisit(this, scope);
}
}
