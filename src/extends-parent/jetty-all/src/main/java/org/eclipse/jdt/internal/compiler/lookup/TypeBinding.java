/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *      Stephen Herrmann <stephan@cs.tu-berlin.de> -  Contribution for bug 317046
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;

/*
 * Not all fields defined by this type (& its subclasses) are initialized when it is created.
 * Some are initialized only when needed.
 *
 * Accessors have been provided for some public fields so all TypeBindings have the same API...
 * but access public fields directly whenever possible.
 * Non-public fields have accessors which should be used everywhere you expect the field to be initialized.
 *
 * null is NOT a valid value for a non-public field... it just means the field is not initialized.
 */
abstract public class TypeBinding extends Binding {

	public int id = TypeIds.NoId;
	public long tagBits = 0; // See values in the interface TagBits below


	/** Base type definitions */
	public final static BaseTypeBinding INT = new BaseTypeBinding(
			TypeIds.T_int, TypeConstants.INT, new char[] { 'I' });

	public final static BaseTypeBinding BYTE = new BaseTypeBinding(
			TypeIds.T_byte, TypeConstants.BYTE, new char[] { 'B' });

	public final static BaseTypeBinding SHORT = new BaseTypeBinding(
			TypeIds.T_short, TypeConstants.SHORT, new char[] { 'S' });

	public final static BaseTypeBinding CHAR = new BaseTypeBinding(
			TypeIds.T_char, TypeConstants.CHAR, new char[] { 'C' });

	public final static BaseTypeBinding LONG = new BaseTypeBinding(
			TypeIds.T_long, TypeConstants.LONG, new char[] { 'J' });

	public final static BaseTypeBinding FLOAT = new BaseTypeBinding(
			TypeIds.T_float, TypeConstants.FLOAT, new char[] { 'F' });

	public final static BaseTypeBinding DOUBLE = new BaseTypeBinding(
			TypeIds.T_double, TypeConstants.DOUBLE, new char[] { 'D' });

	public final static BaseTypeBinding BOOLEAN = new BaseTypeBinding(
			TypeIds.T_boolean, TypeConstants.BOOLEAN, new char[] { 'Z' });

	public final static BaseTypeBinding NULL = new BaseTypeBinding(
			TypeIds.T_null, TypeConstants.NULL, new char[] { 'N' }); //N stands for null even if it is never internally used

