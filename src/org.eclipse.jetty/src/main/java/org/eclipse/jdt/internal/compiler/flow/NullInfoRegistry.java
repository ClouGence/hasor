/*******************************************************************************
 * Copyright (c) 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann <stephan@cs.tu-berlin.de> - Contribution for bug 320170   
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;

/**
 * A degenerate form of UnconditionalFlowInfo explicitly meant to capture
 * the effects of null related operations within try blocks. Given the fact
 * that a try block might exit at any time, a null related operation that
 * occurs within such a block mitigates whatever we know about the previous
 * null status of involved variables. NullInfoRegistry handles that
 * by negating upstream definite information that clashes with what a given
 * statement contends about the same variable. It also implements
 * {@link #mitigateNullInfoOf(FlowInfo) mitigateNullInfo} so as to elaborate the
 * flow info presented in input of finally blocks.
 */
public class NullInfoRegistry extends UnconditionalFlowInfo {
	// significant states at this level:
  	// def. non null, def. null, def. unknown, prot. non null

// PREMATURE implement coverage and low level tests

/**
 * Make a new null info registry, using an upstream flow info. All definite
 * assignments of the upstream are carried forward, since a try block may
 * exit before its first statement.
 * @param upstream - UnconditionalFlowInfo: the flow info before we enter the
 * 		try block; only definite assignments are considered; this parameter is
 *  	not modified by this constructor
 */
public NullInfoRegistry(UnconditionalFlowInfo upstream) {
	this.maxFieldCount = upstream.maxFieldCount;
	if ((upstream.tagBits & NULL_FLAG_MASK) != 0) {
		long u1, u2, u3, u4, nu2, nu3, nu4;
		this.nullBit2 = (u1 = upstream.nullBit1)
			& (u2 = upstream.nullBit2)
			& (nu3 = ~(u3 = upstream.nullBit3))
			& (nu4 = ~(u4 = upstream.nullBit4));
		this.nullBit3 =	u1 & (nu2 = ~u2) & u3 & nu4;
		this.nullBit4 =	u1 & nu2 &nu3 & u4;
		if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0) {
			this.tagBits |= NULL_FLAG_MASK;
		}
		if (upstream.extra != null) {
			this.extra = new long[extraLength][];
			int length = upstream.extra[2].length;
			for (int i = 2; i < extraLength; i++) {
				this.extra[i] = new long[length];
			}
			for (int i = 0; i < length; i++) {
        		this.extra[2 + 1][i] = (u1 = upstream.extra[1 + 1][i])
        			& (u2 = upstream.extra[2 + 1][i])
        			& (nu3 = ~(u3 = upstream.extra[3 + 1][i]))
        			& (nu4 = ~(u4 = upstream.extra[4 + 1][i]));
        		this.extra[3 + 1][i] =	u1 & (nu2 = ~u2) & u3 & nu4;
        		this.extra[4 + 1][i] =	u1 & nu2 &nu3 & u4;
        		if ((this.extra[2 + 1][i] | this.extra[3 + 1][i] | this.extra[4 + 1][i]) != 0) {
        			this.tagBits |= NULL_FLAG_MASK;
        		}
			}
		}
	}
}

/**
 * Add the information held by another NullInfoRegistry instance to this,
 * then return this.
 * @param other - NullInfoRegistry: the information to add to this
 * @return this, modified to carry the information held by other
 */
public NullInfoRegistry add(NullInfoRegistry other) {
	if ((other.tagBits & NULL_FLAG_MASK) == 0) {
		return this;
	}
	this.tagBits |= NULL_FLAG_MASK;
	this.nullBit1 |= other.nullBit1;
	this.nullBit2 |= other.nullBit2;
	this.nullBit3 |= other.nullBit3;
	this.nullBit4 |= other.nullBit4;
	if (other.extra != null) {
		if (this.extra == null) {
			this.extra = new long[extraLength][];
			for (int i = 2, length = other.extra[2].length; i < extraLength; i++) {
				System.arraycopy(other.extra[i], 0,
					(this.extra[i] = new long[length]), 0, length);
			}
		} else {
			int length = this.extra[2].length, otherLength = other.extra[2].length;
			if (otherLength > length) {
				for (int i = 2; i < extraLength; i++) {
					System.arraycopy(this.extra[i], 0,
						(this.extra[i] = new long[otherLength]), 0, length);
					System.arraycopy(other.extra[i], length,
						this.extra[i], length, otherLength - length);
				}
			} else if (otherLength < length) {
				length = otherLength;
			}
			for (int i = 2; i < extraLength; i++) {
				for (int j = 0; j < length; j++) {
					this.extra[i][j] |= other.extra[i][j];
				}
			}
		}
	}
	return this;
}

public void markAsComparedEqualToNonNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// set protected non null
    		this.nullBit1 |= (1L << position);
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 290) {
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
				for (int j = 2; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[2].length)) {
					for (int j = 2; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		this.extra[2][vectorIndex] |= (1L << (position % BitCacheSize));
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 300) {
		   		  	this.extra[5][vectorIndex] = ~0;
    			}
    		}
    	}
	}
}

public void markAsDefinitelyNonNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// set assigned non null
    		this.nullBit3 |= (1L << position);
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 290) {
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
				for (int j = 2; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[2].length)) {
					for (int j = 2; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		this.extra[4][vectorIndex] |= (1L << (position % BitCacheSize));
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 300) {
	    		  	this.extra[5][vectorIndex] = ~0;
    			}
    		}
    	}
	}
}
// PREMATURE consider ignoring extra 0 to 2 included - means a1 should not be used either
// PREMATURE project protected non null onto something else
public void markAsDefinitelyNull(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// set assigned null
    		this.nullBit2 |= (1L << position);
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 290) {
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
				for (int j = 2; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[2].length)) {
					for (int j = 2; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		this.extra[3][vectorIndex] |= (1L << (position % BitCacheSize));
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 300) {
	    		  	this.extra[5][vectorIndex] = ~0;
    			}
    		}
    	}
	}
}

public void markAsDefinitelyUnknown(LocalVariableBinding local) {
	// protected from non-object locals in calling methods
	if (this != DEAD_END) {
    	this.tagBits |= NULL_FLAG_MASK;
    	int position;
    	// position is zero-based
    	if ((position = local.id + this.maxFieldCount) < BitCacheSize) { // use bits
    		// set assigned unknown
    		this.nullBit4 |= (1L << position);
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 290) {
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
				for (int j = 2; j < extraLength; j++) {
					this.extra[j] = new long[length];
				}
			}
			else {
				int oldLength; // might need to grow the arrays
				if (vectorIndex >= (oldLength = this.extra[2].length)) {
					for (int j = 2; j < extraLength; j++) {
						System.arraycopy(this.extra[j], 0,
							(this.extra[j] = new long[vectorIndex + 1]), 0,
							oldLength);
					}
				}
			}
    		this.extra[5][vectorIndex] |= (1L << (position % BitCacheSize));
    		if (COVERAGE_TEST_FLAG) {
    			if (CoverageTestId == 300) {
	    		  	this.extra[5][vectorIndex] = ~0;
    			}
    		}
    	}
	}
}

/**
 * Mitigate the definite and protected info of flowInfo, depending on what
 * this null info registry knows about potential assignments and messages
 * sends involving locals. May return flowInfo unchanged, or a modified,
 * fresh copy of flowInfo.
 * @param flowInfo - FlowInfo: the flow information that this null info
 * 		registry may mitigate
 * @return a copy of flowInfo carrying mitigated information, or else
 * 		flowInfo unchanged
 */
