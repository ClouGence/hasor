/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.*;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class ElementValuePair {
	char[] name;
	public Object value;
	public MethodBinding binding;

public static Object getValue(Expression expression) {
	if (expression == null)
		return null;
	Constant constant = expression.constant;
	// literals would hit this case.
	if (constant != null && constant != Constant.NotAConstant)
		return constant;

	if (expression instanceof Annotation)
		return ((Annotation) expression).getCompilerAnnotation();
	if (expression instanceof ArrayInitializer) {
		Expression[] exprs = ((ArrayInitializer) expression).expressions;
		int length = exprs == null ? 0 : exprs.length;
		Object[] values = new Object[length];
		for (int i = 0; i < length; i++)
			values[i] = getValue(exprs[i]);
		return values;
	}
	if (expression instanceof ClassLiteralAccess)
		return ((ClassLiteralAccess) expression).targetType;
	if (expression instanceof Reference) {
		FieldBinding fieldBinding = null;
		if (expression instanceof FieldReference) {
			fieldBinding = ((FieldReference) expression).fieldBinding();
		} else if (expression instanceof NameReference) {
			Binding binding = ((NameReference) expression).binding;
			if (binding != null && binding.kind() == Binding.FIELD)
				fieldBinding = (FieldBinding) binding;
		}
		if (fieldBinding != null && (fieldBinding.modifiers & ClassFileConstants.AccEnum) > 0)
			return fieldBinding;
	}
	// something that isn't a compile time constant.
	return null;
}

public ElementValuePair(char[] name, Expression expression, MethodBinding binding) {
	this(name, ElementValuePair.getValue(expression), binding);
}

public ElementValuePair(char[] name, Object value, MethodBinding binding) {
	this.name = name;
	this.value = value;
	this.binding = binding;
}

/**
 * @return the name of the element value pair.
 */
public char[] getName() {
	return this.name;
}

/**
 * @return the method binding that defined this member value pair or null if no such binding exists.
 */
public MethodBinding getMethodBinding() {
	return this.binding;
}

/**
 * Return {@link TypeBinding} for member value of type {@link java.lang.Class}
 * Return {@link org.eclipse.jdt.internal.compiler.impl.Constant} for member of primitive type or String
 * Return {@link FieldBinding} for enum constant
 * Return {@link AnnotationBinding} for annotation instance
 * Return <code>Object[]</code> for member value of array type.
 * @return the value of this member value pair or null if the value is missing or is not a compile-time constant
 */
public Object getValue() {
	return this.value;
}

void setMethodBinding(MethodBinding binding) {
	// lazily set after annotation type was resolved
	this.binding = binding;
}

void setValue(Object value) {
	// can be modified after the initialization if holding an unresolved ref
	this.value = value;
}

public String toString() {
	StringBuffer buffer = new StringBuffer(5);
	buffer.append(this.name).append(" = "); //$NON-NLS-1$
	buffer.append(this.value);
	return buffer.toString();
}
}
