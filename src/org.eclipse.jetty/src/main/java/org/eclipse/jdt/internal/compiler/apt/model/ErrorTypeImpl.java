/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

/**
 * Implementation of the {@link ErrorType} interface.
 */
public class ErrorTypeImpl extends DeclaredTypeImpl implements ErrorType {

	/* package */ ErrorTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding) {
		super(env, binding);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.DeclaredType#asElement()
	 */
	@Override
	public Element asElement() {
		return this._env.getFactory().newElement((ReferenceBinding) this._binding);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.DeclaredType#getEnclosingType()
	 */
	@Override
	public TypeMirror getEnclosingType() {
		return NoTypeImpl.NO_TYPE_NONE;
	}

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
		return v.visitError(this, p);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.TypeMirror#getKind()
	 */
	@Override
	public TypeKind getKind() {
		return TypeKind.ERROR;
	}
	
	@Override
	public String toString() {
		return new String(_binding.readableName());
	}
}
