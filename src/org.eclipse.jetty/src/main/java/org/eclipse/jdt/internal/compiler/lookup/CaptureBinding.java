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
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

public class CaptureBinding extends TypeVariableBinding {

	public TypeBinding lowerBound;
	public WildcardBinding wildcard;
	public int captureID;

	/* information to compute unique binding key */
	public ReferenceBinding sourceType;
	public int position;

	public CaptureBinding(WildcardBinding wildcard, ReferenceBinding sourceType, int position, int captureID) {
		super(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX, null, 0, wildcard.environment);
		this.wildcard = wildcard;
		this.modifiers = ClassFileConstants.AccPublic | ExtraCompilerModifiers.AccGenericSignature; // treat capture as public
		this.fPackage = wildcard.fPackage;
		this.sourceType = sourceType;
		this.position = position;
		this.captureID = captureID;
	}

	/*
	 * sourceTypeKey ! wildcardKey position semi-colon
	 * p.X { capture of ? } --> !*123; (Lp/X; in declaring type except if leaf)
	 * p.X { capture of ? extends p.Y } --> !+Lp/Y;123; (Lp/X; in declaring type except if leaf)
	 */
	public char[] computeUniqueKey(boolean isLeaf) {
		StringBuffer buffer = new StringBuffer();
		if (isLeaf) {
			buffer.append(this.sourceType.computeUniqueKey(false/*not a leaf*/));
			buffer.append('&');
		}
		buffer.append(TypeConstants.WILDCARD_CAPTURE);
		buffer.append(this.wildcard.computeUniqueKey(false/*not a leaf*/));
		buffer.append(this.position);
		buffer.append(';');
		int length = buffer.length();
		char[] uniqueKey = new char[length];
		buffer.getChars(0, length, uniqueKey, 0);
		return uniqueKey;
	}

	public String debugName() {

		if (this.wildcard != null) {
			StringBuffer buffer = new StringBuffer(10);
			buffer
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
				.append(this.captureID)
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
				.append(this.wildcard.debugName());
			return buffer.toString();
		}
		return super.debugName();
	}

	public char[] genericTypeSignature() {
		if (this.genericTypeSignature == null) {
			this.genericTypeSignature = CharOperation.concat(TypeConstants.WILDCARD_CAPTURE, this.wildcard.genericTypeSignature());
		}
		return this.genericTypeSignature;
	}

