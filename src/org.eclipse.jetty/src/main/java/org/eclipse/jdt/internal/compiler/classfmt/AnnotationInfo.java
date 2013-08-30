/*******************************************************************************
 * Copyright (c) 2005, 2011 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tyeung@bea.com - initial API and implementation
 *    olivier_thomann@ca.ibm.com - add hashCode() and equals(..) methods
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.*;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;
import org.eclipse.jdt.internal.compiler.util.Util;

public class AnnotationInfo extends ClassFileStruct implements IBinaryAnnotation {
	/** The name of the annotation type */
	private char[] typename;
	/**
	 * null until this annotation is initialized
	 * @see #getElementValuePairs()
	 */
	private ElementValuePairInfo[] pairs;

	long standardAnnotationTagBits = 0;
	int readOffset = 0;

	static Object[] EmptyValueArray = new Object[0];

AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset) {
	super(classFileBytes, contantPoolOffsets, offset);
}
/**
 * @param classFileBytes
 * @param offset the offset into <code>classFileBytes</code> for the "type_index" of the annotation attribute.
 * @param populate <code>true</code> to indicate to build out the annotation structure.
 */
AnnotationInfo(byte[] classFileBytes, int[] contantPoolOffsets, int offset, boolean runtimeVisible, boolean populate) {
	this(classFileBytes, contantPoolOffsets, offset);
	if (populate)
		decodeAnnotation();
	else
		this.readOffset = scanAnnotation(0, runtimeVisible, true);
}
private void decodeAnnotation() {
	this.readOffset = 0;
	int utf8Offset = this.constantPoolOffsets[u2At(0)] - this.structOffset;
	this.typename = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
	int numberOfPairs = u2At(2);
	// u2 type_index + u2 num_member_value_pair
	this.readOffset += 4;
	this.pairs = numberOfPairs == 0 ? ElementValuePairInfo.NoMembers : new ElementValuePairInfo[numberOfPairs];
	for (int i = 0; i < numberOfPairs; i++) {
		// u2 member_name_index;
		utf8Offset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
		char[] membername = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
		this.readOffset += 2;
		Object value = decodeDefaultValue();
		this.pairs[i] = new ElementValuePairInfo(membername, value);
	}
}
Object decodeDefaultValue() {
	Object value = null;
	// u1 tag;
	int tag = u1At(this.readOffset);
	this.readOffset++;
	int constValueOffset = -1;
	switch (tag) {
		case 'Z': // boolean constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = BooleanConstant.fromValue(i4At(constValueOffset + 1) == 1);
			this.readOffset += 2;
			break;
		case 'I': // integer constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = IntConstant.fromValue(i4At(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'C': // char constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = CharConstant.fromValue((char) i4At(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'B': // byte constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = ByteConstant.fromValue((byte) i4At(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'S': // short constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = ShortConstant.fromValue((short) i4At(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'D': // double constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = DoubleConstant.fromValue(doubleAt(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'F': // float constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = FloatConstant.fromValue(floatAt(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 'J': // long constant
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = LongConstant.fromValue(i8At(constValueOffset + 1));
			this.readOffset += 2;
			break;
		case 's': // String
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			value = StringConstant.fromValue(String.valueOf(utf8At(constValueOffset + 3, u2At(constValueOffset + 1))));
			this.readOffset += 2;
			break;
		case 'e':
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			char[] typeName = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
			this.readOffset += 2;
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			char[] constName = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
			this.readOffset += 2;
			value = new EnumConstantSignature(typeName, constName);
			break;
		case 'c':
			constValueOffset = this.constantPoolOffsets[u2At(this.readOffset)] - this.structOffset;
			char[] className = utf8At(constValueOffset + 3, u2At(constValueOffset + 1));
			value = new ClassSignature(className);
			this.readOffset += 2;
			break;
		case '@':
			value = new AnnotationInfo(this.reference, this.constantPoolOffsets, this.readOffset + this.structOffset, false, true);
			this.readOffset += ((AnnotationInfo) value).readOffset;
			break;
		case '[':
			int numberOfValues = u2At(this.readOffset);
			this.readOffset += 2;
			if (numberOfValues == 0) {
				value = EmptyValueArray;
			} else {
				Object[] arrayElements = new Object[numberOfValues];
				value = arrayElements;
				for (int i = 0; i < numberOfValues; i++)
					arrayElements[i] = decodeDefaultValue();
			}
			break;
		default:
			throw new IllegalStateException("Unrecognized tag " + (char) tag); //$NON-NLS-1$
	}
	return value;
}
public IBinaryElementValuePair[] getElementValuePairs() {
	if (this.pairs == null)
		initialize();
	return this.pairs;
}
public char[] getTypeName() {
	return this.typename;
}
void initialize() {
	if (this.pairs == null)
		decodeAnnotation();
}
private int readRetentionPolicy(int offset) {
	int currentOffset = offset;
	int tag = u1At(currentOffset);
	currentOffset++;
	switch (tag) {
		case 'e':
			int utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
			char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
			currentOffset += 2;
			if (typeName.length == 38 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTIONPOLICY)) {
				utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
				char[] constName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
				this.standardAnnotationTagBits |= Annotation.getRetentionPolicy(constName);
			}
			currentOffset += 2;
			break;
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's':
		case 'c':
			currentOffset += 2;
			break;
		case '@':
			// none of the supported standard annotation are in the nested
			// level.
			currentOffset = scanAnnotation(currentOffset, false, false);
			break;
		case '[':
			int numberOfValues = u2At(currentOffset);
			currentOffset += 2;
			for (int i = 0; i < numberOfValues; i++)
				currentOffset = scanElementValue(currentOffset);
			break;
		default:
			throw new IllegalStateException();
	}
	return currentOffset;
}
private int readTargetValue(int offset) {
	int currentOffset = offset;
	int tag = u1At(currentOffset);
	currentOffset++;
	switch (tag) {
		case 'e':
			int utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
			char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
			currentOffset += 2;
			if (typeName.length == 34 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_ELEMENTTYPE)) {
				utf8Offset = this.constantPoolOffsets[u2At(currentOffset)] - this.structOffset;
				char[] constName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
				this.standardAnnotationTagBits |= Annotation.getTargetElementType(constName);
			}
			currentOffset += 2;
			break;
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's':
		case 'c':
			currentOffset += 2;
			break;
		case '@':
			// none of the supported standard annotation are in the nested
			// level.
			currentOffset = scanAnnotation(currentOffset, false, false);
			break;
		case '[':
			int numberOfValues = u2At(currentOffset);
			currentOffset += 2;
			if (numberOfValues == 0) {
				this.standardAnnotationTagBits |= TagBits.AnnotationTarget;
			} else {
				for (int i = 0; i < numberOfValues; i++)
					currentOffset = readTargetValue(currentOffset);
			}
			break;
		default:
			throw new IllegalStateException();
	}
	return currentOffset;
}
/**
 * Read through this annotation in order to figure out the necessary tag
 * bits and the length of this annotation. The data structure will not be
 * flushed out.
 *
 * The tag bits are derived from the following (supported) standard
 * annotation. java.lang.annotation.Documented,
 * java.lang.annotation.Retention, java.lang.annotation.Target, and
 * java.lang.Deprecated
 *
 * @param expectRuntimeVisibleAnno
 *            <code>true</cod> to indicate that this is a runtime-visible annotation
 * @param toplevel <code>false</code> to indicate that an nested annotation is read.
 * 		<code>true</code> otherwise
 * @return the next offset to read.
 */
