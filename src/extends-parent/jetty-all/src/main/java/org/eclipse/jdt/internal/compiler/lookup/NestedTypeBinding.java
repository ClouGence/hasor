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

public class NestedTypeBinding extends SourceTypeBinding {

	public SourceTypeBinding enclosingType;

	public SyntheticArgumentBinding[] enclosingInstances;
	private ReferenceBinding[] enclosingTypes = Binding.UNINITIALIZED_REFERENCE_TYPES;
	public SyntheticArgumentBinding[] outerLocalVariables;
	private int outerLocalVariablesSlotSize = -1; // amount of slots used by synthetic outer local variables

public NestedTypeBinding(char[][] typeName, ClassScope scope, SourceTypeBinding enclosingType) {
	super(typeName, enclosingType.fPackage, scope);
	this.tagBits |= (TagBits.IsNestedType | TagBits.ContainsNestedTypeReferences);
	this.enclosingType = enclosingType;
}

/* Add a new synthetic argument for <actualOuterLocalVariable>.
* Answer the new argument or the existing argument if one already existed.
*/
public SyntheticArgumentBinding addSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
	SyntheticArgumentBinding synthLocal = null;

	if (this.outerLocalVariables == null) {
		synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
		this.outerLocalVariables = new SyntheticArgumentBinding[] {synthLocal};
	} else {
		int size = this.outerLocalVariables.length;
		int newArgIndex = size;
		for (int i = size; --i >= 0;) {		// must search backwards
			if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
				return this.outerLocalVariables[i];	// already exists
			if (this.outerLocalVariables[i].id > actualOuterLocalVariable.id)
				newArgIndex = i;
		}
		SyntheticArgumentBinding[] synthLocals = new SyntheticArgumentBinding[size + 1];
		System.arraycopy(this.outerLocalVariables, 0, synthLocals, 0, newArgIndex);
		synthLocals[newArgIndex] = synthLocal = new SyntheticArgumentBinding(actualOuterLocalVariable);
		System.arraycopy(this.outerLocalVariables, newArgIndex, synthLocals, newArgIndex + 1, size - newArgIndex);
		this.outerLocalVariables = synthLocals;
	}
	//System.out.println("Adding synth arg for local var: " + new String(actualOuterLocalVariable.name) + " to: " + new String(this.readableName()));
	if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
		updateInnerEmulationDependents();
	return synthLocal;
}

/* Add a new synthetic argument for <enclosingType>.
* Answer the new argument or the existing argument if one already existed.
*/
public SyntheticArgumentBinding addSyntheticArgument(ReferenceBinding targetEnclosingType) {
	SyntheticArgumentBinding synthLocal = null;
	if (this.enclosingInstances == null) {
		synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
		this.enclosingInstances = new SyntheticArgumentBinding[] {synthLocal};
	} else {
		int size = this.enclosingInstances.length;
		int newArgIndex = size;
		for (int i = size; --i >= 0;) {
			if (this.enclosingInstances[i].type == targetEnclosingType)
				return this.enclosingInstances[i]; // already exists
			if (enclosingType() == targetEnclosingType)
				newArgIndex = 0;
		}
		SyntheticArgumentBinding[] newInstances = new SyntheticArgumentBinding[size + 1];
		System.arraycopy(this.enclosingInstances, 0, newInstances, newArgIndex == 0 ? 1 : 0, size);
		newInstances[newArgIndex] = synthLocal = new SyntheticArgumentBinding(targetEnclosingType);
		this.enclosingInstances = newInstances;
	}
	//System.out.println("Adding synth arg for enclosing type: " + new String(enclosingType.readableName()) + " to: " + new String(this.readableName()));
	if (this.scope.referenceCompilationUnit().isPropagatingInnerClassEmulation)
		updateInnerEmulationDependents();
	return synthLocal;
}

/* Add a new synthetic argument and field for <actualOuterLocalVariable>.
* Answer the new argument or the existing argument if one already existed.
*/
public SyntheticArgumentBinding addSyntheticArgumentAndField(LocalVariableBinding actualOuterLocalVariable) {
	SyntheticArgumentBinding synthLocal = addSyntheticArgument(actualOuterLocalVariable);
	if (synthLocal == null) return null;

	if (synthLocal.matchingField == null)
		synthLocal.matchingField = addSyntheticFieldForInnerclass(actualOuterLocalVariable);
	return synthLocal;
}

