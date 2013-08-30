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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public class BlockScope extends Scope {

	// Local variable management
	public LocalVariableBinding[] locals;
	public int localIndex; // position for next variable
	public int startIndex;	// start position in this scope - for ordering scopes vs. variables
	public int offset; // for variable allocation throughout scopes
	public int maxOffset; // for variable allocation throughout scopes

	// finally scopes must be shifted behind respective try&catch scope(s) so as to avoid
	// collisions of secret variables (return address, save value).
	public BlockScope[] shiftScopes;

	public Scope[] subscopes = new Scope[1]; // need access from code assist
	public int subscopeCount = 0; // need access from code assist
	// record the current case statement being processed (for entire switch case block).
	public CaseStatement enclosingCase; // from 1.4 on, local types should not be accessed across switch case blocks (52221)

	public final static VariableBinding[] EmulationPathToImplicitThis = {};
	public final static VariableBinding[] NoEnclosingInstanceInConstructorCall = {};

	public final static VariableBinding[] NoEnclosingInstanceInStaticContext = {};

public BlockScope(BlockScope parent) {
	this(parent, true);
}

public BlockScope(BlockScope parent, boolean addToParentScope) {
	this(Scope.BLOCK_SCOPE, parent);
	this.locals = new LocalVariableBinding[5];
	if (addToParentScope) parent.addSubscope(this);
	this.startIndex = parent.localIndex;
}

public BlockScope(BlockScope parent, int variableCount) {
	this(Scope.BLOCK_SCOPE, parent);
	this.locals = new LocalVariableBinding[variableCount];
	parent.addSubscope(this);
	this.startIndex = parent.localIndex;
}

protected BlockScope(int kind, Scope parent) {
	super(kind, parent);
}

/* Create the class scope & binding for the anonymous type.
 */
public final void addAnonymousType(TypeDeclaration anonymousType, ReferenceBinding superBinding) {
	ClassScope anonymousClassScope = new ClassScope(this, anonymousType);
	anonymousClassScope.buildAnonymousTypeBinding(
		enclosingSourceType(),
		superBinding);
}

/* Create the class scope & binding for the local type.
 */
public final void addLocalType(TypeDeclaration localType) {
	ClassScope localTypeScope = new ClassScope(this, localType);
	addSubscope(localTypeScope);
	localTypeScope.buildLocalTypeBinding(enclosingSourceType());
}

/* Insert a local variable into a given scope, updating its position
 * and checking there are not too many locals or arguments allocated.
 */
public final void addLocalVariable(LocalVariableBinding binding) {
	checkAndSetModifiersForVariable(binding);
	// insert local in scope
	if (this.localIndex == this.locals.length)
		System.arraycopy(
			this.locals,
			0,
			(this.locals = new LocalVariableBinding[this.localIndex * 2]),
			0,
			this.localIndex);
	this.locals[this.localIndex++] = binding;

	// update local variable binding
	binding.declaringScope = this;
	binding.id = outerMostMethodScope().analysisIndex++;
	// share the outermost method scope analysisIndex
}

public void addSubscope(Scope childScope) {
	if (this.subscopeCount == this.subscopes.length)
		System.arraycopy(
			this.subscopes,
			0,
			(this.subscopes = new Scope[this.subscopeCount * 2]),
			0,
			this.subscopeCount);
	this.subscopes[this.subscopeCount++] = childScope;
}

/**
 * Answer true if the receiver is suitable for assigning final blank fields.
 * in other words, it is inside an initializer, a constructor or a clinit
 */
public final boolean allowBlankFinalFieldAssignment(FieldBinding binding) {
	if (enclosingReceiverType() != binding.declaringClass)
		return false;

	MethodScope methodScope = methodScope();
	if (methodScope.isStatic != binding.isStatic())
		return false;
	return methodScope.isInsideInitializer() // inside initializer
			|| ((AbstractMethodDeclaration) methodScope.referenceContext).isInitializationMethod(); // inside constructor or clinit
}

String basicToString(int tab) {
	String newLine = "\n"; //$NON-NLS-1$
	for (int i = tab; --i >= 0;)
		newLine += "\t"; //$NON-NLS-1$

	String s = newLine + "--- Block Scope ---"; //$NON-NLS-1$
	newLine += "\t"; //$NON-NLS-1$
	s += newLine + "locals:"; //$NON-NLS-1$
	for (int i = 0; i < this.localIndex; i++)
		s += newLine + "\t" + this.locals[i].toString(); //$NON-NLS-1$
	s += newLine + "startIndex = " + this.startIndex; //$NON-NLS-1$
	return s;
}

private void checkAndSetModifiersForVariable(LocalVariableBinding varBinding) {
	int modifiers = varBinding.modifiers;
	if ((modifiers & ExtraCompilerModifiers.AccAlternateModifierProblem) != 0 && varBinding.declaration != null){
		problemReporter().duplicateModifierForVariable(varBinding.declaration, this instanceof MethodScope);
	}
	int realModifiers = modifiers & ExtraCompilerModifiers.AccJustFlag;

	int unexpectedModifiers = ~ClassFileConstants.AccFinal;
	if ((realModifiers & unexpectedModifiers) != 0 && varBinding.declaration != null){
		problemReporter().illegalModifierForVariable(varBinding.declaration, this instanceof MethodScope);
	}
	varBinding.modifiers = modifiers;
}

/* Compute variable positions in scopes given an initial position offset
 * ignoring unused local variables.
 *
 * No argument is expected here (ilocal is the first non-argument local of the outermost scope)
 * Arguments are managed by the MethodScope method
 */
void computeLocalVariablePositions(int ilocal, int initOffset, CodeStream codeStream) {
	this.offset = initOffset;
	this.maxOffset = initOffset;

	// local variable init
	int maxLocals = this.localIndex;
	boolean hasMoreVariables = ilocal < maxLocals;

	// scope init
	int iscope = 0, maxScopes = this.subscopeCount;
	boolean hasMoreScopes = maxScopes > 0;

	// iterate scopes and variables in parallel
	while (hasMoreVariables || hasMoreScopes) {
		if (hasMoreScopes
			&& (!hasMoreVariables || (this.subscopes[iscope].startIndex() <= ilocal))) {
			// consider subscope first
			if (this.subscopes[iscope] instanceof BlockScope) {
				BlockScope subscope = (BlockScope) this.subscopes[iscope];
				int subOffset = subscope.shiftScopes == null ? this.offset : subscope.maxShiftedOffset();
				subscope.computeLocalVariablePositions(0, subOffset, codeStream);
				if (subscope.maxOffset > this.maxOffset)
					this.maxOffset = subscope.maxOffset;
			}
			hasMoreScopes = ++iscope < maxScopes;
		} else {

			// consider variable first
			LocalVariableBinding local = this.locals[ilocal]; // if no local at all, will be locals[ilocal]==null

			// check if variable is actually used, and may force it to be preserved
			boolean generateCurrentLocalVar = (local.useFlag > LocalVariableBinding.UNUSED && local.constant() == Constant.NotAConstant);

			// do not report fake used variable
			if (local.useFlag == LocalVariableBinding.UNUSED
				&& (local.declaration != null) // unused (and non secret) local
				&& ((local.declaration.bits & ASTNode.IsLocalDeclarationReachable) != 0)) { // declaration is reachable

				if (!(local.declaration instanceof Argument)) // do not report unused catch arguments
					problemReporter().unusedLocalVariable(local.declaration);
			}

			// could be optimized out, but does need to preserve unread variables ?
			if (!generateCurrentLocalVar) {
				if (local.declaration != null && compilerOptions().preserveAllLocalVariables) {
					generateCurrentLocalVar = true; // force it to be preserved in the generated code
					if (local.useFlag == LocalVariableBinding.UNUSED)
						local.useFlag = LocalVariableBinding.USED;
				}
			}

			// allocate variable
			if (generateCurrentLocalVar) {

				if (local.declaration != null) {
					codeStream.record(local); // record user-defined local variables for attribute generation
				}
				// assign variable position
				local.resolvedPosition = this.offset;

				if ((local.type == TypeBinding.LONG) || (local.type == TypeBinding.DOUBLE)) {
					this.offset += 2;
				} else {
					this.offset++;
				}
				if (this.offset > 0xFFFF) { // no more than 65535 words of locals
					problemReporter().noMoreAvailableSpaceForLocal(
						local,
						local.declaration == null ? (ASTNode)methodScope().referenceContext : local.declaration);
				}
			} else {
				local.resolvedPosition = -1; // not generated
			}
			hasMoreVariables = ++ilocal < maxLocals;
		}
	}
	if (this.offset > this.maxOffset)
		this.maxOffset = this.offset;
}

/*
 *	Record the suitable binding denoting a synthetic field or constructor argument,
 * mapping to the actual outer local variable in the scope context.
 * Note that this may not need any effect, in case the outer local variable does not
 * need to be emulated and can directly be used as is (using its back pointer to its
 * declaring scope).
 */
public void emulateOuterAccess(LocalVariableBinding outerLocalVariable) {
	BlockScope outerVariableScope = outerLocalVariable.declaringScope;
	if (outerVariableScope == null)
		return; // no need to further emulate as already inserted (val$this$0)
	MethodScope currentMethodScope = methodScope();
	if (outerVariableScope.methodScope() != currentMethodScope) {
		NestedTypeBinding currentType = (NestedTypeBinding) enclosingSourceType();

		//do nothing for member types, pre emulation was performed already
		if (!currentType.isLocalType()) {
			return;
		}
		// must also add a synthetic field if we're not inside a constructor
		if (!currentMethodScope.isInsideInitializerOrConstructor()) {
			currentType.addSyntheticArgumentAndField(outerLocalVariable);
		} else {
			currentType.addSyntheticArgument(outerLocalVariable);
		}
	}
}

/* Note that it must never produce a direct access to the targetEnclosingType,
 * but instead a field sequence (this$2.this$1.this$0) so as to handle such a test case:
 *
 * class XX {
 *	void foo() {
 *		class A {
 *			class B {
 *				class C {
 *					boolean foo() {
 *						return (Object) A.this == (Object) B.this;
 *					}
 *				}
 *			}
 *		}
 *		new A().new B().new C();
 *	}
 * }
 * where we only want to deal with ONE enclosing instance for C (could not figure out an A for C)
 */
public final ReferenceBinding findLocalType(char[] name) {
	long compliance = compilerOptions().complianceLevel;
	for (int i = this.subscopeCount-1; i >= 0; i--) {
		if (this.subscopes[i] instanceof ClassScope) {
			LocalTypeBinding sourceType = (LocalTypeBinding)((ClassScope) this.subscopes[i]).referenceContext.binding;
			// from 1.4 on, local types should not be accessed across switch case blocks (52221)
			if (compliance >= ClassFileConstants.JDK1_4 && sourceType.enclosingCase != null) {
				if (!isInsideCase(sourceType.enclosingCase)) {
					continue;
				}
			}
			if (CharOperation.equals(sourceType.sourceName(), name))
				return sourceType;
		}
	}
	return null;
}

/**
 * Returns all declarations of most specific locals containing a given position in their source range.
 * This code does not recurse in nested types.
 * Returned array may have null values at trailing indexes.
 */
public LocalDeclaration[] findLocalVariableDeclarations(int position) {
	// local variable init
	int ilocal = 0, maxLocals = this.localIndex;
	boolean hasMoreVariables = maxLocals > 0;
	LocalDeclaration[] localDeclarations = null;
	int declPtr = 0;

	// scope init
	int iscope = 0, maxScopes = this.subscopeCount;
	boolean hasMoreScopes = maxScopes > 0;

	// iterate scopes and variables in parallel
	while (hasMoreVariables || hasMoreScopes) {
		if (hasMoreScopes
			&& (!hasMoreVariables || (this.subscopes[iscope].startIndex() <= ilocal))) {
			// consider subscope first
			Scope subscope = this.subscopes[iscope];
			if (subscope.kind == Scope.BLOCK_SCOPE) { // do not dive in nested types
				localDeclarations = ((BlockScope)subscope).findLocalVariableDeclarations(position);
				if (localDeclarations != null) {
					return localDeclarations;
				}
			}
			hasMoreScopes = ++iscope < maxScopes;
		} else {
			// consider variable first
			LocalVariableBinding local = this.locals[ilocal]; // if no local at all, will be locals[ilocal]==null
			if (local != null) {
				LocalDeclaration localDecl = local.declaration;
				if (localDecl != null) {
					if (localDecl.declarationSourceStart <= position) {
						if (position <= localDecl.declarationSourceEnd) {
							if (localDeclarations == null) {
								localDeclarations = new LocalDeclaration[maxLocals];
							}
							localDeclarations[declPtr++] = localDecl;
						}
					} else {
						return localDeclarations;
					}
				}
			}
			hasMoreVariables = ++ilocal < maxLocals;
			if (!hasMoreVariables && localDeclarations != null) {
				return localDeclarations;
			}
		}
	}
	return null;
}

public LocalVariableBinding findVariable(char[] variableName) {
	int varLength = variableName.length;
	for (int i = this.localIndex-1; i >= 0; i--) { // lookup backward to reach latest additions first
		LocalVariableBinding local;
		char[] localName;
		if ((localName = (local = this.locals[i]).name).length == varLength && CharOperation.equals(localName, variableName))
			return local;
	}
	return null;
}

/* API
 * flag is a mask of the following values VARIABLE (= FIELD or LOCAL), TYPE.
 * Only bindings corresponding to the mask will be answered.
 *
 *	if the VARIABLE mask is set then
 *		If the first name provided is a field (or local) then the field (or local) is answered
 *		Otherwise, package names and type names are consumed until a field is found.
 *		In this case, the field is answered.
 *
 *	if the TYPE mask is set,
 *		package names and type names are consumed until the end of the input.
 *		Only if all of the input is consumed is the type answered
 *
 *	All other conditions are errors, and a problem binding is returned.
 *
 *	NOTE: If a problem binding is returned, senders should extract the compound name
 *	from the binding & not assume the problem applies to the entire compoundName.
 *
 *	The VARIABLE mask has precedence over the TYPE mask.
 *
 *	InvocationSite implements
 *		isSuperAccess(); this is used to determine if the discovered field is visible.
 *		setFieldIndex(int); this is used to record the number of names that were consumed.
 *
 *	For example, getBinding({"foo","y","q", VARIABLE, site) will answer
 *	the binding for the field or local named "foo" (or an error binding if none exists).
 *	In addition, setFieldIndex(1) will be sent to the invocation site.
 *	If a type named "foo" exists, it will not be detected (and an error binding will be answered)
 *
 *	IMPORTANT NOTE: This method is written under the assumption that compoundName is longer than length 1.
 */
public Binding getBinding(char[][] compoundName, int mask, InvocationSite invocationSite, boolean needResolve) {
	Binding binding = getBinding(compoundName[0], mask | Binding.TYPE | Binding.PACKAGE, invocationSite, needResolve);
	invocationSite.setFieldIndex(1);
	if (binding instanceof VariableBinding) return binding;
	CompilationUnitScope unitScope = compilationUnitScope();
	// in the problem case, we want to ensure we record the qualified dependency in case a type is added
	// and we do not know that its package was also added (can happen with CompilationParticipants)
	unitScope.recordQualifiedReference(compoundName);
	if (!binding.isValidBinding()) return binding;

	int length = compoundName.length;
	int currentIndex = 1;
	foundType : if (binding instanceof PackageBinding) {
		PackageBinding packageBinding = (PackageBinding) binding;
		while (currentIndex < length) {
			unitScope.recordReference(packageBinding.compoundName, compoundName[currentIndex]);
			binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
			invocationSite.setFieldIndex(currentIndex);
			if (binding == null) {
				if (currentIndex == length) {
					// must be a type if its the last name, otherwise we have no idea if its a package or type
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						null,
						ProblemReasons.NotFound);
				}
				return new ProblemBinding(
					CharOperation.subarray(compoundName, 0, currentIndex),
					ProblemReasons.NotFound);
			}
			if (binding instanceof ReferenceBinding) {
				if (!binding.isValidBinding())
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						(ReferenceBinding)((ReferenceBinding)binding).closestMatch(),
						binding.problemId());
				if (!((ReferenceBinding) binding).canBeSeenBy(this))
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						(ReferenceBinding) binding,
						ProblemReasons.NotVisible);
				break foundType;
			}
			packageBinding = (PackageBinding) binding;
		}

		// It is illegal to request a PACKAGE from this method.
		return new ProblemReferenceBinding(
			CharOperation.subarray(compoundName, 0, currentIndex),
			null,
			ProblemReasons.NotFound);
	}

	// know binding is now a ReferenceBinding
	ReferenceBinding referenceBinding = (ReferenceBinding) binding;
	binding = environment().convertToRawType(referenceBinding, false /*do not force conversion of enclosing types*/);
	if (invocationSite instanceof ASTNode) {
		ASTNode invocationNode = (ASTNode) invocationSite;
		if (invocationNode.isTypeUseDeprecated(referenceBinding, this)) {
			problemReporter().deprecatedType(referenceBinding, invocationNode);
		}
	}
	Binding problemFieldBinding = null;
	while (currentIndex < length) {
		referenceBinding = (ReferenceBinding) binding;
		char[] nextName = compoundName[currentIndex++];
		invocationSite.setFieldIndex(currentIndex);
		invocationSite.setActualReceiverType(referenceBinding);
		if ((mask & Binding.FIELD) != 0 && (binding = findField(referenceBinding, nextName, invocationSite, true /*resolve*/)) != null) {
			if (binding.isValidBinding()) {
				break; // binding is now a field
			}
			problemFieldBinding = new ProblemFieldBinding(
				((ProblemFieldBinding)binding).closestMatch,
				((ProblemFieldBinding)binding).declaringClass,
				CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
				binding.problemId()); 
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=317858 : If field is inaccessible,
			// don't give up yet, continue to look for a visible member type 
			if (binding.problemId() != ProblemReasons.NotVisible) {  
				return problemFieldBinding;
			}
		}
		if ((binding = findMemberType(nextName, referenceBinding)) == null) {
			if (problemFieldBinding != null) {
				return problemFieldBinding;
			}
			if ((mask & Binding.FIELD) != 0) {
				return new ProblemFieldBinding(
						null,
						referenceBinding,
						nextName,
						ProblemReasons.NotFound);
			} else if ((mask & Binding.VARIABLE) != 0) {
				return new ProblemBinding(
					CharOperation.subarray(compoundName, 0, currentIndex),
					referenceBinding,
					ProblemReasons.NotFound);
			}
			return new ProblemReferenceBinding(
				CharOperation.subarray(compoundName, 0, currentIndex),
				referenceBinding,
				ProblemReasons.NotFound);
		}
		// binding is a ReferenceBinding
		if (!binding.isValidBinding()) {
			if (problemFieldBinding != null) {
				return problemFieldBinding;
			}
			return new ProblemReferenceBinding(
				CharOperation.subarray(compoundName, 0, currentIndex),
				(ReferenceBinding)((ReferenceBinding)binding).closestMatch(),
				binding.problemId());
		}
		if (invocationSite instanceof ASTNode) {
			referenceBinding = (ReferenceBinding) binding;
			ASTNode invocationNode = (ASTNode) invocationSite;
			if (invocationNode.isTypeUseDeprecated(referenceBinding, this)) {
				problemReporter().deprecatedType(referenceBinding, invocationNode);
			}
		}
	}
	if ((mask & Binding.FIELD) != 0 && (binding instanceof FieldBinding)) {
		// was looking for a field and found a field
		FieldBinding field = (FieldBinding) binding;
		if (!field.isStatic())
			return new ProblemFieldBinding(
				field,
				field.declaringClass,
				CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
				ProblemReasons.NonStaticReferenceInStaticContext);
		return binding;
	}
	if ((mask & Binding.TYPE) != 0 && (binding instanceof ReferenceBinding)) {
		// was looking for a type and found a type
		return binding;
	}

	// handle the case when a field or type was asked for but we resolved the compoundName to a type or field
	return new ProblemBinding(
		CharOperation.subarray(compoundName, 0, currentIndex),
		ProblemReasons.NotFound);
}

