/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contributions for
 *     						bug 325755 - [compiler] wrong initialization state after conditional expression
 *     						bug 320170 - [compiler] [null] Whitebox issues in null analysis
 *     						bug 292478 - Report potentially null across variable assignment
 *     						bug 332637 - Dead Code detection removing code that isn't dead
 *     						bug 341499 - [compiler][null] allocate extra bits in all methods of UnconditionalFlowInfo
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TagBits;

/**
 * Record initialization status during definite assignment analysis
 *
 * No caching of pre-allocated instances.
 */
public class UnconditionalFlowInfo extends FlowInfo {
	/**
	 * Exception raised when unexpected behavior is detected.
	 */
	public static class AssertionFailedException extends RuntimeException {
		private static final long serialVersionUID = 1827352841030089703L;

	public AssertionFailedException(String message) {
		super(message);
	}
	}

	// Coverage tests need that the code be instrumented. The following flag
	// controls whether the instrumented code is compiled in or not, and whether
	// the coverage tests methods run or not.
	public final static boolean COVERAGE_TEST_FLAG = false;
	// never release with the coverageTestFlag set to true
	public static int CoverageTestId;

	// assignment bits - first segment
	public long definiteInits;
	public long potentialInits;

	// null bits - first segment
	public long
		nullBit1,
		nullBit2,
		nullBit3,
		nullBit4;
/*
		nullBit1
		 nullBit2...
		0000	start
		0001	pot. unknown
		0010	pot. non null
		0011	pot. nn & pot. un
		0100	pot. null
		0101	pot. n & pot. un
		0110	pot. n & pot. nn
		0111    pot. n & pot. nn & pot. un
		1001	def. unknown
		1010	def. non null
		1011	pot. nn & prot. nn
		1100	def. null
		1101	pot. n & prot. n
		1110	prot. null
		1111	prot. non null
 */

	// extra segments
	public static final int extraLength = 6;
	public long extra[][];
		// extra bit fields for larger numbers of fields/variables
		// extra[0] holds definiteInits values, extra[1] potentialInits, etc.
		// lifecycle is extra == null or else all extra[]'s are allocated
		// arrays which have the same size

	public int maxFieldCount; // limit between fields and locals

	// Constants
	public static final int BitCacheSize = 64; // 64 bits in a long.
	public int[] nullStatusChangedInAssert; // https://bugs.eclipse.org/bugs/show_bug.cgi?id=303448

public FlowInfo addInitializationsFrom(FlowInfo inits) {
	return addInfoFrom(inits, true);
}
public FlowInfo addNullInfoFrom(FlowInfo inits) {
	return addInfoFrom(inits, false);
}
private FlowInfo addInfoFrom(FlowInfo inits, boolean handleInits) {
	if (this == DEAD_END)
		return this;
	if (inits == DEAD_END)
		return this;
	UnconditionalFlowInfo otherInits = inits.unconditionalInits();

	if (handleInits) {
		// union of definitely assigned variables,
		this.definiteInits |= otherInits.definiteInits;
		// union of potentially set ones
		this.potentialInits |= otherInits.potentialInits;
	}
	
	// combine null information
	boolean thisHadNulls = (this.tagBits & NULL_FLAG_MASK) != 0,
		otherHasNulls = (otherInits.tagBits & NULL_FLAG_MASK) != 0;
	long
		a1, a2, a3, a4,
		na1, na2, na3, na4,
		b1, b2, b3, b4,
		nb1, nb2, nb3, nb4;
	if (otherHasNulls) {
		if (!thisHadNulls) {
			this.nullBit1 = otherInits.nullBit1;
			this.nullBit2 = otherInits.nullBit2;
			this.nullBit3 = otherInits.nullBit3;
			this.nullBit4 = otherInits.nullBit4;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 1) {
				  this.nullBit4 = ~0;
				}
			}
		}
		else {
			this.nullBit1 = (b1 = otherInits.nullBit1)
                				| (a1 = this.nullBit1) & ((a3 = this.nullBit3)
                					& (a4 = this.nullBit4) & (nb2 = ~(b2 = otherInits.nullBit2))
                					& (nb4 = ~(b4 = otherInits.nullBit4))
                        		| ((na4 = ~a4) | (na3 = ~a3))
                        			& ((na2 = ~(a2 = this.nullBit2)) & nb2
                        				| a2 & (nb3 = ~(b3 = otherInits.nullBit3)) & nb4));
			this.nullBit2  = b2 & (nb4 | nb3)
                    			| na3 & na4 & b2
                    			| a2 & (nb3 & nb4
                                			| (nb1 = ~b1) & (na3 | (na1 = ~a1))
                                			| a1 & b2);
			this.nullBit3 = b3 & (nb1 & (b2 | a2 | na1)
                        			| b1 & (b4 | nb2 | a1 & a3)
                         			| na1 & na2 & na4)
                    			| a3 & nb2 & nb4
                    			| nb1 & ((na2 & a4 | na1) & a3
                                			| a1 & na2 & na4 & b2);
			this.nullBit4 = nb1 & (a4 & (na3 & nb3	| (a3 | na2) & nb2)
                      			| a1 & (a3 & nb2 & b4
                              			| a2 & b2 & (b4	| a3 & na4 & nb3)))
                      			| b1 & (a3 & a4 & b4
                          			| na2 & na4 & nb3 & b4
                          			| a2 & ((b3 | a4) & b4
                                  				| na3 & a4 & b2 & b3)
                          			| na1 & (b4	| (a4 | a2) & b2 & b3))
                      			| (na1 & (na3 & nb3 | na2 & nb2)
                      				| a1 & (nb2 & nb3 | a2 & a3)) & b4;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 2) {
				  this.nullBit4 = ~0;
				}
			}
		}
		this.tagBits |= NULL_FLAG_MASK; // in all cases - avoid forgetting extras
	}
	// treating extra storage
	if (this.extra != null || otherInits.extra != null) {
		int mergeLimit = 0, copyLimit = 0;
		if (this.extra != null) {
			if (otherInits.extra != null) {
				// both sides have extra storage
				int length, otherLength;
				if ((length = this.extra[0].length) <
						(otherLength = otherInits.extra[0].length)) {
					// current storage is shorter -> grow current
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[otherLength]), 0, length);
					}
					mergeLimit = length;
					copyLimit = otherLength;
					if (COVERAGE_TEST_FLAG) {
						if (CoverageTestId == 3) {
							throw new AssertionFailedException("COVERAGE 3"); //$NON-NLS-1$
						}
					}
				} else {
					// current storage is longer
					mergeLimit = otherLength;
					if (COVERAGE_TEST_FLAG) {
						if (CoverageTestId == 4) {
							throw new AssertionFailedException("COVERAGE 4"); //$NON-NLS-1$
						}
					}
				}
			}
		} else if (otherInits.extra != null) {
			// no storage here, but other has extra storage.
			// shortcut regular copy because array copy is better
			int otherLength;
			this.extra = new long[extraLength][];
			System.arraycopy(otherInits.extra[0], 0,
				(this.extra[0] = new long[otherLength =
					otherInits.extra[0].length]), 0, otherLength);
			System.arraycopy(otherInits.extra[1], 0,
				(this.extra[1] = new long[otherLength]), 0, otherLength);
			if (otherHasNulls) {
				for (int j = 2; j < extraLength; j++) {
					System.arraycopy(otherInits.extra[j], 0,
						(this.extra[j] = new long[otherLength]), 0, otherLength);
				}
				if (COVERAGE_TEST_FLAG) {
					if (CoverageTestId == 5) {
						this.extra[5][otherLength - 1] = ~0;
					}
				}
			}
			else {
				for (int j = 2; j < extraLength; j++) {
					this.extra[j] = new long[otherLength];
				}
				if (COVERAGE_TEST_FLAG) {
					if (CoverageTestId == 6) {
						throw new AssertionFailedException("COVERAGE 6"); //$NON-NLS-1$
					}
				}
			}
		}
		int i;
		if (handleInits) {
			// manage definite assignment info
			for (i = 0; i < mergeLimit; i++) {
				this.extra[0][i] |= otherInits.extra[0][i];
				this.extra[1][i] |= otherInits.extra[1][i];
			}
			for (; i < copyLimit; i++) {
				this.extra[0][i] = otherInits.extra[0][i];
				this.extra[1][i] = otherInits.extra[1][i];
			
			}
		}
		// tweak limits for nulls
		if (!thisHadNulls) {
		    if (copyLimit < mergeLimit) {
		      	copyLimit = mergeLimit;
		    }
		  	mergeLimit = 0;
		}
		if (!otherHasNulls) {
		  	copyLimit = 0;
		  	mergeLimit = 0;
		}
		for (i = 0; i < mergeLimit; i++) {
			this.extra[1 + 1][i] = (b1 = otherInits.extra[1 + 1][i])
                				| (a1 = this.extra[1 + 1][i]) & ((a3 = this.extra[3 + 1][i])
                					& (a4 = this.extra[4 + 1][i]) & (nb2 = ~(b2 = otherInits.extra[2 + 1][i]))
                					& (nb4 = ~(b4 = otherInits.extra[4 + 1][i]))
                        		| ((na4 = ~a4) | (na3 = ~a3))
                        			& ((na2 = ~(a2 = this.extra[2 + 1][i])) & nb2
                        				| a2 & (nb3 = ~(b3 = otherInits.extra[3 + 1][i])) & nb4));
			this.extra[2 + 1][i]  = b2 & (nb4 | nb3)
                    			| na3 & na4 & b2
                    			| a2 & (nb3 & nb4
                                			| (nb1 = ~b1) & (na3 | (na1 = ~a1))
                                			| a1 & b2);
			this.extra[3 + 1][i] = b3 & (nb1 & (b2 | a2 | na1)
                        			| b1 & (b4 | nb2 | a1 & a3)
                         			| na1 & na2 & na4)
                    			| a3 & nb2 & nb4
                    			| nb1 & ((na2 & a4 | na1) & a3
                                			| a1 & na2 & na4 & b2);
			this.extra[4 + 1][i] = nb1 & (a4 & (na3 & nb3	| (a3 | na2) & nb2)
                      			| a1 & (a3 & nb2 & b4
                              			| a2 & b2 & (b4	| a3 & na4 & nb3)))
                      			| b1 & (a3 & a4 & b4
                          			| na2 & na4 & nb3 & b4
                          			| a2 & ((b3 | a4) & b4
                                  				| na3 & a4 & b2 & b3)
                          			| na1 & (b4	| (a4 | a2) & b2 & b3))
                      			| (na1 & (na3 & nb3 | na2 & nb2)
                      				| a1 & (nb2 & nb3 | a2 & a3)) & b4;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 7) {
				  this.extra[5][i] = ~0;
				}
			}
		}
		for (; i < copyLimit; i++) {
			for (int j = 2; j < extraLength; j++) {
				this.extra[j][i] = otherInits.extra[j][i];
			}
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 8) {
				  this.extra[5][i] = ~0;
				}
			}
		}
	}
	combineNullStatusChangeInAssertInfo(otherInits);
	return this;
}

