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

public class AnnotationMethodInfoWithAnnotations extends AnnotationMethodInfo {
	private AnnotationInfo[] annotations;

AnnotationMethodInfoWithAnnotations(MethodInfo methodInfo, Object defaultValue, AnnotationInfo[] annotations) {
	super(methodInfo, defaultValue);
	this.annotations = annotations;
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
