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
 * @(#)ACL.java	1.5 07/05/04
 */

package com.sun.mail.imap;

import java.util.*;

/**
 * An access control list entry for a particular authentication identifier
 * (user or group).  Associates a set of Rights with the identifier.
 * See RFC 2086.
 * <p>
 *
 * @author Bill Shannon
 */

public class ACL implements Cloneable {

    private String name;
    private Rights rights;

    /**
     * Construct an ACL entry for the given identifier and with no rights.
     *
     * @param	name	the identifier name
     */
    public ACL(String name) {
	this.name = name;
	this.rights = new Rights();
    }

    /**
     * Construct an ACL entry for the given identifier with the given rights.
     *
     * @param	name	the identifier name
     * @param	rights	the rights
     */
    public ACL(String name, Rights rights) {
	this.name = name;
	this.rights = rights;
    }

    /**
     * Get the identifier name for this ACL entry.
     *
     * @return	the identifier name
     */
    public String getName() {
	return name;
    }

    /**
     * Set the rights associated with this ACL entry.
     *
     * @param	rights	the rights
     */
    public void setRights(Rights rights) {
	this.rights = rights;
    }

    /**
     * Get the rights associated with this ACL entry.
     * Returns the actual Rights object referenced by this ACL;
     * modifications to the Rights object will effect this ACL.
     *
     * @return	the rights
     */
    public Rights getRights() {
	return rights;
    }

    /**
     * Clone this ACL entry.
     */
    public Object clone() throws CloneNotSupportedException {
	ACL acl = (ACL)super.clone();
	acl.rights = (Rights)this.rights.clone();
	return acl;
    }
}
