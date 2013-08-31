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
package org.eclipse.jdt.internal.compiler.parser;

/**
 * Internal statement structure for parsing recovery
 */
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Statement;

public class RecoveredStatement extends RecoveredElement {

	public Statement statement;
public RecoveredStatement(Statement statement, RecoveredElement parent, int bracketBalance){
	super(parent, bracketBalance);
	this.statement = statement;
}
/*
 * Answer the associated parsed structure
 */
public ASTNode parseTree(){
	return this.statement;
}
/*
 * Answer the very source end of the corresponding parse node
 */
public int sourceEnd(){
	return this.statement.sourceEnd;
}
public String toString(int tab){
	return tabString(tab) + "Recovered statement:\n" + this.statement.print(tab + 1, new StringBuffer(10)); //$NON-NLS-1$
}
public Statement updatedStatement(int depth, Set knownTypes){
	return this.statement;
}
public void updateParseTree(){
	updatedStatement(0, new HashSet());
}
/*
 * Update the declarationSourceEnd of the corresponding parse node
 */
public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd){
	if (this.statement.sourceEnd == 0)
		this.statement.sourceEnd = bodyEnd;
}
public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd){
	if ((--this.bracketBalance <= 0) && (this.parent != null)){
		this.updateSourceEndIfNecessary(braceStart, braceEnd);
		return this.parent.updateOnClosingBrace(braceStart, braceEnd);
	}
	return this;
}
}
