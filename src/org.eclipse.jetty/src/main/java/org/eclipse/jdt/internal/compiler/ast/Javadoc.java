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

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants;

/**
 * Node representing a structured Javadoc comment
 */
public class Javadoc extends ASTNode {

	public JavadocSingleNameReference[] paramReferences; // @param
	public JavadocSingleTypeReference[] paramTypeParameters; // @param
	public TypeReference[] exceptionReferences; // @throws, @exception
	public JavadocReturnStatement returnStatement; // @return
	public Expression[] seeReferences; // @see
	public long[] inheritedPositions = null;
	// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=51600
	// Store param references for tag with invalid syntax
	public JavadocSingleNameReference[] invalidParameters; // @param
	// bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=153399
	// Store value tag positions
	public long valuePositions = -1;

	public Javadoc(int sourceStart, int sourceEnd) {
		this.sourceStart = sourceStart;
		this.sourceEnd = sourceEnd;
		this.bits |= ASTNode.ResolveJavadoc;
	}
	/**
	 * Returns whether a type can be seen at a given visibility level or not.
	 *
	 * @param visibility Level of visiblity allowed to see references
	 * @param modifiers modifiers of java element to be seen
	 * @return true if the type can be seen, false otherwise
	 */
	boolean canBeSeen(int visibility, int modifiers) {
		if (modifiers < 0) return true;
		switch (modifiers & ExtraCompilerModifiers.AccVisibilityMASK) {
			case ClassFileConstants.AccPublic :
				return true;
			case ClassFileConstants.AccProtected:
				return (visibility != ClassFileConstants.AccPublic);
			case ClassFileConstants.AccDefault:
				return (visibility == ClassFileConstants.AccDefault || visibility == ClassFileConstants.AccPrivate);
			case ClassFileConstants.AccPrivate:
				return (visibility == ClassFileConstants.AccPrivate);
		}
		return true;
	}

