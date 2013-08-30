/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Nick Teryaev - fix for bug (https://bugs.eclipse.org/bugs/show_bug.cgi?id=40752)
 *     Stephan Herrmann - Contribution for bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.PolymorphicMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class CastExpression extends Expression {

	public Expression expression;
	public TypeReference type;
	public TypeBinding expectedType; // when assignment conversion to a given expected type: String s = (String) t;

//expression.implicitConversion holds the cast for baseType casting
public CastExpression(Expression expression, TypeReference type) {
	this.expression = expression;
	this.type = type;
	type.bits |= ASTNode.IgnoreRawTypeCheck; // no need to worry about raw type usage
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	FlowInfo result = this.expression
		.analyseCode(currentScope, flowContext, flowInfo)
		.unconditionalInits();
	if ((this.expression.implicitConversion & TypeIds.UNBOXING) != 0) {
		this.expression.checkNPE(currentScope, flowContext, flowInfo);
	}
	return result;
}

/**
 * Complain if assigned expression is cast, but not actually used as such, e.g. Object o = (List) object;
 */
public static void checkNeedForAssignedCast(BlockScope scope, TypeBinding expectedType, CastExpression rhs) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	TypeBinding castedExpressionType = rhs.expression.resolvedType;
	//	int i = (byte) n; // cast still had side effect
	// double d = (float) n; // cast to float is unnecessary
	if (castedExpressionType == null || rhs.resolvedType.isBaseType()) return;
	//if (castedExpressionType.id == T_null) return; // tolerate null expression cast
	if (castedExpressionType.isCompatibleWith(expectedType)) {
		scope.problemReporter().unnecessaryCast(rhs);
	}
}


/**
 * Complain if cast expression is cast, but not actually needed, int i = (int)(Integer) 12;
 * Note that this (int) cast is however needed:   Integer i = 0;  char c = (char)((int) i);
 */
public static void checkNeedForCastCast(BlockScope scope, CastExpression enclosingCast) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	CastExpression nestedCast = (CastExpression) enclosingCast.expression;
	if ((nestedCast.bits & ASTNode.UnnecessaryCast) == 0) return;
	// check if could cast directly to enclosing cast type, without intermediate type cast
	CastExpression alternateCast = new CastExpression(null, enclosingCast.type);
	alternateCast.resolvedType = enclosingCast.resolvedType;
	if (!alternateCast.checkCastTypesCompatibility(scope, enclosingCast.resolvedType, nestedCast.expression.resolvedType, null /* no expr to avoid side-effects*/)) return;
	scope.problemReporter().unnecessaryCast(nestedCast);
}


/**
 * Casting an enclosing instance will considered as useful if removing it would actually bind to a different type
 */
public static void checkNeedForEnclosingInstanceCast(BlockScope scope, Expression enclosingInstance, TypeBinding enclosingInstanceType, TypeBinding memberType) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	TypeBinding castedExpressionType = ((CastExpression)enclosingInstance).expression.resolvedType;
	if (castedExpressionType == null) return; // cannot do better
	// obvious identity cast
	if (castedExpressionType == enclosingInstanceType) {
		scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
	} else if (castedExpressionType == TypeBinding.NULL){
		return; // tolerate null enclosing instance cast
	} else {
		TypeBinding alternateEnclosingInstanceType = castedExpressionType;
		if (castedExpressionType.isBaseType() || castedExpressionType.isArrayType()) return; // error case
		if (memberType == scope.getMemberType(memberType.sourceName(), (ReferenceBinding) alternateEnclosingInstanceType)) {
			scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
		}
	}
}

/**
 * Only complain for identity cast, since other type of casts may be useful: e.g.  ~((~(long) 0) << 32)  is different from: ~((~0) << 32)
 */
public static void checkNeedForArgumentCast(BlockScope scope, int operator, int operatorSignature, Expression expression, int expressionTypeId) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	// check need for left operand cast
	if ((expression.bits & ASTNode.UnnecessaryCast) == 0 && expression.resolvedType.isBaseType()) {
		// narrowing conversion on base type may change value, thus necessary
		return;
	} else {
		TypeBinding alternateLeftType = ((CastExpression)expression).expression.resolvedType;
		if (alternateLeftType == null) return; // cannot do better
		if (alternateLeftType.id == expressionTypeId) { // obvious identity cast
			scope.problemReporter().unnecessaryCast((CastExpression)expression);
			return;
		}
	}
}

/**
 * Cast expressions will considered as useful if removing them all would actually bind to a different method
 * (no fine grain analysis on per casted argument basis, simply separate widening cast from narrowing ones)
 */
