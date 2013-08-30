/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - contribution for Bug 336428 - [compiler][null] bogus warning "redundant null check" in condition of do {} while() loop
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.flow;

import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

/**
 * Reflects the context of code analysis, keeping track of enclosing
 *	try statements, exception handlers, etc...
 */
public class LoopingFlowContext extends SwitchFlowContext {

	public BranchLabel continueLabel;
	public UnconditionalFlowInfo initsOnContinue = FlowInfo.DEAD_END;
	private UnconditionalFlowInfo upstreamNullFlowInfo;
	private LoopingFlowContext innerFlowContexts[] = null;
	private UnconditionalFlowInfo innerFlowInfos[] = null;
	private int innerFlowContextsCount = 0;
	private LabelFlowContext breakTargetContexts[] = null;
	private int breakTargetsCount = 0;

	Reference finalAssignments[];
	VariableBinding finalVariables[];
	int assignCount = 0;

	LocalVariableBinding[] nullLocals;
	Expression[] nullReferences;
	int[] nullCheckTypes;
	int nullCount;

	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=321926
	static private class EscapingExceptionCatchSite {
		final ReferenceBinding caughtException;
		final ExceptionHandlingFlowContext catchingContext;
		public EscapingExceptionCatchSite(ExceptionHandlingFlowContext catchingContext,	ReferenceBinding caughtException) {
			this.catchingContext = catchingContext;
			this.caughtException = caughtException;
		}
		void simulateThrowAfterLoopBack(FlowInfo flowInfo) {
			this.catchingContext.recordHandlingException(this.caughtException,
					flowInfo.unconditionalInits(), null, // raised exception, irrelevant here,
					null, null, /* invocation site, irrelevant here */ true // we have no business altering the needed status.
					);
		}
	}
	private ArrayList escapingExceptionCatchSites = null;

	Scope associatedScope;

