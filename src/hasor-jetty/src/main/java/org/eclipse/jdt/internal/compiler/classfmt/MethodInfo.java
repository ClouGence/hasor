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
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
import org.eclipse.jdt.internal.compiler.util.Util;

public class MethodInfo extends ClassFileStruct implements IBinaryMethod, Comparable {
	static private final char[][] noException = CharOperation.NO_CHAR_CHAR;
	static private final char[][] noArgumentNames = CharOperation.NO_CHAR_CHAR;
	protected int accessFlags;
	protected int attributeBytes;
	protected char[] descriptor;
	protected char[][] exceptionNames;
	protected char[] name;
	protected char[] signature;
	protected int signatureUtf8Offset;
	protected long tagBits;
	protected char[][] argumentNames;
	protected int argumentNamesIndex;

public static MethodInfo createMethod(byte classFileBytes[], int offsets[], int offset) {
	MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
	int attributesCount = methodInfo.u2At(6);
	int readOffset = 8;
	AnnotationInfo[] annotations = null;
	AnnotationInfo[][] parameterAnnotations = null;
	for (int i = 0; i < attributesCount; i++) {
		// check the name of each attribute
		int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
		char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
		if (attributeName.length > 0) {
			switch(attributeName[0]) {
				case 'S' :
					if (CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName))
						methodInfo.signatureUtf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset;
					break;
				case 'R' :
					AnnotationInfo[] methodAnnotations = null;
					AnnotationInfo[][] paramAnnotations = null;
					if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
						methodAnnotations = decodeMethodAnnotations(readOffset, true, methodInfo);
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
						methodAnnotations = decodeMethodAnnotations(readOffset, false, methodInfo);
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName)) {
						paramAnnotations = decodeParamAnnotations(readOffset, true, methodInfo);
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName)) {
						paramAnnotations = decodeParamAnnotations(readOffset, false, methodInfo);
					}
					if (methodAnnotations != null) {
						if (annotations == null) {
							annotations = methodAnnotations;
						} else {
							int length = annotations.length;
							AnnotationInfo[] newAnnotations = new AnnotationInfo[length + methodAnnotations.length];
							System.arraycopy(annotations, 0, newAnnotations, 0, length);
							System.arraycopy(methodAnnotations, 0, newAnnotations, length, methodAnnotations.length);
							annotations = newAnnotations;
						}
					} else if (paramAnnotations != null) {
						int numberOfParameters = paramAnnotations.length;
						if (parameterAnnotations == null) {
							parameterAnnotations = paramAnnotations;
						} else {
							for (int p = 0; p < numberOfParameters; p++) {
								int numberOfAnnotations = paramAnnotations[p] == null ? 0 : paramAnnotations[p].length;
								if (numberOfAnnotations > 0) {
									if (parameterAnnotations[p] == null) {
										parameterAnnotations[p] = paramAnnotations[p];
									} else {
										int length = parameterAnnotations[p].length;
										AnnotationInfo[] newAnnotations = new AnnotationInfo[length + numberOfAnnotations];
										System.arraycopy(parameterAnnotations[p], 0, newAnnotations, 0, length);
										System.arraycopy(paramAnnotations[p], 0, newAnnotations, length, numberOfAnnotations);
										parameterAnnotations[p] = newAnnotations;
									}
								}
							}
						}
					}
					break;
			}
		}
		readOffset += (6 + methodInfo.u4At(readOffset + 2));
	}
	methodInfo.attributeBytes = readOffset;

	if (parameterAnnotations != null)
		return new MethodInfoWithParameterAnnotations(methodInfo, annotations, parameterAnnotations);
	if (annotations != null)
		return new MethodInfoWithAnnotations(methodInfo, annotations);
	return methodInfo;
}
static AnnotationInfo[] decodeAnnotations(int offset, boolean runtimeVisible, int numberOfAnnotations, MethodInfo methodInfo) {
	AnnotationInfo[] result = new AnnotationInfo[numberOfAnnotations];
	int readOffset = offset;
	for (int i = 0; i < numberOfAnnotations; i++) {
		result[i] = new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets,
			readOffset + methodInfo.structOffset, runtimeVisible, false);
		readOffset += result[i].readOffset;
	}
	return result;
}
static AnnotationInfo[] decodeMethodAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
	int numberOfAnnotations = methodInfo.u2At(offset + 6);
	if (numberOfAnnotations > 0) {
		AnnotationInfo[] annos = decodeAnnotations(offset + 8, runtimeVisible, numberOfAnnotations, methodInfo);
		if (runtimeVisible){
			int numStandardAnnotations = 0;
			for( int i=0; i<numberOfAnnotations; i++ ){
				long standardAnnoTagBits = annos[i].standardAnnotationTagBits;
				methodInfo.tagBits |= standardAnnoTagBits;
				if(standardAnnoTagBits != 0){
					annos[i] = null;
					numStandardAnnotations ++;
				}
			}

			if( numStandardAnnotations != 0 ){
				if( numStandardAnnotations == numberOfAnnotations )
					return null;

				// need to resize
				AnnotationInfo[] temp = new AnnotationInfo[numberOfAnnotations - numStandardAnnotations ];
				int tmpIndex = 0;
				for (int i = 0; i < numberOfAnnotations; i++)
					if (annos[i] != null)
						temp[tmpIndex ++] = annos[i];
				annos = temp;
			}
		}
		return annos;
	}
	return null;
}
static AnnotationInfo[][] decodeParamAnnotations(int offset, boolean runtimeVisible, MethodInfo methodInfo) {
	AnnotationInfo[][] allParamAnnotations = null;
	int numberOfParameters = methodInfo.u1At(offset + 6);
	if (numberOfParameters > 0) {
		// u2 attribute_name_index + u4 attribute_length + u1 num_parameters
		int readOffset = offset + 7;
		for (int i=0 ; i < numberOfParameters; i++) {
			int numberOfAnnotations = methodInfo.u2At(readOffset);
			readOffset += 2;
			if (numberOfAnnotations > 0) {
				if (allParamAnnotations == null)
					allParamAnnotations = new AnnotationInfo[numberOfParameters][];
				AnnotationInfo[] annos = decodeAnnotations(readOffset, runtimeVisible, numberOfAnnotations, methodInfo);
				allParamAnnotations[i] = annos;
				for (int aIndex = 0; aIndex < annos.length; aIndex++)
					readOffset += annos[aIndex].readOffset;
			}
		}
	}
	return allParamAnnotations;
}