public static void checkNeedForArgumentCasts(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] argumentTypes, final InvocationSite invocationSite) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	int length = argumentTypes.length;

	// iterate over arguments, and retrieve original argument types (before cast)
	TypeBinding[] rawArgumentTypes = argumentTypes;
	for (int i = 0; i < length; i++) {
		Expression argument = arguments[i];
		if (argument instanceof CastExpression) {
			// narrowing conversion on base type may change value, thus necessary
			if ((argument.bits & ASTNode.UnnecessaryCast) == 0 && argument.resolvedType.isBaseType()) {
				continue;
			}
			TypeBinding castedExpressionType = ((CastExpression)argument).expression.resolvedType;
			if (castedExpressionType == null) return; // cannot do better
			// obvious identity cast
			if (castedExpressionType == argumentTypes[i]) {
				scope.problemReporter().unnecessaryCast((CastExpression)argument);
			} else if (castedExpressionType == TypeBinding.NULL){
				continue; // tolerate null argument cast
			} else if ((argument.implicitConversion & TypeIds.BOXING) != 0) {
				continue; // boxing has a side effect: (int) char   is not boxed as simple char
			} else {
				if (rawArgumentTypes == argumentTypes) {
					System.arraycopy(rawArgumentTypes, 0, rawArgumentTypes = new TypeBinding[length], 0, length);
				}
				// retain original argument type
				rawArgumentTypes[i] = castedExpressionType;
			}
		}
	}
	// perform alternate lookup with original types
	if (rawArgumentTypes != argumentTypes) {
		checkAlternateBinding(scope, receiver, receiverType, binding, arguments, argumentTypes, rawArgumentTypes, invocationSite);
	}
}

/**
 * Check binary operator casted arguments
 */
public static void checkNeedForArgumentCasts(BlockScope scope, int operator, int operatorSignature, Expression left, int leftTypeId, boolean leftIsCast, Expression right, int rightTypeId, boolean rightIsCast) {
	if (scope.compilerOptions().getSeverity(CompilerOptions.UnnecessaryTypeCheck) == ProblemSeverities.Ignore) return;

	// check need for left operand cast
	int alternateLeftTypeId = leftTypeId;
	if (leftIsCast) {
		if ((left.bits & ASTNode.UnnecessaryCast) == 0 && left.resolvedType.isBaseType()) {
			// narrowing conversion on base type may change value, thus necessary
			leftIsCast = false;
		} else  {
			TypeBinding alternateLeftType = ((CastExpression)left).expression.resolvedType;
			if (alternateLeftType == null) return; // cannot do better
			if ((alternateLeftTypeId = alternateLeftType.id) == leftTypeId || scope.environment().computeBoxingType(alternateLeftType).id == leftTypeId) { // obvious identity cast
				scope.problemReporter().unnecessaryCast((CastExpression)left);
				leftIsCast = false;
			} else if (alternateLeftTypeId == TypeIds.T_null) {
				alternateLeftTypeId = leftTypeId;  // tolerate null argument cast
				leftIsCast = false;
			}
		}
	}
	// check need for right operand cast
	int alternateRightTypeId = rightTypeId;
	if (rightIsCast) {
		if ((right.bits & ASTNode.UnnecessaryCast) == 0 && right.resolvedType.isBaseType()) {
			// narrowing conversion on base type may change value, thus necessary
			rightIsCast = false;
		} else {
			TypeBinding alternateRightType = ((CastExpression)right).expression.resolvedType;
			if (alternateRightType == null) return; // cannot do better
			if ((alternateRightTypeId = alternateRightType.id) == rightTypeId || scope.environment().computeBoxingType(alternateRightType).id == rightTypeId) { // obvious identity cast
				scope.problemReporter().unnecessaryCast((CastExpression)right);
				rightIsCast = false;
			} else if (alternateRightTypeId == TypeIds.T_null) {
				alternateRightTypeId = rightTypeId;  // tolerate null argument cast
				rightIsCast = false;
			}
		}
	}
	if (leftIsCast || rightIsCast) {
		if (alternateLeftTypeId > 15 || alternateRightTypeId > 15) { // must convert String + Object || Object + String
			if (alternateLeftTypeId == TypeIds.T_JavaLangString) {
				alternateRightTypeId = TypeIds.T_JavaLangObject;
			} else if (alternateRightTypeId == TypeIds.T_JavaLangString) {
				alternateLeftTypeId = TypeIds.T_JavaLangObject;
			} else {
				return; // invalid operator
			}
		}
		int alternateOperatorSignature = OperatorExpression.OperatorSignatures[operator][(alternateLeftTypeId << 4) + alternateRightTypeId];
		// (cast)  left   Op (cast)  right --> result
		//  1111   0000       1111   0000     1111
		//  <<16   <<12       <<8    <<4       <<0
		final int CompareMASK = (0xF<<16) + (0xF<<8) + 0xF; // mask hiding compile-time types
		if ((operatorSignature & CompareMASK) == (alternateOperatorSignature & CompareMASK)) { // same promotions and result
			if (leftIsCast) scope.problemReporter().unnecessaryCast((CastExpression)left);
			if (rightIsCast) scope.problemReporter().unnecessaryCast((CastExpression)right);
		}
	}
}

