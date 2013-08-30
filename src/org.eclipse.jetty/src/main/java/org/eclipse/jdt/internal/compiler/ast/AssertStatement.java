/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class AssertStatement extends Statement {

	public Expression assertExpression, exceptionArgument;

	// for local variable attribute
	int preAssertInitStateIndex = -1;
	private FieldBinding assertionSyntheticFieldBinding;

public AssertStatement(	Expression exceptionArgument, Expression assertExpression, int startPosition) {
	this.assertExpression = assertExpression;
	this.exceptionArgument = exceptionArgument;
	this.sourceStart = startPosition;
	this.sourceEnd = exceptionArgument.sourceEnd;
}

public AssertStatement(Expression assertExpression, int startPosition) {
	this.assertExpression = assertExpression;
	this.sourceStart = startPosition;
	this.sourceEnd = assertExpression.sourceEnd;
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	this.preAssertInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);

	Constant cst = this.assertExpression.optimizedBooleanConstant();
	if ((this.assertExpression.implicitConversion & TypeIds.UNBOXING) != 0) {
		this.assertExpression.checkNPE(currentScope, flowContext, flowInfo);
	}
	boolean isOptimizedTrueAssertion = cst != Constant.NotAConstant && cst.booleanValue() == true;
	boolean isOptimizedFalseAssertion = cst != Constant.NotAConstant && cst.booleanValue() == false;
	
	flowContext.tagBits |= FlowContext.HIDE_NULL_COMPARISON_WARNING;
	FlowInfo conditionFlowInfo = this.assertExpression.analyseCode(currentScope, flowContext, flowInfo.copy());
	flowContext.tagBits &= ~FlowContext.HIDE_NULL_COMPARISON_WARNING;
	UnconditionalFlowInfo assertWhenTrueInfo = conditionFlowInfo.initsWhenTrue().unconditionalInits();
	FlowInfo assertInfo = conditionFlowInfo.initsWhenFalse();
	if (isOptimizedTrueAssertion) {
		assertInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
	}

	if (this.exceptionArgument != null) {
		// only gets evaluated when escaping - results are not taken into account
		FlowInfo exceptionInfo = this.exceptionArgument.analyseCode(currentScope, flowContext, assertInfo.copy());

		if (isOptimizedTrueAssertion){
			currentScope.problemReporter().fakeReachable(this.exceptionArgument);
		} else {
			flowContext.checkExceptionHandlers(
				currentScope.getJavaLangAssertionError(),
				this,
				exceptionInfo,
				currentScope);
		}
	}

	if (!isOptimizedTrueAssertion){
		// add the assert support in the clinit
		manageSyntheticAccessIfNecessary(currentScope, flowInfo);
	}
	if (isOptimizedFalseAssertion) {
		return flowInfo; // if assertions are enabled, the following code will be unreachable
		// change this if we need to carry null analysis results of the assert
		// expression downstream
	} else {
		CompilerOptions compilerOptions = currentScope.compilerOptions();
		if (!compilerOptions.includeNullInfoFromAsserts) {
			// keep just the initializations info, don't include assert's null info
			// merge initialization info's and then add back the null info from flowInfo to
			// make sure that the empty null info of assertInfo doesnt change flowInfo's null info.
			return ((flowInfo.nullInfoLessUnconditionalCopy()).mergedWith(assertInfo.nullInfoLessUnconditionalCopy())).addNullInfoFrom(flowInfo);
		}
		return flowInfo.mergedWith(assertInfo.nullInfoLessUnconditionalCopy()).
			addInitializationsFrom(assertWhenTrueInfo.discardInitializationInfo());
		// keep the merge from the initial code for the definite assignment
		// analysis, tweak the null part to influence nulls downstream
	}
}

