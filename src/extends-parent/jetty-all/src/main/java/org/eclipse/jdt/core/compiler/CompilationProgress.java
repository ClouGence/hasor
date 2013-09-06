/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.compiler;

import org.eclipse.jdt.core.compiler.batch.BatchCompiler;

/**
 * A compilation progress is used by the {@link BatchCompiler} to report progress during compilation.
 * It is also used to request cancellation of the compilation.
 * Clients of the {@link BatchCompiler} should subclass this class, instantiate the subclass and pass this instance to
 * {@link BatchCompiler#compile(String, java.io.PrintWriter, java.io.PrintWriter, CompilationProgress)}.
 * <p>
 * This class is intended to be instantiated and subclassed by clients.
 * </p>
 *
 * @since 3.4
 */

public abstract class CompilationProgress {

	/**
	 * Notifies that the compilation is beginning. This is called exactly once per batch compilation.
	 * An estimated amount of remaining work is given. This amount will change as the compilation
	 * progresses. The new estimated amount of remaining work is reported using {@link #worked(int, int)}.
	 * <p>
	 * Clients should not call this method.
	 * </p>
	 *
	 * @param remainingWork the estimated amount of remaining work.
	 */
	public abstract void begin(int remainingWork);

	/**
	 * Notifies that the work is done; that is, either the compilation is completed
	 * or a cancellation was requested. This is called exactly once per batch compilation.
	 * <p>
	 * Clients should not call this method.
	 * </p>
	 */
	public abstract void done();

	/**
	 * Returns whether cancellation of the compilation has been requested.
	 *
	 * @return <code>true</code> if cancellation has been requested,
	 *    and <code>false</code> otherwise
	 */
	public abstract boolean isCanceled();

	/**
	 * Reports the name (or description) of the current task.
	 * <p>
	 * Clients should not call this method.
	 * </p>
	 *
	 * @param name the name (or description) of the current task
	 */
	public abstract void setTaskName(String name);


	/**
	 * Notifies that a given amount of work of the compilation
	 * has been completed. Note that this amount represents an
	 * installment, as opposed to a cumulative amount of work done
	 * to date.
	 * Also notifies an estimated amount of remaining work. Note that this
	 * amount of remaining work  may be greater than the previous estimated
	 * amount as new compilation units are injected in the compile loop.
	 * <p>
	 * Clients should not call this method.
	 * </p>
	 *
	 * @param workIncrement a non-negative amount of work just completed
	 * @param remainingWork  a non-negative amount of estimated remaining work
	 */
	public abstract void worked(int workIncrement, int remainingWork);


}