	/**
	 * Initialize capture bounds using substituted supertypes
	 * e.g. given X<U, V extends X<U, V>>,     capture(X<E,?>) = X<E,capture>, where capture extends X<E,capture>
	 */
	public void initializeBounds(Scope scope, ParameterizedTypeBinding capturedParameterizedType) {
		TypeVariableBinding wildcardVariable = this.wildcard.typeVariable();
		if (wildcardVariable == null) {
			// error resilience when capturing Zork<?>
			// no substitution for wildcard bound (only formal bounds from type variables are to be substituted: 104082)
			TypeBinding originalWildcardBound = this.wildcard.bound;
			switch (this.wildcard.boundKind) {
				case Wildcard.EXTENDS :
					// still need to capture bound supertype as well so as not to expose wildcards to the outside (111208)
					TypeBinding capturedWildcardBound = originalWildcardBound.capture(scope, this.position);
					if (originalWildcardBound.isInterface()) {
						this.superclass = scope.getJavaLangObject();
						this.superInterfaces = new ReferenceBinding[] { (ReferenceBinding) capturedWildcardBound };
					} else {
						// the wildcard bound should be a subtype of variable superclass
						// it may occur that the bound is less specific, then consider glb (202404)
						if (capturedWildcardBound.isArrayType() || capturedWildcardBound == this) {
							this.superclass = scope.getJavaLangObject();
						} else {
							this.superclass = (ReferenceBinding) capturedWildcardBound;
						}
						this.superInterfaces = Binding.NO_SUPERINTERFACES;
					}
					this.firstBound =  capturedWildcardBound;
					if ((capturedWildcardBound.tagBits & TagBits.HasTypeVariable) == 0)
						this.tagBits &= ~TagBits.HasTypeVariable;
					break;
				case Wildcard.UNBOUND :
					this.superclass = scope.getJavaLangObject();
					this.superInterfaces = Binding.NO_SUPERINTERFACES;
					this.tagBits &= ~TagBits.HasTypeVariable;
					break;
				case Wildcard.SUPER :
					this.superclass = scope.getJavaLangObject();
					this.superInterfaces = Binding.NO_SUPERINTERFACES;
					this.lowerBound = this.wildcard.bound;
					if ((originalWildcardBound.tagBits & TagBits.HasTypeVariable) == 0)
						this.tagBits &= ~TagBits.HasTypeVariable;
					break;
			}
			return;
		}
		ReferenceBinding originalVariableSuperclass = wildcardVariable.superclass;
		ReferenceBinding substitutedVariableSuperclass = (ReferenceBinding) Scope.substitute(capturedParameterizedType, originalVariableSuperclass);
		// prevent cyclic capture: given X<T>, capture(X<? extends T> could yield a circular type
		if (substitutedVariableSuperclass == this) substitutedVariableSuperclass = originalVariableSuperclass;

		ReferenceBinding[] originalVariableInterfaces = wildcardVariable.superInterfaces();
		ReferenceBinding[] substitutedVariableInterfaces = Scope.substitute(capturedParameterizedType, originalVariableInterfaces);
		if (substitutedVariableInterfaces != originalVariableInterfaces) {
			// prevent cyclic capture: given X<T>, capture(X<? extends T> could yield a circular type
			for (int i = 0, length = substitutedVariableInterfaces.length; i < length; i++) {
				if (substitutedVariableInterfaces[i] == this) substitutedVariableInterfaces[i] = originalVariableInterfaces[i];
			}
		}
		// no substitution for wildcard bound (only formal bounds from type variables are to be substituted: 104082)
		TypeBinding originalWildcardBound = this.wildcard.bound;

		switch (this.wildcard.boundKind) {
			case Wildcard.EXTENDS :
				// still need to capture bound supertype as well so as not to expose wildcards to the outside (111208)
				TypeBinding capturedWildcardBound = originalWildcardBound.capture(scope, this.position);
				if (originalWildcardBound.isInterface()) {
					this.superclass = substitutedVariableSuperclass;
					// merge wildcard bound into variable superinterfaces using glb
					if (substitutedVariableInterfaces == Binding.NO_SUPERINTERFACES) {
						this.superInterfaces = new ReferenceBinding[] { (ReferenceBinding) capturedWildcardBound };
					} else {
						int length = substitutedVariableInterfaces.length;
						System.arraycopy(substitutedVariableInterfaces, 0, substitutedVariableInterfaces = new ReferenceBinding[length+1], 1, length);
						substitutedVariableInterfaces[0] =  (ReferenceBinding) capturedWildcardBound;
						this.superInterfaces = Scope.greaterLowerBound(substitutedVariableInterfaces);
					}
				} else {
					// the wildcard bound should be a subtype of variable superclass
					// it may occur that the bound is less specific, then consider glb (202404)
					if (capturedWildcardBound.isArrayType() || capturedWildcardBound == this) {
						this.superclass = substitutedVariableSuperclass;
					} else {
						this.superclass = (ReferenceBinding) capturedWildcardBound;
						if (this.superclass.isSuperclassOf(substitutedVariableSuperclass)) {
							this.superclass = substitutedVariableSuperclass;
						}
					}
					this.superInterfaces = substitutedVariableInterfaces;
				}
				this.firstBound =  capturedWildcardBound;
				if ((capturedWildcardBound.tagBits & TagBits.HasTypeVariable) == 0)
					this.tagBits &= ~TagBits.HasTypeVariable;
				break;
			case Wildcard.UNBOUND :
				this.superclass = substitutedVariableSuperclass;
				this.superInterfaces = substitutedVariableInterfaces;
				this.tagBits &= ~TagBits.HasTypeVariable;
				break;
			case Wildcard.SUPER :
				this.superclass = substitutedVariableSuperclass;
				if (wildcardVariable.firstBound == substitutedVariableSuperclass || originalWildcardBound == substitutedVariableSuperclass) {
					this.firstBound = substitutedVariableSuperclass;
				}
				this.superInterfaces = substitutedVariableInterfaces;
				this.lowerBound = originalWildcardBound;
				if ((originalWildcardBound.tagBits & TagBits.HasTypeVariable) == 0)
					this.tagBits &= ~TagBits.HasTypeVariable;
				break;
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.lookup.TypeBinding#isCapture()
	 */
	public boolean isCapture() {
		return true;
	}

	/**
	 * @see TypeBinding#isEquivalentTo(TypeBinding)
	 */
	public boolean isEquivalentTo(TypeBinding otherType) {
	    if (this == otherType) return true;
	    if (otherType == null) return false;
		// capture of ? extends X[]
		if (this.firstBound != null && this.firstBound.isArrayType()) {
			if (this.firstBound.isCompatibleWith(otherType))
				return true;
		}
		switch (otherType.kind()) {
			case Binding.WILDCARD_TYPE :
			case Binding.INTERSECTION_TYPE :
				return ((WildcardBinding) otherType).boundCheck(this);
		}
		return false;
	}

	public char[] readableName() {
		if (this.wildcard != null) {
			StringBuffer buffer = new StringBuffer(10);
			buffer
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
				.append(this.captureID)
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
				.append(this.wildcard.readableName());
			int length = buffer.length();
			char[] name = new char[length];
			buffer.getChars(0, length, name, 0);
			return name;
		}
		return super.readableName();
	}

	public char[] shortReadableName() {
		if (this.wildcard != null) {
			StringBuffer buffer = new StringBuffer(10);
			buffer
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
				.append(this.captureID)
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
				.append(this.wildcard.shortReadableName());
			int length = buffer.length();
			char[] name = new char[length];
			buffer.getChars(0, length, name, 0);
			return name;
		}
		return super.shortReadableName();
	}

	public String toString() {
		if (this.wildcard != null) {
			StringBuffer buffer = new StringBuffer(10);
			buffer
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_PREFIX)
				.append(this.captureID)
				.append(TypeConstants.WILDCARD_CAPTURE_NAME_SUFFIX)
				.append(this.wildcard);
			return buffer.toString();
		}
		return super.toString();
	}
}
