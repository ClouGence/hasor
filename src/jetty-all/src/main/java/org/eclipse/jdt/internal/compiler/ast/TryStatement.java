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
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class TryStatement extends SubRoutineStatement {

	static final char[] SECRET_RETURN_ADDRESS_NAME = " returnAddress".toCharArray(); //$NON-NLS-1$
	static final char[] SECRET_ANY_HANDLER_NAME = " anyExceptionHandler".toCharArray(); //$NON-NLS-1$
	static final char[] SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME = " primaryException".toCharArray(); //$NON-NLS-1$
	static final char[] SECRET_CAUGHT_THROWABLE_VARIABLE_NAME = " caughtThrowable".toCharArray(); //$NON-NLS-1$;
	static final char[] SECRET_RETURN_VALUE_NAME = " returnValue".toCharArray(); //$NON-NLS-1$

	private static LocalDeclaration [] NO_RESOURCES = new LocalDeclaration[0];
	public LocalDeclaration[] resources = NO_RESOURCES;

	public Block tryBlock;
	public Block[] catchBlocks;

	public Argument[] catchArguments;

	// should rename into subRoutineComplete to be set to false by default

	public Block finallyBlock;
	BlockScope scope;

	public UnconditionalFlowInfo subRoutineInits;
	ReferenceBinding[] caughtExceptionTypes;
	boolean[] catchExits;

	BranchLabel subRoutineStartLabel;
	public LocalVariableBinding anyExceptionVariable,
		returnAddressVariable,
		secretReturnValue;

	ExceptionLabel[] declaredExceptionLabels; // only set while generating code

	// for inlining/optimizing JSR instructions
	private Object[] reusableJSRTargets;
	private BranchLabel[] reusableJSRSequenceStartLabels;
	private int[] reusableJSRStateIndexes;
	private int reusableJSRTargetsCount = 0;

	private static final int NO_FINALLY = 0;										// no finally block
	private static final int FINALLY_SUBROUTINE = 1; 					// finally is generated as a subroutine (using jsr/ret bytecodes)
	private static final int FINALLY_DOES_NOT_COMPLETE = 2;		// non returning finally is optimized with only one instance of finally block
	private static final int FINALLY_INLINE = 3;								// finally block must be inlined since cannot use jsr/ret bytecodes >1.5

	// for local variables table attributes
	int mergedInitStateIndex = -1;
	int preTryInitStateIndex = -1;
	int[] postResourcesInitStateIndexes;
	int naturalExitMergeInitStateIndex = -1;
	int[] catchExitInitStateIndexes;
	private LocalVariableBinding primaryExceptionVariable;
	private LocalVariableBinding caughtThrowableVariable;
	private ExceptionLabel[] resourceExceptionLabels;
	private int[] caughtExceptionsCatchBlocks;

public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

	// Consider the try block and catch block so as to compute the intersection of initializations and
	// the minimum exit relative depth amongst all of them. Then consider the subroutine, and append its
	// initialization to the try/catch ones, if the subroutine completes normally. If the subroutine does not
	// complete, then only keep this result for the rest of the analysis

	// process the finally block (subroutine) - create a context for the subroutine

	this.preTryInitStateIndex =
		currentScope.methodScope().recordInitializationStates(flowInfo);

	if (this.anyExceptionVariable != null) {
		this.anyExceptionVariable.useFlag = LocalVariableBinding.USED;
	}
	if (this.primaryExceptionVariable != null) {
		this.primaryExceptionVariable.useFlag = LocalVariableBinding.USED;
	}
	if (this.caughtThrowableVariable != null) {
		this.caughtThrowableVariable.useFlag = LocalVariableBinding.USED;
	}
	if (this.returnAddressVariable != null) { // TODO (philippe) if subroutine is escaping, unused
		this.returnAddressVariable.useFlag = LocalVariableBinding.USED;
	}
	int resourcesLength = this.resources.length;
	if (resourcesLength > 0) {
		this.postResourcesInitStateIndexes = new int[resourcesLength];
	}


	if (this.subRoutineStartLabel == null) {
		// no finally block -- this is a simplified copy of the else part
		// process the try block in a context handling the local exceptions.
		ExceptionHandlingFlowContext handlingContext =
			new ExceptionHandlingFlowContext(
				flowContext,
				this,
				this.caughtExceptionTypes,
				this.caughtExceptionsCatchBlocks,
				this.catchArguments,
				null,
				this.scope,
				flowInfo.unconditionalInits());
		handlingContext.initsOnFinally =
			new NullInfoRegistry(flowInfo.unconditionalInits());
		// only try blocks initialize that member - may consider creating a
		// separate class if needed

		for (int i = 0; i < resourcesLength; i++) {
			flowInfo = this.resources[i].analyseCode(currentScope, handlingContext, flowInfo.copy());
			this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(flowInfo);
			this.resources[i].binding.useFlag = LocalVariableBinding.USED; // Is implicitly used anyways.
			TypeBinding type = this.resources[i].binding.type;
			if (type != null && type.isValidBinding()) {
				ReferenceBinding binding = (ReferenceBinding) type;
				MethodBinding closeMethod = binding.getExactMethod(ConstantPool.Close, new TypeBinding [0], this.scope.compilationUnitScope()); // scope needs to be tighter
				if (closeMethod != null && closeMethod.returnType.id == TypeIds.T_void) {
					ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
					for (int j = 0, length = thrownExceptions.length; j < length; j++) {
						handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[i], flowInfo, currentScope, true);
					}
				}
			}
		}
		FlowInfo tryInfo;
		if (this.tryBlock.isEmptyBlock()) {
			tryInfo = flowInfo;
		} else {
			tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
			if ((tryInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
				this.bits |= ASTNode.IsTryBlockExiting;
		}

		// check unreachable catch blocks
		handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);

		// process the catch blocks - computing the minimal exit depth amongst try/catch
		if (this.catchArguments != null) {
			int catchCount;
			this.catchExits = new boolean[catchCount = this.catchBlocks.length];
			this.catchExitInitStateIndexes = new int[catchCount];
			for (int i = 0; i < catchCount; i++) {
				// keep track of the inits that could potentially have led to this exception handler (for final assignments diagnosis)
				FlowInfo catchInfo;
				if (isUncheckedCatchBlock(i)) {
					catchInfo =
						handlingContext.initsOnFinally.mitigateNullInfoOf(
							flowInfo.unconditionalCopy().
								addPotentialInitializationsFrom(
									handlingContext.initsOnException(i)).
								addPotentialInitializationsFrom(tryInfo).
								addPotentialInitializationsFrom(
									handlingContext.initsOnReturn));
				} else {
					FlowInfo initsOnException = handlingContext.initsOnException(i);
					catchInfo =
						flowInfo.nullInfoLessUnconditionalCopy()
							.addPotentialInitializationsFrom(initsOnException)
							.addNullInfoFrom(initsOnException)	// null info only from here, this is the only way to enter the catch block
							.addPotentialInitializationsFrom(
									tryInfo.nullInfoLessUnconditionalCopy())
							.addPotentialInitializationsFrom(
									handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
				}

				// catch var is always set
				LocalVariableBinding catchArg = this.catchArguments[i].binding;
				catchInfo.markAsDefinitelyAssigned(catchArg);
				catchInfo.markAsDefinitelyNonNull(catchArg);
				/*
				"If we are about to consider an unchecked exception handler, potential inits may have occured inside
				the try block that need to be detected , e.g.
				try { x = 1; throwSomething();} catch(Exception e){ x = 2} "
				"(uncheckedExceptionTypes notNil and: [uncheckedExceptionTypes at: index])
				ifTrue: [catchInits addPotentialInitializationsFrom: tryInits]."
				*/
				if (this.tryBlock.statements == null && this.resources == NO_RESOURCES) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350579
					catchInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
				}
				catchInfo =
					this.catchBlocks[i].analyseCode(
						currentScope,
						flowContext,
						catchInfo);
				this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
				this.catchExits[i] =
					(catchInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0;
				tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
			}
		}
		this.mergedInitStateIndex =
			currentScope.methodScope().recordInitializationStates(tryInfo);

		// chain up null info registry
		if (flowContext.initsOnFinally != null) {
			flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
		}

		return tryInfo;
	} else {
		InsideSubRoutineFlowContext insideSubContext;
		FinallyFlowContext finallyContext;
		UnconditionalFlowInfo subInfo;
		// analyse finally block first
		insideSubContext = new InsideSubRoutineFlowContext(flowContext, this);

		subInfo =
			this.finallyBlock
				.analyseCode(
					currentScope,
					finallyContext = new FinallyFlowContext(flowContext, this.finallyBlock),
					flowInfo.nullInfoLessUnconditionalCopy())
				.unconditionalInits();
		if (subInfo == FlowInfo.DEAD_END) {
			this.bits |= ASTNode.IsSubRoutineEscaping;
			this.scope.problemReporter().finallyMustCompleteNormally(this.finallyBlock);
		}
		this.subRoutineInits = subInfo;
		// process the try block in a context handling the local exceptions.
		ExceptionHandlingFlowContext handlingContext =
			new ExceptionHandlingFlowContext(
				insideSubContext,
				this,
				this.caughtExceptionTypes,
				this.caughtExceptionsCatchBlocks,
				this.catchArguments,
				null,
				this.scope,
				flowInfo.unconditionalInits());
		handlingContext.initsOnFinally =
			new NullInfoRegistry(flowInfo.unconditionalInits());
		// only try blocks initialize that member - may consider creating a
		// separate class if needed

		for (int i = 0; i < resourcesLength; i++) {
			flowInfo = this.resources[i].analyseCode(currentScope, handlingContext, flowInfo.copy());
			this.postResourcesInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(flowInfo);
			this.resources[i].binding.useFlag = LocalVariableBinding.USED; // Is implicitly used anyways.
			TypeBinding type = this.resources[i].binding.type;
			if (type != null && type.isValidBinding()) {
				ReferenceBinding binding = (ReferenceBinding) type;
				MethodBinding closeMethod = binding.getExactMethod(ConstantPool.Close, new TypeBinding [0], this.scope.compilationUnitScope()); // scope needs to be tighter
				if (closeMethod != null && closeMethod.returnType.id == TypeIds.T_void) {
					ReferenceBinding[] thrownExceptions = closeMethod.thrownExceptions;
					for (int j = 0, length = thrownExceptions.length; j < length; j++) {
						handlingContext.checkExceptionHandlers(thrownExceptions[j], this.resources[j], flowInfo, currentScope);
					}
				}
			}
		}
		FlowInfo tryInfo;
		if (this.tryBlock.isEmptyBlock()) {
			tryInfo = flowInfo;
		} else {
			tryInfo = this.tryBlock.analyseCode(currentScope, handlingContext, flowInfo.copy());
			if ((tryInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0)
				this.bits |= ASTNode.IsTryBlockExiting;
		}

		// check unreachable catch blocks
		handlingContext.complainIfUnusedExceptionHandlers(this.scope, this);

		// process the catch blocks - computing the minimal exit depth amongst try/catch
		if (this.catchArguments != null) {
			int catchCount;
			this.catchExits = new boolean[catchCount = this.catchBlocks.length];
			this.catchExitInitStateIndexes = new int[catchCount];
			for (int i = 0; i < catchCount; i++) {
				// keep track of the inits that could potentially have led to this exception handler (for final assignments diagnosis)
				FlowInfo catchInfo;
				if (isUncheckedCatchBlock(i)) {
					catchInfo =
						handlingContext.initsOnFinally.mitigateNullInfoOf(
							flowInfo.unconditionalCopy().
								addPotentialInitializationsFrom(
									handlingContext.initsOnException(i)).
								addPotentialInitializationsFrom(tryInfo).
								addPotentialInitializationsFrom(
									handlingContext.initsOnReturn));
				}else {
					FlowInfo initsOnException = handlingContext.initsOnException(i);
					catchInfo =
						flowInfo.nullInfoLessUnconditionalCopy()
							.addPotentialInitializationsFrom(initsOnException)
							.addNullInfoFrom(initsOnException)	// null info only from here, this is the only way to enter the catch block
							.addPotentialInitializationsFrom(
									tryInfo.nullInfoLessUnconditionalCopy())
							.addPotentialInitializationsFrom(
									handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
				}

				// catch var is always set
				LocalVariableBinding catchArg = this.catchArguments[i].binding;
				catchInfo.markAsDefinitelyAssigned(catchArg);
				catchInfo.markAsDefinitelyNonNull(catchArg);
				/*
				"If we are about to consider an unchecked exception handler, potential inits may have occured inside
				the try block that need to be detected , e.g.
				try { x = 1; throwSomething();} catch(Exception e){ x = 2} "
				"(uncheckedExceptionTypes notNil and: [uncheckedExceptionTypes at: index])
				ifTrue: [catchInits addPotentialInitializationsFrom: tryInits]."
				*/
				if (this.tryBlock.statements == null && this.resources == NO_RESOURCES) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350579
					catchInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
				}
				catchInfo =
					this.catchBlocks[i].analyseCode(
						currentScope,
						insideSubContext,
						catchInfo);
				this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
				this.catchExits[i] =
					(catchInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0;
				tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
			}
		}
		// we also need to check potential multiple assignments of final variables inside the finally block
		// need to include potential inits from returns inside the try/catch parts - 1GK2AOF
		finallyContext.complainOnDeferredChecks(
			handlingContext.initsOnFinally.mitigateNullInfoOf(
				(tryInfo.tagBits & FlowInfo.UNREACHABLE) == 0 ?
					flowInfo.unconditionalCopy().
					addPotentialInitializationsFrom(tryInfo).
						// lighten the influence of the try block, which may have
						// exited at any point
					addPotentialInitializationsFrom(insideSubContext.initsOnReturn) :
					insideSubContext.initsOnReturn),
			currentScope);

		// chain up null info registry
		if (flowContext.initsOnFinally != null) {
			flowContext.initsOnFinally.add(handlingContext.initsOnFinally);
		}

		this.naturalExitMergeInitStateIndex =
			currentScope.methodScope().recordInitializationStates(tryInfo);
		if (subInfo == FlowInfo.DEAD_END) {
			this.mergedInitStateIndex =
				currentScope.methodScope().recordInitializationStates(subInfo);
			return subInfo;
		} else {
			FlowInfo mergedInfo = tryInfo.addInitializationsFrom(subInfo);
			this.mergedInitStateIndex =
				currentScope.methodScope().recordInitializationStates(mergedInfo);
			return mergedInfo;
		}
	}
}
// Return true if the catch block corresponds to an unchecked exception making allowance for multi-catch blocks.
private boolean isUncheckedCatchBlock(int catchBlock) {
	if (this.caughtExceptionsCatchBlocks == null) {
		return this.caughtExceptionTypes[catchBlock].isUncheckedException(true);
	}
	for (int i = 0, length = this.caughtExceptionsCatchBlocks.length; i < length; i++) {
		if (this.caughtExceptionsCatchBlocks[i] == catchBlock) {
			if (this.caughtExceptionTypes[i].isUncheckedException(true)) {
				return true;
			}
		}
	}
	return false;
}

public ExceptionLabel enterAnyExceptionHandler(CodeStream codeStream) {
	if (this.subRoutineStartLabel == null)
		return null;
	return super.enterAnyExceptionHandler(codeStream);
}

public void enterDeclaredExceptionHandlers(CodeStream codeStream) {
	for (int i = 0, length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length; i < length; i++) {
		this.declaredExceptionLabels[i].placeStart();
	}
}

public void exitAnyExceptionHandler() {
	if (this.subRoutineStartLabel == null)
		return;
	super.exitAnyExceptionHandler();
}

public void exitDeclaredExceptionHandlers(CodeStream codeStream) {
	for (int i = 0, length = this.declaredExceptionLabels == null ? 0 : this.declaredExceptionLabels.length; i < length; i++) {
		this.declaredExceptionLabels[i].placeEnd();
	}
}

private int finallyMode() {
	if (this.subRoutineStartLabel == null) {
		return NO_FINALLY;
	} else if (isSubRoutineEscaping()) {
		return FINALLY_DOES_NOT_COMPLETE;
	} else if (this.scope.compilerOptions().inlineJsrBytecode) {
		return FINALLY_INLINE;
	} else {
		return FINALLY_SUBROUTINE;
	}
}
/**
 * Try statement code generation with or without jsr bytecode use
 *	post 1.5 target level, cannot use jsr bytecode, must instead inline finally block
 * returnAddress is only allocated if jsr is allowed
 */
public void generateCode(BlockScope currentScope, CodeStream codeStream) {
	if ((this.bits & ASTNode.IsReachable) == 0) {
		return;
	}
	boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
	// in case the labels needs to be reinitialized
	// when the code generation is restarted in wide mode
	this.anyExceptionLabel = null;
	this.reusableJSRTargets = null;
	this.reusableJSRSequenceStartLabels = null;
	this.reusableJSRTargetsCount = 0;

	int pc = codeStream.position;
	int finallyMode = finallyMode();

	boolean requiresNaturalExit = false;
	// preparing exception labels
	int maxCatches = this.catchArguments == null ? 0 : this.catchArguments.length;
	ExceptionLabel[] exceptionLabels;
	if (maxCatches > 0) {
		exceptionLabels = new ExceptionLabel[maxCatches];
		for (int i = 0; i < maxCatches; i++) {
			Argument argument = this.catchArguments[i];
			ExceptionLabel exceptionLabel = null;
			if ((argument.binding.tagBits & TagBits.MultiCatchParameter) != 0) {
				MultiCatchExceptionLabel multiCatchExceptionLabel = new MultiCatchExceptionLabel(codeStream, argument.binding.type);
				multiCatchExceptionLabel.initialize((UnionTypeReference) argument.type);
				exceptionLabel = multiCatchExceptionLabel;
			} else {
				exceptionLabel = new ExceptionLabel(codeStream, argument.binding.type);
			}
			exceptionLabel.placeStart();
			exceptionLabels[i] = exceptionLabel;
		}
	} else {
		exceptionLabels = null;
	}
	if (this.subRoutineStartLabel != null) {
		this.subRoutineStartLabel.initialize(codeStream);
		enterAnyExceptionHandler(codeStream);
	}
	// generate the try block
	try {
		this.declaredExceptionLabels = exceptionLabels;
		int resourceCount = this.resources.length;
		if (resourceCount > 0) {
			// Please see https://bugs.eclipse.org/bugs/show_bug.cgi?id=338402#c16
			this.resourceExceptionLabels = new ExceptionLabel[resourceCount + 1];
			codeStream.aconst_null();
			codeStream.store(this.primaryExceptionVariable, false /* value not required */);
			codeStream.addVariable(this.primaryExceptionVariable);
			codeStream.aconst_null();
			codeStream.store(this.caughtThrowableVariable, false /* value not required */);
			codeStream.addVariable(this.caughtThrowableVariable);
			for (int i = 0; i <= resourceCount; i++) {
				// put null for the exception type to treat them as any exception handlers (equivalent to a try/finally)
				this.resourceExceptionLabels[i] = new ExceptionLabel(codeStream, null);
				this.resourceExceptionLabels[i].placeStart();
				if (i < resourceCount) {
					this.resources[i].generateCode(this.scope, codeStream); // Initialize resources ...
				}
			}
		}
		this.tryBlock.generateCode(this.scope, codeStream);
		if (resourceCount > 0) {
			for (int i = resourceCount; i >= 0; i--) {
				BranchLabel exitLabel = new BranchLabel(codeStream);
				this.resourceExceptionLabels[i].placeEnd(); // outer handler if any is the one that should catch exceptions out of close()
				
				LocalVariableBinding localVariable = i > 0 ? this.resources[i-1].binding : null;
				if ((this.bits & ASTNode.IsTryBlockExiting) == 0) {
					// inline resource closure
					if (i > 0) {
						int invokeCloseStartPc = codeStream.position; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=343785
						codeStream.load(localVariable);
						codeStream.ifnull(exitLabel);
						codeStream.load(localVariable);
						codeStream.invokeAutoCloseableClose(localVariable.type);
						codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
					}
					codeStream.goto_(exitLabel); // skip over the catch block.
				}

				if (i > 0) {
					// i is off by one
					codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[i - 1]);
					codeStream.addDefinitelyAssignedVariables(currentScope, this.postResourcesInitStateIndexes[i - 1]);
				}

				codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
				this.resourceExceptionLabels[i].place();
				if (i == resourceCount) { 
					// inner most try's catch/finally can be a lot simpler. 
					codeStream.store(this.primaryExceptionVariable, false);
					// fall through, invoke close() and re-throw.
				} else {
					BranchLabel elseLabel = new BranchLabel(codeStream), postElseLabel = new BranchLabel(codeStream);
					codeStream.store(this.caughtThrowableVariable, false);
					codeStream.load(this.primaryExceptionVariable);
					codeStream.ifnonnull(elseLabel);
					codeStream.load(this.caughtThrowableVariable);
					codeStream.store(this.primaryExceptionVariable, false);
					codeStream.goto_(postElseLabel);
					elseLabel.place();
					codeStream.load(this.primaryExceptionVariable);
					codeStream.load(this.caughtThrowableVariable);
					codeStream.if_acmpeq(postElseLabel);
					codeStream.load(this.primaryExceptionVariable);
					codeStream.load(this.caughtThrowableVariable);
					codeStream.invokeThrowableAddSuppressed();
					postElseLabel.place();
				}
				if (i > 0) {
					// inline resource close here rather than bracketing the current catch block with a try region.
					BranchLabel postCloseLabel = new BranchLabel(codeStream);
					int invokeCloseStartPc = codeStream.position; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=343785			
					codeStream.load(localVariable);
					codeStream.ifnull(postCloseLabel);
					codeStream.load(localVariable);
					codeStream.invokeAutoCloseableClose(localVariable.type);
					codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
					codeStream.removeVariable(localVariable);
					postCloseLabel.place();
				}
				codeStream.load(this.primaryExceptionVariable);
				codeStream.athrow();
				exitLabel.place();
			}
			codeStream.removeVariable(this.primaryExceptionVariable);
			codeStream.removeVariable(this.caughtThrowableVariable);
		}
	} finally {
		this.declaredExceptionLabels = null;
	}
	boolean tryBlockHasSomeCode = codeStream.position != pc;
	// flag telling if some bytecodes were issued inside the try block

	// place end positions of user-defined exception labels
	if (tryBlockHasSomeCode) {
		// natural exit may require subroutine invocation (if finally != null)
		BranchLabel naturalExitLabel = new BranchLabel(codeStream);
		BranchLabel postCatchesFinallyLabel = null;
		for (int i = 0; i < maxCatches; i++) {
			exceptionLabels[i].placeEnd();
		}
		if ((this.bits & ASTNode.IsTryBlockExiting) == 0) {
			int position = codeStream.position;
			switch(finallyMode) {
				case FINALLY_SUBROUTINE :
				case FINALLY_INLINE :
					requiresNaturalExit = true;
					if (this.naturalExitMergeInitStateIndex != -1) {
						codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
						codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
					}
					codeStream.goto_(naturalExitLabel);
					break;
				case NO_FINALLY :
					if (this.naturalExitMergeInitStateIndex != -1) {
						codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
						codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
					}
					codeStream.goto_(naturalExitLabel);
					break;
				case FINALLY_DOES_NOT_COMPLETE :
					codeStream.goto_(this.subRoutineStartLabel);
					break;
			}
			codeStream.updateLastRecordedEndPC(this.tryBlock.scope, position);
			//goto is tagged as part of the try block
		}
		/* generate sequence of handler, all starting by storing the TOS (exception
		thrown) into their own catch variables, the one specified in the source
		that must denote the handled exception.
		*/
		exitAnyExceptionHandler();
		if (this.catchArguments != null) {
			postCatchesFinallyLabel = new BranchLabel(codeStream);

			for (int i = 0; i < maxCatches; i++) {
				/*
				 * This should not happen. For consistency purpose, if the exception label is never used
				 * we also don't generate the corresponding catch block, otherwise we have some
				 * unreachable bytecodes
				 */
				if (exceptionLabels[i].getCount() == 0) continue;
				enterAnyExceptionHandler(codeStream);
				// May loose some local variable initializations : affecting the local variable attributes
				if (this.preTryInitStateIndex != -1) {
					codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
					codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
				}
				codeStream.pushExceptionOnStack(exceptionLabels[i].exceptionType);
				exceptionLabels[i].place();
				// optimizing the case where the exception variable is not actually used
				LocalVariableBinding catchVar;
				int varPC = codeStream.position;
				if ((catchVar = this.catchArguments[i].binding).resolvedPosition != -1) {
					codeStream.store(catchVar, false);
					catchVar.recordInitializationStartPC(codeStream.position);
					codeStream.addVisibleLocalVariable(catchVar);
				} else {
					codeStream.pop();
				}
				codeStream.recordPositionsFrom(varPC, this.catchArguments[i].sourceStart);
				// Keep track of the pcs at diverging point for computing the local attribute
				// since not passing the catchScope, the block generation will exitUserScope(catchScope)
				this.catchBlocks[i].generateCode(this.scope, codeStream);
				exitAnyExceptionHandler();
				if (!this.catchExits[i]) {
					switch(finallyMode) {
						case FINALLY_INLINE :
							// inlined finally here can see all merged variables
							if (isStackMapFrameCodeStream) {
								((StackMapFrameCodeStream) codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
							}
							if (this.catchExitInitStateIndexes[i] != -1) {
								codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
								codeStream.addDefinitelyAssignedVariables(currentScope, this.catchExitInitStateIndexes[i]);
							}
							// entire sequence for finally is associated to finally block
							this.finallyBlock.generateCode(this.scope, codeStream);
							codeStream.goto_(postCatchesFinallyLabel);
							if (isStackMapFrameCodeStream) {
								((StackMapFrameCodeStream) codeStream).popStateIndex();
							}
							break;
						case FINALLY_SUBROUTINE :
							requiresNaturalExit = true;
							//$FALL-THROUGH$
						case NO_FINALLY :
							if (this.naturalExitMergeInitStateIndex != -1) {
								codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
								codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
							}
							codeStream.goto_(naturalExitLabel);
							break;
						case FINALLY_DOES_NOT_COMPLETE :
							codeStream.goto_(this.subRoutineStartLabel);
							break;
					}
				}
			}
		}
		// extra handler for trailing natural exit (will be fixed up later on when natural exit is generated below)
		ExceptionLabel naturalExitExceptionHandler = requiresNaturalExit && (finallyMode == FINALLY_SUBROUTINE)
					? new ExceptionLabel(codeStream, null)
					: null;

		// addition of a special handler so as to ensure that any uncaught exception (or exception thrown
		// inside catch blocks) will run the finally block
		int finallySequenceStartPC = codeStream.position;
		if (this.subRoutineStartLabel != null && this.anyExceptionLabel.getCount() != 0) {
			codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
			if (this.preTryInitStateIndex != -1) {
				// reset initialization state, as for a normal catch block
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
				codeStream.addDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
			}
			placeAllAnyExceptionHandler();
			if (naturalExitExceptionHandler != null) naturalExitExceptionHandler.place();

			switch(finallyMode) {
				case FINALLY_SUBROUTINE :
					// any exception handler
					codeStream.store(this.anyExceptionVariable, false);
					codeStream.jsr(this.subRoutineStartLabel);
					codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
					int position = codeStream.position;
					codeStream.throwAnyException(this.anyExceptionVariable);
					codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
					// subroutine
					this.subRoutineStartLabel.place();
					codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
					position = codeStream.position;
					codeStream.store(this.returnAddressVariable, false);
					codeStream.recordPositionsFrom(position, this.finallyBlock.sourceStart);
					this.finallyBlock.generateCode(this.scope, codeStream);
					position = codeStream.position;
					codeStream.ret(this.returnAddressVariable.resolvedPosition);
					codeStream.recordPositionsFrom(
						position,
						this.finallyBlock.sourceEnd);
					// the ret bytecode is part of the subroutine
					break;
				case FINALLY_INLINE :
					// any exception handler
					codeStream.store(this.anyExceptionVariable, false);
					codeStream.addVariable(this.anyExceptionVariable);
					codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
					// subroutine
					this.finallyBlock.generateCode(currentScope, codeStream);
					position = codeStream.position;
					codeStream.throwAnyException(this.anyExceptionVariable);
					codeStream.removeVariable(this.anyExceptionVariable);
					if (this.preTryInitStateIndex != -1) {
						codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preTryInitStateIndex);
					}
					this.subRoutineStartLabel.place();
					codeStream.recordPositionsFrom(position, this.finallyBlock.sourceEnd);
					break;
				case FINALLY_DOES_NOT_COMPLETE :
					// any exception handler
					codeStream.pop();
					this.subRoutineStartLabel.place();
					codeStream.recordPositionsFrom(finallySequenceStartPC, this.finallyBlock.sourceStart);
					// subroutine
					this.finallyBlock.generateCode(this.scope, codeStream);
					break;
			}

			// will naturally fall into subsequent code after subroutine invocation
			if (requiresNaturalExit) {
				switch(finallyMode) {
					case FINALLY_SUBROUTINE :
						naturalExitLabel.place();
						int position = codeStream.position;
						naturalExitExceptionHandler.placeStart();
						codeStream.jsr(this.subRoutineStartLabel);
						naturalExitExceptionHandler.placeEnd();
						codeStream.recordPositionsFrom(
							position,
							this.finallyBlock.sourceEnd);
						break;
					case FINALLY_INLINE :
						// inlined finally here can see all merged variables
						if (isStackMapFrameCodeStream) {
							((StackMapFrameCodeStream) codeStream).pushStateIndex(this.naturalExitMergeInitStateIndex);
						}
						if (this.naturalExitMergeInitStateIndex != -1) {
							codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
							codeStream.addDefinitelyAssignedVariables(currentScope, this.naturalExitMergeInitStateIndex);
						}
						naturalExitLabel.place();
						// entire sequence for finally is associated to finally block
						this.finallyBlock.generateCode(this.scope, codeStream);
						if (postCatchesFinallyLabel != null) {
							position = codeStream.position;
							// entire sequence for finally is associated to finally block
							codeStream.goto_(postCatchesFinallyLabel);
							codeStream.recordPositionsFrom(
									position,
									this.finallyBlock.sourceEnd);
						}
						if (isStackMapFrameCodeStream) {
							((StackMapFrameCodeStream) codeStream).popStateIndex();
						}
						break;
					case FINALLY_DOES_NOT_COMPLETE :
						break;
					default :
						naturalExitLabel.place();
						break;
				}
			}
			if (postCatchesFinallyLabel != null) {
				postCatchesFinallyLabel.place();
			}
		} else {
			// no subroutine, simply position end label (natural exit == end)
			naturalExitLabel.place();
		}
	} else {
		// try block had no effect, only generate the body of the finally block if any
		if (this.subRoutineStartLabel != null) {
			this.finallyBlock.generateCode(this.scope, codeStream);
		}
	}
	// May loose some local variable initializations : affecting the local variable attributes
	if (this.mergedInitStateIndex != -1) {
		codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
		codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
	}
	codeStream.recordPositionsFrom(pc, this.sourceStart);
}

/**
 * @see SubRoutineStatement#generateSubRoutineInvocation(BlockScope, CodeStream, Object, int, LocalVariableBinding)
 */
public boolean generateSubRoutineInvocation(BlockScope currentScope, CodeStream codeStream, Object targetLocation, int stateIndex, LocalVariableBinding secretLocal) {

	int resourceCount = this.resources.length;
	if (resourceCount > 0) {
		for (int i = resourceCount; i > 0; --i) {
			// Disarm the handlers and take care of resource closure.
			this.resourceExceptionLabels[i].placeEnd();
			LocalVariableBinding localVariable = this.resources[i-1].binding;
			BranchLabel exitLabel = new BranchLabel(codeStream);
			int invokeCloseStartPc = codeStream.position; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=343785
			codeStream.load(localVariable);
			codeStream.ifnull(exitLabel);
			codeStream.load(localVariable);
			codeStream.invokeAutoCloseableClose(localVariable.type);
			codeStream.recordPositionsFrom(invokeCloseStartPc, this.tryBlock.sourceEnd);
			exitLabel.place();
		}
		// Reinstall handlers
		for (int i = resourceCount; i > 0; --i) {
			this.resourceExceptionLabels[i].placeStart();
		}
	}

	boolean isStackMapFrameCodeStream = codeStream instanceof StackMapFrameCodeStream;
	int finallyMode = finallyMode();
	switch(finallyMode) {
		case FINALLY_DOES_NOT_COMPLETE :
			codeStream.goto_(this.subRoutineStartLabel);
			return true;

		case NO_FINALLY :
			exitDeclaredExceptionHandlers(codeStream);
			return false;
	}
	// optimize subroutine invocation sequences, using the targetLocation (if any)
	if (targetLocation != null) {
		boolean reuseTargetLocation = true;
		if (this.reusableJSRTargetsCount > 0) {
			nextReusableTarget: for (int i = 0, count = this.reusableJSRTargetsCount; i < count; i++) {
				Object reusableJSRTarget = this.reusableJSRTargets[i];
				differentTarget: {
					if (targetLocation == reusableJSRTarget)
						break differentTarget;
					if (targetLocation instanceof Constant
							&& reusableJSRTarget instanceof Constant
							&& ((Constant)targetLocation).hasSameValue((Constant) reusableJSRTarget)) {
						break differentTarget;
					}
					// cannot reuse current target
					continue nextReusableTarget;
				}
				// current target has been used in the past, simply branch to its label
				if ((this.reusableJSRStateIndexes[i] != stateIndex) && finallyMode == FINALLY_INLINE) {
					reuseTargetLocation = false;
					break nextReusableTarget;
				} else {
					codeStream.goto_(this.reusableJSRSequenceStartLabels[i]);
					return true;
				}
			}
		} else {
			this.reusableJSRTargets = new Object[3];
			this.reusableJSRSequenceStartLabels = new BranchLabel[3];
			this.reusableJSRStateIndexes = new int[3];
		}
		if (reuseTargetLocation) {
			if (this.reusableJSRTargetsCount == this.reusableJSRTargets.length) {
				System.arraycopy(this.reusableJSRTargets, 0, this.reusableJSRTargets = new Object[2*this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
				System.arraycopy(this.reusableJSRSequenceStartLabels, 0, this.reusableJSRSequenceStartLabels = new BranchLabel[2*this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
				System.arraycopy(this.reusableJSRStateIndexes, 0, this.reusableJSRStateIndexes = new int[2*this.reusableJSRTargetsCount], 0, this.reusableJSRTargetsCount);
			}
			this.reusableJSRTargets[this.reusableJSRTargetsCount] = targetLocation;
			BranchLabel reusableJSRSequenceStartLabel = new BranchLabel(codeStream);
			reusableJSRSequenceStartLabel.place();
			this.reusableJSRStateIndexes[this.reusableJSRTargetsCount] = stateIndex;
			this.reusableJSRSequenceStartLabels[this.reusableJSRTargetsCount++] = reusableJSRSequenceStartLabel;
		}
	}
	if (finallyMode == FINALLY_INLINE) {
		if (isStackMapFrameCodeStream) {
			((StackMapFrameCodeStream) codeStream).pushStateIndex(stateIndex);
		}
		if (secretLocal != null) {
			codeStream.addVariable(secretLocal);
		}
		// cannot use jsr bytecode, then simply inline the subroutine
		// inside try block, ensure to deactivate all catch block exception handlers while inlining finally block
		exitAnyExceptionHandler();
		exitDeclaredExceptionHandlers(codeStream);
		this.finallyBlock.generateCode(currentScope, codeStream);
		if (isStackMapFrameCodeStream) {
			((StackMapFrameCodeStream) codeStream).popStateIndex();
		}
	} else {
		// classic subroutine invocation, distinguish case of non-returning subroutine
		codeStream.jsr(this.subRoutineStartLabel);
		exitAnyExceptionHandler();
		exitDeclaredExceptionHandlers(codeStream);
	}
	return false;
}
public boolean isSubRoutineEscaping() {
	return (this.bits & ASTNode.IsSubRoutineEscaping) != 0;
}

public StringBuffer printStatement(int indent, StringBuffer output) {
	int length = this.resources.length;
	printIndent(indent, output).append("try" + (length == 0 ? "\n" : " (")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	for (int i = 0; i < length; i++) {
		this.resources[i].printAsExpression(0, output);
		if (i != length - 1) {
			output.append(";\n"); //$NON-NLS-1$
			printIndent(indent + 2, output);
		}
	}
	if (length > 0) {
		output.append(")\n"); //$NON-NLS-1$
	}
	this.tryBlock.printStatement(indent + 1, output);

	//catches
	if (this.catchBlocks != null)
		for (int i = 0; i < this.catchBlocks.length; i++) {
				output.append('\n');
				printIndent(indent, output).append("catch ("); //$NON-NLS-1$
				this.catchArguments[i].print(0, output).append(")\n"); //$NON-NLS-1$
				this.catchBlocks[i].printStatement(indent + 1, output);
		}
	//finally
	if (this.finallyBlock != null) {
		output.append('\n');
		printIndent(indent, output).append("finally\n"); //$NON-NLS-1$
		this.finallyBlock.printStatement(indent + 1, output);
	}
	return output;
}

public void resolve(BlockScope upperScope) {
	// special scope for secret locals optimization.
	this.scope = new BlockScope(upperScope);

	BlockScope finallyScope = null;
    BlockScope resourceManagementScope = null; // Single scope to hold all resources and additional secret variables.
	int resourceCount = this.resources.length;
	if (resourceCount > 0) {
		resourceManagementScope = new BlockScope(this.scope);
		this.primaryExceptionVariable =
			new LocalVariableBinding(TryStatement.SECRET_PRIMARY_EXCEPTION_VARIABLE_NAME, this.scope.getJavaLangThrowable(), ClassFileConstants.AccDefault, false);
		resourceManagementScope.addLocalVariable(this.primaryExceptionVariable);
		this.primaryExceptionVariable.setConstant(Constant.NotAConstant); // not inlinable
		this.caughtThrowableVariable =
			new LocalVariableBinding(TryStatement.SECRET_CAUGHT_THROWABLE_VARIABLE_NAME, this.scope.getJavaLangThrowable(), ClassFileConstants.AccDefault, false);
		resourceManagementScope.addLocalVariable(this.caughtThrowableVariable);
		this.caughtThrowableVariable.setConstant(Constant.NotAConstant); // not inlinable
	}
	for (int i = 0; i < resourceCount; i++) {
		this.resources[i].resolve(resourceManagementScope);
		LocalVariableBinding localVariableBinding = this.resources[i].binding;
		if (localVariableBinding != null && localVariableBinding.isValidBinding()) {
			localVariableBinding.modifiers |= ClassFileConstants.AccFinal;
			localVariableBinding.tagBits |= TagBits.IsResource;
			TypeBinding resourceType = localVariableBinding.type;
			if (resourceType instanceof ReferenceBinding) {
				if (resourceType.findSuperTypeOriginatingFrom(TypeIds.T_JavaLangAutoCloseable, false /*AutoCloseable is not a class*/) == null && resourceType.isValidBinding()) {
					upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
					localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, ProblemReasons.InvalidTypeForAutoManagedResource);
				}
			} else if (resourceType != null) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=349862, avoid secondary error in problematic null case
				upperScope.problemReporter().resourceHasToImplementAutoCloseable(resourceType, this.resources[i].type);
				localVariableBinding.type = new ProblemReferenceBinding(CharOperation.splitOn('.', resourceType.shortReadableName()), null, ProblemReasons.InvalidTypeForAutoManagedResource);
			}
		}
	}
	BlockScope tryScope = new BlockScope(resourceManagementScope != null ? resourceManagementScope : this.scope);

	if (this.finallyBlock != null) {
		if (this.finallyBlock.isEmptyBlock()) {
			if ((this.finallyBlock.bits & ASTNode.UndocumentedEmptyBlock) != 0) {
				this.scope.problemReporter().undocumentedEmptyBlock(this.finallyBlock.sourceStart, this.finallyBlock.sourceEnd);
			}
		} else {
			finallyScope = new BlockScope(this.scope, false); // don't add it yet to parent scope

			// provision for returning and forcing the finally block to run
			MethodScope methodScope = this.scope.methodScope();

			// the type does not matter as long as it is not a base type
			if (!upperScope.compilerOptions().inlineJsrBytecode) {
				this.returnAddressVariable =
					new LocalVariableBinding(TryStatement.SECRET_RETURN_ADDRESS_NAME, upperScope.getJavaLangObject(), ClassFileConstants.AccDefault, false);
				finallyScope.addLocalVariable(this.returnAddressVariable);
				this.returnAddressVariable.setConstant(Constant.NotAConstant); // not inlinable
			}
			this.subRoutineStartLabel = new BranchLabel();

			this.anyExceptionVariable =
				new LocalVariableBinding(TryStatement.SECRET_ANY_HANDLER_NAME, this.scope.getJavaLangThrowable(), ClassFileConstants.AccDefault, false);
			finallyScope.addLocalVariable(this.anyExceptionVariable);
			this.anyExceptionVariable.setConstant(Constant.NotAConstant); // not inlinable

			if (!methodScope.isInsideInitializer()) {
				MethodBinding methodBinding =
					((AbstractMethodDeclaration) methodScope.referenceContext).binding;
				if (methodBinding != null) {
					TypeBinding methodReturnType = methodBinding.returnType;
					if (methodReturnType.id != TypeIds.T_void) {
						this.secretReturnValue =
							new LocalVariableBinding(
								TryStatement.SECRET_RETURN_VALUE_NAME,
								methodReturnType,
								ClassFileConstants.AccDefault,
								false);
						finallyScope.addLocalVariable(this.secretReturnValue);
						this.secretReturnValue.setConstant(Constant.NotAConstant); // not inlinable
					}
				}
			}
			this.finallyBlock.resolveUsing(finallyScope);
			// force the finally scope to have variable positions shifted after its try scope and catch ones
			int shiftScopesLength = this.catchArguments == null ? 1 : this.catchArguments.length + 1;
			finallyScope.shiftScopes = new BlockScope[shiftScopesLength];
			finallyScope.shiftScopes[0] = tryScope;
		}
	}
	this.tryBlock.resolveUsing(tryScope);

	// arguments type are checked against JavaLangThrowable in resolveForCatch(..)
	if (this.catchBlocks != null) {
		int length = this.catchArguments.length;
		TypeBinding[] argumentTypes = new TypeBinding[length];
		boolean containsUnionTypes = false;
		boolean catchHasError = false;
		for (int i = 0; i < length; i++) {
			BlockScope catchScope = new BlockScope(this.scope);
			if (finallyScope != null){
				finallyScope.shiftScopes[i+1] = catchScope;
			}
			// side effect on catchScope in resolveForCatch(..)
			Argument catchArgument = this.catchArguments[i];
			containsUnionTypes |= (catchArgument.type.bits & ASTNode.IsUnionType) != 0;
			if ((argumentTypes[i] = catchArgument.resolveForCatch(catchScope)) == null) {
				catchHasError = true;
			}
			this.catchBlocks[i].resolveUsing(catchScope);
		}
		if (catchHasError) {
			return;
		}
		// Verify that the catch clause are ordered in the right way:
		// more specialized first.
		verifyDuplicationAndOrder(length, argumentTypes, containsUnionTypes);
	} else {
		this.caughtExceptionTypes = new ReferenceBinding[0];
	}

	if (finallyScope != null){
		// add finallyScope as last subscope, so it can be shifted behind try/catch subscopes.
		// the shifting is necessary to achieve no overlay in between the finally scope and its
		// sibling in term of local variable positions.
		this.scope.addSubscope(finallyScope);
	}
}
public void traverse(ASTVisitor visitor, BlockScope blockScope) {
	if (visitor.visit(this, blockScope)) {
		LocalDeclaration[] localDeclarations = this.resources;
		for (int i = 0, max = localDeclarations.length; i < max; i++) {
			localDeclarations[i].traverse(visitor, this.scope);
		}
		this.tryBlock.traverse(visitor, this.scope);
		if (this.catchArguments != null) {
			for (int i = 0, max = this.catchBlocks.length; i < max; i++) {
				this.catchArguments[i].traverse(visitor, this.scope);
				this.catchBlocks[i].traverse(visitor, this.scope);
			}
		}
		if (this.finallyBlock != null)
			this.finallyBlock.traverse(visitor, this.scope);
	}
	visitor.endVisit(this, blockScope);
}
protected void verifyDuplicationAndOrder(int length, TypeBinding[] argumentTypes, boolean containsUnionTypes) {
	// Verify that the catch clause are ordered in the right way:
	// more specialized first.
	if (containsUnionTypes) {
		int totalCount = 0;
		ReferenceBinding[][] allExceptionTypes = new ReferenceBinding[length][];
		for (int i = 0; i < length; i++) {
			ReferenceBinding currentExceptionType = (ReferenceBinding) argumentTypes[i];
			TypeReference catchArgumentType = this.catchArguments[i].type;
			if ((catchArgumentType.bits & ASTNode.IsUnionType) != 0) {
				TypeReference[] typeReferences = ((UnionTypeReference) catchArgumentType).typeReferences;
				int typeReferencesLength = typeReferences.length;
				ReferenceBinding[] unionExceptionTypes = new ReferenceBinding[typeReferencesLength];
				for (int j = 0; j < typeReferencesLength; j++) {
					unionExceptionTypes[j] = (ReferenceBinding) typeReferences[j].resolvedType;
				}
				totalCount += typeReferencesLength;
				allExceptionTypes[i] = unionExceptionTypes;
			} else {
				allExceptionTypes[i] = new ReferenceBinding[] { currentExceptionType };
				totalCount++;
			}
		}
		this.caughtExceptionTypes = new ReferenceBinding[totalCount];
		this.caughtExceptionsCatchBlocks  = new int[totalCount];
		for (int i = 0, l = 0; i < length; i++) {
			ReferenceBinding[] currentExceptions = allExceptionTypes[i];
			loop: for (int j = 0, max = currentExceptions.length; j < max; j++) {
				ReferenceBinding exception = currentExceptions[j];
				this.caughtExceptionTypes[l] = exception;
				this.caughtExceptionsCatchBlocks[l++] = i;
				// now iterate over all previous exceptions
				for (int k = 0; k < i; k++) {
					ReferenceBinding[] exceptions = allExceptionTypes[k];
					for (int n = 0, max2 = exceptions.length; n < max2; n++) {
						ReferenceBinding currentException = exceptions[n];
						if (exception.isCompatibleWith(currentException)) {
							TypeReference catchArgumentType = this.catchArguments[i].type;
							if ((catchArgumentType.bits & ASTNode.IsUnionType) != 0) {
								catchArgumentType = ((UnionTypeReference) catchArgumentType).typeReferences[j];
							}
							this.scope.problemReporter().wrongSequenceOfExceptionTypesError(
								catchArgumentType,
								exception,
								currentException);
							break loop;
						}
					}
				}
			}
		}
	} else {
		this.caughtExceptionTypes = new ReferenceBinding[length];
		for (int i = 0; i < length; i++) {
			this.caughtExceptionTypes[i] = (ReferenceBinding) argumentTypes[i];
			for (int j = 0; j < i; j++) {
				if (this.caughtExceptionTypes[i].isCompatibleWith(argumentTypes[j])) {
					this.scope.problemReporter().wrongSequenceOfExceptionTypesError(
						this.catchArguments[i].type,
						this.caughtExceptionTypes[i],
						argumentTypes[j]);
				}
			}
		}
	}
}
}
