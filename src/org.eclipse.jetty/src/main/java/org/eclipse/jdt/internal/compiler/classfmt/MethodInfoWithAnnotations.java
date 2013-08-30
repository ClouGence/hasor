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
package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class MethodInfoWithAnnotations extends MethodInfo {
	protected AnnotationInfo[] annotations;

MethodInfoWithAnnotations(MethodInfo methodInfo, AnnotationInfo[] annotations) {
	super(methodInfo.reference, methodInfo.constantPoolOffsets, methodInfo.structOffset);
	this.annotations = annotations;

	this.accessFlags = methodInfo.accessFlags;
	this.attributeBytes = methodInfo.attributeBytes;
	this.descriptor = methodInfo.descriptor;
	this.exceptionNames = methodInfo.exceptionNames;
	this.name = methodInfo.name;
	this.signature = methodInfo.signature;
	this.signatureUtf8Offset = methodInfo.signatureUtf8Offset;
	this.tagBits = methodInfo.tagBits;
}
public IBinaryAnnotation[] getAnnotations() {
	return this.annotations;
}
protected void initialize() {
	for (int i = 0, l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
		if (this.annotations[i] != null)
			this.annotations[i].initialize();
	super.initialize();
}
protected void reset() {
	for (int i = 0, l = this.annotations == null ? 0 : this.annotations.length; i < l; i++)
		if (this.annotations[i] != null)
			this.annotations[i].reset();
	super.reset();
}
protected void toStringContent(StringBuffer buffer) {
	super.toStringContent(buffer);
	for (int i = 0, l = this.annotations == null ? 0 : this.annotations.length; i < l; i++) {
		buffer.append(this.annotations[i]);
		buffer.append('\n');
	}
}
}
