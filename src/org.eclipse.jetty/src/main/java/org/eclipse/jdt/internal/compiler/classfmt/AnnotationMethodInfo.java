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
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;

public class AnnotationMethodInfo extends MethodInfo {
	protected Object defaultValue = null;

public static MethodInfo createAnnotationMethod(byte classFileBytes[], int offsets[], int offset) {
	MethodInfo methodInfo = new MethodInfo(classFileBytes, offsets, offset);
	int attributesCount = methodInfo.u2At(6);
	int readOffset = 8;
	AnnotationInfo[] annotations = null;
	Object defaultValue = null;
	for (int i = 0; i < attributesCount; i++) {
		// check the name of each attribute
		int utf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset)] - methodInfo.structOffset;
		char[] attributeName = methodInfo.utf8At(utf8Offset + 3, methodInfo.u2At(utf8Offset + 1));
		if (attributeName.length > 0) {
			switch(attributeName[0]) {
				case 'A':
					if (CharOperation.equals(attributeName, AttributeNamesConstants.AnnotationDefaultName)) {
						// readOffset + 6 so the offset is at the start of the 'member_value' entry
						// u2 attribute_name_index + u4 attribute_length = + 6
						AnnotationInfo info =
							new AnnotationInfo(methodInfo.reference, methodInfo.constantPoolOffsets, readOffset + 6 + methodInfo.structOffset);
						defaultValue = info.decodeDefaultValue();
					}
					break;
				case 'S' :
					if (CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName))
						methodInfo.signatureUtf8Offset = methodInfo.constantPoolOffsets[methodInfo.u2At(readOffset + 6)] - methodInfo.structOffset;
					break;
				case 'R' :
					AnnotationInfo[] methodAnnotations = null;
					if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
						methodAnnotations = decodeMethodAnnotations(readOffset, true, methodInfo);
					} else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
						methodAnnotations = decodeMethodAnnotations(readOffset, false, methodInfo);
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
					}
					break;
			}
		}
		readOffset += (6 + methodInfo.u4At(readOffset + 2));
	}
	methodInfo.attributeBytes = readOffset;

	if (defaultValue != null) {
		if (annotations != null) {
			return new AnnotationMethodInfoWithAnnotations(methodInfo, defaultValue, annotations);
		}
		return new AnnotationMethodInfo(methodInfo, defaultValue);
	}
	if (annotations != null)
		return new MethodInfoWithAnnotations(methodInfo, annotations);
	return methodInfo;
}

AnnotationMethodInfo(MethodInfo methodInfo, Object defaultValue) {
	super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset);
	this.defaultValue = defaultValue;

	this.accessFlags = methodInfo.accessFlags;
	this.attributeBytes = methodInfo.attributeBytes;
	this.descriptor = methodInfo.descriptor;
	this.exceptionNames = methodInfo.exceptionNames;
	this.name = methodInfo.name;
	this.signature = methodInfo.signature;
	this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
	this.tagBits = methodInfo.tagBits;
}
public Object getDefaultValue() {
	return this.defaultValue;
}
protected void toStringContent(StringBuffer buffer) {
	super.toStringContent(buffer);
	if (this.defaultValue != null) {
		buffer.append(" default "); //$NON-NLS-1$
		if (this.defaultValue instanceof Object[]) {
			buffer.append('{');
			Object[] elements = (Object[]) this.defaultValue;
			for (int i = 0, len = elements.length; i < len; i++) {
				if (i > 0)
					buffer.append(", "); //$NON-NLS-1$
				buffer.append(elements[i]);
			}
			buffer.append('}');
		} else {
			buffer.append(this.defaultValue);
		}
		buffer.append('\n');
	}
}
}
