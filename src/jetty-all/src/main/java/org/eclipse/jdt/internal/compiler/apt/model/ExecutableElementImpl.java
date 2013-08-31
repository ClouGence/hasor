/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
import java.util.Set;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationHolder;
import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;

public class ExecutableElementImpl extends ElementImpl implements
		ExecutableElement {
	
	private Name _name = null;
	
	/* package */ ExecutableElementImpl(BaseProcessingEnvImpl env, MethodBinding binding) {
		super(env, binding);
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p)
	{
		return v.visitExecutable(this, p);
	}

	@Override
	protected AnnotationBinding[] getAnnotationBindings()
	{
		return ((MethodBinding)_binding).getAnnotations();
	}

	@Override
	public AnnotationValue getDefaultValue() {
		MethodBinding binding = (MethodBinding)_binding;
		Object defaultValue = binding.getDefaultValue();
		if (defaultValue != null) return new AnnotationMemberValue(_env, defaultValue, binding);
		return null;
	}
	
	@Override
	public List<? extends Element> getEnclosedElements() {
		return Collections.emptyList();
	}

	@Override
	public Element getEnclosingElement() {
		MethodBinding binding = (MethodBinding)_binding;
		if (null == binding.declaringClass) {
			return null;
		}
		return _env.getFactory().newElement(binding.declaringClass);
	}

	@Override
	public String getFileName() {
		ReferenceBinding dc = ((MethodBinding)_binding).declaringClass;
		char[] name = dc.getFileName();
		if (name == null)
			return null;
		return new String(name);
	}

	@Override
	public ElementKind getKind() {
		MethodBinding binding = (MethodBinding)_binding;
		if (binding.isConstructor()) {
			return ElementKind.CONSTRUCTOR;
		}
		else if (CharOperation.equals(binding.selector, TypeConstants.CLINIT)) {
			return ElementKind.STATIC_INIT;
		}
		else if (CharOperation.equals(binding.selector, TypeConstants.INIT)) {
			return ElementKind.INSTANCE_INIT;
		}
		else {
			return ElementKind.METHOD;
		}
	}

	@Override
	public Set<Modifier> getModifiers() {
		MethodBinding binding = (MethodBinding)_binding;
		return Factory.getModifiers(binding.modifiers, getKind());
	}

	@Override
	PackageElement getPackage()
	{
		MethodBinding binding = (MethodBinding)_binding;
		if (null == binding.declaringClass) {
			return null;
		}
		return _env.getFactory().newPackageElement(binding.declaringClass.fPackage);
	}

	@Override
	public List<? extends VariableElement> getParameters() {
		MethodBinding binding = (MethodBinding)_binding;
		int length = binding.parameters == null ? 0 : binding.parameters.length;
		if (0 != length) {
			AbstractMethodDeclaration methodDeclaration = binding.sourceMethod();
			List<VariableElement> params = new ArrayList<VariableElement>(length);
			if (methodDeclaration != null) {
				for (Argument argument : methodDeclaration.arguments) {
					VariableElement param = new VariableElementImpl(_env, argument.binding);
					params.add(param);
				}
			} else {
				// binary method
				boolean isEnumConstructor = binding.isConstructor()
						&& binding.declaringClass.isEnum()
						&& binding.declaringClass.isBinaryBinding()
						&& ((binding.modifiers & ExtraCompilerModifiers.AccGenericSignature) == 0);
				AnnotationBinding[][] parameterAnnotationBindings = null;
				AnnotationHolder annotationHolder = binding.declaringClass.retrieveAnnotationHolder(binding, false);
				if (annotationHolder != null) {
					parameterAnnotationBindings = annotationHolder.getParameterAnnotations();
				}
				// we need to filter the synthetic arguments
				if (isEnumConstructor) {
					if (length == 2) {
						// the two arguments are only the two synthetic arguments
						return Collections.emptyList();
					}
					for (int i = 2; i < length; i++) {
						TypeBinding typeBinding = binding.parameters[i];
						StringBuilder builder = new StringBuilder("arg");//$NON-NLS-1$
						builder.append(i - 2);
						VariableElement param = new VariableElementImpl(_env,
								new AptBinaryLocalVariableBinding(
										String.valueOf(builder).toCharArray(),
										typeBinding,
										0,
										null,
										binding));
						params.add(param);
					}
				} else {
					int i = 0;
					for (TypeBinding typeBinding : binding.parameters) {
						StringBuilder builder = new StringBuilder("arg");//$NON-NLS-1$
						builder.append(i);
						VariableElement param = new VariableElementImpl(_env,
								new AptBinaryLocalVariableBinding(
										String.valueOf(builder).toCharArray(),
										typeBinding,
										0,
										parameterAnnotationBindings != null ? parameterAnnotationBindings[i] : null,
										binding));
						params.add(param);
						i++;
					}
				}
			}
			return Collections.unmodifiableList(params);
		}
		return Collections.emptyList();
	}

	@Override
	public TypeMirror getReturnType() {
		MethodBinding binding = (MethodBinding)_binding;
		if (binding.returnType == null) {
			return null;
		}
		else return _env.getFactory().newTypeMirror(binding.returnType);
	}

	@Override
	public Name getSimpleName() {
		MethodBinding binding = (MethodBinding)_binding;
		if (_name == null) {
			_name = new NameImpl(binding.selector);
		}
		return _name;
	}
	
	@Override
	public List<? extends TypeMirror> getThrownTypes() {
		MethodBinding binding = (MethodBinding)_binding;
		if (binding.thrownExceptions.length == 0) {
			return Collections.emptyList();
		}
		List<TypeMirror> list = new ArrayList<TypeMirror>(binding.thrownExceptions.length);
		for (ReferenceBinding exception : binding.thrownExceptions) {
			list.add(_env.getFactory().newTypeMirror(exception));
		}
		return list;
	}

	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
		MethodBinding binding = (MethodBinding)_binding;
		TypeVariableBinding[] variables = binding.typeVariables();
		if (variables.length == 0) {
			return Collections.emptyList();
		}
		List<TypeParameterElement> params = new ArrayList<TypeParameterElement>(variables.length); 
		for (TypeVariableBinding variable : variables) {
			params.add(_env.getFactory().newTypeParameterElement(variable, this));
		}
		return Collections.unmodifiableList(params);
	}

	@Override
	public boolean hides(Element hidden)
	{
		if (!(hidden instanceof ExecutableElementImpl)) {
			return false;
		}
		MethodBinding hiderBinding = (MethodBinding)_binding;
		MethodBinding hiddenBinding = (MethodBinding)((ExecutableElementImpl)hidden)._binding;
		if (hiderBinding == hiddenBinding) {
			return false;
		}
		if (hiddenBinding.isPrivate()) {
			return false;
		}
		// See JLS 8.4.8: hiding only applies to static methods
		if (!hiderBinding.isStatic() || !hiddenBinding.isStatic()) {
			return false;
		}
		// check names
		if (!CharOperation.equals(hiddenBinding.selector, hiderBinding.selector)) {
			return false;
		}
		// check parameters
		if (!_env.getLookupEnvironment().methodVerifier().isMethodSubsignature(hiderBinding, hiddenBinding)) {
			return false;
		}
		return null != hiderBinding.declaringClass.findSuperTypeOriginatingFrom(hiddenBinding.declaringClass); 
	}

	@Override
	public boolean isVarArgs() {
		return ((MethodBinding) _binding).isVarargs();
	}

	/**
	 * Return true if this method overrides {@code overridden} in the context of {@code type}.  For
	 * instance, consider 
	 * <pre>
	 *   interface A { void f(); }
	 *   class B { void f() {} }
	 *   class C extends B implements I { }
	 * </pre> 
	 * In the context of B, B.f() does not override A.f(); they are unrelated.  But in the context of
	 * C, B.f() does override A.f().  That is, the copy of B.f() that C inherits overrides A.f().
	 * This is equivalent to considering two questions: first, does C inherit B.f(); if so, does
	 * the inherited C.f() override A.f().  If B.f() were private, for instance, then in the context
	 * of C it would still not override A.f().  
	 * 
	 * @see javax.lang.model.util.Elements#overrides(ExecutableElement, ExecutableElement, TypeElement)
     * @jls3 8.4.8 Inheritance, Overriding, and Hiding
     * @jls3 9.4.1 Inheritance and Overriding
	 */
	public boolean overrides(ExecutableElement overridden, TypeElement type)
	{
		MethodBinding overriddenBinding = (MethodBinding)((ExecutableElementImpl) overridden)._binding;
		ReferenceBinding overriderContext = (ReferenceBinding)((TypeElementImpl)type)._binding;
		if ((MethodBinding)_binding == overriddenBinding
				|| overriddenBinding.isStatic()
				|| overriddenBinding.isPrivate()
				|| ((MethodBinding)_binding).isStatic()) {
			return false;
		}
		char[] selector = ((MethodBinding)_binding).selector;
		if (!CharOperation.equals(selector, overriddenBinding.selector))
			return false;
		
		// Construct a binding to the equivalent of this (the overrider) as it would be inherited by 'type'.
		// Can only do this if 'type' is descended from the overrider.
		// Second clause of the AND is required to match a peculiar javac behavior.
		if (null == overriderContext.findSuperTypeOriginatingFrom(((MethodBinding)_binding).declaringClass) &&
				null == ((MethodBinding)_binding).declaringClass.findSuperTypeOriginatingFrom(overriderContext)) {
			return false;
		}
		MethodBinding overriderBinding = new MethodBinding((MethodBinding)_binding, overriderContext);
		if (overriderBinding.isPrivate()) {
			// a private method can never override another method.  The other method would either be
			// private itself, in which case it would not be visible; or this would be a restriction 
			// of access, which is a compile-time error.
			return false;
		}
		
		TypeBinding match = overriderBinding.declaringClass.findSuperTypeOriginatingFrom(overriddenBinding.declaringClass);
		if (!(match instanceof ReferenceBinding)) return false;

		org.eclipse.jdt.internal.compiler.lookup.MethodBinding[] superMethods = ((ReferenceBinding)match).getMethods(selector);
		LookupEnvironment lookupEnvironment = _env.getLookupEnvironment();
		if (lookupEnvironment == null) return false;
		MethodVerifier methodVerifier = lookupEnvironment.methodVerifier();
		for (int i = 0, length = superMethods.length; i < length; i++) {
			if (superMethods[i].original() == overriddenBinding) {
				return methodVerifier.doesMethodOverride(overriderBinding, superMethods[i]);
			}
		}
		return false;
	}

}