// Added for code assist... NOT Public API
public final Binding getBinding(char[][] compoundName, InvocationSite invocationSite) {
	int currentIndex = 0;
	int length = compoundName.length;
	Binding binding =
		getBinding(
			compoundName[currentIndex++],
			Binding.VARIABLE | Binding.TYPE | Binding.PACKAGE,
			invocationSite,
			true /*resolve*/);
	if (!binding.isValidBinding())
		return binding;

	foundType : if (binding instanceof PackageBinding) {
		while (currentIndex < length) {
			PackageBinding packageBinding = (PackageBinding) binding;
			binding = packageBinding.getTypeOrPackage(compoundName[currentIndex++]);
			if (binding == null) {
				if (currentIndex == length) {
					// must be a type if its the last name, otherwise we have no idea if its a package or type
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						null,
						ProblemReasons.NotFound);
				}
				return new ProblemBinding(
					CharOperation.subarray(compoundName, 0, currentIndex),
					ProblemReasons.NotFound);
			}
			if (binding instanceof ReferenceBinding) {
				if (!binding.isValidBinding())
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						(ReferenceBinding)((ReferenceBinding)binding).closestMatch(),
						binding.problemId());
				if (!((ReferenceBinding) binding).canBeSeenBy(this))
					return new ProblemReferenceBinding(
						CharOperation.subarray(compoundName, 0, currentIndex),
						(ReferenceBinding) binding,
						ProblemReasons.NotVisible);
				break foundType;
			}
		}
		return binding;
	}

	foundField : if (binding instanceof ReferenceBinding) {
		while (currentIndex < length) {
			ReferenceBinding typeBinding = (ReferenceBinding) binding;
			char[] nextName = compoundName[currentIndex++];
			TypeBinding receiverType = typeBinding.capture(this, invocationSite.sourceEnd());
			if ((binding = findField(receiverType, nextName, invocationSite, true /*resolve*/)) != null) {
				if (!binding.isValidBinding()) {
					return new ProblemFieldBinding(
						(FieldBinding) binding,
						((FieldBinding) binding).declaringClass,
						CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
						binding.problemId());
				}
				if (!((FieldBinding) binding).isStatic())
					return new ProblemFieldBinding(
						(FieldBinding) binding,
						((FieldBinding) binding).declaringClass,
						CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
						ProblemReasons.NonStaticReferenceInStaticContext);
				break foundField; // binding is now a field
			}
			if ((binding = findMemberType(nextName, typeBinding)) == null) {
				return new ProblemBinding(
					CharOperation.subarray(compoundName, 0, currentIndex),
					typeBinding,
					ProblemReasons.NotFound);
			}
			if (!binding.isValidBinding()) {
				return new ProblemReferenceBinding(
					CharOperation.subarray(compoundName, 0, currentIndex),
					(ReferenceBinding)((ReferenceBinding)binding).closestMatch(),
					binding.problemId());
			}
		}
		return binding;
	}

	VariableBinding variableBinding = (VariableBinding) binding;
	while (currentIndex < length) {
		TypeBinding typeBinding = variableBinding.type;
		if (typeBinding == null) {
			return new ProblemFieldBinding(
				null,
				null,
				CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
				ProblemReasons.NotFound);
		}
		TypeBinding receiverType = typeBinding.capture(this, invocationSite.sourceEnd());
		variableBinding = findField(receiverType, compoundName[currentIndex++], invocationSite, true /*resolve*/);
		if (variableBinding == null) {
			return new ProblemFieldBinding(
				null,
				receiverType instanceof ReferenceBinding ? (ReferenceBinding) receiverType : null,
				CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'),
				ProblemReasons.NotFound);
		}
		if (!variableBinding.isValidBinding())
			return variableBinding;
	}
	return variableBinding;
}

