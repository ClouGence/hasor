/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla - Contribution for bug 239066
 *     Stephan Herrmann  - Contribution for bug 236385
 *     Stephan Herrmann  - Contribution for bug 295551
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Util;

public class CompilerOptions {

	/**
	 * Option IDs
	 */
	public static final String OPTION_LocalVariableAttribute = "org.eclipse.jdt.core.compiler.debug.localVariable"; //$NON-NLS-1$
	public static final String OPTION_LineNumberAttribute = "org.eclipse.jdt.core.compiler.debug.lineNumber"; //$NON-NLS-1$
	public static final String OPTION_SourceFileAttribute = "org.eclipse.jdt.core.compiler.debug.sourceFile"; //$NON-NLS-1$
	public static final String OPTION_PreserveUnusedLocal = "org.eclipse.jdt.core.compiler.codegen.unusedLocal"; //$NON-NLS-1$
	public static final String OPTION_DocCommentSupport= "org.eclipse.jdt.core.compiler.doc.comment.support"; //$NON-NLS-1$
	public static final String OPTION_ReportMethodWithConstructorName = "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName"; //$NON-NLS-1$
	public static final String OPTION_ReportOverridingPackageDefaultMethod = "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportDeprecation = "org.eclipse.jdt.core.compiler.problem.deprecation"; //$NON-NLS-1$
	public static final String OPTION_ReportDeprecationInDeprecatedCode = "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode"; //$NON-NLS-1$
	public static final String OPTION_ReportDeprecationWhenOverridingDeprecatedMethod = "org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportHiddenCatchBlock = "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedLocal = "org.eclipse.jdt.core.compiler.problem.unusedLocal"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameter = "org.eclipse.jdt.core.compiler.problem.unusedParameter"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameterWhenImplementingAbstract = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameterWhenOverridingConcrete = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameterIncludeDocCommentReference = "org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedImport = "org.eclipse.jdt.core.compiler.problem.unusedImport"; //$NON-NLS-1$
	public static final String OPTION_ReportSyntheticAccessEmulation = "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation"; //$NON-NLS-1$
	public static final String OPTION_ReportNoEffectAssignment = "org.eclipse.jdt.core.compiler.problem.noEffectAssignment"; //$NON-NLS-1$
	public static final String OPTION_ReportLocalVariableHiding = "org.eclipse.jdt.core.compiler.problem.localVariableHiding"; //$NON-NLS-1$
	public static final String OPTION_ReportSpecialParameterHidingField = "org.eclipse.jdt.core.compiler.problem.specialParameterHidingField"; //$NON-NLS-1$
	public static final String OPTION_ReportFieldHiding = "org.eclipse.jdt.core.compiler.problem.fieldHiding"; //$NON-NLS-1$
	public static final String OPTION_ReportTypeParameterHiding = "org.eclipse.jdt.core.compiler.problem.typeParameterHiding"; //$NON-NLS-1$
	public static final String OPTION_ReportPossibleAccidentalBooleanAssignment = "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment"; //$NON-NLS-1$
	public static final String OPTION_ReportNonExternalizedStringLiteral = "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral"; //$NON-NLS-1$
	public static final String OPTION_ReportIncompatibleNonInheritedInterfaceMethod = "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedPrivateMember = "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember"; //$NON-NLS-1$
	public static final String OPTION_ReportNoImplicitStringConversion = "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion"; //$NON-NLS-1$
	public static final String OPTION_ReportAssertIdentifier = "org.eclipse.jdt.core.compiler.problem.assertIdentifier"; //$NON-NLS-1$
	public static final String OPTION_ReportEnumIdentifier = "org.eclipse.jdt.core.compiler.problem.enumIdentifier"; //$NON-NLS-1$
	public static final String OPTION_ReportNonStaticAccessToStatic = "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver"; //$NON-NLS-1$
	public static final String OPTION_ReportIndirectStaticAccess = "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess"; //$NON-NLS-1$
	public static final String OPTION_ReportEmptyStatement = "org.eclipse.jdt.core.compiler.problem.emptyStatement"; //$NON-NLS-1$
	public static final String OPTION_ReportUnnecessaryTypeCheck = "org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck"; //$NON-NLS-1$
	public static final String OPTION_ReportUnnecessaryElse = "org.eclipse.jdt.core.compiler.problem.unnecessaryElse"; //$NON-NLS-1$
	public static final String OPTION_ReportUndocumentedEmptyBlock = "org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidJavadoc = "org.eclipse.jdt.core.compiler.problem.invalidJavadoc"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidJavadocTags = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTags"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidJavadocTagsDeprecatedRef = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidJavadocTagsNotVisibleRef = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidJavadocTagsVisibility = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocTags = "org.eclipse.jdt.core.compiler.problem.missingJavadocTags"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocTagsVisibility = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocTagsOverriding = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocTagsMethodTypeParameters = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsMethodTypeParameters"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocComments = "org.eclipse.jdt.core.compiler.problem.missingJavadocComments"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocTagDescription = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocCommentsVisibility = "org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingJavadocCommentsOverriding = "org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding"; //$NON-NLS-1$
	public static final String OPTION_ReportFinallyBlockNotCompletingNormally = "org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedDeclaredThrownException = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionExemptExceptionAndThrowable"; //$NON-NLS-1$
	public static final String OPTION_ReportUnqualifiedFieldAccess = "org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess"; //$NON-NLS-1$
	public static final String OPTION_ReportUnavoidableGenericTypeProblems = "org.eclipse.jdt.core.compiler.problem.unavoidableGenericTypeProblems"; //$NON-NLS-1$
	public static final String OPTION_ReportUncheckedTypeOperation = "org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation"; //$NON-NLS-1$
	public static final String OPTION_ReportRawTypeReference =  "org.eclipse.jdt.core.compiler.problem.rawTypeReference"; //$NON-NLS-1$
	public static final String OPTION_ReportFinalParameterBound = "org.eclipse.jdt.core.compiler.problem.finalParameterBound"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingSerialVersion = "org.eclipse.jdt.core.compiler.problem.missingSerialVersion"; //$NON-NLS-1$
	public static final String OPTION_ReportVarargsArgumentNeedCast = "org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedTypeArgumentsForMethodInvocation = "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation"; //$NON-NLS-1$
	public static final String OPTION_Source = "org.eclipse.jdt.core.compiler.source"; //$NON-NLS-1$
	public static final String OPTION_TargetPlatform = "org.eclipse.jdt.core.compiler.codegen.targetPlatform"; //$NON-NLS-1$
	public static final String OPTION_Compliance = "org.eclipse.jdt.core.compiler.compliance"; //$NON-NLS-1$
	public static final String OPTION_Encoding = "org.eclipse.jdt.core.encoding"; //$NON-NLS-1$
	public static final String OPTION_MaxProblemPerUnit = "org.eclipse.jdt.core.compiler.maxProblemPerUnit"; //$NON-NLS-1$
	public static final String OPTION_TaskTags = "org.eclipse.jdt.core.compiler.taskTags"; //$NON-NLS-1$
	public static final String OPTION_TaskPriorities = "org.eclipse.jdt.core.compiler.taskPriorities"; //$NON-NLS-1$
	public static final String OPTION_TaskCaseSensitive = "org.eclipse.jdt.core.compiler.taskCaseSensitive"; //$NON-NLS-1$
	public static final String OPTION_InlineJsr = "org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode"; //$NON-NLS-1$
	public static final String OPTION_ReportNullReference = "org.eclipse.jdt.core.compiler.problem.nullReference"; //$NON-NLS-1$
	public static final String OPTION_ReportPotentialNullReference = "org.eclipse.jdt.core.compiler.problem.potentialNullReference"; //$NON-NLS-1$
	public static final String OPTION_ReportRedundantNullCheck = "org.eclipse.jdt.core.compiler.problem.redundantNullCheck"; //$NON-NLS-1$
	public static final String OPTION_ReportAutoboxing = "org.eclipse.jdt.core.compiler.problem.autoboxing"; //$NON-NLS-1$
	public static final String OPTION_ReportAnnotationSuperInterface = "org.eclipse.jdt.core.compiler.problem.annotationSuperInterface"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingOverrideAnnotation = "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation = "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotationForInterfaceMethodImplementation"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingDeprecatedAnnotation = "org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation"; //$NON-NLS-1$
	public static final String OPTION_ReportIncompleteEnumSwitch = "org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch"; //$NON-NLS-1$
	public static final String OPTION_ReportForbiddenReference =  "org.eclipse.jdt.core.compiler.problem.forbiddenReference"; //$NON-NLS-1$
	public static final String OPTION_ReportDiscouragedReference =  "org.eclipse.jdt.core.compiler.problem.discouragedReference"; //$NON-NLS-1$
	public static final String OPTION_SuppressWarnings =  "org.eclipse.jdt.core.compiler.problem.suppressWarnings"; //$NON-NLS-1$
	public static final String OPTION_SuppressOptionalErrors = "org.eclipse.jdt.core.compiler.problem.suppressOptionalErrors"; //$NON-NLS-1$
	public static final String OPTION_ReportUnhandledWarningToken =  "org.eclipse.jdt.core.compiler.problem.unhandledWarningToken"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedWarningToken =  "org.eclipse.jdt.core.compiler.problem.unusedWarningToken"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedLabel =  "org.eclipse.jdt.core.compiler.problem.unusedLabel"; //$NON-NLS-1$
	public static final String OPTION_FatalOptionalError =  "org.eclipse.jdt.core.compiler.problem.fatalOptionalError"; //$NON-NLS-1$
	public static final String OPTION_ReportParameterAssignment =  "org.eclipse.jdt.core.compiler.problem.parameterAssignment"; //$NON-NLS-1$
	public static final String OPTION_ReportFallthroughCase =  "org.eclipse.jdt.core.compiler.problem.fallthroughCase"; //$NON-NLS-1$
	public static final String OPTION_ReportOverridingMethodWithoutSuperInvocation =  "org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation"; //$NON-NLS-1$
	public static final String OPTION_GenerateClassFiles = "org.eclipse.jdt.core.compiler.generateClassFiles"; //$NON-NLS-1$
	public static final String OPTION_Process_Annotations = "org.eclipse.jdt.core.compiler.processAnnotations"; //$NON-NLS-1$
	public static final String OPTION_ReportRedundantSuperinterface =  "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface"; //$NON-NLS-1$
	public static final String OPTION_ReportComparingIdentical =  "org.eclipse.jdt.core.compiler.problem.comparingIdentical"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingSynchronizedOnInheritedMethod =  "org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportMissingHashCodeMethod =  "org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportDeadCode =  "org.eclipse.jdt.core.compiler.problem.deadCode"; //$NON-NLS-1$
	public static final String OPTION_ReportDeadCodeInTrivialIfStatement =  "org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement"; //$NON-NLS-1$
	public static final String OPTION_ReportTasks = "org.eclipse.jdt.core.compiler.problem.tasks"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedObjectAllocation = "org.eclipse.jdt.core.compiler.problem.unusedObjectAllocation";  //$NON-NLS-1$
	public static final String OPTION_IncludeNullInfoFromAsserts = "org.eclipse.jdt.core.compiler.problem.includeNullInfoFromAsserts";  //$NON-NLS-1$
	public static final String OPTION_ReportMethodCanBeStatic = "org.eclipse.jdt.core.compiler.problem.reportMethodCanBeStatic";  //$NON-NLS-1$
	public static final String OPTION_ReportMethodCanBePotentiallyStatic = "org.eclipse.jdt.core.compiler.problem.reportMethodCanBePotentiallyStatic";  //$NON-NLS-1$
	public static final String OPTION_ReportRedundantSpecificationOfTypeArguments =  "org.eclipse.jdt.core.compiler.problem.redundantSpecificationOfTypeArguments"; //$NON-NLS-1$
	/**
	 * Possible values for configurable options
	 */
	public static final String GENERATE = "generate";//$NON-NLS-1$
	public static final String DO_NOT_GENERATE = "do not generate"; //$NON-NLS-1$
	public static final String PRESERVE = "preserve"; //$NON-NLS-1$
	public static final String OPTIMIZE_OUT = "optimize out"; //$NON-NLS-1$
	public static final String VERSION_1_1 = "1.1"; //$NON-NLS-1$
	public static final String VERSION_1_2 = "1.2"; //$NON-NLS-1$
	public static final String VERSION_1_3 = "1.3"; //$NON-NLS-1$
	public static final String VERSION_1_4 = "1.4"; //$NON-NLS-1$
	public static final String VERSION_JSR14 = "jsr14"; //$NON-NLS-1$
	public static final String VERSION_CLDC1_1 = "cldc1.1"; //$NON-NLS-1$
	public static final String VERSION_1_5 = "1.5"; //$NON-NLS-1$
	public static final String VERSION_1_6 = "1.6"; //$NON-NLS-1$
	public static final String VERSION_1_7 = "1.7"; //$NON-NLS-1$
	public static final String ERROR = "error"; //$NON-NLS-1$
	public static final String WARNING = "warning"; //$NON-NLS-1$
	public static final String IGNORE = "ignore"; //$NON-NLS-1$
	public static final String ENABLED = "enabled"; //$NON-NLS-1$
	public static final String DISABLED = "disabled"; //$NON-NLS-1$
	public static final String PUBLIC = "public";	//$NON-NLS-1$
	public static final String PROTECTED = "protected";	//$NON-NLS-1$
	public static final String DEFAULT = "default";	//$NON-NLS-1$
	public static final String PRIVATE = "private";	//$NON-NLS-1$
	public static final String RETURN_TAG = "return_tag";	//$NON-NLS-1$
	public static final String NO_TAG = "no_tag";	//$NON-NLS-1$
	public static final String ALL_STANDARD_TAGS = "all_standard_tags";	//$NON-NLS-1$

