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

import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.util.Util;

public class MethodBinding extends Binding {

	public int modifiers;
	public char[] selector;
	public TypeBinding returnType;
	public TypeBinding[] parameters;
	public ReferenceBinding[] thrownExceptions;
	public ReferenceBinding declaringClass;
	public TypeVariableBinding[] typeVariables = Binding.NO_TYPE_VARIABLES;
	char[] signature;
	public long tagBits;

protected MethodBinding() {
	// for creating problem or synthetic method
}
public MethodBinding(int modifiers, char[] selector, TypeBinding returnType, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass) {
	this.modifiers = modifiers;
	this.selector = selector;
	this.returnType = returnType;
	this.parameters = (parameters == null || parameters.length == 0) ? Binding.NO_PARAMETERS : parameters;
	this.thrownExceptions = (thrownExceptions == null || thrownExceptions.length == 0) ? Binding.NO_EXCEPTIONS : thrownExceptions;
	this.declaringClass = declaringClass;

	// propagate the strictfp & deprecated modifiers
	if (this.declaringClass != null) {
		if (this.declaringClass.isStrictfp())
			if (!(isNative() || isAbstract()))
				this.modifiers |= ClassFileConstants.AccStrictfp;
	}
}
public MethodBinding(int modifiers, TypeBinding[] parameters, ReferenceBinding[] thrownExceptions, ReferenceBinding declaringClass) {
	this(modifiers, TypeConstants.INIT, TypeBinding.VOID, parameters, thrownExceptions, declaringClass);
}
// special API used to change method declaring class for runtime visibility check
public MethodBinding(MethodBinding initialMethodBinding, ReferenceBinding declaringClass) {
	this.modifiers = initialMethodBinding.modifiers;
	this.selector = initialMethodBinding.selector;
	this.returnType = initialMethodBinding.returnType;
	this.parameters = initialMethodBinding.parameters;
	this.thrownExceptions = initialMethodBinding.thrownExceptions;
	this.declaringClass = declaringClass;
	declaringClass.storeAnnotationHolder(this, initialMethodBinding.declaringClass.retrieveAnnotationHolder(initialMethodBinding, true));
}
/* Answer true if the argument types & the receiver's parameters have the same erasure
*/
public final boolean areParameterErasuresEqual(MethodBinding method) {
	TypeBinding[] args = method.parameters;
	if (this.parameters == args)
		return true;

	int length = this.parameters.length;
	if (length != args.length)
		return false;

	for (int i = 0; i < length; i++)
		if (this.parameters[i] != args[i] && this.parameters[i].erasure() != args[i].erasure())
			return false;
	return true;
}
/*
 * Returns true if given parameters are compatible with this method parameters.
 * Callers to this method should first check that the number of TypeBindings
 * passed as argument matches this MethodBinding number of parameters
 */
public final boolean areParametersCompatibleWith(TypeBinding[] arguments) {
	int paramLength = this.parameters.length;
	int argLength = arguments.length;
	int lastIndex = argLength;
	if (isVarargs()) {
		lastIndex = paramLength - 1;
		if (paramLength == argLength) { // accept X[] but not X or X[][]
			TypeBinding varArgType = this.parameters[lastIndex]; // is an ArrayBinding by definition
			TypeBinding lastArgument = arguments[lastIndex];
			if (varArgType != lastArgument && !lastArgument.isCompatibleWith(varArgType))
				return false;
		} else if (paramLength < argLength) { // all remainig argument types must be compatible with the elementsType of varArgType
			TypeBinding varArgType = ((ArrayBinding) this.parameters[lastIndex]).elementsType();
			for (int i = lastIndex; i < argLength; i++)
				if (varArgType != arguments[i] && !arguments[i].isCompatibleWith(varArgType))
					return false;
		} else if (lastIndex != argLength) { // can call foo(int i, X ... x) with foo(1) but NOT foo();
			return false;
		}
		// now compare standard arguments from 0 to lastIndex
	}
	for (int i = 0; i < lastIndex; i++)
		if (this.parameters[i] != arguments[i] && !arguments[i].isCompatibleWith(this.parameters[i]))
			return false;
	return true;
}
/* Answer true if the argument types & the receiver's parameters are equal
*/
public final boolean areParametersEqual(MethodBinding method) {
	TypeBinding[] args = method.parameters;
	if (this.parameters == args)
		return true;

	int length = this.parameters.length;
	if (length != args.length)
		return false;

	for (int i = 0; i < length; i++)
		if (this.parameters[i] != args[i])
			return false;
	return true;
}

/* API
* Answer the receiver's binding type from Binding.BindingID.
*/

/* Answer true if the type variables have the same erasure
*/
public final boolean areTypeVariableErasuresEqual(MethodBinding method) {
	TypeVariableBinding[] vars = method.typeVariables;
	if (this.typeVariables == vars)
		return true;

	int length = this.typeVariables.length;
	if (length != vars.length)
		return false;

	for (int i = 0; i < length; i++)
		if (this.typeVariables[i] != vars[i] && this.typeVariables[i].erasure() != vars[i].erasure())
			return false;
	return true;
}
MethodBinding asRawMethod(LookupEnvironment env) {
	if (this.typeVariables == Binding.NO_TYPE_VARIABLES) return this;

	// substitute type arguments with raw types
	int length = this.typeVariables.length;
	TypeBinding[] arguments = new TypeBinding[length];
	for (int i = 0; i < length; i++) {
		TypeVariableBinding var = this.typeVariables[i];
		if (var.boundsCount() <= 1) {
			arguments[i] = env.convertToRawType(var.upperBound(), false /*do not force conversion of enclosing types*/);
		} else {
			// use an intersection type to retain full bound information if more than 1 bound
			TypeBinding[] itsSuperinterfaces = var.superInterfaces();
			int superLength = itsSuperinterfaces.length;
			TypeBinding rawFirstBound = null;
			TypeBinding[] rawOtherBounds = null;
			if (var.boundsCount() == superLength) {
				rawFirstBound = env.convertToRawType(itsSuperinterfaces[0], false);
				rawOtherBounds = new TypeBinding[superLength - 1];
				for (int s = 1; s < superLength; s++)
					rawOtherBounds[s - 1] = env.convertToRawType(itsSuperinterfaces[s], false);
			} else {
				rawFirstBound = env.convertToRawType(var.superclass(), false);
				rawOtherBounds = new TypeBinding[superLength];
				for (int s = 0; s < superLength; s++)
					rawOtherBounds[s] = env.convertToRawType(itsSuperinterfaces[s], false);
			}
			arguments[i] = env.createWildcard(null, 0, rawFirstBound, rawOtherBounds, org.eclipse.jdt.internal.compiler.ast.Wildcard.EXTENDS);
		}
	}
	return env.createParameterizedGenericMethod(this, arguments);
}
/* Answer true if the receiver is visible to the type provided by the scope.
* InvocationSite implements isSuperAccess() to provide additional information
* if the receiver is protected.
*
* NOTE: This method should ONLY be sent if the receiver is a constructor.
*
* NOTE: Cannot invoke this method with a compilation unit scope.
*/

public final boolean canBeSeenBy(InvocationSite invocationSite, Scope scope) {
	if (isPublic()) return true;

	SourceTypeBinding invocationType = scope.enclosingSourceType();
	if (invocationType == this.declaringClass) return true;

	if (isProtected()) {
		// answer true if the receiver is in the same package as the invocationType
		if (invocationType.fPackage == this.declaringClass.fPackage) return true;
		return invocationSite.isSuperAccess();
	}

	if (isPrivate()) {
		// answer true if the invocationType and the declaringClass have a common enclosingType
		// already know they are not the identical type
		ReferenceBinding outerInvocationType = invocationType;
		ReferenceBinding temp = outerInvocationType.enclosingType();
		while (temp != null) {
			outerInvocationType = temp;
			temp = temp.enclosingType();
		}

		ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
		temp = outerDeclaringClass.enclosingType();
		while (temp != null) {
			outerDeclaringClass = temp;
			temp = temp.enclosingType();
		}
		return outerInvocationType == outerDeclaringClass;
	}

	// isDefault()
	return invocationType.fPackage == this.declaringClass.fPackage;
}
public final boolean canBeSeenBy(PackageBinding invocationPackage) {
	if (isPublic()) return true;
	if (isPrivate()) return false;

	// isProtected() or isDefault()
	return invocationPackage == this.declaringClass.getPackage();
}

/* Answer true if the receiver is visible to the type provided by the scope.
* InvocationSite implements isSuperAccess() to provide additional information
* if the receiver is protected.
*
* NOTE: Cannot invoke this method with a compilation unit scope.
*/
public final boolean canBeSeenBy(TypeBinding receiverType, InvocationSite invocationSite, Scope scope) {
	if (isPublic()) return true;

	SourceTypeBinding invocationType = scope.enclosingSourceType();
	if (invocationType == this.declaringClass && invocationType == receiverType) return true;

	if (invocationType == null) // static import call
		return !isPrivate() && scope.getCurrentPackage() == this.declaringClass.fPackage;

	if (isProtected()) {
		// answer true if the invocationType is the declaringClass or they are in the same package
		// OR the invocationType is a subclass of the declaringClass
		//    AND the receiverType is the invocationType or its subclass
		//    OR the method is a static method accessed directly through a type
		//    OR previous assertions are true for one of the enclosing type
		if (invocationType == this.declaringClass) return true;
		if (invocationType.fPackage == this.declaringClass.fPackage) return true;

		ReferenceBinding currentType = invocationType;
		TypeBinding receiverErasure = receiverType.erasure();
		ReferenceBinding declaringErasure = (ReferenceBinding) this.declaringClass.erasure();
		int depth = 0;
		do {
			if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
				if (invocationSite.isSuperAccess())
					return true;
				// receiverType can be an array binding in one case... see if you can change it
				if (receiverType instanceof ArrayBinding)
					return false;
				if (isStatic()) {
					if (depth > 0) invocationSite.setDepth(depth);
					return true; // see 1FMEPDL - return invocationSite.isTypeAccess();
				}
				if (currentType == receiverErasure || receiverErasure.findSuperTypeOriginatingFrom(currentType) != null) {
					if (depth > 0) invocationSite.setDepth(depth);
					return true;
				}
			}
			depth++;
			currentType = currentType.enclosingType();
		} while (currentType != null);
		return false;
	}

