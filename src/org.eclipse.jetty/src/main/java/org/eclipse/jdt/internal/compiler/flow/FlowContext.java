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

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.Reference;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;

/**
 * Reflects the context of code analysis, keeping track of enclosing
 *	try statements, exception handlers, etc...
 */
public class FlowContext implements TypeConstants {

	// preempt marks looping contexts
	public final static FlowContext NotContinuableContext = new FlowContext(null, null);
	public ASTNode associatedNode;
	public FlowContext parent;
	public NullInfoRegistry initsOnFinally;
		// only used within try blocks; remembers upstream flow info mergedWith
		// any null related operation happening within the try block

	public int tagBits;
	public static final int DEFER_NULL_DIAGNOSTIC = 0x1;
	public static final int PREEMPT_NULL_DIAGNOSTIC = 0x2;
	/**
	 * used to hide null comparison related warnings inside assert statements 
	 */
	public static final int HIDE_NULL_COMPARISON_WARNING = 0x4;

public static final int CAN_ONLY_NULL_NON_NULL = 0x0000;
//check against null and non null, with definite values -- comparisons
public static final int CAN_ONLY_NULL = 0x0001;
//check against null, with definite values -- comparisons
public static final int CAN_ONLY_NON_NULL = 0x0002;
//check against non null, with definite values -- comparisons
public static final int MAY_NULL = 0x0003;
// check against null, with potential values -- NPE guard
public static final int CHECK_MASK = 0x00FF;
public static final int IN_COMPARISON_NULL = 0x0100;
public static final int IN_COMPARISON_NON_NULL = 0x0200;
// check happened in a comparison
public static final int IN_ASSIGNMENT = 0x0300;
// check happened in an assignment
public static final int IN_INSTANCEOF = 0x0400;
// check happened in an instanceof expression
public static final int CONTEXT_MASK = ~CHECK_MASK;

public FlowContext(FlowContext parent, ASTNode associatedNode) {
	this.parent = parent;
	this.associatedNode = associatedNode;
	if (parent != null) {
		if ((parent.tagBits & (FlowContext.DEFER_NULL_DIAGNOSTIC | FlowContext.PREEMPT_NULL_DIAGNOSTIC)) != 0) {
			this.tagBits |= FlowContext.DEFER_NULL_DIAGNOSTIC;
		}
		this.initsOnFinally = parent.initsOnFinally;
	}
}

public BranchLabel breakLabel() {
	return null;
}

public void checkExceptionHandlers(TypeBinding raisedException, ASTNode location, FlowInfo flowInfo, BlockScope scope) {
	checkExceptionHandlers(raisedException, location, flowInfo, scope, false);
}
/**
 * @param isExceptionOnAutoClose This is for checking exception handlers for exceptions raised during the
 * auto close of resources inside a try with resources statement. (Relevant for
 * source levels 1.7 and above only)
 */
public void checkExceptionHandlers(TypeBinding raisedException, ASTNode location, FlowInfo flowInfo, BlockScope scope, boolean isExceptionOnAutoClose) {
	// LIGHT-VERSION OF THE EQUIVALENT WITH AN ARRAY OF EXCEPTIONS
	// check that all the argument exception types are handled
	// JDK Compatible implementation - when an exception type is thrown,
	// all related catch blocks are marked as reachable... instead of those only
	// until the point where it is safely handled (Smarter - see comment at the end)
	FlowContext traversedContext = this;
	ArrayList abruptlyExitedLoops = null;
	if (scope.compilerOptions().sourceLevel >= ClassFileConstants.JDK1_7 && location instanceof ThrowStatement) {
		Expression throwExpression = ((ThrowStatement)location).exception;
		LocalVariableBinding throwArgBinding = throwExpression.localVariableBinding();
		if (throwExpression instanceof SingleNameReference // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350361 
				&& throwArgBinding instanceof CatchParameterBinding && throwArgBinding.isEffectivelyFinal()) {
			CatchParameterBinding parameter = (CatchParameterBinding) throwArgBinding;
			checkExceptionHandlers(parameter.getPreciseTypes(), location, flowInfo, scope);
			return;
		}
	}
	while (traversedContext != null) {
		SubRoutineStatement sub;
		if (((sub = traversedContext.subroutine()) != null) && sub.isSubRoutineEscaping()) {
			// traversing a non-returning subroutine means that all unhandled
			// exceptions will actually never get sent...
			return;
		}

		// filter exceptions that are locally caught from the innermost enclosing
		// try statement to the outermost ones.
		if (traversedContext instanceof ExceptionHandlingFlowContext) {
			ExceptionHandlingFlowContext exceptionContext =
				(ExceptionHandlingFlowContext) traversedContext;
			ReferenceBinding[] caughtExceptions;
			if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
				boolean definitelyCaught = false;
				for (int caughtIndex = 0, caughtCount = caughtExceptions.length;
					caughtIndex < caughtCount;
					caughtIndex++) {
					ReferenceBinding caughtException = caughtExceptions[caughtIndex];
				    int state = caughtException == null
				    	? Scope.EQUAL_OR_MORE_SPECIFIC /* any exception */
				        : Scope.compareTypes(raisedException, caughtException);
				    if (abruptlyExitedLoops != null && caughtException != null && state != Scope.NOT_RELATED) {
				    	for (int i = 0, abruptlyExitedLoopsCount = abruptlyExitedLoops.size(); i < abruptlyExitedLoopsCount; i++) {
							LoopingFlowContext loop = (LoopingFlowContext) abruptlyExitedLoops.get(i);
							loop.recordCatchContextOfEscapingException(exceptionContext, caughtException);
						}
					}
					switch (state) {
						case Scope.EQUAL_OR_MORE_SPECIFIC :
							exceptionContext.recordHandlingException(
								caughtException,
								flowInfo.unconditionalInits(),
								raisedException,
								raisedException, // precise exception that will be caught
								location,
								definitelyCaught);
							// was it already definitely caught ?
							definitelyCaught = true;
							break;
						case Scope.MORE_GENERIC :
							exceptionContext.recordHandlingException(
								caughtException,
								flowInfo.unconditionalInits(),
								raisedException,
								caughtException,
								location,
								false);
							// was not caught already per construction
					}
				}
				if (definitelyCaught)
					return;
			}
			// method treatment for unchecked exceptions
			if (exceptionContext.isMethodContext) {
				if (raisedException.isUncheckedException(false))
					return;

				// anonymous constructors are allowed to throw any exceptions (their thrown exceptions
				// clause will be fixed up later as per JLS 8.6).
				if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration){
					AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
					if (method.isConstructor() && method.binding.declaringClass.isAnonymousType()){

						exceptionContext.mergeUnhandledException(raisedException);
						return; // no need to complain, will fix up constructor exceptions
					}
				}
				break; // not handled anywhere, thus jump to error handling
			}
		} else if (traversedContext instanceof LoopingFlowContext) {
			if (abruptlyExitedLoops == null) {
				abruptlyExitedLoops = new ArrayList(5);
			}
			abruptlyExitedLoops.add(traversedContext);
		}

