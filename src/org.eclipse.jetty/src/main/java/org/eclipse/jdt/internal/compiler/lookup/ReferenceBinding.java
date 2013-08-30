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

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;

/*
Not all fields defined by this type (& its subclasses) are initialized when it is created.
Some are initialized only when needed.

Accessors have been provided for some public fields so all TypeBindings have the same API...
but access public fields directly whenever possible.
Non-public fields have accessors which should be used everywhere you expect the field to be initialized.

null is NOT a valid value for a non-public field... it just means the field is not initialized.
*/

abstract public class ReferenceBinding extends TypeBinding {

	public char[][] compoundName;
	public char[] sourceName;
	public int modifiers;
	public PackageBinding fPackage;
	char[] fileName;
	char[] constantPoolName;
	char[] signature;

	private SimpleLookupTable compatibleCache;

	public static final ReferenceBinding LUB_GENERIC = new ReferenceBinding() { /* used for lub computation */};

	private static final Comparator FIELD_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			char[] n1 = ((FieldBinding) o1).name;
			char[] n2 = ((FieldBinding) o2).name;
			return ReferenceBinding.compare(n1, n2, n1.length, n2.length);
		}
	};
	private static final Comparator METHOD_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			MethodBinding m1 = (MethodBinding) o1;
			MethodBinding m2 = (MethodBinding) o2;
			char[] s1 = m1.selector;
			char[] s2 = m2.selector;
			int c = ReferenceBinding.compare(s1, s2, s1.length, s2.length);
			return c == 0 ? m1.parameters.length - m2.parameters.length : c;
		}
	};

public static FieldBinding binarySearch(char[] name, FieldBinding[] sortedFields) {
	if (sortedFields == null)
		return null;
	int max = sortedFields.length;
	if (max == 0)
		return null;
	int left = 0, right = max - 1, nameLength = name.length;
	int mid = 0;
	char[] midName;
	while (left <= right) {
		mid = left + (right - left) /2;
		int compare = compare(name, midName = sortedFields[mid].name, nameLength, midName.length);
		if (compare < 0) {
			right = mid-1;
		} else if (compare > 0) {
			left = mid+1;
		} else {
			return sortedFields[mid];
		}
	}
	return null;
}

/**
 * Returns a combined range value representing: (start + (end<<32)), where start is the index of the first matching method
 * (remember methods are sorted alphabetically on selectors), and end is the index of last contiguous methods with same
 * selector.
 * -1 means no method got found
 * @param selector
 * @param sortedMethods
 * @return (start + (end<<32)) or -1 if no method found
 */
public static long binarySearch(char[] selector, MethodBinding[] sortedMethods) {
	if (sortedMethods == null)
		return -1;
	int max = sortedMethods.length;
	if (max == 0)
		return -1;
	int left = 0, right = max - 1, selectorLength = selector.length;
	int mid = 0;
	char[] midSelector;
	while (left <= right) {
		mid = left + (right - left) /2;
		int compare = compare(selector, midSelector = sortedMethods[mid].selector, selectorLength, midSelector.length);
		if (compare < 0) {
			right = mid-1;
		} else if (compare > 0) {
			left = mid+1;
		} else {
			int start = mid, end = mid;
			// find first method with same selector
			while (start > left && CharOperation.equals(sortedMethods[start-1].selector, selector)){ start--; }
			// find last method with same selector
			while (end < right && CharOperation.equals(sortedMethods[end+1].selector, selector)){ end++; }
			return start + ((long)end<< 32);
		}
	}
	return -1;
}

/**
 * Compares two strings lexicographically.
 * The comparison is based on the Unicode value of each character in
 * the strings.
 *
 * @return  the value <code>0</code> if the str1 is equal to str2;
 *          a value less than <code>0</code> if str1
 *          is lexicographically less than str2;
 *          and a value greater than <code>0</code> if str1 is
 *          lexicographically greater than str2.
 */
static int compare(char[] str1, char[] str2, int len1, int len2) {
	int n= Math.min(len1, len2);
	int i= 0;
	while (n-- != 0) {
		char c1= str1[i];
		char c2= str2[i++];
		if (c1 != c2) {
			return c1 - c2;
		}
	}
	return len1 - len2;
}

/**
 * Sort the field array using a quicksort
 */
public static void sortFields(FieldBinding[] sortedFields, int left, int right) {
	Arrays.sort(sortedFields, left, right, FIELD_COMPARATOR);
}

/**
 * Sort the field array using a quicksort
 */
public static void sortMethods(MethodBinding[] sortedMethods, int left, int right) {
	Arrays.sort(sortedMethods, left, right, METHOD_COMPARATOR);
}

/**
 * Return the array of resolvable fields (resilience)
 */
public FieldBinding[] availableFields() {
	return fields();
}

/**
 * Return the array of resolvable methods (resilience)
 */
public MethodBinding[] availableMethods() {
	return methods();
}

/**
 * Answer true if the receiver can be instantiated
 */
public boolean canBeInstantiated() {
	return (this.modifiers & (ClassFileConstants.AccAbstract | ClassFileConstants.AccInterface | ClassFileConstants.AccEnum | ClassFileConstants.AccAnnotation)) == 0;
}

/**
 * Answer true if the receiver is visible to the invocationPackage.
 */
public final boolean canBeSeenBy(PackageBinding invocationPackage) {
	if (isPublic()) return true;
	if (isPrivate()) return false;

	// isProtected() or isDefault()
	return invocationPackage == this.fPackage;
}

/**
 * Answer true if the receiver is visible to the receiverType and the invocationType.
 */
