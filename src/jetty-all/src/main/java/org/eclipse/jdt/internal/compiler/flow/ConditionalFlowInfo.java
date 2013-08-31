/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 332637 - Dead Code detection removing code that isn't dead
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

/**
 * Record conditional initialization status during definite assignment analysis
 *
 */
public class ConditionalFlowInfo extends FlowInfo {

	public FlowInfo initsWhenTrue;
	public FlowInfo initsWhenFalse;

ConditionalFlowInfo(FlowInfo initsWhenTrue, FlowInfo initsWhenFalse){

	this.initsWhenTrue = initsWhenTrue;
	this.initsWhenFalse = initsWhenFalse;
}

public FlowInfo addInitializationsFrom(FlowInfo otherInits) {

	this.initsWhenTrue.addInitializationsFrom(otherInits);
	this.initsWhenFalse.addInitializationsFrom(otherInits);
	return this;
}

public FlowInfo addNullInfoFrom(FlowInfo otherInits) {

	this.initsWhenTrue.addNullInfoFrom(otherInits);
	this.initsWhenFalse.addNullInfoFrom(otherInits);
	return this;
}

public FlowInfo addPotentialInitializationsFrom(FlowInfo otherInits) {

	this.initsWhenTrue.addPotentialInitializationsFrom(otherInits);
	this.initsWhenFalse.addPotentialInitializationsFrom(otherInits);
	return this;
}

public FlowInfo asNegatedCondition() {

	FlowInfo extra = this.initsWhenTrue;
	this.initsWhenTrue = this.initsWhenFalse;
	this.initsWhenFalse = extra;
	return this;
}

public FlowInfo copy() {

	return new ConditionalFlowInfo(this.initsWhenTrue.copy(), this.initsWhenFalse.copy());
}

public FlowInfo initsWhenFalse() {

	return this.initsWhenFalse;
}

public FlowInfo initsWhenTrue() {

	return this.initsWhenTrue;
}

public boolean isDefinitelyAssigned(FieldBinding field) {

	return this.initsWhenTrue.isDefinitelyAssigned(field)
			&& this.initsWhenFalse.isDefinitelyAssigned(field);
}

public boolean isDefinitelyAssigned(LocalVariableBinding local) {

	return this.initsWhenTrue.isDefinitelyAssigned(local)
			&& this.initsWhenFalse.isDefinitelyAssigned(local);
}

public boolean isDefinitelyNonNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isDefinitelyNonNull(local)
			&& this.initsWhenFalse.isDefinitelyNonNull(local);
}

public boolean isDefinitelyNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isDefinitelyNull(local)
			&& this.initsWhenFalse.isDefinitelyNull(local);
}

public boolean isDefinitelyUnknown(LocalVariableBinding local) {
	return this.initsWhenTrue.isDefinitelyUnknown(local)
			&& this.initsWhenFalse.isDefinitelyUnknown(local);
}

public boolean isPotentiallyAssigned(FieldBinding field) {
	return this.initsWhenTrue.isPotentiallyAssigned(field)
			|| this.initsWhenFalse.isPotentiallyAssigned(field);
}

public boolean isPotentiallyAssigned(LocalVariableBinding local) {
	return this.initsWhenTrue.isPotentiallyAssigned(local)
			|| this.initsWhenFalse.isPotentiallyAssigned(local);
}

public boolean isPotentiallyNonNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isPotentiallyNonNull(local)
		|| this.initsWhenFalse.isPotentiallyNonNull(local);
}

public boolean isPotentiallyNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isPotentiallyNull(local)
		|| this.initsWhenFalse.isPotentiallyNull(local);
}

public boolean isPotentiallyUnknown(LocalVariableBinding local) {
	return this.initsWhenTrue.isPotentiallyUnknown(local)
		|| this.initsWhenFalse.isPotentiallyUnknown(local);
}

public boolean isProtectedNonNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isProtectedNonNull(local)
		&& this.initsWhenFalse.isProtectedNonNull(local);
}

public boolean isProtectedNull(LocalVariableBinding local) {
	return this.initsWhenTrue.isProtectedNull(local)
		&& this.initsWhenFalse.isProtectedNull(local);
}

public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
	this.initsWhenTrue.markAsComparedEqualToNonNull(local);
	this.initsWhenFalse.markAsComparedEqualToNonNull(local);
}

