/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 292478 - Report potentially null across variable assignment
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
import org.eclipse.jdt.internal.compiler.util.Messages;

public abstract class Expression extends Statement {

	public Constant constant;

	public int statementEnd = -1;

	//Some expression may not be used - from a java semantic point
	//of view only - as statements. Other may. In order to avoid the creation
	//of wrappers around expression in order to tune them as expression
	//Expression is a subclass of Statement. See the message isValidJavaStatement()

	public int implicitConversion;
	public TypeBinding resolvedType;

public static final boolean isConstantValueRepresentable(Constant constant, int constantTypeID, int targetTypeID) {
	//true if there is no loss of precision while casting.
	// constantTypeID == constant.typeID
	if (targetTypeID == constantTypeID)
		return true;
	switch (targetTypeID) {
		case T_char :
			switch (constantTypeID) {
				case T_char :
					return true;
				case T_double :
					return constant.doubleValue() == constant.charValue();
				case T_float :
					return constant.floatValue() == constant.charValue();
				case T_int :
					return constant.intValue() == constant.charValue();
				case T_short :
					return constant.shortValue() == constant.charValue();
				case T_byte :
					return constant.byteValue() == constant.charValue();
				case T_long :
					return constant.longValue() == constant.charValue();
				default :
					return false;//boolean
			}

		case T_float :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.floatValue();
				case T_double :
					return constant.doubleValue() == constant.floatValue();
				case T_float :
					return true;
				case T_int :
					return constant.intValue() == constant.floatValue();
				case T_short :
					return constant.shortValue() == constant.floatValue();
				case T_byte :
					return constant.byteValue() == constant.floatValue();
				case T_long :
					return constant.longValue() == constant.floatValue();
				default :
					return false;//boolean
			}

		case T_double :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.doubleValue();
				case T_double :
					return true;
				case T_float :
					return constant.floatValue() == constant.doubleValue();
				case T_int :
					return constant.intValue() == constant.doubleValue();
				case T_short :
					return constant.shortValue() == constant.doubleValue();
				case T_byte :
					return constant.byteValue() == constant.doubleValue();
				case T_long :
					return constant.longValue() == constant.doubleValue();
				default :
					return false; //boolean
			}

		case T_byte :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.byteValue();
				case T_double :
					return constant.doubleValue() == constant.byteValue();
				case T_float :
					return constant.floatValue() == constant.byteValue();
				case T_int :
					return constant.intValue() == constant.byteValue();
				case T_short :
					return constant.shortValue() == constant.byteValue();
				case T_byte :
					return true;
				case T_long :
					return constant.longValue() == constant.byteValue();
				default :
					return false; //boolean
			}

		case T_short :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.shortValue();
				case T_double :
					return constant.doubleValue() == constant.shortValue();
				case T_float :
					return constant.floatValue() == constant.shortValue();
				case T_int :
					return constant.intValue() == constant.shortValue();
				case T_short :
					return true;
				case T_byte :
					return constant.byteValue() == constant.shortValue();
				case T_long :
					return constant.longValue() == constant.shortValue();
				default :
					return false; //boolean
			}

		case T_int :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.intValue();
				case T_double :
					return constant.doubleValue() == constant.intValue();
				case T_float :
					return constant.floatValue() == constant.intValue();
				case T_int :
					return true;
				case T_short :
					return constant.shortValue() == constant.intValue();
				case T_byte :
					return constant.byteValue() == constant.intValue();
				case T_long :
					return constant.longValue() == constant.intValue();
				default :
					return false; //boolean
			}

		case T_long :
			switch (constantTypeID) {
				case T_char :
					return constant.charValue() == constant.longValue();
				case T_double :
					return constant.doubleValue() == constant.longValue();
				case T_float :
					return constant.floatValue() == constant.longValue();
				case T_int :
					return constant.intValue() == constant.longValue();
				case T_short :
					return constant.shortValue() == constant.longValue();
				case T_byte :
					return constant.byteValue() == constant.longValue();
				case T_long :
					return true;
				default :
					return false; //boolean
			}

		default :
			return false; //boolean
	}
}

public Expression() {
	super();
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	return flowInfo;
}

/**
 * More sophisticated for of the flow analysis used for analyzing expressions, and be able to optimize out
 * portions of expressions where no actual value is required.
 *
 * @param currentScope
 * @param flowContext
 * @param flowInfo
 * @param valueRequired
 * @return The state of initialization after the analysis of the current expression
 */
public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
	return analyseCode(currentScope, flowContext, flowInfo);
}

