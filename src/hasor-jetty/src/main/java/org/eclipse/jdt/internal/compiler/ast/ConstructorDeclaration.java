/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for Bug 343713 - [compiler] bogus line number in constructor of inner class in 1.5 compliance
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;

import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.parser.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ConstructorDeclaration extends AbstractMethodDeclaration {

	public ExplicitConstructorCall constructorCall;

	public TypeParameter[] typeParameters;

public ConstructorDeclaration(CompilationResult compilationResult){
	super(compilationResult);
}

/**
 * @see org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration#analyseCode(org.eclipse.jdt.internal.compiler.lookup.ClassScope, org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext, org.eclipse.jdt.internal.compiler.flow.FlowInfo)
 * @deprecated use instead {@link #analyseCode(ClassScope, InitializationFlowContext, FlowInfo, int)}
 */
public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo) {
	analyseCode(classScope, initializerFlowContext, flowInfo, FlowInfo.REACHABLE);
}

/**
 * The flowInfo corresponds to non-static field initialization infos. It may be unreachable (155423), but still the explicit constructor call must be
 * analysed as reachable, since it will be generated in the end.
 */
public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo, int initialReachMode) {
	if (this.ignoreFurtherInvestigation)
		return;

	int nonStaticFieldInfoReachMode = flowInfo.reachMode();
	flowInfo.setReachMode(initialReachMode);

	checkUnused: {
		MethodBinding constructorBinding;
		if ((constructorBinding = this.binding) == null) break checkUnused;
		if ((this.bits & ASTNode.IsDefaultConstructor) != 0) break checkUnused;
		if (constructorBinding.isUsed()) break checkUnused;
		if (constructorBinding.isPrivate()) {
			if ((this.binding.declaringClass.tagBits & TagBits.HasNonPrivateConstructor) == 0)
				break checkUnused; // tolerate as known pattern to block instantiation
		} else if (!constructorBinding.isOrEnclosedByPrivateType()) {
			break checkUnused;
 		}
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=270446, When the AST built is an abridged version
		// we don't have all tree nodes we would otherwise expect. (see ASTParser.setFocalPosition)
		if (this.constructorCall == null)
			break checkUnused; 
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=264991, Don't complain about this
		// constructor being unused if the base class doesn't have a no-arg constructor.
		// See that a seemingly unused constructor that chains to another constructor with a
		// this(...) can be flagged as being unused without hesitation.
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=265142
		if (this.constructorCall.accessMode != ExplicitConstructorCall.This) {
			ReferenceBinding superClass = constructorBinding.declaringClass.superclass();
			if (superClass == null)
				break checkUnused;
			// see if there is a no-arg super constructor
			MethodBinding methodBinding = superClass.getExactConstructor(Binding.NO_PARAMETERS);
			if (methodBinding == null)
				break checkUnused;
			if (!methodBinding.canBeSeenBy(SuperReference.implicitSuperConstructorCall(), this.scope))
				break checkUnused;
			// otherwise default super constructor exists, so go ahead and complain unused.
		}
		// complain unused
		this.scope.problemReporter().unusedPrivateConstructor(this);
	}

	// check constructor recursion, once all constructor got resolved
	if (isRecursive(null /*lazy initialized visited list*/)) {
		this.scope.problemReporter().recursiveConstructorInvocation(this.constructorCall);
	}

	try {
		ExceptionHandlingFlowContext constructorContext =
			new ExceptionHandlingFlowContext(
				initializerFlowContext.parent,
				this,
				this.binding.thrownExceptions,
				initializerFlowContext,
				this.scope,
				FlowInfo.DEAD_END);
		initializerFlowContext.checkInitializerExceptions(
			this.scope,
			constructorContext,
			flowInfo);

		// anonymous constructor can gain extra thrown exceptions from unhandled ones
		if (this.binding.declaringClass.isAnonymousType()) {
			ArrayList computedExceptions = constructorContext.extendedExceptions;
			if (computedExceptions != null){
				int size;
				if ((size = computedExceptions.size()) > 0){
					ReferenceBinding[] actuallyThrownExceptions;
					computedExceptions.toArray(actuallyThrownExceptions = new ReferenceBinding[size]);
					this.binding.thrownExceptions = actuallyThrownExceptions;
				}
			}
		}

		// tag parameters as being set
		if (this.arguments != null) {
			for (int i = 0, count = this.arguments.length; i < count; i++) {
				flowInfo.markAsDefinitelyAssigned(this.arguments[i].binding);
			}
		}

		// propagate to constructor call
		if (this.constructorCall != null) {
			// if calling 'this(...)', then flag all non-static fields as definitely
			// set since they are supposed to be set inside other local constructor
			if (this.constructorCall.accessMode == ExplicitConstructorCall.This) {
				FieldBinding[] fields = this.binding.declaringClass.fields();
				for (int i = 0, count = fields.length; i < count; i++) {
					FieldBinding field;
					if (!(field = fields[i]).isStatic()) {
						flowInfo.markAsDefinitelyAssigned(field);
					}
				}
			}
			flowInfo = this.constructorCall.analyseCode(this.scope, constructorContext, flowInfo);
		}

		// reuse the reachMode from non static field info
		flowInfo.setReachMode(nonStaticFieldInfoReachMode);

		// propagate to statements
		if (this.statements != null) {
			int complaintLevel = (nonStaticFieldInfoReachMode & FlowInfo.UNREACHABLE) == 0 ? Statement.NOT_COMPLAINED : Statement.COMPLAINED_FAKE_REACHABLE;
			for (int i = 0, count = this.statements.length; i < count; i++) {
				Statement stat = this.statements[i];
				if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel)) < Statement.COMPLAINED_UNREACHABLE) {
					flowInfo = stat.analyseCode(this.scope, constructorContext, flowInfo);
				}
			}
		}
		// check for missing returning path
		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
			this.bits |= ASTNode.NeedFreeReturn;
		}

		// reuse the initial reach mode for diagnosing missing blank finals
		// no, we should use the updated reach mode for diagnosing uninitialized blank finals.
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=235781
		// flowInfo.setReachMode(initialReachMode);

		// check missing blank final field initializations
		if ((this.constructorCall != null)
			&& (this.constructorCall.accessMode != ExplicitConstructorCall.This)) {
			flowInfo = flowInfo.mergedWith(constructorContext.initsOnReturn);
			FieldBinding[] fields = this.binding.declaringClass.fields();
			for (int i = 0, count = fields.length; i < count; i++) {
				FieldBinding field;
				if ((!(field = fields[i]).isStatic())
					&& field.isFinal()
					&& (!flowInfo.isDefinitelyAssigned(fields[i]))) {
					this.scope.problemReporter().uninitializedBlankFinalField(
						field,
						((this.bits & ASTNode.IsDefaultConstructor) != 0) ? (ASTNode) this.scope.referenceType() : this);
				}
			}
		}
		// check unreachable catch blocks
		constructorContext.complainIfUnusedExceptionHandlers(this);
		// check unused parameters
		this.scope.checkUnusedParameters(this.binding);
	} catch (AbortMethod e) {
		this.ignoreFurtherInvestigation = true;
	}
}

