/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 292478 - Report potentially null across variable assignment,
 *     											    Contribution for bug 185682 - Increment/decrement operators mark local variables as read
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class SingleNameReference extends NameReference implements OperatorIds {

	public static final int READ = 0;
	public static final int WRITE = 1;
	public char[] token;
	public MethodBinding[] syntheticAccessors; // [0]=read accessor [1]=write accessor
	public TypeBinding genericCast;

public SingleNameReference(char[] source, long pos) {
	super();
	this.token = source;
	this.sourceStart = (int) (pos >>> 32);
	this.sourceEnd = (int) pos;
}

public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound) {
	boolean isReachable = (flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0;
	// compound assignment extra work
	if (isCompound) { // check the variable part is initialized if blank final
		switch (this.bits & ASTNode.RestrictiveFlagMASK) {
			case Binding.FIELD : // reading a field
				FieldBinding fieldBinding = (FieldBinding) this.binding;
				if (fieldBinding.isBlankFinal()
						&& currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
					FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
					if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
						currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
					}
				}
				if (!fieldBinding.isStatic()) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
					currentScope.resetEnclosingMethodStaticFlag();
				}
				manageSyntheticAccessIfNecessary(currentScope, flowInfo, true /*read-access*/);
				break;
			case Binding.LOCAL : // reading a local variable
				// check if assigning a final blank field
				LocalVariableBinding localBinding;
				if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding) this.binding)) {
					currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
					// we could improve error msg here telling "cannot use compound assignment on final local variable"
				}
				if (localBinding.useFlag != LocalVariableBinding.USED) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=185682
					// access from compound assignment does not prevent "unused" warning, unless unboxing is involved:
					if (isReachable && (this.implicitConversion & TypeIds.UNBOXING) != 0) {
						localBinding.useFlag = LocalVariableBinding.USED;
					} else {
						// use values < 0 to count the number of compound uses:
						if (localBinding.useFlag <= LocalVariableBinding.UNUSED)
							localBinding.useFlag--;
					}
				}
		}
	}
	if (assignment.expression != null) {
		flowInfo = assignment.expression.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
	}
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // assigning to a field
			manageSyntheticAccessIfNecessary(currentScope, flowInfo, false /*write-access*/);

			// check if assigning a final field
			FieldBinding fieldBinding = (FieldBinding) this.binding;
			if (fieldBinding.isFinal()) {
				// inside a context where allowed
				if (!isCompound && fieldBinding.isBlankFinal() && currentScope.allowBlankFinalFieldAssignment(fieldBinding)) {
					if (flowInfo.isPotentiallyAssigned(fieldBinding)) {
						currentScope.problemReporter().duplicateInitializationOfBlankFinalField(fieldBinding, this);
					} else {
						flowContext.recordSettingFinal(fieldBinding, this, flowInfo);
					}
					flowInfo.markAsDefinitelyAssigned(fieldBinding);
				} else {
					currentScope.problemReporter().cannotAssignToFinalField(fieldBinding, this);
				}
			}
			if (!fieldBinding.isStatic()) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
				currentScope.resetEnclosingMethodStaticFlag();
			}
			break;
		case Binding.LOCAL : // assigning to a local variable
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			if (!flowInfo.isDefinitelyAssigned(localBinding)){// for local variable debug attributes
				this.bits |= ASTNode.FirstAssignmentToLocal;
			} else {
				this.bits &= ~ASTNode.FirstAssignmentToLocal;
			}
			if (localBinding.isFinal()) {
				if ((this.bits & ASTNode.DepthMASK) == 0) {
					// tolerate assignment to final local in unreachable code (45674)
					if ((isReachable && isCompound) || !localBinding.isBlankFinal()){
						currentScope.problemReporter().cannotAssignToFinalLocal(localBinding, this);
					} else if (flowInfo.isPotentiallyAssigned(localBinding)) {
						currentScope.problemReporter().duplicateInitializationOfFinalLocal(localBinding, this);
					} else {
						flowContext.recordSettingFinal(localBinding, this, flowInfo);
					}
				} else {
					currentScope.problemReporter().cannotAssignToFinalOuterLocal(localBinding, this);
				}
			}
			else /* avoid double diagnostic */ if ((localBinding.tagBits & TagBits.IsArgument) != 0) {
				currentScope.problemReporter().parameterAssignment(localBinding, this);
			}
			flowInfo.markAsDefinitelyAssigned(localBinding);
	}
	manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
	return flowInfo;
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	return analyseCode(currentScope, flowContext, flowInfo, true);
}

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // reading a field
			if (valueRequired || currentScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_4) {
				manageSyntheticAccessIfNecessary(currentScope, flowInfo, true /*read-access*/);
			}
			// check if reading a final blank field
			FieldBinding fieldBinding = (FieldBinding) this.binding;
			if (fieldBinding.isBlankFinal() && currentScope.needBlankFinalFieldInitializationCheck(fieldBinding)) {
				FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
				if (!fieldInits.isDefinitelyAssigned(fieldBinding)) {
					currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
				}
			}
			if (!fieldBinding.isStatic()) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
				currentScope.resetEnclosingMethodStaticFlag();
			}
			break;
		case Binding.LOCAL : // reading a local variable
			LocalVariableBinding localBinding;
			if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding) this.binding)) {
				currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
			}
			if ((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0) {
				localBinding.useFlag = LocalVariableBinding.USED;
			} else if (localBinding.useFlag == LocalVariableBinding.UNUSED) {
				localBinding.useFlag = LocalVariableBinding.FAKE_USED;
			}
	}
	if (valueRequired) {
		manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
	}
	return flowInfo;
}