private static void checkAlternateBinding(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] originalArgumentTypes, TypeBinding[] alternateArgumentTypes, final InvocationSite invocationSite) {
		InvocationSite fakeInvocationSite = new InvocationSite(){
			public TypeBinding[] genericTypeArguments() { return null; }
			public boolean isSuperAccess(){ return invocationSite.isSuperAccess(); }
			public boolean isTypeAccess() { return invocationSite.isTypeAccess(); }
			public void setActualReceiverType(ReferenceBinding actualReceiverType) { /* ignore */}
			public void setDepth(int depth) { /* ignore */}
			public void setFieldIndex(int depth){ /* ignore */}
			public int sourceStart() { return 0; }
			public int sourceEnd() { return 0; }
			public TypeBinding expectedType() { return invocationSite.expectedType(); }
		};
		MethodBinding bindingIfNoCast;
		if (binding.isConstructor()) {
			bindingIfNoCast = scope.getConstructor((ReferenceBinding)receiverType, alternateArgumentTypes, fakeInvocationSite);
		} else {
			bindingIfNoCast = receiver.isImplicitThis()
				? scope.getImplicitMethod(binding.selector, alternateArgumentTypes, fakeInvocationSite)
				: scope.getMethod(receiverType, binding.selector, alternateArgumentTypes, fakeInvocationSite);
		}
		if (bindingIfNoCast == binding) {
			int argumentLength = originalArgumentTypes.length;
			if (binding.isVarargs()) {
				int paramLength = binding.parameters.length;
				if (paramLength == argumentLength) {
					int varargsIndex = paramLength - 1;
					ArrayBinding varargsType = (ArrayBinding) binding.parameters[varargsIndex];
					TypeBinding lastArgType = alternateArgumentTypes[varargsIndex];
					// originalType may be compatible already, but cast mandated
					// to clarify between varargs/non-varargs call
					if (varargsType.dimensions != lastArgType.dimensions()) {
						return;
					}
					if (lastArgType.isCompatibleWith(varargsType.elementsType())
							&& lastArgType.isCompatibleWith(varargsType)) {
						return;
					}
				}
			}
			for (int i = 0; i < argumentLength; i++) {
				if (originalArgumentTypes[i] != alternateArgumentTypes[i]
                       /*&& !originalArgumentTypes[i].needsUncheckedConversion(alternateArgumentTypes[i])*/) {
					scope.problemReporter().unnecessaryCast((CastExpression)arguments[i]);
				}
			}
		}
}

