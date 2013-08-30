/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

public abstract class NumberLiteral extends Literal {

	char[] source;

	public NumberLiteral(char[] token, int s, int e) {
		this(s,e) ;
		this.source = token ;
	}

	public NumberLiteral(int s, int e) {
		super (s,e) ;
	}

	public boolean isValidJavaStatement(){
		return false ;
	}

	public char[] source(){
		return this.source;
	}
	protected static char[] removePrefixZerosAndUnderscores(char[] token, boolean isLong) {
		int max = token.length;
		int start = 0;
		int end = max - 1;
		if (isLong) {
			end--; // remove the 'L' or 'l'
		}
		if (max > 1 && token[0] == '0') {
			if (max > 2 && (token[1] == 'x' || token[1] == 'X')) {
				start = 2;
			} else if (max > 2 && (token[1] == 'b' || token[1] == 'B')) {
				start = 2;
			} else {
				start = 1;
			}
		}
		boolean modified = false;
		boolean ignore = true;
		loop: for (int i = start; i < max; i++) {
			char currentChar = token[i];
			switch(currentChar) {
				case '0' :
					// this is a prefix '0'
					if (ignore && !modified && (i < end)) {
						modified = true;
					}
					break;
				case '_' :
					modified = true;
					break loop;
				default :
					ignore = false;
			}
		}
		if (!modified) {
			return token;
		}
		ignore = true;
		StringBuffer buffer = new StringBuffer();
		buffer.append(token, 0, start);
		loop: for (int i = start; i < max; i++) {
			char currentChar = token[i];
			switch(currentChar) {
				case '0' :
					if (ignore && (i < end)) {
						// this is a prefix '0'
						continue loop;
					}
					break;
				case '_' :
					continue loop;
				default:
					ignore = false;
			}
			buffer.append(currentChar);
		}
		return buffer.toString().toCharArray();
	}
}
