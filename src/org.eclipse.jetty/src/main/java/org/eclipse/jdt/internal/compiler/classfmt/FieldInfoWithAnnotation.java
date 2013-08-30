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

public final class FieldInfoWithAnnotation extends FieldInfo {
	private AnnotationInfo[] annotations;

FieldInfoWithAnnotation(FieldInfo info, AnnotationInfo[] annos) {
	super(info.reference, info.constantPoolOffsets, info.structOffset);
	this.accessFlags = info.accessFlags;
	this.attributeBytes = info.attributeBytes;
	this.constant = info.constant;
	this.constantPoolOffsets = info.constantPoolOffsets;
	this.descriptor = info.descriptor;
	this.name = info.name;
	this.signature = info.signature;
	this.signatureUtf8Offset = info.signatureUtf8Offset;
	this.tagBits = info.tagBits;
	this.wrappedConstantValue = info.wrappedConstantValue;
	this.annotations = annos;
}
public org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation[] getAnnotations() {
	return this.annotations;
}
protected void initialize() {
	for (int i = 0, max = this.annotations.length; i < max; i++)
		this.annotations[i].initialize();
	super.initialize();
}
protected void reset() {
	if (this.annotations != null)
		for (int i = 0, max = this.annotations.length; i < max; i++)
			this.annotations[i].reset();
	super.reset();
}
public String toString() {
	StringBuffer buffer = new StringBuffer(getClass().getName());
	if (this.annotations != null) {
		buffer.append('\n');
		for (int i = 0; i < this.annotations.length; i++) {
			buffer.append(this.annotations[i]);
			buffer.append('\n');
		}
	}
	toStringContent(buffer);
	return buffer.toString();
}
}