/**
 * Bytecode generation for a constructor
 *
 * @param classScope org.eclipse.jdt.internal.compiler.lookup.ClassScope
 * @param classFile org.eclipse.jdt.internal.compiler.codegen.ClassFile
 */
public void generateCode(ClassScope classScope, ClassFile classFile) {
	int problemResetPC = 0;
	if (this.ignoreFurtherInvestigation) {
		if (this.binding == null)
			return; // Handle methods with invalid signature or duplicates
		int problemsLength;
		CategorizedProblem[] problems =
			this.scope.referenceCompilationUnit().compilationResult.getProblems();
		CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
		System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
		classFile.addProblemConstructor(this, this.binding, problemsCopy);
		return;
	}
	boolean restart = false;
	boolean abort = false;
	do {
		try {
			problemResetPC = classFile.contentsOffset;
			internalGenerateCode(classScope, classFile);
			restart = false;
		} catch (AbortMethod e) {
			if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
				// a branch target required a goto_w, restart code gen in wide mode.
				if (!restart) {
					classFile.contentsOffset = problemResetPC;
					classFile.methodCount--;
					classFile.codeStream.resetInWideMode(); // request wide mode
					restart = true;
				} else {
					restart = false;
					abort = true;
				}
			} else if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
				classFile.contentsOffset = problemResetPC;
				classFile.methodCount--;
				classFile.codeStream.resetForCodeGenUnusedLocals();
				restart = true;
			} else {
				restart = false;
				abort = true;
			}
		}
	} while (restart);
	if (abort) {
		int problemsLength;
		CategorizedProblem[] problems =
				this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
		CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
		System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
		classFile.addProblemConstructor(this, this.binding, problemsCopy, problemResetPC);
	}
}