public TypeBinding checkFieldAccess(BlockScope scope) {
	FieldBinding fieldBinding = (FieldBinding) this.binding;
	this.constant = fieldBinding.constant();

	this.bits &= ~ASTNode.RestrictiveFlagMASK; // clear bits
	this.bits |= Binding.FIELD;
	MethodScope methodScope = scope.methodScope();
	if (fieldBinding.isStatic()) {
		// check if accessing enum static field in initializer
		ReferenceBinding declaringClass = fieldBinding.declaringClass;
		if (declaringClass.isEnum()) {
			SourceTypeBinding sourceType = scope.enclosingSourceType();
			if (this.constant == Constant.NotAConstant
					&& !methodScope.isStatic
					&& (sourceType == declaringClass || sourceType.superclass == declaringClass) // enum constant body
					&& methodScope.isInsideInitializerOrConstructor()) {
				scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
			}
		}
	} else {
		if (scope.compilerOptions().getSeverity(CompilerOptions.UnqualifiedFieldAccess) != ProblemSeverities.Ignore) {
			scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
		}
		// must check for the static status....
		if (methodScope.isStatic) {
			scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
			return fieldBinding.type;
		}
	}

	if (isFieldUseDeprecated(fieldBinding, scope, this.bits))
		scope.problemReporter().deprecatedField(fieldBinding, this);

	if ((this.bits & ASTNode.IsStrictlyAssigned) == 0
			&& methodScope.enclosingSourceType() == fieldBinding.original().declaringClass
			&& methodScope.lastVisibleFieldID >= 0
			&& fieldBinding.id >= methodScope.lastVisibleFieldID
			&& (!fieldBinding.isStatic() || methodScope.isStatic)) {
		scope.problemReporter().forwardReference(this, 0, fieldBinding);
		this.bits |= ASTNode.IgnoreNoEffectAssignCheck;
	}
	return fieldBinding.type;

}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#computeConversion(org.eclipse.jdt.internal.compiler.lookup.Scope, org.eclipse.jdt.internal.compiler.lookup.TypeBinding, org.eclipse.jdt.internal.compiler.lookup.TypeBinding)
 */
public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType) {
	if (runtimeTimeType == null || compileTimeType == null)
		return;
	if ((this.bits & Binding.FIELD) != 0 && this.binding != null && this.binding.isValidBinding()) {
		// set the generic cast after the fact, once the type expectation is fully known (no need for strict cast)
		FieldBinding field = (FieldBinding) this.binding;
		FieldBinding originalBinding = field.original();
		TypeBinding originalType = originalBinding.type;
		// extra cast needed if field type is type variable
		if (originalType.leafComponentType().isTypeVariable()) {
	    	TypeBinding targetType = (!compileTimeType.isBaseType() && runtimeTimeType.isBaseType())
	    		? compileTimeType  // unboxing: checkcast before conversion
	    		: runtimeTimeType;
	        this.genericCast = originalType.genericCast(scope.boxing(targetType));
	        if (this.genericCast instanceof ReferenceBinding) {
				ReferenceBinding referenceCast = (ReferenceBinding) this.genericCast;
				if (!referenceCast.canBeSeenBy(scope)) {
		        	scope.problemReporter().invalidType(this,
		        			new ProblemReferenceBinding(
								CharOperation.splitOn('.', referenceCast.shortReadableName()),
								referenceCast,
								ProblemReasons.NotVisible));
				}
	        }
		}
	}
	super.computeConversion(scope, runtimeTimeType, compileTimeType);
}