		traversedContext.recordReturnFrom(flowInfo.unconditionalInits());

		if (traversedContext instanceof InsideSubRoutineFlowContext) {
			ASTNode node = traversedContext.associatedNode;
			if (node instanceof TryStatement) {
				TryStatement tryStatement = (TryStatement) node;
				flowInfo.addInitializationsFrom(tryStatement.subRoutineInits); // collect inits
			}
		}
		traversedContext = traversedContext.parent;
	}
	// if reaches this point, then there are some remaining unhandled exception types.
	if (isExceptionOnAutoClose) {
		scope.problemReporter().unhandledExceptionFromAutoClose(raisedException, location);
	} else {
		scope.problemReporter().unhandledException(raisedException, location);
	}
}

public void checkExceptionHandlers(TypeBinding[] raisedExceptions, ASTNode location, FlowInfo flowInfo, BlockScope scope) {
	// check that all the argument exception types are handled
	// JDK Compatible implementation - when an exception type is thrown,
	// all related catch blocks are marked as reachable... instead of those only
	// until the point where it is safely handled (Smarter - see comment at the end)
	int remainingCount; // counting the number of remaining unhandled exceptions
	int raisedCount; // total number of exceptions raised
	if ((raisedExceptions == null)
		|| ((raisedCount = raisedExceptions.length) == 0))
		return;
	remainingCount = raisedCount;

	// duplicate the array of raised exceptions since it will be updated
	// (null replaces any handled exception)
	System.arraycopy(
		raisedExceptions,
		0,
		(raisedExceptions = new TypeBinding[raisedCount]),
		0,
		raisedCount);
	FlowContext traversedContext = this;

	ArrayList abruptlyExitedLoops = null;
	while (traversedContext != null) {
		SubRoutineStatement sub;
		if (((sub = traversedContext.subroutine()) != null) && sub.isSubRoutineEscaping()) {
			// traversing a non-returning subroutine means that all unhandled
			// exceptions will actually never get sent...
			return;
		}
		// filter exceptions that are locally caught from the innermost enclosing
		// try statement to the outermost ones.
		if (traversedContext instanceof ExceptionHandlingFlowContext) {
			ExceptionHandlingFlowContext exceptionContext =
				(ExceptionHandlingFlowContext) traversedContext;
			ReferenceBinding[] caughtExceptions;
			if ((caughtExceptions = exceptionContext.handledExceptions) != Binding.NO_EXCEPTIONS) {
				int caughtCount = caughtExceptions.length;
				boolean[] locallyCaught = new boolean[raisedCount]; // at most

				for (int caughtIndex = 0; caughtIndex < caughtCount; caughtIndex++) {
					ReferenceBinding caughtException = caughtExceptions[caughtIndex];
					for (int raisedIndex = 0; raisedIndex < raisedCount; raisedIndex++) {
						TypeBinding raisedException;
						if ((raisedException = raisedExceptions[raisedIndex]) != null) {
						    int state = caughtException == null
						    	? Scope.EQUAL_OR_MORE_SPECIFIC /* any exception */
						        : Scope.compareTypes(raisedException, caughtException);
						    if (abruptlyExitedLoops != null && caughtException != null && state != Scope.NOT_RELATED) {
						    	for (int i = 0, abruptlyExitedLoopsCount = abruptlyExitedLoops.size(); i < abruptlyExitedLoopsCount; i++) {
									LoopingFlowContext loop = (LoopingFlowContext) abruptlyExitedLoops.get(i);
									loop.recordCatchContextOfEscapingException(exceptionContext, caughtException);
								}
							}
							switch (state) {
								case Scope.EQUAL_OR_MORE_SPECIFIC :
									exceptionContext.recordHandlingException(
										caughtException,
										flowInfo.unconditionalInits(),
										raisedException,
										raisedException, // precise exception that will be caught
										location,
										locallyCaught[raisedIndex]);
									// was already definitely caught ?
									if (!locallyCaught[raisedIndex]) {
										locallyCaught[raisedIndex] = true;
										// remember that this exception has been definitely caught
										remainingCount--;
									}
									break;
								case Scope.MORE_GENERIC :
									exceptionContext.recordHandlingException(
										caughtException,
										flowInfo.unconditionalInits(),
										raisedException,
										caughtException, 
										location,
										false);
									// was not caught already per construction
							}
						}
					}
				}
				// remove locally caught exceptions from the remaining ones
				for (int i = 0; i < raisedCount; i++) {
					if (locallyCaught[i]) {
						raisedExceptions[i] = null; // removed from the remaining ones.
					}
				}
			}
			// method treatment for unchecked exceptions
			if (exceptionContext.isMethodContext) {
				for (int i = 0; i < raisedCount; i++) {
					TypeBinding raisedException;
					if ((raisedException = raisedExceptions[i]) != null) {
						if (raisedException.isUncheckedException(false)) {
							remainingCount--;
							raisedExceptions[i] = null;
						}
					}
				}
				// anonymous constructors are allowed to throw any exceptions (their thrown exceptions
				// clause will be fixed up later as per JLS 8.6).
				if (exceptionContext.associatedNode instanceof AbstractMethodDeclaration){
					AbstractMethodDeclaration method = (AbstractMethodDeclaration)exceptionContext.associatedNode;
					if (method.isConstructor() && method.binding.declaringClass.isAnonymousType()){

						for (int i = 0; i < raisedCount; i++) {
							TypeBinding raisedException;
							if ((raisedException = raisedExceptions[i]) != null) {
								exceptionContext.mergeUnhandledException(raisedException);
							}
						}
						return; // no need to complain, will fix up constructor exceptions
					}
				}
				break; // not handled anywhere, thus jump to error handling
			}
        } else if (traversedContext instanceof LoopingFlowContext) {
			if (abruptlyExitedLoops == null) {
				abruptlyExitedLoops = new ArrayList(5);
			}
			abruptlyExitedLoops.add(traversedContext);
		}
		if (remainingCount == 0)
			return;

		traversedContext.recordReturnFrom(flowInfo.unconditionalInits());

		if (traversedContext instanceof InsideSubRoutineFlowContext) {
			ASTNode node = traversedContext.associatedNode;
			if (node instanceof TryStatement) {
				TryStatement tryStatement = (TryStatement) node;
				flowInfo.addInitializationsFrom(tryStatement.subRoutineInits); // collect inits
			}
		}
		traversedContext = traversedContext.parent;
	}
	// if reaches this point, then there are some remaining unhandled exception types.
	nextReport: for (int i = 0; i < raisedCount; i++) {
		TypeBinding exception;
		if ((exception = raisedExceptions[i]) != null) {
			// only one complaint if same exception declared to be thrown more than once
			for (int j = 0; j < i; j++) {
				if (raisedExceptions[j] == exception) continue nextReport; // already reported
			}
			scope.problemReporter().unhandledException(exception, location);
		}
	}
}

