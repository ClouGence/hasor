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
 * @(#)OrTerm.java	1.12 07/05/04
 */

package javax.mail.search;

import javax.mail.Message;

/**
 * This class implements the logical OR operator on individual SearchTerms.
 *
 * @author Bill Shannon
 * @author John Mani
 */
public final class OrTerm extends SearchTerm {

    /**
     * The array of terms on which the OR operator should
     * be applied.
     *
     * @serial
     */
    protected SearchTerm[] terms;

    private static final long serialVersionUID = 5380534067523646936L;

    /**
     * Constructor that takes two operands.
     *
     * @param t1 first term
     * @param t2 second term
     */
    public OrTerm(SearchTerm t1, SearchTerm t2) {
	terms = new SearchTerm[2];
	terms[0] = t1;
	terms[1] = t2;
    }

    /**
     * Constructor that takes an array of SearchTerms.
     *
     * @param t array of search terms
     */
    public OrTerm(SearchTerm[] t) {
	terms = new SearchTerm[t.length];
	for (int i = 0; i < t.length; i++)
	    terms[i] = t[i];
    }

    /**
     * Return the search terms.
     */
    public SearchTerm[] getTerms() {
	return (SearchTerm[])terms.clone();
    }

    /**
     * The OR operation. <p>
     *
     * The terms specified in the constructor are applied to
     * the given object and the OR operator is applied to their results.
     *
     * @param msg	The specified SearchTerms are applied to this Message
     *			and the OR operator is applied to their results.
     * @return		true if the OR succeds, otherwise false
     */

    public boolean match(Message msg) {
	for (int i=0; i < terms.length; i++)
	    if (terms[i].match(msg))
		return true;
	return false;
    }

    /**
     * Equality comparison.
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof OrTerm))
	    return false;
	OrTerm ot = (OrTerm)obj;
	if (ot.terms.length != terms.length)
	    return false;
	for (int i=0; i < terms.length; i++)
	    if (!terms[i].equals(ot.terms[i]))
		return false;
	return true;
    }

    /**
     * Compute a hashCode for this object.
     */
    public int hashCode() {
	int hash = 0;
	for (int i=0; i < terms.length; i++)
	    hash += terms[i].hashCode();
	return hash;
    }
}