public FlowInfo addPotentialInitializationsFrom(FlowInfo inits) {
	if (this == DEAD_END){
		return this;
	}
	if (inits == DEAD_END){
		return this;
	}
	UnconditionalFlowInfo otherInits = inits.unconditionalInits();
	// union of potentially set ones
	this.potentialInits |= otherInits.potentialInits;
	// treating extra storage
	if (this.extra != null) {
		if (otherInits.extra != null) {
			// both sides have extra storage
			int i = 0, length, otherLength;
			if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length)) {
				// current storage is shorter -> grow current
				for (int j = 0; j < extraLength; j++) {
					System.arraycopy(this.extra[j], 0,
						(this.extra[j] = new long[otherLength]), 0, length);
				}
				for (; i < length; i++) {
					this.extra[1][i] |= otherInits.extra[1][i];
				}
				for (; i < otherLength; i++) {
					this.extra[1][i] = otherInits.extra[1][i];
				}
			}
			else {
				// current storage is longer
				for (; i < otherLength; i++) {
					this.extra[1][i] |= otherInits.extra[1][i];
				}
			}
		}
	}
	else if (otherInits.extra != null) {
		// no storage here, but other has extra storage.
		int otherLength = otherInits.extra[0].length;
		this.extra = new long[extraLength][];
		for (int j = 0; j < extraLength; j++) {
			this.extra[j] = new long[otherLength];
		}
		System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0,
			otherLength);
	}
	addPotentialNullInfoFrom(otherInits);
	return this;
}

/**
 * Compose other inits over this flow info, then return this. The operation
 * semantics are to wave into this flow info the consequences upon null
 * information of a possible path into the operations that resulted into
 * otherInits. The fact that this path may be left unexecuted under peculiar
 * conditions results into less specific results than
 * {@link #addInitializationsFrom(FlowInfo) addInitializationsFrom}; moreover,
 * only the null information is affected.
 * @param otherInits other null inits to compose over this
 * @return this, modified according to otherInits information
 */
public UnconditionalFlowInfo addPotentialNullInfoFrom(
		UnconditionalFlowInfo otherInits) {
	if ((this.tagBits & UNREACHABLE) != 0 ||
			(otherInits.tagBits & UNREACHABLE) != 0 ||
			(otherInits.tagBits & NULL_FLAG_MASK) == 0) {
		return this;
	}
	// if we get here, otherInits has some null info
	boolean thisHadNulls = (this.tagBits & NULL_FLAG_MASK) != 0,
		thisHasNulls = false;
	long a1, a2, a3, a4,
		na1, na2, na3, na4,
		b1, b2, b3, b4,
		nb1, nb2, nb3, nb4;
	if (thisHadNulls) {
		this.nullBit1  = (a1 = this.nullBit1)
								& ((a3 = this.nullBit3) & (a4 = this.nullBit4)
									& ((nb2 = ~(b2 = otherInits.nullBit2))
										& (nb4 = ~(b4 = otherInits.nullBit4))
											| (b1 = otherInits.nullBit1) & (b3 = otherInits.nullBit3))
                			| (na2 = ~(a2 = this.nullBit2))
                				& (b1 & b3 | ((na4 = ~a4) | (na3 = ~a3)) & nb2)
                			| a2 & ((na4 | na3) & ((nb3 = ~b3) & nb4 | b1 & b2)));
		this.nullBit2 = b2 & (nb3 | (nb1 = ~b1))
    			| a2 & (nb3 & nb4 | b2 | na3 | (na1 = ~a1));
		this.nullBit3 = b3 & (nb1 & b2
            		| a2 & (nb2	| a3)
            		| na1 & nb2
            		| a1 & na2 & na4 & b1)
    			| a3 & (nb2 & nb4 | na2 & a4 | na1)
    			| a1 & na2 & na4 & b2;
		this.nullBit4 = na3 & (nb1 & nb3 & b4
    				| a4 & (nb3 | b1 & b2))
    			| nb2 & (na3 & b1 & nb3	| na2 & (nb1 & b4 | b1 & nb3 | a4))
    			| a3 & (a4 & (nb2 | b1 & b3)
            			| a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3));
		if (COVERAGE_TEST_FLAG) {
			if (CoverageTestId == 9) {
			  this.nullBit4 = ~0;
			}
		}
		if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0) { //  bit1 is redundant
		  	thisHasNulls = true;
		}
	} else {
  		this.nullBit1 = 0;
  		this.nullBit2 = (b2 = otherInits.nullBit2)
  							& ((nb3 = ~(b3 = otherInits.nullBit3)) |
  								(nb1 = ~(b1 = otherInits.nullBit1)));
  		this.nullBit3 = b3 & (nb1 | (nb2 = ~b2));
  		this.nullBit4 = ~b1 & ~b3 & (b4 = otherInits.nullBit4) | ~b2 & (b1 & ~b3 | ~b1 & b4);
		if (COVERAGE_TEST_FLAG) {
			if (CoverageTestId == 10) {
			  this.nullBit4 = ~0;
			}
		}
		if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0) { //  bit1 is redundant
		  	thisHasNulls = true;
		}
	}
	// extra storage management
	if (otherInits.extra != null) {
		int mergeLimit = 0, copyLimit = otherInits.extra[0].length;
		if (this.extra == null) {
			this.extra = new long[extraLength][];
			for (int j = 0; j < extraLength; j++) {
				this.extra[j] = new long[copyLimit];
			}
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 11) {
					throw new AssertionFailedException("COVERAGE 11"); //$NON-NLS-1$
				}
			}
		} else {
			mergeLimit = copyLimit;
			if (mergeLimit > this.extra[0].length) {
				mergeLimit = this.extra[0].length;
				for (int j = 0; j < extraLength; j++) {
					System.arraycopy(this.extra[j], 0,
							this.extra[j] = new long[copyLimit], 0,
							mergeLimit);
				}
				if (! thisHadNulls) {
    				mergeLimit = 0;
    				// will do with a copy -- caveat: only valid because definite assignment bits copied above
        			if (COVERAGE_TEST_FLAG) {
        				if (CoverageTestId == 12) {
							throw new AssertionFailedException("COVERAGE 12"); //$NON-NLS-1$
        				}
        			}
				}
			}
		}
		// PREMATURE skip operations for fields
		int i;
		for (i = 0 ; i < mergeLimit ; i++) {
    		this.extra[1 + 1][i]  = (a1 = this.extra[1 + 1][i])
    								& ((a3 = this.extra[3 + 1][i]) & (a4 = this.extra[4 + 1][i])
    									& ((nb2 = ~(b2 = otherInits.extra[2 + 1][i]))
    										& (nb4 = ~(b4 = otherInits.extra[4 + 1][i]))
    											| (b1 = otherInits.extra[1 + 1][i]) & (b3 = otherInits.extra[3 + 1][i]))
                    			| (na2 = ~(a2 = this.extra[2 + 1][i]))
                    				& (b1 & b3 | ((na4 = ~a4) | (na3 = ~a3)) & nb2)
                    			| a2 & ((na4 | na3) & ((nb3 = ~b3) & nb4 | b1 & b2)));
    		this.extra[2 + 1][i] = b2 & (nb3 | (nb1 = ~b1))
        			| a2 & (nb3 & nb4 | b2 | na3 | (na1 = ~a1));
    		this.extra[3 + 1][i] = b3 & (nb1 & b2
                		| a2 & (nb2	| a3)
                		| na1 & nb2
                		| a1 & na2 & na4 & b1)
        			| a3 & (nb2 & nb4 | na2 & a4 | na1)
        			| a1 & na2 & na4 & b2;
    		this.extra[4 + 1][i] = na3 & (nb1 & nb3 & b4
        				| a4 & (nb3 | b1 & b2))
        			| nb2 & (na3 & b1 & nb3	| na2 & (nb1 & b4 | b1 & nb3 | a4))
        			| a3 & (a4 & (nb2 | b1 & b3)
                			| a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3));
    		if ((this.extra[2 + 1][i] | this.extra[3 + 1][i] | this.extra[4 + 1][i]) != 0) { //  bit1 is redundant
    		  	thisHasNulls = true;
    		}
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 13) {
				  this.nullBit4 = ~0;
				}
			}
		}
		for (; i < copyLimit; i++) {
    		this.extra[1 + 1][i] = 0;
    		this.extra[2 + 1][i] = (b2 = otherInits.extra[2 + 1][i])
    							& ((nb3 = ~(b3 = otherInits.extra[3 + 1][i])) |
    								(nb1 = ~(b1 = otherInits.extra[1 + 1][i])));
    		this.extra[3 + 1][i] = b3 & (nb1 | (nb2 = ~b2));
    		this.extra[4 + 1][i] = ~b1 & ~b3 & (b4 = otherInits.extra[4 + 1][i]) | ~b2 & (b1 & ~b3 | ~b1 & b4);
    		if ((this.extra[2 + 1][i] | this.extra[3 + 1][i] | this.extra[4 + 1][i]) != 0) { //  bit1 is redundant
    		  	thisHasNulls = true;
    		}
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 14) {
				  this.extra[5][i] = ~0;
				}
			}
		}
	}
	combineNullStatusChangeInAssertInfo(otherInits);
	if (thisHasNulls) {
		this.tagBits |= NULL_FLAG_MASK;
	}
	else {
		this.tagBits &= NULL_FLAG_MASK;
	}
	return this;
}