public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
	// optimizing assignment like: i = i + 1 or i = 1 + i
	if (assignment.expression.isCompactableOperation()) {
		BinaryExpression operation = (BinaryExpression) assignment.expression;
		int operator = (operation.bits & ASTNode.OperatorMASK) >> ASTNode.OperatorSHIFT;
		SingleNameReference variableReference;
		if ((operation.left instanceof SingleNameReference) && ((variableReference = (SingleNameReference) operation.left).binding == this.binding)) {
			// i = i + value, then use the variable on the right hand side, since it has the correct implicit conversion
			variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[SingleNameReference.WRITE], operation.right, operator, operation.implicitConversion, valueRequired);
			if (valueRequired) {
				codeStream.generateImplicitConversion(assignment.implicitConversion);
			}
			return;
		}
		if ((operation.right instanceof SingleNameReference)
				&& ((operator == OperatorIds.PLUS) || (operator == OperatorIds.MULTIPLY)) // only commutative operations
				&& ((variableReference = (SingleNameReference) operation.right).binding == this.binding)
				&& (operation.left.constant != Constant.NotAConstant) // exclude non constant expressions, since could have side-effect
				&& (((operation.left.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4) != TypeIds.T_JavaLangString) // exclude string concatenation which would occur backwards
				&& (((operation.right.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4) != TypeIds.T_JavaLangString)) { // exclude string concatenation which would occur backwards
			// i = value + i, then use the variable on the right hand side, since it has the correct implicit conversion
			variableReference.generateCompoundAssignment(currentScope, codeStream, this.syntheticAccessors == null ? null : this.syntheticAccessors[SingleNameReference.WRITE], operation.left, operator, operation.implicitConversion, valueRequired);
			if (valueRequired) {
				codeStream.generateImplicitConversion(assignment.implicitConversion);
			}
			return;
		}
	}
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // assigning to a field
			int pc = codeStream.position;
			FieldBinding codegenBinding = ((FieldBinding) this.binding).original();
			if (!codegenBinding.isStatic()) { // need a receiver?
				if ((this.bits & ASTNode.DepthMASK) != 0) {
					ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT);
					Object[] emulationPath = currentScope.getEmulationPath(targetType, true /*only exact match*/, false/*consider enclosing arg*/);
					codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
				} else {
					generateReceiver(codeStream);
				}
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			assignment.expression.generateCode(currentScope, codeStream, true);
			fieldStore(currentScope, codeStream, codegenBinding, this.syntheticAccessors == null ? null : this.syntheticAccessors[SingleNameReference.WRITE], this.actualReceiverType, true /*implicit this*/, valueRequired);
			if (valueRequired) {
				codeStream.generateImplicitConversion(assignment.implicitConversion);
			}
			// no need for generic cast as value got dupped
			return;
		case Binding.LOCAL : // assigning to a local variable
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			if (localBinding.resolvedPosition != -1) {
				assignment.expression.generateCode(currentScope, codeStream, true);
			} else {
				if (assignment.expression.constant != Constant.NotAConstant) {
					// assigning an unused local to a constant value = no actual assignment is necessary
					if (valueRequired) {
						codeStream.generateConstant(assignment.expression.constant, assignment.implicitConversion);
					}
				} else {
					assignment.expression.generateCode(currentScope, codeStream, true);
					/* Even though the value may not be required, we force it to be produced, and discard it later
					on if it was actually not necessary, so as to provide the same behavior as JDK1.2beta3.	*/
					if (valueRequired) {
						codeStream.generateImplicitConversion(assignment.implicitConversion); // implicit conversion
					} else {
						switch(localBinding.type.id) {
							case TypeIds.T_long :
							case TypeIds.T_double :
								codeStream.pop2();
								break;
							default :
								codeStream.pop();
								break;
						}						
					}
				}
				return;
			}
			// 26903, need extra cast to store null in array local var
			if (localBinding.type.isArrayType()
				&& (assignment.expression.resolvedType == TypeBinding.NULL	// arrayLoc = null
					|| ((assignment.expression instanceof CastExpression)	// arrayLoc = (type[])null
						&& (((CastExpression)assignment.expression).innermostCastedExpression().resolvedType == TypeBinding.NULL)))){
				codeStream.checkcast(localBinding.type);
			}

			// normal local assignment (since cannot store in outer local which are final locations)
			codeStream.store(localBinding, valueRequired);
			if ((this.bits & ASTNode.FirstAssignmentToLocal) != 0) { // for local variable debug attributes
				localBinding.recordInitializationStartPC(codeStream.position);
			}
			// implicit conversion
			if (valueRequired) {
				codeStream.generateImplicitConversion(assignment.implicitConversion);
			}
	}
}