/**
 * Returns false if cast is not legal.
 */
public final boolean checkCastTypesCompatibility(Scope scope, TypeBinding castType, TypeBinding expressionType, Expression expression) {
	// see specifications 5.5
	// handle errors and process constant when needed

	// if either one of the type is null ==>
	// some error has been already reported some where ==>
	// we then do not report an obvious-cascade-error.

	if (castType == null || expressionType == null) return true;

	// identity conversion cannot be performed upfront, due to side-effects
	// like constant propagation
	boolean use15specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
	boolean use17specifics = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_7;
	if (castType.isBaseType()) {
		if (expressionType.isBaseType()) {
			if (expressionType == castType) {
				if (expression != null) {
					this.constant = expression.constant; //use the same constant
				}
				tagAsUnnecessaryCast(scope, castType);
				return true;
			}
			boolean necessary = false;
			if (expressionType.isCompatibleWith(castType)
					|| (necessary = BaseTypeBinding.isNarrowing(castType.id, expressionType.id))) {
				if (expression != null) {
					expression.implicitConversion = (castType.id << 4) + expressionType.id;
					if (expression.constant != Constant.NotAConstant) {
						this.constant = expression.constant.castTo(expression.implicitConversion);
					}
				}
				if (!necessary) tagAsUnnecessaryCast(scope, castType);
				return true;

			}
		} else if (use17specifics && expressionType.id == TypeIds.T_JavaLangObject){
			// cast from Object to base type allowed from 1.7, see JLS $5.5
			return true;
		} else if (use15specifics
							&& scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) { // unboxing - only widening match is allowed
			tagAsUnnecessaryCast(scope, castType);
			return true;
		}
		return false;
	} else if (use15specifics
						&& expressionType.isBaseType()
						&& scope.environment().computeBoxingType(expressionType).isCompatibleWith(castType)) { // boxing - only widening match is allowed
		tagAsUnnecessaryCast(scope, castType);
		return true;
	}

	switch(expressionType.kind()) {
		case Binding.BASE_TYPE :
			//-----------cast to something which is NOT a base type--------------------------
			if (expressionType == TypeBinding.NULL) {
				tagAsUnnecessaryCast(scope, castType);
				return true; //null is compatible with every thing
			}
			return false;

		case Binding.ARRAY_TYPE :
			if (castType == expressionType) {
				tagAsUnnecessaryCast(scope, castType);
				return true; // identity conversion
			}
			switch (castType.kind()) {
				case Binding.ARRAY_TYPE :
					// ( ARRAY ) ARRAY
					TypeBinding castElementType = ((ArrayBinding) castType).elementsType();
					TypeBinding exprElementType = ((ArrayBinding) expressionType).elementsType();
					if (exprElementType.isBaseType() || castElementType.isBaseType()) {
						if (castElementType == exprElementType) {
							tagAsNeedCheckCast();
							return true;
						}
						return false;
					}
					// recurse on array type elements
					return checkCastTypesCompatibility(scope, castElementType, exprElementType, expression);

				case Binding.TYPE_PARAMETER :
					// ( TYPE_PARAMETER ) ARRAY
					TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
					if (match == null) {
						checkUnsafeCast(scope, castType, expressionType, null /*no match*/, true);
					}
					// recurse on the type variable upper bound
					return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);

				default:
					// ( CLASS/INTERFACE ) ARRAY
					switch (castType.id) {
						case T_JavaLangCloneable :
						case T_JavaIoSerializable :
							tagAsNeedCheckCast();
							return true;
						case T_JavaLangObject :
							tagAsUnnecessaryCast(scope, castType);
							return true;
						default :
							return false;
					}
			}

		case Binding.TYPE_PARAMETER :
			TypeBinding match = expressionType.findSuperTypeOriginatingFrom(castType);
			if (match != null) {
				return checkUnsafeCast(scope, castType, expressionType, match, false);
			}
			// recursively on the type variable upper bound
			return checkCastTypesCompatibility(scope, castType, ((TypeVariableBinding)expressionType).upperBound(), expression);

		case Binding.WILDCARD_TYPE :
		case Binding.INTERSECTION_TYPE :
			match = expressionType.findSuperTypeOriginatingFrom(castType);
			if (match != null) {
				return checkUnsafeCast(scope, castType, expressionType, match, false);
			}
			// recursively on the type variable upper bound
			return checkCastTypesCompatibility(scope, castType, ((WildcardBinding)expressionType).bound, expression);

		default:
			if (expressionType.isInterface()) {
				switch (castType.kind()) {
					case Binding.ARRAY_TYPE :
						// ( ARRAY ) INTERFACE
						switch (expressionType.id) {
							case T_JavaLangCloneable :
							case T_JavaIoSerializable :
								tagAsNeedCheckCast();
								return true;
							default :
								return false;
						}

					case Binding.TYPE_PARAMETER :
						// ( INTERFACE ) TYPE_PARAMETER
						match = expressionType.findSuperTypeOriginatingFrom(castType);
						if (match == null) {
							checkUnsafeCast(scope, castType, expressionType, null /*no match*/, true);
						}
						// recurse on the type variable upper bound
						return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);

					default :
						if (castType.isInterface()) {
							// ( INTERFACE ) INTERFACE
							ReferenceBinding interfaceType = (ReferenceBinding) expressionType;
							match = interfaceType.findSuperTypeOriginatingFrom(castType);
							if (match != null) {
								return checkUnsafeCast(scope, castType, interfaceType, match, false);
							}
							tagAsNeedCheckCast();
							match = castType.findSuperTypeOriginatingFrom(interfaceType);
							if (match != null) {
								return checkUnsafeCast(scope, castType, interfaceType, match, true);
							}
							if (use15specifics) {
								checkUnsafeCast(scope, castType, expressionType, null /*no match*/, true);
								// ensure there is no collision between both interfaces: i.e. I1 extends List<String>, I2 extends List<Object>
								if (scope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_7) {
									if (interfaceType.hasIncompatibleSuperType((ReferenceBinding) castType)) {
										return false;
									}
								} else if (!castType.isRawType() && interfaceType.hasIncompatibleSuperType((ReferenceBinding) castType)) {
									return false;
								}
							} else {
								// pre1.5 semantics - no covariance allowed (even if 1.5 compliant, but 1.4 source)
								// look at original methods rather than the parameterized variants at 1.4 to detect
								// covariance. Otherwise when confronted with one raw type and one parameterized type,
								// we could mistakenly detect covariance and scream foul. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=332744
								MethodBinding[] castTypeMethods = getAllOriginalInheritedMethods((ReferenceBinding) castType);
								MethodBinding[] expressionTypeMethods = getAllOriginalInheritedMethods((ReferenceBinding) expressionType);
								int exprMethodsLength = expressionTypeMethods.length;
								for (int i = 0, castMethodsLength = castTypeMethods.length; i < castMethodsLength; i++) {
									for (int j = 0; j < exprMethodsLength; j++) {
										if ((castTypeMethods[i].returnType != expressionTypeMethods[j].returnType)
												&& (CharOperation.equals(castTypeMethods[i].selector, expressionTypeMethods[j].selector))
												&& castTypeMethods[i].areParametersEqual(expressionTypeMethods[j])) {
											return false;

										}
									}
								}
							}
							return true;
						} else {
							// ( CLASS ) INTERFACE
							if (castType.id == TypeIds.T_JavaLangObject) { // no runtime error
								tagAsUnnecessaryCast(scope, castType);
								return true;
							}
							// can only be a downcast
							tagAsNeedCheckCast();
							match = castType.findSuperTypeOriginatingFrom(expressionType);
							if (match != null) {
								return checkUnsafeCast(scope, castType, expressionType, match, true);
							}
							if (((ReferenceBinding) castType).isFinal()) {
								// no subclass for castType, thus compile-time check is invalid
								return false;
							}
							if (use15specifics) {
								checkUnsafeCast(scope, castType, expressionType, null /*no match*/, true);
								// ensure there is no collision between both interfaces: i.e. I1 extends List<String>, I2 extends List<Object>
								if (scope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_7) {
									if (((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding) expressionType)) {
										return false;
									}
								} else if (!castType.isRawType() && ((ReferenceBinding)castType).hasIncompatibleSuperType((ReferenceBinding) expressionType)) {
									return false;
								}
							}
							return true;
						}
				}
			} else {
				switch (castType.kind()) {
					case Binding.ARRAY_TYPE :
						// ( ARRAY ) CLASS
						if (expressionType.id == TypeIds.T_JavaLangObject) { // potential runtime error
							if (use15specifics) checkUnsafeCast(scope, castType, expressionType, expressionType, true);
							tagAsNeedCheckCast();
							return true;
						}
						return false;

					case Binding.TYPE_PARAMETER :
						// ( TYPE_PARAMETER ) CLASS
						match = expressionType.findSuperTypeOriginatingFrom(castType);
						if (match == null) {
							checkUnsafeCast(scope, castType, expressionType, null, true);
						}
						// recurse on the type variable upper bound
						return checkCastTypesCompatibility(scope, ((TypeVariableBinding)castType).upperBound(), expressionType, expression);

					default :
						if (castType.isInterface()) {
							// ( INTERFACE ) CLASS
							ReferenceBinding refExprType = (ReferenceBinding) expressionType;
							match = refExprType.findSuperTypeOriginatingFrom(castType);
							if (match != null) {
								return checkUnsafeCast(scope, castType, expressionType, match, false);
							}
							// unless final a subclass may implement the interface ==> no check at compile time
							if (refExprType.isFinal()) {
								return false;
							}
							tagAsNeedCheckCast();
							match = castType.findSuperTypeOriginatingFrom(expressionType);
							if (match != null) {
								return checkUnsafeCast(scope, castType, expressionType, match, true);
							}
							if (use15specifics) {
								checkUnsafeCast(scope, castType, expressionType, null /*no match*/, true);
								// ensure there is no collision between both interfaces: i.e. I1 extends List<String>, I2 extends List<Object>
								if (scope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_7) {
									if (refExprType.hasIncompatibleSuperType((ReferenceBinding) castType)) {
										return false;
									}
								} else if (!castType.isRawType() && refExprType.hasIncompatibleSuperType((ReferenceBinding) castType)) {
									return false;
								}
							}
							return true;
						} else {
							// ( CLASS ) CLASS
							match = expressionType.findSuperTypeOriginatingFrom(castType);
							if (match != null) {
								if (expression != null && castType.id == TypeIds.T_JavaLangString) this.constant = expression.constant; // (String) cst is still a constant
								return checkUnsafeCast(scope, castType, expressionType, match, false);
							}
							match = castType.findSuperTypeOriginatingFrom(expressionType);
							if (match != null) {
								tagAsNeedCheckCast();
								return checkUnsafeCast(scope, castType, expressionType, match, true);
							}
							return false;
						}
				}
			}
	}
}

