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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.RawTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

public class ExplicitConstructorCall extends Statement implements InvocationSite {

	public Expression[] arguments;
	public Expression qualification;
	public MethodBinding binding;							// exact binding resulting from lookup
	MethodBinding syntheticAccessor;						// synthetic accessor for inner-emulation
	public int accessMode;
	public TypeReference[] typeArguments;
	public TypeBinding[] genericTypeArguments;

	public final static int ImplicitSuper = 1;
	public final static int Super = 2;
	public final static int This = 3;

	public VariableBinding[][] implicitArguments;

	// TODO Remove once DOMParser is activated
	public int typeArgumentsSourceStart;

	public ExplicitConstructorCall(int accessMode) {
		this.accessMode = accessMode;
	}

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		// must verify that exceptions potentially thrown by this expression are caught in the method.

		try {
			((MethodScope) currentScope).isConstructorCall = true;

			// process enclosing instance
			if (this.qualification != null) {
				flowInfo =
					this.qualification
						.analyseCode(currentScope, flowContext, flowInfo)
						.unconditionalInits();
			}
			// process arguments
			if (this.arguments != null) {
				for (int i = 0, max = this.arguments.length; i < max; i++) {
					flowInfo =
						this.arguments[i]
							.analyseCode(currentScope, flowContext, flowInfo)
							.unconditionalInits();
					if ((this.arguments[i].implicitConversion & TypeIds.UNBOXING) != 0) {
						this.arguments[i].checkNPE(currentScope, flowContext, flowInfo);
					}
				}
			}

			ReferenceBinding[] thrownExceptions;
			if ((thrownExceptions = this.binding.thrownExceptions) != Binding.NO_EXCEPTIONS) {
				if ((this.bits & ASTNode.Unchecked) != 0 && this.genericTypeArguments == null) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=277643, align with javac on JLS 15.12.2.6
					thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
				}				
				// check exceptions
				flowContext.checkExceptionHandlers(
					thrownExceptions,
					(this.accessMode == ExplicitConstructorCall.ImplicitSuper)
						? (ASTNode) currentScope.methodScope().referenceContext
						: (ASTNode) this,
					flowInfo,
					currentScope);
			}
			manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
			manageSyntheticAccessIfNecessary(currentScope, flowInfo);
			return flowInfo;
		} finally {
			((MethodScope) currentScope).isConstructorCall = false;
		}
	}

	/**
	 * Constructor call code generation
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream) {
		if ((this.bits & ASTNode.IsReachable) == 0) {
			return;
		}
		try {
			((MethodScope) currentScope).isConstructorCall = true;

			int pc = codeStream.position;
			codeStream.aload_0();

			MethodBinding codegenBinding = this.binding.original();
			ReferenceBinding targetType = codegenBinding.declaringClass;

			// special name&ordinal argument generation for enum constructors
			if (targetType.erasure().id == TypeIds.T_JavaLangEnum || targetType.isEnum()) {
				codeStream.aload_1(); // pass along name param as name arg
				codeStream.iload_2(); // pass along ordinal param as ordinal arg
			}
			// handling innerclass constructor invocation
			// handling innerclass instance allocation - enclosing instance arguments
			if (targetType.isNestedType()) {
				codeStream.generateSyntheticEnclosingInstanceValues(
					currentScope,
					targetType,
					(this.bits & ASTNode.DiscardEnclosingInstance) != 0 ? null : this.qualification,
					this);
			}
			// generate arguments
			generateArguments(this.binding, this.arguments, currentScope, codeStream);

			// handling innerclass instance allocation - outer local arguments
			if (targetType.isNestedType()) {
				codeStream.generateSyntheticOuterArgumentValues(
					currentScope,
					targetType,
					this);
			}
			if (this.syntheticAccessor != null) {
				// synthetic accessor got some extra arguments appended to its signature, which need values
				for (int i = 0,
					max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
					i < max;
					i++) {
					codeStream.aconst_null();
				}
				codeStream.invoke(Opcodes.OPC_invokespecial, this.syntheticAccessor, null /* default declaringClass */);
			} else {
				codeStream.invoke(Opcodes.OPC_invokespecial, codegenBinding, null /* default declaringClass */);
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
		} finally {
			((MethodScope) currentScope).isConstructorCall = false;
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.InvocationSite#genericTypeArguments()
	 */
	public TypeBinding[] genericTypeArguments() {
		return this.genericTypeArguments;
	}

	public boolean isImplicitSuper() {
		return (this.accessMode == ExplicitConstructorCall.ImplicitSuper);
	}

	public boolean isSuperAccess() {
		return this.accessMode != ExplicitConstructorCall.This;
	}

	public boolean isTypeAccess() {
		return true;
	}

	/* Inner emulation consists in either recording a dependency
	 * link only, or performing one level of propagation.
	 *
	 * Dependency mechanism is used whenever dealing with source target
	 * types, since by the time we reach them, we might not yet know their
	 * exact need.
	 */
	void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
		ReferenceBinding superTypeErasure = (ReferenceBinding) this.binding.declaringClass.erasure();

		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0)	{
		// perform some emulation work in case there is some and we are inside a local type only
		if (superTypeErasure.isNestedType()
			&& currentScope.enclosingSourceType().isLocalType()) {

			if (superTypeErasure.isLocalType()) {
				((LocalTypeBinding) superTypeErasure).addInnerEmulationDependent(currentScope, this.qualification != null);
			} else {
				// locally propagate, since we already now the desired shape for sure
				currentScope.propagateInnerEmulation(superTypeErasure, this.qualification != null);
			}
		}
		}
	}

	public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0)	{
			// if constructor from parameterized type got found, use the original constructor at codegen time
			MethodBinding codegenBinding = this.binding.original();

			// perform some emulation work in case there is some and we are inside a local type only
			if (this.binding.isPrivate() && this.accessMode != ExplicitConstructorCall.This) {
				ReferenceBinding declaringClass = codegenBinding.declaringClass;
				// from 1.4 on, local type constructor can lose their private flag to ease emulation
				if ((declaringClass.tagBits & TagBits.IsLocalType) != 0 && currentScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_4) {
					// constructor will not be dumped as private, no emulation required thus
					codegenBinding.tagBits |= TagBits.ClearPrivateModifier;
				} else {
					this.syntheticAccessor = ((SourceTypeBinding) declaringClass).addSyntheticMethod(codegenBinding, isSuperAccess());
					currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
				}
			}
		}
	}

	public StringBuffer printStatement(int indent, StringBuffer output) {
		printIndent(indent, output);
		if (this.qualification != null) this.qualification.printExpression(0, output).append('.');
		if (this.typeArguments != null) {
			output.append('<');
			int max = this.typeArguments.length - 1;
			for (int j = 0; j < max; j++) {
				this.typeArguments[j].print(0, output);
				output.append(", ");//$NON-NLS-1$
			}
			this.typeArguments[max].print(0, output);
			output.append('>');
		}
		if (this.accessMode == ExplicitConstructorCall.This) {
			output.append("this("); //$NON-NLS-1$
		} else {
			output.append("super("); //$NON-NLS-1$
		}
		if (this.arguments != null) {
			for (int i = 0; i < this.arguments.length; i++) {
				if (i > 0) output.append(", "); //$NON-NLS-1$
				this.arguments[i].printExpression(0, output);
			}
		}
		return output.append(");"); //$NON-NLS-1$
	}

	public void resolve(BlockScope scope) {
		// the return type should be void for a constructor.
		// the test is made into getConstructor

		// mark the fact that we are in a constructor call.....
		// unmark at all returns
		MethodScope methodScope = scope.methodScope();
		try {
			AbstractMethodDeclaration methodDeclaration = methodScope.referenceMethod();
			if (methodDeclaration == null
					|| !methodDeclaration.isConstructor()
					|| ((ConstructorDeclaration) methodDeclaration).constructorCall != this) {
				scope.problemReporter().invalidExplicitConstructorCall(this);
				// fault-tolerance
				if (this.qualification != null) {
					this.qualification.resolveType(scope);
				}
				if (this.typeArguments != null) {
					for (int i = 0, max = this.typeArguments.length; i < max; i++) {
						this.typeArguments[i].resolveType(scope, true /* check bounds*/);
					}
				}
				if (this.arguments != null) {
					for (int i = 0, max = this.arguments.length; i < max; i++) {
						this.arguments[i].resolveType(scope);
					}
				}
				return;
			}
			methodScope.isConstructorCall = true;
			ReferenceBinding receiverType = scope.enclosingReceiverType();
			boolean rcvHasError = false;
			if (this.accessMode != ExplicitConstructorCall.This) {
				receiverType = receiverType.superclass();
				TypeReference superclassRef = scope.referenceType().superclass;
				if (superclassRef != null && superclassRef.resolvedType != null && !superclassRef.resolvedType.isValidBinding()) {
					rcvHasError = true;
				}
			}
			if (receiverType != null) {
				// prevent (explicit) super constructor invocation from within enum
				if (this.accessMode == ExplicitConstructorCall.Super && receiverType.erasure().id == TypeIds.T_JavaLangEnum) {
					scope.problemReporter().cannotInvokeSuperConstructorInEnum(this, methodScope.referenceMethod().binding);
				}
				// qualification should be from the type of the enclosingType
				if (this.qualification != null) {
					if (this.accessMode != ExplicitConstructorCall.Super) {
						scope.problemReporter().unnecessaryEnclosingInstanceSpecification(
							this.qualification,
							receiverType);
					}
					if (!rcvHasError) {
						ReferenceBinding enclosingType = receiverType.enclosingType();
						if (enclosingType == null) {
							scope.problemReporter().unnecessaryEnclosingInstanceSpecification(this.qualification, receiverType);
							this.bits |= ASTNode.DiscardEnclosingInstance;
						} else {
							TypeBinding qTb = this.qualification.resolveTypeExpecting(scope, enclosingType);
							this.qualification.computeConversion(scope, qTb, qTb);
						}
					}
				}
			}
			// resolve type arguments (for generic constructor call)
			if (this.typeArguments != null) {
				boolean argHasError = scope.compilerOptions().sourceLevel < ClassFileConstants.JDK1_5;
				int length = this.typeArguments.length;
				this.genericTypeArguments = new TypeBinding[length];
				for (int i = 0; i < length; i++) {
					TypeReference typeReference = this.typeArguments[i];
					if ((this.genericTypeArguments[i] = typeReference.resolveType(scope, true /* check bounds*/)) == null) {
						argHasError = true;
					}
					if (argHasError && typeReference instanceof Wildcard) {
						scope.problemReporter().illegalUsageOfWildcard(typeReference);
					}
				}
				if (argHasError) {
					if (this.arguments != null) { // still attempt to resolve arguments
						for (int i = 0, max = this.arguments.length; i < max; i++) {
							this.arguments[i].resolveType(scope);
						}
					}
					return;
				}
			}
			// arguments buffering for the method lookup
			TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
			boolean argsContainCast = false;
			if (this.arguments != null) {
				boolean argHasError = false; // typeChecks all arguments
				int length = this.arguments.length;
				argumentTypes = new TypeBinding[length];
				for (int i = 0; i < length; i++) {
					Expression argument = this.arguments[i];
					if (argument instanceof CastExpression) {
						argument.bits |= ASTNode.DisableUnnecessaryCastCheck; // will check later on
						argsContainCast = true;
					}
					if ((argumentTypes[i] = argument.resolveType(scope)) == null) {
						argHasError = true;
					}
				}
				if (argHasError) {
					if (receiverType == null) {
						return;
					}
					// record a best guess, for clients who need hint about possible contructor match
					TypeBinding[] pseudoArgs = new TypeBinding[length];
					for (int i = length; --i >= 0;) {
						pseudoArgs[i] = argumentTypes[i] == null ? TypeBinding.NULL : argumentTypes[i]; // replace args with errors with null type
					}
					this.binding = scope.findMethod(receiverType, TypeConstants.INIT, pseudoArgs, this);
					if (this.binding != null && !this.binding.isValidBinding()) {
						MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
						// record the closest match, for clients who may still need hint about possible method match
						if (closestMatch != null) {
							if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES) { // generic method
								// shouldn't return generic method outside its context, rather convert it to raw method (175409)
								closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), (RawTypeBinding)null);
							}
							this.binding = closestMatch;
							MethodBinding closestMatchOriginal = closestMatch.original();
							if (closestMatchOriginal.isOrEnclosedByPrivateType() && !scope.isDefinedInMethod(closestMatchOriginal)) {
								// ignore cases where method is used from within inside itself (e.g. direct recursions)
								closestMatchOriginal.modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
							}
						}
					}
					return;
				}
			} else if (receiverType.erasure().id == TypeIds.T_JavaLangEnum) {
				// TODO (philippe) get rid of once well-known binding is available
				argumentTypes = new TypeBinding[] { scope.getJavaLangString(), TypeBinding.INT };
			}
			if (receiverType == null) {
				return;
			}
			if ((this.binding = scope.getConstructor(receiverType, argumentTypes, this)).isValidBinding()) {
				if ((this.binding.tagBits & TagBits.HasMissingType) != 0) {
					if (!methodScope.enclosingSourceType().isAnonymousType()) {
						scope.problemReporter().missingTypeInConstructor(this, this.binding);
					}
				}
				if (isMethodUseDeprecated(this.binding, scope, this.accessMode != ExplicitConstructorCall.ImplicitSuper)) {
					scope.problemReporter().deprecatedMethod(this.binding, this);
				}
				if (checkInvocationArguments(scope, null, receiverType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
					this.bits |= ASTNode.Unchecked;
				}
				if (this.binding.isOrEnclosedByPrivateType()) {
					this.binding.original().modifiers |= ExtraCompilerModifiers.AccLocallyUsed;
				}
				if (this.typeArguments != null
						&& this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES) {
					scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
				}
			} else {
				if (this.binding.declaringClass == null) {
					this.binding.declaringClass = receiverType;
				}
				if (rcvHasError)
					return;
				scope.problemReporter().invalidConstructor(this, this.binding);
			}
		} finally {
			methodScope.isConstructorCall = false;
		}
	}

	public void setActualReceiverType(ReferenceBinding receiverType) {
		// ignored
	}

	public void setDepth(int depth) {
		// ignore for here
	}

	public void setFieldIndex(int depth) {
		// ignore for here
	}
	
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.qualification != null) {
				this.qualification.traverse(visitor, scope);
			}
			if (this.typeArguments != null) {
				for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
					this.typeArguments[i].traverse(visitor, scope);
				}
			}
			if (this.arguments != null) {
				for (int i = 0, argumentLength = this.arguments.length; i < argumentLength; i++)
					this.arguments[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
}
