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
 * @(#)MimePart.java	1.17 07/05/04
 */

package javax.mail.internet;

import javax.mail.*;
import java.io.*;
import java.util.Enumeration;

/**
 * The MimePart interface models an <strong>Entity</strong> as defined
 * by MIME (RFC2045, Section 2.4). <p>
 *
 * MimePart extends the Part interface to add additional RFC822 and MIME
 * specific semantics and attributes. It provides the base interface for
 * the MimeMessage and  MimeBodyPart classes 
 * 
 * <hr> <strong>A note on RFC822 and MIME headers</strong><p>
 *
 * RFC822 and MIME header fields <strong>must</strong> contain only 
 * US-ASCII characters. If a header contains non US-ASCII characters,
 * it must be encoded as per the rules in RFC 2047. The MimeUtility
 * class provided in this package can be used to to achieve this. 
 * Callers of the <code>setHeader</code>, <code>addHeader</code>, and
 * <code>addHeaderLine</code> methods are responsible for enforcing
 * the MIME requirements for the specified headers.  In addition, these
 * header fields must be folded (wrapped) before being sent if they
 * exceed the line length limitation for the transport (1000 bytes for
 * SMTP).  Received headers may have been folded.  The application is
 * responsible for folding and unfolding headers as appropriate. <p>
 *
 * @see		MimeUtility
 * @see		javax.mail.Part
 * @author 	John Mani
 */

public interface MimePart extends Part {

    /**
     * Get the values of all header fields available for this header,
     * returned as a single String, with the values separated by the 
     * delimiter. If the delimiter is <code>null</code>, only the 
     * first value is returned.
     *
     * @param name		the name of this header
     * @param delimiter		delimiter between fields in returned string
     * @return                  the value fields for all headers with 
     *				this name
     * @exception       	MessagingException
     */
    public String getHeader(String name, String delimiter)
				throws MessagingException;

    /**
     * Add a raw RFC822 header-line. 
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this Part is
     *			obtained from a READ_ONLY folder
     */
    public void addHeaderLine(String line) throws MessagingException;

    /**
     * Get all header lines as an Enumeration of Strings. A Header
     * line is a raw RFC822 header-line, containing both the "name" 
     * and "value" field. 
     */
    public Enumeration getAllHeaderLines() throws MessagingException;

    /**
     * Get matching header lines as an Enumeration of Strings. 
     * A Header line is a raw RFC822 header-line, containing both 
     * the "name" and "value" field.
     */
    public Enumeration getMatchingHeaderLines(String[] names)
			throws MessagingException;

    /**
     * Get non-matching header lines as an Enumeration of Strings. 
     * A Header line is a raw RFC822 header-line, containing both 
     * the "name"  and "value" field.
     */
    public Enumeration getNonMatchingHeaderLines(String[] names)
			throws MessagingException;

    /**
     * Get the transfer encoding of this part.
     *
     * @return		content-transfer-encoding
     * @exception	MessagingException
     */
    public String getEncoding() throws MessagingException;

    /**
     * Get the Content-ID of this part. Returns null if none present.
     *
     * @return		content-ID
     */
    public String getContentID() throws MessagingException;

    /**
     * Get the Content-MD5 digest of this part. Returns null if
     * none present.
     *
     * @return		content-MD5
     */
    public String getContentMD5() throws MessagingException;

    /**
     * Set the Content-MD5 of this part.
     *
     * @param  md5	the MD5 value
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this Part is
     *			obtained from a READ_ONLY folder
     */
    public void setContentMD5(String md5) throws MessagingException;

    /**
     * Get the language tags specified in the Content-Language header
     * of this MimePart. The Content-Language header is defined by
     * RFC 1766. Returns <code>null</code> if this header is not
     * available.
     */
    public String[] getContentLanguage() throws MessagingException;

    /**
     * Set the Content-Language header of this MimePart. The
     * Content-Language header is defined by RFC1766.
     *
     * @param languages	array of language tags
     * @exception	IllegalWriteException if the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this Part is
     *			obtained from a READ_ONLY folder
     */
    public void setContentLanguage(String[] languages)
			throws MessagingException;
    
    /**
     * Convenience method that sets the given String as this
     * part's content, with a MIME type of "text/plain". If the
     * string contains non US-ASCII characters. it will be encoded
     * using the platform's default charset. The charset is also
     * used to set the "charset" parameter. <p>
     *
     * Note that there may be a performance penalty if
     * <code>text</code> is large, since this method may have
     * to scan all the characters to determine what charset to
     * use. <p>
     *
     * If the charset is already known, use the
     * <code>setText</code> method that takes the charset parameter.
     *
     * @param	text	the text content to set
     * @exception	MessagingException	if an error occurs
     * @see	#setText(String text, String charset)
     */
    public void setText(String text) throws MessagingException;

    /**
     * Convenience method that sets the given String as this part's
     * content, with a MIME type of "text/plain" and the specified
     * charset. The given Unicode string will be charset-encoded
     * using the specified charset. The charset is also used to set
     * "charset" parameter.
     *
     * @param	text	the text content to set
     * @param	charset	the charset to use for the text
     * @exception	MessagingException	if an error occurs
     */
    public void setText(String text, String charset)
			throws MessagingException;

    /**
     * Convenience method that sets the given String as this part's
     * content, with a primary MIME type of "text" and the specified
     * MIME subtype.  The given Unicode string will be charset-encoded
     * using the specified charset. The charset is also used to set
     * the "charset" parameter.
     *
     * @param	text	the text content to set
     * @param	charset	the charset to use for the text
     * @param	subtype	the MIME subtype to use (e.g., "html")
     * @exception	MessagingException	if an error occurs
     * @since	JavaMail 1.4
     */
    public void setText(String text, String charset, String subtype)
                        throws MessagingException;
}