/**
 * Check the local variable of this expression, if any, against potential NPEs
 * given a flow context and an upstream flow info. If so, report the risk to
 * the context. Marks the local as checked, which affects the flow info.
 * @param scope the scope of the analysis
 * @param flowContext the current flow context
 * @param flowInfo the upstream flow info; caveat: may get modified
 */
public void checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo) {
	LocalVariableBinding local = localVariableBinding();
	if (local != null &&
			(local.type.tagBits & TagBits.IsBaseType) == 0) {
		if ((this.bits & ASTNode.IsNonNull) == 0) {
			flowContext.recordUsingNullReference(scope, local, this,
					FlowContext.MAY_NULL, flowInfo);
		}
		flowInfo.markAsComparedEqualToNonNull(local);
			// from thereon it is set
		if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0) {
			flowInfo.markedAsNullOrNonNullInAssertExpression(local);
		}
		if (flowContext.initsOnFinally != null) {
			flowContext.initsOnFinally.markAsComparedEqualToNonNull(local);
			if ((flowContext.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) != 0) {
				flowContext.initsOnFinally.markedAsNullOrNonNullInAssertExpression(local);
			}
		}
	}
}

public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
	if (match == castType) {
		if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
		return true;
	}
	if (match != null && (!castType.isReifiable() || !expressionType.isReifiable())) {
		if(isNarrowing
				? match.isProvablyDistinct(expressionType)
				: castType.isProvablyDistinct(match)) {
			return false;
		}
	}
	if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
	return true;
}

