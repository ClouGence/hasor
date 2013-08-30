/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.compiler.batch;

import java.io.PrintWriter;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.batch.Main;

/**
 * A public API for invoking the Eclipse Compiler for Java. E.g.
 * <pre>
 * BatchCompiler.compile("C:\\mySources\\X.java -d C:\\myOutput", new PrintWriter(System.out), new PrintWriter(System.err), null);
 * </pre>
 *
 * @since 3.4
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class BatchCompiler {

	/**
	 * Invokes the Eclipse Compiler for Java with the given command line arguments, using the given writers
	 * to print messages, and reporting progress to the given compilation progress. Returns whether
	 * the compilation completed successfully.
	 * <p>
	 * Reasons for a compilation failing to complete successfully include:</p>
	 * <ul>
	 * <li>an error was reported</li>
	 * <li>a runtime exception occurred</li>
	 * <li>the compilation was canceled using the compilation progress</li>
	 * </ul>
	 * <p>
	 * The specification of the command line arguments is defined by running the batch compiler's help
	 * <pre>BatchCompiler.compile("-help", new PrintWriter(System.out), new PrintWriter(System.err), null);</pre>
	 * </p>
	 *
	 * @param commandLine the command line arguments passed to the compiler
	 * @param outWriter the writer used to print standard messages
	 * @param errWriter the writer used to print error messages
	 * @param progress the object to report progress to and to provide cancellation, or <code>null</code> if no progress is needed
	 * @return whether the compilation completed successfully
	 */
	public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress) {
		return compile(Main.tokenize(commandLine), outWriter, errWriter, progress);
	}

	/**
	 * Invokes the Eclipse Compiler for Java with the given command line arguments, using the given writers
	 * to print messages, and reporting progress to the given compilation progress. Returns whether
	 * the compilation completed successfully.
	 * <p>
	 * Reasons for a compilation failing to complete successfully include:</p>
	 * <ul>
	 * <li>an error was reported</li>
	 * <li>a runtime exception occurred</li>
	 * <li>the compilation was canceled using the compilation progress</li>
	 * </ul>
	 * <p>
	 * The specification of the command line arguments is defined by running the batch compiler's help
	 * <pre>BatchCompiler.compile("-help", new PrintWriter(System.out), new PrintWriter(System.err), null);</pre>
	 * </p>
	 * Note that a <code>true</code> returned value indicates that no errors were reported, no runtime exceptions
	 * occurred and that the compilation was not canceled.
	 *
	 * @param commandLineArguments the command line arguments passed to the compiler
	 * @param outWriter the writer used to print standard messages
	 * @param errWriter the writer used to print error messages
	 * @param progress the object to report progress to and to provide cancellation, or <code>null</code> if no progress is needed
	 * @return whether the compilation completed successfully
	 */
	public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress) {
		return Main.compile(commandLineArguments, outWriter, errWriter, progress);
	}

	private BatchCompiler() {
		// prevent instantiation
	}
}
