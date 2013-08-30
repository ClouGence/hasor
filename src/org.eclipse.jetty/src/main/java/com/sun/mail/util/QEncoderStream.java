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
 * @(#)QEncoderStream.java	1.6 07/05/04
 */

package com.sun.mail.util;

import java.io.*;

/**
 * This class implements a Q Encoder as defined by RFC 2047 for 
 * encoding MIME headers. It subclasses the QPEncoderStream class.
 * 
 * @author John Mani
 */

public class QEncoderStream extends QPEncoderStream {

    private String specials;
    private static String WORD_SPECIALS = "=_?\"#$%&'(),.:;<>@[\\]^`{|}~";
    private static String TEXT_SPECIALS = "=_?";

    /**
     * Create a Q encoder that encodes the specified input stream
     * @param out        the output stream
     * @param encodingWord true if we are Q-encoding a word within a
     *			phrase.
     */
    public QEncoderStream(OutputStream out, boolean encodingWord) {
	super(out, Integer.MAX_VALUE); // MAX_VALUE is 2^31, should
				       // suffice (!) to indicate that
				       // CRLFs should not be inserted
				       // when encoding rfc822 headers

	// a RFC822 "word" token has more restrictions than a
	// RFC822 "text" token.
	specials = encodingWord ? WORD_SPECIALS : TEXT_SPECIALS;
    }

    /**
     * Encodes the specified <code>byte</code> to this output stream.
     * @param      c   the <code>byte</code>.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(int c) throws IOException {
	c = c & 0xff; // Turn off the MSB.
	if (c == ' ')
	    output('_', false);
	else if (c < 040 || c >= 0177 || specials.indexOf(c) >= 0)
	    // Encoding required. 
	    output(c, true);
	else // No encoding required
	    output(c, false);
    }

    /**
     * Returns the length of the encoded version of this byte array.
     */
    public static int encodedLength(byte[] b, boolean encodingWord) {
	int len = 0;
	String specials = encodingWord ? WORD_SPECIALS: TEXT_SPECIALS;
	for (int i = 0; i < b.length; i++) {
	    int c = b[i] & 0xff; // Mask off MSB
	    if (c < 040 || c >= 0177 || specials.indexOf(c) >= 0)
		// needs encoding
		len += 3; // Q-encoding is 1 -> 3 conversion
	    else
		len++;
	}
	return len;
    }

    /**** begin TEST program ***
    public static void main(String argv[]) throws Exception {
        FileInputStream infile = new FileInputStream(argv[0]);
        QEncoderStream encoder = new QEncoderStream(System.out);
        int c;
 
        while ((c = infile.read()) != -1)
            encoder.write(c);
        encoder.close();
    }
    *** end TEST program ***/
}