/**
 * Base types need that the widening is explicitly done by the compiler using some bytecode like i2f.
 * Also check unsafe type operations.
 */
public void computeConversion(Scope scope, TypeBinding runtimeType, TypeBinding compileTimeType) {
	if (runtimeType == null || compileTimeType == null)
		return;
	if (this.implicitConversion != 0) return; // already set independantly

	// it is possible for a Byte to be unboxed to a byte & then converted to an int
	// but it is not possible for a byte to become Byte & then assigned to an Integer,
	// or to become an int before boxed into an Integer
	if (runtimeType != TypeBinding.NULL && runtimeType.isBaseType()) {
		if (!compileTimeType.isBaseType()) {
			TypeBinding unboxedType = scope.environment().computeBoxingType(compileTimeType);
			this.implicitConversion = TypeIds.UNBOXING;
			scope.problemReporter().autoboxing(this, compileTimeType, runtimeType);
			compileTimeType = unboxedType;
		}
	} else if (compileTimeType != TypeBinding.NULL && compileTimeType.isBaseType()) {
		TypeBinding boxedType = scope.environment().computeBoxingType(runtimeType);
		if (boxedType == runtimeType) // Object o = 12;
			boxedType = compileTimeType;
		this.implicitConversion = TypeIds.BOXING | (boxedType.id << 4) + compileTimeType.id;
		scope.problemReporter().autoboxing(this, compileTimeType, scope.environment().computeBoxingType(boxedType));
		return;
	} else if (this.constant != Constant.NotAConstant && this.constant.typeID() != TypeIds.T_JavaLangString) {
		this.implicitConversion = TypeIds.BOXING;
		return;
	}
	int compileTimeTypeID, runtimeTypeID;
	if ((compileTimeTypeID = compileTimeType.id) == TypeIds.NoId) { // e.g. ? extends String  ==> String (103227)
		compileTimeTypeID = compileTimeType.erasure().id == TypeIds.T_JavaLangString ? TypeIds.T_JavaLangString : TypeIds.T_JavaLangObject;
	}
	switch (runtimeTypeID = runtimeType.id) {
		case T_byte :
		case T_short :
		case T_char :
			if (compileTimeTypeID == TypeIds.T_JavaLangObject) {
				this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
			} else {
				this.implicitConversion |= (TypeIds.T_int << 4) + compileTimeTypeID;
			}
			break;
		case T_JavaLangString :
		case T_float :
		case T_boolean :
		case T_double :
		case T_int : //implicitConversion may result in i2i which will result in NO code gen
		case T_long :
			this.implicitConversion |= (runtimeTypeID << 4) + compileTimeTypeID;
			break;
		default : // regular object ref
//				if (compileTimeType.isRawType() && runtimeTimeType.isBoundParameterizedType()) {
//				    scope.problemReporter().unsafeRawExpression(this, compileTimeType, runtimeTimeType);
//				}
	}
}