	public final static BaseTypeBinding VOID = new BaseTypeBinding(
			TypeIds.T_void, TypeConstants.VOID, new char[] { 'V' });

/**
 * Match a well-known type id to its binding
 */
public static final TypeBinding wellKnownType(Scope scope, int id) {
	switch (id) {
	case TypeIds.T_boolean:
		return TypeBinding.BOOLEAN;
	case TypeIds.T_byte:
		return TypeBinding.BYTE;
	case TypeIds.T_char:
		return TypeBinding.CHAR;
	case TypeIds.T_short:
		return TypeBinding.SHORT;
	case TypeIds.T_double:
		return TypeBinding.DOUBLE;
	case TypeIds.T_float:
		return TypeBinding.FLOAT;
	case TypeIds.T_int:
		return TypeBinding.INT;
	case TypeIds.T_long:
		return TypeBinding.LONG;
	case TypeIds.T_JavaLangObject:
		return scope.getJavaLangObject();
	case TypeIds.T_JavaLangString:
		return scope.getJavaLangString();
	default:
		return null;
	}
}

/* Answer true if the receiver can be instantiated
 */
public boolean canBeInstantiated() {
	return !isBaseType();
}

/**
 * Perform capture conversion on a given type (only effective on parameterized type with wildcards)
 */
public TypeBinding capture(Scope scope, int position) {
	return this;
}

/**
 * In case of problems, returns the closest match found. It may not be perfect match, but the
 * result of a best effort to improve fault-tolerance.
 */
public TypeBinding closestMatch() {
	return this; // by default no better type
}

/**
 * Iterate through the type components to collect instances of leaf missing types
 * @param missingTypes
 * @return missing types
 */
public List collectMissingTypes(List missingTypes) {
	return missingTypes;
}

/**
 * Collect the substitutes into a map for certain type variables inside the receiver type
 * e.g.   Collection<T>.findSubstitute(T, Collection<List<X>>):   T --> List<X>
 * Constraints:
 *   A << F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_EXTENDS (1))
 *   A = F   corresponds to:      F.collectSubstitutes(..., A, ..., CONSTRAINT_EQUAL (0))
 *   A >> F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_SUPER (2))
 */
public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {
	// no substitute by default
}

/**
 *  Answer the receiver's constant pool name.
 *  NOTE: This method should only be used during/after code gen.
 *  e.g. 'java/lang/Object'
 */
public abstract char[] constantPoolName();

public String debugName() {
	return new String(readableName());
}

/*
 * Answer the receiver's dimensions - 0 for non-array types
 */
public int dimensions() {
	return 0;
}

/* Answer the receiver's enclosing type... null if the receiver is a top level type.
 */
public ReferenceBinding enclosingType() {
	return null;
}

public TypeBinding erasure() {
	return this;
}

/**
 * Find supertype which originates from a given well-known type, or null if not found
 * (using id avoids triggering the load of well-known type: 73740)
 * NOTE: only works for erasures of well-known types, as random other types may share
 * same id though being distincts.
 * @see TypeIds
 */
public ReferenceBinding findSuperTypeOriginatingFrom(int wellKnownOriginalID, boolean originalIsClass) {

	if (!(this instanceof ReferenceBinding)) return null;
	ReferenceBinding reference = (ReferenceBinding) this;

    // do not allow type variables to match with erasures for free
    if (reference.id == wellKnownOriginalID || (original().id == wellKnownOriginalID)) return reference;

    ReferenceBinding currentType = reference;
    // iterate superclass to avoid recording interfaces if searched supertype is class
    if (originalIsClass) {
		while ((currentType = currentType.superclass()) != null) {
			if (currentType.id == wellKnownOriginalID)
				return currentType;
			if (currentType.original().id == wellKnownOriginalID)
				return currentType;
		}
		return null;
    }
	ReferenceBinding[] interfacesToVisit = null;
	int nextPosition = 0;
	do {
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
		if (currentType.id == wellKnownOriginalID)
			return currentType;
		if (currentType.original().id == wellKnownOriginalID)
			return currentType;
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
	return null;
}

/**
 * Find supertype which originates from a given type, or null if not found
 */
public TypeBinding findSuperTypeOriginatingFrom(TypeBinding otherType) {
	if (this == otherType) return this;
	if (otherType == null) return null;
	switch(kind()) {
		case Binding.ARRAY_TYPE :
			ArrayBinding arrayType = (ArrayBinding) this;
			int otherDim = otherType.dimensions();
			if (arrayType.dimensions != otherDim) {
				switch(otherType.id) {
					case TypeIds.T_JavaLangObject :
					case TypeIds.T_JavaIoSerializable :
					case TypeIds.T_JavaLangCloneable :
						return otherType;
				}
				if (otherDim < arrayType.dimensions && otherType.leafComponentType().id == TypeIds.T_JavaLangObject) {
					return otherType; // X[][] has Object[] as an implicit supertype
				}
				return null;
			}
			if (!(arrayType.leafComponentType instanceof ReferenceBinding)) return null;
			TypeBinding leafSuperType = arrayType.leafComponentType.findSuperTypeOriginatingFrom(otherType.leafComponentType());
			if (leafSuperType == null) return null;
			return arrayType.environment().createArrayType(leafSuperType, arrayType.dimensions);

		case Binding.TYPE_PARAMETER :
		    if (isCapture()) {
		    	CaptureBinding capture = (CaptureBinding) this;
		    	TypeBinding captureBound = capture.firstBound;
		    	if (captureBound instanceof ArrayBinding) {
		    		TypeBinding match = captureBound.findSuperTypeOriginatingFrom(otherType);
		    		if (match != null) return match;
		    	}
		    }
			//$FALL-THROUGH$
		case Binding.TYPE :
		case Binding.PARAMETERIZED_TYPE :
		case Binding.GENERIC_TYPE :
		case Binding.RAW_TYPE :
		case Binding.WILDCARD_TYPE :
		case Binding.INTERSECTION_TYPE:
		    // do not allow type variables/intersection types to match with erasures for free
			otherType = otherType.original();
		    if (this == otherType)
		    	return this;
		    if (original() == otherType)
		    	return this;
		    ReferenceBinding currentType = (ReferenceBinding)this;
		    if (!otherType.isInterface()) {
				while ((currentType = currentType.superclass()) != null) {
					if (currentType == otherType)
						return currentType;
					if (currentType.original() == otherType)
						return currentType;
				}
				return null;
		    }
			ReferenceBinding[] interfacesToVisit = null;
			int nextPosition = 0;
			do {
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
				if (currentType == otherType)
					return currentType;
				if (currentType.original() == otherType)
					return currentType;
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
	}
	return null;
}

/**
 * Returns the type to use for generic cast, or null if none required
 */
public TypeBinding genericCast(TypeBinding targetType) {
	if (this == targetType) 
		return null;
	TypeBinding targetErasure = targetType.erasure();
	// type var get replaced by upper bound
	if (erasure().findSuperTypeOriginatingFrom(targetErasure) != null) 
		return null;
	return targetErasure;
}

/**
 * Answer the receiver classfile signature.
 * Arrays & base types do not distinguish between signature() & constantPoolName().
 * NOTE: This method should only be used during/after code gen.
 */
public char[] genericTypeSignature() {
	return signature();
}

/**
 * Return the supertype which would erase as a subtype of a given declaring class.
 * If the receiver is already erasure compatible, then it will returned. If not, then will return the alternate lowest
 * upper bound compatible with declaring class.
 * NOTE: the declaringClass is already know to be compatible with the receiver
 * @param declaringClass to look for
 * @return the lowest erasure compatible type (considering alternate bounds)
 */
public TypeBinding getErasureCompatibleType(TypeBinding declaringClass) {
	switch(kind()) {
		case Binding.TYPE_PARAMETER :
			TypeVariableBinding variable = (TypeVariableBinding) this;
			if (variable.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
				return this; // no need for alternate receiver type
			}
			if (variable.superclass != null && variable.superclass.findSuperTypeOriginatingFrom(declaringClass) != null) {
				return variable.superclass.getErasureCompatibleType(declaringClass);
			}
			for (int i = 0, otherLength = variable.superInterfaces.length; i < otherLength; i++) {
				ReferenceBinding superInterface = variable.superInterfaces[i];
				if (superInterface.findSuperTypeOriginatingFrom(declaringClass) != null) {
					return superInterface.getErasureCompatibleType(declaringClass);
				}
			}
			return this; // only occur if passed null declaringClass for arraylength
		case Binding.INTERSECTION_TYPE :
			WildcardBinding intersection = (WildcardBinding) this;
			if (intersection.erasure().findSuperTypeOriginatingFrom(declaringClass) != null) {
				return this; // no need for alternate receiver type
			}
			if (intersection.superclass != null && intersection.superclass.findSuperTypeOriginatingFrom(declaringClass) != null) {
				return intersection.superclass.getErasureCompatibleType(declaringClass);
			}
			for (int i = 0, otherLength = intersection.superInterfaces.length; i < otherLength; i++) {
				ReferenceBinding superInterface = intersection.superInterfaces[i];
				if (superInterface.findSuperTypeOriginatingFrom(declaringClass) != null) {
					return superInterface.getErasureCompatibleType(declaringClass);
				}
			}
			return this; // only occur if passed null declaringClass for arraylength
		default :
			return this;
	}
}

public abstract PackageBinding getPackage();

void initializeForStaticImports() {
	// only applicable to source types
}

public boolean isAnnotationType() {
	return false;
}

public final boolean isAnonymousType() {
	return (this.tagBits & TagBits.IsAnonymousType) != 0;
}

/* Answer true if the receiver is an array
 */
public final boolean isArrayType() {
	return (this.tagBits & TagBits.IsArrayType) != 0;
}

/* Answer true if the receiver is a base type
 */
public final boolean isBaseType() {
	return (this.tagBits & TagBits.IsBaseType) != 0;
}

/**
 *  Returns true if parameterized type AND not of the form List<?>
 */
public boolean isBoundParameterizedType() {
	return (this.tagBits & TagBits.IsBoundParameterizedType) != 0;
}

/**
 * Returns true if the type is the capture of some wildcard
 */
public boolean isCapture() {
	return false;
}

public boolean isClass() {
	return false;
}

/* Answer true if the receiver type can be assigned to the argument type (right)
 */
public abstract boolean isCompatibleWith(TypeBinding right);

public boolean isEnum() {
	return false;
}

/**
 * Returns true if a type is identical to another one,
 * or for generic types, true if compared to its raw type.
 */
public boolean isEquivalentTo(TypeBinding otherType) {
	if (this == otherType)
		return true;
	if (otherType == null)
		return false;
	switch (otherType.kind()) {
		case Binding.WILDCARD_TYPE :
		case Binding.INTERSECTION_TYPE :
			return ((WildcardBinding) otherType).boundCheck(this);
	}
	return false;
}

public boolean isGenericType() {
	return false;
}

/* Answer true if the receiver's hierarchy has problems (always false for arrays & base types)
 */
public final boolean isHierarchyInconsistent() {
	return (this.tagBits & TagBits.HierarchyHasProblems) != 0;
}

public boolean isInterface() {
	return false;
}

/**
 * Returns true if the current type denotes an intersection type: Number & Comparable<?>
 */
public boolean isIntersectionType() {
	return false;
}

public final boolean isLocalType() {
	return (this.tagBits & TagBits.IsLocalType) != 0;
}

public final boolean isMemberType() {
	return (this.tagBits & TagBits.IsMemberType) != 0;
}

public final boolean isNestedType() {
	return (this.tagBits & TagBits.IsNestedType) != 0;
}

public final boolean isNumericType() {
	switch (this.id) {
	case TypeIds.T_int:
	case TypeIds.T_float:
	case TypeIds.T_double:
	case TypeIds.T_short:
	case TypeIds.T_byte:
	case TypeIds.T_long:
	case TypeIds.T_char:
		return true;
	default:
		return false;
	}
}

/**
 * Returns true if the type is parameterized, e.g. List<String>.
 * Note that some instances of ParameterizedTypeBinding have no arguments, like for non-generic members 
 * of a parameterized type. Use {@link #isParameterizedTypeWithActualArguments()} instead to find out.
 */
public final boolean isParameterizedType() {
	return kind() == Binding.PARAMETERIZED_TYPE;
}

/**
 * Returns true if the type is parameterized, e.g. List<String>
 * Note that some instances of ParameterizedTypeBinding do answer false to {@link #isParameterizedType()}
 * in case they have no arguments, like for non-generic members of a parameterized type.
 * i.e. {@link #isParameterizedType()} is not equivalent to testing <code>type.kind() == Binding.PARAMETERIZED_TYPE</code>
 */
public final boolean isParameterizedTypeWithActualArguments() {
	return (kind() == Binding.PARAMETERIZED_TYPE) 
					&& ((ParameterizedTypeBinding) this).arguments != null;
}

/**
 * Returns true if the type is parameterized using its own type variables as arguments
 */
public boolean isParameterizedWithOwnVariables() {
	if (kind() != Binding.PARAMETERIZED_TYPE)
		return false;
	ParameterizedTypeBinding paramType = (ParameterizedTypeBinding) this;
	if (paramType.arguments == null)
		return false;
	TypeVariableBinding[] variables = erasure().typeVariables();
	for (int i = 0, length = variables.length; i < length; i++) {
		if (variables[i] != paramType.arguments[i])
			return false;
	}
	ReferenceBinding enclosing = paramType.enclosingType();
	if (enclosing != null && enclosing.erasure().isGenericType()
			&& !enclosing.isParameterizedWithOwnVariables()) {
		return false;
	}
	return true;
}

private boolean isProvableDistinctSubType(TypeBinding otherType) {
	if (otherType.isInterface()) {
		if (isInterface())
			return false;
		if (isArrayType()
				|| ((this instanceof ReferenceBinding) && ((ReferenceBinding) this).isFinal())
				|| (isTypeVariable() && ((TypeVariableBinding)this).superclass().isFinal())) {
			return !isCompatibleWith(otherType);
		}
		return false;
	} else {
		if (isInterface()) {
			if (otherType.isArrayType()
					|| ((otherType instanceof ReferenceBinding) && ((ReferenceBinding) otherType).isFinal())
					|| (otherType.isTypeVariable() && ((TypeVariableBinding)otherType).superclass().isFinal())) {
				return !isCompatibleWith(otherType);
			}
		} else {
			if (!isTypeVariable() && !otherType.isTypeVariable()) {
				return !isCompatibleWith(otherType);
			}
		}
	}
	return false;
}

/**
 * Returns true if a type is provably distinct from another one,
 */
public boolean isProvablyDistinct(TypeBinding otherType) {

	/* With the hybrid 1.4/1.5+ projects modes, while establishing type equivalence, we need to
	   be prepared for a type such as Map appearing in one of three forms: As (a) a ParameterizedTypeBinding 
	   e.g Map<String, String>, (b) as RawTypeBinding Map#RAW and finally (c) as a BinaryTypeBinding 
	   When the usage of a type lacks type parameters, whether we land up with the raw form or not depends
	   on whether the underlying type was "seen to be" a generic type in the particular build environment or
	   not. See:
	    https://bugs.eclipse.org/bugs/show_bug.cgi?id=186565
        https://bugs.eclipse.org/bugs/show_bug.cgi?id=328827 
        https://bugs.eclipse.org/bugs/show_bug.cgi?id=329588
	 */ 

	if (this == otherType)
	    return false;
    if (otherType == null)
        return true;

    switch (kind()) {

		case Binding.PARAMETERIZED_TYPE :
		    ParameterizedTypeBinding paramType = (ParameterizedTypeBinding) this;
		    switch(otherType.kind()) {
		    	case Binding.PARAMETERIZED_TYPE :
		            ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding) otherType;
		            if (paramType.genericType() != otherParamType.genericType())
		                return true;
		            if (!paramType.isStatic()) { // static member types do not compare their enclosing
		            	ReferenceBinding enclosing = enclosingType();
		            	if (enclosing != null) {
		            		ReferenceBinding otherEnclosing = otherParamType.enclosingType();
		            		if (otherEnclosing == null) return true;
		            		if ((otherEnclosing.tagBits & TagBits.HasDirectWildcard) == 0) {
		            			if (enclosing.isProvablyDistinct(otherEnclosing)) return true; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=302919
		            		} else {
		            			if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) return true;
		            		}
		            	}
		            }
		            int length = paramType.arguments == null ? 0 : paramType.arguments.length;
		            TypeBinding[] otherArguments = otherParamType.arguments;
		            int otherLength = otherArguments == null ? 0 : otherArguments.length;
		            if (otherLength != length)
		                return true;
		            for (int i = 0; i < length; i++) {
		            	if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i))
		            		return true;
		            }
		            return false;

		    	case Binding.GENERIC_TYPE :
		            if (paramType.genericType() != otherType)
		                return true;
		            if (!paramType.isStatic()) { // static member types do not compare their enclosing
		            	ReferenceBinding enclosing = enclosingType();
		            	if (enclosing != null) {
		            		ReferenceBinding otherEnclosing = otherType.enclosingType();
		            		if (otherEnclosing == null) return true;
		            		if ((otherEnclosing.tagBits & TagBits.HasDirectWildcard) == 0) {
								if (enclosing != otherEnclosing) return true;
		            		} else {
		            			if (!enclosing.isEquivalentTo(otherType.enclosingType())) return true;
		            		}
		            	}
		            }
		            length = paramType.arguments == null ? 0 : paramType.arguments.length;
		            otherArguments = otherType.typeVariables();
		            otherLength = otherArguments == null ? 0 : otherArguments.length;
		            if (otherLength != length)
		                return true;
		            for (int i = 0; i < length; i++) {
		            	if (paramType.arguments[i].isProvablyDistinctTypeArgument(otherArguments[i], paramType, i))
		            		return true;
		            }
		            return false;

		    	case Binding.RAW_TYPE :
		            return erasure() != otherType.erasure();
		    	case Binding.TYPE:  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=329588
		    		return erasure() != otherType;
		    }
	        return true;

