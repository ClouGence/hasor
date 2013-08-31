/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.batch;

import java.io.File;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

public abstract class ClasspathLocation implements FileSystem.Classpath,
		SuffixConstants {

	public static final int SOURCE = 1;
	public static final int BINARY = 2;

	String path;
	char[] normalizedPath;
	public AccessRuleSet accessRuleSet;

	public String destinationPath;
		// destination path for compilation units that are reached through this
		// classpath location; the coding is consistent with the one of
		// Main.destinationPath:
		// == null: unspecified, use whatever value is set by the enclosing
		//          context, id est Main;
		// == Main.NONE: absorbent element, do not output class files;
		// else: use as the path of the directory into which class files must
		//       be written.
		// potentially carried by any entry that contains to be compiled files

	protected ClasspathLocation(AccessRuleSet accessRuleSet,
			String destinationPath) {
		this.accessRuleSet = accessRuleSet;
		this.destinationPath = destinationPath;
	}

	/**
	 * Return the first access rule which is violated when accessing a given
	 * type, or null if no 'non accessible' access rule applies.
	 *
	 * @param qualifiedBinaryFileName
	 *            tested type specification, formed as:
	 *            "org/eclipse/jdt/core/JavaCore.class"; on systems that
	 *            use \ as File.separator, the
	 *            "org\eclipse\jdt\core\JavaCore.class" is accepted as well
	 * @return the first access rule which is violated when accessing a given
	 *         type, or null if none applies
	 */
	protected AccessRestriction fetchAccessRestriction(String qualifiedBinaryFileName) {
		if (this.accessRuleSet == null)
			return null;
		char [] qualifiedTypeName = qualifiedBinaryFileName.
			substring(0, qualifiedBinaryFileName.length() - SUFFIX_CLASS.length)
			.toCharArray();
		if (File.separatorChar == '\\') {
			CharOperation.replace(qualifiedTypeName, File.separatorChar, '/');
		}
		return this.accessRuleSet.getViolatedRestriction(qualifiedTypeName);
	}
	
	public int getMode() {
		return SOURCE | BINARY;
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getMode();
		result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClasspathLocation other = (ClasspathLocation) obj;
		String localPath = this.getPath();
		String otherPath = other.getPath();
		if (localPath == null) {
			if (otherPath != null)
				return false;
		} else if (!localPath.equals(otherPath))
			return false;
		if (this.getMode() != other.getMode())
			return false;
		return true;
	}
	public String getPath() {
		return this.path;
	}
}
