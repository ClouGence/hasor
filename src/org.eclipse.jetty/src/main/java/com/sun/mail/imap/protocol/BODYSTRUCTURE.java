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
 * @(#)BODYSTRUCTURE.java	1.22 07/05/04
 */

package com.sun.mail.imap.protocol;

import java.util.Vector;
import javax.mail.internet.ParameterList;
import com.sun.mail.iap.*; 

/**
 * A BODYSTRUCTURE response.
 *
 * @version 1.22, 07/05/04
 * @author  John Mani
 * @author  Bill Shannon
 */

public class BODYSTRUCTURE implements Item {
    
    static final char[] name =
	{'B','O','D','Y','S','T','R','U','C','T','U','R','E'};
    public int msgno;

    public String type;		// Type
    public String subtype;	// Subtype
    public String encoding;	// Encoding
    public int lines = -1;	// Size in lines
    public int size = -1;	// Size in bytes
    public String disposition;	// Disposition
    public String id;		// Content-ID
    public String description;	// Content-Description
    public String md5;		// MD-5 checksum
    public String attachment;	// Attachment name
    public ParameterList cParams; // Body parameters
    public ParameterList dParams; // Disposition parameters
    public String[] language;	// Language
    public BODYSTRUCTURE[] bodies; // array of BODYSTRUCTURE objects
				   //  for multipart & message/rfc822
    public ENVELOPE envelope;	// for message/rfc822

    private static int SINGLE	= 1;
    private static int MULTI	= 2;
    private static int NESTED	= 3;
    private int processedType;	// MULTI | SINGLE | NESTED

    // special debugging output to debug parsing errors
    private static boolean parseDebug = false;

    static {
	try {
	    String s = System.getProperty("mail.imap.parse.debug");
	    // default to false
	    parseDebug = s != null && s.equalsIgnoreCase("true");
	} catch (SecurityException sex) {
	    // ignore it
	}
    }

    public BODYSTRUCTURE(FetchResponse r) throws ParsingException {
	if (parseDebug)
	    System.out.println("DEBUG IMAP: parsing BODYSTRUCTURE");
	msgno = r.getNumber();
	if (parseDebug)
	    System.out.println("DEBUG IMAP: msgno " + msgno);

	r.skipSpaces();

	if (r.readByte() != '(')
	    throw new ParsingException(
		"BODYSTRUCTURE parse error: missing ``('' at start");

	if (r.peekByte() == '(') { // multipart
	    if (parseDebug)
		System.out.println("DEBUG IMAP: parsing multipart");
	    type = "multipart";
	    processedType = MULTI;
	    Vector v = new Vector(1);
	    int i = 1;
	    do {
		v.addElement(new BODYSTRUCTURE(r));
		/*
		 * Even though the IMAP spec says there can't be any spaces
		 * between parts, some servers erroneously put a space in
		 * here.  In the spirit of "be liberal in what you accept",
		 * we skip it.
		 */
		r.skipSpaces();
	    } while (r.peekByte() == '(');

	    // setup bodies.
	    bodies = new BODYSTRUCTURE[v.size()];
	    v.copyInto(bodies);

	    subtype = r.readString(); // subtype
	    if (parseDebug)
		System.out.println("DEBUG IMAP: subtype " + subtype);

	    if (r.readByte() == ')') { // done
		if (parseDebug)
		    System.out.println("DEBUG IMAP: parse DONE");
		return;
	    }

	    // Else, we have extension data

	    if (parseDebug)
		System.out.println("DEBUG IMAP: parsing extension data");
	    // Body parameters
	    cParams = parseParameters(r);
	    if (r.readByte() == ')') { // done
		if (parseDebug)
		    System.out.println("DEBUG IMAP: body parameters DONE");
		return;
	    }
	    
	    // Disposition
	    byte b = r.readByte();
	    if (b == '(') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: parse disposition");
		disposition = r.readString();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition " +
							disposition);
		dParams = parseParameters(r);
		if (r.readByte() != ')') // eat the end ')'
		    throw new ParsingException(
			"BODYSTRUCTURE parse error: " +
			"missing ``)'' at end of disposition in multipart");
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition DONE");
	    } else if (b == 'N' || b == 'n') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition NIL");
		r.skip(2); // skip 'NIL'
	    } else {
		throw new ParsingException(
		    "BODYSTRUCTURE parse error: " +
		    type + "/" + subtype + ": " +
		    "bad multipart disposition, b " + b);
	    }

	    // RFC3501 allows no body-fld-lang after body-fld-disp,
	    // even though RFC2060 required it
	    if ((b = r.readByte()) == ')') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: no body-fld-lang");
		return; // done
	    }

	    if (b != ' ')
		throw new ParsingException(
		    "BODYSTRUCTURE parse error: " +
		    "missing space after disposition");

	    // Language
	    if (r.peekByte() == '(') { // a list follows
		language = r.readStringList();
		if (parseDebug)
		    System.out.println(
			"DEBUG IMAP: language len " + language.length);
	    } else {
		String l = r.readString();
		if (l != null) {
		    String[] la = { l };
		    language = la;
		    if (parseDebug)
			System.out.println("DEBUG IMAP: language " + l);
		}
	    }

	    // RFC3501 defines an optional "body location" next,
	    // but for now we ignore it along with other extensions.

	    // Throw away any further extension data
	    while (r.readByte() == ' ')
		parseBodyExtension(r);
	}
	else { // Single part
	    if (parseDebug)
		System.out.println("DEBUG IMAP: single part");
	    type = r.readString();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: type " + type);
	    processedType = SINGLE;
	    subtype = r.readString();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: subtype " + subtype);

	    // SIMS 4.0 returns NIL for a Content-Type of "binary", fix it here
	    if (type == null) {
		type = "application";
		subtype = "octet-stream";
	    }
	    cParams = parseParameters(r);
	    if (parseDebug)
		System.out.println("DEBUG IMAP: cParams " + cParams);
	    id = r.readString();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: id " + id);
	    description = r.readString();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: description " + description);
	    encoding = r.readString();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: encoding " + encoding);
	    size = r.readNumber();
	    if (parseDebug)
		System.out.println("DEBUG IMAP: size " + size);
	    if (size < 0)
		throw new ParsingException(
			    "BODYSTRUCTURE parse error: bad ``size'' element");

	    // "text/*" & "message/rfc822" types have additional data ..
	    if (type.equalsIgnoreCase("text")) {
		lines = r.readNumber();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: lines " + lines);
		if (lines < 0)
		    throw new ParsingException(
			    "BODYSTRUCTURE parse error: bad ``lines'' element");
	    } else if (type.equalsIgnoreCase("message") &&
		     subtype.equalsIgnoreCase("rfc822")) {
		// Nested message
		processedType = NESTED;
		envelope = new ENVELOPE(r);
		BODYSTRUCTURE[] bs = { new BODYSTRUCTURE(r) };
		bodies = bs;
		lines = r.readNumber();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: lines " + lines);
		if (lines < 0)
		    throw new ParsingException(
			    "BODYSTRUCTURE parse error: bad ``lines'' element");
	    } else {
		// Detect common error of including lines element on other types
		r.skipSpaces();
		byte bn = r.peekByte();
		if (Character.isDigit((char)bn)) // number
		    throw new ParsingException(
			    "BODYSTRUCTURE parse error: server erroneously " +
				"included ``lines'' element with type " +
				type + "/" + subtype);
	    }

	    if (r.peekByte() == ')') {
		r.readByte();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: parse DONE");
		return; // done
	    }

	    // Optional extension data

	    // MD5
	    md5 = r.readString();
	    if (r.readByte() == ')') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: no MD5 DONE");
		return; // done
	    }
	    
	    // Disposition
	    byte b = r.readByte();
	    if (b == '(') {
		disposition = r.readString();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition " +
							disposition);
		dParams = parseParameters(r);
		if (parseDebug)
		    System.out.println("DEBUG IMAP: dParams " + dParams);
		if (r.readByte() != ')') // eat the end ')'
		    throw new ParsingException(
			"BODYSTRUCTURE parse error: " +
			"missing ``)'' at end of disposition");
	    } else if (b == 'N' || b == 'n') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition NIL");
		r.skip(2); // skip 'NIL'
	    } else {
		throw new ParsingException(
		    "BODYSTRUCTURE parse error: " +
		    type + "/" + subtype + ": " +
		    "bad single part disposition, b " + b);
	    }

	    if (r.readByte() == ')') {
		if (parseDebug)
		    System.out.println("DEBUG IMAP: disposition DONE");
		return; // done
	    }
	    
	    // Language
	    if (r.peekByte() == '(') { // a list follows
		language = r.readStringList();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: language len " +
							language.length);
	    } else { // protocol is unnessarily complex here
		String l = r.readString();
		if (l != null) {
		    String[] la = { l };
		    language = la;
		    if (parseDebug)
			System.out.println("DEBUG IMAP: language " + l);
		}
	    }

	    // RFC3501 defines an optional "body location" next,
	    // but for now we ignore it along with other extensions.

	    // Throw away any further extension data
	    while (r.readByte() == ' ')
		parseBodyExtension(r);
	    if (parseDebug)
		System.out.println("DEBUG IMAP: all DONE");
	}
    }

    public boolean isMulti() {
	return processedType == MULTI;
    }

    public boolean isSingle() {
	return processedType == SINGLE;
    }

    public boolean isNested() {
	return processedType == NESTED;
    }

    private ParameterList parseParameters(Response r)
			throws ParsingException {
	r.skipSpaces();

	ParameterList list = null;
	byte b = r.readByte();
	if (b == '(') {
	    list = new ParameterList();
	    do {
		String name = r.readString();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: parameter name " + name);
		if (name == null)
		    throw new ParsingException(
			"BODYSTRUCTURE parse error: " +
			type + "/" + subtype + ": " +
			"null name in parameter list");
		String value = r.readString();
		if (parseDebug)
		    System.out.println("DEBUG IMAP: parameter value " + value);
		list.set(name, value);
	    } while (r.readByte() != ')');
	    list.set(null, "DONE");	// XXX - hack
	} else if (b == 'N' || b == 'n') {
	    if (parseDebug)
		System.out.println("DEBUG IMAP: parameter list NIL");
	    r.skip(2);
	} else
	    throw new ParsingException("Parameter list parse error");

	return list;
    }

    private void parseBodyExtension(Response r) throws ParsingException {
	r.skipSpaces();

	byte b = r.peekByte();
	if (b == '(') {
	    r.skip(1); // skip '('
	    do {
		parseBodyExtension(r);
	    } while (r.readByte() != ')');
	} else if (Character.isDigit((char)b)) // number
	    r.readNumber();
	else // nstring
	    r.readString();
    }
}