public UnconditionalFlowInfo mitigateNullInfoOf(FlowInfo flowInfo) {
	if ((this.tagBits & NULL_FLAG_MASK) == 0) {
		return flowInfo.unconditionalInits();
	}
	long m, m1, nm1, m2, nm2, m3, a2, a3, a4, s1, s2, ns2, s3, ns3, s4, ns4;
	boolean newCopy = false;
	UnconditionalFlowInfo source = flowInfo.unconditionalInits();
	// clear incompatible protections
	m1 = (s1 = source.nullBit1) & (s3 = source.nullBit3)
				& (s4 = source.nullBit4)
			// prot. non null
		& ((a2 = this.nullBit2) | (a4 = this.nullBit4));
			// null or unknown
	m2 = s1 & (s2 = this.nullBit2) & (s3 ^ s4) // TODO(stephan): potential typo: should this be "s2 = source.nullBit2"???
			// prot. null
		& ((a3 = this.nullBit3) | a4);
			// non null or unknown
	// clear incompatible assignments
	// PREMATURE check effect of protected non null (no NPE on call)
	// TODO (maxime) code extensive implementation tests
	m3 = s1	& (s2 & (ns3 = ~s3) & (ns4 = ~s4) & (a3 | a4)
				| (ns2 = ~s2) & s3 & ns4 & (a2 | a4)
				| ns2 & ns3 & s4 & (a2 | a3));
	if ((m = (m1 | m2 | m3)) != 0) {
		newCopy = true;
		source = source.unconditionalCopy();
		source.nullBit1 &= ~m;
		source.nullBit2 &= (nm1 = ~m1) & ((nm2 = ~m2) | a4);
		source.nullBit3 &= (nm1 | a2) & nm2;
		source.nullBit4 &= nm1 & nm2;
		// any variable that is (pot n, pot nn, pot un) at end of try (as captured by *this* NullInfoRegistry)
		// has the same uncertainty also for the mitigated case (function result)
		// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=320170 -  [compiler] [null] Whitebox issues in null analysis
		// and org.eclipse.jdt.core.tests.compiler.regression.NullReferenceTest.test0536_try_finally()
		long x = ~this.nullBit1 & a2 & a3 & a4; // x is set for all variable ids that have state 0111 (pot n, pot nn, pot un)
		if (x != 0) {
			// restore state 0111 for all variable ids in x:
			source.nullBit1 &= ~x;
			source.nullBit2 |= x;
			source.nullBit3 |= x;
			source.nullBit4 |= x;
		}
	}
	if (this.extra != null && source.extra != null) {
		int length = this.extra[2].length, sourceLength = source.extra[0].length;
		if (sourceLength < length) {
			length = sourceLength;
		}
		for (int i = 0; i < length; i++) {
        	m1 = (s1 = source.extra[1 + 1][i]) & (s3 = source.extra[3 + 1][i])
        				& (s4 = source.extra[4 + 1][i])
        		& ((a2 = this.extra[2 + 1][i]) | (a4 = this.extra[4 + 1][i]));
        	m2 = s1 & (s2 = this.extra[2 + 1][i]) & (s3 ^ s4)
        		& ((a3 = this.extra[3 + 1][i]) | a4);
        	m3 = s1	& (s2 & (ns3 = ~s3) & (ns4 = ~s4) & (a3 | a4)
        				| (ns2 = ~s2) & s3 & ns4 & (a2 | a4)
        				| ns2 & ns3 & s4 & (a2 | a3));
        	if ((m = (m1 | m2 | m3)) != 0) {
        	  	if (! newCopy) {
            		newCopy = true;
            		source = source.unconditionalCopy();
        	  	}
        		source.extra[1 + 1][i] &= ~m;
        		source.extra[2 + 1][i] &= (nm1 = ~m1) & ((nm2 = ~m2) | a4);
        		source.extra[3 + 1][i] &= (nm1 | a2) & nm2;
        		source.extra[4 + 1][i] &= nm1 & nm2;
        	}
		}
	}
	return source;
}

public String toString(){
	if (this.extra == null) {
		return "NullInfoRegistry<" + this.nullBit1 //$NON-NLS-1$
			+ this.nullBit2 + this.nullBit3 + this.nullBit4
			+ ">"; //$NON-NLS-1$
	}
	else {
		String nullS = "NullInfoRegistry<[" + this.nullBit1 //$NON-NLS-1$
			+ this.nullBit2 + this.nullBit3 + this.nullBit4;
			int i, ceil;
			for (i = 0, ceil = this.extra[0].length > 3 ?
								3 :
								this.extra[0].length;
				i < ceil; i++) {
				nullS += "," + this.extra[2][i] //$NON-NLS-1$
				    + this.extra[3][i] + this.extra[4][i] + this.extra[5][i];
			}
			if (ceil < this.extra[0].length) {
				nullS += ",..."; //$NON-NLS-1$
			}
			return nullS + "]>"; //$NON-NLS-1$
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
    			for (int j = 2; j < extraLength; j++) {
    				this.extra[j] = new long[length];
    			}
    		} else {
    			int oldLength; // might need to grow the arrays
    			if (vectorIndex >= (oldLength = this.extra[2].length)) {
    				for (int j = 2; j < extraLength; j++) {
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
    			for (int j = 2; j < extraLength; j++) {
    				this.extra[j] = new long[length];
    			}
    		} else {
    			int oldLength; // might need to grow the arrays
    			if (vectorIndex >= (oldLength = this.extra[2].length)) {
    				for (int j = 2; j < extraLength; j++) {
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
    			for (int j = 2; j < extraLength; j++) {
    				this.extra[j] = new long[length];
    			}
    		} else {
    			int oldLength; // might need to grow the arrays
    			if (vectorIndex >= (oldLength = this.extra[2].length)) {
    				for (int j = 2; j < extraLength; j++) {
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
}