public void generateCode(BlockScope currentScope, CodeStream codeStream) {
	if ((this.bits & IsReachable) == 0) {
		return;
	}
	int pc = codeStream.position;

	if (this.assertionSyntheticFieldBinding != null) {
		BranchLabel assertionActivationLabel = new BranchLabel(codeStream);
		codeStream.fieldAccess(Opcodes.OPC_getstatic, this.assertionSyntheticFieldBinding, null /* default declaringClass */);
		codeStream.ifne(assertionActivationLabel);

		BranchLabel falseLabel;
		this.assertExpression.generateOptimizedBoolean(currentScope, codeStream, (falseLabel = new BranchLabel(codeStream)), null , true);
		codeStream.newJavaLangAssertionError();
		codeStream.dup();
		if (this.exceptionArgument != null) {
			this.exceptionArgument.generateCode(currentScope, codeStream, true);
			codeStream.invokeJavaLangAssertionErrorConstructor(this.exceptionArgument.implicitConversion & 0xF);
		} else {
			codeStream.invokeJavaLangAssertionErrorDefaultConstructor();
		}
		codeStream.athrow();

		// May loose some local variable initializations : affecting the local variable attributes
		if (this.preAssertInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
		}
		falseLabel.place();
		assertionActivationLabel.place();
	} else {
		// May loose some local variable initializations : affecting the local variable attributes
		if (this.preAssertInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preAssertInitStateIndex);
		}
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

public void resolve(BlockScope scope) {
	this.assertExpression.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
	if (this.exceptionArgument != null) {
		TypeBinding exceptionArgumentType = this.exceptionArgument.resolveType(scope);
		if (exceptionArgumentType != null){
		    int id = exceptionArgumentType.id;
		    switch(id) {
				case T_void :
					scope.problemReporter().illegalVoidExpression(this.exceptionArgument);
					//$FALL-THROUGH$
				default:
				    id = T_JavaLangObject;
				//$FALL-THROUGH$
				case T_boolean :
				case T_byte :
				case T_char :
				case T_short :
				case T_double :
				case T_float :
				case T_int :
				case T_long :
				case T_JavaLangString :
					this.exceptionArgument.implicitConversion = (id << 4) + id;
			}
		}
	}
}

public void traverse(ASTVisitor visitor, BlockScope scope) {
	if (visitor.visit(this, scope)) {
		this.assertExpression.traverse(visitor, scope);
		if (this.exceptionArgument != null) {
			this.exceptionArgument.traverse(visitor, scope);
		}
	}
	visitor.endVisit(this, scope);
}

public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
		// need assertion flag: $assertionsDisabled on outer most source clas
		// (in case of static member of interface, will use the outermost static member - bug 22334)
		SourceTypeBinding outerMostClass = currentScope.enclosingSourceType();
		while (outerMostClass.isLocalType()) {
			ReferenceBinding enclosing = outerMostClass.enclosingType();
			if (enclosing == null || enclosing.isInterface()) break;
			outerMostClass = (SourceTypeBinding) enclosing;
		}
		this.assertionSyntheticFieldBinding = outerMostClass.addSyntheticFieldForAssert(currentScope);

		// find <clinit> and enable assertion support
		TypeDeclaration typeDeclaration = outerMostClass.scope.referenceType();
		AbstractMethodDeclaration[] methods = typeDeclaration.methods;
		for (int i = 0, max = methods.length; i < max; i++) {
			AbstractMethodDeclaration method = methods[i];
			if (method.isClinit()) {
				((Clinit) method).setAssertionSupport(this.assertionSyntheticFieldBinding, currentScope.compilerOptions().sourceLevel < ClassFileConstants.JDK1_5);
				break;
			}
		}
	}
}

public StringBuffer printStatement(int tab, StringBuffer output) {
	printIndent(tab, output);
	output.append("assert "); //$NON-NLS-1$
	this.assertExpression.printExpression(0, output);
	if (this.exceptionArgument != null) {
		output.append(": "); //$NON-NLS-1$
		this.exceptionArgument.printExpression(0, output);
	}
	return output.append(';');
}
}
