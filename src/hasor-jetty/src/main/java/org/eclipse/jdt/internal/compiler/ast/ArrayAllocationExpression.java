/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;

public class ArrayAllocationExpression extends Expression {

	public TypeReference type;

	//dimensions.length gives the number of dimensions, but the
	// last ones may be nulled as in new int[4][5][][]
	public Expression[] dimensions;
	public ArrayInitializer initializer;

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		for (int i = 0, max = this.dimensions.length; i < max; i++) {
			Expression dim;
			if ((dim = this.dimensions[i]) != null) {
				flowInfo = dim.analyseCode(currentScope, flowContext, flowInfo);
				if ((dim.implicitConversion & TypeIds.UNBOXING) != 0) {
					dim.checkNPE(currentScope, flowContext, flowInfo);
				}
			}
		}
		if (this.initializer != null) {
			return this.initializer.analyseCode(currentScope, flowContext, flowInfo);
		}
		return flowInfo;
	}

	/**
	 * Code generation for a array allocation expression
	 */
	public void generateCode(BlockScope currentScope, 	CodeStream codeStream, boolean valueRequired) {

		int pc = codeStream.position;

		if (this.initializer != null) {
			this.initializer.generateCode(currentScope, codeStream, valueRequired);
			return;
		}

		int explicitDimCount = 0;
		for (int i = 0, max = this.dimensions.length; i < max; i++) {
			Expression dimExpression;
			if ((dimExpression = this.dimensions[i]) == null) break; // implicit dim, no further explict after this point
			dimExpression.generateCode(currentScope, codeStream, true);
			explicitDimCount++;
		}

		// array allocation
		if (explicitDimCount == 1) {
			// Mono-dimensional array
			codeStream.newArray((ArrayBinding)this.resolvedType);
		} else {
			// Multi-dimensional array
			codeStream.multianewarray(this.resolvedType, explicitDimCount);
		}
		if (valueRequired) {
			codeStream.generateImplicitConversion(this.implicitConversion);
		} else {
			codeStream.pop();
		}
		codeStream.recordPositionsFrom(pc, this.sourceStart);
	}


	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("new "); //$NON-NLS-1$
		this.type.print(0, output);
		for (int i = 0; i < this.dimensions.length; i++) {
			if (this.dimensions[i] == null)
				output.append("[]"); //$NON-NLS-1$
			else {
				output.append('[');
				this.dimensions[i].printExpression(0, output);
				output.append(']');
			}
		}
		if (this.initializer != null) this.initializer.printExpression(0, output);
		return output;
	}

	public TypeBinding resolveType(BlockScope scope) {
		// Build an array type reference using the current dimensions
		// The parser does not check for the fact that dimension may be null
		// only at the -end- like new int [4][][]. The parser allows new int[][4][]
		// so this must be checked here......(this comes from a reduction to LL1 grammar)

		TypeBinding referenceType = this.type.resolveType(scope, true /* check bounds*/);

		// will check for null after dimensions are checked
		this.constant = Constant.NotAConstant;
		if (referenceType == TypeBinding.VOID) {
			scope.problemReporter().cannotAllocateVoidArray(this);
			referenceType = null;
		}

		// check the validity of the dimension syntax (and test for all null dimensions)
		int explicitDimIndex = -1;
		loop: for (int i = this.dimensions.length; --i >= 0;) {
			if (this.dimensions[i] != null) {
				if (explicitDimIndex < 0) explicitDimIndex = i;
			} else if (explicitDimIndex > 0) {
				// should not have an empty dimension before an non-empty one
				scope.problemReporter().incorrectLocationForNonEmptyDimension(this, explicitDimIndex);
				break loop;
			}
		}

		// explicitDimIndex < 0 says if all dimensions are nulled
		// when an initializer is given, no dimension must be specified
		if (this.initializer == null) {
			if (explicitDimIndex < 0) {
				scope.problemReporter().mustDefineDimensionsOrInitializer(this);
			}
			// allow new List<?>[5] - only check for generic array when no initializer, since also checked inside initializer resolution
			if (referenceType != null && !referenceType.isReifiable()) {
			    scope.problemReporter().illegalGenericArray(referenceType, this);
			}
		} else if (explicitDimIndex >= 0) {
			scope.problemReporter().cannotDefineDimensionsAndInitializer(this);
		}

		// dimensions resolution
		for (int i = 0; i <= explicitDimIndex; i++) {
			Expression dimExpression;
			if ((dimExpression = this.dimensions[i]) != null) {
				TypeBinding dimensionType = dimExpression.resolveTypeExpecting(scope, TypeBinding.INT);
				if (dimensionType != null) {
					this.dimensions[i].computeConversion(scope, TypeBinding.INT, dimensionType);
				}
			}
		}

		// building the array binding
		if (referenceType != null) {
			if (this.dimensions.length > 255) {
				scope.problemReporter().tooManyDimensions(this);
			}
			this.resolvedType = scope.createArrayType(referenceType, this.dimensions.length);

			// check the initializer
			if (this.initializer != null) {
				if ((this.initializer.resolveTypeExpecting(scope, this.resolvedType)) != null)
					this.initializer.binding = (ArrayBinding)this.resolvedType;
			}
			if ((referenceType.tagBits & TagBits.HasMissingType) != 0) {
				return null;
			}
		}
		return this.resolvedType;
	}


	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			int dimensionsLength = this.dimensions.length;
			this.type.traverse(visitor, scope);
			for (int i = 0; i < dimensionsLength; i++) {
				if (this.dimensions[i] != null)
					this.dimensions[i].traverse(visitor, scope);
			}
			if (this.initializer != null)
				this.initializer.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}
}
