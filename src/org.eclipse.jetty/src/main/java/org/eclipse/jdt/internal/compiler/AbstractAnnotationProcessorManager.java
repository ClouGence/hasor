/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     BEA - Patch for bug 172743
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler;

import java.io.PrintWriter;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public abstract class AbstractAnnotationProcessorManager {
	/**
	 * Configure the receiver using the given batch compiler and the given options.
	 * The parameter batchCompiler is expected to be an instance of the batch compiler. This method is
	 * only used for the batch mode. For the IDE mode, please see {@link #configureFromPlatform(Compiler, Object, Object)}.
	 *
	 * @param batchCompiler the given batch compiler object
	 * @param options the given options
	 */
	public abstract void configure(Object batchCompiler, String[] options);

	/**
	 * Configure the receiver using the given compiler, the given compilationUnitLocator and
	 * the given java project.
	 *
	 * @param compiler the given compiler
	 * @param compilationUnitLocator the given compilation unit locator
	 * @param javaProject the given java project
	 */
	public abstract void configureFromPlatform(Compiler compiler, Object compilationUnitLocator, Object javaProject);

	/**
	 * Set the print writer for the standard output.
	 *
	 * @param out the given print writer for output
	 */
	public abstract void setOut(PrintWriter out);

	/**
	 * Set the print writer for the standard error.
	 *
	 * @param err the given print writer for error
	 */
	public abstract void setErr(PrintWriter err);

	/**
	 * Return the new units created in the last round.
	 *
	 * @return the new units created in the last round
	 */
	public abstract ICompilationUnit[] getNewUnits();

	/**
	 * Return the new binary bindings created in the last round.
	 *
	 * @return the new binary bindings created in the last round
	 */
	public abstract ReferenceBinding[] getNewClassFiles();

	/**
	 * Returns the deleted units.
	 * @return the deleted units
	 */
	public abstract ICompilationUnit[] getDeletedUnits();

	/**
	 * Reinitialize the receiver
	 */
	public abstract void reset();

	/**
	 * Run a new annotation processing round on the given values.
	 *
	 * @param units the given source type
	 * @param referenceBindings the given binary types
	 * @param isLastRound flag to notify the last round
	 */
	public abstract void processAnnotations(CompilationUnitDeclaration[] units, ReferenceBinding[] referenceBindings, boolean isLastRound);

	/**
	 * Set the processors for annotation processing.
	 *
	 * @param processors the given processors
	 */
	public abstract void setProcessors(Object[] processors);
}
