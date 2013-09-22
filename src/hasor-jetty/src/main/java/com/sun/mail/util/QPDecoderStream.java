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
 * @(#)QPDecoderStream.java	1.11 07/05/04
 */

package com.sun.mail.util;

import java.io.*;

/**
 * This class implements a QP Decoder. It is implemented as
 * a FilterInputStream, so one can just wrap this class around
 * any input stream and read bytes from this filter. The decoding
 * is done as the bytes are read out.
 * 
 * @author John Mani
 */

public class QPDecoderStream extends FilterInputStream {
    protected byte[] ba = new byte[2];
    protected int spaces = 0;

    /**
     * Create a Quoted Printable decoder that decodes the specified 
     * input stream.
     * @param in        the input stream
     */
    public QPDecoderStream(InputStream in) {
	super(new PushbackInputStream(in, 2)); // pushback of size=2
    }

    /**
     * Read the next decoded byte from this input stream. The byte
     * is returned as an <code>int</code> in the range <code>0</code>
     * to <code>255</code>. If no byte is available because the end of
     * the stream has been reached, the value <code>-1</code> is returned.
     * This method blocks until input data is available, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
	if (spaces > 0) {
	    // We have cached space characters, return one
	    spaces--;
	    return ' ';
	}
	
	int c = in.read();

	if (c == ' ') { 
	    // Got space, keep reading till we get a non-space char
	    while ((c = in.read()) == ' ')
		spaces++;

	    if (c == '\r' || c == '\n' || c == -1)
		// If the non-space char is CR/LF/EOF, the spaces we got
	    	// so far is junk introduced during transport. Junk 'em.
		spaces = 0;
    	    else {
		// The non-space char is NOT CR/LF, the spaces are valid.
		((PushbackInputStream)in).unread(c);
		c = ' ';
	    }
	    return c; // return either <SPACE> or <CR/LF>
	}
	else if (c == '=') {
	    // QP Encoded atom. Decode the next two bytes
	    int a = in.read();

	    if (a == '\n') {
		/* Hmm ... not really confirming QP encoding, but lets
		 * allow this as a LF terminated encoded line .. and
		 * consider this a soft linebreak and recurse to fetch 
		 * the next char.
		 */
		return read();
	    } else if (a == '\r') {
		// Expecting LF. This forms a soft linebreak to be ignored.
		int b = in.read();
		if (b != '\n') 
		    /* Not really confirming QP encoding, but
		     * lets allow this as well.
		     */
		    ((PushbackInputStream)in).unread(b);
		return read();
	    } else if (a == -1) {
	   	// Not valid QP encoding, but we be nice and tolerant here !
		return -1;
	    } else {
		ba[0] = (byte)a;
		ba[1] = (byte)in.read();
		try {
		    return ASCIIUtility.parseInt(ba, 0, 2, 16);
		} catch (NumberFormatException nex) {
		    /*
		    System.err.println(
		     	"Illegal characters in QP encoded stream: " + 
		     	ASCIIUtility.toString(ba, 0, 2)
		    );
		    */

		    ((PushbackInputStream)in).unread(ba);
		    return c;
		}
	    }
	}
	return c;
    }

    /**
     * Reads up to <code>len</code> decoded bytes of data from this input stream
     * into an array of bytes. This method blocks until some input is
     * available.
     * <p>
     *
     * @param      buf   the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read(byte[] buf, int off, int len) throws IOException {
	int i, c;
	for (i = 0; i < len; i++) {
	    if ((c = read()) == -1) {
		if (i == 0) // At end of stream, so we should
		    i = -1; // return -1 , NOT 0.
		break;
	    }
	    buf[off+i] = (byte)c;
	}
        return i;
    }

    /**
     * Tests if this input stream supports marks. Currently this class
     * does not support marks
     */
    public boolean markSupported() {
	return false;
    }

    /**
     * Returns the number of bytes that can be read from this input
     * stream without blocking. The QP algorithm does not permit
     * a priori knowledge of the number of bytes after decoding, so
     * this method just invokes the <code>available</code> method
     * of the original input stream.
     */
    public int available() throws IOException {
	// This is bogus ! We don't really know how much
	// bytes are available *after* decoding
	return in.available();
    }

    /**** begin TEST program
    public static void main(String argv[]) throws Exception {
        FileInputStream infile = new FileInputStream(argv[0]);
        QPDecoderStream decoder = new QPDecoderStream(infile);
        int c;
 
        while ((c = decoder.read()) != -1)
            System.out.print((char)c);
        System.out.println();
    }
    *** end TEST program ****/
}