/**
 * @param classFileBytes byte[]
 * @param offsets int[]
 * @param offset int
 */
protected MethodInfo (byte classFileBytes[], int offsets[], int offset) {
	super(classFileBytes, offsets, offset);
	this.accessFlags = -1;
	this.signatureUtf8Offset = -1;
}
public int compareTo(Object o) {
	MethodInfo otherMethod = (MethodInfo) o;
	int result = new String(getSelector()).compareTo(new String(otherMethod.getSelector()));
	if (result != 0) return result;
	return new String(getMethodDescriptor()).compareTo(new String(otherMethod.getMethodDescriptor()));
}
public boolean equals(Object o) {
	if (!(o instanceof MethodInfo)) {
		return false;
	}
	MethodInfo otherMethod = (MethodInfo) o;
	return CharOperation.equals(getSelector(), otherMethod.getSelector())
			&& CharOperation.equals(getMethodDescriptor(), otherMethod.getMethodDescriptor());
}
public int hashCode() {
	return CharOperation.hashCode(getSelector()) + CharOperation.hashCode(getMethodDescriptor());
}
/**
 * @return the annotations or null if there is none.
 */
public IBinaryAnnotation[] getAnnotations() {
	return null;
}
/**
 * @see org.eclipse.jdt.internal.compiler.env.IGenericMethod#getArgumentNames()
 */
public char[][] getArgumentNames() {
	if (this.argumentNames == null) {
		readCodeAttribute();
	}
	return this.argumentNames;
}
public Object getDefaultValue() {
	return null;
}
/**
 * Answer the resolved names of the exception types in the
 * class file format as specified in section 4.2 of the Java 2 VM spec
 * or null if the array is empty.
 *
 * For example, java.lang.String is java/lang/String.
 * @return char[][]
 */