/* Add a new synthetic argument and field for <enclosingType>.
* Answer the new argument or the existing argument if one already existed.
*/
public SyntheticArgumentBinding addSyntheticArgumentAndField(ReferenceBinding targetEnclosingType) {
	SyntheticArgumentBinding synthLocal = addSyntheticArgument(targetEnclosingType);
	if (synthLocal == null) return null;

	if (synthLocal.matchingField == null)
		synthLocal.matchingField = addSyntheticFieldForInnerclass(targetEnclosingType);
	return synthLocal;
}

/* Answer the receiver's enclosing type... null if the receiver is a top level type.
*/
public ReferenceBinding enclosingType() {
	return this.enclosingType;
}

/**
 * @return the enclosingInstancesSlotSize
 */
public int getEnclosingInstancesSlotSize() {
	return this.enclosingInstances == null ? 0 : this.enclosingInstances.length;
}

/**
 * @return the outerLocalVariablesSlotSize
 */
public int getOuterLocalVariablesSlotSize() {
	if (this.outerLocalVariablesSlotSize < 0) {
		this.outerLocalVariablesSlotSize = 0;
		int outerLocalsCount = this.outerLocalVariables == null ? 0 : this.outerLocalVariables.length;
			for (int i = 0; i < outerLocalsCount; i++){
			SyntheticArgumentBinding argument = this.outerLocalVariables[i];
			switch (argument.type.id) {
				case TypeIds.T_long :
				case TypeIds.T_double :
					this.outerLocalVariablesSlotSize  += 2;
					break;
				default :
					this.outerLocalVariablesSlotSize  ++;
					break;
			}		
		}
	}
	return this.outerLocalVariablesSlotSize;
}

/* Answer the synthetic argument for <actualOuterLocalVariable> or null if one does not exist.
*/
public SyntheticArgumentBinding getSyntheticArgument(LocalVariableBinding actualOuterLocalVariable) {
	if (this.outerLocalVariables == null) return null;		// is null if no outer local variables are known
	for (int i = this.outerLocalVariables.length; --i >= 0;)
		if (this.outerLocalVariables[i].actualOuterLocalVariable == actualOuterLocalVariable)
			return this.outerLocalVariables[i];
	return null;
}

/* Answer the synthetic argument for <targetEnclosingType> or null if one does not exist.
*/
public SyntheticArgumentBinding getSyntheticArgument(ReferenceBinding targetEnclosingType, boolean onlyExactMatch) {
	if (this.enclosingInstances == null) return null;		// is null if no enclosing instances are known
	// exact match
	for (int i = this.enclosingInstances.length; --i >= 0;)
		if (this.enclosingInstances[i].type == targetEnclosingType)
			if (this.enclosingInstances[i].actualOuterLocalVariable == null)
				return this.enclosingInstances[i];

	// type compatibility : to handle cases such as
	// class T { class M{}}
	// class S extends T { class N extends M {}} --> need to use S as a default enclosing instance for the super constructor call in N().
	if (!onlyExactMatch){
		for (int i = this.enclosingInstances.length; --i >= 0;)
			if (this.enclosingInstances[i].actualOuterLocalVariable == null)
				if (this.enclosingInstances[i].type.findSuperTypeOriginatingFrom(targetEnclosingType) != null)
					return this.enclosingInstances[i];
	}
	return null;
}

public SyntheticArgumentBinding[] syntheticEnclosingInstances() {
	return this.enclosingInstances;		// is null if no enclosing instances are required
}

public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
	if (this.enclosingTypes == UNINITIALIZED_REFERENCE_TYPES) {
		if (this.enclosingInstances == null) {
			this.enclosingTypes = null;
		} else {
			int length = this.enclosingInstances.length;
			this.enclosingTypes = new ReferenceBinding[length];
			for (int i = 0; i < length; i++) {
				this.enclosingTypes[i] = (ReferenceBinding) this.enclosingInstances[i].type;
			}
		}
	}
	return this.enclosingTypes;
}

public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
	return this.outerLocalVariables;		// is null if no outer locals are required
}

/*
 * Trigger the dependency mechanism forcing the innerclass emulation
 * to be propagated to all dependent source types.
 */
public void updateInnerEmulationDependents() {
	// nothing to do in general, only local types are doing anything
}
}