/*
 * This retrieves the argument that maps to an enclosing instance of the suitable type,
 * 	if not found then answers nil -- do not create one
 *
 *		#implicitThis		  	 			: the implicit this will be ok
 *		#((arg) this$n)						: available as a constructor arg
 * 		#((arg) this$n ... this$p) 			: available as as a constructor arg + a sequence of fields
 * 		#((fieldDescr) this$n ... this$p) 	: available as a sequence of fields
 * 		nil 		 											: not found
 *
 * 	Note that this algorithm should answer the shortest possible sequence when
 * 		shortcuts are available:
 * 				this$0 . this$0 . this$0
 * 		instead of
 * 				this$2 . this$1 . this$0 . this$1 . this$0
 * 		thus the code generation will be more compact and runtime faster
 */
public VariableBinding[] getEmulationPath(LocalVariableBinding outerLocalVariable) {
	MethodScope currentMethodScope = methodScope();
	SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();

	// identity check
	BlockScope variableScope = outerLocalVariable.declaringScope;
	if (variableScope == null /*val$this$0*/ || currentMethodScope == variableScope.methodScope()) {
		return new VariableBinding[] { outerLocalVariable };
		// implicit this is good enough
	}
	// use synthetic constructor arguments if possible
	if (currentMethodScope.isInsideInitializerOrConstructor()
		&& (sourceType.isNestedType())) {
		SyntheticArgumentBinding syntheticArg;
		if ((syntheticArg = ((NestedTypeBinding) sourceType).getSyntheticArgument(outerLocalVariable)) != null) {
			return new VariableBinding[] { syntheticArg };
		}
	}
	// use a synthetic field then
	if (!currentMethodScope.isStatic) {
		FieldBinding syntheticField;
		if ((syntheticField = sourceType.getSyntheticField(outerLocalVariable)) != null) {
			return new VariableBinding[] { syntheticField };
		}
	}
	return null;
}