public char[][] getExceptionTypeNames() {
	if (this.exceptionNames == null) {
		readExceptionAttributes();
	}
	return this.exceptionNames;
}
public char[] getGenericSignature() {
	if (this.signatureUtf8Offset != -1) {
		if (this.signature == null) {
			// decode the signature
			this.signature = utf8At(this.signatureUtf8Offset + 3, u2At(this.signatureUtf8Offset + 1));
		}
		return this.signature;
	}
	return null;
}
/**
 * Answer the receiver's method descriptor which describes the parameter &
 * return types as specified in section 4.3.3 of the Java 2 VM spec.
 *
 * For example:
 *   - int foo(String) is (Ljava/lang/String;)I
 *   - void foo(Object[]) is (I)[Ljava/lang/Object;
 * @return char[]
 */
public char[] getMethodDescriptor() {
	if (this.descriptor == null) {
		// read the name
		int utf8Offset = this.constantPoolOffsets[u2At(4)] - this.structOffset;
		this.descriptor = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
	}
	return this.descriptor;
}
/**
 * Answer an int whose bits are set according the access constants
 * defined by the VM spec.
 * Set the AccDeprecated and AccSynthetic bits if necessary
 * @return int
 */
public int getModifiers() {
	if (this.accessFlags == -1) {
		// compute the accessflag. Don't forget the deprecated attribute
		this.accessFlags = u2At(0);
		readModifierRelatedAttributes();
	}
	return this.accessFlags;
}
public IBinaryAnnotation[] getParameterAnnotations(int index) {
	return null;
}
/**
 * Answer the name of the method.
 *
 * For a constructor, answer <init> & <clinit> for a clinit method.
 * @return char[]
 */
public char[] getSelector() {
	if (this.name == null) {
		// read the name
		int utf8Offset = this.constantPoolOffsets[u2At(2)] - this.structOffset;
		this.name = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
	}
	return this.name;
}
public long getTagBits() {
	return this.tagBits;
}
/**
 * This method is used to fully initialize the contents of the receiver. All methodinfos, fields infos
 * will be therefore fully initialized and we can get rid of the bytes.
 */
protected void initialize() {
	getModifiers();
	getSelector();
	getMethodDescriptor();
	getExceptionTypeNames();
	getGenericSignature();
	getArgumentNames();
	reset();
}
/**
 * Answer true if the method is a class initializer, false otherwise.
 * @return boolean
 */
public boolean isClinit() {
	char[] selector = getSelector();
	return selector[0] == '<' && selector.length == 8; // Can only match <clinit>
}
/**
 * Answer true if the method is a constructor, false otherwise.
 * @return boolean
 */
public boolean isConstructor() {
	char[] selector = getSelector();
	return selector[0] == '<' && selector.length == 6; // Can only match <init>
}
/**
 * Return true if the field is a synthetic method, false otherwise.
 * @return boolean
 */
public boolean isSynthetic() {
	return (getModifiers() & ClassFileConstants.AccSynthetic) != 0;
}
private void readExceptionAttributes() {
	int attributesCount = u2At(6);
	int readOffset = 8;
	for (int i = 0; i < attributesCount; i++) {
		int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
		char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
		if (CharOperation.equals(attributeName, AttributeNamesConstants.ExceptionsName)) {
			// read the number of exception entries
			int entriesNumber = u2At(readOffset + 6);
			// place the readOffset at the beginning of the exceptions table
			readOffset += 8;
			if (entriesNumber == 0) {
				this.exceptionNames = noException;
			} else {
				this.exceptionNames = new char[entriesNumber][];
				for (int j = 0; j < entriesNumber; j++) {
					utf8Offset =
						this.constantPoolOffsets[u2At(
							this.constantPoolOffsets[u2At(readOffset)] - this.structOffset + 1)]
							- this.structOffset;
					this.exceptionNames[j] = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
					readOffset += 2;
				}
			}
		} else {
			readOffset += (6 + u4At(readOffset + 2));
		}
	}
	if (this.exceptionNames == null) {
		this.exceptionNames = noException;
	}
}
private void readModifierRelatedAttributes() {
	int attributesCount = u2At(6);
	int readOffset = 8;
	for (int i = 0; i < attributesCount; i++) {
		int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
		char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
		// test added for obfuscated .class file. See 79772
		if (attributeName.length != 0) {
			switch(attributeName[0]) {
				case 'D' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName))
						this.accessFlags |= ClassFileConstants.AccDeprecated;
					break;
				case 'S' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName))
						this.accessFlags |= ClassFileConstants.AccSynthetic;
					break;
				case 'A' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName))
						this.accessFlags |= ClassFileConstants.AccAnnotationDefault;
					break;
				case 'V' :
					if (CharOperation.equals(attributeName, AttributeNamesConstants.VarargsName))
						this.accessFlags |= ClassFileConstants.AccVarargs;
			}
		}
		readOffset += (6 + u4At(readOffset + 2));
	}
}
/**
 * Answer the size of the receiver in bytes.
 *
 * @return int
 */