	/**
	 * Bit mask for configurable problems (error/warning threshold)
	 * Note: bitmask assumes 3 highest bits to denote irritant group (to allow storing 8 groups of 29 bits each
	 */
	// group 0
	public static final int MethodWithConstructorName = IrritantSet.GROUP0 | ASTNode.Bit1;
	public static final int OverriddenPackageDefaultMethod = IrritantSet.GROUP0 | ASTNode.Bit2;
	public static final int UsingDeprecatedAPI = IrritantSet.GROUP0 | ASTNode.Bit3;
	public static final int MaskedCatchBlock = IrritantSet.GROUP0 | ASTNode.Bit4;
	public static final int UnusedLocalVariable = IrritantSet.GROUP0 | ASTNode.Bit5;
	public static final int UnusedArgument = IrritantSet.GROUP0 | ASTNode.Bit6;
	public static final int NoImplicitStringConversion = IrritantSet.GROUP0 | ASTNode.Bit7;
	public static final int AccessEmulation = IrritantSet.GROUP0 | ASTNode.Bit8;
	public static final int NonExternalizedString = IrritantSet.GROUP0 | ASTNode.Bit9;
	public static final int AssertUsedAsAnIdentifier = IrritantSet.GROUP0 | ASTNode.Bit10;
	public static final int UnusedImport = IrritantSet.GROUP0 | ASTNode.Bit11;
	public static final int NonStaticAccessToStatic = IrritantSet.GROUP0 | ASTNode.Bit12;
	public static final int Task = IrritantSet.GROUP0 | ASTNode.Bit13;
	public static final int NoEffectAssignment = IrritantSet.GROUP0 | ASTNode.Bit14;
	public static final int IncompatibleNonInheritedInterfaceMethod = IrritantSet.GROUP0 | ASTNode.Bit15;
	public static final int UnusedPrivateMember = IrritantSet.GROUP0 | ASTNode.Bit16;
	public static final int LocalVariableHiding = IrritantSet.GROUP0 | ASTNode.Bit17;
	public static final int FieldHiding = IrritantSet.GROUP0 | ASTNode.Bit18;
	public static final int AccidentalBooleanAssign = IrritantSet.GROUP0 | ASTNode.Bit19;
	public static final int EmptyStatement = IrritantSet.GROUP0 | ASTNode.Bit20;
	public static final int MissingJavadocComments  = IrritantSet.GROUP0 | ASTNode.Bit21;
	public static final int MissingJavadocTags = IrritantSet.GROUP0 | ASTNode.Bit22;
	public static final int UnqualifiedFieldAccess = IrritantSet.GROUP0 | ASTNode.Bit23;
	public static final int UnusedDeclaredThrownException = IrritantSet.GROUP0 | ASTNode.Bit24;
	public static final int FinallyBlockNotCompleting = IrritantSet.GROUP0 | ASTNode.Bit25;
	public static final int InvalidJavadoc = IrritantSet.GROUP0 | ASTNode.Bit26;
	public static final int UnnecessaryTypeCheck = IrritantSet.GROUP0 | ASTNode.Bit27;
	public static final int UndocumentedEmptyBlock = IrritantSet.GROUP0 | ASTNode.Bit28;
	public static final int IndirectStaticAccess = IrritantSet.GROUP0 | ASTNode.Bit29;
	
	// group 1
	public static final int UnnecessaryElse  = IrritantSet.GROUP1 | ASTNode.Bit1;
	public static final int UncheckedTypeOperation = IrritantSet.GROUP1 | ASTNode.Bit2;
	public static final int FinalParameterBound = IrritantSet.GROUP1 | ASTNode.Bit3;
	public static final int MissingSerialVersion = IrritantSet.GROUP1 | ASTNode.Bit4;
	public static final int EnumUsedAsAnIdentifier = IrritantSet.GROUP1 | ASTNode.Bit5;
	public static final int ForbiddenReference = IrritantSet.GROUP1 | ASTNode.Bit6;
	public static final int VarargsArgumentNeedCast = IrritantSet.GROUP1 | ASTNode.Bit7;
	public static final int NullReference = IrritantSet.GROUP1 | ASTNode.Bit8;
	public static final int AutoBoxing = IrritantSet.GROUP1 | ASTNode.Bit9;
	public static final int AnnotationSuperInterface = IrritantSet.GROUP1 | ASTNode.Bit10;
	public static final int TypeHiding = IrritantSet.GROUP1 | ASTNode.Bit11;
	public static final int MissingOverrideAnnotation = IrritantSet.GROUP1 | ASTNode.Bit12;
	public static final int IncompleteEnumSwitch = IrritantSet.GROUP1 | ASTNode.Bit13;
	public static final int MissingDeprecatedAnnotation = IrritantSet.GROUP1 | ASTNode.Bit14;
	public static final int DiscouragedReference = IrritantSet.GROUP1 | ASTNode.Bit15;
	public static final int UnhandledWarningToken = IrritantSet.GROUP1 | ASTNode.Bit16;
	public static final int RawTypeReference = IrritantSet.GROUP1 | ASTNode.Bit17;
	public static final int UnusedLabel = IrritantSet.GROUP1 | ASTNode.Bit18;
	public static final int ParameterAssignment = IrritantSet.GROUP1 | ASTNode.Bit19;
	public static final int FallthroughCase = IrritantSet.GROUP1 | ASTNode.Bit20;
	public static final int OverridingMethodWithoutSuperInvocation = IrritantSet.GROUP1 | ASTNode.Bit21;
	public static final int PotentialNullReference = IrritantSet.GROUP1 | ASTNode.Bit22;
	public static final int RedundantNullCheck = IrritantSet.GROUP1 | ASTNode.Bit23;
	public static final int MissingJavadocTagDescription = IrritantSet.GROUP1 | ASTNode.Bit24;
	public static final int UnusedTypeArguments = IrritantSet.GROUP1 | ASTNode.Bit25;
	public static final int UnusedWarningToken = IrritantSet.GROUP1 | ASTNode.Bit26;
	public static final int RedundantSuperinterface = IrritantSet.GROUP1 | ASTNode.Bit27;
	public static final int ComparingIdentical = IrritantSet.GROUP1 | ASTNode.Bit28;
	public static final int MissingSynchronizedModifierInInheritedMethod= IrritantSet.GROUP1 | ASTNode.Bit29;

	// group 2
	public static final int ShouldImplementHashcode = IrritantSet.GROUP2 | ASTNode.Bit1;
	public static final int DeadCode = IrritantSet.GROUP2 | ASTNode.Bit2;
	public static final int Tasks = IrritantSet.GROUP2 | ASTNode.Bit3;
	public static final int UnusedObjectAllocation = IrritantSet.GROUP2 | ASTNode.Bit4;
	public static final int MethodCanBeStatic = IrritantSet.GROUP2 | ASTNode.Bit5;
	public static final int MethodCanBePotentiallyStatic = IrritantSet.GROUP2 | ASTNode.Bit6;
	public static final int RedundantSpecificationOfTypeArguments = IrritantSet.GROUP2 | ASTNode.Bit7;

	// Severity level for handlers
	/** 
	 * Defaults defined at {@link IrritantSet#COMPILER_DEFAULT_ERRORS} 
	 * @see #resetDefaults()
	 */
	protected IrritantSet errorThreshold;
	/** 
	 * Defaults defined at {@link IrritantSet#COMPILER_DEFAULT_WARNINGS}
	 * @see #resetDefaults()
	 */
	protected IrritantSet warningThreshold;
	
	/**
	 * Default settings are to be defined in {@lnk CompilerOptions#resetDefaults()}
	 */
	
