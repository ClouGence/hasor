/*******************************************************************************
 * Copyright (c) 2006, 2011 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    IBM Corporation - fix for 342598
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

/**
 * Implementation of DeclaredType, which refers to a particular usage or instance of a type.
 * Contrast with {@link TypeElement}, which is an element that potentially defines a family
 * of DeclaredTypes.
 */
public class DeclaredTypeImpl extends TypeMirrorImpl implements DeclaredType {
	
	private final ElementKind _elementKindHint;
	
	/* package */ DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding) {
		super(env, binding);
		_elementKindHint = null;
	}

	/**
	 * Create a DeclaredType that knows in advance what kind of element to produce from asElement().
	 * This is useful in the case where the type binding is to an unresolved type, but we know
	 * from context what type it is - e.g., an annotation type.
	 */
	/* package */ DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding, ElementKind elementKindHint) {
		super(env, binding);
		_elementKindHint = elementKindHint;
	}

	@Override
	public Element asElement() {
		// The JDT compiler does not distinguish between type elements and declared types
		return _env.getFactory().newElement((ReferenceBinding)_binding, _elementKindHint);
	}

	@Override
	public TypeMirror getEnclosingType() {
		ReferenceBinding binding = (ReferenceBinding)_binding;
		ReferenceBinding enclosingType = binding.enclosingType();
		if (enclosingType != null) {
			return _env.getFactory().newTypeMirror(enclosingType);
		}
		return _env.getFactory().getNoType(TypeKind.NONE);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.lang.model.type.DeclaredType#getTypeArguments()
	 * @see javax.lang.model.element.TypeElement#getTypeParameters().
	 */
	@Override
	public List<? extends TypeMirror> getTypeArguments() {
		ReferenceBinding binding = (ReferenceBinding)_binding;
		if (binding.isParameterizedType()) {
			ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)_binding;
			TypeBinding[] arguments = ptb.arguments;
			int length = arguments == null ? 0 : arguments.length;
			if (length == 0) return Collections.emptyList();
			List<TypeMirror> args = new ArrayList<TypeMirror>(length);
			for (TypeBinding arg : arguments) {
				args.add(_env.getFactory().newTypeMirror(arg));
			}
			return Collections.unmodifiableList(args);
		}
		if (binding.isGenericType()) {
			TypeVariableBinding[] typeVariables = binding.typeVariables();
			List<TypeMirror> args = new ArrayList<TypeMirror>(typeVariables.length);
			for (TypeBinding arg : typeVariables) {
				args.add(_env.getFactory().newTypeMirror(arg));
			}
			return Collections.unmodifiableList(args);
		}
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.TypeMirror#accept(javax.lang.model.type.TypeVisitor, java.lang.Object)
	 */
	@Override
	public <R, P> R accept(TypeVisitor<R, P> v, P p) {
		return v.visitDeclared(this, p);
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.DECLARED;
	}

	@Override
	public String toString() {
		return new String(_binding.readableName());
	}

}
