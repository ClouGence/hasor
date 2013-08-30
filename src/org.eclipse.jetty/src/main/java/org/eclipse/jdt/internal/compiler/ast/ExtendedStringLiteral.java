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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ExtendedStringLiteral extends StringLiteral {

	/**
	 *  Build a string+char literal
	 */
	public ExtendedStringLiteral(StringLiteral str, CharLiteral character) {

		super(str.source, str.sourceStart, str.sourceEnd, str.lineNumber);
		extendWith(character);
	}

	/**
	 * Build a two-strings literal
	 * */
	public ExtendedStringLiteral(StringLiteral str1, StringLiteral str2) {

		super(str1.source, str1.sourceStart, str1.sourceEnd, str1.lineNumber);
		extendWith(str2);
	}

	/**
	 * Add the lit source to mine, just as if it was mine
	 */
	public ExtendedStringLiteral extendWith(CharLiteral lit) {

		//update the source
		int length = this.source.length;
		System.arraycopy(this.source, 0, (this.source = new char[length + 1]), 0, length);
		this.source[length] = lit.value;
		//position at the end of all literals
		this.sourceEnd = lit.sourceEnd;
		return this;
	}

	/**
	 *  Add the lit source to mine, just as if it was mine
	 */
	public ExtendedStringLiteral extendWith(StringLiteral lit) {

		//uddate the source
		int length = this.source.length;
		System.arraycopy(
			this.source,
			0,
			this.source = new char[length + lit.source.length],
			0,
			length);
		System.arraycopy(lit.source, 0, this.source, length, lit.source.length);
		//position at the end of all literals
		this.sourceEnd = lit.sourceEnd;
		return this;
	}

	public StringBuffer printExpression(int indent, StringBuffer output) {

		return output.append("ExtendedStringLiteral{").append(this.source).append('}'); //$NON-NLS-1$
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {

		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
}