	if (isPrivate()) {
		// answer true if the receiverType is the declaringClass
		// AND the invocationType and the declaringClass have a common enclosingType
		receiverCheck: {
			if (receiverType != this.declaringClass) {
				// special tolerance for type variable direct bounds, but only if compliance <= 1.6, see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=334622
				if (scope.compilerOptions().complianceLevel <= ClassFileConstants.JDK1_6 && receiverType.isTypeVariable() && ((TypeVariableBinding) receiverType).isErasureBoundTo(this.declaringClass.erasure()))
					break receiverCheck;
				return false;
			}
		}

		if (invocationType != this.declaringClass) {
			ReferenceBinding outerInvocationType = invocationType;
			ReferenceBinding temp = outerInvocationType.enclosingType();
			while (temp != null) {
				outerInvocationType = temp;
				temp = temp.enclosingType();
			}

			ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
			temp = outerDeclaringClass.enclosingType();
			while (temp != null) {
				outerDeclaringClass = temp;
				temp = temp.enclosingType();
			}
			if (outerInvocationType != outerDeclaringClass) return false;
		}
		return true;
	}

	// isDefault()
	PackageBinding declaringPackage = this.declaringClass.fPackage;
	if (invocationType.fPackage != declaringPackage) return false;

	// receiverType can be an array binding in one case... see if you can change it
	if (receiverType instanceof ArrayBinding)
		return false;
	TypeBinding originalDeclaringClass = this.declaringClass.original();
	ReferenceBinding currentType = (ReferenceBinding) (receiverType);
	do {
		if (currentType.isCapture()) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=285002
			if (originalDeclaringClass == currentType.erasure().original()) return true;
		} else {
			if (originalDeclaringClass == currentType.original()) return true;
		}
		PackageBinding currentPackage = currentType.fPackage;
		// package could be null for wildcards/intersection types, ignore and recurse in superclass
		if (currentPackage != null && currentPackage != declaringPackage) return false;
	} while ((currentType = currentType.superclass()) != null);
	return false;
}

