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
 * @(#)QuotaAwareStore.java	1.4 07/05/04
 */

package javax.mail;

/**
 * An interface implemented by Stores that support quotas.
 * The {@link #getQuota getQuota} and {@link #setQuota setQuota} methods
 * support the quota model defined by the IMAP QUOTA extension.
 * Refer to <A HREF="http://www.ietf.org/rfc/rfc2087.txt">RFC 2087</A>
 * for more information. <p>
 *
 * @since JavaMail 1.4
 */
public interface QuotaAwareStore {
    /**
     * Get the quotas for the named quota root.
     * Quotas are controlled on the basis of a quota root, not
     * (necessarily) a folder.  The relationship between folders
     * and quota roots depends on the server.  Some servers
     * might implement a single quota root for all folders owned by
     * a user.  Other servers might implement a separate quota root
     * for each folder.  A single folder can even have multiple
     * quota roots, perhaps controlling quotas for different
     * resources.
     *
     * @param	root	the name of the quota root
     * @return		array of Quota objects
     * @exception MessagingException	if the server doesn't support the
     *					QUOTA extension
     */
    Quota[] getQuota(String root) throws MessagingException;

    /**
     * Set the quotas for the quota root specified in the quota argument.
     * Typically this will be one of the quota roots obtained from the
     * <code>getQuota</code> method, but it need not be.
     *
     * @param	quota	the quota to set
     * @exception MessagingException	if the server doesn't support the
     *					QUOTA extension
     */
    void setQuota(Quota quota) throws MessagingException;
}
