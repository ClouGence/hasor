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

public interface IDebugRequestor {

	/*
	 * Debug callback method allowing to take into account a new compilation result.
	 * Any side-effect performed on the actual result might interfere with the
	 * original compiler requestor, and should be prohibited.
	 */
	void acceptDebugResult(CompilationResult result);

	/*
	 * Answers true when in active mode
	 */
	boolean isActive();

	/*
	 * Activate debug callbacks
	 */
	void activate();

	/*
	 * Deactivate debug callbacks
	 */
	void deactivate();

	/*
	 * Reset debug requestor after compilation has finished
	 */
	void reset();
}