		case Binding.RAW_TYPE : // dead code ??

		    switch(otherType.kind()) {

		    	case Binding.GENERIC_TYPE :
		    	case Binding.PARAMETERIZED_TYPE :
		    	case Binding.RAW_TYPE :
		    	case Binding.TYPE:  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=329588
		            return erasure() != otherType.erasure();
		    }
	        return true;

		case Binding.TYPE: // https://bugs.eclipse.org/bugs/show_bug.cgi?id=329588
		    switch(otherType.kind()) {
		    	case Binding.PARAMETERIZED_TYPE :
		    	case Binding.RAW_TYPE :
		            return this != otherType.erasure();
		    }
		    break;

		default :
			break;
	}
    return true;
}

/**
 * Returns false if two given types could not intersect as argument types:
 * List<Throwable> & List<Runnable> --> false
 * List<? extends Throwable> & List<? extends Runnable> --> true
 * List<? extends String> & List<? extends Runnable> --> false
 */
private boolean isProvablyDistinctTypeArgument(TypeBinding otherArgument, final ParameterizedTypeBinding paramType, final int rank) {
	if (this == otherArgument)
		return false;

	TypeBinding upperBound1 = null;
	TypeBinding lowerBound1 = null;
	ReferenceBinding genericType = paramType.genericType();
	switch (kind()) {
		case Binding.WILDCARD_TYPE :
			WildcardBinding wildcard = (WildcardBinding) this;
			switch (wildcard.boundKind) {
				case Wildcard.EXTENDS:
					upperBound1 = wildcard.bound;
					break;
				case Wildcard.SUPER:
					lowerBound1 = wildcard.bound;
					break;
				case Wildcard.UNBOUND:
					return false;
			}
			break;
		case Binding.INTERSECTION_TYPE :
			break;
		case Binding.TYPE_PARAMETER :
			final TypeVariableBinding variable = (TypeVariableBinding) this;
			if (variable.isCapture()) {
				CaptureBinding capture = (CaptureBinding) variable;
				switch (capture.wildcard.boundKind) {
					case Wildcard.EXTENDS:
						upperBound1 = capture.wildcard.bound;
						break;
					case Wildcard.SUPER:
						lowerBound1 = capture.wildcard.bound;
						break;
					case Wildcard.UNBOUND:
						return false;
				}
				break;
			}
			if (variable.firstBound == null) // unbound variable
				return false;
			TypeBinding eliminatedType = Scope.convertEliminatingTypeVariables(variable, genericType, rank, null);
			switch (eliminatedType.kind()) {
				case Binding.WILDCARD_TYPE :
				case Binding.INTERSECTION_TYPE :
					wildcard = (WildcardBinding) eliminatedType;
					switch (wildcard.boundKind) {
						case Wildcard.EXTENDS:
							upperBound1 = wildcard.bound;
							break;
						case Wildcard.SUPER:
							lowerBound1 = wildcard.bound;
							break;
						case Wildcard.UNBOUND:
							return false;
					}
					break;
			}
			break;
	}
	TypeBinding upperBound2 = null;
	TypeBinding lowerBound2 = null;
	switch (otherArgument.kind()) {
		case Binding.WILDCARD_TYPE :
			WildcardBinding otherWildcard = (WildcardBinding) otherArgument;
			switch (otherWildcard.boundKind) {
				case Wildcard.EXTENDS:
					upperBound2 = otherWildcard.bound;
					break;
				case Wildcard.SUPER:
					lowerBound2 = otherWildcard.bound;
					break;
				case Wildcard.UNBOUND:
					return false;
			}
			break;
		case Binding.INTERSECTION_TYPE :
			break;
		case Binding.TYPE_PARAMETER :
			TypeVariableBinding otherVariable = (TypeVariableBinding) otherArgument;
			if (otherVariable.isCapture()) {
				CaptureBinding otherCapture = (CaptureBinding) otherVariable;
				switch (otherCapture.wildcard.boundKind) {
					case Wildcard.EXTENDS:
						upperBound2 = otherCapture.wildcard.bound;
						break;
					case Wildcard.SUPER:
						lowerBound2 = otherCapture.wildcard.bound;
						break;
					case Wildcard.UNBOUND:
						return false;
				}
				break;
			}
			if (otherVariable.firstBound == null) // unbound variable
				return false;
			TypeBinding otherEliminatedType = Scope.convertEliminatingTypeVariables(otherVariable, genericType, rank, null);
			switch (otherEliminatedType.kind()) {
				case Binding.WILDCARD_TYPE :
				case Binding.INTERSECTION_TYPE :
					otherWildcard = (WildcardBinding) otherEliminatedType;
					switch (otherWildcard.boundKind) {
						case Wildcard.EXTENDS:
							upperBound2 = otherWildcard.bound;
							break;
						case Wildcard.SUPER:
							lowerBound2 = otherWildcard.bound;
							break;
						case Wildcard.UNBOUND:
							return false;
					}
					break;
			}			break;
	}
	if (lowerBound1 != null) {
		if (lowerBound2 != null) {
			return false; // Object could always be a candidate

		} else if (upperBound2 != null) {
			if (lowerBound1.isTypeVariable() || upperBound2.isTypeVariable()) {
				return false;
			}
			return !lowerBound1.isCompatibleWith(upperBound2);
		} else {
			if (lowerBound1.isTypeVariable() || otherArgument.isTypeVariable()) {
				return false;
			}
			return !lowerBound1.isCompatibleWith(otherArgument);
		}
	} else if (upperBound1 != null) {
		if (lowerBound2 != null) {
			return !lowerBound2.isCompatibleWith(upperBound1);
		} else if (upperBound2 != null) {
			return upperBound1.isProvableDistinctSubType(upperBound2)
							&& upperBound2.isProvableDistinctSubType(upperBound1);
		} else {
			return otherArgument.isProvableDistinctSubType(upperBound1);
		}
	} else {
		if (lowerBound2 != null) {
			if (lowerBound2.isTypeVariable() || isTypeVariable()) {
				return false;
			}
			return !lowerBound2.isCompatibleWith(this);
		} else if (upperBound2 != null) {
			return isProvableDistinctSubType(upperBound2);
		} else {
			return true; // ground types should have been the same
		}
	}
}

