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
package org.eclipse.jdt.internal.compiler.ast;

public abstract class  MagicLiteral extends Literal {

	public MagicLiteral(int start , int end) {

		super(start,end);
	}

	public boolean isValidJavaStatement(){

		return false ;
	}

	public char[] source() {

		return null;
	}
}