public final boolean canBeSeenBy(ReferenceBinding receiverType, ReferenceBinding invocationType) {
	if (isPublic()) return true;

	if (invocationType == this && invocationType == receiverType) return true;

	if (isProtected()) {
		// answer true if the invocationType is the declaringClass or they are in the same package
		// OR the invocationType is a subclass of the declaringClass
		//    AND the invocationType is the invocationType or its subclass
		//    OR the type is a static method accessed directly through a type
		//    OR previous assertions are true for one of the enclosing type
		if (invocationType == this) return true;
		if (invocationType.fPackage == this.fPackage) return true;

		TypeBinding currentType = invocationType.erasure();
		TypeBinding declaringClass = enclosingType().erasure(); // protected types always have an enclosing one
		if (declaringClass == invocationType) return true;
		if (declaringClass == null) return false; // could be null if incorrect top-level protected type
		//int depth = 0;
		do {
			if (currentType.findSuperTypeOriginatingFrom(declaringClass) != null) return true;
			//depth++;
			currentType = currentType.enclosingType();
		} while (currentType != null);
		return false;
	}

	if (isPrivate()) {
		// answer true if the receiverType is the receiver or its enclosingType
		// AND the invocationType and the receiver have a common enclosingType
		receiverCheck: {
			if (!(receiverType == this || receiverType == enclosingType())) {
				// special tolerance for type variable direct bounds, but only if compliance <= 1.6, see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=334622
				if (receiverType.isTypeVariable()) {
					TypeVariableBinding typeVariable = (TypeVariableBinding) receiverType;
					if (typeVariable.environment.globalOptions.complianceLevel <= ClassFileConstants.JDK1_6 && (typeVariable.isErasureBoundTo(erasure()) || typeVariable.isErasureBoundTo(enclosingType().erasure())))
						break receiverCheck;
				}
				return false;
			}
		}

		if (invocationType != this) {
			ReferenceBinding outerInvocationType = invocationType;
			ReferenceBinding temp = outerInvocationType.enclosingType();
			while (temp != null) {
				outerInvocationType = temp;
				temp = temp.enclosingType();
			}

			ReferenceBinding outerDeclaringClass = (ReferenceBinding)erasure();
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
	if (invocationType.fPackage != this.fPackage) return false;

	ReferenceBinding currentType = receiverType;
	TypeBinding originalDeclaringClass = (enclosingType() == null ? this : enclosingType()).original();
	do {
		if (currentType.isCapture()) {  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=285002
			if (originalDeclaringClass == currentType.erasure().original()) return true;
		} else { 
			if (originalDeclaringClass == currentType.original()) return true;
		}
		PackageBinding currentPackage = currentType.fPackage;
		// package could be null for wildcards/intersection types, ignore and recurse in superclass
		if (currentPackage != null && currentPackage != this.fPackage) return false;
	} while ((currentType = currentType.superclass()) != null);
	return false;
}

/**
 * Answer true if the receiver is visible to the type provided by the scope.
 */
public final boolean canBeSeenBy(Scope scope) {
	if (isPublic()) return true;

	SourceTypeBinding invocationType = scope.enclosingSourceType();
	if (invocationType == this) return true;

	if (invocationType == null) // static import call
		return !isPrivate() && scope.getCurrentPackage() == this.fPackage;

	if (isProtected()) {
		// answer true if the invocationType is the declaringClass or they are in the same package
		// OR the invocationType is a subclass of the declaringClass
		//    AND the invocationType is the invocationType or its subclass
		//    OR the type is a static method accessed directly through a type
		//    OR previous assertions are true for one of the enclosing type
		if (invocationType.fPackage == this.fPackage) return true;

		TypeBinding declaringClass = enclosingType(); // protected types always have an enclosing one
		if (declaringClass == null) return false; // could be null if incorrect top-level protected type
		declaringClass = declaringClass.erasure();// erasure cannot be null
		TypeBinding currentType = invocationType.erasure();
		// int depth = 0;
		do {
			if (declaringClass == invocationType) return true;
			if (currentType.findSuperTypeOriginatingFrom(declaringClass) != null) return true;
			// depth++;
			currentType = currentType.enclosingType();
		} while (currentType != null);
		return false;
	}
	if (isPrivate()) {
		// answer true if the receiver and the invocationType have a common enclosingType
		// already know they are not the identical type
		ReferenceBinding outerInvocationType = invocationType;
		ReferenceBinding temp = outerInvocationType.enclosingType();
		while (temp != null) {
			outerInvocationType = temp;
			temp = temp.enclosingType();
		}

		ReferenceBinding outerDeclaringClass = (ReferenceBinding)erasure();
		temp = outerDeclaringClass.enclosingType();
		while (temp != null) {
			outerDeclaringClass = temp;
			temp = temp.enclosingType();
		}
		return outerInvocationType == outerDeclaringClass;
	}

	// isDefault()
	return invocationType.fPackage == this.fPackage;
}

public char[] computeGenericTypeSignature(TypeVariableBinding[] typeVariables) {

	boolean isMemberOfGeneric = isMemberType() && (enclosingType().modifiers & ExtraCompilerModifiers.AccGenericSignature) != 0;
	if (typeVariables == Binding.NO_TYPE_VARIABLES && !isMemberOfGeneric) {
		return signature();
	}
	StringBuffer sig = new StringBuffer(10);
	if (isMemberOfGeneric) {
	    char[] typeSig = enclosingType().genericTypeSignature();
	    sig.append(typeSig, 0, typeSig.length-1); // copy all but trailing semicolon
	    sig.append('.'); // NOTE: cannot override trailing ';' with '.' in enclosing signature, since shared char[]
	    sig.append(this.sourceName);
	}	else {
	    char[] typeSig = signature();
	    sig.append(typeSig, 0, typeSig.length-1); // copy all but trailing semicolon
	}
	if (typeVariables == Binding.NO_TYPE_VARIABLES) {
	    sig.append(';');
	} else {
	    sig.append('<');
	    for (int i = 0, length = typeVariables.length; i < length; i++) {
	        sig.append(typeVariables[i].genericTypeSignature());
	    }
	    sig.append(">;"); //$NON-NLS-1$
	}
	int sigLength = sig.length();
	char[] result = new char[sigLength];
	sig.getChars(0, sigLength, result, 0);
	return result;
}

public void computeId() {
	// try to avoid multiple checks against a package/type name
	switch (this.compoundName.length) {

		case 3 :
			if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]))
				return;

			char[] packageName = this.compoundName[1];
			if (packageName.length == 0) return; // just to be safe
			char[] typeName = this.compoundName[2];
			if (typeName.length == 0) return; // just to be safe
			// remaining types MUST be in java.*.*
			if (!CharOperation.equals(TypeConstants.LANG, this.compoundName[1])) {
				switch (packageName[0]) {
					case 'i' :
						if (CharOperation.equals(packageName, TypeConstants.IO)) {
							switch (typeName[0]) {
								case 'E' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_EXTERNALIZABLE[2]))
										this.id = TypeIds.T_JavaIoExternalizable;
									return;
								case 'I' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_IOEXCEPTION[2]))
										this.id = TypeIds.T_JavaIoException;
									return;
								case 'O' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_OBJECTSTREAMEXCEPTION[2]))
										this.id = TypeIds.T_JavaIoObjectStreamException;
									return;
								case 'P' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_PRINTSTREAM[2]))
										this.id = TypeIds.T_JavaIoPrintStream;
									return;
								case 'S' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_SERIALIZABLE[2]))
										this.id = TypeIds.T_JavaIoSerializable;
									return;
							}
						}
						return;
					case 'u' :
						if (CharOperation.equals(packageName, TypeConstants.UTIL)) {
							switch (typeName[0]) {
								case 'C' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_COLLECTION[2]))
										this.id = TypeIds.T_JavaUtilCollection;
									return;
								case 'I' :
									if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_ITERATOR[2]))
										this.id = TypeIds.T_JavaUtilIterator;
									return;
							}
						}
						return;
				}
				return;
			}

			// remaining types MUST be in java.lang.*
			switch (typeName[0]) {
				case 'A' :
					switch(typeName.length) {
						case 13 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_AUTOCLOSEABLE[2]))
								this.id = TypeIds.T_JavaLangAutoCloseable;
							return;
						case 14:
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ASSERTIONERROR[2]))
								this.id = TypeIds.T_JavaLangAssertionError;
							return;
					}
					return;
				case 'B' :
					switch (typeName.length) {
						case 4 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BYTE[2]))
								this.id = TypeIds.T_JavaLangByte;
							return;
						case 7 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BOOLEAN[2]))
								this.id = TypeIds.T_JavaLangBoolean;
							return;
					}
					return;
				case 'C' :
					switch (typeName.length) {
						case 5 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASS[2]))
								this.id = TypeIds.T_JavaLangClass;
							return;
						case 9 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CHARACTER[2]))
								this.id = TypeIds.T_JavaLangCharacter;
							else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLONEABLE[2]))
							    this.id = TypeIds.T_JavaLangCloneable;
							return;
						case 22 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION[2]))
								this.id = TypeIds.T_JavaLangClassNotFoundException;
							return;
					}
					return;
				case 'D' :
					switch (typeName.length) {
						case 6 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DOUBLE[2]))
								this.id = TypeIds.T_JavaLangDouble;
							return;
						case 10 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DEPRECATED[2]))
								this.id = TypeIds.T_JavaLangDeprecated;
							return;
					}
					return;
				case 'E' :
					switch (typeName.length) {
						case 4 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ENUM[2]))
								this.id = TypeIds.T_JavaLangEnum;
							return;
						case 5 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ERROR[2]))
								this.id = TypeIds.T_JavaLangError;
							return;
						case 9 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_EXCEPTION[2]))
								this.id = TypeIds.T_JavaLangException;
							return;
					}
					return;
				case 'F' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FLOAT[2]))
						this.id = TypeIds.T_JavaLangFloat;
					return;
				case 'I' :
					switch (typeName.length) {
						case 7 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INTEGER[2]))
								this.id = TypeIds.T_JavaLangInteger;
							return;
						case 8 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ITERABLE[2]))
								this.id = TypeIds.T_JavaLangIterable;
							return;
						case 24 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION[2]))
								this.id = TypeIds.T_JavaLangIllegalArgumentException;
							return;
					}
					return;
				case 'L' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_LONG[2]))
						this.id = TypeIds.T_JavaLangLong;
					return;
				case 'N' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_NOCLASSDEFERROR[2]))
						this.id = TypeIds.T_JavaLangNoClassDefError;
					return;
				case 'O' :
					switch (typeName.length) {
						case 6 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OBJECT[2]))
								this.id = TypeIds.T_JavaLangObject;
							return;
						case 8 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OVERRIDE[2]))
								this.id = TypeIds.T_JavaLangOverride;
							return;
					}
					return;
				case 'R' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION[2]))
						this.id = 	TypeIds.T_JavaLangRuntimeException;
					break;
				case 'S' :
					switch (typeName.length) {
						case 5 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SHORT[2]))
								this.id = TypeIds.T_JavaLangShort;
							return;
						case 6 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRING[2]))
								this.id = TypeIds.T_JavaLangString;
							else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SYSTEM[2]))
								this.id = TypeIds.T_JavaLangSystem;
							return;
						case 11 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SAFEVARARGS[2]))
								this.id = TypeIds.T_JavaLangSafeVarargs;
							return;
						case 12 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUFFER[2]))
								this.id = TypeIds.T_JavaLangStringBuffer;
							return;
						case 13 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUILDER[2]))
								this.id = TypeIds.T_JavaLangStringBuilder;
							return;
						case 16 :
							if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SUPPRESSWARNINGS[2]))
								this.id = TypeIds.T_JavaLangSuppressWarnings;
							return;
					}
					return;
				case 'T' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_THROWABLE[2]))
						this.id = TypeIds.T_JavaLangThrowable;
					return;
				case 'V' :
					if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_VOID[2]))
						this.id = TypeIds.T_JavaLangVoid;
					return;
			}
		break;

		case 4:
			if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]))
				return;
			packageName = this.compoundName[1];
			if (packageName.length == 0) return; // just to be safe

			packageName = this.compoundName[2];
			if (packageName.length == 0) return; // just to be safe
			typeName = this.compoundName[3];
			if (typeName.length == 0) return; // just to be safe
			switch (packageName[0]) {
				case 'a' :
					if (CharOperation.equals(packageName, TypeConstants.ANNOTATION)) {
						switch (typeName[0]) {
							case 'A' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION[3]))
									this.id = TypeIds.T_JavaLangAnnotationAnnotation;
								return;
							case 'D' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED[3]))
									this.id = TypeIds.T_JavaLangAnnotationDocumented;
								return;
							case 'E' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE[3]))
									this.id = TypeIds.T_JavaLangAnnotationElementType;
								return;
							case 'I' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_INHERITED[3]))
									this.id = TypeIds.T_JavaLangAnnotationInherited;
								return;
							case 'R' :
								switch (typeName.length) {
									case 9 :
										if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTION[3]))
											this.id = TypeIds.T_JavaLangAnnotationRetention;
										return;
									case 15 :
										if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY[3]))
											this.id = TypeIds.T_JavaLangAnnotationRetentionPolicy;
										return;
								}
								return;
							case 'T' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_TARGET[3]))
									this.id = TypeIds.T_JavaLangAnnotationTarget;
								return;
						}
					}
					return;
				case 'i':
					if (CharOperation.equals(packageName, TypeConstants.INVOKE)) {
						if (typeName.length == 0) return; // just to be safe
						switch (typeName[0]) {
							case 'M' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE[3]))
									this.id = TypeIds.T_JavaLangInvokeMethodHandlePolymorphicSignature;
								return;
						}
					}
					return;
				case 'r' :
					if (CharOperation.equals(packageName, TypeConstants.REFLECT)) {
						switch (typeName[0]) {
							case 'C' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_CONSTRUCTOR[2]))
									this.id = TypeIds.T_JavaLangReflectConstructor;
								return;
							case 'F' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_FIELD[2]))
									this.id = TypeIds.T_JavaLangReflectField;
								return;
							case 'M' :
								if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_METHOD[2]))
									this.id = TypeIds.T_JavaLangReflectMethod;
								return;
						}
					}
					return;
			}
			break;
		case 5 :
			if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]))
				return;
			packageName = this.compoundName[1];
			if (packageName.length == 0) return; // just to be safe

			if (CharOperation.equals(TypeConstants.LANG, packageName)) {
				packageName = this.compoundName[2];
				if (packageName.length == 0) return; // just to be safe
				switch (packageName[0]) {
					case 'i' :
						if (CharOperation.equals(packageName, TypeConstants.INVOKE)) { 
							typeName = this.compoundName[3];
							if (typeName.length == 0) return; // just to be safe
							switch (typeName[0]) {
								case 'M' :
									char[] memberTypeName = this.compoundName[4];
									if (memberTypeName.length == 0) return; // just to be safe
									if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[3])
											&& CharOperation.equals(memberTypeName, TypeConstants.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE[4]))
										this.id = TypeIds.T_JavaLangInvokeMethodHandlePolymorphicSignature;
									return;
							}
						}
						return;
				}
				return;
			}
	}
}