public void generateSyntheticFieldInitializationsIfNecessary(MethodScope methodScope, CodeStream codeStream, ReferenceBinding declaringClass) {
	if (!declaringClass.isNestedType()) return;

	NestedTypeBinding nestedType = (NestedTypeBinding) declaringClass;

	SyntheticArgumentBinding[] syntheticArgs = nestedType.syntheticEnclosingInstances();
	if (syntheticArgs != null) {
		for (int i = 0, max = syntheticArgs.length; i < max; i++) {
			SyntheticArgumentBinding syntheticArg;
			if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
				codeStream.aload_0();
				codeStream.load(syntheticArg);
				codeStream.fieldAccess(Opcodes.OPC_putfield, syntheticArg.matchingField, null /* default declaringClass */);
			}
		}
	}
	syntheticArgs = nestedType.syntheticOuterLocalVariables();
	if (syntheticArgs != null) {
		for (int i = 0, max = syntheticArgs.length; i < max; i++) {
			SyntheticArgumentBinding syntheticArg;
			if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
				codeStream.aload_0();
				codeStream.load(syntheticArg);
				codeStream.fieldAccess(Opcodes.OPC_putfield, syntheticArg.matchingField, null /* default declaringClass */);
			}
		}
	}
}

private void internalGenerateCode(ClassScope classScope, ClassFile classFile) {
	classFile.generateMethodInfoHeader(this.binding);
	int methodAttributeOffset = classFile.contentsOffset;
	int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
	if ((!this.binding.isNative()) && (!this.binding.isAbstract())) {

		TypeDeclaration declaringType = classScope.referenceContext;
		int codeAttributeOffset = classFile.contentsOffset;
		classFile.generateCodeAttributeHeader();
		CodeStream codeStream = classFile.codeStream;
		codeStream.reset(this, classFile);

		// initialize local positions - including initializer scope.
		ReferenceBinding declaringClass = this.binding.declaringClass;

		int enumOffset = declaringClass.isEnum() ? 2 : 0; // String name, int ordinal
		int argSlotSize = 1 + enumOffset; // this==aload0

		if (declaringClass.isNestedType()){
			this.scope.extraSyntheticArguments = declaringClass.syntheticOuterLocalVariables();
			this.scope.computeLocalVariablePositions(// consider synthetic arguments if any
					declaringClass.getEnclosingInstancesSlotSize() + 1 + enumOffset,
				codeStream);
			argSlotSize += declaringClass.getEnclosingInstancesSlotSize();
			argSlotSize += declaringClass.getOuterLocalVariablesSlotSize();
		} else {
			this.scope.computeLocalVariablePositions(1 + enumOffset,  codeStream);
		}

		if (this.arguments != null) {
			for (int i = 0, max = this.arguments.length; i < max; i++) {
				// arguments initialization for local variable debug attributes
				LocalVariableBinding argBinding;
				codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
				argBinding.recordInitializationStartPC(0);
				switch(argBinding.type.id) {
					case TypeIds.T_long :
					case TypeIds.T_double :
						argSlotSize += 2;
						break;
					default :
						argSlotSize++;
						break;
				}
			}
		}

		MethodScope initializerScope = declaringType.initializerScope;
		initializerScope.computeLocalVariablePositions(argSlotSize, codeStream); // offset by the argument size (since not linked to method scope)

		boolean needFieldInitializations = this.constructorCall == null || this.constructorCall.accessMode != ExplicitConstructorCall.This;

		// post 1.4 target level, synthetic initializations occur prior to explicit constructor call
		boolean preInitSyntheticFields = this.scope.compilerOptions().targetJDK >= ClassFileConstants.JDK1_4;

		if (needFieldInitializations && preInitSyntheticFields){
			generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
			codeStream.recordPositionsFrom(0, this.bodyStart);
		}
		// generate constructor call
		if (this.constructorCall != null) {
			this.constructorCall.generateCode(this.scope, codeStream);
		}
		// generate field initialization - only if not invoking another constructor call of the same class
		if (needFieldInitializations) {
			if (!preInitSyntheticFields){
				generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
			}
			// generate user field initialization
			if (declaringType.fields != null) {
				for (int i = 0, max = declaringType.fields.length; i < max; i++) {
					FieldDeclaration fieldDecl;
					if (!(fieldDecl = declaringType.fields[i]).isStatic()) {
						fieldDecl.generateCode(initializerScope, codeStream);
					}
				}
			}
		}
		// generate statements
		if (this.statements != null) {
			for (int i = 0, max = this.statements.length; i < max; i++) {
				this.statements[i].generateCode(this.scope, codeStream);
			}
		}
		// if a problem got reported during code gen, then trigger problem method creation
		if (this.ignoreFurtherInvestigation) {
			throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
		}
		if ((this.bits & ASTNode.NeedFreeReturn) != 0) {
			codeStream.return_();
		}
		// local variable attributes
		codeStream.exitUserScope(this.scope);
		codeStream.recordPositionsFrom(0, this.bodyEnd);
		try {
			classFile.completeCodeAttribute(codeAttributeOffset);
		} catch(NegativeArraySizeException e) {
			throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
		}
		attributeNumber++;
		if ((codeStream instanceof StackMapFrameCodeStream)
				&& needFieldInitializations
				&& declaringType.fields != null) {
			((StackMapFrameCodeStream) codeStream).resetSecretLocals();
		}
	}
	classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
}

