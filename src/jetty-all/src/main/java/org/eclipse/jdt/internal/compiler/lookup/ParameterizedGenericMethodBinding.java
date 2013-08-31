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

import org.eclipse.jdt.internal.compiler.ast.Wildcard;

/**
 * Binding denoting a generic method after type parameter substitutions got performed.
 * On parameterized type bindings, all methods got substituted, regardless whether
 * their signature did involve generics or not, so as to get the proper declaringClass for
 * these methods.
 */
public class ParameterizedGenericMethodBinding extends ParameterizedMethodBinding implements Substitution {

    public TypeBinding[] typeArguments;
    private LookupEnvironment environment;
    public boolean inferredReturnType;
    public boolean wasInferred; // only set to true for instances resulting from method invocation inferrence
    public boolean isRaw; // set to true for method behaving as raw for substitution purpose
    private MethodBinding tiebreakMethod;

	/**
	 * Perform inference of generic method type parameters and/or expected type
	 */
	public static MethodBinding computeCompatibleMethod(MethodBinding originalMethod, TypeBinding[] arguments, Scope scope, InvocationSite invocationSite) {
		ParameterizedGenericMethodBinding methodSubstitute;
		TypeVariableBinding[] typeVariables = originalMethod.typeVariables;
		TypeBinding[] substitutes = invocationSite.genericTypeArguments();
		InferenceContext inferenceContext = null;
		TypeBinding[] uncheckedArguments = null;
		computeSubstitutes: {
			if (substitutes != null) {
				// explicit type arguments got supplied
				if (substitutes.length != typeVariables.length) {
			        // incompatible due to wrong arity
			        return new ProblemMethodBinding(originalMethod, originalMethod.selector, substitutes, ProblemReasons.TypeParameterArityMismatch);
				}
				methodSubstitute = scope.environment().createParameterizedGenericMethod(originalMethod, substitutes);
				break computeSubstitutes;
			}
			// perform type argument inference (15.12.2.7)
			// initializes the map of substitutes (var --> type[][]{ equal, extends, super}
			TypeBinding[] parameters = originalMethod.parameters;
			inferenceContext = new InferenceContext(originalMethod);
			methodSubstitute = inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
			if (methodSubstitute == null)
				return null;
			
			// substitutes may hold null to denote unresolved vars, but null arguments got replaced with respective original variable in param method
			// 15.12.2.8 - inferring unresolved type arguments
			if (inferenceContext.hasUnresolvedTypeArgument()) {
				if (inferenceContext.isUnchecked) { // only remember unchecked status post 15.12.2.7
					int length = inferenceContext.substitutes.length;
					System.arraycopy(inferenceContext.substitutes, 0, uncheckedArguments = new TypeBinding[length], 0, length);
				}
				if (methodSubstitute.returnType != TypeBinding.VOID) {
					TypeBinding expectedType = invocationSite.expectedType();
					if (expectedType != null) {
						// record it was explicit from context, as opposed to assumed by default (see below)
						inferenceContext.hasExplicitExpectedType = true;
					} else {
						expectedType = scope.getJavaLangObject(); // assume Object by default
					}
					inferenceContext.expectedType = expectedType;
				}
				methodSubstitute = methodSubstitute.inferFromExpectedType(scope, inferenceContext);
				if (methodSubstitute == null)
					return null;
			}
		}

		/* bounds check: https://bugs.eclipse.org/bugs/show_bug.cgi?id=242159, Inferred types may contain self reference
		   in formal bounds. If "T extends I<T>" is a original type variable and T was inferred to be I<T> due possibly
		   to under constraints and resultant glb application per 15.12.2.8, using this.typeArguments to drive the bounds
		   check against itself is doomed to fail. For, the variable T would after substitution be I<I<T>> and would fail
		   bounds check against I<T>. Use the inferred types from the context directly - see that there is one round of
		   extra substitution that has taken place to properly substitute a remaining unresolved variable which also appears
		   in a formal bound  (So we really have a bounds mismatch between I<I<T>> and I<I<I<T>>>, in the absence of a fix.)
		*/
		Substitution substitution = null;
		if (inferenceContext != null) {
			substitution = new LingeringTypeVariableEliminator(typeVariables, inferenceContext.substitutes, scope);
		} else {
			substitution = methodSubstitute;
		}
		for (int i = 0, length = typeVariables.length; i < length; i++) {
		    TypeVariableBinding typeVariable = typeVariables[i];
		    TypeBinding substitute = methodSubstitute.typeArguments[i]; // retain for diagnostics
		    TypeBinding substituteForChecks = Scope.substitute(new LingeringTypeVariableEliminator(typeVariables, null, scope), substitute); // while using this for bounds check
		    if (uncheckedArguments != null && uncheckedArguments[i] == null) continue; // only bound check if inferred through 15.12.2.6
			switch (typeVariable.boundCheck(substitution, substituteForChecks)) {
				case TypeConstants.MISMATCH :
			        // incompatible due to bound check
					int argLength = arguments.length;
					TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2]; // append offending substitute and typeVariable
					System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
					augmentedArguments[argLength] = substitute;
					augmentedArguments[argLength+1] = typeVariable;
			        return new ProblemMethodBinding(methodSubstitute, originalMethod.selector, augmentedArguments, ProblemReasons.ParameterBoundMismatch);
				case TypeConstants.UNCHECKED :
					// tolerate unchecked bounds
					methodSubstitute.tagBits |= TagBits.HasUncheckedTypeArgumentForBoundCheck;
					break;
			}
		}
		// check presence of unchecked argument conversion a posteriori (15.12.2.6)
		return methodSubstitute;
	}

	/**
	 * Collect argument type mapping, handling varargs
	 */
	private static ParameterizedGenericMethodBinding inferFromArgumentTypes(Scope scope, MethodBinding originalMethod, TypeBinding[] arguments, TypeBinding[] parameters, InferenceContext inferenceContext) {
		if (originalMethod.isVarargs()) {
			int paramLength = parameters.length;
			int minArgLength = paramLength - 1;
			int argLength = arguments.length;
			// process mandatory arguments
			for (int i = 0; i < minArgLength; i++) {
				parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
				if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
			}
			// process optional arguments
			if (minArgLength < argLength) {
				TypeBinding varargType = parameters[minArgLength]; // last arg type - as is ?
				TypeBinding lastArgument = arguments[minArgLength];
				checkVarargDimension: {
					if (paramLength == argLength) {
						if (lastArgument == TypeBinding.NULL) break checkVarargDimension;
						switch (lastArgument.dimensions()) {
							case 0 :
								break; // will remove one dim
							case 1 :
								if (!lastArgument.leafComponentType().isBaseType()) break checkVarargDimension;
								break; // will remove one dim
							default :
								break checkVarargDimension;
						}
					}
					// eliminate one array dimension
					varargType = ((ArrayBinding)varargType).elementsType();
				}
				for (int i = minArgLength; i < argLength; i++) {
					varargType.collectSubstitutes(scope, arguments[i], inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
					if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
				}
			}
		} else {
			int paramLength = parameters.length;
			for (int i = 0; i < paramLength; i++) {
				parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
				if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
			}
		}
		TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
		if (!resolveSubstituteConstraints(scope, originalVariables , inferenceContext, false/*ignore Ti<:Uk*/))
			return null; // impossible substitution

		// apply inferred variable substitutions - replacing unresolved variable with original ones in param method
		TypeBinding[] inferredSustitutes = inferenceContext.substitutes;
		TypeBinding[] actualSubstitutes = inferredSustitutes;
		for (int i = 0, varLength = originalVariables.length; i < varLength; i++) {
			if (inferredSustitutes[i] == null) {
				if (actualSubstitutes == inferredSustitutes) {
					System.arraycopy(inferredSustitutes, 0, actualSubstitutes = new TypeBinding[varLength], 0, i); // clone to replace null with original variable in param method
				}
				actualSubstitutes[i] = originalVariables[i];
			} else if (actualSubstitutes != inferredSustitutes) {
				actualSubstitutes[i] = inferredSustitutes[i];
			}
		}
		ParameterizedGenericMethodBinding paramMethod = scope.environment().createParameterizedGenericMethod(originalMethod, actualSubstitutes);
		return paramMethod;
	}

	private static boolean resolveSubstituteConstraints(Scope scope, TypeVariableBinding[] typeVariables, InferenceContext inferenceContext, boolean considerEXTENDSConstraints) {
		TypeBinding[] substitutes = inferenceContext.substitutes;
		int varLength = typeVariables.length;
		// check Tj=U constraints
		nextTypeParameter:
			for (int i = 0; i < varLength; i++) {
				TypeVariableBinding current = typeVariables[i];
				TypeBinding substitute = substitutes[i];
				if (substitute != null) continue nextTypeParameter; // already inferred previously
				TypeBinding [] equalSubstitutes = inferenceContext.getSubstitutes(current, TypeConstants.CONSTRAINT_EQUAL);
				if (equalSubstitutes != null) {
					nextConstraint:
						for (int j = 0, equalLength = equalSubstitutes.length; j < equalLength; j++) {
							TypeBinding equalSubstitute = equalSubstitutes[j];
							if (equalSubstitute == null) continue nextConstraint;
							if (equalSubstitute == current) {
								// try to find a better different match if any in subsequent equal candidates
								for (int k = j+1; k < equalLength; k++) {
									equalSubstitute = equalSubstitutes[k];
									if (equalSubstitute != current && equalSubstitute != null) {
										substitutes[i] = equalSubstitute;
										continue nextTypeParameter;
									}
								}
								substitutes[i] = current;
								continue nextTypeParameter;
							}
//							if (equalSubstitute.isTypeVariable()) {
//								TypeVariableBinding variable = (TypeVariableBinding) equalSubstitute;
//								// substituted by a variable of the same method, ignore
//								if (variable.rank < varLength && typeVariables[variable.rank] == variable) {
//									// TODO (philippe) rewrite all other constraints to use current instead.
//									continue nextConstraint;
//								}
//							}
							substitutes[i] = equalSubstitute;
							continue nextTypeParameter; // pick first match, applicability check will rule out invalid scenario where others were present
						}
				}
			}
		if (inferenceContext.hasUnresolvedTypeArgument()) {
			// check Tj>:U constraints
			nextTypeParameter:
				for (int i = 0; i < varLength; i++) {
					TypeVariableBinding current = typeVariables[i];
					TypeBinding substitute = substitutes[i];
					if (substitute != null) continue nextTypeParameter; // already inferred previously
					TypeBinding [] bounds = inferenceContext.getSubstitutes(current, TypeConstants.CONSTRAINT_SUPER);
					if (bounds == null) continue nextTypeParameter;
					TypeBinding mostSpecificSubstitute = scope.lowerUpperBound(bounds);
					if (mostSpecificSubstitute == null) {
						return false; // incompatible
					}
					if (mostSpecificSubstitute != TypeBinding.VOID) {
						substitutes[i] = mostSpecificSubstitute;
					}
				}
		}
		if (considerEXTENDSConstraints && inferenceContext.hasUnresolvedTypeArgument()) {
			// check Tj<:U constraints
			nextTypeParameter:
				for (int i = 0; i < varLength; i++) {
					TypeVariableBinding current = typeVariables[i];
					TypeBinding substitute = substitutes[i];
					if (substitute != null) continue nextTypeParameter; // already inferred previously
					TypeBinding [] bounds = inferenceContext.getSubstitutes(current, TypeConstants.CONSTRAINT_EXTENDS);
					if (bounds == null) continue nextTypeParameter;
					TypeBinding[] glb = Scope.greaterLowerBound(bounds);
					TypeBinding mostSpecificSubstitute = null;
					// https://bugs.eclipse.org/bugs/show_bug.cgi?id=341795 - Per 15.12.2.8, we should fully apply glb
					if (glb != null) {
						if (glb.length == 1) {
							mostSpecificSubstitute = glb[0];
						} else {
							TypeBinding [] otherBounds = new TypeBinding[glb.length - 1];
							System.arraycopy(glb, 1, otherBounds, 0, glb.length - 1);
							mostSpecificSubstitute = scope.environment().createWildcard(null, 0, glb[0], otherBounds, Wildcard.EXTENDS);
						}
					}
					if (mostSpecificSubstitute != null) {
						substitutes[i] = mostSpecificSubstitute;
					}
				}
		}
		return true;
	}

	/**
	 * Create raw generic method for raw type (double substitution from type vars with raw type arguments, and erasure of method variables)
	 * Only invoked for non-static generic methods of raw type
	 */
	public ParameterizedGenericMethodBinding(MethodBinding originalMethod, RawTypeBinding rawType, LookupEnvironment environment) {
		TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
		int length = originalVariables.length;
		TypeBinding[] rawArguments = new TypeBinding[length];
		for (int i = 0; i < length; i++) {
			rawArguments[i] =  environment.convertToRawType(originalVariables[i].erasure(), false /*do not force conversion of enclosing types*/);
		}
	    this.isRaw = true;
	    this.tagBits = originalMethod.tagBits;
	    this.environment = environment;
		this.modifiers = originalMethod.modifiers;
		this.selector = originalMethod.selector;
		this.declaringClass = rawType == null ? originalMethod.declaringClass : rawType;
	    this.typeVariables = Binding.NO_TYPE_VARIABLES;
	    this.typeArguments = rawArguments;
	    this.originalMethod = originalMethod;
		boolean ignoreRawTypeSubstitution = rawType == null || originalMethod.isStatic();
	    this.parameters = Scope.substitute(this, ignoreRawTypeSubstitution
	    									? originalMethod.parameters // no substitution if original was static
	    									: Scope.substitute(rawType, originalMethod.parameters));
	    this.thrownExceptions = Scope.substitute(this, 	ignoreRawTypeSubstitution
	    									? originalMethod.thrownExceptions // no substitution if original was static
	    									: Scope.substitute(rawType, originalMethod.thrownExceptions));
	    // error case where exception type variable would have been substituted by a non-reference type (207573)
	    if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
	    this.returnType = Scope.substitute(this, ignoreRawTypeSubstitution
	    									? originalMethod.returnType // no substitution if original was static
	    									: Scope.substitute(rawType, originalMethod.returnType));
	    this.wasInferred = false; // not resulting from method invocation inferrence
	}

    /**
     * Create method of parameterized type, substituting original parameters with type arguments.
     */
	public ParameterizedGenericMethodBinding(MethodBinding originalMethod, TypeBinding[] typeArguments, LookupEnvironment environment) {
	    this.environment = environment;
		this.modifiers = originalMethod.modifiers;
		this.selector = originalMethod.selector;
		this.declaringClass = originalMethod.declaringClass;
	    this.typeVariables = Binding.NO_TYPE_VARIABLES;
	    this.typeArguments = typeArguments;
	    this.isRaw = false;
	    this.tagBits = originalMethod.tagBits;
	    this.originalMethod = originalMethod;
	    this.parameters = Scope.substitute(this, originalMethod.parameters);
	    // error case where exception type variable would have been substituted by a non-reference type (207573)
	    this.returnType = Scope.substitute(this, originalMethod.returnType);
	    this.thrownExceptions = Scope.substitute(this, originalMethod.thrownExceptions);
	    if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
		checkMissingType: {
			if ((this.tagBits & TagBits.HasMissingType) != 0)
				break checkMissingType;
			if ((this.returnType.tagBits & TagBits.HasMissingType) != 0) {
				this.tagBits |=  TagBits.HasMissingType;
				break checkMissingType;
			}
			for (int i = 0, max = this.parameters.length; i < max; i++) {
				if ((this.parameters[i].tagBits & TagBits.HasMissingType) != 0) {
					this.tagBits |=  TagBits.HasMissingType;
					break checkMissingType;
				}
			}
			for (int i = 0, max = this.thrownExceptions.length; i < max; i++) {
				if ((this.thrownExceptions[i].tagBits & TagBits.HasMissingType) != 0) {
					this.tagBits |=  TagBits.HasMissingType;
					break checkMissingType;
				}
			}
		}
	    this.wasInferred = true;// resulting from method invocation inferrence
	}

	/*
	 * parameterizedDeclaringUniqueKey dot selector originalMethodGenericSignature percent typeArguments
	 * p.X<U> { <T> void bar(T t, U u) { new X<String>().bar(this, "") } } --> Lp/X<Ljava/lang/String;>;.bar<T:Ljava/lang/Object;>(TT;Ljava/lang/String;)V%<Lp/X;>
	 */
	public char[] computeUniqueKey(boolean isLeaf) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.originalMethod.computeUniqueKey(false/*not a leaf*/));
		buffer.append('%');
		buffer.append('<');
		if (!this.isRaw) {
			int length = this.typeArguments.length;
			for (int i = 0; i < length; i++) {
				TypeBinding typeArgument = this.typeArguments[i];
				buffer.append(typeArgument.computeUniqueKey(false/*not a leaf*/));
			}
		}
		buffer.append('>');
		int resultLength = buffer.length();
		char[] result = new char[resultLength];
		buffer.getChars(0, resultLength, result, 0);
		return result;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#environment()
	 */
	public LookupEnvironment environment() {
		return this.environment;
	}
	/**
	 * Returns true if some parameters got substituted.
	 * NOTE: generic method invocation delegates to its declaring method (could be a parameterized one)
	 */
	public boolean hasSubstitutedParameters() {
		// generic parameterized method can represent either an invocation or a raw generic method
		if (this.wasInferred)
			return this.originalMethod.hasSubstitutedParameters();
		return super.hasSubstitutedParameters();
	}
	/**
	 * Returns true if the return type got substituted.
	 * NOTE: generic method invocation delegates to its declaring method (could be a parameterized one)
	 */
	public boolean hasSubstitutedReturnType() {
		if (this.inferredReturnType)
			return this.originalMethod.hasSubstitutedReturnType();
		return super.hasSubstitutedReturnType();
	}
	/**
	 * Given some type expectation, and type variable bounds, perform some inference.
	 * Returns true if still had unresolved type variable at the end of the operation
	 */
	private ParameterizedGenericMethodBinding inferFromExpectedType(Scope scope, InferenceContext inferenceContext) {
	    TypeVariableBinding[] originalVariables = this.originalMethod.typeVariables; // immediate parent (could be a parameterized method)
		int varLength = originalVariables.length;
	    // infer from expected return type
		if (inferenceContext.expectedType != null) {
		    this.returnType.collectSubstitutes(scope, inferenceContext.expectedType, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
		    if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
		}
	    // infer from bounds of type parameters
		for (int i = 0; i < varLength; i++) {
			TypeVariableBinding originalVariable = originalVariables[i];
			TypeBinding argument = this.typeArguments[i];
			boolean argAlreadyInferred = argument != originalVariable;
			if (originalVariable.firstBound == originalVariable.superclass) {
				TypeBinding substitutedBound = Scope.substitute(this, originalVariable.superclass);
				argument.collectSubstitutes(scope, substitutedBound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
				if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
				// JLS 15.12.2.8 claims reverse inference shouldn't occur, however it improves inference
				// e.g. given: <E extends Object, S extends Collection<E>> S test1(S param)
				//                   invocation: test1(new Vector<String>())    will infer: S=Vector<String>  and with code below: E=String
				if (argAlreadyInferred) {
					substitutedBound.collectSubstitutes(scope, argument, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
					if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
				}
			}
			for (int j = 0, max = originalVariable.superInterfaces.length; j < max; j++) {
				TypeBinding substitutedBound = Scope.substitute(this, originalVariable.superInterfaces[j]);
				argument.collectSubstitutes(scope, substitutedBound, inferenceContext, TypeConstants.CONSTRAINT_SUPER);
				if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
				// JLS 15.12.2.8 claims reverse inference shouldn't occur, however it improves inference
				if (argAlreadyInferred) {
					substitutedBound.collectSubstitutes(scope, argument, inferenceContext, TypeConstants.CONSTRAINT_EXTENDS);
					if (inferenceContext.status == InferenceContext.FAILED) return null; // impossible substitution
				}
			}
		}
		if (!resolveSubstituteConstraints(scope, originalVariables, inferenceContext, true/*consider Ti<:Uk*/))
			return null; // incompatible
		// this.typeArguments = substitutes; - no op since side effects got performed during #resolveSubstituteConstraints
    	for (int i = 0; i < varLength; i++) {
    		TypeBinding substitute = inferenceContext.substitutes[i];
    		if (substitute != null) {
    			this.typeArguments[i] = substitute;
    		} else {
    			// remaining unresolved variable are considered to be Object (or their bound actually)
	    		this.typeArguments[i] = inferenceContext.substitutes[i] = originalVariables[i].upperBound();
	    	}
    	}
		/* May still need an extra substitution at the end (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=121369)
		   to properly substitute a remaining unresolved variable which also appear in a formal bound. See also
		   http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5021635. It is questionable though whether this extra
		   substitution should take place when the invocation site offers no guidance whatsoever and the type variables
		   are inferred to be the glb of the published bounds - as there can recursion in the formal bounds, the
		   inferred bounds would no longer be glb.
		*/
		
		this.typeArguments = Scope.substitute(this, this.typeArguments);

    	// adjust method types to reflect latest inference
		TypeBinding oldReturnType = this.returnType;
		this.returnType = Scope.substitute(this, this.returnType);
		this.inferredReturnType = inferenceContext.hasExplicitExpectedType && this.returnType != oldReturnType;
	    this.parameters = Scope.substitute(this, this.parameters);
	    this.thrownExceptions = Scope.substitute(this, this.thrownExceptions);
	    // error case where exception type variable would have been substituted by a non-reference type (207573)
	    if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
		checkMissingType: {
			if ((this.tagBits & TagBits.HasMissingType) != 0)
				break checkMissingType;
			if ((this.returnType.tagBits & TagBits.HasMissingType) != 0) {
				this.tagBits |=  TagBits.HasMissingType;
				break checkMissingType;
			}
			for (int i = 0, max = this.parameters.length; i < max; i++) {
				if ((this.parameters[i].tagBits & TagBits.HasMissingType) != 0) {
					this.tagBits |=  TagBits.HasMissingType;
					break checkMissingType;
				}
			}
			for (int i = 0, max = this.thrownExceptions.length; i < max; i++) {
				if ((this.thrownExceptions[i].tagBits & TagBits.HasMissingType) != 0) {
					this.tagBits |=  TagBits.HasMissingType;
					break checkMissingType;
				}
			}
		}
	    return this;
	}

	/* https://bugs.eclipse.org/bugs/show_bug.cgi?id=347600 && https://bugs.eclipse.org/bugs/show_bug.cgi?id=242159
	   Sometimes due to recursion/circularity in formal bounds, even *published bounds* fail bound check. We need to
	   break the circularity/self reference in order not to be overly strict during type equivalence checks.  
	   See also http://bugs.sun.com/view_bug.do?bug_id=6932571
	 */
	private static class LingeringTypeVariableEliminator implements Substitution {

		final private TypeVariableBinding [] variables;
		final private TypeBinding [] substitutes; // when null, substitute type variables by unbounded wildcard
		final private Scope scope;
		
		/**
		 * @param variables
		 * @param substitutes when null, substitute type variable by unbounded wildcard
		 * @param scope
		 */
		public LingeringTypeVariableEliminator(TypeVariableBinding [] variables, TypeBinding [] substitutes, Scope scope) {
			this.variables = variables;
			this.substitutes = substitutes;
			this.scope = scope;
		}
		// With T mapping to I<T>, answer of I<?>, when given T, having eliminated the circularity/self reference.
		public TypeBinding substitute(TypeVariableBinding typeVariable) {
			if (typeVariable.rank >= this.variables.length || this.variables[typeVariable.rank] != typeVariable) {   // not kosher, don't touch.
				return typeVariable;
			}
			if (this.substitutes != null) {
				return Scope.substitute(new LingeringTypeVariableEliminator(this.variables, null, this.scope), this.substitutes[typeVariable.rank]); 
			}
			ReferenceBinding genericType = (ReferenceBinding) (typeVariable.declaringElement instanceof ReferenceBinding ? typeVariable.declaringElement : null);
			return this.scope.environment().createWildcard(genericType, typeVariable.rank, null, null, Wildcard.UNBOUND);
		}

		public LookupEnvironment environment() {
			return this.scope.environment();
		}

		public boolean isRawSubstitution() {
			return false;
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#isRawSubstitution()
	 */
	public boolean isRawSubstitution() {
		return this.isRaw;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.Substitution#substitute(org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding)
	 */
	public TypeBinding substitute(TypeVariableBinding originalVariable) {
        TypeVariableBinding[] variables = this.originalMethod.typeVariables;
        int length = variables.length;
        // check this variable can be substituted given parameterized type
        if (originalVariable.rank < length && variables[originalVariable.rank] == originalVariable) {
			return this.typeArguments[originalVariable.rank];
        }
	    return originalVariable;
	}
	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.MethodBinding#tiebreakMethod()
	 */
	public MethodBinding tiebreakMethod() {
		if (this.tiebreakMethod == null)
			this.tiebreakMethod = this.originalMethod.asRawMethod(this.environment);
		return this.tiebreakMethod;
	}
}