public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
	if (match == castType) {
		if (!isNarrowing && match == this.resolvedType.leafComponentType()) { // do not tag as unnecessary when recursing through upper bounds
			tagAsUnnecessaryCast(scope, castType);
		}
		return true;
	}
	if (match != null) {
		if (isNarrowing
				? match.isProvablyDistinct(expressionType)
				: castType.isProvablyDistinct(match)) {
			return false;
		}
	}
	switch (castType.kind()) {
		case Binding.PARAMETERIZED_TYPE :
			if (!castType.isReifiable()) {
				if (match == null) { // unrelated types
					this.bits |= ASTNode.UnsafeCast;
					return true;
				}
				switch (match.kind()) {
					case Binding.PARAMETERIZED_TYPE :
						if (isNarrowing) {
							// [JLS 5.5] T <: S
							if (expressionType.isRawType() || !expressionType.isEquivalentTo(match)) {
								this.bits |= ASTNode.UnsafeCast;
								return true;
							}
							// [JLS 5.5] S has no subtype X != T, such that |X| == |T|
							// if I2<T,U> extends I1<T>, then cast from I1<T> to I2<T,U> is unchecked
							ParameterizedTypeBinding paramCastType = (ParameterizedTypeBinding) castType;
							ParameterizedTypeBinding paramMatch = (ParameterizedTypeBinding) match;
							// easy case if less parameters on match
							TypeBinding[] castArguments = paramCastType.arguments;
							int length = castArguments == null ? 0 : castArguments.length;
							if (paramMatch.arguments == null || length > paramMatch.arguments.length) {
								this.bits |= ASTNode.UnsafeCast;
							} else if ((paramCastType.tagBits & (TagBits.HasDirectWildcard|TagBits.HasTypeVariable)) != 0) {
								// verify alternate cast type, substituting different type arguments
								nextAlternateArgument: for (int i = 0; i < length; i++) {
									switch (castArguments[i].kind()) {
										case Binding.WILDCARD_TYPE :
										case Binding.TYPE_PARAMETER :
											break; // check substituting with other
										default:
											continue nextAlternateArgument; // no alternative possible
									}
									TypeBinding[] alternateArguments;
									// need to clone for each iteration to avoid env paramtype cache interference
									System.arraycopy(paramCastType.arguments, 0, alternateArguments = new TypeBinding[length], 0, length);
									alternateArguments[i] = scope.getJavaLangObject();
									LookupEnvironment environment = scope.environment();
									ParameterizedTypeBinding alternateCastType = environment.createParameterizedType((ReferenceBinding)castType.erasure(), alternateArguments, castType.enclosingType());
									if (alternateCastType.findSuperTypeOriginatingFrom(expressionType) == match) {
										this.bits |= ASTNode.UnsafeCast;
										break;
									}
								}
							}
							return true;
						} else {
							// [JLS 5.5] T >: S
							if (!match.isEquivalentTo(castType)) {
								this.bits |= ASTNode.UnsafeCast;
								return true;
							}
						}
						break;
					case Binding.RAW_TYPE :
						this.bits |= ASTNode.UnsafeCast; // upcast since castType is known to be bound paramType
						return true;
					default :
						if (isNarrowing){
							// match is not parameterized or raw, then any other subtype of match will erase  to |T|
							this.bits |= ASTNode.UnsafeCast;
							return true;
						}
						break;
				}
			}
			break;
		case Binding.ARRAY_TYPE :
			TypeBinding leafType = castType.leafComponentType();
			if (isNarrowing && (!leafType.isReifiable() || leafType.isTypeVariable())) {
				this.bits |= ASTNode.UnsafeCast;
				return true;
			}
			break;
		case Binding.TYPE_PARAMETER :
			this.bits |= ASTNode.UnsafeCast;
			return true;
//		(disabled) https://bugs.eclipse.org/bugs/show_bug.cgi?id=240807			
//		case Binding.TYPE :
//			if (isNarrowing && match == null && expressionType.isParameterizedType()) {
//				this.bits |= ASTNode.UnsafeCast;
//				return true;
//			}
//			break;
	}
	if (!isNarrowing && match == this.resolvedType.leafComponentType()) { // do not tag as unnecessary when recursing through upper bounds
		tagAsUnnecessaryCast(scope, castType);
	}
	return true;
}

/**
 * Cast expression code generation
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 * @param valueRequired boolean
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	int pc = codeStream.position;
	boolean needRuntimeCheckcast = (this.bits & ASTNode.GenerateCheckcast) != 0;
	if (this.constant != Constant.NotAConstant) {
		if (valueRequired || needRuntimeCheckcast) { // Added for: 1F1W9IG: IVJCOM:WINNT - Compiler omits casting check
			codeStream.generateConstant(this.constant, this.implicitConversion);
			if (needRuntimeCheckcast) {
				codeStream.checkcast(this.resolvedType);
			}
			if (!valueRequired) {
				// the resolveType cannot be double or long
				codeStream.pop();
			}
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
		return;
	}
	this.expression.generateCode(currentScope, codeStream, valueRequired || needRuntimeCheckcast);
	if (needRuntimeCheckcast && this.expression.postConversionType(currentScope) != this.resolvedType.erasure()) { // no need to issue a checkcast if already done as genericCast
		codeStream.checkcast(this.resolvedType);
	}
	if (valueRequired) {
		codeStream.generateImplicitConversion(this.implicitConversion);
	} else if (needRuntimeCheckcast) {
		codeStream.pop();
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

public Expression innermostCastedExpression(){
	Expression current = this.expression;
	while (current instanceof CastExpression) {
		current = ((CastExpression) current).expression;
	}
	return current;
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#localVariableBinding()
 */
public LocalVariableBinding localVariableBinding() {
	return this.expression.localVariableBinding();
}

