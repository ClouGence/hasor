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

import java.io.IOException;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;

/*
 * Special unchecked exception type used
 * to abort from the compilation process
 *
 * should only be thrown from within problem handlers.
 */
public class AbortCompilationUnit extends AbortCompilation {

	private static final long serialVersionUID = -4253893529982226734L; // backward compatible

	public String encoding;

public AbortCompilationUnit(CompilationResult compilationResult, CategorizedProblem problem) {
	super(compilationResult, problem);
}

/**
 * Used to surface encoding issues when reading sources
 */
public AbortCompilationUnit(CompilationResult compilationResult, IOException exception, String encoding) {
	super(compilationResult, exception);
	this.encoding = encoding;
}
}
