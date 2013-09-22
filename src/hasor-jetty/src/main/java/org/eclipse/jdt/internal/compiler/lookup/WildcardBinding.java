/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

/*
 * A wildcard acts as an argument for parameterized types, allowing to
 * abstract parameterized types, e.g. List<String> is not compatible with List<Object>,
 * but compatible with List<?>.
 */
public class WildcardBinding extends ReferenceBinding {

	public ReferenceBinding genericType;
	public int rank;
    public TypeBinding bound; // when unbound denotes the corresponding type variable (so as to retrieve its bound lazily)
    public TypeBinding[] otherBounds; // only positionned by lub computations (if so, #bound is also set) and associated to EXTENDS mode
	char[] genericSignature;
	public int boundKind;
	ReferenceBinding superclass;
	ReferenceBinding[] superInterfaces;
	TypeVariableBinding typeVariable; // corresponding variable
	LookupEnvironment environment;

	/**
	 * When unbound, the bound denotes the corresponding type variable (so as to retrieve its bound lazily)
	 */
	public WildcardBinding(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind, LookupEnvironment environment) {
		this.rank = rank;
	    this.boundKind = boundKind;
		this.modifiers = ClassFileConstants.AccPublic | ExtraCompilerModifiers.AccGenericSignature; // treat wildcard as public
		this.environment = environment;
		initialize(genericType, bound, otherBounds);

//		if (!genericType.isGenericType() && !(genericType instanceof UnresolvedReferenceBinding)) {
//			RuntimeException e = new RuntimeException("WILDCARD with NON GENERIC");
//			e.printStackTrace();
//			throw e;
//		}
		if (genericType instanceof UnresolvedReferenceBinding)
			((UnresolvedReferenceBinding) genericType).addWrapper(this, environment);
		if (bound instanceof UnresolvedReferenceBinding)
			((UnresolvedReferenceBinding) bound).addWrapper(this, environment);
		this.tagBits |=  TagBits.HasUnresolvedTypeVariables; // cleared in resolve()
	}

	public int kind() {
		return this.otherBounds == null ? Binding.WILDCARD_TYPE : Binding.INTERSECTION_TYPE;
	}

