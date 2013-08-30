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
package org.eclipse.jdt.internal.compiler.parser.diagnose;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ExtraCompilerModifiers;

public class RangeUtil {

	// flags
	public static final int NO_FLAG = 0;
	public static final int LBRACE_MISSING = 1;
	public static final int IGNORE = 2;

	static class RangeResult {
		private static final int INITIAL_SIZE = 10;
		int pos;
		int[] intervalStarts;
		int[] intervalEnds;
		int[] intervalFlags;

		RangeResult() {
			this.pos = 0;
			this.intervalStarts = new int[INITIAL_SIZE];
			this.intervalEnds = new int[INITIAL_SIZE];
			this.intervalFlags = new int[INITIAL_SIZE];
		}

		void addInterval(int start, int end){
			addInterval(start, end, NO_FLAG);
		}

		void addInterval(int start, int end, int flags){
			if(this.pos >= this.intervalStarts.length) {
				System.arraycopy(this.intervalStarts, 0, this.intervalStarts = new int[this.pos * 2], 0, this.pos);
				System.arraycopy(this.intervalEnds, 0, this.intervalEnds = new int[this.pos * 2], 0, this.pos);
				System.arraycopy(this.intervalFlags, 0, this.intervalFlags = new int[this.pos * 2], 0, this.pos);
			}
			this.intervalStarts[this.pos] = start;
			this.intervalEnds[this.pos] = end;
			this.intervalFlags[this.pos] = flags;
			this.pos++;
		}

		int[][] getRanges() {
			int[] resultStarts = new int[this.pos];
			int[] resultEnds = new int[this.pos];
			int[] resultFlags = new int[this.pos];

			System.arraycopy(this.intervalStarts, 0, resultStarts, 0, this.pos);
			System.arraycopy(this.intervalEnds, 0, resultEnds, 0, this.pos);
			System.arraycopy(this.intervalFlags, 0, resultFlags, 0, this.pos);

			if (resultStarts.length > 1) {
				quickSort(resultStarts, resultEnds, resultFlags, 0, resultStarts.length - 1);
			}
			return new int[][]{resultStarts, resultEnds, resultFlags};
		}

		private void quickSort(int[] list, int[] list2, int[] list3, int left, int right) {
			int original_left= left;
			int original_right= right;
			int mid= list[left + (right - left) / 2];
			do {
				while (compare(list[left], mid) < 0) {
					left++;
				}
				while (compare(mid, list[right]) < 0) {
					right--;
				}
				if (left <= right) {
					int tmp= list[left];
					list[left]= list[right];
					list[right]= tmp;

					tmp = list2[left];
					list2[left]= list2[right];
					list2[right]= tmp;

					tmp = list3[left];
					list3[left]= list3[right];
					list3[right]= tmp;

					left++;
					right--;
				}
			} while (left <= right);

			if (original_left < right) {
				quickSort(list, list2, list3, original_left, right);
			}
			if (left < original_right) {
				quickSort(list, list2, list3, left, original_right);
			}
		}

		private int compare(int i1, int i2) {
			return i1 - i2;
		}
	}



	public static boolean containsErrorInSignature(AbstractMethodDeclaration method){
		return method.sourceEnd + 1 == method.bodyStart	|| method.bodyEnd == method.declarationSourceEnd;
	}

	public static int[][] computeDietRange(TypeDeclaration[] types) {
		if(types == null || types.length == 0) {
			return new int[3][0];
		} else {
			RangeResult result = new RangeResult();
			computeDietRange0(types, result);
			return result.getRanges();
		}
	}

	private static void computeDietRange0(TypeDeclaration[] types, RangeResult result) {
		for (int j = 0; j < types.length; j++) {
			//members
			TypeDeclaration[] memberTypeDeclarations = types[j].memberTypes;
			if(memberTypeDeclarations != null && memberTypeDeclarations.length > 0) {
				computeDietRange0(types[j].memberTypes, result);
			}
			//methods
			AbstractMethodDeclaration[] methods = types[j].methods;
			if (methods != null) {
				int length = methods.length;
				for (int i = 0; i < length; i++) {
					AbstractMethodDeclaration method = methods[i];
					if(containsIgnoredBody(method)) {
						if(containsErrorInSignature(method)) {
							method.bits |= ASTNode.ErrorInSignature;
							result.addInterval(method.declarationSourceStart, method.declarationSourceEnd, IGNORE);
						} else {
							int flags = method.sourceEnd + 1 == method.bodyStart ? LBRACE_MISSING : NO_FLAG;
							result.addInterval(method.bodyStart, method.bodyEnd, flags);
						}
					}
				}
			}

			//initializers
			FieldDeclaration[] fields = types[j].fields;
			if (fields != null) {
				int length = fields.length;
				for (int i = 0; i < length; i++) {
					if (fields[i] instanceof Initializer) {
						Initializer initializer = (Initializer)fields[i];
						if(initializer.declarationSourceEnd == initializer.bodyEnd && initializer.declarationSourceStart != initializer.declarationSourceEnd){
							initializer.bits |= ASTNode.ErrorInSignature;
							result.addInterval(initializer.declarationSourceStart, initializer.declarationSourceEnd, IGNORE);
						} else {
							result.addInterval(initializer.bodyStart, initializer.bodyEnd);
						}
					}
				}
			}
		}
	}

	public static boolean containsIgnoredBody(AbstractMethodDeclaration method){
		return !method.isDefaultConstructor()
			&& !method.isClinit()
			&& (method.modifiers & ExtraCompilerModifiers.AccSemicolonBody) == 0;
	}
}
