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
package org.eclipse.jdt.internal.compiler;

import java.util.Locale;

import org.eclipse.jdt.core.compiler.CategorizedProblem;

/*
 * Factory used from inside the compiler to build the actual problems
 * which are handed back in the compilation result.
 *
 * This allows sharing the internal problem representation with the environment.
 *
 * Note: The factory is responsible for computing and storing a localized error message.
 */

public interface IProblemFactory {
	CategorizedProblem createProblem(
			char[] originatingFileName,
			int problemId,
			String[] problemArguments,
			String[] messageArguments, // shorter versions of the problemArguments
			int severity,
			int startPosition,
			int endPosition,
			int lineNumber,
			int columnNumber);

	/**
	 * Answer a new IProblem created according to the parameters values.
	 * @param originatingFileName the name of the file from which the problem is originated
	 * @param problemId the problem id
	 * @param problemArguments the fully qualified arguments recorded inside the problem
	 * @param elaborationId the message elaboration id (0 for problems that have no message elaboration)
	 * @param messageArguments the arguments needed to set the error message (shorter names than problemArguments ones)
	 * @param severity the severity of the problem
	 * @param startPosition the start position of the problem
	 * @param endPosition the end position of the problem
	 * @param lineNumber the line on which the problem occurred
	 * @return a new IProblem created according to the parameters values.
	 */
	CategorizedProblem createProblem(
		char[] originatingFileName,
		int problemId,
		String[] problemArguments,
		int elaborationId,
		String[] messageArguments, // shorter versions of the problemArguments
		int severity,
		int startPosition,
		int endPosition,
		int lineNumber,
		int columnNumber);

	Locale getLocale();

	String getLocalizedMessage(int problemId, String[] messageArguments);

	/**
	 * Inject the supplied message arguments into a localized template
	 * elaborated from the supplied problem id and an optional elaboration id
	 * and return the resulting message. The arguments number should match the
	 * highest placeholder index in the template. When an elaboration id is
	 * used, the template matching that elaboration id replaces '{0}' into the
	 * template matching the problem id before the message arguments are
	 * injected.
	 * @param problemId the problem id taken from
	 *        {@link org.eclipse.jdt.core.compiler.IProblem} constants
	 * @param elaborationId 0 if the considered problem has no elaboration, a
	 *        valid elaboration id else
	 * @param messageArguments the arguments to inject into the template
	 * @return a localized message elaborated from the supplied problem id,
	 *         elaboration id and message parameters
	 */
	String getLocalizedMessage(int problemId, int elaborationId, String[] messageArguments);
}