public final boolean isRawType() {
	return kind() == Binding.RAW_TYPE;
}
/**
 * JLS(3) 4.7.
 * Note: Foo<?>.Bar is also reifiable
 */
public boolean isReifiable() {
	TypeBinding leafType = leafComponentType();
	if (!(leafType instanceof ReferenceBinding))
		return true;
	ReferenceBinding current = (ReferenceBinding) leafType;
	do {
		switch (current.kind()) {
			case Binding.TYPE_PARAMETER:
			case Binding.WILDCARD_TYPE:
			case Binding.INTERSECTION_TYPE:
			case Binding.GENERIC_TYPE:
				return false;
			case Binding.PARAMETERIZED_TYPE:
				if (current.isBoundParameterizedType())
					return false;
				break;
			case Binding.RAW_TYPE:
				return true;
		}
		if (current.isStatic()) {
			return true;
		}
		if (current.isLocalType()) {
			LocalTypeBinding localTypeBinding = (LocalTypeBinding) current.erasure();
			MethodBinding enclosingMethod = localTypeBinding.enclosingMethod;
			if (enclosingMethod != null && enclosingMethod.isStatic()) {
				return true;
			}
		}
	} while ((current = current.enclosingType()) != null);
	return true;
}

/**
 * Returns true if a given type may be thrown
 */
