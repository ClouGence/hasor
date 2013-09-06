/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.lookup;

/**
 * Pseudo method binding used to wrapper a real method, and expose less exceptions than original.
 * For other protocols, it should delegate to original method
 */
public class MostSpecificExceptionMethodBinding  extends MethodBinding {

	private MethodBinding originalMethod;
	
	public MostSpecificExceptionMethodBinding (MethodBinding originalMethod, ReferenceBinding[] mostSpecificExceptions) {
		super(
				originalMethod.modifiers, 
				originalMethod.selector, 
				originalMethod.returnType, 
				originalMethod.parameters, 
				mostSpecificExceptions, 
				originalMethod.declaringClass);
		this.originalMethod = originalMethod;
	}
	
	public MethodBinding original() {
		return this.originalMethod.original();
	}
}
