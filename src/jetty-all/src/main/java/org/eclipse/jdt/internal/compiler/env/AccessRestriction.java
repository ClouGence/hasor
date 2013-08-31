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

public class AccessRestriction {

	private AccessRule accessRule;
	public byte classpathEntryType;
	public static final byte
		COMMAND_LINE = 0,
		PROJECT = 1,
		LIBRARY = 2;
	public String classpathEntryName;

	public AccessRestriction(AccessRule accessRule, byte classpathEntryType, String classpathEntryName) {
		this.accessRule = accessRule;
		this.classpathEntryName = classpathEntryName;
		this.classpathEntryType = classpathEntryType;
	}

	public int getProblemId() {
		return this.accessRule.getProblemId();
	}

	public boolean ignoreIfBetter() {
		return this.accessRule.ignoreIfBetter();
	}
}
