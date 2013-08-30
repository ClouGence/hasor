/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.util;

/**
 * Internal utility for declaring with hexadecimal double and float literals.
 *
 * @since 3.1
 */
public class FloatUtil {

	private static final int DOUBLE_FRACTION_WIDTH = 52;

	private static final int DOUBLE_PRECISION = 53;

	private static final int MAX_DOUBLE_EXPONENT = +1023;

	private static final int MIN_NORMALIZED_DOUBLE_EXPONENT = -1022;

	private static final int MIN_UNNORMALIZED_DOUBLE_EXPONENT = MIN_NORMALIZED_DOUBLE_EXPONENT
			- DOUBLE_PRECISION;

	private static final int DOUBLE_EXPONENT_BIAS = +1023;

	private static final int DOUBLE_EXPONENT_SHIFT = 52;

	private static final int SINGLE_FRACTION_WIDTH = 23;

	private static final int SINGLE_PRECISION = 24;

	private static final int MAX_SINGLE_EXPONENT = +127;

	private static final int MIN_NORMALIZED_SINGLE_EXPONENT = -126;

	private static final int MIN_UNNORMALIZED_SINGLE_EXPONENT = MIN_NORMALIZED_SINGLE_EXPONENT
			- SINGLE_PRECISION;

	private static final int SINGLE_EXPONENT_BIAS = +127;

	private static final int SINGLE_EXPONENT_SHIFT = 23;

	/**
	 * Returns the float value corresponding to the given
	 * hexadecimal floating-point single precision literal.
	 * The literal must be syntactically correct, and must be
	 * a float literal (end in a 'f' or 'F'). It must not
	 * include either leading or trailing whitespace or
	 * a sign.
	 * <p>
	 * This method returns the same answer as
	 * Float.parseFloat(new String(source)) does in JDK 1.5,
	 * except that this method returns Floal.NaN if it
	 * would underflow to 0 (parseFloat just returns 0).
	 * The method handles all the tricky cases, including
	 * fraction rounding to 24 bits and gradual underflow.
	 * </p>
	 *
	 * @param source source string containing single precision
	 * hexadecimal floating-point literal
	 * @return the float value, including Float.POSITIVE_INFINITY
	 * if the non-zero value is too large to be represented, and
	 * Float.NaN if the non-zero value is too small to be represented
	 */
	public static float valueOfHexFloatLiteral(char[] source) {
		long bits = convertHexFloatingPointLiteralToBits(source);
		return Float.intBitsToFloat((int) bits);
	}

	/**
	 * Returns the double value corresponding to the given
	 * hexadecimal floating-point double precision literal.
	 * The literal must be syntactially correct, and must be
	 * a double literal (end in an optional 'd' or 'D').
	 * It must not include either leading or trailing whitespace or
	 * a sign.
	 * <p>
	 * This method returns the same answer as
	 * Double.parseDouble(new String(source)) does in JDK 1.5,
	 * except that this method throw NumberFormatException in
	 * the case of overflow to infinity or underflow to 0.
	 * The method handles all the tricky cases, including
	 * fraction rounding to 53 bits and gradual underflow.
	 * </p>
	 *
	 * @param source source string containing double precision
	 * hexadecimal floating-point literal
	 * @return the double value, including Double.POSITIVE_INFINITY
	 * if the non-zero value is too large to be represented, and
	 * Double.NaN if the non-zero value is too small to be represented
	 */
	public static double valueOfHexDoubleLiteral(char[] source) {
		long bits = convertHexFloatingPointLiteralToBits(source);
		return Double.longBitsToDouble(bits);
	}

