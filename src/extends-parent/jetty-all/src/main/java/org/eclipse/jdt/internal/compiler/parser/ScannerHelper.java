/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     David Foerster - patch for toUpperCase as described in https://bugs.eclipse.org/bugs/show_bug.cgi?id=153125
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class ScannerHelper {

	public final static long[] Bits = {
		ASTNode.Bit1, ASTNode.Bit2, ASTNode.Bit3, ASTNode.Bit4, ASTNode.Bit5, ASTNode.Bit6,
		ASTNode.Bit7, ASTNode.Bit8, ASTNode.Bit9, ASTNode.Bit10, ASTNode.Bit11, ASTNode.Bit12,
		ASTNode.Bit13, ASTNode.Bit14, ASTNode.Bit15, ASTNode.Bit16, ASTNode.Bit17, ASTNode.Bit18,
		ASTNode.Bit19, ASTNode.Bit20, ASTNode.Bit21, ASTNode.Bit22, ASTNode.Bit23, ASTNode.Bit24,
		ASTNode.Bit25, ASTNode.Bit26, ASTNode.Bit27, ASTNode.Bit28, ASTNode.Bit29, ASTNode.Bit30,
		ASTNode.Bit31, ASTNode.Bit32, ASTNode.Bit33L, ASTNode.Bit34L, ASTNode.Bit35L, ASTNode.Bit36L,
		ASTNode.Bit37L, ASTNode.Bit38L, ASTNode.Bit39L, ASTNode.Bit40L, ASTNode.Bit41L, ASTNode.Bit42L,
		ASTNode.Bit43L, ASTNode.Bit44L, ASTNode.Bit45L, ASTNode.Bit46L, ASTNode.Bit47L, ASTNode.Bit48L,
		ASTNode.Bit49L, ASTNode.Bit50L, ASTNode.Bit51L, ASTNode.Bit52L, ASTNode.Bit53L, ASTNode.Bit54L,
		ASTNode.Bit55L, ASTNode.Bit56L, ASTNode.Bit57L, ASTNode.Bit58L, ASTNode.Bit59L, ASTNode.Bit60L,
		ASTNode.Bit61L, ASTNode.Bit62L, ASTNode.Bit63L, ASTNode.Bit64L,
	};

	private static final int START_INDEX = 0;
	private static final int PART_INDEX = 1;

	private static long[][][] Tables;
	private static long[][][] Tables7;

	public final static int MAX_OBVIOUS = 128;
	public final static int[] OBVIOUS_IDENT_CHAR_NATURES = new int[MAX_OBVIOUS];

	public final static int C_JLS_SPACE = ASTNode.Bit9;
	public final static int C_SPECIAL = ASTNode.Bit8;
	public final static int C_IDENT_START = ASTNode.Bit7;
	public final static int C_UPPER_LETTER = ASTNode.Bit6;
	public final static int C_LOWER_LETTER = ASTNode.Bit5;
	public final static int C_IDENT_PART = ASTNode.Bit4;
	public final static int C_DIGIT = ASTNode.Bit3;
	public final static int C_SEPARATOR = ASTNode.Bit2;
	public final static int C_SPACE = ASTNode.Bit1;

	static {
		OBVIOUS_IDENT_CHAR_NATURES[0] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[1] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[2] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[3] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[4] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[5] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[6] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[7] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[8] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[14] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[15] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[16] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[17] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[18] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[19] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[20] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[21] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[22] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[23] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[24] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[25] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[26] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[27] = C_IDENT_PART;
		OBVIOUS_IDENT_CHAR_NATURES[127] = C_IDENT_PART;

		for (int i = '0'; i <= '9'; i++)
			OBVIOUS_IDENT_CHAR_NATURES[i] = C_DIGIT | C_IDENT_PART;

		for (int i = 'a'; i <= 'z'; i++)
			OBVIOUS_IDENT_CHAR_NATURES[i] = C_LOWER_LETTER | C_IDENT_PART | C_IDENT_START;
		for (int i = 'A'; i <= 'Z'; i++)
			OBVIOUS_IDENT_CHAR_NATURES[i] = C_UPPER_LETTER | C_IDENT_PART | C_IDENT_START;

		OBVIOUS_IDENT_CHAR_NATURES['_'] = C_SPECIAL | C_IDENT_PART | C_IDENT_START;
		OBVIOUS_IDENT_CHAR_NATURES['$'] = C_SPECIAL | C_IDENT_PART | C_IDENT_START;

		OBVIOUS_IDENT_CHAR_NATURES[9] = C_SPACE | C_JLS_SPACE; // \ u0009: HORIZONTAL TABULATION
		OBVIOUS_IDENT_CHAR_NATURES[10] = C_SPACE | C_JLS_SPACE; // \ u000a: LINE FEED
		OBVIOUS_IDENT_CHAR_NATURES[11] = C_SPACE;
		OBVIOUS_IDENT_CHAR_NATURES[12] = C_SPACE | C_JLS_SPACE; // \ u000c: FORM FEED
		OBVIOUS_IDENT_CHAR_NATURES[13] = C_SPACE | C_JLS_SPACE; //  \ u000d: CARRIAGE RETURN
		OBVIOUS_IDENT_CHAR_NATURES[28] = C_SPACE;
		OBVIOUS_IDENT_CHAR_NATURES[29] = C_SPACE;
		OBVIOUS_IDENT_CHAR_NATURES[30] = C_SPACE;
		OBVIOUS_IDENT_CHAR_NATURES[31] = C_SPACE;
		OBVIOUS_IDENT_CHAR_NATURES[32] = C_SPACE | C_JLS_SPACE; //  \ u0020: SPACE

		OBVIOUS_IDENT_CHAR_NATURES['.'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES[':'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES[';'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES[','] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['['] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES[']'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['('] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES[')'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['{'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['}'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['+'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['-'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['*'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['/'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['='] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['&'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['|'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['?'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['<'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['>'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['!'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['%'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['^'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['~'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['"'] = C_SEPARATOR;
		OBVIOUS_IDENT_CHAR_NATURES['\''] = C_SEPARATOR;
	}

static void initializeTable() {
	Tables = new long[2][][];
	Tables[START_INDEX] = new long[3][];
	Tables[PART_INDEX] = new long[4][];
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/start0.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[START_INDEX][0] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/start1.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[START_INDEX][1] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/start2.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[START_INDEX][2] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/part0.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[PART_INDEX][0] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/part1.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[PART_INDEX][1] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/part2.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[PART_INDEX][2] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode/part14.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables[PART_INDEX][3] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
static void initializeTable17() {
	Tables7 = new long[2][][];
	Tables7[START_INDEX] = new long[3][];
	Tables7[PART_INDEX] = new long[4][];
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/start0.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[START_INDEX][0] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/start1.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[START_INDEX][1] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/start2.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[START_INDEX][2] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/part0.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[PART_INDEX][0] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/part1.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[PART_INDEX][1] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/part2.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[PART_INDEX][2] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	try {
		DataInputStream inputStream = new DataInputStream(new BufferedInputStream(ScannerHelper.class.getResourceAsStream("unicode6/part14.rsc"))); //$NON-NLS-1$
		long[] readValues = new long[1024];
		for (int i = 0; i < 1024; i++) {
			readValues[i] = inputStream.readLong();
		}
		inputStream.close();
		Tables7[PART_INDEX][3] = readValues;
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
private final static boolean isBitSet(long[] values, int i) {
	try {
		return (values[i / 64] & Bits[i % 64]) != 0;
	} catch (NullPointerException e) {
		return false;
	}
}
public static boolean isJavaIdentifierPart(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_IDENT_PART) != 0;
	}
	return Character.isJavaIdentifierPart(c);
}
public static boolean isJavaIdentifierPart(long complianceLevel, char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_IDENT_PART) != 0;
	}
	return isJavaIdentifierPart(complianceLevel, (int) c);
}
public static boolean isJavaIdentifierPart(long complianceLevel, int codePoint) {
	if (complianceLevel <= ClassFileConstants.JDK1_6) {
		if (Tables == null) {
			initializeTable();
		}
		switch((codePoint & 0x1F0000) >> 16) {
			case 0 :
				return isBitSet(Tables[PART_INDEX][0], codePoint & 0xFFFF);
			case 1 :
				return isBitSet(Tables[PART_INDEX][1], codePoint & 0xFFFF);
			case 2 :
				return isBitSet(Tables[PART_INDEX][2], codePoint & 0xFFFF);
			case 14 :
				return isBitSet(Tables[PART_INDEX][3], codePoint & 0xFFFF);
		}
	} else {
		// java 7 supports Unicode 6
		if (Tables7 == null) {
			initializeTable17();
		}
		switch((codePoint & 0x1F0000) >> 16) {
			case 0 :
				return isBitSet(Tables7[PART_INDEX][0], codePoint & 0xFFFF);
			case 1 :
				return isBitSet(Tables7[PART_INDEX][1], codePoint & 0xFFFF);
			case 2 :
				return isBitSet(Tables7[PART_INDEX][2], codePoint & 0xFFFF);
			case 14 :
				return isBitSet(Tables7[PART_INDEX][3], codePoint & 0xFFFF);
		}
	}
	return false;
}
public static boolean isJavaIdentifierPart(long complianceLevel, char high, char low) {
	return isJavaIdentifierPart(complianceLevel, toCodePoint(high, low));
}
public static boolean isJavaIdentifierStart(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_IDENT_START) != 0;
	}
	return Character.isJavaIdentifierStart(c);
}
public static boolean isJavaIdentifierStart(long complianceLevel, char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_IDENT_START) != 0;
	}
	return ScannerHelper.isJavaIdentifierStart(complianceLevel, (int) c);
}
public static boolean isJavaIdentifierStart(long complianceLevel, char high, char low) {
	return isJavaIdentifierStart(complianceLevel, toCodePoint(high, low));
}
public static boolean isJavaIdentifierStart(long complianceLevel, int codePoint) {
	if (complianceLevel <= ClassFileConstants.JDK1_6) {
		if (Tables == null) {
			initializeTable();
		}
		switch((codePoint & 0x1F0000) >> 16) {
			case 0 :
				return isBitSet(Tables[START_INDEX][0], codePoint & 0xFFFF);
			case 1 :
				return isBitSet(Tables[START_INDEX][1], codePoint & 0xFFFF);
			case 2 :
				return isBitSet(Tables[START_INDEX][2], codePoint & 0xFFFF);
		}
	} else {
		// java 7 supports Unicode 6
		if (Tables7 == null) {
			initializeTable17();
		}
		switch((codePoint & 0x1F0000) >> 16) {
			case 0 :
				return isBitSet(Tables7[START_INDEX][0], codePoint & 0xFFFF);
			case 1 :
				return isBitSet(Tables7[START_INDEX][1], codePoint & 0xFFFF);
			case 2 :
				return isBitSet(Tables7[START_INDEX][2], codePoint & 0xFFFF);
		}
	}
	return false;
}
private static int toCodePoint(char high, char low) {
	return (high - Scanner.HIGH_SURROGATE_MIN_VALUE) * 0x400 + (low - Scanner.LOW_SURROGATE_MIN_VALUE) + 0x10000;
}
public static boolean isDigit(char c) throws InvalidInputException {
	if(c < ScannerHelper.MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_DIGIT) != 0;
	}
	if (Character.isDigit(c)) {
		throw new InvalidInputException(Scanner.INVALID_DIGIT);
	}
	return false;
}
public static int digit(char c, int radix) {
	if (c < ScannerHelper.MAX_OBVIOUS) {
		switch(radix) {
			case 8 :
				if (c >= 48 && c <= 55) {
					return c - 48;
				}
				return -1;
			case 10 :
				if (c >= 48 && c <= 57) {
					return c - 48;
				}
				return -1;
			case 16 :
				if (c >= 48 && c <= 57) {
					return c - 48;
				}
				if (c >= 65 && c <= 70) {
					return c - 65 + 10;
				}
				if (c >= 97 && c <= 102) {
					return c - 97 + 10;
				}
				return -1;
		}
	}
	return Character.digit(c, radix);
}
public static int getNumericValue(char c) {
	if (c < ScannerHelper.MAX_OBVIOUS) {
		switch(ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c]) {
			case C_DIGIT :
				return c - '0';
			case C_LOWER_LETTER :
				return 10 + c - 'a';
			case C_UPPER_LETTER :
				return 10 + c - 'A';
		}
	}
	return Character.getNumericValue(c);
}
public static char toUpperCase(char c) {
	if (c < MAX_OBVIOUS) {
		if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_UPPER_LETTER) != 0) {
			return c;
		} else if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_LOWER_LETTER) != 0) {
			return (char) (c - 32);
		}
	}
	return Character.toUpperCase(c);
}
public static char toLowerCase(char c) {
	if (c < MAX_OBVIOUS) {
		if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_LOWER_LETTER) != 0) {
			return c;
		} else if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_UPPER_LETTER) != 0) {
			return (char) (32 + c);
		}
	}
	return Character.toLowerCase(c);
}
public static boolean isLowerCase(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_LOWER_LETTER) != 0;
	}
	return Character.isLowerCase(c);
}
public static boolean isUpperCase(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_UPPER_LETTER) != 0;
	}
	return Character.isUpperCase(c);
}
/**
 * Include also non JLS whitespaces.
 *
 * return true if Character.isWhitespace(c) would return true
 */
public static boolean isWhitespace(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & ScannerHelper.C_SPACE) != 0;
	}
	return Character.isWhitespace(c);
}
public static boolean isLetter(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & (ScannerHelper.C_UPPER_LETTER | ScannerHelper.C_LOWER_LETTER)) != 0;
	}
	return Character.isLetter(c);
}
public static boolean isLetterOrDigit(char c) {
	if (c < MAX_OBVIOUS) {
		return (ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & (ScannerHelper.C_UPPER_LETTER | ScannerHelper.C_LOWER_LETTER | ScannerHelper.C_DIGIT)) != 0;
	}
	return Character.isLetterOrDigit(c);
}
}