public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	int pc = codeStream.position;
	if (this.constant != Constant.NotAConstant) {
		if (valueRequired) {
			codeStream.generateConstant(this.constant, this.implicitConversion);
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
		return;
	} else {
		switch (this.bits & ASTNode.RestrictiveFlagMASK) {
			case Binding.FIELD : // reading a field
				FieldBinding codegenField = ((FieldBinding) this.binding).original();
				Constant fieldConstant = codegenField.constant();
				if (fieldConstant != Constant.NotAConstant) {
					// directly use inlined value for constant fields
					if (valueRequired) {
						codeStream.generateConstant(fieldConstant, this.implicitConversion);
					}
					codeStream.recordPositionsFrom(pc, this.sourceStart);
					return;
				}
				if (codegenField.isStatic()) {
					if (!valueRequired
							// if no valueRequired, still need possible side-effects of <clinit> invocation, if field belongs to different class
							&& ((FieldBinding)this.binding).original().declaringClass == this.actualReceiverType.erasure()
							&& ((this.implicitConversion & TypeIds.UNBOXING) == 0)
							&& this.genericCast == null) {
						// if no valueRequired, optimize out entire gen
						codeStream.recordPositionsFrom(pc, this.sourceStart);
						return;
					}
					// managing private access
					if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
						TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);
						codeStream.fieldAccess(Opcodes.OPC_getstatic, codegenField, constantPoolDeclaringClass);
					} else {
						codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
					}
				} else {
					if (!valueRequired
							&& (this.implicitConversion & TypeIds.UNBOXING) == 0
							&& this.genericCast == null) {
						// if no valueRequired, optimize out entire gen
						codeStream.recordPositionsFrom(pc, this.sourceStart);
						return;
					}
					// managing enclosing instance access
					if ((this.bits & ASTNode.DepthMASK) != 0) {
						ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT);
						Object[] emulationPath = currentScope.getEmulationPath(targetType, true /*only exact match*/, false/*consider enclosing arg*/);
						codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
					} else {
						generateReceiver(codeStream);
					}
					// managing private access
					if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
						TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);
						codeStream.fieldAccess(Opcodes.OPC_getfield, codegenField, constantPoolDeclaringClass);
					} else {
						codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
					}
				}
				break;
			case Binding.LOCAL : // reading a local
				LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
				if (localBinding.resolvedPosition == -1) {
					if (valueRequired) {
						// restart code gen
						localBinding.useFlag = LocalVariableBinding.USED;
						throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
					}
					codeStream.recordPositionsFrom(pc, this.sourceStart);
					return;
				}
				if (!valueRequired && (this.implicitConversion & TypeIds.UNBOXING) == 0) {
					// if no valueRequired, optimize out entire gen
					codeStream.recordPositionsFrom(pc, this.sourceStart);
					return;
				}
				// outer local?
				if ((this.bits & ASTNode.DepthMASK) != 0) {
					// outer local can be reached either through a synthetic arg or a synthetic field
					VariableBinding[] path = currentScope.getEmulationPath(localBinding);
					codeStream.generateOuterAccess(path, this, localBinding, currentScope);
				} else {
					// regular local variable read
					codeStream.load(localBinding);
				}
				break;
			default: // type
				codeStream.recordPositionsFrom(pc, this.sourceStart);
				return;
		}
	}
	// required cast must occur even if no value is required
	if (this.genericCast != null) codeStream.checkcast(this.genericCast);
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