/**
 * p.X<T extends Y & I, U extends Y> {} -> Lp/X<TT;TU;>;
 */
public char[] computeUniqueKey(boolean isLeaf) {
	if (!isLeaf) return signature();
	return genericTypeSignature();
}

/**
 * Answer the receiver's constant pool name.
 *
 * NOTE: This method should only be used during/after code gen.
 */
public char[] constantPoolName() /* java/lang/Object */ {
	if (this.constantPoolName != null) return this.constantPoolName;
	return this.constantPoolName = CharOperation.concatWith(this.compoundName, '/');
}

public String debugName() {
	return (this.compoundName != null) ? new String(readableName()) : "UNNAMED TYPE"; //$NON-NLS-1$
}

public final int depth() {
	int depth = 0;
	ReferenceBinding current = this;
	while ((current = current.enclosingType()) != null)
		depth++;
	return depth;
}

public boolean detectAnnotationCycle() {
	if ((this.tagBits & TagBits.EndAnnotationCheck) != 0) return false; // already checked
	if ((this.tagBits & TagBits.BeginAnnotationCheck) != 0) return true; // in the middle of checking its methods

	this.tagBits |= TagBits.BeginAnnotationCheck;
	MethodBinding[] currentMethods = methods();
	boolean inCycle = false; // check each method before failing
	for (int i = 0, l = currentMethods.length; i < l; i++) {
		TypeBinding returnType = currentMethods[i].returnType.leafComponentType().erasure();
		if (this == returnType) {
			if (this instanceof SourceTypeBinding) {
				MethodDeclaration decl = (MethodDeclaration) currentMethods[i].sourceMethod();
				((SourceTypeBinding) this).scope.problemReporter().annotationCircularity(this, this, decl != null ? decl.returnType : null);
			}
		} else if (returnType.isAnnotationType() && ((ReferenceBinding) returnType).detectAnnotationCycle()) {
			if (this instanceof SourceTypeBinding) {
				MethodDeclaration decl = (MethodDeclaration) currentMethods[i].sourceMethod();
				((SourceTypeBinding) this).scope.problemReporter().annotationCircularity(this, returnType, decl != null ? decl.returnType : null);
			}
			inCycle = true;
		}
	}
	if (inCycle)
		return true;
	this.tagBits |= TagBits.EndAnnotationCheck;
	return false;
}

