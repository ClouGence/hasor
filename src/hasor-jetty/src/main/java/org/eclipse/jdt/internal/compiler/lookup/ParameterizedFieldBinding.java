/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.Constant;

/**
 * Binding denoting a field after type substitution got performed.
 * On parameterized type bindings, all fields got substituted, regardless whether
 * their signature did involve generics or not, so as to get the proper declaringClass for
 * these fields.
 */
public class ParameterizedFieldBinding extends FieldBinding {

    public FieldBinding originalField;

public ParameterizedFieldBinding(ParameterizedTypeBinding parameterizedDeclaringClass, FieldBinding originalField) {
    super (
            originalField.name,
            (originalField.modifiers & ClassFileConstants.AccEnum) != 0
            	? parameterizedDeclaringClass // enum constant get paramType as its type
       			: (originalField.modifiers & ClassFileConstants.AccStatic) != 0
       					? originalField.type // no subst for static field
       					: Scope.substitute(parameterizedDeclaringClass, originalField.type),
            originalField.modifiers,
            parameterizedDeclaringClass,
            null);
    this.originalField = originalField;
    this.tagBits = originalField.tagBits;
    this.id = originalField.id;
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.VariableBinding#constant()
 */
public Constant constant() {
	return this.originalField.constant();
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.FieldBinding#original()
 */
public FieldBinding original() {
	return this.originalField.original();
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.VariableBinding#constant()
 */
public void setConstant(Constant constant) {
	this.originalField.setConstant(constant);
}
}
