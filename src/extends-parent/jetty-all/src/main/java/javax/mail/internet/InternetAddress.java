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
 * @(#)InternetAddress.java	1.49 07/05/04
 */

package javax.mail.internet;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Locale;
import javax.mail.*;

/**
 * This class represents an Internet email address using the syntax
 * of <a href="http://www.ietf.org/rfc/rfc822.txt">RFC822</a>.
 * Typical address syntax is of the form "user@host.domain" or
 * "Personal Name <user@host.domain>".
 *
 * @author Bill Shannon
 * @author John Mani
 */

public class InternetAddress extends Address implements Cloneable {

    protected String address; // email address

    /**
     * The personal name.
     */
    protected String personal;

    /**
     * The RFC 2047 encoded version of the personal name. <p>
     *
     * This field and the <code>personal</code> field track each
     * other, so if a subclass sets one of these fields directly, it
     * should set the other to <code>null</code>, so that it is
     * suitably recomputed.
     */
    protected String encodedPersonal;

    private static final long serialVersionUID = -7507595530758302903L;

    /**
     * Default constructor.
     */
    public InternetAddress() { }

    /**
     * Constructor. <p>
     *
     * Parse the given string and create an InternetAddress.
     * See the <code>parse</code> method for details of the parsing.
     * The address is parsed using "strict" parsing.
     * This constructor does <b>not</b> perform the additional
     * syntax checks that the
     * <code>InternetAddress(String address, boolean strict)</code>
     * constructor does when <code>strict</code> is <code>true</code>.
     * This constructor is equivalent to
     * <code>InternetAddress(address, false)</code>.
     *
     * @param address	the address in RFC822 format
     * @exception	AddressException if the parse failed
     */
    public InternetAddress(String address) throws AddressException {
	// use our address parsing utility routine to parse the string
	InternetAddress a[] = parse(address, true);
	// if we got back anything other than a single address, it's an error
	if (a.length != 1)
	    throw new AddressException("Illegal address", address);

	/*
	 * Now copy the contents of the single address we parsed
	 * into the current object, which will be returned from the
	 * constructor.
	 * XXX - this sure is a round-about way of getting this done.
	 */
	this.address = a[0].address;
	this.personal = a[0].personal;
	this.encodedPersonal = a[0].encodedPersonal;
    }

    /**
     * Parse the given string and create an InternetAddress.
     * If <code>strict</code> is false, the detailed syntax of the
     * address isn't checked.
     *
     * @param	address		the address in RFC822 format
     * @param	strict		enforce RFC822 syntax
     * @exception		AddressException if the parse failed
     * @since			JavaMail 1.3
     */
    public InternetAddress(String address, boolean strict)
						throws AddressException {
	this(address);
	if (strict)
	    checkAddress(this.address, true, true);
    }

    /**
     * Construct an InternetAddress given the address and personal name.
     * The address is assumed to be a syntactically valid RFC822 address.
     *
     * @param address	the address in RFC822 format
     * @param personal	the personal name
     */
    public InternetAddress(String address, String personal)
				throws UnsupportedEncodingException {
	this(address, personal, null);
    }

    /**
     * Construct an InternetAddress given the address and personal name.
     * The address is assumed to be a syntactically valid RFC822 address.
     *
     * @param address	the address in RFC822 format
     * @param personal	the personal name
     * @param charset	the MIME charset for the name
     */
    public InternetAddress(String address, String personal, String charset)
				throws UnsupportedEncodingException {
	this.address = address;
	setPersonal(personal, charset);
    }

    /**
     * Return a copy of this InternetAddress object.
     * @since		JavaMail 1.2
     */
    public Object clone() {
	InternetAddress a = null;
	try {
	    a = (InternetAddress)super.clone();
	} catch (CloneNotSupportedException e) {} // Won't happen
	return a;
    }

    /**
     * Return the type of this address. The type of an InternetAddress
     * is "rfc822".
     */
    public String getType() {
	return "rfc822";
    }

    /**
     * Set the email address.
     *
     * @param	address email address
     */
    public void setAddress(String address) {
	this.address = address;
    }

    /**
     * Set the personal name. If the name contains non US-ASCII
     * characters, then the name will be encoded using the specified
     * charset as per RFC 2047. If the name contains only US-ASCII
     * characters, no encoding is done and the name is used as is. <p>
     *
     * @param	name 	personal name
     * @param	charset	MIME charset to be used to encode the name as 
     *			per RFC 2047
     * @see 	#setPersonal(String)
     * @exception UnsupportedEncodingException if the charset encoding
     *		  fails.
     */
    public void setPersonal(String name, String charset)
				throws UnsupportedEncodingException {
	personal = name;
	if (name != null)
	    encodedPersonal = MimeUtility.encodeWord(name, charset, null);
	else
	    encodedPersonal = null;
    }

    /**
     * Set the personal name. If the name contains non US-ASCII
     * characters, then the name will be encoded using the platform's 
     * default charset. If the name contains only US-ASCII characters,
     * no encoding is done and the name is used as is. <p>
     *
     * @param	name 	personal name
     * @see 	#setPersonal(String name, String charset)
     * @exception UnsupportedEncodingException if the charset encoding
     *		  fails.
     */
    public void setPersonal(String name) 
		throws UnsupportedEncodingException {
	personal = name;
	if (name != null)
	    encodedPersonal = MimeUtility.encodeWord(name);
	else
	    encodedPersonal = null;
    }

    /**
     * Get the email address.
     * @return	email address
     */
    public String getAddress() {
	return address;
    }

    /**
     * Get the personal name. If the name is encoded as per RFC 2047,
     * it is decoded and converted into Unicode. If the decoding or
     * conversion fails, the raw data is returned as is.
     *
     * @return	personal name
     */
    public String getPersonal() {
	if (personal != null)
	    return personal;
	
	if (encodedPersonal != null) {
	    try {
		personal = MimeUtility.decodeText(encodedPersonal);
		return personal;
	    } catch (Exception ex) {
		// 1. ParseException: either its an unencoded string or
		//	it can't be parsed
		// 2. UnsupportedEncodingException: can't decode it.
		return encodedPersonal;
	    }
	}
	// No personal or encodedPersonal, return null
	return null;
    }

    /**
     * Convert this address into a RFC 822 / RFC 2047 encoded address.
     * The resulting string contains only US-ASCII characters, and
     * hence is mail-safe.
     *
     * @return		possibly encoded address string
     */
    public String toString() {
	if (encodedPersonal == null && personal != null)
	    try {
		encodedPersonal = MimeUtility.encodeWord(personal);
	    } catch (UnsupportedEncodingException ex) { }
	
	if (encodedPersonal != null)
	    return quotePhrase(encodedPersonal) + " <" + address + ">";
	else if (isGroup() || isSimple())
	    return address;
	else
	    return "<" + address + ">";
    }

    /**
     * Returns a properly formatted address (RFC 822 syntax) of
     * Unicode characters.
     *   
     * @return          Unicode address string
     * @since           JavaMail 1.2
     */  
    public String toUnicodeString() {
	String p = getPersonal();
        if (p != null)
            return quotePhrase(p) + " <" + address + ">";
        else if (isGroup() || isSimple())
            return address;
        else
            return "<" + address + ">";
    }

    /*
     * quotePhrase() quotes the words within a RFC822 phrase.
     *
     * This is tricky, since a phrase is defined as 1 or more
     * RFC822 words, separated by LWSP. Now, a word that contains
     * LWSP is supposed to be quoted, and this is exactly what the 
     * MimeUtility.quote() method does. However, when dealing with
     * a phrase, any LWSP encountered can be construed to be the
     * separator between words, and not part of the words themselves.
     * To deal with this funkiness, we have the below variant of
     * MimeUtility.quote(), which essentially ignores LWSP when
     * deciding whether to quote a word.
     *
     * It aint pretty, but it gets the job done :)
     */

    private static final String rfc822phrase =
	HeaderTokenizer.RFC822.replace(' ', '\0').replace('\t', '\0');

    private static String quotePhrase(String phrase) {
        int len = phrase.length();
        boolean needQuoting = false;

        for (int i = 0; i < len; i++) {
            char c = phrase.charAt(i);
            if (c == '"' || c == '\\') { 
                // need to escape them and then quote the whole string
                StringBuffer sb = new StringBuffer(len + 3);
                sb.append('"');
                for (int j = 0; j < len; j++) {
                    char cc = phrase.charAt(j);
                    if (cc == '"' || cc == '\\')
                        // Escape the character
                        sb.append('\\');
                    sb.append(cc);
                }
                sb.append('"');
                return sb.toString();
            } else if ((c < 040 && c != '\r' && c != '\n' && c != '\t') || 
			c >= 0177 || rfc822phrase.indexOf(c) >= 0)
               // These characters cause the string to be quoted
                needQuoting = true;
        }

        if (needQuoting) {
            StringBuffer sb = new StringBuffer(len + 2);
            sb.append('"').append(phrase).append('"');
            return sb.toString();
        } else
            return phrase;
    }

    private static String unquote(String s) {
	if (s.startsWith("\"") && s.endsWith("\"")) {
	    s = s.substring(1, s.length() - 1);
	    // check for any escaped characters
	    if (s.indexOf('\\') >= 0) {
		StringBuffer sb = new StringBuffer(s.length());	// approx
		for (int i = 0; i < s.length(); i++) {
		    char c = s.charAt(i);
		    if (c == '\\' && i < s.length() - 1)
			c = s.charAt(++i);
		    sb.append(c);
		}
		s = sb.toString();
	    }
	}
	return s;
    }

    /**
     * The equality operator.
     */
    public boolean equals(Object a) {
	if (!(a instanceof InternetAddress))
	    return false;

	String s = ((InternetAddress)a).getAddress();
	if (s == address)
	    return true;
	if (address != null && address.equalsIgnoreCase(s))
	    return true;

	return false;
    }

    /**
     * Compute a hash code for the address.
     */
    public int hashCode() {
	if (address == null)
	    return 0;
	else
	    return address.toLowerCase(Locale.ENGLISH).hashCode();
    }

    /**
     * Convert the given array of InternetAddress objects into
     * a comma separated sequence of address strings. The
     * resulting string contains only US-ASCII characters, and
     * hence is mail-safe. <p>
     *
     * @param addresses	array of InternetAddress objects
     * @exception 	ClassCastException, if any address object in the 
     *			given array is not an InternetAddress object. Note
     *			that this is a RuntimeException.
     * @return		comma separated string of addresses
     */
    public static String toString(Address[] addresses) {
	return toString(addresses, 0);
    }

    /**
     * Convert the given array of InternetAddress objects into
     * a comma separated sequence of address strings. The
     * resulting string contains only US-ASCII characters, and
     * hence is mail-safe. <p>
     *
     * The 'used' parameter specifies the number of character positions
     * already taken up in the field into which the resulting address 
     * sequence string is to be inserted. It is used to determine the 
     * line-break positions in the resulting address sequence string.
     *
     * @param addresses	array of InternetAddress objects
     * @param used	number of character positions already used, in
     *			the field into which the address string is to
     *			be inserted.
     * @exception 	ClassCastException, if any address object in the 
     *			given array is not an InternetAddress object. Note
     *			that this is a RuntimeException.
     * @return		comma separated string of addresses
     */
    public static String toString(Address[] addresses, int used) {
	if (addresses == null || addresses.length == 0)
	    return null;

	StringBuffer sb = new StringBuffer();

	for (int i = 0; i < addresses.length; i++) {
	    if (i != 0) { // need to append comma
		sb.append(", ");
		used += 2;
	    }

	    String s = addresses[i].toString();
	    int len = lengthOfFirstSegment(s); // length till CRLF
	    if (used + len > 76) { // overflows ...
		sb.append("\r\n\t"); // .. start new continuation line
		used = 8; // account for the starting <tab> char
	    }
	    sb.append(s);
	    used = lengthOfLastSegment(s, used);
	}

	return sb.toString();
    }

    /* Return the length of the first segment within this string.
     * If no segments exist, the length of the whole line is returned.
     */
    private static int lengthOfFirstSegment(String s) {
	int pos;
	if ((pos = s.indexOf("\r\n")) != -1)
	    return pos;
	else
	    return s.length();
    }

    /*
     * Return the length of the last segment within this string.
     * If no segments exist, the length of the whole line plus
     * <code>used</code> is returned.
     */
    private static int lengthOfLastSegment(String s, int used) {
	int pos;
	if ((pos = s.lastIndexOf("\r\n")) != -1)
	    return s.length() - pos - 2;
	else 
	    return s.length() + used;
    }

    /**
     * Return an InternetAddress object representing the current user.
     * The entire email address may be specified in the "mail.from"
     * property.  If not set, the "mail.user" and "mail.host" properties
     * are tried.  If those are not set, the "user.name" property and
     * <code>InetAddress.getLocalHost</code> method are tried.
     * Security exceptions that may occur while accessing this information
     * are ignored.  If it is not possible to determine an email address,
     * null is returned.
     *
     * @param	session		Session object used for property lookup
     * @return			current user's email address
     */
    public static InternetAddress getLocalAddress(Session session) {
	String user=null, host=null, address=null;
	try {
	    if (session == null) {
		user = System.getProperty("user.name");
		host = InetAddress.getLocalHost().getHostName();
	    } else {
		address = session.getProperty("mail.from");
		if (address == null) {
		    user = session.getProperty("mail.user");
		    if (user == null || user.length() == 0)
			user = session.getProperty("user.name");
		    if (user == null || user.length() == 0)
			user = System.getProperty("user.name");
		    host = session.getProperty("mail.host");
		    if (host == null || host.length() == 0) {
			InetAddress me = InetAddress.getLocalHost();
			if (me != null)
			    host = me.getHostName();
		    }
		}
	    }

	    if (address == null && user != null && user.length() != 0 &&
		    host != null && host.length() != 0)
		address = user + "@" + host;

	    if (address != null)
		return new InternetAddress(address);
	} catch (SecurityException sex) {	// ignore it
	} catch (AddressException ex) {		// ignore it
	} catch (UnknownHostException ex) { }	// ignore it
	return null;
    }

    /**
     * Parse the given comma separated sequence of addresses into
     * InternetAddress objects.  Addresses must follow RFC822 syntax.
     *
     * @param addresslist	comma separated address strings
     * @return			array of InternetAddress objects
     * @exception		AddressException if the parse failed
     */
    public static InternetAddress[] parse(String addresslist) 
				throws AddressException {
	return parse(addresslist, true);
    }

    /**
     * Parse the given sequence of addresses into InternetAddress
     * objects.  If <code>strict</code> is false, simple email addresses
     * separated by spaces are also allowed.  If <code>strict</code> is
     * true, many (but not all) of the RFC822 syntax rules are enforced.
     * In particular, even if <code>strict</code> is true, addresses
     * composed of simple names (with no "@domain" part) are allowed.
     * Such "illegal" addresses are not uncommon in real messages. <p>
     *
     * Non-strict parsing is typically used when parsing a list of
     * mail addresses entered by a human.  Strict parsing is typically
     * used when parsing address headers in mail messages.
     *
     * @param	addresslist	comma separated address strings
     * @param	strict		enforce RFC822 syntax
     * @return			array of InternetAddress objects
     * @exception		AddressException if the parse failed
     */
    public static InternetAddress[] parse(String addresslist, boolean strict)
					    throws AddressException {
	return parse(addresslist, strict, false);
    }

    /**
     * Parse the given sequence of addresses into InternetAddress
     * objects.  If <code>strict</code> is false, the full syntax rules for
     * individual addresses are not enforced.  If <code>strict</code> is
     * true, many (but not all) of the RFC822 syntax rules are enforced. <p>
     *
     * To better support the range of "invalid" addresses seen in real
     * messages, this method enforces fewer syntax rules than the
     * <code>parse</code> method when the strict flag is false
     * and enforces more rules when the strict flag is true.  If the
     * strict flag is false and the parse is successful in separating out an
     * email address or addresses, the syntax of the addresses themselves
     * is not checked.
     *
     * @param	addresslist	comma separated address strings
     * @param	strict		enforce RFC822 syntax
     * @return			array of InternetAddress objects
     * @exception		AddressException if the parse failed
     * @since			JavaMail 1.3
     */
    public static InternetAddress[] parseHeader(String addresslist,
				boolean strict) throws AddressException {
	return parse(addresslist, strict, true);
    }

    /*
     * RFC822 Address parser.
     *
     * XXX - This is complex enough that it ought to be a real parser,
     *       not this ad-hoc mess, and because of that, this is not perfect.
     *
     * XXX - Deal with encoded Headers too.
     */
    private static InternetAddress[] parse(String s, boolean strict,
				    boolean parseHdr) throws AddressException {
	int start, end, index, nesting;
	int start_personal = -1, end_personal = -1;
	int length = s.length();
	boolean in_group = false;	// we're processing a group term
	boolean route_addr = false;	// address came from route-addr term
	boolean rfc822 = false;		// looks like an RFC822 address
	char c;
	Vector v = new Vector();
	InternetAddress ma;

	for (start = end = -1, index = 0; index < length; index++) {
    	    c = s.charAt(index);

	    switch (c) {
	    case '(': // We are parsing a Comment. Ignore everything inside.
		// XXX - comment fields should be parsed as whitespace,
		//	 more than one allowed per address
		rfc822 = true;
		if (start >= 0 && end == -1)
		    end = index;
		if (start_personal == -1)
		    start_personal = index + 1;
		for (index++, nesting = 1; index < length && nesting > 0;
		  index++) {
		    c = s.charAt(index);
		    switch (c) {
		    case '\\':
			index++; // skip both '\' and the escaped char
			break;
		    case '(':
			nesting++;
			break;
		    case ')':
			nesting--;
			break;
		    default:
			break;
		    }
		}
		if (nesting > 0)
		    throw new AddressException("Missing ')'", s, index);
		index--;	// point to closing paren
		if (end_personal == -1)
		    end_personal = index;
		break;

	    case ')':
		throw new AddressException("Missing '('", s, index);

	    case '<':
		rfc822 = true;
		if (route_addr)
		    throw new AddressException("Extra route-addr", s, index);
		if (!in_group) {
		    start_personal = start;
		    if (start_personal >= 0)
			end_personal = index;
		    start = index + 1;
		}

		boolean inquote = false;
	      outf:
		for (index++; index < length; index++) {
		    c = s.charAt(index);
		    switch (c) {
		    case '\\':	// XXX - is this needed?
			index++; // skip both '\' and the escaped char
			break;
		    case '"':
			inquote = !inquote;
			break;
		    case '>':
			if (inquote)
			    continue;
			break outf; // out of for loop
		    default:
			break;
		    }
		}
		if (index >= length) {
		    if (inquote)
			throw new AddressException("Missing '\"'", s, index);
		    else
			throw new AddressException("Missing '>'", s, index);
		}
		route_addr = true;
		end = index;
		break;
	    case '>':
		throw new AddressException("Missing '<'", s, index);

	    case '"':	// parse quoted string
		rfc822 = true;
		if (start == -1)
		    start = index;
	      outq:
		for (index++; index < length; index++) {
		    c = s.charAt(index);
		    switch (c) {
		    case '\\':
			index++; // skip both '\' and the escaped char
			break;
		    case '"':
			break outq; // out of for loop
		    default:
			break;
		    }
		}
		if (index >= length)
		    throw new AddressException("Missing '\"'", s, index);
		break;

	    case '[':	// a domain-literal, probably
		rfc822 = true;
	      outb:
		for (index++; index < length; index++) {
		    c = s.charAt(index);
		    switch (c) {
		    case '\\':
			index++; // skip both '\' and the escaped char
			break;
		    case ']':
			break outb; // out of for loop
		    default:
			break;
		    }
		}
		if (index >= length)
		    throw new AddressException("Missing ']'", s, index);
		break;

	    case ',':	// end of an address, probably
		if (start == -1) {
		    route_addr = false;
		    rfc822 = false;
		    start = end = -1;
		    break;	// nope, nothing there
		}
		if (in_group) {
		    route_addr = false;
		    break;
		}
		// got a token, add this to our InternetAddress vector
		if (end == -1)
		    end = index;
		String addr = s.substring(start, end).trim();
		if (rfc822 || strict || parseHdr) {
		    if (strict || !parseHdr)
			checkAddress(addr, route_addr, false);
		    ma = new InternetAddress();
		    ma.setAddress(addr);
		    if (start_personal >= 0) {
			ma.encodedPersonal = unquote(
			    s.substring(start_personal, end_personal).trim());
			start_personal = end_personal = -1;
		    }
		    v.addElement(ma);
		} else {
		    // maybe we passed over more than one space-separated addr
		    StringTokenizer st = new StringTokenizer(addr);
		    while (st.hasMoreTokens()) {
			String a = st.nextToken();
			checkAddress(a, false, false);
			ma = new InternetAddress();
			ma.setAddress(a);
			v.addElement(ma);
		    }
		}

		route_addr = false;
		rfc822 = false;
		start = end = -1;
		break;

	    case ':':
		rfc822 = true;
		if (in_group)
		    throw new AddressException("Nested group", s, index);
		in_group = true;
		if (start == -1)
		    start = index;
		break;

	    case ';':
		if (start == -1)
		    start = index;
		if (!in_group)
		    throw new AddressException(
			    "Illegal semicolon, not in group", s, index);
		in_group = false;
		if (start == -1)
		    start = index;
		ma = new InternetAddress();
		end = index + 1;
		ma.setAddress(s.substring(start, end).trim());
		v.addElement(ma);

		route_addr = false;
		start = end = -1;
		break;

	    // Ignore whitespace
	    case ' ':
	    case '\t':
	    case '\r':
	    case '\n':
		break;

	    default:
		if (start == -1)
		    start = index;
		break;
	     }
	}

	if (start >= 0) {
	    /*
	     * The last token, add this to our InternetAddress vector.
	     * Note that this block of code should be identical to the
	     * block above for "case ','".
	     */
	    if (end == -1)
		end = index;
	    String addr = s.substring(start, end).trim();
	    if (rfc822 || strict || parseHdr) {
		if (strict || !parseHdr)
		    checkAddress(addr, route_addr, false);
		ma = new InternetAddress();
		ma.setAddress(addr);
		if (start_personal >= 0) {
		    ma.encodedPersonal = unquote(
			    s.substring(start_personal, end_personal).trim());
		}
		v.addElement(ma);
	    } else {
		// maybe we passed over more than one space-separated addr
		StringTokenizer st = new StringTokenizer(addr);
		while (st.hasMoreTokens()) {
		    String a = st.nextToken();
		    checkAddress(a, false, false);
		    ma = new InternetAddress();
		    ma.setAddress(a);
		    v.addElement(ma);
		}
	    }
	}

	InternetAddress[] a = new InternetAddress[v.size()];
	v.copyInto(a);
	return a;
    }

    /**
     * Validate that this address conforms to the syntax rules of
     * RFC 822.  The current implementation checks many, but not
     * all, syntax rules.  Note that even though the syntax of
     * the address may be correct, there's no guarantee that a
     * mailbox of that name exists.
     *
     * @exception	AddressException if the address isn't valid.
     * @since		JavaMail 1.3
     */
    public void validate() throws AddressException {
	checkAddress(getAddress(), true, true);
    }

    private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
    private static final String specialsNoDot = specialsNoDotNoAt + "@";

    /**
     * Check that the address is a valid "mailbox" per RFC822.
     * (We also allow simple names.)
     *
     * XXX - much more to check
     * XXX - doesn't handle domain-literals properly (but no one uses them)
     */
    private static void checkAddress(String addr,
				boolean routeAddr, boolean validate)
				throws AddressException {
	int i, start = 0;
	if (addr.indexOf('"') >= 0)
	    return;			// quote in address, too hard to check
	if (routeAddr) {
	    /*
	     * Check for a legal "route-addr":
	     *		[@domain[,@domain ...]:]local@domain
	     */
	    for (start = 0; (i = indexOfAny(addr, ",:", start)) >= 0;
		    start = i+1) {
		if (addr.charAt(start) != '@')
		    throw new AddressException("Illegal route-addr", addr);
		if (addr.charAt(i) == ':') {
		    // end of route-addr
		    start = i + 1;
		    break;
		}
	    }
	}
	/*
	 * The rest should be "local@domain", but we allow simply "local"
	 * unless called from validate.
	 */
	String local;
	String domain;
	if ((i = addr.indexOf('@', start)) >= 0) {
	    if (i == start)
		throw new AddressException("Missing local name", addr);
	    if (i == addr.length() - 1)
		throw new AddressException("Missing domain", addr);
	    local = addr.substring(start, i);
	    domain = addr.substring(i + 1);
	} else {
	    /*
	     * Note that the MimeMessage class doesn't remember addresses
	     * as separate objects; it writes them out as headers and then
	     * parses the headers when the addresses are requested.
	     * In order to support the case where a "simple" address is used,
	     * but the address also has a personal name and thus looks like
	     * it should be a valid RFC822 address when parsed, we only check
	     * this if we're explicitly called from the validate method.
	     */
	    if (validate)
		throw new AddressException("Missing final '@domain'", addr);

	    /*
	     * No '@' so it's not *really* an RFC822 address, but still
	     * we allow some simple forms.
	     */
	    local = addr;
	    domain = null;
	}
	// there better not be any whitespace in it
	if (indexOfAny(addr, " \t\n\r") >= 0)
	    throw new AddressException("Illegal whitespace in address", addr);
	// local-part must follow RFC822, no specials except '.'
	if (indexOfAny(local, specialsNoDot) >= 0)
	    throw new AddressException("Illegal character in local name", addr);
	// check for illegal chars in the domain, but ignore domain literals
	if (domain != null && domain.indexOf('[') < 0) {
	    if (indexOfAny(domain, specialsNoDot) >= 0)
		throw new AddressException("Illegal character in domain", addr);
	}
    }

    /**
     * Is this a "simple" address?  Simple addresses don't contain quotes
     * or any RFC822 special characters other than '@' and '.'.
     */
    private boolean isSimple() {
	return address == null || indexOfAny(address, specialsNoDotNoAt) < 0;
    }

    /**
     * Indicates whether this address is an RFC 822 group address.
     * Note that a group address is different than the mailing
     * list addresses supported by most mail servers.  Group addresses
     * are rarely used; see RFC 822 for details.
     *
     * @return		true if this address represents a group
     * @since		JavaMail 1.3
     */
    public boolean isGroup() {
	// quick and dirty check
	return address != null &&
	    address.endsWith(";") && address.indexOf(':') > 0;
    }

    /**
     * Return the members of a group address.  A group may have zero,
     * one, or more members.  If this address is not a group, null
     * is returned.  The <code>strict</code> parameter controls whether
     * the group list is parsed using strict RFC 822 rules or not.
     * The parsing is done using the <code>parseHeader</code> method.
     *
     * @return		array of InternetAddress objects, or null
     * @exception	AddressException if the group list can't be parsed
     * @since		JavaMail 1.3
     */
    public InternetAddress[] getGroup(boolean strict) throws AddressException {
	Vector groups = null;
	String addr = getAddress();
	// groups are of the form "name:addr,addr,...;"
	if (!addr.endsWith(";"))
	    return null;
	int ix = addr.indexOf(':');
	if (ix < 0)
	    return null;
	// extract the list
	String list = addr.substring(ix + 1, addr.length() - 1);
	// parse it and return the individual addresses
	return InternetAddress.parseHeader(list, strict);
    }

    /**
     * Return the first index of any of the characters in "any" in "s",
     * or -1 if none are found.
     *
     * This should be a method on String.
     */
    private static int indexOfAny(String s, String any) {
	return indexOfAny(s, any, 0);
    }

    private static int indexOfAny(String s, String any, int start) {
	try {
	    int len = s.length();
	    for (int i = start; i < len; i++) {
		if (any.indexOf(s.charAt(i)) >= 0)
		    return i;
	    }
	    return -1;
	} catch (StringIndexOutOfBoundsException e) {
	    return -1;
	}
    }

    /*
    public static void main(String argv[]) throws Exception {
	for (int i = 0; i < argv.length; i++) {
	    InternetAddress[] a = InternetAddress.parse(argv[i]);
	    for (int j = 0; j < a.length; j++) {
		System.out.println("arg " + i + " address " + j + ": " + a[j]);
		System.out.println("\tAddress: " + a[j].getAddress() +
				    "\tPersonal: " + a[j].getPersonal());
	    }
	    if (a.length > 1) {
		System.out.println("address 0 hash code: " + a[0].hashCode());
		System.out.println("address 1 hash code: " + a[1].hashCode());
		if (a[0].hashCode() == a[1].hashCode())
		    System.out.println("success, hashcodes equal");
		else
		    System.out.println("fail, hashcodes not equal");
		if (a[0].equals(a[1]))
		    System.out.println("success, addresses equal");
		else
		    System.out.println("fail, addresses not equal");
		if (a[1].equals(a[0]))
		    System.out.println("success, addresses equal");
		else
		    System.out.println("fail, addresses not equal");
	    }
	}
    }
    */
}
