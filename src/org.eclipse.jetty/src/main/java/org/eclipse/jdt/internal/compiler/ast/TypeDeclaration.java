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
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.parser.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.Util;

public class TypeDeclaration extends Statement implements ProblemSeverities, ReferenceContext {
	// Type decl kinds
	public static final int CLASS_DECL = 1;
	public static final int INTERFACE_DECL = 2;
	public static final int ENUM_DECL = 3;
	public static final int ANNOTATION_TYPE_DECL = 4;

	public int modifiers = ClassFileConstants.AccDefault;
	public int modifiersSourceStart;
	public Annotation[] annotations;
	public char[] name;
	public TypeReference superclass;
	public TypeReference[] superInterfaces;
	public FieldDeclaration[] fields;
	public AbstractMethodDeclaration[] methods;
	public TypeDeclaration[] memberTypes;
	public SourceTypeBinding binding;
	public ClassScope scope;
	public MethodScope initializerScope;
	public MethodScope staticInitializerScope;
	public boolean ignoreFurtherInvestigation = false;
	public int maxFieldCount;
	public int declarationSourceStart;
	public int declarationSourceEnd;
	public int bodyStart;
	public int bodyEnd; // doesn't include the trailing comment if any.
	public CompilationResult compilationResult;
	public MethodDeclaration[] missingAbstractMethods;
	public Javadoc javadoc;

	public QualifiedAllocationExpression allocation; // for anonymous only
	public TypeDeclaration enclosingType; // for member types only

	public FieldBinding enumValuesSyntheticfield; 	// for enum
	public int enumConstantsCounter;

	// 1.5 support
	public TypeParameter[] typeParameters;

public TypeDeclaration(CompilationResult compilationResult){
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
		case AbortMethod :
			throw new AbortMethod(this.compilationResult, problem);
		default :
			throw new AbortType(this.compilationResult, problem);
	}
}

/**
 * This method is responsible for adding a <clinit> method declaration to the type method collections.
 * Note that this implementation is inserting it in first place (as VAJ or javac), and that this
 * impacts the behavior of the method ConstantPool.resetForClinit(int. int), in so far as
 * the latter will have to reset the constant pool state accordingly (if it was added first, it does
 * not need to preserve some of the method specific cached entries since this will be the first method).
 * inserts the clinit method declaration in the first position.
 *
 * @see org.eclipse.jdt.internal.compiler.codegen.ConstantPool#resetForClinit(int, int)
 */
public final void addClinit() {
	//see comment on needClassInitMethod
	if (needClassInitMethod()) {
		int length;
		AbstractMethodDeclaration[] methodDeclarations;
		if ((methodDeclarations = this.methods) == null) {
			length = 0;
			methodDeclarations = new AbstractMethodDeclaration[1];
		} else {
			length = methodDeclarations.length;
			System.arraycopy(
				methodDeclarations,
				0,
				(methodDeclarations = new AbstractMethodDeclaration[length + 1]),
				1,
				length);
		}
		Clinit clinit = new Clinit(this.compilationResult);
		methodDeclarations[0] = clinit;
		// clinit is added in first location, so as to minimize the use of ldcw (big consumer of constant inits)
		clinit.declarationSourceStart = clinit.sourceStart = this.sourceStart;
		clinit.declarationSourceEnd = clinit.sourceEnd = this.sourceEnd;
		clinit.bodyEnd = this.sourceEnd;
		this.methods = methodDeclarations;
	}
}

/*
 * INTERNAL USE ONLY - Creates a fake method declaration for the corresponding binding.
 * It is used to report errors for missing abstract methods.
 */
public MethodDeclaration addMissingAbstractMethodFor(MethodBinding methodBinding) {
	TypeBinding[] argumentTypes = methodBinding.parameters;
	int argumentsLength = argumentTypes.length;
	//the constructor
	MethodDeclaration methodDeclaration = new MethodDeclaration(this.compilationResult);
	methodDeclaration.selector = methodBinding.selector;
	methodDeclaration.sourceStart = this.sourceStart;
	methodDeclaration.sourceEnd = this.sourceEnd;
	methodDeclaration.modifiers = methodBinding.getAccessFlags() & ~ClassFileConstants.AccAbstract;

	if (argumentsLength > 0) {
		String baseName = "arg";//$NON-NLS-1$
		Argument[] arguments = (methodDeclaration.arguments = new Argument[argumentsLength]);
		for (int i = argumentsLength; --i >= 0;) {
			arguments[i] = new Argument((baseName + i).toCharArray(), 0L, null /*type ref*/, ClassFileConstants.AccDefault);
		}
	}

	//adding the constructor in the methods list
	if (this.missingAbstractMethods == null) {
		this.missingAbstractMethods = new MethodDeclaration[] { methodDeclaration };
	} else {
		MethodDeclaration[] newMethods;
		System.arraycopy(
			this.missingAbstractMethods,
			0,
			newMethods = new MethodDeclaration[this.missingAbstractMethods.length + 1],
			1,
			this.missingAbstractMethods.length);
		newMethods[0] = methodDeclaration;
		this.missingAbstractMethods = newMethods;
	}

	//============BINDING UPDATE==========================
	methodDeclaration.binding = new MethodBinding(
			methodDeclaration.modifiers | ClassFileConstants.AccSynthetic, //methodDeclaration
			methodBinding.selector,
			methodBinding.returnType,
			argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, //arguments bindings
			methodBinding.thrownExceptions, //exceptions
			this.binding); //declaringClass

	methodDeclaration.scope = new MethodScope(this.scope, methodDeclaration, true);
	methodDeclaration.bindArguments();

/*		if (binding.methods == null) {
			binding.methods = new MethodBinding[] { methodDeclaration.binding };
		} else {
			MethodBinding[] newMethods;
			System.arraycopy(
				binding.methods,
				0,
				newMethods = new MethodBinding[binding.methods.length + 1],
				1,
				binding.methods.length);
			newMethods[0] = methodDeclaration.binding;
			binding.methods = newMethods;
		}*/
	//===================================================

	return methodDeclaration;
}

/**
 *	Flow analysis for a local innertype
 *
 */
public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	if (this.ignoreFurtherInvestigation)
		return flowInfo;
	try {
		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
			this.bits |= ASTNode.IsReachable;
			LocalTypeBinding localType = (LocalTypeBinding) this.binding;
			localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
		}
		manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
		updateMaxFieldCount(); // propagate down the max field count
		internalAnalyseCode(flowContext, flowInfo);
	} catch (AbortType e) {
		this.ignoreFurtherInvestigation = true;
	}
	return flowInfo;
}

/**
 *	Flow analysis for a member innertype
 *
 */
