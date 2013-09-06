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
package org.eclipse.jdt.internal.compiler.lookup;

public interface ProblemReasons {
	final int NoError = 0;
	final int NotFound = 1;
	final int NotVisible = 2;
	final int Ambiguous = 3;
	final int InternalNameProvided = 4; // used if an internal name is used in source
	final int InheritedNameHidesEnclosingName = 5;
	final int NonStaticReferenceInConstructorInvocation = 6;
	final int NonStaticReferenceInStaticContext = 7;
	final int ReceiverTypeNotVisible = 8;
	final int IllegalSuperTypeVariable = 9;
	final int ParameterBoundMismatch = 10; // for generic method
	final int TypeParameterArityMismatch = 11; // for generic method
	final int ParameterizedMethodTypeMismatch = 12; // for generic method
	final int TypeArgumentsForRawGenericMethod = 13; // for generic method
	final int InvalidTypeForStaticImport = 14;
	final int InvalidTypeForAutoManagedResource = 15;
}
