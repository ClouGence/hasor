/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

public class AptSourceLocalVariableBinding extends LocalVariableBinding {

	// enclosing element
	public MethodBinding methodBinding;
	
	public AptSourceLocalVariableBinding(LocalVariableBinding localVariableBinding, MethodBinding methodBinding) {
		super(localVariableBinding.name, localVariableBinding.type, localVariableBinding.modifiers, true);
		this.constant = localVariableBinding.constant;
		this.declaration = localVariableBinding.declaration;
		this.declaringScope = localVariableBinding.declaringScope;
		this.id = localVariableBinding.id;
		this.resolvedPosition = localVariableBinding.resolvedPosition;
		this.tagBits = localVariableBinding.tagBits;
		this.useFlag = localVariableBinding.useFlag;
		this.initializationCount = localVariableBinding.initializationCount;
		this.initializationPCs = localVariableBinding.initializationPCs;
		this.methodBinding = methodBinding;
	}
}
