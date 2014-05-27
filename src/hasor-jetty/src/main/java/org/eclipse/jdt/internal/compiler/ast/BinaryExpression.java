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
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class BinaryExpression extends OperatorExpression {

/* Tracking helpers
 * The following are used to elaborate realistic statistics about binary
 * expressions. This must be neutralized in the released code.
 * Search the keyword BE_INSTRUMENTATION to reenable.
 * An external device must install a suitable probe so as to monitor the
 * emission of events and publish the results.
	public interface Probe {
		public void ping(int depth);
	}
	public int depthTracker;
	public static Probe probe;
 */

	public Expression left, right;
	public Constant optimizedBooleanConstant;

public BinaryExpression(Expression left, Expression right, int operator) {
	this.left = left;
	this.right = right;
	this.bits |= operator << ASTNode.OperatorSHIFT; // encode operator
	this.sourceStart = left.sourceStart;
	this.sourceEnd = right.sourceEnd;
	// BE_INSTRUMENTATION: neutralized in the released code
//	if (left instanceof BinaryExpression &&
//			((left.bits & OperatorMASK) ^ (this.bits & OperatorMASK)) == 0) {
//		this.depthTracker = ((BinaryExpression)left).depthTracker + 1;
//	} else {
//		this.depthTracker = 1;
//	}
}
public BinaryExpression(BinaryExpression expression) {
	this.left = expression.left;
	this.right = expression.right;
	this.bits = expression.bits;
	this.sourceStart = expression.sourceStart;
	this.sourceEnd = expression.sourceEnd;
}
public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	// keep implementation in sync with CombinedBinaryExpression#analyseCode
	if (this.resolvedType.id == TypeIds.T_JavaLangString) {
		return this.right.analyseCode(
							currentScope, flowContext,
							this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits())
						.unconditionalInits();
	} else {
		this.left.checkNPE(currentScope, flowContext, flowInfo);
		flowInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
		this.right.checkNPE(currentScope, flowContext, flowInfo);
		return this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
	}
}

