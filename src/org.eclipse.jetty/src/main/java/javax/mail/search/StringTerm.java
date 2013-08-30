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
 * @(#)StringTerm.java	1.9 07/05/04
 */

package javax.mail.search;

/**
 * This class implements the match method for Strings. The current
 * implementation provides only for substring matching. We
 * could add comparisons (like strcmp ...).
 *
 * @author Bill Shannon
 * @author John Mani
 */
public abstract class StringTerm extends SearchTerm {
    /**
     * The pattern.
     *
     * @serial
     */
    protected String pattern;

    /**
     * Ignore case when comparing?
     *
     * @serial
     */
    protected boolean ignoreCase;

    private static final long serialVersionUID = 1274042129007696269L;

    protected StringTerm(String pattern) {
	this.pattern = pattern;
	ignoreCase = true;
    }

    protected StringTerm(String pattern, boolean ignoreCase) {
	this.pattern = pattern;
	this.ignoreCase = ignoreCase;
    }

    /**
     * Return the string to match with.
     */
    public String getPattern() {
	return pattern;
    }

    /**
     * Return true if we should ignore case when matching.
     */
    public boolean getIgnoreCase() {
	return ignoreCase;
    }

    protected boolean match(String s) {
	int len = s.length() - pattern.length();
	for (int i=0; i <= len; i++) {
	    if (s.regionMatches(ignoreCase, i, 
				pattern, 0, pattern.length()))
		return true;
	}
	return false;
    }

    /**
     * Equality comparison.
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof StringTerm))
	    return false;
	StringTerm st = (StringTerm)obj;
	if (ignoreCase)
	    return st.pattern.equalsIgnoreCase(this.pattern) &&
		    st.ignoreCase == this.ignoreCase;
	else
	    return st.pattern.equals(this.pattern) &&
		    st.ignoreCase == this.ignoreCase;
    }

    /**
     * Compute a hashCode for this object.
     */
    public int hashCode() {
	return ignoreCase ? pattern.hashCode() : ~pattern.hashCode();
    }
}
