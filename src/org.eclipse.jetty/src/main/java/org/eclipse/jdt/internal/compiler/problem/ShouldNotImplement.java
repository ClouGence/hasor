/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.problem;

/*
 * Special unchecked exception type used
 * to denote implementation that should never be reached.
 *
 *	(internal only)
 */
public class ShouldNotImplement extends RuntimeException {
	private static final long serialVersionUID = 2669970476264283736L; // backward compatible
	public ShouldNotImplement(String message) {
		super(message);
	}
}
