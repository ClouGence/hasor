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

// TODO should rename into TypeNames (once extracted last non name constants)
public interface TypeConstants {

	char[] JAVA = "java".toCharArray(); //$NON-NLS-1$
	char[] LANG = "lang".toCharArray(); //$NON-NLS-1$
	char[] IO = "io".toCharArray(); //$NON-NLS-1$
	char[] UTIL = "util".toCharArray(); //$NON-NLS-1$
	char[] ANNOTATION = "annotation".toCharArray(); //$NON-NLS-1$
	char[] REFLECT = "reflect".toCharArray(); //$NON-NLS-1$
	char[] LENGTH = "length".toCharArray(); //$NON-NLS-1$
	char[] CLONE = "clone".toCharArray(); //$NON-NLS-1$
	char[] EQUALS = "equals".toCharArray(); //$NON-NLS-1$
	char[] GETCLASS = "getClass".toCharArray(); //$NON-NLS-1$
	char[] HASHCODE = "hashCode".toCharArray(); //$NON-NLS-1$
	char[] OBJECT = "Object".toCharArray(); //$NON-NLS-1$
	char[] MAIN = "main".toCharArray(); //$NON-NLS-1$
	char[] SERIALVERSIONUID = "serialVersionUID".toCharArray(); //$NON-NLS-1$
	char[] SERIALPERSISTENTFIELDS = "serialPersistentFields".toCharArray(); //$NON-NLS-1$
	char[] READRESOLVE = "readResolve".toCharArray(); //$NON-NLS-1$
	char[] WRITEREPLACE = "writeReplace".toCharArray(); //$NON-NLS-1$
	char[] READOBJECT = "readObject".toCharArray(); //$NON-NLS-1$
	char[] WRITEOBJECT = "writeObject".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_LANG_OBJECT = "java.lang.Object".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_LANG_ENUM = "java.lang.Enum".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_LANG_ANNOTATION_ANNOTATION = "java.lang.annotation.Annotation".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_IO_OBJECTINPUTSTREAM = "java.io.ObjectInputStream".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_IO_OBJECTOUTPUTSTREAM = "java.io.ObjectOutputStream".toCharArray(); //$NON-NLS-1$
	char[] CharArray_JAVA_IO_OBJECTSTREAMFIELD = "java.io.ObjectStreamField".toCharArray(); //$NON-NLS-1$
	char[] ANONYM_PREFIX = "new ".toCharArray(); //$NON-NLS-1$
	char[] ANONYM_SUFFIX = "(){}".toCharArray(); //$NON-NLS-1$
    char[] WILDCARD_NAME = { '?' };
    char[] WILDCARD_SUPER = " super ".toCharArray(); //$NON-NLS-1$
    char[] WILDCARD_EXTENDS = " extends ".toCharArray(); //$NON-NLS-1$
    char[] WILDCARD_MINUS = { '-' };
    char[] WILDCARD_STAR = { '*' };
    char[] WILDCARD_PLUS = { '+' };
    char[] WILDCARD_CAPTURE_NAME_PREFIX = "capture#".toCharArray(); //$NON-NLS-1$
    char[] WILDCARD_CAPTURE_NAME_SUFFIX = "-of ".toCharArray(); //$NON-NLS-1$
	char[] WILDCARD_CAPTURE = { '!' };
	char[] BYTE = "byte".toCharArray(); //$NON-NLS-1$
	char[] SHORT = "short".toCharArray(); //$NON-NLS-1$
	char[] INT = "int".toCharArray(); //$NON-NLS-1$
	char[] LONG = "long".toCharArray(); //$NON-NLS-1$
	char[] FLOAT = "float".toCharArray(); //$NON-NLS-1$
	char[] DOUBLE = "double".toCharArray(); //$NON-NLS-1$
	char[] CHAR = "char".toCharArray(); //$NON-NLS-1$
	char[] BOOLEAN = "boolean".toCharArray(); //$NON-NLS-1$
	char[] NULL = "null".toCharArray(); //$NON-NLS-1$
	char[] VOID = "void".toCharArray(); //$NON-NLS-1$
    char[] VALUE = "value".toCharArray(); //$NON-NLS-1$
    char[] VALUES = "values".toCharArray(); //$NON-NLS-1$
    char[] VALUEOF = "valueOf".toCharArray(); //$NON-NLS-1$
    char[] UPPER_SOURCE = "SOURCE".toCharArray(); //$NON-NLS-1$
    char[] UPPER_CLASS = "CLASS".toCharArray(); //$NON-NLS-1$
    char[] UPPER_RUNTIME = "RUNTIME".toCharArray(); //$NON-NLS-1$
	char[] ANNOTATION_PREFIX = "@".toCharArray(); //$NON-NLS-1$
	char[] ANNOTATION_SUFFIX = "()".toCharArray(); //$NON-NLS-1$
    char[] TYPE = "TYPE".toCharArray(); //$NON-NLS-1$
    char[] UPPER_FIELD = "FIELD".toCharArray(); //$NON-NLS-1$
    char[] UPPER_METHOD = "METHOD".toCharArray(); //$NON-NLS-1$
    char[] UPPER_PARAMETER = "PARAMETER".toCharArray(); //$NON-NLS-1$
    char[] UPPER_CONSTRUCTOR = "CONSTRUCTOR".toCharArray(); //$NON-NLS-1$
    char[] UPPER_LOCAL_VARIABLE = "LOCAL_VARIABLE".toCharArray(); //$NON-NLS-1$
    char[] UPPER_ANNOTATION_TYPE = "ANNOTATION_TYPE".toCharArray(); //$NON-NLS-1$
    char[] UPPER_PACKAGE = "PACKAGE".toCharArray(); //$NON-NLS-1$

