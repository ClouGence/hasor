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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class ClassLiteralAccess extends Expression {

	public TypeReference type;
	public TypeBinding targetType;
	FieldBinding syntheticField;

	public ClassLiteralAccess(int sourceEnd, TypeReference type) {
		this.type = type;
		type.bits |= IgnoreRawTypeCheck; // no need to worry about raw type usage
		this.sourceStart = type.sourceStart;
		this.sourceEnd = sourceEnd;
	}

	public FlowInfo analyseCode(
		BlockScope currentScope,
		FlowContext flowContext,
		FlowInfo flowInfo) {

		// if reachable, request the addition of a synthetic field for caching the class descriptor
		SourceTypeBinding sourceType = currentScope.outerMostClassScope().enclosingSourceType();
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=22334
		if (!sourceType.isInterface()
				&& !this.targetType.isBaseType()
				&& currentScope.compilerOptions().targetJDK < ClassFileConstants.JDK1_5) {
			this.syntheticField = sourceType.addSyntheticFieldForClassLiteral(this.targetType, currentScope);
		}
		return flowInfo;
	}

	/**
	 * MessageSendDotClass code generation
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 * @param valueRequired boolean
	 */
	public void generateCode(
		BlockScope currentScope,
		CodeStream codeStream,
		boolean valueRequired) {
		int pc = codeStream.position;

		// in interface case, no caching occurs, since cannot make a cache field for interface
		if (valueRequired) {
			codeStream.generateClassLiteralAccessForType(this.type.resolvedType, this.syntheticField);
			codeStream.generateImplicitConversion(this.implicitConversion);
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

	public StringBuffer printExpression(int indent, StringBuffer output) {

		return this.type.print(0, output).append(".class"); //$NON-NLS-1$
	}

	public TypeBinding resolveType(BlockScope scope) {

		this.constant = Constant.NotAConstant;
		if ((this.targetType = this.type.resolveType(scope, true /* check bounds*/)) == null)
			return null;
		
		/* https://bugs.eclipse.org/bugs/show_bug.cgi?id=320463
		   https://bugs.eclipse.org/bugs/show_bug.cgi?id=312076
		   JLS3 15.8.2 forbids the type named in the class literal expression from being a parameterized type.
		   And the grammar in 18.1 disallows (where X and Y are some concrete types) constructs of the form
		   Outer<X>.class, Outer<X>.Inner.class, Outer.Inner<X>.class, Outer<X>.Inner<Y>.class etc.
		   Corollary wise, we should resolve the type of the class literal expression to be a raw type as
		   class literals exist only for the raw underlying type. 
		 */
		this.targetType = scope.environment().convertToRawType(this.targetType, true /* force conversion of enclosing types*/);

		if (this.targetType.isArrayType()) {
			ArrayBinding arrayBinding = (ArrayBinding) this.targetType;
			TypeBinding leafComponentType = arrayBinding.leafComponentType;
			if (leafComponentType == TypeBinding.VOID) {
				scope.problemReporter().cannotAllocateVoidArray(this);
				return null;
			} else if (leafComponentType.isTypeVariable()) {
				scope.problemReporter().illegalClassLiteralForTypeVariable((TypeVariableBinding)leafComponentType, this);
			}
		} else if (this.targetType.isTypeVariable()) {
			scope.problemReporter().illegalClassLiteralForTypeVariable((TypeVariableBinding)this.targetType, this);
		}
		ReferenceBinding classType = scope.getJavaLangClass();
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=328689
		if (scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
			// Integer.class --> Class<Integer>, perform boxing of base types (int.class --> Class<Integer>)
			TypeBinding boxedType = null;
			if (this.targetType.id == T_void) {
				boxedType = scope.environment().getResolvedType(JAVA_LANG_VOID, scope);
			} else {
				boxedType = scope.boxing(this.targetType);
			}
			this.resolvedType = scope.environment().createParameterizedType(classType, new TypeBinding[]{ boxedType }, null/*not a member*/);
		} else {
			this.resolvedType = classType;
		}
		return this.resolvedType;
	}

	public void traverse(
		ASTVisitor visitor,
		BlockScope blockScope) {

		if (visitor.visit(this, blockScope)) {
			this.type.traverse(visitor, blockScope);
		}
		visitor.endVisit(this, blockScope);
	}
}