public boolean isThrowable() {
	return false;
}
// JLS3: 4.5.1.1
public boolean isTypeArgumentContainedBy(TypeBinding otherType) {
	if (this == otherType)
		return true;
	switch (otherType.kind()) {
		// handle captured wildcards.
		case Binding.TYPE_PARAMETER: {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=347426
			if (!isParameterizedType() || !otherType.isCapture()) {
				return false;
			}
			CaptureBinding capture = (CaptureBinding) otherType;
			WildcardBinding wildcard = capture.wildcard;
			TypeBinding upperBound = null;
			TypeBinding [] otherBounds = null;
			switch (wildcard.boundKind) {
				case Wildcard.SUPER:
					return false; // T super syntax isn't allowed, impossible capture.
				case Wildcard.UNBOUND:
					TypeVariableBinding variable = wildcard.genericType.typeVariables()[wildcard.rank];
					upperBound = variable.upperBound();
					otherBounds = variable.boundsCount() > 1 ? variable.otherUpperBounds() : null;
					break;
				case Wildcard.EXTENDS:
					upperBound = wildcard.bound;
					otherBounds = wildcard.otherBounds;
					break;
			}
			// Given class A<T extends B<?>>, A<?> cannot be the universe of all parameterizations of A
			if (upperBound.id == TypeIds.T_JavaLangObject && otherBounds == null) {
				return false; // but given class A<T>, A<?> stays an unbounded wildcard, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=348956
			}
			otherType = capture.environment.createWildcard(null, 0, upperBound, otherBounds, Wildcard.EXTENDS);
			return isTypeArgumentContainedBy(otherType);
		}
		// allow wildcard containment
		case Binding.WILDCARD_TYPE:
		case Binding.INTERSECTION_TYPE:

			TypeBinding lowerBound = this;
			TypeBinding upperBound = this;
			switch (kind()) {
				case Binding.WILDCARD_TYPE:
				case Binding.INTERSECTION_TYPE:
					WildcardBinding wildcard = (WildcardBinding) this;
					switch (wildcard.boundKind) {
						case Wildcard.EXTENDS:
							if (wildcard.otherBounds != null) // intersection type
								break;
							upperBound = wildcard.bound;
							lowerBound = null;
							break;
						case Wildcard.SUPER:
							upperBound = wildcard;
							lowerBound = wildcard.bound;
							break;
						case Wildcard.UNBOUND:
							upperBound = wildcard;
							lowerBound = null;
					}
					break;
				case Binding.TYPE_PARAMETER:
					if (isCapture()) {
						CaptureBinding capture = (CaptureBinding) this;
						if (capture.lowerBound != null)
							lowerBound = capture.lowerBound;
					}
			}
			WildcardBinding otherWildcard = (WildcardBinding) otherType;
			if (otherWildcard.otherBounds != null)
				return false; // not a true wildcard (intersection type)
			TypeBinding otherBound = otherWildcard.bound;
			switch (otherWildcard.boundKind) {
				case Wildcard.EXTENDS:
					if (otherBound == this)
						return true; // ? extends T  <=  ? extends ? extends T
					if (upperBound == null)
						return false;
					TypeBinding match = upperBound.findSuperTypeOriginatingFrom(otherBound);
					if (match != null && (match = match.leafComponentType()).isRawType()) {
						return match == otherBound.leafComponentType(); // forbide: Collection <=  ? extends Collection<?>
																												// forbide: Collection[] <=  ? extends Collection<?>[]
					}
					return upperBound.isCompatibleWith(otherBound);

				case Wildcard.SUPER:
					if (otherBound == this)
						return true; // ? super T  <=  ? super ? super T
					if (lowerBound == null)
						return false;
					match = otherBound.findSuperTypeOriginatingFrom(lowerBound);
					if (match != null && (match = match.leafComponentType()).isRawType()) {
						return match == lowerBound.leafComponentType(); // forbide: Collection <=  ? super Collection<?>
																												// forbide: Collection[] <=  ? super Collection<?>[]
					}
					return otherBound.isCompatibleWith(lowerBound);

				case Wildcard.UNBOUND:
				default:
					return true;
			}
			// allow List<?> to match List<? extends Object> (and reciprocally)
		case Binding.PARAMETERIZED_TYPE:
			if (!isParameterizedType())
				return false;
			ParameterizedTypeBinding paramType = (ParameterizedTypeBinding) this;
			ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding) otherType;
			if (paramType.actualType() != otherParamType.actualType())
				return false;
			if (!paramType.isStatic()) { // static member types do not compare their enclosing
				ReferenceBinding enclosing = enclosingType();
				if (enclosing != null) {
					ReferenceBinding otherEnclosing = otherParamType	.enclosingType();
					if (otherEnclosing == null)
						return false;
					if ((otherEnclosing.tagBits & TagBits.HasDirectWildcard) == 0) {
						if (enclosing != otherEnclosing)
							return false;
					} else {
						if (!enclosing.isEquivalentTo(otherParamType.enclosingType()))
							return false;
					}
				}
			}
			int length = paramType.arguments == null ? 0 : paramType.arguments.length;
			TypeBinding[] otherArguments = otherParamType.arguments;
			int otherLength = otherArguments == null ? 0 : otherArguments.length;
			if (otherLength != length)
				return false;
			nextArgument: for (int i = 0; i < length; i++) {
				TypeBinding argument = paramType.arguments[i];
				TypeBinding otherArgument = otherArguments[i];
				if (argument == otherArgument)
					continue nextArgument;
				int kind = argument.kind();
				if (otherArgument.kind() != kind)
					return false;
				switch (kind) {
					case Binding.PARAMETERIZED_TYPE:
						if (argument.isTypeArgumentContainedBy(otherArgument)) // recurse
							continue nextArgument;
						break;
					case Binding.WILDCARD_TYPE:
					case Binding.INTERSECTION_TYPE:
						WildcardBinding wildcard = (WildcardBinding) argument;
						otherWildcard = (WildcardBinding) otherArgument;
						switch (wildcard.boundKind) {
						case Wildcard.EXTENDS:
							// match "? extends <upperBound>" with "?"
							if (otherWildcard.boundKind == Wildcard.UNBOUND
									&& wildcard.bound == wildcard.typeVariable().upperBound())
								continue nextArgument;
							break;
						case Wildcard.SUPER:
							break;
						case Wildcard.UNBOUND:
							// match "?" with "? extends <upperBound>"
							if (otherWildcard.boundKind == Wildcard.EXTENDS
									&& otherWildcard.bound == otherWildcard.typeVariable().upperBound())
								continue nextArgument;
							break;
						}
						break;
				}
				return false;
			}
			return true;
	}
	// (? super Object) <= Object
	if (otherType.id == TypeIds.T_JavaLangObject) {
		switch (kind()) {
			case Binding.WILDCARD_TYPE:
				WildcardBinding wildcard = (WildcardBinding) this;
				if (wildcard.boundKind == Wildcard.SUPER && wildcard.bound.id == TypeIds.T_JavaLangObject) {
					return true;
				}
				break;
		}
	}
	return false;
}