public final ReferenceBinding enclosingTypeAt(int relativeDepth) {
	ReferenceBinding current = this;
	while (relativeDepth-- > 0 && current != null)
		current = current.enclosingType();
	return current;
}

public int enumConstantCount() {
	int count = 0;
	FieldBinding[] fields = fields();
	for (int i = 0, length = fields.length; i < length; i++) {
		if ((fields[i].modifiers & ClassFileConstants.AccEnum) != 0) count++;
	}
	return count;
}

public int fieldCount() {
	return fields().length;
}

public FieldBinding[] fields() {
	return Binding.NO_FIELDS;
}

public final int getAccessFlags() {
	return this.modifiers & ExtraCompilerModifiers.AccJustFlag;
}

/**
 * @return the JSR 175 annotations for this type.
 */
public AnnotationBinding[] getAnnotations() {
	return retrieveAnnotations(this);
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#getAnnotationTagBits()
 */
public long getAnnotationTagBits() {
	return this.tagBits;
}

/**
 * @return the enclosingInstancesSlotSize
 */
public int getEnclosingInstancesSlotSize() {
	if (isStatic()) return 0;
	return enclosingType() == null ? 0 : 1;
}

public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
	return null;
}

public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
	return null;
}
public FieldBinding getField(char[] fieldName, boolean needResolve) {
	return null;
}
/**
 * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
 */