	/**
	 * Returns true if the argument type satisfies the wildcard bound(s)
	 */
	public boolean boundCheck(TypeBinding argumentType) {
	    switch (this.boundKind) {
	        case Wildcard.UNBOUND :
	            return true;
	        case Wildcard.EXTENDS :
	            if (!argumentType.isCompatibleWith(this.bound)) return false;
	            // check other bounds (lub scenario)
            	for (int i = 0, length = this.otherBounds == null ? 0 : this.otherBounds.length; i < length; i++) {
            		if (!argumentType.isCompatibleWith(this.otherBounds[i])) return false;
            	}
            	return true;
	        default: // SUPER
	        	// ? super Exception   ok for:  IOException, since it would be ok for (Exception)ioException
	            return argumentType.isCompatibleWith(this.bound);
	    }
    }
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#canBeInstantiated()
	 */
	public boolean canBeInstantiated() {
		// cannot be asked per construction
		return false;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#collectMissingTypes(java.util.List)
	 */
	public List collectMissingTypes(List missingTypes) {
		if ((this.tagBits & TagBits.HasMissingType) != 0) {
			missingTypes = this.bound.collectMissingTypes(missingTypes);
		}
		return missingTypes;
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

		if ((this.tagBits & TagBits.HasTypeVariable) == 0) return;
		if (actualType == TypeBinding.NULL) return;

		if (actualType.isCapture()) {
			CaptureBinding capture = (CaptureBinding) actualType;
			actualType = capture.wildcard;
		}

		switch (constraint) {
			case TypeConstants.CONSTRAINT_EXTENDS : // A << F
				switch (this.boundKind) {
					case Wildcard.UNBOUND: // F={?}
//						switch (actualType.kind()) {
//						case Binding.WILDCARD_TYPE :
//							WildcardBinding actualWildcard = (WildcardBinding) actualType;
//							switch(actualWildcard.kind) {
//								case Wildcard.UNBOUND: // A={?} << F={?}  --> 0
//									break;
//								case Wildcard.EXTENDS: // A={? extends V} << F={?} ---> 0
//									break;
//								case Wildcard.SUPER: // A={? super V} << F={?} ---> 0
//									break;
//							}
//							break;
//						case Binding.INTERSECTION_TYPE :// A={? extends V1&...&Vn} << F={?} ---> 0
//							break;
//						default :// A=V << F={?} ---> 0
//							break;
//						}
						break;
					case Wildcard.EXTENDS: // F={? extends U}
						switch(actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} << F={? extends U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} << F={? extends U} ---> V << U
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
										break;
									case Wildcard.SUPER: // A={? super V} << F={? extends U} ---> 0
										break;
								}
								break;
							case Binding.INTERSECTION_TYPE : // A={? extends V1&...&Vn} << F={? extends U} ---> V1 << U, ..., Vn << U
								WildcardBinding actualIntersection = (WildcardBinding) actualType;
								this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
					        	for (int i = 0, length = actualIntersection.otherBounds.length; i < length; i++) {
									this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
					        	}
								break;
							default : // A=V << F={? extends U} ---> V << U
								this.bound.collectSubstitutes(scope, actualType, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
								break;
						}
						break;
					case Wildcard.SUPER: // F={? super U}
						switch (actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} << F={? super U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} << F={? super U} ---> 0
										break;
									case Wildcard.SUPER: // A={? super V} << F={? super U} ---> 0
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	for (int i = 0, length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
											this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	}
										break;
								}
								break;
							case Binding.INTERSECTION_TYPE : // A={? extends V1&...&Vn} << F={? super U} ---> 0
								break;
							default :// A=V << F={? super U} ---> V >> U
								this.bound.collectSubstitutes(scope, actualType, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
								break;
						}
						break;
				}
				break;
			case TypeConstants.CONSTRAINT_EQUAL : // A == F
				switch (this.boundKind) {
					case Wildcard.UNBOUND: // F={?}
//						switch (actualType.kind()) {
//						case Binding.WILDCARD_TYPE :
//							WildcardBinding actualWildcard = (WildcardBinding) actualType;
//							switch(actualWildcard.kind) {
//								case Wildcard.UNBOUND: // A={?} == F={?}  --> 0
//									break;
//								case Wildcard.EXTENDS: // A={? extends V} == F={?} ---> 0
//									break;
//								case Wildcard.SUPER: // A={? super V} == F={?} ---> 0
//									break;
//							}
//							break;
//						case Binding.INTERSECTION_TYPE :// A={? extends V1&...&Vn} == F={?} ---> 0
//							break;
//						default :// A=V == F={?} ---> 0
//							break;
//						}
						break;
					case Wildcard.EXTENDS: // F={? extends U}
						switch (actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} == F={? extends U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} == F={? extends U} ---> V == U
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
							        	for (int i = 0, length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
											this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
							        	}
										break;
									case Wildcard.SUPER: // A={? super V} == F={? extends U} ---> 0
										break;
								}
								break;
							case Binding.INTERSECTION_TYPE : // A={? extends V1&...&Vn} == F={? extends U} ---> V1 == U, ..., Vn == U
								WildcardBinding actuaIntersection = (WildcardBinding) actualType;
								this.bound.collectSubstitutes(scope, actuaIntersection.bound, inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
					        	for (int i = 0, length = actuaIntersection.otherBounds == null ? 0 : actuaIntersection.otherBounds.length; i < length; i++) {
									this.bound.collectSubstitutes(scope, actuaIntersection.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
					        	}
								break;
							default : // A=V == F={? extends U} ---> 0
								break;
						}
						break;
					case Wildcard.SUPER: // F={? super U}
						switch (actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} == F={? super U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} == F={? super U} ---> 0
										break;
									case Wildcard.SUPER: // A={? super V} == F={? super U} ---> 0
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
							        	for (int i = 0, length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
											this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_EQUAL);
							        	}
							        	break;
								}
								break;
							case Binding.INTERSECTION_TYPE :  // A={? extends V1&...&Vn} == F={? super U} ---> 0
								break;
							default : // A=V == F={? super U} ---> 0
								break;
						}
						break;
				}
				break;
			case TypeConstants.CONSTRAINT_SUPER : // A >> F
				switch (this.boundKind) {
					case Wildcard.UNBOUND: // F={?}
//						switch (actualType.kind()) {
//						case Binding.WILDCARD_TYPE :
//							WildcardBinding actualWildcard = (WildcardBinding) actualType;
//							switch(actualWildcard.kind) {
//								case Wildcard.UNBOUND: // A={?} >> F={?}  --> 0
//									break;
//								case Wildcard.EXTENDS: // A={? extends V} >> F={?} ---> 0
//									break;
//								case Wildcard.SUPER: // A={? super V} >> F={?} ---> 0
//									break;
//							}
//							break;
//						case Binding.INTERSECTION_TYPE :// A={? extends V1&...&Vn} >> F={?} ---> 0
//							break;
//						default :// A=V >> F={?} ---> 0
//							break;
//						}
						break;
					case Wildcard.EXTENDS: // F={? extends U}
						switch (actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} >> F={? extends U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} >> F={? extends U} ---> V >> U
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	for (int i = 0, length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
											this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	}
										break;
									case Wildcard.SUPER: // A={? super V} >> F={? extends U} ---> 0
										break;
								}
								break;
							case Binding.INTERSECTION_TYPE : // A={? extends V1&...&Vn} >> F={? extends U} ---> V1 >> U, ..., Vn >> U
								WildcardBinding actualIntersection = (WildcardBinding) actualType;
								this.bound.collectSubstitutes(scope, actualIntersection.bound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
					        	for (int i = 0, length = actualIntersection.otherBounds == null ? 0 : actualIntersection.otherBounds.length; i < length; i++) {
									this.bound.collectSubstitutes(scope, actualIntersection.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_SUPER);
					        	}
								break;
							default : // A=V == F={? extends U} ---> 0
								break;
						}
						break;
					case Wildcard.SUPER: // F={? super U}
						switch (actualType.kind()) {
							case Binding.WILDCARD_TYPE :
								WildcardBinding actualWildcard = (WildcardBinding) actualType;
								switch(actualWildcard.boundKind) {
									case Wildcard.UNBOUND: // A={?} >> F={? super U}  --> 0
										break;
									case Wildcard.EXTENDS: // A={? extends V} >> F={? super U} ---> 0
										break;
									case Wildcard.SUPER: // A={? super V} >> F={? super U} ---> V >> U
										this.bound.collectSubstitutes(scope, actualWildcard.bound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	for (int i = 0, length = actualWildcard.otherBounds == null ? 0 : actualWildcard.otherBounds.length; i < length; i++) {
											this.bound.collectSubstitutes(scope, actualWildcard.otherBounds[i], inferenceContext, TypeConstants.CONSTRAINT_SUPER);
							        	}
							        	break;
								}
								break;
							case Binding.INTERSECTION_TYPE :  // A={? extends V1&...&Vn} >> F={? super U} ---> 0
								break;
							default : // A=V >> F={? super U} ---> 0
								break;
						}
						break;
				}
				break;
		}
	}

	/*
	 * genericTypeKey {rank}*|+|- [boundKey]
	 * p.X<T> { X<?> ... } --> Lp/X<TT;>;{0}*
	 */
	public char[] computeUniqueKey(boolean isLeaf) {
		char[] genericTypeKey = this.genericType.computeUniqueKey(false/*not a leaf*/);
		char[] wildCardKey;
		// We now encode the rank also in the binding key - https://bugs.eclipse.org/bugs/show_bug.cgi?id=234609
		char[] rankComponent = ('{' + String.valueOf(this.rank) + '}').toCharArray();
        switch (this.boundKind) {
            case Wildcard.UNBOUND :
                wildCardKey = TypeConstants.WILDCARD_STAR;
                break;
            case Wildcard.EXTENDS :
                wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.computeUniqueKey(false/*not a leaf*/));
                break;
			default: // SUPER
			    wildCardKey = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.computeUniqueKey(false/*not a leaf*/));
				break;
        }
		return CharOperation.concat(genericTypeKey, rankComponent, wildCardKey);
    }



	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#constantPoolName()
	 */
	public char[] constantPoolName() {
		return erasure().constantPoolName();
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#debugName()
	 */
	public String debugName() {
	    return toString();
	}

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#erasure()
     */
    public TypeBinding erasure() {
    	if (this.otherBounds == null) {
	    	if (this.boundKind == Wildcard.EXTENDS)
		        return this.bound.erasure();
			TypeVariableBinding var = typeVariable();
			if (var != null)
				return var.erasure();
		    return this.genericType; // if typeVariable() == null, then its inconsistent & return this.genericType to avoid NPE case
    	}
    	// intersection type
    	return this.bound.id == TypeIds.T_JavaLangObject
    		? this.otherBounds[0].erasure()  // use first explicit bound to improve stackmap
    		: this.bound.erasure();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#signature()
     */
    public char[] genericTypeSignature() {
        if (this.genericSignature == null) {
            switch (this.boundKind) {
                case Wildcard.UNBOUND :
                    this.genericSignature = TypeConstants.WILDCARD_STAR;
                    break;
                case Wildcard.EXTENDS :
                    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_PLUS, this.bound.genericTypeSignature());
					break;
				default: // SUPER
				    this.genericSignature = CharOperation.concat(TypeConstants.WILDCARD_MINUS, this.bound.genericTypeSignature());
            }
        }
        return this.genericSignature;
    }

	public int hashCode() {
		return this.genericType.hashCode();
	}

	void initialize(ReferenceBinding someGenericType, TypeBinding someBound, TypeBinding[] someOtherBounds) {
		this.genericType = someGenericType;
		this.bound = someBound;
		this.otherBounds = someOtherBounds;
		if (someGenericType != null) {
			this.fPackage = someGenericType.getPackage();
		}
		if (someBound != null) {
			this.tagBits |= someBound.tagBits & (TagBits.HasTypeVariable | TagBits.HasMissingType | TagBits.ContainsNestedTypeReferences);
		}
		if (someOtherBounds != null) {
			for (int i = 0, max = someOtherBounds.length; i < max; i++) {
				TypeBinding someOtherBound = someOtherBounds[i];
				this.tagBits |= someOtherBound.tagBits & TagBits.ContainsNestedTypeReferences;
			}
		}
	}

	/**
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#isSuperclassOf(org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding)
     */
    public boolean isSuperclassOf(ReferenceBinding otherType) {
        if (this.boundKind == Wildcard.SUPER) {
            if (this.bound instanceof ReferenceBinding) {
                return ((ReferenceBinding) this.bound).isSuperclassOf(otherType);
            } else { // array bound
                return otherType.id == TypeIds.T_JavaLangObject;
            }
        }
        return false;
    }

    /**
     * Returns true if the current type denotes an intersection type: Number & Comparable<?>
     */
    public boolean isIntersectionType() {
    	return this.otherBounds != null;
    }

	public boolean isHierarchyConnected() {
		return this.superclass != null && this.superInterfaces != null;
	}

    /**
	 * Returns true if the type is a wildcard
	 */
	public boolean isUnboundWildcard() {
	    return this.boundKind == Wildcard.UNBOUND;
	}

    /**
	 * Returns true if the type is a wildcard
	 */
	public boolean isWildcard() {
	    return true;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.Binding#readableName()
     */
    public char[] readableName() {
        switch (this.boundKind) {
            case Wildcard.UNBOUND :
                return TypeConstants.WILDCARD_NAME;
            case Wildcard.EXTENDS :
            	if (this.otherBounds == null)
	                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.readableName());
            	StringBuffer buffer = new StringBuffer(10);
            	buffer.append(this.bound.readableName());
            	for (int i = 0, length = this.otherBounds.length; i < length; i++) {
            		buffer.append('&').append(this.otherBounds[i].readableName());
            	}
            	int length;
				char[] result = new char[length = buffer.length()];
				buffer.getChars(0, length, result, 0);
				return result;
			default: // SUPER
			    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.readableName());
        }
    }

	ReferenceBinding resolve() {
		if ((this.tagBits & TagBits.HasUnresolvedTypeVariables) == 0)
			return this;

		this.tagBits &= ~TagBits.HasUnresolvedTypeVariables;
		BinaryTypeBinding.resolveType(this.genericType, this.environment, false /* no raw conversion */);
		switch(this.boundKind) {
			case Wildcard.EXTENDS :
				TypeBinding resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true /* raw conversion */);
				this.bound = resolveType;
				this.tagBits |= resolveType.tagBits & TagBits.ContainsNestedTypeReferences;
				for (int i = 0, length = this.otherBounds == null ? 0 : this.otherBounds.length; i < length; i++) {
					resolveType = BinaryTypeBinding.resolveType(this.otherBounds[i], this.environment, true /* raw conversion */);
					this.otherBounds[i]= resolveType;
					this.tagBits |= resolveType.tagBits & TagBits.ContainsNestedTypeReferences;
				}
				break;
			case Wildcard.SUPER :
				resolveType = BinaryTypeBinding.resolveType(this.bound, this.environment, true /* raw conversion */);
				this.bound = resolveType;
				this.tagBits |= resolveType.tagBits & TagBits.ContainsNestedTypeReferences;
				break;
			case Wildcard.UNBOUND :
		}
		return this;
	}

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.Binding#shortReadableName()
     */
    public char[] shortReadableName() {
        switch (this.boundKind) {
            case Wildcard.UNBOUND :
                return TypeConstants.WILDCARD_NAME;
            case Wildcard.EXTENDS :
            	if (this.otherBounds == null)
	                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.shortReadableName());
            	StringBuffer buffer = new StringBuffer(10);
            	buffer.append(this.bound.shortReadableName());
            	for (int i = 0, length = this.otherBounds.length; i < length; i++) {
            		buffer.append('&').append(this.otherBounds[i].shortReadableName());
            	}
            	int length;
				char[] result = new char[length = buffer.length()];
				buffer.getChars(0, length, result, 0);
				return result;
			default: // SUPER
			    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.shortReadableName());
        }
    }

    /**
     * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#signature()
     */
    public char[] signature() {
     	// should not be called directly on a wildcard; signature should only be asked on
    	// original methods or type erasures (which cannot denote wildcards at first level)
		if (this.signature == null) {
	        switch (this.boundKind) {
	            case Wildcard.EXTENDS :
	                return this.bound.signature();
				default: // SUPER | UNBOUND
				    return typeVariable().signature();
	        }
		}
		return this.signature;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#sourceName()
     */
    public char[] sourceName() {
        switch (this.boundKind) {
            case Wildcard.UNBOUND :
                return TypeConstants.WILDCARD_NAME;
            case Wildcard.EXTENDS :
                return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.sourceName());
			default: // SUPER
			    return CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.sourceName());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding#superclass()
     */
    public ReferenceBinding superclass() {
		if (this.superclass == null) {
			TypeBinding superType = null;
			if (this.boundKind == Wildcard.EXTENDS && !this.bound.isInterface()) {
				superType = this.bound;
			} else {
				TypeVariableBinding variable = typeVariable();
				if (variable != null) superType = variable.firstBound;
			}
			this.superclass = superType instanceof ReferenceBinding && !superType.isInterface()
				? (ReferenceBinding) superType
				: this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
		}

		return this.superclass;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding#superInterfaces()
     */
    public ReferenceBinding[] superInterfaces() {
        if (this.superInterfaces == null) {
        	if (typeVariable() != null) {
        		this.superInterfaces = this.typeVariable.superInterfaces();
        	} else {
        		this.superInterfaces = Binding.NO_SUPERINTERFACES;
        	}
			if (this.boundKind == Wildcard.EXTENDS) {
				if (this.bound.isInterface()) {
					// augment super interfaces with the wildcard bound
					int length = this.superInterfaces.length;
					System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length+1], 1, length);
					this.superInterfaces[0] = (ReferenceBinding) this.bound; // make bound first
				}
				if (this.otherBounds != null) {
					// augment super interfaces with the wildcard otherBounds (interfaces per construction)
					int length = this.superInterfaces.length;
					int otherLength = this.otherBounds.length;
					System.arraycopy(this.superInterfaces, 0, this.superInterfaces = new ReferenceBinding[length+otherLength], 0, length);
					for (int i = 0; i < otherLength; i++) {
						this.superInterfaces[length+i] = (ReferenceBinding) this.otherBounds[i];
					}
				}
			}
        }
        return this.superInterfaces;
    }

	public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
		boolean affected = false;
		if (this.genericType == unresolvedType) {
			this.genericType = resolvedType; // no raw conversion
			affected = true;
		}
		if (this.bound == unresolvedType) {
			this.bound = env.convertUnresolvedBinaryToRawType(resolvedType);
			affected = true;
		}
		if (this.otherBounds != null) {
			for (int i = 0, length = this.otherBounds.length; i < length; i++) {
				if (this.otherBounds[i] == unresolvedType) {
					this.otherBounds[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
					affected = true;
				}
			}
		}
		if (affected)
			initialize(this.genericType, this.bound, this.otherBounds);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
        switch (this.boundKind) {
            case Wildcard.UNBOUND :
                return new String(TypeConstants.WILDCARD_NAME);
            case Wildcard.EXTENDS :
            	if (this.otherBounds == null)
                	return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_EXTENDS, this.bound.debugName().toCharArray()));
            	StringBuffer buffer = new StringBuffer(this.bound.debugName());
            	for (int i = 0, length = this.otherBounds.length; i < length; i++) {
            		buffer.append('&').append(this.otherBounds[i].debugName());
            	}
            	return buffer.toString();
			default: // SUPER
			    return new String(CharOperation.concat(TypeConstants.WILDCARD_NAME, TypeConstants.WILDCARD_SUPER, this.bound.debugName().toCharArray()));
        }
	}
	/**
	 * Returns associated type variable, or null in case of inconsistency
	 */
	public TypeVariableBinding typeVariable() {
		if (this.typeVariable == null) {
			TypeVariableBinding[] typeVariables = this.genericType.typeVariables();
			if (this.rank < typeVariables.length)
				this.typeVariable = typeVariables[this.rank];
		}
		return this.typeVariable;
	}
}
