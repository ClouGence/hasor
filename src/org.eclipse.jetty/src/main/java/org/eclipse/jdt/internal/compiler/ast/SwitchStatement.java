/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - Contribution for bug 319201 - [null] no warning when unboxing SingleNameReference causes NPE
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import java.util.Arrays;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class SwitchStatement extends Statement {

	public Expression expression;
	public Statement[] statements;
	public BlockScope scope;
	public int explicitDeclarations;
	public BranchLabel breakLabel;
	public CaseStatement[] cases;
	public CaseStatement defaultCase;
	public int blockStart;
	public int caseCount;
	int[] constants;
	String[] stringConstants;

	// fallthrough
	public final static int CASE = 0;
	public final static int FALLTHROUGH = 1;
	public final static int ESCAPING = 2;
	
	// for switch on strings
	private static final char[] SecretStringVariableName = " switchDispatchString".toCharArray(); //$NON-NLS-1$


	public SyntheticMethodBinding synthetic; // use for switch on enums types

	// for local variables table attributes
	int preSwitchInitStateIndex = -1;
	int mergedInitStateIndex = -1;
	
	CaseStatement[] duplicateCaseStatements = null;
	int duplicateCaseStatementsCounter = 0;
	private LocalVariableBinding dispatchStringCopy = null;

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		try {
			flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
			if ((this.expression.implicitConversion & TypeIds.UNBOXING) != 0
					|| (this.expression.resolvedType != null && this.expression.resolvedType.id == T_JavaLangString)) {
				this.expression.checkNPE(currentScope, flowContext, flowInfo);
			}
			SwitchFlowContext switchContext =
				new SwitchFlowContext(flowContext, this, (this.breakLabel = new BranchLabel()));

			// analyse the block by considering specially the case/default statements (need to bind them
			// to the entry point)
			FlowInfo caseInits = FlowInfo.DEAD_END;
			// in case of statements before the first case
			this.preSwitchInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
			int caseIndex = 0;
			if (this.statements != null) {
				int initialComplaintLevel = (flowInfo.reachMode() & FlowInfo.UNREACHABLE) != 0 ? Statement.COMPLAINED_FAKE_REACHABLE : Statement.NOT_COMPLAINED;
				int complaintLevel = initialComplaintLevel;
				int fallThroughState = CASE;
				for (int i = 0, max = this.statements.length; i < max; i++) {
					Statement statement = this.statements[i];
					if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex])) { // statement is a case
						this.scope.enclosingCase = this.cases[caseIndex]; // record entering in a switch case block
						caseIndex++;
						if (fallThroughState == FALLTHROUGH
								&& (statement.bits & ASTNode.DocumentedFallthrough) == 0) { // the case is not fall-through protected by a line comment
							this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
						}
						caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
						complaintLevel = initialComplaintLevel; // reset complaint
						fallThroughState = CASE;
					} else if (statement == this.defaultCase) { // statement is the default case
						this.scope.enclosingCase = this.defaultCase; // record entering in a switch case block
						if (fallThroughState == FALLTHROUGH
								&& (statement.bits & ASTNode.DocumentedFallthrough) == 0) {
							this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
						}
						caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
						complaintLevel = initialComplaintLevel; // reset complaint
						fallThroughState = CASE;
					} else {
						fallThroughState = FALLTHROUGH; // reset below if needed
					}
					if ((complaintLevel = statement.complainIfUnreachable(caseInits, this.scope, complaintLevel)) < Statement.COMPLAINED_UNREACHABLE) {
						caseInits = statement.analyseCode(this.scope, switchContext, caseInits);
						if (caseInits == FlowInfo.DEAD_END) {
							fallThroughState = ESCAPING;
						}
					}
				}
			}

			final TypeBinding resolvedTypeBinding = this.expression.resolvedType;
			if (resolvedTypeBinding.isEnum()) {
				final SourceTypeBinding sourceTypeBinding = currentScope.classScope().referenceContext.binding;
				this.synthetic = sourceTypeBinding.addSyntheticMethodForSwitchEnum(resolvedTypeBinding);
			}
			// if no default case, then record it may jump over the block directly to the end
			if (this.defaultCase == null) {
				// only retain the potential initializations
				flowInfo.addPotentialInitializationsFrom(caseInits.mergedWith(switchContext.initsOnBreak));
				this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
				return flowInfo;
			}

			// merge all branches inits
			FlowInfo mergedInfo = caseInits.mergedWith(switchContext.initsOnBreak);
			this.mergedInitStateIndex =
				currentScope.methodScope().recordInitializationStates(mergedInfo);
			return mergedInfo;
		} finally {
			if (this.scope != null) this.scope.enclosingCase = null; // no longer inside switch case block
		}
	}

	/**
	 * Switch on String code generation
	 * This assumes that hashCode() specification for java.lang.String is API
	 * and is stable.
	 * @see "http://java.sun.com/j2se/1.4.2/docs/api/java/lang/String.html"
	 * @see "http://download.oracle.com/docs/cd/E17409_01/javase/6/docs/api/java/lang/String.html"
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 */
	public void generateCodeForStringSwitch(BlockScope currentScope, CodeStream codeStream) {

		try {
			if ((this.bits & IsReachable) == 0) {
				return;
			}
			int pc = codeStream.position;
			
			class StringSwitchCase implements Comparable {
				int hashCode;
				String string;
				BranchLabel label;
				public StringSwitchCase(int hashCode, String string, BranchLabel label) {
					this.hashCode = hashCode;
					this.string = string;
					this.label = label;
				}
				public int compareTo(Object o) {
					StringSwitchCase that = (StringSwitchCase) o;
					if (this.hashCode == that.hashCode) {
						return 0;
					}
					if (this.hashCode > that.hashCode) {
						return 1;
					}
					return -1;
				}
				public String toString() {
					return "StringSwitchCase :\n" + //$NON-NLS-1$
					       "case " + this.hashCode + ":(" + this.string + ")\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$	       
				}
			}

			final boolean hasCases = this.caseCount != 0;

			StringSwitchCase [] stringCases = new StringSwitchCase[this.caseCount]; // may have to shrink later if multiple strings hash to same code.
			BranchLabel[] sourceCaseLabels = new BranchLabel[this.caseCount];
			CaseLabel [] hashCodeCaseLabels = new CaseLabel[this.caseCount];
			this.constants = new int[this.caseCount];  // hashCode() values.
			for (int i = 0, max = this.caseCount; i < max; i++) {
				this.cases[i].targetLabel = (sourceCaseLabels[i] = new BranchLabel(codeStream));  // A branch label, not a case label.
				sourceCaseLabels[i].tagBits |= BranchLabel.USED;
				stringCases[i] = new StringSwitchCase(this.stringConstants[i].hashCode(), this.stringConstants[i], sourceCaseLabels[i]);
				hashCodeCaseLabels[i] = new CaseLabel(codeStream);
				hashCodeCaseLabels[i].tagBits |= BranchLabel.USED;
				
			}
			Arrays.sort(stringCases);

			int uniqHashCount = 0;
			int lastHashCode = 0; 
			for (int i = 0, length = this.caseCount; i < length; ++i) {
				int hashCode = stringCases[i].hashCode;
				if (i == 0 || hashCode != lastHashCode) {
					lastHashCode = this.constants[uniqHashCount++] = hashCode;
				}
			}
				
			if (uniqHashCount != this.caseCount) { // multiple keys hashed to the same value.
				System.arraycopy(this.constants, 0, this.constants = new int[uniqHashCount], 0, uniqHashCount);
				System.arraycopy(hashCodeCaseLabels, 0, hashCodeCaseLabels = new CaseLabel[uniqHashCount], 0, uniqHashCount);
			}
			int[] sortedIndexes = new int[uniqHashCount]; // hash code are sorted already anyways.
			for (int i = 0; i < uniqHashCount; i++) {
				sortedIndexes[i] = i;
			}

			CaseLabel defaultCaseLabel = new CaseLabel(codeStream);
			defaultCaseLabel.tagBits |= BranchLabel.USED;

			// prepare the labels and constants
			this.breakLabel.initialize(codeStream);
			
			BranchLabel defaultBranchLabel = new BranchLabel(codeStream);
			if (hasCases) defaultBranchLabel.tagBits |= BranchLabel.USED;
			if (this.defaultCase != null) {
				this.defaultCase.targetLabel = defaultBranchLabel;
			}
			// generate expression
			this.expression.generateCode(currentScope, codeStream, true);
			codeStream.store(this.dispatchStringCopy, true);  // leaves string on operand stack
			codeStream.addVariable(this.dispatchStringCopy);
			codeStream.invokeStringHashCode();
			if (hasCases) {
				codeStream.lookupswitch(defaultCaseLabel, this.constants, sortedIndexes, hashCodeCaseLabels);
				for (int i = 0, j = 0, max = this.caseCount; i < max; i++) {
					int hashCode = stringCases[i].hashCode;
					if (i == 0 || hashCode != lastHashCode) {
						lastHashCode = hashCode;
						if (i != 0) {
							codeStream.goto_(defaultBranchLabel);
						}
						hashCodeCaseLabels[j++].place();
					}
					codeStream.load(this.dispatchStringCopy);
					codeStream.ldc(stringCases[i].string);
					codeStream.invokeStringEquals();
					codeStream.ifne(stringCases[i].label);
				}
				codeStream.goto_(defaultBranchLabel);
			} else {
				codeStream.pop();
			}

			// generate the switch block statements
			int caseIndex = 0;
			if (this.statements != null) {
				for (int i = 0, maxCases = this.statements.length; i < maxCases; i++) {
					Statement statement = this.statements[i];
					if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex])) { // statements[i] is a case
						this.scope.enclosingCase = this.cases[caseIndex]; // record entering in a switch case block
						if (this.preSwitchInitStateIndex != -1) {
							codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
						}
						caseIndex++;
					} else {
						if (statement == this.defaultCase) { // statements[i] is a case or a default case
							defaultCaseLabel.place(); // branch label gets placed by generateCode below.
							this.scope.enclosingCase = this.defaultCase; // record entering in a switch case block
							if (this.preSwitchInitStateIndex != -1) {
								codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
							}
						}
					}
					statement.generateCode(this.scope, codeStream);
				}
			}
			
			// May loose some local variable initializations : affecting the local variable attributes
			if (this.mergedInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
				codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
			}
			codeStream.removeVariable(this.dispatchStringCopy);
			if (this.scope != currentScope) {
				codeStream.exitUserScope(this.scope);
			}
			// place the trailing labels (for break and default case)
			this.breakLabel.place();
			if (this.defaultCase == null) {
				// we want to force an line number entry to get an end position after the switch statement
				codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
				defaultCaseLabel.place();
				defaultBranchLabel.place();
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			if (this.scope != null) this.scope.enclosingCase = null; // no longer inside switch case block
		}
	}

	
	/**
	 * Switch code generation
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream) {
		if (this.expression.resolvedType.id == TypeIds.T_JavaLangString) {
			generateCodeForStringSwitch(currentScope, codeStream);
			return;
		}
		try {
			if ((this.bits & IsReachable) == 0) {
				return;
			}
			int pc = codeStream.position;

			// prepare the labels and constants
			this.breakLabel.initialize(codeStream);
			CaseLabel[] caseLabels = new CaseLabel[this.caseCount];
			for (int i = 0, max = this.caseCount; i < max; i++) {
				this.cases[i].targetLabel = (caseLabels[i] = new CaseLabel(codeStream));
				caseLabels[i].tagBits |= BranchLabel.USED;
			}
			CaseLabel defaultLabel = new CaseLabel(codeStream);
			final boolean hasCases = this.caseCount != 0;
			if (hasCases) defaultLabel.tagBits |= BranchLabel.USED;
			if (this.defaultCase != null) {
				this.defaultCase.targetLabel = defaultLabel;
			}

			final TypeBinding resolvedType = this.expression.resolvedType;
			boolean valueRequired = false;
			if (resolvedType.isEnum()) {
				// go through the translation table
				codeStream.invoke(Opcodes.OPC_invokestatic, this.synthetic, null /* default declaringClass */);
				this.expression.generateCode(currentScope, codeStream, true);
				// get enum constant ordinal()
				codeStream.invokeEnumOrdinal(resolvedType.constantPoolName());
				codeStream.iaload();
				if (!hasCases) {
					// we can get rid of the generated ordinal value
					codeStream.pop();
				}
			} else {
				valueRequired = this.expression.constant == Constant.NotAConstant || hasCases;
				// generate expression
				this.expression.generateCode(currentScope, codeStream, valueRequired);
			}
			// generate the appropriate switch table/lookup bytecode
			if (hasCases) {
				int[] sortedIndexes = new int[this.caseCount];
				// we sort the keys to be able to generate the code for tableswitch or lookupswitch
				for (int i = 0; i < this.caseCount; i++) {
					sortedIndexes[i] = i;
				}
				int[] localKeysCopy;
				System.arraycopy(this.constants, 0, (localKeysCopy = new int[this.caseCount]), 0, this.caseCount);
				CodeStream.sort(localKeysCopy, 0, this.caseCount - 1, sortedIndexes);

				int max = localKeysCopy[this.caseCount - 1];
				int min = localKeysCopy[0];
				if ((long) (this.caseCount * 2.5) > ((long) max - (long) min)) {

					// work-around 1.3 VM bug, if max>0x7FFF0000, must use lookup bytecode
					// see http://dev.eclipse.org/bugs/show_bug.cgi?id=21557
					if (max > 0x7FFF0000 && currentScope.compilerOptions().complianceLevel < ClassFileConstants.JDK1_4) {
						codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);

					} else {
						codeStream.tableswitch(
							defaultLabel,
							min,
							max,
							this.constants,
							sortedIndexes,
							caseLabels);
					}
				} else {
					codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
				}
				codeStream.updateLastRecordedEndPC(this.scope, codeStream.position);
			} else if (valueRequired) {
				codeStream.pop();
			}

			// generate the switch block statements
			int caseIndex = 0;
			if (this.statements != null) {
				for (int i = 0, maxCases = this.statements.length; i < maxCases; i++) {
					Statement statement = this.statements[i];
					if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex])) { // statements[i] is a case
						this.scope.enclosingCase = this.cases[caseIndex]; // record entering in a switch case block
						if (this.preSwitchInitStateIndex != -1) {
							codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
						}
						caseIndex++;
					} else {
						if (statement == this.defaultCase) { // statements[i] is a case or a default case
							this.scope.enclosingCase = this.defaultCase; // record entering in a switch case block
							if (this.preSwitchInitStateIndex != -1) {
								codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
							}
						}
					}
					statement.generateCode(this.scope, codeStream);
				}
			}
			// May loose some local variable initializations : affecting the local variable attributes
			if (this.mergedInitStateIndex != -1) {
				codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
				codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
			}
			if (this.scope != currentScope) {
				codeStream.exitUserScope(this.scope);
			}
			// place the trailing labels (for break and default case)
			this.breakLabel.place();
			if (this.defaultCase == null) {
				// we want to force an line number entry to get an end position after the switch statement
				codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
				defaultLabel.place();
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
		} finally {
			if (this.scope != null) this.scope.enclosingCase = null; // no longer inside switch case block
		}
	}

	public StringBuffer printStatement(int indent, StringBuffer output) {

		printIndent(indent, output).append("switch ("); //$NON-NLS-1$
		this.expression.printExpression(0, output).append(") {"); //$NON-NLS-1$
		if (this.statements != null) {
			for (int i = 0; i < this.statements.length; i++) {
				output.append('\n');
				if (this.statements[i] instanceof CaseStatement) {
					this.statements[i].printStatement(indent, output);
				} else {
					this.statements[i].printStatement(indent+2, output);
				}
			}
		}
		output.append("\n"); //$NON-NLS-1$
		return printIndent(indent, output).append('}');
	}

	public void resolve(BlockScope upperScope) {
		try {
			boolean isEnumSwitch = false;
			boolean isStringSwitch = false;
			TypeBinding expressionType = this.expression.resolveType(upperScope);
			if (expressionType != null) {
				this.expression.computeConversion(upperScope, expressionType, expressionType);
				checkType: {
					if (!expressionType.isValidBinding()) {
						expressionType = null; // fault-tolerance: ignore type mismatch from constants from hereon
						break checkType;
					} else if (expressionType.isBaseType()) {
						if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, TypeBinding.INT))
							break checkType;
						if (expressionType.isCompatibleWith(TypeBinding.INT))
							break checkType;
					} else if (expressionType.isEnum()) {
						isEnumSwitch = true;
						break checkType;
					} else if (upperScope.isBoxingCompatibleWith(expressionType, TypeBinding.INT)) {
						this.expression.computeConversion(upperScope, TypeBinding.INT, expressionType);
						break checkType;
					} else if (upperScope.compilerOptions().complianceLevel >= ClassFileConstants.JDK1_7 && expressionType.id == TypeIds.T_JavaLangString) {
						isStringSwitch = true;
						break checkType;
					}
					upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
					expressionType = null; // fault-tolerance: ignore type mismatch from constants from hereon
				}
			}
			if (isStringSwitch) {
				// the secret variable should be created before iterating over the switch's statements that could
				// create more locals. This must be done to prevent overlapping of locals
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=356002
				this.dispatchStringCopy  = new LocalVariableBinding(SecretStringVariableName, upperScope.getJavaLangString(), ClassFileConstants.AccDefault, false);
				upperScope.addLocalVariable(this.dispatchStringCopy);
				this.dispatchStringCopy.setConstant(Constant.NotAConstant);
				this.dispatchStringCopy.useFlag = LocalVariableBinding.USED;
			}
			if (this.statements != null) {
				this.scope = new BlockScope(upperScope);
				int length;
				// collection of cases is too big but we will only iterate until caseCount
				this.cases = new CaseStatement[length = this.statements.length];
				if (!isStringSwitch) {
					this.constants = new int[length];
				} else {
					this.stringConstants = new String[length];
				}
				int counter = 0;
				for (int i = 0; i < length; i++) {
					Constant constant;
					final Statement statement = this.statements[i];
					if ((constant = statement.resolveCase(this.scope, expressionType, this)) != Constant.NotAConstant) {
						if (!isStringSwitch) {
							int key = constant.intValue();
							//----check for duplicate case statement------------
							for (int j = 0; j < counter; j++) {
								if (this.constants[j] == key) {
									reportDuplicateCase((CaseStatement) statement, this.cases[j], length);
								}
							}
							this.constants[counter++] = key;
						} else {
							String key = constant.stringValue();
							//----check for duplicate case statement------------
							for (int j = 0; j < counter; j++) {
								if (this.stringConstants[j].equals(key)) {
									reportDuplicateCase((CaseStatement) statement, this.cases[j], length);
								}
							}
							this.stringConstants[counter++] = key;			
						}
					}
				}
				if (length != counter) { // resize constants array
					if (!isStringSwitch) {
						System.arraycopy(this.constants, 0, this.constants = new int[counter], 0, counter);
					} else {
						System.arraycopy(this.stringConstants, 0, this.stringConstants = new String[counter], 0, counter);
					}
				}
			} else {
				if ((this.bits & UndocumentedEmptyBlock) != 0) {
					upperScope.problemReporter().undocumentedEmptyBlock(this.blockStart, this.sourceEnd);
				}
			}
			// for enum switch, check if all constants are accounted for (if no default)
			if (isEnumSwitch && this.defaultCase == null
					&& upperScope.compilerOptions().getSeverity(CompilerOptions.IncompleteEnumSwitch) != ProblemSeverities.Ignore) {
				int constantCount = this.constants == null ? 0 : this.constants.length; // could be null if no case statement
				if (constantCount == this.caseCount
						&& this.caseCount != ((ReferenceBinding)expressionType).enumConstantCount()) {
					FieldBinding[] enumFields = ((ReferenceBinding)expressionType.erasure()).fields();
					for (int i = 0, max = enumFields.length; i <max; i++) {
						FieldBinding enumConstant = enumFields[i];
						if ((enumConstant.modifiers & ClassFileConstants.AccEnum) == 0) continue;
						findConstant : {
							for (int j = 0; j < this.caseCount; j++) {
								if ((enumConstant.id + 1) == this.constants[j]) // zero should not be returned see bug 141810
									break findConstant;
							}
							// enum constant did not get referenced from switch
							upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
						}
					}
				}
			}
		} finally {
			if (this.scope != null) this.scope.enclosingCase = null; // no longer inside switch case block
		}
	}

	private void reportDuplicateCase(final CaseStatement duplicate, final CaseStatement original, int length) {
		if (this.duplicateCaseStatements == null) {
			this.scope.problemReporter().duplicateCase(original);
			this.scope.problemReporter().duplicateCase(duplicate);
			this.duplicateCaseStatements = new CaseStatement[length];
			this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = original;
			this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
		} else {
			boolean found = false;
			searchReportedDuplicate: for (int k = 2; k < this.duplicateCaseStatementsCounter; k++) {
				if (this.duplicateCaseStatements[k] == duplicate) {
					found = true;
					break searchReportedDuplicate;
				}
			}
			if (!found) {
				this.scope.problemReporter().duplicateCase(duplicate);
				this.duplicateCaseStatements[this.duplicateCaseStatementsCounter++] = duplicate;
			}
		}
	}

	public void traverse(
			ASTVisitor visitor,
			BlockScope blockScope) {

		if (visitor.visit(this, blockScope)) {
			this.expression.traverse(visitor, blockScope);
			if (this.statements != null) {
				int statementsLength = this.statements.length;
				for (int i = 0; i < statementsLength; i++)
					this.statements[i].traverse(visitor, this.scope);
			}
		}
		visitor.endVisit(this, blockScope);
	}

	/**
	 * Dispatch the call on its last statement.
	 */
	public void branchChainTo(BranchLabel label) {

		// in order to improve debug attributes for stepping (11431)
		// we want to inline the jumps to #breakLabel which already got
		// generated (if any), and have them directly branch to a better
		// location (the argument label).
		// we know at this point that the breakLabel already got placed
		if (this.breakLabel.forwardReferenceCount() > 0) {
			label.becomeDelegateFor(this.breakLabel);
		}
	}
}