public void analyseCode(ClassScope enclosingClassScope) {
	if (this.ignoreFurtherInvestigation)
		return;
	try {
		// propagate down the max field count
		updateMaxFieldCount();
		internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
	} catch (AbortType e) {
		this.ignoreFurtherInvestigation = true;
	}
}

/**
 *	Flow analysis for a local member innertype
 *
 */
public void analyseCode(ClassScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
	if (this.ignoreFurtherInvestigation)
		return;
	try {
		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
			this.bits |= ASTNode.IsReachable;
			LocalTypeBinding localType = (LocalTypeBinding) this.binding;
			localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
		}
		manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
		updateMaxFieldCount(); // propagate down the max field count
		internalAnalyseCode(flowContext, flowInfo);
	} catch (AbortType e) {
		this.ignoreFurtherInvestigation = true;
	}
}

/**
 *	Flow analysis for a package member type
 *
 */
public void analyseCode(CompilationUnitScope unitScope) {
	if (this.ignoreFurtherInvestigation)
		return;
	try {
		internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
	} catch (AbortType e) {
		this.ignoreFurtherInvestigation = true;
	}
}

/**
 * Check for constructor vs. method with no return type.
 * Answers true if at least one constructor is defined
 */
public boolean checkConstructors(Parser parser) {
	//if a constructor has not the name of the type,
	//convert it into a method with 'null' as its return type
	boolean hasConstructor = false;
	if (this.methods != null) {
		for (int i = this.methods.length; --i >= 0;) {
			AbstractMethodDeclaration am;
			if ((am = this.methods[i]).isConstructor()) {
				if (!CharOperation.equals(am.selector, this.name)) {
					// the constructor was in fact a method with no return type
					// unless an explicit constructor call was supplied
					ConstructorDeclaration c = (ConstructorDeclaration) am;
					if (c.constructorCall == null || c.constructorCall.isImplicitSuper()) { //changed to a method
						MethodDeclaration m = parser.convertToMethodDeclaration(c, this.compilationResult);
						this.methods[i] = m;
					}
				} else {
					switch (kind(this.modifiers)) {
						case TypeDeclaration.INTERFACE_DECL :
							// report the problem and continue the parsing
							parser.problemReporter().interfaceCannotHaveConstructors((ConstructorDeclaration) am);
							break;
						case TypeDeclaration.ANNOTATION_TYPE_DECL :
							// report the problem and continue the parsing
							parser.problemReporter().annotationTypeDeclarationCannotHaveConstructor((ConstructorDeclaration) am);
							break;

					}
					hasConstructor = true;
				}
			}
		}
	}
	return hasConstructor;
}

public CompilationResult compilationResult() {
	return this.compilationResult;
}

public ConstructorDeclaration createDefaultConstructor(	boolean needExplicitConstructorCall, boolean needToInsert) {
	//Add to method'set, the default constuctor that just recall the
	//super constructor with no arguments
	//The arguments' type will be positionned by the TC so just use
	//the default int instead of just null (consistency purpose)

	//the constructor
	ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
	constructor.bits |= ASTNode.IsDefaultConstructor;
	constructor.selector = this.name;
	constructor.modifiers = this.modifiers & ExtraCompilerModifiers.AccVisibilityMASK;

	//if you change this setting, please update the
	//SourceIndexer2.buildTypeDeclaration(TypeDeclaration,char[]) method
	constructor.declarationSourceStart = constructor.sourceStart = this.sourceStart;
	constructor.declarationSourceEnd =
		constructor.sourceEnd = constructor.bodyEnd = this.sourceEnd;

	//the super call inside the constructor
	if (needExplicitConstructorCall) {
		constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
		constructor.constructorCall.sourceStart = this.sourceStart;
		constructor.constructorCall.sourceEnd = this.sourceEnd;
	}

	//adding the constructor in the methods list: rank is not critical since bindings will be sorted
	if (needToInsert) {
		if (this.methods == null) {
			this.methods = new AbstractMethodDeclaration[] { constructor };
		} else {
			AbstractMethodDeclaration[] newMethods;
			System.arraycopy(
				this.methods,
				0,
				newMethods = new AbstractMethodDeclaration[this.methods.length + 1],
				1,
				this.methods.length);
			newMethods[0] = constructor;
			this.methods = newMethods;
		}
	}
	return constructor;
}

// anonymous type constructor creation: rank is important since bindings already got sorted
public MethodBinding createDefaultConstructorWithBinding(MethodBinding inheritedConstructorBinding, boolean eraseThrownExceptions) {
	//Add to method'set, the default constuctor that just recall the
	//super constructor with the same arguments
	String baseName = "$anonymous"; //$NON-NLS-1$
	TypeBinding[] argumentTypes = inheritedConstructorBinding.parameters;
	int argumentsLength = argumentTypes.length;
	//the constructor
	ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
	constructor.selector = new char[] { 'x' }; //no maining
	constructor.sourceStart = this.sourceStart;
	constructor.sourceEnd = this.sourceEnd;
	int newModifiers = this.modifiers & ExtraCompilerModifiers.AccVisibilityMASK;
	if (inheritedConstructorBinding.isVarargs()) {
		newModifiers |= ClassFileConstants.AccVarargs;
	}
	constructor.modifiers = newModifiers;
	constructor.bits |= ASTNode.IsDefaultConstructor;

	if (argumentsLength > 0) {
		Argument[] arguments = (constructor.arguments = new Argument[argumentsLength]);
		for (int i = argumentsLength; --i >= 0;) {
			arguments[i] = new Argument((baseName + i).toCharArray(), 0L, null /*type ref*/, ClassFileConstants.AccDefault);
		}
	}
	//the super call inside the constructor
	constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
	constructor.constructorCall.sourceStart = this.sourceStart;
	constructor.constructorCall.sourceEnd = this.sourceEnd;

	if (argumentsLength > 0) {
		Expression[] args;
		args = constructor.constructorCall.arguments = new Expression[argumentsLength];
		for (int i = argumentsLength; --i >= 0;) {
			args[i] = new SingleNameReference((baseName + i).toCharArray(), 0L);
		}
	}

	//adding the constructor in the methods list
	if (this.methods == null) {
		this.methods = new AbstractMethodDeclaration[] { constructor };
	} else {
		AbstractMethodDeclaration[] newMethods;
		System.arraycopy(this.methods, 0, newMethods = new AbstractMethodDeclaration[this.methods.length + 1], 1, this.methods.length);
		newMethods[0] = constructor;
		this.methods = newMethods;
	}

	//============BINDING UPDATE==========================
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=277643, align with javac on JLS 15.12.2.6
	ReferenceBinding[] thrownExceptions = eraseThrownExceptions
			? this.scope.environment().convertToRawTypes(inheritedConstructorBinding.thrownExceptions, true, true)
			: inheritedConstructorBinding.thrownExceptions;

	SourceTypeBinding sourceType = this.binding;
	constructor.binding = new MethodBinding(
			constructor.modifiers, //methodDeclaration
			argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, //arguments bindings
			thrownExceptions, //exceptions
			sourceType); //declaringClass
	constructor.binding.tagBits |= (inheritedConstructorBinding.tagBits & TagBits.HasMissingType);
	constructor.binding.modifiers |= ExtraCompilerModifiers.AccIsDefaultConstructor;

	constructor.scope = new MethodScope(this.scope, constructor, true);
	constructor.bindArguments();
	constructor.constructorCall.resolve(constructor.scope);

	MethodBinding[] methodBindings = sourceType.methods(); // trigger sorting
	int length;
	System.arraycopy(methodBindings, 0, methodBindings = new MethodBinding[(length = methodBindings.length) + 1], 1, length);
	methodBindings[0] = constructor.binding;
	if (++length > 1)
		ReferenceBinding.sortMethods(methodBindings, 0, length);	// need to resort, since could be valid methods ahead (140643) - DOM needs eager sorting
	sourceType.setMethods(methodBindings);
	//===================================================

	return constructor.binding;
}

