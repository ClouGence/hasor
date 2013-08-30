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
 * @(#)BodyTerm.java	1.11 07/05/04
 */

package javax.mail.search;

import javax.mail.*;

/**
 * This class implements searches on a Message Body.
 * All parts of the message that are of MIME type "text/*" are searched.
 * 
 * @author Bill Shannon
 * @author John Mani
 */
public final class BodyTerm extends StringTerm {

    private static final long serialVersionUID = -4888862527916911385L;

    /**
     * Constructor
     * @param pattern	The String to search for
     */
    public BodyTerm(String pattern) {
	// Note: comparison is case-insensitive
	super(pattern);
    }

    /**
     * The match method.
     *
     * @param msg	The pattern search is applied on this Message's body
     * @return		true if the pattern is found; otherwise false 
     */
    public boolean match(Message msg) {
	return matchPart(msg);
    }

    /**
     * Search all the parts of the message for any text part
     * that matches the pattern.
     */
    private boolean matchPart(Part p) {
	try {
	    /*
	     * Using isMimeType to determine the content type avoids
	     * fetching the actual content data until we need it.
	     */
	    if (p.isMimeType("text/*")) {
		String s = (String)p.getContent();
		if (s == null)
		    return false;
		/*
		 * We invoke our superclass' (i.e., StringTerm) match method.
		 * Note however that StringTerm.match() is not optimized 
		 * for substring searches in large string buffers. We really
		 * need to have a StringTerm subclass, say BigStringTerm, 
		 * with its own match() method that uses a better algorithm ..
		 * and then subclass BodyTerm from BigStringTerm.
		 */ 
		return super.match(s);
	    } else if (p.isMimeType("multipart/*")) {
		Multipart mp = (Multipart)p.getContent();
		int count = mp.getCount();
		for (int i = 0; i < count; i++)
		    if (matchPart(mp.getBodyPart(i)))
			return true;
	    } else if (p.isMimeType("message/rfc822")) {
		return matchPart((Part)p.getContent());
	    }
	} catch (Exception ex) {
	}
	return false;
    }

    /**
     * Equality comparison.
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof BodyTerm))
	    return false;
	return super.equals(obj);
    }
}
