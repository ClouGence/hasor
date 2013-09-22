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
 * This represents the target file of a type dependency.
 *
 * All implementors of this interface are containers for types or types
 * themselves which must be able to identify their source file name
 * when file dependencies are collected.
 */
public interface IDependent {
	char JAR_FILE_ENTRY_SEPARATOR = '|';
/**
 * Answer the file name which defines the type.
 *
 * The path part (optional) must be separated from the actual
 * file proper name by a separator suitable for the type (java.io.File.separator for example),
 * e.g.
 *  "c:\\source\\com\\p\\X.java" or
 *  "/com/p/Y.java".
 *
 * The path to the zip or jar file (optional) must be separated
 * from the actual path part by JAR_FILE_ENTRY_SEPARATOR,
 * e.g.
 *  "c:\\lib\\some.jar|/com/p/X.class" or
 *  "/lib/some.zip|/com/q/Y.class".
 *
 * The proper file name includes the suffix extension (e.g.&nbsp;".java")
 * e.g.&nbsp;"c:/org/eclipse/jdt/internal/compileri/env/IDependent.java"
 *
 * Return null if no file defines the type.
 */

char[] getFileName();
}
