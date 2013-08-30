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
 * @(#)SearchTerm.java	1.8 07/05/04
 */

package javax.mail.search;

import java.io.Serializable;

import javax.mail.Message;

/**
 * Search criteria are expressed as a tree of search-terms, forming
 * a parse-tree for the search expression. <p>
 *
 * Search-terms are represented by this class. This is an abstract
 * class; subclasses implement specific match methods. <p>
 *
 * Search terms are serializable, which allows storing a search term
 * between sessions.
 *
 * <strong>Warning:</strong>
 * Serialized objects of this class may not be compatible with future
 * JavaMail API releases.  The current serialization support is
 * appropriate for short term storage. <p>
 *
 * <strong>Warning:</strong>
 * Search terms that include references to objects of type
 * <code>Message.RecipientType</code> will not be deserialized
 * correctly on JDK 1.1 systems.  While these objects will be deserialized
 * without throwing any exceptions, the resulting objects violate the
 * <i>type-safe enum</i> contract of the <code>Message.RecipientType</code>
 * class.  Proper deserialization of these objects depends on support
 * for the <code>readReplace</code> method, added in JDK 1.2.
 *
 * @author Bill Shannon
 * @author John Mani
 */
public abstract class SearchTerm implements Serializable {

    private static final long serialVersionUID = -6652358452205992789L;

    /**
     * This method applies a specific match criterion to the given
     * message and returns the result.
     *
     * @param msg	The match criterion is applied on this message
     * @return		true, it the match succeeds, false if the match fails
     */

    public abstract boolean match(Message msg);
}