public List collectMissingTypes(List missingTypes) {
	if ((this.tagBits & TagBits.HasMissingType) != 0) {
		missingTypes = this.returnType.collectMissingTypes(missingTypes);
		for (int i = 0, max = this.parameters.length; i < max; i++) {
			missingTypes = this.parameters[i].collectMissingTypes(missingTypes);
		}
		for (int i = 0, max = this.thrownExceptions.length; i < max; i++) {
			missingTypes = this.thrownExceptions[i].collectMissingTypes(missingTypes);
		}
		for (int i = 0, max = this.typeVariables.length; i < max; i++) {
			TypeVariableBinding variable = this.typeVariables[i];
			missingTypes = variable.superclass().collectMissingTypes(missingTypes);
			ReferenceBinding[] interfaces = variable.superInterfaces();
			for (int j = 0, length = interfaces.length; j < length; j++) {
				missingTypes = interfaces[j].collectMissingTypes(missingTypes);
			}
		}
	}
	return missingTypes;
}

MethodBinding computeSubstitutedMethod(MethodBinding method, LookupEnvironment env) {
	int length = this.typeVariables.length;
	TypeVariableBinding[] vars = method.typeVariables;
	if (length != vars.length)
		return null;

	// must substitute to detect cases like:
	//   <T1 extends X<T1>> void dup() {}
	//   <T2 extends X<T2>> Object dup() {return null;}
	ParameterizedGenericMethodBinding substitute =
		env.createParameterizedGenericMethod(method, this.typeVariables);
	for (int i = 0; i < length; i++)
		if (!this.typeVariables[i].isInterchangeableWith(vars[i], substitute))
			return null;
	return substitute;
}

/*
 * declaringUniqueKey dot selector genericSignature
 * p.X { <T> void bar(X<T> t) } --> Lp/X;.bar<T:Ljava/lang/Object;>(LX<TT;>;)V
 */
public char[] computeUniqueKey(boolean isLeaf) {
	// declaring class
	char[] declaringKey = this.declaringClass.computeUniqueKey(false/*not a leaf*/);
	int declaringLength = declaringKey.length;

	// selector
	int selectorLength = this.selector == TypeConstants.INIT ? 0 : this.selector.length;

	// generic signature
	char[] sig = genericSignature();
	boolean isGeneric = sig != null;
	if (!isGeneric) sig = signature();
	int signatureLength = sig.length;

	// thrown exceptions
	int thrownExceptionsLength = this.thrownExceptions.length;
	int thrownExceptionsSignatureLength = 0;
	char[][] thrownExceptionsSignatures = null;
	boolean addThrownExceptions = thrownExceptionsLength > 0 && (!isGeneric || CharOperation.lastIndexOf('^', sig) < 0);
	if (addThrownExceptions) {
		thrownExceptionsSignatures = new char[thrownExceptionsLength][];
		for (int i = 0; i < thrownExceptionsLength; i++) {
			if (this.thrownExceptions[i] != null) {
				thrownExceptionsSignatures[i] = this.thrownExceptions[i].signature();
				thrownExceptionsSignatureLength += thrownExceptionsSignatures[i].length + 1;	// add one char for separator
			}
		}
	}

	char[] uniqueKey = new char[declaringLength + 1 + selectorLength + signatureLength + thrownExceptionsSignatureLength];
	int index = 0;
	System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
	index = declaringLength;
	uniqueKey[index++] = '.';
	System.arraycopy(this.selector, 0, uniqueKey, index, selectorLength);
	index += selectorLength;
	System.arraycopy(sig, 0, uniqueKey, index, signatureLength);
	if (thrownExceptionsSignatureLength > 0) {
		index += signatureLength;
		for (int i = 0; i < thrownExceptionsLength; i++) {
			char[] thrownExceptionSignature = thrownExceptionsSignatures[i];
			if (thrownExceptionSignature != null) {
				uniqueKey[index++] = '|';
				int length = thrownExceptionSignature.length;
				System.arraycopy(thrownExceptionSignature, 0, uniqueKey, index, length);
				index += length;
			}
		}
	}
	return uniqueKey;
}

