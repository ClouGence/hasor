/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 185682 - Increment/decrement operators mark local variables as read
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;

public class LocalVariableBinding extends VariableBinding {

	public int resolvedPosition; // for code generation (position in method context)

	public static final int UNUSED = 0;
	public static final int USED = 1;
	public static final int FAKE_USED = 2;
	public int useFlag; // for flow analysis (default is UNUSED), values < 0 indicate the number of compound uses (postIncrement or compoundAssignment)

	public BlockScope declaringScope; // back-pointer to its declaring scope
	public LocalDeclaration declaration; // for source-positions

	public int[] initializationPCs;
	public int initializationCount = 0;

	// for synthetic local variables
	// if declaration slot is not positionned, the variable will not be listed in attribute
	// note that the name of a variable should be chosen so as not to conflict with user ones (usually starting with a space char is all needed)
	public LocalVariableBinding(char[] name, TypeBinding type, int modifiers, boolean isArgument) {
		super(name, type, modifiers, isArgument ? Constant.NotAConstant : null);
		if (isArgument) this.tagBits |= TagBits.IsArgument;
		this.tagBits |= TagBits.IsEffectivelyFinal;
	}

	// regular local variable or argument
	public LocalVariableBinding(LocalDeclaration declaration, TypeBinding type, int modifiers, boolean isArgument) {

		this(declaration.name, type, modifiers, isArgument);
		this.declaration = declaration;
		this.tagBits |= TagBits.IsEffectivelyFinal;
	}

	/* API
	* Answer the receiver's binding type from Binding.BindingID.
	*/
	public final int kind() {
		return LOCAL;
	}

	/*
	 * declaringUniqueKey # scopeIndex(0-based) # varName [# occurrenceCount(0-based)]
	 * p.X { void foo() { int local; int local;} } --> Lp/X;.foo()V#1#local#1
	 */
	public char[] computeUniqueKey(boolean isLeaf) {
		StringBuffer buffer = new StringBuffer();

		// declaring method or type
		BlockScope scope = this.declaringScope;
		int occurenceCount = 0;
		if (scope != null) {
			// the scope can be null. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=185129
			MethodScope methodScope = scope instanceof MethodScope ? (MethodScope) scope : scope.enclosingMethodScope();
			ReferenceContext referenceContext = methodScope.referenceContext;
			if (referenceContext instanceof AbstractMethodDeclaration) {
				MethodBinding methodBinding = ((AbstractMethodDeclaration) referenceContext).binding;
				if (methodBinding != null) {
					buffer.append(methodBinding.computeUniqueKey(false/*not a leaf*/));
				}
			} else if (referenceContext instanceof TypeDeclaration) {
				TypeBinding typeBinding = ((TypeDeclaration) referenceContext).binding;
				if (typeBinding != null) {
					buffer.append(typeBinding.computeUniqueKey(false/*not a leaf*/));
				}
			}

			// scope index
			getScopeKey(scope, buffer);

			// find number of occurences of a variable with the same name in the scope
			LocalVariableBinding[] locals = scope.locals;
			for (int i = 0; i < scope.localIndex; i++) { // use linear search assuming the number of locals per scope is low
				LocalVariableBinding local = locals[i];
				if (CharOperation.equals(this.name, local.name)) {
					if (this == local)
						break;
					occurenceCount++;
				}
			}
		}
		// variable name
		buffer.append('#');
		buffer.append(this.name);

		// add occurence count to avoid same key for duplicate variables
		// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=149590)
		if (occurenceCount > 0) {
			buffer.append('#');
			buffer.append(occurenceCount);
		}

		int length = buffer.length();
		char[] uniqueKey = new char[length];
		buffer.getChars(0, length, uniqueKey, 0);
		return uniqueKey;
	}