public boolean isConstructor() {
	return true;
}

public boolean isDefaultConstructor() {
	return (this.bits & ASTNode.IsDefaultConstructor) != 0;
}

public boolean isInitializationMethod() {
	return true;
}

/*
 * Returns true if the constructor is directly involved in a cycle.
 * Given most constructors aren't, we only allocate the visited list
 * lazily.
 */
public boolean isRecursive(ArrayList visited) {
	if (this.binding == null
			|| this.constructorCall == null
			|| this.constructorCall.binding == null
			|| this.constructorCall.isSuperAccess()
			|| !this.constructorCall.binding.isValidBinding()) {
		return false;
	}

	ConstructorDeclaration targetConstructor =
		((ConstructorDeclaration)this.scope.referenceType().declarationOf(this.constructorCall.binding.original()));
	if (this == targetConstructor) return true; // direct case

	if (visited == null) { // lazy allocation
		visited = new ArrayList(1);
	} else {
		int index = visited.indexOf(this);
		if (index >= 0) return index == 0; // only blame if directly part of the cycle
	}
	visited.add(this);

	return targetConstructor.isRecursive(visited);
}

public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
	//fill up the constructor body with its statements
	if (((this.bits & ASTNode.IsDefaultConstructor) != 0) && this.constructorCall == null){
		this.constructorCall = SuperReference.implicitSuperConstructorCall();
		this.constructorCall.sourceStart = this.sourceStart;
		this.constructorCall.sourceEnd = this.sourceEnd;
		return;
	}
	parser.parse(this, unit, false);

}

public StringBuffer printBody(int indent, StringBuffer output) {
	output.append(" {"); //$NON-NLS-1$
	if (this.constructorCall != null) {
		output.append('\n');
		this.constructorCall.printStatement(indent, output);
	}
	if (this.statements != null) {
		for (int i = 0; i < this.statements.length; i++) {
			output.append('\n');
			this.statements[i].printStatement(indent, output);
		}
	}
	output.append('\n');
	printIndent(indent == 0 ? 0 : indent - 1, output).append('}');
	return output;
}