/*
 * This retrieves the argument that maps to an enclosing instance of the suitable type,
 * 	if not found then answers nil -- do not create one
 *
 *		#implicitThis		  	 											:  the implicit this will be ok
 *		#((arg) this$n)													: available as a constructor arg
 * 	#((arg) this$n access$m... access$p) 		: available as as a constructor arg + a sequence of synthetic accessors to synthetic fields
 * 	#((fieldDescr) this$n access#m... access$p)	: available as a first synthetic field + a sequence of synthetic accessors to synthetic fields
 * 	null 		 															: not found
 *	jls 15.9.2 + http://www.ergnosis.com/java-spec-report/java-language/jls-8.8.5.1-d.html
 */
public Object[] getEmulationPath(ReferenceBinding targetEnclosingType, boolean onlyExactMatch, boolean denyEnclosingArgInConstructorCall) {
	MethodScope currentMethodScope = methodScope();
	SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();

	// use 'this' if possible
	if (!currentMethodScope.isStatic && !currentMethodScope.isConstructorCall) {
		if (sourceType == targetEnclosingType || (!onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
			return BlockScope.EmulationPathToImplicitThis; // implicit this is good enough
		}
	}
	if (!sourceType.isNestedType() || sourceType.isStatic()) { // no emulation from within non-inner types
		if (currentMethodScope.isConstructorCall) {
			return BlockScope.NoEnclosingInstanceInConstructorCall;
		} else if (currentMethodScope.isStatic){
			return BlockScope.NoEnclosingInstanceInStaticContext;
		}
		return null;
	}
	boolean insideConstructor = currentMethodScope.isInsideInitializerOrConstructor();
	// use synthetic constructor arguments if possible
	if (insideConstructor) {
		SyntheticArgumentBinding syntheticArg;
		if ((syntheticArg = ((NestedTypeBinding) sourceType).getSyntheticArgument(targetEnclosingType, onlyExactMatch)) != null) {
			// reject allocation and super constructor call
			if (denyEnclosingArgInConstructorCall
					&& currentMethodScope.isConstructorCall
					&& (sourceType == targetEnclosingType || (!onlyExactMatch && sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null))) {
				return BlockScope.NoEnclosingInstanceInConstructorCall;
			}
			return new Object[] { syntheticArg };
		}
	}

	// use a direct synthetic field then
	if (currentMethodScope.isStatic) {
		return BlockScope.NoEnclosingInstanceInStaticContext;
	}
	if (sourceType.isAnonymousType()) {
		ReferenceBinding enclosingType = sourceType.enclosingType();
		if (enclosingType.isNestedType()) {
			NestedTypeBinding nestedEnclosingType = (NestedTypeBinding) enclosingType;
			SyntheticArgumentBinding enclosingArgument = nestedEnclosingType.getSyntheticArgument(nestedEnclosingType.enclosingType(), onlyExactMatch);
			if (enclosingArgument != null) {
				FieldBinding syntheticField = sourceType.getSyntheticField(enclosingArgument);
				if (syntheticField != null) {
					if (syntheticField.type == targetEnclosingType || (!onlyExactMatch && ((ReferenceBinding)syntheticField.type).findSuperTypeOriginatingFrom(targetEnclosingType) != null))
						return new Object[] { syntheticField };
				}
			}
		}
	}
	FieldBinding syntheticField = sourceType.getSyntheticField(targetEnclosingType, onlyExactMatch);
	if (syntheticField != null) {
		if (currentMethodScope.isConstructorCall){
			return BlockScope.NoEnclosingInstanceInConstructorCall;
		}
		return new Object[] { syntheticField };
	}

	// could be reached through a sequence of enclosing instance link (nested members)
	Object[] path = new Object[2]; // probably at least 2 of them
	ReferenceBinding currentType = sourceType.enclosingType();
	if (insideConstructor) {
		path[0] = ((NestedTypeBinding) sourceType).getSyntheticArgument(currentType, onlyExactMatch);
	} else {
		if (currentMethodScope.isConstructorCall){
			return BlockScope.NoEnclosingInstanceInConstructorCall;
		}
		path[0] = sourceType.getSyntheticField(currentType, onlyExactMatch);
	}
	if (path[0] != null) { // keep accumulating

		int count = 1;
		ReferenceBinding currentEnclosingType;
		while ((currentEnclosingType = currentType.enclosingType()) != null) {

			//done?
			if (currentType == targetEnclosingType
				|| (!onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null))	break;

			if (currentMethodScope != null) {
				currentMethodScope = currentMethodScope.enclosingMethodScope();
				if (currentMethodScope != null && currentMethodScope.isConstructorCall){
					return BlockScope.NoEnclosingInstanceInConstructorCall;
				}
				if (currentMethodScope != null && currentMethodScope.isStatic){
					return BlockScope.NoEnclosingInstanceInStaticContext;
				}
			}

			syntheticField = ((NestedTypeBinding) currentType).getSyntheticField(currentEnclosingType, onlyExactMatch);
			if (syntheticField == null) break;

			// append inside the path
			if (count == path.length) {
				System.arraycopy(path, 0, (path = new Object[count + 1]), 0, count);
			}
			// private access emulation is necessary since synthetic field is private
			path[count++] = ((SourceTypeBinding) syntheticField.declaringClass).addSyntheticMethod(syntheticField, true/*read*/, false /*not super access*/);
			currentType = currentEnclosingType;
		}
		if (currentType == targetEnclosingType
			|| (!onlyExactMatch && currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)) {
			return path;
		}
	}
	return null;
}

/* Answer true if the variable name already exists within the receiver's scope.
 */
public final boolean isDuplicateLocalVariable(char[] name) {
	BlockScope current = this;
	while (true) {
		for (int i = 0; i < this.localIndex; i++) {
			if (CharOperation.equals(name, current.locals[i].name))
				return true;
		}
		if (current.kind != Scope.BLOCK_SCOPE) return false;
		current = (BlockScope)current.parent;
	}
}

public int maxShiftedOffset() {
	int max = -1;
	if (this.shiftScopes != null){
		for (int i = 0, length = this.shiftScopes.length; i < length; i++){
			if (this.shiftScopes[i] != null) {
				int subMaxOffset = this.shiftScopes[i].maxOffset;
				if (subMaxOffset > max) max = subMaxOffset;
			}
		}
	}
	return max;
}

/**
 * Returns true if the context requires to check initialization of final blank fields.
 * in other words, it is inside an initializer, a constructor or a clinit
 */
public final boolean needBlankFinalFieldInitializationCheck(FieldBinding binding) {
	boolean isStatic = binding.isStatic();
	ReferenceBinding fieldDeclaringClass = binding.declaringClass;
	// loop in enclosing context, until reaching the field declaring context
	MethodScope methodScope = methodScope();
	while (methodScope != null) {
		if (methodScope.isStatic != isStatic)
			return false;
		if (!methodScope.isInsideInitializer() // inside initializer
				&& !((AbstractMethodDeclaration) methodScope.referenceContext).isInitializationMethod()) { // inside constructor or clinit
			return false; // found some non-initializer context
		}
		ReferenceBinding enclosingType = methodScope.enclosingReceiverType();
		if (enclosingType == fieldDeclaringClass) {
			return true; // found the field context, no need to check any further
		}
		if (!enclosingType.erasure().isAnonymousType()) {
			return false; // only check inside anonymous type
		}
		methodScope = methodScope.enclosingMethodScope();
	}
	return false;
}

/* Answer the problem reporter to use for raising new problems.
 *
 * Note that as a side-effect, this updates the current reference context
 * (unit, type or method) in case the problem handler decides it is necessary
 * to abort.
 */
public ProblemReporter problemReporter() {
	return methodScope().problemReporter();
}

/*
 * Code responsible to request some more emulation work inside the invocation type, so as to supply
 * correct synthetic arguments to any allocation of the target type.
 */
public void propagateInnerEmulation(ReferenceBinding targetType, boolean isEnclosingInstanceSupplied) {
	// no need to propagate enclosing instances, they got eagerly allocated already.

	SyntheticArgumentBinding[] syntheticArguments;
	if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
		for (int i = 0, max = syntheticArguments.length; i < max; i++) {
			SyntheticArgumentBinding syntheticArg = syntheticArguments[i];
			// need to filter out the one that could match a supplied enclosing instance
			if (!(isEnclosingInstanceSupplied
				&& (syntheticArg.type == targetType.enclosingType()))) {
				emulateOuterAccess(syntheticArg.actualOuterLocalVariable);
			}
		}
	}
}

