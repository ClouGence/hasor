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
 * @(#)Address.java	1.10 07/05/04
 */

package javax.mail;

import java.io.Serializable;

/**
 * This abstract class models the addresses in a message.
 * Subclasses provide specific implementations.  Subclasses
 * will typically be serializable so that (for example) the
 * use of Address objects in search terms can be serialized
 * along with the search terms.
 *
 * @author John Mani
 * @author Bill Shannon
 */

public abstract class Address implements Serializable {

    private static final long serialVersionUID = -5822459626751992278L;

    /**
     * Return a type string that identifies this address type.
     *
     * @return	address type
     * @see	javax.mail.internet.InternetAddress
     */
    public abstract String getType();

    /**
     * Return a String representation of this address object.
     *
     * @return	string representation of this address
     */
    public abstract String toString();

    /**
     * The equality operator.  Subclasses should provide an
     * implementation of this method that supports value equality
     * (do the two Address objects represent the same destination?),
     * not object reference equality.  A subclass must also provide
     * a corresponding implementation of the <code>hashCode</code>
     * method that preserves the general contract of
     * <code>equals</code> and <code>hashCode</code> - objects that
     * compare as equal must have the same hashCode.
     *
     * @param	address	Address object
     */
    public abstract boolean equals(Object address);
}