final public boolean cannotBeDefinitelyNullOrNonNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return (
			(~this.nullBit1
					& (this.nullBit2 & this.nullBit3 | this.nullBit4)
				| ~this.nullBit2 & ~this.nullBit3 & this.nullBit4)
			& (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	long a2, a3, a4;
	return (
			(~this.extra[2][vectorIndex]
					& ((a2 = this.extra[3][vectorIndex]) & (a3 = this.extra[4][vectorIndex]) | (a4 = this.extra[5][vectorIndex]))
				| ~a2 & ~a3 & a4)
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean cannotBeNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return (this.nullBit1 & this.nullBit3
			& ((this.nullBit2 & this.nullBit4) | ~this.nullBit2)
			& (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex]
	        & ((this.extra[3][vectorIndex] & this.extra[5][vectorIndex]) |
	        		~this.extra[3][vectorIndex])
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean canOnlyBeNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return (this.nullBit1 & this.nullBit2
			& (~this.nullBit3 | ~this.nullBit4)
			& (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex]
	        & (~this.extra[4][vectorIndex] | ~this.extra[5][vectorIndex])
		    & (1L << (position % BitCacheSize))) != 0;
}

public FlowInfo copy() {
	// do not clone the DeadEnd
	if (this == DEAD_END) {
		return this;
	}
	UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
	// copy slots
	copy.definiteInits = this.definiteInits;
	copy.potentialInits = this.potentialInits;
	boolean hasNullInfo = (this.tagBits & NULL_FLAG_MASK) != 0;
	if (hasNullInfo) {
		copy.nullBit1 = this.nullBit1;
		copy.nullBit2 = this.nullBit2;
		copy.nullBit3 = this.nullBit3;
		copy.nullBit4 = this.nullBit4;
	}
	copy.tagBits = this.tagBits;
	copy.maxFieldCount = this.maxFieldCount;
	if (this.extra != null) {
		int length;
		copy.extra = new long[extraLength][];
		System.arraycopy(this.extra[0], 0,
			(copy.extra[0] = new long[length = this.extra[0].length]), 0,
			length);
		System.arraycopy(this.extra[1], 0,
			(copy.extra[1] = new long[length]), 0, length);
		if (hasNullInfo) {
			for (int j = 2; j < extraLength; j++) {
				System.arraycopy(this.extra[j], 0,
					(copy.extra[j] = new long[length]), 0, length);
			}
		}
		else {
			for (int j = 2; j < extraLength; j++) {
				copy.extra[j] = new long[length];
			}
		}
	}
	copy.nullStatusChangedInAssert = this.nullStatusChangedInAssert;
	return copy;
}

/**
 * Discard definite inits and potential inits from this, then return this.
 * The returned flow info only holds null related information.
 * @return this flow info, minus definite inits and potential inits
 */
public UnconditionalFlowInfo discardInitializationInfo() {
	if (this == DEAD_END) {
		return this;
	}
	this.definiteInits =
		this.potentialInits = 0;
	if (this.extra != null) {
		for (int i = 0, length = this.extra[0].length; i < length; i++) {
			this.extra[0][i] = this.extra[1][i] = 0;
		}
	}
	return this;
}

/**
 * Remove local variables information from this flow info and return this.
 * @return this, deprived from any local variable information
 */
public UnconditionalFlowInfo discardNonFieldInitializations() {
	int limit = this.maxFieldCount;
	if (limit < BitCacheSize) {
		long mask = (1L << limit)-1;
		this.definiteInits &= mask;
		this.potentialInits &= mask;
		this.nullBit1 &= mask;
		this.nullBit2 &= mask;
		this.nullBit3 &= mask;
		this.nullBit4 &= mask;
	}
	// use extra vector
	if (this.extra == null) {
		return this; // if vector not yet allocated, then not initialized
	}
	int vectorIndex, length = this.extra[0].length;
	if ((vectorIndex = (limit / BitCacheSize) - 1) >= length) {
		return this; // not enough room yet
	}
	if (vectorIndex >= 0) {
		// else we only have complete non field array items left
		long mask = (1L << (limit % BitCacheSize))-1;
		for (int j = 0; j < extraLength; j++) {
			this.extra[j][vectorIndex] &= mask;
		}
	}
	for (int i = vectorIndex + 1; i < length; i++) {
		for (int j = 0; j < extraLength; j++) {
			this.extra[j][i] = 0;
		}
	}
	return this;
}

public FlowInfo initsWhenFalse() {
	return this;
}

public FlowInfo initsWhenTrue() {
	return this;
}

/**
 * Check status of definite assignment at a given position.
 * It deals with the dual representation of the InitializationInfo2:
 * bits for the first 64 entries, then an array of booleans.
 */
final private boolean isDefinitelyAssigned(int position) {
	if (position < BitCacheSize) {
		// use bits
		return (this.definiteInits & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null)
		return false; // if vector not yet allocated, then not initialized
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1)
			>= this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[0][vectorIndex]) &
				(1L << (position % BitCacheSize))) != 0;
}

final public boolean isDefinitelyAssigned(FieldBinding field) {
	// Mirrored in CodeStream.isDefinitelyAssigned(..)
	// do not want to complain in unreachable code
	if ((this.tagBits & UNREACHABLE_OR_DEAD) != 0) {
		return true;
	}
	return isDefinitelyAssigned(field.id);
}

final public boolean isDefinitelyAssigned(LocalVariableBinding local) {
	// do not want to complain in unreachable code if local declared in reachable code
	if ((this.tagBits & UNREACHABLE_OR_DEAD) != 0 && (local.declaration.bits & ASTNode.IsLocalDeclarationReachable) != 0) {
		return true;
	}
	return isDefinitelyAssigned(local.id + this.maxFieldCount);
}

final public boolean isDefinitelyNonNull(LocalVariableBinding local) {
	// do not want to complain in unreachable code
	if ((this.tagBits & UNREACHABLE) != 0 ||
			(this.tagBits & NULL_FLAG_MASK) == 0) {
		return false;
	}
	if ((local.type.tagBits & TagBits.IsBaseType) != 0 ||
			local.constant() != Constant.NotAConstant) { // String instances
		return true;
	}
	int position = local.id + this.maxFieldCount;
	if (position < BitCacheSize) { // use bits
		return ((this.nullBit1 & this.nullBit3 & (~this.nullBit2 | this.nullBit4))
			    & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1)
			>= this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[2][vectorIndex] & this.extra[4][vectorIndex]
		        & (~this.extra[3][vectorIndex] | this.extra[5][vectorIndex]))
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean isDefinitelyNull(LocalVariableBinding local) {
	// do not want to complain in unreachable code
	if ((this.tagBits & UNREACHABLE) != 0 ||
			(this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position = local.id + this.maxFieldCount;
	if (position < BitCacheSize) { // use bits
		return ((this.nullBit1 & this.nullBit2
			        & (~this.nullBit3 | ~this.nullBit4))
			    & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[2][vectorIndex] & this.extra[3][vectorIndex]
		        & (~this.extra[4][vectorIndex] | ~this.extra[5][vectorIndex]))
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean isDefinitelyUnknown(LocalVariableBinding local) {
	// do not want to complain in unreachable code
	if ((this.tagBits & UNREACHABLE) != 0 ||
			(this.tagBits & NULL_FLAG_MASK) == 0) {
		return false;
	}
	int position = local.id + this.maxFieldCount;
	if (position < BitCacheSize) { // use bits
		return ((this.nullBit1 & this.nullBit4
				& ~this.nullBit2 & ~this.nullBit3) & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[2][vectorIndex] & this.extra[5][vectorIndex]
	    & ~this.extra[3][vectorIndex] & ~this.extra[4][vectorIndex])
		    & (1L << (position % BitCacheSize))) != 0;
}

/**
 * Check status of potential assignment at a given position.
 */
final private boolean isPotentiallyAssigned(int position) {
	// id is zero-based
	if (position < BitCacheSize) {
		// use bits
		return (this.potentialInits & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1)
			>= this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[1][vectorIndex]) &
			(1L << (position % BitCacheSize))) != 0;
}

final public boolean isPotentiallyAssigned(FieldBinding field) {
	return isPotentiallyAssigned(field.id);
}

final public boolean isPotentiallyAssigned(LocalVariableBinding local) {
	// final constants are inlined, and thus considered as always initialized
	if (local.constant() != Constant.NotAConstant) {
		return true;
	}
	return isPotentiallyAssigned(local.id + this.maxFieldCount);
}

// TODO (Ayush) Check why this method does not return true for protected non null (1111)
final public boolean isPotentiallyNonNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return ((this.nullBit3 & (~this.nullBit1 | ~this.nullBit2))
			    & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[4][vectorIndex]
		        & (~this.extra[2][vectorIndex] | ~this.extra[3][vectorIndex]))
		    & (1L << (position % BitCacheSize))) != 0;
}

// TODO (Ayush) Check why this method does not return true for protected null
final public boolean isPotentiallyNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return ((this.nullBit2 & (~this.nullBit1 | ~this.nullBit3))
			    & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return ((this.extra[3][vectorIndex]
		        & (~this.extra[2][vectorIndex] | ~this.extra[4][vectorIndex]))
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean isPotentiallyUnknown(LocalVariableBinding local) {
	// do not want to complain in unreachable code
	if ((this.tagBits & UNREACHABLE) != 0 ||
			(this.tagBits & NULL_FLAG_MASK) == 0) {
		return false;
	}
	int position = local.id + this.maxFieldCount;
	if (position < BitCacheSize) { // use bits
		return (this.nullBit4
			& (~this.nullBit1 | ~this.nullBit2 & ~this.nullBit3)
			& (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return (this.extra[5][vectorIndex]
	        & (~this.extra[2][vectorIndex]
	            | ~this.extra[3][vectorIndex] & ~this.extra[4][vectorIndex])
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean isProtectedNonNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return (this.nullBit1 & this.nullBit3 & this.nullBit4 & (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
		this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return (this.extra[2][vectorIndex]
	            & this.extra[4][vectorIndex]
	            & this.extra[5][vectorIndex]
		    & (1L << (position % BitCacheSize))) != 0;
}

final public boolean isProtectedNull(LocalVariableBinding local) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0 ||
			(local.type.tagBits & TagBits.IsBaseType) != 0) {
		return false;
	}
	int position;
	if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
		// use bits
		return (this.nullBit1 & this.nullBit2
			& (this.nullBit3 ^ this.nullBit4)
			& (1L << position)) != 0;
	}
	// use extra vector
	if (this.extra == null) {
		return false; // if vector not yet allocated, then not initialized
	}
	int vectorIndex;
	if ((vectorIndex = (position / BitCacheSize) - 1) >=
			this.extra[0].length) {
		return false; // if not enough room in vector, then not initialized
	}
	return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex]
	        & (this.extra[4][vectorIndex] ^ this.extra[5][vectorIndex])
		    & (1L << (position % BitCacheSize))) != 0;
}
/** Asserts that the given boolean is <code>true</code>. If this
 * is not the case, some kind of unchecked exception is thrown.
 * The given message is included in that exception, to aid debugging.
 *
 * @param expression the outcome of the check
 * @param message the message to include in the exception
 * @return <code>true</code> if the check passes (does not return
 *    if the check fails)
 */
protected static boolean isTrue(boolean expression, String message) {
	if (!expression)
		throw new AssertionFailedException("assertion failed: " + message); //$NON-NLS-1$
	return expression;
}
public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
		int position;
		long mask;
		long a1, a2, a3, a4, na2;
		// position is zero-based
		if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
			// use bits
			if (((mask = 1L << position)
				& (a1 = this.nullBit1)
				& (na2 = ~(a2 = this.nullBit2))
				& ~(a3 = this.nullBit3)
				& (a4 = this.nullBit4))
					!= 0) {
			  	this.nullBit4 &= ~mask;
			} else if ((mask & a1 & na2 & a3) == 0) {
			  	this.nullBit4 |= mask;
			  	if ((mask & a1) == 0) {
			  	  	if ((mask & a2 & (a3 ^ a4)) != 0) {
			  	  	  	this.nullBit2 &= ~mask;
			  	  	}
			  	  	else if ((mask & (a2 | a3 | a4)) == 0) {
			  	  	  	this.nullBit2 |= mask;
			  	  	}
			  	}
			}
			this.nullBit1 |= mask;
			this.nullBit3 |= mask;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 15) {
				  	this.nullBit4 = ~0;
				}
			}
		}
		else {
			// use extra vector
			int vectorIndex = (position / BitCacheSize) - 1;
			if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
				if (COVERAGE_TEST_FLAG) {
					if (CoverageTestId == 16) {
						throw new AssertionFailedException("COVERAGE 16"); //$NON-NLS-1$
					}
				}
			}
			else {
				int oldLength;
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					int newLength = vectorIndex + 1;
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[newLength]), 0,
							oldLength);
					}
					if (COVERAGE_TEST_FLAG) {
						if (CoverageTestId == 17) {
							throw new AssertionFailedException("COVERAGE 17"); //$NON-NLS-1$
						}
					}
				}
			}
			// MACRO :'b,'es/nullBit\(.\)/extra[\1 + 1][vectorIndex]/gc
			if (((mask = 1L << (position % BitCacheSize))
  				& (a1 = this.extra[1 + 1][vectorIndex])
  				& (na2 = ~(a2 = this.extra[2 + 1][vectorIndex]))
  				& ~(a3 = this.extra[3 + 1][vectorIndex])
  				& (a4 = this.extra[4 + 1][vectorIndex]))
  					!= 0) {
  			  	this.extra[4 + 1][vectorIndex] &= ~mask;
  			} else if ((mask & a1 & na2 & a3) == 0) {
  			  	this.extra[4 + 1][vectorIndex] |= mask;
  			  	if ((mask & a1) == 0) {
  			  	  	if ((mask & a2 & (a3 ^ a4)) != 0) {
  			  	  	  	this.extra[2 + 1][vectorIndex] &= ~mask;
  			  	  	}
  			  	  	else if ((mask & (a2 | a3 | a4)) == 0) {
  			  	  	  	this.extra[2 + 1][vectorIndex] |= mask;
  			  	  	}
  			  	}
  			}
  			this.extra[1 + 1][vectorIndex] |= mask;
  			this.extra[3 + 1][vectorIndex] |= mask;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 18) {
				  	this.extra[5][vectorIndex] = ~0;
				}
			}
		}
	}
}

