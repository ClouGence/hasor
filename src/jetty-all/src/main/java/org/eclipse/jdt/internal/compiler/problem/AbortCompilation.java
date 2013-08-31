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

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.util.Util;

/*
 * Special unchecked exception type used
 * to abort from the compilation process
 *
 * should only be thrown from within problem handlers.
 */
public class AbortCompilation extends RuntimeException {

	public CompilationResult compilationResult;
	public Throwable exception;
	public CategorizedProblem problem;

	/* special fields used to abort silently (e.g. when canceling build process) */
	public boolean isSilent;
	public RuntimeException silentException;

	private static final long serialVersionUID = -2047226595083244852L; // backward compatible

	public AbortCompilation() {
		// empty
	}

	public AbortCompilation(CompilationResult compilationResult, CategorizedProblem problem) {
		this();
		this.compilationResult = compilationResult;
		this.problem = problem;
	}

	public AbortCompilation(CompilationResult compilationResult, Throwable exception) {
		this();
		this.compilationResult = compilationResult;
		this.exception = exception;
	}

	public AbortCompilation(boolean isSilent, RuntimeException silentException) {
		this();
		this.isSilent = isSilent;
		this.silentException = silentException;
	}
	public String getMessage() {
		String message = super.getMessage();
		StringBuffer buffer = new StringBuffer(message == null ? Util.EMPTY_STRING : message);
		if (this.problem != null) {
			buffer.append(this.problem);
		} else if (this.exception != null) {
			message = this.exception.getMessage();
			buffer.append(message == null ? Util.EMPTY_STRING : message);
		} else if (this.silentException != null) {
			message = this.silentException.getMessage();
			buffer.append(message == null ? Util.EMPTY_STRING : message);
		}
		return String.valueOf(buffer);
	}
	public void updateContext(InvocationSite invocationSite, CompilationResult unitResult) {
		if (this.problem == null) return;
		if (this.problem.getSourceStart() != 0 || this.problem.getSourceEnd() != 0) return;
		this.problem.setSourceStart(invocationSite.sourceStart());
		this.problem.setSourceEnd(invocationSite.sourceEnd());
		int[] lineEnds = unitResult.getLineSeparatorPositions();
		this.problem.setSourceLineNumber(Util.getLineNumber(invocationSite.sourceStart(), lineEnds, 0, lineEnds.length-1));
		this.compilationResult = unitResult;
	}

	public void updateContext(ASTNode astNode, CompilationResult unitResult) {
		if (this.problem == null) return;
		if (this.problem.getSourceStart() != 0 || this.problem.getSourceEnd() != 0) return;
		this.problem.setSourceStart(astNode.sourceStart());
		this.problem.setSourceEnd(astNode.sourceEnd());
		int[] lineEnds = unitResult.getLineSeparatorPositions();
		this.problem.setSourceLineNumber(Util.getLineNumber(astNode.sourceStart(), lineEnds, 0, lineEnds.length-1));
		this.compilationResult = unitResult;
	}

	public String getKey() {
		StringBuffer buffer = new StringBuffer();
		if (this.problem != null) {
			buffer.append(this.problem);
		}
		return String.valueOf(buffer);
	}
}
