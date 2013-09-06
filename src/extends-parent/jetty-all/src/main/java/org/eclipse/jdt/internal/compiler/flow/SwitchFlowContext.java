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
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

/**
 * Reflects the context of code analysis, keeping track of enclosing
 *	try statements, exception handlers, etc...
 */
public class SwitchFlowContext extends FlowContext {

	public BranchLabel breakLabel;
	public UnconditionalFlowInfo initsOnBreak = FlowInfo.DEAD_END;

public SwitchFlowContext(FlowContext parent, ASTNode associatedNode, BranchLabel breakLabel) {
	super(parent, associatedNode);
	this.breakLabel = breakLabel;
}

public BranchLabel breakLabel() {
	return this.breakLabel;
}

public String individualToString() {
	StringBuffer buffer = new StringBuffer("Switch flow context"); //$NON-NLS-1$
	buffer.append("[initsOnBreak -").append(this.initsOnBreak.toString()).append(']'); //$NON-NLS-1$
	return buffer.toString();
}

public boolean isBreakable() {
	return true;
}

public void recordBreakFrom(FlowInfo flowInfo) {
	if ((this.initsOnBreak.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) == 0) {
		this.initsOnBreak = this.initsOnBreak.mergedWith(flowInfo.unconditionalInits());
	}
	else {
		this.initsOnBreak = flowInfo.unconditionalCopy();
	}
}
}
