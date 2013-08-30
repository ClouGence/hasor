/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * @(#)TraceOutputStream.java	1.6 07/05/04
 */

package com.sun.mail.util;

import java.io.*;

/**
 * This class is a subclass of DataOutputStream that copies the
 * data being written into the DataOutputStream into another output
 * stream. This class is used here to provide a debug trace of the
 * stuff thats being written out into the DataOutputStream.
 *
 * @author John Mani
 */

public class TraceOutputStream extends FilterOutputStream {
    private boolean trace = false;
    private boolean quote = false;
    private OutputStream traceOut;

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param   out   the underlying output stream.
     * @param	traceOut	the trace stream.
     */
    public TraceOutputStream(OutputStream out, OutputStream traceOut) {
	super(out);
	this.traceOut = traceOut;
    }

    /**
     * Set the trace mode.
     */
    public void setTrace(boolean trace) {
	this.trace = trace;
    }

    /**
     * Set quote mode.
     * @param	quote	the quote mode
     */
    public void setQuote(boolean quote) {
	this.quote = quote;
    }

    /**
     * Writes the specified <code>byte</code> to this output stream.
     * Writes out the byte into the trace stream if the trace mode
     * is <code>true</code>
     */
    public void write(int b) throws IOException {
	if (trace) {
	    if (quote)
		writeByte(b);
	    else
		traceOut.write(b);
	}
	out.write(b);
    }
	    
    /**
     * Writes <code>b.length</code> bytes to this output stream.
     * Writes out the bytes into the trace stream if the trace
     * mode is <code>true</code>
     */
    public void write(byte b[], int off, int len) throws IOException {
	if (trace) {
	    if (quote) {
		for (int i = 0; i < len; i++)
		    writeByte(b[off + i]);
	    } else
		traceOut.write(b, off, len);
	}
	out.write(b, off, len);
    }

    /**
     * Write a byte in a way that every byte value is printable ASCII.
     */
    private final void writeByte(int b) throws IOException {
	b &= 0xff;
	if (b > 0x7f) {
	    traceOut.write('M');
	    traceOut.write('-');
	    b &= 0x7f;
	}
	if (b == '\r') {
	    traceOut.write('\\');
	    traceOut.write('r');
	} else if (b == '\n') {
	    traceOut.write('\\');
	    traceOut.write('n');
	    traceOut.write('\n');
	} else if (b == '\t') {
	    traceOut.write('\\');
	    traceOut.write('t');
	} else if (b < ' ') {
	    traceOut.write('^');
	    traceOut.write('@' + b);
	} else {
	    traceOut.write(b);
	}
    }
}
