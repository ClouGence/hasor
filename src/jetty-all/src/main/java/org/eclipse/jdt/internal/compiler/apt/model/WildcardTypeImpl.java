/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;

/**
 * Implementation of the WildcardType
 */
public class WildcardTypeImpl extends TypeMirrorImpl implements WildcardType {
	
	WildcardTypeImpl(BaseProcessingEnvImpl env, WildcardBinding binding) {
		super(env, binding);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.WildcardType#getExtendsBound()
	 */
	@Override
	public TypeMirror getExtendsBound() {
		WildcardBinding wildcardBinding = (WildcardBinding) this._binding;
		if (wildcardBinding.boundKind != Wildcard.EXTENDS) return null;
		TypeBinding bound = wildcardBinding.bound;
		if (bound == null) return null;
		return _env.getFactory().newTypeMirror(bound);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.type.TypeMirror#getKind()
	 */
	@Override
	public TypeKind getKind() {
		return TypeKind.WILDCARD;
	}
	/* (non-Javadoc)
	 * @see javax.lang.model.type.WildcardType#getSuperBound()
	 */
	@Override
	public TypeMirror getSuperBound() {
		WildcardBinding wildcardBinding = (WildcardBinding) this._binding;
		if (wildcardBinding.boundKind != Wildcard.SUPER) return null;
		TypeBinding bound = wildcardBinding.bound;
		if (bound == null) return null;
		return _env.getFactory().newTypeMirror(bound);
	}
	
	@Override
	public <R, P> R accept(TypeVisitor<R, P> v, P p) {
		return v.visitWildcard(this, p);
	}
}