public int nullStatus(FlowInfo flowInfo) {
	return this.expression.nullStatus(flowInfo);
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#optimizedBooleanConstant()
 */
public Constant optimizedBooleanConstant() {
	switch(this.resolvedType.id) {
		case T_boolean :
		case T_JavaLangBoolean :
			return this.expression.optimizedBooleanConstant();
	}
	return Constant.NotAConstant;
}

public StringBuffer printExpression(int indent, StringBuffer output) {
	output.append('(');
	this.type.print(0, output).append(") "); //$NON-NLS-1$
	return this.expression.printExpression(0, output);
}

public TypeBinding resolveType(BlockScope scope) {
	// compute a new constant if the cast is effective

	// due to the fact an expression may start with ( and that a cast can also start with (
	// the field is an expression....it can be a TypeReference OR a NameReference Or
	// any kind of Expression <-- this last one is invalid.......

	this.constant = Constant.NotAConstant;
	this.implicitConversion = TypeIds.T_undefined;

	boolean exprContainCast = false;

	TypeBinding castType = this.resolvedType = this.type.resolveType(scope);
	//expression.setExpectedType(this.resolvedType); // needed in case of generic method invocation
	if (this.expression instanceof CastExpression) {
		this.expression.bits |= ASTNode.DisableUnnecessaryCastCheck;
		exprContainCast = true;
	}
	TypeBinding expressionType = this.expression.resolveType(scope);
	if (this.expression instanceof MessageSend) {
		MessageSend messageSend = (MessageSend) this.expression;
		MethodBinding methodBinding = messageSend.binding;
		if (methodBinding != null && methodBinding.isPolymorphic()) {
			messageSend.binding = scope.environment().updatePolymorphicMethodReturnType((PolymorphicMethodBinding) methodBinding, castType);
			expressionType = castType;
		}
	}
	if (castType != null) {
		if (expressionType != null) {
			boolean isLegal = checkCastTypesCompatibility(scope, castType, expressionType, this.expression);
			if (isLegal) {
				this.expression.computeConversion(scope, castType, expressionType);
				if ((this.bits & ASTNode.UnsafeCast) != 0) { // unsafe cast
					if (scope.compilerOptions().reportUnavoidableGenericTypeProblems || !this.expression.forcedToBeRaw(scope.referenceContext())) {
						scope.problemReporter().unsafeCast(this, scope);
					}
				} else {
					if (castType.isRawType() && scope.compilerOptions().getSeverity(CompilerOptions.RawTypeReference) != ProblemSeverities.Ignore){
						scope.problemReporter().rawTypeReference(this.type, castType);
					}
					if ((this.bits & (ASTNode.UnnecessaryCast|ASTNode.DisableUnnecessaryCastCheck)) == ASTNode.UnnecessaryCast) { // unnecessary cast
						if (!isIndirectlyUsed()) // used for generic type inference or boxing ?
							scope.problemReporter().unnecessaryCast(this);
					}
				}
			} else { // illegal cast
				if ((castType.tagBits & TagBits.HasMissingType) == 0) { // no complaint if secondary error
					scope.problemReporter().typeCastError(this, castType, expressionType);
				}
				this.bits |= ASTNode.DisableUnnecessaryCastCheck; // disable further secondary diagnosis
			}
		}
		this.resolvedType = castType.capture(scope, this.sourceEnd);
		if (exprContainCast) {
			checkNeedForCastCast(scope, this);
		}
	}
	return this.resolvedType;
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#setExpectedType(org.eclipse.jdt.internal.compiler.lookup.TypeBinding)
 */
public void setExpectedType(TypeBinding expectedType) {
	this.expectedType = expectedType;
}

/**
 * Determines whether apparent unnecessary cast wasn't actually used to
 * perform return type inference of generic method invocation or boxing.
 */
private boolean isIndirectlyUsed() {
	if (this.expression instanceof MessageSend) {
		MethodBinding method = ((MessageSend)this.expression).binding;
		if (method instanceof ParameterizedGenericMethodBinding
					&& ((ParameterizedGenericMethodBinding)method).inferredReturnType) {
			if (this.expectedType == null)
				return true;
			if (this.resolvedType != this.expectedType)
				return true;
		}
	}
	if (this.expectedType != null && this.resolvedType.isBaseType() && !this.resolvedType.isCompatibleWith(this.expectedType)) {
		// boxing: Short s = (short) _byte
		return true;
	}
	return false;
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#tagAsNeedCheckCast()
 */
public void tagAsNeedCheckCast() {
	this.bits |= ASTNode.GenerateCheckcast;
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#tagAsUnnecessaryCast(Scope, TypeBinding)
 */
public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
	this.bits |= ASTNode.UnnecessaryCast;
}

public void traverse(ASTVisitor visitor, BlockScope blockScope) {
	if (visitor.visit(this, blockScope)) {
		this.type.traverse(visitor, blockScope);
		this.expression.traverse(visitor, blockScope);
	}
	visitor.endVisit(this, blockScope);
}
}
