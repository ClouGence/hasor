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
package org.eclipse.jdt.internal.compiler.classfmt;
import java.io.PrintStream;
import java.io.PrintWriter;
public class ClassFormatException extends Exception {

	public static final int ErrBadMagic = 1;
	public static final int ErrBadMinorVersion = 2;
	public static final int ErrBadMajorVersion = 3;
	public static final int ErrBadConstantClass = 4;
	public static final int ErrBadConstantString = 5;
	public static final int ErrBadConstantNameAndType = 6;
	public static final int ErrBadConstantFieldRef = 7;
	public static final int ErrBadConstantMethodRef = 8;
	public static final int ErrBadConstantInterfaceMethodRef = 9;
	public static final int ErrBadConstantPoolIndex = 10;
	public static final int ErrBadSuperclassName = 11;
	public static final int ErrInterfaceCannotBeFinal = 12;
	public static final int ErrInterfaceMustBeAbstract = 13;
	public static final int ErrBadModifiers = 14;
	public static final int ErrClassCannotBeAbstractFinal = 15;
	public static final int ErrBadClassname = 16;
	public static final int ErrBadFieldInfo = 17;
	public static final int ErrBadMethodInfo = 17;
	public static final int ErrEmptyConstantPool = 18;
	public static final int ErrMalformedUtf8 = 19;
	public static final int ErrUnknownConstantTag = 20;
	public static final int ErrTruncatedInput = 21;
	public static final int ErrMethodMustBeAbstract = 22;
	public static final int ErrMalformedAttribute = 23;
	public static final int ErrBadInterface = 24;
	public static final int ErrInterfaceMustSubclassObject = 25;
	public static final int ErrIncorrectInterfaceMethods = 26;
	public static final int ErrInvalidMethodName = 27;
	public static final int ErrInvalidMethodSignature = 28;

	private static final long serialVersionUID = 6667458511042774540L; // backward compatible

	private int errorCode;
	private int bufferPosition;
	private RuntimeException nestedException;
	private char[] fileName;

	public ClassFormatException(RuntimeException e, char[] fileName) {
		this.nestedException = e;
		this.fileName = fileName;
	}
	public ClassFormatException(int code) {
		this.errorCode = code;
	}
	public ClassFormatException(int code, int bufPos) {
		this.errorCode = code;
		this.bufferPosition = bufPos;
	}
	/**
	 * @return int
	 */
	public int getErrorCode() {
		return this.errorCode;
	}
	/**
	 * @return int
	 */
	public int getBufferPosition() {
		return this.bufferPosition;
	}
	/**
	 * Returns the underlying <code>Throwable</code> that caused the failure.
	 *
	 * @return the wrappered <code>Throwable</code>, or <code>null</code>
	 *         if the direct case of the failure was at the Java model layer
	 */
	public Throwable getException() {
		return this.nestedException;
	}
	public void printStackTrace() {
		printStackTrace(System.err);
	}
	/**
	 * Prints this exception's stack trace to the given print stream.
	 *
	 * @param output
	 *            the print stream
	 * @since 3.0
	 */
	public void printStackTrace(PrintStream output) {
		synchronized (output) {
			super.printStackTrace(output);
			Throwable throwable = getException();
			if (throwable != null) {
				if (this.fileName != null) {
					output.print("Caused in "); //$NON-NLS-1$
					output.print(this.fileName);
					output.print(" by: "); //$NON-NLS-1$
				} else {
					output.print("Caused by: "); //$NON-NLS-1$
				}
				throwable.printStackTrace(output);
			}
		}
	}
	/**
	 * Prints this exception's stack trace to the given print writer.
	 *
	 * @param output
	 *            the print writer
	 * @since 3.0
	 */
	public void printStackTrace(PrintWriter output) {
		synchronized (output) {
			super.printStackTrace(output);
			Throwable throwable = getException();
			if (throwable != null) {
				if (this.fileName != null) {
					output.print("Caused in "); //$NON-NLS-1$
					output.print(this.fileName);
					output.print(" by: "); //$NON-NLS-1$
				} else {
					output.print("Caused by: "); //$NON-NLS-1$
				}
				throwable.printStackTrace(output);
			}
		}
	}
}