/**
 * Expression statements are plain expressions, however they generate like
 * normal expressions with no value required.
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream) {
	if ((this.bits & ASTNode.IsReachable) == 0) {
		return;
	}
	generateCode(currentScope, codeStream, false);
}

/**
 * Every expression is responsible for generating its implicit conversion when necessary.
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 * @param valueRequired boolean
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	if (this.constant != Constant.NotAConstant) {
		// generate a constant expression
		int pc = codeStream.position;
		codeStream.generateConstant(this.constant, this.implicitConversion);
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	} else {
		// actual non-constant code generation
		throw new ShouldNotImplement(Messages.ast_missingCode);
	}
}

/**
 * Default generation of a boolean value
 * @param currentScope
 * @param codeStream
 * @param trueLabel
 * @param falseLabel
 * @param valueRequired
 */
public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired) {
	// a label valued to nil means: by default we fall through the case...
	// both nil means we leave the value on the stack

	Constant cst = optimizedBooleanConstant();
	generateCode(currentScope, codeStream, valueRequired && cst == Constant.NotAConstant);
	if ((cst != Constant.NotAConstant) && (cst.typeID() == TypeIds.T_boolean)) {
		int pc = codeStream.position;
		if (cst.booleanValue() == true) {
			// constant == true
			if (valueRequired) {
				if (falseLabel == null) {
					// implicit falling through the FALSE case
					if (trueLabel != null) {
						codeStream.goto_(trueLabel);
					}
				}
			}
		} else {
			if (valueRequired) {
				if (falseLabel != null) {
					// implicit falling through the TRUE case
					if (trueLabel == null) {
						codeStream.goto_(falseLabel);
					}
				}
			}
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
		return;
	}
	// branching
	int position = codeStream.position;
	if (valueRequired) {
		if (falseLabel == null) {
			if (trueLabel != null) {
				// Implicit falling through the FALSE case
				codeStream.ifne(trueLabel);
			}
		} else {
			if (trueLabel == null) {
				// Implicit falling through the TRUE case
				codeStream.ifeq(falseLabel);
			} else {
				// No implicit fall through TRUE/FALSE --> should never occur
			}
		}
	}
	// reposition the endPC
	codeStream.updateLastRecordedEndPC(currentScope, position);
}

/* Optimized (java) code generation for string concatenations that involve StringBuffer
 * creation: going through this path means that there is no need for a new StringBuffer
 * creation, further operands should rather be only appended to the current one.
 * By default: no optimization.
 */
public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID) {
	if (typeID == TypeIds.T_JavaLangString && this.constant != Constant.NotAConstant && this.constant.stringValue().length() == 0) {
		return; // optimize str + ""
	}
	generateCode(blockScope, codeStream, true);
	codeStream.invokeStringConcatenationAppendForType(typeID);
}