	/** Classfile debug information, may contain source file name, line numbers, local variable tables, etc... */
	public int produceDebugAttributes; 
	/** Compliance level for the compiler, refers to a JDK version, e.g. {@link ClassFileConstants#JDK1_4} */
	public long complianceLevel;
	/** Original compliance level for the compiler, refers to a JDK version, e.g. {@link ClassFileConstants#JDK1_4},
	 *  Usually same as the field complianceLevel, though the latter could deviate to create temporary sandbox
	 *  modes during reconcile operations. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=323633
	 */
	public long originalComplianceLevel;
	/** Java source level, refers to a JDK version, e.g. {@link ClassFileConstants#JDK1_4} */
	public long sourceLevel;
	/** Original Java source level, refers to a JDK version, e.g. {@link ClassFileConstants#JDK1_4} 
	 *  Usually same as the field sourceLevel, though the latter could deviate to create temporary sandbox
	 *  modes during reconcile operations. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=323633
	 * */
	public long originalSourceLevel;
	/** VM target level, refers to a JDK version, e.g. {@link ClassFileConstants#JDK1_4} */
	public long targetJDK;
	/** Source encoding format */
	public String defaultEncoding;
	/** Compiler trace verbosity */
	public boolean verbose;
	/** Indicates whether reference info is desired */
	public boolean produceReferenceInfo;	
	/** Indicates if unused/optimizable local variables need to be preserved (debugging purpose) */
	public boolean preserveAllLocalVariables;
	/** Indicates whether literal expressions are inlined at parse-time or not */
	public boolean parseLiteralExpressionsAsConstants;
	/** Max problems per compilation unit */
	public int maxProblemsPerUnit;
	/** Tags used to recognize tasks in comments */
	public char[][] taskTags;
	/** Respective priorities of recognized task tags */
	public char[][] taskPriorities;
	/** Indicate whether tag detection is case sensitive or not */
	public boolean isTaskCaseSensitive;
	/** Specify whether deprecation inside deprecated code is to be reported */
	public boolean reportDeprecationInsideDeprecatedCode;
	/** Specify whether override of deprecated method is to be reported */
	public boolean reportDeprecationWhenOverridingDeprecatedMethod;
	/** Specify if should report unused parameter when implementing abstract method */
	public boolean reportUnusedParameterWhenImplementingAbstract;
	/** Specify if should report unused parameter when overriding concrete method */
	public boolean reportUnusedParameterWhenOverridingConcrete;
	/** Specify if should report documented unused parameter (in javadoc) */
	public boolean reportUnusedParameterIncludeDocCommentReference;
	/** Specify if should reported unused declared thrown exception when overriding method */
	public boolean reportUnusedDeclaredThrownExceptionWhenOverriding;
	/** Specify if should reported unused declared thrown exception when documented in javadoc */
	public boolean reportUnusedDeclaredThrownExceptionIncludeDocCommentReference;
	/** Specify if should reported unused declared thrown exception when Exception or Throwable */
	public boolean reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable;
	/** Specify whether should report constructor/setter method parameter hiding */
	public boolean reportSpecialParameterHidingField;
	/** Specify whether trivial deadcode pattern is to be reported (e.g. if (DEBUG) ...) */
	public boolean reportDeadCodeInTrivialIfStatement;
	/** Master flag controlling whether doc comment should be processed */
	public boolean docCommentSupport;
	/** Specify if invalid javadoc shall be reported */
	public boolean reportInvalidJavadocTags;
	/** Only report invalid javadoc above a given level of visibility of associated construct */
	public int reportInvalidJavadocTagsVisibility;
	/** Specify if deprecated javadoc ref is allowed */
	public boolean reportInvalidJavadocTagsDeprecatedRef;
	/** Specify if non visible javadoc ref is allowed */
	public boolean reportInvalidJavadocTagsNotVisibleRef;
	/** Specify when to report missing javadoc tag description */
	public String reportMissingJavadocTagDescription;
	/** Only report missing javadoc tags above a given level of visibility of associated construct */
	public int reportMissingJavadocTagsVisibility;
	/** Specify if need to flag missing javadoc tags for overriding method */
	public boolean reportMissingJavadocTagsOverriding;
	/** Specify if need to flag missing javadoc tags for method type parameters (java 1.5 and above)*/
	public boolean reportMissingJavadocTagsMethodTypeParameters;
	/** Only report missing javadoc comment above a given level of visibility of associated construct */
	public int reportMissingJavadocCommentsVisibility;
	/** Specify if need to flag missing javadoc comment for overriding method */
	public boolean reportMissingJavadocCommentsOverriding;
	/** Indicate whether the JSR bytecode should be inlined to avoid its presence in classfile */
	public boolean inlineJsrBytecode;
	/** Indicate if @SuppressWarning annotations are activated */
	public boolean suppressWarnings;
	/** Indicate if @SuppressWarning annotations should also suppress optional errors */
	public boolean suppressOptionalErrors;
	/** Specify if should treat optional error as fatal or just like warning */
	public boolean treatOptionalErrorAsFatal;
	/** Specify if parser should perform structural recovery in methods */
	public boolean performMethodsFullRecovery;
	/** Specify if parser perform statements recovery */
	public boolean performStatementsRecovery;
	/** Control whether annotation processing is enabled */
	public boolean processAnnotations;
	/** Store annotations */
	public boolean storeAnnotations;
	/** Specify if need to report missing override annotation for a method implementing an interface method (java 1.6 and above)*/
	public boolean reportMissingOverrideAnnotationForInterfaceMethodImplementation;
	/** Indicate if annotation processing generates classfiles */
	public boolean generateClassFiles;
	/** Indicate if method bodies should be ignored */
	public boolean ignoreMethodBodies;
	/** Raise null related warnings for variables tainted inside an assert statement (java 1.4 and above)*/
	public boolean includeNullInfoFromAsserts;
	/** Controls whether forced generic type problems get reported  */
	public boolean reportUnavoidableGenericTypeProblems;

	// keep in sync with warningTokenToIrritant and warningTokenFromIrritant
	public final static String[] warningTokens = {
		"all", //$NON-NLS-1$
		"boxing", //$NON-NLS-1$
		"cast", //$NON-NLS-1$
		"dep-ann", //$NON-NLS-1$
		"deprecation", //$NON-NLS-1$
		"fallthrough", //$NON-NLS-1$
		"finally", //$NON-NLS-1$
		"hiding", //$NON-NLS-1$
		"incomplete-switch", //$NON-NLS-1$
		"javadoc", //$NON-NLS-1$
		"nls", //$NON-NLS-1$
		"null", //$NON-NLS-1$
		"restriction", //$NON-NLS-1$
		"rawtypes", //$NON-NLS-1$
		"serial", //$NON-NLS-1$
		"static-access", //$NON-NLS-1$
		"static-method", //$NON-NLS-1$
		"super", //$NON-NLS-1$
		"synthetic-access", //$NON-NLS-1$
		"unchecked", //$NON-NLS-1$
		"unqualified-field-access", //$NON-NLS-1$
		"unused", //$NON-NLS-1$
	};

	/**
	 * Initializing the compiler options with defaults
	 */
	public CompilerOptions(){
		this(null); // use default options
	}

	/**
	 * Initializing the compiler options with external settings
	 * @param settings
	 */
	public CompilerOptions(Map settings){
		resetDefaults();
		if (settings != null) {
			set(settings);
		}
	}

	/**
	 * @deprecated used to preserve 3.1 and 3.2M4 compatibility of some Compiler constructors
	 */
	public CompilerOptions(Map settings, boolean parseLiteralExpressionsAsConstants){
		this(settings);
		this.parseLiteralExpressionsAsConstants = parseLiteralExpressionsAsConstants;
	}

	/**
	 * Return the most specific option key controlling this irritant. Note that in some case, some irritant is controlled by
	 * other master options (e.g. javadoc, deprecation, etc.).
	 * This information is intended for grouping purpose (several problems governed by a rule)
	 */
	public static String optionKeyFromIrritant(int irritant) {
		// keep in sync with warningTokens and warningTokenToIrritant
		switch (irritant) {
			case MethodWithConstructorName :
				return OPTION_ReportMethodWithConstructorName;
			case OverriddenPackageDefaultMethod  :
				return OPTION_ReportOverridingPackageDefaultMethod;
			case UsingDeprecatedAPI :
			case (InvalidJavadoc | UsingDeprecatedAPI) :
				return OPTION_ReportDeprecation;
			case MaskedCatchBlock  :
				return OPTION_ReportHiddenCatchBlock;
			case UnusedLocalVariable :
				return OPTION_ReportUnusedLocal;
			case UnusedArgument :
				return OPTION_ReportUnusedParameter;
			case NoImplicitStringConversion :
				return OPTION_ReportNoImplicitStringConversion;
			case AccessEmulation :
				return OPTION_ReportSyntheticAccessEmulation;
			case NonExternalizedString :
				return OPTION_ReportNonExternalizedStringLiteral;
			case AssertUsedAsAnIdentifier :
				return OPTION_ReportAssertIdentifier;
			case UnusedImport :
				return OPTION_ReportUnusedImport;
			case NonStaticAccessToStatic :
				return OPTION_ReportNonStaticAccessToStatic;
			case Task :
				return OPTION_TaskTags;
			case NoEffectAssignment :
				return OPTION_ReportNoEffectAssignment;
			case IncompatibleNonInheritedInterfaceMethod :
				return OPTION_ReportIncompatibleNonInheritedInterfaceMethod;
			case UnusedPrivateMember :
				return OPTION_ReportUnusedPrivateMember;
			case LocalVariableHiding :
				return OPTION_ReportLocalVariableHiding;
			case FieldHiding :
				return OPTION_ReportFieldHiding;
			case AccidentalBooleanAssign :
				return OPTION_ReportPossibleAccidentalBooleanAssignment;
			case EmptyStatement :
				return OPTION_ReportEmptyStatement;
			case MissingJavadocComments  :
				return OPTION_ReportMissingJavadocComments;
			case MissingJavadocTags :
				return OPTION_ReportMissingJavadocTags;
			case UnqualifiedFieldAccess :
				return OPTION_ReportUnqualifiedFieldAccess;
			case UnusedDeclaredThrownException :
				return OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding;
			case FinallyBlockNotCompleting :
				return OPTION_ReportFinallyBlockNotCompletingNormally;
			case InvalidJavadoc :
				return OPTION_ReportInvalidJavadoc;
			case UnnecessaryTypeCheck :
				return OPTION_ReportUnnecessaryTypeCheck;
			case UndocumentedEmptyBlock :
				return OPTION_ReportUndocumentedEmptyBlock;
			case IndirectStaticAccess :
				return OPTION_ReportIndirectStaticAccess;
			case UnnecessaryElse  :
				return OPTION_ReportUnnecessaryElse;
			case UncheckedTypeOperation :
				return OPTION_ReportUncheckedTypeOperation;
			case FinalParameterBound :
				return OPTION_ReportFinalParameterBound;
			case MissingSerialVersion :
				return OPTION_ReportMissingSerialVersion ;
			case EnumUsedAsAnIdentifier :
				return OPTION_ReportEnumIdentifier;
			case ForbiddenReference :
				return OPTION_ReportForbiddenReference;
			case VarargsArgumentNeedCast :
				return OPTION_ReportVarargsArgumentNeedCast;
			case NullReference :
				return OPTION_ReportNullReference;
			case PotentialNullReference :
				return OPTION_ReportPotentialNullReference;
			case RedundantNullCheck :
				return OPTION_ReportRedundantNullCheck;
			case AutoBoxing :
				return OPTION_ReportAutoboxing;
			case AnnotationSuperInterface :
				return OPTION_ReportAnnotationSuperInterface;
			case TypeHiding :
				return OPTION_ReportTypeParameterHiding;
			case MissingOverrideAnnotation :
				return OPTION_ReportMissingOverrideAnnotation;
			case IncompleteEnumSwitch :
				return OPTION_ReportIncompleteEnumSwitch;
			case MissingDeprecatedAnnotation :
				return OPTION_ReportMissingDeprecatedAnnotation;
			case DiscouragedReference :
				return OPTION_ReportDiscouragedReference;
			case UnhandledWarningToken :
				return OPTION_ReportUnhandledWarningToken;
			case RawTypeReference :
				return OPTION_ReportRawTypeReference;
			case UnusedLabel :
				return OPTION_ReportUnusedLabel;
			case ParameterAssignment :
				return OPTION_ReportParameterAssignment;
			case FallthroughCase :
				return OPTION_ReportFallthroughCase;
			case OverridingMethodWithoutSuperInvocation :
				return OPTION_ReportOverridingMethodWithoutSuperInvocation;
			case MissingJavadocTagDescription :
				return OPTION_ReportMissingJavadocTagDescription;
			case UnusedTypeArguments :
				return OPTION_ReportUnusedTypeArgumentsForMethodInvocation;
			case UnusedWarningToken :
				return OPTION_ReportUnusedWarningToken;
			case RedundantSuperinterface :
				return OPTION_ReportRedundantSuperinterface;
			case ComparingIdentical :
				return OPTION_ReportComparingIdentical;
			case MissingSynchronizedModifierInInheritedMethod :
				return OPTION_ReportMissingSynchronizedOnInheritedMethod;
			case ShouldImplementHashcode :
				return OPTION_ReportMissingHashCodeMethod;
			case DeadCode :
				return OPTION_ReportDeadCode;
			case UnusedObjectAllocation:
				return OPTION_ReportUnusedObjectAllocation;
			case MethodCanBeStatic :
				return OPTION_ReportMethodCanBeStatic;
			case MethodCanBePotentiallyStatic :
				return OPTION_ReportMethodCanBePotentiallyStatic;
			case RedundantSpecificationOfTypeArguments :
				return OPTION_ReportRedundantSpecificationOfTypeArguments;
		}
		return null;
	}