	/**
	 * Returns the given hexadecimal floating-point literal as
	 * the bits for a single-precision  (float) or a
	 * double-precision (double) IEEE floating point number.
	 * The literal must be syntactically correct.  It must not
	 * include either leading or trailing whitespace or a sign.
	 *
	 * @param source source string containing hexadecimal floating-point literal
	 * @return for double precision literals, bits suitable
	 * for passing to Double.longBitsToDouble; for single precision literals,
	 * bits suitable for passing to Single.intBitsToDouble in the bottom
	 * 32 bits of the result
	 * @throws NumberFormatException if the number cannot be parsed
	 */
	private static long convertHexFloatingPointLiteralToBits(char[] source) {
		int length = source.length;
		long mantissa = 0;

		// Step 1: process the '0x' lead-in
		int next = 0;
		char nextChar = source[next];
		nextChar = source[next];
		if (nextChar == '0') {
			next++;
		} else {
			throw new NumberFormatException();
		}
		nextChar = source[next];
		if (nextChar == 'X' || nextChar == 'x') {
			next++;
		} else {
			throw new NumberFormatException();
		}

		// Step 2: process leading '0's either before or after the '.'
		int binaryPointPosition = -1;
		loop: while (true) {
			nextChar = source[next];
			switch (nextChar) {
			case '0':
				next++;
				continue loop;
			case '.':
				binaryPointPosition = next;
				next++;
				continue loop;
			default:
				break loop;
			}
		}

		// Step 3: process the mantissa
		// leading zeros have been trimmed
		int mantissaBits = 0;
		int leadingDigitPosition = -1;
		loop: while (true) {
			nextChar = source[next];
			int hexdigit;
			switch (nextChar) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				hexdigit = nextChar - '0';
				break;
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				hexdigit = (nextChar - 'a') + 10;
				break;
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				hexdigit = (nextChar - 'A') + 10;
				break;
			case '.':
				binaryPointPosition = next;
				next++;
				continue loop;
			default:
				if (binaryPointPosition < 0) {
					// record virtual '.' as being to right of all digits
					binaryPointPosition = next;
				}
				break loop;
			}
			if (mantissaBits == 0) {
				// this is the first non-zero hex digit
				// ignore leading binary 0's in hex digit
				leadingDigitPosition = next;
				mantissa = hexdigit;
				mantissaBits = 4;
			} else if (mantissaBits < 60) {
				// middle hex digits
				mantissa <<= 4;
				mantissa |= hexdigit;
				mantissaBits += 4;
			} else {
				// more mantissa bits than we can handle
				// drop this hex digit on the ground
			}
			next++;
			continue loop;
		}

		// Step 4: process the 'P'
		nextChar = source[next];
		if (nextChar == 'P' || nextChar == 'p') {
			next++;
		} else {
			throw new NumberFormatException();
		}

		// Step 5: process the exponent
		int exponent = 0;
		int exponentSign = +1;
		loop: while (next < length) {
			nextChar = source[next];
			switch (nextChar) {
			case '+':
				exponentSign = +1;
				next++;
				continue loop;
			case '-':
				exponentSign = -1;
				next++;
				continue loop;
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				int digit = nextChar - '0';
				exponent = (exponent * 10) + digit;
				next++;
				continue loop;
			default:
				break loop;
			}
		}

		// Step 6: process the optional 'f' or 'd'
		boolean doublePrecision = true;
		if (next < length) {
			nextChar = source[next];
			switch (nextChar) {
			case 'f':
			case 'F':
				doublePrecision = false;
				next++;
				break;
			case 'd':
			case 'D':
				doublePrecision = true;
				next++;
				break;
			default:
				throw new NumberFormatException();
			}
		}

		// at this point, all the parsing is done
		// Step 7: handle mantissa of zero
		if (mantissa == 0) {
			return 0L;
		}

		// Step 8: normalize non-zero mantissa
		// mantissa is in right-hand mantissaBits
		// ensure that top bit (as opposed to hex digit) is 1
		int scaleFactorCompensation = 0;
		long top = (mantissa >>> (mantissaBits - 4));
		if ((top & 0x8) == 0) {
			mantissaBits--;
			scaleFactorCompensation++;
			if ((top & 0x4) == 0) {
				mantissaBits--;
				scaleFactorCompensation++;
				if ((top & 0x2) == 0) {
					mantissaBits--;
					scaleFactorCompensation++;
				}
			}
		}