public void markAsComparedEqualToNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
		int position;
		long mask;
		// position is zero-based
		if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
			// use bits
			if (((mask = 1L << position) & this.nullBit1) != 0) {
  			  	if ((mask
  			  		& (~this.nullBit2 | this.nullBit3
  			  			| ~this.nullBit4)) != 0) {
  			  	  	this.nullBit4 &= ~mask;
  			  	}
			} else if ((mask & this.nullBit4) != 0) {
			  	  this.nullBit3 &= ~mask;
			} else {
    			if ((mask & this.nullBit2) != 0) {
    			  	this.nullBit3 &= ~mask;
      			  	this.nullBit4 |= mask;
    			} else {
    			  	this.nullBit3 |= mask;
    			}
			}
			this.nullBit1 |= mask;
			this.nullBit2 |= mask;
			if (COVERAGE_TEST_FLAG) {
				if (CoverageTestId == 19) {
				  	this.nullBit4 = ~0;
				}
			}
		}
		else {
			// use extra vector
			int vectorIndex = (position / BitCacheSize) - 1;
			mask = 1L << (position % BitCacheSize);
			if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length ];
				}
				if (COVERAGE_TEST_FLAG) {
					if(CoverageTestId == 20) {
						throw new AssertionFailedException("COVERAGE 20"); //$NON-NLS-1$
					}
				}
			}
			else {
				int oldLength;
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					int newLength = vectorIndex + 1;
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[newLength]), 0,
							oldLength);
					}
					if (COVERAGE_TEST_FLAG) {
						if(CoverageTestId == 21) {
							throw new AssertionFailedException("COVERAGE 21"); //$NON-NLS-1$
						}
					}
				}
			}
			if ((mask & this.extra[1 + 1][vectorIndex]) != 0) {
  			  	if ((mask
  			  		& (~this.extra[2 + 1][vectorIndex] | this.extra[3 + 1][vectorIndex]
  			  			| ~this.extra[4 + 1][vectorIndex])) != 0) {
  			  	  	this.extra[4 + 1][vectorIndex] &= ~mask;
  			  	}
			} else if ((mask & this.extra[4 + 1][vectorIndex]) != 0) {
			  	  this.extra[3 + 1][vectorIndex] &= ~mask;
			} else {
    			if ((mask & this.extra[2 + 1][vectorIndex]) != 0) {
    			  	this.extra[3 + 1][vectorIndex] &= ~mask;
      			  	this.extra[4 + 1][vectorIndex] |= mask;
    			} else {
    			  	this.extra[3 + 1][vectorIndex] |= mask;
    			}
			}
			this.extra[1 + 1][vectorIndex] |= mask;
			this.extra[2 + 1][vectorIndex] |= mask;
		}
	}
}