	/*
	 * Search node with a given staring position in javadoc objects arrays.
	 */
	public ASTNode getNodeStartingAt(int start) {
		int length = 0;
		// parameters array
		if (this.paramReferences != null) {
			length = this.paramReferences.length;
			for (int i=0; i<length; i++) {
				JavadocSingleNameReference param = this.paramReferences[i];
				if (param.sourceStart==start) {
					return param;
				}
			}
		}
		// array of invalid syntax tags parameters
		if (this.invalidParameters != null) {
			length = this.invalidParameters.length;
			for (int i=0; i<length; i++) {
				JavadocSingleNameReference param = this.invalidParameters[i];
				if (param.sourceStart==start) {
					return param;
				}
			}
		}
		// type parameters array
		if (this.paramTypeParameters != null) {
			length = this.paramTypeParameters.length;
			for (int i=0; i<length; i++) {
				JavadocSingleTypeReference param = this.paramTypeParameters[i];
				if (param.sourceStart==start) {
					return param;
				}
			}
		}
		// thrown exception array
		if (this.exceptionReferences != null) {
			length = this.exceptionReferences.length;
			for (int i=0; i<length; i++) {
				TypeReference typeRef = this.exceptionReferences[i];
				if (typeRef.sourceStart==start) {
					return typeRef;
				}
			}
		}
		// references array
		if (this.seeReferences != null) {
			length = this.seeReferences.length;
			for (int i=0; i<length; i++) {
				org.eclipse.jdt.internal.compiler.ast.Expression expression = this.seeReferences[i];
				if (expression.sourceStart==start) {
					return expression;
				} else if (expression instanceof JavadocAllocationExpression) {
					JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression) this.seeReferences[i];
					// if binding is valid then look at arguments
					if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding()) {
						if (allocationExpr.arguments != null) {
							for (int j=0, l=allocationExpr.arguments.length; j<l; j++) {
								if (allocationExpr.arguments[j].sourceStart == start) {
									return allocationExpr.arguments[j];
								}
							}
						}
					}
				} else if (expression instanceof JavadocMessageSend) {
					JavadocMessageSend messageSend = (JavadocMessageSend) this.seeReferences[i];
					// if binding is valid then look at arguments
					if (messageSend.binding != null && messageSend.binding.isValidBinding()) {
						if (messageSend.arguments != null) {
							for (int j=0, l=messageSend.arguments.length; j<l; j++) {
								if (messageSend.arguments[j].sourceStart == start) {
									return messageSend.arguments[j];
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	/*
	 * @see org.eclipse.jdt.internal.compiler.ast.ASTNode#print(int, java.lang.StringBuffer)
	 */
	public StringBuffer print(int indent, StringBuffer output) {
		printIndent(indent, output).append("/**\n"); //$NON-NLS-1$
		if (this.paramReferences != null) {
			for (int i = 0, length = this.paramReferences.length; i < length; i++) {
				printIndent(indent + 1, output).append(" * @param "); //$NON-NLS-1$
				this.paramReferences[i].print(indent, output).append('\n');
			}
		}
		if (this.paramTypeParameters != null) {
			for (int i = 0, length = this.paramTypeParameters.length; i < length; i++) {
				printIndent(indent + 1, output).append(" * @param <"); //$NON-NLS-1$
				this.paramTypeParameters[i].print(indent, output).append(">\n"); //$NON-NLS-1$
			}
		}
		if (this.returnStatement != null) {
			printIndent(indent + 1, output).append(" * @"); //$NON-NLS-1$
			this.returnStatement.print(indent, output).append('\n');
		}
		if (this.exceptionReferences != null) {
			for (int i = 0, length = this.exceptionReferences.length; i < length; i++) {
				printIndent(indent + 1, output).append(" * @throws "); //$NON-NLS-1$
				this.exceptionReferences[i].print(indent, output).append('\n');
			}
		}
		if (this.seeReferences != null) {
			for (int i = 0, length = this.seeReferences.length; i < length; i++) {
				printIndent(indent + 1, output).append(" * @see "); //$NON-NLS-1$
				this.seeReferences[i].print(indent, output).append('\n');
			}
		}
		printIndent(indent, output).append(" */\n"); //$NON-NLS-1$
		return output;
	}

	/*
	 * Resolve type javadoc
	 */
	public void resolve(ClassScope scope) {
		if ((this.bits & ASTNode.ResolveJavadoc) == 0) {
			return;
		}
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=247037, @inheritDoc tag cannot
		// be used in the documentation comment for a class or interface.
		if (this.inheritedPositions != null) {
			int length = this.inheritedPositions.length;
			for (int i = 0; i < length; ++i) {
				int start = (int) (this.inheritedPositions[i] >>> 32);
				int end = (int) this.inheritedPositions[i];
				scope.problemReporter().javadocUnexpectedTag(start, end);
			}
		}
		// @param tags
		int paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;
		for (int i = 0; i < paramTagsSize; i++) {
			JavadocSingleNameReference param = this.paramReferences[i];
			scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
		}
		resolveTypeParameterTags(scope, true);

		// @return tags
		if (this.returnStatement != null) {
			scope.problemReporter().javadocUnexpectedTag(this.returnStatement.sourceStart, this.returnStatement.sourceEnd);
		}

		// @throws/@exception tags
		int throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;
		for (int i = 0; i < throwsTagsLength; i++) {
			TypeReference typeRef = this.exceptionReferences[i];
			int start, end;
			if (typeRef instanceof JavadocSingleTypeReference) {
				JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference) typeRef;
				start = singleRef.tagSourceStart;
				end = singleRef.tagSourceEnd;
			} else if (typeRef instanceof JavadocQualifiedTypeReference) {
				JavadocQualifiedTypeReference qualifiedRef = (JavadocQualifiedTypeReference) typeRef;
				start = qualifiedRef.tagSourceStart;
				end = qualifiedRef.tagSourceEnd;
			} else {
				start = typeRef.sourceStart;
				end = typeRef.sourceEnd;
			}
			scope.problemReporter().javadocUnexpectedTag(start, end);
		}

		// @see tags
		int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
		for (int i = 0; i < seeTagsLength; i++) {
			resolveReference(this.seeReferences[i], scope);
		}

		// @value tag
		boolean source15 = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
		if (!source15 && this.valuePositions != -1) {
			scope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions>>>32), (int) this.valuePositions);
		}
	}

	/*
	 * Resolve compilation unit javadoc
	 */
	public void resolve(CompilationUnitScope unitScope) {
		if ((this.bits & ASTNode.ResolveJavadoc) == 0) {
			return;
		}
		// Do nothing - This is to mimic the SDK's javadoc tool behavior, which neither
		// sanity checks nor generates documentation using comments at the CU scope 
		// (unless the unit happens to be package-info.java - in which case we don't come here.) 
	}

	/*
	 * Resolve method javadoc
	 */
	public void resolve(MethodScope methScope) {
		if ((this.bits & ASTNode.ResolveJavadoc) == 0) {
			return;
		}
		// get method declaration
		AbstractMethodDeclaration methDecl = methScope.referenceMethod();
		boolean overriding = methDecl == null /* field declaration */ || methDecl.binding == null /* compiler error */
			? false :
			!methDecl.binding.isStatic() && ((methDecl.binding.modifiers & (ExtraCompilerModifiers.AccImplementing | ExtraCompilerModifiers.AccOverriding)) != 0);

		// @see tags
		int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
		boolean superRef = false;
		for (int i = 0; i < seeTagsLength; i++) {

			// Resolve reference
			resolveReference(this.seeReferences[i], methScope);

			// see whether we can have a super reference
			if (methDecl != null && !superRef) {
				if (!methDecl.isConstructor()) {
					if (overriding && this.seeReferences[i] instanceof JavadocMessageSend) {
						JavadocMessageSend messageSend = (JavadocMessageSend) this.seeReferences[i];
						// if binding is valid then look if we have a reference to an overriden method/constructor
						if (messageSend.binding != null && messageSend.binding.isValidBinding() && messageSend.actualReceiverType instanceof ReferenceBinding) {
							ReferenceBinding methodReceiverType = (ReferenceBinding) messageSend.actualReceiverType;
							TypeBinding superType = methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(methodReceiverType);
							if (superType != null && superType.original() != methDecl.binding.declaringClass && CharOperation.equals(messageSend.selector, methDecl.selector)) {
								if (methScope.environment().methodVerifier().doesMethodOverride(methDecl.binding, messageSend.binding.original())) {
									superRef = true;
								}
							}
						}
					}
				} else if (this.seeReferences[i] instanceof JavadocAllocationExpression) {
					JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression) this.seeReferences[i];
					// if binding is valid then look if we have a reference to an overriden method/constructor
					if (allocationExpr.binding != null && allocationExpr.binding.isValidBinding()) {
						ReferenceBinding allocType = (ReferenceBinding) allocationExpr.resolvedType.original();
						ReferenceBinding superType = (ReferenceBinding) methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(allocType);
						if (superType != null && superType.original() != methDecl.binding.declaringClass) {
							MethodBinding superConstructor = methScope.getConstructor(superType, methDecl.binding.parameters, allocationExpr);
							if (superConstructor.isValidBinding() && superConstructor.original() == allocationExpr.binding.original()) {
								if (superConstructor.areParametersEqual(methDecl.binding)) {
									superRef = true;
								}
							}
						}						
					}
				}
			}
		}

		// Look at @Override annotations
		if (!superRef && methDecl != null && methDecl.annotations != null) {
			int length = methDecl.annotations.length;
			for (int i=0; i<length && !superRef; i++) {
				superRef = (methDecl.binding.tagBits & TagBits.AnnotationOverride) != 0;
			}
		}

		// Store if a reference exists to an overriden method/constructor or the method is in a local type,
		boolean reportMissing = methDecl == null || !((overriding && this.inheritedPositions != null) || superRef || (methDecl.binding.declaringClass != null && methDecl.binding.declaringClass.isLocalType()));
		if (!overriding && this.inheritedPositions != null) {
			int length = this.inheritedPositions.length;
			for (int i = 0; i < length; ++i) {
				int start = (int) (this.inheritedPositions[i] >>> 32);
				int end = (int) this.inheritedPositions[i];
				methScope.problemReporter().javadocUnexpectedTag(start, end);
			}
		}

		// @param tags
		CompilerOptions compilerOptions = methScope.compilerOptions();
		resolveParamTags(methScope, reportMissing, compilerOptions.reportUnusedParameterIncludeDocCommentReference /* considerParamRefAsUsage*/);
		resolveTypeParameterTags(methScope, reportMissing && compilerOptions.reportMissingJavadocTagsMethodTypeParameters);

		// @return tags
		if (this.returnStatement == null) {
			if (reportMissing && methDecl != null) {
				if (methDecl.isMethod()) {
					MethodDeclaration meth = (MethodDeclaration) methDecl;
					if (meth.binding.returnType != TypeBinding.VOID) {
						// method with return should have @return tag
						methScope.problemReporter().javadocMissingReturnTag(meth.returnType.sourceStart, meth.returnType.sourceEnd, methDecl.binding.modifiers);
					}
				}
			}
		} else {
			this.returnStatement.resolve(methScope);
		}

		// @throws/@exception tags
		resolveThrowsTags(methScope, reportMissing);

		// @value tag
		boolean source15 = compilerOptions.sourceLevel >= ClassFileConstants.JDK1_5;
		if (!source15 && methDecl != null && this.valuePositions != -1) {
			methScope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions>>>32), (int) this.valuePositions);
		}

		// Resolve param tags with invalid syntax
		int length = this.invalidParameters == null ? 0 : this.invalidParameters.length;
		for (int i = 0; i < length; i++) {
			this.invalidParameters[i].resolve(methScope, false, false);
		}
	}