	public LoopingFlowContext(
		FlowContext parent,
		FlowInfo upstreamNullFlowInfo,
		ASTNode associatedNode,
		BranchLabel breakLabel,
		BranchLabel continueLabel,
		Scope associatedScope) {
		super(parent, associatedNode, breakLabel);
		this.tagBits |= FlowContext.PREEMPT_NULL_DIAGNOSTIC;
			// children will defer to this, which may defer to its own parent
		this.continueLabel = continueLabel;
		this.associatedScope = associatedScope;
		this.upstreamNullFlowInfo = upstreamNullFlowInfo.unconditionalCopy();
	}

/**
 * Perform deferred checks relative to final variables duplicate initialization
 * of lack of initialization.
 * @param scope the scope to which this context is associated
 * @param flowInfo the flow info against which checks must be performed
 */
public void complainOnDeferredFinalChecks(BlockScope scope, FlowInfo flowInfo) {
	// complain on final assignments in loops
	for (int i = 0; i < this.assignCount; i++) {
		VariableBinding variable = this.finalVariables[i];
		if (variable == null) continue;
		boolean complained = false; // remember if have complained on this final assignment
		if (variable instanceof FieldBinding) {
			if (flowInfo.isPotentiallyAssigned((FieldBinding) variable)) {
				complained = true;
				scope.problemReporter().duplicateInitializationOfBlankFinalField(
					(FieldBinding) variable,
					this.finalAssignments[i]);
			}
		} else {
			if (flowInfo.isPotentiallyAssigned((LocalVariableBinding) variable)) {
				complained = true;
				scope.problemReporter().duplicateInitializationOfFinalLocal(
					(LocalVariableBinding) variable,
					this.finalAssignments[i]);
			}
		}
		// any reference reported at this level is removed from the parent context where it
		// could also be reported again
		if (complained) {
			FlowContext context = this.parent;
			while (context != null) {
				context.removeFinalAssignmentIfAny(this.finalAssignments[i]);
				context = context.parent;
			}
		}
	}
}

/**
 * Perform deferred checks relative to the null status of local variables.
 * @param scope the scope to which this context is associated
 * @param callerFlowInfo the flow info against which checks must be performed
 */
public void complainOnDeferredNullChecks(BlockScope scope, FlowInfo callerFlowInfo) {
	for (int i = 0 ; i < this.innerFlowContextsCount ; i++) {
		this.upstreamNullFlowInfo.
			addPotentialNullInfoFrom(
				this.innerFlowContexts[i].upstreamNullFlowInfo).
			addPotentialNullInfoFrom(this.innerFlowInfos[i]);
	}
	this.innerFlowContextsCount = 0;
	UnconditionalFlowInfo flowInfo = this.upstreamNullFlowInfo.
		addPotentialNullInfoFrom(callerFlowInfo.unconditionalInitsWithoutSideEffect());
	if ((this.tagBits & FlowContext.DEFER_NULL_DIAGNOSTIC) != 0) {
		// check only immutable null checks on innermost looping context
		for (int i = 0; i < this.nullCount; i++) {
			LocalVariableBinding local = this.nullLocals[i];
			Expression expression = this.nullReferences[i];
			// final local variable
			switch (this.nullCheckTypes[i]) {
				case CAN_ONLY_NON_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NON_NULL | IN_COMPARISON_NON_NULL:
					if (flowInfo.isDefinitelyNonNull(local)) {
						this.nullReferences[i] = null;
						if (this.nullCheckTypes[i] == (CAN_ONLY_NON_NULL | IN_COMPARISON_NON_NULL)) {
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
					break;
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL:
					if (flowInfo.isDefinitelyNonNull(local)) {
						this.nullReferences[i] = null;
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
					if (flowInfo.isDefinitelyNull(local)) {
						this.nullReferences[i] = null;
						if (this.nullCheckTypes[i] == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL)) {
							if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
								scope.problemReporter().localVariableRedundantCheckOnNull(local, expression);
							}
						} else {
							if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
								scope.problemReporter().localVariableNullComparedToNonNull(local, expression);
							}
						}
						continue;
					}
					break;
				case CAN_ONLY_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NULL | IN_COMPARISON_NON_NULL:
				case CAN_ONLY_NULL | IN_ASSIGNMENT:
				case CAN_ONLY_NULL | IN_INSTANCEOF:
					if (flowInfo.isDefinitelyNull(local)) {
						this.nullReferences[i] = null;
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
						this.nullReferences[i] = null;
						scope.problemReporter().localVariableNullReference(local, expression);
						continue;
					}
					break;
				default:
					// never happens
			}
			this.parent.recordUsingNullReference(scope, local, expression,
					this.nullCheckTypes[i], flowInfo);
		}
	}
	else {
		// check inconsistent null checks on outermost looping context
		for (int i = 0; i < this.nullCount; i++) {
			Expression expression = this.nullReferences[i];
			// final local variable
			LocalVariableBinding local = this.nullLocals[i];
			switch (this.nullCheckTypes[i]) {
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL:
				case CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NON_NULL:
					if (flowInfo.isDefinitelyNonNull(local)) {
						this.nullReferences[i] = null;
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
						this.nullReferences[i] = null;
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
						this.nullReferences[i] = null;
						scope.problemReporter().localVariableNullReference(local, expression);
						continue;
					}
					if (flowInfo.isPotentiallyNull(local)) {
						this.nullReferences[i] = null;
						scope.problemReporter().localVariablePotentialNullReference(local, expression);
						continue;
					}
					break;
				default:
					// never happens
			}
		}
	}
	// propagate breaks
	this.initsOnBreak.addPotentialNullInfoFrom(flowInfo);
	for (int i = 0; i < this.breakTargetsCount; i++) {
		this.breakTargetContexts[i].initsOnBreak.addPotentialNullInfoFrom(flowInfo);
	}
}

	public BranchLabel continueLabel() {
		return this.continueLabel;
	}

	public String individualToString() {
		StringBuffer buffer = new StringBuffer("Looping flow context"); //$NON-NLS-1$
		buffer.append("[initsOnBreak - ").append(this.initsOnBreak.toString()).append(']'); //$NON-NLS-1$
		buffer.append("[initsOnContinue - ").append(this.initsOnContinue.toString()).append(']'); //$NON-NLS-1$
		buffer.append("[finalAssignments count - ").append(this.assignCount).append(']'); //$NON-NLS-1$
		buffer.append("[nullReferences count - ").append(this.nullCount).append(']'); //$NON-NLS-1$
		return buffer.toString();
	}

	public boolean isContinuable() {
		return true;
	}

