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

import org.eclipse.jdt.core.compiler.*;
import org.eclipse.jdt.internal.compiler.*;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.parser.*;
import org.eclipse.jdt.internal.compiler.util.Util;

public abstract class AbstractMethodDeclaration
	extends ASTNode
	implements ProblemSeverities, ReferenceContext {

	public MethodScope scope;
	//it is not relevent for constructor but it helps to have the name of the constructor here
	//which is always the name of the class.....parsing do extra work to fill it up while it do not have to....
	public char[] selector;
	public int declarationSourceStart;
	public int declarationSourceEnd;
	public int modifiers;
	public int modifiersSourceStart;
	public Annotation[] annotations;
	public Argument[] arguments;
	public TypeReference[] thrownExceptions;
	public Statement[] statements;
	public int explicitDeclarations;
	public MethodBinding binding;
	public boolean ignoreFurtherInvestigation = false;

	public Javadoc javadoc;

	public int bodyStart;
	public int bodyEnd = -1;
	public CompilationResult compilationResult;

	AbstractMethodDeclaration(CompilationResult compilationResult){
		this.compilationResult = compilationResult;
	}

	/*
	 *	We cause the compilation task to abort to a given extent.
	 */
	public void abort(int abortLevel, CategorizedProblem problem) {

		switch (abortLevel) {
			case AbortCompilation :
				throw new AbortCompilation(this.compilationResult, problem);
			case AbortCompilationUnit :
				throw new AbortCompilationUnit(this.compilationResult, problem);
			case AbortType :
				throw new AbortType(this.compilationResult, problem);
			default :
				throw new AbortMethod(this.compilationResult, problem);
		}
	}

	public abstract void analyseCode(ClassScope classScope, InitializationFlowContext initializationContext, FlowInfo info);

	/**
	 * Bind and add argument's binding into the scope of the method
	 */
	public void bindArguments() {

		if (this.arguments != null) {
			// by default arguments in abstract/native methods are considered to be used (no complaint is expected)
			if (this.binding == null) {
				for (int i = 0, length = this.arguments.length; i < length; i++) {
					this.arguments[i].bind(this.scope, null, true);
				}
				return;
			}
			boolean used = this.binding.isAbstract() || this.binding.isNative();
			AnnotationBinding[][] paramAnnotations = null;
			for (int i = 0, length = this.arguments.length; i < length; i++) {
				Argument argument = this.arguments[i];
				argument.bind(this.scope, this.binding.parameters[i], used);
				if (argument.annotations != null) {
					if (paramAnnotations == null) {
						paramAnnotations = new AnnotationBinding[length][];
						for (int j=0; j<i; j++) {
							paramAnnotations[j] = Binding.NO_ANNOTATIONS;
						}
					}
					paramAnnotations[i] = argument.binding.getAnnotations();
				} else if (paramAnnotations != null) {
					paramAnnotations[i] = Binding.NO_ANNOTATIONS;
				}
			}
			if (paramAnnotations != null)
				this.binding.setParameterAnnotations(paramAnnotations);
		}
	}

	/**
	 * Record the thrown exception type bindings in the corresponding type references.
	 */
	public void bindThrownExceptions() {

		if (this.thrownExceptions != null
			&& this.binding != null
			&& this.binding.thrownExceptions != null) {
			int thrownExceptionLength = this.thrownExceptions.length;
			int length = this.binding.thrownExceptions.length;
			if (length == thrownExceptionLength) {
				for (int i = 0; i < length; i++) {
					this.thrownExceptions[i].resolvedType = this.binding.thrownExceptions[i];
				}
			} else {
				int bindingIndex = 0;
				for (int i = 0; i < thrownExceptionLength && bindingIndex < length; i++) {
					TypeReference thrownException = this.thrownExceptions[i];
					ReferenceBinding thrownExceptionBinding = this.binding.thrownExceptions[bindingIndex];
					char[][] bindingCompoundName = thrownExceptionBinding.compoundName;
					if (bindingCompoundName == null) continue; // skip problem case
					if (thrownException instanceof SingleTypeReference) {
						// single type reference
						int lengthName = bindingCompoundName.length;
						char[] thrownExceptionTypeName = thrownException.getTypeName()[0];
						if (CharOperation.equals(thrownExceptionTypeName, bindingCompoundName[lengthName - 1])) {
							thrownException.resolvedType = thrownExceptionBinding;
							bindingIndex++;
						}
					} else {
						// qualified type reference
						if (CharOperation.equals(thrownException.getTypeName(), bindingCompoundName)) {
							thrownException.resolvedType = thrownExceptionBinding;
							bindingIndex++;
						}
					}
				}
			}
		}
	}

	public CompilationResult compilationResult() {

		return this.compilationResult;
	}

	/**
	 * Bytecode generation for a method
	 * @param classScope
	 * @param classFile
	 */
	public void generateCode(ClassScope classScope, ClassFile classFile) {

		int problemResetPC = 0;
		classFile.codeStream.wideMode = false; // reset wideMode to false
		if (this.ignoreFurtherInvestigation) {
			// method is known to have errors, dump a problem method
			if (this.binding == null)
				return; // handle methods with invalid signature or duplicates
			int problemsLength;
			CategorizedProblem[] problems =
				this.scope.referenceCompilationUnit().compilationResult.getProblems();
			CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
			System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
			classFile.addProblemMethod(this, this.binding, problemsCopy);
			return;
		}
		boolean restart = false;
		boolean abort = false;
		// regular code generation
		do {
			try {
				problemResetPC = classFile.contentsOffset;
				this.generateCode(classFile);
				restart = false;
			} catch (AbortMethod e) {
				// a fatal error was detected during code generation, need to restart code gen if possible
				if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
					// a branch target required a goto_w, restart code gen in wide mode.
					if (!restart) {
						classFile.contentsOffset = problemResetPC;
						classFile.methodCount--;
						classFile.codeStream.resetInWideMode(); // request wide mode
						restart = true;
					} else {
						// after restarting in wide mode, code generation failed again
						// report a problem
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
		// produce a problem method accounting for this fatal error
		if (abort) {
			int problemsLength;
			CategorizedProblem[] problems =
				this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
			CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
			System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
			classFile.addProblemMethod(this, this.binding, problemsCopy, problemResetPC);
		}
	}

	public void generateCode(ClassFile classFile) {

		classFile.generateMethodInfoHeader(this.binding);
		int methodAttributeOffset = classFile.contentsOffset;
		int attributeNumber = classFile.generateMethodInfoAttributes(this.binding);
		if ((!this.binding.isNative()) && (!this.binding.isAbstract())) {
			int codeAttributeOffset = classFile.contentsOffset;
			classFile.generateCodeAttributeHeader();
			CodeStream codeStream = classFile.codeStream;
			codeStream.reset(this, classFile);
			// initialize local positions
			this.scope.computeLocalVariablePositions(this.binding.isStatic() ? 0 : 1, codeStream);

			// arguments initialization for local variable debug attributes
			if (this.arguments != null) {
				for (int i = 0, max = this.arguments.length; i < max; i++) {
					LocalVariableBinding argBinding;
					codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
					argBinding.recordInitializationStartPC(0);
				}
			}
			if (this.statements != null) {
				for (int i = 0, max = this.statements.length; i < max; i++)
					this.statements[i].generateCode(this.scope, codeStream);
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
			codeStream.recordPositionsFrom(0, this.declarationSourceEnd);
			try {
				classFile.completeCodeAttribute(codeAttributeOffset);
			} catch(NegativeArraySizeException e) {
				throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
			}
			attributeNumber++;
		} else {
			checkArgumentsSize();
		}
		classFile.completeMethodInfo(this.binding, methodAttributeOffset, attributeNumber);
	}

	private void checkArgumentsSize() {
		TypeBinding[] parameters = this.binding.parameters;
		int size = 1; // an abstract method or a native method cannot be static
		for (int i = 0, max = parameters.length; i < max; i++) {
			switch(parameters[i].id) {
				case TypeIds.T_long :
				case TypeIds.T_double :
					size += 2;
					break;
				default :
					size++;
					break;
			}
			if (size > 0xFF) {
				this.scope.problemReporter().noMoreAvailableSpaceForArgument(this.scope.locals[i], this.scope.locals[i].declaration);
			}
		}
	}

	public boolean hasErrors() {
		return this.ignoreFurtherInvestigation;
	}

	public boolean isAbstract() {

		if (this.binding != null)
			return this.binding.isAbstract();
		return (this.modifiers & ClassFileConstants.AccAbstract) != 0;
	}

	public boolean isAnnotationMethod() {

		return false;
	}

	public boolean isClinit() {

		return false;
	}

	public boolean isConstructor() {

		return false;
	}

	public boolean isDefaultConstructor() {

		return false;
	}

	public boolean isInitializationMethod() {

		return false;
	}

	public boolean isMethod() {

		return false;
	}

	public boolean isNative() {

		if (this.binding != null)
			return this.binding.isNative();
		return (this.modifiers & ClassFileConstants.AccNative) != 0;
	}

	public boolean isStatic() {

		if (this.binding != null)
			return this.binding.isStatic();
		return (this.modifiers & ClassFileConstants.AccStatic) != 0;
	}

	/**
	 * Fill up the method body with statement
	 * @param parser
	 * @param unit
	 */
	public abstract void parseStatements(Parser parser, CompilationUnitDeclaration unit);

	public StringBuffer print(int tab, StringBuffer output) {

		if (this.javadoc != null) {
			this.javadoc.print(tab, output);
		}
		printIndent(tab, output);
		printModifiers(this.modifiers, output);
		if (this.annotations != null) printAnnotations(this.annotations, output);

		TypeParameter[] typeParams = typeParameters();
		if (typeParams != null) {
			output.append('<');
			int max = typeParams.length - 1;
			for (int j = 0; j < max; j++) {
				typeParams[j].print(0, output);
				output.append(", ");//$NON-NLS-1$
			}
			typeParams[max].print(0, output);
			output.append('>');
		}

		printReturnType(0, output).append(this.selector).append('(');
		if (this.arguments != null) {
			for (int i = 0; i < this.arguments.length; i++) {
				if (i > 0) output.append(", "); //$NON-NLS-1$
				this.arguments[i].print(0, output);
			}
		}
		output.append(')');
		if (this.thrownExceptions != null) {
			output.append(" throws "); //$NON-NLS-1$
			for (int i = 0; i < this.thrownExceptions.length; i++) {
				if (i > 0) output.append(", "); //$NON-NLS-1$
				this.thrownExceptions[i].print(0, output);
			}
		}
		printBody(tab + 1, output);
		return output;
	}

	public StringBuffer printBody(int indent, StringBuffer output) {

		if (isAbstract() || (this.modifiers & ExtraCompilerModifiers.AccSemicolonBody) != 0)
			return output.append(';');

		output.append(" {"); //$NON-NLS-1$
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

	public StringBuffer printReturnType(int indent, StringBuffer output) {

		return output;
	}

	public void resolve(ClassScope upperScope) {

		if (this.binding == null) {
			this.ignoreFurtherInvestigation = true;
		}

		try {
			bindArguments();
			bindThrownExceptions();
			resolveJavadoc();
			resolveAnnotations(this.scope, this.annotations, this.binding);
			resolveStatements();
			// check @Deprecated annotation presence
			if (this.binding != null
					&& (this.binding.getAnnotationTagBits() & TagBits.AnnotationDeprecated) == 0
					&& (this.binding.modifiers & ClassFileConstants.AccDeprecated) != 0
					&& this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
				this.scope.problemReporter().missingDeprecatedAnnotationForMethod(this);
			}
		} catch (AbortMethod e) {
			// ========= abort on fatal error =============
			this.ignoreFurtherInvestigation = true;
		}
	}

	public void resolveJavadoc() {

		if (this.binding == null) return;
		if (this.javadoc != null) {
			this.javadoc.resolve(this.scope);
			return;
		}
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

	public void resolveStatements() {

		if (this.statements != null) {
			for (int i = 0, length = this.statements.length; i < length; i++) {
				this.statements[i].resolve(this.scope);
			}
		} else if ((this.bits & UndocumentedEmptyBlock) != 0) {
			if (!this.isConstructor() || this.arguments != null) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=319626
				this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart-1, this.bodyEnd+1);
			}
		}
	}

	public void tagAsHavingErrors() {
		this.ignoreFurtherInvestigation = true;
	}

	public void traverse(
		ASTVisitor visitor,
		ClassScope classScope) {
		// default implementation: subclass will define it
	}

	public TypeParameter[] typeParameters() {
	    return null;
	}
}