/* Optimized (java) code generation for string concatenations that involve StringBuffer
 * creation: going through this path means that there is no need for a new StringBuffer
 * creation, further operands should rather be only appended to the current one.
 */
public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID) {
	codeStream.newStringContatenation();
	codeStream.dup();
	switch (typeID) {
		case T_JavaLangObject :
		case T_undefined :
			// in the case the runtime value of valueOf(Object) returns null, we have to use append(Object) instead of directly valueOf(Object)
			// append(Object) returns append(valueOf(Object)), which means that the null case is handled by the next case.
			codeStream.invokeStringConcatenationDefaultConstructor();
			generateCode(blockScope, codeStream, true);
			codeStream.invokeStringConcatenationAppendForType(TypeIds.T_JavaLangObject);
			return;
		case T_JavaLangString :
		case T_null :
			if (this.constant != Constant.NotAConstant) {
				String stringValue = this.constant.stringValue();
				if (stringValue.length() == 0) {  // optimize ""+<str>
					codeStream.invokeStringConcatenationDefaultConstructor();
					return;
				}
				codeStream.ldc(stringValue);
			} else {
				// null case is not a constant
				generateCode(blockScope, codeStream, true);
				codeStream.invokeStringValueOf(TypeIds.T_JavaLangObject);
			}
			break;
		default :
			generateCode(blockScope, codeStream, true);
			codeStream.invokeStringValueOf(typeID);
	}
	codeStream.invokeStringConcatenationStringConstructor();
}

private MethodBinding[] getAllOriginalInheritedMethods(ReferenceBinding binding) {
	ArrayList collector = new ArrayList();
	getAllInheritedMethods0(binding, collector);
	for (int i = 0, len = collector.size(); i < len; i++) {
		collector.set(i, ((MethodBinding)collector.get(i)).original());
	}
	return (MethodBinding[]) collector.toArray(new MethodBinding[collector.size()]);
}

private void getAllInheritedMethods0(ReferenceBinding binding, ArrayList collector) {
	if (!binding.isInterface()) return;
	MethodBinding[] methodBindings = binding.methods();
	for (int i = 0, max = methodBindings.length; i < max; i++) {
		collector.add(methodBindings[i]);
	}
	ReferenceBinding[] superInterfaces = binding.superInterfaces();
	for (int i = 0, max = superInterfaces.length; i < max; i++) {
		getAllInheritedMethods0(superInterfaces[i], collector);
	}
}

public static Binding getDirectBinding(Expression someExpression) {
	if ((someExpression.bits & ASTNode.IgnoreNoEffectAssignCheck) != 0) {
		return null;
	}
	if (someExpression instanceof SingleNameReference) {
		return ((SingleNameReference)someExpression).binding;
	} else if (someExpression instanceof FieldReference) {
		FieldReference fieldRef = (FieldReference)someExpression;
		if (fieldRef.receiver.isThis() && !(fieldRef.receiver instanceof QualifiedThisReference)) {
			return fieldRef.binding;
		}
	} else if (someExpression instanceof Assignment) {
		Expression lhs = ((Assignment)someExpression).lhs;
		if ((lhs.bits & ASTNode.IsStrictlyAssigned) != 0) {
			// i = i = ...; // eq to int i = ...;
			return getDirectBinding (((Assignment)someExpression).lhs);
		} else if (someExpression instanceof PrefixExpression) {
			// i = i++; // eq to ++i;
			return getDirectBinding (((Assignment)someExpression).lhs);
		}
	} else if (someExpression instanceof QualifiedNameReference) {
		QualifiedNameReference qualifiedNameReference = (QualifiedNameReference) someExpression;
		if (qualifiedNameReference.indexOfFirstFieldBinding != 1
				&& qualifiedNameReference.otherBindings == null) {
			// case where a static field is retrieved using ClassName.fieldname
			return qualifiedNameReference.binding;
		}
	} else if (someExpression.isThis()) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=276741
		return someExpression.resolvedType;
	}
//		} else if (someExpression instanceof PostfixExpression) { // recurse for postfix: i++ --> i
//			// note: "b = b++" is equivalent to doing nothing, not to "b++"
//			return getDirectBinding(((PostfixExpression) someExpression).lhs);
	return null;
}

public boolean isCompactableOperation() {
	return false;
}