/*
 * Regular API for compound assignment, relies on the fact that there is only one reference to the
 * variable, which carries both synthetic read/write accessors.
 * The APIs with an extra argument is used whenever there are two references to the same variable which
 * are optimized in one access: e.g "a = a + 1" optimized into "a++".
 */
public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=185682
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.LOCAL:
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			// check if compound assignment is the only usage of this local
			Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
			break;
		case Binding.FIELD:
			// check if compound assignment is the only usage of a private field
			reportOnlyUselesslyReadPrivateField(currentScope, (FieldBinding)this.binding, valueRequired);
	}
	this.generateCompoundAssignment(
		currentScope,
		codeStream,
		this.syntheticAccessors == null ? null : this.syntheticAccessors[SingleNameReference.WRITE],
		expression,
		operator,
		assignmentImplicitConversion,
		valueRequired);
}

/*
 * The APIs with an extra argument is used whenever there are two references to the same variable which
 * are optimized in one access: e.g "a = a + 1" optimized into "a++".
 */
public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, MethodBinding writeAccessor, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // assigning to a field
			FieldBinding codegenField = ((FieldBinding) this.binding).original();
			if (codegenField.isStatic()) {
				if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
					TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);					
					codeStream.fieldAccess(Opcodes.OPC_getstatic, codegenField, constantPoolDeclaringClass);
				} else {
					codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
				}
			} else {
				if ((this.bits & ASTNode.DepthMASK) != 0) {
					ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT);
					Object[] emulationPath = currentScope.getEmulationPath(targetType, true /*only exact match*/, false/*consider enclosing arg*/);
					codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
				} else {
					codeStream.aload_0();
				}
				codeStream.dup();
				if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
					TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);
					codeStream.fieldAccess(Opcodes.OPC_getfield, codegenField, constantPoolDeclaringClass);
				} else {
					codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
				}
			}
			break;
		case Binding.LOCAL : // assigning to a local variable (cannot assign to outer local)
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			// using incr bytecode if possible
			Constant assignConstant;
			switch (localBinding.type.id) {
				case T_JavaLangString :
					codeStream.generateStringConcatenationAppend(currentScope, this, expression);
					if (valueRequired) {
						codeStream.dup();
					}
					codeStream.store(localBinding, false);
					return;
				case T_int :
					assignConstant = expression.constant;
					if (localBinding.resolvedPosition == -1) {
						if (valueRequired) {
							/*
							 * restart code gen because we either:
							 * - need the value
							 * - the constant can have potential side-effect
							 */
							localBinding.useFlag = LocalVariableBinding.USED;
							throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
						} else if (assignConstant == Constant.NotAConstant) {
							// we only need to generate the value of the expression's constant if it is not a constant expression
							expression.generateCode(currentScope, codeStream, false);
						}
						return;
					}
					if ((assignConstant != Constant.NotAConstant)
							&& (assignConstant.typeID() != TypeIds.T_float) // only for integral types
							&& (assignConstant.typeID() != TypeIds.T_double)) { // TODO (philippe) is this test needed ?
						switch (operator) {
							case PLUS :
								int increment  = assignConstant.intValue();
								if (increment != (short) increment) break; // not representable as a 16-bits value
								codeStream.iinc(localBinding.resolvedPosition, increment);
								if (valueRequired) {
									codeStream.load(localBinding);
								}
								return;
							case MINUS :
								increment  = -assignConstant.intValue();
								if (increment != (short) increment) break; // not representable as a 16-bits value
								codeStream.iinc(localBinding.resolvedPosition, increment);
								if (valueRequired) {
									codeStream.load(localBinding);
								}
								return;
						}
					}
					//$FALL-THROUGH$
				default :
					if (localBinding.resolvedPosition == -1) {
						assignConstant = expression.constant;
						if (valueRequired) {
							/*
							 * restart code gen because we either:
							 * - need the value
							 * - the constant can have potential side-effect
							 */
							localBinding.useFlag = LocalVariableBinding.USED;
							throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
						} else if (assignConstant == Constant.NotAConstant) {
							// we only need to generate the value of the expression's constant if it is not a constant expression
							expression.generateCode(currentScope, codeStream, false);
						}
						return;
					}
					codeStream.load(localBinding);
			}
	}
	// perform the actual compound operation
	int operationTypeID;
	switch(operationTypeID = (this.implicitConversion & TypeIds.IMPLICIT_CONVERSION_MASK) >> 4) {
		case T_JavaLangString :
		case T_JavaLangObject :
		case T_undefined :
			// we enter here if the single name reference is a field of type java.lang.String or if the type of the
			// operation is java.lang.Object
			// For example: o = o + ""; // where the compiled type of o is java.lang.Object.
			codeStream.generateStringConcatenationAppend(currentScope, null, expression);
			// no need for generic cast on previous #getfield since using Object string buffer methods.
			break;
		default :
			// promote the array reference to the suitable operation type
			if (this.genericCast != null)
				codeStream.checkcast(this.genericCast);
			codeStream.generateImplicitConversion(this.implicitConversion);
			// generate the increment value (will by itself  be promoted to the operation value)
			if (expression == IntLiteral.One){ // prefix operation
				codeStream.generateConstant(expression.constant, this.implicitConversion);
			} else {
				expression.generateCode(currentScope, codeStream, true);
			}
			// perform the operation
			codeStream.sendOperator(operator, operationTypeID);
			// cast the value back to the array reference type
			codeStream.generateImplicitConversion(assignmentImplicitConversion);
	}
	// store the result back into the variable
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // assigning to a field
			FieldBinding codegenField = ((FieldBinding) this.binding).original();
			fieldStore(currentScope, codeStream, codegenField, writeAccessor, this.actualReceiverType, true /* implicit this*/, valueRequired);
			// no need for generic cast as value got dupped
			return;
		case Binding.LOCAL : // assigning to a local variable
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			if (valueRequired) {
				switch (localBinding.type.id) {
					case TypeIds.T_long :
					case TypeIds.T_double :
						codeStream.dup2();
						break;
					default:
						codeStream.dup();
						break;
				}
			}
			codeStream.store(localBinding, false);
	}
}