public BranchLabel continueLabel() {
	return null;
}

public FlowInfo getInitsForFinalBlankInitializationCheck(TypeBinding declaringType, FlowInfo flowInfo) {
	FlowContext current = this;
	FlowInfo inits = flowInfo;
	do {
		if (current instanceof InitializationFlowContext) {
			InitializationFlowContext initializationContext = (InitializationFlowContext) current;
			if (((TypeDeclaration)initializationContext.associatedNode).binding == declaringType) {
				return inits;
			}
			inits = initializationContext.initsBeforeContext;
			current = initializationContext.initializationParent;
		} else if (current instanceof ExceptionHandlingFlowContext) {
			ExceptionHandlingFlowContext exceptionContext = (ExceptionHandlingFlowContext) current;
			current = exceptionContext.initializationParent == null ? exceptionContext.parent : exceptionContext.initializationParent;
		} else {
			current = current.parent;
		}
	} while (current != null);
	// not found
	return null;
}

/*
 * lookup through break labels
 */
public FlowContext getTargetContextForBreakLabel(char[] labelName) {
	FlowContext current = this, lastNonReturningSubRoutine = null;
	while (current != null) {
		if (current.isNonReturningContext()) {
			lastNonReturningSubRoutine = current;
		}
		char[] currentLabelName;
		if (((currentLabelName = current.labelName()) != null)
			&& CharOperation.equals(currentLabelName, labelName)) {
			((LabeledStatement)current.associatedNode).bits |= ASTNode.LabelUsed;
			if (lastNonReturningSubRoutine == null)
				return current;
			return lastNonReturningSubRoutine;
		}
		current = current.parent;
	}
	// not found
	return null;
}

