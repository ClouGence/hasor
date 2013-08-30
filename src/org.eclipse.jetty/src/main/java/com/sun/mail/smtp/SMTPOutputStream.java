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
 * @(#)SMTPOutputStream.java	1.12 07/05/04
 */

package com.sun.mail.smtp;

import java.io.*;
import com.sun.mail.util.CRLFOutputStream;

/**
 * In addition to converting lines into the canonical format,
 * i.e., terminating lines with the CRLF sequence, escapes the "."
 * by adding another "." to any "." that appears in the beginning
 * of a line.  See RFC821 section 4.5.2.
 * 
 * @author Max Spivak
 * @see CRLFOutputStream
 */
public class SMTPOutputStream extends CRLFOutputStream {
    public SMTPOutputStream(OutputStream os) {
	super(os);
    }

    public void write(int b) throws IOException {
	// if that last character was a newline, and the current
	// character is ".", we always write out an extra ".".
	if ((lastb == '\n' || lastb == '\r' || lastb == -1) && b == '.') {
	    out.write('.');
	}
	
	super.write(b);
    }

    /* 
     * This method has been added to improve performance.
     */
    public void write(byte b[], int off, int len) throws IOException {
	int lastc = (lastb == -1) ? '\n' : lastb;
	int start = off;
	
	len += off;
	for (int i = off; i < len; i++) {
	    if ((lastc == '\n' || lastc == '\r') && b[i] == '.') {
		super.write(b, start, i - start);
		out.write('.');
		start = i;
	    }
	    lastc = b[i];
	}
	if ((len - start) > 0)
	    super.write(b, start, len - start);
    }

    /**
     * Override flush method in FilterOutputStream.
     *
     * The MimeMessage writeTo method flushes its buffer at the end,
     * but we don't want to flush data out to the socket until we've
     * also written the terminating "\r\n.\r\n".
     *
     * We buffer nothing so there's nothing to flush.  We depend
     * on the fact that CRLFOutputStream also buffers nothing.
     * SMTPTransport will manually flush the socket before reading
     * the response.
     */
    public void flush() {
	// do nothing
    }

    /**
     * Ensure we're at the beginning of a line.
     * Write CRLF if not.
     */
    public void ensureAtBOL() throws IOException {
	if (!atBOL)
	    super.writeln();
    }
}
