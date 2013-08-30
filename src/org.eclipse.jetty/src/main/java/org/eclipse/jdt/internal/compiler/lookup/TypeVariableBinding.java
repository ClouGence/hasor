/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 282152 - [1.5][compiler] Generics code rejected by Eclipse but accepted by javac
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

/**
 * Binding for a type parameter, held by source/binary type or method.
 */
public class TypeVariableBinding extends ReferenceBinding {

	public Binding declaringElement; // binding of declaring type or method
	public int rank; // declaration rank, can be used to match variable in parameterized type

	/**
	 * Denote the first explicit (binding) bound amongst the supertypes (from declaration in source)
	 * If no superclass was specified, then it denotes the first superinterface, or null if none was specified.
	 */
	public TypeBinding firstBound;

	// actual resolved variable supertypes (if no superclass bound, then associated to Object)
	public ReferenceBinding superclass;
	public ReferenceBinding[] superInterfaces;
	public char[] genericTypeSignature;
	LookupEnvironment environment;
	
	public TypeVariableBinding(char[] sourceName, Binding declaringElement, int rank, LookupEnvironment environment) {
		this.sourceName = sourceName;
		this.declaringElement = declaringElement;
		this.rank = rank;
		this.modifiers = ClassFileConstants.AccPublic | ExtraCompilerModifiers.AccGenericSignature; // treat type var as public
		this.tagBits |= TagBits.HasTypeVariable;
		this.environment = environment;
	}