/**
 * Find the matching parse node, answers null if nothing found
 */
public FieldDeclaration declarationOf(FieldBinding fieldBinding) {
	if (fieldBinding != null && this.fields != null) {
		for (int i = 0, max = this.fields.length; i < max; i++) {
			FieldDeclaration fieldDecl;
			if ((fieldDecl = this.fields[i]).binding == fieldBinding)
				return fieldDecl;
		}
	}
	return null;
}

/**
 * Find the matching parse node, answers null if nothing found
 */
public TypeDeclaration declarationOf(MemberTypeBinding memberTypeBinding) {
	if (memberTypeBinding != null && this.memberTypes != null) {
		for (int i = 0, max = this.memberTypes.length; i < max; i++) {
			TypeDeclaration memberTypeDecl;
			if ((memberTypeDecl = this.memberTypes[i]).binding == memberTypeBinding)
				return memberTypeDecl;
		}
	}
	return null;
}

/**
 * Find the matching parse node, answers null if nothing found
 */
public AbstractMethodDeclaration declarationOf(MethodBinding methodBinding) {
	if (methodBinding != null && this.methods != null) {
		for (int i = 0, max = this.methods.length; i < max; i++) {
			AbstractMethodDeclaration methodDecl;

			if ((methodDecl = this.methods[i]).binding == methodBinding)
				return methodDecl;
		}
	}
	return null;
}

/**
 * Finds the matching type amoung this type's member types.
 * Returns null if no type with this name is found.
 * The type name is a compound name relative to this type
 * e.g. if this type is X and we're looking for Y.X.A.B
 *     then a type name would be {X, A, B}
 */
public TypeDeclaration declarationOfType(char[][] typeName) {
	int typeNameLength = typeName.length;
	if (typeNameLength < 1 || !CharOperation.equals(typeName[0], this.name)) {
		return null;
	}
	if (typeNameLength == 1) {
		return this;
	}
	char[][] subTypeName = new char[typeNameLength - 1][];
	System.arraycopy(typeName, 1, subTypeName, 0, typeNameLength - 1);
	for (int i = 0; i < this.memberTypes.length; i++) {
		TypeDeclaration typeDecl = this.memberTypes[i].declarationOfType(subTypeName);
		if (typeDecl != null) {
			return typeDecl;
		}
	}
	return null;
}

/**
 * Generic bytecode generation for type
 */
public void generateCode(ClassFile enclosingClassFile) {
	if ((this.bits & ASTNode.HasBeenGenerated) != 0)
		return;
	this.bits |= ASTNode.HasBeenGenerated;
	if (this.ignoreFurtherInvestigation) {
		if (this.binding == null)
			return;
		ClassFile.createProblemType(
			this,
			this.scope.referenceCompilationUnit().compilationResult);
		return;
	}
	try {
		// create the result for a compiled type
		ClassFile classFile = ClassFile.getNewInstance(this.binding);
		classFile.initialize(this.binding, enclosingClassFile, false);
		if (this.binding.isMemberType()) {
			classFile.recordInnerClasses(this.binding);
		} else if (this.binding.isLocalType()) {
			enclosingClassFile.recordInnerClasses(this.binding);
			classFile.recordInnerClasses(this.binding);
		}
		TypeVariableBinding[] typeVariables = this.binding.typeVariables();
		for (int i = 0, max = typeVariables.length; i < max; i++) {
			TypeVariableBinding typeVariableBinding = typeVariables[i];
			if ((typeVariableBinding.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
				Util.recordNestedType(classFile, typeVariableBinding);
			}
		}

		// generate all fiels
		classFile.addFieldInfos();

		if (this.memberTypes != null) {
			for (int i = 0, max = this.memberTypes.length; i < max; i++) {
				TypeDeclaration memberType = this.memberTypes[i];
				classFile.recordInnerClasses(memberType.binding);
				memberType.generateCode(this.scope, classFile);
			}
		}
		// generate all methods
		classFile.setForMethodInfos();
		if (this.methods != null) {
			for (int i = 0, max = this.methods.length; i < max; i++) {
				this.methods[i].generateCode(this.scope, classFile);
			}
		}
		// generate all synthetic and abstract methods
		classFile.addSpecialMethods();

		if (this.ignoreFurtherInvestigation) { // trigger problem type generation for code gen errors
			throw new AbortType(this.scope.referenceCompilationUnit().compilationResult, null);
		}

		// finalize the compiled type result
		classFile.addAttributes();
		this.scope.referenceCompilationUnit().compilationResult.record(
			this.binding.constantPoolName(),
			classFile);
	} catch (AbortType e) {
		if (this.binding == null)
			return;
		ClassFile.createProblemType(
			this,
			this.scope.referenceCompilationUnit().compilationResult);
	}
}

/**
 * Bytecode generation for a local inner type (API as a normal statement code gen)
 */
public void generateCode(BlockScope blockScope, CodeStream codeStream) {
	if ((this.bits & ASTNode.IsReachable) == 0) {
		return;
	}
	if ((this.bits & ASTNode.HasBeenGenerated) != 0) return;
	int pc = codeStream.position;
	if (this.binding != null) {
		SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding) this.binding).syntheticEnclosingInstances();
		for (int i = 0, slotSize = 0, count = enclosingInstances == null ? 0 : enclosingInstances.length; i < count; i++){
			SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
			enclosingInstance.resolvedPosition = ++slotSize; // shift by 1 to leave room for aload0==this
			if (slotSize > 0xFF) { // no more than 255 words of arguments
				blockScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, blockScope.referenceType());
			}
		}
	}
	generateCode(codeStream.classFile);
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

/**
 * Bytecode generation for a member inner type
 */