public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired) {
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // assigning to a field
			FieldBinding fieldBinding = (FieldBinding)this.binding;
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=185682
			// check if postIncrement is the only usage of a private field
			reportOnlyUselesslyReadPrivateField(currentScope, fieldBinding, valueRequired);
			FieldBinding codegenField = fieldBinding.original();
			if (codegenField.isStatic()) {
				if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
					TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);
					codeStream.fieldAccess(Opcodes.OPC_getstatic, codegenField, constantPoolDeclaringClass);
				} else {
					codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
				}
			} else {
				if ((this.bits & ASTNode.DepthMASK) != 0) {
					ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT);
					Object[] emulationPath = currentScope.getEmulationPath(targetType, true /*only exact match*/, false/*consider enclosing arg*/);
					codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
				} else {
					codeStream.aload_0();
				}
				codeStream.dup();
				if ((this.syntheticAccessors == null) || (this.syntheticAccessors[SingleNameReference.READ] == null)) {
					TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, codegenField, this.actualReceiverType, true /* implicit this */);
					codeStream.fieldAccess(Opcodes.OPC_getfield, codegenField, constantPoolDeclaringClass);
				} else {
					codeStream.invoke(Opcodes.OPC_invokestatic, this.syntheticAccessors[SingleNameReference.READ], null /* default declaringClass */);
				}
			}
			TypeBinding operandType;
			if (this.genericCast != null) {
				codeStream.checkcast(this.genericCast);
				operandType = this.genericCast;
			} else {
				operandType = codegenField.type;
			}
			if (valueRequired) {
				if (codegenField.isStatic()) {
					switch (operandType.id) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							codeStream.dup2();
							break;
						default:
							codeStream.dup();
							break;
					}					
				} else { // Stack:  [owner][old field value]  ---> [old field value][owner][old field value]
					switch (operandType.id) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							codeStream.dup2_x1();
							break;
						default:
							codeStream.dup_x1();
							break;
					}
				}
			}
			codeStream.generateImplicitConversion(this.implicitConversion);
			codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
			codeStream.sendOperator(postIncrement.operator, this.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
			codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
			fieldStore(currentScope, codeStream, codegenField, this.syntheticAccessors == null ? null : this.syntheticAccessors[SingleNameReference.WRITE], this.actualReceiverType, true /*implicit this*/, false);
			// no need for generic cast 
			return;
		case Binding.LOCAL : // assigning to a local variable
			LocalVariableBinding localBinding = (LocalVariableBinding) this.binding;
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=185682
			// check if postIncrement is the only usage of this local
			Reference.reportOnlyUselesslyReadLocal(currentScope, localBinding, valueRequired);
			if (localBinding.resolvedPosition == -1) {
				if (valueRequired) {
					// restart code gen
					localBinding.useFlag = LocalVariableBinding.USED;
					throw new AbortMethod(CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE, null);
				}
				return;
			}

			// using incr bytecode if possible
			if (localBinding.type == TypeBinding.INT) {
				if (valueRequired) {
					codeStream.load(localBinding);
				}
				if (postIncrement.operator == OperatorIds.PLUS) {
					codeStream.iinc(localBinding.resolvedPosition, 1);
				} else {
					codeStream.iinc(localBinding.resolvedPosition, -1);
				}
			} else {
				codeStream.load(localBinding);
				if (valueRequired){
					switch (localBinding.type.id) {
						case TypeIds.T_long :
						case TypeIds.T_double :
							codeStream.dup2();
							break;
						default:
							codeStream.dup();
							break;
					}					
				}
				codeStream.generateImplicitConversion(this.implicitConversion);
				codeStream.generateConstant(postIncrement.expression.constant, this.implicitConversion);
				codeStream.sendOperator(postIncrement.operator, this.implicitConversion & TypeIds.COMPILE_TYPE_MASK);
				codeStream.generateImplicitConversion(postIncrement.preAssignImplicitConversion);
				codeStream.store(localBinding, false);
			}
	}
}