/**
 * Record a definite assignment at a given position.
 */
final private void markAsDefinitelyAssigned(int position) {

	if (this != DEAD_END) {
		// position is zero-based
		if (position < BitCacheSize) {
			// use bits
			long mask;
			this.definiteInits |= (mask = 1L << position);
			this.potentialInits |= mask;
		}
		else {
			// use extra vector
			int vectorIndex = (position / BitCacheSize) - 1;
			if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
			long mask;
			this.extra[0][vectorIndex] |=
				(mask = 1L << (position % BitCacheSize));
			this.extra[1][vectorIndex] |= mask;
		}
	}
}

public void markAsDefinitelyAssigned(FieldBinding field) {
	if (this != DEAD_END)
		markAsDefinitelyAssigned(field.id);
}

public void markAsDefinitelyAssigned(LocalVariableBinding local) {
	if (this != DEAD_END)
		markAsDefinitelyAssigned(local.id + this.maxFieldCount);
}

public void markAsDefinitelyNonNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	long mask;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// set assigned non null
    		this.nullBit1 |= (mask = 1L << position);
    		this.nullBit3 |= mask;
    		// clear others
    		this.nullBit2 &= (mask = ~mask);
    		this.nullBit4 &= mask;
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 22) {
	    		  	this.nullBit1 = 0;
    			}
    		}
    	}
    	else {
    		// use extra vector
    		int vectorIndex = (position / BitCacheSize) - 1;
    		if (this.extra == null) {
    			int length = vectorIndex + 1;
    			this.extra = new long[extraLength][];
    			for (int j = 0; j < extraLength; j++) {
    				this.extra[j] = new long[length];
    			}
    		}
    		else {
    			int oldLength; // might need to grow the arrays
    			if (vectorIndex >= (oldLength = this.extra[0].length)) {
    				for (int j = 0; j < extraLength; j++) {
    					System.arraycopy(this.extra[j], 0,
    						(this.extra[j] = new long[vectorIndex + 1]), 0,
    						oldLength);
    				}
    			}
    		}
    		this.extra[2][vectorIndex]
    		    |= (mask = 1L << (position % BitCacheSize));
    		this.extra[4][vectorIndex] |= mask;
    		this.extra[3][vectorIndex] &= (mask = ~mask);
    		this.extra[5][vectorIndex] &= mask;
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 23) {
	    			this.extra[2][vectorIndex] = 0;
    			}
    		}
    	}
	}
}

public void markAsDefinitelyNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	long mask;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// mark assigned null
    		this.nullBit1 |= (mask = 1L << position);
    		this.nullBit2 |= mask;
    		// clear others
    		this.nullBit3 &= (mask = ~mask);
    		this.nullBit4 &= mask;
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 24) {
	    		  	this.nullBit4 = ~0;
    			}
    		}
    	}
    	else {
    		// use extra vector
    		int vectorIndex = (position / BitCacheSize) - 1;
    		if (this.extra == null) {
    			int length = vectorIndex + 1;
    			this.extra = new long[extraLength][];
    			for (int j = 0; j < extraLength; j++) {
    				this.extra[j] = new long[length];
    			}
    		}
    		else {
    			int oldLength; // might need to grow the arrays
    			if (vectorIndex >= (oldLength = this.extra[0].length)) {
    				for (int j = 0; j < extraLength; j++) {
    					System.arraycopy(this.extra[j], 0,
    						(this.extra[j] = new long[vectorIndex + 1]), 0,
    						oldLength);
    				}
    			}
    		}
    		this.extra[2][vectorIndex]
    		    |= (mask = 1L << (position % BitCacheSize));
    		this.extra[3][vectorIndex] |= mask;
    		this.extra[4][vectorIndex] &= (mask = ~mask);
    		this.extra[5][vectorIndex] &= mask;
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 25) {
	    			this.extra[5][vectorIndex] = ~0;
    			}
    		}
    	}
	}
}

/**
 * Mark a local as having been assigned to an unknown value.
 * @param local the local to mark
 */
// PREMATURE may try to get closer to markAsDefinitelyAssigned, but not
//			 obvious
public void markAsDefinitelyUnknown(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
		long mask;
		int position;
		// position is zero-based
		if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
			// use bits
			// mark assigned null
			this.nullBit1 |= (mask = 1L << position);
			this.nullBit4 |= mask;
			// clear others
			this.nullBit2 &= (mask = ~mask);
			this.nullBit3 &= mask;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 26) {
				  	this.nullBit4 = 0;
				}
			}
		}
		else {
			// use extra vector
			int vectorIndex = (position / BitCacheSize) - 1;
			if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
			this.extra[2][vectorIndex]
			    |= (mask = 1L << (position % BitCacheSize));
			this.extra[5][vectorIndex] |= mask;
			this.extra[3][vectorIndex] &= (mask = ~mask);
			this.extra[4][vectorIndex] &= mask;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 27) {
					this.extra[5][vectorIndex] = 0;
				}
			}
		}
	}
}

public void resetNullInfo(LocalVariableBinding local) {
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
        int position;
        long mask;
        if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
            // use bits
            this.nullBit1 &= (mask = ~(1L << position));
            this.nullBit2 &= mask;
            this.nullBit3 &= mask;
            this.nullBit4 &= mask;
        } else {
    		// use extra vector
    		int vectorIndex = (position / BitCacheSize) - 1;
    		if (this.extra == null || vectorIndex >= this.extra[2].length) {
    			// in case we attempt to reset the null info of a variable that has not been encountered
    			// before and for which no null bits exist.
    			return;
    		}
    		this.extra[2][vectorIndex]
    		    &= (mask = ~(1L << (position % BitCacheSize)));
    		this.extra[3][vectorIndex] &= mask;
    		this.extra[4][vectorIndex] &= mask;
    		this.extra[5][vectorIndex] &= mask;
    	}
	}
}

/**
 * Mark a local as potentially having been assigned to an unknown value.
 * @param local the local to mark
 */