public void generateCode(ClassScope classScope, ClassFile enclosingClassFile) {
	if ((this.bits & ASTNode.HasBeenGenerated) != 0) return;
	if (this.binding != null) {
		SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding) this.binding).syntheticEnclosingInstances();
		for (int i = 0, slotSize = 0, count = enclosingInstances == null ? 0 : enclosingInstances.length; i < count; i++){
			SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
			enclosingInstance.resolvedPosition = ++slotSize; // shift by 1 to leave room for aload0==this
			if (slotSize > 0xFF) { // no more than 255 words of arguments
				classScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, classScope.referenceType());
			}
		}
	}
	generateCode(enclosingClassFile);
}

/**
 * Bytecode generation for a package member
 */
public void generateCode(CompilationUnitScope unitScope) {
	generateCode((ClassFile) null);
}

public boolean hasErrors() {
	return this.ignoreFurtherInvestigation;
}

/**
 *	Common flow analysis for all types
 */
private void internalAnalyseCode(FlowContext flowContext, FlowInfo flowInfo) {
	if (!this.binding.isUsed() && this.binding.isOrEnclosedByPrivateType()) {
		if (!this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError) {
			this.scope.problemReporter().unusedPrivateType(this);
		}
	}
	InitializationFlowContext initializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.initializerScope);
	InitializationFlowContext staticInitializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.staticInitializerScope);
	FlowInfo nonStaticFieldInfo = flowInfo.unconditionalFieldLessCopy();
	FlowInfo staticFieldInfo = flowInfo.unconditionalFieldLessCopy();
	if (this.fields != null) {
		for (int i = 0, count = this.fields.length; i < count; i++) {
			FieldDeclaration field = this.fields[i];
			if (field.isStatic()) {
				if ((staticFieldInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
					field.bits &= ~ASTNode.IsReachable;

				/*if (field.isField()){
					staticInitializerContext.handledExceptions = NoExceptions; // no exception is allowed jls8.3.2
				} else {*/
				staticInitializerContext.handledExceptions = Binding.ANY_EXCEPTION; // tolerate them all, and record them
				/*}*/
				staticFieldInfo = field.analyseCode(this.staticInitializerScope, staticInitializerContext, staticFieldInfo);
				// in case the initializer is not reachable, use a reinitialized flowInfo and enter a fake reachable
				// branch, since the previous initializer already got the blame.
				if (staticFieldInfo == FlowInfo.DEAD_END) {
					this.staticInitializerScope.problemReporter().initializerMustCompleteNormally(field);
					staticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
				}
			} else {
				if ((nonStaticFieldInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
					field.bits &= ~ASTNode.IsReachable;

				/*if (field.isField()){
					initializerContext.handledExceptions = NoExceptions; // no exception is allowed jls8.3.2
				} else {*/
					initializerContext.handledExceptions = Binding.ANY_EXCEPTION; // tolerate them all, and record them
				/*}*/
				nonStaticFieldInfo = field.analyseCode(this.initializerScope, initializerContext, nonStaticFieldInfo);
				// in case the initializer is not reachable, use a reinitialized flowInfo and enter a fake reachable
				// branch, since the previous initializer already got the blame.
				if (nonStaticFieldInfo == FlowInfo.DEAD_END) {
					this.initializerScope.problemReporter().initializerMustCompleteNormally(field);
					nonStaticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
				}
			}
		}
	}
	if (this.memberTypes != null) {
		for (int i = 0, count = this.memberTypes.length; i < count; i++) {
			if (flowContext != null){ // local type
				this.memberTypes[i].analyseCode(this.scope, flowContext, nonStaticFieldInfo.copy().setReachMode(flowInfo.reachMode())); // reset reach mode in case initializers did abrupt completely
			} else {
				this.memberTypes[i].analyseCode(this.scope);
			}
		}
	}
	if (this.methods != null) {
		UnconditionalFlowInfo outerInfo = flowInfo.unconditionalFieldLessCopy();
		FlowInfo constructorInfo = nonStaticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo);
		for (int i = 0, count = this.methods.length; i < count; i++) {
			AbstractMethodDeclaration method = this.methods[i];
			if (method.ignoreFurtherInvestigation)
				continue;
			if (method.isInitializationMethod()) {
				if (method.isStatic()) { // <clinit>
					method.analyseCode(
						this.scope,
						staticInitializerContext,
						staticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo));
				} else { // constructor
					((ConstructorDeclaration)method).analyseCode(this.scope, initializerContext, constructorInfo.copy(), flowInfo.reachMode());
				}
			} else { // regular method
				method.analyseCode(this.scope, null, flowInfo.copy());
			}
		}
	}
	// enable enum support ?
	if (this.binding.isEnum() && !this.binding.isAnonymousType()) {
		this.enumValuesSyntheticfield = this.binding.addSyntheticFieldForEnumValues();
	}
}

public final static int kind(int flags) {
	switch (flags & (ClassFileConstants.AccInterface|ClassFileConstants.AccAnnotation|ClassFileConstants.AccEnum)) {
		case ClassFileConstants.AccInterface :
			return TypeDeclaration.INTERFACE_DECL;
		case ClassFileConstants.AccInterface|ClassFileConstants.AccAnnotation :
			return TypeDeclaration.ANNOTATION_TYPE_DECL;
		case ClassFileConstants.AccEnum :
			return TypeDeclaration.ENUM_DECL;
		default :
			return TypeDeclaration.CLASS_DECL;
	}
}

/*
 * Access emulation for a local type
 * force to emulation of access to direct enclosing instance.
 * By using the initializer scope, we actually only request an argument emulation, the
 * field is not added until actually used. However we will force allocations to be qualified
 * with an enclosing instance.
 * 15.9.2
 */
public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0) return;
	NestedTypeBinding nestedType = (NestedTypeBinding) this.binding;

	MethodScope methodScope = currentScope.methodScope();
	if (!methodScope.isStatic && !methodScope.isConstructorCall){
		nestedType.addSyntheticArgumentAndField(nestedType.enclosingType());
	}
	// add superclass enclosing instance arg for anonymous types (if necessary)
	if (nestedType.isAnonymousType()) {
		ReferenceBinding superclassBinding = (ReferenceBinding)nestedType.superclass.erasure();
		if (superclassBinding.enclosingType() != null && !superclassBinding.isStatic()) {
			if (!superclassBinding.isLocalType()
					|| ((NestedTypeBinding)superclassBinding).getSyntheticField(superclassBinding.enclosingType(), true) != null){

				nestedType.addSyntheticArgument(superclassBinding.enclosingType());
			}
		}
		// From 1.5 on, provide access to enclosing instance synthetic constructor argument when declared inside constructor call
		// only for direct anonymous type
		//public class X {
		//	void foo() {}
		//	class M {
		//		M(Object o) {}
		//		M() { this(new Object() { void baz() { foo(); }}); } // access to #foo() indirects through constructor synthetic arg: val$this$0
		//	}
		//}
		if (!methodScope.isStatic && methodScope.isConstructorCall && currentScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_5) {
			ReferenceBinding enclosing = nestedType.enclosingType();
			if (enclosing.isNestedType()) {
				NestedTypeBinding nestedEnclosing = (NestedTypeBinding)enclosing;
//					if (nestedEnclosing.findSuperTypeErasingTo(nestedEnclosing.enclosingType()) == null) { // only if not inheriting
					SyntheticArgumentBinding syntheticEnclosingInstanceArgument = nestedEnclosing.getSyntheticArgument(nestedEnclosing.enclosingType(), true);
					if (syntheticEnclosingInstanceArgument != null) {
						nestedType.addSyntheticArgumentAndField(syntheticEnclosingInstanceArgument);
					}
				}
//				}
		}
	}
}