		// Step 9: convert double literals to IEEE double
		long result = 0L;
		if (doublePrecision) {
			long fraction;
			if (mantissaBits > DOUBLE_PRECISION) {
				// more bits than we can keep
				int extraBits = mantissaBits - DOUBLE_PRECISION;
				// round to DOUBLE_PRECISION bits
				fraction = mantissa >>> (extraBits - 1);
				long lowBit = fraction & 0x1;
				fraction += lowBit;
				fraction = fraction >>> 1;
				if ((fraction & (1L << DOUBLE_PRECISION)) != 0) {
					fraction = fraction >>> 1;
					scaleFactorCompensation -= 1;
				}
			} else {
				// less bits than the faction can hold - pad on right with 0s
				fraction = mantissa << (DOUBLE_PRECISION - mantissaBits);
			}

			int scaleFactor = 0; // how many bits to move '.' to before leading hex digit
			if (mantissaBits > 0) {
				if (leadingDigitPosition < binaryPointPosition) {
					// e.g., 0x80.0p0 has scaleFactor == +8
					scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
					// e.g., 0x10.0p0 has scaleFactorCompensation == +3
					scaleFactor -= scaleFactorCompensation;
				} else {
					// e.g., 0x0.08p0 has scaleFactor == -4
					scaleFactor = -4
							* (leadingDigitPosition - binaryPointPosition - 1);
					// e.g., 0x0.01p0 has scaleFactorCompensation == +3
					scaleFactor -= scaleFactorCompensation;
				}
			}

			int e = (exponentSign * exponent) + scaleFactor;
			if (e - 1 > MAX_DOUBLE_EXPONENT) {
				// overflow to +infinity
				result = Double.doubleToLongBits(Double.POSITIVE_INFINITY);
			} else if (e - 1 >= MIN_NORMALIZED_DOUBLE_EXPONENT) {
				// can be represented as a normalized double
				// the left most bit must be discarded (it's always a 1)
				long biasedExponent = e - 1 + DOUBLE_EXPONENT_BIAS;
				result = fraction & ~(1L << DOUBLE_FRACTION_WIDTH);
				result |= (biasedExponent << DOUBLE_EXPONENT_SHIFT);
			} else if (e - 1 > MIN_UNNORMALIZED_DOUBLE_EXPONENT) {
				// can be represented as an unnormalized double
				long biasedExponent = 0;
				result = fraction >>> (MIN_NORMALIZED_DOUBLE_EXPONENT - e + 1);
				result |= (biasedExponent << DOUBLE_EXPONENT_SHIFT);
			} else {
				// underflow - return Double.NaN
				result = Double.doubleToLongBits(Double.NaN);
			}
			return result;
		}

		// Step 10: convert float literals to IEEE single
		long fraction;
		if (mantissaBits > SINGLE_PRECISION) {
			// more bits than we can keep
			int extraBits = mantissaBits - SINGLE_PRECISION;
			// round to DOUBLE_PRECISION bits
			fraction = mantissa >>> (extraBits - 1);
			long lowBit = fraction & 0x1;
			fraction += lowBit;
			fraction = fraction >>> 1;
			if ((fraction & (1L << SINGLE_PRECISION)) != 0) {
				fraction = fraction >>> 1;
				scaleFactorCompensation -= 1;
			}
		} else {
			// less bits than the faction can hold - pad on right with 0s
			fraction = mantissa << (SINGLE_PRECISION - mantissaBits);
		}

		int scaleFactor = 0; // how many bits to move '.' to before leading hex digit
		if (mantissaBits > 0) {
			if (leadingDigitPosition < binaryPointPosition) {
				// e.g., 0x80.0p0 has scaleFactor == +8
				scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
				// e.g., 0x10.0p0 has scaleFactorCompensation == +3
				scaleFactor -= scaleFactorCompensation;
			} else {
				// e.g., 0x0.08p0 has scaleFactor == -4
				scaleFactor = -4
						* (leadingDigitPosition - binaryPointPosition - 1);
				// e.g., 0x0.01p0 has scaleFactorCompensation == +3
				scaleFactor -= scaleFactorCompensation;
			}
		}

		int e = (exponentSign * exponent) + scaleFactor;
		if (e - 1 > MAX_SINGLE_EXPONENT) {
			// overflow to +infinity
			result = Float.floatToIntBits(Float.POSITIVE_INFINITY);
		} else if (e - 1 >= MIN_NORMALIZED_SINGLE_EXPONENT) {
			// can be represented as a normalized single
			// the left most bit must be discarded (it's always a 1)
			long biasedExponent = e - 1 + SINGLE_EXPONENT_BIAS;
			result = fraction & ~(1L << SINGLE_FRACTION_WIDTH);
			result |= (biasedExponent << SINGLE_EXPONENT_SHIFT);
		} else if (e - 1 > MIN_UNNORMALIZED_SINGLE_EXPONENT) {
			// can be represented as an unnormalized single
			long biasedExponent = 0;
			result = fraction >>> (MIN_NORMALIZED_SINGLE_EXPONENT - e + 1);
			result |= (biasedExponent << SINGLE_EXPONENT_SHIFT);
		} else {
			// underflow - return Float.NaN
			result = Float.floatToIntBits(Float.NaN);
		}
		return result;
	}
}
