/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 *    IBM Corporation - Fix for bug 328575
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.TypeElementImpl;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;

public class RoundEnvImpl implements RoundEnvironment
{
	private final BaseProcessingEnvImpl _processingEnv;
	private final boolean _isLastRound;
	private final CompilationUnitDeclaration[] _units;
	private final ManyToMany<TypeElement, Element> _annoToUnit;
	private final ReferenceBinding[] _binaryTypes;
	private final Factory _factory;
	private Set<Element> _rootElements = null;

	public RoundEnvImpl(CompilationUnitDeclaration[] units, ReferenceBinding[] binaryTypeBindings, boolean isLastRound, BaseProcessingEnvImpl env) {
		_processingEnv = env;
		_isLastRound = isLastRound;
		_units = units;
		_factory = _processingEnv.getFactory();
		
		// Discover the annotations that will be passed to Processor.process()
		AnnotationDiscoveryVisitor visitor = new AnnotationDiscoveryVisitor(_processingEnv);
		if (_units != null) {
			for (CompilationUnitDeclaration unit : _units) {
				unit.traverse(visitor, unit.scope);
			}
		}
		_annoToUnit = visitor._annoToElement;
		if (binaryTypeBindings != null) collectAnnotations(binaryTypeBindings);
		_binaryTypes = binaryTypeBindings;
	}

	private void collectAnnotations(ReferenceBinding[] referenceBindings) {
		for (ReferenceBinding referenceBinding : referenceBindings) {
			// collect all annotations from the binary types
			if (referenceBinding instanceof ParameterizedTypeBinding) {
				referenceBinding = ((ParameterizedTypeBinding) referenceBinding).genericType();
			}
			AnnotationBinding[] annotationBindings = referenceBinding.getAnnotations();
			for (AnnotationBinding annotationBinding : annotationBindings) {
				TypeElement anno = (TypeElement)_factory.newElement(annotationBinding.getAnnotationType()); 
				Element element = _factory.newElement(referenceBinding);
				_annoToUnit.put(anno, element);
			}
			FieldBinding[] fieldBindings = referenceBinding.fields();
			for (FieldBinding fieldBinding : fieldBindings) {
				annotationBindings = fieldBinding.getAnnotations();
				for (AnnotationBinding annotationBinding : annotationBindings) {
					TypeElement anno = (TypeElement)_factory.newElement(annotationBinding.getAnnotationType()); 
					Element element = _factory.newElement(fieldBinding);
					_annoToUnit.put(anno, element);
				}
			}
			MethodBinding[] methodBindings = referenceBinding.methods();
			for (MethodBinding methodBinding : methodBindings) {
				annotationBindings = methodBinding.getAnnotations();
				for (AnnotationBinding annotationBinding : annotationBindings) {
					TypeElement anno = (TypeElement)_factory.newElement(annotationBinding.getAnnotationType()); 
					Element element = _factory.newElement(methodBinding);
					_annoToUnit.put(anno, element);
				}
			}
			ReferenceBinding[] memberTypes = referenceBinding.memberTypes();
			collectAnnotations(memberTypes);
		}
	}

	/**
	 * Return the set of annotation types that were discovered on the root elements.
	 * This does not include inherited annotations, only those directly on the root
	 * elements.
	 * @return a set of annotation types, possibly empty.
	 */
	public Set<TypeElement> getRootAnnotations()
	{
		return Collections.unmodifiableSet(_annoToUnit.getKeySet());
	}

	@Override
	public boolean errorRaised()
	{
		return _processingEnv.errorRaised();
	}

