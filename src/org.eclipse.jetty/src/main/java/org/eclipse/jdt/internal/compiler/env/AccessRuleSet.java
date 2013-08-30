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
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.IProblem;

/**
 * Definition of a set of access rules used to flag forbidden references to non API code.
 */
public class AccessRuleSet {

	private AccessRule[] accessRules;
	public byte classpathEntryType; // one of AccessRestriction#COMMAND_LINE, LIBRARY, PROJECT
	public String classpathEntryName;

/**
 * Make a new set of access rules.
 * @param accessRules the access rules to be contained by the new set
 * @param classpathEntryType one of {@link AccessRestriction#COMMAND_LINE},
 *        {@link AccessRestriction#LIBRARY}, {@link AccessRestriction#PROJECT}
 *        that tells the access restrictions how to render the classpath entry
 * @param classpathEntryName a user-readable name for the classpath entry
 */
public AccessRuleSet(AccessRule[] accessRules, byte classpathEntryType, String classpathEntryName) {
	this.accessRules = accessRules;
	this.classpathEntryType = classpathEntryType;
	this.classpathEntryName = classpathEntryName;
}

/**
 * @see java.lang.Object#equals(java.lang.Object)
 */
public boolean equals(Object object) {
	if (this == object)
		return true;
	if (!(object instanceof AccessRuleSet))
		return false;
	AccessRuleSet otherRuleSet = (AccessRuleSet) object;
	if (this.classpathEntryType != otherRuleSet.classpathEntryType ||
			this.classpathEntryName == null && otherRuleSet.classpathEntryName != null ||
			! this.classpathEntryName.equals(otherRuleSet.classpathEntryName)) {
		return false;
	}
	int rulesLength = this.accessRules.length;
	if (rulesLength != otherRuleSet.accessRules.length) return false;
	for (int i = 0; i < rulesLength; i++)
		if (!this.accessRules[i].equals(otherRuleSet.accessRules[i]))
			return false;
	return true;
}

public AccessRule[] getAccessRules() {
	return this.accessRules;
}

/**
 * Select the first access rule which is violated when accessing a given type,
 * or null if no 'non accessible' access rule applies.
 * @param targetTypeFilePath the target type file path, formed as:
 * "org/eclipse/jdt/core/JavaCore"
 * @return the first access restriction that applies if any, null else
 */
public AccessRestriction getViolatedRestriction(char[] targetTypeFilePath) {
	for (int i = 0, length = this.accessRules.length; i < length; i++) {
		AccessRule accessRule = this.accessRules[i];
		if (CharOperation.pathMatch(accessRule.pattern, targetTypeFilePath,
				true/*case sensitive*/, '/')) {
			switch (accessRule.getProblemId()) {
				case IProblem.ForbiddenReference:
				case IProblem.DiscouragedReference:
					return new AccessRestriction(accessRule, this.classpathEntryType, this.classpathEntryName);
				default:
					return null;
			}
		}
	}
	return null;
}

public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + hashCode(this.accessRules);
	result = prime * result + ((this.classpathEntryName == null) ? 0 : this.classpathEntryName.hashCode());
	result = prime * result + this.classpathEntryType;
	return result;
}

private int hashCode(AccessRule[] rules) {
	final int prime = 31;
	if (rules == null)
		return 0;
	int result = 1;
	for (int i = 0, length = rules.length; i < length; i++) {
		result = prime * result + (rules[i] == null ? 0 : rules[i].hashCode());
	}
	return result;
}

public String toString() {
	return toString(true/*wrap lines*/);
}

public String toString(boolean wrap) {
	StringBuffer buffer = new StringBuffer(200);
	buffer.append("AccessRuleSet {"); //$NON-NLS-1$
	if (wrap)
		buffer.append('\n');
	for (int i = 0, length = this.accessRules.length; i < length; i++) {
		if (wrap)
			buffer.append('\t');
		AccessRule accessRule = this.accessRules[i];
		buffer.append(accessRule);
		if (wrap)
			buffer.append('\n');
		else if (i < length-1)
			buffer.append(", "); //$NON-NLS-1$
	}
	buffer.append("} [classpath entry: "); //$NON-NLS-1$
	buffer.append(this.classpathEntryName);
	buffer.append("]"); //$NON-NLS-1$
	return buffer.toString();
}

}