public char[] getFileName() {
	return this.fileName;
}

public ReferenceBinding getMemberType(char[] typeName) {
	ReferenceBinding[] memberTypes = memberTypes();
	for (int i = memberTypes.length; --i >= 0;)
		if (CharOperation.equals(memberTypes[i].sourceName, typeName))
			return memberTypes[i];
	return null;
}

public MethodBinding[] getMethods(char[] selector) {
	return Binding.NO_METHODS;
}

// Answer methods named selector, which take no more than the suggestedParameterLength.
// The suggested parameter length is optional and may not be guaranteed by every type.
public MethodBinding[] getMethods(char[] selector, int suggestedParameterLength) {
	return getMethods(selector);
}

/**
 * @return the outerLocalVariablesSlotSize
 */
public int getOuterLocalVariablesSlotSize() {
	return 0;
}

public PackageBinding getPackage() {
	return this.fPackage;
}

public TypeVariableBinding getTypeVariable(char[] variableName) {
	TypeVariableBinding[] typeVariables = typeVariables();
	for (int i = typeVariables.length; --i >= 0;)
		if (CharOperation.equals(typeVariables[i].sourceName, variableName))
			return typeVariables[i];
	return null;
}

public int hashCode() {
	// ensure ReferenceBindings hash to the same posiiton as UnresolvedReferenceBindings so they can be replaced without rehashing
	// ALL ReferenceBindings are unique when created so equals() is the same as ==
	return (this.compoundName == null || this.compoundName.length == 0)
		? super.hashCode()
		: CharOperation.hashCode(this.compoundName[this.compoundName.length - 1]);
}

/**
 * Returns true if the two types have an incompatible common supertype,
 * e.g. List<String> and List<Integer>
 */
public boolean hasIncompatibleSuperType(ReferenceBinding otherType) {

    if (this == otherType) return false;

	ReferenceBinding[] interfacesToVisit = null;
	int nextPosition = 0;
    ReferenceBinding currentType = this;
	TypeBinding match;
	do {
		match = otherType.findSuperTypeOriginatingFrom(currentType);
		if (match != null && match.isProvablyDistinct(currentType))
			return true;

		ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
		if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
			if (interfacesToVisit == null) {
				interfacesToVisit = itsInterfaces;
				nextPosition = interfacesToVisit.length;
			} else {
				int itsLength = itsInterfaces.length;
				if (nextPosition + itsLength >= interfacesToVisit.length)
					System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
				nextInterface : for (int a = 0; a < itsLength; a++) {
					ReferenceBinding next = itsInterfaces[a];
					for (int b = 0; b < nextPosition; b++)
						if (next == interfacesToVisit[b]) continue nextInterface;
					interfacesToVisit[nextPosition++] = next;
				}
			}
		}
	} while ((currentType = currentType.superclass()) != null);

	for (int i = 0; i < nextPosition; i++) {
		currentType = interfacesToVisit[i];
		if (currentType == otherType) return false;
		match = otherType.findSuperTypeOriginatingFrom(currentType);
		if (match != null && match.isProvablyDistinct(currentType))
			return true;

		ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
		if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) {
			int itsLength = itsInterfaces.length;
			if (nextPosition + itsLength >= interfacesToVisit.length)
				System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
			nextInterface : for (int a = 0; a < itsLength; a++) {
				ReferenceBinding next = itsInterfaces[a];
				for (int b = 0; b < nextPosition; b++)
					if (next == interfacesToVisit[b]) continue nextInterface;
				interfacesToVisit[nextPosition++] = next;
			}
		}
	}
	return false;
}

