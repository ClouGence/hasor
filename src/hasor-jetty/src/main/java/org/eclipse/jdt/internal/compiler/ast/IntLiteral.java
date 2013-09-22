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

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.parser.ScannerHelper;

public class IntLiteral extends NumberLiteral {
	private static final char[] HEXA_MIN_VALUE        = "0x80000000".toCharArray(); //$NON-NLS-1$
	private static final char[] HEXA_MINUS_ONE_VALUE  = "0xffffffff".toCharArray(); //$NON-NLS-1$
	private static final char[] OCTAL_MIN_VALUE       = "020000000000".toCharArray(); //$NON-NLS-1$
	private static final char[] OCTAL_MINUS_ONE_VALUE = "037777777777".toCharArray(); //$NON-NLS-1$
	private static final char[] DECIMAL_MIN_VALUE     = "2147483648".toCharArray(); //$NON-NLS-1$
	private static final char[] DECIMAL_MAX_VALUE     = "2147483647".toCharArray(); //$NON-NLS-1$

	private char[] reducedForm; // no underscores

	public int value;

	//used for ++ and --
	public static final IntLiteral One = new IntLiteral(new char[]{'1'}, null, 0, 0, 1, IntConstant.fromValue(1));

	public static IntLiteral buildIntLiteral(char[] token, int s, int e) {
		// remove '_' and prefix '0' first
		char[] intReducedToken = removePrefixZerosAndUnderscores(token, false);
		switch(intReducedToken.length) {
			case 10 :
				// 0x80000000
				if (CharOperation.equals(intReducedToken, HEXA_MIN_VALUE)) {
					return new IntLiteralMinValue(token, intReducedToken != token ? intReducedToken : null, s, e);
				}
				break;
			case 12 :
				// 020000000000
				if (CharOperation.equals(intReducedToken, OCTAL_MIN_VALUE)) {
					return new IntLiteralMinValue(token, intReducedToken != token ? intReducedToken : null, s, e);
				}
				break;
		}
		return new IntLiteral(token, intReducedToken != token ? intReducedToken : null, s, e);
	}
IntLiteral(char[] token, char[] reducedForm, int start, int end) {
	super(token, start, end);
	this.reducedForm = reducedForm;
}
IntLiteral(char[] token, char[] reducedForm, int start, int end, int value, Constant constant) {
	super(token, start, end);
	this.reducedForm = reducedForm;
	this.value = value;
	this.constant = constant;
}
public void computeConstant() {
	char[] token = this.reducedForm != null ? this.reducedForm : this.source;
	int tokenLength = token.length;
	int radix = 10;
	int j = 0;
	if (token[0] == '0') {
		if (tokenLength == 1) {
			this.constant = IntConstant.fromValue(0);
			return;
		}
		if ((token[1] == 'x') || (token[1] == 'X')) {
			radix = 16;
			j = 2;
		} else if ((token[1] == 'b') || (token[1] == 'B')) {
			radix = 2;
			j = 2;
		} else {
			radix = 8;
			j = 1;
		}
	}
	switch(radix) {
		case 2 :
			if ((tokenLength - 2) > 32) {
				// remove 0b or 0B
				return; /*constant stays null*/
			}
			computeValue(token, tokenLength, radix, j);
			return;
		case 16 :
			if (tokenLength <= 10) {
				if (CharOperation.equals(token, HEXA_MINUS_ONE_VALUE)) {
					this.constant = IntConstant.fromValue(-1);
					return;
				}
				computeValue(token, tokenLength, radix, j);
				return;
			}
			break;
		case 10 :
			if (tokenLength > DECIMAL_MAX_VALUE.length
					|| (tokenLength == DECIMAL_MAX_VALUE.length
							&& CharOperation.compareTo(token, DECIMAL_MAX_VALUE) > 0)) {
				return; /*constant stays null*/
			}
			computeValue(token, tokenLength, radix, j);
			break;
		case 8 :
			if (tokenLength <= 12) {
				if (tokenLength == 12 && token[j] > '4') {
					return; /*constant stays null*/
				}
				if (CharOperation.equals(token, OCTAL_MINUS_ONE_VALUE)) {
					this.constant = IntConstant.fromValue(-1);
					return;
				}
				computeValue(token, tokenLength, radix, j);
				return;
			}
			break;
	}
}
private void computeValue(char[] token, int tokenLength, int radix, int j) {
	int digitValue;
	int computedValue = 0;
	while (j < tokenLength) {
		if ((digitValue = ScannerHelper.digit(token[j++],radix)) < 0) {
			return; /*constant stays null*/
		}
		computedValue = (computedValue * radix) + digitValue ;
	}
	this.constant = IntConstant.fromValue(computedValue);
}
public IntLiteral convertToMinValue() {
	if (((this.bits & ASTNode.ParenthesizedMASK) >> ASTNode.ParenthesizedSHIFT) != 0) {
		return this;
	}
	char[] token = this.reducedForm != null ? this.reducedForm : this.source;
	switch(token.length) {
		case 10 :
			// 2147483648
			if (CharOperation.equals(token, DECIMAL_MIN_VALUE)) {
				return new IntLiteralMinValue(this.source, this.reducedForm, this.sourceStart, this.sourceEnd);
			}
			break;
	}
	return this;
}
/**
 * Code generation for long literal
 *
 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
 * @param valueRequired boolean
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
	int pc = codeStream.position;
	if (valueRequired) {
		codeStream.generateConstant(this.constant, this.implicitConversion);
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

public TypeBinding literalType(BlockScope scope) {
	return TypeBinding.INT;
}
public void traverse(ASTVisitor visitor, BlockScope scope) {
	visitor.visit(this, scope);
	visitor.endVisit(this, scope);
}
}