/**
 * Access emulation for a local member type
 * force to emulation of access to direct enclosing instance.
 * By using the initializer scope, we actually only request an argument emulation, the
 * field is not added until actually used. However we will force allocations to be qualified
 * with an enclosing instance.
 *
 * Local member cannot be static.
 */
public void manageEnclosingInstanceAccessIfNecessary(ClassScope currentScope, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
	NestedTypeBinding nestedType = (NestedTypeBinding) this.binding;
	nestedType.addSyntheticArgumentAndField(this.binding.enclosingType());
	}
}

/**
 * A <clinit> will be requested as soon as static fields or assertions are present. It will be eliminated during
 * classfile creation if no bytecode was actually produced based on some optimizations/compiler settings.
 */
public final boolean needClassInitMethod() {
	// always need a <clinit> when assertions are present
	if ((this.bits & ASTNode.ContainsAssertion) != 0)
		return true;

	switch (kind(this.modifiers)) {
		case TypeDeclaration.INTERFACE_DECL:
		case TypeDeclaration.ANNOTATION_TYPE_DECL:
			return this.fields != null; // fields are implicitly statics
		case TypeDeclaration.ENUM_DECL:
			return true; // even if no enum constants, need to set $VALUES array
	}
	if (this.fields != null) {
		for (int i = this.fields.length; --i >= 0;) {
			FieldDeclaration field = this.fields[i];
			//need to test the modifier directly while there is no binding yet
			if ((field.modifiers & ClassFileConstants.AccStatic) != 0)
				return true; // TODO (philippe) shouldn't it check whether field is initializer or has some initial value ?
		}
	}
	return false;
}

public void parseMethods(Parser parser, CompilationUnitDeclaration unit) {
	//connect method bodies
	if (unit.ignoreMethodBodies)
		return;

	//members
	if (this.memberTypes != null) {
		int length = this.memberTypes.length;
		for (int i = 0; i < length; i++) {
			TypeDeclaration typeDeclaration = this.memberTypes[i];
			typeDeclaration.parseMethods(parser, unit);
			this.bits |= (typeDeclaration.bits & ASTNode.HasSyntaxErrors);
		}
	}

	//methods
	if (this.methods != null) {
		int length = this.methods.length;
		for (int i = 0; i < length; i++) {
			AbstractMethodDeclaration abstractMethodDeclaration = this.methods[i];
			abstractMethodDeclaration.parseStatements(parser, unit);
			this.bits |= (abstractMethodDeclaration.bits & ASTNode.HasSyntaxErrors);
		}
	}

	//initializers
	if (this.fields != null) {
		int length = this.fields.length;
		for (int i = 0; i < length; i++) {
			final FieldDeclaration fieldDeclaration = this.fields[i];
			switch(fieldDeclaration.getKind()) {
				case AbstractVariableDeclaration.INITIALIZER:
					((Initializer) fieldDeclaration).parseStatements(parser, this, unit);
					this.bits |= (fieldDeclaration.bits & ASTNode.HasSyntaxErrors);
					break;
			}
		}
	}
}

public StringBuffer print(int indent, StringBuffer output) {
	if (this.javadoc != null) {
		this.javadoc.print(indent, output);
	}
	if ((this.bits & ASTNode.IsAnonymousType) == 0) {
		printIndent(indent, output);
		printHeader(0, output);
	}
	return printBody(indent, output);
}

public StringBuffer printBody(int indent, StringBuffer output) {
	output.append(" {"); //$NON-NLS-1$
	if (this.memberTypes != null) {
		for (int i = 0; i < this.memberTypes.length; i++) {
			if (this.memberTypes[i] != null) {
				output.append('\n');
				this.memberTypes[i].print(indent + 1, output);
			}
		}
	}
	if (this.fields != null) {
		for (int fieldI = 0; fieldI < this.fields.length; fieldI++) {
			if (this.fields[fieldI] != null) {
				output.append('\n');
				this.fields[fieldI].print(indent + 1, output);
			}
		}
	}
	if (this.methods != null) {
		for (int i = 0; i < this.methods.length; i++) {
			if (this.methods[i] != null) {
				output.append('\n');
				this.methods[i].print(indent + 1, output);
			}
		}
	}
	output.append('\n');
	return printIndent(indent, output).append('}');
}

public StringBuffer printHeader(int indent, StringBuffer output) {
	printModifiers(this.modifiers, output);
	if (this.annotations != null) printAnnotations(this.annotations, output);

	switch (kind(this.modifiers)) {
		case TypeDeclaration.CLASS_DECL :
			output.append("class "); //$NON-NLS-1$
			break;
		case TypeDeclaration.INTERFACE_DECL :
			output.append("interface "); //$NON-NLS-1$
			break;
		case TypeDeclaration.ENUM_DECL :
			output.append("enum "); //$NON-NLS-1$
			break;
		case TypeDeclaration.ANNOTATION_TYPE_DECL :
			output.append("@interface "); //$NON-NLS-1$
			break;
	}
	output.append(this.name);
	if (this.typeParameters != null) {
		output.append("<");//$NON-NLS-1$
		for (int i = 0; i < this.typeParameters.length; i++) {
			if (i > 0) output.append( ", "); //$NON-NLS-1$
			this.typeParameters[i].print(0, output);
		}
		output.append(">");//$NON-NLS-1$
	}
	if (this.superclass != null) {
		output.append(" extends ");  //$NON-NLS-1$
		this.superclass.print(0, output);
	}
	if (this.superInterfaces != null && this.superInterfaces.length > 0) {
		switch (kind(this.modifiers)) {
			case TypeDeclaration.CLASS_DECL :
			case TypeDeclaration.ENUM_DECL :
				output.append(" implements "); //$NON-NLS-1$
				break;
			case TypeDeclaration.INTERFACE_DECL :
			case TypeDeclaration.ANNOTATION_TYPE_DECL :
				output.append(" extends "); //$NON-NLS-1$
				break;
		}
		for (int i = 0; i < this.superInterfaces.length; i++) {
			if (i > 0) output.append( ", "); //$NON-NLS-1$
			this.superInterfaces[i].print(0, output);
		}
	}
	return output;
}