public void generateReceiver(CodeStream codeStream) {
	codeStream.aload_0();
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.InvocationSite#genericTypeArguments()
 */
public TypeBinding[] genericTypeArguments() {
	return null;
}

/**
 * Returns the local variable referenced by this node. Can be a direct reference (SingleNameReference)
 * or thru a cast expression etc...
 */
public LocalVariableBinding localVariableBinding() {
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // reading a field
			break;
		case Binding.LOCAL : // reading a local variable
			return (LocalVariableBinding) this.binding;
	}
	return null;
}

public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
	//If inlinable field, forget the access emulation, the code gen will directly target it
	if (((this.bits & ASTNode.DepthMASK) == 0) || (this.constant != Constant.NotAConstant)) {
		return;
	}
	if ((this.bits & ASTNode.RestrictiveFlagMASK) == Binding.LOCAL) {
		LocalVariableBinding localVariableBinding = (LocalVariableBinding) this.binding;
		if (localVariableBinding != null) {
			if ((localVariableBinding.tagBits & TagBits.NotInitialized) != 0) {
				// local was tagged as uninitialized
				return;
			}
			switch(localVariableBinding.useFlag) {
				case LocalVariableBinding.FAKE_USED :
				case LocalVariableBinding.USED :
					currentScope.emulateOuterAccess(localVariableBinding);
			}
		}
	}
}

public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo, boolean isReadAccess) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)	return;

	//If inlinable field, forget the access emulation, the code gen will directly target it
	if (this.constant != Constant.NotAConstant)
		return;

	if ((this.bits & Binding.FIELD) != 0) {
		FieldBinding fieldBinding = (FieldBinding) this.binding;
		FieldBinding codegenField = fieldBinding.original();
		if (((this.bits & ASTNode.DepthMASK) != 0)
			&& (codegenField.isPrivate() // private access
				|| (codegenField.isProtected() // implicit protected access
						&& codegenField.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage()))) {
			if (this.syntheticAccessors == null)
				this.syntheticAccessors = new MethodBinding[2];
			this.syntheticAccessors[isReadAccess ? SingleNameReference.READ : SingleNameReference.WRITE] =
			    ((SourceTypeBinding)currentScope.enclosingSourceType().
					enclosingTypeAt((this.bits & ASTNode.DepthMASK) >> ASTNode.DepthSHIFT)).addSyntheticMethod(codegenField, isReadAccess, false /*not super access*/);
			currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, isReadAccess);
			return;
		}
	}
}

