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
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class SyntheticMethodBinding extends MethodBinding {

	public FieldBinding targetReadField;		// read access to a field
	public FieldBinding targetWriteField;		// write access to a field
	public MethodBinding targetMethod;			// method or constructor
	public TypeBinding targetEnumType; 			// enum type
	
	public int purpose;

	// fields used to generate enum constants when too many
	public int startIndex;
	public int endIndex;

	public final static int FieldReadAccess = 1; 		// field read
	public final static int FieldWriteAccess = 2; 		// field write
	public final static int SuperFieldReadAccess = 3; // super field read
	public final static int SuperFieldWriteAccess = 4; // super field write
	public final static int MethodAccess = 5; 		// normal method
	public final static int ConstructorAccess = 6; 	// constructor
	public final static int SuperMethodAccess = 7; // super method
	public final static int BridgeMethod = 8; // bridge method
	public final static int EnumValues = 9; // enum #values()
	public final static int EnumValueOf = 10; // enum #valueOf(String)
	public final static int SwitchTable = 11; // switch table method
	public final static int TooManyEnumsConstants = 12; // too many enum constants

	public int sourceStart = 0; // start position of the matching declaration
	public int index; // used for sorting access methods in the class file

	public SyntheticMethodBinding(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess, ReferenceBinding declaringClass) {

		this.modifiers = ClassFileConstants.AccDefault | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		SourceTypeBinding declaringSourceType = (SourceTypeBinding) declaringClass;
		SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;
		this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
		if (isReadAccess) {
			this.returnType = targetField.type;
			if (targetField.isStatic()) {
				this.parameters = Binding.NO_PARAMETERS;
			} else {
				this.parameters = new TypeBinding[1];
				this.parameters[0] = declaringSourceType;
			}
			this.targetReadField = targetField;
			this.purpose = isSuperAccess ? SyntheticMethodBinding.SuperFieldReadAccess : SyntheticMethodBinding.FieldReadAccess;
		} else {
			this.returnType = TypeBinding.VOID;
			if (targetField.isStatic()) {
				this.parameters = new TypeBinding[1];
				this.parameters[0] = targetField.type;
			} else {
				this.parameters = new TypeBinding[2];
				this.parameters[0] = declaringSourceType;
				this.parameters[1] = targetField.type;
			}
			this.targetWriteField = targetField;
			this.purpose = isSuperAccess ? SyntheticMethodBinding.SuperFieldWriteAccess : SyntheticMethodBinding.FieldWriteAccess;
		}
		this.thrownExceptions = Binding.NO_EXCEPTIONS;
		this.declaringClass = declaringSourceType;

		// check for method collision
		boolean needRename;
		do {
			check : {
				needRename = false;
				// check for collision with known methods
				long range;
				MethodBinding[] methods = declaringSourceType.methods();
				if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0) {
					int paramCount = this.parameters.length;
					nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
						MethodBinding method = methods[imethod];
						if (method.parameters.length == paramCount) {
							TypeBinding[] toMatch = method.parameters;
							for (int i = 0; i < paramCount; i++) {
								if (toMatch[i] != this.parameters[i]) {
									continue nextMethod;
								}
							}
							needRename = true;
							break check;
						}
					}
				}
				// check for collision with synthetic accessors
				if (knownAccessMethods != null) {
					for (int i = 0, length = knownAccessMethods.length; i < length; i++) {
						if (knownAccessMethods[i] == null) continue;
						if (CharOperation.equals(this.selector, knownAccessMethods[i].selector) && areParametersEqual(methods[i])) {
							needRename = true;
							break check;
						}
					}
				}
			}
			if (needRename) { // retry with a selector postfixed by a growing methodId
				setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
			}
		} while (needRename);

		// retrieve sourceStart position for the target field for line number attributes
		FieldDeclaration[] fieldDecls = declaringSourceType.scope.referenceContext.fields;
		if (fieldDecls != null) {
			for (int i = 0, max = fieldDecls.length; i < max; i++) {
				if (fieldDecls[i].binding == targetField) {
					this.sourceStart = fieldDecls[i].sourceStart;
					return;
				}
			}
		}

	/* did not find the target field declaration - it is a synthetic one
		public class A {
			public class B {
				public class C {
					void foo() {
						System.out.println("A.this = " + A.this);
					}
				}
			}
			public static void main(String args[]) {
				new A().new B().new C().foo();
			}
		}
	*/
		// We now at this point - per construction - it is for sure an enclosing instance, we are going to
		// show the target field type declaration location.
		this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart; // use the target declaring class name position instead
	}

	public SyntheticMethodBinding(FieldBinding targetField, ReferenceBinding declaringClass, TypeBinding enumBinding, char[] selector) {
		this.modifiers = ClassFileConstants.AccDefault | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		SourceTypeBinding declaringSourceType = (SourceTypeBinding) declaringClass;
		SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;
		this.selector = selector;
		this.returnType = declaringSourceType.scope.createArrayType(TypeBinding.INT, 1);
		this.parameters = Binding.NO_PARAMETERS;
		this.targetReadField = targetField;
		this.targetEnumType = enumBinding;
		this.purpose = SyntheticMethodBinding.SwitchTable;
		this.thrownExceptions = Binding.NO_EXCEPTIONS;
		this.declaringClass = declaringSourceType;

		if (declaringSourceType.isStrictfp()) {
			this.modifiers |= ClassFileConstants.AccStrictfp;
		}
		// check for method collision
		boolean needRename;
		do {
			check : {
				needRename = false;
				// check for collision with known methods
				long range;
				MethodBinding[] methods = declaringSourceType.methods();
				if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0) {
					int paramCount = this.parameters.length;
					nextMethod: for (int imethod = (int)range, end = (int)(range >> 32); imethod <= end; imethod++) {
						MethodBinding method = methods[imethod];
						if (method.parameters.length == paramCount) {
							TypeBinding[] toMatch = method.parameters;
							for (int i = 0; i < paramCount; i++) {
								if (toMatch[i] != this.parameters[i]) {
									continue nextMethod;
								}
							}
							needRename = true;
							break check;
						}
					}
				}
				// check for collision with synthetic accessors
				if (knownAccessMethods != null) {
					for (int i = 0, length = knownAccessMethods.length; i < length; i++) {
						if (knownAccessMethods[i] == null) continue;
						if (CharOperation.equals(this.selector, knownAccessMethods[i].selector) && areParametersEqual(methods[i])) {
							needRename = true;
							break check;
						}
					}
				}
			}
			if (needRename) { // retry with a selector postfixed by a growing methodId
				setSelector(CharOperation.concat(selector, String.valueOf(++methodId).toCharArray()));
			}
		} while (needRename);

		// We now at this point - per construction - it is for sure an enclosing instance, we are going to
		// show the target field type declaration location.
		this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart; // use the target declaring class name position instead
	}

	public SyntheticMethodBinding(MethodBinding targetMethod, boolean isSuperAccess, ReferenceBinding declaringClass) {

		if (targetMethod.isConstructor()) {
			initializeConstructorAccessor(targetMethod);
		} else {
			initializeMethodAccessor(targetMethod, isSuperAccess, declaringClass);
		}
	}

	/**
	 * Construct a bridge method
	 */
	public SyntheticMethodBinding(MethodBinding overridenMethodToBridge, MethodBinding targetMethod, SourceTypeBinding declaringClass) {

	    this.declaringClass = declaringClass;
	    this.selector = overridenMethodToBridge.selector;
	    // amongst other, clear the AccGenericSignature, so as to ensure no remains of original inherited persist (101794)
	    // also use the modifiers from the target method, as opposed to inherited one (147690)
	    this.modifiers = (targetMethod.modifiers | ClassFileConstants.AccBridge | ClassFileConstants.AccSynthetic) & ~(ClassFileConstants.AccSynchronized | ClassFileConstants.AccAbstract | ClassFileConstants.AccNative  | ClassFileConstants.AccFinal | ExtraCompilerModifiers.AccGenericSignature);
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
	    this.returnType = overridenMethodToBridge.returnType;
	    this.parameters = overridenMethodToBridge.parameters;
	    this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
	    this.targetMethod = targetMethod;
	    this.purpose = SyntheticMethodBinding.BridgeMethod;
		SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;
	}

	/**
	 * Construct enum special methods: values or valueOf methods
	 */
	public SyntheticMethodBinding(SourceTypeBinding declaringEnum, char[] selector) {
	    this.declaringClass = declaringEnum;
	    this.selector = selector;
	    this.modifiers = ClassFileConstants.AccPublic | ClassFileConstants.AccStatic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		LookupEnvironment environment = declaringEnum.scope.environment();
	    this.thrownExceptions = Binding.NO_EXCEPTIONS;
		if (selector == TypeConstants.VALUES) {
		    this.returnType = environment.createArrayType(environment.convertToParameterizedType(declaringEnum), 1);
		    this.parameters = Binding.NO_PARAMETERS;
		    this.purpose = SyntheticMethodBinding.EnumValues;
		} else if (selector == TypeConstants.VALUEOF) {
		    this.returnType = environment.convertToParameterizedType(declaringEnum);
		    this.parameters = new TypeBinding[]{ declaringEnum.scope.getJavaLangString() };
		    this.purpose = SyntheticMethodBinding.EnumValueOf;
		}
		SyntheticMethodBinding[] knownAccessMethods = ((SourceTypeBinding)this.declaringClass).syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;
		if (declaringEnum.isStrictfp()) {
			this.modifiers |= ClassFileConstants.AccStrictfp;
		}
	}
	
	/**
	 * Construct enum special methods: values or valueOf methods
	 */
	public SyntheticMethodBinding(SourceTypeBinding declaringEnum, int startIndex, int endIndex) {
		this.declaringClass = declaringEnum;
		SyntheticMethodBinding[] knownAccessMethods = declaringEnum.syntheticMethods();
		this.index = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		StringBuffer buffer = new StringBuffer();
		buffer.append(TypeConstants.SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX).append(this.index);
		this.selector = String.valueOf(buffer).toCharArray(); 
		this.modifiers = ClassFileConstants.AccPrivate | ClassFileConstants.AccStatic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		this.purpose = SyntheticMethodBinding.TooManyEnumsConstants;
		this.thrownExceptions = Binding.NO_EXCEPTIONS;
		this.returnType = TypeBinding.VOID;
		this.parameters = Binding.NO_PARAMETERS;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	// Create a synthetic method that will simply call the super classes method.
	// Used when a public method is inherited from a non-public class into a public class.
	// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=288658
	public SyntheticMethodBinding(MethodBinding overridenMethodToBridge, SourceTypeBinding declaringClass) {

	    this.declaringClass = declaringClass;
	    this.selector = overridenMethodToBridge.selector;
	    // amongst other, clear the AccGenericSignature, so as to ensure no remains of original inherited persist (101794)
	    // also use the modifiers from the target method, as opposed to inherited one (147690)
	    this.modifiers = (overridenMethodToBridge.modifiers | ClassFileConstants.AccBridge | ClassFileConstants.AccSynthetic) & ~(ClassFileConstants.AccSynchronized | ClassFileConstants.AccAbstract | ClassFileConstants.AccNative  | ClassFileConstants.AccFinal | ExtraCompilerModifiers.AccGenericSignature);
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
	    this.returnType = overridenMethodToBridge.returnType;
	    this.parameters = overridenMethodToBridge.parameters;
	    this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
	    this.targetMethod = overridenMethodToBridge;
	    this.purpose = SyntheticMethodBinding.SuperMethodAccess;
		SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;
	}

	/**
	 * An constructor accessor is a constructor with an extra argument (declaringClass), in case of
	 * collision with an existing constructor, then add again an extra argument (declaringClass again).
	 */
	 public void initializeConstructorAccessor(MethodBinding accessedConstructor) {

		this.targetMethod = accessedConstructor;
		this.modifiers = ClassFileConstants.AccDefault | ClassFileConstants.AccSynthetic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		SourceTypeBinding sourceType = (SourceTypeBinding) accessedConstructor.declaringClass;
		SyntheticMethodBinding[] knownSyntheticMethods = sourceType.syntheticMethods();
		this.index = knownSyntheticMethods == null ? 0 : knownSyntheticMethods.length;

		this.selector = accessedConstructor.selector;
		this.returnType = accessedConstructor.returnType;
		this.purpose = SyntheticMethodBinding.ConstructorAccess;
		final int parametersLength = accessedConstructor.parameters.length;
		this.parameters = new TypeBinding[parametersLength + 1];
		System.arraycopy(
			accessedConstructor.parameters,
			0,
			this.parameters,
			0,
			parametersLength);
		this.parameters[parametersLength] =
			accessedConstructor.declaringClass;
		this.thrownExceptions = accessedConstructor.thrownExceptions;
		this.declaringClass = sourceType;

		// check for method collision
		boolean needRename;
		do {
			check : {
				needRename = false;
				// check for collision with known methods
				MethodBinding[] methods = sourceType.methods();
				for (int i = 0, length = methods.length; i < length; i++) {
					if (CharOperation.equals(this.selector, methods[i].selector) && areParameterErasuresEqual(methods[i])) {
						needRename = true;
						break check;
					}
				}
				// check for collision with synthetic accessors
				if (knownSyntheticMethods != null) {
					for (int i = 0, length = knownSyntheticMethods.length; i < length; i++) {
						if (knownSyntheticMethods[i] == null)
							continue;
						if (CharOperation.equals(this.selector, knownSyntheticMethods[i].selector) && areParameterErasuresEqual(knownSyntheticMethods[i])) {
							needRename = true;
							break check;
						}
					}
				}
			}
			if (needRename) { // retry with a new extra argument
				int length = this.parameters.length;
				System.arraycopy(
					this.parameters,
					0,
					this.parameters = new TypeBinding[length + 1],
					0,
					length);
				this.parameters[length] = this.declaringClass;
			}
		} while (needRename);

		// retrieve sourceStart position for the target method for line number attributes
		AbstractMethodDeclaration[] methodDecls =
			sourceType.scope.referenceContext.methods;
		if (methodDecls != null) {
			for (int i = 0, length = methodDecls.length; i < length; i++) {
				if (methodDecls[i].binding == accessedConstructor) {
					this.sourceStart = methodDecls[i].sourceStart;
					return;
				}
			}
		}
	}

	/**
	 * An method accessor is a method with an access$N selector, where N is incremented in case of collisions.
	 */
	public void initializeMethodAccessor(MethodBinding accessedMethod, boolean isSuperAccess, ReferenceBinding receiverType) {

		this.targetMethod = accessedMethod;
		this.modifiers = ClassFileConstants.AccDefault | ClassFileConstants.AccStatic | ClassFileConstants.AccSynthetic;
		this.tagBits |= (TagBits.AnnotationResolved | TagBits.DeprecatedAnnotationResolved);
		SourceTypeBinding declaringSourceType = (SourceTypeBinding) receiverType;
		SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
		int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
		this.index = methodId;

		this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
		this.returnType = accessedMethod.returnType;
		this.purpose = isSuperAccess ? SyntheticMethodBinding.SuperMethodAccess : SyntheticMethodBinding.MethodAccess;

		if (accessedMethod.isStatic()) {
			this.parameters = accessedMethod.parameters;
		} else {
			this.parameters = new TypeBinding[accessedMethod.parameters.length + 1];
			this.parameters[0] = declaringSourceType;
			System.arraycopy(accessedMethod.parameters, 0, this.parameters, 1, accessedMethod.parameters.length);
		}
		this.thrownExceptions = accessedMethod.thrownExceptions;
		this.declaringClass = declaringSourceType;

		// check for method collision
		boolean needRename;
		do {
			check : {
				needRename = false;
				// check for collision with known methods
				MethodBinding[] methods = declaringSourceType.methods();
				for (int i = 0, length = methods.length; i < length; i++) {
					if (CharOperation.equals(this.selector, methods[i].selector) && areParameterErasuresEqual(methods[i])) {
						needRename = true;
						break check;
					}
				}
				// check for collision with synthetic accessors
				if (knownAccessMethods != null) {
					for (int i = 0, length = knownAccessMethods.length; i < length; i++) {
						if (knownAccessMethods[i] == null) continue;
						if (CharOperation.equals(this.selector, knownAccessMethods[i].selector) && areParameterErasuresEqual(knownAccessMethods[i])) {
							needRename = true;
							break check;
						}
					}
				}
			}
			if (needRename) { // retry with a selector & a growing methodId
				setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(++methodId).toCharArray()));
			}
		} while (needRename);

		// retrieve sourceStart position for the target method for line number attributes
		AbstractMethodDeclaration[] methodDecls = declaringSourceType.scope.referenceContext.methods;
		if (methodDecls != null) {
			for (int i = 0, length = methodDecls.length; i < length; i++) {
				if (methodDecls[i].binding == accessedMethod) {
					this.sourceStart = methodDecls[i].sourceStart;
					return;
				}
			}
		}
	}

	protected boolean isConstructorRelated() {
		return this.purpose == SyntheticMethodBinding.ConstructorAccess;
	}
}
