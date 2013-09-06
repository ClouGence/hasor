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

public class ImportBinding extends Binding {
	public char[][] compoundName;
	public boolean onDemand;
	public ImportReference reference;

	public Binding resolvedImport; // must ensure the import is resolved

public ImportBinding(char[][] compoundName, boolean isOnDemand, Binding binding, ImportReference reference) {
	this.compoundName = compoundName;
	this.onDemand = isOnDemand;
	this.resolvedImport = binding;
	this.reference = reference;
}
/* API
* Answer the receiver's binding type from Binding.BindingID.
*/

public final int kind() {
	return IMPORT;
}
public boolean isStatic() {
	return this.reference != null && this.reference.isStatic();
}
public char[] readableName() {
	if (this.onDemand)
		return CharOperation.concat(CharOperation.concatWith(this.compoundName, '.'), ".*".toCharArray()); //$NON-NLS-1$
	else
		return CharOperation.concatWith(this.compoundName, '.');
}
public String toString() {
	return "import : " + new String(readableName()); //$NON-NLS-1$
}
}