/* Answer the receiver's constant pool name.
*
* <init> for constructors
* <clinit> for clinit methods
* or the source name of the method
*/
public final char[] constantPoolName() {
	return this.selector;
}

public MethodBinding findOriginalInheritedMethod(MethodBinding inheritedMethod) {
	MethodBinding inheritedOriginal = inheritedMethod.original();
	TypeBinding superType = this.declaringClass.findSuperTypeOriginatingFrom(inheritedOriginal.declaringClass);
	if (superType == null || !(superType instanceof ReferenceBinding)) return null;

	if (inheritedOriginal.declaringClass != superType) {
		// must find inherited method with the same substituted variables
		MethodBinding[] superMethods = ((ReferenceBinding) superType).getMethods(inheritedOriginal.selector, inheritedOriginal.parameters.length);
		for (int m = 0, l = superMethods.length; m < l; m++)
			if (superMethods[m].original() == inheritedOriginal)
				return superMethods[m];
	}
	return inheritedOriginal;
}

/**
 * <pre>
 *<typeParam1 ... typeParamM>(param1 ... paramN)returnType thrownException1 ... thrownExceptionP
 * T foo(T t) throws X<T>   --->   (TT;)TT;LX<TT;>;
 * void bar(X<T> t)   -->   (LX<TT;>;)V
 * <T> void bar(X<T> t)   -->  <T:Ljava.lang.Object;>(LX<TT;>;)V
 * </pre>
 */
public char[] genericSignature() {
	if ((this.modifiers & ExtraCompilerModifiers.AccGenericSignature) == 0) return null;
	StringBuffer sig = new StringBuffer(10);
	if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
		sig.append('<');
		for (int i = 0, length = this.typeVariables.length; i < length; i++) {
			sig.append(this.typeVariables[i].genericSignature());
		}
		sig.append('>');
	}
	sig.append('(');
	for (int i = 0, length = this.parameters.length; i < length; i++) {
		sig.append(this.parameters[i].genericTypeSignature());
	}
	sig.append(')');
	if (this.returnType != null)
		sig.append(this.returnType.genericTypeSignature());

	// only append thrown exceptions if any is generic/parameterized
	boolean needExceptionSignatures = false;
	int length = this.thrownExceptions.length;
	for (int i = 0; i < length; i++) {
		if((this.thrownExceptions[i].modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0) {
			needExceptionSignatures = true;
			break;
		}
	}
	if (needExceptionSignatures) {
		for (int i = 0; i < length; i++) {
			sig.append('^');
			sig.append(this.thrownExceptions[i].genericTypeSignature());
		}
	}
	int sigLength = sig.length();
	char[] genericSignature = new char[sigLength];
	sig.getChars(0, sigLength, genericSignature, 0);
	return genericSignature;
}

public final int getAccessFlags() {
	return this.modifiers & ExtraCompilerModifiers.AccJustFlag;
}

public AnnotationBinding[] getAnnotations() {
	MethodBinding originalMethod = original();
	return originalMethod.declaringClass.retrieveAnnotations(originalMethod);
}

/**
 * Compute the tagbits for standard annotations. For source types, these could require
 * lazily resolving corresponding annotation nodes, in case of forward references.
 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#getAnnotationTagBits()
 */
public long getAnnotationTagBits() {
	MethodBinding originalMethod = original();
	if ((originalMethod.tagBits & TagBits.AnnotationResolved) == 0 && originalMethod.declaringClass instanceof SourceTypeBinding) {
		ClassScope scope = ((SourceTypeBinding) originalMethod.declaringClass).scope;
		if (scope != null) {
			TypeDeclaration typeDecl = scope.referenceContext;
			AbstractMethodDeclaration methodDecl = typeDecl.declarationOf(originalMethod);
			if (methodDecl != null)
				ASTNode.resolveAnnotations(methodDecl.scope, methodDecl.annotations, originalMethod);
		}
	}
	return originalMethod.tagBits;
}

/**
 * @return the default value for this annotation method or <code>null</code> if there is no default value
 */