	/**
	 * From the set of root elements and their enclosed elements, return the subset that are annotated
	 * with {@code a}.  If {@code a} is annotated with the {@link java.lang.annotations.Inherited} 
	 * annotation, include those elements that inherit the annotation from their superclasses.
	 * Note that {@link java.lang.annotations.Inherited} only applies to classes (i.e. TypeElements).
	 */
	@Override
	public Set<? extends Element> getElementsAnnotatedWith(TypeElement a)
	{
		if (a.getKind() != ElementKind.ANNOTATION_TYPE) {
			throw new IllegalArgumentException("Argument must represent an annotation type"); //$NON-NLS-1$
		}
		Binding annoBinding = ((TypeElementImpl)a)._binding;
		if (0 != (annoBinding.getAnnotationTagBits() & TagBits.AnnotationInherited)) {
			Set<Element> annotatedElements = new HashSet<Element>(_annoToUnit.getValues(a));
			// For all other root elements that are TypeElements, and for their recursively enclosed
			// types, add each element if it has a superclass are annotated with 'a'
			ReferenceBinding annoTypeBinding = (ReferenceBinding) annoBinding;
			for (TypeElement element : ElementFilter.typesIn(getRootElements())) {
				ReferenceBinding typeBinding = (ReferenceBinding)((TypeElementImpl)element)._binding;
				addAnnotatedElements(annoTypeBinding, typeBinding, annotatedElements);
			}
			return Collections.unmodifiableSet(annotatedElements);
		}
		return Collections.unmodifiableSet(_annoToUnit.getValues(a));
	}
	
	/**
	 * For every type in types that is a class and that is annotated with anno, either directly or by inheritance,
	 * add that type to result.  Recursively descend on each types's child classes as well.
	 * @param anno the compiler binding for an annotation type
	 * @param types a set of types, not necessarily all classes
	 * @param result must be a modifiable Set; will accumulate annotated classes
	 */
	private void addAnnotatedElements(ReferenceBinding anno, ReferenceBinding type, Set<Element> result) {
		if (type.isClass()) {
			if (inheritsAnno(type, anno)) {
				result.add(_factory.newElement(type));
			}
		}
		for (ReferenceBinding element : type.memberTypes()) {
			addAnnotatedElements(anno, element, result);
		}
	}
	
	/**
	 * Check whether an element has a superclass that is annotated with an @Inherited annotation.
	 * @param element must be a class (not an interface, enum, etc.).
	 * @param anno must be an annotation type, and must be @Inherited
	 * @return true if element has a superclass that is annotated with anno
	 */
	private boolean inheritsAnno(ReferenceBinding element, ReferenceBinding anno) {
		ReferenceBinding searchedElement = element;
		do {
			if (searchedElement instanceof ParameterizedTypeBinding) {
				searchedElement = ((ParameterizedTypeBinding) searchedElement).genericType();
			}
			AnnotationBinding[] annos = searchedElement.getAnnotations();
			for (AnnotationBinding annoBinding : annos) {
				if (annoBinding.getAnnotationType() == anno) {
					// element is annotated with anno
					return true;
				}
			}
		} while (null != (searchedElement = searchedElement.superclass()));
		return false;
	}
	
	@Override
	public Set<? extends Element> getElementsAnnotatedWith(Class<? extends Annotation> a)
	{
		String canonicalName = a.getCanonicalName();
		if (canonicalName == null) {
			// null for anonymous and local classes or an array of those
			throw new IllegalArgumentException("Argument must represent an annotation type"); //$NON-NLS-1$
		}
		TypeElement annoType = _processingEnv.getElementUtils().getTypeElement(canonicalName);
		return getElementsAnnotatedWith(annoType);
	}

	@Override
	public Set<? extends Element> getRootElements()
	{
		if (_units == null) {
			return Collections.emptySet();
		}
		if (_rootElements == null) {
			Set<Element> elements = new HashSet<Element>(_units.length);
			for (CompilationUnitDeclaration unit : _units) {
				if (null == unit.scope || null == unit.scope.topLevelTypes)
					continue;
				for (SourceTypeBinding binding : unit.scope.topLevelTypes) {
					Element element = _factory.newElement(binding);
					if (null == element) {
						throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + binding); //$NON-NLS-1$
					}
					elements.add(element);
				}
			}
			if (this._binaryTypes != null) {
				for (ReferenceBinding typeBinding : _binaryTypes) {
					TypeElement element = (TypeElement)_factory.newElement(typeBinding);
					if (null == element) {
						throw new IllegalArgumentException("Top-level type binding could not be converted to element: " + typeBinding); //$NON-NLS-1$
					}
					elements.add(element);
				}
			}
			_rootElements = elements;
		}
		return _rootElements;
	}

	@Override
	public boolean processingOver()
	{
		return _isLastRound;
	}

}
