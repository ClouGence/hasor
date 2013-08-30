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

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Util;

public class FieldDeclaration extends AbstractVariableDeclaration {

	public FieldBinding binding;
	public Javadoc javadoc;

	//allows to retrieve both the "type" part of the declaration (part1)
	//and also the part that decribe the name and the init and optionally
	//some other dimension ! ....
	//public int[] a, b[] = X, c ;
	//for b that would give for
	// - part1 : public int[]
	// - part2 : b[] = X,

	public int endPart1Position;
	public int endPart2Position;

public FieldDeclaration() {
	// for subtypes or conversion
}

public FieldDeclaration(	char[] name, int sourceStart, int sourceEnd) {
	this.name = name;
	//due to some declaration like
	// int x, y = 3, z , x ;
	//the sourceStart and the sourceEnd is ONLY on  the name
	this.sourceStart = sourceStart;
	this.sourceEnd = sourceEnd;
}

public FlowInfo analyseCode(MethodScope initializationScope, FlowContext flowContext, FlowInfo flowInfo) {
	if (this.binding != null && !this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType()) {
		if (!initializationScope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
			initializationScope.problemReporter().unusedPrivateField(this);
		}
	}
	// cannot define static non-constant field inside nested class
	if (this.binding != null
			&& this.binding.isValidBinding()
			&& this.binding.isStatic()
			&& this.binding.constant() == Constant.NotAConstant
			&& this.binding.declaringClass.isNestedType()
			&& !this.binding.declaringClass.isStatic()) {
		initializationScope.problemReporter().unexpectedStaticModifierForField(
			(SourceTypeBinding) this.binding.declaringClass,
			this);
	}

	if (this.initialization != null) {
		flowInfo =
			this.initialization
				.analyseCode(initializationScope, flowContext, flowInfo)
				.unconditionalInits();
		flowInfo.markAsDefinitelyAssigned(this.binding);
	}
	return flowInfo;
}

/**
 * Code generation for a field declaration:
 *	   standard assignment to a field
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream) {
	if ((this.bits & IsReachable) == 0) {
		return;
	}
	// do not generate initialization code if final and static (constant is then
	// recorded inside the field itself).
	int pc = codeStream.position;
	boolean isStatic;
	if (this.initialization != null
			&& !((isStatic = this.binding.isStatic()) && this.binding.constant() != Constant.NotAConstant)) {
		// non-static field, need receiver
		if (!isStatic)
			codeStream.aload_0();
		// generate initialization value
		this.initialization.generateCode(currentScope, codeStream, true);
		// store into field
		if (isStatic) {
			codeStream.fieldAccess(Opcodes.OPC_putstatic, this.binding, null /* default declaringClass */);
		} else {
			codeStream.fieldAccess(Opcodes.OPC_putfield, this.binding, null /* default declaringClass */);
		}
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration#getKind()
 */
public int getKind() {
	return this.type == null ? ENUM_CONSTANT : FIELD;
}

public boolean isStatic() {
	if (this.binding != null)
		return this.binding.isStatic();
	return (this.modifiers & ClassFileConstants.AccStatic) != 0;
}

public StringBuffer printStatement(int indent, StringBuffer output) {
	if (this.javadoc != null) {
		this.javadoc.print(indent, output);
	}
	return super.printStatement(indent, output);
}