	public boolean isContinuedTo() {
		return this.initsOnContinue != FlowInfo.DEAD_END;
	}

public void recordBreakTo(FlowContext targetContext) {
	if (targetContext instanceof LabelFlowContext) {
		int current;
		if ((current = this.breakTargetsCount++) == 0) {
			this.breakTargetContexts = new LabelFlowContext[2];
		} else if (current == this.breakTargetContexts.length) {
			System.arraycopy(this.breakTargetContexts, 0, this.breakTargetContexts = new LabelFlowContext[current + 2], 0, current);
		}
		this.breakTargetContexts[current] = (LabelFlowContext) targetContext;
	}
}

public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0)	{
		if ((this.initsOnContinue.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
			this.initsOnContinue = this.initsOnContinue.
					mergedWith(flowInfo.unconditionalInitsWithoutSideEffect());
		}
		else {
			this.initsOnContinue = flowInfo.unconditionalCopy();
		}
		FlowContext inner = innerFlowContext;
		while (inner != this && !(inner instanceof LoopingFlowContext)) {
			inner = inner.parent;
		}
		if (inner == this) {
			this.upstreamNullFlowInfo.
			addPotentialNullInfoFrom(
					flowInfo.unconditionalInitsWithoutSideEffect());
		}
		else {
			int length = 0;
			if (this.innerFlowContexts == null) {
				this.innerFlowContexts = new LoopingFlowContext[5];
				this.innerFlowInfos = new UnconditionalFlowInfo[5];
			}
			else if (this.innerFlowContextsCount ==
					(length = this.innerFlowContexts.length) - 1) {
				System.arraycopy(this.innerFlowContexts, 0,
						(this.innerFlowContexts = new LoopingFlowContext[length + 5]),
						0, length);
				System.arraycopy(this.innerFlowInfos, 0,
						(this.innerFlowInfos= new UnconditionalFlowInfo[length + 5]),
						0, length);
			}
			this.innerFlowContexts[this.innerFlowContextsCount] = (LoopingFlowContext) inner;
			this.innerFlowInfos[this.innerFlowContextsCount++] =
					flowInfo.unconditionalInitsWithoutSideEffect();
		}
	}
}

	protected boolean recordFinalAssignment(
		VariableBinding binding,
		Reference finalAssignment) {

		// do not consider variables which are defined inside this loop
		if (binding instanceof LocalVariableBinding) {
			Scope scope = ((LocalVariableBinding) binding).declaringScope;
			while ((scope = scope.parent) != null) {
				if (scope == this.associatedScope)
					return false;
			}
		}
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

protected void recordNullReference(LocalVariableBinding local,
	Expression expression, int status) {
	if (this.nullCount == 0) {
		this.nullLocals = new LocalVariableBinding[5];
		this.nullReferences = new Expression[5];
		this.nullCheckTypes = new int[5];
	}
	else if (this.nullCount == this.nullLocals.length) {
		System.arraycopy(this.nullLocals, 0,
			this.nullLocals = new LocalVariableBinding[this.nullCount * 2], 0, this.nullCount);
		System.arraycopy(this.nullReferences, 0,
			this.nullReferences = new Expression[this.nullCount * 2], 0, this.nullCount);
		System.arraycopy(this.nullCheckTypes, 0,
			this.nullCheckTypes = new int[this.nullCount * 2], 0, this.nullCount);
	}
	this.nullLocals[this.nullCount] = local;
	this.nullReferences[this.nullCount] = expression;
	this.nullCheckTypes[this.nullCount++] = status;
}

public void recordUsingNullReference(Scope scope, LocalVariableBinding local,
		Expression reference, int checkType, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE) != 0 ||
			flowInfo.isDefinitelyUnknown(local)) {
		return;
	}
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
			} else if (flowInfo.isDefinitelyNull(local)) {
				if (checkType == (CAN_ONLY_NULL_NON_NULL | IN_COMPARISON_NULL)) {
					if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
						scope.problemReporter().localVariableRedundantCheckOnNull(local, reference);
					}
					if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
						flowInfo.initsWhenFalse().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
					}
				} else {
					if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
						scope.problemReporter().localVariableNullComparedToNonNull(local, reference);
					}
					if (!flowInfo.isMarkedAsNullOrNonNullInAssertExpression(local)) {
						flowInfo.initsWhenTrue().setReachMode(FlowInfo.UNREACHABLE_BY_NULLANALYSIS);
					}
				}
			} else if (this.upstreamNullFlowInfo.isDefinitelyNonNull(local) && !flowInfo.isPotentiallyNull(local) && !flowInfo.isPotentiallyUnknown(local)) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=291418
				flowInfo.markAsDefinitelyNonNull(local);
				if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
					recordNullReference(local, reference, checkType);
				}
			} else if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
				return; // no reason to complain, since there is definitely some uncertainty making the comparison relevant.
			} else {
				if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0) {
					// note: pot non-null & pot null is already captured by cannotBeDefinitelyNullOrNonNull()
					if (flowInfo.isPotentiallyNonNull(local)) {
						// knowing 'local' can be non-null, we're only interested in seeing whether it can *only* be non-null 
						recordNullReference(local, reference, CAN_ONLY_NON_NULL | checkType & CONTEXT_MASK);
					} else if (flowInfo.isPotentiallyNull(local)) {
						// knowing 'local' can be null, we're only interested in seeing whether it can *only* be null
						recordNullReference(local, reference, CAN_ONLY_NULL | checkType & CONTEXT_MASK);
					} else {
						recordNullReference(local, reference, checkType);
					}
				}
			}
			return;
		case CAN_ONLY_NULL | IN_COMPARISON_NULL:
		case CAN_ONLY_NULL | IN_COMPARISON_NON_NULL:
		case CAN_ONLY_NULL | IN_ASSIGNMENT:
		case CAN_ONLY_NULL | IN_INSTANCEOF:
			if (flowInfo.isPotentiallyNonNull(local)
					|| flowInfo.isPotentiallyUnknown(local)
					|| flowInfo.isProtectedNonNull(local)) {
				// if variable is not null, we are not interested in recording null reference for deferred checks.
				// This is because CAN_ONLY_NULL means we're only interested in cases when variable can be null.
				return;
			}
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
			// if the contention is inside assert statement, we want to avoid null warnings only in case of
			// comparisons and not in case of assignment and instanceof
			if ((this.tagBits & FlowContext.HIDE_NULL_COMPARISON_WARNING) == 0 
					|| (checkType & CONTEXT_MASK) == FlowContext.IN_ASSIGNMENT
					|| (checkType & CONTEXT_MASK) == FlowContext.IN_INSTANCEOF) {
				recordNullReference(local, reference, checkType);
			}
			return;
		case MAY_NULL :
			if (flowInfo.isDefinitelyNonNull(local)) {
				return;
			}
			if (flowInfo.isDefinitelyNull(local)) {
				scope.problemReporter().localVariableNullReference(local, reference);
				return;
			}
			if (flowInfo.isPotentiallyNull(local)) {
				scope.problemReporter().localVariablePotentialNullReference(local, reference);
				return;
			}
			recordNullReference(local, reference, checkType);
			return;
		default:
			// never happens
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

	/* Simulate a throw of an exception from inside a loop in its second or subsequent iteration.
	   See https://bugs.eclipse.org/bugs/show_bug.cgi?id=321926
	 */
	public void simulateThrowAfterLoopBack(FlowInfo flowInfo) {
		if (this.escapingExceptionCatchSites != null) {
			for (int i = 0, exceptionCount = this.escapingExceptionCatchSites.size(); i < exceptionCount; i++) {
				((EscapingExceptionCatchSite) this.escapingExceptionCatchSites.get(i)).simulateThrowAfterLoopBack(flowInfo);
			}
			this.escapingExceptionCatchSites = null; // don't care for it anymore.
		}
	}

	/* Record the fact that some exception thrown by code within this loop
	   is caught by an outer catch block. This is used to propagate data flow
	   along the edge back to the next iteration. See simulateThrowAfterLoopBack
	 */
	public void recordCatchContextOfEscapingException(ExceptionHandlingFlowContext catchingContext,	ReferenceBinding caughtException) {
		if (this.escapingExceptionCatchSites == null) {
			this.escapingExceptionCatchSites = new ArrayList(5);
		}
		this.escapingExceptionCatchSites.add(new EscapingExceptionCatchSite(catchingContext, caughtException));
	}

	public boolean hasEscapingExceptions() {
		return this.escapingExceptionCatchSites != null;
	}
}
