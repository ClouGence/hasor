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
package org.eclipse.jdt.internal.compiler.codegen;

public abstract class Label {

	public CodeStream codeStream;
	public int position = POS_NOT_SET; // position=POS_NOT_SET Then it's pos is not set.

	public final static int POS_NOT_SET = -1;

public Label() {
	// for creating labels ahead of code generation
}

public Label(CodeStream codeStream) {
	this.codeStream = codeStream;
}

/*
* Place the label target position.
*/
public abstract void place();

}