public boolean hasMemberTypes() {
    return false;
}

public final boolean hasRestrictedAccess() {
	return (this.modifiers & ExtraCompilerModifiers.AccRestrictedAccess) != 0;
}

/** Answer true if the receiver implements anInterface or is identical to anInterface.
* If searchHierarchy is true, then also search the receiver's superclasses.
*
* NOTE: Assume that anInterface is an interface.
*/
public boolean implementsInterface(ReferenceBinding anInterface, boolean searchHierarchy) {
	if (this == anInterface)
		return true;

	ReferenceBinding[] interfacesToVisit = null;
	int nextPosition = 0;
	ReferenceBinding currentType = this;
	do {
		ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
		if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) { // in code assist cases when source types are added late, may not be finished connecting hierarchy
			if (interfacesToVisit == null) {
				interfacesToVisit = itsInterfaces;
				nextPosition = interfacesToVisit.length;
			} else {
				int itsLength = itsInterfaces.length;
				if (nextPosition + itsLength >= interfacesToVisit.length)
					System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
				nextInterface : for (int a = 0; a < itsLength; a++) {
					ReferenceBinding next = itsInterfaces[a];
					for (int b = 0; b < nextPosition; b++)
						if (next == interfacesToVisit[b]) continue nextInterface;
					interfacesToVisit[nextPosition++] = next;
				}
			}
		}
	} while (searchHierarchy && (currentType = currentType.superclass()) != null);

	for (int i = 0; i < nextPosition; i++) {
		currentType = interfacesToVisit[i];
		if (currentType.isEquivalentTo(anInterface))
			return true;

		ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
		if (itsInterfaces != null && itsInterfaces != Binding.NO_SUPERINTERFACES) { // in code assist cases when source types are added late, may not be finished connecting hierarchy
			int itsLength = itsInterfaces.length;
			if (nextPosition + itsLength >= interfacesToVisit.length)
				System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
			nextInterface : for (int a = 0; a < itsLength; a++) {
				ReferenceBinding next = itsInterfaces[a];
				for (int b = 0; b < nextPosition; b++)
					if (next == interfacesToVisit[b]) continue nextInterface;
				interfacesToVisit[nextPosition++] = next;
			}
		}
	}
	return false;
}

// Internal method... assume its only sent to classes NOT interfaces
boolean implementsMethod(MethodBinding method) {
	char[] selector = method.selector;
	ReferenceBinding type = this;
	while (type != null) {
		MethodBinding[] methods = type.methods();
		long range;
		if ((range = ReferenceBinding.binarySearch(selector, methods)) >= 0) {
			int start = (int) range, end = (int) (range >> 32);
			for (int i = start; i <= end; i++) {
				if (methods[i].areParametersEqual(method))
					return true;
			}
		}
		type = type.superclass();
	}
	return false;
}

/**
 * Answer true if the receiver is an abstract type
*/
public final boolean isAbstract() {
	return (this.modifiers & ClassFileConstants.AccAbstract) != 0;
}

public boolean isAnnotationType() {
	return (this.modifiers & ClassFileConstants.AccAnnotation) != 0;
}

public final boolean isBinaryBinding() {
	return (this.tagBits & TagBits.IsBinaryBinding) != 0;
}

public boolean isClass() {
	return (this.modifiers & (ClassFileConstants.AccInterface | ClassFileConstants.AccAnnotation | ClassFileConstants.AccEnum)) == 0;
}

/**
 * Answer true if the receiver type can be assigned to the argument type (right)
 * In addition to improving performance, caching also ensures there is no infinite regression
 * since per nature, the compatibility check is recursive through parameterized type arguments (122775)
 */
public boolean isCompatibleWith(TypeBinding otherType) {
	if (otherType == this)
		return true;
	if (otherType.id == TypeIds.T_JavaLangObject)
		return true;
	Object result;
	if (this.compatibleCache == null) {
		this.compatibleCache = new SimpleLookupTable(3);
		result = null;
	} else {
		result = this.compatibleCache.get(otherType); // [dbg reset] this.compatibleCache.put(otherType,null)
		if (result != null) {
			return result == Boolean.TRUE;
		}
	}
	this.compatibleCache.put(otherType, Boolean.FALSE); // protect from recursive call
	if (isCompatibleWith0(otherType)) {
		this.compatibleCache.put(otherType, Boolean.TRUE);
		return true;
	}
	return false;
}

/**
 * Answer true if the receiver type can be assigned to the argument type (right)
 */
private boolean isCompatibleWith0(TypeBinding otherType) {
	if (otherType == this)
		return true;
	if (otherType.id == TypeIds.T_JavaLangObject)
		return true;
	// equivalence may allow compatibility with array type through wildcard
	// bound
	if (isEquivalentTo(otherType))
		return true;
	switch (otherType.kind()) {
		case Binding.WILDCARD_TYPE :
		case Binding.INTERSECTION_TYPE:
			return false; // should have passed equivalence check above if
							// wildcard
		case Binding.TYPE_PARAMETER :
			// check compatibility with capture of ? super X
			if (otherType.isCapture()) {
				CaptureBinding otherCapture = (CaptureBinding) otherType;
				TypeBinding otherLowerBound;
				if ((otherLowerBound = otherCapture.lowerBound) != null) {
					if (otherLowerBound.isArrayType()) return false;
					return isCompatibleWith(otherLowerBound);
				}
			}
			//$FALL-THROUGH$
		case Binding.GENERIC_TYPE :
		case Binding.TYPE :
		case Binding.PARAMETERIZED_TYPE :
		case Binding.RAW_TYPE :
			switch (kind()) {
				case Binding.GENERIC_TYPE :
				case Binding.PARAMETERIZED_TYPE :
				case Binding.RAW_TYPE :
					if (erasure() == otherType.erasure())
						return false; // should have passed equivalence check
										// above if same erasure
			}
			ReferenceBinding otherReferenceType = (ReferenceBinding) otherType;
			if (otherReferenceType.isInterface()) // could be annotation type
				return implementsInterface(otherReferenceType, true);
			if (isInterface())  // Explicit conversion from an interface
										// to a class is not allowed
				return false;
			return otherReferenceType.isSuperclassOf(this);
		default :
			return false;
	}
}