//Return true if the conversion is done AUTOMATICALLY by the vm
//while the javaVM is an int based-machine, thus for example pushing
//a byte onto the stack , will automatically create an int on the stack
//(this request some work d be done by the VM on signed numbers)
public boolean isConstantValueOfTypeAssignableToType(TypeBinding constantType, TypeBinding targetType) {

	if (this.constant == Constant.NotAConstant)
		return false;
	if (constantType == targetType)
		return true;
	//No free assignment conversion from anything but to integral ones.
	if (BaseTypeBinding.isWidening(TypeIds.T_int, constantType.id)
			&& (BaseTypeBinding.isNarrowing(targetType.id, TypeIds.T_int))) {
		//use current explicit conversion in order to get some new value to compare with current one
		return isConstantValueRepresentable(this.constant, constantType.id, targetType.id);
	}
	return false;
}

public boolean isTypeReference() {
	return false;
}

/**
 * Returns the local variable referenced by this node. Can be a direct reference (SingleNameReference)
 * or thru a cast expression etc...
 */
public LocalVariableBinding localVariableBinding() {
	return null;
}

/**
 * Mark this expression as being non null, per a specific tag in the
 * source code.
 */
// this is no more called for now, waiting for inter procedural null reference analysis
public void markAsNonNull() {
	this.bits |= ASTNode.IsNonNull;
}

public int nullStatus(FlowInfo flowInfo) {

	if (/* (this.bits & IsNonNull) != 0 || */
		this.constant != null && this.constant != Constant.NotAConstant)
	return FlowInfo.NON_NULL; // constant expression cannot be null

	LocalVariableBinding local = localVariableBinding();
	if (local != null)
		return flowInfo.nullStatus(local);
	return FlowInfo.NON_NULL;
}

/**
 * Constant usable for bytecode pattern optimizations, but cannot be inlined
 * since it is not strictly equivalent to the definition of constant expressions.
 * In particular, some side-effects may be required to occur (only the end value
 * is known).
 * @return Constant known to be of boolean type
 */
public Constant optimizedBooleanConstant() {
	return this.constant;
}

/**
 * Returns the type of the expression after required implicit conversions. When expression type gets promoted
 * or inserted a generic cast, the converted type will differ from the resolved type (surface side-effects from
 * #computeConversion(...)).
 * @return the type after implicit conversion
 */
public TypeBinding postConversionType(Scope scope) {
	TypeBinding convertedType = this.resolvedType;
	int runtimeType = (this.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4;
	switch (runtimeType) {
		case T_boolean :
			convertedType = TypeBinding.BOOLEAN;
			break;
		case T_byte :
			convertedType = TypeBinding.BYTE;
			break;
		case T_short :
			convertedType = TypeBinding.SHORT;
			break;
		case T_char :
			convertedType = TypeBinding.CHAR;
			break;
		case T_int :
			convertedType = TypeBinding.INT;
			break;
		case T_float :
			convertedType = TypeBinding.FLOAT;
			break;
		case T_long :
			convertedType = TypeBinding.LONG;
			break;
		case T_double :
			convertedType = TypeBinding.DOUBLE;
			break;
		default :
	}
	if ((this.implicitConversion & TypeIds.BOXING) != 0) {
		convertedType = scope.environment().computeBoxingType(convertedType);
	}
	return convertedType;
}

public StringBuffer print(int indent, StringBuffer output) {
	printIndent(indent, output);
	return printExpression(indent, output);
}

public abstract StringBuffer printExpression(int indent, StringBuffer output);

public StringBuffer printStatement(int indent, StringBuffer output) {
	return print(indent, output).append(";"); //$NON-NLS-1$
}

public void resolve(BlockScope scope) {
	// drops the returning expression's type whatever the type is.
	this.resolveType(scope);
	return;
}

/**
 * Resolve the type of this expression in the context of a blockScope
 *
 * @param scope
 * @return
 * 	Return the actual type of this expression after resolution
 */
public TypeBinding resolveType(BlockScope scope) {
	// by default... subclasses should implement a better TB if required.
	return null;
}

/**
 * Resolve the type of this expression in the context of a classScope
 *
 * @param scope
 * @return
 * 	Return the actual type of this expression after resolution
 */
public TypeBinding resolveType(ClassScope scope) {
	// by default... subclasses should implement a better TB if required.
	return null;
}

public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType) {
	setExpectedType(expectedType); // needed in case of generic method invocation
	TypeBinding expressionType = this.resolveType(scope);
	if (expressionType == null) return null;
	if (expressionType == expectedType) return expressionType;

	if (!expressionType.isCompatibleWith(expectedType)) {
		if (scope.isBoxingCompatibleWith(expressionType, expectedType)) {
			computeConversion(scope, expectedType, expressionType);
		} else {
			scope.problemReporter().typeMismatchError(expressionType, expectedType, this, null);
			return null;
		}
	}
	return expressionType;
}
/**
 * Returns true if the receiver is forced to be of raw type either to satisfy the contract imposed
 * by a super type or because it *is* raw and the current type has no control over it (i.e the rawness
 * originates from some other file.)
 */
