/*******************************************************************************
 * Copyright (c) 2007, 2009 BEA Systems, Inc. 
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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

/**
 * Implementation of VariableElement, which represents a a field, enum constant, 
 * method or constructor parameter, local variable, or exception parameter.
 */
public class VariableElementImpl extends ElementImpl implements VariableElement {

	/**
	 * @param binding might be a FieldBinding (for a field) or a LocalVariableBinding (for a method param)
	 */
	VariableElementImpl(BaseProcessingEnvImpl env, VariableBinding binding) {
		super(env, binding);
	}
	
	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p)
	{
		return v.visitVariable(this, p);
	}

	@Override
	protected AnnotationBinding[] getAnnotationBindings()
	{
		return ((VariableBinding)_binding).getAnnotations();
	}

	@Override
	public Object getConstantValue() {
		VariableBinding variableBinding = (VariableBinding) _binding;
		Constant constant = variableBinding.constant();
		if (constant == null || constant == Constant.NotAConstant) return null;
		TypeBinding type = variableBinding.type;
		switch (type.id) {
			case TypeIds.T_boolean:
				return constant.booleanValue();
			case TypeIds.T_byte:
				return constant.byteValue();
			case TypeIds.T_char:
				return constant.charValue();
			case TypeIds.T_double:
				return constant.doubleValue();
			case TypeIds.T_float:
				return constant.floatValue();
			case TypeIds.T_int:
				return constant.intValue();
			case TypeIds.T_JavaLangString:
				return constant.stringValue();
			case TypeIds.T_long:
				return constant.longValue();
			case TypeIds.T_short:
				return constant.shortValue();
		}
		return null;
	}
	
	@Override
	public List<? extends Element> getEnclosedElements() {
		return Collections.emptyList();
	}

	@Override
	public Element getEnclosingElement() {
		if (_binding instanceof FieldBinding) {
			return _env.getFactory().newElement(((FieldBinding)_binding).declaringClass);
		}
		else if (_binding instanceof AptSourceLocalVariableBinding){
			return _env.getFactory().newElement(((AptSourceLocalVariableBinding) _binding).methodBinding);
		} else if (_binding instanceof AptBinaryLocalVariableBinding) {
			return _env.getFactory().newElement(((AptBinaryLocalVariableBinding) _binding).methodBinding);
		}
		return null;
	}

	@Override
	public ElementKind getKind() {
		if (_binding instanceof FieldBinding) {
			if (((FieldBinding)_binding).declaringClass.isEnum()) {
				return ElementKind.ENUM_CONSTANT;
			}
			else {
				return ElementKind.FIELD;
			}
		}
		else {
			return ElementKind.PARAMETER;
		}
	}

	@Override
	public Set<Modifier> getModifiers()
	{
		if (_binding instanceof VariableBinding) {
			return Factory.getModifiers(((VariableBinding)_binding).modifiers, getKind());
		}
		return Collections.emptySet();
	}

	@Override
	PackageElement getPackage()
	{
		if (_binding instanceof FieldBinding) {
			PackageBinding pkgBinding = ((FieldBinding)_binding).declaringClass.fPackage;
			return _env.getFactory().newPackageElement(pkgBinding);
		}
		else {
			// TODO: what is the package of a method parameter?
			throw new UnsupportedOperationException("NYI: VariableElmentImpl.getPackage() for method parameter"); //$NON-NLS-1$
		}
	}
	
	@Override
	public Name getSimpleName() {
		return new NameImpl(((VariableBinding)_binding).name);
	}

	@Override
	public boolean hides(Element hiddenElement)
	{
		if (_binding instanceof FieldBinding) {
			if (!(((ElementImpl)hiddenElement)._binding instanceof FieldBinding)) {
				return false;
			}
			FieldBinding hidden = (FieldBinding)((ElementImpl)hiddenElement)._binding;
			if (hidden.isPrivate()) {
				return false;
			}
			FieldBinding hider = (FieldBinding)_binding;
			if (hidden == hider) {
				return false;
			}
			if (!CharOperation.equals(hider.name, hidden.name)) {
				return false;
			}
			return null != hider.declaringClass.findSuperTypeOriginatingFrom(hidden.declaringClass);
		}
		// TODO: should we implement hides() for method parameters?
		return false;
	}

	@Override
	public String toString() {
		return new String(((VariableBinding) _binding).name);
	}
}
