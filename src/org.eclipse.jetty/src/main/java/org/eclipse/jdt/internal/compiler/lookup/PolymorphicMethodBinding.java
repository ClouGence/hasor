/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

/**
 * Binding denoting a polymorphic method
 */
public class PolymorphicMethodBinding extends MethodBinding {

	protected MethodBinding polymorphicMethod;

	public PolymorphicMethodBinding(MethodBinding polymorphicMethod, TypeBinding[] parameterTypes) {
		super(
				polymorphicMethod.modifiers,
				polymorphicMethod.selector,
				polymorphicMethod.returnType,
				parameterTypes,
				polymorphicMethod.thrownExceptions,
				polymorphicMethod.declaringClass);
		this.polymorphicMethod = polymorphicMethod;
		this.tagBits = polymorphicMethod.tagBits;
	}
	
	public PolymorphicMethodBinding(MethodBinding polymorphicMethod, TypeBinding returnType, TypeBinding[] parameterTypes) {
		super(
				polymorphicMethod.modifiers,
				polymorphicMethod.selector,
				returnType,
				parameterTypes,
				polymorphicMethod.thrownExceptions,
				polymorphicMethod.declaringClass);
		this.polymorphicMethod = polymorphicMethod;
		this.tagBits = polymorphicMethod.tagBits;
	}

	public MethodBinding original() {
		return this.polymorphicMethod;
	}
	
	public boolean isPolymorphic() {
		return true;
	}

	public boolean matches(TypeBinding[] matchingParameters, TypeBinding matchingReturnType) {
		int cachedParametersLength = this.parameters == null ? 0 : this.parameters.length;
		int matchingParametersLength = matchingParameters == null ? 0 : matchingParameters.length;
		if (matchingParametersLength != cachedParametersLength) {
			return false;
		}
		for (int j = 0; j < cachedParametersLength; j++){
			if (this.parameters[j] != matchingParameters[j]) {
				return false;
			}
		}
		TypeBinding cachedReturnType = this.returnType;
		if (matchingReturnType == null) {
			if (cachedReturnType != null) {
				return false;
			}
		} else if (cachedReturnType == null) {
			return false;
		} else if (matchingReturnType != cachedReturnType) {
			return false;
		}
		// all arguments match
		return true;
	}
	
	/*
	 * Even if polymorphic methods are varargs method, we don't want them to be treated as varargs method
	 */
	public boolean isVarargs() {
		return false;
	}
}
