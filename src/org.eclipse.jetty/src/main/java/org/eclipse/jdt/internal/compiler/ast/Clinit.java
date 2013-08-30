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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;

public class Clinit extends AbstractMethodDeclaration {
	private static int ENUM_CONSTANTS_THRESHOLD = 2000;

	private FieldBinding assertionSyntheticFieldBinding = null;
	private FieldBinding classLiteralSyntheticField = null;

	public Clinit(CompilationResult compilationResult) {
		super(compilationResult);
		this.modifiers = 0;
		this.selector = TypeConstants.CLINIT;
	}

	public void analyseCode(
		ClassScope classScope,
		InitializationFlowContext staticInitializerFlowContext,
		FlowInfo flowInfo) {

		if (this.ignoreFurtherInvestigation)
			return;
		try {
			ExceptionHandlingFlowContext clinitContext =
				new ExceptionHandlingFlowContext(
					staticInitializerFlowContext.parent,
					this,
					Binding.NO_EXCEPTIONS,
					staticInitializerFlowContext,
					this.scope,
					FlowInfo.DEAD_END);

			// check for missing returning path
			if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
				this.bits |= ASTNode.NeedFreeReturn;
			}

			// check missing blank final field initializations
			flowInfo = flowInfo.mergedWith(staticInitializerFlowContext.initsOnReturn);
			FieldBinding[] fields = this.scope.enclosingSourceType().fields();
			for (int i = 0, count = fields.length; i < count; i++) {
				FieldBinding field;
				if ((field = fields[i]).isStatic()
					&& field.isFinal()
					&& (!flowInfo.isDefinitelyAssigned(fields[i]))) {
					this.scope.problemReporter().uninitializedBlankFinalField(
						field,
						this.scope.referenceType().declarationOf(field.original()));
					// can complain against the field decl, since only one <clinit>
				}
			}
			// check static initializers thrown exceptions
			staticInitializerFlowContext.checkInitializerExceptions(
				this.scope,
				clinitContext,
				flowInfo);
		} catch (AbortMethod e) {
			this.ignoreFurtherInvestigation = true;
		}
	}

	/**
	 * Bytecode generation for a <clinit> method
	 *
	 * @param classScope org.eclipse.jdt.internal.compiler.lookup.ClassScope
	 * @param classFile org.eclipse.jdt.internal.compiler.codegen.ClassFile
	 */
	public void generateCode(ClassScope classScope, ClassFile classFile) {

		int clinitOffset = 0;
		if (this.ignoreFurtherInvestigation) {
			// should never have to add any <clinit> problem method
			return;
		}
		boolean restart = false;
		do {
			try {
				clinitOffset = classFile.contentsOffset;
				this.generateCode(classScope, classFile, clinitOffset);
				restart = false;
			} catch (AbortMethod e) {
				// should never occur
				// the clinit referenceContext is the type declaration
				// All clinit problems will be reported against the type: AbortType instead of AbortMethod
				// reset the contentsOffset to the value before generating the clinit code
				// decrement the number of method info as well.
				// This is done in the addProblemMethod and addProblemConstructor for other
				// cases.
				if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE) {
					// a branch target required a goto_w, restart code gen in wide mode.
					if (!restart) {
						classFile.contentsOffset = clinitOffset;
						classFile.methodCount--;
						classFile.codeStream.resetInWideMode(); // request wide mode
						// restart method generation
						restart = true;
					} else {
						classFile.contentsOffset = clinitOffset;
						classFile.methodCount--;
					}
				} else if (e.compilationResult == CodeStream.RESTART_CODE_GEN_FOR_UNUSED_LOCALS_MODE) {
					classFile.contentsOffset = clinitOffset;
					classFile.methodCount--;
					classFile.codeStream.resetForCodeGenUnusedLocals();
					// restart method generation
					restart = true;
				} else {
					// produce a problem method accounting for this fatal error
					classFile.contentsOffset = clinitOffset;
					classFile.methodCount--;
					restart = false;
				}
			}
		} while (restart);
	}

	/**
	 * Bytecode generation for a <clinit> method
	 *
	 * @param classScope org.eclipse.jdt.internal.compiler.lookup.ClassScope
	 * @param classFile org.eclipse.jdt.internal.compiler.codegen.ClassFile
	 */
	private void generateCode(
		ClassScope classScope,
		ClassFile classFile,
		int clinitOffset) {

		ConstantPool constantPool = classFile.constantPool;
		int constantPoolOffset = constantPool.currentOffset;
		int constantPoolIndex = constantPool.currentIndex;
		classFile.generateMethodInfoHeaderForClinit();
		int codeAttributeOffset = classFile.contentsOffset;
		classFile.generateCodeAttributeHeader();
		CodeStream codeStream = classFile.codeStream;
		resolve(classScope);

		codeStream.reset(this, classFile);
		TypeDeclaration declaringType = classScope.referenceContext;

		// initialize local positions - including initializer scope.
		MethodScope staticInitializerScope = declaringType.staticInitializerScope;
		staticInitializerScope.computeLocalVariablePositions(0, codeStream);

		// 1.4 feature
		// This has to be done before any other initialization
		if (this.assertionSyntheticFieldBinding != null) {
			// generate code related to the activation of assertion for this class
			codeStream.generateClassLiteralAccessForType(
					classScope.outerMostClassScope().enclosingSourceType(),
					this.classLiteralSyntheticField);
			codeStream.invokeJavaLangClassDesiredAssertionStatus();
			BranchLabel falseLabel = new BranchLabel(codeStream);
			codeStream.ifne(falseLabel);
			codeStream.iconst_1();
			BranchLabel jumpLabel = new BranchLabel(codeStream);
			codeStream.decrStackSize(1);
			codeStream.goto_(jumpLabel);
			falseLabel.place();
			codeStream.iconst_0();
			jumpLabel.place();
			codeStream.fieldAccess(Opcodes.OPC_putstatic, this.assertionSyntheticFieldBinding, null /* default declaringClass */);
		}
		// generate static fields/initializers/enum constants
		final FieldDeclaration[] fieldDeclarations = declaringType.fields;
		BlockScope lastInitializerScope = null;
		int remainingFieldCount = 0;
		if (TypeDeclaration.kind(declaringType.modifiers) == TypeDeclaration.ENUM_DECL) {
			int enumCount = declaringType.enumConstantsCounter;
			if (enumCount > ENUM_CONSTANTS_THRESHOLD) {
				// generate synthetic methods to initialize all the enum constants
				int begin = -1;
				int count = 0;
				if (fieldDeclarations != null) {
					int max = fieldDeclarations.length;
					for (int i = 0; i < max; i++) {
						FieldDeclaration fieldDecl = fieldDeclarations[i];
						if (fieldDecl.isStatic()) {
							if (fieldDecl.getKind() == AbstractVariableDeclaration.ENUM_CONSTANT) {
								if (begin == -1) {
									begin = i;
								}
								count++;
								if (count > ENUM_CONSTANTS_THRESHOLD) {
									SyntheticMethodBinding syntheticMethod = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, i);
									codeStream.invoke(Opcodes.OPC_invokestatic, syntheticMethod, null /* default declaringClass */);
									begin = i;
									count = 1;
								}
							}
						}
					}
					if (count != 0) {
						// add last synthetic method
						SyntheticMethodBinding syntheticMethod = declaringType.binding.addSyntheticMethodForEnumInitialization(begin, max);
						codeStream.invoke(Opcodes.OPC_invokestatic, syntheticMethod, null /* default declaringClass */);
					}
				}
			} else if (fieldDeclarations != null) {
				for (int i = 0, max = fieldDeclarations.length; i < max; i++) {
					FieldDeclaration fieldDecl = fieldDeclarations[i];
					if (fieldDecl.isStatic()) {
						if (fieldDecl.getKind() == AbstractVariableDeclaration.ENUM_CONSTANT) {
							fieldDecl.generateCode(staticInitializerScope, codeStream);
						} else {
							remainingFieldCount++;
						}
					}
				}
			}
			// enum need to initialize $VALUES synthetic cache of enum constants
			// $VALUES := new <EnumType>[<enumCount>]
			codeStream.generateInlinedValue(enumCount);
			codeStream.anewarray(declaringType.binding);
			if (enumCount > 0) {
				if (fieldDeclarations != null) {
					for (int i = 0, max = fieldDeclarations.length; i < max; i++) {
						FieldDeclaration fieldDecl = fieldDeclarations[i];
						// $VALUES[i] = <enum-constant-i>
						if (fieldDecl.getKind() == AbstractVariableDeclaration.ENUM_CONSTANT) {
							codeStream.dup();
							codeStream.generateInlinedValue(fieldDecl.binding.id);
							codeStream.fieldAccess(Opcodes.OPC_getstatic, fieldDecl.binding, null /* default declaringClass */);
							codeStream.aastore();
						}
					}
				}
			}
			codeStream.fieldAccess(Opcodes.OPC_putstatic, declaringType.enumValuesSyntheticfield, null /* default declaringClass */);
			if (remainingFieldCount != 0) {
				// if fields that are not enum constants need to be generated (static initializer/static field)
				for (int i = 0, max = fieldDeclarations.length; i < max && remainingFieldCount >= 0; i++) {
					FieldDeclaration fieldDecl = fieldDeclarations[i];
					switch (fieldDecl.getKind()) {
						case AbstractVariableDeclaration.ENUM_CONSTANT :
							break;
						case AbstractVariableDeclaration.INITIALIZER :
							if (!fieldDecl.isStatic()) {
								break;
							}
							remainingFieldCount--;
							lastInitializerScope = ((Initializer) fieldDecl).block.scope;
							fieldDecl.generateCode(staticInitializerScope, codeStream);
							break;
						case AbstractVariableDeclaration.FIELD :
							if (!fieldDecl.binding.isStatic()) {
								break;
							}
							remainingFieldCount--;
							lastInitializerScope = null;
							fieldDecl.generateCode(staticInitializerScope, codeStream);
							break;
					}
				}
			}
		} else {
			if (fieldDeclarations != null) {
				for (int i = 0, max = fieldDeclarations.length; i < max; i++) {
					FieldDeclaration fieldDecl = fieldDeclarations[i];
					switch (fieldDecl.getKind()) {
						case AbstractVariableDeclaration.INITIALIZER :
							if (!fieldDecl.isStatic())
								break;
							lastInitializerScope = ((Initializer) fieldDecl).block.scope;
							fieldDecl.generateCode(staticInitializerScope, codeStream);
							break;
						case AbstractVariableDeclaration.FIELD :
							if (!fieldDecl.binding.isStatic())
								break;
							lastInitializerScope = null;
							fieldDecl.generateCode(staticInitializerScope, codeStream);
							break;
					}
				}
			}
		}

		if (codeStream.position == 0) {
			// do not need to output a Clinit if no bytecodes
			// so we reset the offset inside the byte array contents.
			classFile.contentsOffset = clinitOffset;
			// like we don't addd a method we need to undo the increment on the method count
			classFile.methodCount--;
			// reset the constant pool to its state before the clinit
			constantPool.resetForClinit(constantPoolIndex, constantPoolOffset);
		} else {
			if ((this.bits & ASTNode.NeedFreeReturn) != 0) {
				int before = codeStream.position;
				codeStream.return_();
				if (lastInitializerScope != null) {
					// expand the last initializer variables to include the trailing return
					codeStream.updateLastRecordedEndPC(lastInitializerScope, before);
				}
			}
			// Record the end of the clinit: point to the declaration of the class
			codeStream.recordPositionsFrom(0, declaringType.sourceStart);
			classFile.completeCodeAttributeForClinit(codeAttributeOffset);
		}
	}

	public boolean isClinit() {

		return true;
	}

	public boolean isInitializationMethod() {

		return true;
	}

	public boolean isStatic() {

		return true;
	}

	public void parseStatements(Parser parser, CompilationUnitDeclaration unit) {
		//the clinit is filled by hand ....
	}

	public StringBuffer print(int tab, StringBuffer output) {

		printIndent(tab, output).append("<clinit>()"); //$NON-NLS-1$
		printBody(tab + 1, output);
		return output;
	}

	public void resolve(ClassScope classScope) {

		this.scope = new MethodScope(classScope, classScope.referenceContext, true);
	}

	public void traverse(
		ASTVisitor visitor,
		ClassScope classScope) {

		visitor.visit(this, classScope);
		visitor.endVisit(this, classScope);
	}

	public void setAssertionSupport(FieldBinding assertionSyntheticFieldBinding, boolean needClassLiteralField) {

		this.assertionSyntheticFieldBinding = assertionSyntheticFieldBinding;

		// we need to add the field right now, because the field infos are generated before the methods
		if (needClassLiteralField) {
			SourceTypeBinding sourceType =
				this.scope.outerMostClassScope().enclosingSourceType();
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=22334
			if (!sourceType.isInterface() && !sourceType.isBaseType()) {
				this.classLiteralSyntheticField = sourceType.addSyntheticFieldForClassLiteral(sourceType, this.scope);
			}
		}
	}

}
