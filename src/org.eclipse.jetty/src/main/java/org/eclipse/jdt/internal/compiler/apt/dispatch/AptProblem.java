/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;

public class AptProblem extends DefaultProblem {
	
	// The batch compiler does not depend on org.eclipse.jdt.apt.pluggable.core; this
	// is just an arbitrary string to it, namespace notwithstanding.  However, the IDE
	// cares about the fact that this string is registered as a marker ID by the
	// org.eclipse.jdt.apt.pluggable.core plug-in.
	private static final String MARKER_ID = "org.eclipse.jdt.apt.pluggable.core.compileProblem";  //$NON-NLS-1$
	
	/** May be null, if it was not possible to identify problem context */
	public final ReferenceContext _referenceContext;
	
	public AptProblem(
			ReferenceContext referenceContext,
			char[] originatingFileName,
			String message,
			int id,
			String[] stringArguments,
			int severity,
			int startPosition,
			int endPosition,
			int line,
			int column) 
	{
		super(originatingFileName,
			message,
			id,
			stringArguments,
			severity,
			startPosition,
			endPosition,
			line,
			column);
		_referenceContext = referenceContext;
	}
	
	@Override
	public int getCategoryID() {
		return CAT_UNSPECIFIED;
	}

	@Override
	public String getMarkerType() {
		return MARKER_ID;
	}
}
