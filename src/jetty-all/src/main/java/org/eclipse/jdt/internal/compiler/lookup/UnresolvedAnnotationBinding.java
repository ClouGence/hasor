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
package org.eclipse.jdt.internal.compiler.lookup;

public class UnresolvedAnnotationBinding extends AnnotationBinding {
	private LookupEnvironment env;
	private boolean typeUnresolved = true;

UnresolvedAnnotationBinding(ReferenceBinding type, ElementValuePair[] pairs, LookupEnvironment env) {
	super(type, pairs);
	this.env = env;
}

public ReferenceBinding getAnnotationType() {
	if (this.typeUnresolved) { // the type is resolved when requested
		this.type = (ReferenceBinding) BinaryTypeBinding.resolveType(this.type, this.env, false /* no raw conversion for now */);
			// annotation type are never parameterized
		this.typeUnresolved = false;
	}
	return this.type;
}

public ElementValuePair[] getElementValuePairs() {
	if (this.env != null) {
		if (this.typeUnresolved) {
			getAnnotationType(); // resolve the annotation type
		}
		// resolve method binding and value type (if unresolved) for each pair
		for (int i = this.pairs.length; --i >= 0;) {
			ElementValuePair pair = this.pairs[i];
			MethodBinding[] methods = this.type.getMethods(pair.getName());
			// there should be exactly one since the type is an annotation type.
			if (methods != null && methods.length == 1) {
				pair.setMethodBinding(methods[0]);
			} // else silently leave a null there
			Object value = pair.getValue();
			if (value instanceof UnresolvedReferenceBinding) {
				pair.setValue(((UnresolvedReferenceBinding) value).
						resolve(this.env, false));
							// no parameterized types in annotation values
			} // do nothing for UnresolvedAnnotationBinding-s, since their
			  // content is only accessed through get* methods
		}
		this.env = null;
	}
	return this.pairs;
}
}