public Object getDefaultValue() {
	MethodBinding originalMethod = original();
	if ((originalMethod.tagBits & TagBits.DefaultValueResolved) == 0) {
		//The method has not been resolved nor has its class been resolved.
		//It can only be from a source type within compilation units to process.
		if (originalMethod.declaringClass instanceof SourceTypeBinding) {
			SourceTypeBinding sourceType = (SourceTypeBinding) originalMethod.declaringClass;
			if (sourceType.scope != null) {
				AbstractMethodDeclaration methodDeclaration = originalMethod.sourceMethod();
				if (methodDeclaration != null && methodDeclaration.isAnnotationMethod()) {
					methodDeclaration.resolve(sourceType.scope);
				}
			}
		}
		originalMethod.tagBits |= TagBits.DefaultValueResolved;
	}
	AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
	return holder == null ? null : holder.getDefaultValue();
}

/**
 * @return the annotations for each of the method parameters or <code>null></code>
 * 	if there's no parameter or no annotation at all.
 */
public AnnotationBinding[][] getParameterAnnotations() {
	int length;
	if ((length = this.parameters.length) == 0) {
		return null;
	}
	MethodBinding originalMethod = original();
	AnnotationHolder holder = originalMethod.declaringClass.retrieveAnnotationHolder(originalMethod, true);
	AnnotationBinding[][] allParameterAnnotations = holder == null ? null : holder.getParameterAnnotations();
	if (allParameterAnnotations == null && (this.tagBits & TagBits.HasParameterAnnotations) != 0) {
		allParameterAnnotations = new AnnotationBinding[length][];
		// forward reference to method, where param annotations have not yet been associated to method
		if (this.declaringClass instanceof SourceTypeBinding) {
			SourceTypeBinding sourceType = (SourceTypeBinding) this.declaringClass;
			if (sourceType.scope != null) {
				AbstractMethodDeclaration methodDecl = sourceType.scope.referenceType().declarationOf(this);
				for (int i = 0; i < length; i++) {
					Argument argument = methodDecl.arguments[i];
					if (argument.annotations != null) {
						ASTNode.resolveAnnotations(methodDecl.scope, argument.annotations, argument.binding);
						allParameterAnnotations[i] = argument.binding.getAnnotations();
					} else {
						allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
					}
				}
			} else {
				for (int i = 0; i < length; i++) {
					allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
				}
			}
		} else {
			for (int i = 0; i < length; i++) {
				allParameterAnnotations[i] = Binding.NO_ANNOTATIONS;
			}
		}
		setParameterAnnotations(allParameterAnnotations);
	}
	return allParameterAnnotations;
}

public TypeVariableBinding getTypeVariable(char[] variableName) {
	for (int i = this.typeVariables.length; --i >= 0;)
		if (CharOperation.equals(this.typeVariables[i].sourceName, variableName))
			return this.typeVariables[i];
	return null;
}

/**
 * Returns true if method got substituted parameter types
 * (see ParameterizedMethodBinding)
 */
public boolean hasSubstitutedParameters() {
	return false;
}

/* Answer true if the return type got substituted.
 */
public boolean hasSubstitutedReturnType() {
	return false;
}

/* Answer true if the receiver is an abstract method
*/
public final boolean isAbstract() {
	return (this.modifiers & ClassFileConstants.AccAbstract) != 0;
}

/* Answer true if the receiver is a bridge method
*/
public final boolean isBridge() {
	return (this.modifiers & ClassFileConstants.AccBridge) != 0;
}

/* Answer true if the receiver is a constructor
*/
public final boolean isConstructor() {
	return this.selector == TypeConstants.INIT;
}

/* Answer true if the receiver has default visibility
*/
public final boolean isDefault() {
	return !isPublic() && !isProtected() && !isPrivate();
}

/* Answer true if the receiver is a system generated default abstract method
*/
public final boolean isDefaultAbstract() {
	return (this.modifiers & ExtraCompilerModifiers.AccDefaultAbstract) != 0;
}

/* Answer true if the receiver is a deprecated method
*/
public final boolean isDeprecated() {
	return (this.modifiers & ClassFileConstants.AccDeprecated) != 0;
}

/* Answer true if the receiver is final and cannot be overridden
*/
public final boolean isFinal() {
	return (this.modifiers & ClassFileConstants.AccFinal) != 0;
}

/* Answer true if the receiver is implementing another method
 * in other words, it is overriding and concrete, and overriden method is abstract
 * Only set for source methods
*/
public final boolean isImplementing() {
	return (this.modifiers & ExtraCompilerModifiers.AccImplementing) != 0;
}

/*
 * Answer true if the receiver is a "public static void main(String[])" method
 */
public final boolean isMain() {
	if (this.selector.length == 4 && CharOperation.equals(this.selector, TypeConstants.MAIN)
			&& ((this.modifiers & (ClassFileConstants.AccPublic | ClassFileConstants.AccStatic)) != 0)
			&& TypeBinding.VOID == this.returnType
			&& this.parameters.length == 1) {
		TypeBinding paramType = this.parameters[0];
		if (paramType.dimensions() == 1 && paramType.leafComponentType().id == TypeIds.T_JavaLangString) {
			return true;
		}
	}
	return false;
}

/* Answer true if the receiver is a native method
*/
public final boolean isNative() {
	return (this.modifiers & ClassFileConstants.AccNative) != 0;
}