	// Constant compound names
	char[][] JAVA_LANG = {JAVA, LANG};
	char[][] JAVA_IO = {JAVA, IO};
	char[][] JAVA_LANG_ANNOTATION_ANNOTATION = {JAVA, LANG, ANNOTATION, "Annotation".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ASSERTIONERROR = {JAVA, LANG, "AssertionError".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_CLASS = {JAVA, LANG, "Class".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_CLASSNOTFOUNDEXCEPTION = {JAVA, LANG, "ClassNotFoundException".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_CLONEABLE = {JAVA, LANG, "Cloneable".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ENUM = {JAVA, LANG, "Enum".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_EXCEPTION = {JAVA, LANG, "Exception".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ERROR = {JAVA, LANG, "Error".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ILLEGALARGUMENTEXCEPTION = {JAVA, LANG, "IllegalArgumentException".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ITERABLE = {JAVA, LANG, "Iterable".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_NOCLASSDEFERROR = {JAVA, LANG, "NoClassDefError".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_OBJECT = {JAVA, LANG, OBJECT};
	char[][] JAVA_LANG_STRING = {JAVA, LANG, "String".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_STRINGBUFFER = {JAVA, LANG, "StringBuffer".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_STRINGBUILDER = {JAVA, LANG, "StringBuilder".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_SYSTEM = {JAVA, LANG, "System".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_RUNTIMEEXCEPTION = {JAVA, LANG, "RuntimeException".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_THROWABLE = {JAVA, LANG, "Throwable".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_REFLECT_CONSTRUCTOR = {JAVA, LANG, REFLECT, "Constructor".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_IO_PRINTSTREAM = {JAVA, IO, "PrintStream".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_IO_SERIALIZABLE = {JAVA, IO, "Serializable".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_BYTE = {JAVA, LANG, "Byte".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_SHORT = {JAVA, LANG, "Short".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_CHARACTER = {JAVA, LANG, "Character".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_INTEGER = {JAVA, LANG, "Integer".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_LONG = {JAVA, LANG, "Long".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_FLOAT = {JAVA, LANG, "Float".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_DOUBLE = {JAVA, LANG, "Double".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_BOOLEAN = {JAVA, LANG, "Boolean".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_VOID = {JAVA, LANG, "Void".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_UTIL_COLLECTION = {JAVA, UTIL, "Collection".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_UTIL_ITERATOR = {JAVA, UTIL, "Iterator".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_DEPRECATED = {JAVA, LANG, "Deprecated".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_DOCUMENTED = {JAVA, LANG, ANNOTATION, "Documented".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_INHERITED = {JAVA, LANG, ANNOTATION, "Inherited".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_OVERRIDE = {JAVA, LANG, "Override".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_RETENTION = {JAVA, LANG, ANNOTATION, "Retention".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_SUPPRESSWARNINGS = {JAVA, LANG, "SuppressWarnings".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_TARGET = {JAVA, LANG, ANNOTATION, "Target".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = {JAVA, LANG, ANNOTATION, "RetentionPolicy".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_ANNOTATION_ELEMENTTYPE = {JAVA, LANG, ANNOTATION, "ElementType".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_REFLECT_FIELD = new char[][] {JAVA, LANG, REFLECT, "Field".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_LANG_REFLECT_METHOD = new char[][] {JAVA, LANG, REFLECT, "Method".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_IO_OBJECTSTREAMEXCEPTION = new char[][] { JAVA, IO, "ObjectStreamException".toCharArray()};//$NON-NLS-1$
	char[][] JAVA_IO_EXTERNALIZABLE = {JAVA, IO, "Externalizable".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_IO_IOEXCEPTION = new char[][] { JAVA, IO, "IOException".toCharArray()};//$NON-NLS-1$
	char[][] JAVA_IO_OBJECTOUTPUTSTREAM = new char[][] { JAVA, IO, "ObjectOutputStream".toCharArray()}; //$NON-NLS-1$
	char[][] JAVA_IO_OBJECTINPUTSTREAM = new char[][] { JAVA, IO, "ObjectInputStream".toCharArray()}; //$NON-NLS-1$
	// javax.rmi.CORBA.Stub
	char[][] JAVAX_RMI_CORBA_STUB = new char[][] {
			"javax".toCharArray(), //$NON-NLS-1$
			"rmi".toCharArray(), //$NON-NLS-1$
			"CORBA".toCharArray(), //$NON-NLS-1$
			"Stub".toCharArray(), //$NON-NLS-1$
	};
	char[][] JAVA_LANG_SAFEVARARGS =  {JAVA, LANG, "SafeVarargs".toCharArray()}; //$NON-NLS-1$
	char[] INVOKE = "invoke".toCharArray(); //$NON-NLS-1$
	char[][] JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE = { // Signature while parsing binary file
			JAVA,
			LANG,
			INVOKE,
			"MethodHandle".toCharArray(), //$NON-NLS-1$
			"PolymorphicSignature".toCharArray() //$NON-NLS-1$
	};
	char[][] JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE = { // Signature while parsing source file
			JAVA,
			LANG,
			INVOKE,
			"MethodHandle$PolymorphicSignature".toCharArray() //$NON-NLS-1$
	};
	char[][] JAVA_LANG_AUTOCLOSEABLE =  {JAVA, LANG, "AutoCloseable".toCharArray()}; //$NON-NLS-1$

	// Constraints for generic type argument inference
	int CONSTRAINT_EQUAL = 0;		// Actual = Formal
	int CONSTRAINT_EXTENDS = 1;	// Actual << Formal
	int CONSTRAINT_SUPER = 2;		// Actual >> Formal

	// Constants used to perform bound checks
	int OK = 0;
	int UNCHECKED = 1;
	int MISMATCH = 2;

	// Synthetics
	char[] INIT = "<init>".toCharArray(); //$NON-NLS-1$
	char[] CLINIT = "<clinit>".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_SWITCH_ENUM_TABLE = "$SWITCH_TABLE$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_ENUM_VALUES = "ENUM$VALUES".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_ASSERT_DISABLED = "$assertionsDisabled".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_CLASS = "class$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_OUTER_LOCAL_PREFIX = "val$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_ENCLOSING_INSTANCE_PREFIX = "this$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_ACCESS_METHOD_PREFIX =  "access$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX =  " enum constant initialization$".toCharArray(); //$NON-NLS-1$
	char[] SYNTHETIC_STATIC_FACTORY =  "<factory>".toCharArray(); //$NON-NLS-1$

	// synthetic package-info name
	public static final char[] PACKAGE_INFO_NAME = "package-info".toCharArray(); //$NON-NLS-1$
}