public StringBuffer printStatement(int tab, StringBuffer output) {
	return print(tab, output);
}



public void resolve() {
	SourceTypeBinding sourceType = this.binding;
	if (sourceType == null) {
		this.ignoreFurtherInvestigation = true;
		return;
	}
	try {
		boolean old = this.staticInitializerScope.insideTypeAnnotation;
		try {
			this.staticInitializerScope.insideTypeAnnotation = true;
			resolveAnnotations(this.staticInitializerScope, this.annotations, sourceType);
		} finally {
			this.staticInitializerScope.insideTypeAnnotation = old;
		}
		// check @Deprecated annotation
		if ((sourceType.getAnnotationTagBits() & TagBits.AnnotationDeprecated) == 0
				&& (sourceType.modifiers & ClassFileConstants.AccDeprecated) != 0
				&& this.scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_5) {
			this.scope.problemReporter().missingDeprecatedAnnotationForType(this);
		}
		if ((this.bits & ASTNode.UndocumentedEmptyBlock) != 0) {
			this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart-1, this.bodyEnd);
		}
		boolean needSerialVersion =
						this.scope.compilerOptions().getSeverity(CompilerOptions.MissingSerialVersion) != ProblemSeverities.Ignore
						&& sourceType.isClass()
						&& sourceType.findSuperTypeOriginatingFrom(TypeIds.T_JavaIoExternalizable, false /*Externalizable is not a class*/) == null
						&& sourceType.findSuperTypeOriginatingFrom(TypeIds.T_JavaIoSerializable, false /*Serializable is not a class*/) != null;

		if (needSerialVersion) {
			// if Object writeReplace() throws java.io.ObjectStreamException is present, then no serialVersionUID is needed
			// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=101476
			CompilationUnitScope compilationUnitScope = this.scope.compilationUnitScope();
			MethodBinding methodBinding = sourceType.getExactMethod(TypeConstants.WRITEREPLACE, Binding.NO_TYPES, compilationUnitScope);
			ReferenceBinding[] throwsExceptions;
			needSerialVersion =
				methodBinding == null
					|| !methodBinding.isValidBinding()
					|| methodBinding.returnType.id != TypeIds.T_JavaLangObject
					|| (throwsExceptions = methodBinding.thrownExceptions).length != 1
					|| throwsExceptions[0].id != TypeIds.T_JavaIoObjectStreamException;
			if (needSerialVersion) {
				// check the presence of an implementation of the methods
				// private void writeObject(java.io.ObjectOutputStream out) throws IOException
				// private void readObject(java.io.ObjectInputStream out) throws IOException
				boolean hasWriteObjectMethod = false;
				boolean hasReadObjectMethod = false;
				TypeBinding argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTOUTPUTSTREAM, 3);
				if (argumentTypeBinding.isValidBinding()) {
					methodBinding = sourceType.getExactMethod(TypeConstants.WRITEOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
					hasWriteObjectMethod = methodBinding != null
							&& methodBinding.isValidBinding()
							&& methodBinding.modifiers == ClassFileConstants.AccPrivate
							&& methodBinding.returnType == TypeBinding.VOID
							&& (throwsExceptions = methodBinding.thrownExceptions).length == 1
							&& throwsExceptions[0].id == TypeIds.T_JavaIoException;
				}
				argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTINPUTSTREAM, 3);
				if (argumentTypeBinding.isValidBinding()) {
					methodBinding = sourceType.getExactMethod(TypeConstants.READOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
					hasReadObjectMethod = methodBinding != null
							&& methodBinding.isValidBinding()
							&& methodBinding.modifiers == ClassFileConstants.AccPrivate
							&& methodBinding.returnType == TypeBinding.VOID
							&& (throwsExceptions = methodBinding.thrownExceptions).length == 1
							&& throwsExceptions[0].id == TypeIds.T_JavaIoException;
				}
				needSerialVersion = !hasWriteObjectMethod || !hasReadObjectMethod;
			}
		}
		// generics (and non static generic members) cannot extend Throwable
		if (sourceType.findSuperTypeOriginatingFrom(TypeIds.T_JavaLangThrowable, true) != null) {
			ReferenceBinding current = sourceType;
			checkEnclosedInGeneric : do {
				if (current.isGenericType()) {
					this.scope.problemReporter().genericTypeCannotExtendThrowable(this);
					break checkEnclosedInGeneric;
				}
				if (current.isStatic()) break checkEnclosedInGeneric;
				if (current.isLocalType()) {
					NestedTypeBinding nestedType = (NestedTypeBinding) current.erasure();
					if (nestedType.scope.methodScope().isStatic) break checkEnclosedInGeneric;
				}
			} while ((current = current.enclosingType()) != null);
		}
		// this.maxFieldCount might already be set
		int localMaxFieldCount = 0;
		int lastVisibleFieldID = -1;
		boolean hasEnumConstants = false;
		FieldDeclaration[] enumConstantsWithoutBody = null;

		if (this.typeParameters != null) {
			for (int i = 0, count = this.typeParameters.length; i < count; i++) {
				this.typeParameters[i].resolve(this.scope);
			}
		}
		if (this.memberTypes != null) {
			for (int i = 0, count = this.memberTypes.length; i < count; i++) {
				this.memberTypes[i].resolve(this.scope);
			}
		}
		if (this.fields != null) {
			for (int i = 0, count = this.fields.length; i < count; i++) {
				FieldDeclaration field = this.fields[i];
				switch(field.getKind()) {
					case AbstractVariableDeclaration.ENUM_CONSTANT:
						hasEnumConstants = true;
						if (!(field.initialization instanceof QualifiedAllocationExpression)) {
							if (enumConstantsWithoutBody == null)
								enumConstantsWithoutBody = new FieldDeclaration[count];
							enumConstantsWithoutBody[i] = field;
						}
						//$FALL-THROUGH$
					case AbstractVariableDeclaration.FIELD:
						FieldBinding fieldBinding = field.binding;
						if (fieldBinding == null) {
							// still discover secondary errors
							if (field.initialization != null) field.initialization.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
							this.ignoreFurtherInvestigation = true;
							continue;
						}
						if (needSerialVersion
								&& ((fieldBinding.modifiers & (ClassFileConstants.AccStatic | ClassFileConstants.AccFinal)) == (ClassFileConstants.AccStatic | ClassFileConstants.AccFinal))
								&& CharOperation.equals(TypeConstants.SERIALVERSIONUID, fieldBinding.name)
								&& TypeBinding.LONG == fieldBinding.type) {
							needSerialVersion = false;
						}
						localMaxFieldCount++;
						lastVisibleFieldID = field.binding.id;
						break;

					case AbstractVariableDeclaration.INITIALIZER:
						 ((Initializer) field).lastVisibleFieldID = lastVisibleFieldID + 1;
						break;
				}
				field.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
			}
		}
		if (this.maxFieldCount < localMaxFieldCount) {
			this.maxFieldCount = localMaxFieldCount;
		}
		if (needSerialVersion) {
			//check that the current type doesn't extend javax.rmi.CORBA.Stub
			TypeBinding javaxRmiCorbaStub = this.scope.getType(TypeConstants.JAVAX_RMI_CORBA_STUB, 4);
			if (javaxRmiCorbaStub.isValidBinding()) {
				ReferenceBinding superclassBinding = this.binding.superclass;
				loop: while (superclassBinding != null) {
					if (superclassBinding == javaxRmiCorbaStub) {
						needSerialVersion = false;
						break loop;
					}
					superclassBinding = superclassBinding.superclass();
				}
			}
			if (needSerialVersion) {
				this.scope.problemReporter().missingSerialVersion(this);
			}
		}

		// check extends/implements for annotation type
		switch(kind(this.modifiers)) {
			case TypeDeclaration.ANNOTATION_TYPE_DECL :
				if (this.superclass != null) {
					this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperclass(this);
				}
				if (this.superInterfaces != null) {
					this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperinterfaces(this);
				}
				break;
			case TypeDeclaration.ENUM_DECL :
				// check enum abstract methods
				if (this.binding.isAbstract()) {
					if (!hasEnumConstants) {
						for (int i = 0, count = this.methods.length; i < count; i++) {
							final AbstractMethodDeclaration methodDeclaration = this.methods[i];
							if (methodDeclaration.isAbstract() && methodDeclaration.binding != null)
								this.scope.problemReporter().enumAbstractMethodMustBeImplemented(methodDeclaration);
						}
					} else if (enumConstantsWithoutBody != null) {
						for (int i = 0, count = this.methods.length; i < count; i++) {
							final AbstractMethodDeclaration methodDeclaration = this.methods[i];
							if (methodDeclaration.isAbstract() && methodDeclaration.binding != null) {
								for (int f = 0, l = enumConstantsWithoutBody.length; f < l; f++)
									if (enumConstantsWithoutBody[f] != null)
										this.scope.problemReporter().enumConstantMustImplementAbstractMethod(methodDeclaration, enumConstantsWithoutBody[f]);
							}
						}
					}
				}
				break;
		}

		int missingAbstractMethodslength = this.missingAbstractMethods == null ? 0 : this.missingAbstractMethods.length;
		int methodsLength = this.methods == null ? 0 : this.methods.length;
		if ((methodsLength + missingAbstractMethodslength) > 0xFFFF) {
			this.scope.problemReporter().tooManyMethods(this);
		}
		if (this.methods != null) {
			for (int i = 0, count = this.methods.length; i < count; i++) {
				this.methods[i].resolve(this.scope);
			}
		}
		// Resolve javadoc
		if (this.javadoc != null) {
			if (this.scope != null && (this.name != TypeConstants.PACKAGE_INFO_NAME)) {
				// if the type is package-info, the javadoc was resolved as part of the compilation unit javadoc
				this.javadoc.resolve(this.scope);
			}
		} else if (!sourceType.isLocalType()) {
			// Set javadoc visibility
			int visibility = sourceType.modifiers & ExtraCompilerModifiers.AccVisibilityMASK;
			ProblemReporter reporter = this.scope.problemReporter();
			int severity = reporter.computeSeverity(IProblem.JavadocMissing);
			if (severity != ProblemSeverities.Ignore) {
				if (this.enclosingType != null) {
					visibility = Util.computeOuterMostVisibility(this.enclosingType, visibility);
				}
				int javadocModifiers = (this.binding.modifiers & ~ExtraCompilerModifiers.AccVisibilityMASK) | visibility;
				reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
			}
		}
	} catch (AbortType e) {
		this.ignoreFurtherInvestigation = true;
		return;
	}
}