/* Answer true if the receiver is overriding another method
 * Only set for source methods
*/
public final boolean isOverriding() {
	return (this.modifiers & ExtraCompilerModifiers.AccOverriding) != 0;
}
/* Answer true if the receiver has private visibility
*/
public final boolean isPrivate() {
	return (this.modifiers & ClassFileConstants.AccPrivate) != 0;
}

/* Answer true if the receiver has private visibility or if any of its enclosing types do.
*/
public final boolean isOrEnclosedByPrivateType() {
	if ((this.modifiers & ClassFileConstants.AccPrivate) != 0)
		return true;
	return this.declaringClass != null && this.declaringClass.isOrEnclosedByPrivateType();
}

/* Answer true if the receiver has protected visibility
*/
public final boolean isProtected() {
	return (this.modifiers & ClassFileConstants.AccProtected) != 0;
}

/* Answer true if the receiver has public visibility
*/
public final boolean isPublic() {
	return (this.modifiers & ClassFileConstants.AccPublic) != 0;
}

/* Answer true if the receiver is a static method
*/
public final boolean isStatic() {
	return (this.modifiers & ClassFileConstants.AccStatic) != 0;
}

/* Answer true if all float operations must adher to IEEE 754 float/double rules
*/
public final boolean isStrictfp() {
	return (this.modifiers & ClassFileConstants.AccStrictfp) != 0;
}

/* Answer true if the receiver is a synchronized method
*/
public final boolean isSynchronized() {
	return (this.modifiers & ClassFileConstants.AccSynchronized) != 0;
}

/* Answer true if the receiver has public visibility
*/
public final boolean isSynthetic() {
	return (this.modifiers & ClassFileConstants.AccSynthetic) != 0;
}

/* Answer true if the receiver has private visibility and is used locally
*/
public final boolean isUsed() {
	return (this.modifiers & ExtraCompilerModifiers.AccLocallyUsed) != 0;
}

/* Answer true if the receiver method has varargs
*/
public boolean isVarargs() {
	return (this.modifiers & ClassFileConstants.AccVarargs) != 0;
}
public boolean isPolymorphic() {
	return false;
}
/* Answer true if the receiver's declaring type is deprecated (or any of its enclosing types)
*/
public final boolean isViewedAsDeprecated() {
	return (this.modifiers & (ClassFileConstants.AccDeprecated | ExtraCompilerModifiers.AccDeprecatedImplicitly)) != 0;
}

public final int kind() {
	return Binding.METHOD;
}
/* Answer true if the receiver is visible to the invocationPackage.
*/

/**
 * Returns the original method (as opposed to parameterized/polymorphic instances)
 */
public MethodBinding original() {
	return this;
}

