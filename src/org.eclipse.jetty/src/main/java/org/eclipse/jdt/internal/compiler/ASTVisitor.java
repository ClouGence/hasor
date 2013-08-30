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
package org.eclipse.jdt.internal.compiler;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

/**
 * A visitor for iterating through the parse tree.
 */
public abstract class ASTVisitor {
	public void acceptProblem(IProblem problem) {
		// do nothing by default
	}
	public void endVisit(
		AllocationExpression allocationExpression,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			AnnotationMethodDeclaration annotationTypeDeclaration,
			ClassScope classScope) {
			// do nothing by default
	}
	public void endVisit(Argument argument, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Argument argument,ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		ArrayAllocationExpression arrayAllocationExpression,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ArrayInitializer arrayInitializer, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		ArrayQualifiedTypeReference arrayQualifiedTypeReference,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		ArrayQualifiedTypeReference arrayQualifiedTypeReference,
		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(ArrayReference arrayReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(AssertStatement assertStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Assignment assignment, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(BinaryExpression binaryExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Block block, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(BreakStatement breakStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(CaseStatement caseStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(CastExpression castExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(CharLiteral charLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ClassLiteralAccess classLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Clinit clinit, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
		CompilationUnitDeclaration compilationUnitDeclaration,
		CompilationUnitScope scope) {
		// do nothing by default
	}
	public void endVisit(CompoundAssignment compoundAssignment, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			ConditionalExpression conditionalExpression,
			BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		ConstructorDeclaration constructorDeclaration,
		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(ContinueStatement continueStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(DoStatement doStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(DoubleLiteral doubleLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(EmptyStatement emptyStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(EqualExpression equalExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		ExplicitConstructorCall explicitConstructor,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		ExtendedStringLiteral extendedStringLiteral,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(FalseLiteral falseLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		// do nothing by default
	}
	public void endVisit(FieldReference fieldReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(FieldReference fieldReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(FloatLiteral floatLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ForeachStatement forStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ForStatement forStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(IfStatement ifStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ImportReference importRef, CompilationUnitScope scope) {
		// do nothing by default
	}
	public void endVisit(Initializer initializer, MethodScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		InstanceOfExpression instanceOfExpression,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(IntLiteral intLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Javadoc javadoc, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Javadoc javadoc, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocAllocationExpression expression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocAllocationExpression expression, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArgumentExpression expression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArgumentExpression expression, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArraySingleTypeReference typeRef, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocArraySingleTypeReference typeRef, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocFieldReference fieldRef, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocFieldReference fieldRef, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocMessageSend messageSend, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocMessageSend messageSend, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocQualifiedTypeReference typeRef, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocQualifiedTypeReference typeRef, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocReturnStatement statement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocReturnStatement statement, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocSingleNameReference argument, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocSingleNameReference argument, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocSingleTypeReference typeRef, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(JavadocSingleTypeReference typeRef, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(LabeledStatement labeledStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(LocalDeclaration localDeclaration, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(LongLiteral longLiteral, BlockScope scope) {
		// do nothing by default
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public void endVisit(MarkerAnnotation annotation, BlockScope scope) {
		// do nothing by default
	}
	/**
	 * @param pair
	 * @param scope
	 */
	public void endVisit(MemberValuePair pair, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(MessageSend messageSend, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(StringLiteralConcatenation literal, BlockScope scope) {
		// do nothing by default
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public void endVisit(NormalAnnotation annotation, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(NullLiteral nullLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(PostfixExpression postfixExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(PrefixExpression prefixExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedAllocationExpression qualifiedAllocationExpression,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		QualifiedNameReference qualifiedNameReference,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			QualifiedNameReference qualifiedNameReference,
			ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedSuperReference qualifiedSuperReference,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedSuperReference qualifiedSuperReference,
    		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedThisReference qualifiedThisReference,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedThisReference qualifiedThisReference,
    		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedTypeReference qualifiedTypeReference,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		QualifiedTypeReference qualifiedTypeReference,
    		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(ReturnStatement returnStatement, BlockScope scope) {
		// do nothing by default
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public void endVisit(SingleMemberAnnotation annotation, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		SingleNameReference singleNameReference,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			SingleNameReference singleNameReference,
			ClassScope scope) {
			// do nothing by default
	}
	public void endVisit(
    		SingleTypeReference singleTypeReference,
    		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
    		SingleTypeReference singleTypeReference,
    		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(StringLiteral stringLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(SuperReference superReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(SwitchStatement switchStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		SynchronizedStatement synchronizedStatement,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ThisReference thisReference, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(ThisReference thisReference, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(ThrowStatement throwStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(TrueLiteral trueLiteral, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(TryStatement tryStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		TypeDeclaration localTypeDeclaration,
		BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
		TypeDeclaration memberTypeDeclaration,
		ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(
		TypeDeclaration typeDeclaration,
		CompilationUnitScope scope) {
		// do nothing by default
	}
	public void endVisit(TypeParameter typeParameter, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(TypeParameter typeParameter, ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(UnaryExpression unaryExpression, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			UnionTypeReference unionTypeReference,
			BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(
			UnionTypeReference unionTypeReference,
			ClassScope scope) {
		// do nothing by default
	}
	public void endVisit(WhileStatement whileStatement, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Wildcard wildcard, BlockScope scope) {
		// do nothing by default
	}
	public void endVisit(Wildcard wildcard, ClassScope scope) {
		// do nothing by default
	}
	public boolean visit(
    		AllocationExpression allocationExpression,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(AND_AND_Expression and_and_Expression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			AnnotationMethodDeclaration annotationTypeDeclaration,
			ClassScope classScope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Argument argument, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Argument argument, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ArrayAllocationExpression arrayAllocationExpression,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ArrayInitializer arrayInitializer, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ArrayQualifiedTypeReference arrayQualifiedTypeReference,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ArrayQualifiedTypeReference arrayQualifiedTypeReference,
		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ArrayReference arrayReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(AssertStatement assertStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Assignment assignment, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(BinaryExpression binaryExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Block block, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(BreakStatement breakStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(CaseStatement caseStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(CastExpression castExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(CharLiteral charLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Clinit clinit, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		CompilationUnitDeclaration compilationUnitDeclaration,
		CompilationUnitScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		ConditionalExpression conditionalExpression,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ConstructorDeclaration constructorDeclaration,
		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ContinueStatement continueStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(DoStatement doStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(DoubleLiteral doubleLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(EmptyStatement emptyStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(EqualExpression equalExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ExplicitConstructorCall explicitConstructor,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		ExtendedStringLiteral extendedStringLiteral,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(FalseLiteral falseLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(FieldReference fieldReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(FieldReference fieldReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(FloatLiteral floatLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ForeachStatement forStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ForStatement forStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(IfStatement ifStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ImportReference importRef, CompilationUnitScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Initializer initializer, MethodScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		InstanceOfExpression instanceOfExpression,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(IntLiteral intLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Javadoc javadoc, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Javadoc javadoc, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocAllocationExpression expression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocAllocationExpression expression, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArgumentExpression expression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArgumentExpression expression, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArrayQualifiedTypeReference typeRef, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArrayQualifiedTypeReference typeRef, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArraySingleTypeReference typeRef, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocArraySingleTypeReference typeRef, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocFieldReference fieldRef, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocFieldReference fieldRef, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocImplicitTypeReference implicitTypeReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocImplicitTypeReference implicitTypeReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocMessageSend messageSend, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocMessageSend messageSend, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocQualifiedTypeReference typeRef, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocQualifiedTypeReference typeRef, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocReturnStatement statement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocReturnStatement statement, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocSingleNameReference argument, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocSingleNameReference argument, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocSingleTypeReference typeRef, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(JavadocSingleTypeReference typeRef, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(LabeledStatement labeledStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(LocalDeclaration localDeclaration, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(LongLiteral longLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public boolean visit(MarkerAnnotation annotation, BlockScope scope) {
		return true;
	}
	/**
	 * @param pair
	 * @param scope
	 * @since 3.1
	 */
	public boolean visit(MemberValuePair pair, BlockScope scope) {
		return true;
	}
	public boolean visit(MessageSend messageSend, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			StringLiteralConcatenation literal,
			BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public boolean visit(NormalAnnotation annotation, BlockScope scope) {
		return true;
	}
	public boolean visit(NullLiteral nullLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(OR_OR_Expression or_or_Expression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		QualifiedAllocationExpression qualifiedAllocationExpression,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			QualifiedNameReference qualifiedNameReference,
			BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			QualifiedNameReference qualifiedNameReference,
			ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		QualifiedSuperReference qualifiedSuperReference,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		QualifiedSuperReference qualifiedSuperReference,
    		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			QualifiedThisReference qualifiedThisReference,
			BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			QualifiedThisReference qualifiedThisReference,
			ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		QualifiedTypeReference qualifiedTypeReference,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		QualifiedTypeReference qualifiedTypeReference,
    		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ReturnStatement returnStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	/**
	 * @param annotation
	 * @param scope
	 * @since 3.1
	 */
	public boolean visit(SingleMemberAnnotation annotation, BlockScope scope) {
		return true;
	}
	public boolean visit(
		SingleNameReference singleNameReference,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			SingleNameReference singleNameReference,
			ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		SingleTypeReference singleTypeReference,
    		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
    		SingleTypeReference singleTypeReference,
    		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(StringLiteral stringLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(SuperReference superReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(SwitchStatement switchStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		SynchronizedStatement synchronizedStatement,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ThisReference thisReference, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ThisReference thisReference, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(TrueLiteral trueLiteral, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(TryStatement tryStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		TypeDeclaration localTypeDeclaration,
		BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		TypeDeclaration memberTypeDeclaration,
		ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
		TypeDeclaration typeDeclaration,
		CompilationUnitScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(TypeParameter typeParameter, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(TypeParameter typeParameter, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(UnaryExpression unaryExpression, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			UnionTypeReference unionTypeReference,
			BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(
			UnionTypeReference unionTypeReference,
			ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(WhileStatement whileStatement, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Wildcard wildcard, BlockScope scope) {
		return true; // do nothing by default, keep traversing
	}
	public boolean visit(Wildcard wildcard, ClassScope scope) {
		return true; // do nothing by default, keep traversing
	}
}