	private void resolveReference(Expression reference, Scope scope) {

		// Perform resolve
		int problemCount = scope.referenceContext().compilationResult().problemCount;
		switch (scope.kind) {
			case Scope.METHOD_SCOPE:
				reference.resolveType((MethodScope)scope);
				break;
			case Scope.CLASS_SCOPE:
				reference.resolveType((ClassScope)scope);
				break;
		}
		boolean hasProblems = scope.referenceContext().compilationResult().problemCount > problemCount;

		// Verify field references
		boolean source15 = scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
		int scopeModifiers = -1;
		if (reference instanceof JavadocFieldReference) {
			JavadocFieldReference fieldRef = (JavadocFieldReference) reference;

			// Verify if this is a method reference
			// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=51911
			if (fieldRef.methodBinding != null) {
				// cannot refer to method for @value tag
				if (fieldRef.tagValue == JavadocTagConstants.TAG_VALUE_VALUE) {
					if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
					scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
				}
				else if (fieldRef.actualReceiverType != null) {
					if (scope.enclosingSourceType().isCompatibleWith(fieldRef.actualReceiverType)) {
						fieldRef.bits |= ASTNode.SuperAccess;
					}
					ReferenceBinding resolvedType = (ReferenceBinding) fieldRef.actualReceiverType;
					if (CharOperation.equals(resolvedType.sourceName(), fieldRef.token)) {
						fieldRef.methodBinding = scope.getConstructor(resolvedType, Binding.NO_TYPES, fieldRef);
					} else {
						fieldRef.methodBinding = scope.findMethod(resolvedType, fieldRef.token, Binding.NO_TYPES, fieldRef);
					}
				}
			}

			// Verify whether field ref should be static or not (for @value tags)
			else if (source15 && fieldRef.binding != null && fieldRef.binding.isValidBinding()) {
				if (fieldRef.tagValue == JavadocTagConstants.TAG_VALUE_VALUE && !fieldRef.binding.isStatic()) {
					if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
					scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
				}
			}

			// Verify type references
			if (!hasProblems && fieldRef.binding != null && fieldRef.binding.isValidBinding() && fieldRef.actualReceiverType instanceof ReferenceBinding) {
				ReferenceBinding resolvedType = (ReferenceBinding) fieldRef.actualReceiverType;
				verifyTypeReference(fieldRef, fieldRef.receiver, scope, source15, resolvedType, fieldRef.binding.modifiers);
			}

			// That's it for field references
			return;
		}

		// Verify type references
		if (!hasProblems && (reference instanceof JavadocSingleTypeReference || reference instanceof JavadocQualifiedTypeReference) && reference.resolvedType instanceof ReferenceBinding) {
			ReferenceBinding resolvedType = (ReferenceBinding) reference.resolvedType;
			verifyTypeReference(reference, reference, scope, source15, resolvedType, resolvedType.modifiers);
		}

		// Verify that message reference are not used for @value tags
		if (reference instanceof JavadocMessageSend) {
			JavadocMessageSend msgSend = (JavadocMessageSend) reference;

			// tag value
			if (source15 && msgSend.tagValue == JavadocTagConstants.TAG_VALUE_VALUE) { // cannot refer to method for @value tag
				if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
				scope.problemReporter().javadocInvalidValueReference(msgSend.sourceStart, msgSend.sourceEnd, scopeModifiers);
			}

			// Verify type references
			if (!hasProblems && msgSend.binding != null && msgSend.binding.isValidBinding() && msgSend.actualReceiverType instanceof ReferenceBinding) {
				ReferenceBinding resolvedType = (ReferenceBinding) msgSend.actualReceiverType;
				verifyTypeReference(msgSend, msgSend.receiver, scope, source15, resolvedType, msgSend.binding.modifiers);
			}
		}

		// Verify that constructor reference are not used for @value tags
		else if (reference instanceof JavadocAllocationExpression) {
			JavadocAllocationExpression alloc = (JavadocAllocationExpression) reference;

			// tag value
			if (source15 && alloc.tagValue == JavadocTagConstants.TAG_VALUE_VALUE) { // cannot refer to method for @value tag
				if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
				scope.problemReporter().javadocInvalidValueReference(alloc.sourceStart, alloc.sourceEnd, scopeModifiers);
			}

			// Verify type references
			if (!hasProblems && alloc.binding != null && alloc.binding.isValidBinding() && alloc.resolvedType instanceof ReferenceBinding) {
				ReferenceBinding resolvedType = (ReferenceBinding) alloc.resolvedType;
				verifyTypeReference(alloc, alloc.type, scope, source15, resolvedType, alloc.binding.modifiers);
			}
		}

		// Verify that there's no type variable reference
		// (javadoc does not accept them and this is not a referenced bug or requested enhancement)
		if (reference.resolvedType != null && reference.resolvedType.isTypeVariable()) {
			scope.problemReporter().javadocInvalidReference(reference.sourceStart, reference.sourceEnd);
		}
	}

