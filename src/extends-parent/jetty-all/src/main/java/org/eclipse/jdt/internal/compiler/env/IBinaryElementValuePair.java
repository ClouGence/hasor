/*******************************************************************************
 * Copyright (c) 2005, 2009 BEA Systems, Inc.
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
 * This represents the class file information about a member value pair of an annotation.
 */
public interface IBinaryElementValuePair {

/** @return the name of the member */
char[] getName();

/**
 * Return {@link ClassSignature} for a Class {@link java.lang.Class}.
 * Return {@link org.eclipse.jdt.internal.compiler.impl.Constant} for compile-time constant of primitive type, as well as String literals.
 * Return {@link EnumConstantSignature} if value is an enum constant.
 * Return {@link IBinaryAnnotation} for annotation type.
 * Return {@link Object}[] for array type.
 *
 * @return the value of this member value pair
 */
Object getValue();
}