	/**
	 * Returns true if the argument type satisfies all bounds of the type parameter
	 */
	public int boundCheck(Substitution substitution, TypeBinding argumentType) {
		if (argumentType == TypeBinding.NULL || argumentType == this) {
			return TypeConstants.OK;
		}
		boolean hasSubstitution = substitution != null;
		if (!(argumentType instanceof ReferenceBinding || argumentType.isArrayType()))
			return TypeConstants.MISMATCH;
		// special case for re-entrant source types (selection, code assist, etc)...
		// can request additional types during hierarchy walk that are found as source types that also 'need' to connect their hierarchy
		if (this.superclass == null)
			return TypeConstants.OK;

		if (argumentType.kind() == Binding.WILDCARD_TYPE) {
			WildcardBinding wildcard = (WildcardBinding) argumentType;
			switch(wildcard.boundKind) {
				case Wildcard.EXTENDS :
					TypeBinding wildcardBound = wildcard.bound;
					if (wildcardBound == this)
						return TypeConstants.OK;
					boolean isArrayBound = wildcardBound.isArrayType();
					if (!wildcardBound.isInterface()) {
						TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
						if (substitutedSuperType.id != TypeIds.T_JavaLangObject) {
							if (isArrayBound) {
								if (!wildcardBound.isCompatibleWith(substitutedSuperType))
									return TypeConstants.MISMATCH;
							} else {
								TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
								if (match != null) {
									if (substitutedSuperType.isProvablyDistinct(match)) {
										return TypeConstants.MISMATCH;
									}
								} else {
									match =  substitutedSuperType.findSuperTypeOriginatingFrom(wildcardBound);
									if (match != null) {
										if (match.isProvablyDistinct(wildcardBound)) {
											return TypeConstants.MISMATCH;
										}
									} else {
										if (!wildcardBound.isTypeVariable() && !substitutedSuperType.isTypeVariable()) {
											return TypeConstants.MISMATCH;
										}
									}
								}
							}
						}
					}
					boolean mustImplement = isArrayBound || ((ReferenceBinding)wildcardBound).isFinal();
					for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
						TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
						if (isArrayBound) {
							if (!wildcardBound.isCompatibleWith(substitutedSuperType))
									return TypeConstants.MISMATCH;
						} else {
							TypeBinding match = wildcardBound.findSuperTypeOriginatingFrom(substitutedSuperType);
							if (match != null) {
								if (substitutedSuperType.isProvablyDistinct(match)) {
									return TypeConstants.MISMATCH;
								}
							} else if (mustImplement) {
									return TypeConstants.MISMATCH; // cannot be extended further to satisfy missing bounds
							}
						}

					}
					break;

				case Wildcard.SUPER :
					// if the wildcard is lower-bounded by a type variable that has no relevant upper bound there's nothing to check here (bug 282152):
					if (wildcard.bound.isTypeVariable() && ((TypeVariableBinding)wildcard.bound).superclass.id == TypeIds.T_JavaLangObject)
						break;
					return boundCheck(substitution, wildcard.bound);

				case Wildcard.UNBOUND :
					break;
			}
			return TypeConstants.OK;
		}
		boolean unchecked = false;
		if (this.superclass.id != TypeIds.T_JavaLangObject) {
			TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superclass) : this.superclass;
	    	if (substitutedSuperType != argumentType) {
				if (!argumentType.isCompatibleWith(substitutedSuperType)) {
				    return TypeConstants.MISMATCH;
				}
				TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
				if (match != null){
					// Enum#RAW is not a substitute for <E extends Enum<E>> (86838)
					if (match.isRawType() && substitutedSuperType.isBoundParameterizedType())
						unchecked = true;
				}
	    	}
		}
	    for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
			TypeBinding substitutedSuperType = hasSubstitution ? Scope.substitute(substitution, this.superInterfaces[i]) : this.superInterfaces[i];
	    	if (substitutedSuperType != argumentType) {
				if (!argumentType.isCompatibleWith(substitutedSuperType)) {
				    return TypeConstants.MISMATCH;
				}
				TypeBinding match = argumentType.findSuperTypeOriginatingFrom(substitutedSuperType);
				if (match != null){
					// Enum#RAW is not a substitute for <E extends Enum<E>> (86838)
					if (match.isRawType() && substitutedSuperType.isBoundParameterizedType())
						unchecked = true;
				}
	    	}
	    }
	    return unchecked ? TypeConstants.UNCHECKED : TypeConstants.OK;
	}

	public int boundsCount() {
		if (this.firstBound == null) {
			return 0;
		} else if (this.firstBound == this.superclass) {
			return this.superInterfaces.length + 1;
		} else {
			return this.superInterfaces.length;
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#canBeInstantiated()
	 */
	public boolean canBeInstantiated() {
		return false;
	}
	/**
	 * Collect the substitutes into a map for certain type variables inside the receiver type
	 * e.g.   Collection<T>.collectSubstitutes(Collection<List<X>>, Map), will populate Map with: T --> List<X>
	 * Constraints:
	 *   A << F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_EXTENDS (1))
	 *   A = F   corresponds to:      F.collectSubstitutes(..., A, ..., CONSTRAINT_EQUAL (0))
	 *   A >> F   corresponds to:   F.collectSubstitutes(..., A, ..., CONSTRAINT_SUPER (2))
	 */
	public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint) {

		//	only infer for type params of the generic method
		if (this.declaringElement != inferenceContext.genericMethod) return;

		// cannot infer anything from a null type
		switch (actualType.kind()) {
			case Binding.BASE_TYPE :
				if (actualType == TypeBinding.NULL) return;
				TypeBinding boxedType = scope.environment().computeBoxingType(actualType);
				if (boxedType == actualType) return;
				actualType = boxedType;
				break;
			case Binding.WILDCARD_TYPE :
				return; // wildcards are not true type expressions (JLS 15.12.2.7, p.453 2nd discussion)
		}

		// reverse constraint, to reflect variable on rhs:   A << T --> T >: A
		int variableConstraint;
		switch(constraint) {
			case TypeConstants.CONSTRAINT_EQUAL :
				variableConstraint = TypeConstants.CONSTRAINT_EQUAL;
				break;
			case TypeConstants.CONSTRAINT_EXTENDS :
				variableConstraint = TypeConstants.CONSTRAINT_SUPER;
				break;
			default:
			//case CONSTRAINT_SUPER :
				variableConstraint =TypeConstants.CONSTRAINT_EXTENDS;
				break;
		}
		inferenceContext.recordSubstitute(this, actualType, variableConstraint);
	}

	/*
	 * declaringUniqueKey : genericTypeSignature
	 * p.X<T> { ... } --> Lp/X;:TT;
	 * p.X { <T> void foo() {...} } --> Lp/X;.foo()V:TT;
	 */
	public char[] computeUniqueKey(boolean isLeaf) {
		StringBuffer buffer = new StringBuffer();
		Binding declaring = this.declaringElement;
		if (!isLeaf && declaring.kind() == Binding.METHOD) { // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=97902
			MethodBinding methodBinding = (MethodBinding) declaring;
			ReferenceBinding declaringClass = methodBinding.declaringClass;
			buffer.append(declaringClass.computeUniqueKey(false/*not a leaf*/));
			buffer.append(':');
			MethodBinding[] methods = declaringClass.methods();
			if (methods != null)
				for (int i = 0, length = methods.length; i < length; i++) {
					MethodBinding binding = methods[i];
					if (binding == methodBinding) {
						buffer.append(i);
						break;
					}
				}
		} else {
			buffer.append(declaring.computeUniqueKey(false/*not a leaf*/));
			buffer.append(':');
		}
		buffer.append(genericTypeSignature());
		int length = buffer.length();
		char[] uniqueKey = new char[length];
		buffer.getChars(0, length, uniqueKey, 0);
		return uniqueKey;
	}
	public char[] constantPoolName() { /* java/lang/Object */
	    if (this.firstBound != null) {
			return this.firstBound.constantPoolName();
	    }
	    return this.superclass.constantPoolName(); // java/lang/Object
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#debugName()
	 */
	public String debugName() {
	    return new String(this.sourceName);
	}
	public TypeBinding erasure() {
	    if (this.firstBound != null) {
			return this.firstBound.erasure();
	    }
	    return this.superclass; // java/lang/Object
	}
	/**
	 * T::Ljava/util/Map;:Ljava/io/Serializable;
	 * T:LY<TT;>
	 */
	public char[] genericSignature() {
	    StringBuffer sig = new StringBuffer(10);
	    sig.append(this.sourceName).append(':');
	   	int interfaceLength = this.superInterfaces == null ? 0 : this.superInterfaces.length;
	    if (interfaceLength == 0 || this.firstBound == this.superclass) {
	    	if (this.superclass != null)
		        sig.append(this.superclass.genericTypeSignature());
	    }
		for (int i = 0; i < interfaceLength; i++) {
		    sig.append(':').append(this.superInterfaces[i].genericTypeSignature());
		}
		int sigLength = sig.length();
		char[] genericSignature = new char[sigLength];
		sig.getChars(0, sigLength, genericSignature, 0);
		return genericSignature;
	}
	/**
	 * T::Ljava/util/Map;:Ljava/io/Serializable;
	 * T:LY<TT;>
	 */
	public char[] genericTypeSignature() {
	    if (this.genericTypeSignature != null) return this.genericTypeSignature;
		return this.genericTypeSignature = CharOperation.concat('T', this.sourceName, ';');
	}

	boolean hasOnlyRawBounds() {
		if (this.superclass != null && this.firstBound == this.superclass)
			if (!this.superclass.isRawType())
				return false;

		if (this.superInterfaces != null)
			for (int i = 0, l = this.superInterfaces.length; i < l; i++)
		   		if (!this.superInterfaces[i].isRawType())
		   			return false;

		return true;
	}

	/**
	 * Returns true if the type variable is directly bound to a given type
	 */
	public boolean isErasureBoundTo(TypeBinding type) {
		if (this.superclass.erasure() == type)
			return true;
		for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
			if (this.superInterfaces[i].erasure() == type)
				return true;
		}
		return false;
	}

	public boolean isHierarchyConnected() {
		return (this.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0;
	}

	/**
	 * Returns true if the 2 variables are playing exact same role: they have
	 * the same bounds, providing one is substituted with the other: <T1 extends
	 * List<T1>> is interchangeable with <T2 extends List<T2>>.
	 */
	public boolean isInterchangeableWith(TypeVariableBinding otherVariable, Substitution substitute) {
		if (this == otherVariable)
			return true;
		int length = this.superInterfaces.length;
		if (length != otherVariable.superInterfaces.length)
			return false;

		if (this.superclass != Scope.substitute(substitute, otherVariable.superclass))
			return false;

		next : for (int i = 0; i < length; i++) {
			TypeBinding superType = Scope.substitute(substitute, otherVariable.superInterfaces[i]);
			for (int j = 0; j < length; j++)
				if (superType == this.superInterfaces[j])
					continue next;
			return false; // not a match
		}
		return true;
	}

	/**
	 * Returns true if the type was declared as a type variable
	 */
	public boolean isTypeVariable() {
	    return true;
	}

//	/**
//	 * Returns the original type variable for a given variable.
//	 * Only different from receiver for type variables of generic methods of parameterized types
//	 * e.g. X<U> {   <V1 extends U> U foo(V1)   } --> X<String> { <V2 extends String> String foo(V2)  }
//	 *         and V2.original() --> V1
//	 */
//	public TypeVariableBinding original() {
//		if (this.declaringElement.kind() == Binding.METHOD) {
//			MethodBinding originalMethod = ((MethodBinding)this.declaringElement).original();
//			if (originalMethod != this.declaringElement) {
//				return originalMethod.typeVariables[this.rank];
//			}
//		} else {
//			ReferenceBinding originalType = (ReferenceBinding)((ReferenceBinding)this.declaringElement).erasure();
//			if (originalType != this.declaringElement) {
//				return originalType.typeVariables()[this.rank];
//			}
//		}
//		return this;
//	}

	public int kind() {
		return Binding.TYPE_PARAMETER;
	}

	public TypeBinding[] otherUpperBounds() {
		if (this.firstBound == null)
			return Binding.NO_TYPES;
		if (this.firstBound == this.superclass)
			return this.superInterfaces;
		int otherLength = this.superInterfaces.length - 1;
		if (otherLength > 0) {
			TypeBinding[] otherBounds;
			System.arraycopy(this.superInterfaces, 1, otherBounds = new TypeBinding[otherLength], 0, otherLength);
			return otherBounds;
		}
		return Binding.NO_TYPES;
	}

	/**
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#readableName()
     */
    public char[] readableName() {
        return this.sourceName;
    }
	ReferenceBinding resolve() {
		if ((this.modifiers & ExtraCompilerModifiers.AccUnresolved) == 0)
			return this;

		TypeBinding oldSuperclass = this.superclass, oldFirstInterface = null;
		if (this.superclass != null) {
			ReferenceBinding resolveType = (ReferenceBinding) BinaryTypeBinding.resolveType(this.superclass, this.environment, true /* raw conversion */);
			this.tagBits |= resolveType.tagBits & TagBits.ContainsNestedTypeReferences;
			this.superclass = resolveType;
		}
		ReferenceBinding[] interfaces = this.superInterfaces;
		int length;
		if ((length = interfaces.length) != 0) {
			oldFirstInterface = interfaces[0];
			for (int i = length; --i >= 0;) {
				ReferenceBinding resolveType = (ReferenceBinding) BinaryTypeBinding.resolveType(interfaces[i], this.environment, true /* raw conversion */);
				this.tagBits |= resolveType.tagBits & TagBits.ContainsNestedTypeReferences;
				interfaces[i] = resolveType;
			}
		}
		// refresh the firstBound in case it changed
		if (this.firstBound != null) {
			if (this.firstBound == oldSuperclass) {
				this.firstBound = this.superclass;
			} else if (this.firstBound == oldFirstInterface) {
				this.firstBound = interfaces[0];
			}
		}
		this.modifiers &= ~ExtraCompilerModifiers.AccUnresolved;
		return this;
	}
	/**
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#shortReadableName()
     */
    public char[] shortReadableName() {
        return readableName();
    }
	public ReferenceBinding superclass() {
		return this.superclass;
	}
	
	public ReferenceBinding[] superInterfaces() {
		return this.superInterfaces;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(10);
		buffer.append('<').append(this.sourceName);//.append('[').append(this.rank).append(']');
		if (this.superclass != null && this.firstBound == this.superclass) {
		    buffer.append(" extends ").append(this.superclass.debugName()); //$NON-NLS-1$
		}
		if (this.superInterfaces != null && this.superInterfaces != Binding.NO_SUPERINTERFACES) {
		   if (this.firstBound != this.superclass) {
		        buffer.append(" extends "); //$NON-NLS-1$
	        }
		    for (int i = 0, length = this.superInterfaces.length; i < length; i++) {
		        if (i > 0 || this.firstBound == this.superclass) {
		            buffer.append(" & "); //$NON-NLS-1$
		        }
				buffer.append(this.superInterfaces[i].debugName());
			}
		}
		buffer.append('>');
		return buffer.toString();
	}

	/**
	 * Upper bound doesn't perform erasure
	 */
	public TypeBinding upperBound() {
		if (this.firstBound != null) {
			return this.firstBound;
		}
		return this.superclass; // java/lang/Object
	}
}
