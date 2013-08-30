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
 * @(#)MimePartDataSource.java	1.14 07/05/04
 */

package javax.mail.internet;

import javax.mail.*;
import javax.activation.*;
import java.io.*;
import java.net.UnknownServiceException;

/**
 * A utility class that implements a DataSource out of
 * a MimePart. This class is primarily meant for service providers.
 *
 * @see		javax.mail.internet.MimePart
 * @see		javax.activation.DataSource
 * @author 	John Mani
 */

public class MimePartDataSource implements DataSource, MessageAware {
    /**
     * The MimePart that provides the data for this DataSource.
     *
     * @since	JavaMail 1.4
     */
    protected MimePart part;

    private MessageContext context;

    private static boolean ignoreMultipartEncoding = true;

    static {
	try {
	    String s = System.getProperty("mail.mime.ignoremultipartencoding");
	    // default to true
	    ignoreMultipartEncoding = s == null || !s.equalsIgnoreCase("false");
	} catch (SecurityException sex) {
	    // ignore it
	}
    }

    /**
     * Constructor, that constructs a DataSource from a MimePart.
     */
    public MimePartDataSource(MimePart part) {
	this.part = part;
    }

    /**
     * Returns an input stream from this  MimePart. <p>
     *
     * This method applies the appropriate transfer-decoding, based 
     * on the Content-Transfer-Encoding attribute of this MimePart.
     * Thus the returned input stream is a decoded stream of bytes.<p>
     *
     * This implementation obtains the raw content from the Part
     * using the <code>getContentStream()</code> method and decodes
     * it using the <code>MimeUtility.decode()</code> method.
     *
     * @see	javax.mail.internet.MimeMessage#getContentStream
     * @see	javax.mail.internet.MimeBodyPart#getContentStream
     * @see	javax.mail.internet.MimeUtility#decode
     * @return 	decoded input stream
     */
    public InputStream getInputStream() throws IOException {
	InputStream is;

	try {
	    if (part instanceof MimeBodyPart)
		is = ((MimeBodyPart)part).getContentStream();
	    else if (part instanceof MimeMessage)
		is = ((MimeMessage)part).getContentStream();
	    else
		throw new MessagingException("Unknown part");
	    
	    String encoding = restrictEncoding(part.getEncoding(), part);
	    if (encoding != null)
		return MimeUtility.decode(is, encoding);
	    else
		return is;
	} catch (MessagingException mex) {
	    throw new IOException(mex.getMessage());
	}
    }

    /**
     * Restrict the encoding to values allowed for the
     * Content-Type of the specified MimePart.  Returns
     * either the original encoding or null.
     */
    private static String restrictEncoding(String encoding, MimePart part)
				throws MessagingException {
	if (!ignoreMultipartEncoding || encoding == null)
	    return encoding;

	if (encoding.equalsIgnoreCase("7bit") ||
		encoding.equalsIgnoreCase("8bit") ||
		encoding.equalsIgnoreCase("binary"))
	    return encoding;	// these encodings are always valid

	String type = part.getContentType();
	if (type == null)
	    return encoding;

	try {
	    /*
	     * multipart and message types aren't allowed to have
	     * encodings except for the three mentioned above.
	     * If it's one of these types, ignore the encoding.
	     */
	    ContentType cType = new ContentType(type);
	    if (cType.match("multipart/*") || cType.match("message/*"))
		return null;
	} catch (ParseException pex) {
	    // ignore it
	}
	return encoding;
    }


    /**
     * DataSource method to return an output stream. <p>
     *
     * This implementation throws the UnknownServiceException.
     */
    public OutputStream getOutputStream() throws IOException {
	throw new UnknownServiceException();
    }

    /**
     * Returns the content-type of this DataSource. <p>
     *
     * This implementation just invokes the <code>getContentType</code>
     * method on the MimePart.
     */
    public String getContentType() {
	try {
	    return part.getContentType();
	} catch (MessagingException mex) {
	    // would like to be able to reflect the exception to the
	    // application, but since we can't do that we return a
	    // generic "unknown" value here and hope for another
	    // exception later.
	    return "application/octet-stream";
	}
    }

    /**
     * DataSource method to return a name.  <p>
     *
     * This implementation just returns an empty string.
     */
    public String getName() {
	try {
	    if (part instanceof MimeBodyPart)
		return ((MimeBodyPart)part).getFileName();
	} catch (MessagingException mex) {
	    // ignore it
	}
	return "";
    }

    /**
     * Return the <code>MessageContext</code> for the current part.
     * @since JavaMail 1.1
     */
    public synchronized MessageContext getMessageContext() {
	if (context == null)
	    context = new MessageContext(part);
	return context;
    }
}
