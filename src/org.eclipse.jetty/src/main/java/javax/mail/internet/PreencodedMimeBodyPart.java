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
 * @(#)PreencodedMimeBodyPart.java	1.3 07/05/04
 */

package javax.mail.internet;

import java.io.*;
import java.util.Enumeration;
import javax.mail.*;

import com.sun.mail.util.LineOutputStream;

/**
 * A MimeBodyPart that handles data that has already been encoded.
 * This class is useful when constructing a message and attaching
 * data that has already been encoded (for example, using base64
 * encoding).  The data may have been encoded by the application,
 * or may have been stored in a file or database in encoded form.
 * The encoding is supplied when this object is created.  The data
 * is attached to this object in the usual fashion, by using the
 * <code>setText</code>, <code>setContent</code>, or
 * <code>setDataHandler</code> methods.
 *
 * @since	JavaMail 1.4
 */

public class PreencodedMimeBodyPart extends MimeBodyPart {
    private String encoding;

    /**
     * Create a PreencodedMimeBodyPart that assumes the data is
     * encoded using the specified encoding.  The encoding must
     * be a MIME supported Content-Transfer-Encoding.
     */
    public PreencodedMimeBodyPart(String encoding) {
	this.encoding = encoding;
    }

    /**
     * Returns the content transfer encoding specified when
     * this object was created.
     */
    public String getEncoding() throws MessagingException {
	return encoding;
    }

    /**
     * Output the body part as an RFC 822 format stream.
     *
     * @exception MessagingException
     * @exception IOException	if an error occurs writing to the
     *				stream or if an error is generated
     *				by the javax.activation layer.
     * @see javax.activation.DataHandler#writeTo
     */
    public void writeTo(OutputStream os)
			throws IOException, MessagingException {

	// see if we already have a LOS
	LineOutputStream los = null;
	if (os instanceof LineOutputStream) {
	    los = (LineOutputStream) os;
	} else {
	    los = new LineOutputStream(os);
	}

	// First, write out the header
	Enumeration hdrLines = getAllHeaderLines();
	while (hdrLines.hasMoreElements())
	    los.writeln((String)hdrLines.nextElement());

	// The CRLF separator between header and content
	los.writeln();

	// Finally, the content, already encoded.
	getDataHandler().writeTo(os);
	os.flush();
    }

    /**
     * Force the <code>Content-Transfer-Encoding</code> header to use
     * the encoding that was specified when this object was created.
     */
    protected void updateHeaders() throws MessagingException {
	super.updateHeaders();
	MimeBodyPart.setEncoding(this, encoding);
    }
}
