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
 * @(#)ContentDisposition.java	1.9 07/05/04
 */

package javax.mail.internet;

import javax.mail.*;
import java.util.*;
import java.io.*;

/**
 * This class represents a MIME ContentDisposition value. It provides
 * methods to parse a ContentDisposition string into individual components
 * and to generate a MIME style ContentDisposition string.
 *
 * @version 1.9, 07/05/04
 * @author  John Mani
 */

public class ContentDisposition {

    private String disposition; // disposition
    private ParameterList list;	// parameter list

    /**
     * No-arg Constructor.
     */
    public ContentDisposition() { }

    /**
     * Constructor.
     *
     * @param	disposition	disposition
     * @param	list	ParameterList
     * @since		JavaMail 1.2
     */
    public ContentDisposition(String disposition, ParameterList list) {
	this.disposition = disposition;
	this.list = list;
    }

    /**
     * Constructor that takes a ContentDisposition string. The String
     * is parsed into its constituents: dispostion and parameters. 
     * A ParseException is thrown if the parse fails. 
     *
     * @param	s	the ContentDisposition string.
     * @exception	ParseException if the parse fails.
     * @since		JavaMail 1.2
     */
    public ContentDisposition(String s) throws ParseException {
	HeaderTokenizer h = new HeaderTokenizer(s, HeaderTokenizer.MIME);
	HeaderTokenizer.Token tk;

	// First "disposition" ..
	tk = h.next();
	if (tk.getType() != HeaderTokenizer.Token.ATOM)
	    throw new ParseException();
	disposition = tk.getValue();

	// Then parameters ..
	String rem = h.getRemainder();
	if (rem != null)
	    list = new ParameterList(rem);
    }

    /**
     * Return the disposition value.
     * @return the disposition
     * @since		JavaMail 1.2
     */
    public String getDisposition() {
	return disposition;
    }

    /**
     * Return the specified parameter value. Returns <code>null</code>
     * if this parameter is absent.
     * @return	parameter value
     * @since		JavaMail 1.2
     */
    public String getParameter(String name) {
	if (list == null)
	    return null;

	return list.get(name);
    }

    /**
     * Return a ParameterList object that holds all the available 
     * parameters. Returns null if no parameters are available.
     *
     * @return	ParameterList
     * @since		JavaMail 1.2
     */
    public ParameterList getParameterList() {
	return list;
    }

    /**
     * Set the disposition.  Replaces the existing disposition.
     * @param	disposition	the disposition
     * @since		JavaMail 1.2
     */
    public void setDisposition(String disposition) {
	this.disposition = disposition;
    }

    /**
     * Set the specified parameter. If this parameter already exists,
     * it is replaced by this new value.
     *
     * @param	name	parameter name
     * @param	value	parameter value
     * @since		JavaMail 1.2
     */
    public void setParameter(String name, String value) {
	if (list == null)
	    list = new ParameterList();

	list.set(name, value);
    }

    /**
     * Set a new ParameterList.
     * @param	list	ParameterList
     * @since		JavaMail 1.2
     */
    public void setParameterList(ParameterList list) {
	this.list = list;
    }

    /**
     * Retrieve a RFC2045 style string representation of
     * this ContentDisposition. Returns <code>null</code> if
     * the conversion failed.
     *
     * @return	RFC2045 style string
     * @since		JavaMail 1.2
     */
    public String toString() {
	if (disposition == null)
	    return null;

	if (list == null)
	    return disposition;

	StringBuffer sb = new StringBuffer(disposition);

        // append the parameter list  
        // use the length of the string buffer + the length of 
        // the header name formatted as follows "Content-Disposition: "
	sb.append(list.toString(sb.length() + 21));
	return sb.toString();
    }
}
