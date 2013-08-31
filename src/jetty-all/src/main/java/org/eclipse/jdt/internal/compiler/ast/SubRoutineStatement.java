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
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

/**
 * Extra behavior for statements which are generating subroutines
 */
public abstract class SubRoutineStatement extends Statement {

	public static void reenterAllExceptionHandlers(SubRoutineStatement[] subroutines, int max, CodeStream codeStream) {
		if (subroutines == null) return;
		if (max < 0) max = subroutines.length;
		for (int i = 0; i < max; i++) {
			SubRoutineStatement sub = subroutines[i];
			sub.enterAnyExceptionHandler(codeStream);
			sub.enterDeclaredExceptionHandlers(codeStream);
		}
	}

	ExceptionLabel anyExceptionLabel;

	public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream) {

		if (this.anyExceptionLabel == null) {
			this.anyExceptionLabel = new ExceptionLabel(codeStream, null /*any exception*/);
		}
		this.anyExceptionLabel.placeStart();
		return this.anyExceptionLabel;
	}

	public void enterDeclaredExceptionHandlers(CodeStream codeStream) {
		// do nothing by default
	}

	public void exitAnyExceptionHandler() {
		if (this.anyExceptionLabel != null) {
			this.anyExceptionLabel.placeEnd();
		}
	}

	public void exitDeclaredExceptionHandlers(CodeStream codeStream) {
		// do nothing by default
	}


	/**
	 * Generate an invocation of a subroutine (e.g. jsr finally) in current context.
	 * @param currentScope
	 * @param codeStream
	 * @param targetLocation
	 * @param stateIndex
	 * @param secretLocal
	 * @return boolean, <code>true</code> if the generated code will abrupt completion
	 */
	public abstract boolean generateSubRoutineInvocation(BlockScope currentScope, CodeStream codeStream, Object targetLocation, int stateIndex, LocalVariableBinding secretLocal);

	public abstract boolean isSubRoutineEscaping();

	public void placeAllAnyExceptionHandler() {
		this.anyExceptionLabel.place();
	}
}