	/*
	 * Resolve @param tags while method scope
	 */
	private void resolveParamTags(MethodScope scope, boolean reportMissing, boolean considerParamRefAsUsage) {
		AbstractMethodDeclaration methodDecl = scope.referenceMethod();
		int paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;

		// If no referenced method (field initializer for example) then report a problem for each param tag
		if (methodDecl == null) {
			for (int i = 0; i < paramTagsSize; i++) {
				JavadocSingleNameReference param = this.paramReferences[i];
				scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
			}
			return;
		}

		// If no param tags then report a problem for each method argument
		int argumentsSize = methodDecl.arguments == null ? 0 : methodDecl.arguments.length;
		if (paramTagsSize == 0) {
			if (reportMissing) {
				for (int i = 0; i < argumentsSize; i++) {
					Argument arg = methodDecl.arguments[i];
					scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
				}
			}
		} else {
			LocalVariableBinding[] bindings = new LocalVariableBinding[paramTagsSize];
			int maxBindings = 0;

			// Scan all @param tags
			for (int i = 0; i < paramTagsSize; i++) {
				JavadocSingleNameReference param = this.paramReferences[i];
				param.resolve(scope, true, considerParamRefAsUsage);
				if (param.binding != null && param.binding.isValidBinding()) {
					// Verify duplicated tags
					boolean found = false;
					for (int j = 0; j < maxBindings && !found; j++) {
						if (bindings[j] == param.binding) {
							scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, methodDecl.binding.modifiers);
							found = true;
						}
					}
					if (!found) {
						bindings[maxBindings++] = (LocalVariableBinding) param.binding;
					}
				}
			}

			// Look for undocumented arguments
			if (reportMissing) {
				for (int i = 0; i < argumentsSize; i++) {
					Argument arg = methodDecl.arguments[i];
					boolean found = false;
					for (int j = 0; j < maxBindings && !found; j++) {
						LocalVariableBinding binding = bindings[j];
						if (arg.binding == binding) {
							found = true;
						}
					}
					if (!found) {
						scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
					}
				}
			}
		}
	}

	/*
	 * Resolve @param tags for type parameters
	 */
	private void resolveTypeParameterTags(Scope scope, boolean reportMissing) {
		int paramTypeParamLength = this.paramTypeParameters == null ? 0 : this.paramTypeParameters.length;

		// Get declaration infos
		TypeParameter[] parameters = null;
		TypeVariableBinding[] typeVariables = null;
		int modifiers = -1;
		switch (scope.kind) {
			case Scope.METHOD_SCOPE:
				AbstractMethodDeclaration methodDeclaration = ((MethodScope)scope).referenceMethod();
				// If no referenced method (field initializer for example) then report a problem for each param tag
				if (methodDeclaration == null) {
					for (int i = 0; i < paramTypeParamLength; i++) {
						JavadocSingleTypeReference param = this.paramTypeParameters[i];
						scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
					}
					return;
				}
				parameters = methodDeclaration.typeParameters();
				typeVariables = methodDeclaration.binding.typeVariables;
				modifiers = methodDeclaration.binding.modifiers;
				break;
			case Scope.CLASS_SCOPE:
				TypeDeclaration typeDeclaration = ((ClassScope) scope).referenceContext;
				parameters = typeDeclaration.typeParameters;
				typeVariables = typeDeclaration.binding.typeVariables;
				modifiers = typeDeclaration.binding.modifiers;
				break;
		}

		// If no type variables then report a problem for each param type parameter tag
		if (typeVariables == null || typeVariables.length == 0) {
			for (int i = 0; i < paramTypeParamLength; i++) {
				JavadocSingleTypeReference param = this.paramTypeParameters[i];
				scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
			}
			return;
		}

		// If no param tags then report a problem for each declaration type parameter
		if (parameters != null) {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=324850, avoid secondary errors when <= 1.4 
			reportMissing = reportMissing && scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5;
			int typeParametersLength = parameters.length;
			if (paramTypeParamLength == 0) {
				if (reportMissing) {
					for (int i = 0, l=typeParametersLength; i<l; i++) {
						scope.problemReporter().javadocMissingParamTag(parameters[i].name, parameters[i].sourceStart, parameters[i].sourceEnd, modifiers);
					}
				}

			// Otherwise verify that all param tags match type parameters
			} else if (typeVariables.length == typeParametersLength) {
				TypeVariableBinding[] bindings = new TypeVariableBinding[paramTypeParamLength];

				// Scan all @param tags
				for (int i = 0; i < paramTypeParamLength; i++) {
					JavadocSingleTypeReference param = this.paramTypeParameters[i];
					TypeBinding paramBindind = param.internalResolveType(scope);
					if (paramBindind != null && paramBindind.isValidBinding()) {
						if (paramBindind.isTypeVariable()) {
							// Verify duplicated tags
							boolean duplicate = false;
							for (int j = 0; j < i && !duplicate; j++) {
								if (bindings[j] == param.resolvedType) {
									scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, modifiers);
									duplicate = true;
								}
							}
							if (!duplicate) {
								bindings[i] = (TypeVariableBinding) param.resolvedType;
							}
						} else {
							scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
						}
					}
				}

				// Look for undocumented type parameters
				for (int i = 0; i < typeParametersLength; i++) {
					TypeParameter parameter = parameters[i];
					boolean found = false;
					for (int j = 0; j < paramTypeParamLength && !found; j++) {
						if (parameter.binding == bindings[j]) {
							found = true;
							bindings[j] = null;
						}
					}
					if (!found && reportMissing) {
						scope.problemReporter().javadocMissingParamTag(parameter.name, parameter.sourceStart, parameter.sourceEnd, modifiers);
					}
				}

				// Report invalid param
				for (int i=0; i<paramTypeParamLength; i++) {
					if (bindings[i] != null) {
						JavadocSingleTypeReference param = this.paramTypeParameters[i];
						scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
					}
				}
			}
		}
	}

	/*
	 * Resolve @throws/@exception tags while method scope
	 */
	private void resolveThrowsTags(MethodScope methScope, boolean reportMissing) {
		AbstractMethodDeclaration md = methScope.referenceMethod();
		int throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;

		// If no referenced method (field initializer for example) then report a problem for each throws tag
		if (md == null) {
			for (int i = 0; i < throwsTagsLength; i++) {
				TypeReference typeRef = this.exceptionReferences[i];
				int start = typeRef.sourceStart;
				int end = typeRef.sourceEnd;
				if (typeRef instanceof JavadocQualifiedTypeReference) {
					start = ((JavadocQualifiedTypeReference) typeRef).tagSourceStart;
					end = ((JavadocQualifiedTypeReference) typeRef).tagSourceEnd;
				} else if (typeRef instanceof JavadocSingleTypeReference) {
					start = ((JavadocSingleTypeReference) typeRef).tagSourceStart;
					end = ((JavadocSingleTypeReference) typeRef).tagSourceEnd;
				}
				methScope.problemReporter().javadocUnexpectedTag(start, end);
			}
			return;
		}

		// If no throws tags then report a problem for each method thrown exception
		int boundExceptionLength = (md.binding == null) ? 0 : md.binding.thrownExceptions.length;
		int thrownExceptionLength = md.thrownExceptions == null ? 0 : md.thrownExceptions.length;
		if (throwsTagsLength == 0) {
			if (reportMissing) {
				for (int i = 0; i < boundExceptionLength; i++) {
					ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
					if (exceptionBinding != null && exceptionBinding.isValidBinding()) { // flag only valid class name
						int j=i;
						while (j<thrownExceptionLength && exceptionBinding != md.thrownExceptions[j].resolvedType) j++;
						if (j<thrownExceptionLength) {
							methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[j], md.binding.modifiers);
						}
					}
				}
			}
		} else {
			int maxRef = 0;
			TypeReference[] typeReferences = new TypeReference[throwsTagsLength];

			// Scan all @throws tags
			for (int i = 0; i < throwsTagsLength; i++) {
				TypeReference typeRef = this.exceptionReferences[i];
				typeRef.resolve(methScope);
				TypeBinding typeBinding = typeRef.resolvedType;

				if (typeBinding != null && typeBinding.isValidBinding() && typeBinding.isClass()) {
					// accept only valid class binding
					typeReferences[maxRef++] = typeRef;
				}
			}

			// Look for undocumented thrown exception
			for (int i = 0; i < boundExceptionLength; i++) {
				ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
				if (exceptionBinding != null) exceptionBinding = (ReferenceBinding) exceptionBinding.erasure();
				boolean found = false;
				for (int j = 0; j < maxRef && !found; j++) {
					if (typeReferences[j] != null) {
						TypeBinding typeBinding = typeReferences[j].resolvedType;
						if (exceptionBinding == typeBinding) {
							found = true;
							typeReferences[j] = null;
						}
					}
				}
				if (!found && reportMissing) {
					if (exceptionBinding != null && exceptionBinding.isValidBinding()) { // flag only valid class name
						int k=i;
						while (k<thrownExceptionLength && exceptionBinding != md.thrownExceptions[k].resolvedType) k++;
						if (k<thrownExceptionLength) {
							methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[k], md.binding.modifiers);
						}
					}
				}
			}

			// Verify additional @throws tags
			for (int i = 0; i < maxRef; i++) {
				TypeReference typeRef = typeReferences[i];
				if (typeRef != null) {
					boolean compatible = false;
					// thrown exceptions subclasses are accepted
					for (int j = 0; j<thrownExceptionLength && !compatible; j++) {
						TypeBinding exceptionBinding = md.thrownExceptions[j].resolvedType;
						if (exceptionBinding != null) {
							compatible = typeRef.resolvedType.isCompatibleWith(exceptionBinding);
						}
					}

					//  If not compatible only complain on unchecked exception
					if (!compatible && !typeRef.resolvedType.isUncheckedException(false)) {
						methScope.problemReporter().javadocInvalidThrowsClassName(typeRef, md.binding.modifiers);
					}
				}
			}
		}
	}

	private void verifyTypeReference(Expression reference, Expression typeReference, Scope scope, boolean source15, ReferenceBinding resolvedType, int modifiers) {
		if (resolvedType.isValidBinding()) {
			int scopeModifiers = -1;

			// reference must have enough visibility to be used
			if (!canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, modifiers)) {
				scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, reference.sourceEnd, scope, modifiers);
				return;
			}

			// type reference must have enough visibility to be used
			if (reference != typeReference) {
				if (!canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, resolvedType.modifiers)) {
					scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, typeReference.sourceEnd, scope, resolvedType.modifiers);
					return;
				}
			}

			// member types
			if (resolvedType.isMemberType()) {
				ReferenceBinding topLevelType = resolvedType;
				// rebuild and store (in reverse order) compound name to handle embedded inner class
				int packageLength = topLevelType.fPackage.compoundName.length;
				int depth = resolvedType.depth();
				int idx = depth + packageLength;
				char[][] computedCompoundName = new char[idx+1][];
				computedCompoundName[idx] = topLevelType.sourceName;
				while (topLevelType.enclosingType() != null) {
					topLevelType = topLevelType.enclosingType();
					computedCompoundName[--idx] = topLevelType.sourceName;
				}

				// add package information
				for (int i = packageLength; --i >= 0;) {
					computedCompoundName[--idx] = topLevelType.fPackage.compoundName[i];
				}

				ClassScope topLevelScope = scope.classScope();
				// when scope is not on compilation unit type, then inner class may not be visible...
				if (topLevelScope.parent.kind != Scope.COMPILATION_UNIT_SCOPE ||
					!CharOperation.equals(topLevelType.sourceName, topLevelScope.referenceContext.name)) {
					topLevelScope = topLevelScope.outerMostClassScope();
					if (typeReference instanceof JavadocSingleTypeReference) {
						// inner class single reference can only be done in same unit
						if ((!source15 && depth == 1) || topLevelType != topLevelScope.referenceContext.binding) {
							// search for corresponding import
							boolean hasValidImport = false;
							if (source15) {
								CompilationUnitScope unitScope = topLevelScope.compilationUnitScope();
								ImportBinding[] imports = unitScope.imports;
								int length = imports == null ? 0 : imports.length;
								mainLoop: for (int i=0; i<length; i++) {
									char[][] compoundName = imports[i].compoundName;
									int compoundNameLength = compoundName.length;
									if ((imports[i].onDemand && compoundNameLength == computedCompoundName.length-1) 
											|| (compoundNameLength == computedCompoundName.length)) {
										for (int j = compoundNameLength; --j >= 0;) {
											if (CharOperation.equals(imports[i].compoundName[j], computedCompoundName[j])) {
												if (j == 0) {
													hasValidImport = true;
													ImportReference importReference = imports[i].reference;
													if (importReference != null) {
														importReference.bits |= ASTNode.Used;
													}
													break mainLoop;
												}
											} else {
												break;
											}
										}
									}
								}
								if (!hasValidImport) {
									if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
									scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
								}
							} else {
								if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
								scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
								return;
							}
						}
					}
				}
				if (typeReference instanceof JavadocQualifiedTypeReference && !scope.isDefinedInSameUnit(resolvedType)) {
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=222188
					// partially qualified references from a different CU should be warned
					char[][] typeRefName = ((JavadocQualifiedTypeReference) typeReference).getTypeName();
					int skipLength = 0;
					if (topLevelScope.getCurrentPackage() == resolvedType.getPackage()
							&& typeRefName.length < computedCompoundName.length) {
						// https://bugs.eclipse.org/bugs/show_bug.cgi?id=221539: references can be partially qualified
						// in same package and hence if the package name is not given, ignore package name check
						skipLength = resolvedType.fPackage.compoundName.length;
					}
					boolean valid = true;
					if (typeRefName.length == computedCompoundName.length - skipLength) {
						checkQualification: for (int i = 0; i < typeRefName.length; i++) {
							if (!CharOperation.equals(typeRefName[i], computedCompoundName[i + skipLength])) {
								valid = false;
								break checkQualification;
							}
						}
					} else {
						valid = false;
					}
					// report invalid reference
					if (!valid) {
						if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
						scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
						return;
					}
				}
			}
			/*
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=286918
			 *
			 * We are concerned only about the Single type references (i.e. without any package) If they are not in default package,
			 * then report an error. References with qualified yet incorrect names would have already been taken care of.
			 */
			if (scope.referenceCompilationUnit().isPackageInfo() && typeReference instanceof JavadocSingleTypeReference) {
				if (resolvedType.fPackage.compoundName.length > 0) {
					scope.problemReporter().javadocInvalidReference(typeReference.sourceStart, typeReference.sourceEnd);
					return; // Not really needed - just in case more code added in future
				}
			}
		}
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.paramReferences != null) {
				for (int i = 0, length = this.paramReferences.length; i < length; i++) {
					this.paramReferences[i].traverse(visitor, scope);
				}
			}
			if (this.paramTypeParameters != null) {
				for (int i = 0, length = this.paramTypeParameters.length; i < length; i++) {
					this.paramTypeParameters[i].traverse(visitor, scope);
				}
			}
			if (this.returnStatement != null) {
				this.returnStatement.traverse(visitor, scope);
			}
			if (this.exceptionReferences != null) {
				for (int i = 0, length = this.exceptionReferences.length; i < length; i++) {
					this.exceptionReferences[i].traverse(visitor, scope);
				}
			}
			if (this.seeReferences != null) {
				for (int i = 0, length = this.seeReferences.length; i < length; i++) {
					this.seeReferences[i].traverse(visitor, scope);
				}
			}
		}
		visitor.endVisit(this, scope);
	}
	public void traverse(ASTVisitor visitor, ClassScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.paramReferences != null) {
				for (int i = 0, length = this.paramReferences.length; i < length; i++) {
					this.paramReferences[i].traverse(visitor, scope);
				}
			}
			if (this.paramTypeParameters != null) {
				for (int i = 0, length = this.paramTypeParameters.length; i < length; i++) {
					this.paramTypeParameters[i].traverse(visitor, scope);
				}
			}
			if (this.returnStatement != null) {
				this.returnStatement.traverse(visitor, scope);
			}
			if (this.exceptionReferences != null) {
				for (int i = 0, length = this.exceptionReferences.length; i < length; i++) {
					this.exceptionReferences[i].traverse(visitor, scope);
				}
			}
			if (this.seeReferences != null) {
				for (int i = 0, length = this.seeReferences.length; i < length; i++) {
					this.seeReferences[i].traverse(visitor, scope);
				}
			}
		}
		visitor.endVisit(this, scope);
	}
}
