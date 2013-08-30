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
package org.eclipse.jdt.internal.compiler.env;

/**
 * This interface denotes a compilation unit, providing its name and content.
 */
public interface ICompilationUnit extends IDependent {
/**
 * Answer the contents of the compilation unit.
 *
 * In normal use, the contents are requested twice.
 * Once during the initial lite parsing step, then again for the
 * more detailed parsing step.
 * Implementors must never return null - return an empty char[] instead,
 * CharOperation.NO_CHAR being the candidate of choice.
 */
char[] getContents();
/**
 * Answer the name of the top level public type.
 * For example, {Hashtable}.
 */
char[] getMainTypeName();
/**
 * Answer the name of the package according to the directory structure
 * or null if package consistency checks should be ignored.
 * For example, {java, lang}.
 */
char[][] getPackageName();
}
