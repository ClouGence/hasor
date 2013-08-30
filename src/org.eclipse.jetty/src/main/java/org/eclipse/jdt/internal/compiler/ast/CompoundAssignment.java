/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class CompoundAssignment extends Assignment implements OperatorIds {
	public int operator;
	public int preAssignImplicitConversion;

	//  var op exp is equivalent to var = (varType) var op exp
	// assignmentImplicitConversion stores the cast needed for the assignment

	public CompoundAssignment(Expression lhs, Expression expression,int operator, int sourceEnd) {
		//lhs is always a reference by construction ,
		//but is build as an expression ==> the checkcast cannot fail

		super(lhs, expression, sourceEnd);
		lhs.bits &= ~IsStrictlyAssigned; // tag lhs as NON assigned - it is also a read access
		lhs.bits |= IsCompoundAssigned; // tag lhs as assigned by compound
		this.operator = operator ;
	}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext,
		FlowInfo flowInfo) {
	// record setting a variable: various scenarii are possible, setting an array reference,
	// a field reference, a blank final field reference, a field of an enclosing instance or
	// just a local variable.
	if (this.resolvedType.id != T_JavaLangString) {
		this.lhs.checkNPE(currentScope, flowContext, flowInfo);
	}
	flowInfo = ((Reference) this.lhs).analyseAssignment(currentScope, flowContext, flowInfo, this, true).unconditionalInits();
	if (this.resolvedType.id == T_JavaLangString) {
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=339250
		LocalVariableBinding local = this.lhs.localVariableBinding();
		if (local != null) {
			// compound assignment results in a definitely non null value for String
			flowInfo.markAsDefinitelyNonNull(local);
			if (flowContext.initsOnFinally != null)
				flowContext.initsOnFinally.markAsDefinitelyNonNull(local);
		}
	}
	return flowInfo;
}

	public boolean checkCastCompatibility() {
		return true;
	}
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {

		// various scenarii are possible, setting an array reference,
		// a field reference, a blank final field reference, a field of an enclosing instance or
		// just a local variable.

		int pc = codeStream.position;
		 ((Reference) this.lhs).generateCompoundAssignment(currentScope, codeStream, this.expression, this.operator, this.preAssignImplicitConversion, valueRequired);
		if (valueRequired) {
			codeStream.generateImplicitConversion(this.implicitConversion);
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

public int nullStatus(FlowInfo flowInfo) {
	return FlowInfo.NON_NULL;
	// we may have complained on checkNPE, but we avoid duplicate error
}

	public String operatorToString() {
		switch (this.operator) {
			case PLUS :
				return "+="; //$NON-NLS-1$
			case MINUS :
				return "-="; //$NON-NLS-1$
			case MULTIPLY :
				return "*="; //$NON-NLS-1$
			case DIVIDE :
				return "/="; //$NON-NLS-1$
			case AND :
				return "&="; //$NON-NLS-1$
			case OR :
				return "|="; //$NON-NLS-1$
			case XOR :
				return "^="; //$NON-NLS-1$
			case REMAINDER :
				return "%="; //$NON-NLS-1$
			case LEFT_SHIFT :
				return "<<="; //$NON-NLS-1$
			case RIGHT_SHIFT :
				return ">>="; //$NON-NLS-1$
			case UNSIGNED_RIGHT_SHIFT :
				return ">>>="; //$NON-NLS-1$
		}
		return "unknown operator"; //$NON-NLS-1$
	}

	public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {

		this.lhs.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
		return this.expression.printExpression(0, output) ;
	}

	public TypeBinding resolveType(BlockScope scope) {
		this.constant = Constant.NotAConstant;
		if (!(this.lhs instanceof Reference) || this.lhs.isThis()) {
			scope.problemReporter().expressionShouldBeAVariable(this.lhs);
			return null;
		}
		boolean expressionIsCast = this.expression instanceof CastExpression;
		if (expressionIsCast)
			this.expression.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
		TypeBinding originalLhsType = this.lhs.resolveType(scope);
		TypeBinding originalExpressionType = this.expression.resolveType(scope);
		if (originalLhsType == null || originalExpressionType == null)
			return null;
		LocalVariableBinding localVariableBinding = this.lhs.localVariableBinding();
		if (localVariableBinding != null) {
			localVariableBinding.tagBits &= ~TagBits.IsEffectivelyFinal;
		}
		// autoboxing support
		LookupEnvironment env = scope.environment();
		TypeBinding lhsType = originalLhsType, expressionType = originalExpressionType;
		boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
		boolean unboxedLhs = false;
		if (use15specifics) {
			if (!lhsType.isBaseType() && expressionType.id != T_JavaLangString && expressionType.id != T_null) {
				TypeBinding unboxedType = env.computeBoxingType(lhsType);
				if (unboxedType != lhsType) {
					lhsType = unboxedType;
					unboxedLhs = true;
				}
			}
			if (!expressionType.isBaseType() && lhsType.id != T_JavaLangString  && lhsType.id != T_null) {
				expressionType = env.computeBoxingType(expressionType);
			}
		}

		if (restrainUsageToNumericTypes() && !lhsType.isNumericType()) {
			scope.problemReporter().operatorOnlyValidOnNumericType(this, lhsType, expressionType);
			return null;
		}
		int lhsID = lhsType.id;
		int expressionID = expressionType.id;
		if (lhsID > 15 || expressionID > 15) {
			if (lhsID != T_JavaLangString) { // String += Thread is valid whereas Thread += String  is not
				scope.problemReporter().invalidOperator(this, lhsType, expressionType);
				return null;
			}
			expressionID = T_JavaLangObject; // use the Object has tag table
		}

		// the code is an int
		// (cast)  left   Op (cast)  rigth --> result
		//  0000   0000       0000   0000      0000
		//  <<16   <<12       <<8     <<4        <<0

		// the conversion is stored INTO the reference (info needed for the code gen)
		int result = OperatorExpression.OperatorSignatures[this.operator][ (lhsID << 4) + expressionID];
		if (result == T_undefined) {
			scope.problemReporter().invalidOperator(this, lhsType, expressionType);
			return null;
		}
		if (this.operator == PLUS){
			if(lhsID == T_JavaLangObject && (scope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_7)) {
				// <Object> += <String> is illegal (39248) for compliance < 1.7
				scope.problemReporter().invalidOperator(this, lhsType, expressionType);
				return null;
			} else {
				// <int | boolean> += <String> is illegal
				if ((lhsType.isNumericType() || lhsID == T_boolean) && !expressionType.isNumericType()){
					scope.problemReporter().invalidOperator(this, lhsType, expressionType);
					return null;
				}
			}
		}
		TypeBinding resultType = TypeBinding.wellKnownType(scope, result & 0x0000F);
		if (checkCastCompatibility()) {
			if (originalLhsType.id != T_JavaLangString && resultType.id != T_JavaLangString) {
				if (!checkCastTypesCompatibility(scope, originalLhsType, resultType, null)) {
					scope.problemReporter().invalidOperator(this, originalLhsType, expressionType);
					return null;
				}
			}
		}
		this.lhs.computeConversion(scope, TypeBinding.wellKnownType(scope, (result >>> 16) & 0x0000F), originalLhsType);
		this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, (result >>> 8) & 0x0000F), originalExpressionType);
		this.preAssignImplicitConversion =  (unboxedLhs ? BOXING : 0) | (lhsID << 4) | (result & 0x0000F);
		if (unboxedLhs) scope.problemReporter().autoboxing(this, lhsType, originalLhsType);
		if (expressionIsCast)
			CastExpression.checkNeedForArgumentCasts(scope, this.operator, result, this.lhs, originalLhsType.id, false, this.expression, originalExpressionType.id, true);
		return this.resolvedType = originalLhsType;
	}

	public boolean restrainUsageToNumericTypes(){
		return false ;
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			this.lhs.traverse(visitor, scope);
			this.expression.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}
}
