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
 * @(#)POP3Message.java	1.18 07/05/04
 */

package com.sun.mail.pop3;

import java.io.*;
import java.util.Enumeration;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.event.*;

/**
 * A POP3 Message.  Just like a MimeMessage except that
 * some things are not supported.
 *
 * @author      Bill Shannon
 */
public class POP3Message extends MimeMessage {

    /*
     * Our locking strategy is to always lock the POP3Folder before the
     * POP3Message so we have to be careful to drop our lock before calling
     * back to the folder to close it and notify of connection lost events.
     */

    // flag to indicate we haven't tried to fetch the UID yet
    static final String UNKNOWN = "UNKNOWN";

    private POP3Folder folder;	// overrides folder in MimeMessage
    private int hdrSize = -1;
    private int msgSize = -1;
    String uid = UNKNOWN;	// controlled by folder lock

    public POP3Message(Folder folder, int msgno)
			throws MessagingException {
	super(folder, msgno);
	this.folder = (POP3Folder)folder;
    }

    /**
     * Set the specified flags on this message to the specified value.
     *
     * @param newFlags	the flags to be set
     * @param set	the value to be set
     */
    public void setFlags(Flags newFlags, boolean set)
				throws MessagingException {
	Flags oldFlags = (Flags)flags.clone();
	super.setFlags(newFlags, set);
	if (!flags.equals(oldFlags))
	    folder.notifyMessageChangedListeners(
				MessageChangedEvent.FLAGS_CHANGED, this);
    }

    /**
     * Return the size of the content of this message in bytes. 
     * Returns -1 if the size cannot be determined. <p>
     *
     * Note that this number may not be an exact measure of the
     * content size and may or may not account for any transfer
     * encoding of the content. <p>
     *
     * @return          size of content in bytes
     * @exception	MessagingException
     */  
    public int getSize() throws MessagingException {
	try {
	    synchronized (this) {
		if (msgSize >= 0)
		    return msgSize;
		if (msgSize < 0) {
		    /*
		     * Use LIST to determine the entire message
		     * size and subtract out the header size
		     * (which may involve loading the headers,
		     * which may load the content as a side effect).
		     * If the content is loaded as a side effect of
		     * loading the headers, get the size directly.
		     */
		    if (headers == null)
			loadHeaders();
		    if (contentStream != null)
			msgSize = contentStream.available();
		    else
			msgSize = folder.getProtocol().list(msgnum) - hdrSize;
		}
		return msgSize;
	    }
	} catch (EOFException eex) {
	    folder.close(false);
	    throw new FolderClosedException(folder, eex.toString());
	} catch (IOException ex) {
	    throw new MessagingException("error getting size", ex);
	}
    }

    /**
     * Produce the raw bytes of the content.  The data is fetched using
     * the POP3 RETR command.
     *
     * @see #contentStream
     */
    protected InputStream getContentStream()
					throws MessagingException {
	try {
	synchronized(this) {
	    if (contentStream == null) {
		InputStream rawcontent = folder.getProtocol().retr(msgnum,
					msgSize > 0 ? msgSize + hdrSize : 0);
		if (rawcontent == null) {
		    expunged = true;
		    throw new MessageRemovedException();    //  XXX - what else?
		}
		if (headers == null ||
			((POP3Store)(folder.getStore())).forgetTopHeaders) {
		    headers = new InternetHeaders(rawcontent);
		    hdrSize =
			(int)((SharedInputStream)rawcontent).getPosition();
		} else {
		    /*
		     * Already have the headers, have to skip the headers
		     * in the content array and return the body.
		     *
		     * XXX - It seems that some mail servers return slightly
		     * different headers in the RETR results than were returned
		     * in the TOP results, so we can't depend on remembering
		     * the size of the headers from the TOP command and just
		     * skipping that many bytes.  Instead, we have to process
		     * the content, skipping over the header until we come to
		     * the empty line that separates the header from the body.
		     */
		    int offset = 0;
		    for (;;) {
			int len = 0;	// number of bytes in this line
			int c1;
			while ((c1 = rawcontent.read()) >= 0) {
			    if (c1 == '\n')	// end of line
				break;
			    else if (c1 == '\r') {
				// got CR, is the next char LF?
				if (rawcontent.available() > 0) {
				    rawcontent.mark(1);
				    if (rawcontent.read() != '\n')
					rawcontent.reset();
				}
				break;	// in any case, end of line
			    }

			    // not CR, NL, or CRLF, count the byte
			    len++;
			}
			// here when end of line or out of data

			// if out of data, we're done
			if (rawcontent.available() == 0)
			    break;
			
			// if it was an empty line, we're done
			if (len == 0)
			    break;
		    }
		    hdrSize =
			(int)((SharedInputStream)rawcontent).getPosition();
		}
		contentStream =
		    ((SharedInputStream)rawcontent).newStream(hdrSize, -1);
		rawcontent = null;	// help GC
	    }
	}
	} catch (EOFException eex) {
	    folder.close(false);
	    throw new FolderClosedException(folder, eex.toString());
	} catch (IOException ex) {
	    throw new MessagingException("error fetching POP3 content", ex);
	}
	return super.getContentStream();
    }

    /**
     * Invalidate the cache of content for this message object, causing 
     * it to be fetched again from the server the next time it is needed.
     * If <code>invalidateHeaders</code> is true, invalidate the headers
     * as well.
     *
     * @param	invalidateHeaders	invalidate the headers as well?
     */
    public synchronized void invalidate(boolean invalidateHeaders) {
	content = null;
	contentStream = null;
	msgSize = -1;
	if (invalidateHeaders) {
	    headers = null;
	    hdrSize = -1;
	}
    }

    /**
     * Fetch the header of the message and the first <code>n</code> lines
     * of the raw content of the message.  The headers and data are
     * available in the returned InputStream.
     *
     * @param	n	number of lines of content to fetch
     * @return	InputStream containing the message headers and n content lines
     */
    public InputStream top(int n) throws MessagingException {
	try {
	    synchronized (this) {
		return folder.getProtocol().top(msgnum, n);
	    }
	} catch (EOFException eex) {
	    folder.close(false);
	    throw new FolderClosedException(folder, eex.toString());
	} catch (IOException ex) {
	    throw new MessagingException("error getting size", ex);
	}
    }

    /**
     * Get all the headers for this header_name. Note that certain
     * headers may be encoded as per RFC 2047 if they contain 
     * non US-ASCII characters and these should be decoded. <p>
     *
     * @param	name	name of header
     * @return	array of headers
     * @exception       MessagingException
     * @see 	javax.mail.internet.MimeUtility
     */
    public String[] getHeader(String name)
			throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getHeader(name);
    }

    /**
     * Get all the headers for this header name, returned as a single
     * String, with headers separated by the delimiter. If the
     * delimiter is <code>null</code>, only the first header is 
     * returned.
     *
     * @param name		the name of this header
     * @param delimiter		delimiter between returned headers
     * @return                  the value fields for all headers with 
     *				this name
     * @exception       	MessagingException
     */
    public String getHeader(String name, String delimiter)
				throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getHeader(name, delimiter);
    }

    /**
     * Set the value for this header_name.  Throws IllegalWriteException
     * because POP3 messages are read-only.
     *
     * @param	name 	header name
     * @param	value	header value
     * @see 	javax.mail.internet.MimeUtility
     * @exception	IllegalWriteException because the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this message is
     *			obtained from a READ_ONLY folder.
     */
    public void setHeader(String name, String value)
                                throws MessagingException {
	// XXX - should check for read-only folder?
	throw new IllegalWriteException("POP3 messages are read-only");
    }

    /**
     * Add this value to the existing values for this header_name.
     * Throws IllegalWriteException because POP3 messages are read-only.
     *
     * @param	name 	header name
     * @param	value	header value
     * @see 	javax.mail.internet.MimeUtility
     * @exception	IllegalWriteException because the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this message is
     *			obtained from a READ_ONLY folder.
     */
    public void addHeader(String name, String value)
                                throws MessagingException {
	// XXX - should check for read-only folder?
	throw new IllegalWriteException("POP3 messages are read-only");
    }

    /**
     * Remove all headers with this name.
     * Throws IllegalWriteException because POP3 messages are read-only.
     *
     * @exception	IllegalWriteException because the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this message is
     *			obtained from a READ_ONLY folder.
     */
    public void removeHeader(String name)
                                throws MessagingException {
	// XXX - should check for read-only folder?
	throw new IllegalWriteException("POP3 messages are read-only");
    }

    /**
     * Return all the headers from this Message as an enumeration
     * of Header objects. <p>
     *
     * Note that certain headers may be encoded as per RFC 2047 
     * if they contain non US-ASCII characters and these should 
     * be decoded. <p>
     *
     * @return	array of header objects
     * @exception  MessagingException
     * @see 	javax.mail.internet.MimeUtility
     */
    public Enumeration getAllHeaders() throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getAllHeaders();	
    }

    /**
     * Return matching headers from this Message as an Enumeration of
     * Header objects.
     *
     * @exception  MessagingException
     */
    public Enumeration getMatchingHeaders(String[] names)
			throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getMatchingHeaders(names);
    }

    /**
     * Return non-matching headers from this Message as an
     * Enumeration of Header objects.
     *
     * @exception  MessagingException
     */
    public Enumeration getNonMatchingHeaders(String[] names)
			throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getNonMatchingHeaders(names);
    }

    /**
     * Add a raw RFC822 header-line. 
     * Throws IllegalWriteException because POP3 messages are read-only.
     *
     * @exception	IllegalWriteException because the underlying
     *			implementation does not support modification
     * @exception	IllegalStateException if this message is
     *			obtained from a READ_ONLY folder.
     */
    public void addHeaderLine(String line) throws MessagingException {
	// XXX - should check for read-only folder?
	throw new IllegalWriteException("POP3 messages are read-only");
    }

    /**
     * Get all header lines as an Enumeration of Strings. A Header
     * line is a raw RFC822 header-line, containing both the "name" 
     * and "value" field. 
     *
     * @exception  	MessagingException
     */
    public Enumeration getAllHeaderLines() throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getAllHeaderLines();
    }

    /**
     * Get matching header lines as an Enumeration of Strings. 
     * A Header line is a raw RFC822 header-line, containing both 
     * the "name" and "value" field.
     *
     * @exception  	MessagingException
     */
    public Enumeration getMatchingHeaderLines(String[] names)
                                        throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getMatchingHeaderLines(names);
    }

    /**
     * Get non-matching header lines as an Enumeration of Strings. 
     * A Header line is a raw RFC822 header-line, containing both 
     * the "name" and "value" field.
     *
     * @exception  	MessagingException
     */
    public Enumeration getNonMatchingHeaderLines(String[] names)
                                        throws MessagingException {
	if (headers == null)
	    loadHeaders();
	return headers.getNonMatchingHeaderLines(names);
    }

    /**
     * POP3 message can't be changed.  This method throws
     * IllegalWriteException.
     *
     * @exception	IllegalWriteException because the underlying
     *			implementation does not support modification
     */
    public void saveChanges() throws MessagingException {
	// POP3 Messages are read-only
	throw new IllegalWriteException("POP3 messages are read-only");
    }

    /**
     * Load the headers for this message into the InternetHeaders object.
     * The headers are fetched using the POP3 TOP command.
     */
    private void loadHeaders() throws MessagingException {
	try {
	    synchronized (this) {
		if (headers != null)    // check again under lock
		    return;
		InputStream hdrs = null;
		if (((POP3Store)(folder.getStore())).disableTop ||
			(hdrs = folder.getProtocol().top(msgnum, 0)) == null) {
		    // possibly because the TOP command isn't supported,
		    // load headers as a side effect of loading the entire
		    // content.
		    InputStream cs = getContentStream();
		    cs.close();
		} else {
		    hdrSize = hdrs.available();
		    headers = new InternetHeaders(hdrs);
		}
	    }
	} catch (EOFException eex) {
	    folder.close(false);
	    throw new FolderClosedException(folder, eex.toString());
	} catch (IOException ex) {
	    throw new MessagingException("error loading POP3 headers", ex);
	}
    }
}