/**
 * Answer true if the receiver has default visibility
 */
public final boolean isDefault() {
	return (this.modifiers & (ClassFileConstants.AccPublic | ClassFileConstants.AccProtected | ClassFileConstants.AccPrivate)) == 0;
}

/**
 * Answer true if the receiver is a deprecated type
 */
public final boolean isDeprecated() {
	return (this.modifiers & ClassFileConstants.AccDeprecated) != 0;
}

public boolean isEnum() {
	return (this.modifiers & ClassFileConstants.AccEnum) != 0;
}

/**
 * Answer true if the receiver is final and cannot be subclassed
 */
public final boolean isFinal() {
	return (this.modifiers & ClassFileConstants.AccFinal) != 0;
}

/**
 * Returns true if the type hierarchy is being connected
 */
public boolean isHierarchyBeingConnected() {
	return (this.tagBits & TagBits.EndHierarchyCheck) == 0 && (this.tagBits & TagBits.BeginHierarchyCheck) != 0;
}
/**
 * Returns true if the type hierarchy is being connected "actively" i.e not paused momentatrily, 
 * while resolving type arguments. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=294057
 */
public boolean isHierarchyBeingActivelyConnected() {
	return (this.tagBits & TagBits.EndHierarchyCheck) == 0 && (this.tagBits & TagBits.BeginHierarchyCheck) != 0 && (this.tagBits & TagBits.PauseHierarchyCheck) == 0;
}

/**
 * Returns true if the type hierarchy is connected
 */
public boolean isHierarchyConnected() {
	return true;
}

public boolean isInterface() {
	// consider strict interfaces and annotation types
	return (this.modifiers & ClassFileConstants.AccInterface) != 0;
}

/**
 * Answer true if the receiver has private visibility
 */
public final boolean isPrivate() {
	return (this.modifiers & ClassFileConstants.AccPrivate) != 0;
}

/**
 * Answer true if the receiver or any of its enclosing types have private visibility
 */
public final boolean isOrEnclosedByPrivateType() {
	if (isLocalType()) return true; // catch all local types
	ReferenceBinding type = this;
	while (type != null) {
		if ((type.modifiers & ClassFileConstants.AccPrivate) != 0)
			return true;
		type = type.enclosingType();
	}
	return false;
}

/**
 * Answer true if the receiver has protected visibility
 */
public final boolean isProtected() {
	return (this.modifiers & ClassFileConstants.AccProtected) != 0;
}

/**
 * Answer true if the receiver has public visibility
 */
public final boolean isPublic() {
	return (this.modifiers & ClassFileConstants.AccPublic) != 0;
}

/**
 * Answer true if the receiver is a static member type (or toplevel)
 */
public final boolean isStatic() {
	return (this.modifiers & (ClassFileConstants.AccStatic | ClassFileConstants.AccInterface)) != 0 || (this.tagBits & TagBits.IsNestedType) == 0;
}

/**
 * Answer true if all float operations must adher to IEEE 754 float/double rules
 */
public final boolean isStrictfp() {
	return (this.modifiers & ClassFileConstants.AccStrictfp) != 0;
}

/**
 * Answer true if the receiver is in the superclass hierarchy of aType
 * NOTE: Object.isSuperclassOf(Object) -> false
 */