public void markPotentiallyUnknownBit(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
        int position;
        long mask;
        if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
            // use bits
        	mask = 1L << position;
        	isTrue((this.nullBit1 & mask) == 0, "Adding 'unknown' mark in unexpected state"); //$NON-NLS-1$
            this.nullBit4 |= mask;
            if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 46) {
				  	this.nullBit4 = ~0;
				}
			}
        } else {
    		// use extra vector
    		int vectorIndex = (position / BitCacheSize) - 1;
    		if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		mask = 1L << (position % BitCacheSize);
    		isTrue((this.extra[2][vectorIndex] & mask) == 0, "Adding 'unknown' mark in unexpected state"); //$NON-NLS-1$
    		this.extra[5][vectorIndex] |= mask;
    		if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 47) {
					this.extra[5][vectorIndex] = ~0;
				}
			}
    	}
	}
}

public void markPotentiallyNullBit(LocalVariableBinding local) {
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
        int position;
        long mask;
        if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
            // use bits
        	mask = 1L << position;
        	isTrue((this.nullBit1 & mask) == 0, "Adding 'potentially null' mark in unexpected state"); //$NON-NLS-1$
            this.nullBit2 |= mask;
            if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 40) {
				  	this.nullBit4 = ~0;
				}
			}
        } else {
    		// use extra vector
    		int vectorIndex = (position / BitCacheSize) - 1;
    		if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		mask = 1L << (position % BitCacheSize);
    		this.extra[3][vectorIndex] |= mask;
    		isTrue((this.extra[2][vectorIndex] & mask) == 0, "Adding 'potentially null' mark in unexpected state"); //$NON-NLS-1$
    		if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 41) {
					this.extra[5][vectorIndex] = ~0;
				}
			}
    	}
	}
}

public void markPotentiallyNonNullBit(LocalVariableBinding local) {
	if (this != DEAD_END) {
		this.tagBits |= NULL_FLAG_MASK;
        int position;
        long mask;
        if ((position = local.id + this.maxFieldCount) < BitCacheSize) {
            // use bits
        	mask = 1L << position;
        	isTrue((this.nullBit1 & mask) == 0, "Adding 'potentially non-null' mark in unexpected state"); //$NON-NLS-1$
            this.nullBit3 |= mask;
            if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 42) {
				  	this.nullBit4 = ~0;
				}
			}
        } else {
    		// use extra vector
    		int vectorIndex  = (position / BitCacheSize) - 1;
    		if (this.extra == null) {
				int length = vectorIndex + 1;
				this.extra = new long[extraLength][];
				for (int j = 0; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[0].length)) {
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		mask = 1L << (position % BitCacheSize);
    		isTrue((this.extra[2][vectorIndex] & mask) == 0, "Adding 'potentially non-null' mark in unexpected state"); //$NON-NLS-1$
    		this.extra[4][vectorIndex] |= mask;
    		if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 43) {
					this.extra[5][vectorIndex] = ~0;
				}
			}
    	}
	}
}