/**
 * Returns true if the type was declared as a type variable
 */
public boolean isTypeVariable() {
	return false;
}

/**
 * Returns true if wildcard type of the form '?' (no bound)
 */
public boolean isUnboundWildcard() {
	return false;
}

/**
 * Returns true if the type is a subclass of java.lang.Error or java.lang.RuntimeException
 */
public boolean isUncheckedException(boolean includeSupertype) {
	return false;
}

/**
 * Returns true if the type is a wildcard
 */
public boolean isWildcard() {
	return false;
}

/* API
 * Answer the receiver's binding type from Binding.BindingID.
 */
public int kind() {
	return Binding.TYPE;
}

public TypeBinding leafComponentType() {
	return this;
}

/**
 * Meant to be invoked on compatible types, to figure if unchecked conversion is necessary
 */
public boolean needsUncheckedConversion(TypeBinding targetType) {

	if (this == targetType)
		return false;
	targetType = targetType.leafComponentType();
	if (!(targetType instanceof ReferenceBinding))
		return false;

	TypeBinding currentType = leafComponentType();
	TypeBinding match = currentType.findSuperTypeOriginatingFrom(targetType);
	if (!(match instanceof ReferenceBinding))
		return false;
	ReferenceBinding compatible = (ReferenceBinding) match;
	while (compatible.isRawType()) {
		if (targetType.isBoundParameterizedType())
			return true;
		if (compatible.isStatic())
			break;
		if ((compatible = compatible.enclosingType()) == null)
			break;
		if ((targetType = targetType.enclosingType()) == null)
			break;
	}
	return false;
}

