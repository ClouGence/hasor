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

package org.eclipse.jdt.core.compiler;

/**
 * Exception thrown by a scanner when encountering lexical errors.
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 */
public class InvalidInputException extends Exception {

	private static final long serialVersionUID = 2909732853499731592L; // backward compatible

/**
 * Creates a new exception with no detail message.
 */
public InvalidInputException() {
	super();
}

/**
 * Creates a new exception with the given detail message.
 * @param message the detail message
 */
public InvalidInputException(String message) {
	super(message);
}
}