public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits) {
	if ((otherInits.tagBits & UNREACHABLE_OR_DEAD) != 0 && this != DEAD_END) {
		if (COVERAGE_TEST_FLAG) {
			if(CoverageTestId == 28) {
				throw new AssertionFailedException("COVERAGE 28"); //$NON-NLS-1$
			}
		}
		combineNullStatusChangeInAssertInfo(otherInits);
		return this;
	}
	if ((this.tagBits & UNREACHABLE_OR_DEAD) != 0) {
		if (COVERAGE_TEST_FLAG) {
			if(CoverageTestId == 29) {
				throw new AssertionFailedException("COVERAGE 29"); //$NON-NLS-1$
			}
		}
		otherInits.combineNullStatusChangeInAssertInfo(this);
		return (UnconditionalFlowInfo) otherInits.copy(); // make sure otherInits won't be affected
	}

	// intersection of definitely assigned variables,
	this.definiteInits &= otherInits.definiteInits;
	// union of potentially set ones
	this.potentialInits |= otherInits.potentialInits;

	// null combinations
	boolean
		thisHasNulls = (this.tagBits & NULL_FLAG_MASK) != 0,
		otherHasNulls = (otherInits.tagBits & NULL_FLAG_MASK) != 0,
		thisHadNulls = thisHasNulls;
	long
		a1, a2, a3, a4,
		na1, na2, na3, na4,
		nb1, nb2, nb3, nb4,
		b1, b2, b3, b4;
	if ((otherInits.tagBits & FlowInfo.UNREACHABLE_BY_NULLANALYSIS) != 0) {
		otherHasNulls = false; // skip merging, otherInits is unreachable by null analysis
	} else if ((this.tagBits & FlowInfo.UNREACHABLE_BY_NULLANALYSIS) != 0) { // directly copy if this is unreachable by null analysis
		this.nullBit1 = otherInits.nullBit1;
		this.nullBit2 = otherInits.nullBit2;
		this.nullBit3 = otherInits.nullBit3;
		this.nullBit4 = otherInits.nullBit4;
		thisHadNulls = false;
		thisHasNulls = otherHasNulls;
		this.tagBits = otherInits.tagBits;
	} else if (thisHadNulls) {
    	if (otherHasNulls) {
    		this.nullBit1 = (a2 = this.nullBit2) & (a3 = this.nullBit3)
    							& (a4 = this.nullBit4) & (b1 = otherInits.nullBit1)
    							& (nb2 = ~(b2 = otherInits.nullBit2))
                  			| (a1 = this.nullBit1) & (b1 & (a3 & a4 & (b3 = otherInits.nullBit3)
                  													& (b4 = otherInits.nullBit4)
                  												| (na2 = ~a2) & nb2
                  													& ((nb4 = ~b4) | (na4 = ~a4)
                  															| (na3 = ~a3) & (nb3 = ~b3))
                  												| a2 & b2 & ((na4 | na3) & (nb4	| nb3)))
                  											| na2 & b2 & b3 & b4);
    		this.nullBit2 = b2 & (nb3 | (nb1 = ~b1) | a3 & (a4 | (na1 = ~a1)) & nb4)
        			| a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1);
    		this.nullBit3 = b3 & (nb2 & b4 | nb1 | a3 & (na4 & nb4 | a4 & b4))
        			| a3 & (na2 & a4 | na1)
        			| (a2 | na1) & b1 & nb2 & nb4
        			| a1 & na2 & na4 & (b2 | nb1);
    		this.nullBit4 = na3 & (nb1 & nb3 & b4
              			| b1 & (nb2 & nb3 | a4 & b2 & nb4)
              			| na1 & a4 & (nb3 | b1 & b2))
        			| a3 & a4 & (b3 & b4 | b1 & nb2)
        			| na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2
        			| a1 & (na3 & (nb3 & b4
                        			| b1 & b2 & b3 & nb4
                        			| na2 & (nb3 | nb2))
                			| na2 & b3 & b4
                			| a2 & (nb1 & b4 | a3 & na4 & b1) & nb3);
    		// the above formulae do not handle the state 0111, do it now explicitly:
    		long ax = ~a1 & a2 & a3 & a4;
    		long bx = ~b1 & b2 & b3 & b4;
    		long x = ax|bx;
    		if (x != 0) {
    			// restore state 0111 for all variable ids in x:
    			this.nullBit1 &= ~x;
    			this.nullBit2 |= x;
    			this.nullBit3 |= x;
    			this.nullBit4 |= x;
    		}
		
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 30) {
	    		  	this.nullBit4 = ~0;
    			}
    		}
    	} else { // other has no null info
    		a1 = this.nullBit1;
      		this.nullBit1 = 0;
      		this.nullBit2 = (a2 = this.nullBit2) & (na3 = ~(a3 = this.nullBit3) | (na1 = ~a1));
      		this.nullBit3 = a3 & ((na2 = ~a2) & (a4 = this.nullBit4) | na1) | a1 & na2 & ~a4;
      		this.nullBit4 = (na3 | na2) & na1 & a4	| a1 & na3 & na2;
    		if (COVERAGE_TEST_FLAG) {
    			if(CoverageTestId == 31) {
	    		  	this.nullBit4 = ~0;
    			}
    		}
    	}
	} else if (otherHasNulls) { // only other had nulls
  		this.nullBit1 = 0;
  		this.nullBit2 = (b2 = otherInits.nullBit2) & (nb3 = ~(b3 = otherInits.nullBit3) | (nb1 = ~(b1 = otherInits.nullBit1)));
  		this.nullBit3 = b3 & ((nb2 = ~b2) & (b4 = otherInits.nullBit4) | nb1) | b1 & nb2 & ~b4;
  		this.nullBit4 = (nb3 | nb2) & nb1 & b4	| b1 & nb3 & nb2;
  		if (COVERAGE_TEST_FLAG) {
  			if(CoverageTestId == 32) {
	  		  	this.nullBit4 = ~0;
  			}
  		}
    	thisHasNulls =
    		// redundant with the three following ones
    		this.nullBit2 != 0 ||
    		this.nullBit3 != 0 ||
    		this.nullBit4 != 0;
	}

	// treating extra storage
	if (this.extra != null || otherInits.extra != null) {
		int mergeLimit = 0, copyLimit = 0, resetLimit = 0;
		int i;
		if (this.extra != null) {
			if (otherInits.extra != null) {
				// both sides have extra storage
				int length, otherLength;
				if ((length = this.extra[0].length) <
						(otherLength = otherInits.extra[0].length)) {
					// current storage is shorter -> grow current
					for (int j = 0; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[otherLength]), 0, length);
					}
					mergeLimit = length;
					copyLimit = otherLength;
					if (COVERAGE_TEST_FLAG) {
						if(CoverageTestId == 33) {
							throw new AssertionFailedException("COVERAGE 33"); //$NON-NLS-1$
						}
					}
				}
				else {
					// current storage is longer
					mergeLimit = otherLength;
					resetLimit = length;
					if (COVERAGE_TEST_FLAG) {
						if(CoverageTestId == 34) {
							throw new AssertionFailedException("COVERAGE 34"); //$NON-NLS-1$
						}
					}
				}
			}
			else {
				resetLimit = this.extra[0].length;
				if (COVERAGE_TEST_FLAG) {
					if(CoverageTestId == 35) {
						throw new AssertionFailedException("COVERAGE 35"); //$NON-NLS-1$
					}
				}
			}
		}
		else if (otherInits.extra != null) {
			// no storage here, but other has extra storage.
			int otherLength = otherInits.extra[0].length;
			this.extra = new long[extraLength][];
			for (int j = 0; j < extraLength; j++) {
				this.extra[j] = new long[otherLength];
			}
			System.arraycopy(otherInits.extra[1], 0,
				this.extra[1], 0, otherLength);
			copyLimit = otherLength;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 36) {
					throw new AssertionFailedException("COVERAGE 36"); //$NON-NLS-1$
				}
			}
		}
        // MACRO :'b,'es/nullBit\(.\)/extra[\1 + 1][i]/g
		// manage definite assignment
		for (i = 0; i < mergeLimit; i++) {
	  		this.extra[0][i] &= otherInits.extra[0][i];
	  		this.extra[1][i] |= otherInits.extra[1][i];
		}
		for (; i < copyLimit; i++) {
		  	this.extra[1][i] = otherInits.extra[1][i];
		}
		for (; i < resetLimit; i++) {
		  	this.extra[0][i] = 0;
		}
		// refine null bits requirements
		if (!otherHasNulls) {
		  if (resetLimit < mergeLimit) {
			resetLimit = mergeLimit;
		  }
		  copyLimit = 0; // no need to carry inexisting nulls
		  mergeLimit = 0;
		}
		if (!thisHadNulls) {
		  resetLimit = 0; // no need to reset anything
		}
		// compose nulls
		for (i = 0; i < mergeLimit; i++) {
    		this.extra[1 + 1][i] = (a2 = this.extra[2 + 1][i]) & (a3 = this.extra[3 + 1][i])
    							& (a4 = this.extra[4 + 1][i]) & (b1 = otherInits.extra[1 + 1][i])
    							& (nb2 = ~(b2 = otherInits.extra[2 + 1][i]))
                  			| (a1 = this.extra[1 + 1][i]) & (b1 & (a3 & a4 & (b3 = otherInits.extra[3 + 1][i])
                  													& (b4 = otherInits.extra[4 + 1][i])
                  												| (na2 = ~a2) & nb2
                  													& ((nb4 = ~b4) | (na4 = ~a4)
                  															| (na3 = ~a3) & (nb3 = ~b3))
                  												| a2 & b2 & ((na4 | na3) & (nb4	| nb3)))
                  											| na2 & b2 & b3 & b4);
    		this.extra[2 + 1][i] = b2 & (nb3 | (nb1 = ~b1) | a3 & (a4 | (na1 = ~a1)) & nb4)
        			| a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1);
    		this.extra[3 + 1][i] = b3 & (nb2 & b4 | nb1 | a3 & (na4 & nb4 | a4 & b4))
        			| a3 & (na2 & a4 | na1)
        			| (a2 | na1) & b1 & nb2 & nb4
        			| a1 & na2 & na4 & (b2 | nb1);
    		this.extra[4 + 1][i] = na3 & (nb1 & nb3 & b4
              			| b1 & (nb2 & nb3 | a4 & b2 & nb4)
              			| na1 & a4 & (nb3 | b1 & b2))
        			| a3 & a4 & (b3 & b4 | b1 & nb2)
        			| na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2
        			| a1 & (na3 & (nb3 & b4
                        			| b1 & b2 & b3 & nb4
                        			| na2 & (nb3 | nb2))
                			| na2 & b3 & b4
                			| a2 & (nb1 & b4 | a3 & na4 & b1) & nb3);
    		// the above formulae do not handle the state 0111, do it now explicitly:
    		long ax = ~a1 & a2 & a3 & a4;
    		long bx = ~b1 & b2 & b3 & b4;
    		long x = ax|bx;
    		if (x != 0) {
    			// restore state 0111 for all variable ids in x:
    			this.extra[2][i] &= ~x;
    			this.extra[3][i] |= x;
    			this.extra[4][i] |= x;
    			this.extra[5][i] |= x;
    		}
			thisHasNulls = thisHasNulls ||
				this.extra[3][i] != 0 ||
				this.extra[4][i] != 0 ||
				this.extra[5][i] != 0 ;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 37) {
					this.extra[5][i] = ~0;
				}
			}
		}
		for (; i < copyLimit; i++) {
    		this.extra[1 + 1][i] = 0;
    		this.extra[2 + 1][i] = (b2 = otherInits.extra[2 + 1][i]) & (nb3 = ~(b3 = otherInits.extra[3 + 1][i]) | (nb1 = ~(b1 = otherInits.extra[1 + 1][i])));
    		this.extra[3 + 1][i] = b3 & ((nb2 = ~b2) & (b4 = otherInits.extra[4 + 1][i]) | nb1) | b1 & nb2 & ~b4;
    		this.extra[4 + 1][i] = (nb3 | nb2) & nb1 & b4	| b1 & nb3 & nb2;
			thisHasNulls = thisHasNulls ||
				this.extra[3][i] != 0 ||
				this.extra[4][i] != 0 ||
				this.extra[5][i] != 0;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 38) {
					this.extra[5][i] = ~0;
				}
			}
		}
		for (; i < resetLimit; i++) {
    		a1 = this.extra[1 + 1][i];
      		this.extra[1 + 1][i] = 0;
      		this.extra[2 + 1][i] = (a2 = this.extra[2 + 1][i]) & (na3 = ~(a3 = this.extra[3 + 1][i]) | (na1 = ~a1));
      		this.extra[3 + 1][i] = a3 & ((na2 = ~a2) & (a4 = this.extra[4 + 1][i]) | na1) | a1 & na2 & ~a4;
      		this.extra[4 + 1][i] = (na3 | na2) & na1 & a4	| a1 & na3 & na2;
			thisHasNulls = thisHasNulls ||
				this.extra[3][i] != 0 ||
				this.extra[4][i] != 0 ||
				this.extra[5][i] != 0;
			if (COVERAGE_TEST_FLAG) {
				if(CoverageTestId == 39) {
					this.extra[5][i] = ~0;
				}
			}
		}
	}
	combineNullStatusChangeInAssertInfo(otherInits);
	if (thisHasNulls) {
		this.tagBits |= NULL_FLAG_MASK;
	}
	else {
		this.tagBits &= ~NULL_FLAG_MASK;
	}
	return this;
}

/*
 * Answer the total number of fields in enclosing types of a given type
 */
static int numberOfEnclosingFields(ReferenceBinding type){
	int count = 0;
	type = type.enclosingType();
	while(type != null) {
		count += type.fieldCount();
		type = type.enclosingType();
	}
	return count;
}

public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
	if (this == DEAD_END) {
		return this;
	}
	UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
	copy.definiteInits = this.definiteInits;
	copy.potentialInits = this.potentialInits;
	copy.tagBits = this.tagBits & ~NULL_FLAG_MASK;
	copy.maxFieldCount = this.maxFieldCount;
	copy.nullStatusChangedInAssert = this.nullStatusChangedInAssert;
	if (this.extra != null) {
		int length;
		copy.extra = new long[extraLength][];
		System.arraycopy(this.extra[0], 0,
			(copy.extra[0] =
				new long[length = this.extra[0].length]), 0, length);
		System.arraycopy(this.extra[1], 0,
			(copy.extra[1] = new long[length]), 0, length);
		for (int j = 2; j < extraLength; j++) {
			copy.extra[j] = new long[length];
		}
	}
	return copy;
}

