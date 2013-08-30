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
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

/**
 * Reflects the context of code analysis, keeping track of enclosing
 *	try statements, exception handlers, etc...
 */
public class FinallyFlowContext extends FlowContext {

	Reference[] finalAssignments;
	VariableBinding[] finalVariables;
	int assignCount;

	LocalVariableBinding[] nullLocals;
	Expression[] nullReferences;
	int[] nullCheckTypes;
	int nullCount;

	public FinallyFlowContext(FlowContext parent, ASTNode associatedNode) {
		super(parent, associatedNode);
	}

/**
 * Given some contextual initialization info (derived from a try block or a catch block), this
 * code will check that the subroutine context does not also initialize a final variable potentially set
 * redundantly.
 */
public void complainOnDeferredChecks(FlowInfo flowInfo, BlockScope scope) {

	// check redundant final assignments
	for (int i = 0; i < this.assignCount; i++) {
		VariableBinding variable = this.finalVariables[i];
		if (variable == null) continue;

		boolean complained = false; // remember if have complained on this final assignment
		if (variable instanceof FieldBinding) {
			// final field
			if (flowInfo.isPotentiallyAssigned((FieldBinding)variable)) {
				complained = true;
				scope.problemReporter().duplicateInitializationOfBlankFinalField((FieldBinding)variable, this.finalAssignments[i]);
			}
		} else {
			// final local variable
			if (flowInfo.isPotentiallyAssigned((LocalVariableBinding) variable)) {
				complained = true;
				scope.problemReporter().duplicateInitializationOfFinalLocal(
					(LocalVariableBinding) variable,
					this.finalAssignments[i]);
			}
		}
		// any reference reported at this level is removed from the parent context
		// where it could also be reported again
		if (complained) {
			FlowContext currentContext = this.parent;
			while (currentContext != null) {
				//if (currentContext.isSubRoutine()) {
				currentContext.removeFinalAssignmentIfAny(this.finalAssignments[i]);
				//}
				currentContext = currentContext.parent;
			}
		}
	}

	// check inconsistent null checks
	if ((this.tagBits & FlowContext.DEFER_NULL_DIAGNOSTIC) != 0) { // within an enclosing loop, be conservative
		for (int i = 0; i < this.nullCount; i++) {
			this.parent.recordUsingNullReference(scope, this.nullLocals[i],
					this.nullReferences[i],	this.nullCheckTypes[i], flowInfo);
		}
	}
	else { // no enclosing loop, be as precise as possible right now
		for (int i = 0; i < this.nullCount; i++) {
			Expression expression = this.nullReferences[i];
			// final local variable
			LocalVariableBinding local = this.nullLocals[i];
			switch (this.nullCheckTypes[i]) {
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL:
					if (flowInfo.isDefinitelyNonNull(local)) {
						if (this.nullCheckTypes[i] == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL)) {
							if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
								scope.problemReporter().localVariableRedundantCheckOnNonNull(local, expression);
							}
						} else {
							if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
								scope.problemReporter().localVariableNonNullComparedToNull(local, expression);
							}
						}
						continue;
					}
					//$FALL-THROUGH$
				case CAN_ONLY_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NULL | IN_COMPARISON_NON_NULL:
				case CAN_ONLY_NULL | IN_ASSIGNMENT:
				case CAN_ONLY_NULL | IN_INSTANCEOF:
					if (flowInfo.isDefinitelyNull(local)) {
						switch(this.nullCheckTypes[i] & CONTEXT_MASK) {
							case FlowContext.IN_COMPARISON_NULL:
								if (((this.nullCheckTypes[i] & CHECK_MASK) == CAN_ONLY_NULL) && (expression.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
									scope.problemReporter().localVariableNullReference(local, expression);
									continue;
								}
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
								}
								continue;
							case FlowContext.IN_COMPARISON_NON_NULL:
								if (((this.nullCheckTypes[i] & CHECK_MASK) == CAN_ONLY_NULL) && (expression.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
									scope.problemReporter().localVariableNullReference(local, expression);
									continue;
								}
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
								}
								continue;
							case FlowContext.IN_ASSIGNMENT:
								scope.problemReporter().localVariableRedundantNullAssignment(local, expression);
								continue;
							case FlowContext.IN_INSTANCEOF:
								scope.problemReporter().localVariableNullInstanceof(local, expression);
								continue;
						}
					} else if (flowInfo.isPotentiallyNull(local)) {
						switch(this.nullCheckTypes[i] & CONTEXT_MASK) {
							case FlowContext.IN_COMPARISON_NULL:
								this.nullReferences[i] = null;
								if (((this.nullCheckTypes[i] & CHECK_MASK) == CAN_ONLY_NULL) && (expression.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
									scope.problemReporter().localVariablePotentialNullReference(local, expression);
									continue;
								}
								break;
							case FlowContext.IN_COMPARISON_NON_NULL:
								this.nullReferences[i] = null;
								if (((this.nullCheckTypes[i] & CHECK_MASK) == CAN_ONLY_NULL) && (expression.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
									scope.problemReporter().localVariablePotentialNullReference(local, expression);
									continue;
								}
								break;
						}
					}
					break;
				case MAY_NULL:
					if (flowInfo.isDefinitelyNull(local)) {
						scope.problemReporter().localVariableNullReference(local, expression);
						continue;
					}
					if (flowInfo.isPotentiallyNull(local)) {
						scope.problemReporter().localVariablePotentialNullReference(local, expression);
					}
					break;
				default:
					// should not happen
			}
		}
	}
}

	public String individualToString() {

		StringBuffer buffer = new StringBuffer("Finally flow context"); //$NON-NLS-1$
		buffer.append("[finalAssignments count - ").append(this.assignCount).append(']'); //$NON-NLS-1$
		buffer.append("[nullReferences count - ").append(this.nullCount).append(']'); //$NON-NLS-1$
		return buffer.toString();
	}

	public boolean isSubRoutine() {
		return true;
	}

	protected boolean recordFinalAssignment(
		VariableBinding binding,
		Reference finalAssignment) {
		if (this.assignCount == 0) {
			this.finalAssignments = new Reference[5];
			this.finalVariables = new VariableBinding[5];
		} else {
			if (this.assignCount == this.finalAssignments.length)
				System.arraycopy(
					this.finalAssignments,
					0,
					(this.finalAssignments = new Reference[this.assignCount * 2]),
					0,
					this.assignCount);
			System.arraycopy(
				this.finalVariables,
				0,
				(this.finalVariables = new VariableBinding[this.assignCount * 2]),
				0,
				this.assignCount);
		}
		this.finalAssignments[this.assignCount] = finalAssignment;
		this.finalVariables[this.assignCount++] = binding;
		return true;
	}

	public void recordUsingNullReference(Scope scope, LocalVariableBinding local,
			Expression reference, int checkType, FlowInfo flowInfo) {
		if ((flowInfo.tagBits & FlowInfo.UNREACHABLE) == 0 && !flowInfo.isDefinitelyUnknown(local))	{
			if ((this.tagBits & FlowContext.DEFER_NULL_DIAGNOSTIC) != 0) { // within an enclosing loop, be conservative
				switch (checkType) {
					case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL:
					case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL:
					case CAN_ONLY_NULL | IN_COMPARISON_NULL:
					case CAN_ONLY_NULL | IN_COMPARISON_NON_NULL:
					case CAN_ONLY_NULL | IN_ASSIGNMENT:
					case CAN_ONLY_NULL | IN_INSTANCEOF:
						if (flowInfo.cannotBeNull(local)) {
							if (checkType == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL)) {
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
								}
								if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
									flowInfo.initsWhenFalse().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
								}
							} else if (checkType == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL)) {
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
								}
								if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
									flowInfo.initsWhenTrue().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
								}
							}
							return;
						}
						if (flowInfo.canOnlyBeNull(local)) {
							switch(checkType & CONTEXT_MASK) {
								case FlowContext.IN_COMPARISON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariableNullReference(local, reference);
										return;
									}
									if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
										scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
									}
									if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
										flowInfo.initsWhenFalse().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
									}
									return;
								case FlowContext.IN_COMPARISON_NON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariableNullReference(local, reference);
										return;
									}
									if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
										scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
									}
									if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
										flowInfo.initsWhenTrue().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
									}
									return;
								case FlowContext.IN_ASSIGNMENT:
									scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
									return;
								case FlowContext.IN_INSTANCEOF:
									scope.problemReporter().localVariableNullInstanceof(local, reference);
									return;
							}
						} else if (flowInfo.isPotentiallyNull(local)) {
							switch(checkType & CONTEXT_MASK) {
								case FlowContext.IN_COMPARISON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariablePotentialNullReference(local, reference);
										return;
									}
									break;
								case FlowContext.IN_COMPARISON_NON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariablePotentialNullReference(local, reference);
										return;
									}
									break;
							}
						}
						break;
					case MAY_NULL :
						if (flowInfo.cannotBeNull(local)) {
							return;
						}
						if (flowInfo.canOnlyBeNull(local)) {
							scope.problemReporter().localVariableNullReference(local, reference);
							return;
						}
						break;
					default:
						// never happens
				}
			}
			else { // no enclosing loop, be as precise as possible right now
				switch (checkType) {
					case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL:
					case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL:
						if (flowInfo.isDefinitelyNonNull(local)) {
							if (checkType == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL)) {
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableRedundantCheckOnNonNull(local, reference);
								}
								if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
									flowInfo.initsWhenFalse().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
								}
							} else {
								if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
									scope.problemReporter().localVariableNonNullComparedToNull(local, reference);
								}
								if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
									flowInfo.initsWhenTrue().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
								}
							}
							return;
						}
						//$FALL-THROUGH$
					case CAN_ONLY_NULL | IN_COMPARISON_NULL:
					case CAN_ONLY_NULL | IN_COMPARISON_NON_NULL:
					case CAN_ONLY_NULL | IN_ASSIGNMENT:
					case CAN_ONLY_NULL | IN_INSTANCEOF:
						if (flowInfo.isDefinitelyNull(local)) {
							switch(checkType & CONTEXT_MASK) {
								case FlowContext.IN_COMPARISON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariableNullReference(local, reference);
										return;
									}
									if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
										scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
									}
									if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
										flowInfo.initsWhenFalse().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
									}
									return;
								case FlowContext.IN_COMPARISON_NON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariableNullReference(local, reference);
										return;
									}
									if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
										scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
									}
									if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
										flowInfo.initsWhenTrue().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
									}
									return;
								case FlowContext.IN_ASSIGNMENT:
									scope.problemReporter().localVariableRedundantNullAssignment(local, reference);
									return;
								case FlowContext.IN_INSTANCEOF:
									scope.problemReporter().localVariableNullInstanceof(local, reference);
									return;
							}
						} else if (flowInfo.isPotentiallyNull(local)) {
							switch(checkType & CONTEXT_MASK) {
								case FlowContext.IN_COMPARISON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariablePotentialNullReference(local, reference);
										return;
									}
									break;
								case FlowContext.IN_COMPARISON_NON_NULL:
									if (((checkType & CHECK_MASK) == CAN_ONLY_NULL) && (reference.implicitConversion & TypeIds.UNBOXING) != 0) { // check for auto-unboxing first and report appropriate warning
										scope.problemReporter().localVariablePotentialNullReference(local, reference);
										return;
									}
									break;
							}
						}
						break;
					case MAY_NULL :
						if (flowInfo.isDefinitelyNull(local)) {
							scope.problemReporter().localVariableNullReference(local, reference);
							return;
						}
						if (flowInfo.isPotentiallyNull(local)) {
							scope.problemReporter().localVariablePotentialNullReference(local, reference);
							return;
						}
						if (flowInfo.isDefinitelyNonNull(local)) {
							return; // shortcut: cannot be null
						}
						break;
					default:
						// never happens
				}
			}
			// if the contention is inside assert statement, we want to avoid null warnings only in case of
			// comparisons and not in case of assignment, instanceof, or may be null.
			if(((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) || checkType == MAY_NULL
					|| (checkType & CONTEXT_MASK) == FlowContext.IN_ASSIGNMENT
					|| (checkType & CONTEXT_MASK) == FlowContext.IN_INSTANCEOF) {
				recordNullReference(local, reference, checkType);
			}
			// prepare to re-check with try/catch flow info
		}
	}

	void removeFinalAssignmentIfAny(Reference reference) {
		for (int i = 0; i < this.assignCount; i++) {
			if (this.finalAssignments[i] == reference) {
				this.finalAssignments[i] = null;
				this.finalVariables[i] = null;
				return;
			}
		}
	}

protected void recordNullReference(LocalVariableBinding local,
	Expression expression, int status) {
	if (this.nullCount == 0) {
		this.nullLocals = new LocalVariableBinding[5];
		this.nullReferences = new Expression[5];
		this.nullCheckTypes = new int[5];
	}
	else if (this.nullCount == this.nullLocals.length) {
		int newLength = this.nullCount * 2;
		System.arraycopy(this.nullLocals, 0,
			this.nullLocals = new LocalVariableBinding[newLength], 0,
			this.nullCount);
		System.arraycopy(this.nullReferences, 0,
			this.nullReferences = new Expression[newLength], 0,
			this.nullCount);
		System.arraycopy(this.nullCheckTypes, 0,
			this.nullCheckTypes = new int[newLength], 0,
			this.nullCount);
	}
	this.nullLocals[this.nullCount] = local;
	this.nullReferences[this.nullCount] = expression;
	this.nullCheckTypes[this.nullCount++] = status;
}
}
