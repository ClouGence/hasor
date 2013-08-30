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
package org.eclipse.jdt.internal.compiler.parser;

import java.util.Set;

import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class RecoveredInitializer extends RecoveredField implements TerminalTokens {

	public RecoveredType[] localTypes;
	public int localTypeCount;

	public RecoveredBlock initializerBody;

	int pendingModifiers;
	int pendingModifersSourceStart = -1;
	RecoveredAnnotation[] pendingAnnotations;
	int pendingAnnotationCount;

public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance){
	this(fieldDeclaration, parent, bracketBalance, null);
}
public RecoveredInitializer(FieldDeclaration fieldDeclaration, RecoveredElement parent, int bracketBalance, Parser parser){
	super(fieldDeclaration, parent, bracketBalance, parser);
	this.foundOpeningBrace = true;
}
/*
 * Record a nested block declaration
 */
public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue) {

	/* default behavior is to delegate recording to parent if any,
	do not consider elements passed the known end (if set)
	it must be belonging to an enclosing element
	*/
	if (this.fieldDeclaration.declarationSourceEnd > 0
			&& nestedBlockDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd){
		resetPendingModifiers();
		if (this.parent == null) return this; // ignore
		return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
	}
	/* consider that if the opening brace was not found, it is there */
	if (!this.foundOpeningBrace){
		this.foundOpeningBrace = true;
		this.bracketBalance++;
	}
	this.initializerBody = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
	if (nestedBlockDeclaration.sourceEnd == 0) return this.initializerBody;
	return this;
}
/*
 * Record a field declaration (act like inside method body)
 */
public RecoveredElement add(FieldDeclaration newFieldDeclaration, int bracketBalanceValue) {
	resetPendingModifiers();

	/* local variables inside initializer can only be final and non void */
	char[][] fieldTypeName;
	if ((newFieldDeclaration.modifiers & ~ClassFileConstants.AccFinal) != 0 /* local var can only be final */
			|| (newFieldDeclaration.type == null) // initializer
			|| ((fieldTypeName = newFieldDeclaration.type.getTypeName()).length == 1 // non void
				&& CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName()))){
		if (this.parent == null) return this; // ignore
		this.updateSourceEndIfNecessary(previousAvailableLineEnd(newFieldDeclaration.declarationSourceStart - 1));
		return this.parent.add(newFieldDeclaration, bracketBalanceValue);
	}

	/* default behavior is to delegate recording to parent if any,
	do not consider elements passed the known end (if set)
	it must be belonging to an enclosing element
	*/
	if (this.fieldDeclaration.declarationSourceEnd > 0
			&& newFieldDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd){
		if (this.parent == null) return this; // ignore
		return this.parent.add(newFieldDeclaration, bracketBalanceValue);
	}
	// still inside initializer, treat as local variable
	return this; // ignore
}
/*
 * Record a local declaration - regular method should have been created a block body
 */