public void markAsComparedEqualToNull(LocalVariableBinding local) {
	this.initsWhenTrue.markAsComparedEqualToNull(local);
    this.initsWhenFalse.markAsComparedEqualToNull(local);
}

public void markAsDefinitelyAssigned(FieldBinding field) {
	this.initsWhenTrue.markAsDefinitelyAssigned(field);
	this.initsWhenFalse.markAsDefinitelyAssigned(field);
}

public void markAsDefinitelyAssigned(LocalVariableBinding local) {
	this.initsWhenTrue.markAsDefinitelyAssigned(local);
	this.initsWhenFalse.markAsDefinitelyAssigned(local);
}

public void markAsDefinitelyNonNull(LocalVariableBinding local) {
	this.initsWhenTrue.markAsDefinitelyNonNull(local);
	this.initsWhenFalse.markAsDefinitelyNonNull(local);
}

public void markAsDefinitelyNull(LocalVariableBinding local) {
	this.initsWhenTrue.markAsDefinitelyNull(local);
	this.initsWhenFalse.markAsDefinitelyNull(local);
}

public void resetNullInfo(LocalVariableBinding local) {
	this.initsWhenTrue.resetNullInfo(local);
	this.initsWhenFalse.resetNullInfo(local);
}

public void markPotentiallyNullBit(LocalVariableBinding local) {
	this.initsWhenTrue.markPotentiallyNullBit(local);
	this.initsWhenFalse.markPotentiallyNullBit(local);
}

public void markPotentiallyNonNullBit(LocalVariableBinding local) {
	this.initsWhenTrue.markPotentiallyNonNullBit(local);
	this.initsWhenFalse.markPotentiallyNonNullBit(local);
}

public void markAsDefinitelyUnknown(LocalVariableBinding local) {
	this.initsWhenTrue.markAsDefinitelyUnknown(local);
	this.initsWhenFalse.markAsDefinitelyUnknown(local);
}

public void markPotentiallyUnknownBit(LocalVariableBinding local) {
	this.initsWhenTrue.markPotentiallyUnknownBit(local);
	this.initsWhenFalse.markPotentiallyUnknownBit(local);
}

public FlowInfo setReachMode(int reachMode) {
	if (reachMode == REACHABLE) {
		this.tagBits &= ~UNREACHABLE;
	}
	else {
		this.tagBits |= reachMode;
	}
	this.initsWhenTrue.setReachMode(reachMode);
	this.initsWhenFalse.setReachMode(reachMode);
	return this;
}

public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits) {
	return unconditionalInits().mergedWith(otherInits);
}

public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
	return unconditionalInitsWithoutSideEffect().
		nullInfoLessUnconditionalCopy();
}

public String toString() {

	return "FlowInfo<true: " + this.initsWhenTrue.toString() + ", false: " + this.initsWhenFalse.toString() + ">"; //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-2$
}

public FlowInfo safeInitsWhenTrue() {
	return this.initsWhenTrue;
}

public UnconditionalFlowInfo unconditionalCopy() {
	return this.initsWhenTrue.unconditionalCopy().
			mergedWith(this.initsWhenFalse.unconditionalInits());
}

public UnconditionalFlowInfo unconditionalFieldLessCopy() {
	return this.initsWhenTrue.unconditionalFieldLessCopy().
		mergedWith(this.initsWhenFalse.unconditionalFieldLessCopy());
	// should never happen, hence suboptimal does not hurt
}

public UnconditionalFlowInfo unconditionalInits() {
	return this.initsWhenTrue.unconditionalInits().
			mergedWith(this.initsWhenFalse.unconditionalInits());
}

public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
	// cannot do better here than unconditionalCopy - but still a different
	// operation for UnconditionalFlowInfo
	return this.initsWhenTrue.unconditionalCopy().
			mergedWith(this.initsWhenFalse.unconditionalInits());
}

public void markedAsNullOrNonNullInAssertExpression(LocalVariableBinding local) {
	this.initsWhenTrue.markedAsNullOrNonNullInAssertExpression(local);
	this.initsWhenFalse.markedAsNullOrNonNullInAssertExpression(local);
}

public boolean isMarkedAsNullOrNonNullInAssertExpression(LocalVariableBinding local) {
	return (this.initsWhenTrue.isMarkedAsNullOrNonNullInAssertExpression(local)
		|| this.initsWhenFalse.isMarkedAsNullOrNonNullInAssertExpression(local));
}
}