public boolean isSuperclassOf(ReferenceBinding otherType) {
	while ((otherType = otherType.superclass()) != null) {
		if (otherType.isEquivalentTo(this)) return true;
	}
	return false;
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#isThrowable()
 */
public boolean isThrowable() {
	ReferenceBinding current = this;
	do {
		switch (current.id) {
			case TypeIds.T_JavaLangThrowable :
			case TypeIds.T_JavaLangError :
			case TypeIds.T_JavaLangRuntimeException :
			case TypeIds.T_JavaLangException :
				return true;
		}
	} while ((current = current.superclass()) != null);
	return false;
}

/**
 * JLS 11.5 ensures that Throwable, Exception, RuntimeException and Error are directly connected.
 * (Throwable<- Exception <- RumtimeException, Throwable <- Error). Thus no need to check #isCompatibleWith
 * but rather check in type IDs so as to avoid some eager class loading for JCL writers.
 * When 'includeSupertype' is true, answers true if the given type can be a supertype of some unchecked exception
 * type (i.e. Throwable or Exception).
 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#isUncheckedException(boolean)
 */
public boolean isUncheckedException(boolean includeSupertype) {
	switch (this.id) {
			case TypeIds.T_JavaLangError :
			case TypeIds.T_JavaLangRuntimeException :
				return true;
			case TypeIds.T_JavaLangThrowable :
			case TypeIds.T_JavaLangException :
				return includeSupertype;
	}
	ReferenceBinding current = this;
	while ((current = current.superclass()) != null) {
		switch (current.id) {
			case TypeIds.T_JavaLangError :
			case TypeIds.T_JavaLangRuntimeException :
				return true;
			case TypeIds.T_JavaLangThrowable :
			case TypeIds.T_JavaLangException :
				return false;
		}
	}
	return false;
}

/**
 * Answer true if the receiver has private visibility and is used locally
 */
public final boolean isUsed() {
	return (this.modifiers & ExtraCompilerModifiers.AccLocallyUsed) != 0;
}

/**
 * Answer true if the receiver is deprecated (or any of its enclosing types)
 */
public final boolean isViewedAsDeprecated() {
	return (this.modifiers & (ClassFileConstants.AccDeprecated | ExtraCompilerModifiers.AccDeprecatedImplicitly)) != 0
			|| getPackage().isViewedAsDeprecated();
}

public ReferenceBinding[] memberTypes() {
	return Binding.NO_MEMBER_TYPES;
}

public MethodBinding[] methods() {
	return Binding.NO_METHODS;
}

public final ReferenceBinding outermostEnclosingType() {
	ReferenceBinding current = this;
	while (true) {
		ReferenceBinding last = current;
		if ((current = current.enclosingType()) == null)
			return last;
	}
}

/**
 * Answer the source name for the type.
 * In the case of member types, as the qualified name from its top level type.
 * For example, for a member type N defined inside M & A: "A.M.N".
 */
public char[] qualifiedSourceName() {
	if (isMemberType())
		return CharOperation.concat(enclosingType().qualifiedSourceName(), sourceName(), '.');
	return sourceName();
}

/**
 * Answer the receiver's signature.
 *
 * NOTE: This method should only be used during/after code gen.
 */
public char[] readableName() /*java.lang.Object,  p.X<T> */ {
    char[] readableName;
	if (isMemberType()) {
		readableName = CharOperation.concat(enclosingType().readableName(), this.sourceName, '.');
	} else {
		readableName = CharOperation.concatWith(this.compoundName, '.');
	}
	TypeVariableBinding[] typeVars;
	if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
	    StringBuffer nameBuffer = new StringBuffer(10);
	    nameBuffer.append(readableName).append('<');
	    for (int i = 0, length = typeVars.length; i < length; i++) {
	        if (i > 0) nameBuffer.append(',');
	        nameBuffer.append(typeVars[i].readableName());
	    }
	    nameBuffer.append('>');
		int nameLength = nameBuffer.length();
		readableName = new char[nameLength];
		nameBuffer.getChars(0, nameLength, readableName, 0);
	}
	return readableName;
}

public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
	SimpleLookupTable store = storedAnnotations(forceInitialization);
	return store == null ? null : (AnnotationHolder) store.get(binding);
}

AnnotationBinding[] retrieveAnnotations(Binding binding) {
	AnnotationHolder holder = retrieveAnnotationHolder(binding, true);
	return holder == null ? Binding.NO_ANNOTATIONS : holder.getAnnotations();
}

public void setAnnotations(AnnotationBinding[] annotations) {
	storeAnnotations(this, annotations);
}

public char[] shortReadableName() /*Object*/ {
	char[] shortReadableName;
	if (isMemberType()) {
		shortReadableName = CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.');
	} else {
		shortReadableName = this.sourceName;
	}
	TypeVariableBinding[] typeVars;
	if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
	    StringBuffer nameBuffer = new StringBuffer(10);
	    nameBuffer.append(shortReadableName).append('<');
	    for (int i = 0, length = typeVars.length; i < length; i++) {
	        if (i > 0) nameBuffer.append(',');
	        nameBuffer.append(typeVars[i].shortReadableName());
	    }
	    nameBuffer.append('>');
		int nameLength = nameBuffer.length();
		shortReadableName = new char[nameLength];
		nameBuffer.getChars(0, nameLength, shortReadableName, 0);
	}
	return shortReadableName;
}

public char[] signature() /* Ljava/lang/Object; */ {
	if (this.signature != null)
		return this.signature;

	return this.signature = CharOperation.concat('L', constantPoolName(), ';');
}

public char[] sourceName() {
	return this.sourceName;
}

void storeAnnotationHolder(Binding binding, AnnotationHolder holder) {
	if (holder == null) {
		SimpleLookupTable store = storedAnnotations(false);
		if (store != null)
			store.removeKey(binding);
	} else {
		SimpleLookupTable store = storedAnnotations(true);
		if (store != null)
			store.put(binding, holder);
	}
}

void storeAnnotations(Binding binding, AnnotationBinding[] annotations) {
	AnnotationHolder holder = null;
	if (annotations == null || annotations.length == 0) {
		SimpleLookupTable store = storedAnnotations(false);
		if (store != null)
			holder = (AnnotationHolder) store.get(binding);
		if (holder == null) return; // nothing to delete
	} else {
		SimpleLookupTable store = storedAnnotations(true);
		if (store == null) return; // not supported
		holder = (AnnotationHolder) store.get(binding);
		if (holder == null)
			holder = new AnnotationHolder();
	}
	storeAnnotationHolder(binding, holder.setAnnotations(annotations));
}

SimpleLookupTable storedAnnotations(boolean forceInitialize) {
	return null; // overrride if interested in storing annotations for the receiver, its fields and methods
}

public ReferenceBinding superclass() {
	return null;
}

public ReferenceBinding[] superInterfaces() {
	return Binding.NO_SUPERINTERFACES;
}

public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
	if (isStatic()) return null;
	ReferenceBinding enclosingType = enclosingType();
	if (enclosingType == null)
		return null;
	return new ReferenceBinding[] {enclosingType};
}
public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
	return null;		// is null if no enclosing instances are required
}

MethodBinding[] unResolvedMethods() { // for the MethodVerifier so it doesn't resolve types
	return methods();
}

public FieldBinding[] unResolvedFields() {
	return Binding.NO_FIELDS;
}
}