	public AnnotationBinding[] getAnnotations() {
		if (this.declaringScope == null) {
			if ((this.tagBits & TagBits.AnnotationResolved) != 0) {
				// annotation are already resolved
				if (this.declaration == null) {
					return Binding.NO_ANNOTATIONS;
				}
				Annotation[] annotations = this.declaration.annotations;
				if (annotations != null) {
					int length = annotations.length;
					AnnotationBinding[] annotationBindings = new AnnotationBinding[length];
					for (int i = 0; i < length; i++) {
						AnnotationBinding compilerAnnotation = annotations[i].getCompilerAnnotation();
						if (compilerAnnotation == null) {
							return Binding.NO_ANNOTATIONS;
						}
						annotationBindings[i] = compilerAnnotation;
					}
					return annotationBindings;
				}
			}
			return Binding.NO_ANNOTATIONS;
		}
		SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
		if (sourceType == null)
			return Binding.NO_ANNOTATIONS;

		AnnotationBinding[] annotations = sourceType.retrieveAnnotations(this);
		if ((this.tagBits & TagBits.AnnotationResolved) == 0) {
			if (((this.tagBits & TagBits.IsArgument) != 0) && this.declaration != null) {
				Annotation[] annotationNodes = this.declaration.annotations;
				if (annotationNodes != null) {
					int length = annotationNodes.length;
					ASTNode.resolveAnnotations(this.declaringScope, annotationNodes, this);
					annotations = new AnnotationBinding[length];
					for (int i = 0; i < length; i++)
						annotations[i] = new AnnotationBinding(annotationNodes[i]);
					setAnnotations(annotations);
				}
			}
		}
		return annotations;
	}

	private void getScopeKey(BlockScope scope, StringBuffer buffer) {
		int scopeIndex = scope.scopeIndex();
		if (scopeIndex != -1) {
			getScopeKey((BlockScope)scope.parent, buffer);
			buffer.append('#');
			buffer.append(scopeIndex);
		}
	}

	// Answer whether the variable binding is a secret variable added for code gen purposes
	public boolean isSecret() {

		return this.declaration == null && (this.tagBits & TagBits.IsArgument) == 0;
	}

	public void recordInitializationEndPC(int pc) {

		if (this.initializationPCs[((this.initializationCount - 1) << 1) + 1] == -1)
			this.initializationPCs[((this.initializationCount - 1) << 1) + 1] = pc;
	}

	public void recordInitializationStartPC(int pc) {

		if (this.initializationPCs == null) {
			return;
		}
		if (this.initializationCount > 0) {
			int previousEndPC = this.initializationPCs[ ((this.initializationCount - 1) << 1) + 1];
			 // interval still open, keep using it (108180)
			if (previousEndPC == -1) {
				return;
			}
			// optimize cases where reopening a contiguous interval
			if (previousEndPC == pc) {
				this.initializationPCs[ ((this.initializationCount - 1) << 1) + 1] = -1; // reuse previous interval (its range will be augmented)
				return;
			}
		}
		int index = this.initializationCount << 1;
		if (index == this.initializationPCs.length) {
			System.arraycopy(this.initializationPCs, 0, (this.initializationPCs = new int[this.initializationCount << 2]), 0, index);
		}
		this.initializationPCs[index] = pc;
		this.initializationPCs[index + 1] = -1;
		this.initializationCount++;
	}

	public void setAnnotations(AnnotationBinding[] annotations) {
		if (this.declaringScope == null) return;

		SourceTypeBinding sourceType = this.declaringScope.enclosingSourceType();
		if (sourceType != null)
			sourceType.storeAnnotations(this, annotations);
	}

	public void resetInitializations() {
		this.initializationCount = 0;
		this.initializationPCs = null;
	}

	public String toString() {

		String s = super.toString();
		switch (this.useFlag){
			case USED:
				s += "[pos: " + String.valueOf(this.resolvedPosition) + "]"; //$NON-NLS-2$ //$NON-NLS-1$
				break;
			case UNUSED:
				s += "[pos: unused]"; //$NON-NLS-1$
				break;
			case FAKE_USED:
				s += "[pos: fake_used]"; //$NON-NLS-1$
				break;
		}
		s += "[id:" + String.valueOf(this.id) + "]"; //$NON-NLS-2$ //$NON-NLS-1$
		if (this.initializationCount > 0) {
			s += "[pc: "; //$NON-NLS-1$
			for (int i = 0; i < this.initializationCount; i++) {
				if (i > 0)
					s += ", "; //$NON-NLS-1$
				s += String.valueOf(this.initializationPCs[i << 1]) + "-" + ((this.initializationPCs[(i << 1) + 1] == -1) ? "?" : String.valueOf(this.initializationPCs[(i<< 1) + 1])); //$NON-NLS-2$ //$NON-NLS-1$
			}
			s += "]"; //$NON-NLS-1$
		}
		return s;
	}

	public boolean isParameter() {
		return ((this.tagBits & TagBits.IsArgument) != 0);
	}
}
