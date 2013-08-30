/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;

public class ClassFilePool {
	public static final int POOL_SIZE = 25; // need to have enough for 2 units
	ClassFile[] classFiles;

private ClassFilePool() {
	// prevent instantiation
	this.classFiles = new ClassFile[POOL_SIZE];
}

public static ClassFilePool newInstance() {
	return new ClassFilePool();
}

public synchronized ClassFile acquire(SourceTypeBinding typeBinding) {
	for (int i = 0; i < POOL_SIZE; i++) {
		ClassFile classFile = this.classFiles[i];
		if (classFile == null) {
			ClassFile newClassFile = new ClassFile(typeBinding);
			this.classFiles[i] = newClassFile;
			newClassFile.isShared = true;
			return newClassFile;
		}
		if (!classFile.isShared) {
			classFile.reset(typeBinding);
			classFile.isShared = true;
			return classFile;
		}
	}
	return new ClassFile(typeBinding);
}
public synchronized void release(ClassFile classFile) {
	classFile.isShared = false;
}
public void reset() {
	Arrays.fill(this.classFiles, null);
}
}
