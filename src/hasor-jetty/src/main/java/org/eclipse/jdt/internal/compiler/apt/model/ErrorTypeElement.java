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

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

/**
 * Element corresponding to the Error type mirror
 */
public class ErrorTypeElement extends TypeElementImpl {
	
	ErrorTypeElement(BaseProcessingEnvImpl env, ReferenceBinding binding) {
		super(env, binding, null);
	}
	/* (non-Javadoc)
	 * @see javax.lang.model.element.TypeElement#getInterfaces()
	 */
	@Override
	public List<? extends TypeMirror> getInterfaces() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.TypeElement#getNestingKind()
	 */
	@Override
	public NestingKind getNestingKind() {
		return NestingKind.TOP_LEVEL;
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.TypeElement#getQualifiedName()
	 */
	@Override
	public Name getQualifiedName() {
		ReferenceBinding binding = (ReferenceBinding)_binding;
		char[] qName;
		if (binding.isMemberType()) {
			qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
			CharOperation.replace(qName, '$', '.');
		} else {
			qName = CharOperation.concatWith(binding.compoundName, '.');
		}
		return new NameImpl(qName);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.TypeElement#getSuperclass()
	 */
	@Override
	public TypeMirror getSuperclass() {
		return this._env.getFactory().getNoType(TypeKind.NONE);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.TypeElement#getTypeParameters()
	 */
	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#asType()
	 */
	@Override
	public TypeMirror asType() {
		return this._env.getFactory().getErrorType((ReferenceBinding) this._binding);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getAnnotation(java.lang.Class)
	 */
	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getAnnotationMirrors()
	 */
	@Override
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getEnclosedElements()
	 */
	@Override
	public List<? extends Element> getEnclosedElements() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getEnclosingElement()
	 */
	@Override
	public Element getEnclosingElement() {
		return this._env.getFactory().newPackageElement(this._env.getLookupEnvironment().defaultPackage);
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getKind()
	 */
	@Override
	public ElementKind getKind() {
		return ElementKind.CLASS;
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getModifiers()
	 */
	@Override
	public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	}

	/* (non-Javadoc)
	 * @see javax.lang.model.element.Element#getSimpleName()
	 */
	@Override
	public Name getSimpleName() {
		ReferenceBinding binding = (ReferenceBinding)_binding;
		return new NameImpl(binding.sourceName());
	}
}