/* Answer the reference type of this scope.
 *
 * It is the nearest enclosing type of this scope.
 */
public TypeDeclaration referenceType() {
	return methodScope().referenceType();
}

/*
 * Answer the index of this scope relatively to its parent.
 * For method scope, answers -1 (not a classScope relative position)
 */
public int scopeIndex() {
	if (this instanceof MethodScope) return -1;
	BlockScope parentScope = (BlockScope)this.parent;
	Scope[] parentSubscopes = parentScope.subscopes;
	for (int i = 0, max = parentScope.subscopeCount; i < max; i++) {
		if (parentSubscopes[i] == this) return i;
	}
	return -1;
}

// start position in this scope - for ordering scopes vs. variables
int startIndex() {
	return this.startIndex;
}

public String toString() {
	return toString(0);
}

public String toString(int tab) {
	String s = basicToString(tab);
	for (int i = 0; i < this.subscopeCount; i++)
		if (this.subscopes[i] instanceof BlockScope)
			s += ((BlockScope) this.subscopes[i]).toString(tab + 1) + "\n"; //$NON-NLS-1$
	return s;
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=318682
public void resetEnclosingMethodStaticFlag() {
	MethodScope methodScope = methodScope();
	while (methodScope != null && methodScope.referenceContext instanceof MethodDeclaration) {
		MethodDeclaration methodDeclaration= (MethodDeclaration) methodScope.referenceContext;
		methodDeclaration.bits &= ~ASTNode.CanBeStatic;
		ClassScope enclosingClassScope = methodScope.enclosingClassScope();
		if (enclosingClassScope != null) {
			methodScope = enclosingClassScope.methodScope();
		} else {
			break;
		}
	}
}
}