public boolean forcedToBeRaw(ReferenceContext referenceContext) {
	if (this instanceof NameReference) {
		final Binding receiverBinding = ((NameReference) this).binding;
		if (receiverBinding.isParameter() && (((LocalVariableBinding) receiverBinding).tagBits & TagBits.ForcedToBeRawType) != 0) {
			return true;  // parameter is forced to be raw since super method uses raw types.
		} else if (receiverBinding instanceof FieldBinding) {
			FieldBinding field = (FieldBinding) receiverBinding;
			if (field.type.isRawType()) {
				if (referenceContext instanceof AbstractMethodDeclaration) {
					AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration) referenceContext;
					if (field.declaringClass != methodDecl.binding.declaringClass) { // inherited raw field, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=337962
						return true;
					}
				} else if (referenceContext instanceof TypeDeclaration) {
					TypeDeclaration type = (TypeDeclaration) referenceContext;
					if (field.declaringClass != type.binding) { // inherited raw field, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=337962
						return true;
					}
				}
			}
		}
	} else if (this instanceof MessageSend) {
		if (!CharOperation.equals(((MessageSend) this).binding.declaringClass.getFileName(),
				referenceContext.compilationResult().getFileName())) {  // problem is rooted elsewhere
			return true;
		}
	} else if (this instanceof FieldReference) {
		FieldBinding field = ((FieldReference) this).binding;
		if (!CharOperation.equals(field.declaringClass.getFileName(),
				referenceContext.compilationResult().getFileName())) { // problem is rooted elsewhere
			return true;
		}
		if (field.type.isRawType()) {
			if (referenceContext instanceof AbstractMethodDeclaration) {
				AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration) referenceContext;
				if (field.declaringClass != methodDecl.binding.declaringClass) { // inherited raw field, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=337962
					return true;
				}
			} else if (referenceContext instanceof TypeDeclaration) {
				TypeDeclaration type = (TypeDeclaration) referenceContext;
				if (field.declaringClass != type.binding) { // inherited raw field, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=337962
					return true;
				}
			}
		}
	} else if (this instanceof ConditionalExpression) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=337751
		ConditionalExpression ternary = (ConditionalExpression) this;
		if (ternary.valueIfTrue.forcedToBeRaw(referenceContext) || ternary.valueIfFalse.forcedToBeRaw(referenceContext)) {
			return true;
		}
	}
	return false;
}

/**
 * Returns an object which can be used to identify identical JSR sequence targets
 * (see TryStatement subroutine codegen)
 * or <code>null</null> if not reusable
 */
public Object reusableJSRTarget() {
	if (this.constant != Constant.NotAConstant)
		return this.constant;
	return null;
}

/**
 * Record the type expectation before this expression is typechecked.
 * e.g. String s = foo();, foo() will be tagged as being expected of type String
 * Used to trigger proper inference of generic method invocations.
 *
 * @param expectedType
 * 	The type denoting an expectation in the context of an assignment conversion
 */
public void setExpectedType(TypeBinding expectedType) {
    // do nothing by default
}

public void tagAsNeedCheckCast() {
    // do nothing by default
}

/**
 * Record the fact a cast expression got detected as being unnecessary.
 *
 * @param scope
 * @param castType
 */
public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
    // do nothing by default
}

public Expression toTypeReference() {
	//by default undefined

	//this method is meanly used by the parser in order to transform
	//an expression that is used as a type reference in a cast ....
	//--appreciate the fact that castExpression and ExpressionWithParenthesis
	//--starts with the same pattern.....

	return this;
}

/**
 * Traverse an expression in the context of a blockScope
 * @param visitor
 * @param scope
 */
public void traverse(ASTVisitor visitor, BlockScope scope) {
	// nothing to do
}

/**
 * Traverse an expression in the context of a classScope
 * @param visitor
 * @param scope
 */
public void traverse(ASTVisitor visitor, ClassScope scope) {
	// nothing to do
}
}
