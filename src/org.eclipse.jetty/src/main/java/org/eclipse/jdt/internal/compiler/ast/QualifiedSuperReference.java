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

public class QualifiedSuperReference extends QualifiedThisReference {

public QualifiedSuperReference(TypeReference name, int pos, int sourceEnd) {
	super(name, pos, sourceEnd);
}

public boolean isSuper() {
	return true;
}

public boolean isThis() {
	return false;
}

public StringBuffer printExpression(int indent, StringBuffer output) {
	return this.qualification.print(0, output).append(".super"); //$NON-NLS-1$
}

public TypeBinding resolveType(BlockScope scope) {
	if ((this.bits & ParenthesizedMASK) != 0) {
		scope.problemReporter().invalidParenthesizedExpression(this);
		return null;
	}
	super.resolveType(scope);
	if (this.currentCompatibleType == null)
		return null; // error case

	if (this.currentCompatibleType.id == T_JavaLangObject) {
		scope.problemReporter().cannotUseSuperInJavaLangObject(this);
		return null;
	}
	return this.resolvedType = this.currentCompatibleType.superclass();
}

public void traverse(
	ASTVisitor visitor,
	BlockScope blockScope) {

	if (visitor.visit(this, blockScope)) {
		this.qualification.traverse(visitor, blockScope);
	}
	visitor.endVisit(this, blockScope);
}
public void traverse(
		ASTVisitor visitor,
		ClassScope blockScope) {

	if (visitor.visit(this, blockScope)) {
		this.qualification.traverse(visitor, blockScope);
	}
	visitor.endVisit(this, blockScope);
}
}
