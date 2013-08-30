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

import org.eclipse.jdt.core.compiler.CharOperation;

public final class MemberTypeBinding extends NestedTypeBinding {
public MemberTypeBinding(char[][] compoundName, ClassScope scope, SourceTypeBinding enclosingType) {
	super(compoundName, scope, enclosingType);
	this.tagBits |= TagBits.MemberTypeMask;
}
void checkSyntheticArgsAndFields() {
	if (isStatic()) return;
	if (isInterface()) return;
	this.addSyntheticArgumentAndField(this.enclosingType);
}
/* Answer the receiver's constant pool name.
*
* NOTE: This method should only be used during/after code gen.
*/

public char[] constantPoolName() /* java/lang/Object */ {
	if (this.constantPoolName != null)
		return this.constantPoolName;

	return this.constantPoolName = CharOperation.concat(enclosingType().constantPoolName(), this.sourceName, '$');
}

/**
 * @see org.eclipse.jdt.internal.compiler.lookup.Binding#initializeDeprecatedAnnotationTagBits()
 */
public void initializeDeprecatedAnnotationTagBits() {
	if ((this.tagBits & TagBits.DeprecatedAnnotationResolved) == 0) {
		super.initializeDeprecatedAnnotationTagBits();
		if ((this.tagBits & TagBits.AnnotationDeprecated) == 0) {
			// check enclosing type
			ReferenceBinding enclosing;
			if (((enclosing = enclosingType()).tagBits & TagBits.DeprecatedAnnotationResolved) == 0) {
				enclosing.initializeDeprecatedAnnotationTagBits();
			}
			if (enclosing.isViewedAsDeprecated()) {
				this.modifiers |= ExtraCompilerModifiers.AccDeprecatedImplicitly;
			}
		}
	}
}
public String toString() {
	return "Member type : " + new String(sourceName()) + " " + super.toString(); //$NON-NLS-2$ //$NON-NLS-1$
}
}
