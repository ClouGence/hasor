/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephen Herrmann <stephan@cs.tu-berlin.de> -  Contributions for
 *     						bug 133125 - [compiler][null] need to report the null status of expressions and analyze them simultaneously
 *     						bug 292478 - Report potentially null across variable assignment
 * 							bug 324178 - [null] ConditionalExpression.nullStatus(..) doesn't take into account the analysis of condition itself
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class ConditionalExpression extends OperatorExpression {

	public Expression condition, valueIfTrue, valueIfFalse;
	public Constant optimizedBooleanConstant;
	public Constant optimizedIfTrueConstant;
	public Constant optimizedIfFalseConstant;

	// for local variables table attributes
	int trueInitStateIndex = -1;
	int falseInitStateIndex = -1;
	int mergedInitStateIndex = -1;
	
	// we compute and store the null status during analyseCode (https://bugs.eclipse.org/324178):
	private int nullStatus = FlowInfo.UNKNOWN;

	public ConditionalExpression(
		Expression condition,
		Expression valueIfTrue,
		Expression valueIfFalse) {
		this.condition = condition;
		this.valueIfTrue = valueIfTrue;
		this.valueIfFalse = valueIfFalse;
		this.sourceStart = condition.sourceStart;
		this.sourceEnd = valueIfFalse.sourceEnd;
	}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext,
			FlowInfo flowInfo) {
		int initialComplaintLevel = (flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0 ? Statement.COMPLAINED_FAKE_REACHABLE : Statement.NOT_COMPLAINED;
		Constant cst = this.condition.optimizedBooleanConstant();
		boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue() == true;
		boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && cst.booleanValue() == false;

		int mode = flowInfo.reachMode();
		flowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo, cst == Constant.NotAConstant);

		// process the if-true part
		FlowInfo trueFlowInfo = flowInfo.initsWhenTrue().copy();
		if (isConditionOptimizedFalse) {
			if ((mode & FlowInfo.UNREACHABLE) == 0) {
				trueFlowInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
			}
			if (!isKnowDeadCodePattern(this.condition) || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement) {
				this.valueIfTrue.complainIfUnreachable(trueFlowInfo, currentScope, initialComplaintLevel);
			}
		}
		this.trueInitStateIndex = currentScope.methodScope().recordInitializationStates(trueFlowInfo);
		trueFlowInfo = this.valueIfTrue.analyseCode(currentScope, flowContext, trueFlowInfo);

		// process the if-false part
		FlowInfo falseFlowInfo = flowInfo.initsWhenFalse().copy();
		if (isConditionOptimizedTrue) {
			if ((mode & FlowInfo.UNREACHABLE) == 0) {
				falseFlowInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
			}
			if (!isKnowDeadCodePattern(this.condition) || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement) {
				this.valueIfFalse.complainIfUnreachable(falseFlowInfo, currentScope, initialComplaintLevel);
			}
		}
		this.falseInitStateIndex = currentScope.methodScope().recordInitializationStates(falseFlowInfo);
		falseFlowInfo = this.valueIfFalse.analyseCode(currentScope, flowContext, falseFlowInfo);

		// merge if-true & if-false initializations
		FlowInfo mergedInfo;
		if (isConditionOptimizedTrue){
			mergedInfo = trueFlowInfo.addPotentialInitializationsFrom(falseFlowInfo);
			this.nullStatus = this.valueIfTrue.nullStatus(trueFlowInfo);
		} else if (isConditionOptimizedFalse) {
			mergedInfo = falseFlowInfo.addPotentialInitializationsFrom(trueFlowInfo);
			this.nullStatus = this.valueIfFalse.nullStatus(falseFlowInfo);
		} else {
			// this block must meet two conflicting requirements (see https://bugs.eclipse.org/324178):
			// (1) For null analysis of "Object o2 = (o1 != null) ? o1 : new Object();" we need to distinguish
			//     the paths *originating* from the evaluation of the condition to true/false respectively.
			//     This is used to determine the possible null status of the entire conditional expression.
			// (2) For definite assignment analysis (JLS 16.1.5) of boolean conditional expressions of the form
			//     "if (c1 ? expr1 : expr2) use(v);" we need to check whether any variable v will be definitely
			//     assigned whenever the entire conditional expression evaluates to true (to reach the then branch).
			//     I.e., we need to collect flowInfo *towards* the overall outcome true/false 
			//     (regardless of the evaluation of the condition).
			
			// to support (1) use the infos of both branches originating from the condition for computing the nullStatus:
			computeNullStatus(trueFlowInfo, falseFlowInfo);
			
			// to support (2) we split the true/false branches according to their inner structure. Consider this:
			// if (b ? false : (true && (v = false))) return v; -- ok
			// - expr1 ("false") has no path towards true (mark as unreachable)
			// - expr2 ("(true && (v = false))") has a branch towards true on which v is assigned.
			//   -> merging these two branches yields: v is assigned
			// - the paths towards false are irrelevant since the enclosing if has no else.
			cst = this.optimizedIfTrueConstant;
			boolean isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == true;
			boolean isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == false;

			cst = this.optimizedIfFalseConstant;
			boolean isValueIfFalseOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == true;
			boolean isValueIfFalseOptimizedFalse = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == false;

			UnconditionalFlowInfo trueFlowTowardsTrue = trueFlowInfo.initsWhenTrue().unconditionalCopy();
			UnconditionalFlowInfo falseFlowTowardsTrue = falseFlowInfo.initsWhenTrue().unconditionalCopy();
			UnconditionalFlowInfo trueFlowTowardsFalse = trueFlowInfo.initsWhenFalse().unconditionalInits();
			UnconditionalFlowInfo falseFlowTowardsFalse = falseFlowInfo.initsWhenFalse().unconditionalInits();
			if (isValueIfTrueOptimizedFalse) {
				trueFlowTowardsTrue.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);				
			}
			if (isValueIfFalseOptimizedFalse) {
				falseFlowTowardsTrue.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);	
			}
			if (isValueIfTrueOptimizedTrue) {
				trueFlowTowardsFalse.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);	
			}
			if (isValueIfFalseOptimizedTrue) {
				falseFlowTowardsFalse.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);	
			}
			mergedInfo =
				FlowInfo.conditional(
					trueFlowTowardsTrue.mergedWith(falseFlowTowardsTrue),
					trueFlowTowardsFalse.mergedWith(falseFlowTowardsFalse));
		}
		this.mergedInitStateIndex =
			currentScope.methodScope().recordInitializationStates(mergedInfo);
		mergedInfo.setReachMode(mode);
		return mergedInfo;
	}

	private void computeNullStatus(FlowInfo trueBranchInfo, FlowInfo falseBranchInfo) {
		// given that the condition cannot be optimized to a constant 
		// we now merge the nullStatus from both branches:
		int ifTrueNullStatus = this.valueIfTrue.nullStatus(trueBranchInfo);
		int ifFalseNullStatus = this.valueIfFalse.nullStatus(falseBranchInfo);

		if (ifTrueNullStatus == ifFalseNullStatus) {
			this.nullStatus = ifTrueNullStatus;
			return;
		}
		// is there a chance of null (or non-null)? -> potentially null etc.
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=133125
		int status = 0;
		int combinedStatus = ifTrueNullStatus|ifFalseNullStatus;
		if ((combinedStatus & (FlowInfo.NULL|FlowInfo.POTENTIALLY_NULL)) != 0)
			status |= FlowInfo.POTENTIALLY_NULL;
		if ((combinedStatus & (FlowInfo.NON_NULL|FlowInfo.POTENTIALLY_NON_NULL)) != 0)
			status |= FlowInfo.POTENTIALLY_NON_NULL;
		if ((combinedStatus & (FlowInfo.UNKNOWN|FlowInfo.POTENTIALLY_UNKNOWN)) != 0)
			status |= FlowInfo.POTENTIALLY_UNKNOWN;
		if (status > 0)
			this.nullStatus = status;
	}

	/**
	 * Code generation for the conditional operator ?:
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 * @param valueRequired boolean
	*/
	public void generateCode(
		BlockScope currentScope,
		CodeStream codeStream,
		boolean valueRequired) {

		int pc = codeStream.position;
		BranchLabel endifLabel, falseLabel;
		if (this.constant != Constant.NotAConstant) {
			if (valueRequired)
				codeStream.generateConstant(this.constant, this.implicitConversion);
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			return;
		}
		Constant cst = this.condition.optimizedBooleanConstant();
		boolean needTruePart = !(cst != Constant.NotAConstant && cst.booleanValue() == false);
		boolean needFalsePart = 	!(cst != Constant.NotAConstant && cst.booleanValue() == true);
		endifLabel = new BranchLabel(codeStream);

		// Generate code for the condition
		falseLabel = new BranchLabel(codeStream);
		falseLabel.tagBits |= BranchLabel.USED;
		this.condition.generateOptimizedBoolean(
			currentScope,
			codeStream,
			null,
			falseLabel,
			cst == Constant.NotAConstant);

		if (this.trueInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(
				currentScope,
				this.trueInitStateIndex);
			codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
		}
		// Then code generation
		if (needTruePart) {
			this.valueIfTrue.generateCode(currentScope, codeStream, valueRequired);
			if (needFalsePart) {
				// Jump over the else part
				int position = codeStream.position;
				codeStream.goto_(endifLabel);
				codeStream.updateLastRecordedEndPC(currentScope, position);
				// Tune codestream stack size
				if (valueRequired) {
					switch(this.resolvedType.id) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							codeStream.decrStackSize(2);
							break;
						default :
							codeStream.decrStackSize(1);
							break;
					}
				}
			}
		}
		if (needFalsePart) {
			if (this.falseInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(
					currentScope,
					this.falseInitStateIndex);
				codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
			}
			if (falseLabel.forwardReferenceCount() > 0) {
				falseLabel.place();
			}
			this.valueIfFalse.generateCode(currentScope, codeStream, valueRequired);
			if (valueRequired) {
				codeStream.recordExpressionType(this.resolvedType);
			}
			if (needTruePart) {
				// End of if statement
				endifLabel.place();
			}
		}
		// May loose some local variable initializations : affecting the local variable attributes
		if (this.mergedInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(
				currentScope,
				this.mergedInitStateIndex);
		}
		// implicit conversion
		if (valueRequired)
			codeStream.generateImplicitConversion(this.implicitConversion);
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

	/**
	 * Optimized boolean code generation for the conditional operator ?:
	*/
	public void generateOptimizedBoolean(
		BlockScope currentScope,
		CodeStream codeStream,
		BranchLabel trueLabel,
		BranchLabel falseLabel,
		boolean valueRequired) {

		if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == T_boolean) // constant
			|| ((this.valueIfTrue.implicitConversion & IMPLICIT_CONVERSION_MASK) >> 4) != T_boolean) { // non boolean values
			super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
			return;
		}
		Constant cst = this.condition.constant;
		Constant condCst = this.condition.optimizedBooleanConstant();
		boolean needTruePart =
			!(((cst != Constant.NotAConstant) && (cst.booleanValue() == false))
				|| ((condCst != Constant.NotAConstant) && (condCst.booleanValue() == false)));
		boolean needFalsePart =
			!(((cst != Constant.NotAConstant) && (cst.booleanValue() == true))
				|| ((condCst != Constant.NotAConstant) && (condCst.booleanValue() == true)));

		BranchLabel internalFalseLabel, endifLabel = new BranchLabel(codeStream);

		// Generate code for the condition
		boolean needConditionValue = (cst == Constant.NotAConstant) && (condCst == Constant.NotAConstant);
		this.condition.generateOptimizedBoolean(
				currentScope,
				codeStream,
				null,
				internalFalseLabel = new BranchLabel(codeStream),
				needConditionValue);

		if (this.trueInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(
				currentScope,
				this.trueInitStateIndex);
			codeStream.addDefinitelyAssignedVariables(currentScope, this.trueInitStateIndex);
		}
		// Then code generation
		if (needTruePart) {
			this.valueIfTrue.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);

			if (needFalsePart) {
				// Jump over the else part
				JumpEndif: {
					if (falseLabel == null) {
						if (trueLabel != null) {
							// implicit falling through the FALSE case
							cst = this.optimizedIfTrueConstant;
							boolean isValueIfTrueOptimizedTrue = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == true;
							if (isValueIfTrueOptimizedTrue) break JumpEndif; // no need to jump over, since branched to true already
						}
					} else {
						// implicit falling through the TRUE case
						if (trueLabel == null) {
							cst = this.optimizedIfTrueConstant;
							boolean isValueIfTrueOptimizedFalse = cst != null && cst != Constant.NotAConstant && cst.booleanValue() == false;
							if (isValueIfTrueOptimizedFalse) break JumpEndif; // no need to jump over, since branched to false already
						} else {
							// no implicit fall through TRUE/FALSE --> should never occur
						}
					}
					int position = codeStream.position;
					codeStream.goto_(endifLabel);
					codeStream.updateLastRecordedEndPC(currentScope, position);
				}
				// No need to decrement codestream stack size
				// since valueIfTrue was already consumed by branch bytecode
			}
		}
		if (needFalsePart) {
			internalFalseLabel.place();
			if (this.falseInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
				codeStream.addDefinitelyAssignedVariables(currentScope, this.falseInitStateIndex);
			}
			this.valueIfFalse.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);

			// End of if statement
			endifLabel.place();
		}
		// May loose some local variable initializations : affecting the local variable attributes
		if (this.mergedInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
		}
		// no implicit conversion for boolean values
		codeStream.updateLastRecordedEndPC(currentScope, codeStream.position);
	}

	public int nullStatus(FlowInfo flowInfo) {
		return this.nullStatus;
	}

	public Constant optimizedBooleanConstant() {

		return this.optimizedBooleanConstant == null ? this.constant : this.optimizedBooleanConstant;
	}

	public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output) {

		this.condition.printExpression(indent, output).append(" ? "); //$NON-NLS-1$
		this.valueIfTrue.printExpression(0, output).append(" : "); //$NON-NLS-1$
		return this.valueIfFalse.printExpression(0, output);
	}

	public TypeBinding resolveType(BlockScope scope) {
		// JLS3 15.25
		this.constant = Constant.NotAConstant;
		LookupEnvironment env = scope.environment();
		boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
		TypeBinding conditionType = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
		this.condition.computeConversion(scope, TypeBinding.BOOLEAN, conditionType);

		if (this.valueIfTrue instanceof CastExpression) this.valueIfTrue.bits |= DisableUnnecessaryCastCheck; // will check later on
		TypeBinding originalValueIfTrueType = this.valueIfTrue.resolveType(scope);

		if (this.valueIfFalse instanceof CastExpression) this.valueIfFalse.bits |= DisableUnnecessaryCastCheck; // will check later on
		TypeBinding originalValueIfFalseType = this.valueIfFalse.resolveType(scope);

		if (conditionType == null || originalValueIfTrueType == null || originalValueIfFalseType == null)
			return null;

		TypeBinding valueIfTrueType = originalValueIfTrueType;
		TypeBinding valueIfFalseType = originalValueIfFalseType;
		if (use15specifics && valueIfTrueType != valueIfFalseType) {
			if (valueIfTrueType.isBaseType()) {
				if (valueIfFalseType.isBaseType()) {
					// bool ? baseType : baseType
					if (valueIfTrueType == TypeBinding.NULL) {  // bool ? null : 12 --> Integer
						valueIfFalseType = env.computeBoxingType(valueIfFalseType); // boxing
					} else if (valueIfFalseType == TypeBinding.NULL) {  // bool ? 12 : null --> Integer
						valueIfTrueType = env.computeBoxingType(valueIfTrueType); // boxing
					}
				} else {
					// bool ? baseType : nonBaseType
					TypeBinding unboxedIfFalseType = valueIfFalseType.isBaseType() ? valueIfFalseType : env.computeBoxingType(valueIfFalseType);
					if (valueIfTrueType.isNumericType() && unboxedIfFalseType.isNumericType()) {
						valueIfFalseType = unboxedIfFalseType; // unboxing
					} else if (valueIfTrueType != TypeBinding.NULL) {  // bool ? 12 : new Integer(12) --> int
						valueIfFalseType = env.computeBoxingType(valueIfFalseType); // unboxing
					}
				}
			} else if (valueIfFalseType.isBaseType()) {
					// bool ? nonBaseType : baseType
					TypeBinding unboxedIfTrueType = valueIfTrueType.isBaseType() ? valueIfTrueType : env.computeBoxingType(valueIfTrueType);
					if (unboxedIfTrueType.isNumericType() && valueIfFalseType.isNumericType()) {
						valueIfTrueType = unboxedIfTrueType; // unboxing
					} else if (valueIfFalseType != TypeBinding.NULL) {  // bool ? new Integer(12) : 12 --> int
						valueIfTrueType = env.computeBoxingType(valueIfTrueType); // unboxing
					}
			} else {
					// bool ? nonBaseType : nonBaseType
					TypeBinding unboxedIfTrueType = env.computeBoxingType(valueIfTrueType);
					TypeBinding unboxedIfFalseType = env.computeBoxingType(valueIfFalseType);
					if (unboxedIfTrueType.isNumericType() && unboxedIfFalseType.isNumericType()) {
						valueIfTrueType = unboxedIfTrueType;
						valueIfFalseType = unboxedIfFalseType;
					}
			}
		}
		// Propagate the constant value from the valueIfTrue and valueIFFalse expression if it is possible
		Constant condConstant, trueConstant, falseConstant;
		if ((condConstant = this.condition.constant) != Constant.NotAConstant
			&& (trueConstant = this.valueIfTrue.constant) != Constant.NotAConstant
			&& (falseConstant = this.valueIfFalse.constant) != Constant.NotAConstant) {
			// all terms are constant expression so we can propagate the constant
			// from valueIFTrue or valueIfFalse to the receiver constant
			this.constant = condConstant.booleanValue() ? trueConstant : falseConstant;
		}
		if (valueIfTrueType == valueIfFalseType) { // harmed the implicit conversion
			this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
			this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
			if (valueIfTrueType == TypeBinding.BOOLEAN) {
				this.optimizedIfTrueConstant = this.valueIfTrue.optimizedBooleanConstant();
				this.optimizedIfFalseConstant = this.valueIfFalse.optimizedBooleanConstant();
				if (this.optimizedIfTrueConstant != Constant.NotAConstant
						&& this.optimizedIfFalseConstant != Constant.NotAConstant
						&& this.optimizedIfTrueConstant.booleanValue() == this.optimizedIfFalseConstant.booleanValue()) {
					// a ? true : true  /   a ? false : false
					this.optimizedBooleanConstant = this.optimizedIfTrueConstant;
				} else if ((condConstant = this.condition.optimizedBooleanConstant()) != Constant.NotAConstant) { // Propagate the optimized boolean constant if possible
					this.optimizedBooleanConstant = condConstant.booleanValue()
						? this.optimizedIfTrueConstant
						: this.optimizedIfFalseConstant;
				}
			}
			return this.resolvedType = valueIfTrueType;
		}
		// Determine the return type depending on argument types
		// Numeric types
		if (valueIfTrueType.isNumericType() && valueIfFalseType.isNumericType()) {
			// (Short x Byte) or (Byte x Short)"
			if ((valueIfTrueType == TypeBinding.BYTE && valueIfFalseType == TypeBinding.SHORT)
				|| (valueIfTrueType == TypeBinding.SHORT && valueIfFalseType == TypeBinding.BYTE)) {
				this.valueIfTrue.computeConversion(scope, TypeBinding.SHORT, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, TypeBinding.SHORT, originalValueIfFalseType);
				return this.resolvedType = TypeBinding.SHORT;
			}
			// <Byte|Short|Char> x constant(Int)  ---> <Byte|Short|Char>   and reciprocally
			if ((valueIfTrueType == TypeBinding.BYTE || valueIfTrueType == TypeBinding.SHORT || valueIfTrueType == TypeBinding.CHAR)
					&& (valueIfFalseType == TypeBinding.INT
						&& this.valueIfFalse.isConstantValueOfTypeAssignableToType(valueIfFalseType, valueIfTrueType))) {
				this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, valueIfTrueType, originalValueIfFalseType);
				return this.resolvedType = valueIfTrueType;
			}
			if ((valueIfFalseType == TypeBinding.BYTE
					|| valueIfFalseType == TypeBinding.SHORT
					|| valueIfFalseType == TypeBinding.CHAR)
					&& (valueIfTrueType == TypeBinding.INT
						&& this.valueIfTrue.isConstantValueOfTypeAssignableToType(valueIfTrueType, valueIfFalseType))) {
				this.valueIfTrue.computeConversion(scope, valueIfFalseType, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
				return this.resolvedType = valueIfFalseType;
			}
			// Manual binary numeric promotion
			// int
			if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, T_int)
					&& BaseTypeBinding.isNarrowing(valueIfFalseType.id, T_int)) {
				this.valueIfTrue.computeConversion(scope, TypeBinding.INT, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, TypeBinding.INT, originalValueIfFalseType);
				return this.resolvedType = TypeBinding.INT;
			}
			// long
			if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, T_long)
					&& BaseTypeBinding.isNarrowing(valueIfFalseType.id, T_long)) {
				this.valueIfTrue.computeConversion(scope, TypeBinding.LONG, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, TypeBinding.LONG, originalValueIfFalseType);
				return this.resolvedType = TypeBinding.LONG;
			}
			// float
			if (BaseTypeBinding.isNarrowing(valueIfTrueType.id, T_float)
					&& BaseTypeBinding.isNarrowing(valueIfFalseType.id, T_float)) {
				this.valueIfTrue.computeConversion(scope, TypeBinding.FLOAT, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, TypeBinding.FLOAT, originalValueIfFalseType);
				return this.resolvedType = TypeBinding.FLOAT;
			}
			// double
			this.valueIfTrue.computeConversion(scope, TypeBinding.DOUBLE, originalValueIfTrueType);
			this.valueIfFalse.computeConversion(scope, TypeBinding.DOUBLE, originalValueIfFalseType);
			return this.resolvedType = TypeBinding.DOUBLE;
		}
		// Type references (null null is already tested)
		if (valueIfTrueType.isBaseType() && valueIfTrueType != TypeBinding.NULL) {
			if (use15specifics) {
				valueIfTrueType = env.computeBoxingType(valueIfTrueType);
			} else {
				scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
				return null;
			}
		}
		if (valueIfFalseType.isBaseType() && valueIfFalseType != TypeBinding.NULL) {
			if (use15specifics) {
				valueIfFalseType = env.computeBoxingType(valueIfFalseType);
			} else {
				scope.problemReporter().conditionalArgumentsIncompatibleTypes(this, valueIfTrueType, valueIfFalseType);
				return null;
			}
		}
		if (use15specifics) {
			// >= 1.5 : LUB(operand types) must exist
			TypeBinding commonType = null;
			if (valueIfTrueType == TypeBinding.NULL) {
				commonType = valueIfFalseType;
			} else if (valueIfFalseType == TypeBinding.NULL) {
				commonType = valueIfTrueType;
			} else {
				commonType = scope.lowerUpperBound(new TypeBinding[] { valueIfTrueType, valueIfFalseType });
			}
			if (commonType != null) {
				this.valueIfTrue.computeConversion(scope, commonType, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, commonType, originalValueIfFalseType);
				return this.resolvedType = commonType.capture(scope, this.sourceEnd);
			}
		} else {
			// < 1.5 : one operand must be convertible to the other
			if (valueIfFalseType.isCompatibleWith(valueIfTrueType)) {
				this.valueIfTrue.computeConversion(scope, valueIfTrueType, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, valueIfTrueType, originalValueIfFalseType);
				return this.resolvedType = valueIfTrueType;
			} else if (valueIfTrueType.isCompatibleWith(valueIfFalseType)) {
				this.valueIfTrue.computeConversion(scope, valueIfFalseType, originalValueIfTrueType);
				this.valueIfFalse.computeConversion(scope, valueIfFalseType, originalValueIfFalseType);
				return this.resolvedType = valueIfFalseType;
			}
		}
		scope.problemReporter().conditionalArgumentsIncompatibleTypes(
			this,
			valueIfTrueType,
			valueIfFalseType);
		return null;
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			this.condition.traverse(visitor, scope);
			this.valueIfTrue.traverse(visitor, scope);
			this.valueIfFalse.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}
}
