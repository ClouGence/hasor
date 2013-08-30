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
 * @(#)INTERNALDATE.java	1.17 07/05/04
 */

package com.sun.mail.imap.protocol;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.FieldPosition;

import javax.mail.internet.MailDateFormat;

import com.sun.mail.iap.*; 


/**
 * This class 
 *
 * @version 1.17, 07/05/04
 * @author  John Mani
 */

public class INTERNALDATE implements Item {

    static final char[] name =
	{'I','N','T','E','R','N','A','L','D','A','T','E'};
    public int msgno;
    protected Date date;

    /*
     * Used to parse dates only.  The parse method is thread safe
     * so we only need to create a single object for use by all
     * instances.  We depend on the fact that the MailDateFormat
     * class will parse dates in INTERNALDATE format as well as
     * dates in RFC 822 format.
     */
    private static MailDateFormat mailDateFormat = new MailDateFormat();

    /**
     * Constructor
     */
    public INTERNALDATE(FetchResponse r) throws ParsingException {
	msgno = r.getNumber();
	r.skipSpaces();
	String s = r.readString();
	if (s == null)
	    throw new ParsingException("INTERNALDATE is NIL");
	try {
	    date = mailDateFormat.parse(s);
	} catch (ParseException pex) {
	    throw new ParsingException("INTERNALDATE parse error");
	}
    }

    public Date getDate() {
	return date;
    }

    // INTERNALDATE formatter

    private static SimpleDateFormat df = 
	// Need Locale.US, the "MMM" field can produce unexpected values
	// in non US locales !
	new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss ", Locale.US);

    /**
     * Format given Date object into INTERNALDATE string
     */
    public static String format(Date d) {
	/*
	 * SimpleDateFormat objects aren't thread safe, so rather
	 * than create a separate such object for each request,
	 * we create one object and synchronize its use here
	 * so that only one thread is using it at a time.  This
	 * trades off some potential concurrency for speed in the
	 * common case.
	 *
	 * This method is only used when formatting the date in a
	 * message that's being appended to a folder.
	 */
	StringBuffer sb = new StringBuffer();
	synchronized (df) {
	    df.format(d, sb, new FieldPosition(0));
	}

	// compute timezone offset string
	// XXX - Yes, I know this is deprecated
	int rawOffsetInMins = -d.getTimezoneOffset();
	/*
	 * XXX - in JavaMail 1.4 / J2SE 1.4, possibly replace above with:
	 *
	TimeZone tz = TimeZone.getDefault();
	int offset = tz.getOffset(d);	// get offset from GMT
	int rawOffsetInMins = offset / 60 / 1000; // offset from GMT in mins
	 */
	if (rawOffsetInMins < 0) {
	    sb.append('-');
	    rawOffsetInMins = (-rawOffsetInMins);
	} else
	    sb.append('+');
	
	int offsetInHrs = rawOffsetInMins / 60;
	int offsetInMins = rawOffsetInMins % 60;

	sb.append(Character.forDigit((offsetInHrs/10), 10));
	sb.append(Character.forDigit((offsetInHrs%10), 10));
	sb.append(Character.forDigit((offsetInMins/10), 10));
	sb.append(Character.forDigit((offsetInMins%10), 10));

	return sb.toString();
    }
}
