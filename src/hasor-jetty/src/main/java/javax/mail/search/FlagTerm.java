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
 * @(#)FlagTerm.java	1.13 07/05/04
 */

package javax.mail.search;

import javax.mail.*;

/**
 * This class implements comparisons for Message Flags.
 *
 * @author Bill Shannon
 * @author John Mani
 */
public final class FlagTerm extends SearchTerm {

    /**
     * Indicates whether to test for the presence or
     * absence of the specified Flag. If <code>true</code>,
     * then test whether all the specified flags are present, else
     * test whether all the specified flags are absent.
     *
     * @serial
     */
    protected boolean set;

    /**
     * Flags object containing the flags to test.
     *
     * @serial
     */
    protected Flags flags;

    private static final long serialVersionUID = -142991500302030647L;

    /**
     * Constructor.
     *
     * @param flags	Flags object containing the flags to check for
     * @param set	the flag setting to check for
     */
    public FlagTerm(Flags flags, boolean set) {
	this.flags = flags;
	this.set = set;
    }

    /**
     * Return the Flags to test.
     */
    public Flags getFlags() {
	return (Flags)flags.clone();
    }

    /**
     * Return true if testing whether the flags are set.
     */
    public boolean getTestSet() {
	return set;
    }

    /**
     * The comparison method.
     *
     * @param msg	The flag comparison is applied to this Message
     * @return		true if the comparson succeeds, otherwise false.
     */
    public boolean match(Message msg) {

	try {
	    Flags f = msg.getFlags();
	    if (set) { // This is easy
		if (f.contains(flags))
		    return true;
		else 
		    return false;
	    }

	    // Return true if ALL flags in the passed in Flags
	    // object are NOT set in this Message.

	    // Got to do this the hard way ...
	    Flags.Flag[] sf = flags.getSystemFlags();

	    // Check each flag in the passed in Flags object
	    for (int i = 0; i < sf.length; i++) {
		if (f.contains(sf[i]))
		    // this flag IS set in this Message, get out.
		    return false;
	    }

	    String[] s = flags.getUserFlags();

	    // Check each flag in the passed in Flags object
	    for (int i = 0; i < s.length; i++) {
		if (f.contains(s[i]))
		    // this flag IS set in this Message, get out.
		    return false;
	    }

	    return true;

	} catch (Exception e) {
	    return false;
	}
    }

    /**
     * Equality comparison.
     */
    public boolean equals(Object obj) {
	if (!(obj instanceof FlagTerm))
	    return false;
	FlagTerm ft = (FlagTerm)obj;
	return ft.set == this.set && ft.flags.equals(this.flags);
    }

    /**
     * Compute a hashCode for this object.
     */
    public int hashCode() {
	return set ? flags.hashCode() : ~flags.hashCode();
    }
}