public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue) {

	/* do not consider a type starting passed the type end (if set)
		it must be belonging to an enclosing type */
	if (this.fieldDeclaration.declarationSourceEnd != 0
			&& localDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd){
		resetPendingModifiers();
		if (this.parent == null) return this; // ignore
		return this.parent.add(localDeclaration, bracketBalanceValue);
	}
	/* method body should have been created */
	Block block = new Block(0);
	block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
	RecoveredElement element = this.add(block, 1);
	if (this.initializerBody != null) {
		this.initializerBody.attachPendingModifiers(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();
	return element.add(localDeclaration, bracketBalanceValue);
}
/*
 * Record a statement - regular method should have been created a block body
 */
public RecoveredElement add(Statement statement, int bracketBalanceValue) {

	/* do not consider a statement starting passed the initializer end (if set)
		it must be belonging to an enclosing type */
	if (this.fieldDeclaration.declarationSourceEnd != 0
			&& statement.sourceStart > this.fieldDeclaration.declarationSourceEnd){
		resetPendingModifiers();
		if (this.parent == null) return this; // ignore
		return this.parent.add(statement, bracketBalanceValue);
	}
	/* initializer body should have been created */
	Block block = new Block(0);
	block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
	RecoveredElement element = this.add(block, 1);

	if (this.initializerBody != null) {
		this.initializerBody.attachPendingModifiers(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();

	return element.add(statement, bracketBalanceValue);
}
public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue) {

	/* do not consider a type starting passed the type end (if set)
		it must be belonging to an enclosing type */
	if (this.fieldDeclaration.declarationSourceEnd != 0
			&& typeDeclaration.declarationSourceStart > this.fieldDeclaration.declarationSourceEnd){
		resetPendingModifiers();
		if (this.parent == null) return this; // ignore
		return this.parent.add(typeDeclaration, bracketBalanceValue);
	}
	if ((typeDeclaration.bits & ASTNode.IsLocalType) != 0  || parser().methodRecoveryActivated || parser().statementRecoveryActivated){
		/* method body should have been created */
		Block block = new Block(0);
		block.sourceStart = ((Initializer)this.fieldDeclaration).sourceStart;
		RecoveredElement element = this.add(block, 1);
		if (this.initializerBody != null) {
			this.initializerBody.attachPendingModifiers(
					this.pendingAnnotations,
					this.pendingAnnotationCount,
					this.pendingModifiers,
					this.pendingModifersSourceStart);
		}
		resetPendingModifiers();
		return element.add(typeDeclaration, bracketBalanceValue);
	}
	if (this.localTypes == null) {
		this.localTypes = new RecoveredType[5];
		this.localTypeCount = 0;
	} else {
		if (this.localTypeCount == this.localTypes.length) {
			System.arraycopy(
				this.localTypes,
				0,
				(this.localTypes = new RecoveredType[2 * this.localTypeCount]),
				0,
				this.localTypeCount);
		}
	}
	RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
	this.localTypes[this.localTypeCount++] = element;

	if(this.pendingAnnotationCount > 0) {
		element.attach(
				this.pendingAnnotations,
				this.pendingAnnotationCount,
				this.pendingModifiers,
				this.pendingModifersSourceStart);
	}
	resetPendingModifiers();

	/* consider that if the opening brace was not found, it is there */
	if (!this.foundOpeningBrace){
		this.foundOpeningBrace = true;
		this.bracketBalance++;
	}
	return element;
}
public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
	if (this.pendingAnnotations == null) {
		this.pendingAnnotations = new RecoveredAnnotation[5];
		this.pendingAnnotationCount = 0;
	} else {
		if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
			System.arraycopy(
				this.pendingAnnotations,
				0,
				(this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount]),
				0,
				this.pendingAnnotationCount);
		}
	}

	RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);

	this.pendingAnnotations[this.pendingAnnotationCount++] = element;

	return element;
}
public void addModifier(int flag, int modifiersSourceStart) {
	this.pendingModifiers |= flag;

	if (this.pendingModifersSourceStart < 0) {
		this.pendingModifersSourceStart = modifiersSourceStart;
	}
}
public void resetPendingModifiers() {
	this.pendingAnnotations = null;
	this.pendingAnnotationCount = 0;
	this.pendingModifiers = 0;
	this.pendingModifersSourceStart = -1;
}
public String toString(int tab) {
	StringBuffer result = new StringBuffer(tabString(tab));
	result.append("Recovered initializer:\n"); //$NON-NLS-1$
	this.fieldDeclaration.print(tab + 1, result);
	if (this.annotations != null) {
		for (int i = 0; i < this.annotationCount; i++) {
			result.append("\n"); //$NON-NLS-1$
			result.append(this.annotations[i].toString(tab + 1));
		}
	}
	if (this.initializerBody != null) {
		result.append("\n"); //$NON-NLS-1$
		result.append(this.initializerBody.toString(tab + 1));
	}
	return result.toString();
}
public FieldDeclaration updatedFieldDeclaration(int depth, Set knownTypes){

	if (this.initializerBody != null){
		Block block = this.initializerBody.updatedBlock(depth, knownTypes);
		if (block != null){
			Initializer initializer = (Initializer) this.fieldDeclaration;
			initializer.block = block;

			if (initializer.declarationSourceEnd == 0) {
				initializer.declarationSourceEnd = block.sourceEnd;
				initializer.bodyEnd = block.sourceEnd;
			}
		}
		if (this.localTypeCount > 0) this.fieldDeclaration.bits |= ASTNode.HasLocalType;

	}
	if (this.fieldDeclaration.sourceEnd == 0){
		this.fieldDeclaration.sourceEnd = this.fieldDeclaration.declarationSourceEnd;
	}
	return this.fieldDeclaration;
}
/*
 * A closing brace got consumed, might have closed the current element,
 * in which case both the currentElement is exited
 */
public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd){
	if ((--this.bracketBalance <= 0) && (this.parent != null)){
		this.updateSourceEndIfNecessary(braceStart, braceEnd);
		return this.parent;
	}
	return this;
}
/*
 * An opening brace got consumed, might be the expected opening one of the current element,
 * in which case the bodyStart is updated.
 */
public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd){
	this.bracketBalance++;
	return this; // request to restart
}
/*
 * Update the declarationSourceEnd of the corresponding parse node
 */
public void updateSourceEndIfNecessary(int braceStart, int braceEnd){
	if (this.fieldDeclaration.declarationSourceEnd == 0) {
		Initializer initializer = (Initializer)this.fieldDeclaration;
		if(parser().rBraceSuccessorStart >= braceEnd) {
			if (initializer.bodyStart < parser().rBraceEnd) {
				initializer.declarationSourceEnd = parser().rBraceEnd;
			} else {
				initializer.declarationSourceEnd = initializer.bodyStart;
			}
			if (initializer.bodyStart < parser().rBraceStart) {
				initializer.bodyEnd = parser().rBraceStart;
			} else {
				initializer.bodyEnd = initializer.bodyStart;
			}
		} else {
			initializer.declarationSourceEnd = braceEnd;
			initializer.bodyEnd  = braceStart - 1;
		}
		if(initializer.block != null) {
			initializer.block.sourceEnd = initializer.declarationSourceEnd;
		}
	}
}
}
