/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class ImportReference extends ASTNode {

	public char[][] tokens;
	public long[] sourcePositions; //each entry is using the code : (start<<32) + end
	public int declarationEnd; // doesn't include an potential trailing comment
	public int declarationSourceStart;
	public int declarationSourceEnd;
	public int modifiers; // 1.5 addition for static imports
	public Annotation[] annotations;
	// star end position
	public int trailingStarPosition;

	public ImportReference(
			char[][] tokens,
			long[] sourcePositions,
			boolean onDemand,
			int modifiers) {

		this.tokens = tokens;
		this.sourcePositions = sourcePositions;
		if (onDemand) {
			this.bits |= ASTNode.OnDemand;
		}
		this.sourceEnd = (int) (sourcePositions[sourcePositions.length-1] & 0x00000000FFFFFFFF);
		this.sourceStart = (int) (sourcePositions[0] >>> 32);
		this.modifiers = modifiers;
	}

	public boolean isStatic() {
		return (this.modifiers & ClassFileConstants.AccStatic) != 0;
	}

	/**
	 * @return char[][]
	 */
	public char[][] getImportName() {

		return this.tokens;
	}

	public StringBuffer print(int indent, StringBuffer output) {

		return print(indent, output, true);
	}

	public StringBuffer print(int tab, StringBuffer output, boolean withOnDemand) {

		/* when withOnDemand is false, only the name is printed */
		for (int i = 0; i < this.tokens.length; i++) {
			if (i > 0) output.append('.');
			output.append(this.tokens[i]);
		}
		if (withOnDemand && ((this.bits & ASTNode.OnDemand) != 0)) {
			output.append(".*"); //$NON-NLS-1$
		}
		return output;
	}

	public void traverse(ASTVisitor visitor, CompilationUnitScope scope) {
		// annotations are traversed during the compilation unit traversal using a class scope
		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
}
