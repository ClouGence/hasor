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
 * @(#)AddressException.java	1.9 07/05/04
 */

package javax.mail.internet;

/**
 * The exception thrown when a wrongly formatted address is encountered.
 *
 * @author Bill Shannon
 * @author Max Spivak
 */

public class AddressException extends ParseException {
    /**
     * The string being parsed.
     *
     * @serial
     */
    protected String ref = null;

    /**
     * The index in the string where the error occurred, or -1 if not known.
     *
     * @serial
     */
    protected int pos = -1;

    private static final long serialVersionUID = 9134583443539323120L;

    /**
     * Constructs an AddressException with no detail message.
     */
    public AddressException() {
	super();
    }

    /**
     * Constructs an AddressException with the specified detail message.
     * @param s		the detail message
     */
    public AddressException(String s) {
	super(s);
    }

    /**
     * Constructs an AddressException with the specified detail message
     * and reference info.
     *
     * @param s		the detail message
     */

    public AddressException(String s, String ref) {
	super(s);
	this.ref = ref;
    }
    /**
     * Constructs an AddressException with the specified detail message
     * and reference info.
     *
     * @param s		the detail message
     */
    public AddressException(String s, String ref, int pos) {
	super(s);
	this.ref = ref;
	this.pos = pos;
    }

    /**
     * Get the string that was being parsed when the error was detected
     * (null if not relevant).
     */
    public String getRef() {
	return ref;
    }

    /**
     * Get the position with the reference string where the error was
     * detected (-1 if not relevant).
     */
    public int getPos() {
	return pos;
    }

    public String toString() {
	String s = super.toString();
	if (ref == null)
	    return s;
	s += " in string ``" + ref + "''";
	if (pos < 0)
	    return s;
	return s + " at position " + pos;
    }
}