public FlowInfo safeInitsWhenTrue() {
	return copy();
}

public FlowInfo setReachMode(int reachMode) {
	if (this == DEAD_END) {// cannot modify DEAD_END
		return this;
	}	
	if (reachMode == REACHABLE ) {
		this.tagBits &= ~UNREACHABLE;
	} else if (reachMode == UNREACHABLE_BY_NULLANALYSIS ) {
		this.tagBits |= UNREACHABLE_BY_NULLANALYSIS;	// do not interfere with definite assignment analysis
	} else {
		if ((this.tagBits & UNREACHABLE) == 0) {
			// reset optional inits when becoming unreachable
			// see InitializationTest#test090 (and others)
			this.potentialInits = 0;
			if (this.extra != null) {
				for (int i = 0, length = this.extra[0].length;
						i < length; i++) {
					this.extra[1][i] = 0;
				}
			}
		}
		this.tagBits |= reachMode;
	}
	return this;
}

public String toString(){
	// PREMATURE consider printing bit fields as 0001 0001 1000 0001...
	if (this == DEAD_END){
		return "FlowInfo.DEAD_END"; //$NON-NLS-1$
	}
	if ((this.tagBits & NULL_FLAG_MASK) != 0) {
		if (this.extra == null) {
			return "FlowInfo<def: " + this.definiteInits //$NON-NLS-1$
				+", pot: " + this.potentialInits  //$NON-NLS-1$
				+ ", reachable:" + ((this.tagBits & UNREACHABLE) == 0) //$NON-NLS-1$
				+", null: " + this.nullBit1 //$NON-NLS-1$
					+ this.nullBit2 + this.nullBit3 + this.nullBit4
				+">"; //$NON-NLS-1$
		}
		else {
			String def = "FlowInfo<def:[" + this.definiteInits, //$NON-NLS-1$
				pot = "], pot:[" + this.potentialInits, //$NON-NLS-1$
				nullS = ", null:[" + this.nullBit1 //$NON-NLS-1$
					+ this.nullBit2 + this.nullBit3 + this.nullBit4;
			int i, ceil;
			for (i = 0, ceil = this.extra[0].length > 3 ?
								3 :
								this.extra[0].length;
				i < ceil; i++) {
				def += "," + this.extra[0][i]; //$NON-NLS-1$
				pot += "," + this.extra[1][i]; //$NON-NLS-1$
				nullS += "," + this.extra[2][i] //$NON-NLS-1$
				    + this.extra[3][i] + this.extra[4][i] + this.extra[5][i];
			}
			if (ceil < this.extra[0].length) {
				def += ",..."; //$NON-NLS-1$
				pot += ",..."; //$NON-NLS-1$
				nullS += ",..."; //$NON-NLS-1$
			}
			return def + pot
				+ "], reachable:" + ((this.tagBits & UNREACHABLE) == 0) //$NON-NLS-1$
				+ nullS
				+ "]>"; //$NON-NLS-1$
		}
	}
	else {
		if (this.extra == null) {
			return "FlowInfo<def: " + this.definiteInits //$NON-NLS-1$
				+", pot: " + this.potentialInits  //$NON-NLS-1$
				+ ", reachable:" + ((this.tagBits & UNREACHABLE) == 0) //$NON-NLS-1$
				+", no null info>"; //$NON-NLS-1$
		}
		else {
			String def = "FlowInfo<def:[" + this.definiteInits, //$NON-NLS-1$
				pot = "], pot:[" + this.potentialInits; //$NON-NLS-1$
			int i, ceil;
			for (i = 0, ceil = this.extra[0].length > 3 ?
								3 :
								this.extra[0].length;
				i < ceil; i++) {
				def += "," + this.extra[0][i]; //$NON-NLS-1$
				pot += "," + this.extra[1][i]; //$NON-NLS-1$
			}
			if (ceil < this.extra[0].length) {
				def += ",..."; //$NON-NLS-1$
				pot += ",..."; //$NON-NLS-1$
			}
			return def + pot
				+ "], reachable:" + ((this.tagBits & UNREACHABLE) == 0) //$NON-NLS-1$
				+ ", no null info>"; //$NON-NLS-1$
		}
	}
}

public UnconditionalFlowInfo unconditionalCopy() {
	return (UnconditionalFlowInfo) copy();
}

public UnconditionalFlowInfo unconditionalFieldLessCopy() {
	// TODO (maxime) may consider leveraging null contribution verification as it is done in copy
	UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
	copy.tagBits = this.tagBits;
	copy.maxFieldCount = this.maxFieldCount;
	int limit = this.maxFieldCount;
	if (limit < BitCacheSize) {
		long mask;
		copy.definiteInits = this.definiteInits & (mask = ~((1L << limit)-1));
		copy.potentialInits = this.potentialInits & mask;
		copy.nullBit1 = this.nullBit1 & mask;
		copy.nullBit2 = this.nullBit2 & mask;
		copy.nullBit3 = this.nullBit3 & mask;
		copy.nullBit4 = this.nullBit4 & mask;
	}
	copy.nullStatusChangedInAssert = this.nullStatusChangedInAssert;
	// use extra vector
	if (this.extra == null) {
		return copy; // if vector not yet allocated, then not initialized
	}
	int vectorIndex, length, copyStart;
	if ((vectorIndex = (limit / BitCacheSize) - 1) >=
			(length = this.extra[0].length)) {
		return copy; // not enough room yet
	}
	long mask;
	copy.extra = new long[extraLength][];
	if ((copyStart = vectorIndex + 1) < length) {
		int copyLength = length - copyStart;
		for (int j = 0; j < extraLength; j++) {
			System.arraycopy(this.extra[j], copyStart,
				(copy.extra[j] = new long[length]), copyStart,
				copyLength);
		}
	}
	else if (vectorIndex >= 0) {
		for (int j = 0; j < extraLength; j++) {
			copy.extra[j] = new long[length];
		}
	}
	if (vectorIndex >= 0) {
		mask = ~((1L << (limit % BitCacheSize))-1);
		for (int j = 0; j < extraLength; j++) {
			copy.extra[j][vectorIndex] =
				this.extra[j][vectorIndex] & mask;
		}
	}
	return copy;
}

public UnconditionalFlowInfo unconditionalInits() {
	// also see conditional inits, where it requests them to merge
	return this;
}

public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
	return this;
}

public void markedAsNullOrNonNullInAssertExpression(LocalVariableBinding local) {
	int position = local.id + this.maxFieldCount;
	int oldLength;
	if (this.nullStatusChangedInAssert == null) {
		this.nullStatusChangedInAssert = new int[position + 1];
	}
	else {
		if(position >= (oldLength = this.nullStatusChangedInAssert.length)) {
			System.arraycopy(this.nullStatusChangedInAssert, 0, (this.nullStatusChangedInAssert = new int[position + 1]), 0, oldLength); 
		}
	}
	this.nullStatusChangedInAssert[position] = 1;
}

public boolean isMarkedAsNullOrNonNullInAssertExpression(LocalVariableBinding local) {
	int position = local.id + this.maxFieldCount;
	if(this.nullStatusChangedInAssert == null || position >= this.nullStatusChangedInAssert.length) {
		return false;
	}
	if(this.nullStatusChangedInAssert[position] == 1) {
		return true;
	}
	return false;
}

/**
 * Combine the null status changes in assert expressions info
 * @param otherInits
 */
// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=303448
private void combineNullStatusChangeInAssertInfo(UnconditionalFlowInfo otherInits) {
	if (this.nullStatusChangedInAssert != null || otherInits.nullStatusChangedInAssert != null) {
		int mergedLength, length;
		if (this.nullStatusChangedInAssert != null) {
			if (otherInits.nullStatusChangedInAssert != null) {
				if(otherInits.nullStatusChangedInAssert.length > this.nullStatusChangedInAssert.length) {
					mergedLength = otherInits.nullStatusChangedInAssert.length;
					length = this.nullStatusChangedInAssert.length;
					System.arraycopy(this.nullStatusChangedInAssert, 0, (this.nullStatusChangedInAssert = new int[mergedLength]), 0, length);
					for(int i = 0; i < length; i ++) {
						this.nullStatusChangedInAssert[i] |= otherInits.nullStatusChangedInAssert[i];
					}
					System.arraycopy(otherInits.nullStatusChangedInAssert, length, this.nullStatusChangedInAssert, length, mergedLength - length);
				} else {
					for(int i = 0; i < otherInits.nullStatusChangedInAssert.length; i ++) {
						this.nullStatusChangedInAssert[i] |= otherInits.nullStatusChangedInAssert[i];
					}
				}
			}
		} else if (otherInits.nullStatusChangedInAssert != null) {
			this.nullStatusChangedInAssert = otherInits.nullStatusChangedInAssert;
		}
	}
}
}

