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
package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
import org.eclipse.jdt.internal.compiler.util.Util;
/**
 * This type is used to store all the constant pool entries.
 */
public class ConstantPool implements ClassFileConstants, TypeIds {
	public static final int DOUBLE_INITIAL_SIZE = 5;
	public static final int FLOAT_INITIAL_SIZE = 3;
	public static final int INT_INITIAL_SIZE = 248;
	public static final int LONG_INITIAL_SIZE = 5;
	public static final int UTF8_INITIAL_SIZE = 778;
	public static final int STRING_INITIAL_SIZE = 761;
	public static final int METHODS_AND_FIELDS_INITIAL_SIZE = 450;
	public static final int CLASS_INITIAL_SIZE = 86;
	public static final int NAMEANDTYPE_INITIAL_SIZE = 272;
	public static final int CONSTANTPOOL_INITIAL_SIZE = 2000;
	public static final int CONSTANTPOOL_GROW_SIZE = 6000;
	protected DoubleCache doubleCache;
	protected FloatCache floatCache;
	protected IntegerCache intCache;
	protected LongCache longCache;
	public CharArrayCache UTF8Cache;
	protected CharArrayCache stringCache;
	protected HashtableOfObject methodsAndFieldsCache;
	protected CharArrayCache classCache;
	protected HashtableOfObject nameAndTypeCacheForFieldsAndMethods;
	public byte[] poolContent;
	public int currentIndex = 1;
	public int currentOffset;
	public int[] offsets;