/**
 * Resolve a local type declaration
 */
public void resolve(BlockScope blockScope) {

	// need to build its scope first and proceed with binding's creation
	if ((this.bits & ASTNode.IsAnonymousType) == 0) {
		// check collision scenarii
		Binding existing = blockScope.getType(this.name);
		if (existing instanceof ReferenceBinding
				&& existing != this.binding
				&& existing.isValidBinding()) {
			ReferenceBinding existingType = (ReferenceBinding) existing;
			if (existingType instanceof TypeVariableBinding) {
				blockScope.problemReporter().typeHiding(this, (TypeVariableBinding) existingType);
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=312989, check for collision with enclosing type.
				Scope outerScope = blockScope.parent;
checkOuterScope:while (outerScope != null) {
					Binding existing2 = outerScope.getType(this.name);
					if (existing2 instanceof TypeVariableBinding && existing2.isValidBinding()) {
						TypeVariableBinding tvb = (TypeVariableBinding) existingType;
						Binding declaringElement = tvb.declaringElement;
						if (declaringElement instanceof ReferenceBinding
								&& CharOperation.equals(((ReferenceBinding) declaringElement).sourceName(), this.name)) {
							blockScope.problemReporter().typeCollidesWithEnclosingType(this);
							break checkOuterScope;
						}
					} else if (existing2 instanceof ReferenceBinding
							&& existing2.isValidBinding()
							&& outerScope.isDefinedInType((ReferenceBinding) existing2)) { 
							blockScope.problemReporter().typeCollidesWithEnclosingType(this);
							break checkOuterScope;
					} else if (existing2 == null) {
						break checkOuterScope;
					}
					outerScope = outerScope.parent;
				}
			} else if (existingType instanceof LocalTypeBinding
						&& ((LocalTypeBinding) existingType).scope.methodScope() == blockScope.methodScope()) {
					// dup in same method
					blockScope.problemReporter().duplicateNestedType(this);
			} else if (blockScope.isDefinedInType(existingType)) {
				//	collision with enclosing type
				blockScope.problemReporter().typeCollidesWithEnclosingType(this);
			} else if (blockScope.isDefinedInSameUnit(existingType)){ // only consider hiding inside same unit
				// hiding sibling
				blockScope.problemReporter().typeHiding(this, existingType);
			}
		}
		blockScope.addLocalType(this);
	}

	if (this.binding != null) {
		// remember local types binding for innerclass emulation propagation
		blockScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);

		// binding is not set if the receiver could not be created
		resolve();
		updateMaxFieldCount();
	}
}

/**
 * Resolve a member type declaration (can be a local member)
 */
