/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class LabeledStatement extends Statement {

	public Statement statement;
	public char[] label;
	public BranchLabel targetLabel;
	public int labelEnd;

	// for local variables table attributes
	int mergedInitStateIndex = -1;

	/**
	 * LabeledStatement constructor comment.
	 */
	public LabeledStatement(char[] label, Statement statement, long labelPosition, int sourceEnd) {

		this.statement = statement;
		// remember useful empty statement
		if (statement instanceof EmptyStatement) statement.bits |= IsUsefulEmptyStatement;
		this.label = label;
		this.sourceStart = (int)(labelPosition >>> 32);
		this.labelEnd = (int) labelPosition;
		this.sourceEnd = sourceEnd;
	}

	public FlowInfo analyseCode(
		BlockScope currentScope,
		FlowContext flowContext,
		FlowInfo flowInfo) {

		// need to stack a context to store explicit label, answer inits in case of normal completion merged
		// with those relative to the exit path from break statement occurring inside the labeled statement.
		if (this.statement == null) {
			return flowInfo;
		} else {
			LabelFlowContext labelContext;
			FlowInfo statementInfo, mergedInfo;
			statementInfo = this.statement.analyseCode(
				currentScope,
				(labelContext =
					new LabelFlowContext(
						flowContext,
						this,
						this.label,
						(this.targetLabel = new BranchLabel()),
						currentScope)),
				flowInfo);
			boolean reinjectNullInfo = (statementInfo.tagBits & FlowInfo.UNREACHABLE) != 0 &&
				(labelContext.initsOnBreak.tagBits & FlowInfo.UNREACHABLE) == 0;
			mergedInfo = statementInfo.mergedWith(labelContext.initsOnBreak);
			if (reinjectNullInfo) {
				// an embedded loop has had no chance to reinject forgotten null info
				((UnconditionalFlowInfo)mergedInfo).addInitializationsFrom(flowInfo.unconditionalFieldLessCopy()).
					addInitializationsFrom(labelContext.initsOnBreak.unconditionalFieldLessCopy());
			}
			this.mergedInitStateIndex =
				currentScope.methodScope().recordInitializationStates(mergedInfo);
			if ((this.bits & ASTNode.LabelUsed) == 0) {
				currentScope.problemReporter().unusedLabel(this);
			}
			return mergedInfo;
		}
	}

	public ASTNode concreteStatement() {

		// return statement.concreteStatement(); // for supporting nested labels:   a:b:c: someStatement (see 21912)
		return this.statement;
	}

	/**
	 * Code generation for labeled statement
	 *
	 * may not need actual source positions recording
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream) {

		if ((this.bits & IsReachable) == 0) {
			return;
		}
		int pc = codeStream.position;
		if (this.targetLabel != null) {
			this.targetLabel.initialize(codeStream);
			if (this.statement != null) {
				this.statement.generateCode(currentScope, codeStream);
			}
			this.targetLabel.place();
		}
		// May loose some local variable initializations : affecting the local variable attributes
		if (this.mergedInitStateIndex != -1) {
			codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
			codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}

	public StringBuffer printStatement(int tab, StringBuffer output) {

		printIndent(tab, output).append(this.label).append(": "); //$NON-NLS-1$
		if (this.statement == null)
			output.append(';');
		else
			this.statement.printStatement(0, output);
		return output;
	}

	public void resolve(BlockScope scope) {

		if (this.statement != null) {
			this.statement.resolve(scope);
		}
	}


	public void traverse(
		ASTVisitor visitor,
		BlockScope blockScope) {

		if (visitor.visit(this, blockScope)) {
			if (this.statement != null) this.statement.traverse(visitor, blockScope);
		}
		visitor.endVisit(this, blockScope);
	}
}
