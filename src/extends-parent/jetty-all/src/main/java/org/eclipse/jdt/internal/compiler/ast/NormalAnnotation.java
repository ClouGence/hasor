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
import org.eclipse.jdt.internal.compiler.lookup.*;

/**
 * Normal annotation node
 */
public class NormalAnnotation extends Annotation {

	public MemberValuePair[] memberValuePairs;

	public NormalAnnotation(TypeReference type, int sourceStart) {
		this.type = type;
		this.sourceStart = sourceStart;
		this.sourceEnd = type.sourceEnd;
	}

	public ElementValuePair[] computeElementValuePairs() {
		int numberOfPairs = this.memberValuePairs == null ? 0 : this.memberValuePairs.length;
		if (numberOfPairs == 0)
			return Binding.NO_ELEMENT_VALUE_PAIRS;

		ElementValuePair[] pairs = new ElementValuePair[numberOfPairs];
		for (int i = 0; i < numberOfPairs; i++)
			pairs[i] = this.memberValuePairs[i].compilerElementPair;
		return pairs;
	}

	/**
	 * @see org.eclipse.jdt.internal.compiler.ast.Annotation#memberValuePairs()
	 */
	public MemberValuePair[] memberValuePairs() {
		return this.memberValuePairs == null ? NoValuePairs : this.memberValuePairs;
	}
	public StringBuffer printExpression(int indent, StringBuffer output) {
		super.printExpression(indent, output);
		output.append('(');
		if (this.memberValuePairs != null) {
			for (int i = 0, max = this.memberValuePairs.length; i < max; i++) {
				if (i > 0) {
					output.append(',');
				}
				this.memberValuePairs[i].print(indent, output);
			}
		}
		output.append(')');
		return output;
	}

	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.type != null) {
				this.type.traverse(visitor, scope);
			}
			if (this.memberValuePairs != null) {
				int memberValuePairsLength = this.memberValuePairs.length;
				for (int i = 0; i < memberValuePairsLength; i++)
					this.memberValuePairs[i].traverse(visitor, scope);
			}
		}
		visitor.endVisit(this, scope);
	}
}