public int nullStatus(FlowInfo flowInfo) {
	if (this.constant != null && this.constant != Constant.NotAConstant) {
		return FlowInfo.NON_NULL; // constant expression cannot be null
	}
	switch (this.bits & ASTNode.RestrictiveFlagMASK) {
		case Binding.FIELD : // reading a field
			return FlowInfo.UNKNOWN;
		case Binding.LOCAL : // reading a local variable
			LocalVariableBinding local = (LocalVariableBinding) this.binding;
			if (local != null)
				return flowInfo.nullStatus(local);
	}
	return FlowInfo.NON_NULL; // never get there
}

	/**
 * @see org.eclipse.jdt.internal.compiler.ast.Expression#postConversionType(Scope)
 */
public TypeBinding postConversionType(Scope scope) {
	TypeBinding convertedType = this.resolvedType;
	if (this.genericCast != null)
		convertedType = this.genericCast;
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

public StringBuffer printExpression(int indent, StringBuffer output){
	return output.append(this.token);
}
public TypeBinding reportError(BlockScope scope) {
	//=====error cases=======
	this.constant = Constant.NotAConstant;
	if (this.binding instanceof ProblemFieldBinding) {
		scope.problemReporter().invalidField(this, (FieldBinding) this.binding);
	} else if (this.binding instanceof ProblemReferenceBinding || this.binding instanceof MissingTypeBinding) {
		scope.problemReporter().invalidType(this, (TypeBinding) this.binding);
	} else {
		scope.problemReporter().unresolvableReference(this, this.binding);
	}
	return null;
}

public TypeBinding resolveType(BlockScope scope) {
	// for code gen, harm the restrictiveFlag

	if (this.actualReceiverType != null) {
		this.binding = scope.getField(this.actualReceiverType, this.token, this);
	} else {
		this.actualReceiverType = scope.enclosingSourceType();
		this.binding = scope.getBinding(this.token, this.bits & ASTNode.RestrictiveFlagMASK, this, true /*resolve*/);
	}
	if (this.binding.isValidBinding()) {
		switch (this.bits & ASTNode.RestrictiveFlagMASK) {
			case Binding.VARIABLE : // =========only variable============
			case Binding.VARIABLE | Binding.TYPE : //====both variable and type============
				if (this.binding instanceof VariableBinding) {
					VariableBinding variable = (VariableBinding) this.binding;
					TypeBinding variableType;
					if (this.binding instanceof LocalVariableBinding) {
						this.bits &= ~ASTNode.RestrictiveFlagMASK;  // clear bits
						this.bits |= Binding.LOCAL;
						if (!variable.isFinal() && (this.bits & ASTNode.DepthMASK) != 0) {
							scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)variable, this);
						}
						variableType = variable.type;
						this.constant = (this.bits & ASTNode.IsStrictlyAssigned) == 0 ? variable.constant() : Constant.NotAConstant;
					} else {
						// a field
						variableType = checkFieldAccess(scope);
					}
					// perform capture conversion if read access
					if (variableType != null) {
						this.resolvedType = variableType = (((this.bits & ASTNode.IsStrictlyAssigned) == 0)
								? variableType.capture(scope, this.sourceEnd)
								: variableType);
						if ((variableType.tagBits & TagBits.HasMissingType) != 0) {
							if ((this.bits & Binding.LOCAL) == 0) {
								// only complain if field reference (for local, its type got flagged already)
								scope.problemReporter().invalidType(this, variableType);
							}
							return null;
						}
					}
					return variableType;
				}

				// thus it was a type
				this.bits &= ~ASTNode.RestrictiveFlagMASK;  // clear bits
				this.bits |= Binding.TYPE;
				//$FALL-THROUGH$
			case Binding.TYPE : //========only type==============
				this.constant = Constant.NotAConstant;
				//deprecated test
				TypeBinding type = (TypeBinding)this.binding;
				if (isTypeUseDeprecated(type, scope))
					scope.problemReporter().deprecatedType(type, this);
				type = scope.environment().convertToRawType(type, false /*do not force conversion of enclosing types*/);
				return this.resolvedType = type;
		}
	}
	// error scenarii
	return this.resolvedType = reportError(scope);
}

public void traverse(ASTVisitor visitor, BlockScope scope) {
	visitor.visit(this, scope);
	visitor.endVisit(this, scope);
}

public void traverse(ASTVisitor visitor, ClassScope scope) {
	visitor.visit(this, scope);
	visitor.endVisit(this, scope);
}

public String unboundReferenceErrorName(){
	return new String(this.token);
}
}