/**
 * Returns the orignal generic type instantiated by the receiver type, or itself if not.
 * This is similar to erasure process, except it doesn't erase type variable, wildcard, intersection types etc...
 */
public TypeBinding original() {
	switch(kind()) {
		case Binding.PARAMETERIZED_TYPE :
		case Binding.RAW_TYPE :
		case Binding.ARRAY_TYPE :
			return erasure();
		default :
			return this;
	}
}

/**
 * Answer the qualified name of the receiver's package separated by periods
 * or an empty string if its the default package.
 *
 * For example, {java.util}.
 */

public char[] qualifiedPackageName() {
	PackageBinding packageBinding = getPackage();
	return packageBinding == null
			|| packageBinding.compoundName == CharOperation.NO_CHAR_CHAR ? CharOperation.NO_CHAR
			: packageBinding.readableName();
}

/**
 * Answer the source name for the type.
 * In the case of member types, as the qualified name from its top level type.
 * For example, for a member type N defined inside M & A: "A.M.N".
 */

public abstract char[] qualifiedSourceName();

/**
 * Answer the receiver classfile signature.
 * Arrays & base types do not distinguish between signature() & constantPoolName().
 * NOTE: This method should only be used during/after code gen.
 */
public char[] signature() {
	return constantPoolName();
}

public abstract char[] sourceName();

public void swapUnresolved(UnresolvedReferenceBinding unresolvedType,
		ReferenceBinding resolvedType, LookupEnvironment environment) {
	// subclasses must override if they wrap another type binding
}

public TypeVariableBinding[] typeVariables() {
	return Binding.NO_TYPE_VARIABLES;
}
}