	public static String versionFromJdkLevel(long jdkLevel) {
		switch ((int)(jdkLevel>>16)) {
			case ClassFileConstants.MAJOR_VERSION_1_1 :
				if (jdkLevel == ClassFileConstants.JDK1_1)
					return VERSION_1_1;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_2 :
				if (jdkLevel == ClassFileConstants.JDK1_2)
					return VERSION_1_2;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_3 :
				if (jdkLevel == ClassFileConstants.JDK1_3)
					return VERSION_1_3;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_4 :
				if (jdkLevel == ClassFileConstants.JDK1_4)
					return VERSION_1_4;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_5 :
				if (jdkLevel == ClassFileConstants.JDK1_5)
					return VERSION_1_5;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_6 :
				if (jdkLevel == ClassFileConstants.JDK1_6)
					return VERSION_1_6;
				break;
			case ClassFileConstants.MAJOR_VERSION_1_7 :
				if (jdkLevel == ClassFileConstants.JDK1_7)
					return VERSION_1_7;
				break;
		}
		return Util.EMPTY_STRING; // unknown version
	}

	public static long versionToJdkLevel(Object versionID) {
		if (versionID instanceof String) {
			String version = (String) versionID;
			// verification is optimized for all versions with same length and same "1." prefix
			if (version.length() == 3 && version.charAt(0) == '1' && version.charAt(1) == '.') {
				switch (version.charAt(2)) {
					case '1':
						return ClassFileConstants.JDK1_1;
					case '2':
						return ClassFileConstants.JDK1_2;
					case '3':
						return ClassFileConstants.JDK1_3;
					case '4':
						return ClassFileConstants.JDK1_4;
					case '5':
						return ClassFileConstants.JDK1_5;
					case '6':
						return ClassFileConstants.JDK1_6;
					case '7':
						return ClassFileConstants.JDK1_7;
					default:
						return 0; // unknown
				}
			}
			if (VERSION_JSR14.equals(versionID)) {
				return ClassFileConstants.JDK1_4;
			}
			if (VERSION_CLDC1_1.equals(versionID)) {
				return ClassFileConstants.CLDC_1_1;
			}
		}
		return 0; // unknown
	}

	/**
	 * Return all warning option names for use as keys in compiler options maps.
	 * @return all warning option names
	 */
	public static String[] warningOptionNames() {
		String[] result = {
			OPTION_ReportAnnotationSuperInterface,
			OPTION_ReportAssertIdentifier,
			OPTION_ReportAutoboxing,
			OPTION_ReportComparingIdentical,
			OPTION_ReportDeadCode,
			OPTION_ReportDeadCodeInTrivialIfStatement,
			OPTION_ReportDeprecation,
			OPTION_ReportDeprecationInDeprecatedCode,
			OPTION_ReportDeprecationWhenOverridingDeprecatedMethod,
			OPTION_ReportDiscouragedReference,
			OPTION_ReportEmptyStatement,
			OPTION_ReportEnumIdentifier,
			OPTION_ReportFallthroughCase,
			OPTION_ReportFieldHiding,
			OPTION_ReportFinallyBlockNotCompletingNormally,
			OPTION_ReportFinalParameterBound,
			OPTION_ReportForbiddenReference,
			OPTION_ReportHiddenCatchBlock,
			OPTION_ReportIncompatibleNonInheritedInterfaceMethod,
			OPTION_ReportIncompleteEnumSwitch,
			OPTION_ReportIndirectStaticAccess,
			OPTION_ReportInvalidJavadoc,
			OPTION_ReportInvalidJavadocTags,
			OPTION_ReportInvalidJavadocTagsDeprecatedRef,
			OPTION_ReportInvalidJavadocTagsNotVisibleRef,
			OPTION_ReportInvalidJavadocTagsVisibility,
			OPTION_ReportLocalVariableHiding,
			OPTION_ReportMethodCanBePotentiallyStatic,
			OPTION_ReportMethodCanBeStatic,
			OPTION_ReportMethodWithConstructorName,
			OPTION_ReportMissingDeprecatedAnnotation,
			OPTION_ReportMissingHashCodeMethod,
			OPTION_ReportMissingJavadocComments,
			OPTION_ReportMissingJavadocCommentsOverriding,
			OPTION_ReportMissingJavadocCommentsVisibility,
			OPTION_ReportMissingJavadocTagDescription,
			OPTION_ReportMissingJavadocTags,
			OPTION_ReportMissingJavadocTagsMethodTypeParameters,
			OPTION_ReportMissingJavadocTagsOverriding,
			OPTION_ReportMissingJavadocTagsVisibility,
			OPTION_ReportMissingOverrideAnnotation,
			OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation,
			OPTION_ReportMissingSerialVersion,
			OPTION_ReportMissingSynchronizedOnInheritedMethod,
			OPTION_ReportNoEffectAssignment,
			OPTION_ReportNoImplicitStringConversion,
			OPTION_ReportNonExternalizedStringLiteral,
			OPTION_ReportNonStaticAccessToStatic,
			OPTION_ReportNullReference,
			OPTION_ReportOverridingMethodWithoutSuperInvocation,
			OPTION_ReportOverridingPackageDefaultMethod,
			OPTION_ReportParameterAssignment,
			OPTION_ReportPossibleAccidentalBooleanAssignment,
			OPTION_ReportPotentialNullReference,
			OPTION_ReportRawTypeReference,
			OPTION_ReportRedundantNullCheck,
			OPTION_ReportRedundantSuperinterface,
			OPTION_ReportRedundantSpecificationOfTypeArguments,
			OPTION_ReportSpecialParameterHidingField,
			OPTION_ReportSyntheticAccessEmulation,
			OPTION_ReportTasks,
			OPTION_ReportTypeParameterHiding,
			OPTION_ReportUnavoidableGenericTypeProblems,
			OPTION_ReportUncheckedTypeOperation,
			OPTION_ReportUndocumentedEmptyBlock,
			OPTION_ReportUnhandledWarningToken,
			OPTION_ReportUnnecessaryElse,
			OPTION_ReportUnnecessaryTypeCheck,
			OPTION_ReportUnqualifiedFieldAccess,
			OPTION_ReportUnusedDeclaredThrownException,
			OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable,
			OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference,
			OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding,
			OPTION_ReportUnusedImport,
			OPTION_ReportUnusedLabel,
			OPTION_ReportUnusedLocal,
			OPTION_ReportUnusedObjectAllocation,
			OPTION_ReportUnusedParameter,
			OPTION_ReportUnusedParameterIncludeDocCommentReference,
			OPTION_ReportUnusedParameterWhenImplementingAbstract,
			OPTION_ReportUnusedParameterWhenOverridingConcrete,
			OPTION_ReportUnusedPrivateMember,
			OPTION_ReportUnusedTypeArgumentsForMethodInvocation,
			OPTION_ReportUnusedWarningToken,
			OPTION_ReportVarargsArgumentNeedCast,
		};
		return result;
	}

	/**
	 * For suppressable warnings
	 */
	public static String warningTokenFromIrritant(int irritant) {
		// keep in sync with warningTokens and warningTokenToIrritant
		switch (irritant) {
			case (InvalidJavadoc | UsingDeprecatedAPI) :
			case UsingDeprecatedAPI :
				return "deprecation"; //$NON-NLS-1$
			case FinallyBlockNotCompleting :
				return "finally"; //$NON-NLS-1$
			case FieldHiding :
			case LocalVariableHiding :
			case MaskedCatchBlock :
				return "hiding"; //$NON-NLS-1$
			case NonExternalizedString :
				return "nls"; //$NON-NLS-1$
			case UnnecessaryTypeCheck :
				return "cast"; //$NON-NLS-1$
			case IndirectStaticAccess :
			case NonStaticAccessToStatic :
				return "static-access"; //$NON-NLS-1$
			case AccessEmulation :
				return "synthetic-access"; //$NON-NLS-1$
			case UnqualifiedFieldAccess :
				return "unqualified-field-access"; //$NON-NLS-1$
			case UncheckedTypeOperation :
				return "unchecked"; //$NON-NLS-1$
			case MissingSerialVersion :
				return "serial"; //$NON-NLS-1$
			case AutoBoxing :
				return "boxing"; //$NON-NLS-1$
			case TypeHiding :
				return "hiding"; //$NON-NLS-1$
			case IncompleteEnumSwitch :
				return "incomplete-switch"; //$NON-NLS-1$
			case MissingDeprecatedAnnotation :
				return "dep-ann"; //$NON-NLS-1$
			case RawTypeReference :
				return "rawtypes"; //$NON-NLS-1$
			case UnusedLabel :
			case UnusedTypeArguments :
			case RedundantSuperinterface :
			case UnusedLocalVariable :
			case UnusedArgument :
			case UnusedImport :
			case UnusedPrivateMember :
			case UnusedDeclaredThrownException :
			case DeadCode :
			case UnusedObjectAllocation :
			case RedundantSpecificationOfTypeArguments :
				return "unused"; //$NON-NLS-1$
			case DiscouragedReference :
			case ForbiddenReference :
				return "restriction"; //$NON-NLS-1$
			case NullReference :
			case PotentialNullReference :
			case RedundantNullCheck :
				return "null"; //$NON-NLS-1$
			case FallthroughCase :
				return "fallthrough"; //$NON-NLS-1$
			case OverridingMethodWithoutSuperInvocation :
				return "super"; //$NON-NLS-1$
			case MethodCanBeStatic :
			case MethodCanBePotentiallyStatic :
				return "static-method"; //$NON-NLS-1$
			case InvalidJavadoc :
			case MissingJavadocComments :
			case MissingJavadocTags:
				return "javadoc"; //$NON-NLS-1$				
		}
		return null;
	}

