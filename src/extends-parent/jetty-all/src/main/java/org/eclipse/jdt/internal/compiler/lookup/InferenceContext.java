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
package org.eclipse.jdt.internal.compiler.lookup;

/**
 * Context used during type inference for a generic method invocation
 */
public class InferenceContext {

	private TypeBinding[][][] collectedSubstitutes;
	MethodBinding genericMethod;
	int depth;
	int status;
	TypeBinding expectedType;
	boolean hasExplicitExpectedType; // indicates whether the expectedType (if set) was explicit in code, or set by default
    public boolean isUnchecked;
	TypeBinding[] substitutes;
	final static int FAILED = 1;

public InferenceContext(MethodBinding genericMethod) {
	this.genericMethod = genericMethod;
	TypeVariableBinding[] typeVariables = genericMethod.typeVariables;
	int varLength = typeVariables.length;
	this.collectedSubstitutes = new TypeBinding[varLength][3][];
	this.substitutes = new TypeBinding[varLength];
}

public TypeBinding[] getSubstitutes(TypeVariableBinding typeVariable, int constraint) {
	return this.collectedSubstitutes[typeVariable.rank][constraint];
}

/**
 * Returns true if any unresolved variable is detected, i.e. any variable is substituted with itself
 */
public boolean hasUnresolvedTypeArgument() {
	for (int i = 0, varLength = this.substitutes.length; i <varLength; i++) {
		if (this.substitutes[i] == null) {
			return true;
		}
	}
	return false;
}

public void recordSubstitute(TypeVariableBinding typeVariable, TypeBinding actualType, int constraint) {
    TypeBinding[][] variableSubstitutes = this.collectedSubstitutes[typeVariable.rank];
    insertLoop: {
    	TypeBinding[] constraintSubstitutes = variableSubstitutes[constraint];
    	int length;
    	if (constraintSubstitutes == null) {
    		length = 0;
    		constraintSubstitutes = new TypeBinding[1];
    	} else {
    		length = constraintSubstitutes.length;
	        for (int i = 0; i < length; i++) {
	        	TypeBinding substitute = constraintSubstitutes[i];
	            if (substitute == actualType) return; // already there
	            if (substitute == null) {
	                constraintSubstitutes[i] = actualType;
	                break insertLoop;
	            }
	        }
	        // no free spot found, need to grow by one
	        System.arraycopy(constraintSubstitutes, 0, constraintSubstitutes = new TypeBinding[length+1], 0, length);
    	}
        constraintSubstitutes[length] = actualType;
        variableSubstitutes[constraint] = constraintSubstitutes;
    }
}
public String toString() {
	StringBuffer buffer = new StringBuffer(20);
	buffer.append("InferenceContex for ");//$NON-NLS-1$
	for (int i = 0, length = this.genericMethod.typeVariables.length; i < length; i++) {
		buffer.append(this.genericMethod.typeVariables[i]);
	}
	buffer.append(this.genericMethod);
	buffer.append("\n\t[status=");//$NON-NLS-1$
	switch(this.status) {
		case 0 :
			buffer.append("ok]");//$NON-NLS-1$
			break;
		case FAILED :
			buffer.append("failed]");//$NON-NLS-1$
			break;
	}
	if (this.expectedType == null) {
		buffer.append(" [expectedType=null]"); //$NON-NLS-1$
	} else {
		buffer.append(" [expectedType=").append(this.expectedType.shortReadableName()).append(']'); //$NON-NLS-1$
	}
	buffer.append(" [depth=").append(this.depth).append(']'); //$NON-NLS-1$
	buffer.append("\n\t[collected={");//$NON-NLS-1$
	for (int i = 0, length = this.collectedSubstitutes == null ? 0 : this.collectedSubstitutes.length; i < length; i++) {
		TypeBinding[][] collected = this.collectedSubstitutes[i];
		for (int j = TypeConstants.CONSTRAINT_EQUAL; j <= TypeConstants.CONSTRAINT_SUPER; j++) {
			TypeBinding[] constraintCollected = collected[j];
			if (constraintCollected != null) {
				for (int k = 0, clength = constraintCollected.length; k < clength; k++) {
					buffer.append("\n\t\t").append(this.genericMethod.typeVariables[i].sourceName); //$NON-NLS-1$
					switch (j) {
						case TypeConstants.CONSTRAINT_EQUAL :
							buffer.append("="); //$NON-NLS-1$
							break;
						case TypeConstants.CONSTRAINT_EXTENDS :
							buffer.append("<:"); //$NON-NLS-1$
							break;
						case TypeConstants.CONSTRAINT_SUPER :
							buffer.append(">:"); //$NON-NLS-1$
							break;
					}
					if (constraintCollected[k] != null) {
						buffer.append(constraintCollected[k].shortReadableName());
					}
				}
			}
		}
	}
	buffer.append("}]");//$NON-NLS-1$
	buffer.append("\n\t[inferred=");//$NON-NLS-1$
	int count = 0;
	for (int i = 0, length = this.substitutes == null ? 0 : this.substitutes.length; i < length; i++) {
		if (this.substitutes[i] == null) continue;
		count++;
		buffer.append('{').append(this.genericMethod.typeVariables[i].sourceName);
		buffer.append("=").append(this.substitutes[i].shortReadableName()).append('}'); //$NON-NLS-1$
	}
	if (count == 0) buffer.append("{}"); //$NON-NLS-1$
	buffer.append(']');
	return buffer.toString();
}
}