public char[] readableName() /* foo(int, Thread) */ {
	StringBuffer buffer = new StringBuffer(this.parameters.length + 1 * 20);
	if (isConstructor())
		buffer.append(this.declaringClass.sourceName());
	else
		buffer.append(this.selector);
	buffer.append('(');
	if (this.parameters != Binding.NO_PARAMETERS) {
		for (int i = 0, length = this.parameters.length; i < length; i++) {
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			buffer.append(this.parameters[i].sourceName());
		}
	}
	buffer.append(')');
	return buffer.toString().toCharArray();
}
public void setAnnotations(AnnotationBinding[] annotations) {
	this.declaringClass.storeAnnotations(this, annotations);
}
public void setAnnotations(AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, Object defaultValue, LookupEnvironment optionalEnv) {
	this.declaringClass.storeAnnotationHolder(this,  AnnotationHolder.storeAnnotations(annotations, parameterAnnotations, defaultValue, optionalEnv));
}
public void setDefaultValue(Object defaultValue) {
	MethodBinding originalMethod = original();
	originalMethod.tagBits |= TagBits.DefaultValueResolved;

	AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
	if (holder == null)
		setAnnotations(null, null, defaultValue, null);
	else
		setAnnotations(holder.getAnnotations(), holder.getParameterAnnotations(), defaultValue, null);
}
public void setParameterAnnotations(AnnotationBinding[][] parameterAnnotations) {
	AnnotationHolder holder = this.declaringClass.retrieveAnnotationHolder(this, false);
	if (holder == null)
		setAnnotations(null, parameterAnnotations, null, null);
	else
		setAnnotations(holder.getAnnotations(), parameterAnnotations, holder.getDefaultValue(), null);
}
protected final void setSelector(char[] selector) {
	this.selector = selector;
	this.signature = null;
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#shortReadableName()
 */
public char[] shortReadableName() {
	StringBuffer buffer = new StringBuffer(this.parameters.length + 1 * 20);
	if (isConstructor())
		buffer.append(this.declaringClass.shortReadableName());
	else
		buffer.append(this.selector);
	buffer.append('(');
	if (this.parameters != Binding.NO_PARAMETERS) {
		for (int i = 0, length = this.parameters.length; i < length; i++) {
			if (i > 0)
				buffer.append(", "); //$NON-NLS-1$
			buffer.append(this.parameters[i].shortReadableName());
		}
	}
	buffer.append(')');
	int nameLength = buffer.length();
	char[] shortReadableName = new char[nameLength];
	buffer.getChars(0, nameLength, shortReadableName, 0);
	return shortReadableName;
}

/* Answer the receiver's signature.
*
* NOTE: This method should only be used during/after code gen.
* The signature is cached so if the signature of the return type or any parameter
* type changes, the cached state is invalid.
*/
public final char[] signature() /* (ILjava/lang/Thread;)Ljava/lang/Object; */ {
	if (this.signature != null)
		return this.signature;

	StringBuffer buffer = new StringBuffer(this.parameters.length + 1 * 20);
	buffer.append('(');

	TypeBinding[] targetParameters = this.parameters;
	boolean isConstructor = isConstructor();
	if (isConstructor && this.declaringClass.isEnum()) { // insert String name,int ordinal
		buffer.append(ConstantPool.JavaLangStringSignature);
		buffer.append(TypeBinding.INT.signature());
	}
	boolean needSynthetics = isConstructor && this.declaringClass.isNestedType();
	if (needSynthetics) {
		// take into account the synthetic argument type signatures as well
		ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
		if (syntheticArgumentTypes != null) {
			for (int i = 0, count = syntheticArgumentTypes.length; i < count; i++) {
				buffer.append(syntheticArgumentTypes[i].signature());
			}
		}

		if (this instanceof SyntheticMethodBinding) {
			targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
		}
	}

	if (targetParameters != Binding.NO_PARAMETERS) {
		for (int i = 0; i < targetParameters.length; i++) {
			buffer.append(targetParameters[i].signature());
		}
	}
	if (needSynthetics) {
		SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
		int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
		for (int i = 0; i < count; i++) {
			buffer.append(syntheticOuterArguments[i].type.signature());
		}
		// move the extra padding arguments of the synthetic constructor invocation to the end
		for (int i = targetParameters.length, extraLength = this.parameters.length; i < extraLength; i++) {
			buffer.append(this.parameters[i].signature());
		}
	}
	buffer.append(')');
	if (this.returnType != null)
		buffer.append(this.returnType.signature());
	int nameLength = buffer.length();
	this.signature = new char[nameLength];
	buffer.getChars(0, nameLength, this.signature, 0);

	return this.signature;
}
/*
 * This method is used to record references to nested types inside the method signature.
 * This is the one that must be used during code generation.
 *
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=171184
 */
public final char[] signature(ClassFile classFile) {
	if (this.signature != null) {
		if ((this.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
			// we need to record inner classes references
			boolean isConstructor = isConstructor();
			TypeBinding[] targetParameters = this.parameters;
			boolean needSynthetics = isConstructor && this.declaringClass.isNestedType();
			if (needSynthetics) {
				// take into account the synthetic argument type signatures as well
				ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
				if (syntheticArgumentTypes != null) {
					for (int i = 0, count = syntheticArgumentTypes.length; i < count; i++) {
						ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
						if ((syntheticArgumentType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
							Util.recordNestedType(classFile, syntheticArgumentType);
						}
					}
				}
				if (this instanceof SyntheticMethodBinding) {
					targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
				}
			}

			if (targetParameters != Binding.NO_PARAMETERS) {
				for (int i = 0, max = targetParameters.length; i < max; i++) {
					TypeBinding targetParameter = targetParameters[i];
					TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
					if ((leafTargetParameterType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
						Util.recordNestedType(classFile, leafTargetParameterType);
					}
				}
			}
			if (needSynthetics) {
				// move the extra padding arguments of the synthetic constructor invocation to the end
				for (int i = targetParameters.length, extraLength = this.parameters.length; i < extraLength; i++) {
					TypeBinding parameter = this.parameters[i];
					TypeBinding leafParameterType = parameter.leafComponentType();
					if ((leafParameterType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
						Util.recordNestedType(classFile, leafParameterType);
					}
				}
			}
			if (this.returnType != null) {
				TypeBinding ret = this.returnType.leafComponentType();
				if ((ret.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
					Util.recordNestedType(classFile, ret);
				}
			}
		}
		return this.signature;
	}

	StringBuffer buffer = new StringBuffer((this.parameters.length + 1) * 20);
	buffer.append('(');

	TypeBinding[] targetParameters = this.parameters;
	boolean isConstructor = isConstructor();
	if (isConstructor && this.declaringClass.isEnum()) { // insert String name,int ordinal
		buffer.append(ConstantPool.JavaLangStringSignature);
		buffer.append(TypeBinding.INT.signature());
	}
	boolean needSynthetics = isConstructor && this.declaringClass.isNestedType();
	if (needSynthetics) {
		// take into account the synthetic argument type signatures as well
		ReferenceBinding[] syntheticArgumentTypes = this.declaringClass.syntheticEnclosingInstanceTypes();
		if (syntheticArgumentTypes != null) {
			for (int i = 0, count = syntheticArgumentTypes.length; i < count; i++) {
				ReferenceBinding syntheticArgumentType = syntheticArgumentTypes[i];
				if ((syntheticArgumentType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
					this.tagBits |= TagBits.ContainsNestedTypeReferences;
					Util.recordNestedType(classFile, syntheticArgumentType);
				}
				buffer.append(syntheticArgumentType.signature());
			}
		}

		if (this instanceof SyntheticMethodBinding) {
			targetParameters = ((SyntheticMethodBinding)this).targetMethod.parameters;
		}
	}

	if (targetParameters != Binding.NO_PARAMETERS) {
		for (int i = 0, max = targetParameters.length; i < max; i++) {
			TypeBinding targetParameter = targetParameters[i];
			TypeBinding leafTargetParameterType = targetParameter.leafComponentType();
			if ((leafTargetParameterType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
				this.tagBits |= TagBits.ContainsNestedTypeReferences;
				Util.recordNestedType(classFile, leafTargetParameterType);
			}
			buffer.append(targetParameter.signature());
		}
	}
	if (needSynthetics) {
		SyntheticArgumentBinding[] syntheticOuterArguments = this.declaringClass.syntheticOuterLocalVariables();
		int count = syntheticOuterArguments == null ? 0 : syntheticOuterArguments.length;
		for (int i = 0; i < count; i++) {
			buffer.append(syntheticOuterArguments[i].type.signature());
		}
		// move the extra padding arguments of the synthetic constructor invocation to the end
		for (int i = targetParameters.length, extraLength = this.parameters.length; i < extraLength; i++) {
			TypeBinding parameter = this.parameters[i];
			TypeBinding leafParameterType = parameter.leafComponentType();
			if ((leafParameterType.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
				this.tagBits |= TagBits.ContainsNestedTypeReferences;
				Util.recordNestedType(classFile, leafParameterType);
			}
			buffer.append(parameter.signature());
		}
	}
	buffer.append(')');
	if (this.returnType != null) {
		TypeBinding ret = this.returnType.leafComponentType();
		if ((ret.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
			this.tagBits |= TagBits.ContainsNestedTypeReferences;
			Util.recordNestedType(classFile, ret);
		}
		buffer.append(this.returnType.signature());
	}
	int nameLength = buffer.length();
	this.signature = new char[nameLength];
	buffer.getChars(0, nameLength, this.signature, 0);

	return this.signature;
}
public final int sourceEnd() {
	AbstractMethodDeclaration method = sourceMethod();
	if (method == null) {
		if (this.declaringClass instanceof SourceTypeBinding)
			return ((SourceTypeBinding) this.declaringClass).sourceEnd();
		return 0;
	}
	return method.sourceEnd;
}
public AbstractMethodDeclaration sourceMethod() {
	if (isSynthetic()) {
		return null;
	}
	SourceTypeBinding sourceType;
	try {
		sourceType = (SourceTypeBinding) this.declaringClass;
	} catch (ClassCastException e) {
		return null;
	}

	AbstractMethodDeclaration[] methods = sourceType.scope.referenceContext.methods;
	if (methods != null) {
		for (int i = methods.length; --i >= 0;)
			if (this == methods[i].binding)
				return methods[i];
	}
	return null;
}
public final int sourceStart() {
	AbstractMethodDeclaration method = sourceMethod();
	if (method == null) {
		if (this.declaringClass instanceof SourceTypeBinding)
			return ((SourceTypeBinding) this.declaringClass).sourceStart();
		return 0;
	}
	return method.sourceStart;
}

/**
 * Returns the method to use during tiebreak (usually the method itself).
 * For generic method invocations, tiebreak needs to use generic method with erasure substitutes.
 */
public MethodBinding tiebreakMethod() {
	return this;
}
public String toString() {
	StringBuffer output = new StringBuffer(10);
	if ((this.modifiers & ExtraCompilerModifiers.AccUnresolved) != 0) {
		output.append("[unresolved] "); //$NON-NLS-1$
	}
	ASTNode.printModifiers(this.modifiers, output);
	output.append(this.returnType != null ? this.returnType.debugName() : "<no type>"); //$NON-NLS-1$
	output.append(" "); //$NON-NLS-1$
	output.append(this.selector != null ? new String(this.selector) : "<no selector>"); //$NON-NLS-1$
	output.append("("); //$NON-NLS-1$
	if (this.parameters != null) {
		if (this.parameters != Binding.NO_PARAMETERS) {
			for (int i = 0, length = this.parameters.length; i < length; i++) {
				if (i  > 0)
					output.append(", "); //$NON-NLS-1$
				output.append(this.parameters[i] != null ? this.parameters[i].debugName() : "<no argument type>"); //$NON-NLS-1$
			}
		}
	} else {
		output.append("<no argument types>"); //$NON-NLS-1$
	}
	output.append(") "); //$NON-NLS-1$

	if (this.thrownExceptions != null) {
		if (this.thrownExceptions != Binding.NO_EXCEPTIONS) {
			output.append("throws "); //$NON-NLS-1$
			for (int i = 0, length = this.thrownExceptions.length; i < length; i++) {
				if (i  > 0)
					output.append(", "); //$NON-NLS-1$
				output.append((this.thrownExceptions[i] != null) ? this.thrownExceptions[i].debugName() : "<no exception type>"); //$NON-NLS-1$
			}
		}
	} else {
		output.append("<no exception types>"); //$NON-NLS-1$
	}
	return output.toString();
}
public TypeVariableBinding[] typeVariables() {
	return this.typeVariables;
}
}