public int sizeInBytes() {
	return this.attributeBytes;
}

public String toString() {
	StringBuffer buffer = new StringBuffer();
	toString(buffer);
	return buffer.toString();
}
void toString(StringBuffer buffer) {
	buffer.append(getClass().getName());
	toStringContent(buffer);
}
protected void toStringContent(StringBuffer buffer) {
	int modifiers = getModifiers();
	char[] desc = getGenericSignature();
	if (desc == null)
		desc = getMethodDescriptor();
	buffer
	.append('{')
	.append(
		((modifiers & ClassFileConstants.AccDeprecated) != 0 ? "deprecated " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0001) == 1 ? "public " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0002) == 0x0002 ? "private " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0004) == 0x0004 ? "protected " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0008) == 0x000008 ? "static " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0010) == 0x0010 ? "final " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0040) == 0x0040 ? "bridge " : Util.EMPTY_STRING) //$NON-NLS-1$
			+ ((modifiers & 0x0080) == 0x0080 ? "varargs " : Util.EMPTY_STRING)) //$NON-NLS-1$
	.append(getSelector())
	.append(desc)
	.append('}');
}
private void readCodeAttribute() {
	int attributesCount = u2At(6);
	int readOffset = 8;
	if (attributesCount != 0) {
		for (int i = 0; i < attributesCount; i++) {
			int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
			char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
			if (CharOperation.equals(attributeName, AttributeNamesConstants.CodeName)) {
				decodeCodeAttribute(readOffset);
				if (this.argumentNames == null) {
					this.argumentNames = noArgumentNames;
				}
				return;
			} else {
				readOffset += (6 + u4At(readOffset + 2));
			}
		}
	}
	this.argumentNames = noArgumentNames;
}
private void decodeCodeAttribute(int offset) {
	int readOffset = offset + 10;
	int codeLength = (int) u4At(readOffset);
	readOffset += (4 + codeLength);
	int exceptionTableLength = u2At(readOffset);
	readOffset += 2;
	if (exceptionTableLength != 0) {
		for (int i = 0; i < exceptionTableLength; i++) {
			readOffset += 8;
		}
	}
	int attributesCount = u2At(readOffset);
	readOffset += 2;
	for (int i = 0; i < attributesCount; i++) {
		int utf8Offset = this.constantPoolOffsets[u2At(readOffset)] - this.structOffset;
		char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
		if (CharOperation.equals(attributeName, AttributeNamesConstants.LocalVariableTableName)) {
			decodeLocalVariableAttribute(readOffset, codeLength);
		}
		readOffset += (6 + u4At(readOffset + 2));
	}
}
private void decodeLocalVariableAttribute(int offset, int codeLength) {
	int readOffset = offset + 6;
	final int length = u2At(readOffset);
	if (length != 0) {
		readOffset += 2;
		this.argumentNames = new char[length][];
		this.argumentNamesIndex = 0;
		for (int i = 0; i < length; i++) {
			int startPC = u2At(readOffset);
			if (startPC == 0) {
				int nameIndex = u2At(4 + readOffset);
				int utf8Offset = this.constantPoolOffsets[nameIndex] - this.structOffset;
				char[] localVariableName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
				if (!CharOperation.equals(localVariableName, ConstantPool.This)) {
					this.argumentNames[this.argumentNamesIndex++] = localVariableName;
				}
			} else {
				break;
			}
			readOffset += 10;
		}
		if (this.argumentNamesIndex != this.argumentNames.length) {
			// resize
			System.arraycopy(this.argumentNames, 0, (this.argumentNames = new char[this.argumentNamesIndex][]), 0, this.argumentNamesIndex);
		}
	}
}
}
