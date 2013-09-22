/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

/**
 * This class is used to visit the JDT compiler internal AST to discover annotations, 
 * in the course of dispatching to annotation processors.
 */
public class AnnotationDiscoveryVisitor extends ASTVisitor {
	final BaseProcessingEnvImpl _env;
	final Factory _factory;
	/**
	 * Collects a many-to-many map of annotation types to
	 * the elements they appear on.
	 */
	final ManyToMany<TypeElement, Element> _annoToElement;

	public AnnotationDiscoveryVisitor(BaseProcessingEnvImpl env) {
		_env = env;
		_factory = env.getFactory();
		_annoToElement = new ManyToMany<TypeElement, Element>();
	}

	@Override
	public boolean visit(Argument argument, BlockScope scope) {
		Annotation[] annotations = argument.annotations;
		ReferenceContext referenceContext = scope.referenceContext();
		if (referenceContext instanceof AbstractMethodDeclaration) {
			MethodBinding binding = ((AbstractMethodDeclaration) referenceContext).binding;
			if (binding != null) {
				TypeDeclaration typeDeclaration = scope.referenceType();
				typeDeclaration.binding.resolveTypesFor(binding);
				if (argument.binding != null) {
					argument.binding = new AptSourceLocalVariableBinding(argument.binding, binding);
				}
			}
			if (annotations != null) {
				this.resolveAnnotations(
						scope,
						annotations,
						argument.binding);
			}
		}
		return false;
	}

	@Override
	public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
		Annotation[] annotations = constructorDeclaration.annotations;
		if (annotations != null) {
			MethodBinding constructorBinding = constructorDeclaration.binding;
			if (constructorBinding == null) {
				return false;
			}
			((SourceTypeBinding) constructorBinding.declaringClass).resolveTypesFor(constructorBinding);
			this.resolveAnnotations(
					constructorDeclaration.scope,
					annotations,
					constructorBinding);
		}
		Argument[] arguments = constructorDeclaration.arguments;
		if (arguments != null) {
			int argumentLength = arguments.length;
			for (int i = 0; i < argumentLength; i++) {
				arguments[i].traverse(this, constructorDeclaration.scope);
			}
		}
		return false;
	}

	@Override
	public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
		Annotation[] annotations = fieldDeclaration.annotations;
		if (annotations != null) {
			FieldBinding fieldBinding = fieldDeclaration.binding;
			if (fieldBinding == null) {
				return false;
			}
			((SourceTypeBinding) fieldBinding.declaringClass).resolveTypeFor(fieldBinding);
			this.resolveAnnotations(scope, annotations, fieldBinding);
		}
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
		Annotation[] annotations = methodDeclaration.annotations;
		if (annotations != null) {
			MethodBinding methodBinding = methodDeclaration.binding;
			if (methodBinding == null) {
				return false;
			}
			((SourceTypeBinding) methodBinding.declaringClass).resolveTypesFor(methodBinding);
			this.resolveAnnotations(
					methodDeclaration.scope,
					annotations,
					methodDeclaration.binding);
		}

		Argument[] arguments = methodDeclaration.arguments;
		if (arguments != null) {
			int argumentLength = arguments.length;
			for (int i = 0; i < argumentLength; i++) {
				arguments[i].traverse(this, methodDeclaration.scope);
			}
		}
		return false;
	}

	@Override
	public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
		SourceTypeBinding binding = memberTypeDeclaration.binding;
		if (binding == null) {
			return false;
		}
		Annotation[] annotations = memberTypeDeclaration.annotations;
		if (annotations != null) {
			this.resolveAnnotations(
					memberTypeDeclaration.staticInitializerScope,
					annotations,
					binding);
		}
		return true;
	}

	@Override
	public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
		SourceTypeBinding binding = typeDeclaration.binding;
		if (binding == null) {
			return false;
		}
		Annotation[] annotations = typeDeclaration.annotations;
		if (annotations != null) {
			this.resolveAnnotations(
					typeDeclaration.staticInitializerScope,
					annotations,
					binding);
		}
		return true;
	}

	private void resolveAnnotations(
			BlockScope scope,
			Annotation[] annotations,
			Binding currentBinding) {
		ASTNode.resolveAnnotations(scope, annotations, currentBinding);
		
		for (Annotation annotation : annotations) {
			AnnotationBinding binding = annotation.getCompilerAnnotation();
			if (binding != null) { // binding should be resolved, but in case it's not, ignore it
				TypeElement anno = (TypeElement)_factory.newElement(binding.getAnnotationType()); 
				Element element = _factory.newElement(currentBinding);
				_annoToElement.put(anno, element);
			}
		}
	}
}