public void resolveJavadoc() {
	if (this.binding == null || this.javadoc != null) {
		super.resolveJavadoc();
	} else if ((this.bits & ASTNode.IsDefaultConstructor) == 0) {
		if (this.binding.declaringClass != null && !this.binding.declaringClass.isLocalType()) {
			// Set javadoc visibility
			int javadocVisibility = this.binding.modifiers & ExtraCompilerModifiers.AccVisibilityMASK;
			ClassScope classScope = this.scope.classScope();
			ProblemReporter reporter = this.scope.problemReporter();
			int severity = reporter.computeSeverity(IProblem.JavadocMissing);
			if (severity != ProblemSeverities.Ignore) {
				if (classScope != null) {
					javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
				}
				int javadocModifiers = (this.binding.modifiers & ~ExtraCompilerModifiers.AccVisibilityMASK) | javadocVisibility;
				reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
			}
		}
	}
}

/*
 * Type checking for constructor, just another method, except for special check
 * for recursive constructor invocations.
 */
public void resolveStatements() {
	SourceTypeBinding sourceType = this.scope.enclosingSourceType();
	if (!CharOperation.equals(sourceType.sourceName, this.selector)){
		this.scope.problemReporter().missingReturnType(this);
	}
	if (this.typeParameters != null) {
		for (int i = 0, length = this.typeParameters.length; i < length; i++) {
			this.typeParameters[i].resolve(this.scope);
		}
	}
	if (this.binding != null && !this.binding.isPrivate()) {
		sourceType.tagBits |= TagBits.HasNonPrivateConstructor;
	}
	// if null ==> an error has occurs at parsing time ....
	if (this.constructorCall != null) {
		if (sourceType.id == TypeIds.T_JavaLangObject
				&& this.constructorCall.accessMode != ExplicitConstructorCall.This) {
			// cannot use super() in java.lang.Object
			if (this.constructorCall.accessMode == ExplicitConstructorCall.Super) {
				this.scope.problemReporter().cannotUseSuperInJavaLangObject(this.constructorCall);
			}
			this.constructorCall = null;
		} else {
			this.constructorCall.resolve(this.scope);
		}
	}
	if ((this.modifiers & ExtraCompilerModifiers.AccSemicolonBody) != 0) {
		this.scope.problemReporter().methodNeedBody(this);
	}
	super.resolveStatements();
}

public void traverse(ASTVisitor visitor, ClassScope classScope) {
	if (visitor.visit(this, classScope)) {
		if (this.javadoc != null) {
			this.javadoc.traverse(visitor, this.scope);
		}
		if (this.annotations != null) {
			int annotationsLength = this.annotations.length;
			for (int i = 0; i < annotationsLength; i++)
				this.annotations[i].traverse(visitor, this.scope);
		}
		if (this.typeParameters != null) {
			int typeParametersLength = this.typeParameters.length;
			for (int i = 0; i < typeParametersLength; i++) {
				this.typeParameters[i].traverse(visitor, this.scope);
			}
		}
		if (this.arguments != null) {
			int argumentLength = this.arguments.length;
			for (int i = 0; i < argumentLength; i++)
				this.arguments[i].traverse(visitor, this.scope);
		}
		if (this.thrownExceptions != null) {
			int thrownExceptionsLength = this.thrownExceptions.length;
			for (int i = 0; i < thrownExceptionsLength; i++)
				this.thrownExceptions[i].traverse(visitor, this.scope);
		}
		if (this.constructorCall != null)
			this.constructorCall.traverse(visitor, this.scope);
		if (this.statements != null) {
			int statementsLength = this.statements.length;
			for (int i = 0; i < statementsLength; i++)
				this.statements[i].traverse(visitor, this.scope);
		}
	}
	visitor.endVisit(this, classScope);
}
public TypeParameter[] typeParameters() {
    return this.typeParameters;
}
}
