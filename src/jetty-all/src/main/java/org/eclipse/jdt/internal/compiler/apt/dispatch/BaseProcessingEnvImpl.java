/*******************************************************************************
 * Copyright (c) 2007, 2011 BEA Systems, Inc. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    wharley@bea.com - initial API and implementation
 *    IBM Corporation - fix for 342598
 *******************************************************************************/

package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.TypesImpl;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

/**
 * Implementation of ProcessingEnvironment that is common to batch and IDE environments.
 */
public abstract class BaseProcessingEnvImpl implements ProcessingEnvironment {

	// Initialized in subclasses:
	protected Filer _filer;
	protected Messager _messager;
	protected Map<String, String> _processorOptions;
	protected Compiler _compiler;
	
	// Initialized in this base class:
	protected Elements _elementUtils;
	protected Types _typeUtils;
	private List<ICompilationUnit> _addedUnits;
	private List<ReferenceBinding> _addedClassFiles;
	private List<ICompilationUnit> _deletedUnits;
	private boolean _errorRaised;
	private Factory _factory;

	public BaseProcessingEnvImpl() {
		_addedUnits = new ArrayList<ICompilationUnit>();
		_addedClassFiles = new ArrayList<ReferenceBinding>();
		_deletedUnits = new ArrayList<ICompilationUnit>();
		_elementUtils = new ElementsImpl(this);
		_typeUtils = new TypesImpl(this);
		_factory = new Factory(this);
		_errorRaised = false;
	}

	public void addNewUnit(ICompilationUnit unit) {
		_addedUnits.add(unit);
	}

	public void addNewClassFile(ReferenceBinding binding) {
		_addedClassFiles.add(binding);
	}
	
	public Compiler getCompiler() {
		return _compiler;
	}

	public ICompilationUnit[] getDeletedUnits() {
		ICompilationUnit[] result = new ICompilationUnit[_deletedUnits.size()];
		_deletedUnits.toArray(result);
		return result;
	}

	public ICompilationUnit[] getNewUnits() {
		ICompilationUnit[] result = new ICompilationUnit[_addedUnits.size()];
		_addedUnits.toArray(result);
		return result;
	}

	@Override
	public Elements getElementUtils() {
		return _elementUtils;
	}

	@Override
	public Filer getFiler() {
		return _filer;
	}

	@Override
	public Messager getMessager() {
		return _messager;
	}
	
	@Override
	public Map<String, String> getOptions() {
		return _processorOptions;
	}

	@Override
	public Types getTypeUtils() {
		return _typeUtils;
	}

	public LookupEnvironment getLookupEnvironment() {
		return _compiler.lookupEnvironment;
	}

	@Override
	public SourceVersion getSourceVersion() {
		if (this._compiler.options.sourceLevel <= ClassFileConstants.JDK1_5) {
			return SourceVersion.RELEASE_5;
		}
		if (this._compiler.options.sourceLevel == ClassFileConstants.JDK1_6) {
			return SourceVersion.RELEASE_6;
		}
		try {
			return SourceVersion.valueOf("RELEASE_7"); //$NON-NLS-1$
		} catch(IllegalArgumentException e) {
			// handle call on a JDK 6
			return SourceVersion.RELEASE_6;
		}
	}

	/**
	 * Called when AnnotationProcessorManager has retrieved the list of 
	 * newly generated compilation units (ie, once per round)
	 */
	public void reset() {
		_addedUnits.clear();
		_addedClassFiles.clear();
		_deletedUnits.clear();
	}

	/**
	 * Has an error been raised in any of the rounds of processing in this build?
	 * @return
	 */
	public boolean errorRaised()
	{
		return _errorRaised;
	}

	/**
	 * Set or clear the errorRaised flag.  Typically this will be set by the Messager
	 * when an error has been raised, and it will never be cleared.
	 */
	public void setErrorRaised(boolean b)
	{
		_errorRaised = true;
	}

	public Factory getFactory()
	{
		return _factory;
	}

	public ReferenceBinding[] getNewClassFiles() {
		ReferenceBinding[] result = new ReferenceBinding[_addedClassFiles.size()];
		_addedClassFiles.toArray(result);
		return result;
	}

}
