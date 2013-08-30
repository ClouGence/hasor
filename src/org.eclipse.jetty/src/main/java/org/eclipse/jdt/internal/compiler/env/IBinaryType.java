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
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;

public interface IBinaryType extends IGenericType {

	char[][] NoInterface = CharOperation.NO_CHAR_CHAR;
	IBinaryNestedType[] NoNestedType = new IBinaryNestedType[0];
	IBinaryField[] NoField = new IBinaryField[0];
	IBinaryMethod[] NoMethod = new IBinaryMethod[0];
/**
 * Answer the runtime visible and invisible annotations for this type or null if none.
 */

IBinaryAnnotation[] getAnnotations();
/**
 * Answer the enclosing method (including method selector and method descriptor), or
 * null if none.
 *
 * For example, "foo()Ljava/lang/Object;V"
 */

char[] getEnclosingMethod();
/**
 * Answer the resolved name of the enclosing type in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if the receiver is a top level type.
 *
 * For example, java.lang.String is java/lang/String.
 */

char[] getEnclosingTypeName();
/**
 * Answer the receiver's fields or null if the array is empty.
 */

IBinaryField[] getFields();
/**
 * Answer the receiver's signature which describes the parameter &
 * return types as specified in section 4.4.4 of the Java 2 VM spec 3rd edition.
 * Returns null if none.
 *
 * @return the receiver's signature, null if none
 */
char[] getGenericSignature();
/**
 * Answer the resolved names of the receiver's interfaces in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if the array is empty.
 *
 * For example, java.lang.String is java/lang/String.
 */

char[][] getInterfaceNames();
/**
 * Answer the receiver's nested types or null if the array is empty.
 *
 * This nested type info is extracted from the inner class attributes.
 * Ask the name environment to find a member type using its compound name.
 */

// NOTE: The compiler examines the nested type info & ignores the local types
// so the local types do not have to be included.

IBinaryNestedType[] getMemberTypes();
/**
 * Answer the receiver's methods or null if the array is empty.
 */

IBinaryMethod[] getMethods();

/**
 * Answer the list of missing type names which were referenced from
 * the problem classfile. This list is encoded via an extra attribute.
 */
char[][][] getMissingTypeNames();

/**
 * Answer the resolved name of the type in the
 * class file format as specified in section 4.2 of the Java 2 VM spec.
 *
 * For example, java.lang.String is java/lang/String.
 */
char[] getName();

/**
 * Answer the simple name of the type in the class file.
 * For member A$B, will answer B.
 * For anonymous will answer null.
 */
char[] getSourceName();

/**
 * Answer the resolved name of the receiver's superclass in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if it does not have one.
 *
 * For example, java.lang.String is java/lang/String.
 */

char[] getSuperclassName();
/**
 * Answer the tagbits set according to the bits for annotations.
 */
long getTagBits();
/**
 * Answer true if the receiver is an anonymous class.
 * false otherwise
 */
boolean isAnonymous();

/**
 * Answer true if the receiver is a local class.
 * false otherwise
 */
boolean isLocal();

/**
 * Answer true if the receiver is a member class.
 * false otherwise
 */
boolean isMember();

/**
 * Answer the source file attribute, or null if none.
 *
 * For example, "String.java"
 */

char[] sourceFileName();

}
