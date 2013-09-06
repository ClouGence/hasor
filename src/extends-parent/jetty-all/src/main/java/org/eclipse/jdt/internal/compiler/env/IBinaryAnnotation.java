/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.env;

/**
 * This represents class file information about an annotation instance.
 */
public interface IBinaryAnnotation {

/**
 * @return the signature of the annotation type.
 */
char[] getTypeName();

/**
 * @return the list of element value pairs of the annotation
 */
IBinaryElementValuePair[] getElementValuePairs();
}
