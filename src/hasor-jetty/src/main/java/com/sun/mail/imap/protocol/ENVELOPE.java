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
 * @(#)ENVELOPE.java	1.20 07/05/04
 */

package com.sun.mail.imap.protocol;

import java.util.Vector;
import java.util.Date;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeUtility;
import com.sun.mail.iap.*;

/**
 * The ENEVELOPE item of an IMAP FETCH response.
 *
 * @author  John Mani
 * @author  Bill Shannon
 */

public class ENVELOPE implements Item {
    
    // IMAP item name
    static final char[] name = {'E','N','V','E','L','O','P','E'};
    public int msgno;

    public Date date = null;
    public String subject;
    public InternetAddress[] from;
    public InternetAddress[] sender;
    public InternetAddress[] replyTo;
    public InternetAddress[] to;
    public InternetAddress[] cc;
    public InternetAddress[] bcc;
    public String inReplyTo;
    public String messageId;

    // Used to parse dates
    private static MailDateFormat mailDateFormat = new MailDateFormat();
    
    public ENVELOPE(FetchResponse r) throws ParsingException {
	msgno = r.getNumber();

	r.skipSpaces();

	if (r.readByte() != '(')
	    throw new ParsingException("ENVELOPE parse error");
	
	String s = r.readString();
	if (s != null) {
	    try {
		date = mailDateFormat.parse(s);
	    } catch (Exception pex) {
		// We need to be *very* tolerant about bogus dates (and
		// there's lot of 'em around), so we ignore any 
		// exception (including RunTimeExceptions) and just let 
		// date be null.
	    }
	}

	subject = r.readString();
	from = parseAddressList(r);
	sender = parseAddressList(r);
	replyTo = parseAddressList(r);
	to = parseAddressList(r);
	cc = parseAddressList(r);
	bcc = parseAddressList(r);
	inReplyTo = r.readString();
	messageId = r.readString();

	if (r.readByte() != ')')
	    throw new ParsingException("ENVELOPE parse error");
    }

    private InternetAddress[] parseAddressList(Response r) 
		throws ParsingException {
	r.skipSpaces(); // skip leading spaces

	byte b = r.readByte();
	if (b == '(') {
	    Vector v = new Vector();

	    do {
		IMAPAddress a = new IMAPAddress(r);
		// if we see an end-of-group address at the top, ignore it
		if (!a.isEndOfGroup())
		    v.addElement(a);
	    } while (r.peekByte() != ')');

	    // skip the terminating ')' at the end of the addresslist
	    r.skip(1);

	    InternetAddress[] a = new InternetAddress[v.size()];
	    v.copyInto(a);
	    return a;
	} else if (b == 'N' || b == 'n') { // NIL
	    r.skip(2); // skip 'NIL'
	    return null;
	} else
	    throw new ParsingException("ADDRESS parse error");
    }
}

class IMAPAddress extends InternetAddress {
    private boolean group = false;
    private InternetAddress[] grouplist;
    private String groupname;

    private static final long serialVersionUID = -3835822029483122232L;

    IMAPAddress(Response r) throws ParsingException {
        r.skipSpaces(); // skip leading spaces

        if (r.readByte() != '(')
            throw new ParsingException("ADDRESS parse error");

        encodedPersonal = r.readString();

        r.readString(); // throw away address_list
	String mb = r.readString();
	String host = r.readString();
        if (r.readByte() != ')') // skip past terminating ')'
            throw new ParsingException("ADDRESS parse error");

	if (host == null) {
	    // it's a group list, start or end
	    group = true;
	    groupname = mb;
	    if (groupname == null)	// end of group list
		return;
	    // Accumulate a group list.  The members of the group
	    // are accumulated in a Vector and the corresponding string
	    // representation of the group is accumulated in a StringBuffer.
	    StringBuffer sb = new StringBuffer();
	    sb.append(groupname).append(':');
	    Vector v = new Vector();
	    while (r.peekByte() != ')') {
		IMAPAddress a = new IMAPAddress(r);
		if (a.isEndOfGroup())	// reached end of group
		    break;
		if (v.size() != 0)	// if not first element, need a comma
		    sb.append(',');
		sb.append(a.toString());
		v.addElement(a);
	    }
	    sb.append(';');
	    address = sb.toString();
	    grouplist = new IMAPAddress[v.size()];
	    v.copyInto(grouplist);
	} else {
	    if (mb == null || mb.length() == 0)
		address = host;
	    else if (host.length() == 0)
		address = mb;
	    else
		address = mb + "@" + host;
	}

    }

    boolean isEndOfGroup() {
	return group && groupname == null;
    }

    public boolean isGroup() {
	return group;
    }

    public InternetAddress[] getGroup(boolean strict) throws AddressException {
	if (grouplist == null)
	    return null;
	return (InternetAddress[])grouplist.clone();
    }
}
