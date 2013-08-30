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
 * @(#)FLAGS.java	1.10 07/05/04
 */

package com.sun.mail.imap.protocol;

import javax.mail.Flags;
import com.sun.mail.iap.*; 

/**
 * This class 
 *
 * @version 1.10, 07/05/04
 * @author  John Mani
 */

public class FLAGS extends Flags implements Item {

    // IMAP item name
    static final char[] name = {'F','L','A','G','S'};
    public int msgno;

    private static final long serialVersionUID = 439049847053756670L;

    /**
     * Constructor
     */
    public FLAGS(IMAPResponse r) throws ParsingException {
	msgno = r.getNumber();

	r.skipSpaces();
	String[] flags = r.readSimpleList();
	if (flags != null) { // if not empty flaglist
	    for (int i = 0; i < flags.length; i++) {
		String s = flags[i];
		if (s.length() >= 2 && s.charAt(0) == '\\') {
		    switch (Character.toUpperCase(s.charAt(1))) {
		    case 'S': // \Seen
			add(Flags.Flag.SEEN);
			break;
		    case 'R': // \Recent
			add(Flags.Flag.RECENT);
			break;
		    case 'D':
			if (s.length() >= 3) {
			    char c = s.charAt(2);
			    if (c == 'e' || c == 'E') // \Deleted
				add(Flags.Flag.DELETED);
			    else if (c == 'r' || c == 'R') // \Draft
				add(Flags.Flag.DRAFT);
			} else
			    add(s);	// unknown, treat it as a user flag
			break;
		    case 'A': // \Answered
			add(Flags.Flag.ANSWERED);
			break;
		    case 'F': // \Flagged
			add(Flags.Flag.FLAGGED);
			break;
		    case '*': // \*
			add(Flags.Flag.USER);
			break;
		    default:
			add(s);		// unknown, treat it as a user flag
			break;
		    }
		} else
		    add(s);
	    }
	}
    }
}