/*
 * lookup through continue labels
 */
public FlowContext getTargetContextForContinueLabel(char[] labelName) {
	FlowContext current = this;
	FlowContext lastContinuable = null;
	FlowContext lastNonReturningSubRoutine = null;

	while (current != null) {
		if (current.isNonReturningContext()) {
			lastNonReturningSubRoutine = current;
		} else {
			if (current.isContinuable()) {
				lastContinuable = current;
			}
		}

		char[] currentLabelName;
		if ((currentLabelName = current.labelName()) != null && CharOperation.equals(currentLabelName, labelName)) {
			((LabeledStatement)current.associatedNode).bits |= ASTNode.LabelUsed;

			// matching label found
			if ((lastContinuable != null)
					&& (current.associatedNode.concreteStatement()	== lastContinuable.associatedNode)) {

				if (lastNonReturningSubRoutine == null) return lastContinuable;
				return lastNonReturningSubRoutine;
			}
			// label is found, but not a continuable location
			return FlowContext.NotContinuableContext;
		}
		current = current.parent;
	}
	// not found
	return null;
}

/*
 * lookup a default break through breakable locations
 */
public FlowContext getTargetContextForDefaultBreak() {
	FlowContext current = this, lastNonReturningSubRoutine = null;
	while (current != null) {
		if (current.isNonReturningContext()) {
			lastNonReturningSubRoutine = current;
		}
		if (current.isBreakable() && current.labelName() == null) {
			if (lastNonReturningSubRoutine == null) return current;
			return lastNonReturningSubRoutine;
		}
		current = current.parent;
	}
	// not found
	return null;
}

/*
 * lookup a default continue amongst continuable locations
 */
public FlowContext getTargetContextForDefaultContinue() {
	FlowContext current = this, lastNonReturningSubRoutine = null;
	while (current != null) {
		if (current.isNonReturningContext()) {
			lastNonReturningSubRoutine = current;
		}
		if (current.isContinuable()) {
			if (lastNonReturningSubRoutine == null)
				return current;
			return lastNonReturningSubRoutine;
		}
		current = current.parent;
	}
	// not found
	return null;
}

public String individualToString() {
	return "Flow context"; //$NON-NLS-1$
}

public FlowInfo initsOnBreak() {
	return FlowInfo.DEAD_END;
}

public UnconditionalFlowInfo initsOnReturn() {
	return FlowInfo.DEAD_END;
}

public boolean isBreakable() {
	return false;
}

public boolean isContinuable() {
	return false;
}

