/*******************************************************************************
 * Copyright (c) 2007 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;

/**
 * Implementation of PackageElement, which represents a package
 */
public class PackageElementImpl extends ElementImpl implements PackageElement {

	PackageElementImpl(BaseProcessingEnvImpl env, PackageBinding binding) {
		super(env, binding);
	}
	
	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p)
	{
		return v.visitPackage(this, p);
	}

	@Override
	protected AnnotationBinding[] getAnnotationBindings()
	{
		PackageBinding packageBinding = (PackageBinding) this._binding;
		char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
		ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
		AnnotationBinding[] annotations = null;
		if (type != null && type.isValidBinding()) {
			annotations = type.getAnnotations();
		}
		return annotations;
	}

	@Override
	public List<? extends Element> getEnclosedElements() {
		PackageBinding binding = (PackageBinding)_binding;
		LookupEnvironment environment = binding.environment;
		char[][][] typeNames = null;
		INameEnvironment nameEnvironment = binding.environment.nameEnvironment;
		if (nameEnvironment instanceof FileSystem) {
			typeNames = ((FileSystem) nameEnvironment).findTypeNames(binding.compoundName);
		}
		HashSet<Element> set = new HashSet<Element>(); 
		if (typeNames != null) {
			for (char[][] typeName : typeNames) {
				ReferenceBinding type = environment.getType(typeName);
				if (type != null && type.isValidBinding()) {
					set.add(_env.getFactory().newElement(type));
				}
			}
		}
		ArrayList<Element> list = new ArrayList<Element>(set.size());
		list.addAll(set);
		return Collections.unmodifiableList(list);
	}

	@Override
	public Element getEnclosingElement() {
		// packages have no enclosing element
		return null;
	}

	@Override
	public ElementKind getKind() {
		return ElementKind.PACKAGE;
	}

	@Override
	PackageElement getPackage()
	{
		return this;
	}

	@Override
	public Name getSimpleName() {
		char[][] compoundName = ((PackageBinding)_binding).compoundName;
		int length = compoundName.length;
		if (length == 0) {
			return new NameImpl(CharOperation.NO_CHAR);
		}
		return new NameImpl(compoundName[length - 1]);
	}

	@Override
	public Name getQualifiedName() {
		return new NameImpl(CharOperation.concatWith(((PackageBinding)_binding).compoundName, '.'));
	}

	@Override
	public boolean isUnnamed() {
		PackageBinding binding = (PackageBinding)_binding;
		return binding.compoundName == CharOperation.NO_CHAR_CHAR;
	}

}
