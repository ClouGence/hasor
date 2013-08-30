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
 * @(#)InternetHeaders.java	1.22 07/05/04
 */

package javax.mail.internet;

import java.io.*;
import java.util.*;
import javax.mail.*;
import com.sun.mail.util.LineInputStream;

/**
 * InternetHeaders is a utility class that manages RFC822 style
 * headers. Given an RFC822 format message stream, it reads lines
 * until the blank line that indicates end of header. The input stream
 * is positioned at the start of the body. The lines are stored 
 * within the object and can be extracted as either Strings or
 * {@link javax.mail.Header} objects. <p>
 *
 * This class is mostly intended for service providers. MimeMessage
 * and MimeBody use this class for holding their headers. <p>
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
 * @see	javax.mail.internet.MimeUtility
 * @author John Mani
 * @author Bill Shannon
 */

public class InternetHeaders {
    /**
     * An individual internet header.  This class is only used by
     * subclasses of InternetHeaders. <p>
     *
     * An InternetHeader object with a null value is used as a placeholder
     * for headers of that name, to preserve the order of headers.
     * A placeholder InternetHeader object with a name of ":" marks
     * the location in the list of headers where new headers are
     * added by default.
     *
     * @since	JavaMail 1.4
     */
    protected static final class InternetHeader extends Header {
	/*
	 * Note that the value field from the superclass
	 * isn't used in this class.  We extract the value
	 * from the line field as needed.  We store the line
	 * rather than just the value to ensure that we can
	 * get back the exact original line, with the original
	 * whitespace, etc.
	 */
	String line;    // the entire RFC822 header "line",
			// or null if placeholder

	/**
	 * Constructor that takes a line and splits out
	 * the header name.
	 */
	public InternetHeader(String l) {
	    super("", "");	// XXX - we'll change it later
	    int i = l.indexOf(':');
	    if (i < 0) {
		// should never happen
		name = l.trim();
	    } else {
		name = l.substring(0, i).trim();
	    }
	    line = l;
	}

	/**
	 * Constructor that takes a header name and value.
	 */
	public InternetHeader(String n, String v) {
	    super(n, "");
	    if (v != null)
		line = n + ": " + v;
	    else
		line = null;
	}

	/**
	 * Return the "value" part of the header line.
	 */
	public String getValue() {
	    int i = line.indexOf(':');
	    if (i < 0)
		return line;
	    // skip whitespace after ':'
	    int j;
	    for (j = i + 1; j < line.length(); j++) {
		char c = line.charAt(j);
		if (!(c == ' ' || c == '\t' || c == '\r' || c == '\n'))
		    break;
	    }
	    return line.substring(j);
	}
    }

    /*
     * The enumeration object used to enumerate an
     * InternetHeaders object.  Can return
     * either a String or a Header object.
     */
    static class matchEnum implements Enumeration {
	private Iterator e;	// enum object of headers List
	// XXX - is this overkill?  should we step through in index
	// order instead?
	private String names[];	// names to match, or not
	private boolean match;	// return matching headers?
	private boolean want_line;	// return header lines?
	private InternetHeader next_header; // the next header to be returned

	/*
	 * Constructor.  Initialize the enumeration for the entire
	 * List of headers, the set of headers, whether to return
	 * matching or non-matching headers, and whether to return
	 * header lines or Header objects.
	 */
	matchEnum(List v, String n[], boolean m, boolean l) {
	    e = v.iterator();
	    names = n;
	    match = m;
	    want_line = l;
	    next_header = null;
	}

	/*
	 * Any more elements in this enumeration?
	 */
	public boolean hasMoreElements() {
	    // if necessary, prefetch the next matching header,
	    // and remember it.
	    if (next_header == null)
		next_header = nextMatch();
	    return next_header != null;
	}

	/*
	 * Return the next element.
	 */
	public Object nextElement() {
	    if (next_header == null)
		next_header = nextMatch();

	    if (next_header == null)
		throw new NoSuchElementException("No more headers");

	    InternetHeader h = next_header;
	    next_header = null;
	    if (want_line)
		return h.line;
	    else
		return new Header(h.getName(), h.getValue());
	}

	/*
	 * Return the next Header object according to the match
	 * criteria, or null if none left.
	 */
	private InternetHeader nextMatch() {
	    next:
	    while (e.hasNext()) {
		InternetHeader h = (InternetHeader)e.next();

		// skip "place holder" headers
		if (h.line == null)
		    continue;

		// if no names to match against, return appropriately
		if (names == null)
		    return match ? null : h;

		// check whether this header matches any of the names
		for (int i = 0; i < names.length; i++) {
		    if (names[i].equalsIgnoreCase(h.getName())) {
			if (match)
			    return h;
			else
			    // found a match, but we're
			    // looking for non-matches.
			    // try next header.
			    continue next;
		    }
		}
		// found no matches.  if that's what we wanted, return it.
		if (!match)
		    return h;
	    }
	    return null;
	}
    }


    /**
     * The actual list of Headers, including placeholder entries.
     * Placeholder entries are Headers with a null value and
     * are never seen by clients of the InternetHeaders class.
     * Placeholder entries are used to keep track of the preferred
     * order of headers.  Headers are never actually removed from
     * the list, they're converted into placeholder entries.
     * New headers are added after existing headers of the same name
     * (or before in the case of <code>Received</code> and
     * <code>Return-Path</code> headers).  If no existing header
     * or placeholder for the header is found, new headers are
     * added after the special placeholder with the name ":".
     *
     * @since	JavaMail 1.4
     */
    protected List headers;

    /**
     * Create an empty InternetHeaders object.  Placeholder entries
     * are inserted to indicate the preferred order of headers.
     */
    public InternetHeaders() { 
   	headers = new ArrayList(40); 
	headers.add(new InternetHeader("Return-Path", null));
	headers.add(new InternetHeader("Received", null));
	headers.add(new InternetHeader("Resent-Date", null));
	headers.add(new InternetHeader("Resent-From", null));
	headers.add(new InternetHeader("Resent-Sender", null));
	headers.add(new InternetHeader("Resent-To", null));
	headers.add(new InternetHeader("Resent-Cc", null));
	headers.add(new InternetHeader("Resent-Bcc", null));
	headers.add(new InternetHeader("Resent-Message-Id", null));
	headers.add(new InternetHeader("Date", null));
	headers.add(new InternetHeader("From", null));
	headers.add(new InternetHeader("Sender", null));
	headers.add(new InternetHeader("Reply-To", null));
	headers.add(new InternetHeader("To", null));
	headers.add(new InternetHeader("Cc", null));
	headers.add(new InternetHeader("Bcc", null));
	headers.add(new InternetHeader("Message-Id", null));
	headers.add(new InternetHeader("In-Reply-To", null));
	headers.add(new InternetHeader("References", null));
	headers.add(new InternetHeader("Subject", null));
	headers.add(new InternetHeader("Comments", null));
	headers.add(new InternetHeader("Keywords", null));
	headers.add(new InternetHeader("Errors-To", null));
	headers.add(new InternetHeader("MIME-Version", null));
	headers.add(new InternetHeader("Content-Type", null));
	headers.add(new InternetHeader("Content-Transfer-Encoding", null));
	headers.add(new InternetHeader("Content-MD5", null));
	headers.add(new InternetHeader(":", null));
	headers.add(new InternetHeader("Content-Length", null));
	headers.add(new InternetHeader("Status", null));
    }

    /**
     * Read and parse the given RFC822 message stream till the 
     * blank line separating the header from the body. The input 
     * stream is left positioned at the start of the body. The 
     * header lines are stored internally. <p>
     *
     * For efficiency, wrap a BufferedInputStream around the actual
     * input stream and pass it as the parameter. <p>
     *
     * No placeholder entries are inserted; the original order of
     * the headers is preserved.
     *
     * @param	is 	RFC822 input stream
     */
    public InternetHeaders(InputStream is) throws MessagingException {
   	headers = new ArrayList(40); 
	load(is);
    }

    /**
     * Read and parse the given RFC822 message stream till the
     * blank line separating the header from the body. Store the
     * header lines inside this InternetHeaders object. The order
     * of header lines is preserved. <p>
     *
     * Note that the header lines are added into this InternetHeaders
     * object, so any existing headers in this object will not be
     * affected.  Headers are added to the end of the existing list
     * of headers, in order.
     *
     * @param	is 	RFC822 input stream
     */
    public void load(InputStream is) throws MessagingException {
	// Read header lines until a blank line. It is valid
	// to have BodyParts with no header lines.
	String line;
	LineInputStream lis = new LineInputStream(is);
	String prevline = null;	// the previous header line, as a string
	// a buffer to accumulate the header in, when we know it's needed
	StringBuffer lineBuffer = new StringBuffer();

	try {
	    //while ((line = lis.readLine()) != null) {
	    do {
		line = lis.readLine();
		if (line != null &&
			(line.startsWith(" ") || line.startsWith("\t"))) {
		    // continuation of header
		    if (prevline != null) {
			lineBuffer.append(prevline);
			prevline = null;
		    }
		    lineBuffer.append("\r\n");
		    lineBuffer.append(line);
		} else {
		    // new header
		    if (prevline != null)
			addHeaderLine(prevline);
		    else if (lineBuffer.length() > 0) {
			// store previous header first
			addHeaderLine(lineBuffer.toString());
			lineBuffer.setLength(0);
		    }
		    prevline = line;
		}
	    } while (line != null && line.length() > 0);
	} catch (IOException ioex) {
	    throw new MessagingException("Error in input stream", ioex);
	}
    }

    /**
     * Return all the values for the specified header. The
     * values are String objects.  Returns <code>null</code>
     * if no headers with the specified name exist.
     *
     * @param	name 	header name
     * @return		array of header values, or null if none
     */
    public String[] getHeader(String name) {
	Iterator e = headers.iterator();
	// XXX - should we just step through in index order?
	List v = new ArrayList(); // accumulate return values

	while (e.hasNext()) {
	    InternetHeader h = (InternetHeader)e.next();
	    if (name.equalsIgnoreCase(h.getName()) && h.line != null) {
		v.add(h.getValue());
	    }
	}
	if (v.size() == 0)
	    return (null);
	// convert List to an array for return
	String r[] = new String[v.size()];
	r = (String[])v.toArray(r);
	return (r);
    }

    /**
     * Get all the headers for this header name, returned as a single
     * String, with headers separated by the delimiter. If the
     * delimiter is <code>null</code>, only the first header is 
     * returned.  Returns <code>null</code>
     * if no headers with the specified name exist.
     *
     * @param	name 		header name
     * @param   delimiter	delimiter
     * @return                  the value fields for all headers with
     *				this name, or null if none
     */
    public String getHeader(String name, String delimiter) {
	String s[] = getHeader(name);

	if (s == null)
	    return null;
	
	if ((s.length == 1) || delimiter == null)
	    return s[0];
	
	StringBuffer r = new StringBuffer(s[0]);
	for (int i = 1; i < s.length; i++) {
	    r.append(delimiter);
	    r.append(s[i]);
	}
	return r.toString();
    }

    /**
     * Change the first header line that matches name
     * to have value, adding a new header if no existing header
     * matches. Remove all matching headers but the first. <p>
     *
     * Note that RFC822 headers can only contain US-ASCII characters
     *
     * @param	name	header name
     * @param	value	header value
     */
    public void setHeader(String name, String value) {
	boolean found = false;

	for (int i = 0; i < headers.size(); i++) {
	    InternetHeader h = (InternetHeader)headers.get(i);
	    if (name.equalsIgnoreCase(h.getName())) {
		if (!found) {
		    int j;
		    if (h.line != null && (j = h.line.indexOf(':')) >= 0) {
			h.line = h.line.substring(0, j + 1) + " " + value;
			// preserves capitalization, spacing
		    } else {
			h.line = name + ": " + value;
		    }
		    found = true;
		} else {
		    headers.remove(i);
		    i--;    // have to look at i again
		}
	    }
	}
    
	if (!found) {
	    addHeader(name, value);
	}
    }

    /**
     * Add a header with the specified name and value to the header list. <p>
     *
     * The current implementation knows about the preferred order of most
     * well-known headers and will insert headers in that order.  In
     * addition, it knows that <code>Received</code> headers should be
     * inserted in reverse order (newest before oldest), and that they
     * should appear at the beginning of the headers, preceeded only by
     * a possible <code>Return-Path</code> header.  <p>
     *
     * Note that RFC822 headers can only contain US-ASCII characters.
     *
     * @param	name	header name
     * @param	value	header value
     */ 
    public void addHeader(String name, String value) {
	int pos = headers.size();
	boolean addReverse =
	    name.equalsIgnoreCase("Received") ||
	    name.equalsIgnoreCase("Return-Path");
	if (addReverse)
	    pos = 0;
	for (int i = headers.size() - 1; i >= 0; i--) {
	    InternetHeader h = (InternetHeader)headers.get(i);
	    if (name.equalsIgnoreCase(h.getName())) {
		if (addReverse) {
		    pos = i;
		} else {
		    headers.add(i + 1, new InternetHeader(name, value));
		    return;
		}
	    }
	    // marker for default place to add new headers
	    if (h.getName().equals(":"))
		pos = i;
	}
	headers.add(pos, new InternetHeader(name, value));
    }

    /**
     * Remove all header entries that match the given name
     * @param	name 	header name
     */
    public void removeHeader(String name) { 
	for (int i = 0; i < headers.size(); i++) {
	    InternetHeader h = (InternetHeader)headers.get(i);
	    if (name.equalsIgnoreCase(h.getName())) {
		h.line = null;
		//headers.remove(i);
		//i--;    // have to look at i again
	    }
	}
    }

    /**
     * Return all the headers as an Enumeration of
     * {@link javax.mail.Header} objects.
     *
     * @return	Header objects	
     */
    public Enumeration getAllHeaders() {
	return (new matchEnum(headers, null, false, false));
    }

    /**
     * Return all matching {@link javax.mail.Header} objects.
     *
     * @return	matching Header objects	
     */
    public Enumeration getMatchingHeaders(String[] names) {
	return (new matchEnum(headers, names, true, false));
    }

    /**
     * Return all non-matching {@link javax.mail.Header} objects.
     *
     * @return	non-matching Header objects	
     */
    public Enumeration getNonMatchingHeaders(String[] names) {
	return (new matchEnum(headers, names, false, false));
    }

    /**
     * Add an RFC822 header line to the header store.
     * If the line starts with a space or tab (a continuation line),
     * add it to the last header line in the list.  Otherwise,
     * append the new header line to the list.  <p>
     *
     * Note that RFC822 headers can only contain US-ASCII characters
     *
     * @param	line	raw RFC822 header line
     */
    public void addHeaderLine(String line) {
	try {
	    char c = line.charAt(0);
	    if (c == ' ' || c == '\t') {
		InternetHeader h =
		    (InternetHeader)headers.get(headers.size() - 1);
		h.line += "\r\n" + line;
	    } else
		headers.add(new InternetHeader(line));
	} catch (StringIndexOutOfBoundsException e) {
	    // line is empty, ignore it
	    return;
	} catch (NoSuchElementException e) {
	    // XXX - vector is empty?
	}
    }

    /**
     * Return all the header lines as an Enumeration of Strings.
     */
    public Enumeration getAllHeaderLines() { 
	return (getNonMatchingHeaderLines(null));
    }

    /**
     * Return all matching header lines as an Enumeration of Strings.
     */
    public Enumeration getMatchingHeaderLines(String[] names) {
	return (new matchEnum(headers, names, true, true));	
    }

    /**
     * Return all non-matching header lines
     */
    public Enumeration getNonMatchingHeaderLines(String[] names) {
	return (new matchEnum(headers, names, false, true));
    }
}
