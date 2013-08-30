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
package org.eclipse.jdt.internal.compiler.impl;

/*
 * Implementors are valid compilation contexts from which we can
 * escape in case of error:
 *	For example: method, type or compilation unit.
 */

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public interface ReferenceContext {

	void abort(int abortLevel, CategorizedProblem problem);

	CompilationResult compilationResult();

	boolean hasErrors();

	void tagAsHavingErrors();
}
