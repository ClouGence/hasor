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
package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;

public class ImportConflictBinding extends ImportBinding {
public ReferenceBinding conflictingTypeBinding; // must ensure the import is resolved

public ImportConflictBinding(char[][] compoundName, Binding methodBinding, ReferenceBinding conflictingTypeBinding, ImportReference reference) {
	super(compoundName, false, methodBinding, reference);
	this.conflictingTypeBinding = conflictingTypeBinding;
}
public char[] readableName() {
	return CharOperation.concatWith(this.compoundName, '.');
}
public String toString() {
	return "method import : " + new String(readableName()); //$NON-NLS-1$
}
}