public void computeConstant(BlockScope scope, int leftId, int rightId) {
	//compute the constant when valid
	if ((this.left.constant != Constant.NotAConstant)
		&& (this.right.constant != Constant.NotAConstant)) {
		try {
			this.constant =
				Constant.computeConstantOperation(
					this.left.constant,
					leftId,
					(this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT,
					this.right.constant,
					rightId);
		} catch (ArithmeticException e) {
			this.constant = Constant.NotAConstant;
			// 1.2 no longer throws an exception at compile-time
			//scope.problemReporter().compileTimeConstantThrowsArithmeticException(this);
		}
	} else {
		this.constant = Constant.NotAConstant;
		//add some work for the boolean operators & |
		this.optimizedBooleanConstant(
			leftId,
			(this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT,
			rightId);
	}
}

public Constant optimizedBooleanConstant() {
	return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
}

/**
 * Code generation for a binary operation
 */
// given the current focus of CombinedBinaryExpression on strings concatenation,
// we do not provide a general, non-recursive implementation of generateCode,
// but rely upon generateOptimizedStringConcatenationCreation instead
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	int pc = codeStream.position;
	if (this.constant != Constant.NotAConstant) {
		if (valueRequired)
			codeStream.generateConstant(this.constant, this.implicitConversion);
		codeStream.recordPositionsFrom(pc, this.sourceStart);
		return;
	}
	switch ((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
		case PLUS :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_JavaLangString :
					// BE_INSTRUMENTATION: neutralized in the released code
//					if (probe != null) {
//						probe.ping(this.depthTracker);
//					}
					codeStream.generateStringConcatenationAppend(currentScope, this.left, this.right);
					if (!valueRequired)
						codeStream.pop();
					break;
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.iadd();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.ladd();
					break;
				case T_double :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.dadd();
					break;
				case T_float :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.fadd();
					break;
			}
			break;
		case MINUS :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.isub();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.lsub();
					break;
				case T_double :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.dsub();
					break;
				case T_float :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.fsub();
					break;
			}
			break;
		case MULTIPLY :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.imul();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.lmul();
					break;
				case T_double :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.dmul();
					break;
				case T_float :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.fmul();
					break;
			}
			break;
		case DIVIDE :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, true);
					this.right.generateCode(currentScope, codeStream, true);
					codeStream.idiv();
					if (!valueRequired)
						codeStream.pop();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, true);
					this.right.generateCode(currentScope, codeStream, true);
					codeStream.ldiv();
					if (!valueRequired)
						codeStream.pop2();
					break;
				case T_double :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.ddiv();
					break;
				case T_float :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.fdiv();
					break;
			}
			break;
		case REMAINDER :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, true);
					this.right.generateCode(currentScope, codeStream, true);
					codeStream.irem();
					if (!valueRequired)
						codeStream.pop();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, true);
					this.right.generateCode(currentScope, codeStream, true);
					codeStream.lrem();
					if (!valueRequired)
						codeStream.pop2();
					break;
				case T_double :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.drem();
					break;
				case T_float :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.frem();
					break;
			}
			break;
		case AND :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					// 0 & x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_int)
						&& (this.left.constant.intValue() == 0)) {
						this.right.generateCode(currentScope, codeStream, false);
						if (valueRequired)
							codeStream.iconst_0();
					} else {
						// x & 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_int)
							&& (this.right.constant.intValue() == 0)) {
							this.left.generateCode(currentScope, codeStream, false);
							if (valueRequired)
								codeStream.iconst_0();
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.iand();
						}
					}
					break;
				case T_long :
					// 0 & x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_long)
						&& (this.left.constant.longValue() == 0L)) {
						this.right.generateCode(currentScope, codeStream, false);
						if (valueRequired)
							codeStream.lconst_0();
					} else {
						// x & 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_long)
							&& (this.right.constant.longValue() == 0L)) {
							this.left.generateCode(currentScope, codeStream, false);
							if (valueRequired)
								codeStream.lconst_0();
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.land();
						}
					}
					break;
				case T_boolean : // logical and
					generateLogicalAnd(currentScope, codeStream, valueRequired);
					break;
			}
			break;
		case OR :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					// 0 | x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_int)
						&& (this.left.constant.intValue() == 0)) {
						this.right.generateCode(currentScope, codeStream, valueRequired);
					} else {
						// x | 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_int)
							&& (this.right.constant.intValue() == 0)) {
							this.left.generateCode(currentScope, codeStream, valueRequired);
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.ior();
						}
					}
					break;
				case T_long :
					// 0 | x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_long)
						&& (this.left.constant.longValue() == 0L)) {
						this.right.generateCode(currentScope, codeStream, valueRequired);
					} else {
						// x | 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_long)
							&& (this.right.constant.longValue() == 0L)) {
							this.left.generateCode(currentScope, codeStream, valueRequired);
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.lor();
						}
					}
					break;
				case T_boolean : // logical or
					generateLogicalOr(currentScope, codeStream, valueRequired);
					break;
			}
			break;
		case XOR :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					// 0 ^ x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_int)
						&& (this.left.constant.intValue() == 0)) {
						this.right.generateCode(currentScope, codeStream, valueRequired);
					} else {
						// x ^ 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_int)
							&& (this.right.constant.intValue() == 0)) {
							this.left.generateCode(currentScope, codeStream, valueRequired);
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.ixor();
						}
					}
					break;
				case T_long :
					// 0 ^ x
					if ((this.left.constant != Constant.NotAConstant)
						&& (this.left.constant.typeID() == TypeIds.T_long)
						&& (this.left.constant.longValue() == 0L)) {
						this.right.generateCode(currentScope, codeStream, valueRequired);
					} else {
						// x ^ 0
						if ((this.right.constant != Constant.NotAConstant)
							&& (this.right.constant.typeID() == TypeIds.T_long)
							&& (this.right.constant.longValue() == 0L)) {
							this.left.generateCode(currentScope, codeStream, valueRequired);
						} else {
							this.left.generateCode(currentScope, codeStream, valueRequired);
							this.right.generateCode(currentScope, codeStream, valueRequired);
							if (valueRequired)
								codeStream.lxor();
						}
					}
					break;
				case T_boolean :
					generateLogicalXor(currentScope, 	codeStream, valueRequired);
					break;
			}
			break;
		case LEFT_SHIFT :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.ishl();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.lshl();
			}
			break;
		case RIGHT_SHIFT :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.ishr();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.lshr();
			}
			break;
		case UNSIGNED_RIGHT_SHIFT :
			switch (this.bits & ASTNode.ReturnTypeIDMASK) {
				case T_int :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.iushr();
					break;
				case T_long :
					this.left.generateCode(currentScope, codeStream, valueRequired);
					this.right.generateCode(currentScope, codeStream, valueRequired);
					if (valueRequired)
						codeStream.lushr();
			}
			break;
		case GREATER :
			BranchLabel falseLabel, endLabel;
			generateOptimizedGreaterThan(
				currentScope,
				codeStream,
				null,
				(falseLabel = new BranchLabel(codeStream)),
				valueRequired);
			if (valueRequired) {
				codeStream.iconst_1();
				if ((this.bits & ASTNode.IsReturnedValue) != 0) {
					codeStream.generateImplicitConversion(this.implicitConversion);
					codeStream.generateReturnBytecode(this);
					falseLabel.place();
					codeStream.iconst_0();
				} else {
					codeStream.goto_(endLabel = new BranchLabel(codeStream));
					codeStream.decrStackSize(1);
					falseLabel.place();
					codeStream.iconst_0();
					endLabel.place();
				}
			}
			break;
		case GREATER_EQUAL :
			generateOptimizedGreaterThanOrEqual(
				currentScope,
				codeStream,
				null,
				(falseLabel = new BranchLabel(codeStream)),
				valueRequired);
			if (valueRequired) {
				codeStream.iconst_1();
				if ((this.bits & ASTNode.IsReturnedValue) != 0) {
					codeStream.generateImplicitConversion(this.implicitConversion);
					codeStream.generateReturnBytecode(this);
					falseLabel.place();
					codeStream.iconst_0();
				} else {
					codeStream.goto_(endLabel = new BranchLabel(codeStream));
					codeStream.decrStackSize(1);
					falseLabel.place();
					codeStream.iconst_0();
					endLabel.place();
				}
			}
			break;
		case LESS :
			generateOptimizedLessThan(
				currentScope,
				codeStream,
				null,
				(falseLabel = new BranchLabel(codeStream)),
				valueRequired);
			if (valueRequired) {
				codeStream.iconst_1();
				if ((this.bits & ASTNode.IsReturnedValue) != 0) {
					codeStream.generateImplicitConversion(this.implicitConversion);
					codeStream.generateReturnBytecode(this);
					falseLabel.place();
					codeStream.iconst_0();
				} else {
					codeStream.goto_(endLabel = new BranchLabel(codeStream));
					codeStream.decrStackSize(1);
					falseLabel.place();
					codeStream.iconst_0();
					endLabel.place();
				}
			}
			break;
		case LESS_EQUAL :
			generateOptimizedLessThanOrEqual(
				currentScope,
				codeStream,
				null,
				(falseLabel = new BranchLabel(codeStream)),
				valueRequired);
			if (valueRequired) {
				codeStream.iconst_1();
				if ((this.bits & ASTNode.IsReturnedValue) != 0) {
					codeStream.generateImplicitConversion(this.implicitConversion);
					codeStream.generateReturnBytecode(this);
					falseLabel.place();
					codeStream.iconst_0();
				} else {
					codeStream.goto_(endLabel = new BranchLabel(codeStream));
					codeStream.decrStackSize(1);
					falseLabel.place();
					codeStream.iconst_0();
					endLabel.place();
				}
			}
	}
	if (valueRequired) {
		codeStream.generateImplicitConversion(this.implicitConversion);
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

/**
 * Boolean operator code generation
 *	Optimized operations are: <, <=, >, >=, &, |, ^
 */
public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == TypeIds.T_boolean)) {
		super.generateOptimizedBoolean(
			currentScope,
			codeStream,
			trueLabel,
			falseLabel,
			valueRequired);
		return;
	}
	switch ((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) {
		case LESS :
			generateOptimizedLessThan(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case LESS_EQUAL :
			generateOptimizedLessThanOrEqual(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case GREATER :
			generateOptimizedGreaterThan(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case GREATER_EQUAL :
			generateOptimizedGreaterThanOrEqual(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case AND :
			generateOptimizedLogicalAnd(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case OR :
			generateOptimizedLogicalOr(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
		case XOR :
			generateOptimizedLogicalXor(
				currentScope,
				codeStream,
				trueLabel,
				falseLabel,
				valueRequired);
			return;
	}
	super.generateOptimizedBoolean(
		currentScope,
		codeStream,
		trueLabel,
		falseLabel,
		valueRequired);
}

/**
 * Boolean generation for >
 */
public void generateOptimizedGreaterThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
	// both sides got promoted in the same way
	if (promotedTypeID == TypeIds.T_int) {
		// 0 > x
		if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
			this.right.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.iflt(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifge(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
		// x > 0
		if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
			this.left.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifgt(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifle(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
	}
	// default comparison
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmpgt(trueLabel);
						break;
					case T_float :
						codeStream.fcmpl();
						codeStream.ifgt(trueLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifgt(trueLabel);
						break;
					case T_double :
						codeStream.dcmpl();
						codeStream.ifgt(trueLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			}
		} else {
			if (trueLabel == null) {
				// implicit falling through the TRUE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmple(falseLabel);
						break;
					case T_float :
						codeStream.fcmpl();
						codeStream.ifle(falseLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifle(falseLabel);
						break;
					case T_double :
						codeStream.dcmpl();
						codeStream.ifle(falseLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
}

/**
 * Boolean generation for >=
 */
public void generateOptimizedGreaterThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
	// both sides got promoted in the same way
	if (promotedTypeID == TypeIds.T_int) {
		// 0 >= x
		if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
			this.right.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifle(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifgt(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
		// x >= 0
		if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
			this.left.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifge(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.iflt(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
	}
	// default comparison
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmpge(trueLabel);
						break;
					case T_float :
						codeStream.fcmpl();
						codeStream.ifge(trueLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifge(trueLabel);
						break;
					case T_double :
						codeStream.dcmpl();
						codeStream.ifge(trueLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			}
		} else {
			if (trueLabel == null) {
				// implicit falling through the TRUE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmplt(falseLabel);
						break;
					case T_float :
						codeStream.fcmpl();
						codeStream.iflt(falseLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.iflt(falseLabel);
						break;
					case T_double :
						codeStream.dcmpl();
						codeStream.iflt(falseLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
}

/**
 * Boolean generation for <
 */
public void generateOptimizedLessThan(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
	// both sides got promoted in the same way
	if (promotedTypeID == TypeIds.T_int) {
		// 0 < x
		if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
			this.right.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifgt(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifle(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
		// x < 0
		if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
			this.left.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.iflt(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifge(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
	}
	// default comparison
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmplt(trueLabel);
						break;
					case T_float :
						codeStream.fcmpg();
						codeStream.iflt(trueLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.iflt(trueLabel);
						break;
					case T_double :
						codeStream.dcmpg();
						codeStream.iflt(trueLabel);
				}
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			}
		} else {
			if (trueLabel == null) {
				// implicit falling through the TRUE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmpge(falseLabel);
						break;
					case T_float :
						codeStream.fcmpg();
						codeStream.ifge(falseLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifge(falseLabel);
						break;
					case T_double :
						codeStream.dcmpg();
						codeStream.ifge(falseLabel);
				}
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
}

/**
 * Boolean generation for <=
 */
public void generateOptimizedLessThanOrEqual(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	int promotedTypeID = (this.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
	// both sides got promoted in the same way
	if (promotedTypeID == TypeIds.T_int) {
		// 0 <= x
		if ((this.left.constant != Constant.NotAConstant) && (this.left.constant.intValue() == 0)) {
			this.right.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifge(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.iflt(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
		// x <= 0
		if ((this.right.constant != Constant.NotAConstant) && (this.right.constant.intValue() == 0)) {
			this.left.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				if (falseLabel == null) {
					if (trueLabel != null) {
						// implicitly falling through the FALSE case
						codeStream.ifle(trueLabel);
					}
				} else {
					if (trueLabel == null) {
						// implicitly falling through the TRUE case
						codeStream.ifgt(falseLabel);
					} else {
						// no implicit fall through TRUE/FALSE --> should never occur
					}
				}
			}
			// reposition the endPC
			codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			return;
		}
	}
	// default comparison
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmple(trueLabel);
						break;
					case T_float :
						codeStream.fcmpg();
						codeStream.ifle(trueLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifle(trueLabel);
						break;
					case T_double :
						codeStream.dcmpg();
						codeStream.ifle(trueLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			}
		} else {
			if (trueLabel == null) {
				// implicit falling through the TRUE case
				switch (promotedTypeID) {
					case T_int :
						codeStream.if_icmpgt(falseLabel);
						break;
					case T_float :
						codeStream.fcmpg();
						codeStream.ifgt(falseLabel);
						break;
					case T_long :
						codeStream.lcmp();
						codeStream.ifgt(falseLabel);
						break;
					case T_double :
						codeStream.dcmpg();
						codeStream.ifgt(falseLabel);
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				return;
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
}

/**
 * Boolean generation for &
 */
public void generateLogicalAnd(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> & x
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, valueRequired);
			} else {
				// <something equivalent to false> & x
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_0();
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x & <something equivalent to true>
				this.left.generateCode(currentScope, codeStream, valueRequired);
				this.right.generateCode(currentScope, codeStream, false);
			} else {
				// x & <something equivalent to false>
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_0();
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.iand();
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

/**
 * Boolean generation for |
 */
public void generateLogicalOr(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> | x
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_1();
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			} else {
				// <something equivalent to false> | x
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, valueRequired);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x | <something equivalent to true>
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_1();
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			} else {
				// x | <something equivalent to false>
				this.left.generateCode(currentScope, codeStream, valueRequired);
				this.right.generateCode(currentScope, codeStream, false);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.ior();
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

/**
 * Boolean generation for ^
 */
public void generateLogicalXor(BlockScope currentScope,	CodeStream codeStream, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> ^ x
				this.left.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_1();
				}
				this.right.generateCode(currentScope, codeStream, valueRequired);
				if (valueRequired) {
					codeStream.ixor(); // negate
					codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				}
			} else {
				// <something equivalent to false> ^ x
				this.left.generateCode(currentScope, codeStream, false);
				this.right.generateCode(currentScope, codeStream, valueRequired);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x ^ <something equivalent to true>
				this.left.generateCode(currentScope, codeStream, valueRequired);
				this.right.generateCode(currentScope, codeStream, false);
				if (valueRequired) {
					codeStream.iconst_1();
					codeStream.ixor(); // negate
					codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
				}
			} else {
				// x ^ <something equivalent to false>
				this.left.generateCode(currentScope, codeStream, valueRequired);
				this.right.generateCode(currentScope, codeStream, false);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.ixor();
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

/**
 * Boolean generation for &
 */
public void generateOptimizedLogicalAnd(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> & x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
			} else {
				// <something equivalent to false> & x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				if (valueRequired) {
					if (falseLabel != null) {
						// implicit falling through the TRUE case
						codeStream.goto_(falseLabel);
					}
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x & <something equivalent to true>
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
			} else {
				// x & <something equivalent to false>
				BranchLabel internalTrueLabel = new BranchLabel(codeStream);
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					internalTrueLabel,
					falseLabel,
					false);
				internalTrueLabel.place();
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				if (valueRequired) {
					if (falseLabel != null) {
						// implicit falling through the TRUE case
						codeStream.goto_(falseLabel);
					}
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.iand();
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				codeStream.ifne(trueLabel);
			}
		} else {
			// implicit falling through the TRUE case
			if (trueLabel == null) {
				codeStream.ifeq(falseLabel);
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

/**
 * Boolean generation for |
 */
public void generateOptimizedLogicalOr(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> | x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				BranchLabel internalFalseLabel = new BranchLabel(codeStream);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					internalFalseLabel,
					false);
				internalFalseLabel.place();
				if (valueRequired) {
					if (trueLabel != null) {
						codeStream.goto_(trueLabel);
					}
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			} else {
				// <something equivalent to false> | x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x | <something equivalent to true>
				BranchLabel internalFalseLabel = new BranchLabel(codeStream);
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					internalFalseLabel,
					false);
				internalFalseLabel.place();
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				if (valueRequired) {
					if (trueLabel != null) {
						codeStream.goto_(trueLabel);
					}
				}
				// reposition the endPC
				codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
			} else {
				// x | <something equivalent to false>
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.ior();
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				codeStream.ifne(trueLabel);
			}
		} else {
			// implicit falling through the TRUE case
			if (trueLabel == null) {
				codeStream.ifeq(falseLabel);
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

/**
 * Boolean generation for ^
 */
public void generateOptimizedLogicalXor(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	Constant condConst;
	if ((this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK) == TypeIds.T_boolean) {
		if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// <something equivalent to true> ^ x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					falseLabel, // negating
					trueLabel,
					valueRequired);
			} else {
				// <something equivalent to false> ^ x
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
			}
			return;
		}
		if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
			if (condConst.booleanValue() == true) {
				// x ^ <something equivalent to true>
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					falseLabel, // negating
					trueLabel,
					valueRequired);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
			} else {
				// x ^ <something equivalent to false>
				this.left.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					valueRequired);
				this.right.generateOptimizedBoolean(
					currentScope,
					codeStream,
					trueLabel,
					falseLabel,
					false);
			}
			return;
		}
	}
	// default case
	this.left.generateCode(currentScope, codeStream, valueRequired);
	this.right.generateCode(currentScope, codeStream, valueRequired);
	if (valueRequired) {
		codeStream.ixor();
		if (falseLabel == null) {
			if (trueLabel != null) {
				// implicit falling through the FALSE case
				codeStream.ifne(trueLabel);
			}
		} else {
			// implicit falling through the TRUE case
			if (trueLabel == null) {
				codeStream.ifeq(falseLabel);
			} else {
				// no implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
}

public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID) {
	// keep implementation in sync with CombinedBinaryExpression
	// #generateOptimizedStringConcatenation
	/* In the case trying to make a string concatenation, there is no need to create a new
	 * string buffer, thus use a lower-level API for code generation involving only the
	 * appending of arguments to the existing StringBuffer
	 */

	if ((((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS)
		&& ((this.bits & ASTNode.ReturnTypeIDMASK) == TypeIds.T_JavaLangString)) {
		if (this.constant != Constant.NotAConstant) {
			codeStream.generateConstant(this.constant, this.implicitConversion);
			codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
		} else {
			int pc = codeStream.position;
			this.left.generateOptimizedStringConcatenation(
				blockScope,
				codeStream,
				this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
			codeStream.recordPositionsFrom(pc, this.left.sourceStart);
			pc = codeStream.position;
			this.right.generateOptimizedStringConcatenation(
				blockScope,
				codeStream,
				this.right.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
			codeStream.recordPositionsFrom(pc, this.right.sourceStart);
		}
	} else {
		super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
	}
}

public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID) {
	// keep implementation in sync with CombinedBinaryExpression
	// #generateOptimizedStringConcatenationCreation
	/* In the case trying to make a string concatenation, there is no need to create a new
	 * string buffer, thus use a lower-level API for code generation involving only the
	 * appending of arguments to the existing StringBuffer
	 */
	if ((((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS)
		&& ((this.bits & ASTNode.ReturnTypeIDMASK) == TypeIds.T_JavaLangString)) {
		if (this.constant != Constant.NotAConstant) {
			codeStream.newStringContatenation(); // new: java.lang.StringBuffer
			codeStream.dup();
			codeStream.ldc(this.constant.stringValue());
			codeStream.invokeStringConcatenationStringConstructor();
			// invokespecial: java.lang.StringBuffer.<init>(Ljava.lang.String;)V
		} else {
			int pc = codeStream.position;
			this.left.generateOptimizedStringConcatenationCreation(
				blockScope,
				codeStream,
				this.left.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
			codeStream.recordPositionsFrom(pc, this.left.sourceStart);
			pc = codeStream.position;
			this.right.generateOptimizedStringConcatenation(
				blockScope,
				codeStream,
				this.right.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
			codeStream.recordPositionsFrom(pc, this.right.sourceStart);
		}
	} else {
		super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
	}
}

public boolean isCompactableOperation() {
	return true;
}

/**
 * Separates into a reusable method the subpart of {@link
 * #resolveType(BlockScope)} that needs to be executed while climbing up the
 * chain of expressions of this' leftmost branch. For use by {@link
 * CombinedBinaryExpression#resolveType(BlockScope)}.
 * @param scope the scope within which the resolution occurs
 */
void nonRecursiveResolveTypeUpwards(BlockScope scope) {
	// keep implementation in sync with BinaryExpression#resolveType
	boolean leftIsCast, rightIsCast;
	TypeBinding leftType = this.left.resolvedType;

	if ((rightIsCast = this.right instanceof CastExpression) == true) {
		this.right.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
	}
	TypeBinding rightType = this.right.resolveType(scope);

	// use the id of the type to navigate into the table
	if (leftType == null || rightType == null) {
		this.constant = Constant.NotAConstant;
		return;
	}

	int leftTypeID = leftType.id;
	int rightTypeID = rightType.id;

	// autoboxing support
	boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
	if (use15specifics) {
		if (!leftType.isBaseType() && rightTypeID != TypeIds.T_JavaLangString && rightTypeID != TypeIds.T_null) {
			leftTypeID = scope.environment().computeBoxingType(leftType).id;
		}
		if (!rightType.isBaseType() && leftTypeID != TypeIds.T_JavaLangString && leftTypeID != TypeIds.T_null) {
			rightTypeID = scope.environment().computeBoxingType(rightType).id;
		}
	}
	if (leftTypeID > 15
		|| rightTypeID > 15) { // must convert String + Object || Object + String
		if (leftTypeID == TypeIds.T_JavaLangString) {
			rightTypeID = TypeIds.T_JavaLangObject;
		} else if (rightTypeID == TypeIds.T_JavaLangString) {
			leftTypeID = TypeIds.T_JavaLangObject;
		} else {
			this.constant = Constant.NotAConstant;
			scope.problemReporter().invalidOperator(this, leftType, rightType);
			return;
		}
	}
	if (((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
		if (leftTypeID == TypeIds.T_JavaLangString) {
			this.left.computeConversion(scope, leftType, leftType);
			if (rightType.isArrayType() && ((ArrayBinding) rightType).elementsType() == TypeBinding.CHAR) {
				scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
			}
		}
		if (rightTypeID == TypeIds.T_JavaLangString) {
			this.right.computeConversion(scope, rightType, rightType);
			if (leftType.isArrayType() && ((ArrayBinding) leftType).elementsType() == TypeBinding.CHAR) {
				scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
			}
		}
	}

	// the code is an int
	// (cast)  left   Op (cast)  right --> result
	//  0000   0000       0000   0000      0000
	//  <<16   <<12       <<8    <<4       <<0

	// Don't test for result = 0. If it is zero, some more work is done.
	// On the one hand when it is not zero (correct code) we avoid doing the test
	int operator = (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT;
	int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];

	this.left.computeConversion(scope, 	TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F), leftType);
	this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F), rightType);
	this.bits |= operatorSignature & 0xF;
	switch (operatorSignature & 0xF) { // record the current ReturnTypeID
		// only switch on possible result type.....
		case T_boolean :
			this.resolvedType = TypeBinding.BOOLEAN;
			break;
		case T_byte :
			this.resolvedType = TypeBinding.BYTE;
			break;
		case T_char :
			this.resolvedType = TypeBinding.CHAR;
			break;
		case T_double :
			this.resolvedType = TypeBinding.DOUBLE;
			break;
		case T_float :
			this.resolvedType = TypeBinding.FLOAT;
			break;
		case T_int :
			this.resolvedType = TypeBinding.INT;
			break;
		case T_long :
			this.resolvedType = TypeBinding.LONG;
			break;
		case T_JavaLangString :
			this.resolvedType = scope.getJavaLangString();
			break;
		default : //error........
			this.constant = Constant.NotAConstant;
			scope.problemReporter().invalidOperator(this, leftType, rightType);
			return;
	}

	// check need for operand cast
	if ((leftIsCast = (this.left instanceof CastExpression)) == true ||
			rightIsCast) {
		CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
	}
	// compute the constant when valid
	computeConstant(scope, leftTypeID, rightTypeID);
}

public void optimizedBooleanConstant(int leftId, int operator, int rightId) {
	switch (operator) {
		case AND :
			if ((leftId != TypeIds.T_boolean) || (rightId != TypeIds.T_boolean))
				return;
			//$FALL-THROUGH$
		case AND_AND :
			Constant cst;
			if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
				if (cst.booleanValue() == false) { // left is equivalent to false
					this.optimizedBooleanConstant = cst; // constant(false)
					return;
				} else { //left is equivalent to true
					if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
						this.optimizedBooleanConstant = cst;
						// the conditional result is equivalent to the right conditional value
					}
					return;
				}
			}
			if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
				if (cst.booleanValue() == false) { // right is equivalent to false
					this.optimizedBooleanConstant = cst; // constant(false)
				}
			}
			return;
		case OR :
			if ((leftId != TypeIds.T_boolean) || (rightId != TypeIds.T_boolean))
				return;
			//$FALL-THROUGH$
		case OR_OR :
			if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
				if (cst.booleanValue() == true) { // left is equivalent to true
					this.optimizedBooleanConstant = cst; // constant(true)
					return;
				} else { //left is equivalent to false
					if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
						this.optimizedBooleanConstant = cst;
					}
					return;
				}
			}
			if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
				if (cst.booleanValue() == true) { // right is equivalent to true
					this.optimizedBooleanConstant = cst; // constant(true)
				}
			}
	}
}

public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {
	// keep implementation in sync with
	// CombinedBinaryExpression#printExpressionNoParenthesis
	this.left.printExpression(indent, output).append(' ').append(operatorToString()).append(' ');
	return this.right.printExpression(0, output);
}

public TypeBinding resolveType(BlockScope scope) {
	// keep implementation in sync with CombinedBinaryExpression#resolveType
	// and nonRecursiveResolveTypeUpwards
	boolean leftIsCast, rightIsCast;
	if ((leftIsCast = this.left instanceof CastExpression) == true) this.left.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
	TypeBinding leftType = this.left.resolveType(scope);

	if ((rightIsCast = this.right instanceof CastExpression) == true) this.right.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
	TypeBinding rightType = this.right.resolveType(scope);

	// use the id of the type to navigate into the table
	if (leftType == null || rightType == null) {
		this.constant = Constant.NotAConstant;
		return null;
	}

	int leftTypeID = leftType.id;
	int rightTypeID = rightType.id;

	// autoboxing support
	boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
	if (use15specifics) {
		if (!leftType.isBaseType() && rightTypeID != TypeIds.T_JavaLangString && rightTypeID != TypeIds.T_null) {
			leftTypeID = scope.environment().computeBoxingType(leftType).id;
		}
		if (!rightType.isBaseType() && leftTypeID != TypeIds.T_JavaLangString && leftTypeID != TypeIds.T_null) {
			rightTypeID = scope.environment().computeBoxingType(rightType).id;
		}
	}
	if (leftTypeID > 15
		|| rightTypeID > 15) { // must convert String + Object || Object + String
		if (leftTypeID == TypeIds.T_JavaLangString) {
			rightTypeID = TypeIds.T_JavaLangObject;
		} else if (rightTypeID == TypeIds.T_JavaLangString) {
			leftTypeID = TypeIds.T_JavaLangObject;
		} else {
			this.constant = Constant.NotAConstant;
			scope.problemReporter().invalidOperator(this, leftType, rightType);
			return null;
		}
	}
	if (((this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT) == OperatorIds.PLUS) {
		if (leftTypeID == TypeIds.T_JavaLangString) {
			this.left.computeConversion(scope, leftType, leftType);
			if (rightType.isArrayType() && ((ArrayBinding) rightType).elementsType() == TypeBinding.CHAR) {
				scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
			}
		}
		if (rightTypeID == TypeIds.T_JavaLangString) {
			this.right.computeConversion(scope, rightType, rightType);
			if (leftType.isArrayType() && ((ArrayBinding) leftType).elementsType() == TypeBinding.CHAR) {
				scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
			}
		}
	}

	// the code is an int
	// (cast)  left   Op (cast)  right --> result
	//  0000   0000       0000   0000      0000
	//  <<16   <<12       <<8    <<4       <<0

	// Don't test for result = 0. If it is zero, some more work is done.
	// On the one hand when it is not zero (correct code) we avoid doing the test
	int operator = (this.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT;
	int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];

	this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 16) & 0x0000F), leftType);
	this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, (operatorSignature >>> 8) & 0x0000F), rightType);
	this.bits |= operatorSignature & 0xF;
	switch (operatorSignature & 0xF) { // record the current ReturnTypeID
		// only switch on possible result type.....
		case T_boolean :
			this.resolvedType = TypeBinding.BOOLEAN;
			break;
		case T_byte :
			this.resolvedType = TypeBinding.BYTE;
			break;
		case T_char :
			this.resolvedType = TypeBinding.CHAR;
			break;
		case T_double :
			this.resolvedType = TypeBinding.DOUBLE;
			break;
		case T_float :
			this.resolvedType = TypeBinding.FLOAT;
			break;
		case T_int :
			this.resolvedType = TypeBinding.INT;
			break;
		case T_long :
			this.resolvedType = TypeBinding.LONG;
			break;
		case T_JavaLangString :
			this.resolvedType = scope.getJavaLangString();
			break;
		default : //error........
			this.constant = Constant.NotAConstant;
			scope.problemReporter().invalidOperator(this, leftType, rightType);
			return null;
	}

	// check need for operand cast
	if (leftIsCast || rightIsCast) {
		CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
	}
	// compute the constant when valid
	computeConstant(scope, leftTypeID, rightTypeID);
	return this.resolvedType;
}

public void traverse(ASTVisitor visitor, BlockScope scope) {
	if (visitor.visit(this, scope)) {
		this.left.traverse(visitor, scope);
		this.right.traverse(visitor, scope);
	}
	visitor.endVisit(this, scope);
}
}
