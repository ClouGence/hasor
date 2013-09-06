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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

//dedicated treatment for the ||
public class OR_OR_Expression extends BinaryExpression {

	int rightInitStateIndex = -1;
	int mergedInitStateIndex = -1;

	public OR_OR_Expression(Expression left, Expression right, int operator) {
		super(left, right, operator);
	}

	public FlowInfo analyseCode(
		BlockScope currentScope,
		FlowContext flowContext,
		FlowInfo flowInfo) {

		Constant cst = this.left.optimizedBooleanConstant();
		boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
		boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

		if (isLeftOptimizedFalse) {
			// FALSE || anything
			 // need to be careful of scenario:
			//		(x || y) || !z, if passing the left info to the right, it would be swapped by the !
			FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
			mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
			this.mergedInitStateIndex =
				currentScope.methodScope().recordInitializationStates(mergedInfo);
			return mergedInfo;
		}

		FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);

		 // need to be careful of scenario:
		//		(x || y) || !z, if passing the left info to the right, it would be swapped by the !
		FlowInfo rightInfo = leftInfo.initsWhenFalse().unconditionalCopy();
		this.rightInitStateIndex =
			currentScope.methodScope().recordInitializationStates(rightInfo);

		int previousMode = rightInfo.reachMode();
		if (isLeftOptimizedTrue){
			if ((rightInfo.reachMode() & FlowInfo.UNREACHABLE) == 0) {
				currentScope.problemReporter().fakeReachable(this.right);
				rightInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
			}
		}
		rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
		if ((this.left.implicitConversion & TypeIds.UNBOXING) != 0) {
			this.left.checkNPE(currentScope, flowContext, flowInfo);
		}
		if ((this.right.implicitConversion & TypeIds.UNBOXING) != 0) {
			this.right.checkNPE(currentScope, flowContext, flowInfo);
		}
		// The definitely null variables in right info when true should not be missed out while merging
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=299900
		FlowInfo leftInfoWhenTrueForMerging = leftInfo.initsWhenTrue().unconditionalCopy().addPotentialInitializationsFrom(rightInfo.unconditionalInitsWithoutSideEffect());
		FlowInfo mergedInfo = FlowInfo.conditional(
					// merging two true initInfos for such a negative case: if ((t && (b = t)) || f) r = b; // b may not have been initialized
				leftInfoWhenTrueForMerging.unconditionalInits().mergedWith(
						rightInfo.safeInitsWhenTrue().setReachMode(previousMode).unconditionalInits()),
					rightInfo.initsWhenFalse());
		this.mergedInitStateIndex =
			currentScope.methodScope().recordInitializationStates(mergedInfo);
		return mergedInfo;
	}

	/**
	 * Code generation for a binary operation
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		int pc = codeStream.position;
		if (this.constant != Constant.NotAConstant) {
			// inlined value
			if (valueRequired)
				codeStream.generateConstant(this.constant, this.implicitConversion);
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			return;
		}
		Constant cst = this.right.constant;
		if (cst != Constant.NotAConstant) {
			// <expr> || true --> true
			if (cst.booleanValue() == true) {
				this.left.generateCode(currentScope, codeStream, false);
				if (valueRequired) codeStream.iconst_1();
			} else {
				// <expr>|| false --> <expr>
				this.left.generateCode(currentScope, codeStream, valueRequired);
			}
			if (this.mergedInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
			}
			codeStream.generateImplicitConversion(this.implicitConversion);
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			return;
		}

		BranchLabel trueLabel = new BranchLabel(codeStream), endLabel;
		cst = this.left.optimizedBooleanConstant();
		boolean leftIsConst = cst != Constant.NotAConstant;
		boolean leftIsTrue = leftIsConst && cst.booleanValue() == true;

		cst = this.right.optimizedBooleanConstant();
		boolean rightIsConst = cst != Constant.NotAConstant;
		boolean rightIsTrue = rightIsConst && cst.booleanValue() == true;

		generateOperands : {
			if (leftIsConst) {
				this.left.generateCode(currentScope, codeStream, false);
				if (leftIsTrue) {
					break generateOperands; // no need to generate right operand
				}
			} else {
				this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, true);
				// need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
			}
			if (this.rightInitStateIndex != -1) {
				codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
			}
			if (rightIsConst) {
				this.right.generateCode(currentScope, codeStream, false);
			} else {
				this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired);
			}
		}
		if (this.mergedInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
		}
		/*
		 * improving code gen for such a case: boolean b = i < 0 || true since
		 * the label has never been used, we have the inlined value on the
		 * stack.
		 */
		if (valueRequired) {
			if (leftIsConst && leftIsTrue) {
				codeStream.iconst_1();
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			} else {
				if (rightIsConst && rightIsTrue) {
					codeStream.iconst_1();
					codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				} else {
					codeStream.iconst_0();
				}
				if (trueLabel.forwardReferenceCount() > 0) {
					if ((this.bits & IsReturnedValue) != 0) {
						codeStream.generateImplicitConversion(this.implicitConversion);
						codeStream.generateReturnBytecode(this);
						trueLabel.place();
						codeStream.iconst_1();
					} else {
						codeStream.goto_(endLabel = new BranchLabel(codeStream));
						codeStream.decrStackSize(1);
						trueLabel.place();
						codeStream.iconst_1();
						endLabel.place();
					}
				} else {
					trueLabel.place();
				}
			}
			codeStream.generateImplicitConversion(this.implicitConversion);
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
		} else {
			trueLabel.place();
		}
	}

	/**
	 * Boolean operator code generation Optimized operations are: ||
	 */
	public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
		if (this.constant != Constant.NotAConstant) {
			super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
			return;
		}

		// <expr> || false --> <expr>
		Constant cst = this.right.constant;
		if (cst != Constant.NotAConstant && cst.booleanValue() == false) {
			int pc = codeStream.position;
			this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
			if (this.mergedInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			return;
		}

		cst = this.left.optimizedBooleanConstant();
		boolean leftIsConst = cst != Constant.NotAConstant;
		boolean leftIsTrue = leftIsConst && cst.booleanValue() == true;

		cst = this.right.optimizedBooleanConstant();
		boolean rightIsConst = cst != Constant.NotAConstant;
		boolean rightIsTrue = rightIsConst && cst.booleanValue() == true;

		// default case
		generateOperands : {
			if (falseLabel == null) {
				if (trueLabel != null) {
					// implicit falling through the FALSE case
					this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, !leftIsConst);
					// need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
					if (leftIsTrue) {
						if (valueRequired) codeStream.goto_(trueLabel);
						codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
						break generateOperands; // no need to generate right operand
					}
					if (this.rightInitStateIndex != -1) {
						codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
					}
					this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired && !rightIsConst);
					if (valueRequired && rightIsTrue) {
						codeStream.goto_(trueLabel);
						codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
					}
				}
			} else {
				// implicit falling through the TRUE case
				if (trueLabel == null) {
					BranchLabel internalTrueLabel = new BranchLabel(codeStream);
					this.left.generateOptimizedBoolean(currentScope, codeStream, internalTrueLabel, null, !leftIsConst);
					// need value, e.g. if (a == 1 || ((b = 2) > 0)) {} -> shouldn't initialize 'b' if a==1
					if (leftIsTrue) {
						internalTrueLabel.place();
						break generateOperands; // no need to generate right operand
					}
					if (this.rightInitStateIndex != -1) {
						codeStream
								.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
					}
					this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired && !rightIsConst);
					if (valueRequired && rightIsConst && !rightIsTrue) {
						codeStream.goto_(falseLabel);
						codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
					}
					internalTrueLabel.place();
				} else {
					// no implicit fall through TRUE/FALSE --> should never occur
				}
			}
		}
		if (this.mergedInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
		}
	}

	public boolean isCompactableOperation() {
		return false;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ast.BinaryExpression#resolveType(org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public TypeBinding resolveType(BlockScope scope) {
		TypeBinding result = super.resolveType(scope);
		// check whether comparing identical expressions
		Binding leftDirect = Expression.getDirectBinding(this.left);
		if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
			if (!(this.right instanceof Assignment))
				scope.problemReporter().comparingIdenticalExpressions(this);
		}
		return result;
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			this.left.traverse(visitor, scope);
			this.right.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}
}