	public static IrritantSet warningTokenToIrritants(String warningToken) {
		// keep in sync with warningTokens and warningTokenFromIrritant
		if (warningToken == null || warningToken.length() == 0) return null;
		switch (warningToken.charAt(0)) {
			case 'a' :
				if ("all".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.ALL;
				break;
			case 'b' :
				if ("boxing".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.BOXING;
				break;
			case 'c' :
				if ("cast".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.CAST;
				break;
			case 'd' :
				if ("deprecation".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.DEPRECATION;
				if ("dep-ann".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.DEP_ANN;
				break;
			case 'f' :
				if ("fallthrough".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.FALLTHROUGH;
				if ("finally".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.FINALLY;
				break;
			case 'h' :
				if ("hiding".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.HIDING;
				break;
			case 'i' :
				if ("incomplete-switch".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.INCOMPLETE_SWITCH;
				break;
			case 'j' :
				if ("javadoc".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.JAVADOC;
				break;
			case 'n' :
				if ("nls".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.NLS;
				if ("null".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.NULL;
				break;
			case 'r' :
				if ("rawtypes".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.RAW;
				if ("restriction".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.RESTRICTION;
				break;
			case 's' :
				if ("serial".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.SERIAL;
				if ("static-access".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.STATIC_ACCESS;
				if ("static-method".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.STATIC_METHOD;
				if ("synthetic-access".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.SYNTHETIC_ACCESS;
				if ("super".equals(warningToken)) { //$NON-NLS-1$
					return IrritantSet.SUPER;
				}
				break;
			case 'u' :
				if ("unused".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.UNUSED;
				if ("unchecked".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.UNCHECKED;
				if ("unqualified-field-access".equals(warningToken)) //$NON-NLS-1$
					return IrritantSet.UNQUALIFIED_FIELD_ACCESS;
				break;
		}
		return null;
	}

	
	public Map getMap() {
		Map optionsMap = new HashMap(30);
		optionsMap.put(OPTION_LocalVariableAttribute, (this.produceDebugAttributes & ClassFileConstants.ATTR_VARS) != 0 ? GENERATE : DO_NOT_GENERATE);
		optionsMap.put(OPTION_LineNumberAttribute, (this.produceDebugAttributes & ClassFileConstants.ATTR_LINES) != 0 ? GENERATE : DO_NOT_GENERATE);
		optionsMap.put(OPTION_SourceFileAttribute, (this.produceDebugAttributes & ClassFileConstants.ATTR_SOURCE) != 0 ? GENERATE : DO_NOT_GENERATE);
		optionsMap.put(OPTION_PreserveUnusedLocal, this.preserveAllLocalVariables ? PRESERVE : OPTIMIZE_OUT);
		optionsMap.put(OPTION_DocCommentSupport, this.docCommentSupport ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMethodWithConstructorName, getSeverityString(MethodWithConstructorName));
		optionsMap.put(OPTION_ReportOverridingPackageDefaultMethod, getSeverityString(OverriddenPackageDefaultMethod));
		optionsMap.put(OPTION_ReportDeprecation, getSeverityString(UsingDeprecatedAPI));
		optionsMap.put(OPTION_ReportDeprecationInDeprecatedCode, this.reportDeprecationInsideDeprecatedCode ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportDeprecationWhenOverridingDeprecatedMethod, this.reportDeprecationWhenOverridingDeprecatedMethod ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportHiddenCatchBlock, getSeverityString(MaskedCatchBlock));
		optionsMap.put(OPTION_ReportUnusedLocal, getSeverityString(UnusedLocalVariable));
		optionsMap.put(OPTION_ReportUnusedParameter, getSeverityString(UnusedArgument));
		optionsMap.put(OPTION_ReportUnusedImport, getSeverityString(UnusedImport));
		optionsMap.put(OPTION_ReportSyntheticAccessEmulation, getSeverityString(AccessEmulation));
		optionsMap.put(OPTION_ReportNoEffectAssignment, getSeverityString(NoEffectAssignment));
		optionsMap.put(OPTION_ReportNonExternalizedStringLiteral, getSeverityString(NonExternalizedString));
		optionsMap.put(OPTION_ReportNoImplicitStringConversion, getSeverityString(NoImplicitStringConversion));
		optionsMap.put(OPTION_ReportNonStaticAccessToStatic, getSeverityString(NonStaticAccessToStatic));
		optionsMap.put(OPTION_ReportIndirectStaticAccess, getSeverityString(IndirectStaticAccess));
		optionsMap.put(OPTION_ReportIncompatibleNonInheritedInterfaceMethod, getSeverityString(IncompatibleNonInheritedInterfaceMethod));
		optionsMap.put(OPTION_ReportUnusedPrivateMember, getSeverityString(UnusedPrivateMember));
		optionsMap.put(OPTION_ReportLocalVariableHiding, getSeverityString(LocalVariableHiding));
		optionsMap.put(OPTION_ReportFieldHiding, getSeverityString(FieldHiding));
		optionsMap.put(OPTION_ReportTypeParameterHiding, getSeverityString(TypeHiding));
		optionsMap.put(OPTION_ReportPossibleAccidentalBooleanAssignment, getSeverityString(AccidentalBooleanAssign));
		optionsMap.put(OPTION_ReportEmptyStatement, getSeverityString(EmptyStatement));
		optionsMap.put(OPTION_ReportAssertIdentifier, getSeverityString(AssertUsedAsAnIdentifier));
		optionsMap.put(OPTION_ReportEnumIdentifier, getSeverityString(EnumUsedAsAnIdentifier));
		optionsMap.put(OPTION_ReportUndocumentedEmptyBlock, getSeverityString(UndocumentedEmptyBlock));
		optionsMap.put(OPTION_ReportUnnecessaryTypeCheck, getSeverityString(UnnecessaryTypeCheck));
		optionsMap.put(OPTION_ReportUnnecessaryElse, getSeverityString(UnnecessaryElse));
		optionsMap.put(OPTION_ReportAutoboxing, getSeverityString(AutoBoxing));
		optionsMap.put(OPTION_ReportAnnotationSuperInterface, getSeverityString(AnnotationSuperInterface));
		optionsMap.put(OPTION_ReportIncompleteEnumSwitch, getSeverityString(IncompleteEnumSwitch));
		optionsMap.put(OPTION_ReportInvalidJavadoc, getSeverityString(InvalidJavadoc));
		optionsMap.put(OPTION_ReportInvalidJavadocTagsVisibility, getVisibilityString(this.reportInvalidJavadocTagsVisibility));
		optionsMap.put(OPTION_ReportInvalidJavadocTags, this.reportInvalidJavadocTags ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportInvalidJavadocTagsDeprecatedRef, this.reportInvalidJavadocTagsDeprecatedRef ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportInvalidJavadocTagsNotVisibleRef, this.reportInvalidJavadocTagsNotVisibleRef ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMissingJavadocTags, getSeverityString(MissingJavadocTags));
		optionsMap.put(OPTION_ReportMissingJavadocTagsVisibility, getVisibilityString(this.reportMissingJavadocTagsVisibility));
		optionsMap.put(OPTION_ReportMissingJavadocTagsOverriding, this.reportMissingJavadocTagsOverriding ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMissingJavadocTagsMethodTypeParameters, this.reportMissingJavadocTagsMethodTypeParameters ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMissingJavadocComments, getSeverityString(MissingJavadocComments));
		optionsMap.put(OPTION_ReportMissingJavadocTagDescription, this.reportMissingJavadocTagDescription);
		optionsMap.put(OPTION_ReportMissingJavadocCommentsVisibility, getVisibilityString(this.reportMissingJavadocCommentsVisibility));
		optionsMap.put(OPTION_ReportMissingJavadocCommentsOverriding, this.reportMissingJavadocCommentsOverriding ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportFinallyBlockNotCompletingNormally, getSeverityString(FinallyBlockNotCompleting));
		optionsMap.put(OPTION_ReportUnusedDeclaredThrownException, getSeverityString(UnusedDeclaredThrownException));
		optionsMap.put(OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding, this.reportUnusedDeclaredThrownExceptionWhenOverriding ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference, this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable, this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnqualifiedFieldAccess, getSeverityString(UnqualifiedFieldAccess));
		optionsMap.put(OPTION_ReportUnavoidableGenericTypeProblems, this.reportUnavoidableGenericTypeProblems ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUncheckedTypeOperation, getSeverityString(UncheckedTypeOperation));
		optionsMap.put(OPTION_ReportRawTypeReference, getSeverityString(RawTypeReference));
		optionsMap.put(OPTION_ReportFinalParameterBound, getSeverityString(FinalParameterBound));
		optionsMap.put(OPTION_ReportMissingSerialVersion, getSeverityString(MissingSerialVersion));
		optionsMap.put(OPTION_ReportForbiddenReference, getSeverityString(ForbiddenReference));
		optionsMap.put(OPTION_ReportDiscouragedReference, getSeverityString(DiscouragedReference));
		optionsMap.put(OPTION_ReportVarargsArgumentNeedCast, getSeverityString(VarargsArgumentNeedCast));
		optionsMap.put(OPTION_ReportMissingOverrideAnnotation, getSeverityString(MissingOverrideAnnotation));
		optionsMap.put(OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation, this.reportMissingOverrideAnnotationForInterfaceMethodImplementation ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMissingDeprecatedAnnotation, getSeverityString(MissingDeprecatedAnnotation));
		optionsMap.put(OPTION_ReportIncompleteEnumSwitch, getSeverityString(IncompleteEnumSwitch));
		optionsMap.put(OPTION_ReportUnusedLabel, getSeverityString(UnusedLabel));
		optionsMap.put(OPTION_ReportUnusedTypeArgumentsForMethodInvocation, getSeverityString(UnusedTypeArguments));
		optionsMap.put(OPTION_Compliance, versionFromJdkLevel(this.complianceLevel));
		optionsMap.put(OPTION_Source, versionFromJdkLevel(this.sourceLevel));
		optionsMap.put(OPTION_TargetPlatform, versionFromJdkLevel(this.targetJDK));
		optionsMap.put(OPTION_FatalOptionalError, this.treatOptionalErrorAsFatal ? ENABLED : DISABLED);
		if (this.defaultEncoding != null) {
			optionsMap.put(OPTION_Encoding, this.defaultEncoding);
		}
		optionsMap.put(OPTION_TaskTags, this.taskTags == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskTags,',')));
		optionsMap.put(OPTION_TaskPriorities, this.taskPriorities == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskPriorities,',')));
		optionsMap.put(OPTION_TaskCaseSensitive, this.isTaskCaseSensitive ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnusedParameterWhenImplementingAbstract, this.reportUnusedParameterWhenImplementingAbstract ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnusedParameterWhenOverridingConcrete, this.reportUnusedParameterWhenOverridingConcrete ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnusedParameterIncludeDocCommentReference, this.reportUnusedParameterIncludeDocCommentReference ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportSpecialParameterHidingField, this.reportSpecialParameterHidingField ? ENABLED : DISABLED);
		optionsMap.put(OPTION_MaxProblemPerUnit, String.valueOf(this.maxProblemsPerUnit));
		optionsMap.put(OPTION_InlineJsr, this.inlineJsrBytecode ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportNullReference, getSeverityString(NullReference));
		optionsMap.put(OPTION_ReportPotentialNullReference, getSeverityString(PotentialNullReference));
		optionsMap.put(OPTION_ReportRedundantNullCheck, getSeverityString(RedundantNullCheck));
		optionsMap.put(OPTION_SuppressWarnings, this.suppressWarnings ? ENABLED : DISABLED);
		optionsMap.put(OPTION_SuppressOptionalErrors, this.suppressOptionalErrors ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportUnhandledWarningToken, getSeverityString(UnhandledWarningToken));
		optionsMap.put(OPTION_ReportUnusedWarningToken, getSeverityString(UnusedWarningToken));
		optionsMap.put(OPTION_ReportParameterAssignment, getSeverityString(ParameterAssignment));
		optionsMap.put(OPTION_ReportFallthroughCase, getSeverityString(FallthroughCase));
		optionsMap.put(OPTION_ReportOverridingMethodWithoutSuperInvocation, getSeverityString(OverridingMethodWithoutSuperInvocation));
		optionsMap.put(OPTION_GenerateClassFiles, this.generateClassFiles ? ENABLED : DISABLED);
		optionsMap.put(OPTION_Process_Annotations, this.processAnnotations ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportRedundantSuperinterface, getSeverityString(RedundantSuperinterface));
		optionsMap.put(OPTION_ReportComparingIdentical, getSeverityString(ComparingIdentical));
		optionsMap.put(OPTION_ReportMissingSynchronizedOnInheritedMethod, getSeverityString(MissingSynchronizedModifierInInheritedMethod));
		optionsMap.put(OPTION_ReportMissingHashCodeMethod, getSeverityString(ShouldImplementHashcode));
		optionsMap.put(OPTION_ReportDeadCode, getSeverityString(DeadCode));
		optionsMap.put(OPTION_ReportDeadCodeInTrivialIfStatement, this.reportDeadCodeInTrivialIfStatement ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportTasks, getSeverityString(Tasks));
		optionsMap.put(OPTION_ReportUnusedObjectAllocation, getSeverityString(UnusedObjectAllocation));
		optionsMap.put(OPTION_IncludeNullInfoFromAsserts, this.includeNullInfoFromAsserts ? ENABLED : DISABLED);
		optionsMap.put(OPTION_ReportMethodCanBeStatic, getSeverityString(MethodCanBeStatic));
		optionsMap.put(OPTION_ReportMethodCanBePotentiallyStatic, getSeverityString(MethodCanBePotentiallyStatic));
		optionsMap.put(OPTION_ReportRedundantSpecificationOfTypeArguments, getSeverityString(RedundantSpecificationOfTypeArguments));
		return optionsMap;
	}

	public int getSeverity(int irritant) {
		if (this.errorThreshold.isSet(irritant)) {
			if ((irritant & (IrritantSet.GROUP_MASK | UnusedWarningToken)) == UnusedWarningToken) {
				return ProblemSeverities.Error | ProblemSeverities.Optional; // cannot be treated as fatal - codegen already occurred
			}
			return this.treatOptionalErrorAsFatal
				? ProblemSeverities.Error | ProblemSeverities.Optional | ProblemSeverities.Fatal
				: ProblemSeverities.Error | ProblemSeverities.Optional;
		}
		if (this.warningThreshold.isSet(irritant)) {
			return ProblemSeverities.Warning | ProblemSeverities.Optional;
		}
		return ProblemSeverities.Ignore;
	}

	public String getSeverityString(int irritant) {
		if(this.errorThreshold.isSet(irritant))
			return ERROR;
		if(this.warningThreshold.isSet(irritant))
			return WARNING;
		return IGNORE;
	}
	public String getVisibilityString(int level) {
		switch (level & ExtraCompilerModifiers.AccVisibilityMASK) {
			case ClassFileConstants.AccPublic:
				return PUBLIC;
			case ClassFileConstants.AccProtected:
				return PROTECTED;
			case ClassFileConstants.AccPrivate:
				return PRIVATE;
			default:
				return DEFAULT;
		}
	}

	public boolean isAnyEnabled(IrritantSet irritants) {
		return this.warningThreshold.isAnySet(irritants) || this.errorThreshold.isAnySet(irritants);
	}

	protected void resetDefaults() {
		// problem default severities defined on IrritantSet
		this.errorThreshold = new IrritantSet(IrritantSet.COMPILER_DEFAULT_ERRORS);
		this.warningThreshold = new IrritantSet(IrritantSet.COMPILER_DEFAULT_WARNINGS);
		
		// by default only lines and source attributes are generated.
		this.produceDebugAttributes = ClassFileConstants.ATTR_SOURCE | ClassFileConstants.ATTR_LINES;
		this.complianceLevel = this.originalComplianceLevel = ClassFileConstants.JDK1_4; // by default be compliant with 1.4
		this.sourceLevel = this.originalSourceLevel = ClassFileConstants.JDK1_3; //1.3 source behavior by default
		this.targetJDK = ClassFileConstants.JDK1_2; // default generates for JVM1.2

		this.defaultEncoding = null; // will use the platform default encoding

		// print what unit is being processed
		this.verbose = Compiler.DEBUG;

		this.produceReferenceInfo = false; // no reference info by default

		// indicates if unused/optimizable local variables need to be preserved (debugging purpose)
		this.preserveAllLocalVariables = false;

		// indicates whether literal expressions are inlined at parse-time or not
		this.parseLiteralExpressionsAsConstants = true;

		// max problems per compilation unit
		this.maxProblemsPerUnit = 100; // no more than 100 problems per default

		// tags used to recognize tasks in comments
		this.taskTags = null;
		this.taskPriorities = null;
		this.isTaskCaseSensitive = true;

		// deprecation report
		this.reportDeprecationInsideDeprecatedCode = false;
		this.reportDeprecationWhenOverridingDeprecatedMethod = false;
		
		// unused parameters report
		this.reportUnusedParameterWhenImplementingAbstract = false;
		this.reportUnusedParameterWhenOverridingConcrete = false;
		this.reportUnusedParameterIncludeDocCommentReference = true;
		
		// unused declaration of thrown exception
		this.reportUnusedDeclaredThrownExceptionWhenOverriding = false;
		this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = true;
		this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = true;
		
		// constructor/setter parameter hiding
		this.reportSpecialParameterHidingField = false;

		this.reportUnavoidableGenericTypeProblems = true;

		// check javadoc comments tags
		this.reportInvalidJavadocTagsVisibility = ClassFileConstants.AccPublic;
		this.reportInvalidJavadocTags = false;
		this.reportInvalidJavadocTagsDeprecatedRef = false;
		this.reportInvalidJavadocTagsNotVisibleRef = false;
		this.reportMissingJavadocTagDescription = RETURN_TAG;
		
		// check missing javadoc tags
		this.reportMissingJavadocTagsVisibility = ClassFileConstants.AccPublic;
		this.reportMissingJavadocTagsOverriding = false;
		this.reportMissingJavadocTagsMethodTypeParameters = false;

		// check missing javadoc comments
		this.reportMissingJavadocCommentsVisibility = ClassFileConstants.AccPublic;
		this.reportMissingJavadocCommentsOverriding = false;
		
		// JSR bytecode inlining
		this.inlineJsrBytecode = false;

		// javadoc comment support
		this.docCommentSupport = false;

		// suppress warning annotation
		this.suppressWarnings = true;

		// suppress also optional errors
		this.suppressOptionalErrors = false;

		// treat optional error as non fatal
		this.treatOptionalErrorAsFatal = false;

		// parser perform statements recovery
		this.performMethodsFullRecovery = true;

		// parser perform statements recovery
		this.performStatementsRecovery = true;

		// store annotations
		this.storeAnnotations = false;

		// annotation processing
		this.generateClassFiles = true;

		// enable annotation processing by default only in batch mode
		this.processAnnotations = false;
		
		// disable missing override annotation reporting for interface method implementation
		this.reportMissingOverrideAnnotationForInterfaceMethodImplementation = true;
		
		// dead code detection
		this.reportDeadCodeInTrivialIfStatement = false;
		
		// ignore method bodies
		this.ignoreMethodBodies = false;
		
		// allow null info from asserts to be considered downstream by default
		this.includeNullInfoFromAsserts = false;
	}

	public void set(Map optionsMap) {
		Object optionValue;
		if ((optionValue = optionsMap.get(OPTION_LocalVariableAttribute)) != null) {
			if (GENERATE.equals(optionValue)) {
				this.produceDebugAttributes |= ClassFileConstants.ATTR_VARS;
			} else if (DO_NOT_GENERATE.equals(optionValue)) {
				this.produceDebugAttributes &= ~ClassFileConstants.ATTR_VARS;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_LineNumberAttribute)) != null) {
			if (GENERATE.equals(optionValue)) {
				this.produceDebugAttributes |= ClassFileConstants.ATTR_LINES;
			} else if (DO_NOT_GENERATE.equals(optionValue)) {
				this.produceDebugAttributes &= ~ClassFileConstants.ATTR_LINES;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_SourceFileAttribute)) != null) {
			if (GENERATE.equals(optionValue)) {
				this.produceDebugAttributes |= ClassFileConstants.ATTR_SOURCE;
			} else if (DO_NOT_GENERATE.equals(optionValue)) {
				this.produceDebugAttributes &= ~ClassFileConstants.ATTR_SOURCE;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_PreserveUnusedLocal)) != null) {
			if (PRESERVE.equals(optionValue)) {
				this.preserveAllLocalVariables = true;
			} else if (OPTIMIZE_OUT.equals(optionValue)) {
				this.preserveAllLocalVariables = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportDeprecationInDeprecatedCode)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportDeprecationInsideDeprecatedCode = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportDeprecationInsideDeprecatedCode = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportDeprecationWhenOverridingDeprecatedMethod)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportDeprecationWhenOverridingDeprecatedMethod = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportDeprecationWhenOverridingDeprecatedMethod = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionWhenOverriding = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionWhenOverriding = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_Compliance)) != null) {
			long level = versionToJdkLevel(optionValue);
			if (level != 0) this.complianceLevel = this.originalComplianceLevel = level;
		}
		if ((optionValue = optionsMap.get(OPTION_Source)) != null) {
			long level = versionToJdkLevel(optionValue);
			if (level != 0) this.sourceLevel = this.originalSourceLevel = level;
		}
		if ((optionValue = optionsMap.get(OPTION_TargetPlatform)) != null) {
			long level = versionToJdkLevel(optionValue);
			if (level != 0) {
				this.targetJDK = level;
			}
			if (this.targetJDK >= ClassFileConstants.JDK1_5) this.inlineJsrBytecode = true; // forced from 1.5 mode on
		}
		if ((optionValue = optionsMap.get(OPTION_Encoding)) != null) {
			if (optionValue instanceof String) {
				this.defaultEncoding = null;
				String stringValue = (String) optionValue;
				if (stringValue.length() > 0){
					try {
						new InputStreamReader(new ByteArrayInputStream(new byte[0]), stringValue);
						this.defaultEncoding = stringValue;
					} catch(UnsupportedEncodingException e){
						// ignore unsupported encoding
					}
				}
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedParameterWhenImplementingAbstract)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedParameterWhenImplementingAbstract = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedParameterWhenImplementingAbstract = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedParameterWhenOverridingConcrete)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedParameterWhenOverridingConcrete = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedParameterWhenOverridingConcrete = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedParameterIncludeDocCommentReference)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnusedParameterIncludeDocCommentReference = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnusedParameterIncludeDocCommentReference = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportSpecialParameterHidingField)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportSpecialParameterHidingField = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportSpecialParameterHidingField = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportUnavoidableGenericTypeProblems)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportUnavoidableGenericTypeProblems = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportUnavoidableGenericTypeProblems = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportDeadCodeInTrivialIfStatement )) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportDeadCodeInTrivialIfStatement = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportDeadCodeInTrivialIfStatement = false;
			}
		}		
		if ((optionValue = optionsMap.get(OPTION_MaxProblemPerUnit)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxProblemsPerUnit = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}
			}
		}
		if ((optionValue = optionsMap.get(OPTION_TaskTags)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				if (stringValue.length() == 0) {
					this.taskTags = null;
				} else {
					this.taskTags = CharOperation.splitAndTrimOn(',', stringValue.toCharArray());
				}
			}
		}
		if ((optionValue = optionsMap.get(OPTION_TaskPriorities)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				if (stringValue.length() == 0) {
					this.taskPriorities = null;
				} else {
					this.taskPriorities = CharOperation.splitAndTrimOn(',', stringValue.toCharArray());
				}
			}
		}
		if ((optionValue = optionsMap.get(OPTION_TaskCaseSensitive)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.isTaskCaseSensitive = true;
			} else if (DISABLED.equals(optionValue)) {
				this.isTaskCaseSensitive = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_InlineJsr)) != null) {
			if (this.targetJDK < ClassFileConstants.JDK1_5) { // only optional if target < 1.5 (inlining on from 1.5 on)
				if (ENABLED.equals(optionValue)) {
					this.inlineJsrBytecode = true;
				} else if (DISABLED.equals(optionValue)) {
					this.inlineJsrBytecode = false;
				}
			}
		}
		if ((optionValue = optionsMap.get(OPTION_SuppressWarnings)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.suppressWarnings = true;
			} else if (DISABLED.equals(optionValue)) {
				this.suppressWarnings = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_SuppressOptionalErrors)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.suppressOptionalErrors = true;
			} else if (DISABLED.equals(optionValue)) {
				this.suppressOptionalErrors = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_FatalOptionalError)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.treatOptionalErrorAsFatal = true;
			} else if (DISABLED.equals(optionValue)) {
				this.treatOptionalErrorAsFatal = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingOverrideAnnotationForInterfaceMethodImplementation)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportMissingOverrideAnnotationForInterfaceMethodImplementation = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportMissingOverrideAnnotationForInterfaceMethodImplementation = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_IncludeNullInfoFromAsserts)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.includeNullInfoFromAsserts = true;
			} else if (DISABLED.equals(optionValue)) {
				this.includeNullInfoFromAsserts = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMethodWithConstructorName)) != null) updateSeverity(MethodWithConstructorName, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportOverridingPackageDefaultMethod)) != null) updateSeverity(OverriddenPackageDefaultMethod, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportDeprecation)) != null) updateSeverity(UsingDeprecatedAPI, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportHiddenCatchBlock)) != null) updateSeverity(MaskedCatchBlock, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedLocal)) != null) updateSeverity(UnusedLocalVariable, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedParameter)) != null) updateSeverity(UnusedArgument, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedImport)) != null) updateSeverity(UnusedImport, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedPrivateMember)) != null) updateSeverity(UnusedPrivateMember, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedDeclaredThrownException)) != null) updateSeverity(UnusedDeclaredThrownException, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportNoImplicitStringConversion)) != null) updateSeverity(NoImplicitStringConversion, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportSyntheticAccessEmulation)) != null) updateSeverity(AccessEmulation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportLocalVariableHiding)) != null) updateSeverity(LocalVariableHiding, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportFieldHiding)) != null) updateSeverity(FieldHiding, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportTypeParameterHiding)) != null) updateSeverity(TypeHiding, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportPossibleAccidentalBooleanAssignment)) != null) updateSeverity(AccidentalBooleanAssign, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportEmptyStatement)) != null) updateSeverity(EmptyStatement, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportNonExternalizedStringLiteral)) != null) updateSeverity(NonExternalizedString, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportAssertIdentifier)) != null) updateSeverity(AssertUsedAsAnIdentifier, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportEnumIdentifier)) != null) updateSeverity(EnumUsedAsAnIdentifier, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportNonStaticAccessToStatic)) != null) updateSeverity(NonStaticAccessToStatic, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportIndirectStaticAccess)) != null) updateSeverity(IndirectStaticAccess, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportIncompatibleNonInheritedInterfaceMethod)) != null) updateSeverity(IncompatibleNonInheritedInterfaceMethod, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUndocumentedEmptyBlock)) != null) updateSeverity(UndocumentedEmptyBlock, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnnecessaryTypeCheck)) != null) updateSeverity(UnnecessaryTypeCheck, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnnecessaryElse)) != null) updateSeverity(UnnecessaryElse, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportFinallyBlockNotCompletingNormally)) != null) updateSeverity(FinallyBlockNotCompleting, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnqualifiedFieldAccess)) != null) updateSeverity(UnqualifiedFieldAccess, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportNoEffectAssignment)) != null) updateSeverity(NoEffectAssignment, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUncheckedTypeOperation)) != null) updateSeverity(UncheckedTypeOperation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportRawTypeReference)) != null) updateSeverity(RawTypeReference, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportFinalParameterBound)) != null) updateSeverity(FinalParameterBound, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMissingSerialVersion)) != null) updateSeverity(MissingSerialVersion, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportForbiddenReference)) != null) updateSeverity(ForbiddenReference, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportDiscouragedReference)) != null) updateSeverity(DiscouragedReference, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportVarargsArgumentNeedCast)) != null) updateSeverity(VarargsArgumentNeedCast, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportNullReference)) != null) updateSeverity(NullReference, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportPotentialNullReference)) != null) updateSeverity(PotentialNullReference, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportRedundantNullCheck)) != null) updateSeverity(RedundantNullCheck, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportAutoboxing)) != null) updateSeverity(AutoBoxing, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportAnnotationSuperInterface)) != null) updateSeverity(AnnotationSuperInterface, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMissingOverrideAnnotation)) != null) updateSeverity(MissingOverrideAnnotation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMissingDeprecatedAnnotation)) != null) updateSeverity(MissingDeprecatedAnnotation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportIncompleteEnumSwitch)) != null) updateSeverity(IncompleteEnumSwitch, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnhandledWarningToken)) != null) updateSeverity(UnhandledWarningToken, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedWarningToken)) != null) updateSeverity(UnusedWarningToken, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedLabel)) != null) updateSeverity(UnusedLabel, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportParameterAssignment)) != null) updateSeverity(ParameterAssignment, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportFallthroughCase)) != null) updateSeverity(FallthroughCase, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportOverridingMethodWithoutSuperInvocation)) != null) updateSeverity(OverridingMethodWithoutSuperInvocation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedTypeArgumentsForMethodInvocation)) != null) updateSeverity(UnusedTypeArguments, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportRedundantSuperinterface)) != null) updateSeverity(RedundantSuperinterface, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportComparingIdentical)) != null) updateSeverity(ComparingIdentical, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMissingSynchronizedOnInheritedMethod)) != null) updateSeverity(MissingSynchronizedModifierInInheritedMethod, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMissingHashCodeMethod)) != null) updateSeverity(ShouldImplementHashcode, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportDeadCode)) != null) updateSeverity(DeadCode, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportTasks)) != null) updateSeverity(Tasks, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportUnusedObjectAllocation)) != null) updateSeverity(UnusedObjectAllocation, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMethodCanBeStatic)) != null) updateSeverity(MethodCanBeStatic, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportMethodCanBePotentiallyStatic)) != null) updateSeverity(MethodCanBePotentiallyStatic, optionValue);
		if ((optionValue = optionsMap.get(OPTION_ReportRedundantSpecificationOfTypeArguments)) != null) updateSeverity(RedundantSpecificationOfTypeArguments, optionValue);

		// Javadoc options
		if ((optionValue = optionsMap.get(OPTION_DocCommentSupport)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.docCommentSupport = true;
			} else if (DISABLED.equals(optionValue)) {
				this.docCommentSupport = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportInvalidJavadoc)) != null) {
			updateSeverity(InvalidJavadoc, optionValue);
		}
		if ( (optionValue = optionsMap.get(OPTION_ReportInvalidJavadocTagsVisibility)) != null) {
			if (PUBLIC.equals(optionValue)) {
				this.reportInvalidJavadocTagsVisibility = ClassFileConstants.AccPublic;
			} else if (PROTECTED.equals(optionValue)) {
				this.reportInvalidJavadocTagsVisibility = ClassFileConstants.AccProtected;
			} else if (DEFAULT.equals(optionValue)) {
				this.reportInvalidJavadocTagsVisibility = ClassFileConstants.AccDefault;
			} else if (PRIVATE.equals(optionValue)) {
				this.reportInvalidJavadocTagsVisibility = ClassFileConstants.AccPrivate;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportInvalidJavadocTags)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportInvalidJavadocTags = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportInvalidJavadocTags = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportInvalidJavadocTagsDeprecatedRef)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportInvalidJavadocTagsDeprecatedRef = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportInvalidJavadocTagsDeprecatedRef = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportInvalidJavadocTagsNotVisibleRef)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportInvalidJavadocTagsNotVisibleRef = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportInvalidJavadocTagsNotVisibleRef = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocTags)) != null) {
			updateSeverity(MissingJavadocTags, optionValue);
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocTagsVisibility)) != null) {
			if (PUBLIC.equals(optionValue)) {
				this.reportMissingJavadocTagsVisibility = ClassFileConstants.AccPublic;
			} else if (PROTECTED.equals(optionValue)) {
				this.reportMissingJavadocTagsVisibility = ClassFileConstants.AccProtected;
			} else if (DEFAULT.equals(optionValue)) {
				this.reportMissingJavadocTagsVisibility = ClassFileConstants.AccDefault;
			} else if (PRIVATE.equals(optionValue)) {
				this.reportMissingJavadocTagsVisibility = ClassFileConstants.AccPrivate;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocTagsOverriding)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportMissingJavadocTagsOverriding = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportMissingJavadocTagsOverriding = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocTagsMethodTypeParameters)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportMissingJavadocTagsMethodTypeParameters = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportMissingJavadocTagsMethodTypeParameters = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocComments)) != null) {
			updateSeverity(MissingJavadocComments, optionValue);
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocTagDescription)) != null) {
			this.reportMissingJavadocTagDescription = (String) optionValue;
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocCommentsVisibility)) != null) {
			if (PUBLIC.equals(optionValue)) {
				this.reportMissingJavadocCommentsVisibility = ClassFileConstants.AccPublic;
			} else if (PROTECTED.equals(optionValue)) {
				this.reportMissingJavadocCommentsVisibility = ClassFileConstants.AccProtected;
			} else if (DEFAULT.equals(optionValue)) {
				this.reportMissingJavadocCommentsVisibility = ClassFileConstants.AccDefault;
			} else if (PRIVATE.equals(optionValue)) {
				this.reportMissingJavadocCommentsVisibility = ClassFileConstants.AccPrivate;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_ReportMissingJavadocCommentsOverriding)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.reportMissingJavadocCommentsOverriding = true;
			} else if (DISABLED.equals(optionValue)) {
				this.reportMissingJavadocCommentsOverriding = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_GenerateClassFiles)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.generateClassFiles = true;
			} else if (DISABLED.equals(optionValue)) {
				this.generateClassFiles = false;
			}
		}
		if ((optionValue = optionsMap.get(OPTION_Process_Annotations)) != null) {
			if (ENABLED.equals(optionValue)) {
				this.processAnnotations = true;
				this.storeAnnotations = true; // annotation processing requires annotation to be stored
			} else if (DISABLED.equals(optionValue)) {
				this.processAnnotations = false;
				this.storeAnnotations = false;
			}
		}
	}
	public String toString() {
		StringBuffer buf = new StringBuffer("CompilerOptions:"); //$NON-NLS-1$
		buf.append("\n\t- local variables debug attributes: ").append((this.produceDebugAttributes & ClassFileConstants.ATTR_VARS) != 0 ? "ON" : " OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- line number debug attributes: ").append((this.produceDebugAttributes & ClassFileConstants.ATTR_LINES) != 0 ? "ON" : " OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- source debug attributes: ").append((this.produceDebugAttributes & ClassFileConstants.ATTR_SOURCE) != 0 ? "ON" : " OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- preserve all local variables: ").append(this.preserveAllLocalVariables ? "ON" : " OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- method with constructor name: ").append(getSeverityString(MethodWithConstructorName)); //$NON-NLS-1$
		buf.append("\n\t- overridden package default method: ").append(getSeverityString(OverriddenPackageDefaultMethod)); //$NON-NLS-1$
		buf.append("\n\t- deprecation: ").append(getSeverityString(UsingDeprecatedAPI)); //$NON-NLS-1$
		buf.append("\n\t- masked catch block: ").append(getSeverityString(MaskedCatchBlock)); //$NON-NLS-1$
		buf.append("\n\t- unused local variable: ").append(getSeverityString(UnusedLocalVariable)); //$NON-NLS-1$
		buf.append("\n\t- unused parameter: ").append(getSeverityString(UnusedArgument)); //$NON-NLS-1$
		buf.append("\n\t- unused import: ").append(getSeverityString(UnusedImport)); //$NON-NLS-1$
		buf.append("\n\t- synthetic access emulation: ").append(getSeverityString(AccessEmulation)); //$NON-NLS-1$
		buf.append("\n\t- assignment with no effect: ").append(getSeverityString(NoEffectAssignment)); //$NON-NLS-1$
		buf.append("\n\t- non externalized string: ").append(getSeverityString(NonExternalizedString)); //$NON-NLS-1$
		buf.append("\n\t- static access receiver: ").append(getSeverityString(NonStaticAccessToStatic)); //$NON-NLS-1$
		buf.append("\n\t- indirect static access: ").append(getSeverityString(IndirectStaticAccess)); //$NON-NLS-1$
		buf.append("\n\t- incompatible non inherited interface method: ").append(getSeverityString(IncompatibleNonInheritedInterfaceMethod)); //$NON-NLS-1$
		buf.append("\n\t- unused private member: ").append(getSeverityString(UnusedPrivateMember)); //$NON-NLS-1$
		buf.append("\n\t- local variable hiding another variable: ").append(getSeverityString(LocalVariableHiding)); //$NON-NLS-1$
		buf.append("\n\t- field hiding another variable: ").append(getSeverityString(FieldHiding)); //$NON-NLS-1$
		buf.append("\n\t- type hiding another type: ").append(getSeverityString(TypeHiding)); //$NON-NLS-1$
		buf.append("\n\t- possible accidental boolean assignment: ").append(getSeverityString(AccidentalBooleanAssign)); //$NON-NLS-1$
		buf.append("\n\t- superfluous semicolon: ").append(getSeverityString(EmptyStatement)); //$NON-NLS-1$
		buf.append("\n\t- uncommented empty block: ").append(getSeverityString(UndocumentedEmptyBlock)); //$NON-NLS-1$
		buf.append("\n\t- unnecessary type check: ").append(getSeverityString(UnnecessaryTypeCheck)); //$NON-NLS-1$
		buf.append("\n\t- javadoc comment support: ").append(this.docCommentSupport ? "ON" : " OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t\t+ invalid javadoc: ").append(getSeverityString(InvalidJavadoc)); //$NON-NLS-1$
		buf.append("\n\t\t+ report invalid javadoc tags: ").append(this.reportInvalidJavadocTags ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t\t\t* deprecated references: ").append(this.reportInvalidJavadocTagsDeprecatedRef ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t\t\t* not visible references: ").append(this.reportInvalidJavadocTagsNotVisibleRef ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t\t+ visibility level to report invalid javadoc tags: ").append(getVisibilityString(this.reportInvalidJavadocTagsVisibility)); //$NON-NLS-1$
		buf.append("\n\t\t+ missing javadoc tags: ").append(getSeverityString(MissingJavadocTags)); //$NON-NLS-1$
		buf.append("\n\t\t+ visibility level to report missing javadoc tags: ").append(getVisibilityString(this.reportMissingJavadocTagsVisibility)); //$NON-NLS-1$
		buf.append("\n\t\t+ report missing javadoc tags for method type parameters: ").append(this.reportMissingJavadocTagsMethodTypeParameters ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t\t+ report missing javadoc tags in overriding methods: ").append(this.reportMissingJavadocTagsOverriding ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t\t+ missing javadoc comments: ").append(getSeverityString(MissingJavadocComments)); //$NON-NLS-1$
		buf.append("\n\t\t+ report missing tag description option: ").append(this.reportMissingJavadocTagDescription); //$NON-NLS-1$
		buf.append("\n\t\t+ visibility level to report missing javadoc comments: ").append(getVisibilityString(this.reportMissingJavadocCommentsVisibility)); //$NON-NLS-1$
		buf.append("\n\t\t+ report missing javadoc comments in overriding methods: ").append(this.reportMissingJavadocCommentsOverriding ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- finally block not completing normally: ").append(getSeverityString(FinallyBlockNotCompleting)); //$NON-NLS-1$
		buf.append("\n\t- report unused declared thrown exception: ").append(getSeverityString(UnusedDeclaredThrownException)); //$NON-NLS-1$
		buf.append("\n\t- report unused declared thrown exception when overriding: ").append(this.reportUnusedDeclaredThrownExceptionWhenOverriding ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unused declared thrown exception include doc comment reference: ").append(this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unused declared thrown exception exempt exception and throwable: ").append(this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- unnecessary else: ").append(getSeverityString(UnnecessaryElse)); //$NON-NLS-1$
		buf.append("\n\t- JDK compliance level: "+ versionFromJdkLevel(this.complianceLevel)); //$NON-NLS-1$
		buf.append("\n\t- JDK source level: "+ versionFromJdkLevel(this.sourceLevel)); //$NON-NLS-1$
		buf.append("\n\t- JDK target level: "+ versionFromJdkLevel(this.targetJDK)); //$NON-NLS-1$
		buf.append("\n\t- verbose : ").append(this.verbose ? "ON" : "OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- produce reference info : ").append(this.produceReferenceInfo ? "ON" : "OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- parse literal expressions as constants : ").append(this.parseLiteralExpressionsAsConstants ? "ON" : "OFF"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n\t- encoding : ").append(this.defaultEncoding == null ? "<default>" : this.defaultEncoding); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n\t- task tags: ").append(this.taskTags == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskTags,',')));  //$NON-NLS-1$
		buf.append("\n\t- task priorities : ").append(this.taskPriorities == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskPriorities,','))); //$NON-NLS-1$
		buf.append("\n\t- report deprecation inside deprecated code : ").append(this.reportDeprecationInsideDeprecatedCode ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report deprecation when overriding deprecated method : ").append(this.reportDeprecationWhenOverridingDeprecatedMethod ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unused parameter when implementing abstract method : ").append(this.reportUnusedParameterWhenImplementingAbstract ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unused parameter when overriding concrete method : ").append(this.reportUnusedParameterWhenOverridingConcrete ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unused parameter include doc comment reference : ").append(this.reportUnusedParameterIncludeDocCommentReference ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report constructor/setter parameter hiding existing field : ").append(this.reportSpecialParameterHidingField ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- inline JSR bytecode : ").append(this.inlineJsrBytecode ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- report unavoidable generic type problems : ").append(this.reportUnavoidableGenericTypeProblems ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- unsafe type operation: ").append(getSeverityString(UncheckedTypeOperation)); //$NON-NLS-1$
		buf.append("\n\t- unsafe raw type: ").append(getSeverityString(RawTypeReference)); //$NON-NLS-1$
		buf.append("\n\t- final bound for type parameter: ").append(getSeverityString(FinalParameterBound)); //$NON-NLS-1$
		buf.append("\n\t- missing serialVersionUID: ").append(getSeverityString(MissingSerialVersion)); //$NON-NLS-1$
		buf.append("\n\t- varargs argument need cast: ").append(getSeverityString(VarargsArgumentNeedCast)); //$NON-NLS-1$
		buf.append("\n\t- forbidden reference to type with access restriction: ").append(getSeverityString(ForbiddenReference)); //$NON-NLS-1$
		buf.append("\n\t- discouraged reference to type with access restriction: ").append(getSeverityString(DiscouragedReference)); //$NON-NLS-1$
		buf.append("\n\t- null reference: ").append(getSeverityString(NullReference)); //$NON-NLS-1$
		buf.append("\n\t- potential null reference: ").append(getSeverityString(PotentialNullReference)); //$NON-NLS-1$
		buf.append("\n\t- redundant null check: ").append(getSeverityString(RedundantNullCheck)); //$NON-NLS-1$
		buf.append("\n\t- autoboxing: ").append(getSeverityString(AutoBoxing)); //$NON-NLS-1$
		buf.append("\n\t- annotation super interface: ").append(getSeverityString(AnnotationSuperInterface)); //$NON-NLS-1$
		buf.append("\n\t- missing @Override annotation: ").append(getSeverityString(MissingOverrideAnnotation)); //$NON-NLS-1$
		buf.append("\n\t- missing @Override annotation for interface method implementation: ").append(this.reportMissingOverrideAnnotationForInterfaceMethodImplementation ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- missing @Deprecated annotation: ").append(getSeverityString(MissingDeprecatedAnnotation)); //$NON-NLS-1$
		buf.append("\n\t- incomplete enum switch: ").append(getSeverityString(IncompleteEnumSwitch)); //$NON-NLS-1$
		buf.append("\n\t- raise null related warnings for variables tainted in assert statements: ").append(this.includeNullInfoFromAsserts ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- suppress warnings: ").append(this.suppressWarnings ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- suppress optional errors: ").append(this.suppressOptionalErrors ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- unhandled warning token: ").append(getSeverityString(UnhandledWarningToken)); //$NON-NLS-1$
		buf.append("\n\t- unused warning token: ").append(getSeverityString(UnusedWarningToken)); //$NON-NLS-1$
		buf.append("\n\t- unused label: ").append(getSeverityString(UnusedLabel)); //$NON-NLS-1$
		buf.append("\n\t- treat optional error as fatal: ").append(this.treatOptionalErrorAsFatal ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- parameter assignment: ").append(getSeverityString(ParameterAssignment)); //$NON-NLS-1$
		buf.append("\n\t- generate class files: ").append(this.generateClassFiles ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- process annotations: ").append(this.processAnnotations ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- unused type arguments for method/constructor invocation: ").append(getSeverityString(UnusedTypeArguments)); //$NON-NLS-1$
		buf.append("\n\t- redundant superinterface: ").append(getSeverityString(RedundantSuperinterface)); //$NON-NLS-1$
		buf.append("\n\t- comparing identical expr: ").append(getSeverityString(ComparingIdentical)); //$NON-NLS-1$
		buf.append("\n\t- missing synchronized on inherited method: ").append(getSeverityString(MissingSynchronizedModifierInInheritedMethod)); //$NON-NLS-1$
		buf.append("\n\t- should implement hashCode() method: ").append(getSeverityString(ShouldImplementHashcode)); //$NON-NLS-1$
		buf.append("\n\t- dead code: ").append(getSeverityString(DeadCode)); //$NON-NLS-1$
		buf.append("\n\t- dead code in trivial if statement: ").append(this.reportDeadCodeInTrivialIfStatement ? ENABLED : DISABLED); //$NON-NLS-1$
		buf.append("\n\t- tasks severity: ").append(getSeverityString(Tasks)); //$NON-NLS-1$
		buf.append("\n\t- unused object allocation: ").append(getSeverityString(UnusedObjectAllocation)); //$NON-NLS-1$
		buf.append("\n\t- method can be static: ").append(getSeverityString(MethodCanBeStatic)); //$NON-NLS-1$
		buf.append("\n\t- method can be potentially static: ").append(getSeverityString(MethodCanBePotentiallyStatic)); //$NON-NLS-1$
		buf.append("\n\t- redundant specification of type arguments: ").append(getSeverityString(RedundantSpecificationOfTypeArguments)); //$NON-NLS-1$
		return buf.toString();
	}
	
	protected void updateSeverity(int irritant, Object severityString) {
		if (ERROR.equals(severityString)) {
			this.errorThreshold.set(irritant);
			this.warningThreshold.clear(irritant);
		} else if (WARNING.equals(severityString)) {
			this.errorThreshold.clear(irritant);
			this.warningThreshold.set(irritant);
		} else if (IGNORE.equals(severityString)) {
			this.errorThreshold.clear(irritant);
			this.warningThreshold.clear(irritant);
		}
	}
}
