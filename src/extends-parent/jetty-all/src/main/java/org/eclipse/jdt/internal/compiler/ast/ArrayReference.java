/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class ArrayReference extends Reference {

	public Expression receiver;
	public Expression position;

public ArrayReference(Expression rec, Expression pos) {
	this.receiver = rec;
	this.position = pos;
	this.sourceStart = rec.sourceStart;
}

public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean compoundAssignment) {
	// TODO (maxime) optimization: unconditionalInits is applied to all existing calls
	if (assignment.expression == null) {
		return analyseCode(currentScope, flowContext, flowInfo);
	}
	return assignment
		.expression
		.analyseCode(
			currentScope,
			flowContext,
			analyseCode(currentScope, flowContext, flowInfo).unconditionalInits());
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	this.receiver.checkNPE(currentScope, flowContext, flowInfo);
	flowInfo = this.receiver.analyseCode(currentScope, flowContext, flowInfo);
	return this.position.analyseCode(currentScope, flowContext, flowInfo);
}

public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
	int pc = codeStream.position;
	this.receiver.generateCode(currentScope, codeStream, true);
	if (this.receiver instanceof CastExpression	// ((type[])null)[0]
			&& ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL){
		codeStream.checkcast(this.receiver.resolvedType);
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
	this.position.generateCode(currentScope, codeStream, true);
	assignment.expression.generateCode(currentScope, codeStream, true);
	codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
	if (valueRequired) {
		codeStream.generateImplicitConversion(assignment.implicitConversion);
	}
}

/**
 * Code generation for a array reference
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	int pc = codeStream.position;
	this.receiver.generateCode(currentScope, codeStream, true);
	if (this.receiver instanceof CastExpression	// ((type[])null)[0]
			&& ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL){
		codeStream.checkcast(this.receiver.resolvedType);
	}
	this.position.generateCode(currentScope, codeStream, true);
	codeStream.arrayAt(this.resolvedType.id);
	// Generating code for the potential runtime type checking
	if (valueRequired) {
		codeStream.generateImplicitConversion(this.implicitConversion);
	} else {
		boolean isUnboxing = (this.implicitConversion & TypeIds.UNBOXING) != 0;
		// conversion only generated if unboxing
		if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion);
		switch (isUnboxing ? postConversionType(currentScope).id : this.resolvedType.id) {
			case T_long :
			case T_double :
				codeStream.pop2();
				break;
			default :
				codeStream.pop();
		}
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
	this.receiver.generateCode(currentScope, codeStream, true);
	if (this.receiver instanceof CastExpression	// ((type[])null)[0]
			&& ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL){
		codeStream.checkcast(this.receiver.resolvedType);
	}
	this.position.generateCode(currentScope, codeStream, true);
	codeStream.dup2();
	codeStream.arrayAt(this.resolvedType.id);
	int operationTypeID;
	switch(operationTypeID = (this.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4) {
		case T_JavaLangString :
		case T_JavaLangObject :
		case T_undefined :
			codeStream.generateStringConcatenationAppend(currentScope, null, expression);
			break;
		default :
			// promote the array reference to the suitable operation type
			codeStream.generateImplicitConversion(this.implicitConversion);
			// generate the increment value (will by itself  be promoted to the operation value)
			if (expression == IntLiteral.One) { // prefix operation
				codeStream.generateConstant(expression.constant, this.implicitConversion);
			} else {
				expression.generateCode(currentScope, codeStream, true);
			}
			// perform the operation
			codeStream.sendOperator(operator, operationTypeID);
			// cast the value back to the array reference type
			codeStream.generateImplicitConversion(assignmentImplicitConversion);
	}
	codeStream.arrayAtPut(this.resolvedType.id, valueRequired);
}

public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
	this.receiver.generateCode(currentScope, codeStream, true);
	if (this.receiver instanceof CastExpression	// ((type[])null)[0]
			&& ((CastExpression)this.receiver).innermostCastedExpression().resolvedType == TypeBinding.NULL){
		codeStream.checkcast(this.receiver.resolvedType);
	}
	this.position.generateCode(currentScope, codeStream, true);
	codeStream.dup2();
	codeStream.arrayAt(this.resolvedType.id);
	if (valueRequired) {
		switch(this.resolvedType.id) {
			case TypeIds.T_long :
			case TypeIds.T_double :
				codeStream.dup2_x2();
				break;
			default :
				codeStream.dup_x2();
				break;
		}
	}
	codeStream.generateImplicitConversion(this.implicitConversion);
	codeStream.generateConstant(
		postIncrement.expression.constant,
		this.implicitConversion);
	codeStream.sendOperator(postIncrement.operator, this.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
	codeStream.generateImplicitConversion(
		postIncrement.preAssignImplicitConversion);
	codeStream.arrayAtPut(this.resolvedType.id, false);
}

public int nullStatus(FlowInfo flowInfo) {
	return FlowInfo.UNKNOWN;
}

public StringBuffer printExpression(int indent, StringBuffer output) {
	this.receiver.printExpression(0, output).append('[');
	return this.position.printExpression(0, output).append(']');
}

public TypeBinding resolveType(BlockScope scope) {
	this.constant = Constant.NotAConstant;
	if (this.receiver instanceof CastExpression	// no cast check for ((type[])null)[0]
			&& ((CastExpression)this.receiver).innermostCastedExpression() instanceof NullLiteral) {
		this.receiver.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
	}
	TypeBinding arrayType = this.receiver.resolveType(scope);
	if (arrayType != null) {
		this.receiver.computeConversion(scope, arrayType, arrayType);
		if (arrayType.isArrayType()) {
			TypeBinding elementType = ((ArrayBinding) arrayType).elementsType();
			this.resolvedType = ((this.bits & ASTNode.IsStrictlyAssigned) == 0) ? elementType.capture(scope, this.sourceEnd) : elementType;
		} else {
			scope.problemReporter().referenceMustBeArrayTypeAt(arrayType, this);
		}
	}
	TypeBinding positionType = this.position.resolveTypeExpecting(scope, TypeBinding.INT);
	if (positionType != null) {
		this.position.computeConversion(scope, TypeBinding.INT, positionType);
	}
	return this.resolvedType;
}

public void traverse(ASTVisitor visitor, BlockScope scope) {
	if (visitor.visit(this, scope)) {
		this.receiver.traverse(visitor, scope);
		this.position.traverse(visitor, scope);
	}
	visitor.endVisit(this, scope);
}
}