public void resolve(MethodScope initializationScope) {
	// the two <constant = Constant.NotAConstant> could be regrouped into
	// a single line but it is clearer to have two lines while the reason of their
	// existence is not at all the same. See comment for the second one.

	//--------------------------------------------------------
	if ((this.bits & ASTNode.HasBeenResolved) != 0) return;
	if (this.binding == null || !this.binding.isValidBinding()) return;

	this.bits |= ASTNode.HasBeenResolved;

	// check if field is hiding some variable - issue is that field binding already got inserted in scope
	// thus must lookup separately in super type and outer context
	ClassScope classScope = initializationScope.enclosingClassScope();

	if (classScope != null) {
		checkHiding: {
			SourceTypeBinding declaringType = classScope.enclosingSourceType();
			checkHidingSuperField: {
				if (declaringType.superclass == null) break checkHidingSuperField;
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318171, find field skipping visibility checks
				// we do the checks below ourselves, using the appropriate conditions for access check of
				// protected members from superclasses.
				FieldBinding existingVariable = classScope.findField(declaringType.superclass, this.name, this,  false /*do not resolve hidden field*/, true /* no visibility checks please */);
				if (existingVariable == null) break checkHidingSuperField; // keep checking outer scenario
				if (!existingVariable.isValidBinding())  break checkHidingSuperField; // keep checking outer scenario
				if (existingVariable.original() == this.binding) break checkHidingSuperField; // keep checking outer scenario
				if (!existingVariable.canBeSeenBy(declaringType, this, initializationScope)) break checkHidingSuperField; // keep checking outer scenario
				// collision with supertype field
				initializationScope.problemReporter().fieldHiding(this, existingVariable);
				break checkHiding; // already found a matching field
			}
			// only corner case is: lookup of outer field through static declaringType, which isn't detected by #getBinding as lookup starts
			// from outer scope. Subsequent static contexts are detected for free.
			Scope outerScope = classScope.parent;
			if (outerScope.kind == Scope.COMPILATION_UNIT_SCOPE) break checkHiding;
			Binding existingVariable = outerScope.getBinding(this.name, Binding.VARIABLE, this, false /*do not resolve hidden field*/);
			if (existingVariable == null) break checkHiding;
			if (!existingVariable.isValidBinding()) break checkHiding;
			if (existingVariable == this.binding) break checkHiding;
			if (existingVariable instanceof FieldBinding) {
				FieldBinding existingField = (FieldBinding) existingVariable;
				if (existingField.original() == this.binding) break checkHiding;
				if (!existingField.isStatic() && declaringType.isStatic()) break checkHiding;
			}
			// collision with outer field or local variable
			initializationScope.problemReporter().fieldHiding(this, existingVariable);
		}
	}

	if (this.type != null ) { // enum constants have no declared type
		this.type.resolvedType = this.binding.type; // update binding for type reference
	}

	FieldBinding previousField = initializationScope.initializedField;
	int previousFieldID = initializationScope.lastVisibleFieldID;
	try {
		initializationScope.initializedField = this.binding;
		initializationScope.lastVisibleFieldID = this.binding.id;

		resolveAnnotations(initializationScope, this.annotations, this.binding);
		// check @Deprecated annotation presence
		if ((this.binding.getAnnotationTagBits() & TagBits.AnnotationDeprecated) == 0
				&& (this.binding.modifiers & ClassFileConstants.AccDeprecated) != 0
				&& initializationScope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
			initializationScope.problemReporter().missingDeprecatedAnnotationForField(this);
		}
		// the resolution of the initialization hasn't been done
		if (this.initialization == null) {
			this.binding.setConstant(Constant.NotAConstant);
		} else {
			// break dead-lock cycles by forcing constant to NotAConstant
			this.binding.setConstant(Constant.NotAConstant);

			TypeBinding fieldType = this.binding.type;
			TypeBinding initializationType;
			this.initialization.setExpectedType(fieldType); // needed in case of generic method invocation
			if (this.initialization instanceof ArrayInitializer) {

				if ((initializationType = this.initialization.resolveTypeExpecting(initializationScope, fieldType)) != null) {
					((ArrayInitializer) this.initialization).binding = (ArrayBinding) initializationType;
					this.initialization.computeConversion(initializationScope, fieldType, initializationType);
				}
			} else if ((initializationType = this.initialization.resolveType(initializationScope)) != null) {

				if (fieldType != initializationType) // must call before computeConversion() and typeMismatchError()
					initializationScope.compilationUnitScope().recordTypeConversion(fieldType, initializationType);
				if (this.initialization.isConstantValueOfTypeAssignableToType(initializationType, fieldType)
						|| initializationType.isCompatibleWith(fieldType)) {
					this.initialization.computeConversion(initializationScope, fieldType, initializationType);
					if (initializationType.needsUncheckedConversion(fieldType)) {
						    initializationScope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, fieldType);
					}
					if (this.initialization instanceof CastExpression
							&& (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
						CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression) this.initialization);
					}
				} else if (isBoxingCompatible(initializationType, fieldType, this.initialization, initializationScope)) {
					this.initialization.computeConversion(initializationScope, fieldType, initializationType);
					if (this.initialization instanceof CastExpression
							&& (this.initialization.bits & ASTNode.UnnecessaryCast) == 0) {
						CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression) this.initialization);
					}
				} else {
					if ((fieldType.tagBits & TagBits.HasMissingType) == 0) {
						// if problem already got signaled on type, do not report secondary problem
						initializationScope.problemReporter().typeMismatchError(initializationType, fieldType, this.initialization, null);
					}
				}
				if (this.binding.isFinal()){ // cast from constant actual type to variable type
					this.binding.setConstant(this.initialization.constant.castTo((this.binding.type.id << 4) + this.initialization.constant.typeID()));
				}
			} else {
				this.binding.setConstant(Constant.NotAConstant);
			}
			// check for assignment with no effect
			if (this.binding == Expression.getDirectBinding(this.initialization)) {
				initializationScope.problemReporter().assignmentHasNoEffect(this, this.name);
			}
		}
		// Resolve Javadoc comment if one is present
		if (this.javadoc != null) {
			this.javadoc.resolve(initializationScope);
		} else if (this.binding != null && this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
			// Set javadoc visibility
			int javadocVisibility = this.binding.modifiers & ExtraCompilerModifiers.AccVisibilityMASK;
			ProblemReporter reporter = initializationScope.problemReporter();
			int severity = reporter.computeSeverity(IProblem.JavadocMissing);
			if (severity != ProblemSeverities.Ignore) {
				if (classScope != null) {
					javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
				}
				int javadocModifiers = (this.binding.modifiers & ~ExtraCompilerModifiers.AccVisibilityMASK) | javadocVisibility;
				reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
			}
		}
	} finally {
		initializationScope.initializedField = previousField;
		initializationScope.lastVisibleFieldID = previousFieldID;
		if (this.binding.constant() == null)
			this.binding.setConstant(Constant.NotAConstant);
	}
}

public void traverse(ASTVisitor visitor, MethodScope scope) {
	if (visitor.visit(this, scope)) {
		if (this.javadoc != null) {
			this.javadoc.traverse(visitor, scope);
		}
		if (this.annotations != null) {
			int annotationsLength = this.annotations.length;
			for (int i = 0; i < annotationsLength; i++)
				this.annotations[i].traverse(visitor, scope);
		}
		if (this.type != null) {
			this.type.traverse(visitor, scope);
		}
		if (this.initialization != null)
			this.initialization.traverse(visitor, scope);
	}
	visitor.endVisit(this, scope);
}
}