public boolean isNonReturningContext() {
	return false;
}

public boolean isSubRoutine() {
	return false;
}

public char[] labelName() {
	return null;
}

public void recordBreakFrom(FlowInfo flowInfo) {
	// default implementation: do nothing
}

public void recordBreakTo(FlowContext targetContext) {
	// default implementation: do nothing
}

public void recordContinueFrom(FlowContext innerFlowContext, FlowInfo flowInfo) {
	// default implementation: do nothing
}

protected boolean recordFinalAssignment(VariableBinding variable, Reference finalReference) {
	return true; // keep going
}

/**
 * Record a null reference for use by deferred checks. Only looping or
 * finally contexts really record that information.
 * @param local the local variable involved in the check
 * @param expression the expression within which local lays
 * @param status the status against which the check must be performed; one of
 * 		{@link #CAN_ONLY_NULL CAN_ONLY_NULL}, {@link #CAN_ONLY_NULL_NON_NULL
 * 		CAN_ONLY_NULL_NON_NULL}, {@link #MAY_NULL MAY_NULL},
 *      {@link #CAN_ONLY_NON_NULL CAN_ONLY_NON_NULL}, potentially
 *      combined with a context indicator (one of {@link #IN_COMPARISON_NULL},
 *      {@link #IN_COMPARISON_NON_NULL}, {@link #IN_ASSIGNMENT} or {@link #IN_INSTANCEOF})
 */
protected void recordNullReference(LocalVariableBinding local,
	Expression expression, int status) {
	// default implementation: do nothing
}

public void recordReturnFrom(UnconditionalFlowInfo flowInfo) {
	// default implementation: do nothing
}

public void recordSettingFinal(VariableBinding variable, Reference finalReference, FlowInfo flowInfo) {
	if ((flowInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0)	{
	// for initialization inside looping statement that effectively loops
	FlowContext context = this;
	while (context != null) {
		if (!context.recordFinalAssignment(variable, finalReference)) {
			break; // no need to keep going
		}
		context = context.parent;
	}
	}
}

/**
 * Record a null reference for use by deferred checks. Only looping or
 * finally contexts really record that information. The context may
 * emit an error immediately depending on the status of local against
 * flowInfo and its nature (only looping of finally contexts defer part
 * of the checks; nonetheless, contexts that are nested into a looping or a
 * finally context get affected and delegate some checks to their enclosing
 * context).
 * @param scope the scope into which the check is performed
 * @param local the local variable involved in the check
 * @param reference the expression within which local lies
 * @param checkType the status against which the check must be performed; one
 * 		of {@link #CAN_ONLY_NULL CAN_ONLY_NULL}, {@link #CAN_ONLY_NULL_NON_NULL
 * 		CAN_ONLY_NULL_NON_NULL}, {@link #MAY_NULL MAY_NULL}, potentially
 *      combined with a context indicator (one of {@link #IN_COMPARISON_NULL},
 *      {@link #IN_COMPARISON_NON_NULL}, {@link #IN_ASSIGNMENT} or {@link #IN_INSTANCEOF})
 * @param flowInfo the flow info at the check point; deferring contexts will
 *  	perform supplementary checks against flow info instances that cannot
 *  	be known at the time of calling this method (they are influenced by
 * 		code that follows the current point)
 */
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
				return;
			}
			else if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
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
			} else if (flowInfo.cannotBeDefinitelyNullOrNonNull(local)) {
				return;
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
			break;
		default:
			// never happens
	}
	if (this.parent != null) {
		this.parent.recordUsingNullReference(scope, local, reference, checkType,
				flowInfo);
	}
}

void removeFinalAssignmentIfAny(Reference reference) {
	// default implementation: do nothing
}

public SubRoutineStatement subroutine() {
	return null;
}

public String toString() {
	StringBuffer buffer = new StringBuffer();
	FlowContext current = this;
	int parentsCount = 0;
	while ((current = current.parent) != null) {
		parentsCount++;
	}
	FlowContext[] parents = new FlowContext[parentsCount + 1];
	current = this;
	int index = parentsCount;
	while (index >= 0) {
		parents[index--] = current;
		current = current.parent;
	}
	for (int i = 0; i < parentsCount; i++) {
		for (int j = 0; j < i; j++)
			buffer.append('\t');
		buffer.append(parents[i].individualToString()).append('\n');
	}
	buffer.append('*');
	for (int j = 0; j < parentsCount + 1; j++)
		buffer.append('\t');
	buffer.append(individualToString()).append('\n');
	return buffer.toString();
}
}