public void resolve(ClassScope upperScope) {
	// member scopes are already created
	// request the construction of a binding if local member type

	if (this.binding != null && this.binding instanceof LocalTypeBinding) {
		// remember local types binding for innerclass emulation propagation
		upperScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
	}
	resolve();
	updateMaxFieldCount();
}

/**
 * Resolve a top level type declaration
 */
public void resolve(CompilationUnitScope upperScope) {
	// top level : scope are already created
	resolve();
	updateMaxFieldCount();
}

public void tagAsHavingErrors() {
	this.ignoreFurtherInvestigation = true;
}

/**
 *	Iteration for a package member type
 *
 */
public void traverse(ASTVisitor visitor, CompilationUnitScope unitScope) {
	try {
		if (visitor.visit(this, unitScope)) {
			if (this.javadoc != null) {
				this.javadoc.traverse(visitor, this.scope);
			}
			if (this.annotations != null) {
				int annotationsLength = this.annotations.length;
				for (int i = 0; i < annotationsLength; i++)
					this.annotations[i].traverse(visitor, this.staticInitializerScope);
			}
			if (this.superclass != null)
				this.superclass.traverse(visitor, this.scope);
			if (this.superInterfaces != null) {
				int length = this.superInterfaces.length;
				for (int i = 0; i < length; i++)
					this.superInterfaces[i].traverse(visitor, this.scope);
			}
			if (this.typeParameters != null) {
				int length = this.typeParameters.length;
				for (int i = 0; i < length; i++) {
					this.typeParameters[i].traverse(visitor, this.scope);
				}
			}
			if (this.memberTypes != null) {
				int length = this.memberTypes.length;
				for (int i = 0; i < length; i++)
					this.memberTypes[i].traverse(visitor, this.scope);
			}
			if (this.fields != null) {
				int length = this.fields.length;
				for (int i = 0; i < length; i++) {
					FieldDeclaration field;
					if ((field = this.fields[i]).isStatic()) {
						field.traverse(visitor, this.staticInitializerScope);
					} else {
						field.traverse(visitor, this.initializerScope);
					}
				}
			}
			if (this.methods != null) {
				int length = this.methods.length;
				for (int i = 0; i < length; i++)
					this.methods[i].traverse(visitor, this.scope);
			}
		}
		visitor.endVisit(this, unitScope);
	} catch (AbortType e) {
		// silent abort
	}
}

/**
 *	Iteration for a local innertype
 */
public void traverse(ASTVisitor visitor, BlockScope blockScope) {
	try {
		if (visitor.visit(this, blockScope)) {
			if (this.javadoc != null) {
				this.javadoc.traverse(visitor, this.scope);
			}
			if (this.annotations != null) {
				int annotationsLength = this.annotations.length;
				for (int i = 0; i < annotationsLength; i++)
					this.annotations[i].traverse(visitor, this.staticInitializerScope);
			}
			if (this.superclass != null)
				this.superclass.traverse(visitor, this.scope);
			if (this.superInterfaces != null) {
				int length = this.superInterfaces.length;
				for (int i = 0; i < length; i++)
					this.superInterfaces[i].traverse(visitor, this.scope);
			}
			if (this.typeParameters != null) {
				int length = this.typeParameters.length;
				for (int i = 0; i < length; i++) {
					this.typeParameters[i].traverse(visitor, this.scope);
				}
			}
			if (this.memberTypes != null) {
				int length = this.memberTypes.length;
				for (int i = 0; i < length; i++)
					this.memberTypes[i].traverse(visitor, this.scope);
			}
			if (this.fields != null) {
				int length = this.fields.length;
				for (int i = 0; i < length; i++) {
					FieldDeclaration field;
					if ((field = this.fields[i]).isStatic()) {
						// local type cannot have static fields
					} else {
						field.traverse(visitor, this.initializerScope);
					}
				}
			}
			if (this.methods != null) {
				int length = this.methods.length;
				for (int i = 0; i < length; i++)
					this.methods[i].traverse(visitor, this.scope);
			}
		}
		visitor.endVisit(this, blockScope);
	} catch (AbortType e) {
		// silent abort
	}
}

/**
 *	Iteration for a member innertype
 *
 */
public void traverse(ASTVisitor visitor, ClassScope classScope) {
	try {
		if (visitor.visit(this, classScope)) {
			if (this.javadoc != null) {
				this.javadoc.traverse(visitor, this.scope);
			}
			if (this.annotations != null) {
				int annotationsLength = this.annotations.length;
				for (int i = 0; i < annotationsLength; i++)
					this.annotations[i].traverse(visitor, this.staticInitializerScope);
			}
			if (this.superclass != null)
				this.superclass.traverse(visitor, this.scope);
			if (this.superInterfaces != null) {
				int length = this.superInterfaces.length;
				for (int i = 0; i < length; i++)
					this.superInterfaces[i].traverse(visitor, this.scope);
			}
			if (this.typeParameters != null) {
				int length = this.typeParameters.length;
				for (int i = 0; i < length; i++) {
					this.typeParameters[i].traverse(visitor, this.scope);
				}
			}
			if (this.memberTypes != null) {
				int length = this.memberTypes.length;
				for (int i = 0; i < length; i++)
					this.memberTypes[i].traverse(visitor, this.scope);
			}
			if (this.fields != null) {
				int length = this.fields.length;
				for (int i = 0; i < length; i++) {
					FieldDeclaration field;
					if ((field = this.fields[i]).isStatic()) {
						field.traverse(visitor, this.staticInitializerScope);
					} else {
						field.traverse(visitor, this.initializerScope);
					}
				}
			}
			if (this.methods != null) {
				int length = this.methods.length;
				for (int i = 0; i < length; i++)
					this.methods[i].traverse(visitor, this.scope);
			}
		}
		visitor.endVisit(this, classScope);
	} catch (AbortType e) {
		// silent abort
	}
}

/**
 * MaxFieldCount's computation is necessary so as to reserve space for
 * the flow info field portions. It corresponds to the maximum amount of
 * fields this class or one of its innertypes have.
 *
 * During name resolution, types are traversed, and the max field count is recorded
 * on the outermost type. It is then propagated down during the flow analysis.
 *
 * This method is doing either up/down propagation.
 */
void updateMaxFieldCount() {
	if (this.binding == null)
		return; // error scenario
	TypeDeclaration outerMostType = this.scope.outerMostClassScope().referenceType();
	if (this.maxFieldCount > outerMostType.maxFieldCount) {
		outerMostType.maxFieldCount = this.maxFieldCount; // up
	} else {
		this.maxFieldCount = outerMostType.maxFieldCount; // down
	}
}

/**
 * Returns whether the type is a secondary one or not.
 */
public boolean isSecondary() {
	return (this.bits & ASTNode.IsSecondaryType) != 0;
}
}