	public ClassFile classFile;
	public static final char[] Append = "append".toCharArray(); //$NON-NLS-1$
	public static final char[] ARRAY_NEWINSTANCE_NAME = "newInstance".toCharArray(); //$NON-NLS-1$
	public static final char[] ARRAY_NEWINSTANCE_SIGNATURE = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] ArrayCopy = "arraycopy".toCharArray(); //$NON-NLS-1$
	public static final char[] ArrayCopySignature = "(Ljava/lang/Object;ILjava/lang/Object;II)V".toCharArray(); //$NON-NLS-1$
	public static final char[] ArrayJavaLangClassConstantPoolName = "[Ljava/lang/Class;".toCharArray(); //$NON-NLS-1$
	public static final char[] ArrayJavaLangObjectConstantPoolName = "[Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] booleanBooleanSignature = "(Z)Ljava/lang/Boolean;".toCharArray(); //$NON-NLS-1$
	public static final char[] BooleanConstrSignature = "(Z)V".toCharArray(); //$NON-NLS-1$
	public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_NAME = "booleanValue".toCharArray(); //$NON-NLS-1$
	public static final char[] BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE = "()Z".toCharArray(); //$NON-NLS-1$
	public static final char[] byteByteSignature = "(B)Ljava/lang/Byte;".toCharArray(); //$NON-NLS-1$
	public static final char[] ByteConstrSignature = "(B)V".toCharArray(); //$NON-NLS-1$
	public static final char[] BYTEVALUE_BYTE_METHOD_NAME = "byteValue".toCharArray(); //$NON-NLS-1$
	public static final char[] BYTEVALUE_BYTE_METHOD_SIGNATURE = "()B".toCharArray(); //$NON-NLS-1$
	public static final char[] charCharacterSignature = "(C)Ljava/lang/Character;".toCharArray(); //$NON-NLS-1$
	public static final char[] CharConstrSignature = "(C)V".toCharArray(); //$NON-NLS-1$
	public static final char[] CHARVALUE_CHARACTER_METHOD_NAME = "charValue".toCharArray(); //$NON-NLS-1$
	public static final char[] CHARVALUE_CHARACTER_METHOD_SIGNATURE = "()C".toCharArray(); //$NON-NLS-1$
	public static final char[] Clinit = "<clinit>".toCharArray(); //$NON-NLS-1$
	public static final char[] DefaultConstructorSignature = "()V".toCharArray(); //$NON-NLS-1$
	public static final char[] ClinitSignature = DefaultConstructorSignature;
	public static final char[] Close = "close".toCharArray(); //$NON-NLS-1$
	public static final char[] CloseSignature = "()V".toCharArray(); //$NON-NLS-1$
	public static final char[] DesiredAssertionStatus = "desiredAssertionStatus".toCharArray(); //$NON-NLS-1$
	public static final char[] DesiredAssertionStatusSignature = "()Z".toCharArray(); //$NON-NLS-1$
	public static final char[] DoubleConstrSignature = "(D)V".toCharArray(); //$NON-NLS-1$
	public static final char[] doubleDoubleSignature = "(D)Ljava/lang/Double;".toCharArray(); //$NON-NLS-1$
	public static final char[] DOUBLEVALUE_DOUBLE_METHOD_NAME = "doubleValue".toCharArray(); //$NON-NLS-1$
	public static final char[] DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE = "()D".toCharArray(); //$NON-NLS-1$
	public static final char[] Exit = "exit".toCharArray(); //$NON-NLS-1$
	public static final char[] ExitIntSignature = "(I)V".toCharArray(); //$NON-NLS-1$
	public static final char[] FloatConstrSignature = "(F)V".toCharArray(); //$NON-NLS-1$
	public static final char[] floatFloatSignature = "(F)Ljava/lang/Float;".toCharArray(); //$NON-NLS-1$
	public static final char[] FLOATVALUE_FLOAT_METHOD_NAME = "floatValue".toCharArray(); //$NON-NLS-1$
	public static final char[] FLOATVALUE_FLOAT_METHOD_SIGNATURE = "()F".toCharArray(); //$NON-NLS-1$
	public static final char[] ForName = "forName".toCharArray(); //$NON-NLS-1$
	public static final char[] ForNameSignature = "(Ljava/lang/String;)Ljava/lang/Class;".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_BOOLEAN_METHOD_NAME = "getBoolean".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;)Z".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_BYTE_METHOD_NAME = "getByte".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;)B".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_CHAR_METHOD_NAME = "getChar".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;)C".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_DOUBLE_METHOD_NAME = "getDouble".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;)D".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_FLOAT_METHOD_NAME = "getFloat".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;)F".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_INT_METHOD_NAME = "getInt".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;)I".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_LONG_METHOD_NAME = "getLong".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;)J".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_OBJECT_METHOD_NAME = "get".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;)Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_SHORT_METHOD_NAME = "getShort".toCharArray(); //$NON-NLS-1$
	public static final char[] GET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;)S".toCharArray(); //$NON-NLS-1$
	public static final char[] GetClass = "getClass".toCharArray(); //$NON-NLS-1$
	public static final char[] GetClassSignature = "()Ljava/lang/Class;".toCharArray(); //$NON-NLS-1$
	public static final char[] GetComponentType = "getComponentType".toCharArray(); //$NON-NLS-1$
	public static final char[] GetComponentTypeSignature = GetClassSignature;
	public static final char[] GetConstructor = "getConstructor".toCharArray(); //$NON-NLS-1$
	public static final char[] GetConstructorSignature = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray(); //$NON-NLS-1$
	public static final char[] GETDECLAREDCONSTRUCTOR_NAME = "getDeclaredConstructor".toCharArray(); //$NON-NLS-1$
	public static final char[] GETDECLAREDCONSTRUCTOR_SIGNATURE = "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;".toCharArray(); //$NON-NLS-1$
	// predefined methods constant names
	public static final char[] GETDECLAREDFIELD_NAME = "getDeclaredField".toCharArray(); //$NON-NLS-1$
	public static final char[] GETDECLAREDFIELD_SIGNATURE = "(Ljava/lang/String;)Ljava/lang/reflect/Field;".toCharArray(); //$NON-NLS-1$
	public static final char[] GETDECLAREDMETHOD_NAME = "getDeclaredMethod".toCharArray(); //$NON-NLS-1$
	public static final char[] GETDECLAREDMETHOD_SIGNATURE = "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;".toCharArray(); //$NON-NLS-1$
	public static final char[] GetMessage = "getMessage".toCharArray(); //$NON-NLS-1$
	public static final char[] GetMessageSignature = "()Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] HasNext = "hasNext".toCharArray();//$NON-NLS-1$
	public static final char[] HasNextSignature = "()Z".toCharArray();//$NON-NLS-1$
	public static final char[] Init = "<init>".toCharArray(); //$NON-NLS-1$
	public static final char[] IntConstrSignature = "(I)V".toCharArray(); //$NON-NLS-1$
	public static final char[] ITERATOR_NAME = "iterator".toCharArray(); //$NON-NLS-1$
	public static final char[] ITERATOR_SIGNATURE = "()Ljava/util/Iterator;".toCharArray(); //$NON-NLS-1$
	public static final char[] Intern = "intern".toCharArray(); //$NON-NLS-1$
	public static final char[] InternSignature = GetMessageSignature;
	public static final char[] IntIntegerSignature = "(I)Ljava/lang/Integer;".toCharArray(); //$NON-NLS-1$
	public static final char[] INTVALUE_INTEGER_METHOD_NAME = "intValue".toCharArray(); //$NON-NLS-1$
	public static final char[] INTVALUE_INTEGER_METHOD_SIGNATURE = "()I".toCharArray(); //$NON-NLS-1$
	public static final char[] INVOKE_METHOD_METHOD_NAME = "invoke".toCharArray(); //$NON-NLS-1$
	public static final char[] INVOKE_METHOD_METHOD_SIGNATURE = "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[][] JAVA_LANG_REFLECT_ACCESSIBLEOBJECT = new char[][] {TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "AccessibleObject".toCharArray()}; //$NON-NLS-1$
	public static final char[][] JAVA_LANG_REFLECT_ARRAY = new char[][] {TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Array".toCharArray()}; //$NON-NLS-1$
	// predefined type constant names
	public static final char[] JavaIoPrintStreamSignature = "Ljava/io/PrintStream;".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangAssertionErrorConstantPoolName = "java/lang/AssertionError".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangBooleanConstantPoolName = "java/lang/Boolean".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangByteConstantPoolName = "java/lang/Byte".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangCharacterConstantPoolName = "java/lang/Character".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangClassConstantPoolName = "java/lang/Class".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangClassNotFoundExceptionConstantPoolName = "java/lang/ClassNotFoundException".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangClassSignature = "Ljava/lang/Class;".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangDoubleConstantPoolName = "java/lang/Double".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangEnumConstantPoolName = "java/lang/Enum".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangErrorConstantPoolName = "java/lang/Error".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangExceptionConstantPoolName = "java/lang/Exception".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangFloatConstantPoolName = "java/lang/Float".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangIntegerConstantPoolName = "java/lang/Integer".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangLongConstantPoolName = "java/lang/Long".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangNoClassDefFoundErrorConstantPoolName = "java/lang/NoClassDefFoundError".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangNoSuchFieldErrorConstantPoolName = "java/lang/NoSuchFieldError".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangObjectConstantPoolName = "java/lang/Object".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME = "java/lang/reflect/AccessibleObject".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVALANGREFLECTARRAY_CONSTANTPOOLNAME = "java/lang/reflect/Array".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangReflectConstructorConstantPoolName = "java/lang/reflect/Constructor".toCharArray();   //$NON-NLS-1$
	public static final char[] JavaLangReflectConstructorNewInstanceSignature = "([Ljava/lang/Object;)Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVALANGREFLECTFIELD_CONSTANTPOOLNAME = "java/lang/reflect/Field".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME = "java/lang/reflect/Method".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangShortConstantPoolName = "java/lang/Short".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangStringBufferConstantPoolName = "java/lang/StringBuffer".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangStringBuilderConstantPoolName = "java/lang/StringBuilder".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangStringConstantPoolName = "java/lang/String".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangStringSignature = "Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangObjectSignature = "Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangSystemConstantPoolName = "java/lang/System".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangThrowableConstantPoolName = "java/lang/Throwable".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaLangVoidConstantPoolName = "java/lang/Void".toCharArray(); //$NON-NLS-1$
	public static final char[] JavaUtilIteratorConstantPoolName = "java/util/Iterator".toCharArray(); //$NON-NLS-1$
	public static final char[] LongConstrSignature = "(J)V".toCharArray(); //$NON-NLS-1$
	public static final char[] longLongSignature = "(J)Ljava/lang/Long;".toCharArray(); //$NON-NLS-1$
	public static final char[] LONGVALUE_LONG_METHOD_NAME = "longValue".toCharArray(); //$NON-NLS-1$
	public static final char[] LONGVALUE_LONG_METHOD_SIGNATURE = "()J".toCharArray(); //$NON-NLS-1$
	public static final char[] NewInstance = "newInstance".toCharArray(); //$NON-NLS-1$
	public static final char[] NewInstanceSignature = "(Ljava/lang/Class;[I)Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] Next = "next".toCharArray();//$NON-NLS-1$
	public static final char[] NextSignature = "()Ljava/lang/Object;".toCharArray();//$NON-NLS-1$
	public static final char[] ObjectConstrSignature = "(Ljava/lang/Object;)V".toCharArray(); //$NON-NLS-1$
	public static final char[] ObjectSignature = "Ljava/lang/Object;".toCharArray(); //$NON-NLS-1$
	public static final char[] Ordinal = "ordinal".toCharArray(); //$NON-NLS-1$
	public static final char[] OrdinalSignature = "()I".toCharArray(); //$NON-NLS-1$
	public static final char[] Out = "out".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_BOOLEAN_METHOD_NAME = "setBoolean".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_BOOLEAN_METHOD_SIGNATURE = "(Ljava/lang/Object;Z)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_BYTE_METHOD_NAME = "setByte".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_BYTE_METHOD_SIGNATURE = "(Ljava/lang/Object;B)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_CHAR_METHOD_NAME = "setChar".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_CHAR_METHOD_SIGNATURE = "(Ljava/lang/Object;C)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_DOUBLE_METHOD_NAME = "setDouble".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_DOUBLE_METHOD_SIGNATURE = "(Ljava/lang/Object;D)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_FLOAT_METHOD_NAME = "setFloat".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_FLOAT_METHOD_SIGNATURE = "(Ljava/lang/Object;F)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_INT_METHOD_NAME = "setInt".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_INT_METHOD_SIGNATURE = "(Ljava/lang/Object;I)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_LONG_METHOD_NAME = "setLong".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_LONG_METHOD_SIGNATURE = "(Ljava/lang/Object;J)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_OBJECT_METHOD_NAME = "set".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_OBJECT_METHOD_SIGNATURE = "(Ljava/lang/Object;Ljava/lang/Object;)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_SHORT_METHOD_NAME = "setShort".toCharArray(); //$NON-NLS-1$
	public static final char[] SET_SHORT_METHOD_SIGNATURE = "(Ljava/lang/Object;S)V".toCharArray(); //$NON-NLS-1$
	public static final char[] SETACCESSIBLE_NAME = "setAccessible".toCharArray(); //$NON-NLS-1$
	public static final char[] SETACCESSIBLE_SIGNATURE = "(Z)V".toCharArray(); //$NON-NLS-1$
	public static final char[] ShortConstrSignature = "(S)V".toCharArray(); //$NON-NLS-1$
	public static final char[] shortShortSignature = "(S)Ljava/lang/Short;".toCharArray(); //$NON-NLS-1$
	public static final char[] SHORTVALUE_SHORT_METHOD_NAME = "shortValue".toCharArray(); //$NON-NLS-1$
	public static final char[] SHORTVALUE_SHORT_METHOD_SIGNATURE = "()S".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendBooleanSignature = "(Z)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendCharSignature = "(C)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendDoubleSignature = "(D)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendFloatSignature = "(F)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendIntSignature = "(I)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendLongSignature = "(J)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBufferAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuffer;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendBooleanSignature = "(Z)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendCharSignature = "(C)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendDoubleSignature = "(D)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendFloatSignature = "(F)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendIntSignature = "(I)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendLongSignature = "(J)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendObjectSignature = "(Ljava/lang/Object;)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringBuilderAppendStringSignature = "(Ljava/lang/String;)Ljava/lang/StringBuilder;".toCharArray(); //$NON-NLS-1$
	public static final char[] StringConstructorSignature = "(Ljava/lang/String;)V".toCharArray(); //$NON-NLS-1$
	public static final char[] This = "this".toCharArray(); //$NON-NLS-1$
	public static final char[] ToString = "toString".toCharArray(); //$NON-NLS-1$
	public static final char[] ToStringSignature = GetMessageSignature;
	public static final char[] TYPE = "TYPE".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOf = "valueOf".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfBooleanSignature = "(Z)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfCharSignature = "(C)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfDoubleSignature = "(D)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfFloatSignature = "(F)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfIntSignature = "(I)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfLongSignature = "(J)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfObjectSignature = "(Ljava/lang/Object;)Ljava/lang/String;".toCharArray(); //$NON-NLS-1$
	public static final char[] ValueOfStringClassSignature = "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_DOCUMENTED = "Ljava/lang/annotation/Documented;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_ELEMENTTYPE = "Ljava/lang/annotation/ElementType;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_RETENTION = "Ljava/lang/annotation/Retention;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = "Ljava/lang/annotation/RetentionPolicy;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_TARGET = "Ljava/lang/annotation/Target;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_DEPRECATED = "Ljava/lang/Deprecated;".toCharArray(); //$NON-NLS-1$
	public static final char[] JAVA_LANG_ANNOTATION_INHERITED = "Ljava/lang/annotation/Inherited;".toCharArray(); //$NON-NLS-1$
	// java 7  java.lang.SafeVarargs
	public static final char[] JAVA_LANG_SAFEVARARGS = "Ljava/lang/SafeVarargs;".toCharArray(); //$NON-NLS-1$
	// java 7 java.lang.invoke.MethodHandle.invokeExact(..)/invokeGeneric(..)
	public static final char[] JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE = "Ljava/lang/invoke/MethodHandle$PolymorphicSignature;".toCharArray(); //$NON-NLS-1$

	public static final char[] HashCode = "hashCode".toCharArray(); //$NON-NLS-1$
	public static final char[] HashCodeSignature = "()I".toCharArray(); //$NON-NLS-1$; 
	public static final char[] Equals = "equals".toCharArray(); //$NON-NLS-1$
	public static final char[] EqualsSignature = "(Ljava/lang/Object;)Z".toCharArray(); //$NON-NLS-1$; 
	public static final char[] AddSuppressed = "addSuppressed".toCharArray(); //$NON-NLS-1$;
	public static final char[] AddSuppressedSignature = "(Ljava/lang/Throwable;)V".toCharArray(); //$NON-NLS-1$
	/**
	 * ConstantPool constructor comment.
	 */
	public ConstantPool(ClassFile classFile) {
		this.UTF8Cache = new CharArrayCache(UTF8_INITIAL_SIZE);
		this.stringCache = new CharArrayCache(STRING_INITIAL_SIZE);
		this.methodsAndFieldsCache = new HashtableOfObject(METHODS_AND_FIELDS_INITIAL_SIZE);
		this.classCache = new CharArrayCache(CLASS_INITIAL_SIZE);
		this.nameAndTypeCacheForFieldsAndMethods = new HashtableOfObject(NAMEANDTYPE_INITIAL_SIZE);
		this.offsets = new int[5];
		initialize(classFile);
	}
	public void initialize(ClassFile givenClassFile) {
		this.poolContent = givenClassFile.header;
		this.currentOffset = givenClassFile.headerOffset;
		// currentOffset is initialized to 0 by default
		this.currentIndex = 1;
		this.classFile = givenClassFile;
	}
	/**
	 * Return the content of the receiver
	 */
	public byte[] dumpBytes() {
		System.arraycopy(this.poolContent, 0, (this.poolContent = new byte[this.currentOffset]), 0, this.currentOffset);
		return this.poolContent;
	}
	public int literalIndex(byte[] utf8encoding, char[] stringCharArray) {
		int index;
		if ((index = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
			// The entry doesn't exit yet
			if ((index = -index)> 0xFFFF) {
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			this.currentIndex++;
			// Write the tag first
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(Utf8Tag);
			int utf8encodingLength = utf8encoding.length;
			if (this.currentOffset + 2 + utf8encodingLength >= this.poolContent.length) {
				// we need to resize the poolContent array because we won't have
				// enough space to write the length
				resizePoolContents(2 + utf8encodingLength);
			}
			this.poolContent[this.currentOffset++] = (byte) (utf8encodingLength >> 8);
			this.poolContent[this.currentOffset++] = (byte) utf8encodingLength;
			// add in once the whole byte array
			System.arraycopy(utf8encoding, 0, this.poolContent, this.currentOffset, utf8encodingLength);
			this.currentOffset += utf8encodingLength;
		}
		return index;
	}
	public int literalIndex(TypeBinding binding) {
		TypeBinding typeBinding = binding.leafComponentType();
		if ((typeBinding.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
			Util.recordNestedType(this.classFile, typeBinding);
		}
		return literalIndex(binding.signature());
	}
	/**
	 * This method returns the index into the constantPool corresponding to the type descriptor.
	 *
	 * @param utf8Constant char[]
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(char[] utf8Constant) {
		int index;
		if ((index = this.UTF8Cache.putIfAbsent(utf8Constant, this.currentIndex)) < 0) {
			if ((index = -index)> 0xFFFF) {
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// The entry doesn't exit yet
			// Write the tag first
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(Utf8Tag);
			// Then the size of the stringName array
			int savedCurrentOffset = this.currentOffset;
			if (this.currentOffset + 2 >= this.poolContent.length) {
				// we need to resize the poolContent array because we won't have
				// enough space to write the length
				resizePoolContents(2);
			}
			this.currentOffset += 2;
			length = 0;
			for (int i = 0; i < utf8Constant.length; i++) {
				char current = utf8Constant[i];
				if ((current >= 0x0001) && (current <= 0x007F)) {
					// we only need one byte: ASCII table
					writeU1(current);
					length++;
				} else {
					if (current > 0x07FF) {
						// we need 3 bytes
						length += 3;
						writeU1(0xE0 | ((current >> 12) & 0x0F)); // 0xE0 = 1110 0000
						writeU1(0x80 | ((current >> 6) & 0x3F)); // 0x80 = 1000 0000
						writeU1(0x80 | (current & 0x3F)); // 0x80 = 1000 0000
					} else {
						// we can be 0 or between 0x0080 and 0x07FF
						// In that case we only need 2 bytes
						length += 2;
						writeU1(0xC0 | ((current >> 6) & 0x1F)); // 0xC0 = 1100 0000
						writeU1(0x80 | (current & 0x3F)); // 0x80 = 1000 0000
					}
				}
			}
			if (length >= 65535) {
				this.currentOffset = savedCurrentOffset - 1;
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceForConstant(this.classFile.referenceBinding.scope.referenceType());
			}
			if (index > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			this.currentIndex++;
			// Now we know the length that we have to write in the constant pool
			// we use savedCurrentOffset to do that
			this.poolContent[savedCurrentOffset] = (byte) (length >> 8);
			this.poolContent[savedCurrentOffset + 1] = (byte) length;
		}
		return index;
	}
	public int literalIndex(char[] stringCharArray, byte[] utf8encoding) {
		int index;
		if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
			// The entry doesn't exit yet
			this.currentIndex++;
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// Write the tag first
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(StringTag);
			// Then the string index
			int stringIndexOffset = this.currentOffset;
			if (this.currentOffset + 2 >= this.poolContent.length) {
				resizePoolContents(2);
			}
			this.currentOffset+=2;

			final int stringIndex = literalIndex(utf8encoding, stringCharArray);
			this.poolContent[stringIndexOffset++] = (byte) (stringIndex >> 8);
			this.poolContent[stringIndexOffset] = (byte) stringIndex;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the double
	 * value. If the double is not already present into the pool, it is added. The
	 * double cache is updated and it returns the right index.
	 *
	 * @param key <CODE>double</CODE>
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(double key) {
		//Retrieve the index from the cache
		// The double constant takes two indexes into the constant pool, but we only store
		// the first index into the long table
		int index;
		// lazy initialization for base type caches
		// If it is null, initialize it, otherwise use it
		if (this.doubleCache == null) {
			this.doubleCache = new DoubleCache(DOUBLE_INITIAL_SIZE);
		}
		if ((index = this.doubleCache.putIfAbsent(key, this.currentIndex)) < 0) {
			if ((index = -index)> 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			this.currentIndex += 2; // a double needs an extra place into the constant pool
			// Write the double into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(DoubleTag);
			// Then add the 8 bytes representing the double
			long temp = java.lang.Double.doubleToLongBits(key);
			length = this.poolContent.length;
			if (this.currentOffset + 8 >= length) {
				resizePoolContents(8);
			}
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 56);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 48);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 40);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 32);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 24);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 16);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 8);
			this.poolContent[this.currentOffset++] = (byte) temp;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the float
	 * value. If the float is not already present into the pool, it is added. The
	 * int cache is updated and it returns the right index.
	 *
	 * @param key <CODE>float</CODE>
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(float key) {
		//Retrieve the index from the cache
		int index;
		// lazy initialization for base type caches
		// If it is null, initialize it, otherwise use it
		if (this.floatCache == null) {
			this.floatCache = new FloatCache(FLOAT_INITIAL_SIZE);
		}
		if ((index = this.floatCache.putIfAbsent(key, this.currentIndex)) < 0) {
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			this.currentIndex++;
			// Write the float constant entry into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(FloatTag);
			// Then add the 4 bytes representing the float
			int temp = java.lang.Float.floatToIntBits(key);
			if (this.currentOffset + 4 >= this.poolContent.length) {
				resizePoolContents(4);
			}
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 24);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 16);
			this.poolContent[this.currentOffset++] = (byte) (temp >>> 8);
			this.poolContent[this.currentOffset++] = (byte) temp;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the int
	 * value. If the int is not already present into the pool, it is added. The
	 * int cache is updated and it returns the right index.
	 *
	 * @param key <CODE>int</CODE>
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(int key) {
		//Retrieve the index from the cache
		int index;
		// lazy initialization for base type caches
		// If it is null, initialize it, otherwise use it
		if (this.intCache == null) {
			this.intCache = new IntegerCache(INT_INITIAL_SIZE);
		}
		if ((index = this.intCache.putIfAbsent(key, this.currentIndex)) < 0) {
			this.currentIndex++;
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// Write the integer constant entry into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(IntegerTag);
			// Then add the 4 bytes representing the int
			if (this.currentOffset + 4 >= this.poolContent.length) {
				resizePoolContents(4);
			}
			this.poolContent[this.currentOffset++] = (byte) (key >>> 24);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 16);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 8);
			this.poolContent[this.currentOffset++] = (byte) key;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the long
	 * value. If the long is not already present into the pool, it is added. The
	 * long cache is updated and it returns the right index.
	 *
	 * @param key <CODE>long</CODE>
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(long key) {
		// Retrieve the index from the cache
		// The long constant takes two indexes into the constant pool, but we only store
		// the first index into the long table
		int index;
		// lazy initialization for base type caches
		// If it is null, initialize it, otherwise use it
		if (this.longCache == null) {
			this.longCache = new LongCache(LONG_INITIAL_SIZE);
		}
		if ((index = this.longCache.putIfAbsent(key, this.currentIndex)) < 0) {
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			this.currentIndex+= 2; // long value need an extra place into thwe constant pool
			// Write the long into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(LongTag);
			// Then add the 8 bytes representing the long
			if (this.currentOffset + 8 >= this.poolContent.length) {
				resizePoolContents(8);
			}
			this.poolContent[this.currentOffset++] = (byte) (key >>> 56);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 48);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 40);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 32);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 24);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 16);
			this.poolContent[this.currentOffset++] = (byte) (key >>> 8);
			this.poolContent[this.currentOffset++] = (byte) key;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the type descriptor.
	 *
	 * @param stringConstant java.lang.String
	 * @return <CODE>int</CODE>
	 */
	public int literalIndex(String stringConstant) {
		int index;
		char[] stringCharArray = stringConstant.toCharArray();
		if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
			// The entry doesn't exit yet
			this.currentIndex++;
			if ((index  = -index)> 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// Write the tag first
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(StringTag);
			// Then the string index
			int stringIndexOffset = this.currentOffset;
			if (this.currentOffset + 2 >= this.poolContent.length) {
				resizePoolContents(2);
			}
			this.currentOffset+=2;
			final int stringIndex = literalIndex(stringCharArray);
			this.poolContent[stringIndexOffset++] = (byte) (stringIndex >> 8);
			this.poolContent[stringIndexOffset] = (byte) stringIndex;
		}
		return index;
	}
	public int literalIndexForType(final char[] constantPoolName) {
		int index;
		if ((index = this.classCache.putIfAbsent(constantPoolName, this.currentIndex)) < 0) {
			// The entry doesn't exit yet
			this.currentIndex++;
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(ClassTag);

			// Then the name index
			int nameIndexOffset = this.currentOffset;
			if (this.currentOffset + 2 >= this.poolContent.length) {
				resizePoolContents(2);
			}
			this.currentOffset+=2;
			final int nameIndex = literalIndex(constantPoolName);
			this.poolContent[nameIndexOffset++] = (byte) (nameIndex >> 8);
			this.poolContent[nameIndexOffset] = (byte) nameIndex;
		}
		return index;
	}
	/*
	 * This method returns the index into the constantPool corresponding to the type descriptor
	 * corresponding to a type constant pool name
	 * binding must not be an array type.
	 */
	public int literalIndexForType(final TypeBinding binding) {
		TypeBinding typeBinding = binding.leafComponentType();
		if ((typeBinding.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
			Util.recordNestedType(this.classFile, typeBinding);
		}
		return this.literalIndexForType(binding.constantPoolName());
	}
	public int literalIndexForMethod(char[] declaringClass, char[] selector, char[] signature, boolean isInterface) {
		int index;
		if ((index = putInCacheIfAbsent(declaringClass, selector, signature, this.currentIndex)) < 0) {
			// it doesn't exist yet
			this.currentIndex++;
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// Write the interface method ref constant into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(isInterface ? InterfaceMethodRefTag : MethodRefTag);

			int classIndexOffset = this.currentOffset;
			if (this.currentOffset + 4 >= this.poolContent.length) {
				resizePoolContents(4);
			}
			this.currentOffset+=4;

			final int classIndex = literalIndexForType(declaringClass);
			final int nameAndTypeIndex = literalIndexForNameAndType(selector, signature);

			this.poolContent[classIndexOffset++] = (byte) (classIndex >> 8);
			this.poolContent[classIndexOffset++] = (byte) classIndex;
			this.poolContent[classIndexOffset++] = (byte) (nameAndTypeIndex >> 8);
			this.poolContent[classIndexOffset] = (byte) nameAndTypeIndex;
		}
		return index;
	}
	public int literalIndexForMethod(TypeBinding declaringClass, char[] selector, char[] signature, boolean isInterface) {
		if ((declaringClass.tagBits & TagBits.ContainsNestedTypeReferences) != 0) {
			Util.recordNestedType(this.classFile, declaringClass);
		}
		return this.literalIndexForMethod(declaringClass.constantPoolName(), selector, signature, isInterface);
	}
	public int literalIndexForNameAndType(char[] name, char[] signature) {
		int index;
		if ((index = putInNameAndTypeCacheIfAbsent(name, signature, this.currentIndex)) < 0) {
			// The entry doesn't exit yet
			this.currentIndex++;
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(NameAndTypeTag);
			int nameIndexOffset = this.currentOffset;
			if (this.currentOffset + 4 >= this.poolContent.length) {
				resizePoolContents(4);
			}
			this.currentOffset+=4;

			final int nameIndex = literalIndex(name);
			final int typeIndex = literalIndex(signature);
			this.poolContent[nameIndexOffset++] = (byte) (nameIndex >> 8);
			this.poolContent[nameIndexOffset++] = (byte) nameIndex;
			this.poolContent[nameIndexOffset++] = (byte) (typeIndex >> 8);
			this.poolContent[nameIndexOffset] = (byte) typeIndex;
		}
		return index;
	}
	public int literalIndexForField(char[] declaringClass, char[] name, char[] signature) {
		int index;
		if ((index = putInCacheIfAbsent(declaringClass, name, signature, this.currentIndex)) < 0) {
			this.currentIndex++;
			// doesn't exist yet
			if ((index = -index) > 0xFFFF){
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// Write the interface method ref constant into the constant pool
			// First add the tag
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(FieldRefTag);
			int classIndexOffset = this.currentOffset;
			if (this.currentOffset + 4 >= this.poolContent.length) {
				resizePoolContents(4);
			}
			this.currentOffset+=4;

			final int classIndex = literalIndexForType(declaringClass);
			final int nameAndTypeIndex = literalIndexForNameAndType(name, signature);

			this.poolContent[classIndexOffset++] = (byte) (classIndex >> 8);
			this.poolContent[classIndexOffset++] = (byte) classIndex;
			this.poolContent[classIndexOffset++] = (byte) (nameAndTypeIndex >> 8);
			this.poolContent[classIndexOffset] = (byte) nameAndTypeIndex;
		}
		return index;
	}
	/**
	 * This method returns the index into the constantPool corresponding to the type descriptor.
	 *
	 * @param stringCharArray char[]
	 * @return <CODE>int</CODE>
	 */
	public int literalIndexForLdc(char[] stringCharArray) {
		int savedCurrentIndex = this.currentIndex;
		int savedCurrentOffset = this.currentOffset;
		int index;
		if ((index = this.stringCache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
			if ((index = -index)> 0xFFFF) {
				this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
			}
			// The entry doesn't exit yet
			this.currentIndex++;
			// Write the tag first
			int length = this.offsets.length;
			if (length <= index) {
				// resize
				System.arraycopy(this.offsets, 0, (this.offsets = new int[index * 2]), 0, length);
			}
			this.offsets[index] = this.currentOffset;
			writeU1(StringTag);

			// Then the string index
			int stringIndexOffset = this.currentOffset;
			if (this.currentOffset + 2 >= this.poolContent.length) {
				resizePoolContents(2);
			}
			this.currentOffset+=2;

			int stringIndex;
			if ((stringIndex = this.UTF8Cache.putIfAbsent(stringCharArray, this.currentIndex)) < 0) {
				if ((stringIndex = -stringIndex)> 0xFFFF) {
					this.classFile.referenceBinding.scope.problemReporter().noMoreAvailableSpaceInConstantPool(this.classFile.referenceBinding.scope.referenceType());
				}
				// The entry doesn't exit yet
				this.currentIndex++;
				// Write the tag first
				length = this.offsets.length;
				if (length <= stringIndex) {
					// resize
					System.arraycopy(this.offsets, 0, (this.offsets = new int[stringIndex * 2]), 0, length);
				}
				this.offsets[stringIndex] = this.currentOffset;
				writeU1(Utf8Tag);
				// Then the size of the stringName array
				int lengthOffset = this.currentOffset;
				if (this.currentOffset + 2 >= this.poolContent.length) {
					// we need to resize the poolContent array because we won't have
					// enough space to write the length
					resizePoolContents(2);
				}
				this.currentOffset += 2;
				length = 0;
				for (int i = 0; i < stringCharArray.length; i++) {
					char current = stringCharArray[i];
					if ((current >= 0x0001) && (current <= 0x007F)) {
						// we only need one byte: ASCII table
						length++;
						if (this.currentOffset + 1 >= this.poolContent.length) {
							// we need to resize the poolContent array because we won't have
							// enough space to write the length
							resizePoolContents(1);
						}
						this.poolContent[this.currentOffset++] = (byte)(current);
					} else
						if (current > 0x07FF) {
							// we need 3 bytes
							length += 3;
							if (this.currentOffset + 3 >= this.poolContent.length) {
								// we need to resize the poolContent array because we won't have
								// enough space to write the length
								resizePoolContents(3);
							}
							this.poolContent[this.currentOffset++] = (byte) (0xE0 | ((current >> 12) & 0x0F)); // 0xE0 = 1110 0000
							this.poolContent[this.currentOffset++] = (byte) (0x80 | ((current >> 6) & 0x3F)); // 0x80 = 1000 0000
							this.poolContent[this.currentOffset++] = (byte) (0x80 | (current & 0x3F)); // 0x80 = 1000 0000
						} else {
							if (this.currentOffset + 2 >= this.poolContent.length) {
								// we need to resize the poolContent array because we won't have
								// enough space to write the length
								resizePoolContents(2);
							}
							// we can be 0 or between 0x0080 and 0x07FF
							// In that case we only need 2 bytes
							length += 2;
							this.poolContent[this.currentOffset++] = (byte) (0xC0 | ((current >> 6) & 0x1F)); // 0xC0 = 1100 0000
							this.poolContent[this.currentOffset++] = (byte) (0x80 | (current & 0x3F)); // 0x80 = 1000 0000
						}
				}
				if (length >= 65535) {
					this.currentOffset = savedCurrentOffset;
					this.currentIndex = savedCurrentIndex;
					this.stringCache.remove(stringCharArray);
					this.UTF8Cache.remove(stringCharArray);
					return 0;
				}
				this.poolContent[lengthOffset++] = (byte) (length >> 8);
				this.poolContent[lengthOffset] = (byte) length;
			}
			this.poolContent[stringIndexOffset++] = (byte) (stringIndex >> 8);
			this.poolContent[stringIndexOffset] = (byte) stringIndex;
		}
		return index;
	}
	/**
	 * @param key1 the given name
	 * @param key2 the given signature
	 * @param value the given index
	 * @return the new index
	 */
	private int putInNameAndTypeCacheIfAbsent(final char[] key1, final char[] key2, int value) {
		int index ;
		Object key1Value = this.nameAndTypeCacheForFieldsAndMethods.get(key1);
		if (key1Value == null) {
			CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key2, value);
			index = -value;
			this.nameAndTypeCacheForFieldsAndMethods.put(key1, cachedIndexEntry);
		} else if (key1Value instanceof CachedIndexEntry) {
			// adding a second entry
			CachedIndexEntry entry = (CachedIndexEntry) key1Value;
			if (CharOperation.equals(key2, entry.signature)) {
				index = entry.index;
			} else {
				CharArrayCache charArrayCache = new CharArrayCache();
				charArrayCache.putIfAbsent(entry.signature, entry.index);
				index = charArrayCache.putIfAbsent(key2, value);
				this.nameAndTypeCacheForFieldsAndMethods.put(key1, charArrayCache);
			}
		} else {
			CharArrayCache charArrayCache = (CharArrayCache) key1Value;
			index = charArrayCache.putIfAbsent(key2, value);
		}
		return index;
	}
	/**
	 * @param key1 the given declaring class name
	 * @param key2 the given field name or method selector
	 * @param key3 the given signature
	 * @param value the new index
	 * @return the given index
	 */
	private int putInCacheIfAbsent(final char[] key1, final char[] key2, final char[] key3, int value) {
		int index;
		HashtableOfObject key1Value = (HashtableOfObject) this.methodsAndFieldsCache.get(key1);
		if (key1Value == null) {
			key1Value = new HashtableOfObject();
			this.methodsAndFieldsCache.put(key1, key1Value);
			CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
			index = -value;
			key1Value.put(key2, cachedIndexEntry);
		} else {
			Object key2Value = key1Value.get(key2);
			if (key2Value == null) {
				CachedIndexEntry cachedIndexEntry = new CachedIndexEntry(key3, value);
				index = -value;
				key1Value.put(key2, cachedIndexEntry);
			} else if (key2Value instanceof CachedIndexEntry) {
				// adding a second entry
				CachedIndexEntry entry = (CachedIndexEntry) key2Value;
				if (CharOperation.equals(key3, entry.signature)) {
					index = entry.index;
				} else {
					CharArrayCache charArrayCache = new CharArrayCache();
					charArrayCache.putIfAbsent(entry.signature, entry.index);
					index = charArrayCache.putIfAbsent(key3, value);
					key1Value.put(key2, charArrayCache);
				}
			} else {
				CharArrayCache charArrayCache = (CharArrayCache) key2Value;
				index = charArrayCache.putIfAbsent(key3, value);
			}
		}
		return index;
	}
	/**
	 * This method is used to clean the receiver in case of a clinit header is generated, but the
	 * clinit has no code.
	 * This implementation assumes that the clinit is the first method to be generated.
	 * @see org.eclipse.jdt.internal.compiler.ast.TypeDeclaration#addClinit()
	 */
	public void resetForClinit(int constantPoolIndex, int constantPoolOffset) {
		this.currentIndex = constantPoolIndex;
		this.currentOffset = constantPoolOffset;
		if (this.UTF8Cache.get(AttributeNamesConstants.CodeName) >= constantPoolIndex) {
			this.UTF8Cache.remove(AttributeNamesConstants.CodeName);
		}
		if (this.UTF8Cache.get(ConstantPool.ClinitSignature) >= constantPoolIndex) {
			this.UTF8Cache.remove(ConstantPool.ClinitSignature);
		}
		if (this.UTF8Cache.get(ConstantPool.Clinit) >= constantPoolIndex) {
			this.UTF8Cache.remove(ConstantPool.Clinit);
		}
	}

	/**
	 * Resize the pool contents
	 */
	private final void resizePoolContents(int minimalSize) {
		int length = this.poolContent.length;
		int toAdd = length;
		if (toAdd < minimalSize)
			toAdd = minimalSize;
		System.arraycopy(this.poolContent, 0, this.poolContent = new byte[length + toAdd], 0, length);
	}
	/**
	 * Write a unsigned byte into the byte array
	 *
	 * @param value <CODE>int</CODE> The value to write into the byte array
	 */
	protected final void writeU1(int value) {
		if (this.currentOffset + 1 >= this.poolContent.length) {
			resizePoolContents(1);
		}
		this.poolContent[this.currentOffset++] = (byte) value;
	}
	/**
	 * Write a unsigned byte into the byte array
	 *
	 * @param value <CODE>int</CODE> The value to write into the byte array
	 */
	protected final void writeU2(int value) {
		if (this.currentOffset + 2 >= this.poolContent.length) {
			resizePoolContents(2);
		}
		this.poolContent[this.currentOffset++] = (byte) (value >>> 8);
		this.poolContent[this.currentOffset++] = (byte) value;
	}
	public void reset() {
		if (this.doubleCache != null) this.doubleCache.clear();
		if (this.floatCache != null) this.floatCache.clear();
		if (this.intCache != null) this.intCache.clear();
		if (this.longCache != null) this.longCache.clear();
		this.UTF8Cache.clear();
		this.stringCache.clear();
		this.methodsAndFieldsCache.clear();
		this.classCache.clear();
		this.nameAndTypeCacheForFieldsAndMethods.clear();
		this.currentIndex = 1;
		this.currentOffset = 0;
	}
	public void resetForAttributeName(char[] attributeName, int constantPoolIndex, int constantPoolOffset) {
		this.currentIndex = constantPoolIndex;
		this.currentOffset = constantPoolOffset;
		if (this.UTF8Cache.get(attributeName) >= constantPoolIndex) {
			this.UTF8Cache.remove(attributeName);
		}
	}
}
