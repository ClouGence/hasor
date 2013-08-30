/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

public class AptBinaryLocalVariableBinding extends LocalVariableBinding {
	AnnotationBinding[] annotationBindings;
	// enclosing element
	public MethodBinding methodBinding;
	
	public AptBinaryLocalVariableBinding(char[] name, TypeBinding type, int modifiers, AnnotationBinding[] annotationBindings, MethodBinding methodBinding) {
		super(name, type, modifiers, true);
		this.annotationBindings = annotationBindings == null ? Binding.NO_ANNOTATIONS : annotationBindings;
		this.methodBinding = methodBinding;
	}

	public AnnotationBinding[] getAnnotations() {
		return this.annotationBindings;
	}
}