private int scanAnnotation(int offset, boolean expectRuntimeVisibleAnno, boolean toplevel) {
	int currentOffset = offset;
	int utf8Offset = this.constantPoolOffsets[u2At(offset)] - this.structOffset;
	char[] typeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
	if (toplevel)
		this.typename = typeName;
	int numberOfPairs = u2At(offset + 2);
	// u2 type_index + u2 number_member_value_pair
	currentOffset += 4;
	if (expectRuntimeVisibleAnno && toplevel) {
		switch (typeName.length) {
			case 22:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_DEPRECATED)) {
					this.standardAnnotationTagBits |= TagBits.AnnotationDeprecated;
					return currentOffset;
				}
				break;
			case 23:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_SAFEVARARGS)) {
					this.standardAnnotationTagBits |= TagBits.AnnotationSafeVarargs;
					return currentOffset;
				}
				break;
			case 29:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_TARGET)) {
					currentOffset += 2;
					return readTargetValue(currentOffset);
				}
				break;
			case 32:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTION)) {
					currentOffset += 2;
					return readRetentionPolicy(currentOffset);
				}
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_INHERITED)) {
					this.standardAnnotationTagBits |= TagBits.AnnotationInherited;
					return currentOffset;
				}
				break;
			case 33:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_DOCUMENTED)) {
					this.standardAnnotationTagBits |= TagBits.AnnotationDocumented;
					return currentOffset;
				}
				break;
			case 52:
				if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE)) {
					this.standardAnnotationTagBits |= TagBits.AnnotationPolymorphicSignature;
					return currentOffset;
				}
				break;
		}
	}
	for (int i = 0; i < numberOfPairs; i++) {
		// u2 member_name_index
		currentOffset += 2;
		currentOffset = scanElementValue(currentOffset);
	}
	return currentOffset;
}
/**
 * @param offset
 *            the offset to start reading.
 * @return the next offset to read.
 */
private int scanElementValue(int offset) {
	int currentOffset = offset;
	int tag = u1At(currentOffset);
	currentOffset++;
	switch (tag) {
		case 'B':
		case 'C':
		case 'D':
		case 'F':
		case 'I':
		case 'J':
		case 'S':
		case 'Z':
		case 's':
		case 'c':
			currentOffset += 2;
			break;
		case 'e':
			currentOffset += 4;
			break;
		case '@':
			// none of the supported standard annotation are in the nested
			// level.
			currentOffset = scanAnnotation(currentOffset, false, false);
			break;
		case '[':
			int numberOfValues = u2At(currentOffset);
			currentOffset += 2;
			for (int i = 0; i < numberOfValues; i++)
				currentOffset = scanElementValue(currentOffset);
			break;
		default:
			throw new IllegalStateException();
	}
	return currentOffset;
}
public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append('@');
	buffer.append(this.typename);
	if (this.pairs != null) {
		buffer.append('(');
		buffer.append("\n\t"); //$NON-NLS-1$
		for (int i = 0, len = this.pairs.length; i < len; i++) {
			if (i > 0)
				buffer.append(",\n\t"); //$NON-NLS-1$
			buffer.append(this.pairs[i]);
		}
		buffer.append(')');
	}
	return buffer.toString();
}
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Util.hashCode(this.pairs);
	result = prime * result + CharOperation.hashCode(this.typename);
	return result;
}
public boolean equals(Object obj) {
	if (this == obj) {
		return true;
	}
	if (obj == null) {
		return false;
	}
	if (getClass() != obj.getClass()) {
		return false;
	}
	AnnotationInfo other = (AnnotationInfo) obj;
	if (!Arrays.equals(this.pairs, other.pairs)) {
		return false;
	}
	if (!Arrays.equals(this.typename, other.typename)) {
		return false;
	}
	return true;
}
}
